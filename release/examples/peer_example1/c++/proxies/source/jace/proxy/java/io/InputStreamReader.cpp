
#ifndef JACE_PROXY_JAVA_IO_INPUTSTREAMREADER_H
#include "jace/proxy/java/io/InputStreamReader.h"
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
#ifndef JACE_PROXY_JAVA_IO_INPUTSTREAM_H
#include "jace/proxy/java/io/InputStream.h"
#endif
#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_PROXY_JAVA_IO_IOEXCEPTION_H
#include "jace/proxy/java/io/IOException.h"
#endif
#ifndef JACE_TYPES_JBOOLEAN_H
#include "jace/proxy/types/JBoolean.h"
#endif

BEGIN_NAMESPACE_4( jace, proxy, java, io )

/**
 * The Jace C++ proxy class source for java/io/InputStreamReader.
 * Please do not edit this source, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
#ifndef VIRTUAL_INHERITANCE_IS_BROKEN
  #define InputStreamReader_INITIALIZER : ::jace::proxy::java::io::Reader( NO_OP ), ::jace::proxy::java::lang::Object( NO_OP )
#else
  #define InputStreamReader_INITIALIZER : ::jace::proxy::java::io::Reader( NO_OP )
#endif

InputStreamReader::InputStreamReader( ::jace::proxy::java::io::InputStream p0 ) InputStreamReader_INITIALIZER {
  ::jace::JArguments arguments;
  arguments << p0;
  jobject localRef = newObject( arguments );
  setJavaJniObject( localRef );
  JNIEnv* env = ::jace::helper::attach();
  ::jace::helper::deleteLocalRef( env, localRef );
}

InputStreamReader::InputStreamReader( ::jace::proxy::java::io::InputStream p0, ::jace::proxy::java::lang::String p1 ) InputStreamReader_INITIALIZER {
  ::jace::JArguments arguments;
  arguments << p0 << p1;
  jobject localRef = newObject( arguments );
  setJavaJniObject( localRef );
  JNIEnv* env = ::jace::helper::attach();
  ::jace::helper::deleteLocalRef( env, localRef );
}

::jace::proxy::java::lang::String InputStreamReader::getEncoding() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::String >( "getEncoding" ).invoke( *this, arguments );
}

::jace::proxy::types::JInt InputStreamReader::read() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JInt >( "read" ).invoke( *this, arguments );
}

::jace::proxy::types::JInt InputStreamReader::read( ::jace::JArray< ::jace::proxy::types::JChar > p0, ::jace::proxy::types::JInt p1, ::jace::proxy::types::JInt p2 ) {
  ::jace::JArguments arguments;
  arguments << p0 << p1 << p2;
  return ::jace::JMethod< ::jace::proxy::types::JInt >( "read" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean InputStreamReader::ready() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "ready" ).invoke( *this, arguments );
}

void InputStreamReader::close() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "close" ).invoke( *this, arguments );
}

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
InputStreamReader::InputStreamReader( jvalue value ) InputStreamReader_INITIALIZER {
  setJavaJniValue( value );
}

InputStreamReader::InputStreamReader( jobject object ) InputStreamReader_INITIALIZER {
  setJavaJniObject( object );
}

InputStreamReader::InputStreamReader( const InputStreamReader& object ) InputStreamReader_INITIALIZER {
  setJavaJniObject( object.getJavaJniObject() );
}

InputStreamReader::InputStreamReader( const NoOp& noOp ) InputStreamReader_INITIALIZER {
}

const JClass* InputStreamReader::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "java/io/InputStreamReader" );
  return &javaClass;
}

const JClass* InputStreamReader::getJavaJniClass() const throw ( JNIException ) {
  return InputStreamReader::staticGetJavaJniClass();
}

JEnlister< InputStreamReader> InputStreamReader::enlister;

END_NAMESPACE_4( jace, proxy, java, io )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
  template <> ElementProxy< ::jace::proxy::java::io::InputStreamReader>::ElementProxy( jarray array, jvalue element, int index ) : 
    ::jace::proxy::java::io::InputStreamReader( element ), Object( NO_OP ), mIndex( index ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, array ) );
  }
  template <> ElementProxy< ::jace::proxy::java::io::InputStreamReader>::ElementProxy( const jace::ElementProxy< ::jace::proxy::java::io::InputStreamReader>& proxy ) : 
    ::jace::proxy::java::io::InputStreamReader( proxy.getJavaJniObject() ), Object( NO_OP ), mIndex( proxy.mIndex ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, proxy.parent ) );
  }
#endif
#ifndef PUT_TSDS_IN_HEADER
  template <> JFieldProxy< ::jace::proxy::java::io::InputStreamReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) : 
    ::jace::proxy::java::io::InputStreamReader( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    if ( parent_ ) {
      parent = ::jace::helper::newGlobalRef( env, parent_ );
    }
    else {
      parent = parent_;
    }

    parentClass = 0;
  }
  template <> JFieldProxy< ::jace::proxy::java::io::InputStreamReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) : 
    ::jace::proxy::java::io::InputStreamReader( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    parent = 0;
    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, parentClass_ ) );
  }
  template <> JFieldProxy< ::jace::proxy::java::io::InputStreamReader>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::java::io::InputStreamReader>& object ) : 
    ::jace::proxy::java::io::InputStreamReader( object.getJavaJniValue() ), Object( NO_OP ) {

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

