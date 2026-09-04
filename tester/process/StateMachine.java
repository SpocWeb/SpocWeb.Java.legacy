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

	/** @return TODO:   */
	public Object getState() { return state; }

	/** sets the TODO: 	 */
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

	/** @return the State of this State Machine,
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

