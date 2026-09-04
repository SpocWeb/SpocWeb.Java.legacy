package tester; // Function;

import java.util.Comparator;

/**
  * Interface defining an Equivalence Relation and 
  * an according HashCode Function
  * because both must always be associated to work with Hash based Containers
  * i.e. equals(A,B) == true => HashCode(A) == HashCode(B)
  *
  * This Interface defines a still discrete Topology,
  * because the Return Type of 'int' prevents infinite "Closeness"
  *
  * Related Interfaces: 
  * @see java.util.Comparator Interface defines the same    Method compare(A,B)
  * @see java.lang.Comparable Interface defines the single Arg Method A.compareto(B)
  *
  * SubClasses:
  * @see IScalarMetric for a consistent Topology Metric
  * 
  * Implementors:
  * @see tester.Discrete 
  * @see tester.MetricByHash
  * @see tester.MetricMeasurAble
  * @see tester.OrderatorComparable
  * @see tester.OrderatorOrderable
  *
  */
public interface IComparator
extends IOrderator, Comparator {

	/**
	  * Compares its two arguments for order.
	  * Returns a negative integer, zero, or a positive integer
	  * as the first argument is less than, equal to, or greater than the second.
	  * The implementor must ensure that sgn(compare(x, y)) == -sgn(compare(y, x))
	  * for all x and y.
	  * (This implies that compare(x, y) must throw an exception
	  * if and only if compare(y, x) throws an exception.)
	  *
	  * The implementor must also ensure that the relation is transitive:
	  * ((compare(x, y)>0) && (compare(y, z)>0)) implies compare(x, z)>0.
	  *
	  * Finally, the implementer must ensure that compare(x, y) == 0 implies that
	  * sgn(compare(x, z))==sgn(compare(y, z)) for all z.
	  *
	  * It is generally the case, but not strictly required that
	  * (compare(x, y)==0) == (x.equals(y)).
	  * Generally speaking, any comparator that violates this condition
	  * should clearly indicate this fact.
	  * The recommended language is
	  * "Note: this comparator imposes orderings that are inconsistent with equals."
	  *
	  * @param o1 - the first object to be compared.
	  * @param o2 - the second object to be compared.
	  * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	  * @throws ClassCastException - if the arguments' types prevent them from being compared by this Comparator.
	  */
	//public int compare(final Object A, final Object B);
	
}
