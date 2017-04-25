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


class firFeedbackNoMultiTests[T <: Data:RealBits](c: firFeedbackNoMulti[T]) extends DspTester(c) {
  //var len = Random.nextInt(2000)
  var len = 12
  //val real = Array.fill(len)(Random.nextDouble*2-1)
  //val img = Array.fill(len)(Random.nextDouble*2-1)
  val real = Array(1.0,-1.0,1.0,-1.0,1.0,-1.0,1.0,-1.0,1.0,-1.0,1.0,-1.0)   //0.1,0.2,0.3,0.4,0.5,0.6,0.7)
  val img = Array(1.0,1.0,-1.0,-1.0,1.0,-1.0,1.0,-1.0,1.0,-1.0,1.0,-1.0)

  val size = 3
  val tap_real = Array.fill(size)(Random.nextDouble*2-1) //(1.0,1.0)
  val tap_img = Array.fill(size)(Random.nextDouble*2-1) //(1.0,1.0)
  /* tap_real(2) = 0.0
   tap_img(2) = 0.0
   tap_real(4) = 0.0
   tap_img(4) = 0.0
  */
  val (expect_real,expect_img) = fir_filter2(real, img, tap_real, tap_img,
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
       poke(c.io.lms_en, false)
       //poke(c.io.error.real, tap_real(i)/10)
       //poke(c.io.error.imag, tap_real(i)/10)
       peek(c.io.tap_coeff_complex.real)
       peek(c.io.tap_coeff_complex.imag)
     }
   step(1)
   expect (c.io.output_complex.real, expect_real(i))
   expect (c.io.output_complex.imag, expect_img(i))

  }//end for
}

// Scala style testing
class firFeedbackNoMultiSpec extends FlatSpec with Matchers {
  
  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = false,
        backendName = "verilator")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/firFeedbackNoMulti_dsp_fix")
  }

  behavior of "firFeedbackNoMulti dsp module"

  it should "properly add fixed point types" in {
dsptools.Driver.execute(() => new firFeedbackNoMulti(FixedPoint(16.W, 12.BP),3,3), testOptions) { c =>      
  new firFeedbackNoMultiTests(c)
    } should be (true)
  }
}





object fir_filter2 {
  def apply(signal_real:Array[Double], signal_img: Array[Double], 
            coef_real:Array[Double], coef_img:Array[Double], 
            sig_len:Int , coef_len:Int) : (Array[Double], Array[Double])={
   val out_real = new Array[Double](sig_len+coef_len)
   val out_img = new Array[Double](sig_len+coef_len)
   for (i <- 0 until coef_len) {
      val tmp_real1 = signal_real.map(_*coef_real(i)) 
      val tmp_real2 = signal_img.map(_*coef_img(i))
      val tmp_img1 = signal_img.map(_*coef_real(i)) 
      val tmp_img2 = signal_real.map(_*coef_img(i))
      //NOT SURE:
      for (j <- i until sig_len+i) {
        out_real(j) = out_real(j) + tmp_real1(j-i) - tmp_real2(j-i)
        out_img(j) = out_img(j) + tmp_img1(j-i) + tmp_img2(j-i)
      }
    }//end for loop
    return (out_real,out_img)
  } 
}
