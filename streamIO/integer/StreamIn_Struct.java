/*
 * Created on 15.03.2006
 *
 */
package streamIO.integer;

import java.io.IOException;
import java.io.StringReader;

import math.vector.VectorString;
import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.StringBufferOutputStream;
import streamIO.diffPatch.VersionTree;
import streamIO.diffPatch.VersionedObjects;
import streamIO.exception.BaseException;
import streamIO.integer.adapter.ReaderToStreamIn_Byte;
import streamIO.integer.encoding.FilterLookup;
import streamIO.object.parser.InputStream2StreamIn;
import tools.IOError;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Implements a generic, configurable Scanner & Parser for Data Structures 
 * like XML, JSON, YAML, CSV or Tab-separated Files. 
 * Restores whole Object Hierarchies from the Stream. 
 * 
 * Design Decisions / Implementation Details:
 * Exposes all Methods of the Parent Class and thus allows 
 * to flexibly mix the Techniques for reading and Parsing within the same Stream. 
 * 
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
final public class StreamIn_Struct 
extends StreamIn_Primitive 
implements IStreamIn_Struct, IStreamIn_StructX {
	
	/** The Logger for this Class	 */
	private static final Log L = new Log(StreamIn_Struct.class); 
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static String Constants
	///////////////////////////////////////////////////////////////////////////////
	
	/**Error Message String Constant      */
	final static public String STR_ERR_EXPECTED = " Expected: ";
	
	/**Error Message String Constant      */
	final static public String STR_ERR_OCCURRED = " Occurred: ";
	
	/**Error Message String Constant      */
	final static public String STR_TOKEN = "Token ";
	
	///////////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param _stream the Stream to read from 
	 */
	public StreamIn_Struct(final IStreamIn_Byte _stream) { super(_stream); }
	
	/**
	 * 
	 * @param _stream the Stream to read from 
	 * @param separators the Separator Characters to use. 
	 */
	public StreamIn_Struct(final IStreamIn_Byte _stream, final String separators) {
		super(_stream);
		setSeparators(separators); 
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//  abstract Methods
	///////////////////////////////////////////////////////////////////////////////
		
	/** @see streamIO.integer.IStreamIn_Byte#available()	 */
	//abstract public int available() throws IOException; 
	
	/** return the Order in which Elements are returned by the Iterators
	 * @return the Order in which Elements are returned by the Iterators
	 * when they are added using addItem() and removed using nextItem().
	 * @see streamIO.IOrdered#getOrder()	 */
	//abstract public byte getOrder(); //{ return ORDER_NONE; } 

	/** @see streamIO.IAvailAble#getPosition()	 */
	//abstract public long getPosition(); 
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	//abstract public long getMaxMarkSize(); 
	
	/** @see streamIO.integer.IStreamIn_Byte#read()	 */
	//abstract public int read() throws IOException; 
	
	/** @see streamIO.integer.IStreamIn_Byte#close()	 */
	//abstract public void close() throws IOException;
	
	///////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/** @return the Reference to the current Input streamIO.  */
	//public IStreamIn_Byte getStreamIn() { return is; }
	
	/** Sets the current Input streamIO.   */
	//public void setStreamIn(final IStreamIn_Byte Value) { is = Value; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Separator Handling
	////////////////////////////////////////////////////////////////////////////
	
	/** Inverse Mapping of the Separator Characters to their Position for faster parsing
	  * This saves the Scanning Loop for long Lists of Separators, but is of little use otherwise.	 
	  * Not made final due to the setSeparators() Method!  
	  */
	protected byte[] invSeps;
	
	/** late Initialization: Sets the Separator of this Parser to this Value.
	  * This is necessary, 
	  * because the outer Parsers determine the inner Parsers' Separators, 
	  * but must know the inner Parsers to call their Methods.
	  * This saves the Scanning Loop for long Lists of Separators, but is of little use otherwise.	 
	  * @param the Separators String
	  */
	public void setSeparators(final String Separators) { this.invSeps = CALC_SEPS(Separators); }
	
	/** return the Token Value for the given Character
	 * @return the Token Value for the given Character
	 * @param sep the Character to retrieve the Token Value for */
	public byte getSep(final char sep) {
		if (sep >= invSeps.length) 
			return 0; 
		return invSeps[sep+1];
	}
	
	/** sets the Token Value for the given Character
	 * @param sep the Character to set the Token Value for */
	final public void setSep(final char sep) { setSep(sep, TAG_EOF); }
	
	/** sets the Token Value for the given Character
	 * @param val the Token Value to set (0 for unsetting the Token)
	 * @param sep the Character to set the Token Value for */
	final public void setSep(final char sep, final byte val) {
		if (sep >= invSeps.length) {
			final byte[] tmp = new byte[sep+1]; 
			System.arraycopy(invSeps, 0, tmp, 0, invSeps.length); 
			invSeps = tmp; }
		invSeps[sep+1] = val; 
	}
	
	/** static Method to be used by the Constructor
	  * @param the Separators String containing Escape-Char and Separators in ascending Priority. 
	  * If first and second Character are identical, no Escaping takes place! 
	  */
	final static public byte[] CALC_SEPS(final String Separators) {
		if (Separators == null) 
			return null; 
		int cMax = 0; 
		for (byte i = (byte) Separators.length(); --i >= 0; ) //find out the maximum Character
			if (cMax < Separators.charAt(i)) 
				cMax = Separators.charAt(i); 
		final byte[] inverseSep = new byte[cMax+2]; //new Array Contents is already initialized to 0 (or null)
		inverseSep[IStreamIn_Byte.EOF+1] = TAG_EOF; //the EOF Character is added as a Watcher Element! Saves one Check...
		for (byte i = (byte) Separators.length(), token = (byte) (TAG_EOF-Separators.length()-1); --i >= 0; ) //invert the mapping.
			inverseSep[Separators.charAt(i)+1] = ++token;
		return inverseSep; 
	}
	
	/**
	 * reads the Separator Definitions directly from the given Stream. 
	 * The Convention is: 
	 * 1st Char is the Escape Char
	 * all following Characters up to the next Occurrence of the Escape Char 
	 * are Separator Characters with descending Priority / ascending Value. 
	 * 
	 * Design Decisions: 
	 * mark() and reset() can be applied externally before and after calling this Method. 
	 * @param is the Stream to read from 
	 * @return a StringBuffer filled with the Escape and Separator Characters. 
	 * @throws IOException  */
	final static public StringBuffer READ_SEPS(final IStreamIn_Byte is) throws IOException {
		final StringBuffer seps = new StringBuffer(9);
		int EscapeChar, chr = EscapeChar = (char) is.read();  //first Character is Escape Symbol (necessary to transfer the Separator)
		//seps.append((char)EscapeChar);
		for(;;) { //read and assemble the next Characters...
			do { chr = is.read(); //skip all other Characters... 
			} while(chr != EscapeChar); //although I could already parse them now  
			if (EscapeChar == (chr = is.read())) { //a double Escape Symbol succeeds the last Separator...
				seps.append(EscapeChar); //setSeparators(seps.toString()); 
				return seps; //...and signals the End of Separator Definition
			} else { //Separators are preceded by a single Escape Symbol
				if (VectorString.INDEX_OF(seps, (char) chr, false) < 0)
					seps.append((char)chr); //duplicate Separators should be ignored (ambiguous Priority!)
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Overwriting Implementation of AStreamIn_Byte
	////////////////////////////////////////////////////////////////////////////
	
	/** StringBuffer containing the current String to be returned by currItem(). 	 */
	final public StringBuffer buffer = new StringBuffer(); 
	
	/** If true, the Separators found on parsing, are removed from the Result */
	public boolean removeLast = true;
	
	/** @return the current Object (the collected StringBuffer) without moving.
	  * Here it is used to return the actual Data instead of the Tokens.
	  * It also optionally removes the last Character
	  * and prepares clearing the String at the next nextItem() Call.
	  */
	public StringBuffer currBuffer() {
		if (EOF == currToken)
			return null; //EOI; //this is only to keep the Semantics and return EOI instead of -1
		return buffer; }
	
	/** @return the current Object (the collected StringBuffer) without moving.
	  * Here it is used to return the actual Data instead of the Tokens.
	  * It also optionally removes the last Character
	  * and prepares clearing the String at the next nextItem() Call.
	  */
	public String currString() {
		if (EOF == currToken)
			return null; //EOI; //this is only to keep the Semantics and return EOI instead of -1
		return buffer.substring(0, buffer.length()-(removeLast ? 1 : 0)); }
	
	/** @return the next Token or Character (in a ByRefInt when Separator == null or == "")
	  * and adds the intermediate Characters to the StringBuffer returned by currItem().
	  * Wraps IO Exceptions into BaseExceptions
	  * Most simple Scanning Routine...
	  * Design Decisions:
	  * handing back the ByRefInt. 
	  * To hand back the actual Object filter using Parser2StreamIn! 
	  */
	public StringBuffer nextBuffer() {
		if (TAG_EOF == currToken)
			return null; //EOI; //this is only to keep the Semantics and return EOI instead of -1
		if (!clearOnNext) //if not done anyway...
			buffer.setLength(0);  //...do it now!
		try { nextToken(); //to read the last String too! 
		} catch(final IOException x) {
			throw new BaseException("IOException during Parsing:", x); }
		if (removeLast) //prevent multiple Removing...
			buffer.setLength(buffer.length()-1); //remove the Separator from the Token
		return currBuffer(); 
	}
	
	/** @see streamIO.integer.IStreamIn_Struct#nextString()	 */
	public String nextString() {
		if (EOI == nextBuffer())
			return null; //EOI; 
		return buffer.toString(); 
	}
	
	/** @return the next Token or Character (in a ByRefInt when Separator == null or == "")
	  * and adds the intermediate Characters to the StringBuffer returned by currItem().
	  * Wraps IO Exceptions into BaseExceptions
	  * Most simple Scanning Routine...
	  * Design Decisions:
	  * handing back the ByRefInt. 
	  * To hand back the actual Object filter using Parser2StreamIn! 
	  */
	public Object nextItem() { return nextBuffer(); }
	
	/** @return the current Object (the collected StringBuffer) without moving.
	  * Here it is used to return the actual Data instead of the Tokens.
	  * It also optionally removes the last Character
	  * and prepares clearing the String at the next nextItem() Call.
	  */
	public Object currItem() { return currBuffer(); }
	
	////////////////////////////////////////////////////////////////////////////
	
	/** the minimum Token to exceed.
	 * This allows to read or skip the Data in larger Batches.  	 */
	public int minToken = TAG_PLAIN; 
	
	/** used solely in currToken()	 */
	public int currToken; //
	
	/** @see streamIO.integer.IStreamIn_Struct#currToken()	 */
	public int currToken() { return currToken;	}
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() {
		currToken = TAG_PLAIN;
		return super.reSet(); }
	
	/**
	 * If true, the Result is automatically cleared at the Start of nextToken()
	 * if false, the String is assembled across several Calls. */
	public boolean clearOnNext = false; //true; //
	
	/** @return the next Token or Character (in a ByRefInt when Separator == null or == "")
	  * and add the intermediate Characters to the StringBuffer returned by currItem().
	  * Most simple Scanning Routine...
	  * Design Decisions:
	  * Created to provide a typesafe and more intuitively named Routine,
	  * additionally to the generic nextItem() Routine!
	  */
	public int nextToken() throws IOException {
		if (clearOnNext) 
			buffer.setLength(0); 
		return currToken = NEXT_TOKEN(streamByte, invSeps, buffer, minToken); 
		//currItem.Value = Separator.charAt();
		//return currToken; 
	} 
	
	////////////////////////////////////////////////////////////////////////////
	//	static Methods for basic Parsing
	////////////////////////////////////////////////////////////////////////////
	
	/** return the next Token larger than minToken
	 * and add the intermediate Characters to the StringBuffer returned by currItem().
	 * 
	 * Design Decisions:
	 * made static to hand over the Stream directly thus saving expensive Delegation. 
	 * 
	 * @param is the most direct Reference to the Input Stream (to save Delegation)
	 * @param invSeps the inverse Mapping of Separator Characters 
	 * @return the next Token larger than minToken */
	final static public int NEXT_TOKEN(final IStreamIn_Byte is
			, final byte[] invSeps) throws IOException {
		return NEXT_TOKEN(is, invSeps, 0); } 
	
	/** return the next Token larger than minToken
	 * and add the intermediate Characters to the StringBuffer returned by currItem().
	 * 
	 * Design Decisions:
	 * made static to hand over the Stream directly thus saving expensive Delegation. 
	 * 
	 * @param is the most direct Reference to the Input Stream (to save Delegation)
	 * @param invSeps the inverse Mapping of Separator Characters 
	 * @param minToken the minimum Token to stop at, useful to rapidly browse through Streams 
	 * @return the next Token larger than minToken */
	final static public int NEXT_TOKEN(final IStreamIn_Byte is
			, final byte[] invSeps, final int minToken) throws IOException {
		return NEXT_TOKEN(is, invSeps, null, minToken);	} 
	
	/** return the next Token larger than minToken
	 * and add the intermediate Characters to the StringBuffer returned by currItem().
	 * 
	 * Design Decisions:
	 * made static to hand over the Stream directly thus saving expensive Delegation. 
	 * 
	 * @param is the most direct Reference to the Input Stream (to save Delegation)
	 * @param invSeps the inverse Mapping of Separator Characters 
	 * @param minToken the minimum Token to stop at, useful to rapidly browse through Streams 
	 * @return the next Token larger than minToken */
	final static public int NEXT_TOKEN(final IStreamIn_Byte is
			, final byte[] invSeps, final StringBuffer buffer) throws IOException {
		return NEXT_TOKEN(is, invSeps, buffer, 0);	} 
	
	/** return the next Token larger than minToken
	 * and add the intermediate Characters to the StringBuffer returned by currItem().
	 * This is THE fundamental Parser Method. 
	 * 
	 * Design Decisions:
	 * made static to be able to hand over the Stream directly 
	 * thus saving expensive Delegation. 
	 * 
	 * @param is the most direct Reference to the Input Stream (to save Delegation)
	 * @param invSeps the inverse Mapping of Separator Characters 
	 * @param minToken optional minimum Token to stop at, useful to rapidly browse through Streams 
	 * @param buffer optional (null allowed) Buffer to collect the Characters 
	 * @return the next Token larger than minToken */
	final static public int NEXT_TOKEN(final IStreamIn_Byte is
			, final byte[] invSeps, final StringBuffer buffer
			, final int _minToken) throws IOException {
		if (invSeps == null) {
			final int ret = is.read(); 
			if((buffer != null) && (ret >= 0)) //allow for quickly reading through the Data
				buffer.append ((char) ret);  //without Escaping!
			return ret; }
		int currItem, currChar; 
		char currQuote = 0; 
		int minToken = _minToken; 
		for (;;) { //nested fast inner loop
			do { //prevChar.Value = currItem.Value; //not needed
				currChar = is.read(); 
				if (buffer != null) //allow for quickly reading through the Data
					buffer.append ((char) currChar);  //without Escaping!
			} while ((currChar >= invSeps.length-1) || //not found, because larger than Array
					((currItem  = invSeps[currChar+1]) <= minToken)); //not found, because not EOF in Array
			switch   (currItem) {
				case TAG_QUOTE: 
					if (currQuote == 0) { //start Quotation Section
						currQuote = (char) currChar; 
						minToken = TAG_QUOTE-1; 
						StringBufferOutputStream.PULL_BACK(buffer);
					} else 	if (currQuote == currChar) { 
						if (minToken != _minToken) { //mark for End of Quotation
							minToken  = _minToken; StringBufferOutputStream.PULL_BACK(buffer);
						} else { //Quote Character is escaped
							currQuote = (char) currChar; 
							minToken = TAG_QUOTE-1; 
						}
					} else {//end of Quotation
						currQuote  = 0; 
						//continue; 
					} 
						//continue; //alternative Quote Character encountered					
					break;
				case TAG_ESCAPE: 
					StringBufferOutputStream.REPLACE_LAST(buffer, READ_ESCAPED(is)); 
					break;
				default:
					return currItem; 
			}
		}
	} 
	
	/** Routine to parse an Escape Sequence	
	 * Precondition: The Escape Character was already removed 
	 * @return the decoded Escape Sequence */
	public static final char READ_ESCAPED(final IStreamIn_Byte is) throws IOException {
		final char chr = FilterLookup.ESCAPE2ASCII((char) is.read()); 
		switch (chr) {
			case 'x': return (char) StreamIn_Primitive.READ_INT_FROM(is, 16, 2); 
			case 'u': return (char) StreamIn_Primitive.READ_INT_FROM(is, 16, 4); 
			case 'U': return (char) StreamIn_Primitive.READ_INT_FROM(is, 16, 8); 
		}
		return chr; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	specific typed Reader Methods for reuse. 
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected int[] intBuf = new int[5]; 
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream */
	public int[] nextInts() { 
		final int   len = nextInts(Integer.MAX_VALUE, 0); 
		final int[] ret = new int[len]; 
		System.arraycopy(intBuf, 0, ret, 0, len); 
		return ret; }
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream */
	public int nextInts(final int[] ret) {
		return nextInts(null, (ret != null) ? ret.length : Integer.MAX_VALUE); }
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream */
	public int nextInts(final int[] ret, final int stop) {
		return nextInts(ret, stop, 0); }
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream */
	public int nextInts(final int[] ret, final int stop, int start) {
		start = nextInts(stop, start);
		System.arraycopy(intBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextInts(final int stop, int start) {
		try { currItem.Value = streamByte.read(); //skip Separator
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		for(; start < stop; ) {
			final double dbl = nextDouble(); 
			if (dbl != dbl) 
				break; 
			if (start >= intBuf.length) {
				final int[] tmp = new int[intBuf.length+intBuf.length+1]; 
				System.arraycopy(intBuf, 0, tmp, 0, intBuf.length); 
				intBuf = tmp; 
			}
			intBuf[start++] = (int) dbl; 
			if (TAG_COL < currToken)
				break; 
		}
		//streamByte.pushBack(); //to restore the last Separator Character! 
		return start;
	}


	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected short[] shortBuf = new short[5];  
	
	/** @see streamIO.integer.IStreamIn_Struct#nextShorts() */
	public short[] nextShorts() { 
		final int len = nextShorts(Integer.MAX_VALUE, 0); 
		final short[] ret = new short[len]; 
		System.arraycopy(shortBuf, 0, ret, 0, len); 
		return ret; }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextShorts(short[]) */
	public int nextShorts(final short[] ret) {
		return nextShorts(null, (ret != null) ? ret.length : Integer.MAX_VALUE); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextShorts(short[], int) */
	public int nextShorts(final short[] ret, final int stop) {
		return nextShorts(ret, stop, 0); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextShorts(short[], int, int) */
	public int nextShorts(final short[] ret, final int stop, int start) {
		start = nextShorts(stop, start);
		System.arraycopy(shortBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextShorts(final int stop, int start) {
		for(; start < stop; ) {
			final double dbl = nextDouble(); 
			if (dbl != dbl) 
				return start; //break; 
			if (start >= shortBuf.length) {
				final short[] tmp = new short[shortBuf.length+shortBuf.length+1]; 
				System.arraycopy(shortBuf, 0, tmp, 0, shortBuf.length); 
				shortBuf = tmp; 
			}
			shortBuf[start++] = (short) dbl; 
			if (TAG_COL < currToken)
				return start; 
		}
		return start;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected long[] longBuf = new long[5];  
	
	/** @see streamIO.integer.IStreamIn_Struct#nextLongs() */
	public long[] nextLongs() { 
		final int len = nextLongs(Integer.MAX_VALUE, 0); 
		final long[] ret = new long[len]; 
		System.arraycopy(longBuf, 0, ret, 0, len); 
		return ret; }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextLongs(long[]) */
	public int nextLongs(final long[] ret) {
		return nextLongs(null, (ret != null) ? ret.length : Integer.MAX_VALUE); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextLongs(long[], int) */
	public int nextLongs(final long[] ret, final int stop) {
		return nextLongs(ret, stop, 0); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextLongs(long[], int, int) */
	public int nextLongs(final long[] ret, final int stop, int start) {
		start = nextLongs(stop, start);
		System.arraycopy(longBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextLongs(final int stop, int start) {
		for(; start < stop; ) {
			final double dbl = nextDouble(); 
			if (dbl != dbl) 
				return start; //break; 
			if (start >= longBuf.length) {
				final long[] tmp = new long[longBuf.length+longBuf.length+1]; 
				System.arraycopy(longBuf, 0, tmp, 0, longBuf.length); 
				longBuf = tmp; 
			}
			longBuf[start++] = (long) dbl; 
			if (TAG_COL < currToken)
				return start; 
		}
		return start;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected float[] floatBuf = new float[5];  
	
	/** @see streamIO.integer.IStreamIn_Struct#nextFloats() */
	public float[] nextFloats() { 
		final int len = nextFloats(Integer.MAX_VALUE, 0); 
		final float[] ret = new float[len]; 
		System.arraycopy(floatBuf, 0, ret, 0, len); 
		return ret; }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextFloats(float[]) */
	public int nextFloats(final float[] ret) {
		return nextFloats(null, (ret != null) ? ret.length : Integer.MAX_VALUE); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextFloats(float[], int) */
	public int nextFloats(final float[] ret, final int stop) {
		return nextFloats(ret, stop, 0); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextFloats(float[], int, int) */
	public int nextFloats(final float[] ret, final int stop, int start) {
		start = nextFloats(stop, start);
		System.arraycopy(floatBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextFloats(final int stop, int start) {
		for(; start < stop; ) {
			final double dbl = nextDouble(); 
			if (dbl != dbl) 
				return start; //break; 
			if (start >= floatBuf.length) {
				final float[] tmp = new float[floatBuf.length+floatBuf.length+1]; 
				System.arraycopy(floatBuf, 0, tmp, 0, floatBuf.length); 
				floatBuf = tmp; 
			}
			floatBuf[start++] = (float) dbl; 
			if (TAG_COL < currToken)
				return start; 
		}
		return start;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected double[] doubleBuf = new double[5];  
	
	/** @see streamIO.integer.IStreamIn_Struct#nextDoubles() */
	public double[] nextDoubles() { 
		final int len = nextDoubles(Integer.MAX_VALUE, 0); 
		final double[] ret = new double[len]; 
		System.arraycopy(doubleBuf, 0, ret, 0, len); 
		return ret; }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextDoubles(double[]) */
	public int nextDoubles(final double[] ret) {
		return nextDoubles(null, (ret != null) ? ret.length : Integer.MAX_VALUE); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextDoubles(double[], int) */
	public int nextDoubles(final double[] ret, final int stop) {
		return nextDoubles(ret, stop, 0); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextDoubles(double[], int, int) */
	public int nextDoubles(final double[] ret, final int stop, int start) {
		start = nextDoubles(stop, start);
		System.arraycopy(doubleBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextDoubles(final int stop, int start) {
		for(; start < stop; ) {
			final double dbl = nextDouble(); 
			if (dbl != dbl) 
				return start; //break; 
			if (start >= doubleBuf.length) {
				final double[] tmp = new double[doubleBuf.length+doubleBuf.length+1]; 
				System.arraycopy(doubleBuf, 0, tmp, 0, doubleBuf.length); 
				doubleBuf = tmp; 
			}
			doubleBuf[start++] = dbl; 
			if (TAG_COL < currToken)
				return start; 
		}
		return start;
	}
	
	/** when Errors are expected, the Client should check 
	 * (currToken != TAG_PLAIN) && (ret == 0) 
	 * @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	public double nextDouble() {
		final double ret; 
		//if(currToken == TAG_PLAIN) // 
		//	currItem.Value = streamByte.read(); 
		ret = READ_DOUBLE_FROM_SAFE(streamByte, locale, currItem);
		currToken = getTag(currItem.Value);
		return ret; }
	
	/** Decodes the given Character returned by read() into a Token 	 */
	public final int getTag(final int chr) {
		if (chr < invSeps.length-1) 
			return invSeps[chr+1];  
		return TAG_PLAIN; }
		
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected String[] stringBuf = new String[5];  
	
	/** @see streamIO.integer.IStreamIn_Struct#nextStrings() */
	public String[] nextStrings() { 
		final int len = nextStrings(Integer.MAX_VALUE, 0); 
		final String[] ret = new String[len]; 
		System.arraycopy(stringBuf, 0, ret, 0, len); 
		return ret; }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextStrings(java.lang.String[]) */
	public int nextStrings(final String[] ret) {
		return nextStrings(null, (ret != null) ? ret.length : Integer.MAX_VALUE); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextStrings(java.lang.String[], int) */
	public int nextStrings(final String[] ret, final int stop) {
		return nextStrings(ret, stop, 0); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextStrings(java.lang.String[], int, int) */
	public int nextStrings(final String[] ret, final int stop, int start) {
		start = nextStrings(stop, start);
		System.arraycopy(stringBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextStrings(final int stop, int start) {
		for(; start < stop; ) {
			final String curr = nextString(); 
			if (EOI == curr) 
				return start; //break; 
			if (start >= stringBuf.length) {
				final String[] tmp = new String[stringBuf.length+stringBuf.length+1]; 
				System.arraycopy(stringBuf, 0, tmp, 0, stringBuf.length); 
				stringBuf = tmp; 
			}
			stringBuf[start++] = curr; 
			if (TAG_COL < currToken)
				return start; 
		}
		return start;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	
	/** Buffer to collect a List of int Values	*/
	protected Object[] objectBuf = new Object[5];  
	
	/** @see streamIO.integer.IStreamIn_Struct#nextStrings() */
	public Object[] nextItems() { 
		final int len = nextItems(Integer.MAX_VALUE, 0); 
		final Object[] ret = new Object[len]; 
		System.arraycopy(objectBuf, 0, ret, 0, len); 
		return ret; }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextItems(Object[]) */
	public int nextItems(final Object[] ret) {
		return nextItems(ret, ret.length); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextItems(Object[], int) */
	public int nextItems(final Object[] ret, final int stop) {
		return nextItems(ret, stop, 0); }
	
	/** @see streamIO.integer.IStreamIn_Struct#nextItems(Object[], int, int) */
	public int nextItems(final Object[] ret, final int stop, int start) {
		start = nextItems(stop, start);
		System.arraycopy(objectBuf, 0, ret, 0, start); 
		return start; }
	
	/**
	 * @param stop
	 * @param start
	 * @return
	 */
	private int nextItems(final int stop, int start) {
		for(; start < stop; ) {
			final Object curr = nextItem(); 
			if (EOI == curr) 
				return start; //break; 
			if (start >= objectBuf.length) {
				final Object[] tmp = new Object[objectBuf.length+objectBuf.length+1]; 
				System.arraycopy(objectBuf, 0, tmp, 0, objectBuf.length); 
				objectBuf = tmp; 
			}
			objectBuf[start++] = curr; 
			if (TAG_COL < currToken)
				return start; 
		}
		return start; }
	
	/** 
	 * streams this IStreamIn_Struct to the given IStreamOutStruct 
	 * @see StreamOutInstantiator allows to restore the whole Object Hierarchy! 
	 * @param out the IStreamOutStruct Instance to stream to
	 */
	public void stream(final IStreamOutStruct out) throws IOException {
		//minToken = TAG_FORMAT; //TAG_PLAIN; //this is Default
		clearOnNext = true; 
		//String currName = null; //need to cache it here to prevent early 
		for(int token = nextToken();;) {
			//final String str = nextString().trim(); //remove WhiteSpace
			switch (token) {
				case TAG_FORMAT: break; //only a Formatting Character here!
				case TAG_ROW   : break; //only a Formatting Character here!
				case TAG_CLOSE : out.closeStruct(); break; 
				case TAG_OPEN  : out.open_Struct(); break; //currName); break; //should trigger a readField Method
				case TAG_PAIR  : out.writeName(currString()); break; //currName = currString(); break; //
				case TAG_COL   : out.addString(currString()); break; //a new Name could start here! 
				case TAG_EOF   : return; 
				case TAG_ESCAPE: throw new IOException("Should never pop up to here!"); //break; 
				case TAG_QUOTE : throw new IOException("Should never pop up to here!"); //break;
				case TAG_PLAIN : break; 
				default        : throw new IOException("Should never pop up to here!"); //break;
			}
			 token  = nextToken();
			//(token != currToken) //Stream was not read by the Data itself!
			//? currToken 
			//: nextToken();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	public static void testStreaming() throws Exception {
		final VersionTree vs = VersionedObjects.testIt(); 
		final String original = vs.toString(); 
		final StringReader sr = new StringReader(original); 
		final IStreamIn_Byte inStream = new ReaderToStreamIn_Byte(sr); 
		final StreamIn_Struct reader = new StreamIn_Struct(inStream, DEFAULT_SEPS); 
		final StreamOutInstantiator instant = new StreamOutInstantiator(reader); 
		reader.stream(instant); 
		final String copy = instant.currObj.toString(); 
		Assert.EQUALS(original, copy); 
	}
	
	/** Tests all Methods of this Class	 */
	public static void testArrays() throws Exception {
		final StringReader sr = new StringReader("1,2,3,%,3,2,1"); 
		final IStreamIn_Byte inStream = new ReaderToStreamIn_Byte(sr); 
		final StreamIn_Struct parser  = new StreamIn_Struct(inStream, IStreamIn_Struct.DEFAULT_SEPS);
		int count; 
		
		parser.reSet(); count = 0; 
		for(String str; EOI != (str = parser.nextString()); ) {
			L.n(str); ++count; 
		}
		Assert.EQUALS(7, count); 
		
		parser.reSet(); count = 0;  
		for(int str; (TAG_PLAIN != (str = parser.nextInt())) || (TAG_COL >= parser.currToken());) {
			L.n(str); ++count; 
		}
		Assert.EQUALS(8, count); 
		
		parser.reSet(); 
		final String[] strings = parser.nextStrings();  
		Assert.EQUALS(7, strings.length); 
		
		parser.reSet(); 
		final Object[] items = parser.nextItems();  
		Assert.EQUALS(7, items.length); 
		
		parser.reSet(); 
		final int[] ints = parser.nextInts(); //reads the Values correctly! 
		Assert.EQUALS(3, ints.length); 
		
		parser.reSet(); 
		final short[] shorts = parser.nextShorts();  
		Assert.EQUALS(3, shorts.length); 
		
		parser.reSet(); 
		final long[] longs = parser.nextLongs();  
		Assert.EQUALS(3, longs.length); 
		
		parser.reSet(); 
		final float[] floats = parser.nextFloats();  
		Assert.EQUALS(3, floats.length); 
		
		parser.reSet(); 
		final double[] doubles = parser.nextDoubles();  
		Assert.EQUALS(3, doubles.length); 
		
	}
	
	/** Tests all Methods of this Class	 */
	public static void testTags() throws Exception {
		final StreamIn_Struct parser = new StreamIn_Struct(null, DEFAULT_SEPS); 
		Assert.EQUALS(TAG_EOF   , parser.getTag(-1)); 
		Assert.EQUALS(TAG_ESCAPE, parser.getTag('\\')); 
		Assert.EQUALS(TAG_QUOTE , parser.getTag('"')); 
		Assert.EQUALS(TAG_OPEN  , parser.getTag('{')); 
		Assert.EQUALS(TAG_CLOSE , parser.getTag('}')); 
		Assert.EQUALS(TAG_COL   , parser.getTag(',')); 
		Assert.EQUALS(TAG_PLAIN , parser.getTag('A')); 
		Assert.EQUALS(TAG_ROW   , parser.getTag('\n')); 
	}

	/**
	 * 
	 */
	private static void testDoubleToLong() {
	}

	/** Tests all Methods of this Class	 */
	public static void testQuoting() throws Exception {
		//java.nio.ByteBuffer buf; byte byt = buf.get(); 
		final StringReader sr = new StringReader("Hallo, hier ist ein '\\'doppelt'' gequoteter, zusammenhängender' String, der nicht getrennt werden sollte!");
		final IStreamIn_Byte inStream = new ReaderToStreamIn_Byte(sr); 
		final String Separators = "\\', "; 
		final StreamIn_Struct parser = new StreamIn_Struct(inStream, Separators); 
		int count = 0; parser.reSet();
		for(StringBuffer str; EOI != (str = parser.nextBuffer()); ) {
			L.n(str); ++count; 
		}
		Assert.EQUALS(13, count); 
	}

	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		L.n("Testing " + InputStream2StreamIn.class.getName()); 
		testStreaming(); 
		testDoubleToLong(); 
		testTags(); 
		testArrays(); 
		testQuoting(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	specific typed Reader Methods for reuse. 
	////////////////////////////////////////////////////////////////////////////
	
}
