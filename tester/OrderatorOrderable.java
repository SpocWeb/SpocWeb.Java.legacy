/*
 * Created on 16.03.2005
 *
 * Orderator which relies on the Objects to implement IOrderAble 
 */
package tester;

import function.IIOrderAble;

/**
 * Orderator which relies on the Objects to implement IOrderAble 
 * 
 * It is advantageous to use Comparators in all sorted Containers, 
 * even when the Default Comparator just reflects back to the Objects "compare()" Method. 
 * 'null' for the Comparator should be equivalent to this reflecting Implementation 
 * to speed up Implementation by saving a (virtual) Call, even if it can be inlined. 
 * 
 * @see function.IIOrderAble
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:10:12Z
 * digest: 9092e07eee4624b63817847c0bc75de049256dec9e938a1dd93ab7eefc627dcd
 * stale: false
 * tags: [code/comparator]
 * concepts: [Orderable-Based Orderator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final public class OrderatorOrderable
extends AComparator
implements IComparator {

	/** only a single Instance is necessary 	*/
	final static public OrderatorOrderable Orderator = new OrderatorOrderable(); 
	
	/** Singleton Constructor 	 */
	private OrderatorOrderable() { }

	/** Returns whether a is less than b by delegating to {@link IIOrderAble#isLessThan(Object)} on a.
	 * @see tester.IOrderator#less(java.lang.Object, java.lang.Object)	 */
	public boolean less(final Object a, final Object b) {
		return ((IIOrderAble) a).isLessThan(b);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * tests the given Parameters
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length == 2) 
			System.out.println(Orderator.compare(args[0], args[1])); 
		else 
			MetricByHash.testIt(Orderator); 
	}
	
}
