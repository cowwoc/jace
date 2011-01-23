package org.jace.metaclass;

import java.util.Collections;

/**
 * Represents meta-data for primitive types.
 *
 * @author Gili Tzabari
 */
public abstract class PrimitiveMetaClass implements MetaClass
{
	private final static String newLine = System.getProperty("line.separator");
	private final boolean isProxy;

	/**
	 * Creates a new instance.
	 *
	 * @param isProxy true if the meta-data represents a proxy
	 * @return the new instance
	 */
	protected abstract MetaClass newInstance(boolean isProxy);

	/**
	 * Creates a new PrimitiveMetaClass.
	 *
	 * @param isProxy true if the meta-data represents a proxy
	 */
	protected PrimitiveMetaClass(boolean isProxy)
	{
		this.isProxy = isProxy;
	}

	@Override
	public String getFullyQualifiedName(String separator)
	{
		if (!isProxy)
			return getSimpleName();
		return JaceConstants.getProxyPackage().asPath().replace("/", separator) + separator + "types"
					 + separator + getSimpleName();
	}

	@Override
	public ClassPackage getPackage()
	{
		return new ClassPackage(Collections.<String>emptyList());
	}

	@Override
	public String beginGuard()
	{
		return "JACE_TYPES_" + getSimpleName().toUpperCase() + "_H";
	}

	@Override
	public String endGuard()
	{
		return "// #ifndef JACE_TYPES_" + getSimpleName().toUpperCase() + "_H";
	}

	@Override
	public String include()
	{
		return "#include \"" + JaceConstants.getProxyPackage().asPath() + "/types/" + getSimpleName()
					 + ".h\"";
	}

	@Override
	public String using()
	{
		return "using jace::proxy::types::" + getSimpleName() + ";";
	}

	@Override
	public String forwardDeclare()
	{
		return "BEGIN_NAMESPACE_3(jace, proxy, types)" + newLine + "class " + getSimpleName() + ";"
					 + newLine + "END_NAMESPACE_3(jace, proxy, types)";
	}

	@Override
	public MetaClass proxy()
	{
		if (isProxy)
			throw new IllegalStateException(getClass().getSimpleName() + " is already a proxy: " + getFullyQualifiedName(
				"."));
		return newInstance(true);
	}

	@Override
	public MetaClass unProxy()
	{
		if (!isProxy)
			throw new IllegalStateException(getClass().getSimpleName() + " is not a proxy: " + getFullyQualifiedName(
				"."));
		return newInstance(false);
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return getClass().getName() + "(proxy=" + isProxy + ")";
	}
}
