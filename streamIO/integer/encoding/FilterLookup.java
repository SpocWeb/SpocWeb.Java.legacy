package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import math.vector.VectorChar;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Title: FilterLookup<p>
  * Description:
  * Recodes the Bytes coming through this Input streamIO
  * by looking up their Values in a byte[] Array.
  * This is a Size-preserving Transformation 
  * that is being reused in Escaping Characters. 
  *
  * Example Byte Arrays are given for EBCDIC Encoding and ASCII Backslash Escaping  
  * and their respective Reverses.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterLookup
extends FilterByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Converts ASCII Character Values to EBCDIC character values
	 * Using the ASCII Value as an Index, this Array returns the EBCDIC Value
	 * There is no clean formula for iteratively calculating the byte Values.
	 * Because of this, we build a Conversion Lookup Table in an Array.
	 */
	protected static final char[] ASCII2EBCDIC = {
		0,1,2,3,55,45,46,47,22,5,37,11,12,13,14,15,
		16,17,18,19,60,61,50,38,24,25,63,39,28,29,30,31,
		64,79,127,123,91,108,80,125,77,93,92,78,107,96,75,97,
		240,241,242,243,244,245,246,247,248,249,122,94,76,126,110,111,
		124,193,194,195,196,197,198,199,200,201,209,210,211,212,213,214,
		215,216,217,226,227,228,229,230,231,232,233,74,224,90,95,109,
		121,129,130,131,132,133,134,135,136,137,145,146,147,148,149,150,
		151,152,153,162,163,164,165,166,167,168,169,192,106,208,161,7,
		32,33,34,35,36,21,6,23,40,41,42,43,44,9,10,27,
		48,49,26,51,52,53,54,8,56,57,58,59,4,20,62,225,
		65,66,67,68,69,70,71,72,73,81,82,83,84,85,86,87,
		88,89,98,99,100,101,102,103,104,105,112,113,114,115,116,117,
		118,119,120,128,138,139,140,141,142,143,144,154,155,156,157,158,
		159,160,170,171,172,173,174,175,176,177,178,179,180,181,182,183,
		184,185,186,187,188,189,190,191,202,203,204,205,206,207,218,219,
		220,221,222,223,234,235,236,237,238,239,250,251,252,253,254,255};

	/**
	 * Converts EBCDIC Character Values to ASCII character values
	 * Using the EBCDIC Value as an Index, this Array returns the ASCII Value
	 * There is no clean Formula for iteratively calculating the byte Values.
	 * Because of this, we build a Conversion Lookup Table in an Array.
	 */
	private static final char[] EBCDIC2ASCII = VectorChar.Inverse(ASCII2EBCDIC, (char) 256);

	/** Escape Replacements.
	  * The following Characters are replaced when occuring behind an Escape Character
	  * The Escape Character itself is filtered out, except if escaped!
	  * Escaping has to take place at the lowest Level of Parsing,
	  * because they prevent using Parsing Characters in the Text.
	  */
	private static final char[][] ASCII_ESCAPES = 
		{ 	 {'0', 0x0} //null (NUL) x0
			,{'a', '\b'} //Alarm (BEL) x7
			,{'b', 0x8} //Backspace x8
			,{'t', '\t'} //Tab x9
			,{'n', '\n'} //New Line (LF Line Feed) xA
			,{'v', 0xB} //vertical Tab xB
			,{'f', '\f'} //Form Feed (FF) xC
			,{'r', '\r'} //Carriage Return xD
			,{'e', 0x1B} //Escape (ESC) x1B
		//	,{' ', ' '} //escaped Space
		//	,{'\'','\''} //escaped Quote
		//	,{'"', '\"'} //escaped Double-Quote
		//	,{'x'','x'} //1 Byte Hex Escape-Sequence
		//	,{'u'','u'} //2 Byte Hex Escape-Sequence
		//	,{'U'','U'} //4 Byte Hex Escape-Sequence
		//	,{'L', 0x2028} //escaped Line Separator x2028
		//	,{'P', 0x2029} //escaped Paragraph Separator x2029
		}; //any other Escape Sequences are formally forbidden, but can be tolerated
	
	/** Converts Escape Character Values to ASCII character values	 */
	final static public char[] ESCAPE2ASCII = VectorChar.MAP(ASCII_ESCAPES);

	/** 
	 * Converts ASCII Character Values to Escape character values
	 * The Array is filled up to the max. escaped Character 
	 * and thus allows for most efficient Lookup! 
	 */
	final static public char[] ASCII2ESCAPE = VectorChar.Inverse(ESCAPE2ASCII);

	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** escapes the given String */
	final static public void ESCAPE2ASCII(final String str, final StringBuffer ret) {
		int len = str.length(); 
		for (int i = -1; ++i < len;){
			char chr = str.charAt(i); 
			if  (chr == IStreamIn_Byte.CHR_ESCAPE) {
				 chr = ESCAPE2ASCII(str.charAt(++i)); } 
			ret.append(chr); 
		}
	}

	/** escapes the given String */
	final static public StringBuffer ESCAPE2ASCII(final String str) {
//		if (str == null) { return null; }
		StringBuffer ret = new StringBuffer(str.length());
		ESCAPE2ASCII(str, ret); 
		return ret; }

	/** de-escapes the given Character */
	final static public char ESCAPE2ASCII(final char chr) {
		if (chr  < ESCAPE2ASCII.length) 
			return ESCAPE2ASCII[chr]; 
/*		for (int i = ESCAPES.length; --i >= 0; ) { //searching takes too long
			if (chr == ESCAPES[i][0]) {
				return ESCAPES[i][1]; }
		}
*/		return chr; } //return the Item literally

	/** escapes the given Character */
	final static public char ASCII2ESCAPE(final char chr) {
		if (chr  < ASCII2ESCAPE.length) { 
			return ASCII2ESCAPE[chr]; }
		return chr; } //return the Item literally

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetInteger.Permutation 	 */
	final static public char ASCII2EBCDIC(int Value) {
		return ASCII2EBCDIC[Value]; }

	/** Reverts a complete Encoding Table for Bytes
	  * This is equivalent to inverting the Permutation
	  * in Class streamIO.Copy.Monoid.SetInteger.Permutation 	 */
	final static public char EBCDIC2ASCII(int Value) {
		return EBCDIC2ASCII[Value]; }

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Encoding Array used for encoding the incoming Bytes	 */
	protected int[] encoding;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	protected FilterLookup(IStreamIn_Byte streamIn_, int[] encoding_) {
		super(streamIn_);
		this.encoding = encoding_; }

	/** Constructor	 */
	protected FilterLookup(InputStream streamIn_, int[] encoding_) {
		super(streamIn_);
		this.encoding = encoding_; }

	/** Constructor	 */
	protected FilterLookup(IStreamOutByte streamOut_, int[] encoding_) {
		super(streamOut_);
		this.encoding = encoding_; }

	/** Constructor	 */
	protected FilterLookup(OutputStream streamOut_, int[] encoding_) {
		super(streamOut_);
		this.encoding = encoding_; }

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
		return encoding[streamIn.read()]; }

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int b) throws IOException { streamOut.write(encoding[b]); }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterLookup.class.getName());
		System.out.println("\'\n\',\"t\tab\",'\f','\\");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

