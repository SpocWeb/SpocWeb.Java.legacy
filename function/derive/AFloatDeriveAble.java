package function.derive;

//import Stream.Copy.ACopyAble;	//Exception in Hierarchy here and in AConst
import streamIO.object.IStreamIn;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;

/**Title:        AFloatDeriveAble<p>
  * Description:  Defines Interfaces and Default Implementations
  * 	for deriveable real valued Functions. <p>
  * Copyright:    Copyright (c) Matthias Heuer<p>
  * Company:      personal<p>
  * Design
  * Decisions: This class is the Base Class for most Singletons.
  * 		  Singletons are used, because Map() is faster than with Delegation
  * 		  (VMT Lookup instead of calling Method of local Variable),
  * 		  although the number of Classes increases considerably.
  * 		  Equivalent Representations could be added as Member Variables.
  * @author Matthias Heuer
  * @version 1.0
  * Design
  * Decisions: R is the only complete Space where Derivatives can be defined
  * 		  so it is only natural to implement the Interface 'IFloatFunction'
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:14:54Z
  * digest: a74a980c5fb4958754067b9483ac83e51b9a25f19cf92dfa273146f969965e69
  * stale: false
  * tags: [code/derivable_function_contract, code/numerical_differentiation]
  * concepts: [Calculus, Singleton Pattern]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class AFloatDeriveAble
extends ADeriveAble
implements IFloatDeriveAble { //
    
	///////////////////////////////////////////////////////////////////////////
	//  abstract Methods
	///////////////////////////////////////////////////////////////////////////
    
	/** Returns the Function Value (mapping) of the Argument arg  */
	public abstract double Map (final double arg); // { return Map((float ) arg); }
	
    /**Reports that this Function imposes no particular serialization order on its arguments.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/**Returns the Derivative of the Function	 */ //relies on assumptions.
	public abstract double getDerivative(final double x); // { return ((IFloatFunction) Derivative()).Map(x); }
	
	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public abstract double getFuncDerive (final double x, final ByRefDouble derivative); //too slow
		//derivative.Value = Derivative(x);
		//return Map(x); }
	
	///////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns the Function Value (mapping) of the Argument arg  */
	public float  Map (final float  arg) { return (float) Map((double) arg); }
	
	/**Returns the Derivative of the Function at Point x	 */
	public float getDerivative(final float x) { return (float) getDerivative((double) x); }
	
	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public float getFuncDerive (final float x, final ByRefFloat derivative) {
		final ByRefDouble tmp = new ByRefDouble();
		float ret = (float) getFuncDerive(x, tmp);
		derivative.Value = (float) tmp.Value;
		return ret; }
	
}

