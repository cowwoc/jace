#include "jace/proxy/JObject.h"

#include "jace/Jace.h"
#include "jace/JClassImpl.h"
#include "jace/JConstructor.h"
#include "jace/JMethod.h"
#include "jace/JArguments.h"
#include "jace/VirtualMachineShutdownError.h"

#include <iostream>
using std::cout;
using std::endl;

#include <exception>
using std::exception;

#include "jace/BoostWarningOff.h"
#include <boost/thread/mutex.hpp>
#include "jace/BoostWarningOn.h"

BEGIN_NAMESPACE_2(jace, proxy)

/**
 * Creates a new reference to an existing jvalue.
 */
JObject::JObject(jvalue value)
{
  setJavaJniValue(value);
}

/**
 * Creates a new reference to an existnig jobject.
 */
JObject::JObject(jobject object)
{
  setJavaJniObject(object);
}

/**
 * Creates a new null reference.
 *
 * All subclasses of JObject should provide this constructor
 * for their own subclasses.
 */
JObject::JObject()
{
  // By default, all objects start out with a null reference,
  // so, in case of an exception, destruction will not fail on
  // an uninitialized reference.
  jvalue newValue;
  newValue.l = 0;
  JValue::setJavaJniValue(newValue);
}

/**
 * Creates a new reference to an existing object.
 *
 * @param object the object
 */
JObject::JObject(const JObject& other)
{
	JValue::setJavaJniValue(static_cast<jvalue>(other));
}

/**
 * Destroys an object reference.
 */
JObject::~JObject() throw ()
{
	try
	{
		jobject ref = *this;

		// Save an attach and a delete if we are a null object.
		if (ref)
		{
			JNIEnv* env = attach();
			deleteGlobalRef(env, ref);
		}
	}
	catch (VirtualMachineShutdownError&)
	{
		// instance already deleted
	}
	catch (exception& e)
	{
		cout << "JObject::~JObject - Unable to delete the global ref." << endl;
		cout << e.what() << endl;
  }
}


/** 
 * Returns the underlying JNI jobject for this JObject.
 */
JObject::operator jobject()
{
	return static_cast<jvalue>(*this).l;
}


/** 
 * Returns the underlying JNI jobject for this JObject.
 *
 * Users of this method should be careful not to modify the
 * object through calls against the returned jobject.
 */
JObject::operator jobject() const
{
	return static_cast<jvalue>(*this).l;
}


/**
 * Returns true if this JObject represents a null java reference.
 *
 * If this method returns true, it is not safe to call any proxy
 * method on this JObject. Doing so will invoke undefined behavior.
 */
bool JObject::isNull() const
{
  return static_cast<jobject>(*this) == 0;
}

JObject& JObject::operator=(const JObject& object)
{
  setJavaJniObject(static_cast<jobject>(object));
  return *this;
}

/**
 * Sets the jobject for this JObject.
 *
 * This method is simply a convenience method for calling 
 * setValue(jvalue) with a jobject.
 */
void JObject::setJavaJniObject(jobject object) throw (JNIException)
{
  jvalue value;
  value.l = object;
  setJavaJniValue(value);
}


/**
 * This method sets the jobject for this JObject.
 *
 * @param object The jobject which represents this JObject.
 *
 * @throw JNIException if the JVM runs out of memory while 
 *   trying to create a new global reference.
 */
void JObject::setJavaJniValue(jvalue value) throw (JNIException)
{
  JNIEnv* env = attach();

  // If we have previously referenced another java object,
  // we release that reference here.
  jobject oldRef = *this;

  if (oldRef)
	{
    // printClass(oldRef);
    // print(oldRef);
    deleteGlobalRef(env, oldRef);
  }

  // If this is a null ref, we save a little time by not creating
  // a new global ref.
  if (!value.l)
	{
    // cout << "JObject::setJavaJniValue - Creating a NULL object." << endl;
    JValue::setJavaJniValue(value);
    return;
  }

  // cout << "JObject::setJavaJniValue - Creating a new reference to " << object << endl;
  jobject object = newGlobalRef(env, value.l);
  // cout << "JObject::setJavaJniValue - Created the reference." << endl;

  jvalue newValue;
  newValue.l = object;

  // cout << "JObject::setJavaJniValue - Calling JValue::setJavaJniValue..." << endl;
  JValue::setJavaJniValue(newValue);
  // cout << "JObject::setJavaJniValue - Called JValue::setJavaJniValue." << endl;
}

/**
 * Constructs a new instance of the given class
 * with the given arguments.
 *
 * @return the JNI jobject representing the new object. 
 * The returned reference is a local reference.
 *
 * @throws JNIException if a JNI error occurs while trying to locate the method.
 * @throws the corresponding C++ proxy exception, if a java exception
 *   is thrown during method execution.
 */
jobject JObject::newObject(const JClass& jClass, const JArguments& arguments)
{
  return JConstructor(jClass).invoke(arguments);
}

static boost::mutex javaClassMutex;
const JClass& JObject::staticGetJavaJniClass() throw (JNIException)
{
	static boost::shared_ptr<JClassImpl> result;
	boost::mutex::scoped_lock lock(javaClassMutex);
	if (result == 0)
		result = boost::shared_ptr<JClassImpl>(new JClassImpl("java/lang/Object"));
	return *result;
}

const JClass& JObject::getJavaJniClass() const throw (JNIException)
{
  return staticGetJavaJniClass();
}

END_NAMESPACE_2(jace, proxy)
