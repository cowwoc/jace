package org.jace.maven;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.MojoFailureException;
import org.jace.parser.ClassFile;
import org.jace.peer.PeerGenerator;

/**
 * Generates a C++ peer.
 *
 * @goal generate-cpp-peer
 * @phase generate-sources
 * @author Gili Tzabari
 */
public class GenerateCppPeerMojo
	extends AbstractMojo
{
	/**
	 * The Java file to generate a peer for.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private File classFile;
	/**
	 * The directory of the output header files.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private File outputHeaders;
	/**
	 * The directory of the output source files.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private File outputSources;
	/**
	 * Indicates if <code>{peer_class_name}</code>_user.h should be generated.
	 *
	 * @parameter default-value="false"
	 */
	private boolean userDefinedMembers;

	@Override
	@SuppressWarnings("NP_UNWRITTEN_FIELD")
	public void execute()
		throws MojoExecutionException, MojoFailureException
	{
		PeerGenerator peerGenerator = new PeerGenerator(new ClassFile(classFile),
			classFile.lastModified(), outputHeaders, outputSources, userDefinedMembers);
		try
		{
			peerGenerator.generate();
		}
		catch (IOException e)
		{
			throw new MojoExecutionException("", e);
		}
	}
}
