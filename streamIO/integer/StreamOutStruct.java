/*
 * Created on 15.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

import streamIO.Log;
import streamIO.StringBufferOutputStream;
import streamIO.integer.encoding.FilterLookup;
import tools.IOError;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Implements a generic, configurable Writer for Data Structures 
 * like XML, JSON, CSV or Tab-separated Files. 
 * Also allows to Format and indent Data like in YAML, 
 * when chrOpen and chrClose are 0. 
 * 
 * Design Decisions / Implementation Details:
 * Exposes all Methods of the Parent Class and thus allows 
 * to flexibly mix the Techniques for reading and Parsing within the same Stream. 
 * 
 * For this it needs 
 * 1 Escape Symbol \
 * 1 or 2 Quoting Characters ' and " 
 * 1 or 2 Line Break Symbols (CR or CR/LF)
 * 2 Brackets for local and arbitrary deep Nesting: {} 
 * 1 List Separator: ,
 * 
 * alle add-Operationen sollten mit einem Separator versehen werden, 
 * damit man auch bei variablen und unbekannten Längen, 
 * die bei lesbaren Formaten unweigerlich auftreten, noch parsen kann. 
 * 
 * Bei binären Formaten ist das weniger notwendig,
 * denn die Größe der Elemente (int, byte, double etc.) sind bekannt
 * und mit der Struktur ist auch das Rekonstruieren trivial!
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
final public class StreamOutStruct 
extends AStreamOutStruct
implements IStreamOutStruct 
{
	private static final Log L = new Log(StreamOutStruct.class); 
	
	/** Flag to insert Row Breaks for Formatting */
	public boolean insertRows = true; //false; //
	
	///////////////////////////////////////////////////////////////////////////
	/// Separator Handling, could be a distince Class
	///////////////////////////////////////////////////////////////////////////
	
	/** Separator Characters	 */
	//protected String seps; 
	
	/** Separator Characters	 */
	//public String getSeps() { return seps; }
	
	/** marks all Characters in the given String as Token Values with their resp. Position
	 * @param seps the Separactor Characters
	 */
	public void setSeps(final String _seps) {
		//this.seps = _seps; 
		int i = -1; 
		if (++i < _seps.length()) chrEscape = _seps.charAt(i); 
		if (++i < _seps.length()) chrQuote  = _seps.charAt(i); 
		if (++i < _seps.length()) chrOpen   = _seps.charAt(i); //otherwise use at least Indenting for Structuring
		if (++i < _seps.length()) chrClose  = _seps.charAt(i); //else { chrTab = CHR_TAB; } 
		if (++i < _seps.length()) chrRow    = _seps.charAt(i); 
		if (++i < _seps.length()) chrCol    = _seps.charAt(i); 
		if (++i < _seps.length()) chrPair   = _seps.charAt(i); 
		if (++i < _seps.length()) chrTab    = _seps.charAt(i); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Character Constants for the Writer (faster Access than in String or Array)
	/// Assumption: these Characters don't need to be encoded 
	/// and can directly (thus faster) be written to streamByte
	/// Although they are publicly readable, they should not be modified 
	/// unless temporarily so that the original Values are restored. 
	///////////////////////////////////////////////////////////////////////////
	
	/** Escape Character for this Writer	 */
	public char chrEscape; 
	
	/** Quote Character for this Writer	 */
	public char chrQuote; 
	
	/** Structure Closing Character for this Writer	 */
	public char chrClose; 
	
	/** Structure Opening Character for this Writer	 */
	public char chrOpen; 
	
	/** Name-Value-Pair Separator Character for this Writer	 */
	public char chrPair; 
	
	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor taking an (possibly unencoded) Byte Stream
	 * @param _stream the (possibly unencoded) Byte Stream
	 */
	public StreamOutStruct(final IStreamOutByte _stream) { this(_stream, IStreamIn_Struct.DEFAULT_SEPS); }
	/** Initializing Constructor taking an (possibly unencoded) Byte Stream
	 * @param _stream the (possibly unencoded) Byte Stream
	 */
	public StreamOutStruct(final IStreamOutByte _stream, final String seps) { 
		super(_stream); 
		setSeps(seps); 
		//SP = 1; //for Modulation of Indenting Depth
	}
	
	/** @see streamIO.integer.IStreamOutStruct#getStream()	 */
	//public IStreamOutPrimitive getStreamOutPrimitive() { return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Abstract Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** All Characters above this are Escaped and Hex-Encoded	 */
	public char maxUnencodedChar = 255; 
	
	/**Overwrites the Implementation in StreamOutPrimitive 
	 * to substitute binary Encoding with readable Escape-Encoding. 
	 * @see streamIO.integer.IStreamOutByte#write(int)	 */
	public void write(final int b) throws IOException {
		//super.write(b); //requires binary Encoding 
		if (peekStruct() == chrQuote) { 
			if (b == chrQuote) 
				streamByte.write(chrQuote); // addChar(chrQuote); 
			streamByte.write(b);	
			return; 
		} 
		if (b > maxUnencodedChar) { //Assumption here: Separators are smaller Chars
			streamByte.write(chrEscape); // addChar(separators.charAt(0)); 
			streamByte.write('x'); 
			LocalePrimitive.ADD_LONG_SAFE(streamByte, b, (char)16, (char)4); 
			return; 
		} 
		if (//seps.indexOf(b) > 0)
				(b == chrClose ) || 
				(b == chrCol   ) || 
				(b == chrEscape) || 
				(b == chrOpen  ) || 
				(b == chrPair  ) || 
				(b == chrQuote ) || 
				(b == chrRow   ) ) 
		{
			streamByte.write(chrEscape); // addChar(separators.charAt(0)); 
			streamByte.write(FilterLookup.ASCII2ESCAPE((char) b)); //use Standard Escape Sequences like \t\r\n\\
			return; 
		}
		streamByte.write(b);	
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Nesting of Sections for the Writer 
	///////////////////////////////////////////////////////////////////////////
	
	protected boolean startedTag; 
	//protected String startedTag; // = false; 
	
	protected char[] structs = new char[5]; 
	
	/** @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	final protected void pushStruct(final char struct) {
		structs[SP] = struct; 
		if (++SP >= structs.length) {
			final char[] tmp = new char[structs.length+structs.length];  
			System.arraycopy(structs, 0, tmp, 0, structs.length); 
			structs = tmp; 
		}
	}
	
	/** closes the current Structure without checking	 */
	final public void closeSection() { 
		try { streamByte.write(structs[--SP]);  
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** closes the current Structure with checking	 
	 * @param chr the closing Character to check for. 
	 */
	final public void closeSection(final char chr) {
		closeSection(); 
		if (structs[SP] != chr)
			throw new RuntimeException("Structure not closed properly! \n" +
					"Expected to close with: "+structs[SP]); 
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Interface IStreamOutStruct  
	///////////////////////////////////////////////////////////////////////////
	
	/** closes all open Structures	 */
	final public void closeAll() {
		try {
			while(--SP >= 0)
				streamByte.write(structs[SP]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** closes the current Structure with checking	 
	 * @param chr the closing Character to check for. 
	 */
	final public IStreamOutStruct closeStruct(final String chr) { 
		closeSection(chr.charAt(0)); 
		return this; }
	
	/** returns the Closing Character for the currently open Structure  	 */
	final public String peek_Struct() { return new String(new char[] {peekStruct()}); }
	
	/** returns the Closing Character for the currently open Structure  	 */
	final public char peekStruct() { 
		if (SP <= 0)
			return Character.MAX_VALUE;
		return structs[SP-1]; 
	}
	
	/** Opens up a Structure with the given Opening and Closing Structures. 
	 * When both are the same (e.g. Quote Chars), the Structure cannot be nested 
	 * and is thus primitive (typically Strings)
	 * @param open  the Opening Character for the Structure 
	 * @param close the Closing Character for the Structure 
	 * @return this Stream to allow for Concatenation 
	 */
	final public void open_Section(final char open, final char close) {
		try { streamByte.write(open); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		pushStruct(close); 
	}
	
	/** @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	public IStreamOutStruct writeNameValuePair(final String name, final String value) {
		return writeNameValuePair(name, value, true); }
	
	///////////////////////////////////////////////////////////////////////////
	/// Lists of Values
	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String)	 */
	public IStreamOutStruct open_Struct(final String openClose) { 
		writeName(openClose); 
		open_Struct(); 
		return this; }
	
	/** Opens up a List of Values. 
	 * Examples are primitive Values e.g. in Arrays 
	 * or Name-Value Pairs separated e.g. by White Space like XML Attributes.  
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct open_Struct() {
		try { 
			if (!insertRows) 
				indent(2); //CR before Bracket to structure Text without insertRows  
			listChr = CHR_IGNORE; 
			if (chrOpen != CHR_IGNORE) //for purely indented Formats
				streamByte.write(chrOpen); 
			pushStruct(chrClose); 
			startedTag = true; 
			//if (insertRows) //CR after Bracket
			//	indent(2);
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** closes the current Structure without checking	 */
	final public IStreamOutStruct closeStruct() { 
		if (SP <= 1)
			return null; 
		char chr  = structs[--SP]; 
		if  (chr == chrCol) 
			 chr  = structs[--SP]; 
		if  (chr != chrClose)
			throw new RuntimeException("Structure not closed properly! \n" +
					"Expected to close with: "+structs[SP]); 
		try { 
			if (chrClose != 0) {
				if (!startedTag) // 
					indent(2); //to properly close previously opened Structures.
				streamByte.write(chrClose); 
			} else if (!startedTag && insertRows)
				indent(2); //to properly close previously opened Structures.
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		startedTag = false; 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Name-Value Pairs
	///////////////////////////////////////////////////////////////////////////
	
	/** writes the Name of a Name-Value Pair to the underlying Stream
	 * The Value can be written directly to the Stream 
	 * without having to buffer it into a String. 
	 * @param name  the Name  of the Pair
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct writeName(final String name) {
		try { 
			char struct  = peekStruct(); 
			if  (struct == chrQuote) { 
				           closeStruct(); 
				 struct  = peekStruct(); 
			}
			if  (struct == chrCol) {
				if (!startedTag && !insertRows) { 
					SP-=2; indent(2); SP+=2; // 
				} //else
				listChr(); 
			} else //start a List...
				pushStruct(chrCol); 
			if (insertRows) // == startedTag) // für leere Seps
				indent(2); // 
			WRITE(streamByte, name); //addString(name); 
			streamByte.write(chrPair); 
			listChr = CHR_IGNORE; 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** writes the given Name-Value Pair to the underlying Stream 
	 * @param name  the Name  of the Pair
	 * @param value the Value of the Pair as a single String
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct writeNameValuePair(final String name, final String value
			, final boolean useQuotes) {
		writeName(name); 
		if (useQuotes)
			open_Quote();
		addString(value); 
		if (useQuotes)
			closeQuote(); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Quoting
	///////////////////////////////////////////////////////////////////////////
	
	/** Starts the Quote-Mode. 
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct open_Quote() {
		if (peekStruct() == chrQuote)
			throw new RuntimeException("Already in Quote Mode (cannot be nested)!"); 
		open_Section(chrQuote, chrQuote); 
		return this; }
	
	/** Ends the Quote-Mode. 
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct closeQuote() {
		closeSection(chrQuote); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods to write out Arrays & Vectors of primitive Types
	///////////////////////////////////////////////////////////////////////////
	
	/** writes the given Array in structured Manner to the Stream 
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addShorts(final short[] values, final int stop, int start) {
		--start; 
		while(++start < stop) 
			addInt(values[start]); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** writes the given Array in structured Manner to the Stream 
	 * Does NOT add Field Start or End Indicators, 
	 * so several int[] Arrays can be concatenated.  
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addInts(final int[] values, final int stop, int start) {
		--start; 
		while(++start < stop) 
			addInt(values[start]); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** writes the given Array in structured Manner to the Stream 
	 * Does NOT add Field Start or End Indicators, 
	 * so several Arrays can be concatenated.  
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addLongs(final long[] values, final int stop, int start) {
		--start; 
		while(++start < stop) 
			addLong(values[start]); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** writes the given Array in structured Manner to the Stream 
	 * Does NOT add Field Start or End Indicators, 
	 * so several Arrays can be concatenated.  
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addFloats(final float[] values, final int stop, int start) {
		--start; 
		while(++start < stop) 
			addFloat(values[start]); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	boolean inList = false; 
	
	/** writes the given Array in structured Manner to the Stream 
	 * Does NOT add Field Start or End Indicators, 
	 * so several Arrays can be concatenated.  
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addDoubles(final double[] values, final int stop, int start) {
		--start; 
		while(++start < stop) 
			addDouble(values[start]); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** writes the given Array in structured Manner to the Stream 
	 * Does NOT add Field Start or End Indicators, 
	 * so several Arrays can be concatenated.  
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addStrings(final String[] values, final int stop, int start) {
		--start; 
		while(++start < stop) //TODO: possibly change to Quote Mode when any of the Seps appear to reduce Escaping? 
			addString(values[start]); 
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  IStreamOut Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** writes the given Array in structured Manner to the Stream 
	 * Does NOT add Field Start or End Indicators, 
	 * so several Arrays can be concatenated.  
	 * @param values the Values to write
	 * @param stop   the  last Value to write (exclusive)
	 * @param start  the first Value to write (inclusive)
	 * @return this Stream to allow for Concatenation 
	 */
	public IStreamOutStruct addItems(final Object[] values, final int stop, int start) {
		--start; 
		while(++start < stop) 
			addItem(values[start]); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// static Testing & Main Methods
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
		final StreamOutStruct stream = new StreamOutStruct(new StringBufferOutputStream()); 
		final double[] values = new double[9]; 
		stream.addDoubles(values); 
		stream.addDoubles(values); 
		stream.addDouble(Math.PI); 
		stream.addDouble(Math.E); 
		L.n(stream); 
	}
	
}
