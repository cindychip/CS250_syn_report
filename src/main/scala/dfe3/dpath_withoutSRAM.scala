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

class dpathlWithoutSRAM_feedbackIo[T <: Data:RealBits](gen: T) extends Bundle {
val signal_in = Input(DspComplex(gen.cloneType, gen.cloneType))
val signal_out = Output(DspComplex(gen.cloneType, gen.cloneType))
val stage = Input(UInt(2.W))
val count = Input(UInt(12.W))
val lms_en = Input(Bool())
}

class dpathlWithoutSRAM[T <: Data:RealBits](gen: T) extends Module {
 val io = IO(new fir_feedbackIo(gen))
 val window_size = 512.U
 val step_size = 5.U
 //import submodule 
 val corr = Module(new correlator(T)).io
 val dec = Module(new decision_device(T)).io
 val fbf = Module(new fir_feedback(T,window_size,step_size).io
 
 if (state == 0.U) {
    //reset
}
//only correlator is working
 if (state == 1.U) {
    corr.input_complex := io.signal_in
    io.signal_out := corr.output.complex
 }

 //dfe is working
 if (state == 2.U) {
 	corr.input_complex := io.signal_in
 	dec.input_complex := corr.output_complex - fbf.output_complex
 	dec.output_complex <> fbf.input_complex
 	dec.error_complex <> fbf.error
 	fbf.tap_coeff_complex <> corr.output_coeff
 	fbf.tap_index := io.count
 	fbf.lms_en := io.lms_en
 	io.signal_out := dec.output_complex
 }