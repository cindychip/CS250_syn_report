//Henry Zhu
package dfe3

import chisel3._
import chisel3.util._
//import chisel3.core._
import chisel3.experimental.FixedPoint
import chisel3.iotesters.{Backend}
import chisel3.{Bundle, Module}
import dsptools.{DspContext, DspTester}
import dsptools.numbers.{FixedPointRing, DspComplexRing, DspComplex}
import dsptools.numbers.implicits._
import org.scalatest.{Matchers, FlatSpec}
import spire.algebra.Ring
import dsptools.numbers.{RealBits}

class fir_Io[T <: Data:RealBits](gen: T) extends Bundle {
  val input_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val tap_coeff_complex = Input(DspComplex(gen.cloneType, gen.cloneType))
  val tap_index = Input(UInt(10.W))
  val coef_en = Input(Bool())
  val output_complex = Output(DspComplex(gen.cloneType, gen.cloneType))
  val counter = Input(UInt(10.W))
  val buffer_rdata1_test = Output(DspComplex(gen.cloneType, gen.cloneType))
  val buffer_rdata2_test = Output(DspComplex(gen.cloneType, gen.cloneType))
  val buffer_rdata3_test = Output(DspComplex(gen.cloneType, gen.cloneType))
  val index0 = Output(UInt(10.W))
  val index1 = Output(UInt(10.W))
  val index2 = Output(UInt(10.W))
  override def cloneType: this.type = new fir_Io(gen).asInstanceOf[this.type]
}

// data width 32, 12
class fir[T <: Data:RealBits](gen: => T,var window_size: Int, var step_size: Int) extends Module {
  val io = IO(new fir_Io(gen))

  // Instantiated 1st SRAM 
  val sram_depth = 512
  val buffer_mem1 = SyncReadMem(DspComplex(gen.cloneType, gen.cloneType), 512)
  val buffer_wen = Wire(Bool()); buffer_wen := true.B //Default value  
  val buffer_raddr1 = Wire(UInt(log2Ceil(sram_depth).W)); buffer_raddr1 := 0.U
  val buffer_waddr = Wire(UInt(log2Ceil(sram_depth).W)); buffer_waddr := 0.U
  val buffer_wdata = Wire(DspComplex(gen.cloneType, gen.cloneType)); 
  val buffer_rdata1 = Wire(DspComplex(gen.cloneType, gen.cloneType));  

  // instantiated 2nd SRAM
  val buffer_mem2 = SyncReadMem(DspComplex(gen.cloneType, gen.cloneType), 512) 
  val buffer_raddr2 = Wire(UInt(log2Ceil(sram_depth).W)); buffer_raddr2 := 0.U
  val buffer_rdata2 = Wire(DspComplex(gen.cloneType, gen.cloneType));

  // Instantiated 3rd SRAM
  val buffer_mem3 = SyncReadMem(DspComplex(gen.cloneType, gen.cloneType), 512) 
  val buffer_raddr3 = Wire(UInt(log2Ceil(sram_depth).W)); buffer_raddr3 := 0.U
  val buffer_rdata3 = Wire(DspComplex(gen.cloneType, gen.cloneType));

  // read and write for all of the srams
  when(buffer_wen) {
    buffer_mem1.write(buffer_waddr, buffer_wdata)
  }

  when(buffer_wen) {
  buffer_mem2.write(buffer_waddr, buffer_wdata)
  }

  when(buffer_wen) {
    buffer_mem3.write(buffer_waddr, buffer_wdata) 
  }

  val index_count = Reg(init = 0.U(2.W))
  val index = Reg(Vec(3,0.U(10.W)))
  val buffer_complex = Reg(Vec(3, DspComplex(gen, gen)))

  // coefficient update block
  when (io.coef_en) {
    when(io.tap_coeff_complex.imag > 0 || io.tap_coeff_complex.real > 0 ||
          io.tap_coeff_complex.imag < 0 || io.tap_coeff_complex.real < 0) {
      index(index_count) := io.tap_index
      buffer_complex(index_count) := io.tap_coeff_complex
      index_count := index_count + 1.U    
    }
  }

  // read/write address update
  buffer_waddr := io.counter
  buffer_wdata := io.input_complex

  buffer_raddr1 := io.counter-index(0)
  buffer_raddr2 := io.counter-index(1)
  buffer_raddr3 := io.counter-index(2)


  when(buffer_raddr1===0.U){
  	buffer_rdata1 := buffer_mem1(0)
  }
  .otherwise{
  	buffer_rdata1 := buffer_mem1(buffer_raddr1)
  }
  when(buffer_raddr2===0.U){
  	buffer_rdata2 := buffer_mem2(0)
  }
  .otherwise{
  	buffer_rdata2 := buffer_mem2(buffer_raddr2)
  }
  when(buffer_raddr3===0.U){
  	buffer_rdata3 := buffer_mem3(0)
  }
  .otherwise{
  	buffer_rdata3 := buffer_mem3(buffer_raddr3)
  }


  // For testing
  io.buffer_rdata1_test := buffer_rdata1
  io.buffer_rdata2_test := buffer_rdata2
  io.buffer_rdata3_test := buffer_rdata3
  io.index0 := buffer_raddr1
  io.index1 := buffer_raddr2
  io.index2 := buffer_raddr3


  // final sum result 
  io.output_complex := buffer_complex(0)*buffer_rdata1 + buffer_complex(1)*buffer_rdata2 + buffer_complex(2)*buffer_rdata3
  //io.output_complex := delays(index(0))* buffer_complex(0) + 
  //                     delays(index(1))* buffer_complex(1) + 
  //                       delays(index(2))* buffer_complex(2)
}



object FirSramMain extends App {
  /** Fetch the configuration parameters.
    * We will look for a class with the name projectName.configClassName
    * @param projectName
    * @param topModuleName
    * @param configClassName
    * @return
    */
  /*def getParamsFromConfig(projectName: String, topModuleName: String, configClassName: String): Parameters = {
    println("ConfigClass: "+projectName+"."+configClassName)
    val config = try {
      Class.forName(s"$projectName.$configClassName").newInstance.asInstanceOf[Config]
    }catch{
      case e: java.lang.ClassNotFoundException =>
        throw new Exception(s"Could not find the cde.Config subclass you asked for " +
          "(i.e. \"$configClassName\"), did you misspell it?", e)
    }

    val world = config.toInstance
    val paramsFromConfig: Parameters = Parameters.root(world)
    paramsFromConfig
 }*/
  
  Driver.execute(args.drop(4), () => new fir(FixedPoint(16.W, 12.BP),5,3))
}








