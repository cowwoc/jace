
#ifndef JACE_PROXY_JAVA_IO_BUFFEREDREADER_H
#define JACE_PROXY_JAVA_IO_BUFFEREDREADER_H

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
#ifndef JACE_PROXY_JAVA_IO_READER_H
#include "jace/proxy/java/io/Reader.h"
#endif

/**
 * Classes which this class is fully dependent upon.
 *
 */
#ifndef JACE_TYPES_JCHAR_H
#include "jace/proxy/types/JChar.h"
#endif

/**
 * Forward declarations for the classes that this class uses.
 *
 */
BEGIN_NAMESPACE_3( jace, proxy, types )
class JVoid;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_3( jace, proxy, types )
class JInt;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_4( jace, proxy, java, io )
class IOException;
END_NAMESPACE_4( jace, proxy, java, io )

BEGIN_NAMESPACE_4( jace, proxy, java, lang )
class String;
END_NAMESPACE_4( jace, proxy, java, lang )

BEGIN_NAMESPACE_3( jace, proxy, types )
class JLong;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_3( jace, proxy, types )
class JBoolean;
END_NAMESPACE_3( jace, proxy, types )

BEGIN_NAMESPACE_4( jace, proxy, java, io )

/**
 * The Jace C++ proxy class for java/io/BufferedReader.
 * Please do not edit this class, as any changes you make will be overwritten.
 * For more information, please refer to the Jace Developer's Guide.
 *
 */
class BufferedReader : public ::jace::proxy::java::io::Reader {

public: 

/**
 * BufferedReader
 *
 */
BufferedReader( ::jace::proxy::java::io::Reader p0, ::jace::proxy::types::JInt p1 );

/**
 * BufferedReader
 *
 */
BufferedReader( ::jace::proxy::java::io::Reader p0 );

/**
 * read
 *
 */
::jace::proxy::types::JInt read();

/**
 * read
 *
 */
::jace::proxy::types::JInt read( ::jace::JArray< ::jace::proxy::types::JChar > p0, ::jace::proxy::types::JInt p1, ::jace::proxy::types::JInt p2 );

/**
 * readLine
 *
 */
::jace::proxy::java::lang::String readLine();

/**
 * skip
 *
 */
::jace::proxy::types::JLong skip( ::jace::proxy::types::JLong p0 );

/**
 * ready
 *
 */
::jace::proxy::types::JBoolean ready();

/**
 * markSupported
 *
 */
::jace::proxy::types::JBoolean markSupported();

/**
 * mark
 *
 */
void mark( ::jace::proxy::types::JInt p0 );

/**
 * reset
 *
 */
void reset();

/**
 * close
 *
 */
void close();

/**
 * The following methods are required to integrate this class
 * with the Jace framework.
 *
 */
BufferedReader( jvalue value );
BufferedReader( jobject object );
BufferedReader( const BufferedReader& object );
BufferedReader( const NoOp& noOp );
virtual const JClass* getJavaJniClass() const throw ( JNIException );
static const JClass* staticGetJavaJniClass() throw ( JNIException );
static JEnlister< BufferedReader> enlister;
};

END_NAMESPACE_4( jace, proxy, java, io )

BEGIN_NAMESPACE( jace )

#ifndef PUT_TSDS_IN_HEADER
  template <> ElementProxy< ::jace::proxy::java::io::BufferedReader>::ElementProxy( jarray array, jvalue element, int index );
  template <> ElementProxy< ::jace::proxy::java::io::BufferedReader>::ElementProxy( const jace::ElementProxy< ::jace::proxy::java::io::BufferedReader>& proxy );
#else
  template <> ElementProxy< ::jace::proxy::java::io::BufferedReader>::ElementProxy( jarray array, jvalue element, int index ) : 
    ::jace::proxy::java::io::BufferedReader( element ), Object( NO_OP ), mIndex( index ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, array ) );
  }
  template <> ElementProxy< ::jace::proxy::java::io::BufferedReader>::ElementProxy( const jace::ElementProxy< ::jace::proxy::java::io::BufferedReader>& proxy ) : 
    ::jace::proxy::java::io::BufferedReader( proxy.getJavaJniObject() ), Object( NO_OP ), mIndex( proxy.mIndex ) {
    JNIEnv* env = ::jace::helper::attach();
    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, proxy.parent ) );
  }
#endif

#ifndef PUT_TSDS_IN_HEADER
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ );
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ );
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::java::io::BufferedReader>& object );
#else
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) : 
    ::jace::proxy::java::io::BufferedReader( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    if ( parent_ ) {
      parent = ::jace::helper::newGlobalRef( env, parent_ );
    }
    else {
      parent = parent_;
    }

    parentClass = 0;
  }
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) : 
    ::jace::proxy::java::io::BufferedReader( value ), Object( NO_OP ), fieldID( fieldID_ ) {
    JNIEnv* env = ::jace::helper::attach();

    parent = 0;
    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, parentClass_ ) );
  }
  template <> JFieldProxy< ::jace::proxy::java::io::BufferedReader>::JFieldProxy( const ::jace::JFieldProxy< ::jace::proxy::java::io::BufferedReader>& object ) : 
    ::jace::proxy::java::io::BufferedReader( object.getJavaJniValue() ), Object( NO_OP ) {

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

#endif // #ifndef JACE_PROXY_JAVA_IO_BUFFEREDREADER_H

