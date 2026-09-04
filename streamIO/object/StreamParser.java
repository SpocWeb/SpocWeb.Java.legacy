package streamIO.object;

import java.io.IOException;
import java.io.InputStream;

import streamIO.exception.BaseException;

/**
 * Outdated Parser!
 * Recursively parses a Structured streamIO
 * (can also be from a String by using StringBufferInputStream)
 * into a Hierarchy of String Arrays.
 * Uses a (,) Grammar like Scanner.nextItem(), which is more generic.
 * This is similar but more powerful than the StreamEnumerator,
 * since it can parse nested Objects, not only Strings.
 * This Class replaces 'StreamEnumerator', which uses a StreamParser.
 * InputStreams and Enumerators are quite the same,
 * except that the first returns byte and the second one Objects.
 * So an Enumerator can be implemented on top of an Input streamIO using normal Filters.
 * This Enumerator is able to use Length Information stored at the Start of a List
 * to save some copying, but also parses Lists without this Information.
 * It also handles Lists with Separators at the end creatd by simple Routines.
 *
 * Design Decisions:
 * It is not possible to hand over the IOExceptions from the streamIO,
 * because they are not declared in the Interface Enumerator!
 * So I throw AbstractMethodErrors with the same Message.
 * Additionally I mustn't catch all Throwable Objects, but only IOExceptions!
 *
 * The Starter and Stopper Characters have to be escaped,
 * because they mustn't appear within an Object.
 * Starter Characters could as well be ", [, < or anything else for [|] {,}, <|>, (;) etc.
 * It should be a rare character so it doesn't have to be escaped too often
 * on the other hand it should be a printable character to easily read and write Structures!
 * ByteArrayOutputStream, StringBufferInputStream and ByteArrayInputStream
 * allow for easy In and Output to Strings and Byte/Char Arrays. String also has Methods to change between Byte Arrays:
 * getBytes is the Complement to the Constructor taking a Byte Array
 *
 */
public class StreamParser
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////
	//  Members, mutable to control the Behavior
	////////////////////////////////////////////////////////////////////////////
	
	/** Determines, whether Information about the Length is stored in the Lists. */
	public boolean lengthInfo = false;
	
	/** Determines, whether Separators at the End of a List are ignored. */
	public boolean ignoreNulls = true;
	
	/** Default Starter Character for an inner structure */
	public char Starter = '{';
	
	/** Default Stopper Character for an inner structure */
	public char Stopper = '}';
	
	/** Default Separator Character for the structure */
	public char Separator = '|';
	
	/** Length of the temporary List of Objects */
	protected int length = 32;
	
	/** temporary List of Objects */
	protected Object[] list = new Object[length];
	
	/** Contains the String while it is being constructed, fastest to fill from a streamIO. */
	protected StringBuffer Buffer = new StringBuffer();
	
	/** Contains the current String or Object[] Array. */
	protected Object currItem;
	
	/** Local Reference to the Input streamIO */
	protected InputStream inStream;
	
	/** The actual Order, defaulted to 'no Order' */
	protected byte Order = ORDER_NONE;
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return Order; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor */
	public StreamParser(final InputStream inStream) {
		this.inStream = inStream;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** Feedback, whether more Items are available */
	public long availAble() { //throws IOException {
		try { return inStream.available(); 
		} catch (final IOException t) { return 0; }
	}
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return inStream.markSupported() ? Integer.MAX_VALUE : -1; }
	
	/** @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return 0; } //iter.; }
	
	/** Restarts the Enumerator */
	public void reStart() {
		try { inStream.reset(); }
		catch (IOException x) { throw new BaseException("", x); }
	}
	
	/** Returns the current Object. */
	public Object currItem() { return currItem; }
	
	/**
	 * Retrieves the next Item and determines, whether more is to come
	 * Don't use a StreamTokenizer, because it separates too much (taking all WhiteSpaces as Separators).
	 * Works similar to "streamIO.Stream2String.parseList"
	 */
	public Object nextItem() { //
		//By Convention the first Item in a List is an Integer with the Length of the List.
		try {
			while (inStream.available() > 0) {
				//			while ((available.Value = inStream.availAble()) > 0) {
				char Char;
				if ((Char = (char)inStream.read()) == Separator) break; //a new Item starts, give out the actual
				if  (Char == Starter) { //start an inner (recursive) Parsing
					final StreamParser inner = parseInner();
					if ((inStream.available() > 0) && //read the next Separator
						(((String)inner.nextItem()).length() > 0)) {
						throw new AbstractMethodError("List not terminated correctly!"); }
					return currItem; 
				}
				if (Char == Stopper) { //Stop the inner (recursive) Parsing
					return EOI; } //{ available.Value = 0; break; }  //return null;	//Signal for the calling Routine that
				// nothing is to be returned!

/*			if (Char == Attribute) { 	//use the previous String as the Key
				Key = Buffer.toString();
				Buffer.setLength(0);	//Clear the Buffer
			}
*/			  	Buffer.append(Char);
			}
		} catch (IOException x) { 
			throw new BaseException("Error during Parsing:", x); 
		}
		final String strTmp = (Buffer.length() > 0 ? Buffer.toString() : null); 
		Buffer.setLength(0); //Clear the Buffer
		return currItem = strTmp; //return the current Item
	}
	
	/** @see #nextItem() calls this Method exclusively 	 */
	private StreamParser parseInner() throws NumberFormatException, AbstractMethodError {
		Object[] lList = list;
		StreamParser inner = new StreamParser(inStream);
		if (lengthInfo) { //get the Array Length out
			length = Integer.parseInt((String)inner.nextItem()); //available));	//just reuse 'available'
			currItem = (lList = new Object[length]); //The first item is the Length!
		}
		int i = -1;
		while (i < length) { //&& (available.Value > 0)) { // && (i < Length))
			if (++i >= length) { //Resize the big Array
				length += length; //double the Size
				lList = (list = new Object[length]);
				System.arraycopy(lList, 0, list, 0, i);
				if (lengthInfo) {
					throw new AbstractMethodError("List too long !"); } 
			}
			final Object tmp = lList[i] = inner.nextItem(); //available);	//more Redundancy, but also better check!
			if (ignoreNulls && (tmp == null)) {
				i--; } 
		}
		if (!lengthInfo) { //Copy from the large Array to a small one
			currItem = new Object[i];
			System.arraycopy(lList, 0, currItem, 0, i);
		}
		return inner;
	}

}
