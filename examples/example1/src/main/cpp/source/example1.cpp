#include "jace/Jace.h"
using jace::java_new;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JArray.h"
using jace::JArray;

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

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;

/**
 * A prototypical Hello World example.
 *
 * This program demonstrates a few different ways to print "Hello World"
 * to standard output. For more information about this example, please read
 * the "Jace Developer's Guide".
 *
 * NOTE: jvm.dll must be in the system PATH at runtime. You cannot simply copy the library
 *       into the application directory because it locates dependencies relative to its location.
 *       See http://java.sun.com/products/jdk/faq/jni-j2sdk-faq.html#move for more information.
 */
int main()
{
	try
	{
		StaticVmLoader loader(JNI_VERSION_1_2);
		OptionList list;
		list.push_back(jace::CustomOption("-Xcheck:jni"));
		list.push_back(jace::CustomOption("-Xmx16M"));
		list.push_back(jace::ClassPath("jace-runtime.jar"));
		jace::createVm(loader, list, false);

		for (int i = 0; i < 1000; ++i)
		{
			String s1("Hello World");
			cout << s1 << endl;

			String s2(std::string("Hello World"));
			cout << s2 << endl;

			String s3("Hello World");
			PrintStream out(System::out());
			out.println(s3);

			PrintWriter writer(java_new<PrintWriter>(System::out()));
			writer.println("Hello World");
			writer.flush();

			cout << i << endl;
		}
		return 0;
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
		return -2;
	}
}
