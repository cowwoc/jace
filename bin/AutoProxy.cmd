@echo off
setlocal

set SCRIPT_PATH=%~dp0%
set JACE_HOME=%SCRIPT_PATH%..
set CLASSPATH="%JACE_HOME%\core\java\target\*";"%JACE_HOME%\core\java\target\dependencies\*";.

java -classpath %CLASSPATH% org.jace.proxy.AutoProxy %*

endlocal
@echo on
