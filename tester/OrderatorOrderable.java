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
 */
final public class OrderatorOrderable
extends AComparator
implements IComparator {

	/** only a single Instance is necessary 	*/
	final static public OrderatorOrderable Orderator = new OrderatorOrderable(); 
	
	/** Singleton Constructor 	 */
	private OrderatorOrderable() { }

	/** @see tester.IOrderator#less(java.lang.Object, java.lang.Object)	 */
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
