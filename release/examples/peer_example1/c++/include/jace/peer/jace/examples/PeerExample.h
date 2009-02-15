
#ifndef JACE_PEER_JACE_EXAMPLES_PEEREXAMPLE_H
#define JACE_PEER_JACE_EXAMPLES_PEEREXAMPLE_H

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
#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif


#ifndef JACE_PROXY_JACE_EXAMPLES_PEEREXAMPLE_H
#include "jace/proxy/jace/examples/PeerExample.h"
#endif

BEGIN_NAMESPACE_4( jace, peer, jace, examples )

/**
 * PeerExample
 * 
 * This header provides the declaration for the Jace Peer, PeerExample.
 * To complete this Peer, you must create a new source file containing the
 * definitions for all native methods declared for this Peer.
 * 
 * You may also override initialize() and destroy(), if your Peer requires
 * custom initialization or destruction.
 *
 */
class PeerExample : public ::jace::Peer, public virtual ::jace::proxy::java::lang::Object {

public: 

// Methods which must be implemented by the Developer
// --------------------------------------------------

/**
 * getResources
 *
 */
virtual ::jace::JArray< ::jace::proxy::java::lang::String > getResources( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );


// Methods made available by Jace
// ------------------------------

/**
 * main
 *
 */
static void main( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );

/**
 * run
 *
 */
static void run( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );

/**
 * close
 *
 */
void close();

/**
 * finalize
 *
 */
void finalize();

// Fields made available by Jace
// -----------------------------

/**
 * private server
 *
 */
::jace::JFieldProxy< ::jace::proxy::java::lang::String > server();

/**
 * private port
 *
 */
::jace::JFieldProxy< ::jace::proxy::types::JInt > port();

/**
 * private resource
 *
 */
::jace::JFieldProxy< ::jace::proxy::java::lang::String > resource();

// Methods internal to Jace
// ------------------------

/**
 * Called when the VM instantiates a new PeerExample.
 *
 */
PeerExample( jobject obj );

/**
 * Called when the the user explicitly collects a PeerExample
 * or when the VM garbage collects a PeerExample.
 *
 */
virtual ~PeerExample() throw ();

virtual const JClass* getJavaJniClass() const throw ( JNIException );
static const JClass* staticGetJavaJniClass() throw ( JNIException );

// User defined members
// --------------------
#include "jace/peer/jace/examples/PeerExample_user.h"

};

END_NAMESPACE_4( jace, peer, jace, examples )
#endif // #ifndef JACE_PEER_JACE_EXAMPLES_PEEREXAMPLE_H

