package org.jace.util;

public class ShutdownHook extends Thread
{
	private static ShutdownHook instance;
	private boolean registered;

	/**
	 * Creates and registers the shutdown hook.
	 */
	private ShutdownHook()
	{
		System.loadLibrary("jace");
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the singleton instance
	 */
	public static synchronized ShutdownHook getInstance()
	{
		if (instance == null)
			instance = new ShutdownHook();
		return instance;
	}

	/**
	 * The first time this method is invoked per JVM, it registers the shutdown hook. Any subsequent
	 * invocations do nothing. This method is invoked internally by Jace and is not meant for end-users.
	 */
	public synchronized void registerIfNecessary()
	{
		if (!registered)
		{
			Runtime.getRuntime().addShutdownHook(this);
			registered = true;
		}
	}

	@Override
	public synchronized void run()
	{
		registered = false;
		signalVMShutdown();
	}

	/**
	 * Signal to Jace that the JVM is shutting down.
	 */
	private static native void signalVMShutdown();
}
