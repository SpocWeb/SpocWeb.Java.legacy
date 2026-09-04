package tester.process;

import function.AFunction;

/**
  * Complete Implementation of an Automaton: a[x][q] -> q
  * The InPut x, States q and OutPut Q are generic Objects.
  * The Value of the Coefficient q' = a[x,q] represents the next State
  * They represent the State Change Function Lambda.
  * 	The Output Function Beta q -> Q can either be based on the State  (Moore Automaton)
  * 	or the State and the Input Value (Mealy Automaton) [q,x] -> Q
  * This is not made an Interface, because there is only this Type of Automaton
  * except for more type-safe Automatons. 
  *
  * The interesting Thing about Automatons is that they are reCoupled,
  * i.e. their current State is an Input to the next State.
  *
  * The Automaton walks along the (infinite) Graph
  * and their current Position corresponds to their State Object being a Node.
  * Several Automatons can process the same (infinite) Graph!
  * Sometimes these Automatons have to synchronize themselves.
  * This is the Graph View of concurrent Processing. 
  * 
  * @see stringOp.search.RegExp implements a non-deterministic Automaton 
  */
public class Automaton
extends AFunction {
	
	////////////////////////////////////////////////////////////////////////////
	//	static Members
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////
	
	/** Transition Function for the next State	 */
	protected Operator Lambda;
	
	/** Mapping Function for the Output from State and Input	 */
	protected Operator Output;
	
	/** The current State of the Automaton.
	  * Made public, because it may be read and set, although rarely,
	  * why it also could have been made protected using get/set Methods. */
	protected Object mState;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** Sets the current State of the Automaton.
	  * Usually only done on Instatiation.  */
	public void setState(Object State) {
		mState = State; }
	
	/** @return the current State of the Automaton.
	  * Also returned by Operation if the Output Function is null. */
	public Object getState() { return mState; }
	
	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructor taking the Transition Function and the Output Function
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public Automaton(final Operator Lambda, final Operator Output, final Object State) {
		this.Output = Output;
		this.Lambda = Lambda;
		this.mState = State ; }


	/** Constructor taking the Transition Function and the Output Function
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned.
	  * The State can be set and modified after Creation.	 */
	public Automaton(final Operator Lambda, final Operator Output) {
		this(Lambda, Output, null); }
	
	/** Constructor taking the Transition Function and the Output Function
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public Automaton(final Operator Lambda) {
		this(Lambda, null, null); }
	
	/** Constructor taking the Transition Function and the Output Function
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public Automaton() { this(null, null, null); }
	
	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Single Step Operation of this Automaton.
	  * Performs both State Transition and calculation of the Output Function.
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public Object Map(final Object InPut) {
		if (Lambda == null) mState = InPut; else mState = Lambda.Operation(InPut, mState);
		if (Output == null) return  mState; else return   Output.Operation(InPut, mState); }
	
	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests all Methods of this Class	 */
	public static void testIt(String[] args) {
		System.out.println("Testing " + Automaton.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
