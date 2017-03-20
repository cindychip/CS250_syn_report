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
class correlator[T <: Data:RealBits](gen: T, val n: Int ,val preamble: Seq[DspComplex[T]]) extends Module {
// Calculate preamble reference j
  val io = IO(new correlatorIo(gen))
  val preambleConj: Seq[DspComplex[T]] = preamble.map(tap => tap.conj())
  val product_ref : Seq[DspComplex[T]] = preamble.map{sl =>  DspContext.withComplexUse4Muls(true) { sl * sl.conj()}}
  val j = product_ref.reduceLeft{
    (left: DspComplex[T], right: DspComplex[T]) =>
    val reg = Reg(left.cloneType)
    reg := left
    reg + right  
  }
  //state machine
   val endSignal = Reg(init = 0.U) // for testing shift two signals
   val initial :: calculate :: shift :: Nil = Enum(3)
   val shiftState = Reg(init=initial)
   val counter = Reg(init = 0.U)
   //ShiftRegister
   val delays = Vec(n, Reg(DspComplex(gen, gen)))
   val buffer_complex = Vec(n, Reg(DspComplex(gen, gen)))
   val Multi   = Array.fill(n)(Module(new Multiply(gen)).io)
   val sum  = Wire(Vec(n+1, DspComplex(gen, gen)))
   switch(shiftState) {
     is(initial) {      
        for (i<- 1 until n) {
          delays(i) := delays(i-1)
        }
        when (counter < n) {
          delays(0) := io.input_complex
          counter := counter + 1.U
        }.otherwise{
          counter := 0.U
          shiftState := calculate
        }
      }
     is(calculate){
        Multi(0).cx := preambleConj(0)
        Multi(0).cy := delays(0)
        sum(0) := Multi(0).out
        for (i <- 1 until n) {
          Multi(i).cx := preambleConj(i)
          Multi(i).cy := delays(i)
          //Multi(i).cin := sum(i)
          //sum(i+1) := Multi(i).cout
          sum(i) := Multi(i).out+sum(i-1)
        }
        io.fbf_coeff := sum(n).divj()
        io.output_complex := delays(n-1)
        shiftState := shift
      }
     is(shift){
      when (counter < 1.U) {
          delays(0) := io.input_complex
          counter := counter + 1.U
          endSignal := endSignal + 1.U
        }.otherwise{
          when (endSignal === 2.U){
            shiftState := initial
          }.otherwise{
            counter := 0.U
            shiftState := calculate
          }          
        }

      }
    }
  }






