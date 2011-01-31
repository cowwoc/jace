package org.jace.proxy;

import com.google.common.collect.Sets;
import org.jace.metaclass.ArrayMetaClass;
import org.jace.metaclass.ClassMetaClass;
import org.jace.metaclass.MetaClass;
import org.jace.metaclass.MetaClassFactory;
import org.jace.metaclass.TypeName;
import org.jace.metaclass.TypeNameFactory;
import org.jace.parser.ClassFile;
import org.jace.proxy.ProxyGenerator.AcceptAll;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of classes.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassSet
{
	private final Logger log = LoggerFactory.getLogger(ClassSet.class);
	/**
	 * The classpath used to search for dependencies.
	 */
	private final ClassPath classPath;
	private final Set<MetaClass> classes = Sets.newHashSet();
	private boolean minimizeDependencies;

	/**
	 * Constructs a new ClassSet that looks for classes on the given classpath.
	 *
	 * @param classPath the classpath used to search for dependencies
	 * @param minimizeDependencies set to true to only generate proxies based on minimum dependency
	 * (superclass, interfaces and any classes used by the input files). Set to false to generate proxies for all class
	 * dependencies (arguments, return values, and fields). The latter is used to generate proxies for a Java library when
	 * the input files are unknown.
	 */
	public ClassSet(ClassPath classPath, boolean minimizeDependencies)
	{
		this.classPath = classPath;
		this.minimizeDependencies = minimizeDependencies;
	}

	/**
	 * Adds a class and its dependencies to the library.
	 *
	 * In the case that minimizeDependencies=true, only the superclasses are added and
	 * not the other dependee classes.
	 *
	 * @param packageName the package name
	 * @param className the class name
	 */
	public void addClass(String packageName, String className)
	{
		// rewrite the separated name as a single name
		StringBuilder result = new StringBuilder(packageName.length() + 1 + className.length());
		result.append(packageName.trim());
		int length = result.length();

		// Add a separator if necessary
		if (length > 0 && result.charAt(length - 1) != '/')
			result.append('/');
		result.append(className.replace('_', '$'));
		TypeName name = TypeNameFactory.fromPath(result.toString());
		addClass(name);
	}

	/**
	 * Adds a class and its dependencies to the library.
	 *
	 * In the case that minimizeDependencies=true, only the superclasses are added and
	 * not the other dependee classes.
	 *
	 * @param fullyQualifiedName the fully qualified class name
	 */
	public void addClass(TypeName fullyQualifiedName)
	{
		// add in with dependent classes
		addDependentClasses(classes, MetaClassFactory.getMetaClass(fullyQualifiedName));
	}

	/**
	 * Calls addClass( String ) for each member in the collection.
	 *
	 * @param classes the classes
	 */
	public void addClasses(Collection<TypeName> classes)
	{
		for (TypeName fullyQualifiedName: classes)
			addClass(fullyQualifiedName);
	}

	/**
	 * Returns a {@code Set<MetaClass>} of the classes in this library.
	 *
	 * @return {@code Set<MetaClass>} of the classes in this library
	 */
	public Set<MetaClass> getClasses()
	{
		return Collections.unmodifiableSet(classes);
	}

	/**
	 * Generates the entire set of dependencies for a single class.
	 *
	 * @param result the set to add to
	 * @param clazz the (unproxied) class to generate the list of dependencies for
	 *
	 */
	private void addDependentClasses(Set<MetaClass> result, MetaClass clazz)
	{
		// First, handle this class
		ClassMetaClass plainClass = toClassMetaClass(clazz);
		if (plainClass == null)
			return;
		ClassMetaClass proxy = plainClass.proxy();
		if (result.contains(proxy))
		{
			// We've already seen this class
			return;
		}
		result.add(proxy);
		if (log.isDebugEnabled())
			log.debug("Adding " + plainClass + " to " + result);

		// handle the super class and interfaces next
		try
		{
			TypeName typeName = TypeNameFactory.fromPath(plainClass.getFullyQualifiedTrueName("/"));
			InputStream classInput = classPath.openClass(typeName);
			ClassFile classFile = new ClassFile(classInput);
			classInput.close();

			if (classFile.getSuperClassName() != null)
			{
				MetaClass superMetaClass = MetaClassFactory.getMetaClass(classFile.getSuperClassName());
				addDependentClasses(result, superMetaClass);
			}

			for (TypeName i: classFile.getInterfaces())
			{
				MetaClass interfaceClass = MetaClassFactory.getMetaClass(i);
				addDependentClasses(result, interfaceClass);
			}

			if (classFile.getClassName().asIdentifier().equals("java.lang.String"))
			{
				// String(std::string&) and String(std::wstring&) constructors make use of java.lang.Integer
				addDependentClasses(result, MetaClassFactory.getMetaClass(TypeNameFactory.fromIdentifier(
					"java.lang.Integer")));
			}
			// If we are only working with the minimum dependencies, then we are done
			if (minimizeDependencies)
				return;

			// now handle all of the other classes
			ProxyGenerator generator = new ProxyGenerator.Builder(classPath, classFile, new AcceptAll()).
				build();

			for (MetaClass metaClass: generator.getDependentClasses(true))
				addDependentClasses(result, metaClass.unProxy());

			for (MetaClass metaClass: generator.getDependentClasses(false))
				addDependentClasses(result, metaClass.unProxy());
		}
		catch (IOException e)
		{
			log.warn("", e);
		}
		catch (ClassNotFoundException e)
		{
			log.warn("", e);
		}
	}

	/**
	 * Returns the underlying ClassMetaClass.
	 *
	 * @param metaClass the class
	 * @return null if the underlying type was a primitive
	 */
	private ClassMetaClass toClassMetaClass(MetaClass metaClass)
	{
		MetaClass result;
		if (metaClass instanceof ArrayMetaClass)
			result = ((ArrayMetaClass) metaClass).getInnermostElementType();
		else
			result = metaClass;
		if (result.isPrimitive())
			return null;
		return (ClassMetaClass) result;
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

	@Override
	public String toString()
	{
		return getClass().getName() + "[classPath=" + classPath + ", classes=" + classes
					 + ", minimizeDependencies=" + minimizeDependencies + "]";
	}

	/**
	 * Tests ClassSet.
	 *
	 * @param args the command-line argument
	 */
	public static void main(String[] args)
	{
		ClassSet library = new ClassSet(new ClassPath(args[0]), Boolean.valueOf(args[2]).booleanValue());
		Set<MetaClass> classes = Sets.newHashSet();
		library.addDependentClasses(classes, MetaClassFactory.getMetaClass(TypeNameFactory.
			fromIdentifier(args[1])));

		Logger log = library.getLogger();
		for (MetaClass metaClass: classes)
		{
			if (log.isDebugEnabled())
				log.debug(metaClass.getFullyQualifiedName("."));
		}
	}
}
