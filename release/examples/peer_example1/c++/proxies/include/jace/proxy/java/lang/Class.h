
#ifndef JACE_PROXY_JAVA_LANG_CLASS_H
#define JACE_PROXY_JAVA_LANG_CLASS_H

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
 * The interfaces implemented by this class.
 *
 */
#ifndef JACE_PROXY_JAVA_IO_SERIALIZABLE_H
#include "jace/proxy/java/io/Serializable.h"
#endif

/**
 * Classes which this class is fully dependent upon.
 *
 */

/**
 * Forward declarations for the classes that this class uses.
 *
 */
BEGIN_NAMESPACE_4( jace, proxy, java, lang )
class String;
END_NAMESPACE_4( jace, proxy, java, lang )

BEGIN_NAMESPACE_3( jace, proxy, types )
class JBoolean;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_3( jace, proxy, types )
class JInt;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_4( jace, proxy, java, io )
class InputStream;
END_NAMESPACE_4( jace, proxy, java, io )

BEGIN_NAMESPACE_4( jace, proxy, java, net )
class URL;
END_NAMESPACE_4( jace, proxy, java, net )

BEGIN_NAMESPACE_4( jace, proxy, java, lang )

/**
 * The Jace C++ proxy class for java/lang/Class.
 * Please do not edit this class, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
class Class : public virtual ::jace::proxy::java::lang::Object, public virtual ::jace::proxy::java::io::Serializable {

public: 

/**
 * toString
 *
 */
::jace::proxy::java::lang::String toString();

/**
 * forName
 *
 */
static ::jace::proxy::java::lang::Class forName( ::jace::proxy::java::lang::String p0 );

/**
 * newInstance
 *
 */
::jace::proxy::java::lang::Object newInstance();

/**
 * isInstance
 *
 */
::jace::proxy::types::JBoolean isInstance( ::jace::proxy::java::lang::Object p0 );

/**
 * isAssignableFrom
 *
 */
::jace::proxy::types::JBoolean isAssignableFrom( ::jace::proxy::java::lang::Class p0 );

/**
 * isInterface
 *
 */
::jace::proxy::types::JBoolean isInterface();

/**
 * isArray
 *
 */
::jace::proxy::types::JBoolean isArray();

/**
 * isPrimitive
 *
 */
::jace::proxy::types::JBoolean isPrimitive();

/**
 * getName
 *
 */
::jace::proxy::java::lang::String getName();

/**
 * getSuperclass
 *
 */
::jace::proxy::java::lang::Class getSuperclass();

/**
 * getInterfaces
 *
 */
::jace::JArray< ::jace::proxy::java::lang::Class > getInterfaces();

/**
 * getComponentType
 *
 */
::jace::proxy::java::lang::Class getComponentType();

/**
 * getModifiers
 *
 */
::jace::proxy::types::JInt getModifiers();

/**
 * getSigners
 *
 */
::jace::JArray< ::jace::proxy::java::lang::Object > getSigners();

/**
 * getDeclaringClass
 *
 */
::jace::proxy::java::lang::Class getDeclaringClass();

/**
 * getClasses
 *
 */
::jace::JArray< ::jace::proxy::java::lang::Class > getClasses();

/**
 * getDeclaredClasses
 *
 */
::jace::JArray< ::jace::proxy::java::lang::Class > getDeclaredClasses();

/**
 * getResourceAsStream
 *
 */
::jace::proxy::java::io::InputStream getResourceAsStream( ::jace::proxy::java::lang::String p0 );

/**
 * getResource
 *
 */
::jace::proxy::java::net::URL getResource( ::jace::proxy::java::lang::String p0 );

/**
 * desiredAssertionStatus
 *
 */
::jace::proxy::types::JBoolean desiredAssertionStatus();

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
Class( jvalue value );
Class( jobject object );
Class( const Class& object );
Class( const NoOp& noOp );
virtual const JClass* getJavaJniClass() const throw ( JNIException );
static const JClass* staticGetJavaJniClass() throw ( JNIException );
static JEnlister< Class> enlister;
};

END_NAMESPACE_4( jace, proxy, java, lang )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
  template <> ElementProxy< ::jace::proxy::java::lang::Class>::ElementProxy( jarray array, jvalue element, int index );
  template <> ElementProxy< ::jace::proxy::java::lang::Class>::ElementProxy( const jace::ElementProxy< ::jace::proxy::java::lang::Class>& proxy );
#else
  template <> ElementProxy< ::jace::proxy::java::lang::Class>::ElementProxy( jarray array, jvalue element, int index ) : 
    ::jace::proxy::java::lang::Class( element ), Object( NO_OP ), mIndex( index ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, array ) );
  }
  template <> ElementProxy< ::jace::proxy::java::lang::Class>::ElementProxy( const jace::ElementProxy< ::jace::proxy::java::lang::Class>& proxy ) : 
    ::jace::proxy::java::lang::Class( proxy.getJavaJniObject() ), Object( NO_OP ), mIndex( proxy.mIndex ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, proxy.parent ) );
  }
#endif

#ifndef PUT_TSDS_IN_HEADER
  template <> JFieldProxy< ::jace::proxy::java::lang::Class>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ );
  template <> JFieldProxy< ::jace::proxy::java::lang::Class>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ );
  template <> JFieldProxy< ::jace::proxy::java::lang::Class>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::java::lang::Class>& object );
#else
  template <> JFieldProxy< ::jace::proxy::java::lang::Class>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) : 
    ::jace::proxy::java::lang::Class( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    if ( parent_ ) {
      parent = ::jace::helper::newGlobalRef( env, parent_ );
    }
    else {
      parent = parent_;
    }

    parentClass = 0;
  }
  template <> JFieldProxy< ::jace::proxy::java::lang::Class>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) : 
    ::jace::proxy::java::lang::Class( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    parent = 0;
    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, parentClass_ ) );
  }
  template <> JFieldProxy< ::jace::proxy::java::lang::Class>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::java::lang::Class>& object ) : 
    ::jace::proxy::java::lang::Class( object.getJavaJniValue() ), Object( NO_OP ) {

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

#endif // #ifndef JACE_PROXY_JAVA_LANG_CLASS_H

