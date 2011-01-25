#ifndef ORG_JACE_JFACTORY_H
#define ORG_JACE_JFACTORY_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"

#include <jni.h>

#include "org/jace/BoostWarningOff.h"
#include <boost/shared_ptr.hpp>
#include "org/jace/BoostWarningOn.h"

BEGIN_NAMESPACE_3(org, jace, proxy)
class JValue;
END_NAMESPACE_3(org, jace, proxy)

BEGIN_NAMESPACE_2(org, jace)
class JClass;
END_NAMESPACE_2(org, jace)


BEGIN_NAMESPACE_2(org, jace)


/**
 * An interface for a factory that creates new instances
 * of a specific JValue subclass.
 *
 * @author Toby Reyelts
 */
class JFactory
{
public:
	/**
	 * Creates a new instance of the value type
	 * for this JFactory.
	 */
	JACE_API virtual boost::shared_ptr< ::org::jace::proxy::JValue > create(jvalue val) = 0;


	/**
	 * Creates a new instance of the value type for this JFactory
	 * and throws that instance.
	 *
	 * This method is equivalent to 
	 *
	 *   throw *(JFactory::create(aValue)).get();
	 *
	 * except that the return value's real type is preserved and 
	 * not sliced to a JValue upon being thrown.
	 */
	JACE_API virtual void throwInstance(jvalue val) = 0;

	/**
	 * Returns the class of which this factory
	 * creates instances.
	 */
	JACE_API virtual const ::org::jace::JClass& getClass() = 0;

	/**
	 * Destroys this JFactory.
	 */
	JACE_API virtual ~JFactory();
};

END_NAMESPACE_2(org, jace)

#endif // #ifndef ORG_JACE_JFACTORY_H
