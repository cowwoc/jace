package jace.parser;

/**
 * A utility class used to perform formatting conversions.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ClassFormatter {

  /**
   * Converts a field descriptor to a type.
   *
   * For example, the field descriptor, "[[Ljava/lang/String;" gets converted to
   * the type, "String[][]".
   */
  String fieldDescriptorToType(String descriptor) {

    /* From the JVM specification:
     *
     * FieldDescriptor -> FieldType
     * ComponentType   -> FieldType
     * FieldType  -> BaseType | ObjectType | ArrayType
     * BaseType   -> B | C | D | F | I | J | S | Z
     * ObjectType -> L<classname>;
     * ArrayType  -> [ComponentType
     */

    if (descriptor.length() == 0) {
      throw new RuntimeException("The descriptor <" + descriptor + "> is malformed. The descriptor is empty.");
    }

    /* First, check to see if the descriptor is a BaseType.
     */
    for (int i = 0; i < TypeMap.MAPS.length; ++i) {
      TypeMap map = TypeMap.MAPS[i];
      if (descriptor.equals(map.type())) {
        return map.name();
      }
    }

    /* Then, check to see if the descriptor is an ObjectType.
     */
    if (descriptor.charAt(0) == 'L') {

      /* Replace all of the '/' characters with '.' characters.
       */
      descriptor = descriptor.replace('/', '.');

      /* Remove the starting 'L' and the trailing ';'
       */
      if (descriptor.charAt(descriptor.length() - 1) != ';') {
        throw new RuntimeException("The descriptor <" + descriptor + "> is malformed. The descriptor is missing a " +
          "trailing ';' character.");
      }

      descriptor = descriptor.substring(1, descriptor.length() - 1);

      return descriptor;
    }

    /* Check to see if this is an ArrayType.
     */
    if (descriptor.charAt(0) == '[') {
      return fieldDescriptorToType(descriptor.substring(1, descriptor.length())) + "[]";
    }

    /* Otherwise, this is a malformed descriptor.
     */
    String msg = "The descriptor <" + descriptor + "> is malformed.";
    throw new RuntimeException(msg);
  }

  /**
   * Tests ClassFormatter.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: ClassFormatter <descriptor>");
      System.out.println("For example: ClassFormatter Ljava/lang/String;");
    }

    ClassFormatter cf = new ClassFormatter();
    System.out.println(cf.fieldDescriptorToType(args[ 0]));
  }

  static class TypeMap {

    public TypeMap(String t, String n) {
      mType = t;
      mName = n;
    }

    public String type() {
      return mType;
    }

    public String name() {
      return mName;
    }
    String mType;
    String mName;
    public final static TypeMap[] MAPS = {
      new TypeMap("I", "int"),
      new TypeMap("D", "double"),
      new TypeMap("F", "float"),
      new TypeMap("C", "char"),
      new TypeMap("S", "short"),
      new TypeMap("Z", "boolean"),
      new TypeMap("B", "byte"),
      new TypeMap("J", "long")
    };
  }
}
