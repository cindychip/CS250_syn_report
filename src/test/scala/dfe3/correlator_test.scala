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

class correlatorTests[T <: Data:RealBits](c: correlator[T]) extends DspTester(c) {
val ga128 = Array(1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1, 1, -1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1)
val gab128 = Array(1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1, 1, -1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1,-1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1, 1, -1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1)
val gb128 = Array(-1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1, 1, -1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1)
val n = 128*2
for (i<-0 until n){
    poke (c.io.input_complex.real,gab128(i))
    poke (c.io.input_complex.imag, 0)
    step(1)
    if (i == 126){
      expect (c.io.ra_out.real, 126)
      expect (c.io.output_complex.real, gab128(0))
      peek(c.io.output_coefficient.real)
    }
    if (i == 127*2){
      expect (c.io.rb_out.real, 126)
      expect (c.io.output_complex.real, gab128((i*0.5+1).toInt))
      expect(c.io.output_coefficient.real,1)
    }
  }
}

// Scala style testing
class correlatorSpec extends FlatSpec with Matchers {

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
dsptools.Driver.execute(() => new correlator(FixedPoint(32.W, 12.BP)), testOptions) { c =>      
  new correlatorTests(c)
    } should be (true)
  }
}
