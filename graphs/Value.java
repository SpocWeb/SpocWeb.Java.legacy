package graphs;

/**
  * Extends {@link CValue} with a public {@link #setVal(Object)}, implementing
  * {@link IValue} for a mutable, read/write single-Value holder.
  * @see graphs.KeyValuePair
  * @see graphs.Pair
  * @author 	Matthias Heuer
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:29Z
  * digest: 8b1f92079d952aab897ddabf24ff33779654228f90c2fa841a452c9ea2eb6e08
  * stale: false
  * tags: [code/graph_element]
  * concepts: [Value Holder]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public class Value
extends CValue
implements IValue {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IValue: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @param sets Value of the Pair */
	final public void setVal(final Object Value) { this.val = Value; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public Value() { }

	/** Initializing Constructor,
	  * here not calling the Base Constructor to save the Call!	 */
	public Value(final Object Value) { val = Value; }
	
}

