package org.jace.examples;

import java.io.IOException;

/**
 * An example Java class that is used to demonstrate
 * Jace's Peer generation capabilities.
 *
 * @author Toby Reyelts
 *
 */
public class PeerExample
{
	private String server;
	private int port;

	static
	{
		// Jace enhances the static initializer so that it makes a call
		// to System.loadLibrary() to load the native library
		//
		// Jace will automatically create a static initializer if the class
		// doesn't already contain one.
	}

	public PeerExample(String server, int port)
	{
		this.server = server;
		this.port = port;

		// Jace enhances constructors so that a new C++ Peer
		// is created immediately before the constructor exits.
		// (allowing the constructor of the C++ Peer to access
		// initialized variables like server and port).
		//
		// If an exception is thrown from a constructor, the C++
		// Peer is not created. Users may also throw an exception
		// from the C++ Peer during construction, in which case
		// it will be automatically thrown from the Java constructor.
	}

	public PeerExample(String server)
	{
		this(server, 80);

		// Jace does not need to enhance constructors which chain
		// to other local constructors.
	}

	public void close()
	{
		// Jace enhances the developer specified resource deallocation
		// method so that it also releases the C++ Peer associated
		// with the class. Strictly speaking, developers need not specify
		// a method for enhancement so long as the class defines a finalizer.
		// Jace will automatically enhance the finalizer (if one exists)
		// to release the C++ peer. It is good practice, however,
		// not to rely on the garbage collector to clean up allocated resources.
		//
		// As with all of the enhanced methods, you can put any code you
		// want to in your method, and it won't affect or be affected by
		// Jace's enhancement.
	}

	@Override
	@SuppressWarnings(
	{
		"FinalizeDeclaration", "UseOfSystemOutOrSystemErr"
	})
	protected void finalize() throws Throwable
	{
		super.finalize();
		// Jace enhances the finalizer (if one exists) so that it ensures
		// the C++ Peer is deallocated.
		System.out.println("The PeerExample has been collected!");
	}

	// You (the developer) must implement the native methods for the C++ Peer.
	//
	// The header for the C++ Peer (which contains the member function
	// prototypes for the native methods) is written during Peer generation.
	// In this case, it is written to org/jace/peer/jace/examples/PeerExample.h.
	//
	// The developer can write his definitions of the native methods in
	// any source file he chooses. In this case, an example
	// implementation has already been written, and is located in
	// org/jace/peer/jace/examples/PeerExampleImpl.cpp.
	//
	public native String[] getResources(String[] resources) throws IOException;

	// The test driver.
	//
	// This class will connect to the http server resource you specify and print
	// out the returned contents.
	//
	// For example:
	//   PeerExample www.google.com /grphp?hl=en&ie=UTF-8&oe=UTF-8&q=
	//
	// will return the Google page containing a listing of news groups.
	//
	// You may also specify an optional port.
	//
	// When running this class, make sure to use release\peer_example1.jar
	// in your classpath, which contains the enhanced version of this class.
	//
	public static void main(String[] args) throws Exception
	{
		run(args);

		// Try to give some time to the garbage collector to come
		for (int i = 0; i < 5; ++i)
		{
			System.gc();
			Thread.sleep(200);
		}
	}

	@SuppressWarnings(
	{
		"CallToThreadDumpStack", "UseOfSystemOutOrSystemErr"
	})
	public static void run(String[] args)
	{
		if (args.length > 3 || args.length < 2)
		{
			System.out.println("Usage: PeerExample <server> <resource> [optional port]");
			return;
		}

		PeerExample example;

		String server = args[ 0];
		String resource = args[ 1];

		if (args.length == 3)
		{

			int port;

			try
			{
				port = Integer.parseInt(args[ 2]);
			}
			catch (NumberFormatException nfe)
			{
				nfe.printStackTrace();
				return;
			}
			example = new PeerExample(server, port);
		}
		else
		{
			example = new PeerExample(server);
		}

		try
		{
			String[] resources = example.getResources(new String[]
				{
					resource
				});

			System.out.println("Received reply: " + resources.length);

			for (int i = 0; i < resources.length; ++i)
			{
				System.out.println(resources[i]);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			example.close();
		}
	}
}
