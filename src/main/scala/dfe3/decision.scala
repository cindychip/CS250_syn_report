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
import dsptools.numbers._


class DFE_decisionIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_real = Input(gen.cloneType)
  val input_img  = Input(gen.cloneType)
  val output_real = Output(gen.cloneType)
  val output_img  = Output(gen.cloneType)
  override def cloneType: this.type = new DFE_decisionIo(gen).asInstanceOf[this.type]
}

class DFE_decision[T <: Data:RealBits](gen: T) extends Module {
  val io = IO(new DFE_decisionIo(gen))
  
  val positive = DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(sqrt(0.5.toDouble)) }
  val negative = DspContext.withBinaryPoint(12) { ConvertableTo[FixedPoint].fromDouble(-sqrt(0.5.toDouble)) }

  when(io.input_real<0){
   when(io.input_img<0){
      io.output_real := negative
      io.output_img := negative
    }
    .otherwise{
      io.output_real := negative
      io.output_img := positive
    }
  }.otherwise {
    when(io.input_img<0){
      io.output_real := positive
      io.output_img := negative
    }
    .otherwise{
      io.output_real := positive
      io.output_img := positive
    }
  }

}