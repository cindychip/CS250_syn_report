#=======================================================================
# UCB VLSI FLOW: Toplevel Makefile
#-----------------------------------------------------------------------
# Yunsup Lee (yunsup@cs.berkeley.edu)
#
# The toplevel makefile connects together all the tools in the
# toolflow, by including Makefrags from subdirectories.
#
# to synthesize:
#  make dc
# to check rtl vs. gate-level netlist (implicitly runs dc):
#  make fm
# to place-and-route (implicitly runs dc)
#  make icc
# to measure energy (implicitly runs dc/icc):
#  make pt
#

default: pt-example

base_dir = $(abspath ../..)
vlsi_dir = $(abspath ..)
build_dir = $(abspath .)
sim_dir = $(abspath ./vcs-sim-gl-par)

include $(base_dir)/Makefrag
include $(build_dir)/Makefrag
include $(build_dir)/dc-syn/Makefrag
include $(build_dir)/icc-par/Makefrag
include $(build_dir)/vcs-sim-gl-par/Makefrag
include $(build_dir)/Makefrag-sim
include $(build_dir)/pt-pwr/Makefrag

pt-example: $(pt_cur_build_dir)/$(pt_reports_dir)/rv64ui-p-add.power.avg.max.report

.PHONY: dc fm icc pt
