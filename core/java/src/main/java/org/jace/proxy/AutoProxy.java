package org.jace.proxy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jace.metaclass.BooleanClass;
import org.jace.metaclass.ByteClass;
import org.jace.metaclass.CharClass;
import org.jace.metaclass.ClassMetaClass;
import org.jace.metaclass.DoubleClass;
import org.jace.metaclass.FloatClass;
import org.jace.metaclass.IntClass;
import org.jace.metaclass.LongClass;
import org.jace.metaclass.MetaClass;
import org.jace.metaclass.JaceConstants;
import org.jace.metaclass.ShortClass;
import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.metaclass.VoidClass;
import org.jace.parser.ClassFile;
import org.jace.proxy.ProxyGenerator.AccessibilityType;
import org.jace.proxy.ProxyGenerator.FilteringCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AutoProxy application generates the Jace proxies needed for a C++ application
 * to run correctly.
 *
 * It examines the #includes of C++ headers and source files looking for
 * #include "org/jace/proxy/xxx". When it finds one of these includes, it generates
 * the corresponding Jace C++ proxy class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class AutoProxy
{
	private final Collection<File> inputHeaders;
	private final Collection<File> inputSources;
	private final File outputHeaders;
	private final File outputSources;
	private final ClassPath classPath;
	private final AccessibilityType accessibility;
	private final boolean exportSymbols;
	/**
	 * The set of classes to process.
	 */
	private ClassSet proxies;
	private final Logger log = LoggerFactory.getLogger(AutoProxy.class);

	/**
	 * Creates a new AutoProxy.
	 *
	 * @param builder
	 *        An instance of <code>Builder</code>
	 */
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	private AutoProxy(Builder builder)
	{
		assert (builder != null);
		this.inputHeaders = new ArrayList<File>(builder.inputHeaders);
		this.inputSources = new ArrayList<File>(builder.inputSources);
		this.outputHeaders = builder.outputHeaders;
		this.outputSources = builder.outputSources;
		this.classPath = builder.classPath;
		this.accessibility = builder.accessibility;
		this.exportSymbols = builder.exportSymbols;
		this.proxies = new ClassSet(builder.classPath, builder.minimizeDependencies);
		if (builder.minimizeDependencies)
			proxies.addClasses(builder.extraDependencies);
	}

	/**
	 * Generates the Jace proxies for the specified C++ sources.
	 *
	 * @throws IOException if an error occurs while writing
	 * @throws ClassNotFoundException if a class file cannot be found while generating proxies
	 */
	private void run() throws IOException, ClassNotFoundException
	{
		FilenameFilter headerFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (new File(dir, name).isDirectory())
					return true;
				String ucaseName = name.toUpperCase();
				return ucaseName.endsWith(".H") || ucaseName.endsWith(".HPP") || ucaseName.endsWith(".INL");
			}
		};

		FilenameFilter sourceFilter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (new File(dir, name).isDirectory())
					return true;
				String ucaseName = name.toUpperCase();
				return ucaseName.endsWith(".C") || ucaseName.endsWith(".CPP") || ucaseName.endsWith(".CXX");
			}
		};

		// traverse the header and source C++ directories to generate the needed class library
		for (File directory: inputHeaders)
			traverse(directory, headerFilter);
		for (File directory: inputSources)
			traverse(directory, sourceFilter);

		// set up the dependency list for ProxyGenerator
		FilteringCollection dependencies = new FilteringCollection();

		// Include all of the dependent classes
		for (MetaClass metaClass: proxies.getClasses())
			dependencies.add(metaClass);

		// Include all primitives
		dependencies.add(new BooleanClass(false));
		dependencies.add(new ByteClass(false));
		dependencies.add(new CharClass(false));
		dependencies.add(new ShortClass(false));
		dependencies.add(new IntClass(false));
		dependencies.add(new LongClass(false));
		dependencies.add(new FloatClass(false));
		dependencies.add(new DoubleClass(false));
		dependencies.add(new VoidClass(false));

		// now generate all of the proxies
		for (MetaClass proxy: proxies.getClasses())
		{
			ClassMetaClass proxyClass = (ClassMetaClass) proxy;
			TypeName inputName = TypeNameFactory.fromPath(proxyClass.unProxy().getFullyQualifiedTrueName(
				"/"));
			TypeName outputName = TypeNameFactory.fromPath(proxyClass.getFullyQualifiedName("/"));

			File outputSourceFile = new File(outputSources, outputName.asPath() + ".cpp");
			File outputHeaderFile = new File(outputHeaders, outputName.asPath() + ".h");
			File inputParentFile = classPath.getFirstMatch(inputName);
			if (inputParentFile != null)
			{
				File inputFile;
				if (inputParentFile.isDirectory())
					inputFile = new File(inputParentFile, proxyClass.getTrueName() + ".class");
				else
					inputFile = inputParentFile;
				assert (inputFile.exists()): inputFile;
				if (!(inputFile.lastModified() > outputSourceFile.lastModified() || inputFile.lastModified() > outputHeaderFile.
							lastModified()))
				{
					// the input file has not been modified since we last generated the corresponding output files
					if (inputParentFile.isDirectory())
						log.info(inputFile + " has not been modified, skipping...");
					else
						log.info(inputParentFile + "!" + inputName.asPath()
										 + " has not been modified, skipping...");
					continue;
				}
			}

			if (log.isTraceEnabled())
				log.trace("Generating proxies for " + inputName + "...");
			InputStream input = classPath.openClass(inputName);
			ClassFile classFile = new ClassFile(input);
			new ProxyGenerator.Builder(classPath, classFile, dependencies).accessibility(accessibility).
				exportSymbols(exportSymbols).build().writeProxy(outputHeaders, outputSources);
			input.close();
		}

		/* I just realized that package import headers don't make any sense related to AutoProxy
		 * I'll just leave this in here for now in case something changes, but I find that unlikely.
		 */
		// Now update package import headers
		// PackageGen packageGen = PackageGen.newMetaClassInstance( destHeaderDir, classes );
		// packageGen.execute();
	}

	/**
	 * Traverses the specified File looking for Jace proxy includes.
	 * For every #include, it adds the fully-qualified class name
	 * to <code>proxies</code>.
	 *
	 * @param f may be a file or a directory. If it is a directory, all of the
	 * sub-directories and files in that directory are traversed.
	 * @param filter the filename filter
	 */
	private void traverse(File f, FilenameFilter filter)
	{
		log.debug(f.getAbsolutePath());
		File[] files = f.listFiles(filter);
		if (files != null)
		{
			for (File file: files)
				traverse(file, filter);
			return;
		}

		// look through the file to see if we can find any Jace proxy includes
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(f));

			while (true)
			{
				String line = reader.readLine();
				if (line == null)
					break;
				line = line.trim();
				String[] lineTokens = line.split("\\s");

				// make sure this line is an #include
				if (line.length() >= 2 && lineTokens[0].equals("#include") && isQuoted(lineTokens[1]))
				{
					String header = unQuote(lineTokens[1]);

					// Ensure that the separator character is "/"
					header = header.replace(File.separator, "/");

					// make sure the #include is for an actual Jace proxy
					String prefix = JaceConstants.getProxyPackage().asPath() + "/";
					if (!header.startsWith(prefix))
						continue;

					// Remove JaceConstants.getProxyPackage() from the header name
					String[] headerTokens = header.substring(prefix.length()).split("/");

					// Assume 30 characters per header path
					StringBuilder packageName = new StringBuilder((headerTokens.length - 1) * 30);
					String className = headerTokens[headerTokens.length - 1];
					for (int i = 0, size = headerTokens.length - 1; i < size; ++i)
					{
						String component = headerTokens[i];
						packageName.append(component);
						packageName.append("/");
					}

					// Ignore any of the built in Jace proxies
					String packageNameStr = packageName.toString();
					if (packageNameStr.startsWith("types/") || className.startsWith("JObject") || className.
						startsWith("JValue"))
					{
						continue;
					}

					// delete ".h" suffix
					if (className.toLowerCase().endsWith(".h"))
						className = className.substring(0, className.length() - ".h".length());
					try
					{
						proxies.addClass(packageNameStr, className);
					}
					catch (NoClassDefFoundError e)
					{
						throw new RuntimeException("Error parsing " + f, e);
					}
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(
				"Unexpected I/O exception while traversing the C++ source directories", e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					log.error("", e);
				}
			}
		}
	}

	/**
	 * Returns true if a String starts and ends with a quote character.
	 *
	 * @param str the string
	 * @return true if a String starts and ends with a quote character
	 */
	private boolean isQuoted(String str)
	{
		return str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"';
	}

	/**
	 * Removes quotes from a string.
	 *
	 * @param str the string
	 * @return the string without the quotes
	 */
	private String unQuote(String str)
	{
		return str.substring(1, str.length() - 1);
	}

	/**
	 * Returns a String describing the usage of this tool.
	 *
	 * @return String describing the usage of this tool
	 */
	public static String getUsage()
	{
		String newLine = System.getProperty("line.separator");

		return "Usage: AutoProxy " + newLine + "  <" + File.pathSeparator
					 + "-separated list of c++ header directories> "
					 + newLine + "  <" + File.pathSeparator + "-separated list of c++ source directories> "
					 + newLine
					 + "  <destination proxy header directory> " + newLine
					 + "  <destination proxy source directory> " + newLine
					 + "  <java classpath for proxies> " + newLine + "  [options]" + newLine + newLine
					 + "Where options can be:"
					 + newLine + "  -mindep " + newLine
					 + "  -extraDependencies=<comma-separated list of classes>" + newLine
					 + "  -exportsymbols" + newLine + "  -public    : Generate public fields and methods."
					 + newLine
					 + "  -protected : Generate public, protected fields and methods." + newLine
					 + "  -package : Generate public, protected, package-private fields and methods."
					 + newLine
					 + "  -private : Generate public, protected, package-private, private fields and methods.";
	}

	/**
	 * Returns the logger associated with the object.
	 *
	 * @return the logger associated with the object
	 */
	private Logger getLogger()
	{
		return log;
	}

	/**
	 * Generates C++ proxies based on input C++ files.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		if (args.length < 5 || args.length > 7)
		{
			System.out.println(getUsage());
			return;
		}

		Collection<File> inputHeaders = Lists.newArrayList();
		for (String path: args[0].split(Pattern.quote(File.pathSeparator)))
			inputHeaders.add(new File(path));
		Collection<File> inputSources = Lists.newArrayList();
		for (String path: args[1].split(Pattern.quote(File.pathSeparator)))
			inputSources.add(new File(path));

		File outputHeaders = new File(args[2]);
		File outputSources = new File(args[3]);
		String classPath = args[4];

		boolean minimizeDependencies = false;
		Set<TypeName> extraDependencies = Sets.newHashSetWithExpectedSize(args.length - 5);
		boolean exportSymbols = false;
		AccessibilityType accessibility = AccessibilityType.PUBLIC;
		for (int i = 5; i < args.length; ++i)
		{
			String option = args[i];

			if (option.equals("-mindep"))
				minimizeDependencies = true;
			else if (option.startsWith("-deplist"))
			{
				String[] equalTokens = option.split("=");
				String[] commaTokens = equalTokens[1].split(",");
				for (String token: commaTokens)
					extraDependencies.add(TypeNameFactory.fromIdentifier(token));
			}
			else if (option.equals("-exportsymbols"))
				exportSymbols = true;
			else if (option.equals("-public"))
				accessibility = AccessibilityType.PUBLIC;
			else if (option.equals("-protected"))
				accessibility = AccessibilityType.PROTECTED;
			else if (option.equals("-package"))
				accessibility = AccessibilityType.PACKAGE;
			else if (option.equals("-private"))
				accessibility = AccessibilityType.PRIVATE;
			else
			{
				System.out.println("Not an understood option: [" + option + "]");
				System.out.println();
				System.out.println(getUsage());
				return;
			}
		}

		AutoProxy.Builder autoProxy = new AutoProxy.Builder(inputHeaders, inputSources, outputHeaders,
			outputSources,
			new ClassPath(classPath)).accessibility(accessibility).minimizeDependencies(
			minimizeDependencies).exportSymbols(
			exportSymbols);
		for (TypeName dependency: extraDependencies)
			autoProxy.extraDependency(dependency);
		Logger log = LoggerFactory.getLogger(AutoProxy.class);
		log.info("Beginning Proxy generation.");
		try
		{
			autoProxy.generateProxies();
		}
		catch (IOException e)
		{
			log.error("", e);
		}
		catch (ClassNotFoundException e)
		{
			log.error("", e);
		}
		log.info("Finished Proxy generation.");
	}

	/**
	 * Creates a new AutoProxy.
	 */
	@SuppressWarnings("PublicInnerClass")
	public static final class Builder
	{
		private final Collection<File> inputHeaders;
		private final Collection<File> inputSources;
		private final File outputHeaders;
		private final File outputSources;
		private final ClassPath classPath;
		private AccessibilityType accessibility = AccessibilityType.PUBLIC;
		private boolean minimizeDependencies = true;
		private final Set<TypeName> extraDependencies = Sets.newHashSet();
		private boolean exportSymbols;

		/**
		 * Creates a new AutoProxy.
		 *
		 * @param inputHeaders
		 *        The directories to recursively search for header files
		 * @param inputSources
		 *        The directories to recursively search for source files
		 * @param outputHeaders
		 *        The directory to write new proxy header files to
		 * @param outputSources
		 *        The directory to write new proxy source files to
		 * @param classPath
		 *        The path to search for class files when resolving class dependencies
		 * @throws IllegalArgumentException
		 *         If <code>inputHeaders</code>, <code>inputSources</code>, <code>outputHeaders</code>,
		 *         <code>outputSources</code>, <code>extraDependencies</code> or <code>classPath</code> are null. Or if one of
		 *         the <code>inputHeaders</code>/<code>inputSources</code> elements is not adirectory or does not exist.
		 */
		public Builder(Collection<File> inputHeaders, Collection<File> inputSources, File outputHeaders,
									 File outputSources,
									 ClassPath classPath)
			throws IllegalArgumentException
		{
			if (inputHeaders == null)
				throw new IllegalArgumentException("inputHeaders may not be null");
			if (inputSources == null)
				throw new IllegalArgumentException("inputSources may not be null");
			if (outputHeaders == null)
				throw new IllegalArgumentException("outputHeaders may not be null");
			if (outputSources == null)
				throw new IllegalArgumentException("outputSources may not be null");
			if (extraDependencies == null)
				throw new IllegalArgumentException("extraDependencies may not be null");
			if (classPath == null)
				throw new IllegalArgumentException("classPath may not be null");
			if (!outputHeaders.isDirectory())
			{
				throw new IllegalArgumentException("outputHeaders refers to a non-existant directory: " + outputHeaders.
					getAbsolutePath());
			}
			if (!outputSources.isDirectory())
			{
				throw new IllegalArgumentException("outputSources refers to a non-existant directory: " + outputSources.
					getAbsolutePath());
			}
			this.inputHeaders = new ArrayList<File>(inputHeaders);
			this.inputSources = new ArrayList<File>(inputSources);
			for (File file: this.inputHeaders)
			{
				if (!file.isDirectory())
					throw new IllegalArgumentException("inputHeaders must be an existing directory: " + file.
						getAbsolutePath());
			}
			for (File file: this.inputSources)
			{
				if (!file.isDirectory())
					throw new IllegalArgumentException("inputSources must be an existing directory: " + file.
						getAbsolutePath());
			}
			this.outputHeaders = outputHeaders;
			this.outputSources = outputSources;
			this.classPath = classPath;
		}

		/**
		 * Indicates the method accessibility to expose.
		 *
		 * @param accessibility
		 *        The method accessibility to expose. The default is AccessibilityType.PUBLIC.
		 * @return the Builder
		 */
		public Builder accessibility(AccessibilityType accessibility)
		{
			this.accessibility = accessibility;
			return this;
		}

		/**
		 * Indicates whether classes should be exported even if they are not referenced by the input files.
		 *
		 * @param value
		 *        <code>true</code> if the minimum set of classes should be generated (superclass, interfaces and
		 *        any classes used by the input files). <code>false</code> if all class dependencies (arguments,
		 *        return values, and fields) should be exported. The latter is used to generate proxies for a Java library,
		 *        where the set of input files are not known ahead of time. The default is true.
		 * @return the Builder
		 */
		public Builder minimizeDependencies(boolean value)
		{
			this.minimizeDependencies = value;
			return this;
		}

		/**
		 * Specifies classes that should be exported in spite of the fact that they are not referenced by input files.
		 * This is mechanism is only enabled when <code>minimizeDependencies</code> is <code>true</code>.
		 *
		 * When generating proxies for a Java library there is no way of knowing what classes will be referenced ahead of
		 * time so you must use this mechanism to specify any classes above those that are automatically determined.
		 *
		 * @param dependency
		 *        A class that should be exported
		 * @return the Builder
		 */
		public Builder extraDependency(TypeName dependency)
		{
			extraDependencies.add(dependency);
			return this;
		}

		/**
		 * Indicates if proxy symbols should be exported (i.e. for use in DLLs)
		 *
		 * @param value
		 *        <code>true</code> if proxy symbols should be exported. The default is false.
		 * @return the Builder
		 */
		public Builder exportSymbols(boolean value)
		{
			this.exportSymbols = value;
			return this;
		}

		/**
		 * Generates the proxies.
		 *
		 * @throws IOException if an I/O exception occurs
		 * @throws ClassNotFoundException if a class file cannot be found while generating proxies
		 */
		public void generateProxies() throws IOException, ClassNotFoundException
		{
			new AutoProxy(this).run();
		}
	}
}
