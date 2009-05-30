package jace.autoproxy;

import jace.metaclass.ArrayMetaClass;
import jace.metaclass.ClassMetaClass;
import jace.metaclass.MetaClass;
import jace.metaclass.MetaClassFactory;
import jace.metaclass.TypeName;
import jace.metaclass.TypeNameFactory;
import jace.parser.ClassFile;
import jace.proxygen.ProxyGenerator;
import jace.util.Util;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
  private final List<File> classPath;
  private final Set<MetaClass> classes = new HashSet<MetaClass>();
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
  public ClassSet(List<File> classPath, boolean minimizeDependencies)
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
    packageName = packageName.trim();
    int packageLength = packageName.length();

    if (packageLength > 0)
    {
      char endChar = packageName.charAt(packageLength - 1);

      if (packageName.indexOf('/') != -1 && endChar != '/')
      {
        // package contains a slash but not at the end
        packageName += '/';
      }
      else if (packageName.indexOf('.') != -1 && endChar != '.')
      {
        // package contains a dot, but not at the end
        packageName += '.';
      }
    }
    TypeName name = TypeNameFactory.fromPath(packageName + className.replace('.', '$'));
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
    addDependentClasses(classes, fullyQualifiedName);
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
    return classes;
  }

  /**
   * Generates the entire set of dependencies for a single class.
   *
   * @param classSet The set of dependent classes.
   * @param className the class name to generate the list of dependencies for
   *
   */
  private void addDependentClasses(Set<MetaClass> classSet, TypeName className)
  {
    ClassPath classSource = new ClassPath(classPath);
    InputStream classInput = classSource.openClass(className);
    ClassFile classFile = new ClassFile(classInput);

    try
    {
      classInput.close();
    }
    catch (IOException e)
    {
      log.warn("failed to close the class file", e);
    }

    // First, handle this class
    MetaClass thisMetaClass = MetaClassFactory.getMetaClass(className).proxy();
    handleClass(classSet, thisMetaClass);

    // handle the super class and interfaces next
    MetaClass superMetaClass = MetaClassFactory.getMetaClass(classFile.getSuperClassName()).proxy();
    handleClass(classSet, superMetaClass);

    for (TypeName i: classFile.getInterfaces())
    {
      MetaClass interfaceClass = MetaClassFactory.getMetaClass(i).proxy();
      handleClass(classSet, interfaceClass);
    }

    // If we are only working with the minimum dependencies, then we are done
    if (minimizeDependencies)
      return;

    // now handle all of the other classes
    ProxyGenerator pg = new ProxyGenerator(classFile);

    for (MetaClass metaClass: pg.getDependentClasses(true))
      handleClass(classSet, metaClass);

    for (MetaClass metaClass: pg.getDependentClasses(false))
      handleClass(classSet, metaClass);
  }

  private void handleClass(Set<MetaClass> classSet, MetaClass metaClass)
  {
    if (metaClass instanceof ArrayMetaClass)
    {
      ArrayMetaClass arrayMetaClass = (ArrayMetaClass) metaClass;
      metaClass = arrayMetaClass.getBaseClass();
    }

    if (metaClass.isPrimitive())
      return;

    if (!classSet.contains(metaClass))
    {
      classSet.add(metaClass);
      TypeName fullName = TypeNameFactory.fromPath(
        ((ClassMetaClass) metaClass.unProxy()).getFullyQualifiedTrueName("/"));
      if (log.isDebugEnabled())
      {
        log.debug("Adding " + fullName);
        printClassSet(classSet);
      }
      addDependentClasses(classSet, fullName);
    }
  }

  /**
   * Logs a set of meta-classes.
   *
   * @param metaClasses the meta-classes to log
   */
  private void printClassSet(Set<MetaClass> metaClasses)
  {
    for (MetaClass metaClass: metaClasses)
      log.debug(metaClass.getFullyQualifiedName("."));
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
   * Tests ClassSet.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args)
  {
    ClassSet library = new ClassSet(Util.parseClasspath(args[0]), Boolean.valueOf(args[2]).booleanValue());
    Set<MetaClass> classes = new HashSet<MetaClass>();
    library.addDependentClasses(classes, TypeNameFactory.fromIdentifier(args[1]));

    Logger log = library.getLogger();
    for (MetaClass metaClass: classes)
    {
      if (log.isDebugEnabled())
        log.debug(metaClass.getFullyQualifiedName("."));
    }
  }
}
