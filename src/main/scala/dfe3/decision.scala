    //see LICENSE for license
//authors: Liheng Zhu
package dfe3

import math._
// Allows you to use Chisel Module, Bundle, etc.
import chisel3._
import dsptools._
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
import dsptools.numbers._


class DFE_decision(val W: Int = 32, val R: Int = 15) extends Module {
  val io = IO(new Bundle {
    val input_real = Input(FixedPoint(32.W,4.BP))
    val input_img  = Input(FixedPoint(32.W,4.BP)) 
    val output_real = Output(FixedPoint(32.W,4.BP))
    val output_img = Output(FixedPoint(32.W,4.BP))  
    })

val positive = DspContext.withBinaryPoint(8) { ConvertableTo[FixedPoint].fromDouble(sqrt(2.toDouble)) }
val negative = DspContext.withBinaryPoint(8) { ConvertableTo[FixedPoint].fromDouble(-sqrt(2.toDouble)) }

when(io.input_real(31.U)){
	when(io.input_img(31.U)){
		io.output_real := negative
		io.output_img := negative
	}
	.otherwise{
		io.output_real := negative
		io.output_img := positive
	}
}.otherwise {
	when(io.input_img(31.U)){
		io.output_real := positive
		io.output_img := negative
	}
	.otherwise{
		io.output_real := positive
		io.output_img := positive
	}
 }
}