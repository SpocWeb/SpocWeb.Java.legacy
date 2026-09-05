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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:10:08Z
 * digest: 6ef4ed43681d352b5addfd986da1edae8b29665b88b0f24c88f95da7d91bc615
 * stale: false
 * tags: [code/comparator]
 * concepts: [Comparable-Based Orderator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final public class OrderatorComparable 
extends AComparator
implements IComparator {

	/** Shared singleton instance, since this Comparator is stateless. */
	final static public OrderatorComparable Orderator = new OrderatorComparable();

	/** Singleton Constructor 	 */
	private OrderatorComparable() { }

	/** Compares two objects via their own {@link Comparable#compareTo(Object)} implementation.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
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
