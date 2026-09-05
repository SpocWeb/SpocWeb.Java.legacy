package streamIO.object.enumer;

import java.io.IOException;

import streamIO.AStreamOut;
import streamIO.IIStreamIn;
import streamIO.object.AStreamIn;
import streamIO.object.IPipe;

/**This Class models a Pipe which allows more flexible,
 * asynchronous Communication between two Threads than 'streamIO.Monitor':
 * To facilitate continuous Operation on both Sides without Memory Overload,
 * this Class also has a MaxCapacity.
 *
 * Thus neither the Sender nor the Receiver are blocked in any way,
 * except if the Queue is empty.
 * TODO: Introduce a Timeout for the Blocking.
 * @stereotype enumeration
 * <!-- docstate
 * tags: [code/enumerator, code/iterator_adapter]
 * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public abstract class APipe
extends AStreamIn //has more virtual Methods
implements IPipe {

	///////////////////////////////////////////////////////////////////////////
	//	Interface StreamOut: abstract Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Add this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	//public abstract StreamOut addItem(Object arg);
	
	///////////////////////////////////////////////////////////////////////////
	//	Interface StreamOut: Implementation
	///////////////////////////////////////////////////////////////////////////
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added recursively,
	  * up to the given flatDepth.	  */
	public long addItems(Object arg, int flatDepth) {
		return AStreamOut.ADD_ITEMS(this, arg, flatDepth); }
	
	/** adds these Items to the Store in Place: +=
	 * The Type of Item is analyzed, i.e. Containers Contents is added,
	 * but not recursively, but only flattened by one Level (flatDepth == 1).
	 * Named with capital A, to distinguish it from streamIO.Copy.Group.add()*/
	public long addItems(Object arg) { return AStreamOut.ADD_ITEMS(this, arg, 1); }
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(Object[] arg) { return AStreamOut.ADD_ITEMS(this, arg); }
	
	/** adds all Items from the Enumerator to the Store in Place: +=
	 * The Type of Item is not analyzed, i.e. Containers are added as is.	   */
	public long addItems(IIStreamIn Iter) { return AStreamOut.STREAM(Iter, this); }
	
	/** Does nothing; this pipe has no buffered output to flush.
	  * @see streamIO.IStreamOut#flush()	 */
	public void flush() throws IOException { }
	
}
