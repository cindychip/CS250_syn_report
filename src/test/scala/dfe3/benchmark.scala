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

// You create a tester that must extend DspTester to support Dsp type peeks/pokes (with doubles, complex, etc.)
class DecisionBlockTests[T <: Data:RealBits](c: DFE_decision[T]) extends DspTester(c) {
  var len = 4
  val real = Seq(1.2,2.2,-1.3,-1.7)
  val img = Seq(-1.1,1.1,1.1,-1.8)
  for (i <- 0 until len) {
   poke (c.io.input_real,real(i) )
   poke (c.io.input_img, img(i) )
   if (real(i)>=0) {
    expect(c.io.output_real, 0)
    if (img(i)>=0) {
     expect(c.io.output_img,0)
    }
    else {
     expect(c.io.output_img,0)
    }
   }
   else {
    expect(c.io.output_real,0)
    if (img(i)>=0) {
     expect(c.io.output_img,0)
    }
    else {
     expect(c.io.output_img,0)
    }
   }
    step(1)
  }//end for
}

// Scala style testing
class DFE_decisionSpec extends FlatSpec with Matchers {
  
  // If you don't want to use default tester options, you need to create your own DspTesterOptionsManager
  val testOptions = new DspTesterOptionsManager {
    // Customizing Dsp-specific tester features (unspecified options remain @ default values)
    dspTesterOptions = DspTesterOptions(
        // # of bits of error tolerance allowed by expect (for FixedPoint, UInt, SInt type classes)
        fixTolLSBs = 1,
        // Generate a Verilog testbench to mimic peek/poke testing
        genVerilogTb = true,
        // Show all tester interactions with the module (not just failed expects) on the console
        isVerbose = true)
    // Customizing Chisel tester features
    testerOptions = TesterOptions(
        // If set to true, prints out all nested peeks/pokes (i.e. for FixedPoint or DspReal, would
        // print out BigInt or base n versions of peeks/pokes -- rather than the proper decimal representation)
        isVerbose = false,
        // Default backend uses FirrtlInterpreter. If you want to simulate with the generated Verilog,
        // you need to switch the backend to Verilator. Note that tests currently need to be dumped in 
        // different output directories with Verilator; otherwise you run into weird concurrency issues (bug!)...
        backendName = "verilator")
    // Override default output directory while maintaining other default settings
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/simple_dsp_fix")
  }

  behavior of "simple dsp module"

  it should "properly add fixed point types" in {
    // Run the dsp tester by following this style: You need to pass in the Chisel Module [SimpleDspModule] 
    // to test and your created DspTesterOptionsManager [testOptions]. You must also specify the tester 
    // [SimpleDspModuleTester] to run with the module. This tester should be something that extends DspTester. 
    // Note that here, you're testing the module with inputs/outputs of FixedPoint type (Q15.12) 
    // and 3 registers (for retiming) at the output. You could alternatively use DspReal()
    // Scala keeps track of which tests pass/fail; the execute method returns true if the test passes. 
    // Supposedly, Chisel3 testing infrastructure might be overhauled to reduce the amount of boilerplate, 
    // but this is currently the endorsed way to do things.
    dsptools.Driver.execute(() => new DFE_decision(FixedPoint(32.W, 12.BP)), testOptions) { c =>
      new DecisionBlockTests(c)
    } should be (true)
  }

}