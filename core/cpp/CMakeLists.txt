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

################
# Build script #
################

cmake_minimum_required (VERSION 2.8)
project (jace)

set (jace_VERSION_MAJOR 1)
set (jace_VERSION_MINOR 2)
add_definitions (-D_UNICODE -DUNICODE -DJACE_EXPORTS)

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
  file (TO_NATIVE_PATH "$ENV{BOOST_HOME}/stage64/lib" temp)
  set (BOOST_LIBRARY ${temp})
else ()
  file (TO_NATIVE_PATH "$ENV{BOOST_HOME}/stage32/lib" temp)
  set (BOOST_LIBRARY ${temp})
endif (CMAKE_SIZEOF_VOID_P EQUAL 8)
set (CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /MP")

include_directories (include $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/win32 $ENV{BOOST_HOME})
link_directories (${BOOST_LIBRARY})

get_headers ("${CMAKE_CURRENT_SOURCE_DIR}/include" HEADER_FILES)
get_sources ("${CMAKE_CURRENT_SOURCE_DIR}/source" SOURCE_FILES)

add_library (jace SHARED ${SOURCE_FILES} ${HEADER_FILES})

if (${CMAKE_GENERATOR} MATCHES "Visual Studio")
  file (TO_NATIVE_PATH ${PROJECT_BINARY_DIR} NATIVE_PROJECT_BINARY_DIR)
  add_custom_command (TARGET jace POST_BUILD COMMAND ${CMAKE_CURRENT_SOURCE_DIR}/post-build \"${CMAKE_CFG_INTDIR}\" \"${BOOST_LIBRARY}\" \"${NATIVE_PROJECT_BINARY_DIR}\" \"$(PlatformToolsetVersion)\")
endif ()