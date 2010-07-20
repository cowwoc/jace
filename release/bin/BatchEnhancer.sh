export SCRIPT_PATH="$(readlink -f $(dirname "$0"))"
export JACE_HOME=%SCRIPT_PATH%../..
export CLASSPATH="$(JACE_HOME)/release/lib/asm-3.3.jar":"$(JACE_HOME)/release/lib/asm-commons-3.3.jar":"$(JACE_HOME)/release/lib/asm-tree-3.3.jar":"$(JACE_HOME)/release/lib/jace.jar":"$(JACE_HOME)/release/lib/logback-classic-0.9.21.jar":"$(JACE_HOME)/release/lib/logback-core-0.9.21.jar":"$(JACE_HOME)/release/lib/retroweaver-rt-2.0.7.jar":"$(JACE_HOME)/release/lib/slf4j-api-1.6.0.jar":"%JACE_HOME%/release/lib"
java -classpath $(CLASSPATH) jace.peergen.BatchEnhancer $*
