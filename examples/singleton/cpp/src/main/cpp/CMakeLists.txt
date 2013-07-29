#############
# FUNCTIONS #
#############

# Returns all header files in the current directory, recursing into sub-directories.
FUNCTION (get_headers DIRECTORY RESULT)
	FILE (GLOB_RECURSE HEADER_FILES_TMP "${DIRECTORY}/*.h" "${DIRECTORY}/*.hpp" "${DIRECTORY}/*.inl" "${DIRECTORY}/*.pch" "${DIRECTORY}/*.tsd" "${DIRECTORY}/*.tsp")
	SET (${RESULT} ${HEADER_FILES_TMP} PARENT_SCOPE)
ENDFUNCTION ()

# Returns all source files in the current directory, recursing into sub-directories
FUNCTION (get_sources DIRECTORY RESULT)
	FILE (GLOB_RECURSE SOURCE_FILES_TMP "${DIRECTORY}/*.c" "${DIRECTORY}/*.cpp")
	SET (${RESULT} ${SOURCE_FILES_TMP} PARENT_SCOPE)
ENDFUNCTION ()

# Converts all elements of a list to native paths
FUNCTION (TO_CMAKE_PATH collection result)
	LIST (LENGTH collection count)
	SET (previous_index 0)
	SET (index 0)
	WHILE (index LESS count)
		LIST (GET collection ${index} element)
		LIST (REMOVE_AT collection ${index})
		FILE (TO_CMAKE_PATH ${element} element)
		LIST (INSERT collection ${previous_index} ${element})
		MATH (EXPR previous_index "${index}")
		MATH (EXPR index "0 + ${index} + 1")
	ENDWHILE ()
	SET (${result} ${collection} PARENT_SCOPE)
ENDFUNCTION ()

################
# Build script #
################

CMAKE_MINIMUM_REQUIRED (VERSION 2.8)
PROJECT  (singleton)

SET (singleton_VERSION_MAJOR 1)
SET (singleton_VERSION_MINOR 2)
ADD_DEFINITIONS (-DJACE_EXPORTS -DBOOST_AUTO_LINK_NOMANGLE)

SET (CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /MP")
IF(CMAKE_COMPILER_IS_GNUCXX)
	SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Werror")
ENDIF(CMAKE_COMPILER_IS_GNUCXX)

FILE (TO_CMAKE_PATH "${PROJECT_BINARY_DIR}/../proxies" PROXIES_PATH)
FILE (TO_CMAKE_PATH "${PROJECT_BINARY_DIR}/../../../../java/target/peers" PEERS_PATH)

get_headers ("${PROXIES_PATH}/include" HEADER_FILES)
LIST (APPEND HEADER_FILES ${temp})
get_headers ("${PEERS_PATH}/include" HEADER_FILES)
LIST (APPEND HEADER_FILES ${temp})
TO_CMAKE_PATH ("${HEADER_FILES}" HEADER_FILES)

get_sources ("${CMAKE_CURRENT_SOURCE_DIR}/source" temp)
LIST (APPEND SOURCE_FILES ${temp})
get_sources ("${PROXIES_PATH}/source" temp)
LIST (APPEND SOURCE_FILES ${temp})
get_sources ("${PEERS_PATH}/source" temp)
LIST (APPEND SOURCE_FILES ${temp})
TO_CMAKE_PATH ("${SOURCE_FILES}" SOURCE_FILES)

FILE (TO_CMAKE_PATH "$ENV{JACE_HOME}/lib" JACE_LIBRARY)
IF (${CMAKE_SYSTEM_NAME} STREQUAL "Windows")
	IF (NOT $ENV{BUILD_ARCHITECTURE} STREQUAL "i386" AND NOT $ENV{BUILD_ARCHITECTURE} STREQUAL "amd64")
		MESSAGE (FATAL_ERROR "Environment variable BUILD_ARCHITECTURE must be one of [i386, amd64], was $ENV{BUILD_ARCHITECTURE}")
	ENDIF ()
	INCLUDE_DIRECTORIES (include $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/win32 $ENV{BOOST_HOME} $ENV{JACE_HOME}/include ${PROXIES_PATH}/include ${PEERS_PATH}/include)
	FILE (TO_CMAKE_PATH "$ENV{JAVA_HOME}/lib" JAVA_LIBRARY)
ELSEIF (${CMAKE_SYSTEM_NAME} STREQUAL "Linux")
	IF ($ENV{BUILD_ARCHITECTURE} STREQUAL "i386")
		SET (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -m32")
		SET (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -m32")
		FILE (TO_CMAKE_PATH "$ENV{JAVA_HOME}/jre/lib/i386/client" JAVA_LIBRARY)
	ELSEIF ($ENV{BUILD_ARCHITECTURE} STREQUAL "amd64")
		SET (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -m64")
		SET (CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -m64")
		FILE (TO_CMAKE_PATH "$ENV{JAVA_HOME}/jre/lib/amd64/server" JAVA_LIBRARY)
	ELSE ()
		MESSAGE (FATAL_ERROR "Environment variable BUILD_ARCHITECTURE must be one of [i386, amd64], was $ENV{BUILD_ARCHITECTURE}")
	ENDIF ()
	INCLUDE_DIRECTORIES (include $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux $ENV{BOOST_HOME} $ENV{JACE_HOME}/include ${PROXIES_PATH}/include ${PEERS_PATH}/include)
ELSEIF (${CMAKE_SYSTEM_NAME} STREQUAL "Darwin")
	FILE (TO_CMAKE_PATH "$ENV{JAVA_HOME}/jre/lib/server" JAVA_LIBRARY)
	INCLUDE_DIRECTORIES (include $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/darwin $ENV{BOOST_HOME} $ENV{JACE_HOME}/include ${PROXIES_PATH}/include ${PEERS_PATH}/include)
ELSE ()
	MESSAGE (FATAL_ERROR "CMAKE_SYSTEM_NAME must be one of [Windows, Linux, Darwin], was ${CMAKE_SYSTEM_NAME}")
ENDIF ()
LINK_DIRECTORIES (${JAVA_LIBRARY} ${JACE_LIBRARY})

ADD_LIBRARY (singleton SHARED ${SOURCE_FILES} ${HEADER_FILES})

TARGET_LINK_LIBRARIES (singleton jvm)
TARGET_LINK_LIBRARIES (singleton jace)

FILE (TO_NATIVE_PATH ${PROJECT_BINARY_DIR} NATIVE_PROJECT_BINARY_DIR)
FILE (TO_NATIVE_PATH "$ENV{JACE_HOME}" JACE_HOME)
IF (${CMAKE_GENERATOR} MATCHES "Visual Studio")
	ADD_CUSTOM_COMMAND (TARGET singleton POST_BUILD COMMAND ${CMAKE_CURRENT_SOURCE_DIR}/post-build.bat \"${CMAKE_CFG_INTDIR}\" \"${JACE_HOME}\" \"${NATIVE_PROJECT_BINARY_DIR}\")
ELSEIF (${CMAKE_GENERATOR} MATCHES "Unix Makefiles")
	ADD_CUSTOM_COMMAND (TARGET singleton POST_BUILD COMMAND ${CMAKE_CURRENT_SOURCE_DIR}/post-build.sh \"${JACE_HOME}\" \"${NATIVE_PROJECT_BINARY_DIR}\")
ENDIF ()