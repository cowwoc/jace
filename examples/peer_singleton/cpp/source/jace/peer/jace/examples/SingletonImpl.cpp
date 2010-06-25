
#include "jace/peer/jace/examples/Singleton.h"
using ::jace::peer::jace::examples::Singleton;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

using jace::JArray;

#include <iostream>
using std::cout;
using std::endl;


/**
 * The implementation of the Singleton.
 *
 * As you can see, there's not much exciting going on in here.
 *
 * One notable thing is that we do override Peer::initialize(), which gets called when 
 * the Singleton gets constructed.
 *
 * To do that, we have to set user_defined_members=true for the PeerGenerator, and
 * we have to create the Singleton_user.h header that contains the prototype for 
 * intialize. Of course, we can stick anything we want to into Singleton_user.h.
 *
 */

void ::jace::peer::jace::examples::Singleton::initialize() {
  cout << "Initializing the peer" << endl;
  cout << "Changing count from " << count() << " to 0." << endl;
  count() = 0;
  cout << "Count is now " << count() << endl;
}


/**
 * print
 *
 */
void Singleton::print() {
  cout << "Empty print()" << endl;
  count() = count() + 1;
}

/**
 * print
 *
 */
void Singleton::print( ::jace::proxy::java::lang::String p0 ) {
  cout << p0 << endl;
  count() = count() + 1;
}

/**
 * print
 *
 */
void Singleton::print( ::jace::proxy::java::lang::String p0, ::jace::proxy::java::lang::String p1 ) {
  cout << p0 << " " << p1 << endl;
  count() = count() + 1;
}

/**
 * print
 *
 */
void Singleton::print( ::jace::JArray< ::jace::proxy::java::lang::String > p0 ) {
 
  typedef JArray<String> str_array;

  for ( str_array::Iterator it = p0.begin(); it != p0.end(); ++it ) {
    cout << *it << " ";
  }

  cout << endl;

  count() = count() + 1;
}


/**
 * printHelloWorld
 *
 */
void Singleton::printHelloWorld() {
  cout << "Hello World!" << endl;
  count() = count() + 1;
}

/**
 * print
 *
 */
void Singleton::print( ::jace::proxy::types::JInt p0 ) {
  cout << p0 << endl;
  count() = count() + 1;
}

/**
 * getCount
 *
 */
::jace::proxy::types::JInt Singleton::getCount() {
  count() = count() + 1;
  return count();
}

/**
 * setString
 *
 */
void Singleton::setString( ::jace::proxy::java::lang::String p0 ) {
  currentString() = p0;
}

/**
 * getString
 *
 */
::jace::proxy::java::lang::String Singleton::getString() {
  return currentString();
}
