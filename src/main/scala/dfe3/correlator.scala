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
  val preambleConjImFix = preambleImFix.map(tap => -tap)
  val RefRe1 = preambleReFix.map{tap: T => tap*tap}
  val RefRe2 = preambleImFix.map{tap: T => tap*tap}
  val RefIm1 = preambleReFix.zip(preambleImFix).map{case(x,y) => -x*y }
  val RefIm2 = preambleImFix.zip(preambleReFix).map{case(x,y) => x*y }
  val RefRe = RefRe1.zip(RefRe2).map{case(x,y) => x+y}.reduceLeft( _ + _ )
  val RefIm = RefIm1.zip(RefIm2).map{case(x,y) => x+y}.reduceLeft( _ + _ )
  val preambleConjFix = preambleReFix.zip(preambleConjImFix).map{case(x,y) => DspComplex(x, y)}
  val Ref = DspComplex(RefRe, RefIm)

//state machine initialization
   val endSignal = RegInit(init = 0.U) // for testing shift two signals
   val initial :: calculate :: shift :: Nil = Enum(3)
   val shiftState = Reg(init=initial)
   val counter = Reg(init = 0.U)
   //ShiftRegister initialization
   val delays = Reg(Vec(n, DspComplex(gen, gen)))
  // val Multi   = Array.fill(n)(Module(new Multiply(gen)).io)
   val sum  = Wire(Vec(n, DspComplex(gen, gen)))
   // Set up ShiftRegister
   delays(0) := io.input_complex
   for (i<- 1 until n) {
          delays(i) := delays(i-1)
    }

   switch(shiftState) {
     is(initial) {    
     	when (counter < n){  
          counter := counter + 1.U
        }.otherwise{
         // preambleEn := false.B
          counter := 0.U
          shiftState := calculate
        }
      }
     is(calculate){
        sum(0) := DspContext.withComplexUse4Muls(true) { delays(0) * preambleConjFix(0) }
        for (i <- 1 until n) {
         sum(i) := sum(i-1) + DspContext.withComplexUse4Muls(true) { delays(i) * preambleConjFix(i) }
        }
        io.fbf_coeff := sum(n-1) //divide by ref
        io.output_complex := delays(n-1)
        shiftState := shift
      }
     is(shift){
      when (counter < 1.U) {
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







