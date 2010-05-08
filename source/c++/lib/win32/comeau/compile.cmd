
REM A compilation script for Comeau 3.4.0.1

del *.obj /Q

set CC=como

REM Flags needed for compilation
REM
REM NOTE - These flags are provided in a particular order. This order must be
REM        maintained for the code to be correctly built.
REM
REM  -mthread = Support multi-threaded code
REM  -c       = Compile only
REM  -Wall    = All warnings
REM  -fPIC    = Create relocateable code 
REM
set COMPILER_FLAGS= /Zi --display_error_number --long_long 

REM --A --diag_suppress "#174-D" 
REM The directory containg the JDK header files.
REM
set JDK_INCLUDE=%JAVA_HOME%\include

REM The directory containing the operating system specific JDK header files.
REM
set JDK_OS_INCLUDE=%JDK_INCLUDE%\win32

set JACE_ROOT=..\..\..\..\..\..
set SOURCE_ROOT=%JACE_ROOT%\source\c++
set RELEASE_ROOT=%JACE_ROOT%\release\lib\win32\comeau

echo "Compiling"
%CC% -I%SOURCE_ROOT%\include -I%JDK_INCLUDE% -I%JDK_OS_INCLUDE%  %COMPILER_FLAGS% -o %RELEASE_ROOT%\jaced.lib %SOURCE_ROOT%\source\jace\*.cpp %SOURCE_ROOT%\source\jace\proxy\*.cpp %SOURCE_ROOT%\source\jace\proxy\types\*.cpp 

