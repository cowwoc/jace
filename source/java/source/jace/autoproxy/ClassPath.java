package jace.autoproxy;

import jace.metaclass.ArrayMetaClass;
import jace.metaclass.ClassMetaClass;
import jace.metaclass.MetaClass;
import jace.metaclass.MetaClassFactory;
import jace.metaclass.TypeName;
import jace.metaclass.TypeNameFactory;
import jace.parser.ClassFile;
import jace.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
   */
  public ClassPath(List<File> elements)
  {
    if (elements == null)
      throw new IllegalArgumentException("classPath may not be null");
    this.elements = elements;
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
              // This shouldn't happen, because we just checked.
              e.printStackTrace();
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
        // Not a zip file.
        continue;
      }
      try
      {
        log.trace("Checking compressed file " + fileName);
        ZipFile zipFile = new ZipFile(path);
        MetaClass metaClass = MetaClassFactory.getMetaClass(name);
        if (metaClass instanceof ArrayMetaClass)
          metaClass = ((ArrayMetaClass) metaClass).getBaseClass();
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
        throw new RuntimeException("An error occured while looking for " + name, e);
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
    ClassPath source = new ClassPath(Util.parseClasspath(classPath));
    InputStream input = source.openClass(TypeNameFactory.fromPath(args[1]));
    ClassFile classFile = new ClassFile(input);
    classFile.print();
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
