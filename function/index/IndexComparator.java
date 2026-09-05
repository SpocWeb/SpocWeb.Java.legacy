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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:41:20Z
 * digest: dc4c8c47c6824b4e517e5defc113c304a0222519fa16c80c50270a1e8eda30e6
 * stale: false
 * tags: [code/indexing]
 * concepts: [Indexed Collection Access]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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

	/** Compares two entries by their {@link IndexEntry#key} values, using the wrapped comparator.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final IndexEntry o1, final IndexEntry o2) {
		return comparator.compare(o1.key, o2.key);
	}

	/** Casts both arguments to {@link IndexEntry} and delegates to the typed overload.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)	 */
	public int compare(final Object o1, final Object o2) {
		return compare((IndexEntry) o1, (IndexEntry) o2);
	}

}
