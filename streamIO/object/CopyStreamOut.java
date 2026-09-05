package streamIO.object;

import graphs.ICopy;
import streamIO.AStreamOut;
import streamIO.FilterOut;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;
import streamIO.copy.ICopyAble;
import streamIO.copy.IICopyAble;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;
import tester.process.StreamProcessor;

/** Creates Copies of the given Depth of the Objects returned by the appended
  * OutputStream, if possible.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:35:26Z
  * digest: f723f5e7599893e72d9cc9f5746f1af052386867ded2808e96ddfd38a21c5293
  * stale: false
  * tags: [code/stream_processing, code/iterator]
  * concepts: [Object Stream Pipeline]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  * This Class breaks the Hierarchy and should normally be placed in streamIO.Object.Enumeration. */
public class CopyStreamOut extends FilterOut {

	/** streams the whole Contents of the Input streamIO to the Output streamIO.
	  * creates Copies of the given Depth of the Items from the Input streamIO.
	  * Does not flatten the Input streamIO though.
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see CopyStreamIn
	  * @see CopyStreamOut
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening
	  * @see AContainer.copy() where this is being used...	 */
	public static IStreamOut stream(IIStreamIn In, IStreamOut out, int Depth) {
		Object currItem;
		while ((IIStreamIn.EOI != (currItem = In.nextItem())) || In.isValid()) {
			if (IIStreamIn.EOI ==  currItem)
				 out.addItem(IIStreamIn.EOI);
			else if (currItem instanceof ICopyAble)
				 out.addItem(((ICopyAble) currItem).copy(Depth));
	    	else if (currItem instanceof IICopyAble)
				 out.addItem(((IICopyAble)currItem).newInstance().copyAt(currItem, Depth));
			else if (currItem instanceof ICopy) //check for ICopy Interface...
				 out.addItem(((ICopy)    currItem).Copy());
			else out.addItem(currItem); }
		return out; }

	/** Depth of the Copies:
      * 0 means no Copy
      * 1 means shallow Copy
      * higher represents different Levels of Deep Copies 	 */
	protected int Depth;

    /** Initializing Constructor	 */
    public CopyStreamOut(IStreamOut Out, int Depth_) {
        super(Out);
    	this.Depth = Depth_; }

    /** Initializing Constructor	 */
    public CopyStreamOut(IStreamOut Out) { this(Out, Integer.MAX_VALUE); }

	/**
	 * Adds a copy of {@code Item}, made to this instance's configured depth, to the wrapped
	 * output.
	 *
	 * @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * This is less explicit, but much faster for a regular Operation
	  * because Exception Handling can be extremely slow.
	  * Tries to create a Copy of certain Depth of this Object
	  * using ICopy or ICopyAble
	  */
	public IIStreamOut addItem(Object Item) {
    	if (Item instanceof ICopy) {
            out.addItem(((ICopy    ) Item).Copy()); return this; }
    	if (Item instanceof  ICopyAble) {
            out.addItem((( ICopyAble) Item).copy(Depth)); return this; }
    	if (Item instanceof IICopyAble) {
            out.addItem(((IICopyAble) Item).newInstance().copyAt(Item, Depth)); return this; }
    	out.addItem(Item);
    	return this; }

}
