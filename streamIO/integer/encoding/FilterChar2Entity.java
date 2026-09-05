package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import streamIO.Assert;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.LocalePrimitive;
import streamIO.integer.adapter.ReaderToStreamIn_Byte;
import streamIO.integer.filter.FilterByte;

/**
  * Recodes the Characters coming through this Input streamIO
  * by converting their Values into a decimal or hexadecimal Representation.
  * This is NOT a Size-preserving Transformation!
  *
  * Usually you would chain FilterChar2String and FilterChar2Entity so
  * that first FilterChar2String is applied, eliminating all those Characters, 
  * that can safely be substituted by simple Combinations like ae, ue and oe for �,� and �
  * or can be replaced by readable Entity Names (that need to be declared in a DTD though).
  * 
  * In Reverse also the Decoding can be chained,
  * because FilterChar2Entity uses a different (longer) Escape Prefix
  * than FilterChar2String and should be applied first!
  *
  * Known SubClasses:
  *
  * Related Classes: 
  * @see FilterChar2String uses readable Entity Names, that require DTD Declaration
  * @see FilterChar2Entity uses hex or decimal Entities, that don't need Declaration
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:36:19Z
  * digest: 4695c9fe54350d7096a0053c2cc8f5ec84c9fd0e4f1f1b2fca5179318d4bef2a
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterChar2Entity 
extends FilterByte {

	/** Filters a String using the given Filter */
	final static public StringBuffer ENCODE_STRING(final String arg)
		throws IOException { //
		return ENCODE_STRING(arg, new StringBuffer(arg.length() * 6));
	}

	/** Filters a String using the given Filter */
	final static public StringBuffer ENCODE_STRING(
		final String arg,
		StringBuffer sb)
		throws IOException { //more effective to use Readers (active) than Writers...
		final IStreamIn_Byte filter =
			GET_XML_ENCODER(new ReaderToStreamIn_Byte(new StringReader(arg)));
		return filter.read(-1, sb);
		//		.toString().trim(); //read up to the End...
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Character starting an XML String Encoding
	 * Starting with the same Character considerably speeds up Parsing!
	 */
	final static public String XML_NUMBER_ENTITY_START = "&#";
	
	/** Character starting an XML String Encoding
	 * Starting with the same Character considerably speeds up Parsing!
	 */
	final static public String XML_NUMBER_ENTITY_START_HEX = "&#x";
	
	/** Character ending an XML String Encoding
	 * Ending with the same Character considerably speeds up Parsing!
	 */
	final static public String XML_NUMBER_ENTITY_STOP = ";";
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// Factory Methods for complete XML Encoding of the Unicode Character Set
	////////////////////////////////////////////////////////////////////////////////
	
	/** Maximum ASCII Character is only 7 Bit */
	final static public char MAX_ASCII_CHAR = 0x80;
	
	/** Maximum ASCII Character is only 7 Bit */
	final static public char MAX_ANSI_CHAR = 0x100;
	
	/** Radix used for encoding the Data */
	final static public int ENTITY_RADIX = 10;
	
	/** Maximum ASCII Character is only 7 Bit */
	public static char MAX_XML_CHAR = MAX_ASCII_CHAR;
	
	/** XML String Encoding Filter Factory  */
	final static public FilterChar2Entity GET_XML_ENCODER(OutputStream out) {
		return new FilterChar2Entity(
			out,
			XML_NUMBER_ENTITY_START,
			XML_NUMBER_ENTITY_STOP,
			MAX_XML_CHAR,
			ENTITY_RADIX);
	}
	
	/** XML String Encoding Filter Factory  */
	final static public FilterChar2Entity GET_XML_ENCODER(InputStream in) {
		return new FilterChar2Entity(
			in,
			XML_NUMBER_ENTITY_START,
			XML_NUMBER_ENTITY_STOP,
			MAX_XML_CHAR,
			ENTITY_RADIX);
	}
	
	/** XML String Encoding Filter Factory  */
	final static public FilterChar2Entity GET_XML_ENCODER(IStreamIn_Byte in) {
		return new FilterChar2Entity(
			in,
			XML_NUMBER_ENTITY_START,
			XML_NUMBER_ENTITY_STOP,
			MAX_XML_CHAR,
			ENTITY_RADIX);
	}
	
	/** XML String Encoding Filter Factory  */
	final static public FilterChar2Entity GET_XML_ENCODER(IStreamOutByte out) {
		return new FilterChar2Entity(
			out,
			XML_NUMBER_ENTITY_START,
			XML_NUMBER_ENTITY_STOP,
			MAX_XML_CHAR,
			ENTITY_RADIX);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Radix for the String Representation of the Value */
	protected int radix; // = 0x10;

	/** Start Character for a String encoded Section */
	protected String start;

	/** Stop Character for a String encoded Section */
	protected String stop;

	/** Minimum encoded Character */
	protected char minChar;

	/** Maximum encoded Character */
	protected char maxChar;

	/** Collects the Characters on reading and writing */
	private StringBuffer SB = new StringBuffer();

	/** Flag, set after an Entity Start String was encountered */
	//private boolean collecting; //= false;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Constructor
	 * @param chars_ The Characters to be encoded
	 * @param strings_ The Strings to encode the Characters
	 */
	public FilterChar2Entity(
		IStreamIn_Byte streamIn_,
		String start_,
		String stop_,
		char minChar,
		char maxChar,
		int radix) {
		super(streamIn_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = radix;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor	 */
	public FilterChar2Entity(
		InputStream streamIn_,
		String start_,
		String stop_,
		char minChar,
		char maxChar,
		int radix) {
		super(streamIn_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = radix;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor
	 */
	public FilterChar2Entity(
		IStreamOutByte streamOut_,
		String start_,
		String stop_,
		char minChar,
		char maxChar,
		int radix) {
		super(streamOut_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = radix;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor	 */
	public FilterChar2Entity(
		OutputStream streamOut_,
		String start_,
		String stop_,
		char minChar,
		char maxChar,
		int radix) {
		super(streamOut_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = radix;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor
	 * @param chars_ The Characters to be encoded
	 * @param strings_ The Strings to encode the Characters
	 */
	public FilterChar2Entity(
		IStreamIn_Byte streamIn_,
		String start_,
		String stop_,
		char minChar,
		int radix) {
		this(streamIn_, start_, stop_, minChar, Character.MAX_VALUE, radix);
	}

	/** Constructor	 */
	public FilterChar2Entity(
		InputStream streamIn_,
		String start_,
		String stop_,
		char minChar,
		int radix) {
		this(streamIn_, start_, stop_, minChar, Character.MAX_VALUE, radix);
	}

	/** Constructor
	 */
	public FilterChar2Entity(
		IStreamOutByte streamOut_,
		String start_,
		String stop_,
		char minChar,
		int radix) {
		this(streamOut_, start_, stop_, minChar, Character.MAX_VALUE, radix);
	}

	/** Constructor	 */
	public FilterChar2Entity(
		OutputStream streamOut_,
		String start_,
		String stop_,
		char minChar,
		int radix) {
		this(streamOut_, start_, stop_, minChar, Character.MAX_VALUE, radix);
	}

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	  * Reads the next Character from this input stream.
	  * The general contract for read is that one char is read from the input stream.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the input stream has been closed.
	  */
	public int read() throws IOException {
		if (SB.length() > 0) {
			int chr = SB.charAt(0);
			SB.deleteCharAt(0);
			return chr;
		}
		int chr = streamIn.read();
		if ((chr < minChar) || (chr > maxChar)) {
			return chr;
		}
		SB.append(start).append(Integer.toString(chr, radix)).append(stop);
		return read();
	}

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(final int chr) throws IOException {
		if ((chr < minChar) || (chr > maxChar)) { //write it unchanged
			streamOut.write(chr);
		} else { //encode it
			//final String str = Integer.toString(chr, radix);
			AStreamOutByte.WRITE(streamOut, start);
			LocalePrimitive.ADD_LONG_SAFE(streamOut, chr);
			AStreamOutByte.WRITE(streamOut, stop);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		final String strUmlaute = "aeiou���AEIOU���_";
		final String strExpected =
			"aeiou&#228;&#246;&#252;AEIOU&#196;&#214;&#220;_";
		final String strEncoded = ENCODE_STRING(strUmlaute).toString();
		Assert.EQUALS(strExpected, strEncoded);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		testIt(args);
	}

}
