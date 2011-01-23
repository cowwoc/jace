package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class MethodRefConstant implements TypedConstant
{
	private final int classIndex;
	private final int nameAndTypeIndex;

	public MethodRefConstant(int classIndex, int nameAndTypeIndex)
	{
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	@Override
	public int getNameAndTypeIndex()
	{
		return nameAndTypeIndex;
	}

	@Override
	public int getClassIndex()
	{
		return classIndex;
	}

	@Override
	public Object getValue()
	{
		return "MethodRefConstant.getValue() has not yet been implemented.";
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new MethodRefConstantReader().getTag());
		output.writeShort(classIndex);
		output.writeShort(nameAndTypeIndex);
	}
}
