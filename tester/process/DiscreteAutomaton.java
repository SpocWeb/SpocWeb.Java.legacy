package tester.process;

import function.byref.ByRefInt;

/**
  * DiscreteAutomaton.java
  * Complete Implementation of an Automaton: a[x][q] -> q
  * The InPut is a generic Object, the States is an Integer.
  * The Value of the Coefficients a[x,q] represents the next State
  * They represent the State Change Function Lambda.
  *		The Output Function Beta can either be based on the State  (Moore Automaton)
  *		or the State and the Input Value (Mealy Automaton)
  *
  * The interesting Thing about Automatons is that they are reCoupled,
  * i.e. their current State is an Input to the next State.
  *
  * The Automaton walks along the (infinite) Graph.
  * Several Automatons can process the same (infinite) Graph!
  * Sometimes these Automatons have to synchronize themselves.
  * This is the Graph View of concurrent Processing.
  *
  * Created on 24. Mai 2001, 14:38
  *
  * @author  Matthias Heuer
  * @version
  */
public class DiscreteAutomaton
extends Object {

	////////////////////////////////////////////////////////////////////////////
	//	static Members
	////////////////////////////////////////////////////////////////////////////

	/** Transformation Object performing an Addition to the State
	  * Has no State, so it is made static and hidden.  	*/
	final static public IDiscreteTransition AddInt = new AddInt();

	/** Transformation Object performing a Multiplication on the State
	  * Has no State, so it is made static and hidden.  	*/
	final static public IDiscreteTransition MulInt = new MulInt();

	/** Transformation Object performing an Division of the State
	  * Has no State, so it is made static and hidden.  	*/
	final static public IDiscreteTransition DivInt = new DivInt();

	/** Transformation Object performing an Subtraction from the State
	  * Has no State, so it is made static and hidden.  	*/
	final static public IDiscreteTransition SubtInt = new SubtInt();

	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////

	/** Transition Function for the next State	 */
	protected IDiscreteTransition Lambda;

	/** The current (discrete) State of the Automaton	 */
	public int State;

	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////

    /** Creates new DiscreteAutomaton */
    public DiscreteAutomaton (IDiscreteTransition Lambda) {
		this.Lambda = Lambda; }

	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////

	/** Single Step Operation of this Automaton.
	  * Performs both State Transition and calculation of the Output Function.
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public int Operation(Object InPut) {
		return State = Lambda.map(InPut, State); }

	/**Tests all Methods of this Class	 */
	public static void testIt() {
		System.out.println("Testing " + Automaton.class.getName());
	}

	/**Helper Routine creating a finite State Machine
	 * defined by the Alphabet and the Pattern. State is represented by an Integer. 	 */
	public static DiscreteFnByHash Searcher(Object[] Pattern_, Object[] Alphabet) {
		int PL = Pattern_.length;
		int AL = Alphabet.length;
		DiscreteFnByHash ret = new DiscreteFnByHash(PL);
		Object A;
		int a, k, q = -1;
		while (++q <= PL) { //loop through the States
			a = -1; while (++a < AL) { //loop through the Alphabet
				A = Alphabet[a];
				k = q+1; if (k > PL) k = PL; //maximum next State.
//TODO				while (false) --k; //reduce k until P[1..k] is Suffix of P[1..q]A
				ret.setAt(A, q, new Integer(k));
			}
		}
		return ret; }

}

/** Example Class for a simple discrete Automaton with calculated Output.
  * This Function adds the Input to the State.
  */
class AddInt
implements IDiscreteTransition {

	/** Generic Representation of a State Change Function.
	 * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	 *
	 * The mapping Function can be represented analytically
	 * or an Array of HashTables (double hashing).    */
	public int map (Object InPut,int State) {
		return State + ByRefInt.TO_INT(InPut); }

}

/** Example Class for a simple discrete Automaton with calculated Output.
  * This Function multiplies the Input to the State.
  */
class MulInt
implements IDiscreteTransition {

	/** Generic Representation of a State Change Function.
	 * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	 *
	 * The mapping Function can be represented analytically
	 * or an Array of HashTables (double hashing).    */
	public int map (Object InPut,int State) {
		return State * ByRefInt.TO_INT(InPut); }

}

/** Example Class for a simple discrete Automaton with calculated Output.
  * This Function subtracts the Input from the State.
  */
class SubtInt
implements IDiscreteTransition {

	/** Generic Representation of a State Change Function.
	 * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	 *
	 * The mapping Function can be represented analytically
	 * or an Array of HashTables (double hashing).    */
	public int map (Object InPut,int State) {
		return State - ByRefInt.TO_INT(InPut); }

}

/** Example Class for a simple discrete Automaton with calculated Output.
  * This Function divides the State by the Input.
  */
class DivInt
implements IDiscreteTransition {

	/** Generic Representation of a State Change Function.
	 * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	 *
	 * The mapping Function can be represented analytically
	 * or an Array of HashTables (double hashing).    */
	public int map (Object InPut,int State) {
		return State / ByRefInt.TO_INT(InPut); }

}
