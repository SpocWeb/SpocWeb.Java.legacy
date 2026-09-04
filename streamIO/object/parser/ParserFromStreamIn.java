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
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return in.getPosition(); }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }
	
	/** Returns the Number of Items available */
	public long availAble() {
		long ret = in.availAble();
		if ((ret < 0) && (currLevel.Value >= 0)) {
			 ret = 0; }
		return ret; }
	
	/** @return the next Item,
	 * An Iterator indicates a nested streamIO / Container
	 */
	public Object nextItem() {
		for (;;) {
			Object nextItem = in.nextItem();
				  if((nextItem == EOI) && !in.isValid()) { --currLevel.Value; //EOF: up one Level
			}else if (nextItem instanceof IStreamIn) {
				in = (IStreamIn) nextItem;                  ++currLevel.Value; //StreamIn: down one Level
			}else{
				currItem = in.currItem(); // for calling currItem()
				return currLevel; //for returning the InputStream2Stream Metaphor
			}
		}
	}
	
	/** @see streamIO.object.IStreamIn#reSet(long)	 */
	public long reSet(final long _position) { //throws NoSuchMethodException {
		return in.reSet(_position);
	}
	
}
