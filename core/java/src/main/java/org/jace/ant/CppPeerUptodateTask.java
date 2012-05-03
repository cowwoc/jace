package org.jace.ant;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jace.metaclass.JaceConstants;
import org.jace.metaclass.MetaClass;
import org.jace.metaclass.MetaClassFactory;
import org.jace.parser.ClassFile;

/**
 * {@code <CppPeerUptodate>} check for whether C++ peer files are up to date.
 *
 * Example: {@code <CppPeerUptodate inputFile="input.class" outputHeaders="output/include"
 * outputSources="output/source" property="cpp.peer.skip"/>}
 *
 * @author Gili Tzbari
 */
public class CppPeerUptodateTask extends Task
{
	private File inputFile;
	private File outputHeaders;
	private File outputSources;
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
	 * Sets the directory containing the output header files.
	 *
	 * @param outputHeaders the directory containing the output header files
	 */
	public void setOutputHeaders(File outputHeaders)
	{
		this.outputHeaders = outputHeaders;
	}

	/**
	 * Sets the directory containing the output source files.
	 *
	 * @param outputSources the directory containing the output source files
	 */
	public void setOutputSources(File outputSources)
	{
		this.outputSources = outputSources;
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
		if (outputHeaders == null)
			throw new BuildException("outputHeaders must be set", getLocation());
		if (outputSources == null)
			throw new BuildException("outputSources must be set", getLocation());
		if (property == null)
			throw new BuildException("property must be set", getLocation());

		ClassFile inputFileParser;
		try
		{
			inputFileParser = new ClassFile(inputFile);
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
		MetaClass metaClass = MetaClassFactory.getMetaClass(inputFileParser.getClassName());
		String path = metaClass.getFullyQualifiedName("/");
		File headerFile = new File(outputHeaders, JaceConstants.getPeerPackage().asPath() + path + ".h");
		File mappingsFile = new File(outputSources, JaceConstants.getPeerPackage().asPath() + path
																								+ "Mappings.cpp");
		File peerFile = new File(outputSources, JaceConstants.getPeerPackage().asPath() + path
																						+ "_peer.cpp");
		log("headerFile: " + headerFile + ", lastModified: " + headerFile.lastModified(),
			Project.MSG_VERBOSE);
		log("mappingsFile: " + mappingsFile + ", lastModified: " + mappingsFile.lastModified(),
			Project.MSG_VERBOSE);
		log("peerFile: " + peerFile + ", lastModified: " + peerFile.lastModified(), Project.MSG_VERBOSE);
		log("inputFile: " + inputFile + ", lastModified: " + inputFile.lastModified(),
			Project.MSG_VERBOSE);
		boolean isUptodate = headerFile.lastModified() > inputFile.lastModified() && mappingsFile.
			lastModified() > inputFile.lastModified() && peerFile.lastModified()
																									 > inputFile.lastModified();
		log(toString() + " returning " + isUptodate, Project.MSG_VERBOSE);
		if (isUptodate)
			getProject().setNewProperty(property, "true");
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[inputFile=" + inputFile + ", outputHeaders="
					 + outputHeaders + ", outputSources=" + outputSources + ", property=" + property + "]";
	}
}
