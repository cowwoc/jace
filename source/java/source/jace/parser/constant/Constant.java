
package jace.parser.constant;

import java.io.*;

public interface Constant {
  public int getSize();
  public Object getValue();
  public void write( DataOutputStream output ) throws IOException;
}

