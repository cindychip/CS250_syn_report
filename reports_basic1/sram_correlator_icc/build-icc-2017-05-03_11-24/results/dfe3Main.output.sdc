###################################################################

# Created by write_sdc on Wed May  3 12:18:40 2017

###################################################################
set sdc_version 1.9

set_units -time ns -resistance MOhm -capacitance fF -voltage V -current uA
set_driving_cell -lib_cell INVX1_RVT [get_ports clock]
set_driving_cell -lib_cell INVX1_RVT [get_ports reset]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[15]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[14]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[13]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[12]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[11]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[10]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[9]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[8]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[7]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[6]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[5]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[4]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[3]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[2]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[1]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_real[0]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[15]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[14]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[13]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[12]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[11]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[10]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[9]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[8]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[7]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[6]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[5]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[4]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[3]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[2]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[1]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports {io_signal_in_imag[0]}]
set_driving_cell -lib_cell INVX1_RVT [get_ports io_enable]
set_driving_cell -lib_cell INVX1_RVT [get_ports io_reset]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[15]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[14]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[13]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[12]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[11]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[10]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[9]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[8]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[7]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[6]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[5]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[4]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[3]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[2]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[1]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_real[0]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[15]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[14]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[13]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[12]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[11]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[10]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[9]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[8]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[7]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[6]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[5]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[4]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[3]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[2]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[1]}]
set_load -pin_load 0.004 [get_ports {io_signal_out_imag[0]}]
set_propagated_clock [get_ports clock]
create_clock [get_ports clock]  -period 2  -waveform {0 1}
set_clock_uncertainty 0.04  [get_clocks clock]
group_path -name FEEDTHROUGH  -from [list [get_ports reset] [get_ports {io_signal_in_real[15]}] [get_ports  \
{io_signal_in_real[14]}] [get_ports {io_signal_in_real[13]}] [get_ports        \
{io_signal_in_real[12]}] [get_ports {io_signal_in_real[11]}] [get_ports        \
{io_signal_in_real[10]}] [get_ports {io_signal_in_real[9]}] [get_ports         \
{io_signal_in_real[8]}] [get_ports {io_signal_in_real[7]}] [get_ports          \
{io_signal_in_real[6]}] [get_ports {io_signal_in_real[5]}] [get_ports          \
{io_signal_in_real[4]}] [get_ports {io_signal_in_real[3]}] [get_ports          \
{io_signal_in_real[2]}] [get_ports {io_signal_in_real[1]}] [get_ports          \
{io_signal_in_real[0]}] [get_ports {io_signal_in_imag[15]}] [get_ports         \
{io_signal_in_imag[14]}] [get_ports {io_signal_in_imag[13]}] [get_ports        \
{io_signal_in_imag[12]}] [get_ports {io_signal_in_imag[11]}] [get_ports        \
{io_signal_in_imag[10]}] [get_ports {io_signal_in_imag[9]}] [get_ports         \
{io_signal_in_imag[8]}] [get_ports {io_signal_in_imag[7]}] [get_ports          \
{io_signal_in_imag[6]}] [get_ports {io_signal_in_imag[5]}] [get_ports          \
{io_signal_in_imag[4]}] [get_ports {io_signal_in_imag[3]}] [get_ports          \
{io_signal_in_imag[2]}] [get_ports {io_signal_in_imag[1]}] [get_ports          \
{io_signal_in_imag[0]}] [get_ports io_enable] [get_ports io_reset]]  -to [list [get_ports {io_signal_out_real[15]}] [get_ports                     \
{io_signal_out_real[14]}] [get_ports {io_signal_out_real[13]}] [get_ports      \
{io_signal_out_real[12]}] [get_ports {io_signal_out_real[11]}] [get_ports      \
{io_signal_out_real[10]}] [get_ports {io_signal_out_real[9]}] [get_ports       \
{io_signal_out_real[8]}] [get_ports {io_signal_out_real[7]}] [get_ports        \
{io_signal_out_real[6]}] [get_ports {io_signal_out_real[5]}] [get_ports        \
{io_signal_out_real[4]}] [get_ports {io_signal_out_real[3]}] [get_ports        \
{io_signal_out_real[2]}] [get_ports {io_signal_out_real[1]}] [get_ports        \
{io_signal_out_real[0]}] [get_ports {io_signal_out_imag[15]}] [get_ports       \
{io_signal_out_imag[14]}] [get_ports {io_signal_out_imag[13]}] [get_ports      \
{io_signal_out_imag[12]}] [get_ports {io_signal_out_imag[11]}] [get_ports      \
{io_signal_out_imag[10]}] [get_ports {io_signal_out_imag[9]}] [get_ports       \
{io_signal_out_imag[8]}] [get_ports {io_signal_out_imag[7]}] [get_ports        \
{io_signal_out_imag[6]}] [get_ports {io_signal_out_imag[5]}] [get_ports        \
{io_signal_out_imag[4]}] [get_ports {io_signal_out_imag[3]}] [get_ports        \
{io_signal_out_imag[2]}] [get_ports {io_signal_out_imag[1]}] [get_ports        \
{io_signal_out_imag[0]}]]
group_path -name REGIN  -from [list [get_ports reset] [get_ports {io_signal_in_real[15]}] [get_ports  \
{io_signal_in_real[14]}] [get_ports {io_signal_in_real[13]}] [get_ports        \
{io_signal_in_real[12]}] [get_ports {io_signal_in_real[11]}] [get_ports        \
{io_signal_in_real[10]}] [get_ports {io_signal_in_real[9]}] [get_ports         \
{io_signal_in_real[8]}] [get_ports {io_signal_in_real[7]}] [get_ports          \
{io_signal_in_real[6]}] [get_ports {io_signal_in_real[5]}] [get_ports          \
{io_signal_in_real[4]}] [get_ports {io_signal_in_real[3]}] [get_ports          \
{io_signal_in_real[2]}] [get_ports {io_signal_in_real[1]}] [get_ports          \
{io_signal_in_real[0]}] [get_ports {io_signal_in_imag[15]}] [get_ports         \
{io_signal_in_imag[14]}] [get_ports {io_signal_in_imag[13]}] [get_ports        \
{io_signal_in_imag[12]}] [get_ports {io_signal_in_imag[11]}] [get_ports        \
{io_signal_in_imag[10]}] [get_ports {io_signal_in_imag[9]}] [get_ports         \
{io_signal_in_imag[8]}] [get_ports {io_signal_in_imag[7]}] [get_ports          \
{io_signal_in_imag[6]}] [get_ports {io_signal_in_imag[5]}] [get_ports          \
{io_signal_in_imag[4]}] [get_ports {io_signal_in_imag[3]}] [get_ports          \
{io_signal_in_imag[2]}] [get_ports {io_signal_in_imag[1]}] [get_ports          \
{io_signal_in_imag[0]}] [get_ports io_enable] [get_ports io_reset]]
group_path -name REGOUT  -to [list [get_ports {io_signal_out_real[15]}] [get_ports                     \
{io_signal_out_real[14]}] [get_ports {io_signal_out_real[13]}] [get_ports      \
{io_signal_out_real[12]}] [get_ports {io_signal_out_real[11]}] [get_ports      \
{io_signal_out_real[10]}] [get_ports {io_signal_out_real[9]}] [get_ports       \
{io_signal_out_real[8]}] [get_ports {io_signal_out_real[7]}] [get_ports        \
{io_signal_out_real[6]}] [get_ports {io_signal_out_real[5]}] [get_ports        \
{io_signal_out_real[4]}] [get_ports {io_signal_out_real[3]}] [get_ports        \
{io_signal_out_real[2]}] [get_ports {io_signal_out_real[1]}] [get_ports        \
{io_signal_out_real[0]}] [get_ports {io_signal_out_imag[15]}] [get_ports       \
{io_signal_out_imag[14]}] [get_ports {io_signal_out_imag[13]}] [get_ports      \
{io_signal_out_imag[12]}] [get_ports {io_signal_out_imag[11]}] [get_ports      \
{io_signal_out_imag[10]}] [get_ports {io_signal_out_imag[9]}] [get_ports       \
{io_signal_out_imag[8]}] [get_ports {io_signal_out_imag[7]}] [get_ports        \
{io_signal_out_imag[6]}] [get_ports {io_signal_out_imag[5]}] [get_ports        \
{io_signal_out_imag[4]}] [get_ports {io_signal_out_imag[3]}] [get_ports        \
{io_signal_out_imag[2]}] [get_ports {io_signal_out_imag[1]}] [get_ports        \
{io_signal_out_imag[0]}]]
set_input_delay -clock clock  0.1  [get_ports reset]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[15]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[14]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[13]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[12]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[11]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[10]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[9]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[8]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[7]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[6]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[5]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[4]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[3]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[2]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[1]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_real[0]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[15]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[14]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[13]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[12]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[11]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[10]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[9]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[8]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[7]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[6]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[5]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[4]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[3]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[2]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[1]}]
set_input_delay -clock clock  0.1  [get_ports {io_signal_in_imag[0]}]
set_input_delay -clock clock  0.1  [get_ports io_enable]
set_input_delay -clock clock  0.1  [get_ports io_reset]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[15]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[14]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[13]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[12]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[11]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[10]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[9]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[8]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[7]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[6]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[5]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[4]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[3]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[2]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[1]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_real[0]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[15]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[14]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[13]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[12]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[11]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[10]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[9]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[8]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[7]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[6]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[5]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[4]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[3]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[2]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[1]}]
set_output_delay -clock clock  0.1  [get_ports {io_signal_out_imag[0]}]
set_timing_derate -late -net_delay  1.01 
set_timing_derate -early -net_delay  0.99 
set_timing_derate -late -cell_delay 1.01 [current_design]
set_timing_derate -early -cell_delay 0.99 [current_design]
