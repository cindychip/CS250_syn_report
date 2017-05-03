//Henry Zhu
package dfe3

import chisel3._
import chisel3.experimental.FixedPoint
import chisel3.iotesters.{Backend}
import chisel3.{Bundle, Module}
import dsptools.{DspContext, DspTester}
import dsptools.numbers.{FixedPointRing, DspComplexRing, DspComplex}
import dsptools.numbers.implicits._
import org.scalatest.{Matchers, FlatSpec}
import spire.algebra.Ring
import dsptools.numbers.{RealBits}


class dfe3Io[T <: Data:RealBits](gen: T, var S_w: Int, var C_w: Int, var bp: Int) extends Bundle {
  val signal_in = Input(DspComplex(FixedPoint(S_w, bp) ))
  val signal_out = Output(DspComplex(FixedPoint(S_w, bp) ))
  val enable = Input(Bool())
  val reset = Input(Bool())
}

class dfe3Main[T <: Data:RealBits](gen: T, var S_w: Int, var C_w: Int, var bp: Int) extends Module {
 val io = IO(new dfe3Io(gen, S_w, C_w, bp))
 
 val dpath = Module(new dpathtotal(gen, S_w, C_w, bp)).io
 val ctrl = Module(new ctrl(gen, C_w, bp)).io

 ctrl.enable := io.enable
 ctrl.reset := io.reset  

 dpath.signal_in := io.signal_in
 io.signal_out := dpath.signal_out

 ctrl.stage <> dpath.stage
 ctrl.count <> dpath.count
 ctrl.fbf_coeff <> dpath.coeff_out
 ctrl.coeff_output <> dpath.coeff_in
 ctrl.tap_en <> dpath.tap_en
 ctrl.lms_en <> dpath.lms_en
}



object dfe3MainTest extends App {
  var S_w = 16 //22 
  var C_w = 22 
  var bp = 12
  
  Driver.execute(args.drop(3), () => new dfe3Main(FixedPoint(22, 12), S_w, C_w, bp))

}
