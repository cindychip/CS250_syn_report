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
  var len = 2
  val real = Array.fill(len)(Random.nextDouble*2-1)
  val img = Array.fill(len)(Random.nextDouble*2-1)

  val size = 2
  val tap_real = Array.fill(size)(Random.nextDouble*2-1)
  val tap_img = Array.fill(size)(Random.nextDouble*2-1)
  val (expect_real,expect_img) = Adder(real, img, tap_real, tap_img,
                                      len, size)

  for (i <- 0 until len) {
   poke (c.io.input_complex.real,real(i))
   poke (c.io.input_complex.imag, img(i))
   if (i<size) {
       poke (c.io.tap_coeff_complex.real,tap_real(i))
       poke (c.io.tap_coeff_complex.imag, tap_img(i)) 

     }
   step(1)
   expect (c.io.output_complex.real, expect_real(i))
   expect (c.io.output_complex.imag, expect_img(i))
  }//end for
}

object Adder{
  def apply(signal_real:Array[Double], signal_img:Array[Double], preamble_real:Array[Double], preamble_img:Array[Double], n:Int): (Double, Double) = {
  var out_real = 0.0
  var out_img = 0.0
    if (n == 0){
      out_real = signal_real(0) * preamble_real(0) - signal_img(0) * preamble_img(0)
      out_img = signal_real(0) * preamble_img(0) - signal_real(0) * preamble_real(0)
    }else{
      out_real = Adder(signal_real.take(n-1),signal_img.take(n-1), preamble_real.take(n-1), preamble_real.take(n-1), n-1)._1+signal_real(n) * preamble_real(n) - signal_img(n) * preamble_img(n)
      out_img = Adder(signal_real.take(n-1),signal_img.take(n-1), preamble_real.take(n-1), preamble_real.take(n-1), n-1)._2+signal_real(n) * preamble_img(n) - signal_real(n) * preamble_real(n)
    }
    return (out_real, out_img)
  } 
}



