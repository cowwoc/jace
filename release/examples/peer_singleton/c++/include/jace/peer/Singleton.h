
#ifndef JACE_PEER_SINGLETON_H
#define JACE_PEER_SINGLETON_H

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


/**
 * The Peer class from which this class derives.
 *
 */
#include "jace/Peer.h"

/**
 * The super class for this class.
 *
 */
#ifndef JACE_PROXY_JAVA_LANG_OBJECT_H
#include "jace/proxy/java/lang/Object.h"
#endif

/**
 * Classes which this class is fully dependent upon.
 *
 */
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif


#ifndef JACE_PROXY_SINGLETON_H
#include "jace/proxy/Singleton.h"
#endif

BEGIN_NAMESPACE_2( jace, peer )

/**
 * Singleton
 * 
 * This header provides the declaration for the Jace Peer, Singleton.
 * To complete this Peer, you must create a new source file containing the
 * definitions for all native methods declared for this Peer.
 * 
 * You may also override initialize() and destroy(), if your Peer requires
 * custom initialization or destruction.
 *
 */
class Singleton : public ::jace::Peer, public virtual ::jace::proxy::java::lang::Object {

public: 

// Methods which must be implemented by the Developer
// --------------------------------------------------

/**
 * print
 *
 */
void print();

/**
 * print
 *
 */
void print( ::jace::proxy::java::lang::String p0 );

/**
 * print
 *
 */
void print( ::jace::proxy::java::lang::String p0, ::jace::proxy::java::lang::String p1 );

/**
 * print
 *
 */
void print( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );

/**
 * printHelloWorld
 *
 */
static void printHelloWorld();

/**
 * print
 *
 */
static void print( ::jace::proxy::types::JInt p0 );

/**
 * setString
 *
 */
static void setString( ::jace::proxy::java::lang::String p0 );

/**
 * getString
 *
 */
static ::jace::proxy::java::lang::String getString();

/**
 * getCount
 *
 */
static ::jace::proxy::types::JInt getCount();


// Methods made available by Jace
// ------------------------------

/**
 * getInstance
 *
 */
static ::jace::proxy::Singleton getInstance();

/**
 * run
 *
 */
void run( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );

/**
 * main
 *
 */
static void main( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );

/**
 * finalize
 *
 */
void finalize();

// Fields made available by Jace
// -----------------------------

/**
 * private static count
 *
 */
static ::jace::JFieldProxy< ::jace::proxy::types::JInt > count();

/**
 * private static currentString
 *
 */
static ::jace::JFieldProxy< ::jace::proxy::java::lang::String > currentString();

/**
 * private static instance
 *
 */
static ::jace::JFieldProxy< ::jace::proxy::Singleton > instance();

// Methods internal to Jace
// ------------------------

/**
 * Called when the VM instantiates a new Singleton.
 *
 */
Singleton( jobject obj );

/**
 * Called when the the user explicitly collects a Singleton
 * or when the VM garbage collects a Singleton.
 *
 */
virtual ~Singleton() throw ();

virtual const JClass* getJavaJniClass() const throw ( JNIException );
static const JClass* staticGetJavaJniClass() throw ( JNIException );

// User defined members
// --------------------
#include "jace/peer/Singleton_user.h"

};

END_NAMESPACE_2( jace, peer )
#endif // #ifndef JACE_PEER_SINGLETON_H

