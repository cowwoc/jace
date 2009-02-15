
/**
 * This is the source for the implementation of the Jace Peer for Singleton.
 * Please do not edit this source. Any changes made will be overwritten.
 * 
 * For more information, please refer to the Jace Developer's Guide.
 *
 */

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

#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_PROXY_SINGLETON_H
#include "jace/proxy/Singleton.h"
#endif
#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif
#ifndef JACE_TYPES_JVOID_H
#include "jace/proxy/types/JVoid.h"
#endif

#ifndef JACE_PEER_SINGLETON_H
#include "jace/peer/Singleton.h"
#endif

BEGIN_NAMESPACE_2( jace, peer )

#ifndef VIRTUAL_INHERITANCE_IS_BROKEN
  #define Singleton_INITIALIZER : ::jace::proxy::java::lang::Object( NO_OP ), ::jace::Peer( jPeer )
#else
  #define Singleton_INITIALIZER : ::jace::Peer( jPeer )
#endif

::jace::proxy::Singleton Singleton::getInstance() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::Singleton >( "getInstance" ).invoke( staticGetJavaJniClass(), arguments );
}

void Singleton::run( ::jace::JArray< ::jace::proxy::java::lang::String > p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "run" ).invoke( *this, arguments );
}

void Singleton::main( ::jace::JArray< ::jace::proxy::java::lang::String > p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "main" ).invoke( staticGetJavaJniClass(), arguments );
}

void Singleton::finalize() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "finalize" ).invoke( *this, arguments );
}

/**
 * private static count
 *
 */
::jace::JFieldProxy< ::jace::proxy::types::JInt > Singleton::count() {
  return ::jace::JField< ::jace::proxy::types::JInt >( "count" ).get( staticGetJavaJniClass() );
}

/**
 * private static currentString
 *
 */
::jace::JFieldProxy< ::jace::proxy::java::lang::String > Singleton::currentString() {
  return ::jace::JField< ::jace::proxy::java::lang::String >( "currentString" ).get( staticGetJavaJniClass() );
}

/**
 * private static instance
 *
 */
::jace::JFieldProxy< ::jace::proxy::Singleton > Singleton::instance() {
  return ::jace::JField< ::jace::proxy::Singleton >( "instance" ).get( staticGetJavaJniClass() );
}

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
Singleton::Singleton( jobject jPeer ) Singleton_INITIALIZER {
  setJavaJniObject( jPeer );
}

Singleton::~Singleton() throw () {
}

const JClass* Singleton::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "Singleton" );
  return &javaClass;
}

const JClass* Singleton::getJavaJniClass() const throw ( JNIException ) {
  return Singleton::staticGetJavaJniClass();
}

END_NAMESPACE_2( jace, peer )

