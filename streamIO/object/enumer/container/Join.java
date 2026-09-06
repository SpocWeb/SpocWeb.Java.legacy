package streamIO.object.enumer.container;

import tester.ITester;

/**This Class implements a Join Filter on a Container, i.e.
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: cf3e76b36193b10ed733e39ee6b564f02286cdbdf7d4da03db33d378e3e1f866
 * stale: false
 * -->
 * it returns only those Rows, where Columns i and j are identical. */
public class Join
implements ITester {

	/**Columns tested for Equality	 */
	private int i, j;

	/**Constructor to initialize the Columns checked for equality	 */
	public Join(int i_, int j_)	{ i = i_; j = j_;}

	/**
	 * Returns whether columns {@code i} and {@code j} of the given {@link Container}
	 * row hold equal values.
	 *
	 * @param arg the {@link Container} row to test
	 * @return true when the values at columns {@code i} and {@code j} are equal, false
	 *         when they differ or either column lookup fails
	 */
	public boolean test(Object arg)	{
		try {
		return  ((Container) arg).getAt(i).equals
				(((Container) arg).getAt(j));
		} catch(NoSuchMethodException x) {
			return false; 
		}
	}
	
}
