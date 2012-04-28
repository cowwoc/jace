package org.jace.maven;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.proxy.AutoProxy;
import org.jace.proxy.ClassPath;
import org.jace.proxy.ProxyGenerator.AccessibilityType;

/**
 * Generates a C++ proxies.
 *
 * @goal generate-cpp-proxies
 * @phase generate-sources
 * @author Gili Tzabari
 */
public class GenerateCppProxiesMojo
	extends AbstractMojo
{
	/**
	 * The directory of the input header files.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private String[] inputHeaders;
	/**
	 * The directory of the input source files.
	 *
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("UWF_UNWRITTEN_FIELD")
	private String[] inputSources;
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
	 * The search path for Java classes referenced by C++ files.
	 *
	 * @parameter
	 * @required
	 */
	private File[] classPath;
	/**
	 * Indicates the method accessibility to expose.
	 *
	 * Acceptable values include: PUBLIC, PROTECTED, PACKAGE or PRIVATE.
	 * For example, a value of PROTECTED indicates that public or protected
	 * methods should be generated.
	 *
	 * @parameter default-value="PUBLIC"
	 */
	private String accessibility;
	/**
	 * Indicates if the proxy symbols should be exported (for generating DLLs/SOs).
	 *
	 * @parameter default-value="false"
	 */
	private boolean exportSymbols;
	/**
	 * Indicates whether classes should be exported even if they are not referenced by the input
	 * files.
	 *
	 * {@code true} if the minimum set of classes should be generated (superclass,
	 * interfaces and any classes used by the input files). {@code false} if all class dependencies
	 * (arguments, return values, and fields) should be exported. The latter is used to generate
	 * proxies for a Java library, where the set of input files are not known ahead of time.
	 *
	 * @parameter default-value="true"
	 */
	private boolean minimizeDependencies;
	/**
	 * A list of fully-qualified class names that must be exported.
	 *
	 * When generating C++ proxies for a Java library, there is no way of
	 * knowing which classes will be referenced by 3rd-party code. This feature
	 * enables developers to export C++ proxies for Java classes even if they are
	 * not referenced at the time the generator is run.
	 *
	 * @parameter
	 */
	private String[] forcedClasses = new String[0];

	@Override
	@SuppressWarnings("NP_UNWRITTEN_FIELD")
	public void execute()
		throws MojoExecutionException, MojoFailureException
	{
		AccessibilityType accessibilityType = AccessibilityType.valueOf(accessibility);

		Set<TypeName> extraDependencies = Sets.newHashSetWithExpectedSize(forcedClasses.length);
		for (String forcedClass: forcedClasses)
			extraDependencies.add(TypeNameFactory.fromIdentifier(forcedClass));

		List<File> inputHeaderFiles = Lists.newArrayList();
		for (String path: inputHeaders)
			inputHeaderFiles.add(new File(path));
		List<File> inputSourceFiles = Lists.newArrayList();
		for (String path: inputSources)
			inputSourceFiles.add(new File(path));
		final List<File> classPathList;
		if (classPath == null)
			classPathList = Collections.emptyList();
		else
			classPathList = Arrays.asList(classPath);
		AutoProxy.Builder autoProxy = new AutoProxy.Builder(inputHeaderFiles, inputSourceFiles,
			outputHeaders, outputSources, new ClassPath(classPathList)).accessibility(accessibilityType).
			minimizeDependencies(minimizeDependencies).exportSymbols(exportSymbols);
		for (TypeName dependency: extraDependencies)
			autoProxy.extraDependency(dependency);
		try
		{
			autoProxy.generateProxies();
		}
		catch (IOException e)
		{
			throw new MojoExecutionException("", e);
		}
		catch (ClassNotFoundException e)
		{
			throw new MojoExecutionException("", e);
		}
	}
}
