// DFEE correlator
package dfe3

import chisel3._
import chisel3.util._
import chisel3.experimental.FixedPoint
import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import iotesters.TesterOptions
import org.scalatest.{FlatSpec, Matchers}
import math._
import breeze.math.Complex
import dsptools.numbers._

class correlatorIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val fbf_coeff = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new correlatorIo(gen).asInstanceOf[this.type]
}

class correlator[T <: Data:RealBits](gen: T, var n: Int ,var preambleRe: Array[Double], var preambleIm: Array[Double]) extends Module {
// Calculate preamble reference j
  val io = IO(new correlatorIo(gen))
  val preambleReFix = preambleRe.map(tap => ConvertableTo[T].fromDouble(tap))
  val preambleImFix = preambleIm.map ( tap =>ConvertableTo[T].fromDouble(tap))
  //ShiftRegister initialization
  val delays = Reg(Vec(n, DspComplex(gen, gen)))
  val sum  = Wire(Vec(n, DspComplex(gen, gen)))
  // Set up ShiftRegister
  delays(0) := io.input_complex
  for (i<- 1 until n) {
  	delays(i) := delays(i-1)
  }
  sum(0).real := delays(0).real * preambleReFix(n-1) + delays(0).imag * preambleImFix(n-1)
  sum(0).imag := delays(0).imag * preambleReFix(n-1) - delays(0).real * preambleImFix(n-1)
  for (i <- 1 until n) {
  	sum(i).real := sum(i-1).real + delays(i).real * preambleReFix(n-i-1) + delays(i).imag * preambleImFix(n-i-1)
	sum(i).imag := sum(i-1).imag + delays(i).imag * preambleReFix(n-i-1) - delays(i).real * preambleImFix(n-i-1)
    }
  io.fbf_coeff := sum(n-1) 	 
  io.output_complex := delays(n-1)
}







