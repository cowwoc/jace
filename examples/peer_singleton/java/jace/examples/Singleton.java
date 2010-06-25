package jace.examples;

/**
 * Another Peer example.
 * This time we demonstrate a Singleton.
 *
 * @author Toby Reyelts
 *
 */
public class Singleton {

  private static int count; // number of native method calls made.
  private static String currentString;

  private static Singleton instance = new Singleton();

  private Singleton() {
    count = -1;
  }

  public static Singleton getInstance() {
    return instance;
  }

  /**
   * Play around some with some overloaded methods.
   *
   */
  public native void print();
  public native void print( String str );
  public native void print( String str1, String str2 );
  public native void print( String[] str );

  /**
   * Play around with some static native methods.
   *
   */
  private static native void printHelloWorld();
  private static native void print( int i );
  private static native void setString( String str );
  private static native String getString();
  private static native int getCount();

  public void run( String[] args ) {

    printHelloWorld();

    print();
    print( count );
    print( args[ 0 ] );
    print( count );
    print( args[ 0 ], args[ 1 ] );
    print( count );
    print( args );
    print( count );

    setString( "Hello" );
    System.out.println( getString() );

    setString( "World" );
    System.out.println( getString() );

    System.out.println( "Total native method calls made: " + getCount() );
  }

  public static void main( String[] args ) {

    if ( args.length < 2 ) {
      System.out.println( "Singleton: Need at least two arguments." );
      return;
    }

    Singleton singleton = Singleton.getInstance();
    singleton.run( args );
  }
}

