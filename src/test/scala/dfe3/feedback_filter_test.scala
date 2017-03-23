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


class feedbackfirTests[T <: Data:RealBits](c: fir_feedback[T]) extends DspTester(c) {
  //var len = Random.nextInt(2000)
  var len = 6
  val real = Array.fill(len)(Random.nextDouble*2-1)
  val img = Array.fill(len)(Random.nextDouble*2-1)
  //val real = Array(0.0,0.1,0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7)
  //val img = Array(0.0,0.1,0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7)

  val size = 5
  val tap_real = Array.fill(size)(Random.nextDouble*2-1)
  val tap_img = Array.fill(size)(Random.nextDouble*2-1)
  tap_real(2) = 0.0
  tap_img(2) = 0.0
  tap_real(4) = 0.0
  tap_img(4) = 0.0

  val (expect_real,expect_img) = fir_filter(real, img, tap_real, tap_img,
                                      len, size)

  for (i <- 0 until len) {
   poke (c.io.lms_en, false)
   poke (c.io.coef_en, false)
   poke (c.io.input_complex.real,real(i))
   poke (c.io.input_complex.imag, img(i))
   if (i<size) {
       poke(c.io.coef_en, true)
       poke (c.io.tap_coeff_complex.real,tap_real(i))
       poke (c.io.tap_coeff_complex.imag, tap_img(i)) 
       poke (c.io.tap_index, i)
     }
   step(1)
   expect (c.io.output_complex.real, expect_real(i))
   expect (c.io.output_complex.imag, expect_img(i))
  }//end for
}

// Scala style testing
class feedbackfirSpec extends FlatSpec with Matchers {
  
  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = false,
        backendName = "verilator")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/simple_dsp_fix")
  }

  behavior of "simple dsp module"

  it should "properly add fixed point types" in {
dsptools.Driver.execute(() => new fir_feedback(FixedPoint(16.W, 12.BP),5,3), testOptions) { c =>      
  new feedbackfirTests(c)
    } should be (true)
  }
}

