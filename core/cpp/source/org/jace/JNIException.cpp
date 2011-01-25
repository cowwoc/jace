
#include "org/jace/JNIException.h"

#include <string>
using std::string;
using std::wstring;


BEGIN_NAMESPACE_2(org, jace)

JNIException::JNIException(const string& value) throw (): 
  BaseException(value)
{}

JNIException::JNIException(const wstring& value) throw (): 
  BaseException(value)
{}

JNIException::JNIException(const JNIException& rhs) throw (): 
  BaseException(rhs)
{}


JNIException& JNIException::operator=(const JNIException& rhs) throw ()
{
  if (this == &rhs)
		return *this;

  ((BaseException&) *this) = (BaseException&) rhs;
  return *this;
}


END_NAMESPACE_2(org, jace)
