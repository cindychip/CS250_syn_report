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
import scala.io.Source

class dpathtotalTests[T <: Data:RealBits](c: dpathtotal[T]) extends DspTester(c) {
val input_real = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/filter_real.txt").getLines.toArray.map(x => x.toDouble)
val input_imag = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/filter_imag.txt").getLines.toArray.map(x => x.toDouble)
val n = input_real.length

for (i <- 0 until 256) { 
  poke (c.io.signal_in.real, input_real(i%10))
  poke (c.io.signal_in.imag, input_imag(i%10))
  poke (c.io.stage, 1)
  poke (c.io.count, 0)
  poke (c.io.lms_en, false)
  poke (c.io.tap_en, true)
  peek (c.io.signal_out)
  step(1)
}
for (i<- 256 until n) {
  poke (c.io.signal_in.real, input_real(i%10))
  poke (c.io.signal_in.imag, input_imag(i%10))
  poke (c.io.stage, 2)
  poke (c.io.count, 0)
  poke (c.io.lms_en, false)
  poke (c.io.tap_en, true)
  peek (c.io.signal_out)
  step(1)
}

}



class dpathtotalSpec extends FlatSpec with Matchers {

  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = false,
        backendName = "verilator")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/decision_device_fix")
  }

  behavior of "dpathtotal module"

  it should "properly add fixed point types" in {
dsptools.Driver.execute(() => new dpathtotal(FixedPoint(32.W, 12.BP)), testOptions) { c =>      
  new dpathtotalTests(c)
    } should be (true)
  }
}