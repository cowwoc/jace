# Microsoft Developer Studio Project File - Name="peer_example1" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Dynamic-Link Library" 0x0102

CFG=peer_example1 - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "peer_example1.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "peer_example1.mak" CFG="peer_example1 - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "peer_example1 - Win32 Release" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE "peer_example1 - Win32 Debug" (based on "Win32 (x86) Dynamic-Link Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
MTL=midl.exe
RSC=rc.exe

!IF  "$(CFG)" == "peer_example1 - Win32 Release"

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
# ADD BASE CPP /nologo /MT /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "PEER_EXAMPLE1_EXPORTS" /YX /FD /c
# ADD CPP /nologo /MD /W3 /GR /GX /O2 /I "..\..\..\include" /I "..\..\..\proxies\include" /I "..\..\..\..\..\..\include" /I "c:\java\jdk1.4\include" /I "c:\java\jdk1.4\include\win32" /D "NDEBUG" /D "WIN32" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "PEER_EXAMPLE1_EXPORTS" /FR /YX /FD /c
# ADD BASE MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "NDEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib jvm.lib jace.lib /nologo /dll /machine:I386 /libpath:"c:\java\jdk1.4\lib" /libpath:"..\..\..\..\..\..\lib\win32\vc++6.0"

!ELSEIF  "$(CFG)" == "peer_example1 - Win32 Debug"

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
# ADD BASE CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "PEER_EXAMPLE1_EXPORTS" /YX /FD /GZ /c
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "..\..\..\include" /I "..\..\..\proxies\include" /I "..\..\..\..\..\..\include" /I "c:\java\jdk1.4\include" /I "c:\java\jdk1.4\include\win32" /D "_DEBUG" /D "WIN32" /D "_WINDOWS" /D "_MBCS" /D "_USRDLL" /D "PEER_EXAMPLE1_EXPORTS" /FR /YX /FD /GZ /c
# ADD BASE MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD MTL /nologo /D "_DEBUG" /mktyplib203 /win32
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /dll /debug /machine:I386 /pdbtype:sept
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib jvm.lib jaced.lib /nologo /dll /debug /machine:I386 /pdbtype:sept /libpath:"c:\java\jdk1.4\lib" /libpath:"..\..\..\..\..\..\lib\win32\vc++6.0"

!ENDIF 

# Begin Target

# Name "peer_example1 - Win32 Release"
# Name "peer_example1 - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Group ".jace"

# PROP Default_Filter ""
# Begin Group "peer"

# PROP Default_Filter ""
# Begin Group "jace"

# PROP Default_Filter ""
# Begin Group "examples"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\source\jace\peer\jace\examples\PeerExample.cpp

!IF  "$(CFG)" == "peer_example1 - Win32 Release"

# PROP Intermediate_Dir "Release\peer"

!ELSEIF  "$(CFG)" == "peer_example1 - Win32 Debug"

# PROP Intermediate_Dir "Debug\peer"

!ENDIF 

# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\peer\jace\examples\PeerExampleImpl.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\peer\jace\examples\PeerExampleMappings.cpp
# End Source File
# End Group
# End Group
# End Group
# End Group
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Group ".jace."

# PROP Default_Filter ""
# Begin Group "peer."

# PROP Default_Filter ""
# Begin Group "jace."

# PROP Default_Filter ""
# Begin Group "examples."

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\include\jace\peer\jace\examples\PeerExample.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\peer\jace\examples\PeerExample_user.h
# End Source File
# End Group
# End Group
# End Group
# End Group
# End Group
# Begin Group "Proxy files"

# PROP Default_Filter ""
# Begin Group ".jace.."

# PROP Default_Filter ""
# Begin Group "proxy"

# PROP Default_Filter ""
# Begin Group "java"

# PROP Default_Filter ""
# Begin Group "io"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\io\BufferedReader.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\io\InputStream.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\io\InputStreamReader.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\io\IOException.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\io\Reader.cpp
# End Source File
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
# Begin Group "net"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\java\net\URL.cpp
# End Source File
# End Group
# End Group
# Begin Group "..jace.."

# PROP Default_Filter ""
# Begin Group ".examples."

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\proxies\source\jace\proxy\jace\examples\PeerExample.cpp
# End Source File
# End Group
# End Group
# End Group
# End Group
# End Group
# End Target
# End Project
