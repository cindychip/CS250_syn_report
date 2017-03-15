package dfe3

import chisel3._
import scala.util.Random
import chisel3.experimental.FixedPoint
import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import iotesters.TesterOptions
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._

class DecisionBlockTests[T <: Data:RealBits](c: DFE_decision[T]) extends DspTester(c) {
  var len = Random.nextInt(2000)
  val real = Seq.fill(len)(Random.nextDouble*2-1)
  val img = Seq.fill(len)(Random.nextDouble*2-1)

  val positive = sqrt(2.toDouble)/2
  val negative = -sqrt(2.toDouble)/2

  for (i <- 0 until len) {
   poke (c.io.input_real,real(i) )
   poke (c.io.input_img, img(i) )
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
    step(1)
  }//end for
}

// Scala style testing
class DFE_decisionSpec extends FlatSpec with Matchers {
  
  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = false,
        backendName = "verilator")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/simple_dsp_fix")
  }

  behavior of "simple dsp module"

  it should "properly add fixed point types" in {
    dsptools.Driver.execute(() => new DFE_decision(FixedPoint(32.W, 12.BP)), testOptions) { c =>
      new DecisionBlockTests(c)
    } should be (true)
  }

}