
#ifndef JACE_PROXY_JACE_EXAMPLES_PEEREXAMPLE_H
#define JACE_PROXY_JACE_EXAMPLES_PEEREXAMPLE_H

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

/**
 * Forward declarations for the classes that this class uses.
 *
 */
BEGIN_NAMESPACE_3( jace, proxy, types )
class JVoid;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_4( jace, proxy, java, lang )
class String;
END_NAMESPACE_4( jace, proxy, java, lang )

BEGIN_NAMESPACE_3( jace, proxy, types )
class JInt;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_4( jace, proxy, java, io )
class IOException;
END_NAMESPACE_4( jace, proxy, java, io )

BEGIN_NAMESPACE_4( jace, proxy, java, lang )
class Exception;
END_NAMESPACE_4( jace, proxy, java, lang )

BEGIN_NAMESPACE_4( jace, proxy, jace, examples )

/**
 * The Jace C++ proxy class for jace/examples/PeerExample.
 * Please do not edit this class, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
class PeerExample : public virtual ::jace::proxy::java::lang::Object {

public: 

/**
 * PeerExample
 *
 */
PeerExample( ::jace::proxy::java::lang::String p0, ::jace::proxy::types::JInt p1 );

/**
 * PeerExample
 *
 */
PeerExample( ::jace::proxy::java::lang::String p0 );

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

/**
 * getResources
 *
 */
::jace::JArray< ::jace::proxy::java::lang::String > getResources( ::jace::JArray< ::jace::proxy::java::lang::String > p0 );

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
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
PeerExample( jvalue value );
PeerExample( jobject object );
PeerExample( const PeerExample& object );
PeerExample( const NoOp& noOp );
virtual const JClass* getJavaJniClass() const throw ( JNIException );
static const JClass* staticGetJavaJniClass() throw ( JNIException );
static JEnlister< PeerExample> enlister;
};

END_NAMESPACE_4( jace, proxy, jace, examples )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
  template <> ElementProxy< ::jace::proxy::jace::examples::PeerExample>::ElementProxy( jarray array, jvalue element, int index );
  template <> ElementProxy< ::jace::proxy::jace::examples::PeerExample>::ElementProxy( const jace::ElementProxy< ::jace::proxy::jace::examples::PeerExample>& proxy );
#else
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
  template <> JFieldProxy< ::jace::proxy::jace::examples::PeerExample>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ );
  template <> JFieldProxy< ::jace::proxy::jace::examples::PeerExample>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ );
  template <> JFieldProxy< ::jace::proxy::jace::examples::PeerExample>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::jace::examples::PeerExample>& object );
#else
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

#endif // #ifndef JACE_PROXY_JACE_EXAMPLES_PEEREXAMPLE_H

