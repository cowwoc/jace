package jace.autoproxy;

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
import jace.metaclass.VoidClass;
import jace.parser.ClassFile;
import jace.proxygen.ProxyGenerator;
import jace.proxygen.ProxyGenerator.AccessibilityType;
import jace.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AutoProxy application generates the Jace proxies needed for a C++ application
 * to run correctly.
 *
 * It examines the #includes of C++ headers and source files looking for
 * #include "jace/proxy/xxx". When it finds one of these includes, it generates
 * the corresponding Jace C++ proxy class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class AutoProxy
{
  private final Collection<File> inputHeaders;
  private final Collection<File> inputSources;
  private final File outputHeaders;
  private final File outputSources;
  private final List<File> classPath;
  private final boolean minimizeDependencies;
  private final Set<String> extraDependencies;
  private final boolean exportSymbols;
  /**
   * The set of classes to process.
   */
  private ClassSet classSet;
  private final Logger log = LoggerFactory.getLogger(AutoProxy.class);

  /**
   * Same as AutoProxy(inputHeaders, inputSources, outputHeaders, outputSources, classPath, minimizeDependencies,
   * extraDependencies, false)
   *
   * @param inputHeaders the directories to recursively search for header files
   * @param inputSources the directories to recursively search for source files
   * @param outputHeaders The directory to write new proxy header files to
   * @param outputSources The directory to write new proxy source files to
   * @param classPath the path to search for class files when resolving class dependencies
   * @param minimizeDependencies set to true to only generate proxies based on minimum dependency
   * (superclass, interfaces and any classes used by the input files). Set to false to generate proxies for
   * all class dependencies (arguments, return values, and fields). The latter is used to generate proxies
   * for a Java library when the input files are unknown.
   * @param extraDependencies a list of unused classes to generate proxies for. For example, when generating
   * C++ classes for a Java library there is no way of knowing ahead of time which proxies end-users will
   * need so you may use this argument to specify those classes ahead of time above those that are
   * automatically determined. This is only examined when minDep=true.
   * @throws IllegalArgumentException if inputHeaders, inputSources, outputHeaders, outputSources are null
   * or are not a directory, or if classpath or extraDependencies are null
   * @see AutoProxy(File, File, File, File, String, boolean, Set<String>, boolean)
   */
  public AutoProxy(Collection<File> inputHeaders, Collection<File> inputSources, File outputHeaders,
                   File outputSources, List<File> classPath, boolean minimizeDependencies,
                   Set<String> extraDependencies)
  {
    this(inputHeaders, inputSources, outputHeaders, outputSources, classPath, minimizeDependencies,
      extraDependencies, false);
  }

  /**
   * Creates a new AutoProxy.
   *
   * @param inputHeaders the directories to recursively search for header files
   * @param inputSources the directories to recursively search for source files
   * @param outputHeaders The directory to write new proxy header files to
   * @param outputSources The directory to write new proxy source files to
   * @param classPath the path to search for class files when resolving class dependencies
   * @param minimizeDependencies set to true to only generate proxies based on minimum dependency
   * (superclass, interfaces and any classes used by the input files). Set to false to generate proxies for all class
   * dependencies (arguments, return values, and fields). The latter is used to generate proxies for a Java library when
   * the input files are unknown.
   * @param extraDependencies a list of unused classes to generate proxies for. For example, when generating C++ classes
   * for a Java library there is no way of knowing ahead of time which proxies end-users will need so you may use this
   * argument to specify those classes ahead of time.
   * above those that are automatically determined. This is only examined when minDep=true.
   * @param exportSymbols true if proxies should export their symbols (i.e. when used in DLLs)
   *
   */
  public AutoProxy(Collection<File> inputHeaders, Collection<File> inputSources, File outputHeaders,
                   File outputSources, List<File> classPath, boolean minimizeDependencies,
                   Set<String> extraDependencies, boolean exportSymbols)
  {
    if (inputHeaders == null)
      throw new IllegalArgumentException("inputHeaders may not be null");
    if (inputSources == null)
      throw new IllegalArgumentException("inputSources may not be null");
    if (outputHeaders == null)
      throw new IllegalArgumentException("outputHeaders may not be null");
    if (outputSources == null)
      throw new IllegalArgumentException("outputSources may not be null");
    if (extraDependencies == null)
      throw new IllegalArgumentException("extraDependencies may not be null");
    if (classPath == null)
      throw new IllegalArgumentException("classPath may not be null");
    for (File file: inputHeaders)
    {
      if (!file.isDirectory())
        throw new IllegalArgumentException("inputHeaders must be an existing directory: " + file);
    }
    for (File file: inputSources)
    {
      if (!file.isDirectory())
        throw new IllegalArgumentException("inputSources must be an existing directory: " + file);
    }
    if (!outputHeaders.isDirectory())
      throw new IllegalArgumentException("outputHeaders must be an existing directory: " + outputHeaders);
    if (!outputSources.isDirectory())
      throw new IllegalArgumentException("outputSources must be an existing directory: " + outputSources);
    this.inputHeaders = inputHeaders;
    this.inputSources = inputSources;
    this.outputHeaders = outputHeaders;
    this.outputSources = outputSources;
    this.classPath = classPath;
    this.extraDependencies = extraDependencies;
    this.minimizeDependencies = minimizeDependencies;
    this.exportSymbols = exportSymbols;
    this.classSet = new ClassSet(classPath, minimizeDependencies);
    if (minimizeDependencies)
      classSet.addClasses(extraDependencies);
  }

  /**
   * Generates the Jace proxies for the specified C++ sources.
   *
   * @throws IOException if an error occurs while writing
   */
  public void generateProxies() throws IOException
  {
    FilenameFilter headerFilter = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        if (new File(dir, name).isDirectory())
          return true;
        String ucaseName = name.toUpperCase();
        return ucaseName.endsWith(".H") || ucaseName.endsWith(".HPP") || ucaseName.endsWith(".INL");
      }
    };

    FilenameFilter sourceFilter = new FilenameFilter()
    {
      public boolean accept(File dir, String name)
      {
        if (new File(dir, name).isDirectory())
          return true;
        String ucaseName = name.toUpperCase();
        return ucaseName.endsWith(".C") || ucaseName.endsWith(".CPP") || ucaseName.endsWith(".CXX");
      }
    };

    // traverse the header and source C++ directories to generate the needed class library
    for (File directory: inputHeaders)
      traverse(directory, headerFilter);
    for (File directory: inputSources)
      traverse(directory, sourceFilter);

    // do the actual proxy generation
    Set<MetaClass> classes = classSet.getClasses();
    ClassPath source = new ClassPath(classPath);

    // set up the dependency list for ProxyGenerator
    Set<MetaClass> dependencies = new HashSet<MetaClass>();

    // Include all of the dependent classes
    dependencies.addAll(classes);

    // Include all primitives
    dependencies.add(new BooleanClass());
    dependencies.add(new ByteClass());
    dependencies.add(new CharClass());
    dependencies.add(new ShortClass());
    dependencies.add(new IntClass());
    dependencies.add(new LongClass());
    dependencies.add(new FloatClass());
    dependencies.add(new DoubleClass());
    dependencies.add(new VoidClass());

    // now generate all of the proxies
    for (MetaClass dependency: classes)
    {
      ClassMetaClass metaClass = (ClassMetaClass) dependency;
      String sourceName = ((ClassMetaClass) metaClass.deProxy()).getFullyQualifiedTrueName("/");

      String classFileName = metaClass.getFileName();
      File sourceFile = new File(sourceName);
      File targetHeaderFile = new File(outputHeaders, classFileName + ".h");
      File targetSourceFile = new File(outputSources, classFileName + ".cpp");
      if (targetSourceFile.exists() && targetHeaderFile.exists() &&
          sourceFile.lastModified() <= targetSourceFile.lastModified() &&
          sourceFile.lastModified() <= targetHeaderFile.lastModified())
      {
        // the source-file has not been modified since we last generated the target source/header files
        log.info(sourceFile + " has not been modified, skipping...");
        continue;
      }

      InputStream input = source.openClass(sourceName);
      ClassFile classFile = new ClassFile(input);
      ProxyGenerator.writeProxy(metaClass, classFile, AccessibilityType.PUBLIC, outputHeaders, outputSources,
        dependencies, exportSymbols);
      input.close();
    }

  /* I just realized that package import headers don't make any sense related to AutoProxy
   * I'll just leave this in here for now in case something changes, but I find that unlikely.
   */
  // Now update package import headers
  // PackageGen packageGen = PackageGen.newMetaClassInstance( destHeaderDir, classes );
  // packageGen.execute();
  }

  /**
   * Traverses the specified File looking for Jace proxy includes.
   * For every #include, it adds the fully-qualified class name
   * to <code>proxies</code>.
   *
   * @param f - May be a file or a directory. If it is a directory, all of the
   * sub-directories and files in that directory are traversed.
   * @param filter the filename filter
   */
  private void traverse(File f, FilenameFilter filter)
  {
    log.debug(f.getAbsolutePath());
    if (f.isDirectory())
    {
      File[] files = f.listFiles(filter);
      for (int i = 0; i < files.length; ++i)
        traverse(files[i], filter);
      return;
    }

    // look through the file to see if we can find any Jace proxy includes
    BufferedReader reader = null;

    try
    {
      reader = new BufferedReader(new FileReader(f));

      for (String line; (line = reader.readLine()) != null;)
      {
        line = line.trim();

        String[] lineTokens = line.split("\\s");

        // make sure this line is an #include
        if (line.length() >= 2 && lineTokens[0].equals("#include") && isQuoted(lineTokens[1]))
        {
          String header = unQuote(lineTokens[1]);

          // Ensure that the separator character is "/"
          header = header.replaceAll(Pattern.quote(File.separator), "/");
          String[] headerTokens = header.split("/");

          // make sure the #include is for an actual Jace proxy
          if (headerTokens.length < 3 || !headerTokens[0].equals("jace") || !headerTokens[1].equals("proxy"))
            continue;

          // Strip "jace/proxy" from the package name
          StringBuilder packageName = new StringBuilder();
          String className = headerTokens[headerTokens.length - 1];
          for (int i = 2, size = headerTokens.length - 1; i < size; ++i)
          {
            String component = headerTokens[i];
            packageName.append(component + "/");
          }

          // Ignore any of the built in Jace proxies
          String packageNameStr = packageName.toString();
          if (packageNameStr.startsWith("types/") ||
              className.startsWith("JObject") ||
              className.startsWith("JValue"))
          {
            continue;
          }

          // delete ".h" suffix
          if (className.toLowerCase().endsWith(".h"))
            className = className.substring(0, className.length() - ".h".length());
          try
          {
            classSet.addClass(packageNameStr, className);
          }
          catch (NoClassDefFoundError e)
          {
            throw new RuntimeException("Error parsing " + f, e);
          }
        }
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException("Unexpected I/O exception while traversing the C++ source directories", e);
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();
        }
      }
    }
  }

  /**
   * Returns true if a String starts and ends with a quote character.
   *
   * @param str the string
   * @return true if a String starts and ends with a quote character
   */
  private boolean isQuoted(String str)
  {
    return str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"';
  }

  /**
   * Removes quotes from a string.
   *
   * @param str the string
   * @return the string without the quotes
   */
  private String unQuote(String str)
  {
    return str.substring(1, str.length() - 1);
  }

  /**
   * Returns a String describing the usage of this tool.
   *
   * @return String describing the usage of this tool
   */
  public static String getUsage()
  {

    String newLine = System.getProperty("line.separator");

    return "Usage: AutoProxy " + newLine +
           "  <" + File.pathSeparator + "-separated list of c++ header directories> " + newLine +
           "  <" + File.pathSeparator + "-separated list of c++ source directories> " + newLine +
           "  <destination proxy header directory> " + newLine +
           "  <destination proxy source directory> " + newLine +
           "  <java classpath for proxies> " + newLine +
           "  [options]" + newLine +
           newLine +
           "Where options can be:" + newLine +
           "  -mindep " + newLine +
           "  -extraDependencies=<comma-separated list of classes> " + newLine +
           "  -exportsymbols";
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
   * Generates C++ proxies based on input C++ files.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args)
  {

    if (args.length < 5 || args.length > 7)
    {
      System.out.println(getUsage());
      return;
    }

    Collection<File> inputHeaders = new ArrayList<File>();
    for (String path: args[0].split(Pattern.quote(File.pathSeparator)))
      inputHeaders.add(new File(path));
    Collection<File> inputSources = new ArrayList<File>();
    for (String path: args[1].split(Pattern.quote(File.pathSeparator)))
      inputSources.add(new File(path));

    File outputHeaders = new File(args[2]);
    File outputSources = new File(args[3]);
    String classPath = args[4];

    boolean minimizeDependencies = false;
    Set<String> extraDependencies = new HashSet<String>();
    boolean exportSymbols = false;

    for (int i = 5; i < args.length; ++i)
    {
      if (args[i].equals("-mindep"))
        minimizeDependencies = true;
      else if (args[i].startsWith("-deplist"))
      {
        String[] equalTokens = args[i].split("=");
        String[] commaTokens = equalTokens[1].split(",");
        for (String token: commaTokens)
          extraDependencies.add(token);
      }
      else if (args[i].equals("-exportsymbols"))
      {
        exportSymbols = true;
      }
    }

    AutoProxy ap = new AutoProxy(inputHeaders, inputSources, outputHeaders, outputSources,
      Util.parseClasspath(classPath), minimizeDependencies, extraDependencies, exportSymbols);
    Logger log = ap.getLogger();
    log.info("Beginning Proxy generation.");
    try
    {
      ap.generateProxies();
    }
    catch (IOException e)
    {
      log.error("", e);
    }
    log.info("Finished Proxy generation.");
  }
}
