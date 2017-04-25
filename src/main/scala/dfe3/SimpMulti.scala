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
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val DecisionOut_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))

  override def cloneType: this.type = new SimpMultiIo(gen).asInstanceOf[this.type]
}

class SimpMulti[T <: Data:RealBits](gen: T) extends Module {
  val io = IO(new SimpMultiIo(gen))
 val sign = Wire(UInt (3.W))
  when (io.DecisionOut_complex.imag > 0){
    //QPSK imag >0
    when (io.DecisionOut_complex.real >= 0){
      sign := 4.U
    }.otherwise{
      sign := 6.U
    }
  } .elsewhen(io.DecisionOut_complex.imag < 0){
    //QPSK imag <0
    when (io.DecisionOut_complex.real >= 0){
      sign := 5.U
    }.otherwise{
      sign := 7.U
    }
  }.otherwise{ 
    //BPSK
    when(io.DecisionOut_complex.real >= 0){
      sign := 0.U
    } .otherwise{
      sign := 2.U
    }
  }
  //BPSK
  when (sign(2) === 0.U){
    when (sign(1) === 0.U){
      io.output_complex.real :=  io.input_complex.real
      io.output_complex.imag :=  io.input_complex.imag
    } .otherwise{
      io.output_complex.real :=  -io.input_complex.real
      io.output_complex.imag :=  io.input_complex.imag
    }
  } .otherwise{
    //QPSK
    when(sign(1) === sign(0)){
      when (sign(1)=== 0.U){
        io.output_complex.real :=  io.input_complex.real - io.input_complex.imag
        io.output_complex.imag :=  io.input_complex.real + io.input_complex.imag
      } .otherwise{
        io.output_complex.real :=  -io.input_complex.real + io.input_complex.imag
        io.output_complex.imag :=  -io.input_complex.real - io.input_complex.imag
      }
    } .otherwise{
      when (sign(1) === 0.U){
        io.output_complex.real :=  io.input_complex.real + io.input_complex.imag
        io.output_complex.imag :=  -io.input_complex.real + io.input_complex.imag
      } .otherwise{
        io.output_complex.real :=  -io.input_complex.real - io.input_complex.imag
        io.output_complex.imag :=  io.input_complex.real - io.input_complex.imag
      }
    }
  }
  // when(io.DecisionOut_complex.real === io.DecisionOut_complex.imag){
  //   when (io.DecisionOut_complex.real>= 0){
  //     io.output_complex.real :=  io.input_complex.real - io.input_complex.imag
  //     io.output_complex.imag :=  io.input_complex.real + io.input_complex.imag
  //   } .otherwise{
  //     io.output_complex.real :=  -io.input_complex.real + io.input_complex.imag
  //     io.output_complex.imag :=  -io.input_complex.real - io.input_complex.imag
  //   }
  // } .otherwise{
  //   when (io.DecisionOut_complex.real>= 0){
  //     io.output_complex.real :=  io.input_complex.real + io.input_complex.imag
  //     io.output_complex.imag :=  -io.input_complex.real + io.input_complex.imag
  //   } .otherwise{
  //     io.output_complex.real :=  -io.input_complex.real - io.input_complex.imag
  //     io.output_complex.imag :=  io.input_complex.real - io.input_complex.imag
  //   }
  // }

}