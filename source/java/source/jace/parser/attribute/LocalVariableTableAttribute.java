package jace.parser.attribute;

import jace.parser.ConstantPool;
import jace.parser.constant.Constant;
import jace.parser.constant.UTF8Constant;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A LocalVariableTableAttribute
 * compiled with the new generics-enabled 1.5 compilers.
 *
 * @author Toby Reyelts
 *
 */
public class LocalVariableTableAttribute implements Attribute {

  /* From the JVM spec:
   *
   * LocalVariableTable_attribute {
   *  	u2 attribute_name_index;
   *  	u4 attribute_length;
   *  	u2 local_variable_table_length;
   *  	{  u2 start_pc;
   *  	    u2 length;
   *  	    u2 name_index;
   *  	    u2 descriptor_index;
   *  	    u2 index;
   *  	} local_variable_table[local_variable_table_length];
   *  }	
   */
  private int nameIndex;
  private int length;
  private ArrayList<Variable> variables;
  private ConstantPool pool;

  public class Variable {

    int startPc;
    int length;
    int nameIndex;
    int descriptorIndex;
    int index;

    public Variable(DataInputStream input) throws IOException {
      startPc = input.readShort();
      length = input.readShort();
      nameIndex = input.readShort();
      descriptorIndex = input.readShort();
      index = input.readShort();
    }

    public void write(DataOutputStream output) throws IOException {
      output.writeShort(startPc);
      output.writeShort(length);
      output.writeShort(nameIndex);
      output.writeShort(descriptorIndex);
      output.writeShort(index);
    }

    public int startPc() {
      return startPc;
    }

    public int length() {
      return length;
    }

    public int nameIndex() {
      return nameIndex;
    }

    public void setNameIndex(int index) {
      nameIndex = index;
    }

    public int descriptorIndex() {
      return descriptorIndex;
    }

    public void setDescriptorIndex(int index) {
      descriptorIndex = index;
    }

    public int index() {
      return index;
    }
  }

  /**
   * Creates a new LocalVariableTableAttribute
   *
   */
  public LocalVariableTableAttribute(InputStream stream, int nameIndex, ConstantPool pool) throws IOException {

    this.pool = pool;
    this.nameIndex = nameIndex;

    Constant c = pool.getConstantAt(nameIndex);

    if (c instanceof UTF8Constant) {
      String name = c.getValue().toString();

      if (!name.equals("LocalVariableTable")) {
        throw new ClassFormatError("While reading a LocalVariableTableAttribute, the name LocalVariableTable was expected, " +
          "but the name " + name + " was encountered.");
      }
    } else {
      throw new ClassFormatError("While reading a LocalVariableTableAttribute, a UTF8Constant was expected, " +
        "but a constant of type " + c.getClass().getName() + " was encountered.");
    }

    DataInputStream input = new DataInputStream(stream);

    // Read the length of the attribute.
    length = input.readInt();

    // Read the variables 
    int numVariables = input.readShort();
    variables = new ArrayList<Variable>(numVariables);

    for (int i = 0; i < numVariables; ++i) {
      variables.add(new Variable(input));
    }
  }

  public void write(DataOutputStream output) throws IOException {
    output.writeShort(nameIndex);
    output.writeInt(length);
    output.writeShort(variables.size());
    for (Variable v : variables) {
      v.write(output);
    }
  }

  public List<Variable> getVariables() {
    return Collections.unmodifiableList(variables);
  }

  public String getName() {
    return pool.getConstantAt(nameIndex).toString();
  }

  /**
   * Returns the length of this Attribute.
   *
   * @return the length of this Attribute
   */
  public int getLength() {
    return length;
  }

  @Override
  public String toString() {
    return getClass().getName();
  }
}

