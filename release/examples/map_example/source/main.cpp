
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

#include "jace/proxy/java/util/Map.Entry.h"
using jace::proxy::java::util::Map_Entry;

#include "jace/proxy/java/util/Iterator.h"
using jace::proxy::java::util::Iterator;

#include "jace/javacast.h"
using jace::java_cast;

#include <vector>
using std::vector;

#include <iostream>
using std::cout;
using std::endl;


int main() {

  try {
    StaticVmLoader loader( JNI_VERSION_1_2 );
    jace::helper::createVm( loader, OptionList(), false );

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
  catch ( std::exception& e ) {
    cout << e.what() << endl;
    return -1;
  }

  return 0;
}

