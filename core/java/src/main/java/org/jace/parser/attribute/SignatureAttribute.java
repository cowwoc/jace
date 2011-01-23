package org.jace.parser.attribute;

import org.jace.parser.ConstantPool;
import org.jace.parser.constant.Constant;
import org.jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A SignatureAttribute represents a type signature for a generic type
 * compiled with the new generics-enabled 1.5 compilers.
 *
 * @author Toby Reyelts
 */
public class SignatureAttribute implements Attribute
{
	/* Not yet available in the JVM specification.
	 * Rather, this info is gleaned from Scott Ananian at
	 *   http://cscott.net/Projects/GJ/signature-explained-2_2.html
	 *
	 * (u1 represents an unsigned byte)
	 * (u2 represents an unsigned short)
	 * (u4 represents an unsigned int)
	 *
	 * attribute_info {
	 *   u2 attribute_name_index;
	 *   u4 attribute_length;
	 *   u2 signature_index;
	 * }
	 */
	private final int nameIndex;
	private final int length;
	private final int valueIndex;
	private final ConstantPool pool;

	/**
	 * Creates a new SignatureAttribute.
	 * 
	 * @param stream the stream to read from
	 * @param nameIndex the attribute index in the constant pool
	 * @param pool the constant pool to read from
	 * @throws IOException if an I/O error occurs while reading the attribute
	 */
	public SignatureAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException
	{
		this.pool = pool;
		this.nameIndex = nameIndex;

		/* Read the name for this constant.
		 * From the spec, we know it must be equal to "Signature".
		 */
		Constant c = pool.getConstantAt(nameIndex);

		if (c instanceof UTF8Constant)
		{
			String name = c.getValue().toString();

			if (!name.equals("Signature"))
			{
				throw new ClassFormatError("While reading a SignatureAttribute, the name Signature was expected, "
																	 + "but the name " + name + " was encountered.");
			}
		}
		else
		{
			throw new ClassFormatError("While reading a SignatureAttribute, a UTF8Constant was expected, "
																 + "but a constant of type " + c.getClass().getName()
																 + " was encountered.");
		}

		DataInputStream input = new DataInputStream(stream);

		// Read the length of the attribute.
		// This should be 2 according to spec.
		this.length = input.readInt();

		if (length != 2)
		{
			throw new ClassFormatError("While reading a SignatureAttribute, an attribute length of size 2 was expected, "
																 + "but an attribute length of size " + length
																 + " was encountered.");
		}

		// Read the signature itself
		this.valueIndex = input.readUnsignedShort();
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeShort(nameIndex);
		output.writeInt(length);
		output.writeShort(valueIndex);
	}

	/**
	 * Returns the attribute name.
	 *
	 * @return the attribute name
	 */
	@Override
	public String getName()
	{
		return pool.getConstantAt(nameIndex).toString();
	}

	/**
	 * Returns the Signature string that represents this generic type.
	 *
	 * More information from Scott follows:
	 *
	 * When used as an attribute of a method or field, a signature gives the full
	 * (possibly generic) type of that method or field. When used as a class attribute,
	 * a signature indicates the type parameters of the class, followed by its supertype,
	 * followed by all its interfaces.
	 *
	 * The type syntax in signatures is extended to parameterized types and type variables.
	 * There is also a new signature syntax for formal type parameters. The syntax
	 * extensions for signature strings are as follows:
	 *
	 * MethodOrFieldSignature ::= TypeSignature
	 * ClassSignature         ::= ParameterPartOpt super_TypeSignature interface_TypeSignatures
	 * TypeSignatures         ::= TypeSignatures TypeSignature
	 *
	 * TypeSignature          ::= ...
	 * |  ArrayTypeSignature
	 * |  ClassTypeSignature
	 * |  MethodTypeSignature
	 * |  TypeVariableSignature
	 *
	 * VariantTypeSignature   ::= '-' TypeSignature
	 * |  '+' TypeSignature
	 * |  TypeSignature
	 * |  '*'
	 *
	 * VariantTypeSignatures  ::= VariantTypeSignatures VariantTypeSignature
	 * |
	 *
	 * ClassTypeSignature     ::= 'L' Ident TypeArgumentsOpt ';'
	 * |  ClassTypeSignature '.' 'L' Ident TypeArgumentsOpt ';'
	 *
	 * MethodTypeSignature    ::= ParameterPartOpt '(' TypeSignatures ')'
	 * TypeSignature ThrowsSignatureListOpt
	 *
	 * ThrowsSignatureList    ::= ThrowsSignature ThrowsSignatureList
	 * |  ThrowsSignature
	 *
	 * ThrowsSignature        ::= '^' TypeSignature
	 *
	 * TypeVariableSignature  ::= 'T' Ident ';'
	 *
	 * TypeArguments          ::= '<' VariantTypeSignature VariantTypeSignatures '>'
	 * ParameterPart          ::= '<' ParameterSignature ParameterSignatures '>'
	 *
	 * ParameterSignatures    ::= ParameterSignatures ParameterSignature
	 * |
	 *
	 * ParameterSignature     ::= Ident ':' TypeSignature
	 * |  Ident ':' TypeSignatureOpt ':' TypeSignature TypeSignatures
	 *
	 * ArrayTypeSignature     ::= '[' TypeSignature
	 *
	 * interface_TypeSignature::= TypeSignature
	 *
	 * super_TypeSignature    ::= TypeSignature
	 *
	 * @return the Signature string that represents this generic type
	 */
	public String getValue()
	{
		Constant c = pool.getConstantAt(valueIndex);

		if (!(c instanceof UTF8Constant))
		{
			String msg = "Expecting a UTF8Constant for the Signature index.";
			throw new ClassFormatError(msg);
		}

		return c.getValue().toString();
	}

	/**
	 * Returns the length of this Attribute.
	 *
	 * @return the length of this Attribute
	 */
	public int getLength()
	{
		return length;
	}

	@Override
	public String toString()
	{
		return getClass() + " " + getValue();
	}
}
