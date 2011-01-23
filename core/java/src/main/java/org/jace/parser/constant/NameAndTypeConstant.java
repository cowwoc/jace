package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public class NameAndTypeConstant implements Constant
{
	private int nameIndex;
	private int descriptorIndex;

	public NameAndTypeConstant(int nameIndex, int descriptorIndex)
	{
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public int getNameIndex()
	{
		return nameIndex;
	}

	public void setNameIndex(int index)
	{
		nameIndex = index;
	}

	public int getDescriptorIndex()
	{
		return descriptorIndex;
	}

	public void setDescriptorIndex(int index)
	{
		descriptorIndex = index;
	}

	@Override
	public Object getValue()
	{
		return "NameAndTypeConstant.getValue() has not yet been implemented.";
	}

	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	public void write(DataOutputStream output) throws IOException
	{
		output.writeByte(new NameAndTypeConstantReader().getTag());
		output.writeShort(nameIndex);
		output.writeShort(descriptorIndex);
	}
}
