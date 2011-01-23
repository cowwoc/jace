package org.jace.ant;

/**
 * Specifies libraries used by class peers. Meant to be used as a child of
 * the &lt;EnhancePeer&gt; tag.
 *
 * @author Gili Tzbari
 */
public class Library
{
	private String name;

	/**
	 * Sets the library name.
	 * 
	 * @param name the library name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the library name.
	 * 
	 * @return the library name
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "Library[name=" + getName() + "]";
	}
}
