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

class MultiplyIo[T <: Data:RealBits](gen: T) extends Bundle {
  val cx = Input(DspComplex(gen.cloneType, gen.cloneType))
  val cy = Input(DspComplex(gen.cloneType, gen.cloneType))
  val out = Output(DspComplex(gen.cloneType, gen.cloneType))
  override def cloneType: this.type = new correlatorIo(gen).asInstanceOf[this.type]
}
class Multiply[T <: Data:RealBits](gen: T) extends Module {
    val io = IO(new MultiplyIo(gen))
    //Generate product
    io.out := DspContext.withComplexUse4Muls(true) {io.cx * io.cy}
}