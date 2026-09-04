package tester.process;

import java.util.Hashtable;

/**
  * DiscreteFnByHash.java
  * HashTable Representation of an Automaton: a[x][q] -> q
  * The States Q are mapped to the Integer Numbers 0..q.
  * The Value of the Coefficients a[x,q] represents the next State
  * They represent the State Change Function Lambda.
  *		The Output Function Beta can either be based on the State  (Moore Automaton)
  *		or the State and the Input Value (Mealy Automaton)
  *
  * Created on 25. Mai 2001, 10:03
  *
  * @author  Matthias Heuer
  * @version
  */
public class DiscreteFnByHash
extends Object
implements IDynamicTransition {

	/** local Reference to the HashTable Function
	  * What is the Advantage of double Hashing?
	  * None: it takes double as long and requires to create new HashTables.
	  * Only when the Function actually partitions,
	  * the Mapping Set is much smaller:  	 */
	protected Hashtable[] f;

    /** Creates new DiscreteFnByHash */
    public DiscreteFnByHash (int NumStates) {
		f = new Hashtable[NumStates];
		while (--NumStates >= 0)
			f[NumStates] = new Hashtable();
    }

	/** add a new Operation / Production to the State Change Function of the Automaton.
	 * Returns the previous Mapping, when there was one, otherwise null.  */
	public Integer setAt(Object InPut,int State,Integer OutPut) {
		return (Integer) f[State].put(InPut, OutPut); }

	/** Generic Representation of a State Change Function.
	 * Can be used for the State Change Function Beta, but NOT the Output (Mealy).
	 *
	 * The mapping Function can be represented analytically
	 * or an Array of HashTables (double hashing).    */
	public int map (Object InPut,int State) {
		return ((Integer) f[State].get(InPut)).intValue(); }

}
