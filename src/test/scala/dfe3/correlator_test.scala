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
  val len = 4
  val size = 2
  val real = Array.fill(len)(Random.nextDouble*2-1)
  val img = Array.fill(len)(Random.nextDouble*2-1)
  val preamble_real = Array(11.0, 12.0)
  val preamble_img = Array(9.0, 10.0)
  val (expect_real, expect_img) = Adder(real, img, preamble_real, preamble_img,
                                     len, size)
  for (i<-0 until len){
    poke (c.io.input_complex.real,real(i))
    poke (c.io.input_complex.imag, img(i))
    step(1)
    if (i>=size-1){
      expect (c.io.fbf_coeff.real, expect_real(i-size+1))
      expect (c.io.fbf_coeff.imag, expect_img(i-size+1))
    }
  }//end for
}

// Scala style testing
class correlatorSpec extends FlatSpec with Matchers {
  val preamble_real = Array(11.0, 12.0)
  val preamble_img = Array(9.0, 10.0)

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
dsptools.Driver.execute(() => new correlator(FixedPoint(32.W, 12.BP),2, preamble_real, preamble_img), testOptions) { c =>      
  new correlatorTests(c)
    } should be (true)
  }
}

object Adder {
  def apply(signal_real:Array[Double], signal_img: Array[Double], 
            preamble_real:Array[Double], preamble_img:Array[Double], 
            sig_len:Int , preamble_len:Int) : (Array[Double], Array[Double])={
   val tmp_Re = new Array[Double](preamble_len)
   val tmp_Im = new Array[Double](preamble_len)
   val outRe = new Array[Double](sig_len-preamble_len+1)
   val outIm = new Array[Double](sig_len-preamble_len+1)
   for(j<-0 until (sig_len-preamble_len+1)){
      for (i<-0 until preamble_len){
      tmp_Re(i) = preamble_real(i)*signal_real(i+j)-(-preamble_img(i))*signal_img(i+j)
      tmp_Im(i) = preamble_real(i)*signal_img(i+j)+(-preamble_img(i))*signal_real(i+j)
      }
      outRe(j) = tmp_Re.reduceLeft( _ + _ )
      outIm(j) = tmp_Im.reduceLeft( _ + _ )
    }
   return (outRe, outIm)
   }
 }

