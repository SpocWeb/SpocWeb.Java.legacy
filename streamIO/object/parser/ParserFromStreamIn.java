package streamIO.object.parser;

import streamIO.object.AStreamIn;
import streamIO.object.IStreamIn;
import function.byref.ByRefInt;

/** Bridges / Filters the more consistent nested StreamIn Interface,
  * which returns an Iterator  when the Level increases
  * and an intermediate 'EOI' ('null') when the Level decreases,
  * to the 'older' StreamIn Implementation
  * that returns the Level of the Separator in nextItem()
  * and the actual Object in currItem()
  *
  * This is more consistent than the mixed Meaning of
  * nextItem() and currItem() in the InputStream2StreamIn Implementation,
  * but only useful for LL(0) Languages regular Data Structures,
  * not for complex Languages Parsing!
  *
  * @see streamIO.object.parser.StreamInFromParser which does the Reverse 
  * @see which implements a different Approach for Bracketed streamIO Structures,
  * that translates more easily into the Parser Format.
  * <!-- docstate
  * tags: [code/stream_parsing, code/parser]
  * concepts: [Separator-Driven Token Parsing and Stream Adapters]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class ParserFromStreamIn
extends AStreamIn {
	
	/** Reference to the Parser Input streamIO */
	protected IStreamIn in;
	
	/** The current Level, returned last from nextItem.
	  *  0 is the Default Level
	  * -1 is one Level up and closes this Iterator
	  *  1,2,... Levels indicate lower Levels in the Iterator Hierarchy.
	  */
	protected ByRefInt currLevel = new ByRefInt(0);
	
	/** Reference to the current Item, returned last from nextItem */
	protected Object currItem;
	
	/** Returns the current Item, returned last from nextItem */
	public Object currItem() { return currItem; }
	
	/** Delegates to the wrapped nested Stream's own Position tracking.
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return in.getPosition(); }

	/** Delegates to the wrapped nested Stream's own Mark support.
	  * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }
	
	/** Returns the Number of Items available */
	public long availAble() {
		long ret = in.availAble();
		if ((ret < 0) && (currLevel.Value >= 0)) {
			 ret = 0; }
		return ret; }
	
	/** Advances the wrapped nested Stream by one Step, translating a Sub-Stream/EOI transition
	  * into a Level change and returning the current Level for the older Parser Protocol.
	  * @return the next Item,
	 * An Iterator indicates a nested streamIO / Container
	 */
	public Object nextItem() {
		for (;;) {
			Object nextItem = in.nextItem();
				  // TODO: LOGIC: on EOI this only decrements currLevel; `in` is never restored to
				  // the parent Stream that was replaced when descending a Level (see the
				  // `in = (IStreamIn) nextItem` branch below), so after the first nested Stream
				  // is exhausted, subsequent calls keep querying the same now-exhausted inner
				  // Stream instead of resuming the parent - no Stack of enclosing Streams is kept.
				  if((nextItem == EOI) && !in.isValid()) { --currLevel.Value; //EOF: up one Level
			}else if (nextItem instanceof IStreamIn) {
				in = (IStreamIn) nextItem;                  ++currLevel.Value; //StreamIn: down one Level
			}else{
				currItem = in.currItem(); // for calling currItem()
				return currLevel; //for returning the InputStream2Stream Metaphor
			}
		}
	}

	/** Delegates to the wrapped nested Stream's own reSet(long).
	  * @see streamIO.object.IStreamIn#reSet(long)	 */
	public long reSet(final long _position) { //throws NoSuchMethodException {
		return in.reSet(_position);
	}
	
}
