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
  val counter_debug = Input(UInt(12.W))
  val debug = Output(DspComplex(gen.cloneType, gen.cloneType))
  //val count = Output(UInt(12.W)) //debugging output
  val output_debug1 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug2 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug3 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug4 = Output(UInt(10.W))
  val output_debug5 = Output(UInt(10.W))
  val output_debug6 = Output(UInt(10.W))
  val output_debugstage = Output(UInt(2.W))
  val debug7_input = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug8 = Output(DspComplex(gen.cloneType, gen.cloneType))
}

class dfe3Main[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new dfe3Io(gen))
 
 val dpath = Module(new dpathtotal(gen)).io
 val ctrl = Module(new ctrl(gen)).io

 ctrl.enable := io.enable
 ctrl.reset := io.reset  
 io.debug := dpath.coeff_out
  io.output_debug1 := dpath.output_debug1
  io.output_debug2 := dpath.output_debug2
  io.output_debug3 := dpath.output_debug3 
  io.output_debug4 := dpath.output_debug4
  io.output_debug5 := dpath.output_debug5
  io.output_debug6 := dpath.output_debug6

  io.output_debugstage := ctrl.stage
  io.debug7_input := dpath.debug7_input
  io.output_debug8 := dpath.output_debug8
 // input and output connection
 dpath.signal_in := io.signal_in
 dpath.counter_debug := io.counter_debug
 io.signal_out := dpath.signal_out

 ctrl.stage <> dpath.stage
 ctrl.count <> dpath.count
 ctrl.fbf_coeff <> dpath.coeff_out
 ctrl.coeff_output <> dpath.coeff_in
 ctrl.tap_en <> dpath.tap_en
 ctrl.lms_en <> dpath.lms_en

}

object dfe3MainTest extends App {
  
  Driver.execute(args.drop(3), () => new dfe3Main(FixedPoint(22, 12)))

}
