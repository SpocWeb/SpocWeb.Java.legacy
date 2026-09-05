package tester.process;

import function.IFunction;
import function.IProcessor;
import graphs.Pair;

/**
  * Title: StateMachine<p>
  * Description:
  * Purpose:
  * State Machine to execute the TransitionByFunction
  *
  * Design Decisions / Implementation Details:
  * @see synch.StateMachine for a Machine working with int Numbers.
  *
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-16-2002, 10:49 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:13:18Z
  * digest: 7ea861cdbfc75d6064c70f9087d9236de59435300fd35ae11304ba9e13f7ae1a
  * stale: false
  * tags: [code/state_machine]
  * concepts: [Finite State Machine]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class StateMachine
implements IProcessor {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** The current State of this Machine	 */
	protected Object state;

	/** The Transition Function of this Machine	 */
	protected IFunction transit;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns this machine's current state.
	 * @return the current state	 */
	public Object getState() { return state; }

	/** Sets this machine's current state, bypassing the transition function.	 */
	public void setState(Object arg) { state = arg; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected StateMachine(IFunction transit_) {
		this.transit = transit_; }

	/** Initializing  Constructor	 */
	protected StateMachine(IFunction transit_, Object initialState) {
		this.transit = transit_;
		this.state = initialState; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IProcessor : Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Pair Object to look up the Combination (Input, State) in the Transition Function */
	protected Pair lookup = new Pair();

	/** Advances this machine to its next state by applying {@link #transit} to arg and the
	  * previous state.
	  * @return the State of this State Machine,
	  * reacting to the given Input and its previous State
	  * The Output Function must be a separate Map of this State to the Output Space
	  * and can be concatenated with this Mapping.
	  */
	public Object MapAt(Object arg) {
		lookup.Key = arg;
		return this.state = lookup.val = transit.Map(lookup); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + StateMachine.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

