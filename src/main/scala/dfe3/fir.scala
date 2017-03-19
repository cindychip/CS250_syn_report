package dfe3

import chisel3._
import chisel3.experimental.FixedPoint
import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import iotesters.TesterOptions
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._


class fir_feedbackIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val tap_coeff_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new fir_feedbackIo(gen).asInstanceOf[this.type]
}

class fir_feedback[T <: Data:RealBits](gen: => T, var window_size: Int) extends Module {
  // This is how you declare an IO with parameters
  val io = IO(new fir_feedbackIo(gen))
  // define the shift register
  val delays = Vec(window_size, Reg(DspComplex(gen, gen)))
  delays(0) := io.input_complex
  for (i<- 1 until window_size) {
  		delays(i) := delays(i-1)
	}
  val coeff_count = Reg(init = 0.U) //max 512 in darpa
  val buffer_complex = Vec(window_size, Reg(DspComplex(gen, gen))) //vector of reg
  when (coeff_count < window_size.asUInt) {
  	buffer_complex(coeff_count) := io.tap_coeff_complex
  	coeff_count := coeff_count + 1
  }.otherwise {
  	coeff_count := 0.U
  	buffer_complex(coeff_count) := io.tap_coeff_complex
  }
  //delays.zip(buffer_complex).map{case(x,y) =>x*y}
  io.output_complex := delays(0) * buffer_complex(0) + delays(1) * buffer_complex(1)
}

