
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
set COMPILER_FLAGS= -c /Zi --display_error_number --long_long 

REM --A --diag_suppress "#174-D" 
REM The directory containg the JDK header files.
REM
set JDK_INCLUDE=%JAVA_HOME%\include

REM The directory containing the operating system specific JDK header files.
REM
set JDK_OS_INCLUDE=%JDK_INCLUDE%\win32

set JACE_ROOT=..\..\..\..\..\..
set LOCAL_SOURCE=..\..\..\..\source
set LOCAL_PROXY_SOURCE=..\..\..\..\proxies\source
set RELEASE_ROOT=.\
                                                                                                              
echo "Compiling"

%CC% %COMPILER_FLAGS% -I%JACE_ROOT%\include -I..\..\..\..\include -I..\..\..\..\proxies\include -I%JDK_INCLUDE% -I%JDK_OS_INCLUDE% %LOCAL_SOURCE%\*.cpp %LOCAL_PROXY_SOURCE%\jace\proxy\java\io\*.cpp %LOCAL_PROXY_SOURCE%\jace\proxy\java\lang\*.cpp %LOCAL_PROXY_SOURCE%\jace\proxy\java\util\*.cpp 

