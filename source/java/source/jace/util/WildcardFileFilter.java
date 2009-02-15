package jace.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Filters filenames containing wildcard characters.
 * 
 * @author Gili Tzabari
 */
public class WildcardFileFilter implements FileFilter {
	private final Pattern pattern;

	/**
	 * Create a new WildcardFileFilter.
	 * 
	 * @param filename the filename containing wildcards
	 */
	public WildcardFileFilter(String filename) {
		StringBuilder escaped = new StringBuilder();
		for (int i = 0, size = filename.length(); i < size; ++i) {
			char c = filename.charAt(i);
			switch (c) {
				case '.':
					escaped.append(Pattern.quote("."));
					break;
				case '?':
					escaped.append(".");
					break;
				case '*':
					escaped.append(".*");
					break;
				default:
					escaped.append(c);
			}
		}
		this.pattern = Pattern.compile(escaped.toString());
	}

	public boolean accept(File file) {
		return pattern.matcher(file.getName()).matches();
	}
}