/*
 * Created on 19.03.2005
 * @author heuerm
 *
 */
package tester;

/**
 * minimum Interface for most Sorting Applications. 
 * Two Objects for which is: false == less(a,b) == less(b,a) 
 * are either incomparable or should be considered equal. 
 * 
 * This is a weaker, but usually sufficient Interface for sorting and finding.
 * 
 * Implementations: 
 * @see java.util.Comparator Interface defines the same Method compare(A,B)
 * @see java.lang.Comparable Interface defines the same Method compareto(B)
 * 
 * @see java.util.Comparator which requires a more heavyweight Implementation. 
 * @author heuerm
 * 
 */
public interface IOrderator 
extends IEquivalence {

	/**
	 * returns true, when a is less than b 
	 * @param a first Object to compare
	 * @param b second Object to compare 
	 * @return true, when a is less than b 
	 */
	boolean less(final Object a, final Object b);
	
}
