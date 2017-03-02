#=======================================================================
# UCB Chisel Flow: Makefile 
#-----------------------------------------------------------------------
# James Martin
#
# This makefile will generate verilog files or an emulator from chisel code

include Makefrag

project_main := $(PROJ).$(MODEL)
src_files    := $(src_path)/$(PROJ)/*.scala

tester_main  := $(PROJ).$(TEST_MODEL)
test_files   := $(test_path)/$(PROJ)/*.scala

vcs_timestamp       := test_run_dir/vcs/timestamp
verilator_timestamp := test_run_dir/verilator/timestamp
firrtl_timestamp    := test_run_dir/firrtl/timestamp

firrtl_timestamp_all := test_run_dir/timestamp_all

vlsi_gen_dir    := build/vlsi/generated-src

verilog_file    := $(MODULE).$(CONFIG).v
verilog_harness := $(MODULE)-harness.v

with_srams_vcs_dir := build/vlsi/vcs-sim-rtl
post_syn_vcs_dir   := build/vlsi/vcs-sim-gl-syn
post_par_vcs_dir   := build/vlsi/vcs-sim-gl-par

#-----------------------------------------------------------------------
# Run Unit Tests
#-----------------------------------------------------------------------
# By not specifying a main, sbt will prompt for a main.
test-unit: test-unit-firrtl

test-unit-firrtl: $(src_files) $(test_files)
	sbt "test:run $(PROJ) $(MODULE) $(CONFIG) --backend-name firrtl    --target-dir test_run_dir/firrtl"

test-unit-verilator: $(src_files) $(test_files)
	sbt "test:run $(PROJ) $(MODULE) $(CONFIG) --backend-name verilator --target-dir test_run_dir/verilator"

test-unit-vcs: $(src_files) $(test_files)
	sbt "test:run $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs       --target-dir test_run_dir/vcs"
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Run All Tests
#-----------------------------------------------------------------------
test-all: test-all-firrtl

test-all-firrtl: $(firrtl_timestamp_all)

$(firrtl_timestamp_all): $(src_files) $(test_files)
	sbt test
	date > $(firrtl_timestamp_all)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Run Top Level Test
#-----------------------------------------------------------------------
test: test-firrtl

test-verilator: $(verilator_timestamp)

test-vcs: $(vcs_timestamp)

test-firrtl: $(firrtl_timestamp)

$(verilator_timestamp): $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name verilator --target-dir test_run_dir/verilator"
	date > $(verilator_timestamp)

$(vcs_timestamp): $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs       --target-dir test_run_dir/vcs"
	date > $(vcs_timestamp)

$(firrtl_timestamp): $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name firrtl    --target-dir test_run_dir/firrtl"
	date > $(firrtl_timestamp)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#  Reports
#-----------------------------------------------------------------------
reports: firrtl-report vcs-report verilator-report

firrtl-report: build/firrtl-report

vcs-report: build/vcs-report

verilator-report: build/verilator-report

build/firrtl-report: $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name firrtl    --target-dir test_run_dir/firrtl" > $@

build/verilator-report: $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name verilator --target-dir test_run_dir/verilator" > $@

build/vcs-report: $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs       --target-dir test_run_dir/vcs" > $@
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Generate Verilog Files
#-----------------------------------------------------------------------
$(vlsi_gen_dir)/$(verilog_file): $(src_files)
	sbt "run-main $(project_main) $(PROJ) $(MODULE) $(CONFIG) --target-dir $(vlsi_gen_dir)  -frsq -c:$(MODULE):-o:$(vlsi_gen_dir)/mem.conf -o $@"

verilog: $(vlsi_gen_dir)/$(verilog_file)

$(vlsi_gen_dir)/$(CONFIG)/$(verilog_harness): $(top_level_defn_files)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs --target-dir $(vlsi_gen_dir)/$(CONFIG) --top-name $(MODULE)"

verilog-harness: $(vlsi_gen_dir)/$(CONFIG)/$(verilog_harness)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#  VLSI
#-----------------------------------------------------------------------
test-with-srams: build/vlsi/vcs-with-srams-report

$(with_srams_vcs_dir)/$(MODULE).$(CONFIG): verilog-harness verilog
	cd $(with_srams_vcs_dir) && make CONFIG=$(CONFIG)
	cd ../../..

build/vlsi/vcs-with-srams-report: $(with_srams_vcs_dir)/$(MODULE)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs --target-dir test_run_dir/vcs-with-srams --test-command $<" > $@

test-post-syn: build/vlsi/vcs-syn-report

$(post_syn_vcs_dir)/$(MODULE).$(CONFIG): verilog-harness verilog
	cd $(post_syn_vcs_dir) && make CONFIG=$(CONFIG)
	cd ../../..

build/vlsi/vcs-syn-report: $(post_syn_vcs_dir)/$(MODULE).$(CONFIG)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs --target-dir test_run_dir/vcs-post-syn --test-command $<" > $@

test-post-par: build/vlsi/vcs-par-report

$(post_par_vcs_dir)/$(MODULE).$(CONFIG): verilog-harness verilog
	cd $(post_par_vcs_dir) && make CONFIG=$(CONFIG)
	cd ../../..

build/vlsi/vcs-par-report: $(post_par_vcs_dir)/$(MODULE).$(CONFIG)
	sbt "test:run-main $(tester_main) $(PROJ) $(MODULE) $(CONFIG) --backend-name vcs --target-dir test_run_dir/vcs-post-par --test-command $<" > $@
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# 	Generate Config Files
#-----------------------------------------------------------------------
config: $(src_files)
	sbt "run-main $(PROJ).$(CONFIG_FUNC) $(PROJ) $(MODULE) $(CONFIG) $(src_path)/config --targetDir $(src_path)/config"
#-----------------------------------------------------------------------

clean:
	rm -rf build/vlsi/generated-src target csrc ucli.key

deepclean: clean
	#cd build/vlsi && make clean
	cd build/vlsi/dc-syn && make clean
	cd build/vlsi/icc-par && make clean
	cd build/vlsi/vcs-sim-rtl && make clean
	rm build/vlsi/vcs-sim-gl-syn/$(MODULE)*
	cd build/vlsi/vcs-sim-gl-syn && make clean
	rm build/vlsi/vcs-sim-gl-syn/$(MODULE)*
	cd build/vlsi/vcs-sim-gl-par && make clean
	rm build/vlsi/vcs-sim-gl-par/$(MODULE)*

.PHONY: test-unit test-unit-firrtl test-unit-vcs test-all test-all-firrtl test test-vcs test-verilator test-firrtl \
		verilog verilog-harness test-post-syn test-post-par config deepclean clean
