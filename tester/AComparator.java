/*
 * Created on 19.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tester;

import streamIO.Assert;
import streamIO.Log;
import function.byref.ByRefChar;

/**
 * @author heuerm
 * 
 * Default Implementations for other Comparators 
 */
public abstract class AComparator 
implements IComparator {

	/**Fail-Safe Test for Equality (provided the equals() Method can handle null).  
	 * @see tester.IEquivalence#equals(java.lang.Object, java.lang.Object)	 */
	public boolean equals(final Object A, final Object B) {
		if (A == B) 
			return true; 
		if (A == null)
			return false; 
		return A.equals(B);
	}

	/** @see tester.IEquivalence#HashCode(java.lang.Object)	 */
	public int HashCode(final Object A) {
		return A.hashCode();
	}

	/**Quite ineffective Method, since compare returns much more Information  
	 * @see tester.IOrderator#less(java.lang.Object, java.lang.Object)	 */
	public boolean less(final Object a, final Object b) {
		return compare(a, b) < 0;
	}

	/** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final Object o1, final Object o2) {
		if (o1 == o2) 
			return 0; 
		if (less(o2, o1))
			return 1;
		if (less(o1, o2))
			return -1;
		return 0; 
	}

	/**
	 * tests the given Instance
	 */
	public static void testIt(final IScalarMetric metric) {
		Assert.EQUALS( 0 , metric.dist(null, null));
		Assert.EQUALS( 0 , metric.dist( new ByRefChar('A'), new ByRefChar('A')));
		Assert.EQUALS(-1 , metric.dist( new ByRefChar('A'), new ByRefChar('B')));
		Assert.EQUALS( 1 , metric.dist( new ByRefChar('B'), new ByRefChar('A')));
		Assert.EQUALS(-2 , metric.dist( new ByRefChar('A'), new ByRefChar('C')));
		Assert.EQUALS( 2 , metric.dist( new ByRefChar('C'), new ByRefChar('A')));
		try { metric.dist(null, new ByRefChar('B'));
			Assert.FAIL("'null' should not be comparable to anything"); 
		} catch (final Exception x) {
			Log.N(x);
		}
	}
	
}
