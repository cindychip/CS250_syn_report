#=======================================================================
# UCB VLSI FLOW: Makefile for pt-pwr
#-----------------------------------------------------------------------
# Yunsup Lee (yunsup@cs.berkeley.edu)
#
# This makefile will use Synopsys PrimeTime to check timing and analyze
# power of your gate-level verilog netlist.
#

default: all

base_dir = $(abspath ../../..)
vlsi_dir = $(abspath ../..)
build_dir = $(abspath ..)
sim_dir = $(abspath ../vcs-sim-gl-par)

include $(build_dir)/Makefrag
include $(build_dir)/dc-syn/Makefrag
include $(build_dir)/icc-par/Makefrag
#include $(build_dir)/vcs-sim-gl-par/Makefrag
include $(build_dir)/pt-pwr/Makefrag

#--------------------------------------------------------------------
# Default make target
#--------------------------------------------------------------------

.PHONY: pt

pt_sim = $(pt_cur_build_dir)/$(pt_reports_dir)/vcdplus.power.avg.max.report

all: $(pt_sim)
  

#--------------------------------------------------------------------
# Clean up
#--------------------------------------------------------------------

junk +=

clean:
	rm -rf build* current* $(junk) *~ \#*
