package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class InterfaceMethodRefConstant implements TypedConstant
{
	private final int classIndex;
	private final int nameAndTypeIndex;

	public InterfaceMethodRefConstant(int classIndex, int nameAndTypeIndex)
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
		return "Not yet implemented.";
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new InterfaceMethodRefConstantReader().getTag());
		output.writeShort(classIndex);
		output.writeShort(nameAndTypeIndex);
	}
}
