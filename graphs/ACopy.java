package graphs;

/** Default Implementation of Interface ICopy
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 869682918298db3d61f7b14e57309c8c1574194f815858861e6fce6fd8b5f1d5
 * stale: false
 * tags: [code/graph_edge]
 * concepts: [Copyable Base Class]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class ACopy
implements ICopy {

	/////////////////////////////////////////////////////////////////////////////////
	//	Interface ICopy
	/////////////////////////////////////////////////////////////////////////////////

	/**Creates a new shallow Copy of this Instance.
	 * I.e. both Instances will share their inner Components.
	 * shallowCopy also clones the Types, but does not initialize them!
	 * rarely used.
	 * This is the Default Implementation and should always work,
	 * although a direct Implementation could be faster.	 */
	public ICopy Copy() {
		try { return (ICopy) clone(); }
		catch(CloneNotSupportedException e) { //should never happen
			throw new IllegalStateException(e.toString()); } }

}
