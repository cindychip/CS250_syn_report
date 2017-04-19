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

class decision_deviceTests[T <: Data:RealBits](c: decision_device[T]) extends DspTester(c) {
var len = 100
var input_real = Array.fill(len)(Random.nextDouble*2-1)
var input_img = Array.fill(len)(Random.nextDouble*2-1)
var (expect_real,expect_img)  = decisionDevice(input_real,input_img,len)
var (expect_real2,expect_img2)  = decisionDevice2(input_real,input_img,len)

for (i<-0 until len/2){
  poke (c.io.input_complex.real,input_real(i))
  poke (c.io.input_complex.imag, input_img(i))
  poke (c.io.qpsk_en, true)
  step(1)
  expect (c.io.output_complex.real,expect_real(i))
  expect (c.io.output_complex.imag,expect_img(i))
}
for (i<- len/2 until len){
  poke (c.io.input_complex.real,input_real(i))
  poke (c.io.input_complex.imag, input_img(i))
  poke (c.io.qpsk_en, false)
  step(1)
  expect (c.io.output_complex.real,expect_real2(i))
  expect (c.io.output_complex.imag,expect_img2(i))  
}
}

// Scala style testing
class decision_deviceSpec extends FlatSpec with Matchers {

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

  behavior of "decision_device module"

  it should "properly add fixed point types" in {
dsptools.Driver.execute(() => new decision_device(FixedPoint(32.W, 12.BP)), testOptions) { c =>      
  new decision_deviceTests(c)
    } should be (true)
  }
}

object decisionDevice {
  def apply(input_real:Array[Double], input_img: Array[Double], len: Int) : (Array[Double], Array[Double])={
   val out_real = new Array[Double](len)
   val out_img = new Array[Double](len)
   var positive = sqrt(0.5.toDouble)
   var negative = -sqrt(0.5.toDouble)
   for (i <- 0 until len) {
      if(input_real(i)<0){
        if(input_img(i)<0){
          out_real(i) = negative
          out_img(i) = negative
        }else{
          out_real(i) = negative
          out_img(i) = positive
        }
      }else{
        if(input_img(i)<0){
          out_real(i) = positive
          out_img(i) = negative
        }else{
          out_real(i) = positive
          out_img(i) = positive
          }
      }
    }
    return(out_real, out_img)
  }
} 

object decisionDevice2 {
  def apply(input_real:Array[Double], input_img: Array[Double], len: Int) : (Array[Double], Array[Double])={
   val out_real = new Array[Double](len)
   val out_img = new Array[Double](len)
   var positive = 1.toDouble
   var negative = -1.toDouble
   var zero = 0.toDouble
   for (i <- 0 until len) {
     out_img(i) = zero
      if(input_real(i)<0){
          out_real(i) = negative
      }else{
          out_real(i) = positive
          }
      }
    return(out_real, out_img)
  }
} 
