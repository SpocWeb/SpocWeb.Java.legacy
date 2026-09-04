package streamIO.object;

import graphs.ICopy;
import streamIO.IIStreamIn;
import streamIO.copy.ICopyAble;
import streamIO.copy.IICopyAble;

/** Creates Copies of the given Depth of the Objects returned by the appended
  * OutputStream, if possible.
  * This Class breaks the Hierarchy and should normally be placed into Enumeration.
  * @see CopyStreamOut for the stream() Method to stream with creating Copies... */
public class CopyStreamIn
extends AFilterIn {

    /** Depth of the Copies:
      * 0 means no Copy
      * 1 means shallow Copy
      * higher represents different Levels of Deep Copies 	 */
	protected int Depth;

    /** Initializing Constructor	 */
    public CopyStreamIn(IIStreamIn In) { this(In, Integer.MAX_VALUE); }

    /** Initializing Constructor	 */
    public CopyStreamIn(IIStreamIn In, int Depth_) {
        super(In);
    	this.Depth = Depth_; }

	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
     * Tries to create a Copy of certain Depth of this Object
     * using ICopy or ICopyAble
	 */
    protected Object nextItemInternal() {
        currItem = in.nextItem();
		if (currItem instanceof ICopy)
			return ((ICopy    ) currItem).Copy(); //usually the fastest...
		if (currItem instanceof  ICopyAble)
			return (( ICopyAble) currItem).copy(Depth); //slower...
		if (currItem instanceof IICopyAble)
			return ((IICopyAble) currItem).newInstance().copyAt(currItem, Depth); //slowest...
		return currItem; }

}
