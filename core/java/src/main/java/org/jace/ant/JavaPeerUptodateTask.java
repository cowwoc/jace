package org.jace.ant;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * &lt;JavaPeerUptodate&gt; check for whether a Java peer file is up to date. Must be used
 * in coordination with the &lt;EnhanceJavaPeer&gt; task.
 *
 * Example:
 * &lt;JavaPeerUptodate inputFile="input.class" outputFile="output.class" property="java.peer.skip"/&gt;
 *
 * @author Gili Tzbari
 */
public class JavaPeerUptodateTask extends Task
{
	private File inputFile;
	private File outputFile;
	private String property;

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
	 * Sets the name of the property to set if the peer class needs to be enhanced.
	 *
	 * @param property the name of the property to set if the peer class needs to be enhanced
	 */
	public void setProperty(String property)
	{
		this.property = property;
	}

	@Override
	public void execute() throws BuildException
	{
		if (inputFile == null)
			throw new BuildException("inputFile must be set", getLocation());
		if (outputFile == null)
			throw new BuildException("outputFile must be set", getLocation());
		if (property == null)
			throw new BuildException("property must be set", getLocation());

		File enhancedFile;
		try
		{
			if (inputFile.getCanonicalFile().equals(outputFile))
				enhancedFile = new File(outputFile.getPath() + ".enhanced");
			else
				enhancedFile = outputFile;
			log("inputFile.lastModified()=" + inputFile.lastModified() + ", enhancedFile.lastModified()=" + enhancedFile.
				lastModified() + ", enhancedFile=" + enhancedFile, Project.MSG_VERBOSE);
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
		boolean isUptodate = enhancedFile.lastModified() > inputFile.lastModified();
		log(toString() + " returning " + isUptodate, Project.MSG_VERBOSE);
		if (isUptodate)
			getProject().setNewProperty(property, "true");
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[inputFile=" + inputFile + ", outputFile=" + outputFile
					 + ", property=" + property + "]";
	}
}
