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
import math._

class firFeedbackNoMultiIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(FixedPoint(16.W, 12.BP),FixedPoint(16.W, 12.BP) ))
  val tap_coeff_complex = Input(DspComplex(FixedPoint(22.W, 12.BP),FixedPoint(22.W, 12.BP) ))
  val error = Input(DspComplex(FixedPoint(16.W, 12.BP),FixedPoint(16.W, 12.BP) ))
  val tap_index = Input(UInt(12.W))
  val coef_en = Input(Bool())
  val lms_en = Input(Bool())
  val output_complex = Output(DspComplex(FixedPoint(16.W, 12.BP),FixedPoint(16.W, 12.BP) ))
 // val output_debug1 = Output(DspComplex(gen.cloneType, gen.cloneType))
 // val output_debug2 = Output(DspComplex(gen.cloneType, gen.cloneType))
 // val output_debug3 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val rst = Input(Bool())
 // val output_debug4 = Output(UInt(2.W))

  override def cloneType: this.type = new firFeedbackNoMultiIo(gen).asInstanceOf[this.type]
}


//step_size: int indicate how much left shift the user want to input, min:0
class firFeedbackNoMulti[T <: Data:RealBits](gen: T,var window_size: Int, var step_size: Int) extends Module {
  val io = IO(new firFeedbackNoMultiIo(gen))
  // Added+++++++++++++++++++
  val Multi_0 = Module(new SimpMulti(gen)).io
  val Multi_1 = Module(new SimpMulti(gen)).io
  val Multi_2 = Module(new SimpMulti(gen)).io
  //Added+++++++++++++++++++
  val delays = Reg(Vec(window_size, UInt(3.W)))
  val index_count = Reg(init = 0.U(2.W))
  val buffer_complex = Reg(Vec(3, DspComplex(FixedPoint(22.W, 12.BP),FixedPoint(22.W, 12.BP) )))
  val index = Reg(Vec(3,0.U(12.W)))
  val sign = Wire(UInt (3.W))
  when (io.input_complex.imag > 0){
    //QPSK imag >0
    when (io.input_complex.real >= 0){
      sign := 4.U
    }.otherwise{
      sign := 6.U
    }
  } .elsewhen(io.input_complex.imag < 0){
    //QPSK imag <0
    when (io.input_complex.real >= 0){
      sign := 5.U
    }.otherwise{
      sign := 7.U
    }
  }.otherwise{ 
    //BPSK
    when(io.input_complex.real >= 0){
      sign := 0.U
    } .otherwise{
      sign := 2.U
    }
  }
  
  when(io.rst){
    buffer_complex(0) := DspComplex(0.0.F(22.W,12.BP), 0.0.F(22.W,12.BP))
    buffer_complex(1) := DspComplex(0.0.F(22.W,12.BP), 0.0.F(22.W,12.BP))
    buffer_complex(2) := DspComplex(0.0.F(22.W,12.BP), 0.0.F(22.W,12.BP))
    index_count := 0.U
    index(0) := 0.U
    index(1) := 0.U
    index(2) := 0.U
    for (i <- 0 until window_size) {
      delays(i) := 0.U
    }
  }
  .otherwise{
  delays(0) := sign
  for (i <- 1 until window_size) {
    delays(i) := delays(i-1)
  }
//update non-zero coef while count the index
  when ((io.coef_en) && (index_count < 3.U )) {
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
    val error = Reg(DspComplex(gen,gen))
    error.real := io.error.real >> step_size
    error.imag := io.error.imag >> step_size

    val Multi_3 = Module(new SimpMulti(gen)).io
    val Multi_4 = Module(new SimpMulti(gen)).io
    val Multi_5 = Module(new SimpMulti(gen)).io
    
    Multi_3.input_complex := error
    Multi_3.sign := delays(index(0))

    Multi_4.input_complex := error
    Multi_4.sign := delays(index(1))

    Multi_5.input_complex := error
    Multi_5.sign := delays(index(2))

    buffer_complex(0) := buffer_complex(0) - Multi_3.output_complex
    buffer_complex(1) := buffer_complex(1) - Multi_4.output_complex
    buffer_complex(2) := buffer_complex(2) - Multi_5.output_complex

}
/*
when (index_count === 1.U ) {
Multi_0.input_complex := buffer_complex(0) 
Multi_0.sign := delays(index(0))
io.output_complex := Multi_0.output_complex
}
.elsewhen (index_count === 2.U) {
Multi_0.input_complex := buffer_complex(0) 
Multi_0.sign := delays(index(0))
Multi_1.input_complex := buffer_complex(1) 
Multi_1.sign := delays(index(1)) 
io.output_complex := Multi_0.output_complex + Multi_1.output_complex
}
.elsewhen (index_count === 3.U) {
	*/
Multi_0.input_complex := buffer_complex(0) 
Multi_0.sign := delays(index(0))
Multi_1.input_complex := buffer_complex(1) 
Multi_1.sign := delays(index(1)) 
Multi_2.input_complex := buffer_complex(2) 
Multi_2.sign := delays(index(2))
io.output_complex := Multi_0.output_complex + Multi_1.output_complex + Multi_2.output_complex
//}
//.otherwise {
//	io.output_complex := DspComplex(0.0.F(16.W,12.BP), 0.0.F(16.W,12.BP)) 
}


//}
