/*
 * Created on 18.03.2005
 *
 * Metric which relies on the Fact that the given Objects implement IMeasurAble
 */
package tester;

import function.IMeasurAble;

/**
 * Metric which reflects the Methods back to the given Objects, which must implement 
 * IMeasurAble, IOrderAble, OrderAble or Comparable respectively. 
 * 
 * It is advantageous to use Comparators in all sorted Containers, 
 * even when the Default Comparator just reflects back to the Objects "compare()" Method. 
 * 'null' for the Comparator should be equivalent to this reflecting Implementation 
 * to speed up Implementation by saving a (virtual) Call, even if it can be inlined. 
 * 
 * @see function.IMeasurAble 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:10:01Z
 * digest: 6e4e0034229206e573e3f42b103e3a271d4c59872a2fc6f57e0a40c42468867e
 * stale: false
 * tags: [code/metric_interface]
 * concepts: [Metric Base Class]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
final public class MetricMeasurAble
extends AComparator
implements IScalarMetric {

	/** Singleton Instance 	 */
	final static public MetricMeasurAble Metric = new MetricMeasurAble(); 
	
	/** Singleton Constructor 	 */
	private MetricMeasurAble() {}

	/** Returns whether a's measured value is less than b's, via {@link #dist(Object, Object)}.
	 * @see tester.IOrderator#less(java.lang.Object, java.lang.Object)	 */
	public boolean less(final Object a, final Object b) {
		return dist(a, b) < 0;
	}
	/** Returns the difference of the two objects' {@link IMeasurAble#getDouble()} values, or 0 when they are the same object.
	 * @see tester.IScalarMetric#dist(java.lang.Object, java.lang.Object)	 */
	public double dist(final Object a, final Object b) {
		if (a == b)
			return 0;
		// TODO: LOGIC: both operands call a.getDouble() - b's value is never read, so dist()
		// always returns 0 for distinct objects instead of their actual difference. Should be
		// ((IMeasurAble) a).getDouble() - ((IMeasurAble) b).getDouble().
		return ((IMeasurAble) a).getDouble() - ((IMeasurAble) a).getDouble();
	}

	/** Returns whether A and B have the same measured value, via {@link #dist(Object, Object)}.
	 * @see tester.IEquivalence#equals(java.lang.Object, java.lang.Object)	 */
	public boolean equals(final Object A, final Object B) {
		if (A == B)
			return true;
		return dist(A, B) == 0;
	}

	/** Returns a hash code derived from the object's {@link IMeasurAble#getFloat()} value.
	 * @see tester.IEquivalence#HashCode(java.lang.Object)	 */
	public int HashCode(final Object A) {
		return Float.floatToIntBits(((IMeasurAble) A).getFloat());
	}

	/** Compares two objects by their {@link IMeasurAble#getDouble()} values, returning -1, 0 or 1 accordingly.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final Object o1, final Object o2) {
		if (o1 == o2) 
			return 0; 
		final double h1 = ((IMeasurAble) o1).getDouble(); 
		final double h2 = ((IMeasurAble) o2).getDouble(); 
		if (h1 > h2)
			return 1;
		if (h1 < h2)
			return -1;
		return 0; 
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
