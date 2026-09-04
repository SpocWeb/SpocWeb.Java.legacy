package streamIO.object.parser;

import java.io.IOException;
import java.io.InputStream;

import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.exception.BaseException;
import streamIO.exception.OperationNotSupported;
import streamIO.integer.IStreamIn_Byte;
import streamIO.object.AStreamIn;
import streamIO.object.IStreamIn;
import tools.IOError;
import function.byref.ByRefInt;

/** EscapeStreamIn
  * deprecated Implementation, replacing those in Package Byte,
  * only retained to keep things running...
  * 
  * This could be the first Element for a Parser Chain:
  * It returns byte Characters from an InputStream, and filters out Escape Characters.
  * The new Implementation is in
  * @see EscapedStreamIn and replaces this Class and the ScannerSreamIn Class
  *
  * It is a Scanner that filters out Escape Characters
  * and creates a StreamIn of Strings from a Character (byte) streamIO.
  *
  * Escaping is a standard Technique for structured Character separated Files.
  * It allows to include the Separator Character in the Data.
  * A Standard Escape Character is the BackSlash '\'
  * To include the Escape Character itself, also escape it: '\\'
  * Also supports several nonprintable Characters like CR LF, Tab etc.
  *
  * Escaping can be done best at this Level,
  * which should not break on encountering an escaped Token.
  * At a higher Level
  * the Escape Character would have to be tested for after detecting a possible Token.
  * Masking can also be done best at this Level.
  * This is the first Level of a Parsing Framework
  * based on the IStreamIn Interface!
  *
  * Design Decisions:
  * Instead of using both nextItem() and currItem() to return Token and Result,
  * a Container Object with public int and StringBuffer Members could have
  * been used to return the Result,
  * which would have increased the Number of Classes,
  * but made the Protocol simpler and thus more robust (less State).
  *
  * @see nextItem() returns the next Character
  * @see currItem() returns and clears the current String
  *
  * @see StreamInParser for the Post Procesing of this streamIO.
  * @see java.io.InputStream into the
  * @see streamIO.IIStreamIn Interface
  *
  * It replaces
  * @see StreamInScanner which implements only the
  * @see Scanner which is older
  *
  * This is a Filter mediating between the character base Interface
  * @see InputStream which is handed over in the Constructor and the Object based
  * @see IStreamIn which it implements.
  *
  */
