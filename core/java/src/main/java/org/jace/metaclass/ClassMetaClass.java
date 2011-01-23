package org.jace.metaclass;

import com.google.common.collect.Lists;
import org.jace.util.CKeyword;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents meta-data for class types.
 *
 * This helps prevent any sort of naming clashes caused by using other tools
 * that work with standard java class libraries.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassMetaClass implements MetaClass
{
	private final static String newLine = System.getProperty("line.separator");
	private String mName;
	private String mNewName;
	private ClassPackage mPackage;

	/**
	 * Creates a new ClassMetaClass.
	 *
	 * @param name the class name
	 * @param aPackage the class package
	 * @see MetaClassFactory#getMetaClass(org.jace.metaclass.TypeName)
	 */
	ClassMetaClass(String name, ClassPackage aPackage)
	{
		mName = name;
		mNewName = CKeyword.adjust(mName);
		mPackage = aPackage;
	}

	@Override
	public String getSimpleName()
	{
		return mNewName;
	}

	/**
	 * Returns the C++ file name that should be used for this MetaClass.
	 *
	 * For example, for java.lang.String, this would return String. For
	 * java.util.Map.EntrySet, this would return Map_EntrySet.
	 *
	 * @return the file name that should be used for this MetaClass
	 */
	public String getFileName()
	{
		return mName.replace('$', '_');
	}

	/**
	 * Returns the name of the class, without the keyword mangling that occurs to make it compatible with C++.
	 *
	 * @return the name of the class, without the keyword mangling that occurs to make it compatible with C++
	 */
	public String getTrueName()
	{
		return mName;
	}

	@Override
	public String getFullyQualifiedName(String separator)
	{
		return getPackage().toName(separator, true) + getSimpleName();
	}

	public String getFullyQualifiedTrueName(String separator)
	{
		return getPackage().toName(separator, true) + getTrueName();
	}

	/**
	 * Returns the ClassPackage for this MetaClass.
	 *
	 * @return the ClassPackage for this MetaClass
	 */
	@Override
	public ClassPackage getPackage()
	{
		return mPackage;
	}

	private String getGuardName()
	{
		StringBuilder guardName = new StringBuilder(mPackage.toName("_", true).toUpperCase());
		guardName.append(mNewName.toUpperCase()).append("_H");

		return guardName.toString();
	}

	@Override
	public String beginGuard()
	{
		String guardName = getGuardName();
		StringBuilder result = new StringBuilder("#ifndef ".length() + guardName.length() * 2 + "#define ".
			length());

		result.append("#ifndef ");
		result.append(guardName);
		result.append(newLine);
		result.append("#define ");
		result.append(guardName);
		return result.toString();
	}

	@Override
	public String endGuard()
	{
		StringBuilder guardName = new StringBuilder(mPackage.toName("_", true).toUpperCase());
		guardName.append(mNewName.toUpperCase()).append("_H");

		return "#endif // #ifndef " + guardName.toString();
	}

	@Override
	public String include()
	{
		StringBuilder include = new StringBuilder("#include \"");

		String packageName = mPackage.toName("/", true);
		String includeName = mName.replace('$', '_');
		include.append(packageName).append(includeName).append(".h\"");

		return include.toString();
	}

	@Override
	public String using()
	{
		StringBuilder using = new StringBuilder("using ");
		String packageName = mPackage.toName("::", true);
		using.append(packageName).append(mNewName).append(";");

		return using.toString();
	}

	@Override
	public String forwardDeclare()
	{
		StringBuilder forwardDeclaration = new StringBuilder("BEGIN_NAMESPACE_");
		String length = String.valueOf(mPackage.getPath().size());

		StringBuilder namespace = new StringBuilder("(");
		namespace.append(mPackage.toName(", ", false));
		namespace.append(")");

		forwardDeclaration.append(length).append(namespace.toString()).append(newLine);
		forwardDeclaration.append("class ").append(getSimpleName()).append(";").append(newLine);
		forwardDeclaration.append("END_NAMESPACE_").append(length).append(namespace);

		return forwardDeclaration.toString();
	}

	@Override
	public ClassMetaClass proxy()
	{
		if (mPackage.isProxied())
			throw new IllegalStateException("MetaClass is already a proxy: " + getFullyQualifiedName("."));
		List<String> jaceProxy = JaceConstants.getProxyPackage().getComponents();
		List<String> packagePath = mPackage.getPath();
		List<String> newPackage = Lists.newArrayListWithCapacity(jaceProxy.size() + packagePath.size());
		newPackage.addAll(jaceProxy);
		newPackage.addAll(packagePath);
		return new ClassMetaClass(mName, new ClassPackage(newPackage));
	}

	@Override
	public ClassMetaClass unProxy()
	{
		if (!mPackage.isProxied())
			throw new IllegalStateException("MetaClass is not a proxy: " + getFullyQualifiedName("."));
		List<String> path = mPackage.getPath();
		List<String> newPackage = path.subList(JaceConstants.getProxyPackage().getComponents().size(), path.
			size());
		assert (newPackage.size() != 1 || !newPackage.get(0).equals("types"));
		return new ClassMetaClass(mName, new ClassPackage(newPackage));
	}

	public ClassMetaClass toPeer()
	{
		List<String> result = new ArrayList<String>(JaceConstants.getPeerPackage().getComponents());
		for (String path: mPackage.getPath())
			result.add(path);
		return new ClassMetaClass(mName, new ClassPackage(result));
	}

	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	@Override
	public String getJniType()
	{
		String name = getFullyQualifiedTrueName(".");
		if (name.equals("java.lang.Class"))
			return "jclass";
		if (name.equals("java.lang.String"))
			return "jstring";
		if (name.equals("java.lang.Throwable"))
			return "jthrowable";
		return "jobject";
	}

	/**
	 * Compares this MetaClass to another.
	 *
	 * Two MetaClasses are equal if they have the same name and belong to the same package.
	 * @param o the object to compare to
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof ClassMetaClass))
			return false;
		ClassMetaClass other = (ClassMetaClass) o;
		return other.getSimpleName().equals(getSimpleName()) && other.getPackage().equals(getPackage());
	}

	@Override
	public int hashCode()
	{
		// Can't do a better hashCode than this, because we want to have proxied and deproxied classes compare equal
		return mName.hashCode();
	}

	@Override
	public String toString()
	{
		return getFullyQualifiedTrueName(".");
	}

	/**
	 * Tests ClassMetaClass.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		ClassMetaClass metaClass = (ClassMetaClass) MetaClassFactory.getMetaClass(TypeNameFactory.
			fromIdentifier(args[0])).
			proxy();
		MetaClass metaClass2 = MetaClassFactory.getMetaClass(TypeNameFactory.fromIdentifier(args[0])).
			proxy();
		MetaClass metaClass3 = MetaClassFactory.getMetaClass(TypeNameFactory.fromIdentifier("["
																																												+ args[0])).
			proxy();
		MetaClass metaClass4 = MetaClassFactory.getMetaClass(TypeNameFactory.fromIdentifier("[["
																																												+ args[0])).
			proxy();

		System.out.println(metaClass.equals(metaClass2));
		System.out.println(metaClass.equals(metaClass3));
		System.out.println(metaClass3.equals(metaClass));
		System.out.println(metaClass.equals(metaClass4));
		System.out.println(metaClass3.equals(metaClass4));
		System.out.println(metaClass4.equals(metaClass3));
		System.out.println(metaClass4.equals(metaClass));
	}
}
