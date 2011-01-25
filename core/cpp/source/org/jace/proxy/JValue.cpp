
#include "org/jace/proxy/JValue.h"

BEGIN_NAMESPACE_3(org, jace, proxy)


/**
 * Constructs a new JValue.
 */
JValue::JValue()
{
  mValue.l = 0;
}


/** 
 * Returns the underlying JNI jvalue for this JValue.
 */
JValue::operator jvalue()
{
  return mValue;
}

/** 
 * Returns the underlying JNI jvalue for this JValue.
 */
JValue::operator jvalue() const
{
  return mValue;
}


void JValue::setJavaJniValue(jvalue value) throw (JNIException)
{
  mValue = value;
}

JValue::~JValue()
{
}

END_NAMESPACE_3(org, jace, proxy)
