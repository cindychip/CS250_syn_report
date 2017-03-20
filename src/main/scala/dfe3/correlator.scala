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


// import scala.collection.mutable.HashMap
// import scala.collection.mutable.ArrayBuffer
// import scala.util.Random
// import cde.{Parameters, Config, Dump, Knob, Ex, ViewSym}
// import cde.Implicits._

class correlatorIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val fbf_coeff = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new correlatorIo(gen).asInstanceOf[this.type]
}
class correlator[T <: Data:RealBits](gen: T, val n: Int ,val preamble: Seq[DspComplex[T]]) extends Module {
// shift preamble reference 
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
   val initial :: read :: shift :: Nil = Enum(3)
  
   val shiftState = Reg(init=initial)
   val counter = Reg(init = 0.U)
   //ShiftRegister
   val delays = Vec(n, Reg(DspComplex(gen, gen)))
   val buffer_complex = Vec(n, Reg(DspComplex(gen, gen)))
   val preambleConj_Reg = Reg(Vec(preambleConj))
   //val result = Reg(DspComplex(gen, gen))
   val Multi   = Array.fill(n)(Module(new Multiply(gen)).io)
   val carry  = Wire(Vec(n+1, DspComplex(gen, gen)))
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
          shiftState := read
        }
      }
     is(read){
        Multi(0).cx := preambleConj(0)
        Multi(0).cy := delays(0)
        carry(0) := Multi(0).out
        for (i <- 1 until n) {
          Multi(i).cx := preambleConj(i)
          Multi(i).cy := delays(i)
          //Multi(i).cin := carry(i)
          //carry(i+1) := Multi(i).cout
          carry(i) := Multi(i).out+carry(i-1)
        }
        io.output_complex = carry(n)
        shiftState := shift
      }
     is(shift){

      }
    }
  }












//       when (counter < n) {
//         result := result + buffer_complex(counter)
//         counter := counter + 1.U 
//       }.otherwise{
//         io.fbf_coeff := result.divj()
//         io.output_complex := delays(n-1.U)
//        counter := 0.U
//        shiftState := initial 
//       }




//       buffer_complex(counter) := DspContext.withComplexUse4Muls(true) { delays(counter) * preambleConj_Reg(counter)}
//       counter := counter + 1.U
//       }.otherwise{
//         counter := 1.U
//         result := buffer_complex(0.U)
//         shiftState := shift
//       }

//   object Adder{
//   def apply(x: Seq[DspComplex[T <: Data:RealBits]], n: Int): DspComplex[T <: Data:RealBits] = {
//     var sum = 0 
//     if (n == 0){
//       sum = x(n)
//     }else{
//       sum = Adder(x,n-1)+x(n)
//     }
//     return sum
//   } 
// }

//  when (coeff_count < window_size.asUInt) {
//     buffer_complex(coeff_count) := io.tap_coeff_complex
//     coeff_count := coeff_count + 1.U



// ShiftRegister
//   val delays = Vec(n, Reg(DspComplex(gen, gen)))
//   delays(0) := io.input_complex
//   for (i<- 1 until window_size) {
//       delays(i) := delays(i-1)
//   }
//   val coeff_count = Reg(init = 0.U) //max 512 in darpa
//   val buffer_complex = Vec(window_size, Reg(DspComplex(gen, gen))) //vector of reg
//   when (coeff_count < window_size.asUInt) {
//     buffer_complex(coeff_count) := io.tap_coeff_complex
//     coeff_count := coeff_count + 1
//   }.otherwise {
//     coeff_count := 0.U
//     buffer_complex(coeff_count) := io.tap_coeff_complex
//   }
//   //delays.zip(buffer_complex).map{case(x,y) =>x*y}
//   io.output_complex := delays(0) * buffer_complex(0) + delays(1) * buffer_complex(1)


//   val delays = Reg(Vec(n, DspComplex(gen, gen)))
//   val coef = Reg(Vec(n, DspComplex(gen, gen)))
//   delays(0) := io.input_complex
 
//   for (i<- 1 until window_size) {
//     delays(i) := delays(i-1)
//   }

//   ShiftRegister(in:Data, n:Int, [en:Bool])

