/*
 * Metric which uses the HashCode Method exclusively. 
 * Doesn't make any Assumptions on the Objects compared. 
 * Created on 18.03.2005
 *
 */
package tester;

import streamIO.Assert;
import streamIO.Log;
import function.byref.ByRefChar;

/**
 * Metric which uses the HashCode Method exclusively. 
 * Doesn't make any Assumptions on the Objects compared. 
 * 
 * It is advantageous to use Comparators in all sorted Containers, 
 * even when the Default Comparator just reflects back to the Objects "compare()" Method. 
 * 'null' for the Comparator should be equivalent to this reflecting Implementation 
 * to speed up Implementation by saving a (virtual) Call, even if it can be inlined. 
 * 
 * @author heuerm
 *
 */
final public class MetricByHash 
extends AComparator
implements IScalarMetric {

	/** Singleton Instance 	 */
	final static public MetricByHash Metric = new MetricByHash(); 

	/** Singleton Constructor 	 */
	protected MetricByHash() { }

	///////////////////////////////////////////////////////////////////////////
	/// IFloatMetric
	///////////////////////////////////////////////////////////////////////////
	
	/** @see tester.IScalarMetric#dist(java.lang.Object, java.lang.Object)	 */
	public double dist(final Object a, final Object b) {
		if (a == b)
			return 0;
		return a.hashCode() - b.hashCode(); 
	}
	
	/** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final Object o1, final Object o2) {
		if (o1 == o2) 
			return 0; 
		final int h1 = o1.hashCode(); 
		final int h2 = o2.hashCode(); 
		if (h1 > h2)
			return 1;
		if (h1 < h2)
			return -1;
		return 0; 
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * tests the given Instance
	 */
	public static void testIt(final IComparator order) {
		Assert.EQUALS( 0 , order.compare(null, null));
		Assert.EQUALS( 0 , order.compare( new ByRefChar('A'), new ByRefChar('A')));
		Assert.EQUALS(-1 , order.compare( new ByRefChar('A'), new ByRefChar('B')));
		Assert.EQUALS( 1 , order.compare( new ByRefChar('B'), new ByRefChar('A')));
		try { order.compare(null, new ByRefChar('B'));
			Assert.FAIL("'null' should not be comparable to anything"); 
		} catch (final Exception x) {
			Log.N(x);
		}
	}
	
	/**
	 * tests the given Parameters
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length == 2) 
			System.out.println(Metric.compare(args[0], args[1])); 
		else 
			MetricByHash.testIt(Metric); 
	}
	
}
