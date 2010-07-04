package jace.proxy;

import jace.metaclass.ArrayMetaClass;
import jace.metaclass.ClassMetaClass;
import jace.metaclass.MetaClass;
import jace.metaclass.MetaClassFactory;
import jace.metaclass.TypeName;
import jace.metaclass.TypeNameFactory;
import jace.parser.ClassFile;
import jace.util.WildcardFileFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A source for class files.
 *
 * @author Toby Reyelts
 */
public class ClassPath
{
  private final List<File> elements;
  private final Logger log = LoggerFactory.getLogger(ClassPath.class);

  /**
   * Creates a new ClassPath.
   *
   * @param elements the paths to be used to search for classes
   * @throws IllegalArgumentException if elements is null
   */
  public ClassPath(List<File> elements)
  {
    if (elements == null)
      throw new IllegalArgumentException("classPath may not be null");
    this.elements = elements;
  }

  /**
   * Creates a new ClassPath.
   *
   * @param text the String representation of the classpath
   * @throws IllegalArgumentException if text is null
   */
  public ClassPath(String text)
  {
    if (text == null)
      throw new IllegalArgumentException("text may not be null");
    this.elements = new ArrayList<File>();
    List<String> classPathArray = Arrays.asList(text.split(File.pathSeparator));
    for (String path: classPathArray)
    {
      if (path.contains("*") || path.contains("?"))
      {
        path = path.replace(File.separator, "/");
        int index = path.lastIndexOf("/");
        String directory;
        String filename;
        if (index == -1)
        {
          directory = ".";
          filename = path;
        }
        else
        {
          directory = path.substring(0, index);
          filename = path.substring(index + "/".length());
        }
        if (directory.contains("*") || directory.contains("?"))
        {
          throw new IllegalArgumentException("classpath directories may not contain wildcards");
        }
        WildcardFileFilter filter = new WildcardFileFilter(filename);
        File[] files = new File(directory).listFiles(filter);
        for (File file: files)
          elements.add(file);
      }
      else
        elements.add(new File(path));
    }
  }

  /**
   * Returns the first match for <code>name</code> in the class path. The class may be packed within
   * a zip or jar file.
   *
   * @param name the class name
   * @return the directory, zip or jar file containing the class file. Null if the class was not found.
   * @throws IOException if an I/O error occurs while reading a zip or jar file
   */
  public File getFirstMatch(TypeName name) throws IOException
  {
    for (File path: elements)
    {
      if (!path.exists())
        continue;
      log.trace("Checking " + path);

      // if the file is a directory, search for the .class file in the appropriate subfolder
      if (path.isDirectory())
      {
        MetaClass metaClass = MetaClassFactory.getMetaClass(name);
        String directory = metaClass.getPackage().toName("/", false);
        File subDirectory = new File(path.getAbsolutePath(), directory);

        log.trace("Looking for directory " + subDirectory);
        if (subDirectory.exists())
        {
          String fileName = ((ClassMetaClass) metaClass).getTrueName() + ".class";
          File classFile = new File(subDirectory, fileName);

          log.trace("Looking for file " + classFile);
          if (classFile.exists())
            return classFile.getParentFile();
        }
        continue;
      }

      // Search inside zip or jar files
      String fileName = path.getName();
      if (!path.getName().endsWith(".zip") && !path.getName().endsWith(".jar"))
        continue;
      log.trace("Checking compressed file " + fileName);
      ZipFile zipFile = new ZipFile(path);
      MetaClass metaClass = MetaClassFactory.getMetaClass(name);
      if (metaClass instanceof ArrayMetaClass)
        metaClass = ((ArrayMetaClass) metaClass).getInnermostElementType();
      String entryName = ((ClassMetaClass) metaClass).getFullyQualifiedTrueName("/") + ".class";
      log.trace("Looking for entry " + entryName);
      ZipEntry entry = zipFile.getEntry(entryName);
      if (entry != null)
        return path;
    }
    return null;
  }

  /**
   * Reads the supplied class path looking for the class <code>name</code>.
   * Returns the InputStream to the class. (The class may be packed within
   * a Zip or Jar file).
   *
   * @param name this may be any of the canonical styles of class naming
   * @return the InputStream to the class
   * @throws NoClassDefFoundError if no matching class can be found.
   */
  public InputStream openClass(TypeName name) throws NoClassDefFoundError
  {
    // TODO: reuse result from getFirstMatch() to pick up from where we left off
    for (File path: elements)
    {
      if (!path.exists())
        continue;
      log.trace("checking " + path);

      // if the file is a directory, search for the .class file in the appropriate subfolder
      if (path.isDirectory())
      {
        MetaClass metaClass = MetaClassFactory.getMetaClass(name);
        String directory = metaClass.getPackage().toName("/", false);
        File subDirectory = new File(path.getAbsolutePath(), directory);

        log.trace("Looking for directory " + subDirectory);

        if (subDirectory.exists())
        {
          String fileName = ((ClassMetaClass) metaClass).getTrueName() + ".class";
          File classFile = new File(subDirectory, fileName);

          log.trace("Looking for file " + classFile);
          if (classFile.exists())
          {
            try
            {
              return new FileInputStream(classFile);
            }
            catch (FileNotFoundException e)
            {
              // this shouldn't happen, because we just checked
              throw new RuntimeException("The file, " + fileName + " could not be found.");
            }
          }
        }
        continue;
      }

      // the file is a Jar or Zip file, so we need to search for the entry within the file
      String fileName = path.getName();

      if (!path.getName().endsWith(".zip") && !path.getName().endsWith(".jar"))
      {
        // not a zip file
        continue;
      }
      try
      {
        log.trace("Checking compressed file " + fileName);
        ZipFile zipFile = new ZipFile(path);
        MetaClass metaClass = MetaClassFactory.getMetaClass(name);
        if (metaClass instanceof ArrayMetaClass)
          metaClass = ((ArrayMetaClass) metaClass).getInnermostElementType();
        String entryName = ((ClassMetaClass) metaClass).getFullyQualifiedTrueName("/") + ".class";
        log.trace("Looking for entry " + entryName);
        ZipEntry entry = zipFile.getEntry(entryName);

        if (entry != null)
        {
          // This is kinda nasty, right here. We should really be closing the ZipFile and we're not.
          // There are a couple of different approaches we could take.
          //
          // 1) Read the ZipEntry into a byte[] and return that as a ByteArrayInputStream
          // so we can close the file immediately - Disadvantage - more memory, slower.
          // Advantage - pretty simple.
          //
          // 2) Create a wrapper InputStream that upon close, would close the ZipFile.
          // Advantage - no copying required. Disadvantage - Wrappering takes a minor performance hit.
          return zipFile.getInputStream(entry);
        }
      }
      catch (Exception e)
      {
        throw new RuntimeException("An error occurred while looking for " + name, e);
      }
    }
    throw new NoClassDefFoundError("The class, " + name + " could not be found on the class path, " + elements);
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
   * Tests ClassPath.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args)
  {
    String classPath = args[0];
    ClassPath source = new ClassPath(classPath);
    InputStream input = source.openClass(TypeNameFactory.fromPath(args[1]));
    ClassFile classFile = new ClassFile(input);
    System.out.println(classFile.toString());
    try
    {
      input.close();
    }
    catch (IOException e)
    {
      source.getLogger().error("closing the class file", e);
    }
  }
}
