#include "jace/Jace.h"
using jace::java_cast;
using jace::java_new;

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

/**
 * NOTE: jvm.dll must be in the system PATH at runtime. You cannot simply copy the library
 *       into the application directory because it locates dependencies relative to its location.
 *       See http://java.sun.com/products/jdk/faq/jni-j2sdk-faq.html#move for more information.
 */
int main()
{
  try
	{
    StaticVmLoader loader(JNI_VERSION_1_2);
    jace::OptionList list;
		list.push_back(jace::ClassPath("jace-runtime.jar"));
    jace::createVm(loader, list, false);

    for (int i = 0; i < 1000; ++i)
		{
			Map map = java_new<HashMap>();

			map.put(java_new<Integer>("1"), java_new<String>("Hello 1"));
      map.put(java_new<Integer>("2"), java_new<String>("Hello 2"));
      map.put(java_new<Integer>("3"), java_new<String>("Hello 3"));

      Set entrySet(map.entrySet());

      for (Iterator it(entrySet.iterator()); it.hasNext();)
			{
        Map_Entry entry = java_cast<Map_Entry>(it.next());
        Integer key = java_cast<Integer>(entry.getKey());
        String value = java_cast<String>(entry.getValue());
        cout << "key: <" << key << "> value: <" << value << ">" << endl;
      }
    }
  }
	catch (VirtualMachineShutdownError&)
	{
		cout << "The JVM was terminated in mid-execution. " << endl;
    return -2;
	}
  catch (JNIException& jniException)
	{
    cout << "An unexpected JNI error has occurred: " << jniException.what() << endl;
    return -2;
  }
	catch (Throwable& t)
	{
		t.printStackTrace();
		return -2;
	}
  catch (std::exception& e)
	{
    cout << "An unexpected C++ error has occurred: " << e.what() << endl;
    return -1;
  }
  return 0;
}
