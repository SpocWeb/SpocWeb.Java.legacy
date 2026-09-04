package streamIO.object.parser;

import streamIO.object.AStreamIn;
import streamIO.object.IStreamIn;
import function.byref.ByRefInt;

/** Bridges / Filters the older StreamIn Implementation
  * that returns the Level of the Separator in nextItem()
  * and the actual Object in currItem()
  * to the more consistent nested StreamIn Interface
  * which returns an Iterator when the Level increases
  * and null when the Level decreases.
  * 
  * This is more consistent than the mixed Meaning of
  * nextItem() and currItem() in the InputStream2StreamIn Implementation,
  * but only useful for LL(0) Languages like Data Structures,
  * not for very complex Language Parsing!
  * 
  * Similar Classes: 
  * @see streamIO.object.parser.ParserFromStreamIn which does the Reverse
  * @see ParserBracket2StreamIn
  * which creates a nested structure from only two Separator Characters
  * which are recursively nested, unlike here,
  * where only finite deep Structures are possible.
  *
  */
public class StreamInFromParser
extends AStreamIn {
	
	/** The current Level, returned last from nextItem.
	  *  0 is the Default Level
	  * -1 is one Level up and closes this Iterator
	  *  1,2,... Levels indicate lower Levels in the Iterator Hierarchy. */
	protected int currLevel = 1;
	
	/** The current Level, returned last from nextItem */
	protected int newLevel = 1;
	
	/** Reference to the current Item, returned last from nextItem */
	protected Object currItem;
	
	/** Returns the current Item, returned last from nextItem */
	public Object currItem() { return currItem; }
	
	/** Reference to the Parser Input streamIO */
	protected final IStreamIn in;
	
	public StreamInFromParser(final IStreamIn _in) { this.in = _in; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Returns the Number of Items available for this Level */
	public long availAble() {
		if (newLevel < currLevel) return -1; 
		if (newLevel > currLevel) return  newLevel - currLevel; //at least!
		return in.availAble(); }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return in.getPosition(); }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }
	
	/** Returns the next Item,
	  * An Iterator indicates a nested streamIO / Container
	  * an 'EOI' indicates the End of the nested streamIO.
	  */
	public Object nextItem() {
		if (newLevel < currLevel) { --currLevel; return null; } //null symbolizes the End of the (Sub-)Stream
		if (newLevel > currLevel) { ++currLevel; return this; }//a Stream Object symbolizes a SubStream
		final ByRefInt level = (ByRefInt) in.nextItem();
		newLevel = level.Value;
		return currItem = in.currItem(); }
	
	/** @see streamIO.object.IStreamIn#reSet(long)	 */
	public long reSet(final long position) { //throws NoSuchMethodException {
		return in.reSet(position);
	}
	
}
