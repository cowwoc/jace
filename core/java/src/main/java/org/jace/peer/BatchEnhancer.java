package org.jace.peer;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * Enhances an entire set of peer classes in a single invocation.
 *
 * @author Toby Reyelts
 */
public class BatchEnhancer
{
	private final String libraries;
	private final String deallocationMethod;
	private final Collection<File> sources;
	private final boolean verbose;
	private static final String newLine = System.getProperty("line.separator");

	/**
	 * Creates a new BatchEnhancer.
	 *
	 * @param sources the peer files to enhance
	 * @param libraries the native libraries to load when the peers are initialized
	 * @param deallocationMethod the method to invoke in order to deallocate the native peer (may be null)
	 * @param verbose true if Java peers should output library names before loading them
	 */
	public BatchEnhancer(Collection<File> sources, String libraries, String deallocationMethod,
											 boolean verbose)
	{
		this.sources = sources;
		this.libraries = libraries;
		this.deallocationMethod = deallocationMethod;
		this.verbose = verbose;
	}

	/**
	 * Enhances the files.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	public void enhance() throws IOException
	{
		String[] tokens = libraries.split(",");
		for (File source: sources)
		{
			PeerEnhancer.Builder enhancer = new PeerEnhancer.Builder(source, source);
			for (String token: tokens)
				enhancer.library(token);
			enhancer.deallocationMethod(deallocationMethod).verbose(verbose).enhance();
		}
	}

	public static String getUsage()
	{
		String usage =
					 "Usage: BatchEnhancer " + newLine + "  <" + File.pathSeparator
					 + "-separated list of sources>" + newLine + "  <comma-separated list of libraries>"
					 + newLine + "  [options]" + newLine + newLine + "Where options can be:"
					 + "  -deallocator=<deallocation method>" + newLine
					 + "  -verbose (if Java peers should output library names before loading them)" + newLine;
		return usage;
	}

	/**
	 * Enhances a collection of Java peers.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.out.println(getUsage());
			return;
		}

		String[] sourceTokens = args[0].split(",");
		List<File> sources = Lists.newArrayListWithCapacity(sourceTokens.length);
		for (String token: sourceTokens)
			sources.add(new File(token));
		String libraries = args[1];

		String deallocationMethod = null;
		boolean verbose = false;
		for (int i = 2; i < args.length; ++i)
		{
			String option = args[i];

			if (option.equals("-deallocator"))
			{
				String[] tokens = args[i].split("=");
				if (tokens.length == 2)
				{
					deallocationMethod = tokens[1];
					continue;
				}
			}
			else if (option.equals("-verbose"))
			{
				verbose = true;
				continue;
			}
			System.out.println("Not an understood option: [" + option + "]");
			System.out.println();
			System.out.println(getUsage());
			return;
		}

		BatchEnhancer be = new BatchEnhancer(sources, libraries, deallocationMethod, verbose);
		try
		{
			be.enhance();
		}
		catch (IOException e)
		{
			LoggerFactory.getLogger(BatchEnhancer.class).error("", e);
		}
	}
}
