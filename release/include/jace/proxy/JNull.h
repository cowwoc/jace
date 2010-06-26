
#ifndef JACE_JNULL_H
#define JACE_JNULL_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

BEGIN_NAMESPACE_2(jace, proxy)


/**
 * A null reference.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
class JNull
{
public:
	/**
	 * Constructs a new JNull.
	 */
	JACE_API JNull();
};


END_NAMESPACE_2(jace, proxy)

#endif
