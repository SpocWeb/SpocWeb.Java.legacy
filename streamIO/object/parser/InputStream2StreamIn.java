package streamIO.object.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import math.vector.VectorString;
import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.exception.BaseException;
import streamIO.exception.OperationNotSupported;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.adapter.InputStreamToStreamIn_Byte;
import streamIO.integer.encoding.FilterLookup;
import streamIO.integer.file.FileStreamByte;
import streamIO.integer.file.FileStreamIn_Byte;
import streamIO.integer.file.FileStreamOutByte;
import streamIO.object.AStreamIn;
import tools.IOError;
import function.byref.ByRefInt;

/** InputStream2StreamIn
  * New Approach to define performant (and pluggable) Parsing,
  * replacing those in Package Byte.
  * 
  * This Tokenizer is implemented at the lowest Level (i.e. byte/char Input streamIO)
  * to reduce nested Calling of nextItem() for low Level Items like single Characters.
  * To enable both Pluggable Filters and good Performance
  * it implements the IParser Interface which allows higher Levels
  * to dynamically add or remove Separator Characters for their own Purpose. 
  * Escaping and Masking can be plugged in, when necessary
  * and doesn't impact the Performance anymore.
  * If the Separator String is empty, the whole streamIO is returned as a single Object.
  * If it is null, single Characters are returned.
  * 
  * To be able to extend the Grammar, it is necessary to allow for direct Access to the Stream, 
  * e.g. to test for Key Characters and to overwrite the Dispatcher Method 
  * to be able to add new Productions. 
  * 
  * Each Call to nextItem() returns the same ByRefInt Object
  * containing the Position of the found Separator Character in the Separator String.
  * Each subsequent Call to currItem() returns the same assembled StringBuffer.
  * This also truncates the assembled String, which would otherwise grow.
  * (This is a Means to intermediately ignore Separator Characters)
  * 
  * @see ParserFromStreamIn which translates this somewhat inconsistent Protocol
  * into a consistent "Parser" Protocol with SubStreams.
  * 
  * It is the first and last Element for processing a streamIO and replaces
  * @see EscapeStreamIn.
  * It's Methods are a Mix of
  * @see  EscapeStreamIn and
  * @see ScannerStreamIn.
  * @see streamIO.StreamOutPrimitive which does the reverse: 
  * write a Stream of Objects to an Output Stream.  
  * 
  * Properties Files / Streams Support:
  * Allows to read a Properties File/streamIO up to a certain last key
  * and to determine the Position of the Value of a certain optional key in it.
  *
  * Design Decisions:
  */
