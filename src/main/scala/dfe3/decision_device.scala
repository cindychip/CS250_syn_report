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


class decision_deviceIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val error_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new DFE_decisionIo(gen).asInstanceOf[this.type]
}

class decision_device[T <: Data:RealBits](gen: T) extends Module {
  val io = IO(new DFE_decisionIo(gen))
  
  val positive = DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(sqrt(0.5.toDouble)) }
  val negative = DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(-sqrt(0.5.toDouble)) }
 
  when(io.input_complex.real<0){
   when(io.input_complex.imag<0){
      io.output_complex.real := negative
      io.output_complex.imag := negative
    }
    .otherwise{
      io.output_complex.real := negative
      io.output_complex.imag := positive
    }
  }.otherwise {
    when(io.input_complex.imag<0){
      io.output_complex.real := positive
      io.output_complex.imag := negative
    }
    .otherwise{
      io.output_complex.real := positive
      io.output_complex.imag := positive
    }
  }
 io.error_complex = io.output_complex - io.input_complex
}