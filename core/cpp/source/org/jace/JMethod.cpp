#include "org/jace/JMethod.h"

#include "org/jace/JArguments.h"
using org::jace::JArguments;

#include "org/jace/proxy/JValue.h"
using org::jace::proxy::JValue;

#include <list>
using std::list;

#include <vector>
using std::vector;

BEGIN_NAMESPACE_2(org, jace)


/**
 * Transforms a JArguments to a vector of jvalue's.
 */
vector<jvalue> toVector(const JArguments& arguments)
{
  typedef list<const JValue*> ValueList;
  vector<jvalue> argsVector;
  ValueList argsList = arguments.asList();

  ValueList::iterator end = argsList.end();
  for (ValueList::iterator i = argsList.begin(); i != end; ++i)
    argsVector.push_back(static_cast<jvalue>(**i));

  return argsVector;
}

END_NAMESPACE_2(org, jace)

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifndef PUT_TSDS_IN_HEADER
  #include "org/jace/JMethod.tsd"
#endif
