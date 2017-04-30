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
import spire.math.{ConvertableTo}


class SimpMultiIo[T <: Data:RealBits](gen: T, var S_w: Int, var bp: Int) extends Bundle {
  val input_complex = Input(DspComplex(FixedPoint(S_w, bp),FixedPoint(S_w, bp) ))  
  val sign = Input(UInt(3.W))
  val output_complex = Output(DspComplex(FixedPoint(S_w, bp),FixedPoint(S_w, bp) ))
  override def cloneType: this.type = new SimpMultiIo(gen, S_w, bp).asInstanceOf[this.type]
}

class SimpMulti[T <: Data:RealBits](gen: T, var S_w: Int, var bp: Int) extends Module {
  val io = IO(new SimpMultiIo(gen, S_w, bp))

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
        io.output_complex.real :=  (io.input_complex.real - io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
        io.output_complex.imag :=  (io.input_complex.real + io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) } 
      } .otherwise{
        io.output_complex.real :=  (-io.input_complex.real + io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
        io.output_complex.imag :=  (-io.input_complex.real - io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
      }
    } .otherwise{
      when (io.sign(1) === 0.U){
        io.output_complex.real :=  (io.input_complex.real + io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
        io.output_complex.imag :=  (-io.input_complex.real + io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
      } .otherwise{
        io.output_complex.real :=  (-io.input_complex.real - io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
        io.output_complex.imag :=  (io.input_complex.real - io.input_complex.imag) * { ConvertableTo[FixedPoint].fromDouble(0.7071067811865475244) }
      }
    }
  }
 
}
