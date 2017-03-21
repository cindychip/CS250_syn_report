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
  //var len = Random.nextInt(2000)
  var len = 4
  // val real = Array.fill(len)(Random.nextDouble*2-1)
  // val img = Array.fill(len)(Random.nextDouble*2-1)
  val real = Array.fill(len)(Random.nextDouble*2-1)
 // val real = real_double.map(tap =>DspComplex(FixedPoint.fromDouble(tap, width = 32, binaryPoint = 12)))
  val img = Array.fill(len)(Random.nextDouble*2-1)
  //val img = img_double.map(tap =>DspComplex(FixedPoint.fromDouble(tap, width = 32, binaryPoint = 12)))

  val size = 2
 // val tap_real = Array.fill(size)(FixedPoint.fromDouble(Random.nextDouble*2-1), width = 32, binaryPoint = 12)
  //val tap_img = Array.fill(size)(Random.nextDouble*2-1)
  for (i<-0 until size){
    val (expect_real,expect_img) = Adder(real.slice(i,size+i), img.slice(i,size+i), real.take(size), img.take(size),
                                      size)
     poke (c.io.input_complex.real,real(i))
     poke (c.io.input_complex.imag, img(i))
     step(1)
     expect (c.io.output_complex.real, expect_real)
     expect (c.io.output_complex.imag, expect_img)
    }//end for
  }
  
// Scala style testing
class correlatorSpec extends FlatSpec with Matchers {

 // real.zip(img).map{case(x,y) =>DspComplex(FixedPoint.fromDouble(x, width = 32, binaryPoint = 12), FixedPoint.fromDouble(y, width = 32, binaryPoint = 12))}
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
dsptools.Driver.execute(() => new correlator(FixedPoint(32.W, 12.BP),2), testOptions) { c =>      
  new correlatorTests(c)
    } should be (true)
  }
}

object Adder{
  def apply(signal_real:Array[Double], signal_img:Array[Double], preamble_real:Array[Double], preamble_img:Array[Double], n:Int): (Double, Double) = {
  var out_real = 0.0
  var out_img = 0.0
    if (n == 0){
      out_real = signal_real(n) * preamble_real(n) - signal_img(n) * preamble_img(n)
      out_img = signal_real(n) * preamble_img(n) + signal_real(n) * preamble_real(n)
    }else{
      out_real = Adder(signal_real.take(n-1),signal_img.take(n-1), preamble_real.take(n-1), preamble_real.take(n-1), n-1)._1 + signal_real(n) * preamble_real(n) - signal_img(n) * preamble_img(n)
      out_img = Adder(signal_real.take(n-1),signal_img.take(n-1), preamble_real.take(n-1), preamble_real.take(n-1), n-1)._2 + signal_real(n) * preamble_img(n) + signal_real(n) * preamble_real(n)
    }
    return (out_real, out_img)
  } 
}



