
/**
 * These JNI mappings are for the Jace Peer for jace.examples.PeerExample.
 * Please do not edit these JNI mappings. Any changes made will be overwritten.
 * 
 * For more information, please refer to the Jace Developer's Guide.
 *
 */

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif

#ifndef JACE_JENLISTER_H
#include "jace/JEnlister.h"
#endif

#ifndef JACE_JARRAY_H
#include "jace/JArray.h"
#endif

#ifndef JACE_JFIELD_PROXY_H
#include "jace/JFieldProxy.h"
#endif

#include "jace/proxy/java/lang/Throwable.h"
#include "jace/proxy/java/lang/RuntimeException.h"
#include "jace/proxy/java/lang/Class.h"
#include "jace/proxy/java/lang/String.h"
#include "jace/JNIHelper.h"
#include "jace/StaticVmLoader.h"

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

#include <iostream>

/**
 * The JNI mapping for
 * 
 * Class: jace_examples_PeerExample
 * Method: getResources
 * Signature: ([Ljava/lang/String;)[Ljava/lang/String;
 *
 */
extern "C" JNIEXPORT jobjectArray JNICALL Java_jace_examples_PeerExample_getResources___3Ljava_lang_String_2( JNIEnv* env, jobject jP0, jobjectArray jP1 ) { 

  try {
    ::jace::peer::jace::examples::PeerExample* peer = dynamic_cast< ::jace::peer::jace::examples::PeerExample*>( ::jace::helper::getPeer( jP0 ) );
    ::jace::JArray< ::jace::proxy::java::lang::String > p1( jP1 );

    return static_cast<jobjectArray>( env->NewLocalRef( peer->getResources( p1 ).getJavaJniObject() ) );
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return NULL;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return NULL;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: jace_examples_PeerExample
 * Method: jaceSetVm
 * Signature: ()V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_jace_examples_PeerExample_jaceSetVm( JNIEnv *env, jclass jPeerClass ) {
  try {
    jclass jClassClass = env->FindClass( "java/lang/Class" );
    jmethodID forName = env->GetStaticMethodID( jClassClass, "forName", "(Ljava/lang/String;)Ljava/lang/Class;" );
    jstring objectClassStr = env->NewStringUTF( "java.lang.Object" );
    jobject loaderLock = env->CallStaticObjectMethod( jClassClass, forName, objectClassStr );

    jint rc = env->MonitorEnter( loaderLock );

    if ( rc < 0 ) {
      std::string msg = "Unable to obtain a lock on Object.class";
      std::cerr << msg;
      return;
    }

    if ( ! ::jace::helper::getVmLoader() ) {
      ::jace::helper::setVmLoader( ::jace::StaticVmLoader( 0 ) );
    }

    rc = env->MonitorExit( loaderLock );

    if ( rc < 0 ) {
      std::string msg = "Unable to release a lock on Object.class";
      std::cerr << msg;
      return;
    }
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    std::cerr << msg;
    return;
  }
}



/**
 * The JNI mapping for
 * 
 * Class: jace_examples_PeerExample
 * Method: jaceCreateInstance
 * Signature: ()J
 *
 */
extern "C" JNIEXPORT jlong JNICALL Java_jace_examples_PeerExample_jaceCreateInstance( JNIEnv *env, jobject jPeer ) {
  try {
    ::jace::Peer* peer = new ::jace::peer::jace::examples::PeerExample( jPeer );
    peer->initialize(); 
    return reinterpret_cast<jlong>( peer );
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return 0;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return 0;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: jace_examples_PeerExample
 * Method: jaceDestroyInstance
 * Signature: (J)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_jace_examples_PeerExample_jaceDestroyInstance( JNIEnv *env, jclass jPeerClass, jlong jNativeHandle ) {
  try {
    ::jace::Peer* peer = reinterpret_cast< ::jace::Peer*>( jNativeHandle );
    peer->destroy();
    delete peer; 
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    std::cerr << msg;
  }
}

