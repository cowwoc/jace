package jace.peergen;

import jace.metaclass.ArrayMetaClass;
import jace.metaclass.ClassMetaClass;
import jace.metaclass.ClassPackage;
import jace.metaclass.MetaClass;
import jace.metaclass.MetaClassFactory;
import jace.metaclass.JaceConstants;
import jace.metaclass.TypeName;
import jace.metaclass.VoidClass;
import jace.parser.ClassFile;
import jace.parser.method.ClassMethod;
import jace.parser.method.MethodAccessFlag;
import jace.proxygen.ProxyGenerator;
import jace.proxygen.ProxyGenerator.AccessibilityType;
import jace.util.DelimitedCollection;
import jace.util.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a Java implementation, and a C++ peer for the specified class.
 *
 * @author Toby Reyelts
 * @author Gili Tzabari
 */
public class PeerGenerator
{
  private static final String newLine = System.getProperty("line.separator");
  private final Logger log = LoggerFactory.getLogger(PeerGenerator.class);
  private ClassFile classFile;
  private ClassMetaClass metaClass;
  private ClassMetaClass proxyMetaClass;
  private final String includeDir;
  private final String sourceDir;
  private final boolean userDefinedMembers;

  /**
   * Constructs a new PeerGenerator for the given class.
   *
   * @param classFile the Java class that needs a C++ peer
   * @param includeDir the directory containing the output header files
   * @param sourceDir the directory containing the output source files
   * @param userDefinedMembers true if &lt;peer_class_name&gt;_user.h should be generated
   */
  public PeerGenerator(ClassFile classFile, String includeDir, String sourceDir, boolean userDefinedMembers)
  {
    this.classFile = classFile;
    proxyMetaClass = (ClassMetaClass) MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();
    metaClass = (ClassMetaClass) proxyMetaClass.unProxy();
    this.includeDir = includeDir;
    this.sourceDir = sourceDir;
    this.userDefinedMembers = userDefinedMembers;
  }

  /**
   * Generates the C++ peer.
   *
   * @throws IOException if an error occurs while writing the files
   */
  public void generate() throws IOException
  {
    MetaClass peerMetaClass = metaClass.toPeer();
    String directory = peerMetaClass.getPackage().toName("/", false).replace('/', File.separatorChar);

    File includeDirectory = new File(includeDir, directory);
    File sourceDirectory = new File(sourceDir, directory);

    File cppHeader = new File(includeDirectory, peerMetaClass.getName() + ".h");
    File actualDirectory = cppHeader.getParentFile();
    if (!actualDirectory.exists() && !actualDirectory.mkdirs())
      throw new IOException("Failed to create " + actualDirectory);
    BufferedWriter out = new BufferedWriter(new FileWriter(cppHeader));
    try
    {
      generateCppPeerHeader(out);
    }
    finally
    {
      out.close();
    }

    File cppMappings = new File(sourceDirectory, peerMetaClass.getName() + "Mappings.cpp");
    actualDirectory = cppMappings.getParentFile();
    if (!actualDirectory.exists() && !actualDirectory.mkdirs())
      throw new IOException("Failed to create " + actualDirectory);
    out = new BufferedWriter(new FileWriter(cppMappings));
    try
    {
      generateCppPeerMappings(out);
    }
    finally
    {
      out.close();
    }

    File cppSource = new File(sourceDirectory, peerMetaClass.getName() + "_peer.cpp");
    actualDirectory = cppSource.getParentFile();
    if (!actualDirectory.exists() && !actualDirectory.mkdirs())
    {
      throw new IOException("Failed to create " + actualDirectory);
    }
    out = new BufferedWriter(new FileWriter(cppSource));
    try
    {
      generateCppPeerSource(out);
    }
    finally
    {
      out.close();
    }
  }

