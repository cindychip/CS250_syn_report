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
 // val preamble = Input(DspComplex(gen.cloneType, gen.cloneType))
  val fbf_coeff = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new correlatorIo(gen).asInstanceOf[this.type]
}
class correlator[T <: Data:RealBits](gen: T, val n: Int) extends Module {
  val io = IO(new correlatorIo(gen))
  //state machine initialization
   val endSignal = Reg(init = 0.U) // for testing shift two signals
   val initial :: calculate :: shift :: Nil = Enum(3)
   val shiftState = Reg(init=initial)
   val counter = Reg(init = 0.U)
   //ShiftRegister
   val preamble = Vec(n, Reg(DspComplex(gen, gen)))
   val preambleEn = Reg(init = true.B)
   val delays = Vec(n, Reg(DspComplex(gen, gen)))
   val Multi   = Array.fill(n)(Module(new Multiply(gen)).io)
   val sum  = Wire(Vec(n, DspComplex(gen, gen)))
   val init = Reg(init = true.B)
   val ref = Reg(DspComplex(gen, gen))
   
   for (i<- 1 until n) {
          delays(i) := delays(i-1)
        }
        when(preambleEn){
	        for (i<- 1 until n) {
	          preamble(i) := preamble(i-1)
	        }
        }.otherwise{
        	for (i<- 0 until n) {
	          preamble(i) := preamble(i)
	        }
        }

   switch(shiftState) {
     is(initial) {      
        when (counter < n) {
          delays(0) := io.input_complex
          when(preambleEn){
          	preamble(0):=io.input_complex
     	  }
          counter := counter + 1.U
        }.otherwise{
          preambleEn := false.B
          counter := 0.U
          shiftState := calculate
        }
      }
     is(calculate){
        Multi(0).cx := preamble(0).conj()
        Multi(0).cy := delays(0)
        sum(0) := Multi(0).out
        for (i <- 1 until n) {
          Multi(i).cx := preamble(i).conj()
          Multi(i).cy := delays(i)
          sum(i) := Multi(i).out+sum(i-1)
        }
        when (init){
        	ref:= sum(n-1).conj()
        	//ref:= sum(n-1).conj().div2(DspContext.withComplexUse4Muls(true) { sum(n-1)*sum(n-1).conj()})
        	init := false.B
        }
        io.fbf_coeff := sum(n-1) //divide by ref
        io.output_complex := delays(n-1)
        shiftState := shift
      }
     is(shift){
      when (counter < 1.U) {
          delays(0) := io.input_complex
          counter := counter + 1.U
          endSignal := endSignal + 1.U
        }.otherwise{
          when (endSignal === 2.U){ //testing two input signals
            shiftState := initial
          }.otherwise{
            counter := 0.U
            shiftState := calculate
          }          
        }

      }
    }
  }






