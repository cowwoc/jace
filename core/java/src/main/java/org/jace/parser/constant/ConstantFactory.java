package org.jace.parser.constant;

import com.google.common.collect.Maps;
import org.jace.parser.ConstantPool;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Used to create constants from an InputStream.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ConstantFactory
{
  private static final ConstantFactory constantFactory = new ConstantFactory();
  private final Map<Integer, ConstantReader> constantReaders = Maps.newHashMap();

  public static ConstantFactory getInstance()
  {
    return constantFactory;
  }

  public Constant readConstant(InputStream inputStream, ConstantPool pool) throws ClassFormatError
  {
    DataInputStream input;
    int tag;

    try
    {
      input = new DataInputStream(inputStream);
      tag = input.readUnsignedByte();
    }
    catch (IOException e)
    {
      ClassFormatError exception = new ClassFormatError("Unexpected end of class definition");
      exception.initCause(e);
      throw exception;
    }

    ConstantReader reader = getReader(tag);

    if (reader == null)
      throw new ClassFormatError("Unrecognized constant tag value < " + tag + " >");
    return reader.readConstant(input, pool);
  }

  private ConstantFactory()
  {
    addReader(new ClassConstantReader());
    addReader(new FieldRefConstantReader());
    addReader(new MethodRefConstantReader());
    addReader(new InterfaceMethodRefConstantReader());
    addReader(new StringConstantReader());
    addReader(new IntegerConstantReader());
    addReader(new FloatConstantReader());
    addReader(new LongConstantReader());
    addReader(new DoubleConstantReader());
    addReader(new NameAndTypeConstantReader());
    addReader(new UTF8ConstantReader());
  }

  private void addReader(ConstantReader reader)
  {
    constantReaders.put(Integer.valueOf(reader.getTag()), reader);
  }

  private ConstantReader getReader(int tag)
  {
    return constantReaders.get(Integer.valueOf(tag));
  }
}
