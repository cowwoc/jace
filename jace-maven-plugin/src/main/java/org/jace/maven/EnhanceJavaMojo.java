package org.jace.maven;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.jace.peer.PeerEnhancer;

/**
 * Transforms the bytecode of Java class files to load/unload C++ peers.
 *
 * @goal enhance-java
 * @phase process-classes
 * @author Gili Tzabari
 */
public class EnhanceJavaMojo
	extends AbstractMojo
{
	/**
	 * The input file.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private File inputFile;
	/**
	 * The output file.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private File outputFile;
	/**
	 * The Java method used to deallocate the Java peer.
	 *
	 * @parameter
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private String deallocationMethod;
	/**
	 * Indicates if Java peers should output library names before loading them.
	 *
	 * @parameter default-value="false"
	 */
	private boolean verbose;
	/**
	 * The native libraries to load before initializing the Java peer.
	 *
	 * @parameter
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private String[] libraries = new String[0];

	@Override
	@SuppressWarnings("NP_UNWRITTEN_FIELD")
	public void execute()
		throws MojoExecutionException, MojoFailureException
	{
		Log log = getLog();
		if (log.isInfoEnabled())
			log.info("Enhancing " + inputFile + " -> " + outputFile);
		try
		{
			PeerEnhancer.Builder enhancer = new PeerEnhancer.Builder(inputFile, outputFile).verbose(
				verbose);
			if (deallocationMethod != null)
				enhancer.deallocationMethod(deallocationMethod);
			for (String library: libraries)
				enhancer.library(library);
			enhancer.enhance();

			if (inputFile.getCanonicalFile().equals(outputFile))
			{
				// back up the enhanced file for JavaPeerUptodateTask
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(outputFile));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile
																																								 + ".enhanced"));
				byte[] buffer = new byte[10 * 1024];
				while (true)
				{
					int rc = in.read(buffer);
					if (rc == -1)
						break;
					out.write(buffer, 0, rc);
				}
				in.close();
				out.close();
				long lastModified = inputFile.lastModified();
				if (lastModified != 0)
					outputFile.setLastModified(lastModified);
			}
		}
		catch (IOException e)
		{
			throw new MojoExecutionException("", e);
		}
	}
}
