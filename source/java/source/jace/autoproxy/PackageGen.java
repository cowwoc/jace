package jace.autoproxy;

import jace.metaclass.ClassPackage;
import jace.metaclass.MetaClass;
import jace.metaclass.MetaClassFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates pkg_import headers given a set of classes.
 *
 * If a pkg_import header already exists, it will be
 * updated with the new set of classes.
 *
 */
public class PackageGen {

  private final Logger log = LoggerFactory.getLogger(PackageGen.class);
  /**
   * The base directory containing the header files.
   * This is where the pkg_import headers get written to.
   *
   */
  private String headerDir;
  /**
   * A mapping of classes to their respective packages.
   *
   */
  private Map<ClassPackage, Set<MetaClass>> classesByPackage;
  public static final String PACKAGE_HEADER = "pkg_import.h";

  /**
   * Creates a new PackageGen.
   *
   * @param headerDir the header directory
   * @param classes the names of the classes in the package
   */
  public PackageGen(String headerDir, Collection<String> classes) {
    if (headerDir.charAt(headerDir.length() - 1) != File.separatorChar) {
      headerDir += File.separator;
    }

    this.headerDir = headerDir;

    classesByPackage = new HashMap<ClassPackage, Set<MetaClass>>();

    for (String className : classes) {
      MetaClass mc = MetaClassFactory.getMetaClass(className, false);
      addClass(mc);
    }
  }

  private PackageGen() {
  }

  public static PackageGen newMetaClassInstance(String headerDir, Collection metaClasses) {

    if (headerDir.charAt(headerDir.length() - 1) != File.separatorChar) {
      headerDir += File.separator;
    }

    PackageGen pg = new PackageGen();

    pg.headerDir = headerDir;
    pg.classesByPackage = new HashMap<ClassPackage, Set<MetaClass>>();

    for (Iterator it = metaClasses.iterator(); it.hasNext();) {
      pg.addClass((MetaClass) it.next());
    }

    return pg;
  }

  private void addClass(MetaClass mc) {
    Set<MetaClass> s = classesByPackage.get(mc.getPackage());
    if (s == null) {
      s = new HashSet<MetaClass>();
      classesByPackage.put(mc.getPackage(), s);
    }
    s.add(mc);
  }

  /**
   * Runs the PackageGen.
   *
   */
  public void execute() throws IOException {

    // Now go through all of the packages and update the package headers
    //
    for (Iterator it = classesByPackage.keySet().iterator(); it.hasNext();) {

      ClassPackage cp = (ClassPackage) it.next();
      Set<MetaClass> classSet = classesByPackage.get(cp);
      String packageDir = headerDir + cp.toName(File.separator, false);
      String packageFile = packageDir + File.separator + PACKAGE_HEADER;

      new File(packageDir).mkdirs();

      // Read in everything if it already exists.
      if (new File(packageFile).exists()) {

        FileInputStream input = new FileInputStream(packageFile);
        BufferedReader r = new BufferedReader(new InputStreamReader(input));

        while (true) {
          String line = r.readLine();

          if (line == null) {
            break;
          }

          line = line.trim();

          if (line.startsWith("#include")) {
            StringTokenizer st = new StringTokenizer(line, " ");
            st.nextToken();
            String include = st.nextToken();
            String class_ = unQuote(include);
            // Remove the trailing ".h" charachters.
            class_ = class_.substring(0, class_.length() - 2);
            // Already coming in proxied
            MetaClass mc = MetaClassFactory.getMetaClass(class_, false, false);
            classSet.add(mc);
          }
        }

        r.close();
      }

      FileOutputStream output = new FileOutputStream(packageFile);
      PrintWriter w = new PrintWriter(new OutputStreamWriter(output));

      for (Iterator classesIt = classSet.iterator(); classesIt.hasNext();) {
        Object obj = classesIt.next();
        log.debug("{}", obj);
        MetaClass mc = (MetaClass) obj;
        w.println(mc.include());
      }

      w.println();
      w.close();
    }
  }

  private String unQuote(String str) {
    return str.substring(1, str.length() - 1);
  }

	/**
	 * Prints out debugging information.
	 */
  public void print() {
    for (ClassPackage cp: classesByPackage.keySet()) {
      Set<MetaClass> c = classesByPackage.get(cp);
      log.debug(cp + ": " + c);
    }
  }

  /**
   * Returns the logger associated with the object.
   *
   * @return the logger associated with the object
   */
  private Logger getLogger() {
    return log;
  }

	/**
   * Tests PackageGen.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args) {

    if (args.length < 2) {
      System.out.println("PackageGen <header directory> [ classes... ]");
      return;
    }

    String headerDir = args[ 0];

    ArrayList<String> classes = new ArrayList<String>(args.length - 1);

    for (int i = 1; i < args.length; ++i) {
      classes.add(args[i]);
    }

    PackageGen pg = new PackageGen(headerDir, classes);
		try {
    pg.execute();
		}
		catch (IOException e) {
			pg.getLogger().error("", e);
		}
    pg.print();
  }
}
