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
import breeze.math.Complex
import breeze.signal._

class correlatorTests[T <: Data:RealBits](c: correlator[T]) extends DspTester(c) {
val ga128 = Array(1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1, 1, -1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1)
val gb128 = Array(-1, -1, 1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1, 1, -1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, -1, 1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1, -1, -1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, 1, -1, -1, -1, -1, 1, -1, 1, -1, 1, -1, -1, 1)
val n = 128
val Dk = Array(1, 8, 2, 4, 16, 32, 64) 
val DW = new Array[Int](7)
val ra = new Array[Int](7)
val rb = new Array[Int](7)
val D1 = new Array[Int](1)
val D2 = new Array[Int](8)
val D3 = new Array[Int](2)
val D4 = new Array[Int](4)
val D5 = new Array[Int](16)
val D6 = new Array[Int](32)
val D7 = new Array[Int](64)
for (i<-0 until n){
      // D1(0)=ga128(i)
      // DW(0)= D1(0)
      // rb(0)=-ga128(i)-DW(0)
      // ra(0)=-ga128(i)+DW(0)

      // D2(0)=rb(0)
      // for (j<-0 until Dk(1)){
      //   D2(j)= D2(j-1)
      // }
      // DW(1)= D2(Dk(1)-1)
      // rb(1)=-ra(0)-DW(1)
      // ra(1)=-ra(0)+DW(1)

      // D3(0)=rb(1)
      // for (j<-0 until Dk(2)){
      //   D3(j)= D3(j-1)
      // }
      // DW(2)= D3(Dk(2)-1)
      // rb(2)=-ra(1)-DW(2)
      // ra(2)=-ra(1)+DW(2)

      // D4(0)=rb(2)
      // for (j<-0 until Dk(3)){
      //   D4(j)= D4(j-1)
      // }
      // DW(3)= D4(Dk(3)-1)
      // rb(3)=-ra(2)-DW(3)
      // ra(3)=-ra(2)+DW(3)

      // D5(0)=rb(3)
      // for (j<-0 until Dk(4)){
      //   D5(j)= D5(j-1)
      // }
      // DW(4)= D5(Dk(4)-1)
      // rb(4)=ra(3)-DW(4)
      // ra(4)=ra(3)+DW(4)

      // D6(0)=rb(4)
      // for (j<-0 until Dk(5)){
      //   D6(j)= D6(j-1)
      // }
      // DW(5)= D5(Dk(5)-1)
      // rb(5)=-ra(4)-DW(5)
      // ra(5)=-ra(4)+DW(5)

      // D7(0)=rb(5)
      // for (j<-0 until Dk(6)){
      //   D7(j)= D7(j-1)
      // }
      // DW(6)= D5(Dk(6)-1)
      // rb(6)=-ra(5)-DW(6)
      // ra(6)=-ra(5)+DW(6)
    
  

    poke (c.io.input_complex.real,ga128(i))
    //poke (c.io.input_complex.real,0)
    poke (c.io.input_complex.imag, 0)
    step(1)
    println("ra ==================================> " + peek(c.io.ra_out.real))
   // println("expect================================>", ra(6))
  }

}



// for (i<-0 until n){
//   poke (c.io.input_complex.real,ga128(i))
//   poke (c.io.input_complex.imag, 0)
//   step(1)
//   println("ra ==================================> " + peek(c.io.ra_out.real))
// }
//println("ra -----------> " + peek(c.io.ra_out.real))



  // val len = 4
  // val size = 2
  // val real = Array.fill(len)(Random.nextDouble*2-1,)
  // val img = Array.fill(len)(Random.nextDouble*2-1)
  // val preamble_real = Array(11.0, 12.0)
  // val preamble_img = Array(9.0, 10.0)
  // val (expect_real, expect_img) = Adder(real, img, preamble_real, preamble_img,
  //                                    len, size)
  // for (i<-0 until len){
  //   poke (c.io.input_complex.real,real(i))
  //   poke (c.io.input_complex.imag, img(i))
  //   step(1)
  //   if (i>=size-1){
  //     expect (c.io.fbf_coeff.real, expect_real(i-size1,))
  //     expect (c.io.fbf_coeff.imag, expect_img(i-size1,))
  //     expect (c.io.output_complex.real, real(i-size+1))
  //     expect (c.io.output_complex.imag, img(i-size+1))

  //   }
  // }//end for


// Scala style testing
class correlatorSpec extends FlatSpec with Matchers {

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
dsptools.Driver.execute(() => new correlator(FixedPoint(32.W, 12.BP)), testOptions) { c =>      
  new correlatorTests(c)
    } should be (true)
  }
}

// object shiftReg{
//   def apply(inputSeq: Array[Int], length: Int): Int={
//     val reg = new Array[Int](length)
//     for (t<-0 until length){
//       reg(0) = inputSeq(t)
//       for (i <- length-1 to 1 by -1) {
//         reg(i) = reg(i-1)
//       }
//       }
//     return reg(length-1)
// }
// }
//   class ShiftRegisterTests(c: ShiftRegister) extends PeekPokeTester(c) {
//   val reg = Array.fill(4){ 0 }
//   for (t <- 0 until 64) {
//     val in = rnd.nextInt(2)
//     poke(c.io.in, in)
//     step(1)
//     for (i <- 3 to 1 by -1)
//       reg(i) = reg(i-1)
//     reg(0) = in
//     if (t >= 4) expect(c.io.out, reg(3))
//   }
// }

// }


// object Adder {
//   def apply(signal_real:Array[Double], signal_img: Array[Double], 
//             preamble_real:Array[Double], preamble_img:Array[Double], 
//             sig_len:Int , preamble_len:Int) : (Array[Double], Array[Double])={
//    val tmp_Re = new Array[Double](preamble_len)
//    val tmp_Im = new Array[Double](preamble_len)
//    val outRe = new Array[Double](sig_len-preamble_len+1)
//    val outIm = new Array[Double](sig_len-preamble_len+1)
//    for(j<-0 until (sig_len-preamble_len+1)){
//       for (i<-0 until preamble_len){
//       tmp_Re(i) = preamble_real(i)*signal_real(i+j)-(-preamble_img(i))*signal_img(i+j)
//       tmp_Im(i) = preamble_real(i)*signal_img(i+j)+(-preamble_img(i))*signal_real(i+j)
//       }
//       outRe(j) = tmp_Re.reduceLeft( _ + _ )
//       outIm(j) = tmp_Im.reduceLeft( _ + _ )
//     }
//    return (outRe, outIm)
//    }
//  }