  /**
   * Generates a C++ peer header.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void generateCppPeerHeader(Writer output) throws IOException
  {
    MetaClass peerMetaClass = metaClass.toPeer();
    output.write(newLine);
    output.write(peerMetaClass.beginGuard() + newLine);
    output.write(newLine);

    // Generate the #includes
    ProxyGenerator proxyGen = new ProxyGenerator(classFile, AccessibilityType.PRIVATE);
    proxyGen.includeStandardHeaders(output);
    output.write(newLine);

    Util.generateComment(output, "The Peer class from which this class derives.");
    output.write("#include \"jace/Peer.h\"" + newLine);
    output.write(newLine);

    proxyGen.includeDependentHeaders(output);
    proxyGen.makeForwardDeclarations(output);
    output.write(newLine);

    // The ProxyGenerator explicitly disallows a class from including itself,
    // but the problem is that we're creating a peer, and it may actually
    // depend upon its proxy. (For example, a Singleton's getInstance method).
    //
    // We could walk through the set of methods/fields to figure this out, but for
    // now we'll just always include it.
    output.write(proxyMetaClass.include() + newLine);
    output.write(newLine);

    beginNamespace(output);
    output.write(newLine);

    // Generate the class declaration
    String name = metaClass.getName();
    Util.generateComment(output, name + newLine +
                                 newLine +
                                 "This header provides the declaration for the Jace Peer, " + name + "." +
                                 newLine +
                                 "To complete this Peer, you must create a new source file containing the" +
                                 newLine +
                                 "definitions for all native methods declared for this Peer." + newLine +
                                 newLine +
                                 "You may also override initialize() and destroy(), if your Peer requires" +
                                 newLine +
                                 "custom initialization or destruction.");

    output.write("class " + name + " : public ::jace::Peer, public ");

    // If we are derived directly from java.lang.Object, we need to derive from it virtually
    if (classFile.getSuperClassName().asIdentifier().equals("java.lang.Object"))
      output.write("virtual ");

    MetaClass superMetaClass = MetaClassFactory.getMetaClass(classFile.getSuperClassName()).proxy();
    output.write("::" + superMetaClass.getFullyQualifiedName("::"));

    // Now, add all of the interfaces that are implemented
    for (TypeName i: classFile.getInterfaces())
    {
      MetaClass interfaceClass = MetaClassFactory.getMetaClass(i).proxy();
      output.write(", public virtual ");
      output.write("::" + interfaceClass.getFullyQualifiedName("::"));
    }

    output.write(" {" + newLine);
    output.write(newLine);

    output.write("public: " + newLine);
    output.write(newLine);

    output.write("// Methods which must be implemented by the Developer" + newLine);
    output.write("// --------------------------------------------------" + newLine);
    output.write(newLine);

    // Generate the native method declarations
    Collection<ClassMethod> methods = classFile.getMethods();
    for (Iterator it = methods.iterator(); it.hasNext();)
    {
      ClassMethod method = (ClassMethod) it.next();
      if (!method.getAccessFlags().contains(MethodAccessFlag.NATIVE))
        continue;

      String methodName = method.getName();
      if (methodName.equals("jaceCreateInstance") || methodName.equals("jaceDestroyInstance") ||
          methodName.equals("jaceSetVm"))
      {
        continue;
      }

      if (!method.getAccessFlags().contains(MethodAccessFlag.STATIC))
        proxyGen.generateMethodDeclaration(output, metaClass, method, ProxyGenerator.InvokeStyle.VIRTUAL);
      else
        proxyGen.generateMethodDeclaration(output, metaClass, method, ProxyGenerator.InvokeStyle.NORMAL);
    }

    output.write(newLine);

    output.write("// Methods made available by Jace" + newLine);
    output.write("// ------------------------------" + newLine);
    output.write(newLine);

    // Generate non-native method declarations.
    for (ClassMethod method: methods)
    {
      if (method.getAccessFlags().contains(MethodAccessFlag.NATIVE))
        continue;

      String methodName = method.getName();
      if (methodName.equals("<init>") || methodName.equals("<clinit>") ||
          methodName.equals("jaceUserStaticInit") || methodName.equals("jaceUserClose") ||
          methodName.equals("jaceUserFinalize") || methodName.equals("jaceDispose") ||
          methodName.equals("jaceSetNativeHandle") || methodName.equals("jaceGetNativeHandle"))
      {
        continue;
      }
      proxyGen.generateMethodDeclaration(output, metaClass, method);
    }

    output.write("// Fields made available by Jace" + newLine);
    output.write("// -----------------------------" + newLine);
    output.write(newLine);

    proxyGen.generateFieldDeclarations(output);

    output.write("// Methods internal to Jace" + newLine);
    output.write("// ------------------------" + newLine);
    output.write(newLine);

    Util.generateComment(output, "Called when the VM instantiates a new " + name + ".");
    output.write(name + "( jobject obj );" + newLine);
    output.write(newLine);

    Util.generateComment(output, "Called when the the user explicitly collects a " + name + newLine +
                                 "or when the VM garbage collects a " + name + ".");
    output.write("virtual ~" + name + "() throw ();" + newLine);
    output.write(newLine);

    output.write("virtual const JClass* getJavaJniClass() const throw ( ::jace::JNIException );" + newLine);
    output.write("static const JClass* staticGetJavaJniClass() throw ( ::jace::JNIException );" + newLine);
    output.write(newLine);

    if (userDefinedMembers)
    {
      output.write("// User defined members" + newLine);
      output.write("// --------------------" + newLine);
      String userInclude = peerMetaClass.getPackage().toName("/", true) +
                           peerMetaClass.getName() + "_user.h";
      output.write("#include \"" + userInclude + "\"" + newLine);
      output.write(newLine);
    }

    output.write("};" + newLine);

    output.write(newLine);
    endNamespace(output);

    output.write(peerMetaClass.endGuard() + newLine);
    output.write(newLine);
  }

  /**
   * Generates the C++ Peer source that is required to implement the non-native
   * member functions of the Peer.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void generateCppPeerSource(Writer output) throws IOException
  {
    String fullName = metaClass.getFullyQualifiedName(".");

    output.write(newLine);
    Util.generateComment(output, "This is the source for the implementation of the Jace Peer for " +
                                 fullName + "." + newLine +
                                 "Please do not edit this source. Any changes made will be overwritten." +
                                 newLine +
                                 newLine +
                                 "For more information, please refer to the Jace Developer's Guide." + newLine);
    output.write(newLine);

    ProxyGenerator proxyGen = new ProxyGenerator(classFile, AccessibilityType.PRIVATE);
    proxyGen.includeStandardSourceHeaders(output);

    for (MetaClass dependency: getDependencies(classFile))
      output.write(dependency.include() + newLine);

    output.write(newLine);

    output.write(metaClass.toPeer().include() + newLine);
    output.write(newLine);

    beginNamespace(output);
    output.write(newLine);

    output.write(proxyGen.getInitializerValue(true) + newLine);
    proxyGen.generateMethodDefinitions(output, true);
    proxyGen.generateFieldDefinitions(output, true);
    proxyGen.generateJaceDefinitions(output, true);

    endNamespace(output);
    output.write(newLine);
  }

  /**
   * Generates the mappings from native methods to C++ Peer member functions.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void generateCppPeerMappings(Writer output) throws IOException
  {
    String fullName = metaClass.getFullyQualifiedName(".");

    MetaClass peerMetaClass = metaClass.toPeer();
    String fullPeerName = "::" + peerMetaClass.getFullyQualifiedName("::");
    String className = mangleName(metaClass.getFullyQualifiedName("/"));

    String msg = "These JNI mappings are for the Jace Peer for " + fullName + "." + newLine +
                 "Please do not edit these JNI mappings. Any changes made will be overwritten." + newLine +
                 newLine +
                 "For more information, please refer to the Jace Developer's Guide.";

    output.write(newLine);
    Util.generateComment(output, msg);
    output.write(newLine);

    ProxyGenerator proxyGen = new ProxyGenerator(classFile, AccessibilityType.PRIVATE);
    proxyGen.includeStandardHeaders(output);

    output.write("#include \"" + JaceConstants.getProxyPackage().asPath() + "/java/lang/Throwable.h\"" + newLine);
    output.write("#include \"" + JaceConstants.getProxyPackage().asPath() + "/java/lang/RuntimeException.h\"" + newLine);
    output.write("#include \"" + JaceConstants.getProxyPackage().asPath() + "/java/lang/Class.h\"" + newLine);
    output.write("#include \"" + JaceConstants.getProxyPackage().asPath() + "/java/lang/String.h\"" + newLine);
    output.write("#include \"jace/JNIHelper.h\"" + newLine);
    output.write("#include \"jace/WrapperVmLoader.h\"" + newLine);
    output.write(newLine);

    for (MetaClass dependency: getDependencies(classFile))
      output.write(dependency.include() + newLine);
    output.write(newLine);

    output.write(metaClass.toPeer().include() + newLine);
    output.write(newLine);

    output.write("#include <iostream>" + newLine);
    output.write("#include <assert.h>" + newLine);
    output.write(newLine);

    // generate the native method implementations
    for (ClassMethod method: classFile.getMethods())
    {
      if (!method.getAccessFlags().contains(MethodAccessFlag.NATIVE))
        continue;

      String methodName = method.getName();

      Util.generateComment(output, "The JNI mapping for" + newLine +
                                   newLine +
                                   "Class: " + mangleName(metaClass.getFullyQualifiedName("/")) + newLine +
                                   "Method: " + method.getName() + newLine +
                                   "Signature: " + method.getDescriptor());

      // treat jaceCreateInstance, jaceDestroyInstance, and jaceSetVm specially
      if (methodName.equals("jaceCreateInstance"))
      {
        output.write("extern \"C\" JNIEXPORT jlong JNICALL ");
        output.write("Java_" + className + "_jaceCreateInstance( JNIEnv *env, jobject jPeer ) {" + newLine);
        output.write("  try {" + newLine);
        output.write("    ::jace::Peer* peer = new " + fullPeerName + "( jPeer );" + newLine);
        output.write("    peer->initialize(); " + newLine);
        output.write("    return reinterpret_cast<jlong>( peer );" + newLine);
        output.write("  }" + newLine);
        output.write("  catch ( jace::proxy::java::lang::Throwable& t ) {" + newLine);
        output.write("    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );" +
                     newLine);
        output.write("    return 0;" + newLine);
        output.write("  }" + newLine);
        output.write("  catch ( std::exception& e ) {" + newLine);
        output.write("    std::string msg = std::string( \"An unexpected JNI error has occurred: \" ) + e.what();" +
                     newLine);
        output.write("    jace::proxy::java::lang::RuntimeException ex( msg );" + newLine);
        output.write("    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );" +
                     newLine);
        output.write("    return 0;" + newLine);
        output.write("  }" + newLine);
        output.write("}" + newLine);
        output.write(newLine);
        continue;
      }
      else if (methodName.equals("jaceDestroyInstance"))
      {
        output.write("extern \"C\" JNIEXPORT void JNICALL ");
        output.write("Java_" + className + "_jaceDestroyInstance( JNIEnv*, jlong jNativeHandle ) {" + newLine);
        output.write("  try {" + newLine);
        output.write("    ::jace::Peer* peer = reinterpret_cast< ::jace::Peer*>( jNativeHandle );" + newLine);
        output.write("    peer->destroy();" + newLine);
        output.write("    delete peer; " + newLine);
        output.write("  }" + newLine);
        output.write("  catch ( std::exception& e ) {" + newLine);
        output.write("    std::string msg = std::string( \"An unexpected JNI error has occurred: \" ) + e.what();" +
                     newLine);
        output.write("    std::cerr << msg;" + newLine);
        output.write("  }" + newLine);
        output.write("}" + newLine);
        output.write(newLine);
        continue;
      }
      else if (methodName.equals("jaceSetVm"))
      {
        output.write("extern \"C\" JNIEXPORT void JNICALL ");
        output.write("Java_" + className + "_jaceSetVm( JNIEnv *env, jclass ) {" + newLine);
        output.write("  try {" + newLine);
        output.write("    jclass jClassClass = env->FindClass( \"java/lang/Class\" );" + newLine);
        output.write("    jmethodID forName = env->GetStaticMethodID( jClassClass, \"forName\", " +
                     "\"(Ljava/lang/String;)Ljava/lang/Class;\" );" + newLine);
        output.write("    jstring objectClassStr = env->NewStringUTF( \"java.lang.Object\" );" + newLine);
        output.write("    jobject loaderLock = env->CallStaticObjectMethod( jClassClass, forName, objectClassStr );" +
                     newLine);
        output.write(newLine);
        output.write("    jint rc = env->MonitorEnter( loaderLock );" + newLine);
        output.write(newLine);
        output.write("    if ( rc < 0 ) {" + newLine);
        output.write("      std::string msg = \"Unable to obtain a lock on Object.class\";" + newLine);
        output.write("      std::cerr << msg;" + newLine);
        output.write("      return;" + newLine);
        output.write("    }" + newLine);
        output.write(newLine);
        output.write("    if ( ! ::jace::helper::getVmLoader() ) {" + newLine);
        output.write("      ::jace::helper::setVmLoader( ::jace::WrapperVmLoader( env ) );" + newLine);
        output.write("      ::jace::helper::registerShutdownHook(env);" + newLine);
        output.write("    }" + newLine);
        output.write(newLine);
        output.write("    rc = env->MonitorExit( loaderLock );" + newLine);
        output.write(newLine);
        output.write("    if ( rc < 0 ) {" + newLine);
        output.write("      std::string msg = \"Unable to release a lock on Object.class\";" + newLine);
        output.write("      std::cerr << msg;" + newLine);
        output.write("      return;" + newLine);
        output.write("    }" + newLine);
        output.write("  }" + newLine);
        output.write("  catch ( std::exception& e ) {" + newLine);
        output.write("    std::string msg = std::string( \"An unexpected JNI error has occurred: \" ) + e.what();" +
                     newLine);
        output.write("    std::cerr << msg;" + newLine);
        output.write("    return;" + newLine);
        output.write("  }" + newLine);
        output.write("}" + newLine);
        output.write(newLine);
        output.write(newLine);
        output.write(newLine);
        continue;
      }

      // now, handle the normal case
      String functionName = getNativeMethodName(metaClass, method);

      MetaClass returnType = MetaClassFactory.getMetaClass(method.getReturnType()).proxy();

      boolean isStatic = method.getAccessFlags().contains(MethodAccessFlag.STATIC);

      Collection<String> params = new ArrayList<String>();

      if (isStatic)
        params.add("jclass jP0");
      else
        params.add("jobject jP0");

      List<TypeName> parameterTypes = method.getParameterTypes();
      int parameterIndex = 1;

      for (TypeName param: parameterTypes)
      {
        MetaClass paramClass = MetaClassFactory.getMetaClass(param).proxy();
        params.add(paramClass.getJniType() + " jP" + parameterIndex);
        ++parameterIndex;
      }

      output.write("extern \"C\" JNIEXPORT " + returnType.getJniType() + " JNICALL " + functionName);
      output.write("( JNIEnv* env, ");
      output.write(new DelimitedCollection<String>(params).toString(", ", false));
      output.write(" ) { " + newLine);
      output.write(newLine);

      output.write("  try {" + newLine);

      // define the Jace Proxy version of the parameters
      String target;

      if (!isStatic)
      {
        output.write("    " + fullPeerName + "* peer = dynamic_cast< " + fullPeerName +
                     "*>( ::jace::helper::getPeer( jP0 ) );" + newLine);
        output.write("    assert(peer!=0);" + newLine);
        target = "peer->";
      }
      else
        target = fullPeerName + "::";

      parameterIndex = 1;

      for (TypeName param: parameterTypes)
      {
        MetaClass paramClass = MetaClassFactory.getMetaClass(param).proxy();
        output.write("    " + "::" + paramClass.getFullyQualifiedName("::"));
        output.write(" p" + parameterIndex + "( jP" + parameterIndex + " );" + newLine);
        ++parameterIndex;
      }

      if (parameterTypes.size() > 0 || !isStatic)
      {
        output.write(newLine);
      }

      if (returnType instanceof VoidClass)
      {
        output.write("    " + target + methodName + "( ");
        for (int i = 0; i < params.size() - 1; ++i)
        {
          output.write("p" + (i + 1));
          if (i < params.size() - 2)
            output.write(",");
          output.write(" ");
        }
        output.write(" );" + newLine);
        output.write("    return;" + newLine);
      }
      else
      {
        output.write("    return ");

        if (!returnType.isPrimitive())
          output.write("static_cast<" + returnType.getJniType() + ">( env->NewLocalRef( ");

        output.write(target + methodName + "( ");
        for (int i = 0; i < params.size() - 1; ++i)
        {
          output.write("p" + (i + 1));
          if (i < params.size() - 2)
            output.write(",");
          output.write(" ");
        }
        output.write(")");
        if (!returnType.isPrimitive())
          output.write(".getJavaJniObject() ) )");
        output.write(";" + newLine);
      }
      output.write("  }" + newLine);

      String returnValue = returnType instanceof VoidClass ? "" : "NULL";

      output.write("  catch ( jace::proxy::java::lang::Throwable& t ) {" + newLine);
      output.write("    env->Throw( static_cast<jthrowable>( env->NewLocalRef( t.getJavaJniObject() ) ) );" +
                   newLine);
      output.write("    return " + returnValue + ";" + newLine);
      output.write("  }" + newLine);
      output.write("  catch ( std::exception& e ) {" + newLine);
      output.write("    std::string msg = std::string( \"An unexpected JNI error has occurred: \" ) + e.what();" +
                   newLine);
      output.write("    jace::proxy::java::lang::RuntimeException ex( msg );" + newLine);
      output.write("    env->Throw( static_cast<jthrowable>( env->NewLocalRef( ex.getJavaJniObject() ) ) );" +
                   newLine);
      output.write("    return " + returnValue + ";" + newLine);
      output.write("  }" + newLine);
      output.write("}" + newLine);
      output.write(newLine);
    }
  }

  /**
   * Generates the appropriate C function name for the native method.
   *
   * Note: There are two forms of native method names: short, which is the simple
   * method, and long, which is used in the presence of overloading. This method
   * always returns the long form of the method name which includes the argument signature,
   * as it should always work - with or without the presence of overloading.
   *
   * @param metaClass the class
   * @param method the method
   * @return the C++ function name
   */
  private String getNativeMethodName(MetaClass metaClass, ClassMethod method)
  {

    List parameterTypes = method.getParameterTypes();

    StringBuilder nativeName = new StringBuilder("Java_");
    String mangledClassName = metaClass.getFullyQualifiedName("/");
    mangledClassName = mangleName(mangledClassName);
    String mangledMethodName = mangleName(method.getName());

    nativeName.append(mangledClassName);
    nativeName.append("_");
    nativeName.append(mangledMethodName);
    nativeName.append("__");

    for (Iterator it = parameterTypes.iterator(); it.hasNext();)
    {

      String type = (String) it.next();

      // If this is a class type, make sure it ends with a semi-colon.
      if (type.startsWith("L") && !(type.charAt(type.length() - 1) == ';'))
      {
        type += ";";
      }

      type = mangleName(type);
      nativeName.append(type);
    }

    return nativeName.toString();
  }

