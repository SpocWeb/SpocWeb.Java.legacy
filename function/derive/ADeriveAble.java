package function.derive;

import function.AInvertAble;

/**Title:        ADeriveAble<p>
 * Description:  Defines Interfaces and Default Implementations for deriveable Functions. <p>
 * Copyright:    Copyright (c) Matthias Heuer<p>
 * Company:      personal<p>
 * Design
 * Decisions: This class is the Base Class for most Function Singletons.
 * 			  Singletons are used, because Map() is faster than with Delegation
 * 			  (VMT Lookup instead of calling Method of local Variable),
 * 			  although the number of Classes increases considerably.
 * 			  Equivalent Representations could be added as Member Variables.
 * @author Matthias Heuer
 * @version 1.0
 * Design
 * Decisions: R is the only complete Space where Derivatives can be defined
 * 			  so it is only natural to implement the Interface 'IFloatFunction'
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:14:51Z
 * digest: 75d6bc4371fcf02ba3586ce7bde566ae3c3caf500906d478e3261da487149728
 * stale: false
 * tags: [code/derivable_function_contract, code/derivative_calculation]
 * concepts: [Calculus, Singleton Pattern]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public abstract class ADeriveAble
extends AInvertAble
implements IDeriveAble { //

	///////////////////////////////////////////////////////////////////////////
	//	static Members
	///////////////////////////////////////////////////////////////////////////

	///////////////////////////////////////////////////////////////////////////
	//	Members
	///////////////////////////////////////////////////////////////////////////

	/**Local Storage for the Integral to be set by setIntegral()	 */
	protected IDeriveAble Integral;

	/**Sets the Integral from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	public void	setIntegral(IDeriveAble integral) {
		if (this.Integral == integral) return; //prevent Recursion
		if (this.Integral != null) return; 
//		if(!this.Integral.equals(integral)) { //take this out,
//			throw new IllegalStateException(); } //because the Integral is not unique!
			this.Integral  = integral;
			this.Integral.setDerivative (this);  }

	/**Returns the Integral of this Function	 */
	public IDeriveAble getIntegral() {
		if (Integral == null)
			throw new AbstractMethodError(); // realize early that an Error occurred!
		return Integral; }

	/**Local Storage for the Integral to be set by setIntegral()	 */
	protected IDeriveAble Derivative;

	/**Sets the Derivative from outside
	 * This can be done only once, after that an IllegalStateException is thrown.	 */
	public void	setDerivative(IDeriveAble derivative) {
		if (this.Derivative   ==   derivative) return; //prevent Recursion
		if (this.Derivative   !=   null)
		if(!this.Derivative.equals(derivative)) {
			throw new IllegalStateException(); }
			this.Derivative    =   derivative; //close the Reference
			this.Derivative.setIntegral (this); } //this could overwrite the Integral!

	/**Returns the Derivative of this Function	 */
	public IDeriveAble getDerivative() {
		if (Derivative == null) {
			throw new AbstractMethodError("No Derivative defined for "+this); } // realize early that an Error occurred!
		return Derivative; }

	/**Returns the n-th Derivative of this Function
	 * Negative n denote Integration  */
	public IDeriveAble getDerivative(int n) { return Derivative(this, n); }
/*		if (n == 0) return this; //recursive Implementation
		if (n >  0) return Derivative().Derivative(--n);
					return Integral  ().Derivative(++n); }
*/

	/**Walks the derivative/integral chain of {@code f} by {@code n} steps and returns the result.
	 * Negative n denote Integration
	 * @param f the Function whose derivative chain is walked
	 * @param n the number of steps; positive differentiates, negative integrates
	 * @return the n-th Derivative of this Function	 */
	public static IDeriveAble Derivative(IDeriveAble f, int n) {
		while (--n >  0) f = f.getDerivative();
		while (++n <  0) f = f.getIntegral  ();
		return f; } //iterative Implementation

	/**Returns the fully qualified Function Name for (De-)Serialization
	 * Should be overwritten by parameterized Functions.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return getClass().getName(); }

}

