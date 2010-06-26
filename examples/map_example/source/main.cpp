
#include "jace/JNIHelper.h"
using jace::OptionList;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/proxy/java/util/Set.h"
using jace::proxy::java::util::Set;

#include "jace/proxy/java/lang/System.h"
using jace::proxy::java::lang::System;

#include "jace/proxy/java/lang/Object.h"
using ::jace::proxy::java::lang::Object;

#include "jace/proxy/java/lang/Integer.h"
using jace::proxy::java::lang::Integer;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

#include "jace/proxy/java/util/Map.h"
using jace::proxy::java::util::Map;

#include "jace/proxy/java/util/HashMap.h"
using jace::proxy::java::util::HashMap;

#include "jace/proxy/java/util/Map_Entry.h"
using jace::proxy::java::util::Map_Entry;

#include "jace/proxy/java/util/Iterator.h"
using jace::proxy::java::util::Iterator;

#include "jace/operators.h"
using jace::java_cast;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/VirtualMachineShutdownError.h"
using jace::VirtualMachineShutdownError;

#include "jace/proxy/java/lang/Throwable.h"
using jace::proxy::java::lang::Throwable;

#include <vector>
using std::vector;

#include <iostream>
using std::cout;
using std::endl;


int main() {

  try {
    StaticVmLoader loader( JNI_VERSION_1_2 );
    OptionList list;
		list.push_back( jace::ClassPath( "jace-runtime.jar" ) );
    jace::helper::createVm( loader, list, false );

    for ( int i = 0; i < 1000; ++i ) {

      Map map = HashMap();

      map.put( Integer( "1" ), String( "Hello 1" ) );
      map.put( Integer( "2" ), String( "Hello 2" ) );
      map.put( Integer( "3" ), String( "Hello 3" ) );

      Set entrySet( map.entrySet() );
 
      for ( Iterator it( entrySet.iterator() ); it.hasNext(); ) {
        Map_Entry entry = jace::java_cast<Map_Entry>( it.next() );
        Integer key = jace::java_cast<Integer>( entry.getKey() );
        String value = jace::java_cast<String>( entry.getValue() );
        cout << "key: <" << key << "> value: <" << value << ">" << endl;
      }
    }
  }
	catch ( VirtualMachineShutdownError& ) {
		cout << "The JVM was terminated in mid-execution. " << endl;
    return -2;
	}
  catch ( JNIException& jniException ) {
    cout << "An unexpected JNI error occured. " << jniException.what() << endl;
    return -2;
  }
	catch (Throwable& t) {
		t.printStackTrace();
		return -2;
	}
  catch ( std::exception& e ) {
    cout << e.what() << endl;
    return -1;
  }

  return 0;
}

