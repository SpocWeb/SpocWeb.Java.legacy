package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import math.vector.VectorString;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;
import function.string.StringFunction;

/**
  * Recodes the Bytes coming through this Input streamIO
  * by looking up their Values in a String[] Array.
  * This is NOT a Size-preserving Transformation!
  *
  * Example Byte Arrays are given for XML Entities.
  * Only '<' and '&' (plus " and ' in Attributes) Markup needs to be escaped in XML!
  * Three other Markup Characters are defined that can, but must not be escaped:
  * '>', "'" and '"' need only be escaped, when in Danger of being recognized as Markup.
  *
  * Other XML Markup can encode all printable Latin-1 (ISO-8859-1) Characters up to 255,
  * so they can be written in UTF-8 with single Byte Unicode Characters!
  * Other Alternatives for german Umlaut Characters is their standard Replacement:
  * ss for �
  * ae for �
  * oe for �
  * ue for �
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:39:24Z
  * digest: b18d775d60d627d93688515ff4b12c0ab9767617608e6fa071e34cb498b78ba7
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterString2Char
extends FilterByte {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

	/** Looks up the given string in the map and returns one character of the result.
	 * @return the String if chr is contained in chars_,
	 *  @throws NullPointerException otherwise	 */
	final static public char LOOKUP(Map string2char, String str, int pos)
	throws NullPointerException { //IndexOutOfBoundsException {
		String ret = string2char.get(str).toString();
		return ret.charAt(pos); }

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Mapping used for decoding the incoming Bytes	 */
	protected Map string2char;

	/** The Mapping used for decoding the incoming Bytes	 */
	protected StringFunction stringFunction;

	/** Start Character for a String encoded Section */
	protected char start;

	/** Stop Character for a String encoded Section */
	protected char stop;

	/** Collects the Characters on reading and writing */
	private StringBuffer SB = new StringBuffer();

	/** Flag to indicate that a String is being collected in SB */
	private boolean collecting; //= false;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(IStreamIn_Byte streamIn_, StringFunction stringFunction_, char start_, char stop_) {
		super(streamIn_);
		this.stringFunction = stringFunction_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(InputStream streamIn_, StringFunction stringFunction_, char start_, char stop_) {
		super(streamIn_);
		this.stringFunction = stringFunction_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(IStreamOutByte streamIn_, StringFunction stringFunction_, char start_, char stop_) {
		super(streamIn_);
		this.stringFunction = stringFunction_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(OutputStream streamIn_, StringFunction stringFunction_, char start_, char stop_) {
		super(streamIn_);
		this.stringFunction = stringFunction_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(IStreamIn_Byte streamIn_, Map string2char_, char start_, char stop_) {
		super(streamIn_);
		this.string2char = string2char_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(InputStream streamIn_, Map string2char_, char start_, char stop_) {
		super(streamIn_);
		this.string2char = string2char_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(IStreamOutByte streamIn_, Map string2char_, char start_, char stop_) {
		super(streamIn_);
		this.string2char = string2char_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param string2char_ The Mapping of Strings to Characters to be decoded
	 */
	public FilterString2Char(OutputStream streamIn_, Map string2char_, char start_, char stop_) {
		super(streamIn_);
		this.string2char = string2char_;
		start = start_;
		stop = stop_;
	}

	/** Constructor
	 * @param chars_ The Characters to be encoded
	 * @param strings_ The Strings to encode the Characters
	 */
	public FilterString2Char(IStreamIn_Byte streamIn_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamIn_, VectorString.MAP(strings_, chars_), start_, stop_); }

	/** Constructor	 */
	public FilterString2Char(InputStream streamIn_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamIn_, VectorString.MAP(strings_, chars_), start_, stop_); }

	/** Constructor
	 */
	public FilterString2Char(IStreamOutByte streamOut_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamOut_, VectorString.MAP(strings_, chars_), start_, stop_); }

	/** Constructor	 */
	public FilterString2Char(OutputStream streamOut_, char[] chars_, String[] strings_, char start_, char stop_) {
		this(streamOut_, VectorString.MAP(strings_, chars_), start_, stop_); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface IStreamIn_Byte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Looks up the StringBuffer Value */
	private final char lookup() {
		return (stringFunction != null ?
				stringFunction.Map(SB.toString()) :
				string2char   .get(SB.toString()).toString()
				).charAt(0); }

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
		  * Subclasses of InputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public int read() throws IOException {
		int chr;
		if (start != (chr = streamIn.read())) {
			return chr; }
		while (stop != (chr = streamIn.read())) {
			SB.append((char) chr); }
		// TODO: LOGIC: SB is never cleared (e.g. `SB.setLength(0)`) after lookup() consumes
		// it, so the next encoded section's characters are appended onto this section's
		// leftover content instead of starting fresh, corrupting every lookup after the
		// first encoded section in a stream.
		return lookup(); }

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int chr) throws IOException {
		if (collecting) {
			if (chr != stop) {
				SB.append((char) chr);
				return; }
			// TODO: LOGIC: `collecting` is never reset to false here, and SB is never
			// cleared, after lookup() consumes the collected section - every subsequent
			// character is treated as still inside a collected section (appended to the
			// stale SB instead of written through), so only the very first encoded section
			// in a stream is ever decoded correctly.
			chr = lookup();
		} else { //!collecting
			if (chr == start) {
				collecting = true;
				return;
			}
		}
		streamOut.write(chr);
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

