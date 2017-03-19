// DFEE correlator
package dfe3

import chisel3._
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
  override def cloneType: this.type = new correlatorIo(gen).asInstanceOf[this.type]
}
class correlator[T <: Data:RealBits](gen: T, val n: Int ,val preamble: Seq[DspComplex[T]]) extends Module {
  val io = IO(new correlatorIo(gen))
  val preambleConj: Seq[DspComplex[T]] = preamble.map(tap => tap.conj())
  //sl => (sl, sl.map{case(x) => x.conj()}))
  //map {(x:Int) => x.asUInt
  // // calculate the sum of the product of the preamble  
  // val preambleReFix: Seq[T] = preambleRe.map ( tap =>ConvertableTo[FixedPoint].fromDouble(tap))
  // val preambleImFix: Seq[T] = preambleIm.map ( tap =>ConvertableTo[FixedPoint].fromDouble(tap))
  // val RefRe1 : Seq[T] = preambleReFix.transpose.map(sl => (sl, preambleReFix).zipped.map{case(x,y) => x*y })
  // val RefRe2 : Seq[T] = preambleImFix.transpose.map(sl => (sl, preambleImFix).zipped.map{case(x,y) => x*y })
  // val Ref : Seq[T] = RefRe1.transpose.map(sl => (sl, RefRe2).zipped.map{case(x,y) => x+y })
  // val Ref_sum = Ref.reduceLeft { (left: T, right: T) =>
  // 	val reg = Reg(left.cloneType)//?????initial reg to left? how? 
  //   reg + right
  // }  



}







////////////////////////////////////////////////////////
// val delays = (0 until n).scanLeft(ConvertableTo[FixedPoint].fromDouble(preambleRe(0)) { case (left, _) =>
//     val nextReg = Reg(gen.cloneType)
//     nextReg := left
//     nextReg
//   }






//val RefIm1 : Seq[T] = preambleReFix.transpose.map(sl => (sl, preambleImFix).zipped.map{case(x,y) => -x*y })
  //val RefIm1 : Seq[T] = preambleReFix.transpose.map(sl => (sl, preambleImFix).zipped.map{case(x,y) => x*y })
 //val rdata = Reg(next = Vec(initialV map {(x:Int) => x.asUInt})(raddr))
  // val RefRe1_reg = Vec.fill(n){ Reg(gen.cloneType) }
  // val RefRe2_reg = Vec.fill(n){ Reg(gen.cloneType) }
  // val RefIm1_reg = Vec.fill(n){ Reg(gen.cloneType) }
  // val RefIm2_reg = Vec.fill(n){ Reg(gen.cloneType) } 
  // val dotProduct1 = Module(new dotProduct(32, preambleRe))
  // val RefRe_reg = Reg (gen.cloneType)
  // sum_Reg := Mux(io.rst_count, 0.U, , RefRe_reg + RefRe1_Reg(i))


  // for (i<-0 until n){
  // 	RefRe1_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(preambleRe(i)* preambleRe(i)}
  // 	RefRe2_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(preambleIm(i)* preambleIm(i)}  
  // 	RefIm1_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(-preambleRe(i)* preambleIm(i)} 
  // 	RefIm2_Reg(i) := DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(preambleRe(i)* preambleIm(i)} 
  // }	
 // create a sequence of registers (head is io.input)



  // val products: Seq[T] = io.taps.map { tap: T =>
  //   io.input * tap
  // }
  //val last = Reg[T](outputGenerator)






  // when(io.input.valid) {
  //   last := nextLast
  // }







  // val last1 = Reg(products.head.cloneType)
  // last := products.reduceLeft { (left: T, right: T) =>
  //   val reg = Reg(left.cloneType)
  //   reg := left
  //   reg + right
  // }



