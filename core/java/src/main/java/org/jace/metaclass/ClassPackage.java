package org.jace.metaclass;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the package for a class.
 *
 * For example, the ClassPackage for java.lang.Object would
 * represent the package "java.lang"
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassPackage
{
	private final List<String> components;

	/**
	 * Creates a new ClassPackage.
	 *
	 * @param packagePath the package components
	 */
	public ClassPackage(List<String> packagePath)
	{
		this.components = Lists.newArrayList(packagePath);
	}

	/**
	 * Returns the name of this package, separating individual packages
	 * by the path separator.
	 *
	 * @param separator - The separator to be used
	 *
	 * @param trailingSeparator - True if a trailing separator should
	 * be added.
	 *
	 * For example,
	 *
	 *   ClassPackage p = new ClassPackage( new String[] { "java", "lang" } );
	 *   package.toName( "/", true );    // returns "java/lang/"
	 *   package.toName( ", ", false );  // returns "java, lang"
	 * @return the name of this package
	 */
	public String toName(String separator, boolean trailingSeparator)
	{
		// Assume 16 characters per component
		StringBuilder result = new StringBuilder(components.size() * (16 + separator.length()));
		for (int i = 0, size = components.size(); i < size; ++i)
		{
			result.append(components.get(i));
			if ((i == size - 1) && !trailingSeparator)
				break;
			result.append(separator);
		}
		return result.toString();
	}

	@Override
	public String toString()
	{
		return toName(".", false);
	}

	/**
	 * Returns the path for the package.
	 *
	 * For example,
	 *
	 *  "java", "lang"
	 *
	 * @return the path for the package
	 */
	public List<String> getPath()
	{
		return Collections.unmodifiableList(components);
	}

	/**
	 * Indicates if the package contains a proxy.
	 *
	 * @return true if the package contains a proxy
	 */
	public boolean isProxied()
	{
		List<String> proxyComponents = JaceConstants.getProxyPackage().getComponents();
		if (components.size() < proxyComponents.size())
			return false;
		for (int i = 0, size = proxyComponents.size(); i < size; ++i)
		{
			if (!components.get(i).equals(proxyComponents.get(i)))
				return false;
		}
		return true;
	}

	@Override
	@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ClassPackage))
			return false;
		ClassPackage other = (ClassPackage) obj;
		if (other.components.size() != components.size())
			return false;

		for (int i = 0, size = components.size(); i < size; ++i)
		{
			if (!other.components.get(i).equals(components.get(i)))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}

	/**
	 * A test harness for this class.
	 *
	 * @param args the command-line arguments
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		ClassPackage p = new ClassPackage(Arrays.asList(args));

		System.out.println(p.toName(".", true));
		System.out.println("( " + p.toName(", ", false) + " )");
	}
}
