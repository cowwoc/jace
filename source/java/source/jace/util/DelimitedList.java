package jace.util;

import java.util.*;

/**
 * A utility class that can take a list of Objects
 * and generate a string version of the list.
 *
 * @author Toby Reyelts
 *
 */
public class DelimitedList {

  public interface Stringifier {

    public String toString(Object obj);
  }
  private static Stringifier defaultStringifier = new Stringifier() {

    public String toString(Object obj) {
      return obj.toString();
    }
  };

  public DelimitedList(Collection c) {
    mCollection = c;
  }

  public String toList(String separator, boolean terminated) {
    return toList(defaultStringifier, separator, terminated);
  }

  public String toList(Stringifier stringifier, String separator, boolean terminated) {

    StringBuilder sb = new StringBuilder();

    for (Iterator it = mCollection.iterator(); it.hasNext();) {
      sb.append(stringifier.toString(it.next()));

      if (!it.hasNext() && !terminated) {
        return sb.toString();
      }

      sb.append(separator);
    }

    return sb.toString();
  }
  private Collection mCollection;
}

