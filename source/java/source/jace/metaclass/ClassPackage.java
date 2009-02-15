package jace.metaclass;

import java.util.*;

/**
 * Represents the package for a class.
 *
 * For example, the ClassPackage for java.lang.Object would
 * represent the package "java.lang"
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassPackage {

  /**
   * Creates a new ClassPackage.
   *
   * @param packagePath the package components
   */
  public ClassPackage(String[] packagePath) {
    List<String> list = Arrays.asList(packagePath);
    mPackage = new String[packagePath.length];
    list.toArray(mPackage);
  }

  /**
   * Creates a new ClassPackage.
   *
   * @param packagePath the package components
   */
  public ClassPackage(Collection<String> packagePath) {
    mPackage = new String[packagePath.size()];
    packagePath.toArray(mPackage);
  }

  /**
   * Returns the name of this package, separating individual packages
   * by the path separator.
   *
   * @param separator - The separator to be used
   *
   * @param trailingSeparator - True if a trailing separator should
   * be added.
   *
   * For example,
   *
   *   ClassPackage p = new ClassPackage( new String[] { "java", "lang" } );
   *   package.toName( "/", true );    // returns "java/lang/"
   *   package.toName( ", ", false );  // returns "java, lang"
   * @return the name of this package
   */
  public String toName(String separator, boolean trailingSeparator) {

    StringBuilder name = new StringBuilder();

    for (int i = 0; i < mPackage.length; ++i) {

      name.append(mPackage[i]);

      if ((i == mPackage.length - 1) && !trailingSeparator) {
        return name.toString();
      }

      name.append(separator);
    }

    return name.toString();
  }

  public String toString() {
    return toName(".", false);
  }

  /**
   * Returns the path for the package.
   *
   * For example,
   *
   *  new String[] { "java", "lang" }
   *
   * @return the path for the package
   */
  public String[] getPath() {
    List<String> l = Arrays.asList(mPackage);
    String[] path = new String[mPackage.length];
    l.toArray(path);
    return path;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ClassPackage))
      return false;
    try {
      ClassPackage cp = (ClassPackage) obj;

      if (cp.mPackage.length != mPackage.length) {
        return false;
      }

      for (int i = 0; i < mPackage.length; ++i) {
        if (!cp.mPackage[i].equals(mPackage[i])) {
          return false;
        }
      }
    }
    catch (ClassCastException cce) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return toString().hashCode();
  }

  /**
   * A test harness for this class.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {

    ClassPackage p = new ClassPackage(args);

    System.out.println(p.toName(".", true));
    System.out.println("( " + p.toName(", ", false) + " )");

  }
  private String[] mPackage;
}