public class InputStream2StreamIn
extends AStreamIn
implements IParserIn {
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static Constants
	///////////////////////////////////////////////////////////////////////////////
	
	/**Error Message String Constant      */
	final static public String STR_ERR_EXPECTED = " Expected: ";
	
	/**Error Message String Constant      */
	final static public String STR_ERR_OCCURRED = " Occurred: ";
	
	/**Error Message String Constant      */
	final static public String STR_TOKEN = "Token ";
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Indicator: Character returned at the End of a streamIO 	*/
	final static public int END_OF_STREAM = -1;
	
	/** Indicator of a non Separator Token.
	  * The Value corresponds to the Position in the Separator String */
	final static public int SCN_TAG_PLAIN = -2;
	
	/** Indicator of the End of the Input streamIO.
	  * The Value -1 corresponds to the Position in the Separator String
	  * and is chosen to be the same as StreamTokenizer.TT_EOF for consistency. 	 */
	final static public int SCN_TAG_EOF = java.io.StreamTokenizer.TT_EOF;
	
	/** Indicates the start Tag of a new Element, the Name of the Element is returned
	  * The Value corresponds to the Position in the Separator String */
	final static public int SCN_TAG_ESCAPE = 0;
	
	/** Indicates the start Tag of a new Element, the Name of the Element is returned
	  * The Value corresponds to the Position in the Separator String */
	//final static public int SCN_TAG_START = 0;
	
	/**Indicates the Stop Tag of an Element, the Name of the Element is returned
	  * The Value corresponds to the Position in the Separator String */
	//final static public int SCN_TAG_STOP = 1;
	
	/**Indicates an Attribute Element using Apostrophe or Quote,
	 * Name and Value are returned in an Association Object	 */
	//final static public int SCN_TAG_ATTRIBUTE = 2;
	
	/** Default Value for the Capitalization of the Parameter Keys	 */
	final static public boolean capitalizeByDefault = false;
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * reads the Separator Definitions directly from the given Stream. 
	 * The Convention is: 
	 * 1st Char is the Escape Char
	 * all following Characters up to the next Occurrence of the Escape Char 
	 * are Separator Characters with descending Priority / ascending Value. 
	 * @param is the Stream to read from 
	 * @return a StringBuffer filled with the Escape and Separator Characters. 
	 * @throws IOException 
	 */
	final static public StringBuffer READ_SEPS(final IStreamIn_Byte is) throws IOException {
		is.mark(); 
		final StringBuffer seps = new StringBuffer();
		int EscapeChar, chr = EscapeChar = (char) is.read();  //first Character is Escape Symbol (necessary to transfer the Separator)
		seps.append((char)EscapeChar);
		int prevChar; 
		for(;;) { //read and assemble the next Characters...
			do { //skip all other Characters, although I could already parse them now...
				prevChar = chr; chr = is.read(); 
			} while(chr != EscapeChar);  
			chr = is.read();
			if (chr == EscapeChar) { //a double Escape Symbol succeeds the last Separator...
				seps.append((char)prevChar); //setSeparators(seps.toString()); 
				is.reSet(); //what if the Stream is not resettable? Wrap it with a Buffer?
				return seps; //...and signals the End of Separator Definition
			} else { //Separators are preceded by a single Escape Symbol
				if (VectorString.INDEX_OF(seps, (char) chr, false) < 0)
					seps.append((char)chr); //duplicate Separators should be ignored
			}
		}
		//the Stream should be reset to read from the Start. 
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/**	Reference to the Iterator giving the next Expression.
	  * This Iterator may already deliver fully parsed Objects like Numbers etc.	 */
	private IStreamIn_Byte is; // InputStream IS;
	
	/** @return the Reference to the current Input streamIO.  */
	public IStreamIn_Byte getStreamIn() { return is; }
	
	/** Sets the current Input streamIO.   */
	public void setStreamIn(final IStreamIn_Byte Value) { is = Value; }
	
	/** Inverse Mapping of the Separator Characters to their Position for faster parsing
	  * This saves the Scanning Loop for long Lists of Separators, but is of little use otherwise.	 
	  * Not made final due to the setSeparators() Method!  
	  */
	private byte[] inverseSep;
	
	/** StringBuffer containing the current String to be returned by currItem(). 	 */
	protected final StringBuffer Buffer = new StringBuffer();
	
	/** true, if currItem() is being called after nextItem() 
	  * @see currItem() 
	  * @see nextItem() 
	  */
	protected boolean currItemCalled = true; //to allow getting currItem() before the first nextItem()!
	
	/** Escape Character.
	  * Escapes the following Character to enable parsing.
	  * The Escape Character itself is filtered out, except if escaped!
	  * The Escape Character can also be used
	  * to filter out CRLFs, if absolutely necessary.
	  */
	//public char EscapeChar = IStreamIn_Byte.NO_ESCAPE; //no Escaping by Default...
	
	/** ByRefByte containing the Code for the current Character.
	  * Returned by nextItem() 	 */
	final public ByRefInt currChar = new ByRefInt();
	
	/**
	 * If true, the Result is automatically cleared at the Start of nextItem()
	 * if false, the String is assembled across several Calls.
	 * It cannot be cleared on the currItem Call, because
	 * @see currItem() might be called several times
	 * the Result will be reused further.
	 *
	 * To optimize setting this Value,
	 * the Result will only be cleared if currItem() was actually called in between!
	 */
	public boolean clearOnNext = false;
	
	/** If true, the Separators found on parsing, are removed from the Result */
	public boolean removeLast = true;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX/isXXX/makeXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** String containing all Separator Characters with descending Priority, 0 is Escape Char, 1 Row Sep etc. 	 */
	protected String seps; 
	
	/** late Initialization: Sets the Separator of this Parser to this Value.
	  * This is necessary, 
	  * because the outer Parsers determine the inner Parsers' Separators, 
	  * but must know the inner Parsers to call their Methods.
	  * @param the Separators String
	  */
	public String getSeparators() { return seps; }
	
	/** late Initialization: Sets the Separator of this Parser to this Value.
	  * This is necessary, 
	  * because the outer Parsers determine the inner Parsers' Separators, 
	  * but must know the inner Parsers to call their Methods.
	  * This saves the Scanning Loop for long Lists of Separators, but is of little use otherwise.	 
	  * @param the Separators String
	  */
	public void setSeparators(final String Separators) {
		//this.seps = Separators; 
		this.inverseSep = CALC_SEPARATORS(Separators);
	}
	
	/** static Method to be used by the Constructor
	  * @param the Separators String containing Escape-Char and Separators in ascending Priority. 
	  * If first and second Character are identical, no Escaping takes place! 
	  */
	final static public byte[] CALC_SEPARATORS(final String Separators) {
		if (Separators == null) 
			return null; 
		int cMax = 0;
		for (byte i = (byte) Separators.length(); --i >= 0; ) //find out the maximum Character
			if (cMax < Separators.charAt(i)) 
				cMax = Separators.charAt(i); 
		final byte[] inverseSep = new byte[cMax+2]; //new Arrays are already initialized (null or 0)!
//		while (--cMax >= 0) //could use the new Array Methods to prefill the Array.
//			InverseSep[cMax] = (byte) -1; //but I rather add 2 to all Elements (which I later have to remove again!)
		for (byte i = (byte) Separators.length(); --i >= 0; ) //invert the mapping.
			inverseSep[Separators.charAt(i)+1] = (byte) (i+2);
		//inverseSep[IStreamIn_Byte.EOF+1] = 1; //the EOF Character is added as a Watcher Element! Saves one Check...
		inverseSep[1-1] = 1; //the EOF Character is added as a Watcher Element! Saves one Check...
		return inverseSep; 
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////////
	
	/** Constructor for late Initialization. 
	 * reads the Separators intrinsically from the Stream. 
	 * @param IS_ InputStream that returns Tokens (nextItem) and Strings (currItem)
	 * @see InputStream2StreamIn#setSeparators(String) has to be called for full Initialization call 
	 */
	public InputStream2StreamIn(final IStreamIn_Byte _IS) throws IOException { // InputStream IS_) {
		this(_IS, null); }
	
	/** Constructor for late Initialization. 
	 * reads the Separators intrinsically from the Stream. 
	 * @param IS_ InputStream that returns Tokens (nextItem) and Strings (currItem)
	 * @see InputStream2StreamIn#setSeparators(String) has to be called for full Initialization call 
	 */
	public InputStream2StreamIn(final InputStream _IS) throws IOException { // InputStream IS_) {
		this(_IS, null); }
	
	/** Initializing Constructor
	  * @param IS_ InputStream that returns Tokens (nextItem) and Strings (currItem)
	  * @param optional String Separators is max. 127 Characters long
	  * which allows using the smaller 'byte' Type and should be sufficient
	  * for most Applications (127 dimensional Data)
	  * The Priority of the Separator is it's Position in the String. 
	  * If null, reads the Separators intrinsically from the Stream.
	  */
	public InputStream2StreamIn(final InputStream _IS, final String _Separators) throws IOException {
		this(new InputStreamToStreamIn_Byte(_IS), _Separators); 
	}
	
	/** Initializing Constructor
	  * @param IS_ InputStream that returns Tokens (nextItem) and Strings (currItem)
	  * @param optional String Separators is max. 127 Characters long
	  * which allows using the smaller 'byte' Type and should be sufficient
	  * for most Applications (127 dimensional Data)
	  * The Priority of the Separator is it's Position in the String. 
	  * If null, reads the Separators intrinsically from the Stream.
	  */
	public InputStream2StreamIn(final IStreamIn_Byte _IS, final String _Separators) throws IOException {
		this.is = _IS; 
		this.seps = (_Separators == null) ? READ_SEPS(_IS).toString() : _Separators;
		inverseSep = CALC_SEPARATORS(seps); 
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return is.getMaxMarkSize(); }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return is.getPosition(); }
	
	/** @return the current Object (the collected StringBuffer) without moving.
	  * Here it is used to return the actual Data instead of the Tokens.
	  * It also optionally removes the last Character
	  * and prepares clearing the String at the next nextItem() Call.
	  */
	public Object currItem() {
		if (removeLast && !currItemCalled) //prevent multiple Removing...
			Buffer.setLength(Buffer.length()-1); //remove the Separator from the Token
		currItemCalled = true;
//		if (END_OF_STREAM == currChar.Value)
//			return EOI; //this is only to keep the Semantics and return EOI instead of -1
			return Buffer; }
	
	/** used solely in nextItem()	 */
	protected ByRefInt currToken = new ByRefInt(); //why create a new Instance all the time?
	
	/** @return the next Token or Character (in a ByRefInt when Separator == null or == "")
	  * and adds the intermediate Characters to the StringBuffer returned by currItem().
	  * Wraps IO Exceptions into BaseExceptions
	  * Most simple Scanning Routine...
	  * Design Decisions:
	  * handing back the ByRefInt. 
	  * To hand back the actual Object filter using Parser2StreamIn! 
	  */
	public Object nextItem() {
		try { return nextToken(); //inner loop for Performance Reasons
		} catch (final IOException x) {
			throw new BaseException("IOException during Parsing:", x); }
	}
	
	/** Flag to indicate that the Separators are not complete yet! 	 */
	//boolean readingSeps = true; 
	
	/** @return the next Token or Character (in a ByRefInt when Separator == null or == "")
	  * and add the intermediate Characters to the StringBuffer returned by currItem().
	  * Most simple Scanning Routine...
	  * Design Decisions:
	  * Created to provide a typesafe and more intuitively named Routine,
	  * additionally to the generic nextItem() Routine!
	  */
	public ByRefInt nextToken() throws IOException {
		if (inverseSep == null) {
			currChar.Value = currToken.Value = is.read();
			return currToken; 
		}
		if (clearOnNext || currItemCalled) 
			Buffer.setLength(0); 
		currItemCalled = false;
		for (;;) {
			do { //read and assemble the next Characters...
	//			prevChar.Value = currChar.Value; //not needed
				Buffer.append ((char) (currChar.Value = is.read()));  //without Escaping!
			} while ((currChar .Value >=  inverseSep.length-1) || //not found, because larger than Array
					((currToken.Value  = (inverseSep[currChar.Value +1] -2)) == SCN_TAG_PLAIN)); //not found, because not EOF in Array
			if ((currToken.Value == SCN_TAG_ESCAPE)) {
				if (doEscape)
					Buffer.setCharAt(Buffer.length()-1, FilterLookup.ESCAPE2ASCII((char) is.read())); //Decode the Standard Escape Sequences
				else if (removeLast)
					Buffer.setLength(Buffer.length()-1); 
			} else 
				break; 
		}
		return currToken; } //currChar; }

	/** Flag to switch off Escaping	 */
	public boolean doEscape = true; 
	
	/** @return the Order in which Elements are returned by the Iterators
	  * when they are added using addItem() and removed using nextItem().	 */
	public byte getOrder() { return ORDER_NONE; }

	///////////////////////////////////////////////////////////////////////////////////
	/// Properties Files and Streams Support
	///
	/// Allows to read a Properties File
	/// and to determine the Position of the Value of a certain optional Key in it.
	/// Property Lines must not contain more than one separator! 
	///////////////////////////////////////////////////////////////////////////////////
	
	/** Reads until the End of the current Line
	 *  i.e. until the most significant Separator is returned.
	 *  The Tokens are lost, except for the last one.
	 *  @return the Number of Elements skipped.
	 */
	public int readLine() {
		int ret = 0; 
		for (ByRefInt item; 0 < (item = (ByRefInt) nextItem()).Value;)
			++ret; //search for the End of Line
		return ret; } //exit the Loop
	
	/**
	 * Reads a Parameter List, i.e. Properties List from the InputStream.
	 * @see java.util.Properties
	 * @return String[2] containing the key and Value
	 */
	public String[] readParameter() {
		String[] ret = new String[2];
		readParameter(ret);
		return ret; }

	/**
	 * Reads a single Parameter, i.e. Property List from the InputStream.
	 * @see java.util.Properties
	 * @param KeyValue String[2] containing the key and Value in the Order read
	 * @return the Number of Elements in this 'Row'
	 */
	public int readParameter(final String[] KeyValue) {
		ByRefInt item = (ByRefInt) nextItem();
		KeyValue[0] = currItem().toString();
		if (item.Value <= 0) { //empty Value
			KeyValue[1] = null; 
			return 1; 
		} 
		item = (ByRefInt) nextItem();
		KeyValue[1] = currItem().toString();
		return 1+readLine(); //
	}

	/**
	 * Reads a Parameter List, i.e. Properties List from the InputStream.
	 * @see java.util.Properties
	 * @param capitalize if true, all Parameter Names are capitalized
	 * to enable Case Insensitive Access.
	 * Defaulted to @see capitalizeByDefault
	 * @return the Parameters read into a HashTable
	 */
	public Hashtable readParameters() {
		return readParameters(capitalizeByDefault, null, null); }
	
	/**
	 * Reads a Parameter List, i.e. Properties List from the InputStream.
	 * @see java.util.Properties
	 * @param capitalize if true, all Parameter Names are capitalized
	 * to enable Case Insensitive Access.
	 * @return the Parameters read into a HashTable
	 */
	public Hashtable readParameters(boolean capitalize) {
		return readParameters(capitalize, null, null); }

	/**
	 * Reads a Parameter List, i.e. Properties List from the InputStream.
	 * @see java.util.Properties
	 * @param lastKey key at  which to stop reading
	 * @param capitalize if true, all Parameter Names are capitalized
	 * to enable Case Insensitive Access.
	 * @return the Parameters read into a HashTable
	 */
	public Hashtable readParameters(boolean capitalize, String lastKey) {
		return readParameters(capitalize, lastKey, null); }

	/**
	 * Reads a Parameter List, i.e. Properties List from the InputStream.
	 * @see java.util.Properties
	 * @param lastKey key at  which to stop reading
	 * @param  PosKey key for which to read the Position of the Value
	 * @param capitalize if true, all Parameter Names are capitalized
	 * to enable Case Insensitive Access.
	 * @return the Parameters read into a HashTable
	 */
	public Hashtable readParameters(boolean capitalize, String lastKey, String PosKey) {
		Hashtable ret = new Hashtable();
		readParameters(ret, capitalize, lastKey);
		return ret; }

	/**
	 * Fills the given Parameter List, i.e. Properties List from the InputStream.
	 * Useful to
	 * @see java.util.Properties
	 * @param lastKey key at  which to stop reading
	 * @param capitalize if true, all Parameter Names are capitalized
	 * to enable Case Insensitive Access.
	 * @return the File Position of the
	 */
	public int readParameters(Hashtable this_, boolean capitalize, String lastKey) {
		return readParameters(this_, capitalize, lastKey, null); }

	/**
	 * Fills the given Parameter List, i.e. Properties List from the InputStream.
	 * Used to read Name-Value Pairs e.g. for ResultSets. 
	 * @see java.util.Properties
	 * @param lastKey key at  which to stop reading
	 * @param  PosKey key for which to read the Position of the Value
	 * @param capitalize if true, all Parameter Names are capitalized
	 * to enable Case Insensitive Access.
	 * @return the File Position of the Value of the PosKey key
	 * -1 if PosKey is null or does not occur.
	 */
	public int readParameters(final Map map, final boolean capitalize
	, String lastKey, String posKey) {
		int ret = -1;
		if (capitalize) {
			if (lastKey != null) { lastKey = lastKey.toUpperCase(); }
			if ( posKey != null) { posKey  = posKey .toUpperCase(); } }
		if (posKey == null) {
			ret = 0; } //don't search for PosKey
		final String[]KeyValue = {"", ""}; //new String[2]; //initial Values
		try {
			Log.N("Table-Parameters:");
			while( (KeyValue[1] != null) &&
				 ( !KeyValue[1].equals(lastKey))) { 	//Read the Parameters and skip the Comments
				if (ret < 0) { //if no PosKey found yet...
					ret = -(int) ((FileStreamByte) is).getFilePointer(); }
				final int numFields = readParameter(KeyValue);
				Log.N(KeyValue[0]).l(KeyValue[1]);
				if ((numFields > 2) || (KeyValue[1] == null)) 
					return ret;  //prevent adding null Key to the Hashtable
				if (capitalize) {
					KeyValue[1] = KeyValue[1].toUpperCase(); }
				if((ret < 0) && (posKey.equals(KeyValue[1]))) { //if the Key matches...
					ret = -ret; } //store it
				map.put(KeyValue[1], KeyValue[0]); }
		} catch (IOException x) {
			Log.N(x); 
		}
		return ret; }

	////////////////////////////////////////////////////////////////////////////
	//  Optimizations...
	////////////////////////////////////////////////////////////////////////////
	
	/** Searches for the first Occurrence of this Character in the streamIO
	  * still assembles all Characters
	  * This is an Optimization, because it avoids calling Subroutines! */
	public Object findNext(Object Item) {
		int Item_  = ((ByRefInt) Item).Value;
		try {
			while (true) { //this short loop is considerably faster!
//				prevChar.Value = currChar.Value; //not needed
				Buffer.append ((char) (currChar.Value = is.read()));
				if (inverseSep == null) return currChar;  //return it character wise
			if ((currChar.Value == Item_) ||
				(currChar.Value == END_OF_STREAM))
				return currChar; }  //it saves returning each Item and the Call to the equals() Method!
			} catch (IOException x) { throw new BaseException(x.toString()); }
	}

	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation
	  * @return this StreamIn to allow for Concatenation 	 */
	public IReSetAble reSet() { //throws NoSuchMethodException{
		is.reSet();
		return this; }

	/** Resets the Iterator to the given Position
	  * counted from the last marked Position.
	  * @return the Number of Positions actually skipped	 */
	public long reSet(final long position) { //throws	NoSuchMethodException {
		is.reSet();
		return jump(position); 
	} //cannot use IS.skip(), because Characters don't match Tokens!

	/** @return the next Item without moving to it.	 */
	public Object peekItem() { //throws	NoSuchMethodException {
		throw new OperationNotSupported(InputStream2StreamIn.class); } //not possible, left same Implementation as in AStreamIn

	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * at the last marked position.	 */
	public IMarkAble mark() { //throws NoSuchMethodException {
		is.mark(Integer.MAX_VALUE); return this; }

	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  *
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  */
	public long availAble() { //consider Escaping! at least half of the Characters are valid
		try {
			return (is.available() + 1) >> 1;
		} catch (final IOException x) { 
		    throw new IOError(x); 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Methods for treating exemplary File Formats
	////////////////////////////////////////////////////////////////////////////
	
	/** Test Method:
	  * Parses a separated streamIO
	  * and writes the Tokens out into a streamIO with exactly the same Separators
	  *
	  */
	final static public void Identical(
		String InputFile,
		String OutputFile,
		String Separator
		) throws IOException {
		IStreamIn_Byte FI = new FileStreamIn_Byte(InputFile);
		FileStreamOutByte FO = new FileStreamOutByte(OutputFile);
		PrintStream PS = new PrintStream(FO);
//		StreamIn Scannr =
		MaskedStreamIn Scannr =
			new MaskedStreamIn(';', '\n', false,
			new MaskedStreamIn('"', '"', false,
			new InputStream2StreamIn(FI)));
		Scannr.setSeparators(Separator);
		String strToken;
		int Token;
		while ((Token = ((ByRefInt) Scannr.nextItem()).Value) != InputStream2StreamIn.SCN_TAG_EOF) { //EOI) { //Scanner.SCN_TAG_EOF) {
			strToken = ((StringBuffer) Scannr.currItem()).toString();
			switch(Token) {
//				case 0:
				default: //lesser Separators, no matter which Level (Space)
					PS.print(strToken + Separator.charAt(Token)); //add the correct Separator
					break; //results in the identical Mapping
			}
		}
		PS.close();
	}
	
	/** Parses a separated streamIO
	  * and writes the Tokens out into a streamIO
	  */
	final static public void PARSE_FILE(
		final String InputFile,
		final String OutputFile,
		final String Separator
		) throws IOException {
		final IStreamIn_Byte FI = new FileStreamIn_Byte(InputFile);
		final FileStreamOutByte FO = new FileStreamOutByte(OutputFile);
		final PrintStream PS = new PrintStream(FO);
//		final StreamIn Scannr =
		final MaskedStreamIn scanner =
			new MaskedStreamIn(';', '\n', false,
			new MaskedStreamIn('"', '"' , false,
			new InputStream2StreamIn(FI)));
		scanner.setSeparators(Separator);
		int token; //, curr = 0, counter, numCol = 0, numRep = 0;
		int collect = -1;
		int nest = 0;
		while ((token = ((ByRefInt) scanner.nextItem()).Value) != InputStream2StreamIn.SCN_TAG_EOF) { //EOI) { //Scanner.SCN_TAG_EOF) {
//			strToken = ((StringBuffer) Scannr.currItem()).toString();
			switch(token) {
				case 0: ++nest; //'('
					break;
				case 1: if (--nest == collect) {
					PS.println(((StringBuffer) scanner.currItem()).toString() + Separator.charAt(token)); //add the correct Separator
					collect = -1; } //')'
					break;
				case 2: collect = nest-1; //'@' instead of 'is' //start collecting
				default: //lesser Separators, no matter which Level (Space)
					break;
			}
			if (collect < 0) {
				scanner.currItem();   //clear the Buffer
			}else{
				PS.print(((StringBuffer) scanner.currItem()).toString() + Separator.charAt(token)); //add the correct Separator
			}
		}
//		writer.println();
		PS.close();
	}

	/**
	  * This Procedure extracts Data from the Cycorp Notation System
	  */
	final static public void PARSE_CYCORP_LOOM() throws IOException {
		//Post Processing is required by removing the Starter Combinations
		String Path = "D:/Personal/Webs/SemanticWeb/UpperModel/";
		String InputFile = Path + "GUM-2-0a_loom.txt";
		String OutputFile;
		String Separator = "()@";
		OutputFile = Path + "Objects.txt";
		PARSE_FILE (InputFile, OutputFile, Separator);

	}

	/**
	 * Beispiel für das Ermitteln statistischer Funktionen aus Dateien:
	 * Vorkommen bestimmter Zeichen v.a. { und }
	 * Vorkommen bestimmter Zeichenfolgen (keywords wie class, for, while, if etc.)
	 *
	 */
	
	/**
	  * Reads all Characters from the input stream 
	  * until one of the Separator Characters is encountered 
	  * and stores them into the StringBuffer b.
	  * 
	  * @param _sep the Separator Characters to stop after. 
	  * @param b - the StringBuffer into which the data is read.
	  * @return the same StringBuffer
	  * @throws IOException - if an I/O error occurs on reading the first Byte.
	  * @see read(char[], int, int)
	final static public StringBuffer READ(final IStreamIn_Byte _this
			, final String _sep) throws IOException {
		return READ(_this, _sep, null); }
	  */
	
	/**
	  * Reads all Characters from the input stream 
	  * until one of the Separator Characters is encountered 
	  * and stores them into the StringBuffer b.
	  * 
	  * @param _sep the Separator Characters to stop after. 
	  * @param b - the StringBuffer into which the data is read.
	  * @return the same StringBuffer
	  * @throws IOException - if an I/O error occurs on reading the first Byte.
	  * @see read(char[], int, int)
	final static public StringBuffer READ(final IStreamIn_Byte _this, final String _sep, 
	        StringBuffer b) throws IOException {
	    if (b == null) 
	    	b  = new StringBuffer(); 
		int     val; //= Sep-1;
		while(((val =  _this.read ()) != EOF) || (_this.available() > 0))  { //read it without the EOF Character!
			if (_sep.indexOf(val) >= 0) 
			    return b; //read it with the Separator Character.
			b.append((char) (val)); //to be able to distinguish from the EOF Case!
		}
		return b; }
	  */
	
	/**
	 * parses the 1Dim. Fields from an input streamIO
	 * 
	 * @param fileStream_ the Stream to parse
	 * @param sep the Separator for the Fields 
	 * @param breakSep the Separator for stopping the Parsing (End of the Row) 
	 * @param allowMultipleSeps doesn't stop on consecutive Field Separators
	 * @return a List of Strings including the Separator Character, 
	 * 			which must be stripped off later
	 * @throws IOException
	 */
	final static public ArrayList PARSE_FIELDS(final IStreamIn_Byte fileStream_, 
		final byte sep, final byte breakSep, final boolean allowMultipleSeps) throws IOException {
		ArrayList AL = new ArrayList();
		StringBuffer SB = new StringBuffer();
		int chr;
		do { //read Field Names by Leading Separator Character
			chr = fileStream_.read();
			if (chr == sep) { //new Field starts with leading Separator
//				if (SB.length() > 0) {
					AL.add(SB.toString());
					SB.setLength(0);
//				}
				continue; }
			if (chr == breakSep) {
				AL.add(SB.toString());
				return AL;
			}
			SB.append((char) chr);
		} while (true);
	}
	
	/**
	 * parses the 1Dim. Fields from an input streamIO
	 * 
	 * @param fileStream_ the Stream to parse
	 * @param sep the Separator for the Fields 
	 * @param breakSep the Separator for stopping the Parsing (End of the Row) 
	 * @param allowMultipleSeps doesn't stop on consecutive Field Separators
	 * @return a List of Strings including the Separator Character, 
	 * 			which must be stripped off later
	 * @throws IOException
	 */
	final static public ArrayList PARSE_FIELDS_LAST(final IStreamIn_Byte fileStream_, 
		final byte sep, final byte breakSep, final boolean allowMultipleSeps) throws IOException {
		ArrayList AL = new ArrayList();
		StringBuffer SB = new StringBuffer();
		int chr, lastChr = breakSep;
		do { //read Field Names
			chr = fileStream_.read();
			if (chr == breakSep) {
				AL.add(SB.toString()); //add the last Field
				return AL;
			}
			if (chr == sep) {
			} else {
				if (lastChr == sep) { //new Field starts with trailing Sep.
					AL.add(SB.toString());
					SB.setLength(0);
				}
			}
			SB.append((char) chr);
			lastChr = chr;
		} while (true);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + InputStream2StreamIn.class.getName());
//		treatLoom();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
