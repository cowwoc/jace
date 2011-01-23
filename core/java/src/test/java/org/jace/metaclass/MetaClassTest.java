package org.jace.metaclass;

import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Tests PeerGenerator.
 *
 * @author Gili Tzabari
 */
public class MetaClassTest
{
	/**
	 * Asserts that a MetaClass' proxy() and unproxy() methods are symmetrical.
	 *
	 * 
	 * @param metaClass the MetaClass object
	 * @return the unproxied MetaClass object
	 */
	private void assertProxing(MetaClass metaClass)
	{
		assertEquals(metaClass.proxy().unProxy(), metaClass);
	}

	/**
	 * Test handling multi-dimensional arrays.
	 */
	@Test
	public void testMultiDimensionalArrays()
	{
		assertProxing(new BooleanClass(false));
		assertProxing(new ByteClass(false));
		assertProxing(new CharClass(false));
		assertProxing(new ClassMetaClass("String", new ClassPackage(Arrays.asList("java", "lang"))));
		assertProxing(new DoubleClass(false));
		assertProxing(new FloatClass(false));
		assertProxing(new IntClass(false));
		assertProxing(new LongClass(false));
		assertProxing(new ShortClass(false));
		assertProxing(new VoidClass(false));
		assertProxing(new ArrayMetaClass(new BooleanClass(false)));
		assertProxing(new ArrayMetaClass(new ArrayMetaClass(new BooleanClass(false))));
	}
}
