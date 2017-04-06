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
val W = Array(-1, -1, -1, -1, 1, -1, -1)
val Dk = Array(1, 8, 2, 4, 16, 32, 64)
val output = Reg(Vec(Dk.sum, DspComplex(gen, gen)))
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
//set up ShiftRegister for output Complex
output(0) := io.input_complex
for (i<-1 until Dk.sum){
  output(i) := output(i-1)
}
io.output_complex := output(Dk.sum-1)

// Set up ShiftRegister for delay
//D1
D1(0) := io.input_complex
DW(0) := D1(0)
//D2
for (i<- 0 until Dk(1)) {
  if(i == 0){
    D2(0) := rb(0) 
  }else{
    D2(i) := D2(i-1)
  }
}
DW(1) := D2(Dk(1)-1)
//D3
for (i<- 0 until Dk(2)) {
  if(i == 0){
    D3(0) := rb(1)
  }else{
    D3(i) := D3(i-1)
  }
}
DW(2) := D3(Dk(2)-1)
//D4
for (i<- 0 until Dk(3)) {
  if(i == 0){
    D4(0) := rb(2)
  }else{
    D4(i) := D4(i-1)
  }
}
DW(3) := D4(Dk(3)-1)
//D5
for (i<- 0 until Dk(4)) {
  if(i == 0){
    D5(0) := rb(3)
  }else{
    D5(i) := D5(i-1)
  }
}
DW(4) := D5(Dk(4)-1)
//D6
for (i<- 0 until Dk(5)) {
  if(i == 0){
    D6(0) := rb(4)
  }else{
    D6(i) := D6(i-1)
  }
}
DW(5) := D6(Dk(5)-1)
//D7
for (i<- 0 until Dk(6)) {
  if(i == 0){
    D7(0) := rb(5)
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
    rb(i) := ra(i-1)-DW(i)
  }else{
    rb(i) := -ra(i-1)-DW(i)
  }
}
//io.ra_out := ra(6)
io.ra_out := ra(6)
io.rb_out := rb(6)
}
