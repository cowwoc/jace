# Microsoft Developer Studio Project File - Name="singleton" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=singleton - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "singleton.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "singleton.mak" CFG="singleton - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "singleton - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "singleton - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "singleton - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SINGLETON_EXPORTS" /YX /FD /c
# ADD CPP /nologo /MD /W3 /GR /GX /O2 /I "..\..\..\include" /I "..\..\..\proxies\include" /I "..\..\..\..\..\..\include" /I "c:\java\jdk1.4\include" /I "c:\java\jdk1.4\include\win32" /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SINGLETON_EXPORTS" /YX /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib jvm.lib jace.lib /nologo /dll /machine:I386 /out:"Release/peer_singleton.dll" /libpath:"..\..\..\..\..\..\lib\win32\vc++6.0" /libpath:"c:\java\jdk1.4\lib"

!ELSEIF  "$(CFG)" == "singleton - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Ignore_Export_Lib 0
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SINGLETON_EXPORTS" /YX /FD /GZ /c
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "..\..\..\include" /I "..\..\..\proxies\include" /I "..\..\..\..\..\..\include" /I "c:\java\jdk1.4\include" /I "c:\java\jdk1.4\include\win32" /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "SINGLETON_EXPORTS" /FR /YX /FD /GZ /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib jaced.lib jvm.lib /nologo /dll /debug /machine:I386 /out:"Debug/peer_singleton.dll" /pdbtype:sept /libpath:"..\..\..\..\..\..\lib\win32\vc++6.0" /libpath:"c:\java\jdk1.4\lib"

!ENDIF 

# Begin Target

# Name "singleton - Win32 Release"
# Name "singleton - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Group "peer"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\source\jace\peer\Singleton.cpp

!IF  "$(CFG)" == "singleton - Win32 Release"

# PROP Intermediate_Dir "Release\peer"

!ELSEIF  "$(CFG)" == "singleton - Win32 Debug"

# PROP Intermediate_Dir "Debug\peer"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\peer\SingletonImpl.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\peer\SingletonMappings.cpp
# End Source File
# End Group
# Begin Group "proxies"

# PROP Default_Filter ""
# Begin Group "java"

# PROP Default_Filter ""
# Begin Group "io"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\io\Serializable.cpp
# End Source File
# End Group
# Begin Group "lang"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\CharSequence.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\Class.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\Comparable.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\Exception.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\Object.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\RuntimeException.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\String.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\lang\Throwable.cpp
# End Source File
# End Group
# End Group
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\Singleton.cpp
# End Source File
# End Group
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Source File

SOURCE=..\..\..\include\jace\peer\Singleton.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\peer\Singleton_user.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# End Target
# End Project
