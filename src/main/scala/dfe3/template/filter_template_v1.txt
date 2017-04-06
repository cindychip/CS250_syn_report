package fir_feedback

// Allows you to use Chisel Module, Bundle, etc.
import chisel3._
// Allows you to use FixedPoint
import chisel3.experimental.FixedPoint
// If you want to take advantage of type classes >> Data:RealBits (i.e. pass in FixedPoint or DspReal)
import dsptools.numbers.{RealBits}
// Required for you to use operators defined via type classes (+ has special Dsp overflow behavior, etc.)
import dsptools.numbers.implicits._
// Enables you to set DspContext's for things like overflow behavior, rounding modes, etc.
import dsptools.DspContext
// Use DspTester, specify options for testing (i.e. expect tolerances on fixed point, etc.)
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
// Allows you to modify default Chisel tester behavior (note that DspTester is a special version of Chisel tester)
import iotesters.TesterOptions
// Scala unit testing style
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._


// of the type you specify via gen (must be Data:RealBits = UInt, SInt, FixedPoint, DspReal)
class FIRIo[T <: Data:RealBits](gen: => T) extends Bundle {
  val input_real = Input(gen.cloneType)
  val input_img  = Input(gen.cloneType)
  val tap_coeff_real   = Input(gen.cloneType)
  val tap_coeff_img = Input(gen.cloneType)
  //val window_size  = Input(gen.cloneType)
  val output_real = Output(gen.cloneType)
  val output_img  = Output(gen.cloneType)
  override def cloneType: this.type = new FIRIo(gen).asInstanceOf[this.type]
}

class FIR[T <: Data:RealBits](gen: => T) extends Module {
  // This is how you declare an IO with parameters
  val io = IO(new FIRIo(gen))

  // define the shift register
  val delay0_real = Reg[T](gen)//(gen,next = io.input_real)
  val delay1_real = Reg[T](gen)
  val delay2_real = Reg[T](gen)
  val delay3_real = Reg[T](gen)
  delay0_real := io.input_real
  delay1_real := delay0_real
  delay2_real := delay1_real
  delay3_real := delay2_real

  val delay0_img = Reg[T](gen)//(gen,next = io.input_real)
  val delay1_img = Reg[T](gen)
  val delay2_img = Reg[T](gen)
  val delay3_img = Reg[T](gen)
  delay0_img := io.input_img
  delay1_img := delay0_img
  delay2_img := delay1_img
  delay3_img := delay2_img
  // define the buffer for the coefficient
  val buffer_real = Reg(Vec(5,gen.cloneType))
  val buffer_img = Reg(Vec(5,gen.cloneType))
  val coeff_count = Reg(init = 0.U(32.W))
  when (coeff_count===4.U) {
  	coeff_count := 0.U
  	buffer_real(coeff_count) := io.tap_coeff_real
  	buffer_img(coeff_count) := io.tap_coeff_img
  } .otherwise {
  	coeff_count := coeff_count +1.U
  	buffer_real(coeff_count) := io.tap_coeff_real
  	buffer_img(coeff_count) := io.tap_coeff_img
  }


  io.output_real := (io.input_real*buffer_real(0)+io.input_img*buffer_img(0))+ (delay0_real*buffer_real(1)+delay0_img*buffer_img(1))+(delay1_real*buffer_real(2)+delay1_img*buffer_img(2))+(delay2_real*buffer_real(3)+delay2_img*buffer_img(3))+(delay3_real*buffer_real(4)+delay3_img*buffer_img(4))
  io.output_img := (io.input_real*buffer_img(0)+io.input_img*buffer_real(0))+ (delay0_real*buffer_img(1)+delay0_img*buffer_real(1))+(delay1_real*buffer_img(2)+delay1_img*buffer_real(2))+(delay2_real*buffer_img(3)+delay2_img*buffer_real(3))+(delay3_real*buffer_img(4)+delay3_img*buffer_real(4))


  //io.input_real*buffer_real(0)+delay0*buffer_real(1) + delay1*buffer_real(2) + delay2*buffer_real(3)+ delay3*buffer_real(4)
  //io.output_real := delay1//io.input_real + delay0 + delay1 + delay2 +delay3
}
