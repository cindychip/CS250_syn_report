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
import breeze.math.Complex

//tap_coeff_complex only allows three non-zero inputs
class fir_feedbackIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val tap_coeff_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val error = Input(DspComplex(gen.cloneType, gen.cloneType))
  val coef_en = Input(Bool())
  val lms_en = Input(Bool())
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new fir_feedbackIo(gen).asInstanceOf[this.type]
}

//step_size: int indicate how much left shift the user want to input, min:0
class fir_feedback[T <: Data:RealBits](gen: => T,var window_size: Int, var step_size: Int) extends Module {
  val io = IO(new fir_feedbackIo(gen))

  val delays = Reg(Vec(window_size, DspComplex(gen, gen)))
  val coeff_count = Reg(init = 0.U(16.W)) //max 512 in darpa
  val index_count = Reg(init = 0.U(2.W))
  val buffer_complex = Reg(Vec(window_size, DspComplex(gen, gen))) //vector of reg
  val index = Reg(Vec(3,0.U(16.W)))

 //input in a shift register
  delays(0) := io.input_complex
  for (i<- 1 until window_size) {
  	delays(i) := delays(i-1)
  }

//update non-zero coef while count the index
  when (io.coef_en) {
  	buffer_complex(coeff_count) := io.tap_coeff_complex
    when (io.tap_coeff_complex =/= DspComplex[T]Complex(0, 0)) {
      index(index_count) := coeff_count
      index_count := index_count+1
    }
    coeff_count := coeff_count + 1.U
  }

  io.output_complex := delays(index(0))* buffer_complex(index(0)) + 
                        delays(index(1))* buffer_complex(index(1)) + 
                         delays(index(2))* buffer_complex(index(2))
}