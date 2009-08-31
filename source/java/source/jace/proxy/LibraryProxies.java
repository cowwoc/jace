package jace.proxy;

import jace.metaclass.BooleanClass;
import jace.metaclass.ByteClass;
import jace.metaclass.CharClass;
import jace.metaclass.ClassMetaClass;
import jace.metaclass.DoubleClass;
import jace.metaclass.FloatClass;
import jace.metaclass.IntClass;
import jace.metaclass.LongClass;
import jace.metaclass.MetaClass;
import jace.metaclass.ShortClass;
import jace.metaclass.TypeName;
import jace.metaclass.TypeNameFactory;
import jace.metaclass.VoidClass;
import jace.parser.ClassFile;
import jace.proxy.ProxyGenerator.AccessibilityType;
import jace.proxy.ProxyGenerator.FilteringCollection;
import jace.util.Util;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates proxies for a Java library.
 *
 * All library classes must be exported because there is no way of knowing what subset will be used by the end-user.
 *
 * @author Gili Tzabari
 */
public class LibraryProxies
{
  private final Logger log = LoggerFactory.getLogger(LibraryProxies.class);
  private final List<File> classPath;
  private final File outputHeaders;
  private final File outputSources;
  private final ClassSet library;

  /**
   * Creates a new LibraryProxies.
   *
   * @param classPath the path to search for class files
   * @param classes the fully-qualified names of the library classes
   * @param outputHeaders the output directory for proxy header files
   * @param outputSources the output directory for proxy source files
   * @throws IllegalArgumentException if classPath, classes, outputHeaders, outputSources are null
   * or if outputHeaders, outputSources are not directories
   */
  public LibraryProxies(List<File> classPath, Set<TypeName> classes, File outputHeaders, File outputSources)
    throws IllegalArgumentException
  {
    if (classPath == null)
      throw new IllegalArgumentException("classPath may not be null");
    if (classes == null)
      throw new IllegalArgumentException("classes may not be null");
    if (outputHeaders == null)
      throw new IllegalArgumentException("outputHeaders may not be null");
    if (outputSources == null)
      throw new IllegalArgumentException("outputSources may not be null");
    if (!outputHeaders.isDirectory())
      throw new IllegalArgumentException("outputHeaders must be a directory: " + outputHeaders);
    if (!outputSources.isDirectory())
      throw new IllegalArgumentException("outputSources must be a directory: " + outputSources);
    this.classPath = classPath;
    this.outputHeaders = outputHeaders;
    this.outputSources = outputSources;
    this.library = new ClassSet(classPath, true);
    for (TypeName dependency: classes)
      library.addClass(dependency);
  }

  /**
   * Generates the proxies.
   *
   * @throws IOException if an error occurs while writing the proxy files
   */
  public void generateProxies() throws IOException
  {
    Set<MetaClass> classes = library.getClasses();
    ClassPath source = new ClassPath(classPath);

    // generate the dependency list
    FilteringCollection dependencies = new FilteringCollection();

    // include all primitives
    dependencies.add(new BooleanClass(false));
    dependencies.add(new ByteClass(false));
    dependencies.add(new CharClass(false));
    dependencies.add(new ShortClass(false));
    dependencies.add(new IntClass(false));
    dependencies.add(new LongClass(false));
    dependencies.add(new FloatClass(false));
    dependencies.add(new DoubleClass(false));
    dependencies.add(new VoidClass(false));

    // include all of the dependent classes
    for (MetaClass metaClass: classes)
      dependencies.add(metaClass);

    // now generate all of the proxies
    for (MetaClass clazz: classes)
    {
      ClassMetaClass metaClass = (ClassMetaClass) clazz;
      String sourceName = metaClass.unProxy().getFullyQualifiedTrueName("/");

      String classFileName = metaClass.getFileName();
      File sourceFile = new File(sourceName);
      File targetHeaderFile = new File(outputHeaders, classFileName + ".h");
      File targetSourceFile = new File(outputSources, classFileName + ".cpp");
      if (!(sourceFile.lastModified() > targetSourceFile.lastModified() ||
            sourceFile.lastModified() > targetHeaderFile.lastModified()))
      {
        log.warn(sourceFile + " has not been modified, skipping...");
        continue;
      }

      InputStream input = source.openClass(TypeNameFactory.fromPath(sourceName));
      ClassFile classFile = new ClassFile(input);
      new ProxyGenerator.Builder(classFile, dependencies).accessibility(AccessibilityType.PROTECTED).build().
        writeProxy(outputHeaders, outputSources);
      input.close();
    }
  }

  /**
   * Returns the fully-qualified class names of all classes within a directory.
   *
   * @param directory the directory
   * @return the fully-qualified class names of all classes within a directory
   * @throws IllegalArgumentException if directory is not a directory
   */
  public static Set<TypeName> getClasses(File directory) throws IllegalArgumentException
  {
    if (!directory.isDirectory())
      throw new IllegalArgumentException(directory + " is not a directory");
    File[] classes = directory.listFiles(new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        return name.endsWith(".class");
      }
    });
    Set<TypeName> result = new HashSet<TypeName>();
    for (File clazz: classes)
    {
      ClassFile file = new ClassFile(clazz);
      if (file.getClassName().asIdentifier().isEmpty())
      {
        // skip anonymous classes
        continue;
      }
      result.add(file.getClassName());
    }
    return result;
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
   * Returns a String describing the usage of this tool.
   *
   * @return String describing the usage of this tool
   */
  public static String getUsage()
  {
    String newLine = System.getProperty("line.separator");

    return "Usage: LibraryProxies " + newLine +
           "  <java classpath> " + newLine +
           "  <comma-separated list of class directories> " + newLine +
           "  <output directory for proxy header files> " + newLine +
           "  <output directory for proxy source files>";
  }

  /**
   * Main entry point.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args)
  {
    if (args.length < 4 || args.length > 4)
    {
      System.out.println(getUsage());
      return;
    }

    String classPath = args[0];
    String[] directories = args[1].split(",");
    File outputHeaders = new File(args[2]);
    File outputSources = new File(args[3]);

    Set<TypeName> classes = new HashSet<TypeName>();
    for (String directory: directories)
      classes.addAll(LibraryProxies.getClasses(new File(directory)));
    LibraryProxies proxies = new LibraryProxies(Util.parseClasspath(classPath), classes, outputHeaders,
      outputSources);
    Logger log = proxies.getLogger();
    log.info("Beginning Proxy generation.");
    try
    {
      proxies.generateProxies();
    }
    catch (IOException e)
    {
      log.error("closing class file", e);
    }
    log.info("Finished Proxy generation.");
  }
}
