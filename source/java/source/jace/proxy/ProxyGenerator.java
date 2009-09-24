package jace.proxy;

import jace.metaclass.ArrayMetaClass;
import jace.metaclass.MetaClassFilter;
import jace.metaclass.ClassMetaClass;
import jace.metaclass.ClassPackage;
import jace.metaclass.MetaClass;
import jace.metaclass.MetaClassFactory;
import jace.metaclass.JaceConstants;
import jace.metaclass.TypeName;
import jace.metaclass.TypeNameFactory;
import jace.parser.ClassAccessFlag;
import jace.parser.ClassFile;
import jace.parser.field.ClassField;
import jace.parser.field.FieldAccessFlag;
import jace.parser.field.FieldAccessFlagSet;
import jace.parser.method.ClassMethod;
import jace.parser.method.MethodAccessFlag;
import jace.parser.method.MethodAccessFlagSet;
import jace.util.CKeyword;
import jace.util.DelimitedCollection;
import jace.util.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a C++ proxy class for use with the Jace library.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class ProxyGenerator
{
  /**
   * Type of method invocation.
   */
  public enum InvokeStyle
  {
    NORMAL,
    VIRTUAL
  }

  /**
   * Indicates the member accessibility that proxies should expose.
   */
  public enum AccessibilityType
  {
    //
    // WARNING: Do not change the element order! shouldBeSkipped() expects the elements to be ordered in
    // increasing visibility.
    //
    /**
     * Generate public fields and methods.
     */
    PUBLIC,
    /**
     * Generate public and protected fields and methods.
     */
    PROTECTED,
    /**
     * Generate public, protected and package-private fields and methods.
     */
    PACKAGE,
    /**
     * Generate public, protected, package-private and private fields and methods.
     */
    PRIVATE,
  }
  private final static String newLine = System.getProperty("line.separator");
  private final ClassFile classFile;
  private final ClassPath classPath;
  private final AccessibilityType accessibility;
  private final MetaClassFilter dependencyFilter;
  private final boolean exportSymbols;
  private final Logger log = LoggerFactory.getLogger(ProxyGenerator.class);
  /**
   * The names of macros that may conflict with generated code.
   */
  private Collection<String> reservedFields = Arrays.asList(
    new String[]
    {
      "BIG_ENDIAN",
      "LITTLE_ENDIAN",
      "UNDERFLOW",
      "OVERFLOW",
      "minor",
      "TRUE",
      "FALSE",
      "EOF"
    });

  /**
   * Creates a new ProxyGenerator.
   *
   * @param builder an instance of <code>ProxyGenerator.Builder</code>
   */
  private ProxyGenerator(Builder builder)
  {
    assert (builder != null);
    this.classFile = builder.classFile;
    this.accessibility = builder.accessibility;
    this.dependencyFilter = builder.dependencyFilter;
    this.exportSymbols = builder.exportSymbols;
    this.classPath = builder.classPath;
  }

  /**
   * Generates the proxy header.
   *
   * @param output the output writer
   * @throws IOException if an I/O exception occurs while writing the header file
   */
  public void generateHeader(Writer output) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();

    output.write(metaClass.beginGuard() + newLine);
    output.write(newLine);

    includeStandardHeaders(output, false);
    includeDependentHeaders(output);
    makeForwardDeclarations(output);

    generateClassDeclaration(output);
    output.write(newLine);

    output.write(metaClass.endGuard() + newLine);
    output.write(newLine);
  }

  /**
   * Generates the proxy source code.
   *
   * @param output the output writer
   * @throws IOException if an I/O exception occurs while writing the source file
   */
  public void generateSource(Writer output) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();

    output.write(metaClass.include() + newLine);
    output.write(newLine);

    includeStandardSourceHeaders(output);
    output.write(newLine);

    includeDependentSourceHeaders(output);
    output.write(newLine);

    generateClassDefinition(output);
    output.write(newLine);
  }

  /**
   * Generates the source-code includes.
   *
   * @param output the output writer
   * @throws IOException if an I/O exception occurs while writing
   */
  public void includeStandardSourceHeaders(Writer output) throws IOException
  {
    Util.generateComment(output, "Standard Jace headers needed to implement this class.");

    output.write("#ifndef JACE_JARGUMENTS_H" + newLine);
    output.write("#include \"jace/JArguments.h\"" + newLine);
    output.write("#endif" + newLine);
    output.write("using jace::JArguments;" + newLine);
    output.write(newLine);

    output.write("#ifndef JACE_JMETHOD_H" + newLine);
    output.write("#include \"jace/JMethod.h\"" + newLine);
    output.write("#endif" + newLine);
    output.write("using jace::JMethod;" + newLine);
    output.write(newLine);

    output.write("#ifndef JACE_JFIELD_H" + newLine);
    output.write("#include \"jace/JField.h\"" + newLine);
    output.write("#endif" + newLine);
    output.write("using jace::JField;" + newLine + newLine);
    output.write(newLine);

    output.write("#ifndef JACE_JCLASS_IMPL_H" + newLine);
    output.write("#include \"jace/JClassImpl.h\"" + newLine);
    output.write("#endif" + newLine);
    output.write("using jace::JClassImpl;" + newLine);
    output.write(newLine);

    output.write("#pragma warning(push)" + newLine);
    output.write("#pragma warning(disable: 4103 4244 4512)" + newLine);
    output.write("#include <boost/thread/mutex.hpp>" + newLine);
    output.write("#pragma warning(pop)" + newLine);

    output.write(newLine);

    String className = classFile.getClassName().asIdentifier();
    if (className.equals("java.lang.String"))
    {
      output.write(newLine);
      output.write("#include \"jace/proxy/java/lang/Integer.h\"" + newLine);
    }
  }

  /**
   * Generates #include statements for any headers the class depends on.
   *
   * @param output the output writer
   * @throws IOException if an I/O exception occurs while writing
   */
  private void includeDependentSourceHeaders(Writer output) throws IOException
  {
    Util.generateComment(output, "Headers for the classes that this class uses.");

    for (MetaClass dependentMetaClass: getDependentClasses(false))
    {
      // Skip includes for classes that aren't part of our dependency list
      if (!dependencyFilter.accept(dependentMetaClass))
        continue;

      // Hack in special support for org.omg.CORBA.Object
      // This is the only class that causes Jace to break, based on the
      // special way it creates dependencies between subclasses and superclasses.
      //
      // The better way to deal with this would be by handling the case generically,
      // but since we only see one example of this out of over 10,000 cases,
      // it's just not important enough to deal with right now.
      if (classFile.getClassName().asIdentifier().equals("org.omg.CORBA.Object"))
      {
        String dependentName = dependentMetaClass.getSimpleName();
        if (dependentName.equals("DomainManager") || dependentName.equals("Policy"))
          continue;
      }
      output.write(dependentMetaClass.include() + newLine);
      output.write(newLine);
    }
  }

  /**
   * Generates the C++ class definition.
   *
   * @param output the output writer
   * @throws IOException if an I/O exception occurs while writing
   */
  private void generateClassDefinition(Writer output) throws IOException
  {
    beginNamespace(output);
    output.write(newLine);

    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();

    Util.generateComment(output, "The Jace C++ proxy class source for " + classFile.getClassName() + "." + newLine
                                 + "Please do not edit this source, as any changes you make will be overwritten."
                                 + newLine + "For more information, please refer to the Jace Developer's Guide.");

    output.write(getInitializerValue(false) + newLine);
    generateMethodDefinitions(output, false);
    generateFieldDefinitions(output, false);
    generateJaceDefinitions(output, false);
    output.write(newLine);

    endNamespace(output);
    output.write(newLine);

    // Define (if necessary) the ElementProxy specializations
    output.write("BEGIN_NAMESPACE( jace )" + newLine);
    output.write(newLine);
    output.write("#ifndef PUT_TSDS_IN_HEADER" + newLine);
    printElementProxyTsd(output, metaClass);
    output.write("#endif" + newLine);

    // Define (if necessary) the ElementProxy specializations
    output.write("#ifndef PUT_TSDS_IN_HEADER" + newLine);
    printFieldProxyTsd(output, metaClass);
    output.write("#endif" + newLine);
    output.write(newLine);
    output.write("END_NAMESPACE( jace )" + newLine);
  }

  /**
   * Returns true if the class method should be skipped.
   *
   * @param method the class method
   * @return true if the class method should be skipped
   */
  private boolean shouldBeSkipped(ClassMethod method)
  {
    MethodAccessFlagSet flagSet = method.getAccessFlags();

    // skip the method if it is a BRIDGE method
    if ((flagSet.getValue() & 0x0040) != 0)
      return true;

    // if the method is public, we always generate it
    if (flagSet.contains(MethodAccessFlag.PUBLIC))
      return false;

    if (flagSet.contains(MethodAccessFlag.PROTECTED))
      return accessibility.compareTo(AccessibilityType.PROTECTED) < 0;

    if (flagSet.contains(MethodAccessFlag.PRIVATE))
      return accessibility.compareTo(AccessibilityType.PRIVATE) < 0;

    // method is package-private
    return accessibility.compareTo(AccessibilityType.PACKAGE) < 0;
  }

  /**
   * Returns true if the class field should be skipped.
   *
   * @param field the class field
   * @return true if the class field should be skipped
   */
  private boolean shouldBeSkipped(ClassField field)
  {
    FieldAccessFlagSet flagSet = field.getAccessFlags();

    // if the field is public, we always generate it
    if (flagSet.contains(FieldAccessFlag.PUBLIC))
      return false;


    if (flagSet.contains(FieldAccessFlag.PROTECTED))
      return accessibility.compareTo(AccessibilityType.PROTECTED) < 0;

    if (flagSet.contains(FieldAccessFlag.PRIVATE))
      return accessibility.compareTo(AccessibilityType.PRIVATE) < 0;

    // method is package-private
    return accessibility.compareTo(AccessibilityType.PACKAGE) < 0;
  }

  /**
   * Generate the method definitions.
   *
   * @param output the output writer
   * @param forPeer true if the methods are being generated for a peer, false for a proxy
   * @throws IOException if an error occurs while writing
   */
  public void generateMethodDefinitions(Writer output, boolean forPeer) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    String className = metaClass.getSimpleName();

    // go through all of the methods
    for (ClassMethod method: classFile.getMethods())
    {
      if (shouldBeSkipped(method))
        continue;

      if (!isPartOfDependencies(method))
        continue;

      MetaClass returnType = MetaClassFactory.getMetaClass(method.getReturnType()).proxy();
      String methodName = method.getName();

      // Hack in special support for org.omg.CORBA.Object
      // This is the only class that causes Jace to break, based on the
      // special way it creates dependencies between subclasses and superclasses.
      //
      // The better way to deal with this would be by handling the case generically,
      // but since we only see one example of this out of over 10,000 cases,
      // it's just not important enough to deal with right now.
      if (classFile.getClassName().asIdentifier().equals("org.omg.CORBA.Object"))
      {
        if (methodName.equals("_get_policy") || methodName.equals("_get_domain_managers") || methodName.equals(
          "_set_policy_override"))
        {
          continue;
        }
      }

      // If this is a static initializer, then we don't need to declare it
      if (methodName.equals("<clinit>"))
      {
        continue;
      }

      boolean isConstructor = methodName.equals("<init>");
      MethodAccessFlagSet accessFlagSet = method.getAccessFlags();

      // skip the methods that we don't need to be generating for a Peer
      if (forPeer)
      {
        if (isConstructor || methodName.equals("jaceUserStaticInit") || methodName.equals("jaceUserClose")
            || methodName.equals("jaceUserFinalize") || methodName.equals("jaceSetNativeHandle") || methodName.equals(
          "jaceGetNativeHandle") || methodName.equals("jaceCreateInstance") || methodName.equals("jaceDestroyInstance")
            || methodName.equals("jaceDispose") || accessFlagSet.contains(MethodAccessFlag.NATIVE))
        {
          continue;
        }
      }

      // If this is a constructor, there is no return-type
      if (isConstructor)
        output.write(className + "::" + className);
      else
      {
        // handle clashes between C++ keywords and java identifiers
        methodName = CKeyword.adjust(methodName);

        if (returnType.getSimpleName().equals("JVoid"))
          output.write("void");
        else
          output.write("::" + returnType.getFullyQualifiedName("::"));
        output.write(" " + className + "::" + methodName);
      }
      output.write("(");

      List<TypeName> parameterTypes = method.getParameterTypes();
      DelimitedCollection<TypeName> parameterList = new DelimitedCollection<TypeName>(parameterTypes);
      DelimitedCollection.Stringifier<TypeName> sf = new DelimitedCollection.Stringifier<TypeName>()
      {
        int current = 0;

        @Override
        public String toString(TypeName typeName)
        {
          MetaClass mc = MetaClassFactory.getMetaClass(typeName).proxy();
          return "::" + mc.getFullyQualifiedName("::") + " p" + current++;
        }
      };
      String parameterListStr = parameterList.toString(sf, ", ");

      // If this is a constructor, and it is a 'java copy constructor' (it takes only one
      // argument which is the same type as this class) we need to change the signature so
      // that it takes a 'CopyConstructorSpecifier' in addition. That allows us to avoid
      // confusion with C++ compilers that would think we were defining a C++ copy constructor
      // that took its argument by value.
      if (isConstructor && parameterTypes.size() == 1)
      {
        MetaClass argType = MetaClassFactory.getMetaClass(parameterTypes.iterator().next()).proxy();
        if (argType.equals(metaClass))
        {
          parameterListStr += ", CopyConstructorSpecifier";
        }
      }

      if (!parameterListStr.equals(""))
        parameterListStr = " " + parameterListStr + " ";

      output.write(parameterListStr);
      output.write(") ");

      // If this is a constructor, we need to handle it differently from other methods
      if (isConstructor)
      {
        // initialize parent classes
        output.write(getInitializerName() + newLine);
        output.write("{" + newLine);

        // initialize any arguments we have
        output.write("  ::jace::JArguments arguments;" + newLine);

        if (parameterTypes.size() > 0)
        {
          output.write("  arguments");
          for (int i = 0; i < parameterTypes.size(); ++i)
            output.write(" << p" + i);
          output.write(";" + newLine);
        }

        // set the jni object for this c++ object to the result of the call to newObject
        output.write("  jobject localRef = newObject( arguments );" + newLine);
        output.write("  setJavaJniObject( localRef );" + newLine);
        output.write("  JNIEnv* env = ::jace::helper::attach();" + newLine);
        output.write("  ::jace::helper::deleteLocalRef( env, localRef );" + newLine);
      }
      else
      {
//        if (!accessFlagSet.contains(MethodAccessFlag.STATIC))
//          output.write( "const " + newLine);
        output.write("{" + newLine);

        // Initialize any arguments we have
        output.write("  ::jace::JArguments arguments;" + newLine);

        if (parameterTypes.size() > 0)
        {
          output.write("  arguments");
          for (int i = 0; i < parameterTypes.size(); ++i)
          {
            output.write(" << p" + i);
          }
          output.write(";" + newLine);
        }

        // If this is a non-void method call we need to return the result of the method call
        output.write("  ");

        if (!returnType.getSimpleName().equals("JVoid"))
          output.write("return ");

        output.write("::jace::JMethod< ");
        output.write("::" + returnType.getFullyQualifiedName("::"));
        output.write(" >");
        output.write("( \"" + methodName + "\" ).invoke( ");

        // If this method is static, we need to provide the class info, otherwise we provide a reference to itself.
        if (method.getAccessFlags().contains(MethodAccessFlag.STATIC))
          output.write("staticGetJavaJniClass()");
        else
          output.write("*this");
        output.write(", arguments );" + newLine);
      }
      output.write("}" + newLine);
      output.write(newLine);
    }

    // Now define the special "one-off" methods that we add to classes like,
    // Object, String, and Throwable to provide better C++ and Java integration.
    String fullyQualifiedName = classFile.getClassName().asIdentifier();
    if (fullyQualifiedName.equals("java.lang.Object"))
    {
      Util.generateComment(output, "Provide the standard \"System.out.println()\" semantics for ostreams.");
      output.write("std::ostream& operator<<( std::ostream& out, Object& object )" + newLine);
      output.write("{" + newLine);
      output.write("  out << object.toString();" + newLine);
      output.write("  return out;" + newLine);
      output.write("}" + newLine);
    }
    else if (fullyQualifiedName.equals("java.lang.String"))
    {
      output.write("String::String( const std::string& str ) : Object( NO_OP )" + newLine);
      output.write("{" + newLine);
      output.write("  jstring strRef = createString( str );" + newLine);
      output.write("  setJavaJniObject( strRef );" + newLine);
      output.write("  JNIEnv* env = ::jace::helper::attach();" + newLine);
      output.write("  ::jace::helper::deleteLocalRef( env, strRef );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("String::String( const std::wstring& str ) : Object( NO_OP )" + newLine);
      output.write("{" + newLine);
      output.write("  JNIEnv* env = ::jace::helper::attach();" + newLine);
      output.write("  size_t nativeLength = str.size();" + newLine);
      output.write("  if (nativeLength > static_cast<size_t>(::jace::proxy::java::lang::Integer::MAX_VALUE())) { "
                   + newLine);
      output.write("    throw ::jace::JNIException( std::string(\"String::String ( const std::wstring& str ) - "
                   + "str.size() (\") +" + newLine);
      output.write("      jace::helper::toString(nativeLength) + \") > Integer.MAX_VALUE.\" );" + newLine);
      output.write("  }" + newLine);
      output.write("  jsize length = jsize( str.size() );" + newLine);
      output.write("  jstring strRef = env->NewString( reinterpret_cast<const jchar*>( str.c_str() ), length );"
                   + newLine);
      output.write("  setJavaJniObject( strRef );" + newLine);
      output.write("  ::jace::helper::deleteLocalRef( env, strRef );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("String::String( const char* str ) : Object( NO_OP )" + newLine);
      output.write("{" + newLine);
      output.write("  jstring strRef = createString( str );" + newLine);
      output.write("  setJavaJniObject( strRef );" + newLine);
      output.write("  JNIEnv* env = ::jace::helper::attach();" + newLine);
      output.write("  ::jace::helper::deleteLocalRef( env, strRef );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("String& String::operator=( const String& str )" + newLine);
      output.write("{" + newLine);
      output.write("  setJavaJniObject( str.getJavaJniObject() );" + newLine);
      output.write("  return *this;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("String::operator std::string() const" + newLine);
      output.write("{" + newLine);
      output.write("  JNIEnv* env = helper::attach();" + newLine);
      output.write("  jstring thisString = static_cast<jstring>( getJavaJniObject() );" + newLine);
      output.write("  jclass cls = getJavaJniClass().getClass();" + newLine);
      output.write("  jmethodID getBytes = env->GetMethodID( cls, \"getBytes\", \"()[B\" );" + newLine);
      output.write("  jbyteArray array = static_cast<jbyteArray>( env->CallObjectMethod( thisString, getBytes ) );"
                   + newLine);
      output.write(newLine);
      output.write("  if ( !array )" + newLine);
      output.write("  {" + newLine);
      output.write("    env->ExceptionDescribe();" + newLine);
      output.write("    env->ExceptionClear();" + newLine);
      output.write("    throw ::jace::JNIException( \"String::operator std::string()- Unable to get the contents of the java String.\" );"
                   + newLine);
      output.write("  }" + newLine);
      output.write(newLine);
      output.write("  int arraySize = env->GetArrayLength( array );" + newLine);
      output.write("  jbyte* byteArray = env->GetByteArrayElements( array, 0 );" + newLine);
      output.write(newLine);
      output.write("  if ( !byteArray )" + newLine);
      output.write("  {" + newLine);
      output.write("    env->ExceptionDescribe();" + newLine);
      output.write("    env->ExceptionClear();" + newLine);
      output.write("    throw ::jace::JNIException( \"String::operator std::string() - Unable to get the "
                   + "contents of the java String.\" );" + newLine);
      output.write("  }" + newLine);
      output.write(newLine);
      output.write("  std::string str( ( char* ) byteArray, ( char* ) byteArray + arraySize );" + newLine);
      output.write("  env->ReleaseByteArrayElements( array, byteArray, JNI_ABORT );" + newLine);
      output.write("  ::jace::helper::deleteLocalRef( env, array );" + newLine);
      output.write("  return str;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("String::operator std::wstring() const" + newLine);
      output.write("{" + newLine);
      output.write("  JNIEnv* env = helper::attach();" + newLine);
      output.write("  jstring thisString = static_cast<jstring>( getJavaJniObject() );" + newLine);
      output.write("  const jchar* buffer = env->GetStringChars(thisString, 0);" + newLine);
      output.write("  if ( !buffer )" + newLine);
      output.write("  {" + newLine);
      output.write("    env->ExceptionDescribe();" + newLine);
      output.write("    env->ExceptionClear();" + newLine);
      output.write("    throw ::jace::JNIException( \"String::operator std::wstring() - Unable to get the "
                   + "contents of the java String.\" );" + newLine);
      output.write("  }" + newLine);
      output.write("  std::wstring result = reinterpret_cast<const wchar_t*>(buffer);" + newLine);
      output.write("  env->ReleaseStringChars(thisString, buffer);" + newLine);
      output.write("  return result;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("jstring String::createString( const std::string& str )" + newLine);
      output.write("{" + newLine);
      output.write("  JNIEnv* env = helper::attach();" + newLine);
      output.write("  size_t nativeLength = str.size();" + newLine);
      output.write("  if (nativeLength > static_cast<size_t>(::jace::proxy::java::lang::Integer::MAX_VALUE())) { "
                   + newLine);
      output.write("    throw ::jace::JNIException( std::string(\"String::String ( const std::string& str ) - "
                   + "str.size() (\") +" + newLine);
      output.write("      jace::helper::toString(nativeLength) + \") > Integer.MAX_VALUE.\" );" + newLine);
      output.write("  }" + newLine);
      output.write("  jsize bufLen = jsize( nativeLength );" + newLine);
      output.write("  jbyteArray jbuf = env->NewByteArray( bufLen );" + newLine + newLine);
      output.write("  if ( !jbuf )" + newLine);
      output.write("  {" + newLine);
      output.write("    env->ExceptionDescribe();" + newLine);
      output.write("    env->ExceptionClear();" + newLine);
      output.write("    throw ::jace::JNIException( \"String::createString - Unable to allocate a new java String.\" );"
                   + newLine);
      output.write("  }" + newLine);
      output.write(newLine);
      output.write("  env->SetByteArrayRegion( jbuf, 0, bufLen, ( jbyte* ) str.c_str() );" + newLine);
      output.write("  jclass cls = getJavaJniClass().getClass();" + newLine);
      output.write("  jmethodID init = env->GetMethodID( cls, \"<init>\", \"([BII)V\");" + newLine);
      output.write("  jstring jstr = ( jstring ) env->NewObject( cls, init, jbuf, 0, bufLen); " + newLine);
      output.write(newLine);
      output.write("  if ( !jstr )" + newLine);
      output.write("  {" + newLine);
      output.write("    env->ExceptionDescribe();" + newLine);
      output.write("    env->ExceptionClear();" + newLine);
      output.write("    throw ::jace::JNIException( \"String::createString - Unable to allocate a new java String.\" );"
                   + newLine);
      output.write("  }" + newLine);
      output.write(newLine);
      output.write("  ::jace::helper::deleteLocalRef( env, jbuf );" + newLine);
      output.write("  return jstr;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("std::ostream& operator<<( std::ostream& stream, const String& str )" + newLine);
      output.write("{" + newLine);
      output.write("  return stream << (std::string) str;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("std::string operator+( const std::string& stdStr, const String& jStr )" + newLine);
      output.write("{" + newLine);
      output.write("  return stdStr + (std::string) jStr;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("std::string operator+( const String& jStr, const std::string& stdStr )" + newLine);
      output.write("{" + newLine);
      output.write("  return (std::string) jStr + stdStr;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("String String::operator+( String str )" + newLine);
      output.write("{" + newLine);
      output.write("  return (std::string) *this + (std::string) str;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("bool operator==( const std::string& stdStr, const String& str )" + newLine);
      output.write("{" + newLine);
      output.write("  return (std::string) str == stdStr;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write("bool operator==( const String& str, const std::string& stdStr )" + newLine);
      output.write("{" + newLine);
      output.write("  return (std::string) str == stdStr;" + newLine);
      output.write("}" + newLine);
      output.write(newLine);
    }
    else if (fullyQualifiedName.equals("java.lang.Throwable"))
    {
      output.write("Throwable::~Throwable() throw ()" + newLine);
      output.write("{}" + newLine);
      output.write(newLine);

      output.write("const char* Throwable::what() const throw()" + newLine);
      output.write("{" + newLine);
      output.write("  // JACE really isn't const correct like it should be, yet." + newLine);
      output.write("  // For now, the easiest way to get around this is to cast constness away." + newLine);
      output.write("  Throwable* t = const_cast<Throwable*>( this );" + newLine);
      output.write(newLine);
      output.write("  /* Get the string contents of this exception." + newLine);
      output.write("   */" + newLine);
      output.write("  t->msg = t->toString();" + newLine);
      output.write(newLine);
      output.write("  /* Return a handle to the msg." + newLine);
      output.write("   */" + newLine);
      output.write("  return t->msg.c_str();" + newLine);
      output.write("}" + newLine);
      output.write(newLine);
    }
  }

  /**
   * Same as generateFieldDefinitions(output, false).
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   * @see generateFieldDefinitions(Writer, boolean)
   */
  public void generateFieldDefinitions(Writer output) throws IOException
  {
    generateFieldDefinitions(output, false);
  }

  /**
   * Generate the field definitions.
   *
   * @param output the output writer
   * @param forPeer true if the fields are being generated for a peer, false for a proxy
   * @throws IOException if an error occurs while writing
   */
  public void generateFieldDefinitions(Writer output, boolean forPeer) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    String className = metaClass.getSimpleName();

    Collection<String> methodNames = new ArrayList<String>();
    for (ClassMethod method: classFile.getMethods())
    {
      if (shouldBeSkipped(method))
        continue;
      methodNames.add(CKeyword.adjust(method.getName()));
    }

    for (ClassField field: classFile.getFields())
    {
      if (shouldBeSkipped(field))
        continue;
      MetaClass mc = MetaClassFactory.getMetaClass(field.getDescriptor()).proxy();

      // Don't generate the field if it's type is not part of our dependency list
      if (!dependencyFilter.accept(mc))
        continue;

      String name = field.getName();
      if (forPeer && name.equals("jaceNativeHandle"))
        continue;

      // handle clashes between C++ keywords and java identifiers
      name = CKeyword.adjust(name);

      // handle clashes with method names by appending an underscore to the field name
      if (methodNames.contains(name))
      {
        name = name + "_";
      }

      // handle clashes with "reserved fields"
      if (reservedFields.contains(name))
      {
        name += "_Jace";
      }

      String fieldType = "::jace::JField< " + "::" + mc.getFullyQualifiedName("::") + " >";
      String proxyType = "::jace::JFieldProxy< " + "::" + mc.getFullyQualifiedName("::") + " >";
      FieldAccessFlagSet accessFlagSet = field.getAccessFlags();

      Util.generateComment(output, accessFlagSet.getName() + " " + name);

      String modifiers = "";

      // can't call non-const methods on const fields. There's probably a better way of going about doing this.
      //
      //if (accessFlagSet.contains(FieldAccessFlag.FINAL))
      //  modifiers = modifiers + "const ";

      output.write(modifiers + proxyType + " " + className + "::" + name + "()" + newLine);
      output.write("{" + newLine);
      output.write("  return " + fieldType + "( \"" + name + "\" ).get( ");

      // if this field is static, we need to provide the class info, otherwise we provide a reference to itself
      if (accessFlagSet.contains(FieldAccessFlag.STATIC))
        output.write("staticGetJavaJniClass()");
      else
        output.write("*this");

      output.write(" );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);
    }
  }

  /**
   * Same as generateJaceDefinitions(output, false).
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   * @see generateJaceDefinitions(Writer, boolean).
   */
  public void generateJaceDefinitions(Writer output) throws IOException
  {
    generateJaceDefinitions(output, false);
  }

  /**
   * Generate the jace-specific methods.
   *
   * @param output the output writer
   * @param forPeer true if the fields are being generated for a peer, false for a proxy
   * @throws IOException if an error occurs while writing
   */
  public void generateJaceDefinitions(Writer output, boolean forPeer) throws IOException
  {
    MetaClass classMetaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    String className = classMetaClass.getSimpleName();

    Util.generateComment(output, "The following methods are required to integrate this class" + newLine
                                 + "with the Jace framework.");

    if (!forPeer)
    {
      // If we're an interface, we declare a no argument constructor that delegates to Object( NO_OP )
      // to make virtual inheritance of interfaces easier.
      if (classFile.getAccessFlags().contains(ClassAccessFlag.INTERFACE))
      {
        Util.generateComment(output, "Special no arg constructor for interface virtual inheritance");
        output.write(className + "::" + className + "() : Object( NO_OP ) { " + newLine);
        output.write("}" + newLine);
        output.write(newLine);
      }

      output.write(className + "::" + className + "( jvalue value ) " + getInitializerName() + newLine);
      output.write("{" + newLine);
      output.write("  setJavaJniValue( value );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write(className + "::" + className + "( jobject object ) " + getInitializerName() + newLine);
      output.write("{" + newLine);
      output.write("  setJavaJniObject( object );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write(className + "::" + className + "( const " + className + "& object ) " + getInitializerName()
                   + newLine);
      output.write("{" + newLine);
      output.write("  setJavaJniObject( object.getJavaJniObject() );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write(className + "::" + className + "( const NoOp& ) " + getInitializerName() + newLine);
      output.write("{}" + newLine);
      output.write(newLine);
    }

    if (forPeer)
    {
      output.write(className + "::" + className + "( jobject jPeer ) " + getInitializerName() + newLine);
      output.write("{" + newLine);
      output.write("  setJavaJniObject( jPeer );" + newLine);
      output.write("}" + newLine);
      output.write(newLine);

      output.write(className + "::~" + className + "() throw ()" + newLine);
      output.write("{}" + newLine);
      output.write(newLine);
    }

    output.write("static boost::mutex javaClassMutex;" + newLine);
    output.write("const JClass& " + className + "::staticGetJavaJniClass() throw ( ::jace::JNIException )" + newLine);
    output.write("{" + newLine);
    output.write("  static boost::shared_ptr<JClassImpl> result;" + newLine);
    output.write("  boost::mutex::scoped_lock(javaClassMutex);" + newLine);
    output.write("  if (result == 0)" + newLine);
    output.write("    result = boost::shared_ptr<JClassImpl>(new JClassImpl(\"" + classFile.getClassName() + "\"));"
                 + newLine);
    output.write("  return *result;" + newLine);
    output.write("}" + newLine);
    output.write(newLine);

    output.write("const JClass& " + className + "::getJavaJniClass() const throw ( ::jace::JNIException )" + newLine);
    output.write("{" + newLine);
    output.write("  return " + className + "::staticGetJavaJniClass();" + newLine);
    output.write("}" + newLine);
    output.write(newLine);

    if (forPeer)
    {
      output.write(classMetaClass.getFullyQualifiedName("::") + " " + className + "::getJaceProxy()" + newLine);
      output.write("{" + newLine);
      output.write("  return " + classMetaClass.getFullyQualifiedName("::") + "(getJavaJniObject());" + newLine);
      output.write("}" + newLine);
      output.write(newLine);
    }

    try
    {
      if (!forPeer && isException(classFile.getClassName()))
        output.write("JEnlister<" + className + "> " + className + "::enlister;" + newLine);
    }
    catch (ClassNotFoundException e)
    {
      throw new IOException(e);
    }
  }

  /**
   * Indicates if a class is an exception.
   *
   * @param className the class name
   * @return true if the class is an exception
   * @throws ClassNotFoundException if the class was not found
   */
  private boolean isException(TypeName className) throws ClassNotFoundException
  {
    ClassFile c = new ClassFile(classPath.openClass(className));
    while (true)
    {
      if (c.getClassName().asIdentifier().equals("java.lang.Throwable"))
        return true;
      TypeName superName = c.getSuperClassName();
      if (superName == null)
        return false;
      c = new ClassFile(classPath.openClass(superName));
    }
  }

  /**
   * Same as getInitializerValue(false).
   *
   * @return the initializer list
   * @see defineInitializerValue(boolean)
   */
  public String getInitializerValue()
  {
    return getInitializerValue(false);
  }

  /**
   * Returns the initializer list for the current class.
   *
   * For example, ": Object( NO_OP ), OutputStream( NO_OP )"
   *
   * This could probably be placed in MetaClass. It might not hurt to
   * put the initializers in some sort of array instead of a formatted
   * String.
   *
   * This method used to include interfaces in the initializer list,
   * but now that interfaces have a default constructor that does what
   * we want, they are no longer included.
   *
   * @param forPeer true if the methods are being generated for a peer, false for a proxy
   * @return the initializer list
   */
  public String getInitializerValue(boolean forPeer)
  {
    TypeName objectName = TypeNameFactory.fromPath("java/lang/Object");
    String objectConstructor = "::" + MetaClassFactory.getMetaClass(objectName).proxy().getFullyQualifiedName("::")
                               + "( NO_OP )";

    TypeName superName = classFile.getSuperClassName();

    String initializerName = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy().getSimpleName()
                             + "_INITIALIZER";

    StringBuilder definition = new StringBuilder("#ifndef VIRTUAL_INHERITANCE_IS_BROKEN" + newLine);

    if (classFile.getClassName().equals(objectName))
    {
      definition.append("  #define " + initializerName + newLine);
      definition.append("#else" + newLine);
      definition.append("  #define " + initializerName + newLine);
    }
    else if (superName.equals(objectName))
    {
      definition.append("  #define " + initializerName + " : " + objectConstructor);
      if (forPeer)
        definition.append(", ::jace::Peer( jPeer )");
      definition.append(newLine);
      definition.append("#else" + newLine);
      definition.append("  #define " + initializerName);
      if (forPeer)
        definition.append(" : ::jace::Peer( jPeer )");
      definition.append(newLine);
    }
    else
    {
      assert (superName != null): classFile.getClassName();

      Collection<String> constructors = new ArrayList<String>();
      constructors.add("::" + MetaClassFactory.getMetaClass(superName).proxy().getFullyQualifiedName("::")
                       + "( NO_OP )");
      constructors.add(objectConstructor);
      if (forPeer)
        constructors.add("::jace::Peer( jPeer )");
      DelimitedCollection<String> delimited = new DelimitedCollection<String>(constructors);

      definition.append("  #define " + initializerName + " : " + delimited.toString(", ") + newLine);
      definition.append("#else" + newLine);

      constructors.remove(objectConstructor);
      delimited = new DelimitedCollection<String>(constructors);

      definition.append("  #define " + initializerName + " : " + delimited.toString(", ") + newLine);
    }
    definition.append("#endif" + newLine);
    return definition.toString();
  }

  /**
   * Returns the name of the initializer list.
   *
   * For example, "java/lang/Cloneable_INITIALIZER"
   *
   * @return the name of the intializer list
   */
  private String getInitializerName()
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    return metaClass.getSimpleName() + "_INITIALIZER";
  }

  /**
   * Generate the class file declaration.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void generateClassDeclaration(Writer output) throws IOException
  {
    beginNamespace(output);
    output.write(newLine);

    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();

    Util.generateComment(output, "The Jace C++ proxy class for " + classFile.getClassName().asIdentifier() + "."
                                 + newLine + "Please do not edit this class, as any changes you make will be "
                                 + "overwritten." + newLine
                                 + "For more information, please refer to the Jace Developer's Guide.");

    output.write("class ");
    output.write(metaClass.getSimpleName());
    output.write(" : ");

    // Write out the super classes.
    output.write("public ");

    TypeName superName = classFile.getSuperClassName();
    if (superName == null)
      output.write("virtual JObject");
    else
    {
      // if we are derived directly from java.lang.Object, we need to derive from it virtually
      if (superName.asIdentifier().equals("java.lang.Object"))
        output.write("virtual ");
      MetaClass superMetaClass = MetaClassFactory.getMetaClass(superName).proxy();
      output.write("::" + superMetaClass.getFullyQualifiedName("::"));

      // We add in special support for java.lang.Throwable, because we want it to derive from std::exception.
      if (classFile.getClassName().asIdentifier().equals("java.lang.Throwable"))
        output.write(", public std::exception");

      // now, add all of the interfaces that are implemented
      for (TypeName i: classFile.getInterfaces())
      {
        MetaClass interfaceClass = MetaClassFactory.getMetaClass(i).proxy();
        output.write(", public virtual ");
        output.write("::" + interfaceClass.getFullyQualifiedName("::"));
      }
    }
    output.write(newLine);

    output.write("{" + newLine);
    output.write("public: " + newLine);
    output.write(newLine);

    generateMethodDeclarations(output);
    generateFieldDeclarations(output, false);
    generateJaceDeclarations(output);

    output.write("};" + newLine);
    output.write(newLine);

    endNamespace(output);
    output.write(newLine);

    // declare (and if necessary define) the ElementProxy specialization
    String fullName = "::" + metaClass.getFullyQualifiedName("::");

    output.write("BEGIN_NAMESPACE( jace )" + newLine);
    output.write(newLine);
    output.write("#ifndef PUT_TSDS_IN_HEADER" + newLine);
    output.write("  template <> ElementProxy< " + fullName
                 + ">::ElementProxy( jarray array, jvalue element, int index );" + newLine);
    output.write("  template <> ElementProxy< " + fullName + ">::ElementProxy( const jace::ElementProxy< " + fullName
                 + ">& proxy );" + newLine);
    output.write("#else" + newLine);
    printElementProxyTsd(output, metaClass);
    output.write("#endif" + newLine);
    output.write(newLine);

    // declare (and if necessary define) the FieldProxy specialization
    output.write("#ifndef PUT_TSDS_IN_HEADER" + newLine);
    output.write("  template <> JFieldProxy< " + fullName
                 + ">::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ );" + newLine);
    output.write("  template <> JFieldProxy< " + fullName
                 + ">::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ );" + newLine);
    output.write("  template <> JFieldProxy< " + fullName + ">::JFieldProxy( const ::jace::JFieldProxy< " + fullName
                 + ">& object );" + newLine);
    output.write("#else" + newLine);
    printFieldProxyTsd(output, metaClass);
    output.write("#endif" + newLine);
    output.write(newLine);
    output.write("END_NAMESPACE( jace )" + newLine);
  }

  /**
   * Generates the ElementProxy template specialization declaration.
   *
   * @param output the output writer
   * @param mc the class meta-data
   * @throws IOException if an error occurs while writing
   */
  private void printElementProxyTsd(Writer output, MetaClass mc) throws IOException
  {
    String name = "::" + mc.getFullyQualifiedName("::");

    // normal constructor
    output.write("  template <> inline ElementProxy< " + name
                 + ">::ElementProxy( jarray array, jvalue element, int index ) : " + newLine);

    if (mc.getFullyQualifiedName("/").equals(JaceConstants.getProxyPackage().asPath() + "/java/lang/Object"))
      output.write("    Object( element ), mIndex( index )");
    else
      output.write("    " + name + "( element ), Object( NO_OP ), mIndex( index )");
    output.write(newLine);

    output.write("  {" + newLine);
    output.write("    JNIEnv* env = ::jace::helper::attach();" + newLine);
    output.write("    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, array ) );" + newLine);
    output.write("  }" + newLine);

    // copy constructor
    output.write("  template <> inline ElementProxy< " + name + ">::ElementProxy( const jace::ElementProxy< " + name
                 + ">& proxy ) : " + newLine);

    if (mc.getFullyQualifiedName("/").equals(JaceConstants.getProxyPackage().asPath() + "/java/lang/Object"))
      output.write("    Object( proxy.getJavaJniObject() ), mIndex( proxy.mIndex )");
    else
      output.write("    " + name + "( proxy.getJavaJniObject() ), Object( NO_OP ), mIndex( proxy.mIndex )");
    output.write(newLine);

    output.write("  {" + newLine);
    output.write("    JNIEnv* env = ::jace::helper::attach();" + newLine);
    output.write("    parent = static_cast<jarray>( ::jace::helper::newGlobalRef( env, proxy.parent ) );" + newLine);
    output.write("  }" + newLine);
  }

  /**
   * Generate the JFieldProxy template specification declaration.
   *
   * @param output the output writer
   * @param mc the class meta-data
   * @throws IOException if an error occurs while writing
   */
  private void printFieldProxyTsd(Writer output, MetaClass mc) throws IOException
  {
    String name = "::" + mc.getFullyQualifiedName("::");

    // normal constructor
    output.write("  template <> inline JFieldProxy< " + name
                 + ">::JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) : " + newLine);

    if (mc.getFullyQualifiedName("/").equals(JaceConstants.getProxyPackage().asPath() + "/java/lang/Object"))
      output.write("    Object( value ), fieldID( fieldID_ )");
    else
      output.write("    " + name + "( value ), Object( NO_OP ), fieldID( fieldID_ )");
    output.write(newLine);

    output.write("  {" + newLine);
    output.write("    JNIEnv* env = ::jace::helper::attach();" + newLine);
    output.write(newLine);
    output.write("    if ( parent_ )" + newLine);
    output.write("      parent = ::jace::helper::newGlobalRef( env, parent_ );" + newLine);
    output.write("    else" + newLine);
    output.write("      parent = parent_;" + newLine);
    output.write(newLine);
    output.write("    parentClass = 0;" + newLine);
    output.write("  }" + newLine);

    // static normal constructor
    output.write("  template <> inline JFieldProxy< " + name
                 + ">::JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) : " + newLine);

    if (mc.getFullyQualifiedName("/").equals(JaceConstants.getProxyPackage().asPath() + "/java/lang/Object"))
      output.write("    Object( value ), fieldID( fieldID_ )");
    else
      output.write("    " + name + "( value ), Object( NO_OP ), fieldID( fieldID_ )");
    output.write(newLine);

    output.write("  {" + newLine);
    output.write("    JNIEnv* env = ::jace::helper::attach();" + newLine);
    output.write(newLine);
    output.write("    parent = 0;" + newLine);
    output.write("    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, parentClass_ ) );" + newLine);
    output.write("  }" + newLine);

    // copy constructor
    output.write("  template <> inline JFieldProxy< " + name + ">::JFieldProxy( const ::jace::JFieldProxy< " + name
                 + ">& object ) : " + newLine);

    if (mc.getFullyQualifiedName("/").equals(JaceConstants.getProxyPackage().asPath() + "/java/lang/Object"))
      output.write("    Object( object.getJavaJniValue() )");
    else
      output.write("    " + name + "( object.getJavaJniValue() ), Object( NO_OP )");
    output.write(newLine);

    output.write("  {" + newLine);
    output.write("    fieldID = object.fieldID; " + newLine);
    output.write(newLine);
    output.write("    if ( object.parent )" + newLine);
    output.write("    {" + newLine);
    output.write("      JNIEnv* env = ::jace::helper::attach();" + newLine);
    output.write("      parent = ::jace::helper::newGlobalRef( env, object.parent );" + newLine);
    output.write("    }" + newLine);
    output.write("    else" + newLine);
    output.write("      parent = 0;" + newLine);
    output.write(newLine);
    output.write("    if ( object.parentClass )" + newLine);
    output.write("    {" + newLine);
    output.write("      JNIEnv* env = ::jace::helper::attach();" + newLine);
    output.write("      parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, object.parentClass ) );"
                 + newLine);
    output.write("    }" + newLine);
    output.write("    else" + newLine);
    output.write("      parentClass = 0;" + newLine);
    output.write("  }" + newLine);
  }

  /**
   * Indicates if the method parameters and return-type are part of the dependency listing.
   *
   * @param method the method
   * @return true if the method parameters and return-type are part of the dependency listing
   */
  private boolean isPartOfDependencies(ClassMethod method)
  {
    if (dependencyFilter instanceof AcceptAll)
      return true;

    String methodName = method.getName();
    boolean isConstructor = methodName.equals("<init>");

    if (!isConstructor)
    {
      MetaClass returnType = MetaClassFactory.getMetaClass(method.getReturnType()).proxy();
      if (returnType instanceof ArrayMetaClass)
        returnType = ((ArrayMetaClass) returnType).getInnermostElementType();

      if (!dependencyFilter.accept(returnType))
      {
        log.debug("ReturnType: " + returnType + " not in dependency list");
        return false;
      }
    }

    for (TypeName parameter: method.getParameterTypes())
    {
      MetaClass parameterType = MetaClassFactory.getMetaClass(parameter).proxy();

      if (parameterType instanceof ArrayMetaClass)
        parameterType = ((ArrayMetaClass) parameterType).getInnermostElementType();

      if (!dependencyFilter.accept(parameterType))
      {
        log.debug("ParameterType: " + parameterType + " not in dependency list");
        return false;
      }
    }
    return true;
  }

  /**
   * Same as generateMethodDeclaration(output, metaClass, method, InvokeStyle.NORMAL).
   *
   * @param output the output writer
   * @param metaClass the MetaClass describing the class
   * @param method the method
   * @throws IOException if an error occurs while writing
   * @see generateMethodDeclaration(Writer, MetaClass, ClassMethod, InvokeStyle)
   */
  public void generateMethodDeclaration(Writer output, MetaClass metaClass, ClassMethod method) throws
    IOException
  {
    generateMethodDeclaration(output, metaClass, method, InvokeStyle.NORMAL);
  }

  /**
   * Generates the method declaration.
   *
   * @param output the output writer
   * @param metaClass the MetaClass describing the class
   * @param method the method
   * @param invokeStyle the method invocation style
   * @throws IOException if an error occurs while writing
   */
  public void generateMethodDeclaration(Writer output, MetaClass metaClass, ClassMethod method,
                                        InvokeStyle invokeStyle) throws IOException
  {
    if (shouldBeSkipped(method))
      return;

    if (!isPartOfDependencies(method))
      return;

    String className = metaClass.getSimpleName();
    String methodName = method.getName();
    boolean isConstructor = methodName.equals("<init>");
    MethodAccessFlagSet accessFlagSet = method.getAccessFlags();

    // handle clashes between C++ keywords and java identifiers
    methodName = CKeyword.adjust(methodName);

    // if this is a static initializer, then we don't need to declare it
    if (methodName.equals("<clinit>"))
      return;

    Util.generateComment(output, isConstructor ? className : methodName);

    if (exportSymbols)
      output.write("JACE_PROXY_API ");

    // if this is a constructor, there is no return-type
    if (isConstructor)
      output.write(className);
    else
    {
      if (accessFlagSet.contains(MethodAccessFlag.STATIC))
        output.write("static ");

      if (invokeStyle.equals(InvokeStyle.VIRTUAL))
      {
        if (accessFlagSet.contains(MethodAccessFlag.STATIC))
          throw new IllegalStateException("A method may not be both static and virtual");
        output.write("virtual ");
      }

      MetaClass returnType = MetaClassFactory.getMetaClass(method.getReturnType()).proxy();

      if (returnType.getSimpleName().equals("JVoid"))
        output.write("void");
      else
        output.write("::" + returnType.getFullyQualifiedName("::"));
      output.write(" ");
      output.write(methodName);
    }
    output.write("(");

    List<TypeName> parameterTypes = method.getParameterTypes();
    DelimitedCollection<TypeName> parameterList = new DelimitedCollection<TypeName>(parameterTypes);
    DelimitedCollection.Stringifier<TypeName> sf = new DelimitedCollection.Stringifier<TypeName>()
    {
      private int current = 0;

      public String toString(TypeName typeName)
      {
        MetaClass mc = MetaClassFactory.getMetaClass(typeName).proxy();
        return "::" + mc.getFullyQualifiedName("::") + " p" + current++;
      }
    };
    String parameterListStr = parameterList.toString(sf, ", ");

    // If this is a constructor, and it is a 'java copy constructor' (it takes only one
    // argument which is the same type as this class) we need to change the signature so
    // that it takes a 'CopyConstructorSpecifier' in addition. That allows us to avoid
    // confusion with C++ compilers that would think we were defining a C++ copy constructor
    // that took its argument by value.
    if (isConstructor && parameterTypes.size() == 1)
    {
      MetaClass argType = MetaClassFactory.getMetaClass(parameterTypes.iterator().next()).proxy();
      if (argType.equals(metaClass))
        parameterListStr += ", CopyConstructorSpecifier";
    }

    if (!parameterListStr.equals(""))
      parameterListStr = " " + parameterListStr + " ";

    output.write(parameterListStr);
    output.write(")");

    if (!isConstructor && !accessFlagSet.contains(MethodAccessFlag.STATIC))
    {
      // All of the methods should be declared const so that you can
      // call methods on const fields.
      // output.write( " const" );
    }

    output.write(";" + newLine);
    output.write(newLine);
  }

  /**
   * Generates the method declarations.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void generateMethodDeclarations(Writer output) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();

    Collection<ClassMethod> methods = classFile.getMethods();
    String fullyQualifiedName = classFile.getClassName().asIdentifier();
    for (ClassMethod method: methods)
    {
      // Hack in special support for org.omg.CORBA.Object
      // This is the only class that causes Jace to break, based on the
      // special way it creates dependencies between subclasses and superclasses.
      //
      // The better way to deal with this would be by handling the case generically,
      // but since we only see one example of this out of over 10,000 cases,
      // it's just not important enough to deal with right now.
      if (fullyQualifiedName.equals("org.omg.CORBA.Object"))
      {
        String methodName = method.getName();
        if (methodName.equals("_get_policy") || methodName.equals("_get_domain_managers") || methodName.equals(
          "_set_policy_override"))
        {
          continue;
        }
      }
      generateMethodDeclaration(output, metaClass, method);
    }

    // now declare the special "one-off" methods that we add to classes like, Object, String, and Throwable to provide
    // better C++ and Java integration
    if (fullyQualifiedName.equals("java.lang.Object"))
    {
      Util.generateComment(output, "Provide the standard \"System.out.println()\" semantics for ostreams.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("friend std::ostream& operator<<( std::ostream& out, Object& object );" + newLine);
      output.write(newLine);
    }
    else if (fullyQualifiedName.equals("java.lang.String"))
    {
      Util.generateComment(output, "Creates a String from a std::string using the default charset.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("String( const std::string& str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Creates a String from a std::wstring.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("String( const std::wstring& str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Creates a String from a c string.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("String( const char* str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Handle assignment between two Strings.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("String& operator=( const String& str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Converts a String to a std::string.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("operator std::string() const;" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Converts a String to a std::wstring.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("operator std::wstring() const;" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Allows Strings to be written to ostreams.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("friend std::ostream& operator<<( std::ostream& stream, const String& str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Provide concatentation for Strings.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("String operator+( String str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Provide concatenation between Strings and std::strings.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("friend std::string operator+( const std::string& stdStr, const String& jStr );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Provide concatenation between Strings and std::strings.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("friend std::string operator+( const String& jStr, const std::string& stdStr );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Provide comparison between Strings and std::strings.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("friend bool operator==( const std::string& stdStr, const String& str );" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Provide comparison between Strings and std::strings.");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("friend bool operator==( const String& str, const std::string& stdStr );" + newLine);
      output.write(newLine);

      output.write("private:" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Creates a new jstring from a std::string using the default charset.");
      output.write("jstring createString( const std::string& str );" + newLine);
      output.write(newLine);

      output.write("public:" + newLine);
      output.write(newLine);
    }
    else if (fullyQualifiedName.equals("java.lang.Throwable"))
    {
      Util.generateComment(output, "Need to support a non-throwing destructor");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("~Throwable() throw ();" + newLine);
      output.write(newLine);

      Util.generateComment(output, "Overrides std::exception::what() by returning this.toString();");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write("const char *what() const throw();" + newLine);
      output.write(newLine);
    }
  }

  /**
   * Generates the field declarations.
   *
   * @param output the output writer
   * @param forPeer true if the fields are being generated for a peer, false for a proxy
   * @throws IOException if an error occurs while writing
   */
  public void generateFieldDeclarations(Writer output, boolean forPeer) throws IOException
  {
    // Generate a list of method names so we can
    // handle field/method-name clashes.
    Collection<String> methodNames = new ArrayList<String>();

    for (ClassMethod method: classFile.getMethods())
    {
      if (shouldBeSkipped(method))
        continue;
      methodNames.add(CKeyword.adjust(method.getName()));
    }

    for (ClassField field: classFile.getFields())
    {
      if (shouldBeSkipped(field))
        continue;

      MetaClass mc = MetaClassFactory.getMetaClass(field.getDescriptor()).proxy();

      // Don't generate the field if it's type is not part of our dependency list
      if (!dependencyFilter.accept(mc))
        continue;

      String name = field.getName();

      if (forPeer && name.equals("jaceNativeHandle"))
        continue;

      // handle clashes between C++ keywords and java identifiers by appending an underscore to the end of the java
      // identifier
      name = CKeyword.adjust(name);

      // handle clashes with method names by appending an underscore to the field name
      if (methodNames.contains(name))
        name = name + "_";

      // handle clashes with "reserved fields"
      if (reservedFields.contains(name))
        name += "_Jace";

      String type = "::jace::JFieldProxy< " + "::" + mc.getFullyQualifiedName("::") + " >";
      FieldAccessFlagSet accessFlagSet = field.getAccessFlags();

      Util.generateComment(output, accessFlagSet.getName() + " " + name);

      String modifiers = "";
      if (accessFlagSet.contains(FieldAccessFlag.STATIC))
        modifiers = modifiers + "static ";

      // Can't call non-const methods on const fields. There's probably
      // a better way of going about doing this.
      //
      // if ( accessFlagSet.contains( FieldAccessFlag.FINAL ) ) {
      // modifiers = modifiers + "const ";
      // }

      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write(modifiers + type + " " + name + "();" + newLine);
      output.write(newLine);
    }

    if (classFile.getClassName().asIdentifier().equals("java.lang.Throwable"))
    {
      // now define the special "one-off" methods that we add to classes like,
      // Object, String, and Throwable to provide better C++ and Java integration
      Util.generateComment(output, "The message represented by this Throwable." + newLine + newLine
                                   + "This member variable is necessary to keep the contract" + newLine
                                   + "for exception.what().");
      output.write("private: " + newLine);
      output.write("std::string msg;" + newLine);
      output.write("public: " + newLine);
      output.write(newLine);
    }
  }

  /**
   * Generates Jace-specific method declarations.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void generateJaceDeclarations(Writer output) throws IOException
  {
    MetaClass classMetaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    String className = classMetaClass.getSimpleName();

    Util.generateComment(output, "The following methods are required to integrate this class" + newLine
                                 + "with the Jace framework.");

    // if we're an interface, we declare a no argument constructor that delegates to Object( NO_OP ) to make virtual
    // inheritance of interfaces easier
    if (classFile.getAccessFlags().contains(ClassAccessFlag.INTERFACE))
    {
      Util.generateComment(output, "Special no arg constructor for interface virtual inheritance");
      if (exportSymbols)
        output.write("JACE_PROXY_API ");
      output.write(className + "();" + newLine);
    }

    if (exportSymbols)
      output.write("JACE_PROXY_API ");
    output.write(className + "( jvalue value );" + newLine);
    if (exportSymbols)
      output.write("JACE_PROXY_API ");
    output.write(className + "( jobject object );" + newLine);
    if (exportSymbols)
      output.write("JACE_PROXY_API ");
    output.write(className + "( const " + className + "& object );" + newLine);
    if (exportSymbols)
      output.write("JACE_PROXY_API ");
    output.write(className + "( const NoOp& noOp );" + newLine);
    if (exportSymbols)
      output.write("JACE_PROXY_API ");
    output.write("virtual const JClass& getJavaJniClass() const throw ( ::jace::JNIException );" + newLine);
    if (exportSymbols)
      output.write("JACE_PROXY_API ");
    output.write("static const JClass& staticGetJavaJniClass() throw ( ::jace::JNIException );" + newLine);
    try
    {
      if (isException(classFile.getClassName()))
        output.write("static JEnlister<" + className + "> enlister;" + newLine);
    }
    catch (ClassNotFoundException e)
    {
      throw new IOException(e);
    }
  }

  /**
   * Generates the opening of a namespace.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  public void beginNamespace(Writer output) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    ClassPackage classPackage = metaClass.getPackage();

    List<String> path = classPackage.getPath();
    if (path.isEmpty())
      return;

    StringBuilder namespace = new StringBuilder("BEGIN_NAMESPACE_");
    namespace.append(path.size());
    namespace.append("( ");
    namespace.append(classPackage.toName(", ", false));
    namespace.append(" )");
    output.write(namespace.toString() + newLine);
  }

  /**
   * Closes the namespace declaration.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void endNamespace(Writer output) throws IOException
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    ClassPackage classPackage = metaClass.getPackage();

    List<String> path = classPackage.getPath();
    if (path.isEmpty())
      return;

    StringBuilder namespace = new StringBuilder("END_NAMESPACE_");
    namespace.append(path.size());
    namespace.append("( ");
    namespace.append(classPackage.toName(", ", false));
    namespace.append(" )");
    output.write(namespace.toString() + newLine);
  }

  /**
   * Generate includes for the Jace library headers, which should always be included.
   *
   * @param output the output writer
   * @param forPeer true if the methods are being generated for a peer, false for a proxy
   * @throws IOException if an error occurs while writing
   */
  public void includeStandardHeaders(Writer output, boolean forPeer) throws IOException
  {
    // We don't need to include any of the primitive types ( JInt, JByte, etc... )
    // because they are all included by both JArray.h and JFieldProxy.h
    output.write("#ifndef JACE_OS_DEP_H" + newLine);
    output.write("#include \"jace/os_dep.h\"" + newLine);
    output.write("#endif" + newLine);
    output.write(newLine);

    output.write("#ifndef JACE_NAMESPACE_H" + newLine);
    output.write("#include \"jace/namespace.h\"" + newLine);
    output.write("#endif" + newLine);
    output.write(newLine);

    if (!forPeer)
    {
      output.write("#ifndef JACE_JOBJECT_H" + newLine);
      output.write("#include \"" + JaceConstants.getProxyPackage().asPath() + "/JObject.h\"" + newLine);
      output.write("#endif" + newLine);
      output.write(newLine);
      try
      {
        if (isException(classFile.getClassName()))
        {
          output.write("#ifndef JACE_JENLISTER_H" + newLine);
          output.write("#include \"jace/JEnlister.h\"" + newLine);
          output.write("#endif" + newLine);
          output.write(newLine);
        }
      }
      catch (ClassNotFoundException e)
      {
        throw new IOException(e);
      }

      output.write("#ifndef JACE_JARRAY_H" + newLine);
      output.write("#include \"jace/JArray.h\"" + newLine);
      output.write("#endif" + newLine);
      output.write(newLine);

      output.write("#ifndef JACE_JFIELD_PROXY_H" + newLine);
      output.write("#include \"jace/JFieldProxy.h\"" + newLine);
      output.write("#endif" + newLine);
      output.write(newLine);

      output.write("#ifndef JACE_JCLASSIMPL_H" + newLine);
      output.write("#include \"jace/JClassImpl.h\"" + newLine);
      output.write("#endif" + newLine);
      output.write(newLine);

      String className = classFile.getClassName().asIdentifier();
      if (className.equals("java.lang.Throwable") || className.equals("java.lang.String"))
      {
        output.write("#include <string>" + newLine);
        output.write(newLine);
      }
    }
  }

  /**
   * Includes the headers for the super-class and all implemented interfaces.
   *
   * This is necessary, because we can't inherit from a class unless it is fully defined.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  public void includeDependentHeaders(Writer output) throws IOException
  {
    if (classFile.getSuperClassName() != null)
    {
      Util.generateComment(output, "The super class for this class.");
      MetaClass superClassMetaClass = MetaClassFactory.getMetaClass(classFile.getSuperClassName()).proxy();
      output.write(superClassMetaClass.include() + newLine);
      output.write(newLine);
    }

    Collection<TypeName> interfaces = classFile.getInterfaces();
    if (interfaces.size() > 0)
    {
      Util.generateComment(output, "The interfaces implemented by this class.");
      for (TypeName i: interfaces)
      {
        MetaClass interfaceClass = MetaClassFactory.getMetaClass(i).proxy();
        output.write(interfaceClass.include() + newLine);
        output.write(newLine);
      }
    }

    Collection<MetaClass> dependentClasses = getDependentClasses(true);
    if (!dependentClasses.isEmpty())
    {
      Util.generateComment(output, "Classes which this class is fully dependent upon.");
      for (MetaClass dependentMetaClass: dependentClasses)
      {
        // Skip includes for classes that aren't part of our dependency list
        if (!dependencyFilter.accept(dependentMetaClass))
          continue;

        // This is the special case when the dependent class is an array
        if (dependentMetaClass instanceof ArrayMetaClass)
          dependentMetaClass = ((ArrayMetaClass) dependentMetaClass).getInnermostElementType();

        // Hack in special support for org.omg.CORBA.Object
        // This is the only class that causes Jace to break, based on the
        // special way it creates dependencies between subclasses and superclasses.
        //
        // The better way to deal with this would be by handling the case generically,
        // but since we only see one example of this out of over 10,000 cases,
        // it's just not important enough to deal with right now.
        if (classFile.getClassName().asIdentifier().equals("org.omg.CORBA.Object"))
        {
          String dependentName = dependentMetaClass.getSimpleName();
          if (dependentName.equals("DomainManager") || dependentName.equals("Policy"))
            continue;
        }
        output.write(dependentMetaClass.include() + newLine);
        output.write(newLine);
      }
    }
  }

  /**
   * Make forward declarations for all of the types
   * for which the class is dependent.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  public void makeForwardDeclarations(Writer output) throws IOException
  {
    Util.generateComment(output, "Forward declarations for the classes that this class uses.");

    for (MetaClass mc: getDependentClasses(false))
    {
      // Don't include classes that aren't in our dependency list
      if (!dependencyFilter.accept(mc))
        continue;

      output.write(mc.forwardDeclare() + newLine);
      output.write(newLine);
    }
  }

  /**
   * Returns the classes which the class we are generating is dependent upon.
   *
   * @param fullyDependent true if the method is to return classes which need to
   * be fully defined before reference. This includes fields and the array elements.
   * Otherwise, false if the method is to return classes which may be forward
   * declared ( all other parameter types and exceptions ).
   *
   * @return a list of the dependee MetaClasses for this class. It does not
   * include the super class, and the interfaces implemented by this class.
   */
  public Set<MetaClass> getDependentClasses(boolean fullyDependent)
  {
    // this method finds all dependencies by scanning through the field and methods belonging to the class
    Set<MetaClass> excludedClasses = new HashSet<MetaClass>();
    if (classFile.getSuperClassName() != null)
      excludedClasses.add(MetaClassFactory.getMetaClass(classFile.getSuperClassName()).proxy());

    for (TypeName i: classFile.getInterfaces())
      excludedClasses.add(MetaClassFactory.getMetaClass(i).proxy());

    // Add our current class to the list
    excludedClasses.add(MetaClassFactory.getMetaClass(classFile.getClassName()).proxy());

    Set<MetaClass> result = new HashSet<MetaClass>();
    // first, get the fields for the class. We only include fields if we are listing fullyDependent classes.
    if (fullyDependent)
    {
      for (ClassField field: classFile.getFields())
      {
        if (shouldBeSkipped(field))
          continue;
        MetaClass metaClass = MetaClassFactory.getMetaClass(field.getDescriptor()).proxy();
        if (!excludedClasses.contains(metaClass))
          result.add(metaClass);
      }
    }

    // now we check for method parameters and exceptions
    for (ClassMethod method: classFile.getMethods())
    {
      if (shouldBeSkipped(method))
        continue;

      MetaClass returnType = MetaClassFactory.getMetaClass(method.getReturnType()).proxy();
      addDependentClass(result, fullyDependent, returnType, excludedClasses);

      for (TypeName parameter: method.getParameterTypes())
      {
        MetaClass parameterType = MetaClassFactory.getMetaClass(parameter).proxy();
        addDependentClass(result, fullyDependent, parameterType, excludedClasses);
      }

      for (TypeName exception: method.getExceptions())
      {
        MetaClass exceptionType = MetaClassFactory.getMetaClass(exception).proxy();
        addDependentClass(result, fullyDependent, exceptionType, excludedClasses);
      }
    }
    return result;
  }

  /**
   * Adds class dependencies to a set.
   *
   * @param result the set to add to
   * @param fullyDependent true if the method is to return classes which need to
   * be fully defined before reference. This includes fields and the array elements.
   * Otherwise, false if the method is to return classes which may be forward
   * declared ( all other parameter types and exceptions ).
   * @param classType the class
   * @param excludedClasses the dependencies to exclude from the set
   */
  private void addDependentClass(Collection<MetaClass> result, boolean fullyDependent, MetaClass classType,
                                 Set<MetaClass> excludedClasses)
  {
    if (classType instanceof ArrayMetaClass)
    {
      if (fullyDependent)
      {
        ArrayMetaClass arrayType = (ArrayMetaClass) classType;
        MetaClass elementType = arrayType.getInnermostElementType();

        if (!excludedClasses.contains(elementType))
          result.add(elementType);
      }
    }
    else if (!fullyDependent && !excludedClasses.contains(classType))
      result.add(classType);
  }

  /**
   * Generates the C++ proxy (header and source) for the specified class.
   *
   * @param outputHeaders the directory to write the header file to
   * @param outputSources the directory to write the source file to
   * @throws IOException if an error occurs while writing the proxy files
   */
  public void writeProxy(File outputHeaders, File outputSources)
    throws IOException
  {
    ClassMetaClass metaClass = (ClassMetaClass) MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    String subDir = metaClass.getPackage().toName("/", false).replace('/', File.separatorChar);
    File fullHeaderDir = outputHeaders;
    File fullSourceDir = outputSources;

    // make the source and header directories but only if the class resides in a package. Fix for Bug 598457
    if (subDir.contains(File.separator))
    {
      fullHeaderDir = new File(fullHeaderDir, subDir);
      fullSourceDir = new File(fullSourceDir, subDir);
      fullHeaderDir.mkdirs();
      fullSourceDir.mkdirs();
    }

    // then generate the header and include files
    String classFileName = metaClass.getFileName();
    assert (!classFileName.contains("/") && !classFileName.contains("\\")): classFileName;

    if (log.isInfoEnabled())
      log.info("Generating the Proxy for " + metaClass.getFullyQualifiedName("."));
    File fullHeaderFile = new File(fullHeaderDir, classFileName + ".h");
    File actualDirectory = fullHeaderFile.getParentFile();
    if (!actualDirectory.exists() && !actualDirectory.mkdirs())
      throw new IOException("Failed to create " + actualDirectory);
    BufferedWriter out = new BufferedWriter(new FileWriter(fullHeaderFile));
    try
    {
      generateHeader(out);
    }
    finally
    {
      out.close();
    }

    File fullSourceFile = new File(fullSourceDir, classFileName + ".cpp");
    actualDirectory = fullSourceFile.getParentFile();
    if (!actualDirectory.exists() && !actualDirectory.mkdirs())
      throw new IOException("Failed to create " + actualDirectory);
    out = new BufferedWriter(new FileWriter(fullSourceFile));
    try
    {
      generateSource(out);
    }
    finally
    {
      out.close();
    }
  }

  /**
   * Returns the logger associated with the object.
   *
   * @return the logger associated with the object
   */
  private Logger getLogger()
  {
    return log;
  }

  /**
   * Returns a String describing the usage of this tool.
   *
   * @return String describing the usage of this tool
   */
  private static String getUsage()
  {
    return "Usage: ProxyGenerator <class file> <header | source> [ options ]" + newLine + "Where options can be:"
           + newLine + "  -public : Generate public fields and members (default)" + newLine
           + "  -protected : Generate public, protected fields and members" + newLine
           + "  -package : Generate public, protected, package-private fields and methods." + newLine
           + "  -private : Generate public, protected, package-private, private fields and methods." + newLine;
  }

  /**
   * Generates a C++ proxy for a java class.
   *
   * @param args the command-line argument
   */
  public static void main(String[] args)
  {
    if (args.length < 2)
    {
      System.out.println(getUsage());
      return;
    }

    AccessibilityType accessibility = AccessibilityType.PUBLIC;
    for (int i = 2; i < args.length; ++i)
    {
      String option = args[i];

      if (option.equals("-public"))
        accessibility = AccessibilityType.PUBLIC;
      if (option.equals("-protected"))
        accessibility = AccessibilityType.PROTECTED;
      else if (option.equals("-package"))
        accessibility = AccessibilityType.PACKAGE;
      else if (option.equals("-private"))
        accessibility = AccessibilityType.PRIVATE;
      else
      {
        System.out.println("Not an understood option: " + option);
        System.out.println();
        System.out.println(getUsage());
        return;
      }
    }

    ProxyGenerator generator = new ProxyGenerator.Builder(new ClassPath(Collections.<File>emptyList()),
      new ClassFile(new File(args[0])), new AcceptAll()).accessibility(accessibility).build();
    try
    {
      if (args[1].equals("header"))
        generator.generateHeader(new OutputStreamWriter(System.out));
      else if (args[1].equals("source"))
        generator.generateSource(new OutputStreamWriter(System.out));
    }
    catch (IOException e)
    {
      generator.getLogger().error("", e);
    }
  }

  /**
   * Builds a ProxyGenerator.
   *
   * @author Gili Tzabari
   */
  public static class Builder
  {
    private final ClassFile classFile;
    private final MetaClassFilter dependencyFilter;
    private final ClassPath classPath;
    private AccessibilityType accessibility = AccessibilityType.PUBLIC;
    private boolean exportSymbols;

    /**
     * Creates a new Builder.
     *
     * @param classPath the path to search for class files when resolving class dependencies
     * @param classFile the class to generate a proxy for
     * @param dependencyFilter indicates whether methods should be exported. Methods whose parameters or return types
     * are rejected by the filter are omitted.
     * @throws IllegalArgumentException if <code>classFile</code>, <code>dependencyFilter</code> or
     * <code>classPath</code> are null
     */
    public Builder(ClassPath classPath, ClassFile classFile, MetaClassFilter dependencyFilter)
      throws IllegalArgumentException
    {
      if (classFile == null)
        throw new IllegalArgumentException("classFile may not be null");
      if (dependencyFilter == null)
        throw new IllegalArgumentException("dependencyFilter may not be null");
      if (classPath == null)
        throw new IllegalArgumentException("classPath may not be null");
      this.classFile = classFile;
      this.dependencyFilter = dependencyFilter;
      this.classPath = classPath;
    }

    /**
     * Indicates if proxy symbols should be exported (i.e. for use in DLLs)
     *
     * @param value true if proxy symbols should be exported. The default is false.
     * @return the Builder
     */
    public Builder exportSymbols(boolean value)
    {
      this.exportSymbols = value;
      return this;
    }

    /**
     * Indicates the member accessibility to expose.
     *
     * @param accessibility the member accessibility to expose, The default is AccessibilityType.PUBLIC.
     * @return the Builder
     * @throws IllegalArgumentException if <code>accessibility</code> is null
     */
    public Builder accessibility(AccessibilityType accessibility) throws IllegalArgumentException
    {
      if (accessibility == null)
        throw new IllegalArgumentException("accessibility may not be null");
      this.accessibility = accessibility;
      return this;
    }

    /**
     * Builds a ProxyGenerator.
     *
     * @return the ProxyGenerator
     */
    public ProxyGenerator build()
    {
      return new ProxyGenerator(this);
    }
  }

  /**
   * Accepts all classes.
   *
   * @author Gili Tzabari
   */
  public static class AcceptAll implements MetaClassFilter
  {
    @Override
    public boolean accept(MetaClass candidate)
    {
      return true;
    }
  }

  /**
   * Accepts a collection of classes.
   *
   * @author Gili Tzabari
   */
  public static class FilteringCollection implements MetaClassFilter
  {
    private final Collection<MetaClass> collection = new ArrayList<MetaClass>();

    /**
     * Adds a metaclass to be accepted.
     *
     * @param metaClass the metaclass to accept
     */
    public void add(MetaClass metaClass)
    {
      collection.add(metaClass);
    }

    @Override
    public boolean accept(MetaClass candidate)
    {
      return collection.contains(candidate);
    }
  }
}
