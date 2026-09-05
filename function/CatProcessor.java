package function;

import graphs.ILinked;

/** Processor that concatenates two Processors
  * This is more modular and standalone than using the
  * @see ILinked Interface which allows to only link 'this' with another Object.
  * On the other hand none of the linked Processors know anything about their Use
  * in a linked structure, unlike in an ILinked structure. 
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:34:04Z
  * digest: efa14ac51ea352c50f9d15bf46524296f0d34946032efb8c7f237b2ae61ead9b
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class CatProcessor {
//implements Graph.ICPair { //Creates cross-dependencies and is of only documentary use

	/** Reference to the inner of the concatenated Functions	 */
	protected IProcessor inner;

	/** Reference to the outer of the concatenated Functions	 */
	protected IProcessor outer;

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the inner processor, applied first.
	 * @return the inner of the concatenated Functions	 */
	public IProcessor getInner() { return inner; }

	/** Returns the outer processor, applied to the inner processor's result.
	 * @return the outer of the concatenated Functions	 */
	public IProcessor getOuter() { return outer; }

	/** Accessor Method
	  * @return the key of the Pair */
	public Object getKey() { return inner; }
	
	/** Accessor Method
	  * @return the Value of the Pair */
	public Object getValue() { return outer; }

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Constructor for a concatenated Function, the Inverse is optional	 */
	public CatProcessor(IProcessor Outer, IProcessor Inner) {
		// TODO: LOGIC: this null-check reads the instance fields 'inner'/'outer', which are always
		// null at this point in the constructor (they are only assigned below), not the 'Inner'
		// parameter. The apparent intent - fall back to using Outer as inner when Inner is null -
		// never happens; a caller passing Inner == null ends up with inner == null and a
		// NullPointerException later from MapAt()/equals().
		if  (inner == null) { inner = outer; outer = null; }
		this.inner = Inner;
		this.outer = Outer;
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	 */
	final public Object MapAt(Object arg) {
//		if (arg.getClass().isArray()) return super.Function ((Object[]) arg);
		Object tmp = inner.MapAt(arg); if (outer == null) return tmp;
		return       outer.MapAt(tmp); }

	/** Returns the inner processor's string form, wrapped by the outer's when one is set.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() {
		String Return = inner.toString();
		if (outer != null) Return = "[" + outer.toString() + "]@(" + Return + ")";
		return Return; }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	final public boolean equals  (Object arg) {
		if (!(arg instanceof CatProcessor)) return false;
		CatProcessor arg_ = (CatProcessor) arg;
		return ((inner == arg_.inner) || inner.equals(arg_.inner)) &&
			   ((outer == arg_.outer) || outer.equals(arg_.outer));	}

}
