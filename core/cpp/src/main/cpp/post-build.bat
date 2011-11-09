@echo off
rem ********************************
rem * %1 = build configuration     *
rem * %2 = boost library directory *
rem * %3 = output directory        *
rem ********************************

echo Copying dependencies to output directory...
copy "%2\*" "%3\%1" /y