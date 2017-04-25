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
  val tap_index = Input(UInt(12.W))
  val coef_en = Input(Bool())
  val lms_en = Input(Bool())
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug1 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug2 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug3 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val rst = Input(Bool())
  val output_debug4 = Output(UInt(2.W))

  override def cloneType: this.type = new fir_feedbackIo(gen).asInstanceOf[this.type]
}

//step_size: int indicate how much left shift the user want to input, min:0
class fir_feedback[T <: Data:RealBits](gen: T,var window_size: Int, var step_size: Int) extends Module {
  val io = IO(new fir_feedbackIo(gen))

  val delays = Reg(Vec(window_size, DspComplex(gen, gen)))
  val index_count = Reg(init = 0.U(2.W))
  val buffer_complex = Reg(Vec(3, DspComplex(gen, gen))) //vector of reg
  val index = Reg(Vec(3,0.U(12.W)))
  io.output_debug1 := buffer_complex(0)
  io.output_debug2 := buffer_complex(1)
  io.output_debug3 := buffer_complex(2)
  io.output_debug4 := index_count


  when(io.rst){
    buffer_complex(0) := DspComplex[T](Complex(0.0, 0.0))
    buffer_complex(1) := DspComplex[T](Complex(0.0, 0.0))
    buffer_complex(2) := DspComplex[T](Complex(0.0, 0.0))
    for (i <- 0 until window_size) {
      delays(i) := DspComplex[T](Complex(0.0, 0.0))
    }
  }
  .otherwise{
//input in a shift register
  delays(0) := io.input_complex
  for (i <- 1 until window_size) {
    delays(i) := delays(i-1)
  }
  
//update non-zero coef while count the index
  when (io.coef_en && (index_count < 3.U )) {
    when(io.tap_coeff_complex.imag > 0 || io.tap_coeff_complex.real > 0 ||
          io.tap_coeff_complex.imag < 0 || io.tap_coeff_complex.real < 0) {
      index(index_count) := io.tap_index -1.U
      buffer_complex(index_count) := io.tap_coeff_complex
      index_count := index_count + 1.U  
    }
  }
  }
//update lms
  when (io.lms_en) {
    // io.error needs to be conjugated
   buffer_complex(0).real := buffer_complex(0).real + (delays(index(0)).real * io.error.real +delays(index(0)).imag * io.error.imag)>> step_size 
   buffer_complex(0).imag := buffer_complex(0).imag + (delays(index(0)).imag * io.error.real -delays(index(0)).real * io.error.imag)>> step_size
   buffer_complex(1).real := buffer_complex(1).real + (delays(index(1)).real * io.error.real +delays(index(1)).imag * io.error.imag)>> step_size 
   buffer_complex(1).imag := buffer_complex(1).imag + (delays(index(1)).imag * io.error.imag -delays(index(1)).real * io.error.imag)>> step_size
   buffer_complex(2).real := buffer_complex(2).real + (delays(index(2)).real * io.error.real +delays(index(2)).imag * io.error.imag)>> step_size 
   buffer_complex(2).imag := buffer_complex(2).imag + (delays(index(2)).imag * io.error.imag -delays(index(2)).real * io.error.imag)>> step_size
}
/*
  when (index_count === 0.U) {
  io.output_complex := DspComplex[T](Complex(0.0, 0.0)) 
  } .elsewhen (index_count === 1.U) {
  io.output_complex := delays(index(0))* buffer_complex(0) 
  } .elsewhen (index_count === 2.U) {
  io.output_complex := delays(index(0))* buffer_complex(0) + 
                        delays(index(1))* buffer_complex(1)
  } .otherwise {
  io.output_complex := delays(index(0))* buffer_complex(0) + 
                        delays(index(1))* buffer_complex(1) + 
                         delays(index(2))* buffer_complex(2) 
  }
*/
  io.output_complex := delays(index(0))* buffer_complex(0) + 
                        delays(index(1))* buffer_complex(1) + 
                         delays(index(2))* buffer_complex(2) 
}
