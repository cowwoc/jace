package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class FieldRefConstant implements TypedConstant
{
	private final int classIndex;
	private final int nameAndTypeIndex;

	public FieldRefConstant(int classIndex, int nameAndTypeIndex)
	{
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	@Override
	public int getClassIndex()
	{
		return classIndex;
	}

	@Override
	public int getNameAndTypeIndex()
	{
		return nameAndTypeIndex;
	}

	@Override
	public Object getValue()
	{
		return "FieldRefConstant.getValue() has not yet been implemented.";
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new FieldRefConstantReader().getTag());
		output.writeShort(classIndex);
		output.writeShort(nameAndTypeIndex);
	}
}
