/**
 * Perf_Test
 *
 * Generates some performance data for Jace by making some
 * comparisons to direct JNI.
 *
 * @author Toby Reyelts
 */
#include "jace/JMethod.h"
#include "jace/proxy/types/JInt.h"
#include "jace/JArguments.h"

#include "jace/Jace.h"
using jace::java_cast;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/VirtualMachineShutdownError.h"
using jace::VirtualMachineShutdownError;

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
using std::cin;

const long count = 500000;

struct JaceHashCodeInvoke
{
  Object obj;

	JaceHashCodeInvoke()
	{
		obj = jace::java_new<Object>();
	}

  void operator()()
	{
    for (int i = 0; i < count; ++i)
      obj.hashCode();
  }
};

struct JniHashCodeInvoke
{
  jobject obj;
  jclass objClass;
  jmethodID hashCodeMethod;
  JNIEnv* env;

  JniHashCodeInvoke()
	{
		Object ob = jace::java_new<Object>();
    env = jace::attach();
    obj = env->NewLocalRef(ob);
    objClass = env->FindClass("java/lang/Object");
    hashCodeMethod = env->GetMethodID(objClass, "hashCode", "()I");
  }

  void operator()()
	{
    for (int i = 0; i < count; ++i)
		{
      // hashCodeMethod = env->GetMethodID(objClass, "hashCode", "()I");
      env->CallIntMethod(obj, hashCodeMethod);
    }
  }
};

struct JaceAttach
{

  void operator()()
	{
    for (int i = 0; i < count; ++i)
      jace::attach();
  }
};

struct JaceGlobalRef
{
  JNIEnv* env;
  jobject obj;

  JaceGlobalRef()
	{
    Object ob = jace::java_new<Object>();
    env = jace::attach();
    obj = env->NewLocalRef(ob);
  }

  void operator()()
	{
    for (int i = 0; i < count; ++i)
		{
      jobject ref = jace::newGlobalRef(env, obj);
      jace::deleteGlobalRef(env, ref);
    }
  }
};

struct JaceLocalRef
{
  JNIEnv* env;
  jobject obj;

  JaceLocalRef()
	{
    Object ob = jace::java_new<Object>();
    env = jace::attach();
    obj = env->NewLocalRef(ob);
  }

  void operator()()
	{
    for (int i = 0; i < count; ++i)
		{
      jobject ref = jace::newLocalRef(env, obj);
      jace::deleteLocalRef(env, ref);
    }
  }
};

struct JaceExceptionCheck
{
  void operator()()
	{
    JNIEnv* env = jace::attach();

    for (int i = 0; i < count; ++i)
      env->ExceptionCheck();
  }
};

struct JaceGetMethod
{
	/**
	 * Used to gain access to jace::Method::getMethodID().
	 */
	template<class ResultType> class ProfiledMethod: public jace::JMethod<ResultType>
	{
	public:
		/**
		 * Creates a new JMethod representing the method with the
		 * given name, belonging to the given class.
		 */
		ProfiledMethod(const std::string& name): jace::JMethod<ResultType>(name)
		{}

		/**
		 * Expose method publically.
		 */
		jmethodID getMethodID(const jace::JClass& jClass, const jace::JArguments& arguments, bool isStatic = false)
		{
			return jace::JMethod<ResultType>::getMethodID(jClass, arguments, isStatic);
		}
	};

  void operator()()
	{
    const jace::JClass& jClass = Object::staticGetJavaJniClass();
    jace::JArguments arguments;

    for (int i = 0; i < count; ++i)
		{
      ProfiledMethod<jace::proxy::types::JInt> method("hashCode");
      method.getMethodID(jClass, arguments);
    }
  }
};

template <class Op> void perform(Op& op, string msg)
{
  jlong startTime = System::currentTimeMillis();
  op();
  jlong endTime = System::currentTimeMillis();
  jlong elapsedTime = endTime - startTime;
  double average = (elapsedTime * 1.0) / count;

  cout << msg << " " << average << " (ms) " << endl;
}

int main()
{
  try
	{
		OptionList list;
		list.push_back(jace::ClassPath("jace-runtime.jar"));
	  jace::createVm(StaticVmLoader(JNI_VERSION_1_2), list);

    perform(JniHashCodeInvoke(), "Average JNI Object.hashCode");
    perform(JaceHashCodeInvoke(), "Average Jace Object.hashCode");
    perform(JaceAttach(), "Average Jace attach");
    perform(JaceGlobalRef(), "Average Jace NewGlobalRef+DeleteGlobalRef");
    perform(JaceLocalRef(), "Average Jace NewLocalRef+DeleteLocalRef");
    perform(JaceExceptionCheck(), "Average ExceptionCheck");
    perform(JaceGetMethod(), "Average Method lookup");
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
  }
  return 0;
}
