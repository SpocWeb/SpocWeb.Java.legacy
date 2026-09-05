package streamIO.copy;

import java.io.IOException;

import streamIO.IDeserializer;
import streamIO.IFormatOut;
import streamIO.IInstantiAble;

/**Basic Interface for classes with an empty constructor
 * and copying of Values in a later state.
 * This is somewhat against the Paradigm of a stable Object,
 * because also the HashValue etc. are no longer constant
 * so the Location in a Container could be falsified!
 * The Advantage with numerical Calculations is
 * that not with every operation a new Object has to be instantiated.
 * Additionally optimizations are possible to save creation and termination.
 *
 * It also creates the opportunity to define Methods that
 * 1. don't have an overhead to create a new instance
 * 2. can replace the inner contents of an object.
 *
 * Because in Java any Field Variable is automatically initialized,
 * this doesn't really save time for simple Classes, but for Containers.
 *
 * Design Decisions:
 * toString and fromString could use the Class name
 * to automatically determine the Class to be created.
 *
 * @see streamIO.IInstantiAble is not a Parent of this Interface
 * 	because it is too weak and would require rewriting many Implementations.
 *  The Casting from CopyAble is still mostly necessary,
 * 	except for copy(ST) = newInstance().copyAt(ST)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 7808ddedd7455c81fd404614598d743f6415ad7014d01e3b062c906d70add4ec
 * stale: false
 * tags: [code/abstract_interface]
 * concepts: [Copy Semantics]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IICopyAble
extends IInstantiAble {

	/** Creates an uninitalized new Instance of it's class.
	  * When overriding, use newInstance() on all Components.
	  * Redefines InstantiAble.newInstance_()
	  * to return CopyAble and thus reduce Casting in the most frequent Case: copy() */
	public ICopyAble newInstance();
	
	/** Creates a random new Instance of it's class.
	  * When overriding, use newInstance on all Components.
	  * Redefines InstantiAble.newInstance_()
	  * to return CopyAble and thus reduce Casting in the most frequent Case: copy() */
	public ICopyAble randomizeAt();
	
	/** Integrates deepCopyAt() and shallopCopyAt().
	  * @param  Object to deep copy the Contents from.
	  * @param  Depth >= 0 the Depth up to which the Object is copied. For 0 nothing is copied. 
	  * @return 'this', if feasible, but with the Contents or arg
	  * Tries to return 'this' for further use, but this is not guaranteed.
	  * Does a Copy to a certain Level
	  * i.e. also inner Components are copied up to the Depth.
	  * 
	  */
	public ICopyAble copyAt(final Object arg, final int Depth);
	
	/**Fills this Instance with the Contents read from the streamIO.	 */
	public ICopyAble fromStreamAt(IDeserializer ST) throws IOException;
	
	//public PrintStreamOut toStream(final PrintStreamOut stream) {//throws IOException
	
	/** Writes the Contents of this Object into the streamIO.	 */
	public void toStream(IFormatOut st) throws IOException;
	
}
