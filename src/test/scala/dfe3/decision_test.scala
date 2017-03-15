package dfe3

//import chisel3._

import chisel3.iotesters._
import scala.util.Random

class DecisionBlockTests(c: DFE_decision) extends PeekPokeTester(c) {
  var len = 100 //Random.nextInt(2000)
  val real = Seq.fill(len)(Random.nextInt)
  val img = Seq.fill(len)(Random.nextInt)
  for (i <- 0 until len) {
   poke (c.io.input_real,real(i))
   poke (c.io.input_img, img(i))
   step(1)
   if (real(i)>=0) {
    expect(c.io.output_real, FixedPoint.fromDouble(sqrt(2.toDouble)))
    if (img(i)>=0) {
     expect(c.io.output_img,FixedPoint.fromDouble(sqrt(2.toDouble)))
    }
    else {
     expect(c.io.output_img,FixedPoint.fromDouble(sqrt(2.toDouble)))
    }
   }
   else {
    expect(c.io.output_real,FixedPoint.fromDouble(sqrt(2.toDouble)))
    if (img(i)>=0) {
     expect(c.io.output_img,FixedPoint.fromDouble(sqrt(2.toDouble)))
    }
    else {
     expect(c.io.output_img,FixedPoint.fromDouble(sqrt(2.toDouble)))
    }
   }
  }//end for

} //end module
 

class decisionTester extends ChiselFlatSpec {
    behavior of "DFE_decision"
    backends foreach {backend =>
        it should s"do the dfe decision function in $backend" in {
            Driver(() => new DFE_decision(32,15), backend)(c => new DecisionBlockTests(c)) should be (true)
        }
    }
}

object decisionTester extends App {
    Driver.execute(args, () => new DFE_decision(32,15)){ c => new DecisionBlockTests(c) }
}