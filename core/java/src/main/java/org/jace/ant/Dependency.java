package org.jace.ant;

/**
 * Specifies a class that requires a C++ proxy. Meant to be used as a child of the &lt;GenerateProxies&gt; tag.
 *
 * @author Gili Tzbari
 */
public class Dependency
{
	private String name;

	/**
	 * Sets the class name.
	 *
	 * @param name the class name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the class name.
	 *
	 * @return the class name
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "Dependency[name=" + getName() + "]";
	}
}
