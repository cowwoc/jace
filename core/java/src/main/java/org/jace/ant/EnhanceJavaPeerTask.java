package org.jace.ant;

import com.google.common.collect.Lists;
import org.jace.peer.PeerEnhancer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhances the specified peer class. Meant to be used in coordination with
 * &lt;PeerUptodate&gt; task.
 *
 * Example:
 * &lt;EnhanceJavaPeer inputFile="input.class" outputFile="output.class" deallocationMethod="dispose" verbose="false"&gt;
 *   &lt;library name="browser"/&gt;
 *   &lt;library name="tray"/&gt;
 * &lt;/EnhanceJavaPeer&gt;
 *
 * @author Gili Tzbari
 */
public class EnhanceJavaPeerTask extends Task
{
	private final Logger log = LoggerFactory.getLogger(EnhanceJavaPeerTask.class);
	private File inputFile;
	private File outputFile;
	/**
	 * Native libraries to be loaded when the peer is initialized.
	 */
	private final List<Library> libraries = Lists.newArrayList();
	/**
	 * An optional method used to deallocate the peer from the Java-end.
	 */
	private String deallocationMethod;
	private boolean verbose;

	/**
	 * Sets the class file to enhance.
	 *
	 * @param inputFile the class file to enhance
	 */
	public void setInputFile(File inputFile)
	{
		this.inputFile = inputFile;
	}

	/**
	 * Sets the enhanced output file.
	 *
	 * @param outputFile the enhanced output file
	 */
	public void setOutputFile(File outputFile)
	{
		this.outputFile = outputFile;
	}

	/**
	 * Sets the name of the method used to deallocate the Java peer.
	 *
	 * @param deallocationMethod the name of the method used to deallocate the Java peer
	 */
	public void setDeallocationMethod(String deallocationMethod)
	{
		this.deallocationMethod = deallocationMethod;
	}

	/**
	 * Indicates if Java peers should output library names before loading them.
	 *
	 * @param verbose true if Java peers should output library names before loading them
	 */
	public void setVerbose(boolean verbose)
	{
		this.verbose = verbose;
	}

	@Override
	public void execute() throws BuildException
	{
		if (inputFile == null)
			throw new BuildException("inputFile must be set", getLocation());
		if (outputFile == null)
			throw new BuildException("outputFile must be set", getLocation());
		log(toString(), Project.MSG_DEBUG);
		if (log.isInfoEnabled())
			log.info("Enhancing " + inputFile + " -> " + outputFile);
		try
		{
			PeerEnhancer.Builder enhancer = new PeerEnhancer.Builder(inputFile, outputFile).verbose(
				verbose);
			if (deallocationMethod != null)
				enhancer.deallocationMethod(deallocationMethod);
			for (Library library: libraries)
				enhancer.library(library.getName());
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
			throw new BuildException(e);
		}
	}

	/**
	 * Adds a native library to be loaded when the peer is initialized.
	 *
	 * @param library a native library
	 */
	public void addConfiguredLibrary(Library library)
	{
		if (library.getName() == null)
			throw new BuildException("name must be set", getLocation());
		libraries.add(library);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[inputFile=" + inputFile + ", outputFile=" + outputFile
					 + ", libraries=" + libraries + ", deallocationMethod=" + deallocationMethod + "]";
	}
}
