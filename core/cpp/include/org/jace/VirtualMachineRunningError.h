#ifndef ORG_JACE_VIRTUAL_MACHINE_RUNNING_ERROR_H
#define ORG_JACE_VIRTUAL_MACHINE_RUNNING_ERROR_H

#include "org/jace/JNIException.h"

BEGIN_NAMESPACE_2(org, jace)


/**
 * An operation has failed because the virtual machine is running.
 *
 * @author Gili Tzabari
 */
class VirtualMachineRunningError: public ::org::jace::JNIException
{
public:
	/**
	 * Creates a new VirtualMachineRunningError with the given message.
	 */
	VirtualMachineRunningError(const std::string& value) throw ():
			JNIException(value)
	{}
};

END_NAMESPACE_2(org, jace)

#endif // #ifndef ORG_JACE_VIRTUAL_MACHINE_RUNNING_ERROR_H

