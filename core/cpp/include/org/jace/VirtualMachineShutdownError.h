#ifndef ORG_JACE_VIRTUAL_MACHINE_SHUTDOWN_ERROR_H
#define ORG_JACE_VIRTUAL_MACHINE_SHUTDOWN_ERROR_H

#include "org/jace/JNIException.h"

BEGIN_NAMESPACE_2(org, jace)


/**
 * An operation has failed because the virtual machine has shut down.
 *
 * @author Toby Reyelts
 */
class VirtualMachineShutdownError: public ::org::jace::JNIException
{
public:
	/**
	 * Creates a new VirtualMachineShutdownError with the given message.
	 */
	VirtualMachineShutdownError(const std::string& value) throw ():
			JNIException(value)
	{}
};

END_NAMESPACE_2(org, jace)

#endif // #ifndef ORG_JACE_VIRTUAL_MACHINE_SHUTDOWN_ERROR_H

