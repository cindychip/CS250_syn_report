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

class dpathdecision_feedbackIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val error_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val coef_en = Input(Bool())
  val tap_coeff_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new decision_deviceIo(gen).asInstanceOf[this.type]
}


class dpathdecision_feedback[T <: Data:RealBits](gen: T) extends Module {
	val io = IO(new dpathdecision_feedbackIo(gen))
  	//instantiate the two submodules
	val dec = Module(new decision_device(gen)).io
 	val fbf = Module(new fir_feedback(gen,512,4)).io  	

 	(io.input_complex-fbf.output_complex) := dec.input_complex
 	io.output_complex := dec.output_complex
 	io.error_complex := dec.error_complex
 	dec.output_complex <> fbf.input_complex
	dec.lms_en := false.B
	dec.coef_en := io.coef_en
	dec.tap_coeff_complex := io.tap_coeff_complex
}
