@echo off

REM ---
REM - This is the build script for generating a binary release of Jace.
REM - 
REM - 
REM ---
setlocal

call setenv

set path="f:\program files\doxygen\bin";%path%

REM ---
REM - Local variables for use by the script
REM -
REM ---
set ANT=%ANT_HOME%\bin\ant

REM ---
REM - Call Ant, setting the variables and passing any extra parameters
REM -
REM ---
%ANT% -Ddoxygen.home=%DOXYGEN_HOME% -Dtarget-platform=win32 -Dtarget-compiler=vc++ %1 %2 %3 %4 %5 %6

endlocal

@echo on
