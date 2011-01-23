package org.jace.util;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Filters filenames containing wildcard characters.
 * 
 * @author Gili Tzabari
 */
public class WildcardFileFilter implements FileFilter
{
	private final Pattern pattern;

	/**
	 * Create a new WildcardFileFilter.
	 *
	 * @param path a path containing wildcards
	 */
	public WildcardFileFilter(String path)
	{
		// Assume 10% overhead
		StringBuilder escaped = new StringBuilder((int) (path.length() * 1.1));
		for (int i = 0, size = path.length(); i < size; ++i)
		{
			char c = path.charAt(i);
			switch (c)
			{
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

	@Override
	public boolean accept(File file)
	{
		return pattern.matcher(file.getName()).matches();
	}
}
