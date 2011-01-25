#ifndef ORG_JACE_ELEMENT_PROXY_H
#define ORG_JACE_ELEMENT_PROXY_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/Jace.h"
#include "org/jace/ElementProxyHelper.h"
#include "org/jace/proxy/JObject.h"
#include "org/jace/JClass.h"
#include "org/jace/proxy/types/JBoolean.h"
#include "org/jace/proxy/types/JByte.h"
#include "org/jace/proxy/types/JChar.h"
#include "org/jace/proxy/types/JDouble.h"
#include "org/jace/proxy/types/JFloat.h"
#include "org/jace/proxy/types/JInt.h"
#include "org/jace/proxy/types/JLong.h"
#include "org/jace/proxy/types/JShort.h"

BEGIN_NAMESPACE_2(org, jace)

/**
 * An ElementProxy is a wrapper around a JArray element.
 *
 * An ElementProxy is responsible for pinning and depinning
 * its element as required.
 *
 * @author Toby Reyelts
 */
template <class ElementType> class ElementProxy: public virtual ::org::jace::proxy::JObject, public ElementType
{
public:
	/**
	 * Creates a new ElementProxy that belongs to the given array.
	 *
	 * This constructor shouldn't be called anymore, as it should be specialized
	 * by every proxy type. Every ElementProxy instance should allocate
	 * a new global ref to its parent array.
	 */
	ElementProxy(jarray array, jvalue element, int _index):
		ElementType(element), parent(array), index(_index)
	{
		// #error "ElementProxy was not properly specialized."

		std::cout << "ElementProxy was not properly specialized for " <<
						 ElementType::staticGetJavaJniClass().getName() << std::endl;
	}


	/**
	 * Copy constructor. This constructor should also never be called. It should be specialized away.
	 */
	ElementProxy(const ElementProxy& proxy):
		ElementType(0), parent(proxy.parent), index(proxy.index)
	{
		std::cout << "ElementProxy was not properly specialized for " <<
						 ElementType::staticGetJavaJniClass().getName() << std::endl;
	}


	/**
	 * If someone assigns to this array element, they're really assigning
	 * to an array, so we need to call SetObjectArrayElement.
	 */
	ElementType& operator=(const ElementType& element)
	{
		::org::jace::ElementProxyHelper::assign(element, index, parent);
		return *this;
	}


	/**
	 * If someone assigns to this array element, they're really assigning
	 * to an array, so we need to call SetObjectArrayElement.
	 */
	const ElementType& operator=(const ElementType& element) const
	{
		::org::jace::ElementProxyHelper::assign(element, index, parent);
		return *this;
	}


	~ElementProxy() throw()
	{
		try
		{
			JNIEnv* env = attach();
			deleteGlobalRef(env, parent);
		}
		catch (VirtualMachineShutdownError&)
		{
				// We tried to attach when the JVM has already been destroyed
		}
	}

private:
	jarray parent;
	int index;
};

END_NAMESPACE_2(org, jace)

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifdef PUT_TSDS_IN_HEADER
  #include "org/jace/ElementProxy.tsd"
#else
  #include "org/jace/ElementProxy.tsp"
#endif

#endif

