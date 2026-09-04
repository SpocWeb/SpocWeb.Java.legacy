/*
 * Created on 19.03.2005
 *
 * Comparator to use with the Index. 
 */
package function.index;

import java.util.Comparator;

/**
 * Allows to use a Comparator with IndexElement Objects. 
 * @author heuerm
 *
 */
final public class IndexComparator 
implements Comparator {

	/** the Comparator to use for the Values 	 */
	protected final Comparator comparator; 
	
	/**
	 * Initializing Constructor 
	 * @param _valueComparator the Comparator to use for the Values
	 */
	public IndexComparator(final Comparator _valueComparator) { 
		this.comparator = _valueComparator; }

	/** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final IndexEntry o1, final IndexEntry o2) {
		return comparator.compare(o1.key, o2.key);
	}

	/** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final Object o1, final Object o2) {
		return compare((IndexEntry) o1, (IndexEntry) o2);
	}

}
