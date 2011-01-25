#ifndef ORG_JACE_JCLASS_IMPL_H
#define ORG_JACE_JCLASS_IMPL_H

#include "org/jace/os_dep.h"
#include "org/jace/namespace.h"
#include "org/jace/JClass.h"
#include "org/jace/JNIException.h"

BEGIN_NAMESPACE_1(boost)
	// End-users shouldn't have to include Boost header files
	class mutex;
END_NAMESPACE_1(boost)

#include <string>

BEGIN_NAMESPACE_2(org, jace)


/**
 * The implementation of the JClass interface.
 *
 * @author Toby Reyelts
 */
class JClassImpl: public ::org::jace::JClass
{
public:
	/**
	 * Creates a new JClassImpl with the given name, and 
	 * type name.
	 *
	 * @param name - The name of this class, suitable for use
	 * in a call to JNIEnv::FindClass.
	 *
	 * For example, "java/lang/Object"
	 *
	 * @param nameAsType The name of this class as a type, 
	 * suitable for use in a call to JNIEnv::GetMethodID.
	 *
	 * For example, "Ljava/lang/Object;"
	 */
	JACE_API JClassImpl(const std::string& name, const std::string& nameAsType);

	/**
	 * Creates a new JClassImpl with the given name.
	 *
	 * @param name - The name of the class, suitable for use
	 * in a call to JNIEnv::FindClass.
	 *
	 * For example, "java/lang/Object".
	 *
	 * ------------------------------------------------------
	 *
	 * The type name for the class is created by preprending
	 * "L" and appending ";" to name.
	 *
	 * For example,
	 *
	 *  JClassImpl("java/lang/String");
	 *
	 * is equivalent to
	 *
	 *  JClassImpl("java/lang/String", "Ljava/lang/String;");
	 */
	JACE_API JClassImpl(const std::string& name);

	/**
	 * Destroys this JClassImpl.
	 */
	JACE_API virtual ~JClassImpl() throw ();

	/**
	 * Returns the name of this class. suitable for use in a call to
	 * JNIEnv::FindClass.
	 *
	 * For example, "java/lang/Object".
	 */
	JACE_API virtual const std::string& getName() const;

	/**
	 * Returns the name of this class as a type, suitable for use
	 * in a call to JNIEnv::GetMethodID.
	 *
	 * For example, "Ljava/lang/Object;".
	 */
	JACE_API virtual const std::string& getNameAsType() const;

	/**
	 * Returns the JNI representation of this class.
	 */
	JACE_API virtual jclass getClass() const throw (::org::jace::JNIException);

private:
	/**
	 * Prevent copying.
	 */
	JClassImpl(JClassImpl&);
	/**
	 * Prevent assignment.
	 */
	JClassImpl& operator=(JClassImpl&);
	std::string mName;
	std::string mNameAsType;
	mutable jclass mClass;
	boost::mutex* mutex;
};


END_NAMESPACE_2(org, jace)

#endif
