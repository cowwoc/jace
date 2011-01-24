package org.jace.ant;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jace.proxy.AutoProxy;
import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.proxy.ClassPath;
import org.jace.proxy.ProxyGenerator.AccessibilityType;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Path;

/**
 * Generates C++ proxies for Java classes.
 *
 * Example:
 * &lt;GenerateCppProxies inputHeaders="input/include" inputSources="input/source"
 * outputHeaders="output/include" outputSources="output/source" exportSymbols="false"
 * classpath="rt.jar" accessibility="PUBLIC"&gt;
 *   &lt;classpath&gt;
 *     &lt;pathelement location="classes"/&gt;
 *   &lt;/classpath&gt;
 *   &lt;dependency name="java.lang.String"/&gt;
 *   &lt;inputHeaders dir="input"&gt;
 *     &lt;include name="include1"&gt;
 *     &lt;include name="include2"&gt;
 *   &lt;/inputHeaders&gt;
 *   &lt;inputSources dir="input"&gt;
 *     &lt;include name="source1"&gt;
 *     &lt;include name="source2"&gt;
 *   &lt;/inputSources&gt;
 * &lt;/GenerateCppProxies&gt;
 *
 * @author Gili Tzbari
 */
public class GenerateCppProxiesTask extends Task
{
	private final Collection<File> inputHeaders = Lists.newArrayList();
	private boolean inputHeadersSpecified = false;
	private final Collection<File> inputSources = Lists.newArrayList();
	private boolean inputSourcesSpecified = false;
	private File outputHeaders;
	private File outputSources;
	private Path classpath = new Path(getProject());
	private AccessibilityType accessibility = AccessibilityType.PUBLIC;
	/**
	 * A list of fully-qualified class names that must be exported.
	 *
	 * When generating C++ proxies for a Java library, there is no way of
	 * knowing which classes will be referenced by 3rd-party code. This feature
	 * enables developers to export C++ proxies for Java classes even if they are
	 * not referenced at the time the generator is run.
	 */
	private Set<Dependency> dependencies = Sets.newHashSet();
	/**
	 * True if proxies should export their symbols (for DLLs/SOs).
	 */
	private boolean exportSymbols;

	/**
	 * Sets the directory containing the input header files.
	 *
	 * @param inputHeaders the directory containing the input header files
	 */
	public void setInputHeaders(String inputHeaders)
	{
		inputHeadersSpecified = true;
		this.inputHeaders.clear();
		for (String path: inputHeaders.split(File.pathSeparator))
			this.inputHeaders.add(new File(path));
	}

	/**
	 * Sets the directory containing the input source files.
	 *
	 * @param inputSources the directory containing the input source files
	 */
	public void setInputSources(String inputSources)
	{
		inputSourcesSpecified = true;
		this.inputSources.clear();
		for (String path: inputSources.split(File.pathSeparator))
			this.inputSources.add(new File(path));
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
	 * Indicates the method accessibility to expose.
	 *
	 * For example, a value of PROTECTED indicates that public or protected
	 * methods should be generated.
	 * 
	 * @param accessibility PUBLIC, PROTECTED, PACKAGE or PRIVATE
	 * @throws IllegalArgumentException if an unknown accessibility type is specified
	 */
	public void setAccessibility(String accessibility) throws IllegalArgumentException
	{
		this.accessibility = AccessibilityType.valueOf(accessibility.toUpperCase());
	}

	/**
	 * Indicates if the proxy symbols should be exported (for generating DLLs/SOs).
	 *
	 * @param exportSymbols true if the proxy symbols should be exported
	 */
	public void setExportSymbols(boolean exportSymbols)
	{
		this.exportSymbols = exportSymbols;
	}

	/**
	 * Sets the Java classpath.
	 *
	 * @param classpath the Java classpath
	 */
	public void setClasspath(Path classpath)
	{
		this.classpath = classpath;
	}

	@Override
	public void execute() throws BuildException
	{
		if (!inputHeadersSpecified && !inputSourcesSpecified)
			throw new BuildException("Must specify at least one inputHeaders or inputSources directory",
				getLocation());
		if (inputHeadersSpecified && inputHeaders.isEmpty())
			throw new BuildException("inputHeaders refers to non-existant directories", getLocation());
		if (inputSourcesSpecified && inputSources.isEmpty())
			throw new BuildException("inputSources refers to non-existant directories", getLocation());
		if (outputHeaders == null)
			throw new BuildException("outputHeaders must be set", getLocation());
		if (outputSources == null)
			throw new BuildException("outputSources must be set", getLocation());
		log(toString(), Project.MSG_DEBUG);
		Set<TypeName> extraDependencies = Sets.newHashSetWithExpectedSize(dependencies.size());
		for (Dependency dependency: dependencies)
			extraDependencies.add(TypeNameFactory.fromIdentifier(dependency.getName()));
		AutoProxy.Builder autoProxy = new AutoProxy.Builder(inputHeaders, inputSources, outputHeaders,
			outputSources, new ClassPath(classpath.toString())).accessibility(accessibility).
			minimizeDependencies(true).exportSymbols(exportSymbols);
		for (TypeName dependency: extraDependencies)
			autoProxy.extraDependency(dependency);
		try
		{
			autoProxy.generateProxies();
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new BuildException(e);
		}
	}

	/**
	 * Adds a class that must have a C++ proxy generated.
	 *
	 * @param dependency a class that must have a C++ proxy generated
	 */
	public void addConfiguredDependency(Dependency dependency)
	{
		if (dependency.getName() == null)
			throw new BuildException("name must be set", getLocation());
		dependencies.add(dependency);
	}

	/**
	 * Adds to the Java classpath.
	 *
	 * @param classpath the Java classpath
	 */
	public void addConfiguredClasspath(Path classpath)
	{
		this.classpath.add(classpath);
	}

	/**
	 * Adds input header directories.
	 *
	 * @param headers the input header directories
	 */
	public void addConfiguredInputHeaders(DirSet headers)
	{
		inputHeadersSpecified = true;
		DirectoryScanner scanner = headers.getDirectoryScanner(getProject());
		scanner.scan();
		for (String directory: scanner.getIncludedDirectories())
			this.inputHeaders.add(new File(scanner.getBasedir(), directory));
	}

	/**
	 * Adds input source directories.
	 *
	 * @param sources the input source directories
	 */
	public void addConfiguredInputSources(DirSet sources)
	{
		inputSourcesSpecified = true;
		DirectoryScanner scanner = sources.getDirectoryScanner(getProject());
		scanner.scan();
		for (String directory: scanner.getIncludedDirectories())
			this.inputSources.add(new File(scanner.getBasedir(), directory));
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[inputHeaders=" + inputHeaders + ", inputSources="
					 + inputSources
					 + ", outputHeader=" + outputHeaders + ", outputSources=" + outputSources
					 + ", exportSymbols=" + exportSymbols
					 + "]";
	}
}
