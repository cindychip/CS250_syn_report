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

class dpathtotalTests[T <: Data:RealBits](c: dpathtotal[T]) extends DspTester(c) {
var input_real = Array(0.7071,-0.1414,0.5657,-0.2828,-1.1314,-0.0000,-0.8485,0.2828,0.4243,0.1414,0.2828)
var input_imag = Array( 0.7071 ,   1.2728,    0.2828,   -0.5657,   -1.4142,-1.9799, 
   -0.5657,    0.5657,    0.7071,    0.7071,    0.2828)


for (i <- 0 until 11) { 
	poke (c.io.signal_in.real, input_real(i))
	poke (c.io.signal_in.imag, input_imag(i))
	poke (c.io.stage, 0.U)
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