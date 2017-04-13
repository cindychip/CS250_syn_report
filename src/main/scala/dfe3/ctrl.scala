//Cindy Chen
package dfe3

import chisel3._
import chisel3.util._

import chisel3.experimental.FixedPoint
import chisel3.iotesters.{Backend}
import chisel3.{Bundle, Module}
import dsptools.{DspContext, DspTester}
import dsptools.numbers.{FixedPointRing, DspComplexRing, DspComplex}
import dsptools.numbers.implicits._
import org.scalatest.{Matchers, FlatSpec}
import spire.algebra.Ring
import dsptools.numbers.{RealBits}
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer



class ctrlIo[T <: Data:RealBits](gen: T) extends Bundle {
	val enable = Input(Bool())
	val reset = Input(Bool())
	val stage = Output(UInt(2.W))
	val count = Output(UInt(12.W))
	val fbf_coeff = Input(DspComplex(gen.cloneType, gen.cloneType))
  val ga_coeff = Input(Bool())
  val coeff_output = Output(DspComplex(gen.cloneType, gen.cloneType))
	val tap_en = Output(Bool())
  val lms_en = Output(Bool())
}

class ctrl[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new ctrlIo(gen))
 
 //import submodule 
 val count = Reg(init = 0.U)
 val s_idle :: s_correlator :: s_dfe_bpsk :: s_def_qpsk :: Nil = Enum(4)
 val stage = Reg(init = s_idle)

 io.lms_en := false.B

 switch (stage) {
  is (s_idle) {
  	count := 0.U
  	if (io.enable == true) {
  		  stage := s_correlator
    }
  }
  is (s_correlator) {
    if (io.ga_coeff == true) {
    	count := count + 1.U
    	stage := s_dfe_bpsk
    }
  }
  is (s_dfe_bpsk) {
    count := count +1.U
  	if (count == 1.U) {
  		io.coeff_output := 0.U
  		io.tap_en := true.B
  	}
    if (count == 128.U) {
        when (io.fbf_coeff.real > 0 || io.fbf_coeff.real < 0 || io.fbf_coeff.imag > 0 || io.fbf_coeff.imag < 0) {
        stage := s_def_qpsk 
        }.otherwise {
          stage := s_correlator
          count := 0.U //does that conflict with count := count +1
        }
    }
  }
  is (s_def_qpsk) {
  	count := count +1 
    io.coeff_output := io.fbf_coeff
  	if (count == 513.U) {
  		io.tap_en := false.B
  	}
  } 
 } //end switch
 io.stage := stage
 io.count := count
}
