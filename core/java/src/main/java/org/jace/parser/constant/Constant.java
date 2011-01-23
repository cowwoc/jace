package org.jace.parser.constant;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Constant
{
	public int getSize();

	public Object getValue();

	public void write(DataOutputStream output) throws IOException;
}
