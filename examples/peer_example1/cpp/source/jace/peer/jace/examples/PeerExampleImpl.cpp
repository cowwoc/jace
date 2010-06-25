

/**
 * Jace generates the class definition (PeerExample.h), JNI mappings (PeerExampleMappings.cpp),
 * and proxy methods and fields (PeerExample.cpp) for a class.
 *
 * The developer, however, has to provide the implementation of the native methods.
 *
 * This file contains the implementation of the native methods for
 * the Java class, jace.examples.PeerExample.
 *
 */
#include "jace/peer/jace/examples/PeerExample.h"
using jace::peer::jace::examples::PeerExample;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

#include "jace/proxy/java/io/IOException.h"
using jace::proxy::java::io::IOException;

#include "jace/proxy/java/net/URL.h"
using jace::proxy::java::net::URL;

#include "jace/proxy/java/io/InputStream.h"
using jace::proxy::java::io::InputStream;

#include "jace/proxy/java/io/BufferedReader.h"
using jace::proxy::java::io::BufferedReader;

#include "jace/proxy/java/io/InputStreamReader.h"
using jace::proxy::java::io::InputStreamReader;

#include <iostream>
using std::cout;
using std::endl;

/**
 * getResources
 *
 */
JArray<String> PeerExample::getResources( JArray<String> urls ) {

  try {

    String server_ = server();
    int length = urls.length();
    int port_ = port();

    JArray<String> returnValues( length );

    int i;

    for ( i = 0; i < length; ++i ) {

      String resource( urls[ i ] );
      URL url( "http", server_, port_, resource );
      InputStreamReader inputReader( url.openStream() );
      BufferedReader reader( inputReader );

      String buffer = "";
      String line;

      while ( true ) {
        line = reader.readLine();
        if ( line.isNull() ) {
          break;
        }

        buffer = buffer + line;
      }

      returnValues[ i ] = buffer;
    }

    return returnValues;
  }
  catch ( IOException& ioe ) {
    cout << "Caught the following exception, but letting it pass to the VM: " << std::endl;
    cout << ioe << std::endl;
    throw;
  }

  return JArray<String>( 0 );
}


void PeerExample::destroy() {
  cout << "C++ PeerExample destroyed." << endl;
}
