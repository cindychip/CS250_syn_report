#=======================================================================
# UCB VLSI FLOW: Makefile for vcs-sim-gl-syn
#-----------------------------------------------------------------------
# Yunsup Lee (yunsup@cs.berkeley.edu)
#
# This makefile will build a rtl simulator and run various tests to
# verify proper functionality.
#

default : all

base_dir = $(abspath ../../..)
vlsi_dir = $(abspath ../..)
build_dir = $(abspath ..)
sim_dir = .

include $(base_dir)/Makefrag
include $(build_dir)/Makefrag
include $(build_dir)/Makefrag-sim
include $(build_dir)/dc-syn/Makefrag
include $(build_dir)/vcs-sim-gl-syn/Makefrag

#--------------------------------------------------------------------
# Default make target
#--------------------------------------------------------------------

.PHONY: run all

all : $(simv) $(MODULE).$(CONFIG) $(MODULE).$(CONFIG).daidir

$(MODULE).$(CONFIG): $(simv)
	cp $(simv) $@

$(MODULE).$(CONFIG).daidir: $(simv)
	ln -s $(simv).daidir $@

#--------------------------------------------------------------------
# Clean up
#--------------------------------------------------------------------

junk += simv* csrc *.vpd *.key DVE* .vcs* timestamp *.out *.h *.a *.ucli  $(MODULE).$(CONFIG) $(MODULE).$(CONFIG).daidir

clean :
	rm -rf $(junk) *~ \#* *.log *.cmd *.daidir
