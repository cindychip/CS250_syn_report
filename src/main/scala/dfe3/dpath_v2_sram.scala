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

class dpathtotalIo[T <: Data:RealBits](gen: T) extends Bundle {
  val signal_in = Input(DspComplex(gen.cloneType, gen.cloneType))
  val signal_out = Output(DspComplex(gen.cloneType, gen.cloneType))
  val coeff_in = Input(DspComplex(gen.cloneType, gen.cloneType))
  val coeff_out = Output(DspComplex(gen.cloneType, gen.cloneType))
  val stage = Input(UInt(2.W))
  val count = Input(UInt(12.W))
  val lms_en = Input(Bool())
  val tap_en = Input(Bool())
  val counter_debug = Input(UInt(12.W))
  val output_debug1 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug2 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug3 = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug4 = Output(UInt(10.W))
  val output_debug5 = Output(UInt(10.W))
  val output_debug6 = Output(UInt(10.W))
  val debug7_input = Output(DspComplex(gen.cloneType, gen.cloneType))
  val output_debug8 = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new dpathtotalIo(gen).asInstanceOf[this.type]
}


class dpathtotal[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new dpathtotalIo(gen))
 val window_size = 512
 val step_size = 5
 //import submodule 
 val corr = Module(new correlator(gen)).io
 val dec = Module(new decision_device(gen)).io
 val fbf = Module(new fir(gen,window_size,step_size)).io
 //val fbf = Module(new fir_feedback(gen,window_size,step_size)).io

 io.output_debug1 := fbf.output_debug1
 io.output_debug2 := fbf.output_debug2
 io.output_debug3 := fbf.output_debug3
 io.output_debug4 := fbf.output_debug4
 io.output_debug5 := fbf.output_debug5
io.output_debug6 := fbf.output_debug6
 io.debug7_input := dec.input_complex //dec.output_complex//fbf.debug7_input
 io.output_debug8 := fbf.debug7_output

 when (io.stage === 0.U) {
    //IDLE state
    fbf.rst := true.B
    corr.rst := true.B
 }
//only correlator is working
 when (io.stage === 1.U) {
    fbf.rst := false.B
    corr.rst := false.B
    dec.qpsk_en := false.B
    corr.input_complex := io.signal_in
    //dec.input_complex := corr.output_complex
    //io.signal_out := dec.output_complex
    io.signal_out := corr.output_complex
 }

 //dfe is working
 when (io.stage === 2.U) {
  fbf.rst := false.B
  corr.rst := false.B
  corr.input_complex := io.signal_in
  dec.input_complex := corr.output_complex - fbf.output_complex
  dec.output_complex <> fbf.input_complex
  //dec.error_complex <> fbf.error
  fbf.tap_coeff_complex := io.coeff_in  //corr.output_coefficient
  fbf.tap_index := io.count
  fbf.counter := io.counter_debug
  //fbf.lms_en := io.lms_en
  fbf.coef_en := io.tap_en
  io.signal_out := dec.output_complex
  dec.qpsk_en := false.B
 }

  when (io.stage === 3.U) {
  fbf.rst := false.B
  corr.rst := false.B
  corr.input_complex := io.signal_in
  dec.input_complex := corr.output_complex - fbf.output_complex
  dec.output_complex <> fbf.input_complex
  //dec.error_complex <> fbf.error
  fbf.tap_coeff_complex := io.coeff_in  //corr.output_coefficient
  fbf.tap_index := io.count
  fbf.counter := io.counter_debug
  //fbf.lms_en := io.lms_en
  fbf.coef_en := io.tap_en
  io.signal_out := dec.output_complex
  dec.qpsk_en := true.B

 }
 io.coeff_out := corr.output_coefficient

}