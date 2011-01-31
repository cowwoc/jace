#include "jace/JArray.h"

#include "jace/BoostWarningOff.h"
#include <boost/thread/mutex.hpp>
#include "jace/BoostWarningOn.h"

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifndef PUT_TSDS_IN_HEADER
  #include "jace/JArray.tsd"
#endif

#include "jace/JNIException.h"
using jace::JNIException;

template <class ElementType> 
const jace::JClass& jace::JArray<ElementType>::staticGetJavaJniClass() throw (JNIException)
{
	static boost::shared_ptr<JClassImpl> result;
	boost::mutex::scoped_lock lock(javaClassMutex);
	if (result == 0)
	{
		const std::string nameAsType = "[" + ElementType::staticGetJavaJniClass().getNameAsType();

		// REFERENCE: http://java.sun.com/javase/6/docs/technotes/guides/jni/spec/types.html#wp16432
		const std::string name = nameAsType;

		result = boost::shared_ptr<JClassImpl>(new JClassImpl(name, nameAsType));
	}
	return *result;
}
