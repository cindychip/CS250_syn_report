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

class fir_feedbackIo extends Bundle {
  val input_complex = Input(DspComplex(FixedPoint(16.W, 12.BP), FixedPoint(16.W, 12.BP)))
  val tap_coeff_complex = Input(DspComplex(FixedPoint(16.W, 12.BP), FixedPoint(16.W, 12.BP)))
  val output_complex = Output(DspComplex(FixedPoint(16.W, 12.BP), FixedPoint(16.W, 12.BP)))
  override def cloneType: this.type = new fir_feedbackIo().asInstanceOf[this.type]
}

class fir_feedback(var window_size: Int) extends Module {
  val io = IO(new fir_feedbackIo())

  val delays = Reg(Vec(2, DspComplex(FixedPoint(16.W, 12.BP), FixedPoint(16.W, 12.BP))))
  val coef = Reg(Vec(2, DspComplex(FixedPoint(16.W, 12.BP), FixedPoint(16.W, 12.BP))))
  delays(0) := io.input_complex
 
  for (i<- 1 until window_size) {
  	delays(i) := delays(i-1)
  }
  val coeff_count = Reg(init = 0.U(16.W)) //max 512 in darpa
  val buffer_complex = Reg(Vec(window_size, DspComplex(FixedPoint(16.W, 12.BP), FixedPoint(16.W, 12.BP)))) //vector of reg
  when (coeff_count < window_size.asUInt) {
  	buffer_complex(coeff_count) := io.tap_coeff_complex
  	coeff_count := coeff_count + 1.U
  }.otherwise {
  	coeff_count := 0.U
  	buffer_complex(coeff_count) := io.tap_coeff_complex
  }
  //delays.zip(buffer_complex).map{case(x,y) =>x*y}
  io.output_complex := delays(0) * buffer_complex(0) + delays(1) * buffer_complex(1)

  
}

