package reflect;

//import Synch.InvalidException;
import tester.ITester;

/**
  * Title: IReflectAble<p>
  * Description:
  * Defines the Interface for Classes that expose their Data and Methods via Reflection.
  * The Contract is that null can be used instead of void!
  *
  * Accesses Fields and Methods uniformly by Name and Parameters only!
  * Also allows to use nested Names for reading and writing Properties!
  * Can be used for Smalltalk-like Programming by sending Messages.
  * Runtime Exceptions due to missing Methods can be caught and suppressed.
  *
  * Does not add any Information about the concrete Elements of this Instance.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-24-2002, 09:15 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:22:58Z
  * digest: 1010e4e23f74f9de03293f1c16a1e2fee3fe06f1d1323e55a28977740941dbd1
  * stale: false
  * tags: [code/reflection, code/interface_contract]
  * concepts: [Reflection]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface IReflectAble {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants
////////////////////////////////////////////////////////////////////////////////

	/** Underscore as Separator between Name Parts of a Path 	 */
	final static public char SEP = '_';

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Iterates over all Values and Properties
	  * Sets the Data Value selected by the ITester
	  * This can be used to implement logical Queries.
	  * @TODO: what if no Value was selected, should it throw an Exception?
	  */
	void set(ITester select, Object value); // throws InvalidException;

	/** Iterates over all Values and Properties
	  * This can be used to implement logical Queries.
	  * @return the Data Value selected by the ITester
	  * or null when no Value was selected.
	  */
	Object get(ITester select);

	/** Sets the given Data Value  	 */
	void set(String data, Object value); // throws InvalidException;

	/** Reads a Property, possibly nested along a {@link #SEP}-separated Path.
	  * @return the given Data Value 	 */
	Object get(String data);

	/** Invokes a named Method or indexed Data accessor with the given Parameters, analogous to a SmallTalk message send.
	  * @return the given Data Value or Function Result (analogous to SmallTalk) 	 */
	Object call(String data, Object[] params);

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** @return a new Instace of this Type 	 */
//	IReflectAble newInstance();

	/** Creates a new, empty Instance of the same concrete Type as this Object, via reflection.
	  * @return a new Instace of this Type
	  * @param Params the Parameters to the Constructor.
	  * If null or an empty Array, the empty Constructor is called.
	  */
	IReflectAble newInstance(Object[] Params);

	/**
	 * Performs a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared final public to enforce Value Types!
	 * and are initialized in the Constructor.
	 */
	public IReflectAble CopyAt(Object Value);

	/**
	 * Creates and returns an independent, recursively-copied clone of this Object.
	 * @return a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared final public to enforce Value Types!
	 * and are initialized in the Constructor.
	 */
	public IReflectAble Copy();

}

