package jace.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
  private static final String newLine = System.getProperty("line.separator");

  /**
   * Generates a header comment with the given message.
   *
   * Instead of doing a nicer, yet more complicated word-parsing algorithm,
   * this function supports new lines, by parsing any '\n' characters passed
   * in the given message.
   *
   * @param output the output writer
   * @param message the comment body
   * @throws IOException if an error occurs while writing
   */
  public static void generateComment(Writer output, String message) throws IOException {
    output.write("/**" + newLine);

    StringReader reader = new StringReader(message);
    StringBuilder line = new StringBuilder();
    while (true) {
      output.write(" * ");
      line.setLength(0);
      while (true) {
        int i = reader.read();
        if (i == -1) {
          output.write(newLine);
          output.write(" *" + newLine);
          output.write(" */" + newLine);
          return;
        }

        char c = (char) i;
        output.write(c);
        line.append(c);

        // if we've encountered a newLine, we need to start a new line
        if (line.toString().endsWith(newLine))
          break;
      }
    }
  }
	
	/**
	 * Parses a String classpath to a {@code List<File>} format.
	 * 
	 * @param classPath the String representation of the classpath
	 * @return the {@code List<File>} making up the classpath
	 * @throws IllegalArgumentException if classPath is null
	 */
	public static List<File> parseClasspath(String classPath) throws IllegalArgumentException {		
		if (classPath==null)
			throw new IllegalArgumentException("classPath may not be null");
	  List<File> result = new ArrayList<File>();
		List<String> classPathArray = Arrays.asList(classPath.split(File.pathSeparator));
		for (String path: classPathArray) {
  		if (path.contains("*") || path.contains("?")) {
				path = path.replaceAll(Pattern.quote(File.separator), "/");
				int index = path.lastIndexOf("/");
				String directory;
				String filename;
				if (index==-1) {
					directory = ".";
					filename = path;
				}
				else {
					directory = path.substring(0, index);
					filename = path.substring(index + "/".length());
				}
				if (directory.contains("*") || directory.contains("?"))	{
					throw new IllegalArgumentException("classpath directories may not contain wildcards");
				}
				WildcardFileFilter filter = new WildcardFileFilter(filename);
				File[] files = new File(directory).listFiles(filter);
				for (File file: files)
					result.add(file);
			}
			else
				result.add(new File(path));
		}
		return result;
	}
}
