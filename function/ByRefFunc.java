package function;

/**
 * ByRefFunc.java
 * Allows ByRef Transport of Function Objects
 * and returns the Function Object's Value in Map().
 * So this is practically a (faster) Concatenation with the Identity Function.
 *
 * Created on 26. Dezember 2000, 18:18
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:12:24Z
 * digest: edfc3103d0bf7caecbccf691e46bac81516751ec9ea8188c6fc2fc123e3a92c0
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class ByRefFunc
extends AFunction { //absStatic {

	/**Actual Function that is evaluated on 'Map()'  */
	public IFunction Value; // = this; //leads to an infinite Recursion! 

	/** Returns arg Mapped by this Object: this.Map(arg) == this�arg
	 * This is the Function working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.	  */
	public Object Map(final Object arg) { return Value.Map (arg); }

}