final public class EscapeStreamIn
extends AStreamIn {
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static Constants
	///////////////////////////////////////////////////////////////////////////////
	
	/**Indicator of the End of the Input streamIO.
	 * The Value is chosen to be the same as StreamTokenizer.TT_EOF for consistency
	 * and it corresponds to the Position in the Separator String (as a Watcher)  */
	final static public char SCN_CHR_EOF = (char) java.io.StreamTokenizer.TT_EOF;
	
	/**Constant for the Character '0'	 */
	final static public char CHR_0 = '0';
	
	/**Constant for the Character '9'	 */
	final static public char CHR_9 = '9';
	
	/**Constant for the Character 'A'
	 * implicit Assumption in HexNumber(): A < a	 */
	final static public char CHR_A = 'A';
	
	/**Constant for the Character 'E'	 */
	final static public char CHR_E = 'E';
	
	/**Constant for the Character 'F'	 */
	final static public char CHR_F = 'F';
	
	/**Constant for the Character 'Z'	 */
	final static public char CHR_Z = 'Z';
	
	/**Constant for the Character 'a'
	 * implicit Assumption in HexNumber(): A < a	 */
	final static public char CHR_a = 'a';
	
	/**Constant for the Character 'e'	 */
	final static public char CHR_e = 'e';
	
	/**Constant for the Character 'f'	 */
	final static public char CHR_f = 'f';
	
	/**Constant for the Character 'z'	 */
	final static public char CHR_z = 'z';
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static Methods
	///////////////////////////////////////////////////////////////////////////////
	
	//these Methods complement the isSpace, isSpaceChar, isWhiteSpace and isIsoControl
	//in Class 'java.lang.Character'
	
	/** @return true when this Character is a Letter	 */
	public static boolean isWhiteSpace(int c) {
		return IStreamIn_Byte.WHITESPACE.indexOf(c) >= 0; }
	
	/** @return true when this Character is a Letter	 */
	public static boolean isLetter(int c) {
		return ((c >= CHR_a && c <= CHR_z) ||
				(c >= CHR_A && c <= CHR_Z)); }
	
	/** @return true when this Character is a Number	 */
	public static boolean isDigit(int c) {
		return (c >= CHR_0 && c <= CHR_9); }
	
	/** @return true, when c is a Hex Digit	 */
	public static boolean isHexDigit(int c) {
		return (isDigit(c)	|| ((c >= CHR_A) && (c <= CHR_F))
							|| ((c >= CHR_a) && (c <= CHR_f)) ); }
	
	///////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/**	Reference to the Iterator giving the next Expression.
	  * This Iterator may already deliver fully parsed Objects like Numbers etc.	 */
	private InputStream in;
	
	/** StringBuffer containing the current String to be returned by currItem(). 	 */
	protected StringBuffer Buffer = new StringBuffer();
	
	/** true, if
	  * @see currItem() has been called after
	  * @see nextItem() */
	protected boolean currItemCalled; // = false; //not necessary!
	
	/** ByRefByte containing the Code for the current Character. 	 */
	final public ByRefInt currChar = new ByRefInt();
	
	/** Escape Character.
	  * Escapes the following Character to enable parsing.
	  * The Escape Character itself is filtered out, except if escaped!  */
	public char EscapeChar = IStreamIn_Byte.NO_ESCAPE; //no Escaping by Default...
	
	/** If true, the Result is cleared before the nextItem() Call
	  * It cannot be cleared on the currItem Call, because
	  * @see currItem() might be called several times
	  * the Result will be reused further.
	  *
	  * To optimize setting this Value,
	  * the Result will only be cleared if currItem() was actually called in between! */
	public boolean clearOnNext = true;
	
	/** If true, the Separators found on parsing, are removed from the Result */
	public boolean removeLast = true;
	
	///////////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public EscapeStreamIn(final InputStream _in) { this.in = _in; }
	
	///////////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////////
	
	/** @return the current Object without moving.
	  * Here it is used to return the actual Data instead of the Tokens.
	  * It also optionally removes the last Character
	  * and prepares clearing the String at the next nextItem() Call. */
	public Object currItem() {
		if (removeLast && !currItemCalled) //prevent multiple Removing...
			Buffer.setLength(Buffer.length()-1); //remove the Separator from the Token
		currItemCalled = true;
	//	if (END_OF_STREAM == currChar.Value)
	//		return EOI;
	//	else //this is only to keep the Semantics and return EOI instead of -1
			return Buffer; }
	
	/** @return the next Character and adds it to the StringBuffer.
	  * Filters out Escape Characters and wraps IO Exceptions into BaseExceptions
	  * Most simple Scanning Routine... */
	public Object nextItem() {
		if (clearOnNext && currItemCalled) {
			Buffer.setLength(0); }
		currItemCalled = false;
		try {
	//		prevChar.Value = currChar.Value; //not needed
			if (EscapeChar != (       currChar.Value = in.read())) {
				Buffer.append ((char) currChar.Value);
			}else{
				char   chr;
				switch(chr = (char) in.read()) { //standard Escaping of nonprintable Characters.
					case 't':  Buffer.append(  9); break; //Tab
					case 'n':  Buffer.append( 10); break; //New Line (Line Feed)
					case 'f':  Buffer.append( 12); break; //Form Feed
					case 'r':  Buffer.append( 13); break; //Carriage Return
					default :  Buffer.append(chr); //add the Item literally
				} //returns the Escape Char to prevent parsing the escaped Char
	//		}else if (END_OF_STREAM == currChar.Value) {
	//			return EOI; //currChar = EOI; //not necessary!
			}
		} catch (IOException x) { throw new BaseException(x.toString()); }
		return currChar; }
	
	//  Optimizations...
	
	/** Searches for the first Occurrence of this Character in the streamIO
	  * still assembles all Characters	*/
	public Object findNext(Object Item) {
		if (EscapeChar != IStreamIn_Byte.NO_ESCAPE) //didn't want to insert all the Logic
			return super.findNext(Item);
		int    Item_  = ((ByRefInt) Item).Value;
		while (true) { //this short loop is considerably faster!
			try {
				if (EscapeChar != (       currChar.Value = in.read())) {
					Buffer.append ((char) currChar.Value);
				}else{
					char   chr;
					switch(chr = (char) in.read()) { //standard Escaping of nonprintable Characters.
						case 't':  Buffer.append(  9); break; //Tab
						case 'n':  Buffer.append( 10); break; //New Line (Line Feed)
						case 'f':  Buffer.append( 12); break; //Form Feed
						case 'r':  Buffer.append( 13); break; //Carriage Return
						default :  Buffer.append(chr); //add the Item literally
					} //returns the Escape Char to prevent parsing the escaped Char
				}
			} catch (IOException x) { throw new BaseException(x.toString()); }
			if ((currChar.Value == Item_) ||
				(currChar.Value == InputStream2StreamIn.END_OF_STREAM))
				return currChar; } } //it saves returning each Item and the Call to the equals() Method!
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation
	  * @return this StreamIn to allow for Concatenation 	 */
	public IReSetAble reSet() { //throws NoSuchMethodException{
		try { in.reset();
		} catch (final IOException x) { 
		    throw new IOError(x); 
		}
		return this; }
	
	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(long Position) { //throws    NoSuchMethodException {
		try { in.reset();
		} catch (final IOException x) { 
		    throw new IOError(x); 
		}
		return jump(Position); } //cannot use IS.skip(), because Characters don't match Tokens!
	
	/** @return the next Item without moving to it.	 */
	public Object peekItem() { //throws    NoSuchMethodException {
	    throw new OperationNotSupported(EscapeStreamIn.class); } //not possible, left same Implementation as in AStreamIn
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return ORDER_NONE; }

	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.markSupported() ? Long.MAX_VALUE : -1; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException {
		in.mark(Integer.MAX_VALUE); return this; }
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() { //consider Escaping! at least half of the Characters are valid
		try { return (in.available() + 1) >> 1;
		} catch (final IOException x) { 
			throw new BaseException(x.toString()); }
	}
	
}