//   val products: Seq[DspComplex[T]] = preambleConj.map { sl: DspComplex[T] =>
//     DspContext.withComplexUse4Muls(true) { sl * io.input_complex}
//   }
//   val last = Reg(products.head.cloneType)
//   last := products.reduceLeft { 
//     (left: DspComplex[T], right: DspComplex[T]) =>
//     val reg = Reg(left.cloneType)
//     reg := left
//     reg + right
//   }

//   io.fbf_coeff := last.divj()
//   io.output := 
//     val products: Seq[T] = io.taps.reverse.map { tap: T =>
//     io.input * tap
//   }
 







// val sum: DspComplex[T] = preamble.map((preambleConj,_).zipped.map(DspContext.withComplexUse4Muls(true)(_*_)))

//     //val t : DspComplex[T] = implicitly[ConvertableTo[DspComplex[T]]].fromType(tap)
//     tap.mul(tap.conj())
//   }
//   DspContext.withComplexUse4Muls(true) { ca * cb }
// (sl, preambleConj).zipped.map{case(x,y) => x.mul(y)}}
//   sl => (sl, sl.map{case(x) => x.conj()}))
//   map {(x:Int) => x.asUInt
//   // shift the sum of the product of the preamble  
//   val preambleReFix: Seq[T] = preambleRe.map ( tap =>ConvertableTo[FixedPoint].fromDouble(tap))
//   val preambleImFix: Seq[T] = preambleIm.map ( tap =>ConvertableTo[FixedPoint].fromDouble(tap))
//   val RefRe1 : Seq[T] = preambleReFix.transpose.map(sl => (sl, preambleReFix).zipped.map{case(x,y) => x*y })
//   val RefRe2 : Seq[T] = preambleImFix.transpose.map(sl => (sl, preambleImFix).zipped.map{case(x,y) => x*y })
//   val Ref : Seq[T] = RefRe1.transpose.map(sl => (sl, RefRe2).zipped.map{case(x,y) => x+y })
//   val Ref_sum = Ref.reduceLeft { (left: T, right: T) =>
//    val reg = Reg(left.cloneType)//?????initial reg to left? how? 
//     reg + right
//   }  
// val products: Seq[T] = taps.reverse.map { tap =>
//     val t : T = implicitly[ConvertableTo[T]].fromType(tap)
//     io.input.bits * t
//   }
// //////////////////////////////////////////////////////
// val delays = (0 until n).scanLeft(ConvertableTo[FixedPoint].fromDouble(preambleRe(0)) { case (left, _) =>
//     val nextReg = Reg(gen.cloneType)
//     nextReg := left
//     nextReg
//   }






// val RefIm1 : Seq[T] = preambleReFix.transpose.map(sl => (sl, preambleImFix).zipped.map{case(x,y) => -x*y })
//   val RefIm1 : Seq[T] = preambleReFix.transpose.map(sl => (sl, preambleImFix).zipped.map{case(x,y) => x*y })
//  val rdata = Reg(next = Vec(initialV map {(x:Int) => x.asUInt})(raddr))
//   val RefRe1_reg = Vec.fill(n){ Reg(gen.cloneType) }
//   val RefRe2_reg = Vec.fill(n){ Reg(gen.cloneType) }
//   val RefIm1_reg = Vec.fill(n){ Reg(gen.cloneType) }
//   val RefIm2_reg = Vec.fill(n){ Reg(gen.cloneType) } 
//   val dotProduct1 = Module(new dotProduct(32, preambleRe))
//   val RefRe_reg = Reg (gen.cloneType)
//   sum_Reg := Mux(io.rst_count, 0.U, , RefRe_reg + RefRe1_Reg(i))


//   for (i<-0 until n){
//   	RefRe1_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(preambleRe(i)* preambleRe(i)}
//   	RefRe2_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(preambleIm(i)* preambleIm(i)}  
//   	RefIm1_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(-preambleRe(i)* preambleIm(i)} 
//   	RefIm2_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(preambleRe(i)* preambleIm(i)} 
//   }	
//  create a sequence of registers (head is io.input)



//   val products: Seq[T] = io.taps.map { tap: T =>
//     io.input * tap
//   }
//   val last = Reg[T](outputGenerator)






//   when(io.input.valid) {
//     last := nextLast
//   }







//   val last1 = Reg(products.head.cloneType)
//   last := products.reduceLeft { (left: T, right: T) =>
//     val reg = Reg(left.cloneType)
//     reg := left
//     reg + right
//   }



