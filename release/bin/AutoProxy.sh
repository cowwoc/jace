export SCRIPT_PATH="$(readlink -f $(dirname "$0"))"
export JACE_HOME=%SCRIPT_PATH%..
export CLASSPATH="$(JACE_HOME)\lib\asm-3.1.jar":"%JACE_HOME%\lib\asm-commons-3.1.jar":"%JACE_HOME%\lib\asm-tree-3.1.jar":"%JACE_HOME%\lib\jace.jar":"%JACE_HOME%\lib\logback-classic-0.9.11.jar":"%JACE_HOME%\lib\logback-core-0.9.11.jar":"%JACE_HOME%\lib\retroweaver-rt-2.0.7.jar":"%JACE_HOME%\lib\slf4j-api-1.5.5.jar"
java -classpath $(CLASSPATH) jace.proxy.AutoProxy $*
