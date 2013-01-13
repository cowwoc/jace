#############
# FUNCTIONS #
#############

# Returns all header files in the current directory, recursing into sub-directories.
function (get_headers DIRECTORY RESULT)
  file (GLOB_RECURSE HEADER_FILES_TMP "${DIRECTORY}/*.h" "${DIRECTORY}/*.hpp" "${DIRECTORY}/*.inl" "${DIRECTORY}/*.pch" "${DIRECTORY}/*.tsd" "${DIRECTORY}/*.tsp")
  set (${RESULT} ${HEADER_FILES_TMP} PARENT_SCOPE)
endfunction ()

# Returns all source files in the current directory, recursing into sub-directories
function (get_sources DIRECTORY RESULT)
  file (GLOB_RECURSE SOURCE_FILES_TMP "${DIRECTORY}/*.c" "${DIRECTORY}/*.cpp")
  set (${RESULT} ${SOURCE_FILES_TMP} PARENT_SCOPE)
endfunction ()

# Converts all elements of a list to native paths
function (to_cmake_path collection result)
	LIST (LENGTH collection count)
	set (previous_index 0)
	set (index 0)
	WHILE (index LESS count)
		LIST (GET collection ${index} element)
		LIST (REMOVE_AT collection ${index})
		file (TO_CMAKE_PATH ${element} element)
		LIST (INSERT collection ${previous_index} ${element})
		MATH (EXPR previous_index "${index}")
		MATH (EXPR index "0 + ${index} + 1")
	ENDWHILE ()
  set (${result} ${collection} PARENT_SCOPE)
endfunction ()

################
# Build script #
################

cmake_minimum_required (VERSION 2.8)
project (example1)

set (example1_VERSION_MAJOR 1)
set (example1_VERSION_MINOR 2)
add_definitions (-DJACE_EXPORTS -DBOOST_AUTO_LINK_NOMANGLE)

set (CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /MP")
IF(CMAKE_COMPILER_IS_GNUCXX)
	SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Werror")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

if (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
  include_directories (include $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/win32 $ENV{BOOST_HOME} $ENV{JACE_HOME}/include "${PROJECT_BINARY_DIR}/../proxies/include")
elseif (${CMAKE_SYSTEM_NAME} MATCHES "Linux")
  include_directories (include $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux $ENV{BOOST_HOME} $ENV{JACE_HOME}/include "${PROJECT_BINARY_DIR}/../proxies/include")
endif ()

get_headers ("${PROJECT_BINARY_DIR}/../proxies/include" HEADER_FILES)
to_cmake_path("${HEADER_FILES}" HEADER_FILES)

get_sources ("${CMAKE_CURRENT_SOURCE_DIR}/source" temp)
LIST (APPEND SOURCE_FILES ${temp})
get_sources ("${PROJECT_BINARY_DIR}/../proxies/source" temp)
LIST (APPEND SOURCE_FILES ${temp})
to_cmake_path("${SOURCE_FILES}" SOURCE_FILES)

file (TO_CMAKE_PATH "$ENV{JACE_HOME}/lib" JACE_LIBRARY)
if (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
  file (TO_CMAKE_PATH "$ENV{JAVA_HOME}/lib" JAVA_LIBRARY)
elseif (${CMAKE_SYSTEM_NAME} MATCHES "Linux")
  if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    file (TO_CMAKE_PATH "$ENV{JAVA_HOME}/jre/lib/amd64/server" JAVA_LIBRARY)
  else ()
    file (TO_CMAKE_PATH "$ENV{JAVA_HOME}/jre/lib/i386/client" JAVA_LIBRARY)
  endif ()
endif ()
link_directories (${JAVA_LIBRARY} ${JACE_LIBRARY})

add_executable (example1 ${SOURCE_FILES} ${HEADER_FILES})

target_link_libraries (example1 jvm)
target_link_libraries (example1 jace)
target_link_libraries (example1 boost_thread)

file (TO_NATIVE_PATH ${PROJECT_BINARY_DIR} NATIVE_PROJECT_BINARY_DIR)
file (TO_NATIVE_PATH "$ENV{JACE_HOME}" JACE_HOME)
if (${CMAKE_GENERATOR} MATCHES "Visual Studio")
  add_custom_command (TARGET example1 POST_BUILD COMMAND ${CMAKE_CURRENT_SOURCE_DIR}/post-build.bat \"${CMAKE_CFG_INTDIR}\" \"${JACE_HOME}\" \"${NATIVE_PROJECT_BINARY_DIR}\")
elseif (${CMAKE_GENERATOR} MATCHES "Unix Makefiles")
  add_custom_command (TARGET example1 POST_BUILD COMMAND ${CMAKE_CURRENT_SOURCE_DIR}/post-build.sh \"${JACE_HOME}\" \"${NATIVE_PROJECT_BINARY_DIR}\")
endif ()