package dfe3

import chisel3.Data
import chisel3.iotesters._
import scala.util.Random
import chisel3.experimental.FixedPoint
import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import chisel3.iotesters.TesterOptions
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._
import breeze.math.Complex
import breeze.signal._


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
        out_real(j) = out_real(j) + tmp_real1(j-i) - tmp_real2(j-i)
        out_img(j) = out_img(j) + tmp_img1(j-i) + tmp_img2(j-i)
      }
    }//end for loop
    return (out_real,out_img)
  } 
}

class feedbackfirTests[T <: Data:RealBits](c: fir[T]) extends DspTester(c) {
  //var len = Random.nextInt(2000)
  var len = 10
  val real = Array.fill(len)(Random.nextDouble*2-1)
  val img = Array.fill(len)(Random.nextDouble*2-1)
  //val real = Array(0.0,0.1,0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7)
  val counter = Array(0,1,2,3,4,5,6,7,8,9)

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
   poke (c.io.coef_en, false)
   poke (c.io.input_complex.real,real(i))
   poke (c.io.input_complex.imag, img(i))
   poke (c.io.counter,counter(i))
   if (i<size) {
       poke(c.io.coef_en, true)
       poke (c.io.tap_coeff_complex.real,tap_real(i))
       poke (c.io.tap_coeff_complex.imag, tap_img(i)) 
       poke (c.io.tap_index, i)
       peek(c.io.tap_coeff_complex.real)
       peek(c.io.tap_coeff_complex.imag)
     }
   step(1)
   expect (c.io.output_complex.real, expect_real(i))
   expect (c.io.output_complex.imag, expect_img(i))
   expect(c.io.buffer_rdata1_test.real, expect_real(i))
   expect(c.io.buffer_rdata1_test.imag, expect_real(i))
   expect(c.io.buffer_rdata2_test.real, expect_real(i))
   expect(c.io.buffer_rdata2_test.imag, expect_real(i))
   expect(c.io.buffer_rdata3_test.real, expect_real(i))
   expect(c.io.buffer_rdata3_test.imag, expect_real(i))
   expect(c.io.index0,0)
   expect(c.io.index1,0)
   expect(c.io.index2,0)
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
dsptools.Driver.execute(() => new fir(FixedPoint(16, 12),5,3), testOptions) { c =>      
  new feedbackfirTests(c)
    } should be (true)
  }
}


object firTester extends App {
  //We pass in some positional arguments to make things easier
  //This should be integrated with the CLI flags at some point
  //but is how rocket-chip accomplishes this
  //val paramsFromConfig = Sha3AccelMain.getParamsFromConfig(projectName = args(0), topModuleName = args(1), configClassName = args(2))
  Driver.execute(args.drop(4),() => new fir(FixedPoint(16, 12),5,3)){ c => new feedbackfirTests(c) }

}