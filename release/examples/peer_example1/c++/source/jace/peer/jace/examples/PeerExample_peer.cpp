
/**
 * This is the source for the implementation of the Jace Peer for jace.examples.PeerExample.
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
#ifndef JACE_PROXY_JAVA_LANG_EXCEPTION_H
#include "jace/proxy/java/lang/Exception.h"
#endif
#ifndef JACE_PROXY_JAVA_IO_IOEXCEPTION_H
#include "jace/proxy/java/io/IOException.h"
#endif
#ifndef JACE_PROXY_JACE_EXAMPLES_PEEREXAMPLE_H
#include "jace/proxy/jace/examples/PeerExample.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif
#ifndef JACE_TYPES_JVOID_H
#include "jace/proxy/types/JVoid.h"
#endif

#ifndef JACE_PEER_JACE_EXAMPLES_PEEREXAMPLE_H
#include "jace/peer/jace/examples/PeerExample.h"
#endif

BEGIN_NAMESPACE_4( jace, peer, jace, examples )

#ifndef VIRTUAL_INHERITANCE_IS_BROKEN
  #define PeerExample_INITIALIZER : ::jace::proxy::java::lang::Object( NO_OP ), ::jace::Peer( jPeer )
#else
  #define PeerExample_INITIALIZER : ::jace::Peer( jPeer )
#endif

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

void PeerExample::close() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "close" ).invoke( *this, arguments );
}

void PeerExample::finalize() {
  ::jace::JArguments arguments;
  ::jace::JMethod< ::jace::proxy::types::JVoid >( "finalize" ).invoke( *this, arguments );
}

/**
 * private server
 *
 */
::jace::JFieldProxy< ::jace::proxy::java::lang::String > PeerExample::server() {
  return ::jace::JField< ::jace::proxy::java::lang::String >( "server" ).get( *this );
}

/**
 * private port
 *
 */
::jace::JFieldProxy< ::jace::proxy::types::JInt > PeerExample::port() {
  return ::jace::JField< ::jace::proxy::types::JInt >( "port" ).get( *this );
}

/**
 * private resource
 *
 */
::jace::JFieldProxy< ::jace::proxy::java::lang::String > PeerExample::resource() {
  return ::jace::JField< ::jace::proxy::java::lang::String >( "resource" ).get( *this );
}

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
PeerExample::PeerExample( jobject jPeer ) PeerExample_INITIALIZER {
  setJavaJniObject( jPeer );
}

PeerExample::~PeerExample() throw () {
}

const JClass* PeerExample::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "jace/examples/PeerExample" );
  return &javaClass;
}

const JClass* PeerExample::getJavaJniClass() const throw ( JNIException ) {
  return PeerExample::staticGetJavaJniClass();
}

END_NAMESPACE_4( jace, peer, jace, examples )

