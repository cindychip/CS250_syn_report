package dfe3
import chisel3._
import scala.util.Random
import chisel3.experimental.FixedPoint
import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import iotesters.TesterOptions
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._
import breeze.math.Complex
import breeze.signal._
import scala.io.Source  // Added

class dfeTests[T <: Data:RealBits](c: dfe3[T]) extends DspTester(c) {
val real = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/filter_real1.txt").getLines.toArray.map(x => x.toDouble)
val imag = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/filter_imag1.txt").getLines.toArray.map(x => x.toDouble)
val test_real = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/test_real1.txt").getLines.toArray.map(x => x.toDouble)
val test_imag = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/test_imag1.txt").getLines.toArray.map(x => x.toDouble)

val n = real.length
for (i<-0 until n){
    poke (c.io.signal_in.real,real(i))
    poke (c.io.signal_in.imag, imag(i))
    poke (c.io.enable, true)
    poke (c.io.reset, false)
    peek (c.io.debug)
    if(i > 254) { ///correct
      expect(c.io.signal_out.real,test_real(i-255))
      expect(c.io.signal_out.imag,test_imag(i-255))
    }
    step(1)

  }
}

// Scala style testing
class dfeSpec extends FlatSpec with Matchers {

  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = false,
        backendName = "verilator")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/correlator_fix")
  }

  behavior of "correlator module"

  it should "properly add fixed point types" in {
dsptools.Driver.execute(() => new dfe3(FixedPoint(22.W, 12.BP)), testOptions) { c =>      
  new dfeTests(c)
    } should be (true)
  }
}