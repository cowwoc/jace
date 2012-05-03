package org.jace.ant;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jace.parser.ClassFile;
import org.jace.peer.PeerGenerator;

/**
 * Generates C++ peers for Java classes.
 *
 * Example: {@code <GenerateCppPeer file="peer.class" outputHeaders="output/include" outputSources="output/source"
 * userDefinedMembers="false"/>}
 *
 * @author Gili Tzbari
 */
public class GenerateCppPeerTask extends Task
{
	private File file;
	private File outputHeaders;
	private File outputSources;
	private boolean userDefinedMembers;

	/**
	 * Sets the filename of the Java peer.
	 *
	 * @param file the Java peer
	 */
	public void setFile(File file)
	{
		this.file = file;
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
	 * Indicates if &lt;peer_class_name%gt;_user.h should be generated.
	 *
	 * @param userDefinedMembers true if &lt;peer_class_name%gt;_user.h should be generated
	 */
	public void setUserDefinedMembers(boolean userDefinedMembers)
	{
		this.userDefinedMembers = userDefinedMembers;
	}

	@Override
	public void execute() throws BuildException
	{
		if (file == null)
			throw new BuildException("file must be set", getLocation());
		if (outputHeaders == null)
			throw new BuildException("outputHeaders must be set", getLocation());
		if (outputSources == null)
			throw new BuildException("outputSources must be set", getLocation());
		log(toString(), Project.MSG_DEBUG);
		try
		{
			PeerGenerator peerGenerator = new PeerGenerator(new ClassFile(file), file.lastModified(),
				outputHeaders, outputSources, userDefinedMembers);
			peerGenerator.generate();
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[file=" + file + ", outputHeader=" + outputHeaders
					 + ", outputSources=" + outputSources + ", userDefinedMembers=" + userDefinedMembers + "]";
	}
}
