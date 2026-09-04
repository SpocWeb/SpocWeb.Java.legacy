package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Title: FilterEntity2Char<p>
  * Description:
  * Recodes the Characters coming through this Input streamIO
  * by converting their Values from a decimal or hexadecimal Representation into Characters.
  * This is NOT a Size-preserving Transformation!
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterEntity2Char
extends FilterByte {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Radix for the String Representation of the Value */
	protected int radix = 0x10;

	/** Start Characters for a String encoded Section */
	protected String start;

	/** Stop Character for a String encoded Section */
	protected char stop;

	/** Minimum encoded Character */
	protected char minChar;

	/** Maximum encoded Character */
	protected char maxChar;

	/** Collects the Characters on reading and writing */
	private StringBuffer SB = new StringBuffer();

	/** current Position in the Start String */
	//private int posInStart;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor
	 * @param chars_ The Characters to be encoded
	 * @param strings_ The Strings to encode the Characters
	 */
	public FilterEntity2Char(IStreamIn_Byte streamIn_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, char maxChar, boolean hex) {
		super(streamIn_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = hex?0x10:10;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor	 */
	public FilterEntity2Char(InputStream streamIn_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, char maxChar, boolean hex) {
		super(streamIn_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = hex?0x10:10;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor
	 */
	public FilterEntity2Char(IStreamOutByte streamOut_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, char maxChar, boolean hex) {
		super(streamOut_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = hex?0x10:10;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor	 */
	public FilterEntity2Char(OutputStream streamOut_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, char maxChar, boolean hex) {
		super(streamOut_);
		this.minChar = minChar;
		this.maxChar = maxChar;
		this.radix = hex?0x10:10;
		this.start = start_;
		this.stop = stop_;
	}

	/** Constructor
	 * @param chars_ The Characters to be encoded
	 * @param strings_ The Strings to encode the Characters
	 */
	public FilterEntity2Char(IStreamIn_Byte streamIn_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, boolean hex) {
		this(streamIn_, chars_, strings_, start_, stop_, minChar, Character.MAX_VALUE, hex); }

	/** Constructor	 */
	public FilterEntity2Char(InputStream streamIn_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, boolean hex) {
		this(streamIn_, chars_, strings_, start_, stop_, minChar, Character.MAX_VALUE, hex); }

	/** Constructor
	 */
	public FilterEntity2Char(IStreamOutByte streamOut_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, boolean hex) {
		this(streamOut_, chars_, strings_, start_, stop_, minChar, Character.MAX_VALUE, hex); }

	/** Constructor	 */
	public FilterEntity2Char(OutputStream streamOut_, char[] chars_, String[] strings_, String start_, char stop_, char minChar, boolean hex) {
		this(streamOut_, chars_, strings_, start_, stop_, minChar, Character.MAX_VALUE, hex); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface IStreamIn_Byte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

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
		if (SB.length() > 0) {
			chr = SB.charAt(0);
			SB.deleteCharAt(0);
			return chr; }
		for (int pos = -1; ++pos < start.length();) {
			chr = streamIn.read();
			SB.append((char) chr);
			if (start.charAt(pos) != chr) {
				return read(); } //no Match! return the original Sequence!
		}
		while (stop != (chr = streamIn.read())) {
			SB.append((char) chr); }
		return getChar(); }

	/** @return the current Radix by Default, 0x10 when starting with 'x' */
	private char getChar() {
		int radix = this.radix;
		if (SB.charAt(0) == 'x') { //switch between defined and hex Representation
			SB.deleteCharAt(0);
			radix = 0x10; }
		char ret = (char) Integer.parseInt(SB.toString(), radix);
		SB.setLength(0);
		return ret; }

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int chr) throws IOException {
		if (SB.length() >= start.length()) {
			if (chr != stop) {
				SB.append((char) chr);
				return; }
			SB.delete(0, start.length());
			chr = getChar();
			streamOut.write(chr);
		} else { //
			SB.append((char) chr);
			if (chr == start.charAt(SB.length()-1)) {
				return; }
			//no Match, write all all Characters up to now...
			int len = SB.length();
			for (int i = -1; ++i < len;) {
				chr = SB.charAt(i);
				streamOut.write(chr);
			}
		}
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

