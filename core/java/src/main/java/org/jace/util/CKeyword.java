package org.jace.util;

import com.google.common.collect.Sets;
import java.util.Set;

public class CKeyword
{
	/**
	 * Contains the entire collection of C++ keywords along with any other commonly
	 * encountered C++ identifiers (like NULL).
	 */
	private static final Set<String> CKeywords = Sets.newHashSetWithExpectedSize(75);

	static
	{
		CKeywords.add("and");
		CKeywords.add("and_eq");
		CKeywords.add("asm");
		CKeywords.add("auto");
		CKeywords.add("bitand");
		CKeywords.add("bitor");
		CKeywords.add("bool");
		CKeywords.add("break");
		CKeywords.add("case");
		CKeywords.add("catch");
		CKeywords.add("char");
		CKeywords.add("class");
		CKeywords.add("compl");
		CKeywords.add("const");
		CKeywords.add("const_cast");
		CKeywords.add("continue");
		CKeywords.add("default");
		CKeywords.add("delete");
		CKeywords.add("do");
		CKeywords.add("double");
		CKeywords.add("dynamic_cast");
		CKeywords.add("else");
		CKeywords.add("enum");
		CKeywords.add("explicit");
		CKeywords.add("export");
		CKeywords.add("extern");
		CKeywords.add("false");
		CKeywords.add("float");
		CKeywords.add("for");
		CKeywords.add("friend");
		CKeywords.add("goto");
		CKeywords.add("if");
		CKeywords.add("inline");
		CKeywords.add("int");
		CKeywords.add("long");
		CKeywords.add("mutable");
		CKeywords.add("namespace");
		CKeywords.add("new");
		CKeywords.add("not");
		CKeywords.add("not_eq");
		CKeywords.add("operator");
		CKeywords.add("or");
		CKeywords.add("or_eq");
		CKeywords.add("private");
		CKeywords.add("protected");
		CKeywords.add("public");
		CKeywords.add("register");
		CKeywords.add("reinterpret_cast");
		CKeywords.add("return");
		CKeywords.add("short");
		CKeywords.add("signed");
		CKeywords.add("sizeof");
		CKeywords.add("static");
		CKeywords.add("static_cast");
		CKeywords.add("struct");
		CKeywords.add("switch");
		CKeywords.add("template");
		CKeywords.add("this");
		CKeywords.add("throw");
		CKeywords.add("true");
		CKeywords.add("try");
		CKeywords.add("typedef");
		CKeywords.add("typeid");
		CKeywords.add("typename");
		CKeywords.add("union");
		CKeywords.add("unsigned");
		CKeywords.add("using");
		CKeywords.add("virtual");
		CKeywords.add("void");
		CKeywords.add("volatile");
		CKeywords.add("wchar_t");
		CKeywords.add("while");
		CKeywords.add("xor");
		CKeywords.add("xor_eq");
		CKeywords.add("NULL");
	}

	/**
	 * Adjust the identifier to make sure that it is C++ compatible.
	 *
	 * For example, '$' characters are not allowed in C++, and many identifiers
	 * which are legal in Java are keywords or commonly used macros in C++.
	 *
	 * @param identifier the Java identifier
	 * @return a C++-compatible mangling of the identifier
	 */
	public static String adjust(String identifier)
	{
		String result = identifier;
		if (CKeywords.contains(result))
			result += "_";
		return result.replace('$', '_');
	}
}
