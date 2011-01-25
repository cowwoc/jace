#include "org/jace/proxy/types/JVoid.h"

#include "org/jace/JClassImpl.h"

#include "org/jace/BoostWarningOff.h"
#include <boost/thread/mutex.hpp>
#include "org/jace/BoostWarningOn.h"

BEGIN_NAMESPACE_4(org, jace, proxy, types)


static boost::mutex javaClassMutex;
const JClass& JVoid::staticGetJavaJniClass() throw (JNIException)
{
	static boost::shared_ptr<JClassImpl> result;
	boost::mutex::scoped_lock lock(javaClassMutex);
	if (result == 0)
		result = boost::shared_ptr<JClassImpl>(new JClassImpl("void", "V"));
	return *result;
}

const JClass& JVoid::getJavaJniClass() const throw (JNIException)
{
  return JVoid::staticGetJavaJniClass();
}

END_NAMESPACE_4(org, jace, proxy, types)
