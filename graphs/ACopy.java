package graphs;

/** Default Implementation of Interface ICopy 	 */
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