  /**
   * Converts a Java class name into a legal C++ name.
   *
   * @param name the Java class name
   * @return the C++ class name
   */
  private String mangleName(String name)
  {

    StringBuilder newName = new StringBuilder(name);

    for (int i = 0; i < newName.length(); ++i)
    {

      char c = newName.charAt(i);

      if (c == '/')
      {
        newName.replace(i, i + 1, "_");
      }
      else if (c == '_')
      {
        newName.replace(i, i + 1, "_1");
        ++i;
      }
      else if (c == ';')
      {
        newName.replace(i, i + 1, "_2");
        ++i;
      }
      else if (c == '[')
      {
        newName.replace(i, i + 1, "_3");
        ++i;
      }
      else if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
      {
        StringBuilder unicodeRepresentation = new StringBuilder(Integer.toHexString(c));
        while (unicodeRepresentation.length() < 5)
        {
          unicodeRepresentation.insert(0, '0');
        }
        unicodeRepresentation.insert(0, '_');
        newName.replace(i, i + 1, unicodeRepresentation.toString());
        ++i;
      }
    }

    return newName.toString();
  }

  /**
   * Returns all classes a class depends on.
   *
   * @param classFile the class
   * @return the classes the class depends on
   */
  private static Set<MetaClass> getDependencies(ClassFile classFile)
  {
    MetaClass metaClass = MetaClassFactory.getMetaClass(classFile.getClassName()).proxy();

    Set<MetaClass> result = new HashSet<MetaClass>();
    result.add(metaClass);

    for (ClassMethod method: classFile.getMethods())
    {
      if (!method.getName().equals("<init>"))
      {
        // Skip constructor return types
        MetaClass returnType = MetaClassFactory.getMetaClass(method.getReturnType()).proxy();
        addDependentClass(result, returnType);
      }

      for (TypeName parameter: method.getParameterTypes())
      {
        MetaClass parameterType = MetaClassFactory.getMetaClass(parameter).proxy();
        addDependentClass(result, parameterType);
      }

      for (TypeName exception: method.getExceptions())
      {
        MetaClass exceptionType = MetaClassFactory.getMetaClass(exception).proxy();
        addDependentClass(result, exceptionType);
      }
    }

    return result;
  }

