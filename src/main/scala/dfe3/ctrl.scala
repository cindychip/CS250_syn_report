//Cindy Chen
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
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import spire.algebra.Ring
import chisel3.util._
import breeze.math.Complex


class ctrlIo[T <: Data:RealBits](gen: T) extends Bundle {
	val enable = Input(Bool())
	val reset = Input(Bool())
	val stage = Output(UInt(2.W))
	val count = Output(UInt(12.W))
	val fbf_coeff = Input(DspComplex(gen.cloneType, gen.cloneType))
  val ga_coeff = Input(Bool()) //might needed
  val coeff_output = Output(DspComplex(gen.cloneType, gen.cloneType))
	val tap_en = Output(Bool())
  val lms_en = Output(Bool())
}

class ctrl[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new ctrlIo(gen))
 
 //import submodule 
 val count = Reg(init = 0.U(12.W))
 val s_idle :: s_correlator :: s_dfe_bpsk :: s_dfe_qpsk :: Nil = Enum(4)
 val stage = Reg(init = s_idle)

 io.lms_en := false.B
 io.tap_en := false.B

 switch (stage) {
  is (s_idle) {
  	count := 0.U
  	when (io.enable) {
  		  stage := s_correlator
    }
  }
  is (s_correlator) {
    when (io.fbf_coeff.real > 0 || io.fbf_coeff.real < 0 || io.fbf_coeff.imag > 0 || io.fbf_coeff.imag < 0) {
    	count := count + 1.U
    	stage := s_dfe_bpsk
    }
  }
  is (s_dfe_bpsk) {
    count := count +1.U
    io.coeff_output := io.fbf_coeff //NOT SURE
  	when  (count === 1.U) {
  		io.coeff_output := DspComplex[T](Complex(0.0,0.0))
  		io.tap_en := true.B
  	}
    when (count === 255.U) {  //Golay B finished coming out in the correlator
      stage := s_dfe_qpsk
    }
  }
  
  is (s_dfe_qpsk) {
  	count := count +1.U 
    io.coeff_output := io.fbf_coeff
  	if (count == 513.U) {
  		io.tap_en := false.B
  	}
  } 
 } //end switch
 io.stage := stage
 io.count := count
}
