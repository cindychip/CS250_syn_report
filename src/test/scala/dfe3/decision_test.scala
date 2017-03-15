package dfe3

//import chisel3._

import chisel3.iotesters._
import scala.util.Random


import math._
// Allows you to use Chisel Module, Bundle, etc.
//import chisel3._
//import dsptools._
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
import chisel3.iotesters.TesterOptions
// Scala unit testing style
import org.scalatest.{FlatSpec, Matchers}
import dsptools.numbers._



class DecisionBlockTests(c: DFE_decision) extends PeekPokeTester(c) {
  var len = 100 //Random.nextInt(2000)
  val real = Seq.fill(len)(Random.nextInt)
  val img = Seq.fill(len)(Random.nextInt)

  val positive = DspContext.withBinaryPoint(16) { ConvertableTo[FixedPoint].fromDouble(sqrt(2.toDouble)) }
  val negative = DspContext.withBinaryPoint(16) { ConvertableTo[FixedPoint].fromDouble(-sqrt(2.toDouble)) }


  for (i <- 0 until len) {
   poke (c.io.input_real,real(i))
   poke (c.io.input_img, img(i))
   step(1)
   if (real(i)>=0) {
    expect(c.io.output_real, positive)
    if (img(i)>=0) {
     expect(c.io.output_img,positive)
    }
    else {
     expect(c.io.output_img,negative)
    }
   }
   else {
    expect(c.io.output_real,negative)
    if (img(i)>=0) {
     expect(c.io.output_img,positive)
    }
    else {
     expect(c.io.output_img,negative)
    }
   }
  }//end for

} //end module
 

class decisionTester extends ChiselFlatSpec {
    behavior of "DFE_decision"
    backends foreach {backend =>
        it should s"do the dfe decision function in $backend" in {
            Driver(() => new DFE_decision(32,16), backend)(c => new DecisionBlockTests(c)) should be (true)
        }
    }
}

object decisionTester extends App {
    Driver.execute(args, () => new DFE_decision(32,16)){ c => new DecisionBlockTests(c) }
}