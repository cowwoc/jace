package jace.proxy;

import jace.metaclass.ClassMetaClass;
import jace.metaclass.MetaClassFactory;
import jace.metaclass.TypeNameFactory;
import jace.parser.ClassFile;
import jace.proxy.ProxyGenerator.AccessibilityType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Batch generates C++ proxy classes. The BatchGenerator reads a Jar file,
 * and generates a Jace proxy for each and every Java class in the file.
 *
 * We should allow for all sorts of options, but unfortunately time is pressing,
 * so we'll only provide the basics for now.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class BatchGenerator
{
  private static final String newLine = System.getProperty("line.separator");
  private final File outputHeaders;
  private final File outputSources;
  private final AccessibilityType accessibility;

  /**
   * Creates a new BatchGenerator
   *
   * @param outputHeaders the directory to which the proxy header files should be written
   * @param outputSources the directory to which the proxy source files should be written
   * @param accessibility the class accessibility to expose
   */
  public BatchGenerator(File outputHeaders, File outputSources, AccessibilityType accessibility)
  {
    if (outputHeaders == null)
      throw new IllegalArgumentException("outputHeaders may not be null");
    if (outputSources == null)
      throw new IllegalArgumentException("outputSources may not be null");
    if (accessibility == null)
      throw new IllegalArgumentException("accessibility may not be null");
    if (!outputHeaders.isDirectory())
      throw new IllegalArgumentException("outputHeaders must be a directory");
    if (!outputSources.isDirectory())
      throw new IllegalArgumentException("outputSources must be a directory");
    this.outputHeaders = outputHeaders;
    this.outputSources = outputSources;
    this.accessibility = accessibility;
  }

  /**
   * Generates a proxy for every class in a jar file.
   *
   * @param file the jar file
   * @throws IOException if an I/O error occurs while generating proxies
   */
  private void generateFromJar(File jarFile) throws IOException
  {
    JarInputStream in = new JarInputStream(new FileInputStream(jarFile));

    while (true)
    {
      JarEntry entry = in.getNextJarEntry();
      if (entry == null)
        break;
      if (entry.isDirectory())
      {
        String dirName = entry.getName();
        dirName = "jace" + File.separator + "proxy" + File.separator + dirName;

        File headerDir = new File(outputHeaders + File.separator + dirName);
        headerDir.mkdirs();

        File sourceDir = new File(outputSources + File.separator + dirName);
        sourceDir.mkdirs();

        continue;
      }

      StringBuilder fullyQualifiedPath = new StringBuilder(entry.getName());
      if (!fullyQualifiedPath.toString().endsWith(".class"))
        continue;

      // Remove the ".class" extension
      fullyQualifiedPath.setLength(fullyQualifiedPath.length() - ".class".length());

      // Fix for bug 607754. Stealing code from AutoProxy to do this right now
      ClassMetaClass metaClass = (ClassMetaClass) MetaClassFactory.getMetaClass(
        TypeNameFactory.fromPath(fullyQualifiedPath.toString())).proxy();

      String classFileName = metaClass.getFileName();
      File targetHeaderFile = new File(outputHeaders, classFileName + ".h");
      File targetSourceFile = new File(outputSources, classFileName + ".cpp");
      if (targetSourceFile.exists() && targetHeaderFile.exists() &&
          jarFile.lastModified() <= targetSourceFile.lastModified() &&
          jarFile.lastModified() <= targetHeaderFile.lastModified())
      {
        // The source-file has not been modified since we last generated the
        // target source/header files.
        System.out.println(jarFile + " has not been modified, skipping...");
        continue;
      }

      ClassFile classFile = new ClassFile(in);
      ProxyGenerator.writeProxy(metaClass, classFile, accessibility, outputHeaders, outputSources);
    }

    in.close();
  }

  /**
   * Returns a String describing the usage of this tool.
   *
   * @return String describing the usage of this tool
   */
  private static String getUsage()
  {
    return "Usage: BatchGenerator <jar or zip file containing classes>" + newLine +
           "                      <destination directory for header files>" + newLine +
           "                      <destination directory for source files>" + newLine +
           "                     [ options ]" + newLine +
           "Where options can be:" + newLine +
           "  -public    : Generate public fields and methods." + newLine +
           "  -protected : Generate public, protected fields and methods." + newLine +
           "  -package : Generate public, protected, package-private fields and methods." + newLine +
           "  -private : Generate public, protected, package-private, private fields and methods." + newLine;
  }

  /**
   * Runs the BatchGenerator using the specified options.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args)
  {
    if (args.length < 3)
    {
      System.out.println(getUsage());
      return;
    }

    AccessibilityType accessibility = AccessibilityType.PUBLIC;
    for (int i = 3; i < args.length; ++i)
    {
      String option = args[i];

      if (option.equals("-public"))
        accessibility = AccessibilityType.PUBLIC;
      else if (option.equals("-protected"))
        accessibility = AccessibilityType.PROTECTED;
      else if (option.equals("-package"))
        accessibility = AccessibilityType.PACKAGE;
      else if (option.equals("-private"))
        accessibility = AccessibilityType.PRIVATE;
      else
      {
        System.out.println("Not an understood option: [" + option + "]");
        System.out.println();
        System.out.println(getUsage());
        return;
      }
    }
    try
    {
      new BatchGenerator(new File(args[1]), new File(args[2]), accessibility).generateFromJar(new File(args[0]));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
