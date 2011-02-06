@echo off
rem ********************************
rem * %1 = build configuration     *
rem * %2 = boost library directory *
rem * %3 = output directory        *
rem * %4 = visual studio version   *
rem ********************************

if /i %1 == "debug" (
  echo Copying debug dependencies to output directory...
  copy "%2\*boost_thread-vc%4-mt-gd-1*" "%3\%1" /y
  copy "%2\*boost_date_time-vc%4-mt-gd-1*" "%3\%1" /y
) else (
  echo Copying release dependencies to output directory...
  copy "%2\*boost_thread-vc%4-mt-1*" "%3\%1" /y
  copy "%2\*boost_date_time-vc%4-mt-1*" "%3\%1" /y
)