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
val ready = Input(Bool())
val valid = Output(Bool())
}

class dctrlWithoutSRAM[T <: Data:RealBits](gen: T,var window_size: Int, var step_size: Int) extends Module {
 val io = IO(new fir_feedbackIo(gen))
 
 //import submodule 
 val corr = Module(new correlator(T)).io
 val dec = Module(new decision_device(T)).io
 val fbf = Module(new fir_feedback(T)).io
 
}
