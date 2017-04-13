//Henry Zhu
package dfe3

import chisel3._
import chisel3.experimental.FixedPoint
import chisel3.iotesters.{Backend}
import chisel3.{Bundle, Module}
import dsptools.{DspContext, DspTester}
import dsptools.numbers.{FixedPointRing, DspComplexRing, DspComplex}
import dsptools.numbers.implicits._
import org.scalatest.{Matchers, FlatSpec}
import spire.algebra.Ring
import dsptools.numbers.{RealBits}

class dfe3Io[T <: Data:RealBits](gen: T) extends Bundle {
  val signal_in = Input(DspComplex(gen.cloneType, gen.cloneType))
  val signal_out = Output(DspComplex(gen.cloneType, gen.cloneType))
  val enable = Input(Bool())
  val reset = Input(Bool())
}

class dfe3[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new dfe3Io(gen))
 
 val dpath = Module(new dpathtotal(T)).io
 val ctrl = Module(new ctrl(T)).io

 ctrl.enable := io.enable
 ctrl.reset := io.reset  

 // input and output connection
 dpath.signal_in := io.signal_in
 io.signal_out := dpath.signal_out

 ctrl.stage <> dpath.stage
 ctrl.count	<> dpath.count
 ctrl.fbf_coeff <> dpath.coeff_out
 ctrl.coeff_output <> dpath.coeff_in
 ctrl.tap_en <> dpath.tap_en
 ctrl.lms_en <> dpath.lms_en

}