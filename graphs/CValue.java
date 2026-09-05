package graphs;

/**
  * Minimal, mutable holder for a single Object Value, implementing {@link ICValue}.
  * The Value is exposed as a public Field for fast direct Access as well as through
  * {@link #getVal()}.
  * @see graphs.Value which adds a public setVal() Method
  * @author 	Matthias Heuer
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:25Z
  * digest: ff5062ea16b8cebeb428c168c0442a1dbeb8c15d82027fcfdb3bc6db9a072524
  * stale: false
  * tags: [code/graph_element]
  * concepts: [Comparable Value Holder]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public class CValue
implements ICValue {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Value of this Pair
	  * Due to the Fact that most Algorithms use the Interface,
	  * and the actual Class is not relied upon,
	  * the public Properties should not be tampered with
	  * and are only for Performance Boosting in critical Situations.
	  */
	public Object val;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ICValue: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @return Value */
	final public Object getVal() { return val; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public CValue() { }

	/** Empty Constructor	 */
	public CValue(final Object Value) { val = Value; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Object Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the wrapped Value converted to its String representation.
	 * @see java.lang.Object#toString()	 */
	public String toString() { return String.valueOf(val); }
	
}

