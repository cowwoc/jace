
#ifndef JACE_PROXY_JACE_EXAMPLES_PEEREXAMPLE_H
#include "jace/proxy/jace/examples/PeerExample.h"
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
#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_PROXY_JAVA_IO_IOEXCEPTION_H
#include "jace/proxy/java/io/IOException.h"
#endif
#ifndef JACE_PROXY_JAVA_LANG_EXCEPTION_H
#include "jace/proxy/java/lang/Exception.h"
#endif

BEGIN_NAMESPACE_4( jace, proxy, jace, examples )

/**
 * The Jace C++ proxy class source for jace/examples/PeerExample.
 * Please do not edit this source, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
#ifndef VIRTUAL_INHERITANCE_IS_BROKEN
  #define PeerExample_INITIALIZER : ::jace::proxy::java::lang::Object( NO_OP )
#else
  #define PeerExample_INITIALIZER
#endif

PeerExample::PeerExample( ::jace::proxy::java::lang::String p0, ::jace::proxy::types::JInt p1 ) PeerExample_INITIALIZER {
  ::jace::JArguments arguments;
  arguments << p0 << p1;
  jobject localRef = newObject( arguments );
  setJavaJniObject( localRef );
  JNIEnv* env = ::jace::helper::attach();
  ::jace::helper::deleteLocalRef( env, localRef );
}

PeerExample::PeerExample( ::jace::proxy::java::lang::String p0 ) PeerExample_INITIALIZER {
  ::jace::JArguments arguments;
  arguments << p0;
  jobject localRef = newObject( arguments );
  setJavaJniObject( localRef );
  JNIEnv* env = ::jace::helper::attach();
  ::jace::helper::deleteLocalRef( env, localRef );
}

void PeerExample::close() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "close" ).invoke( *this, arguments );
}

void PeerExample::finalize() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "finalize" ).invoke( *this, arguments );
}

::jace::JArray< ::jace::proxy::java::lang::String > PeerExample::getResources( ::jace::JArray< ::jace::proxy::java::lang::String > p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::JArray< ::jace::proxy::java::lang::String > >( "getResources" ).invoke( *this, arguments );
}

void PeerExample::main( ::jace::JArray< ::jace::proxy::java::lang::String > p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "main" ).invoke( staticGetJavaJniClass(), arguments );
}

void PeerExample::run( ::jace::JArray< ::jace::proxy::java::lang::String > p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "run" ).invoke( staticGetJavaJniClass(), arguments );
}

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
PeerExample::PeerExample( jvalue value ) PeerExample_INITIALIZER {
  setJavaJniValue( value );
}

PeerExample::PeerExample( jobject object ) PeerExample_INITIALIZER {
  setJavaJniObject( object );
}

PeerExample::PeerExample( const PeerExample& object ) PeerExample_INITIALIZER {
  setJavaJniObject( object.getJavaJniObject() );
}

PeerExample::PeerExample( const NoOp& noOp ) PeerExample_INITIALIZER {
}

const JClass* PeerExample::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "jace/examples/PeerExample" );
  return &javaClass;
}

const JClass* PeerExample::getJavaJniClass() const throw ( JNIException ) {
  return PeerExample::staticGetJavaJniClass();
}

JEnlister< PeerExample> PeerExample::enlister;

END_NAMESPACE_4( jace, proxy, jace, examples )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
  template <> ElementProxy< ::jace::proxy::jace::examples::PeerExample>::ElementProxy( jarray array, jvalue element, int index ) : 
    ::jace::proxy::jace::examples::PeerExample( element ), Object( NO_OP ), mIndex( index ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, array ) );
  }
  template <> ElementProxy< ::jace::proxy::jace::examples::PeerExample>::ElementProxy( const jace::ElementProxy< ::jace::proxy::jace::examples::PeerExample>& proxy ) : 
    ::jace::proxy::jace::examples::PeerExample( proxy.getJavaJniObject() ), Object( NO_OP ), mIndex( proxy.mIndex ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, proxy.parent ) );
  }
#endif
#ifndef PUT_TSDS_IN_HEADER
  template <> JFieldProxy< ::jace::proxy::jace::examples::PeerExample>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) : 
    ::jace::proxy::jace::examples::PeerExample( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    if ( parent_ ) {
      parent = ::jace::helper::newGlobalRef( env, parent_ );
    }
    else {
      parent = parent_;
    }

    parentClass = 0;
  }
  template <> JFieldProxy< ::jace::proxy::jace::examples::PeerExample>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) : 
    ::jace::proxy::jace::examples::PeerExample( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    parent = 0;
    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, parentClass_ ) );
  }
  template <> JFieldProxy< ::jace::proxy::jace::examples::PeerExample>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::jace::examples::PeerExample>& object ) : 
    ::jace::proxy::jace::examples::PeerExample( object.getJavaJniValue() ), Object( NO_OP ) {

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

