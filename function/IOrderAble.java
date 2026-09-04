package function;

/**OrderAble:
 * Interface for a Class whose Objects have a strict Order Relation ">" resp. "<".
 * Complements the pure virtual Interface 'intOrderable'.
 * If a Set is not ordered completely, these Relations are not defined
 * for any two Elements. In this Case, both >= and <= give False.
 * When the Element is the same or equivalent, both >= and <= give True.
 * When the Element is comparable, only one gives True.
 *
 * Absolute Value is only important because of the Metric defined by "<".
 * A Default Implementation is done in 'absOrderable'.
 */
public interface IOrderAble
extends IIOrderAble, Comparable {
	
	/** Sloppy (on Equality) but fast 'between' Implementation
	  * @param arg1 : first  Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return True, when 'Self' is between arg1 and arg2
	  */
	boolean isBetween (final Object arg1, final Object arg2);
	
	/** greater: '>' Returns True, when 'Self' > arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	boolean isMoreThan (final Object arg);
	
	/** greater or equal: '>=' Returns True, when 'Self' >= arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	boolean notLessThan (final Object arg);
	
	/** less or equal: '<=' Returns True, when 'Self' <= arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	boolean notMoreThan (final Object arg);
	
	/** Returns the Position of this Object relative to arg:
	  * This Operation is leaner than compareTo.
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 for smaller, otherwise +1
	  */
	int Position(final Object arg);
	
	/**
	  * @return Sign(this-arg), the exact Position of this Object relative to arg
	  * Not added here, since inherited from @see Comparable#compareTo.
	  *
	  * The Difference is that the Absolute Value returned by
	  * is not defined, whereas it should be 1 with all Implementors of this Interface,
	  * because it is used to calculate Interval Overlappings etc.
	  *
	  * @see java.lang.Comparable#compareTo
	  * @see java.util.Comparator#compare
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  * -1 for smaller, 0 for equal, otherwise +1
	  */
//	int compareTo(final Object arg);
	
}
