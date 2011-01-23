package org.jace.parser;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * A utility class used to perform formatting conversions.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassFormatter
{
	private final Map<String, String> typeMap = Maps.newHashMapWithExpectedSize(8);

	public ClassFormatter()
	{
		typeMap.put("I", "int");
		typeMap.put("D", "double");
		typeMap.put("F", "float");
		typeMap.put("C", "char");
		typeMap.put("S", "short");
		typeMap.put("Z", "boolean");
		typeMap.put("B", "byte");
		typeMap.put("J", "long");
	}

	/**
	 * Converts a field descriptor to a type.
	 *
	 * For example, the field descriptor, "[[Ljava/lang/String;" gets converted to
	 * the type, "String[][]".
	 */
	private String fieldDescriptorToType(String descriptor)
	{
		// From the JVM specification:
		//
		// FieldDescriptor -> FieldType
		// ComponentType   -> FieldType
		// FieldType  -> BaseType | ObjectType | ArrayType
		// BaseType   -> B | C | D | F | I | J | S | Z
		// ObjectType -> L<classname>;
		// ArrayType  -> [ComponentType
		if (descriptor.length() == 0)
		{
			throw new RuntimeException("The descriptor <" + descriptor
																 + "> is malformed. The descriptor is empty.");
		}

		// First, check to see if the descriptor is a BaseType
		String temp = typeMap.get(descriptor);
		if (temp != null)
			return temp;

		// Then, check to see if the descriptor is an ObjectType
		if (descriptor.charAt(0) == 'L')
		{
			// Replace all of the '/' characters with '.' characters
			temp = descriptor.replace('/', '.');

			// Remove the starting 'L' and the trailing ';'
			if (temp.charAt(descriptor.length() - 1) != ';')
			{
				throw new RuntimeException("The descriptor <" + descriptor
																	 + "> is malformed. The descriptor is missing a "
																	 + "trailing ';' character.");
			}

			return temp.substring(1, temp.length() - 1);
		}

		// Check to see if this is an ArrayType
		if (descriptor.charAt(0) == '[')
			return fieldDescriptorToType(descriptor.substring(1, descriptor.length())) + "[]";

		// Otherwise, this is a malformed descriptor
		throw new RuntimeException("The descriptor <" + descriptor + "> is malformed.");
	}

	/**
	 * Tests ClassFormatter.
	 *
	 * @param args the command-line argument
	 */
	@SuppressWarnings("UseOfSystemOutOrSystemErr")
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: ClassFormatter <descriptor>");
			System.out.println("For example: ClassFormatter Ljava/lang/String;");
		}

		ClassFormatter cf = new ClassFormatter();
		System.out.println(cf.fieldDescriptorToType(args[ 0]));
	}
}
