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
import breeze.math.Complex


class decision_deviceIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val qpsk_en = Input(Bool()) 
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val error_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new decision_deviceIo(gen).asInstanceOf[this.type]
}

class decision_device[T <: Data:RealBits](gen: T) extends Module {
  val io = IO(new decision_deviceIo(gen))
  
  when (io.qpsk_en) {
    val positive1 = DspComplex[T](Complex(sqrt(0.5), sqrt(0.5)))
    val positive2 = DspComplex[T](Complex(-sqrt(0.5), sqrt(0.5)))
    val positive3 = DspComplex[T](Complex(sqrt(0.5), -sqrt(0.5)))
    val positive4 = DspComplex[T](Complex(-sqrt(0.5), -sqrt(0.5)))
 
  when(io.input_complex.real<0){
   when(io.input_complex.imag<0){
      io.output_complex := positive4
    }
    .otherwise{
      io.output_complex := positive2
    }
  }.otherwise {
    when(io.input_complex.imag<0){
      io.output_complex := positive3
    }
    .otherwise{
      io.output_complex := positive1
    }
  }
 }.otherwise{
 val positive = DspComplex[T](Complex(1.0, 0.0))
 val negative = DspComplex[T](Complex(-1.0, 0.0))
   when(io.input_complex.real<0){
        io.output_complex := negative
   }.otherwise {
   io.output_complex := positive
   }
  }
 io.error_complex := io.output_complex - io.input_complex
}