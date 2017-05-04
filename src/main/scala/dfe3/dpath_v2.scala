
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

class dpathtotalIo[T <: Data:RealBits](gen: T, var S_w: Int, var C_w: Int, var bp: Int) extends Bundle {
  val signal_in = Input(DspComplex(FixedPoint(S_w, bp) ))
  val signal_out = Output(DspComplex(FixedPoint(S_w, bp) ))
  val coeff_in = Input(DspComplex(FixedPoint(C_w, bp) ))
  val coeff_out = Output(DspComplex(FixedPoint(C_w, bp) ))
  val stage = Input(UInt(2.W))
  val count = Input(UInt(12.W))
  val lms_en = Input(Bool())
  val tap_en = Input(Bool())

  override def cloneType: this.type = new dpathtotalIo(gen, S_w, C_w, bp).asInstanceOf[this.type]
}


class dpathtotal[T <: Data:RealBits](gen: T,var S_w: Int, var C_w: Int, var bp: Int) extends Module {
 val io = IO(new dpathtotalIo(gen, S_w, C_w, bp))
 val window_size = 128
 val step_size = 5
 
 //val corr = Module(new correlator(gen, S_w, C_w, bp)).io
 val corr = Module(new correlator(gen)).io
 val dec = Module(new decision_device(FixedPoint(S_w, bp))).io
// val fbf = Module(new fir_feedback(gen,window_size,step_size)).io //fir_feedback
 val fbf = Module(new firFeedbackNoMulti(gen,window_size,step_size, S_w, C_w, bp)).io


 when (io.stage === 0.U) {
    fbf.rst := true.B
    corr.rst := true.B
 }
//only correlator is working
 when (io.stage === 1.U) {
    fbf.rst := false.B
    corr.rst := false.B
    dec.qpsk_en := false.B
    corr.input_complex := io.signal_in
    io.signal_out := corr.output_complex
 }

 //dfe is working
 when (io.stage === 2.U) {
  fbf.rst := false.B
  corr.rst := false.B
  corr.input_complex := io.signal_in
  dec.input_complex := corr.output_complex - fbf.output_complex
  dec.output_complex <> fbf.input_complex
  dec.error_complex <> fbf.error
  fbf.tap_coeff_complex := io.coeff_in  //corr.output_coefficient
  fbf.tap_index := io.count
  fbf.lms_en := io.lms_en
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
  dec.error_complex <> fbf.error
  fbf.tap_coeff_complex := io.coeff_in  //corr.output_coefficient
  fbf.tap_index := io.count
  fbf.lms_en := io.lms_en
  fbf.coef_en := io.tap_en
  io.signal_out := dec.output_complex
  dec.qpsk_en := true.B
 }
 io.coeff_out := corr.output_coefficient

}
