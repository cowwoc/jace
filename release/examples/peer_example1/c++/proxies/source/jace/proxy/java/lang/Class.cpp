
#ifndef JACE_PROXY_JAVA_LANG_CLASS_H
#include "jace/proxy/java/lang/Class.h"
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
#ifndef JACE_PROXY_JAVA_LANG_STRING_H
#include "jace/proxy/java/lang/String.h"
#endif
#ifndef JACE_TYPES_JBOOLEAN_H
#include "jace/proxy/types/JBoolean.h"
#endif
#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif
#ifndef JACE_PROXY_JAVA_IO_INPUTSTREAM_H
#include "jace/proxy/java/io/InputStream.h"
#endif
#ifndef JACE_PROXY_JAVA_NET_URL_H
#include "jace/proxy/java/net/URL.h"
#endif

BEGIN_NAMESPACE_4( jace, proxy, java, lang )

/**
 * The Jace C++ proxy class source for java/lang/Class.
 * Please do not edit this source, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
#ifndef VIRTUAL_INHERITANCE_IS_BROKEN
  #define Class_INITIALIZER : ::jace::proxy::java::lang::Object( NO_OP )
#else
  #define Class_INITIALIZER
#endif

::jace::proxy::java::lang::String Class::toString() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::String >( "toString" ).invoke( *this, arguments );
}

::jace::proxy::java::lang::Class Class::forName( ::jace::proxy::java::lang::String p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::proxy::java::lang::Class >( "forName" ).invoke( staticGetJavaJniClass(), arguments );
}

::jace::proxy::java::lang::Object Class::newInstance() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::Object >( "newInstance" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean Class::isInstance( ::jace::proxy::java::lang::Object p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "isInstance" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean Class::isAssignableFrom( ::jace::proxy::java::lang::Class p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "isAssignableFrom" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean Class::isInterface() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "isInterface" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean Class::isArray() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "isArray" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean Class::isPrimitive() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "isPrimitive" ).invoke( *this, arguments );
}

::jace::proxy::java::lang::String Class::getName() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::String >( "getName" ).invoke( *this, arguments );
}

::jace::proxy::java::lang::Class Class::getSuperclass() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::Class >( "getSuperclass" ).invoke( *this, arguments );
}

::jace::JArray< ::jace::proxy::java::lang::Class > Class::getInterfaces() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::JArray< ::jace::proxy::java::lang::Class > >( "getInterfaces" ).invoke( *this, arguments );
}

::jace::proxy::java::lang::Class Class::getComponentType() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::Class >( "getComponentType" ).invoke( *this, arguments );
}

::jace::proxy::types::JInt Class::getModifiers() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JInt >( "getModifiers" ).invoke( *this, arguments );
}

::jace::JArray< ::jace::proxy::java::lang::Object > Class::getSigners() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::JArray< ::jace::proxy::java::lang::Object > >( "getSigners" ).invoke( *this, arguments );
}

::jace::proxy::java::lang::Class Class::getDeclaringClass() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::java::lang::Class >( "getDeclaringClass" ).invoke( *this, arguments );
}

::jace::JArray< ::jace::proxy::java::lang::Class > Class::getClasses() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::JArray< ::jace::proxy::java::lang::Class > >( "getClasses" ).invoke( *this, arguments );
}

::jace::JArray< ::jace::proxy::java::lang::Class > Class::getDeclaredClasses() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::JArray< ::jace::proxy::java::lang::Class > >( "getDeclaredClasses" ).invoke( *this, arguments );
}

::jace::proxy::java::io::InputStream Class::getResourceAsStream( ::jace::proxy::java::lang::String p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::proxy::java::io::InputStream >( "getResourceAsStream" ).invoke( *this, arguments );
}

::jace::proxy::java::net::URL Class::getResource( ::jace::proxy::java::lang::String p0 ) {
  ::jace::JArguments arguments;
  arguments << p0;
  return ::jace::JMethod< ::jace::proxy::java::net::URL >( "getResource" ).invoke( *this, arguments );
}

::jace::proxy::types::JBoolean Class::desiredAssertionStatus() {
  ::jace::JArguments arguments;
  return ::jace::JMethod< ::jace::proxy::types::JBoolean >( "desiredAssertionStatus" ).invoke( *this, arguments );
}

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
Class::Class( jvalue value ) Class_INITIALIZER {
  setJavaJniValue( value );
}

Class::Class( jobject object ) Class_INITIALIZER {
  setJavaJniObject( object );
}

Class::Class( const Class& object ) Class_INITIALIZER {
  setJavaJniObject( object.getJavaJniObject() );
}

Class::Class( const NoOp& noOp ) Class_INITIALIZER {
}

const JClass* Class::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "java/lang/Class" );
  return &javaClass;
}

const JClass* Class::getJavaJniClass() const throw ( JNIException ) {
  return Class::staticGetJavaJniClass();
}

JEnlister< Class> Class::enlister;

END_NAMESPACE_4( jace, proxy, java, lang )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
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

