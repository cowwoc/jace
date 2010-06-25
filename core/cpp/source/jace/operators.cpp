
#include "jace/operators.h"

BEGIN_NAMESPACE(jace)

JACE_API const NoOp NO_OP;
JACE_API const JavaMethod JAVA_METHOD;

END_NAMESPACE(jace)

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifndef PUT_TSDS_IN_HEADER
  #include "jace/operators.tsd"
#endif
