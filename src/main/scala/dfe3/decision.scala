package dfe3

// Allows you to use Chisel Module, Bundle, etc.
import chisel3._
// Allows you to use FixedPoint
import chisel3.experimental.FixedPoint
// If you want to take advantage of type classes >> Data:RealBits (i.e. pass in FixedPoint or DspReal)
import dsptools.numbers.{RealBits}
// Required for you to use operators defined via type classes (+ has special Dsp overflow behavior, etc.)
import dsptools.numbers.implicits._
// Enables you to set DspContext's for things like overflow behavior, rounding modes, etc.
import dsptools.DspContext
// Use DspTester, specify options for testing (i.e. expect tolerances on fixed point, etc.)
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
// Allows you to modify default Chisel tester behavior (note that DspTester is a special version of Chisel tester)
import iotesters.TesterOptions
// Scala unit testing style
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._



// IO Bundle. Note that when you parameterize the bundle, you MUST override cloneType.
// This also creates x, y, z inputs/outputs (direction must be specified at some IO hierarchy level)
// of the type you specify via gen (must be Data:RealBits = UInt, SInt, FixedPoint, DspReal)
class DFE_decisionIo[T <: Data:RealBits](gen: T) extends Bundle {
  val input_real = Input(gen.cloneType)
  val input_img  = Input(gen.cloneType)
  val output_real = Output(gen.cloneType)
  val output_img  = Output(gen.cloneType)
  override def cloneType: this.type = new DFE_decisionIo(gen).asInstanceOf[this.type]
}

// Parameterized Chisel Module; takes in type parameters as explained above
class DFE_decision[T <: Data:RealBits](gen: T) extends Module {
  // This is how you declare an IO with parameters
  val io = IO(new DFE_decisionIo(gen))
  // Output will be current x + y addPipes clock cycles later
  // Note that this relies on the fact that type classes have a special + that
  // add addPipes # of ShiftRegister after the sum. If you don't wrap the sum in 
  // DspContext.withNumAddPipes(addPipes), the default # of addPipes is used.
  //DspContext.withNumAddPipes(addPipes) { 
  //  io.z := io.x + io.y
  //}
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