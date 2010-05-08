@echo off
setlocal

set SCRIPT_PATH=%~dp0%
set JACE_HOME=%SCRIPT_PATH%..
set CLASSPATH="%JACE_HOME%\lib\asm-3.1.jar";"%JACE_HOME%\lib\asm-commons-3.1.jar";"%JACE_HOME%\lib\asm-tree-3.1.jar";"%JACE_HOME%\lib\jace.jar";"%JACE_HOME%\lib\logback-classic-0.9.11.jar";"%JACE_HOME%\lib\logback-core-0.9.11.jar";"%JACE_HOME%\lib\retroweaver-rt-2.0.7.jar";"%JACE_HOME%\lib\slf4j-api-1.5.5.jar";"%JACE_HOME%\lib"

java -classpath %CLASSPATH% jace.proxy.AutoProxy %*

endlocal
@echo on
