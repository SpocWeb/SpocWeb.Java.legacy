package streamIO.copy.monoid;

import java.io.IOException;

import streamIO.AStreamOut;
import streamIO.IIStreamIn;
import streamIO.IStreamOut;
import function.IInvertAble;

/** Abstract Class that implements both the IInvertAble and the Monoid Interface
  * Sublasses:
  * @see Relation
  * @see Association
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:41:47Z
  * digest: a8977c5db1c4c5c988b6864757ce0465a962cf3df6b7318a9e315424472fffaa
  * stale: false
  * tags: [code/abstract_base, code/concatenation]
  * concepts: [Monoid, Function Mapping]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class AMapper
extends AMonoid
implements IInvertAble, IStreamOut {

	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** cached Reference to the Inverse Mapper	*/
	protected IInvertAble mInverse; //= null;

	///////////////////////////////////////////////////////////////////////////
	// Interface IStreamOut
	///////////////////////////////////////////////////////////////////////////

	/**Does nothing; there is no buffered output to flush at this level.
	 * @see streamIO.IStreamOut#flush()	 */
	public void flush() throws IOException { }
	
	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but not recursively, but only flattened by one Level (flatDepth == 1).	  */
	public long addItems(final Object arg) { return AStreamOut.ADD_ITEMS(this, arg, 1); }

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is analyzed, i.e. Containers Contents is added recursively,
	  * up to the given flatDepth.	  */
	public long addItems(final Object arg, final int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }

	/** adds these Items to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(final Object[] arg) { 
		return AStreamOut.ADD_ITEMS(this, arg); }

	/** adds all Items from the Enumerator to the Store in Place: +=
	  * @return the Output streamIO for adding further Items
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public long addItems(final IIStreamIn Iter) { 
		return AStreamOut.STREAM(Iter, this); }
	
	///////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IInvertAble: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Sets the Inverse from outside.
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setInverse(IInvertAble inverse) {//AMapper inverse) {
		if (this.mInverse   ==   inverse) return; //prevent Recursion
		if (this.mInverse   !=   null)
		if(!this.mInverse.equals(inverse)) throw new IllegalStateException();
		    this.mInverse    =   inverse;
		    this.mInverse.setInverse (this);  }

	/**Returns the cached inverse mapper, computing and caching it via {@link #rev()} on first use.
	 * @return the Inverse, cached for here for all inheriting Classes */
	public IInvertAble getInverse() { //
		if (mInverse == null) return rev(); // fail fast Method! realize early that an Error occurred!
		return mInverse; }

	/**Inversion: Id\x
	 * A Standard Implementation. Can be overwritten by faster Implementations.	 */
	public IMonoid rev() {
		if (mInverse != null)
			return (IMonoid) mInverse;
			return (IMonoid)(mInverse =(IInvertAble) ((IMonoid) self).pamAt(((IMonoid) self).Identity())); }

	/**Returns arg Mapped in Place by this Object: this.MapAt(arg) this=�arg
     * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMap(Object arg) {
		if (mInverse == null)
			getInverse(); //Optimization!
		return mInverse.UnMap(arg); }

	/**Returns arg Mapped in Place by this Object: this.MapAt(arg) this=�arg
     * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object UnMapAt(Object arg) {
		if (mInverse == null)
			getInverse(); //Optimization!
		mInverse.UnMapAt(arg);
        return arg; }

	/**This applies the Function to each Item of an Array of Objects	 */
	public Object[] UnMap(Object[] arg) {
		if (mInverse == null)
			getInverse(); //Optimization!
		int Length = arg.length;
		Object[] Return = new Object[Length];
		while (--Length >= 0)
			Return[Length] = mInverse.Map(arg[Length]);
		return Return; }

}
