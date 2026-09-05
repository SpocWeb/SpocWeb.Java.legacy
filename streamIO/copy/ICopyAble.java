package streamIO.copy;

import graphs.ICopy;

import java.io.IOException;
import java.io.InputStream;

import streamIO.IDeserializer;

/**Full Interface for lightweight Classes with an empty Constructor
 * and copying of Values in a later state.
 * This is somewhat against the Paradigm of a stable Object,
 * because also the HashValue etc. are no longer constant
 * so the Location in a Container could be falsified!
 * The Advantage with e.g. numerical Calculations is
 * that not with every operation a new Object has to be instantiated.
 * Additionally Optimizations are possible to save creation and termination.
 *
 * It also creates the Opportunity to define Methods that
 * 1. don't have an Overhead to create a new Instance
 * 2. can replace the inner Contents of an Object.
 *
 * Because in Java any local Variable must be initialized,
 * this doesn't really save time for simple Classes, but for Containers.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 639af05a9a4a2a9562b0f3bf9e22e02a718f79defeaa1dab065caa810409c6e9
 * stale: false
 * tags: [code/abstract_interface, code/serialization]
 * concepts: [Copy Semantics, Serialization]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface ICopyAble	//The last two Interfaces define no Methods!
extends ICopy, IICopyAble, java.lang.Cloneable, java.io.Serializable {

	/** Creates a random new Instance of it's class.
	  * When overriding, use random() on all Components.
	  */
	public ICopyAble random();

	/**
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Returns the Copy for further use.
	 *
	 * Negative Depth create no Copy, but return the Original.
	 * This makes it easier for Containers to create Copies.  */
	public ICopyAble copy();

	/** Complement to Copy.
	  * @param  Object to deep copy the Contents from.
	  * @return 'this' but with the Contents or arg
	  * Does a 'deepCopy', i.e. also inner Components are copied.
	  * Copies the Value of arg into it's own Value
	  * and returns itself for further use.
	  * When overriding, use copyAt on all Components.	 */
	public ICopyAble copyAt(Object arg);

	/**Integrates deepCopyAt() and shallopCopyAt().
	 * Does a Copy to a certain Level
	 * i.e. also inner Components are copied up to the Depth.
	 * Returns the itself for further use.
	 * Depth is only valid >= 0, for 0 it returns itself.
	 *
	 * @return	 a copy of this instance.
	 * @see		java.lang.Cloneable
	 */
	public ICopyAble copy(int Depth);

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg);

	/**Creates a new shallow Copy of this Instance.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopy();

	/**Swap Algorithm: this <-> arg
	 * swaps the internal Components by using copyAt()	 */
	public ICopyAble swap (Object arg);

	//(De-)Serialization:

	/**Writes the Contents of this Object into the streamIO.	 */
//	public String toString(); //already declared in 'Object'

	/** Creates an uninitalized new Instance of it's class
	 *  and fills it with the Contents read from the streamIO.
	 *  This is in Fact a Prototype Approach combined with an internal Builder	 */
	public ICopyAble fromStream(IDeserializer ST)
	throws ClassNotFoundException, IllegalAccessException, IOException;

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the streamIO.	 */
	public ICopyAble fromStream(InputStream ST)
	throws ClassNotFoundException, IllegalAccessException, IOException;

	/**fills this Instance with the Contents read from the streamIO.	 */
	public ICopyAble fromStreamAt(InputStream ST) throws IOException;

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
	public ICopyAble fromString(String ST)
	throws ClassNotFoundException, IllegalAccessException, IOException;

	/**fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStringAt(String ST) throws IOException;

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
//	public CopyAble fromStream(StreamTokenizer ST) throws IOException;

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
//	public CopyAble fromStream(Reader ST) throws IOException;

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
//	public CopyAble fromStreamAt(Reader ST) throws IOException;

}
