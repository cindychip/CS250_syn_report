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
  val ra_out = Output(DspComplex(gen.cloneType, gen.cloneType))
  val rb_out = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new correlatorIo(gen).asInstanceOf[this.type]
}

class correlator[T <: Data:RealBits](gen: T) extends Module {
val io = IO(new correlatorIo(gen))
//Set up constant 
val n = 7
val W = List(-1, -1, -1, -1, 1, -1, -1)
val Dk = List(1, 8, 2, 4, 16, 32, 64)
val D1 = Reg(Vec(Dk(0), DspComplex(gen, gen)))
val D2 = Reg(Vec(Dk(1), DspComplex(gen, gen)))
val D3 = Reg(Vec(Dk(2), DspComplex(gen, gen)))
val D4 = Reg(Vec(Dk(3), DspComplex(gen, gen)))
val D5 = Reg(Vec(Dk(4), DspComplex(gen, gen)))
val D6 = Reg(Vec(Dk(5), DspComplex(gen, gen)))
val D7 = Reg(Vec(Dk(6), DspComplex(gen, gen)))
val DW = Wire(Vec(n, DspComplex(gen, gen)))
val ra  = Wire(Vec(n, DspComplex(gen, gen)))
val rb  = Wire(Vec(n, DspComplex(gen, gen)))

// Set up ShiftRegister
//D1
D1(0) := io.input_complex
DW(0) := D1(0)
//D2
for (i<- 0 until Dk(1)) {
  if(i == 0){
    D2(0) := rb(0) /////////////////////
  }else{
    D2(i) := D2(i-1)
  }
}
DW(1) := D2(Dk(1)-1)
//D3
for (i<- 0 until Dk(2)) {
  if(i == 0){
    D3(0) := rb(1)///////////////////////////
  }else{
    D3(i) := D3(i-1)
  }
}
DW(2) := D3(Dk(2)-1)
//D4
for (i<- 0 until Dk(3)) {
  if(i == 0){
    D4(0) := rb(2) ///////////////////
  }else{
    D4(i) := D4(i-1)
  }
}
DW(3) := D4(Dk(3)-1)
//D5
for (i<- 0 until Dk(3)) {
  if(i == 0){
    D5(0) := rb(3)///////////////////////
  }else{
    D5(i) := D5(i-1)
  }
}
DW(4) := D5(Dk(4)-1)
//D6
for (i<- 0 until Dk(3)) {
  if(i == 0){
    D6(0) := rb(4)////////////
  }else{
    D6(i) := D6(i-1)
  }
}
DW(5) := D6(Dk(5)-1)
//D7
for (i<- 0 until Dk(3)) {
  if(i == 0){
    D7(0) := rb(5)///////////////
  }else{
    D7(i) := D7(i-1)
  }
}
DW(6) := D7(Dk(6)-1)
// Calculate ra
for (i <- 0 until n){
  if (i == 0){
    ra(i) := -io.input_complex+DW(i)
  }else if (i == 4){
    ra(i) := ra(i-1)+DW(i)
  }else{
    ra(i) := -ra(i-1)+DW(i)
  }
}
// Calculate rb
for (i <- 0 until n){
  if (i == 0){
    rb(i) := -io.input_complex-DW(i)
  }else if (i == 4){
    rb(i) := rb(i-1)-DW(i)
  }else{
    rb(i) := -rb(i-1)-DW(i)
  }
}
io.ra_out := ra(6) 
io.rb_out := rb(6)










// // Calculate preamble reference j
//   val io = IO(new correlatorIo(gen))
//   val preambleReFix = preambleRe.map(tap => ConvertableTo[T].fromDouble(tap))
//   val preambleImFix = preambleIm.map ( tap =>ConvertableTo[T].fromDouble(tap))
//   //ShiftRegister initialization
//   val delays = Reg(Vec(n, DspComplex(gen, gen)))
//   val sum  = Wire(Vec(n, DspComplex(gen, gen)))
//   // Set up ShiftRegister
//   delays(0) := io.input_complex
//   for (i<- 1 until n) {
//   	delays(i) := delays(i-1)
//   }
//   sum(0).real := delays(0).real * preambleReFix(n-1) + delays(0).imag * preambleImFix(n-1)
//   sum(0).imag := delays(0).imag * preambleReFix(n-1) - delays(0).real * preambleImFix(n-1)
//   for (i <- 1 until n) {
//   	sum(i).real := sum(i-1).real + delays(i).real * preambleReFix(n-i-1) + delays(i).imag * preambleImFix(n-i-1)
// 	sum(i).imag := sum(i-1).imag + delays(i).imag * preambleReFix(n-i-1) - delays(i).real * preambleImFix(n-i-1)
//     }
//   io.fbf_coeff := sum(n-1) 	 
//   io.output_complex := delays(n-1)
}
