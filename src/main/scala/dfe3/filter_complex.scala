//Cindy Chen
package dfe3

import chisel3._
//import chisel3.core._
import chisel3.experimental.FixedPoint
import chisel3.iotesters.{Backend}
import chisel3.{Bundle, Module}
import dsptools.{DspContext, DspTester}
import dsptools.numbers.{FixedPointRing, DspComplexRing, DspComplex}
import dsptools.numbers.implicits._
import org.scalatest.{Matchers, FlatSpec}
import spire.algebra.Ring
import dsptools.numbers.{RealBits}

class fir_feedbackIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val tap_coeff_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new fir_feedbackIo(gen).asInstanceOf[this.type]
}

class fir_feedback[T <: Data:RealBits](gen: => T,var window_size: Int) extends Module {
  val io = IO(new fir_feedbackIo(gen))

  val delays = Reg(Vec(window_size, DspComplex(gen, gen)))
  delays(0) := io.input_complex
 
  for (i<- 1 until window_size) {
  	delays(i) := delays(i-1)
  }
  val coeff_count = Reg(init = 0.U(16.W)) //max 512 in darpa
  val buffer_complex = Reg(Vec(window_size, DspComplex(gen, gen))) //vector of reg
  val sum = Wire(Vec(window_size, DspComplex(gen,gen)))
  when (coeff_count < window_size.asUInt) {
  	buffer_complex(coeff_count) := io.tap_coeff_complex
  	coeff_count := coeff_count + 1.U
  }
  sum(0) := delays(0) * buffer_complex(0)
  for (i <- 1 until window_size) {
  	sum(i) := delays(i) * buffer_complex(i) + sum(i-1)
  }
  io.output_complex := sum(window_size-1)
}

