
#ifndef JACE_PROXY_JAVA_IO_BUFFEREDREADER_H
#include "jace/proxy/java/io/BufferedReader.h"
#endif

/**
 * Standard Jace headers needed to implement this class.
 *
 */
#ifndef JACE_JARGUMENTS_H
#include "jace/JArguments.h"
#endif
using jace::JArguments;

#ifndef JACE_JMETHOD_H
#include "jace/JMethod.h"
#endif
using jace::JMethod;

#ifndef JACE_JFIELD_H
#include "jace/JField.h"
#endif
using jace::JField;

#ifndef JACE_JCLASS_IMPL_H
#include "jace/JClassImpl.h"
#endif
using jace::JClassImpl;


/**
 * Headers for the classes that this class uses.
 *
 */
#ifndef JACE_TYPES_JVOID_H
#include "jace/proxy/types/JVoid.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_PROXY_JAVA_IO_IOEXCEPTION_H
#include "jace/proxy/java/io/IOException.h"
#endif
#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif
#ifndef JACE_TYPES_JBOOLEAN_H
#include "jace/proxy/types/JBoolean.h"
#endif

BEGIN_NAMESPACE_4( jace, proxy, java, io )

/**
 * The Jace C++ proxy class source for java/io/BufferedReader.
 * Please do not edit this source, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
#ifndef VIRTUAL_INHERITANCE_IS_BROKEN
  #define BufferedReader_INITIALIZER : ::jace::proxy::java::io::Reader( NO_OP ), ::jace::proxy::java::lang::Object( NO_OP )
#else
  #define BufferedReader_INITIALIZER : ::jace::proxy::java::io::Reader( NO_OP )
#endif

BufferedReader::BufferedReader( ::jace::proxy::java::io::Reader p0, ::jace::proxy::types::JInt p1 ) BufferedReader_INITIALIZER {
  ::jace::JArguments arguments;
  arguments << p0 << p1;
  jobject localRef = newObject( arguments );
  setJavaJniObject( localRef );
  JNIEnv* env = ::jace::helper::attach();
  ::jace::helper::deleteLocalRef( env, localRef );
}

BufferedReader::BufferedReader( ::jace::proxy::java::io::Reader p0 ) BufferedReader_INITIALIZER {
  ::jace::JArguments arguments;
  arguments << p0;
  jobject localRef = newObject( arguments );
  setJavaJniObject( localRef );
  JNIEnv* env = ::jace::helper::attach();
  ::jace::helper::deleteLocalRef( env, localRef );
}

::jace::proxy::types::JInt BufferedReader::read() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JInt >( "read" ).invoke( *this, arguments );
}

::jace::proxy::types::JInt BufferedReader::read( ::jace::JArray< ::jace::proxy::types::JChar > p0, ::jace::proxy::types::JInt p1, ::jace::proxy::types::JInt p2 ) {
  ::jace::JArguments arguments;
  arguments << p0 << p1 << p2;
  return ::jace::JMethod< ::jace::proxy::types::JInt >( "read" ).invoke( *this, arguments );
}

::jace::proxy::java::lang::String BufferedReader::readLine() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::String >( "readLine" ).invoke( *this, arguments );
}

::jace::proxy::types::JLong BufferedReader::skip( ::jace::proxy::types::JLong p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::proxy::types::JLong >( "skip" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean BufferedReader::ready() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "ready" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean BufferedReader::markSupported() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "markSupported" ).invoke( *this, arguments );
}

void BufferedReader::mark( ::jace::proxy::types::JInt p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "mark" ).invoke( *this, arguments );
}

void BufferedReader::reset() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "reset" ).invoke( *this, arguments );
}

void BufferedReader::close() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "close" ).invoke( *this, arguments );
}

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
BufferedReader::BufferedReader( jvalue value ) BufferedReader_INITIALIZER {
  setJavaJniValue( value );
}

BufferedReader::BufferedReader( jobject object ) BufferedReader_INITIALIZER {
  setJavaJniObject( object );
}

BufferedReader::BufferedReader( const BufferedReader& object ) BufferedReader_INITIALIZER {
  setJavaJniObject( object.getJavaJniObject() );
}

BufferedReader::BufferedReader( const NoOp& noOp ) BufferedReader_INITIALIZER {
}

const JClass* BufferedReader::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "java/io/BufferedReader" );
  return &javaClass;
}

const JClass* BufferedReader::getJavaJniClass() const throw ( JNIException ) {
  return BufferedReader::staticGetJavaJniClass();
}

JEnlister< BufferedReader> BufferedReader::enlister;

END_NAMESPACE_4( jace, proxy, java, io )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
  template <> ElementProxy< ::jace::proxy::java::io::BufferedReader>::ElementProxy( jarray array, jvalue element, int index ) : 
    ::jace::proxy::java::io::BufferedReader( element ), Object( NO_OP ), mIndex( index ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, array ) );
  }
  template <> ElementProxy< ::jace::proxy::java::io::BufferedReader>::ElementProxy( const jace::ElementProxy< ::jace::proxy::java::io::BufferedReader>& proxy ) : 
    ::jace::proxy::java::io::BufferedReader( proxy.getJavaJniObject() ), Object( NO_OP ), mIndex( proxy.mIndex ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, proxy.parent ) );
  }
#endif
#ifndef PUT_TSDS_IN_HEADER
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) : 
    ::jace::proxy::java::io::BufferedReader( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    if ( parent_ ) {
      parent = ::jace::helper::newGlobalRef( env, parent_ );
    }
    else {
      parent = parent_;
    }

    parentClass = 0;
  }
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) : 
    ::jace::proxy::java::io::BufferedReader( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    parent = 0;
    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, parentClass_ ) );
  }
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::java::io::BufferedReader>& object ) : 
    ::jace::proxy::java::io::BufferedReader( object.getJavaJniValue() ), Object( NO_OP ) {

    fieldID = object.fieldID; 

    if ( object.parent ) {
      JNIEnv* env = ::jace::helper::attach();
      parent = ::jace::helper::newGlobalRef( env, object.parent );
    }
    else {
      parent = 0;
    }

    if ( object.parentClass ) {
      JNIEnv* env = ::jace::helper::attach();
      parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, object.parentClass ) );
    }
    else {
      parentClass = 0;
    }
  }
#endif

END_NAMESPACE( jace )

