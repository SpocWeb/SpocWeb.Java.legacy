/*
 * Created on 16.03.2005
 *
 * Orderator that reflects back on the Comparable Interface of the Argument Objects. 
 */
package tester;

/**
 * Orderator that reflects back on the Comparable Interface of the Argument Objects. 
 *  
 * It is advantageous to use Comparators in all sorted Containers, 
 * even when the Default Comparator just reflects back to the Objects "compare()" Method. 
 * 'null' for the Comparator should be equivalent to this reflecting Implementation 
 * to speed up Implementation by saving a (virtual) Call, even if it can be inlined. 
 * 
 * @author heuerm
 *
 */
final public class OrderatorComparable 
extends AComparator
implements IComparator {

	final static public OrderatorComparable Orderator = new OrderatorComparable(); 
	
	/** Singleton Constructor 	 */
	private OrderatorComparable() { }
	
	/** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final Object o1, final Object o2) {
		if (o1 == o2) 
			return 0; 
		//if (o1 == null) //null cannot be compared to anything, except for itself!
		//	return -compare(o2, o1); 
		return ((Comparable) o1).compareTo(o2); 
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
