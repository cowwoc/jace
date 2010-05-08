
/**
 * These JNI mappings are for the Jace Peer for Singleton.
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

#include <iostream>

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: print
 * Signature: ()V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_print__( JNIEnv* env, jobject jP0 ) { 

  try {
    ::jace::peer::Singleton* peer = dynamic_cast< ::jace::peer::Singleton*>( ::jace::helper::getPeer( jP0 ) );

    peer->print(  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: print
 * Signature: (Ljava/lang/String;)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_print__Ljava_lang_String_2( JNIEnv* env, jobject jP0, jobject jP1 ) { 

  try {
    ::jace::peer::Singleton* peer = dynamic_cast< ::jace::peer::Singleton*>( ::jace::helper::getPeer( jP0 ) );
    ::jace::proxy::java::lang::String p1( jP1 );

    peer->print( p1  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: print
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_print__Ljava_lang_String_2Ljava_lang_String_2( JNIEnv* env, jobject jP0, jobject jP1, jobject jP2 ) { 

  try {
    ::jace::peer::Singleton* peer = dynamic_cast< ::jace::peer::Singleton*>( ::jace::helper::getPeer( jP0 ) );
    ::jace::proxy::java::lang::String p1( jP1 );
    ::jace::proxy::java::lang::String p2( jP2 );

    peer->print( p1, p2  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: print
 * Signature: ([Ljava/lang/String;)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_print___3Ljava_lang_String_2( JNIEnv* env, jobject jP0, jobjectArray jP1 ) { 

  try {
    ::jace::peer::Singleton* peer = dynamic_cast< ::jace::peer::Singleton*>( ::jace::helper::getPeer( jP0 ) );
    ::jace::JArray< ::jace::proxy::java::lang::String > p1( jP1 );

    peer->print( p1  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: printHelloWorld
 * Signature: ()V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_printHelloWorld__( JNIEnv* env, jclass jP0 ) { 

  try {
    ::jace::peer::Singleton::printHelloWorld(  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: print
 * Signature: (I)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_print__I( JNIEnv* env, jclass jP0, jint jP1 ) { 

  try {
    ::jace::proxy::types::JInt p1( jP1 );

    ::jace::peer::Singleton::print( p1  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: setString
 * Signature: (Ljava/lang/String;)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_setString__Ljava_lang_String_2( JNIEnv* env, jclass jP0, jobject jP1 ) { 

  try {
    ::jace::proxy::java::lang::String p1( jP1 );

    ::jace::peer::Singleton::setString( p1  );
    return;
  }
  catch ( jace::proxy::java::lang::Throwable& t ) {
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );
    return ;
  }
  catch ( std::exception& e ) {
    std::string msg = std::string( "An unexpected JNI error has occurred: " ) + e.what();
    jace::proxy::java::lang::RuntimeException ex( msg );
    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );
    return ;
  }
}

/**
 * The JNI mapping for
 * 
 * Class: Singleton
 * Method: getString
 * Signature: ()Ljava/lang/String;
 *
 */
extern "C" JNIEXPORT jobject JNICALL Java_Singleton_getString__( JNIEnv* env, jclass jP0 ) { 

  try {
    return static_cast<jobject>( env->NewLocalRef( ::jace::peer::Singleton::getString( ).getJavaJniObject() ) );
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
 * Class: Singleton
 * Method: getCount
 * Signature: ()I
 *
 */
extern "C" JNIEXPORT jint JNICALL Java_Singleton_getCount__( JNIEnv* env, jclass jP0 ) { 

  try {
    return ::jace::peer::Singleton::getCount( );
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
 * Class: Singleton
 * Method: jaceSetVm
 * Signature: ()V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_jaceSetVm( JNIEnv *env, jclass jPeerClass ) {
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
 * Class: Singleton
 * Method: jaceCreateInstance
 * Signature: ()J
 *
 */
extern "C" JNIEXPORT jlong JNICALL Java_Singleton_jaceCreateInstance( JNIEnv *env, jobject jPeer ) {
  try {
    ::jace::Peer* peer = new ::jace::peer::Singleton( jPeer );
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
 * Class: Singleton
 * Method: jaceDestroyInstance
 * Signature: (J)V
 *
 */
extern "C" JNIEXPORT void JNICALL Java_Singleton_jaceDestroyInstance( JNIEnv *env, jclass jPeerClass, jlong jNativeHandle ) {
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

