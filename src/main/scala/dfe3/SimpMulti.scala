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

class SimpMultiIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(FixedPoint(16.W, 12.BP),FixedPoint(16.W, 12.BP) ))  
  val sign = Input(UInt(3.W))
  val output_complex = Output(DspComplex(FixedPoint(16.W, 12.BP),FixedPoint(16.W, 12.BP) ))
  override def cloneType: this.type = new SimpMultiIo(gen).asInstanceOf[this.type]
}

class SimpMulti[T <: Data:RealBits](gen: T) extends Module {
  val io = IO(new SimpMultiIo(gen))

  //BPSK
  when (io.sign(2) === 0.U){
    when (io.sign(1) === 0.U){
      io.output_complex.real :=  io.input_complex.real
      io.output_complex.imag :=  io.input_complex.imag
    } .otherwise{
      io.output_complex.real :=  -io.input_complex.real
      io.output_complex.imag :=  -io.input_complex.imag
    }
  } .otherwise{
    //QPSK
    when(io.sign(1) === io.sign(0)){
      when (io.sign(1)=== 0.U){
        io.output_complex.real :=  io.input_complex.real - io.input_complex.imag
        io.output_complex.imag :=  io.input_complex.real + io.input_complex.imag
      } .otherwise{
        io.output_complex.real :=  -io.input_complex.real + io.input_complex.imag
        io.output_complex.imag :=  -io.input_complex.real - io.input_complex.imag
      }
    } .otherwise{
      when (io.sign(1) === 0.U){
        io.output_complex.real :=  io.input_complex.real + io.input_complex.imag
        io.output_complex.imag :=  -io.input_complex.real + io.input_complex.imag
      } .otherwise{
        io.output_complex.real :=  -io.input_complex.real - io.input_complex.imag
        io.output_complex.imag :=  io.input_complex.real - io.input_complex.imag
      }
    }
  }
 
}
