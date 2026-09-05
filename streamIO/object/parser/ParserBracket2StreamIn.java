package streamIO.object.parser;

import streamIO.object.AStreamIn;
import streamIO.object.IStreamIn;
import function.byref.ByRefInt;

/** Bridges / Filters the older StreamIn Implementation
  * that returns the Level of the Separator in nextItem()
  * and the actual Object in currItem()
  * to the more consistent nested StreamIn Interface
  * which returns an Iterator when the Level increases
  * and EOF=null when the Level decreases.
  * Creates a nested streamIO structure from only two Separator Characters
  * which are recursively nested:
  * 
  * ...{...{...}...{...}...{...}..}...
  *    0   1   0   1   0   1   0  1
  * 
  * similar to the Java Hierarchy: Package < File < Class < Method < structure < Substructure < ...
  * 0: FileName {
  * ...
  * 0: class xxx {
  * ...
  * 0: method1(...){...
  * 1: } ...
  * 0: method2(...){...
  * 1: } ...
  * 1: }
  * ...
  * 0: class yyy {
  * ...
  * 0: method1(...){...
  * 1: } ...
  * 0: method2(...){...
  * 1: } ...
  * 1: }
  * ...
  * }
  * ...
  * 
  * #Rows ~ #Commands => count Rows!
  * an Alternative is to use regular Expressions
  * to filter out relevant Details from a streamIO,
  * but that is unclear, because it may not consider Quoting!
  *
  * This is more consistent than the mixed Meaning of
  * nextItem() and currItem() in the InputStream2StreamIn Implementation,
  * but only useful for very regular Data Structures,
  * not for very complex Language Parsing!
  *
  * Similar Classes:
  * @see StreamInFromParser where only finite deep Structures are possible,
  * but the Level is directly encoded in the Separator Character
  *
  * <!-- docstate
  * tags: [code/stream_parsing, code/parser]
  * concepts: [Separator-Driven Token Parsing and Stream Adapters]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class ParserBracket2StreamIn
extends AStreamIn {

	/** Reference to the Parser Input streamIO */
	protected IStreamIn in;

	/** The current Level, returned last from nextItem.
	  *  0 is the Default Level
	  * -1 is one Level up and closes this Iterator
	  *  1,2,... Levels indicate lower Levels in the Iterator Hierarchy. */
	protected int currLevel = 0;

	/** The current Level, returned last from nextItem */
	protected int newLevel;

	/** Reference to the current Item, returned last from nextItem */
	protected Object currItem;

	/** Returns the current Item, returned last from nextItem */
	public Object currItem() { return currItem; }

	/** Returns the Number of Items available for this Level */
	public long availAble() {
		if (newLevel < currLevel) { return -1; }
		if (newLevel > currLevel) { return  newLevel - currLevel; } //at least!
		return in.availAble(); }

	/** Returns the next Item,
	  * An Iterator indicates a nested streamIO / Container
	  * an 'EOI' indicates the End of the nested streamIO.
	  */
	public Object nextItem() {
		ByRefInt level = (ByRefInt) in.nextItem();
		newLevel = level.Value;
		if (newLevel == 1) { return null; } //Closing Bracket
		if (newLevel == 0) { return this; } //opening Bracket
		return currItem = in.currItem(); }
	
	/** Delegates to the wrapped Parser Stream's own Mark support.
	  * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }

	/** Delegates to the wrapped Parser Stream's own Position tracking.
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return in.getPosition(); }
	
}
