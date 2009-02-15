
/**
 * Perf_Test
 *
 * Generates some performance data for Jace by making some
 * comparisons to direct JNI.
 *
 * @author Toby Reyelts
 *
 */

#include "jace/JMethod.h"
#include "jace/proxy/types/JInt.h"
#include "jace/JArguments.h"

#include "jace/JNIHelper.h"

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/javacast.h"
using jace::java_cast;

#include "jace/proxy/java/lang/String.h"
#include "jace/proxy/java/lang/System.h"
#include "jace/proxy/java/io/PrintWriter.h"
#include "jace/proxy/java/io/IOException.h"
#include "jace/proxy/java/io/PrintStream.h"

using namespace jace::proxy::java::lang;
using namespace jace::proxy::java::io;

#include <string>
using std::string;

#include <iostream>
using std::cout;
using std::endl;

const long count = 500000;

struct JaceHashCodeInvoke {

  Object obj;

  void operator()() {
    for ( int i = 0; i < count; ++i ) {
      obj.hashCode();
    }
  }
};

struct JniHashCodeInvoke {

  jobject obj;
  jclass objClass;
  jmethodID hashCodeMethod;
  JNIEnv* env;

  JniHashCodeInvoke() {
    Object ob;
    env = jace::helper::attach();
    obj = env->NewLocalRef( jace::java_cast<jobject>( ob ) );
    objClass = env->FindClass( "java/lang/Object" );
    hashCodeMethod = env->GetMethodID( objClass, "hashCode", "()I" );
  }

  void operator()() {
    for ( int i = 0; i < count; ++i ) {
      // hashCodeMethod = env->GetMethodID( objClass, "hashCode", "()I" );
      env->CallIntMethod( obj, hashCodeMethod );
    }
  }
};

struct JaceAttach {

  void operator()() {
    for ( int i = 0; i < count; ++i ) {
      jace::helper::attach();
    }
  }
};

struct JaceGlobalRef {

  JNIEnv* env;
  jobject obj;

  JaceGlobalRef() {
    Object ob;
    env = jace::helper::attach();
    obj = env->NewLocalRef( jace::java_cast<jobject>( ob ) );
  }

  void operator()() { 
    for ( int i = 0; i < count; ++i ) {
      jobject ref = jace::helper::newGlobalRef( env, obj );
      jace::helper::deleteGlobalRef( env, ref );
    }
  }
};

struct JaceLocalRef {

  JNIEnv* env;
  jobject obj;

  JaceLocalRef() {
    Object ob;
    env = jace::helper::attach();
    obj = env->NewLocalRef( jace::java_cast<jobject>( ob ) );
  }

  void operator()() { 
    for ( int i = 0; i < count; ++i ) {
      jobject ref = jace::helper::newLocalRef( env, obj );
      jace::helper::deleteLocalRef( env, ref );
    }
  }
};

struct JaceExceptionCheck {
  void operator()() {
    JNIEnv* env = jace::helper::attach();

    for ( int i = 0; i < count; ++i ) {
      env->ExceptionCheck();
    }
  }
};

struct JaceGetMethod {

  void operator()() {
    const jace::JClass* jClass = Object::staticGetJavaJniClass();
    jace::JArguments arguments;

    for ( int i = 0; i < count; ++i ) {
      jace::JMethod<jace::proxy::types::JInt> method( "hashCode" );
      method.getMethodID( jClass, arguments );
    }
  }
};

template <class Op> void perform( Op& op, string msg ) {

  jlong startTime = System::currentTimeMillis();
  op();
  jlong endTime = System::currentTimeMillis();
  jlong elapsedTime = endTime - startTime;
  double average = ( elapsedTime * 1.0 ) / count;

  cout << msg << " " << average << " (ms) " << endl;
}

int main() {

  jace::helper::createVm( StaticVmLoader( JNI_VERSION_1_2 ), OptionList() );

  try { 
    perform( JniHashCodeInvoke(), "Average JNI Object.hashCode" );
    perform( JaceHashCodeInvoke(), "Average Jace Object.hashCode" );
    perform( JaceAttach(), "Average Jace attach" );
    perform( JaceGlobalRef(), "Average Jace NewGlobalRef+DeleteGlobalRef" );
    perform( JaceLocalRef(), "Average Jace NewLocalRef+DeleteLocalRef" );
    perform( JaceExceptionCheck(), "Average ExceptionCheck" );
    perform( JaceGetMethod(), "Average Method lookup" );
  }
  catch ( std::exception& e ) {
    cout << e.what() << endl;
  }

  return 0;
}


