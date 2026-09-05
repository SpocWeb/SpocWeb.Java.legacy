package streamIO.object;

import streamIO.IIStreamIn;
import streamIO.IMarkAble;
import streamIO.IReSetAble;

/**
 * Interleaves a fixed array of input streams in round-robin order, s[0][0], s[1][0], ...,
 * s[n][0], s[0][1], ... until every source is exhausted.
 * <p>
 * DeMultiplexerIn.java
 * The DeMultiplexerIn is derived from the abstract Base Class AStreamIn
 * and de-multiplexes this Input streamIO from a List of Input Streams 
 * in a Round Robin Fashion, i.e. the resulting Order is
 * s[0][0], s[1][0],... s[n][0], 
 * s[0][1], s[1][1],... s[n][1], 
 * ... 
 * This controlled Round Robin Algorithm is only possible 
 * by actively reading from a Set of StreamIn Objects. 
 * Uncontrolled passive Demultiplexing happens with any IStreamOut, 
 * when several Clients write to it concurrently. 
 * 
 * @see Union Appends an (infinite) Number of finite Input Streams. 
 * In Opposition, this Class interleaves a fixed Number (thus an Array!)
 * of (possibly infinite) Input Streams
 * which cannot be done recursively by (de-) multiplexing two Streams,
 * except for a binary Powers of streamIO Numbers:
 * merge A,B into C multiplexing two  Streams
 * merge X,Y into Z multiplexing two  Streams
 * merge C,Z into O multiplexing four Streams
 * etc. giving a,x,b,y,a,x,b,y,...
 *
 * or by accepting a mixed Frequency of Elements:
 * merge A,B into C multiplexing two  Streams
 * merge C,D into E multiplexing two  Streams giving a,d,b,d,a,d,b,d,...
 * 
 * @see streamIO.object.MultiplexerOut 
 * @see streamIO.object.Union which 
 * 
 * @see Merger, which merges two sorted Input Streams into a new one.
 * Any StreamIn can also be used as a DeMultiplexer
 * by just having several Processes, Threads etc. writing to it
 *
 * Created on 26. Mai 2001, 22:08
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:39:21Z
 * digest: f181290f999117af47287d21b71d100a20272e9c6e347bf2bcb592323013271f
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class DeMultiplexerIn
extends AStreamIn {

	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the actual Output Streams */
	protected final IIStreamIn[] sources;

	/** current Output Object, originally defined in AStreamIn */
	protected Object currItem;

	/** Number of the current Input streamIO */
	protected int currInStream;

	/** Flag indicating that ALL Input Streams are empty. */
	protected boolean outOfData;

	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////

	/** Creates new DeMultiplexerIn */
	public DeMultiplexerIn(final IIStreamIn[] Forwards) { this.sources = Forwards; }

	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////

	/**Returns the (minimum) Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 */
	public long availAble() {
		if (outOfData) return -1;
		return ((IStreamIn)sources[currInStream]).availAble(); }

	/** Returns the smallest maximum mark size across every source, or -1 when any source does
	 * not support marking.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() {
		long min = Long.MAX_VALUE; 
		for(int i = sources.length; --i >= 0;) {
			if (!(sources[i] instanceof IMarkAble)) 
				return -1;
			final long m = ((IMarkAble)sources[i]).getMaxMarkSize(); 
			if (min > m)
				min = m; 
		}
		return min;
	}
	
	/** Returns a combined position encoding the current source index and its own position;
	 * only meaningful until one source runs out of data.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { //only valid until one of the Sources runs out of Data!
		return ((IMarkAble)sources[currInStream]).getPosition()*sources.length+currInStream;
	}
	
	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	public Object nextItem() {
		outOfData = false;
		while (true) { //try it until it reaches a full loop!
			if (++currInStream >= sources.length) {
				if (outOfData) return null; //after a full loop, give it up!
				currInStream = 0; outOfData = true; }//Modulus Increment
			if (EOI != ((currItem = sources[currInStream].nextItem())) ||
									sources[currInStream].isValid())
				return currItem; }
	}

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		for(int i = sources.length; --i >= 0;)
			((IStreamIn) sources[i]).reSet();
		return this; }

	////////////////////////////////////////////////////////////////////////////
	//  Optimization:
	////////////////////////////////////////////////////////////////////////////

	/** current Output Object, originally defined in AStreamIn */
	public Object currItem() { return currItem; }
	
}
