#include "org/jace/proxy/types/JByte.h"

#include "org/jace/JClassImpl.h"

#include "org/jace/BoostWarningOff.h"
#include <boost/thread/mutex.hpp>
#include "org/jace/BoostWarningOn.h"

BEGIN_NAMESPACE_4(org, jace, proxy, types)

JByte::JByte(jvalue value)
{
  setJavaJniValue(value);
}

JByte::JByte(jbyte byte)
{
  jvalue value;
  value.b = byte;
  setJavaJniValue(value);
}

JByte::~JByte()
{}

JByte::operator jbyte() const
{ 
  return static_cast<jvalue>(*this).b;
}

bool JByte::operator==(const JByte& _byte) const
{
  return static_cast<jbyte>(_byte) == static_cast<jbyte>(*this);
}

bool JByte::operator!=(const JByte& _byte) const
{
  return !(*this == _byte);
}

bool JByte::operator==(jbyte val) const
{
  return val == static_cast<jbyte>(*this);
}

bool JByte::operator!=(jbyte val) const
{
  return !(*this == val);
}

static boost::mutex javaClassMutex;
const JClass& JByte::staticGetJavaJniClass() throw (JNIException)
{
	static boost::shared_ptr<JClassImpl> result;
	boost::mutex::scoped_lock lock(javaClassMutex);
	if (result == 0)
		result = boost::shared_ptr<JClassImpl>(new JClassImpl("byte", "B"));
	return *result;
}

const JClass& JByte::getJavaJniClass() const throw (JNIException)
{
  return JByte::staticGetJavaJniClass();
}


END_NAMESPACE_4(org, jace, proxy, types)
