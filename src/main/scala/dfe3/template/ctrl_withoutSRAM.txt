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

class ctrlWithoutSRAM_feedbackIo[T <: Data:RealBits](gen: T) extends Bundle {
	val enable = Input(Bool())
	val reset = Input(Bool())
	val stage = Output(UInt(2.W))
	val count = Output(UInt(12.W))
	val fbf_coeff = Output(DspComplex(gen.cloneType, gen.cloneType))
	val tap_en = Output(Bool())
}

class ctrlWithoutSRAM[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new fir_feedbackIo(gen))
 
 //import submodule 
 val corr = Module(new correlator(T)).io
 val count = Reg(init = 0.U)
 val s_reset :: s_correlator :: s_dfe :: Nil = Enum(3)
 val stage = Reg(init = s_reset)

 switch (stage) {
  is (s_reset) {
  	count := 0
  	if (io.enable) {
  		stage := correlator
  	    }
     }
  is (s_correlator) {
      if (corr.out =/= 0) {
      	count := count + 1
      	stage := s_dfe
      }
    }
  is (s_dfe) {
  	count := count +1
    	if (count == 1.U) {
    		io.fbf_coeff := 0.U
    		io.tap_en := true.B
    	}
    	else { io.fbf_coeff := corr.out
    	}
    	if (count == 513.U) {
    		io.tap_en = false.B
    	}
    } 
 } //end switch
 io.stage := stage
 io.count := count
}
