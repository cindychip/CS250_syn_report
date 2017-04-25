package dfe3
import chisel3.Data
import chisel3.iotesters._
import scala.util.Random
import chisel3.experimental.FixedPoint
import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import chisel3.iotesters.TesterOptions
import org.scalatest.{FlatSpec, Matchers}
import math._
import dsptools.numbers._
import breeze.math.Complex
import breeze.signal._
import scala.io.Source  // Added

class dfeTests[T <: Data:RealBits](c: dfe3Main[T]) extends DspTester(c) {
val real = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/filter_real2.txt").getLines.toArray.map(x => x.toDouble)
val imag = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/filter_imag2.txt").getLines.toArray.map(x => x.toDouble)
val test_real = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/test_real2.txt").getLines.toArray.map(x => x.toDouble)
val test_imag = Source.fromFile("/scratch/cs250-aac/dfe/src/test/scala/dfe3/test_imag2.txt").getLines.toArray.map(x => x.toDouble)

val n = real.length
    
for (j<-0 until 2) {
poke (c.io.reset, true)
step(1)
poke (c.io.enable, true)
poke (c.io.reset, false)
step (1)

for (i<-0 until n) {
    poke (c.io.signal_in.real,real(i))
    poke (c.io.signal_in.imag, imag(i))
    poke (c.io.enable, true)
    poke (c.io.reset, false)
    //peek (c.io.debug)
    //peek (c.io.output_debug1)
    //peek (c.io.output_debug2)
    //peek (c.io.output_debug3)
    //peek (c.io.output_debug4)
    //peek (c.io.output_debug5)


    if(i > 255) { ///correct
      expect(c.io.signal_out.real,test_real(i-256))
      expect(c.io.signal_out.imag,test_imag(i-256))
    }
    step(1)
    }
  }
}

// Scala style testing
class dfeSpec extends FlatSpec with Matchers {

  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = false,
        backendName = "verilator")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/correlator_fix")
  }

  behavior of "correlator module"

  it should "properly add fixed point types" in {
dsptools.Driver.execute(() => new dfe3Main(FixedPoint(22, 12)), testOptions) { c =>      
  new dfeTests(c)
    } should be (true)
  }
}


object dfeTester extends App {
  //We pass in some positional arguments to make things easier
  //This should be integrated with the CLI flags at some point
  //but is how rocket-chip accomplishes this
  //val paramsFromConfig = Sha3AccelMain.getParamsFromConfig(projectName = args(0), topModuleName = args(1), configClassName = args(2))
  Driver.execute(args.drop(3),() => new dfe3Main(FixedPoint(22, 12))){ c => new dfeTests(c) }
}


