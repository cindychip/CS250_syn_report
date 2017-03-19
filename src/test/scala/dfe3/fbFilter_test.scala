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

class fir_feedbackTests[T <: Data:RealBits](c: fir_feedback[T]) extends DspTester(c) {
  //var len = Random.nextInt(2000)
  var len = 4
  val real = Array.fill(len)(Random.nextDouble*2-1)
  val img = Array.fill(len)(Random.nextDouble*2-1)
  val size = 2
  val tap_real = Array.fill(size)(Random.nextDouble*2-1)
  val tap_img = Array.fill(size)(Random.nextDouble*2-1)
  val (expect_real,expect_img) = fir_filter(real, img, tap_real, tap_img,
                                      len, size)

  for (i <- 0 until len) {
   poke (c.io.input_real,real(i))
   poke (c.io.input_img, img(i))
   if (i<size) {
       poke (c.io.tap_coeff_real,tap_real(i))
       poke (c.io.tap_coeff_img, tap_img(i)) 
     }
   poke (c.io.window_size, size)
   expect (c.io.output_real, expect_real(i))
   expect (c.io.output_img, expect_img(i))
  step(1)
  }//end for
}

// Scala style testing
class fir_feedbackSpec extends FlatSpec with Matchers {
  
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
    dsptools.Driver.execute(() => new fir_feedback(FixedPoint(32.W, 12.BP)), testOptions) { c =>
      new fir_feedbackTests(c)
    } should be (true)
  }
}


object fir_filter {
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
        out_real(j) = out_real(j) + tmp_real1(j-i) + tmp_real2(j-i)
        out_img(j) = out_img(j) + tmp_img1(j-i) + tmp_img2(j-i)
      }
    }//end for loop
    return (out_real,out_img)
  } 

}