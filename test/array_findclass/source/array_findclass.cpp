
/**
 * Tests that FindClass() works properly for JArray. FindClass() expects a special syntax for arrays
 * containing objects: [L...; whereas normally we don't include the surrounding L...; in FindClass()
 *
 * @author Gili Tzabari
 *
 */

/**
 * Jace has an optional array checking mechanism that you can turn on 
 * by #defining JACE_CHECK_ARRAYS in your code. This must be turned
 * on or off for your entire project.
 *
 */
#define JACE_CHECK_ARRAYS

#include "jace/JNIHelper.h"

#include "jace/proxy/types/JInt.h"
using jace::proxy::types::JInt;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JNIException.h"
using jace::JNIException;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;


int main() {

  try {
    // Standard Vm setup
    StaticVmLoader loader( JNI_VERSION_1_2 );
    OptionList list;
    list.push_back( jace::CustomOption( "-Xcheck:jni" ) );
    list.push_back( jace::CustomOption( "-Xmx16M" ) );
    jace::helper::createVm( loader, list, false );

    typedef JArray<String> StringArray;

    // Creates a new array of Java String with 1000 null elements
    StringArray strArray( 1000 );
		JNIEnv* env = jace::helper::attach();

		jclass result = env->FindClass(strArray.getJavaJniClass()->getName().c_str());

		if (result!=0)
			cout << "success!" << endl;
		else
			cout << "failure!" << endl;
  }
  catch ( exception& e ) {
    cout << e.what() << endl;
    return -1;
  }

  return 0;
}
