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
  val real = Array(1.0, 2.0, 3.0, 4.0)
  val img = Array(1.0, 2.0, 3.0, 4.0)
  val preamble_real = Array(1.0, 2.0)
  val preamble_img = Array(1.0, 2.0)
  //val real = Array.fill(len)(Random.nextDouble*2-1)
  //val img = Array.fill(len)(Random.nextDouble*2-1)
  val size = 2
  //val expect_real = new Array[Double](len-size+1)
  //val expect_real = new Array[Double](len-size+1)
  val (expect_real, expect_img) = Adder(real, img, preamble_real, preamble_img,
                                     len, size)
  for (i<-0 until size){
    
    println("expect_real --> " + expect_real(i))
  //  poke (c.io.input_complex.real,real(i))
    //  poke (c.io.input_complex.imag, img(i))
    //  step(1)
    //  expect (c.io.output_complex.real, expect_real)
    //  expect (c.io.output_complex.imag, expect_img)
  }//end for
}

// Scala style testing
class correlatorSpec extends FlatSpec with Matchers {
  val real = Array(1.0, 2.0, 3.0, 4.0)
  val img = Array(1.0, 2.0, 3.0, 4.0)

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
dsptools.Driver.execute(() => new correlator(FixedPoint(32.W, 12.BP),4, real, img), testOptions) { c =>      
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
      tmp_Re(i) = preamble_real(i)*signal_real(i+j)+preamble_img(i)*signal_img(i+j)
      tmp_Im(i) = preamble_real(i)*preamble_img(i)-preamble_img(i)*preamble_img(i)
      }
      outRe(j) = tmp_Re.reduceLeft( _ + _ )
      outIm(j) = tmp_Im.reduceLeft( _ + _ )
    }
   return (outRe, outIm)
   }
 }






//    val out_real = new Array[Double](sig_len+coef_len)
//    val out_img = new Array[Double](sig_len+coef_len)
//    for (i <- 0 until coef_len) {
//       val tmp_real1 = signal_real.map(_*preamble_real(i)) 
//       val tmp_real2 = signal_img.map(_*preamble_img(i))
//       val tmp_img1 = signal_img.map(_*preamble_real(i)) 
//       val tmp_img2 = signal_real.map(_*preamble_img(i))
//       //NOT SURE:
//       for (j <- i until sig_len+i) {
//         out_real(j) = out_real(j) + tmp_real1(j-i) - tmp_real2(j-i)
//         out_img(j) = out_img(j) + tmp_img1(j-i) + tmp_img2(j-i)
//       }
//     }//end for loop
//     // for (j <- 0 until sig_len) {
//     //     out_real(j) = tmp_real1(j) + tmp_real2(j)
//     //     out_img(j) = tmp_img1(j) + tmp_img2(j)
//     // }
//     val real = out_real.sum
//     val img = out_img.sum
//     return (real,img)
//   } 

// }