  /**
   * Adds a dependency into a set.
   *
   * @param dependencies the set
   * @param dependency the dependency
   */
  private static void addDependentClass(Set<MetaClass> dependencies, MetaClass dependency)
  {
    if (dependency instanceof ArrayMetaClass)
    {
      ArrayMetaClass arrayType = (ArrayMetaClass) dependency;
      MetaClass baseType = arrayType.getBaseClass();
      dependencies.add(baseType);
      return;
    }
    dependencies.add(dependency);
  }

  /**
   * Opens the namespace block.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void beginNamespace(Writer output) throws IOException
  {
    ClassPackage classPackage = metaClass.toPeer().getPackage();

    StringBuilder namespace = new StringBuilder("BEGIN_NAMESPACE_");
    namespace.append(classPackage.getPath().size());
    namespace.append("( ");
    namespace.append(classPackage.toName(", ", false));
    namespace.append(" )");
    output.write(namespace + newLine);
  }

  /**
   * Closes the namespace block.
   *
   * @param output the output writer
   * @throws IOException if an error occurs while writing
   */
  private void endNamespace(Writer output) throws IOException
  {
    ClassPackage classPackage = metaClass.toPeer().getPackage();

    StringBuilder namespace = new StringBuilder("END_NAMESPACE_");
    namespace.append(classPackage.getPath().size());
    namespace.append("( ");
    namespace.append(classPackage.toName(", ", false));
    namespace.append(" )");
    output.write(namespace + newLine);
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
    return "Usage: PeerGenerator <class file> " + newLine +
           "<destination_header_directory> <destination_source_directory>" + newLine +
           "<user_defined_members = {true|false}>" + newLine;
  }

  public static void main(String[] args)
  {

    if (args.length != 4)
    {
      System.out.println(getUsage());
      return;
    }

    String classFilePath = args[0];
    String includeDir = args[1];
    String sourceDir = args[2];
    boolean userDefinedMembers = Boolean.valueOf(args[3]).booleanValue();

    PeerGenerator generator = new PeerGenerator(new ClassFile(classFilePath), includeDir, sourceDir,
      userDefinedMembers);
    Logger log = generator.getLogger();
    log.info("Beginning Peer generation.");
    try
    {
      generator.generate();
    }
    catch (IOException e)
    {
      log.error("", e);
    }
    log.info("Finished Peer generation.");
  }
}