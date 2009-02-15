# Microsoft Developer Studio Project File - Name="jace" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Static Library" 0x0104

CFG=jace - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "jace.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "jace.mak" CFG="jace - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "jace - Win32 Release" (based on "Win32 (x86) Static Library")
!MESSAGE "jace - Win32 Debug" (based on "Win32 (x86) Static Library")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "jace - Win32 Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_MBCS" /D "_LIB" /YX /FD /c
# ADD CPP /nologo /MD /W3 /GR /GX /O2 /Ob2 /I "..\..\include" /I "..\..\..\include" /I "c:\java\jdk1.4\include" /I "c:\java\jdk1.4\include\win32" /D "WIN32" /D "NDEBUG" /D "_MBCS" /D "_LIB" /FR /YX /FD /c
# ADD BASE RSC /l 0x409 /d "NDEBUG"
# ADD RSC /l 0x409 /d "NDEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\..\..\..\..\release\lib\win32\vc++6.0\jace.lib"

!ELSEIF  "$(CFG)" == "jace - Win32 Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_MBCS" /D "_LIB" /YX /FD /GZ /c
# ADD CPP /nologo /MDd /W3 /Gm /GR /GX /ZI /Od /I "..\..\..\include" /I "c:\java\jdk1.4\include" /I "c:\java\jdk1.4\include\win32" /D "WIN32" /D "_DEBUG" /D "_MBCS" /D "_LIB" /FR /YX /FD /GZ /c
# ADD BASE RSC /l 0x409 /d "_DEBUG"
# ADD RSC /l 0x409 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LIB32=link.exe -lib
# ADD BASE LIB32 /nologo
# ADD LIB32 /nologo /out:"..\..\..\..\..\release\lib\win32\vc++6.0\jaced.lib"

!ENDIF 

# Begin Target

# Name "jace - Win32 Release"
# Name "jace - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Group "jace"

# PROP Default_Filter ""
# Begin Group "proxy"

# PROP Default_Filter ""
# Begin Group "types"

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JBoolean.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JByte.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JChar.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JDouble.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JFloat.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JInt.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JLong.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JShort.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\types\JVoid.cpp
# End Source File
# End Group
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\JObject.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\proxy\JValue.cpp
# End Source File
# End Group
# Begin Source File

SOURCE=..\..\..\source\jace\BaseException.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\ElementProxy.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\ElementProxyHelper.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JArguments.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JArray.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JArrayHelper.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\javacast.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JClass.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JClassImpl.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JConstructor.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JEnlister.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JFactory.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JField.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JFieldHelper.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JFieldProxy.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JFieldProxyHelper.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JMethod.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JNIException.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JNIHelper.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\JSignature.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\OptionList.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\Peer.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\StaticVmLoader.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\UnixVmLoader.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\VmLoader.cpp
# End Source File
# Begin Source File

SOURCE=..\..\..\source\jace\Win32VmLoader.cpp
# End Source File
# End Group
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Group "jace."

# PROP Default_Filter ""
# Begin Group "proxy."

# PROP Default_Filter ""
# Begin Group "types."

# PROP Default_Filter ""
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JBoolean.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JByte.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JChar.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JDouble.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JFloat.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JInt.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JLong.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JShort.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\types\JVoid.h
# End Source File
# End Group
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\JObject.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\proxy\JValue.h
# End Source File
# End Group
# Begin Source File

SOURCE=..\..\..\include\jace\BaseException.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\counted_ptr.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\ElementProxy.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\ElementProxyHelper.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JArguments.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JArray.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JArrayHelper.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JClass.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JClassImpl.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JConstructor.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JEnlister.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JFactory.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JField.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JFieldHelper.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JFieldProxy.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JFieldProxyHelper.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JMethod.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JNIException.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JNIHelper.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\JSignature.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\namespace.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\OptionList.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\os_dep.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\Peer.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\StaticVmLoader.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\UnixVmLoader.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\VmLoader.h
# End Source File
# Begin Source File

SOURCE=..\..\..\include\jace\Win32VmLoader.h
# End Source File
# End Group
# End Group
# End Target
# End Project
