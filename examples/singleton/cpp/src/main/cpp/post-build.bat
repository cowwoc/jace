@echo off
rem ****************************
rem * %1 = build configuration *
rem * %2 = jace home directory *
rem * %3 = output directory    *
rem ****************************

echo Copying dependencies to output directory...
copy /b "%~2\lib\*" "%~3\%~1" /y
copy /b "%~3\..\..\..\..\java\target\jace-examples-singleton*.jar" "%~3\%~1\singleton.jar" /y
copy /b "%~2\jace-core-runtime-*" "%~3\%~1\" /y