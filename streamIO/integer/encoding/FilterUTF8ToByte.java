package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Title: FilterUTF8ToByte<p>
  * Description:
  * 
  * Encodes the Characters coming through this Output streamIO
  * by converting their Values into UTF-8 Encoding.
  * 
  * Unicode takes 16 Bits instead of 8 Bits like ASCII.
  * UTF-8 can compensate this Overhead completely, i.e. be 50% shorter,
  * when the Character range is 0-127, because these are identical to the Unicode Encoding.
  * For the other Character 2 or 3 Bytes are used, which increases Size by at most 50%.
  * 
  * To determine the actual Encoding used, a BOM (Byte Order Mark) is used
  * Bytes 			Encoding Form
  * 00 00 FE FF 	UTF-32, big-endian
  * FF FE 00 00 	UTF-32, little-endian
  * FE FF 			UTF-16, big-endian
  * FF FE 			UTF-16, little-endian
  * EF BB BF 		UTF-8 
  * If, additionally, an Encoding is given in an XML Document, 
  * it must indicate the same encoding, otherwise the Parser may throw an Exception
  * that it cannot change the Encoding in-between. 
  * 
  * Encoding is organized so that reading can start at any Position
  * by recognizing the first Character of a new 1, 2 or 3 Byte Sequence.
  *
  * Von    | Bis    |Bits| Hex Sequence      | Bit Sequence
  * -------+--------+----+-------------------+---------------------------
  * \u0000 | \u007F |  7 | 0x-7x             | 0xxxxxxx
  * \u0080 | \u07FF | 11 | Cx-Dx 8x-Bx       | 110xxxxx 10xxxxxx
  * \u0800 | \uFFFF | 16 | Ex    8x-Bx 8x-Bx | 1110xxxx 10xxxxxx 10xxxxxx
  *
  * The first Character of a Sequence of ...
  * 1 Byte starts with 0x to 7x, so it can be readily recognized as single Character
  * 2 Byte starts with Cx or Dx, so it can be recognized as doule Character
  * 3 Byte starts with Ex, so it can be recognized as triple Character
  * inner Bytes start with 10xx, so 8x, 9x, Ax oder Bx,
  * no matter if they are Part of a 2 Byte or 3 Byte Sequence
  * can be readily recognized and skipped as inner Characters.
  *
  * Of course you cannot use UTF-8 for a Database,
  * because the variable Length disallows fast skipping of Records.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterUTF8ToByte
extends FilterByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** ASCII Range of the UTF-8 Code   */
	final static public int UTF8_1 = 0x7F;

	/** Middle Range of the UTF-8 Code   */
	final static public int UTF8_2 = 0x7FF;

	/** High Range of the UTF-8 Code   */
	final static public int UTF8_3 = 0xFFFF;

	/** Mask for the inner Characters 1 << 5 -1  */
	final static public int tripleMask = 0x1F;

	/** Mask for the inner Characters = 1 << 6 -1   */
	final static public int inner_Mask = 0x3F;

	// before this Value are the single Bytes

	/** Set Bits for the inner Characters   */
	final static public int inner_Set_ = 0x80;

	// between these Values are the inner Bytes

	/** Set Bits for the first Character of a double Set   */
	final static public int doubleSet_ = 0xC0;

	// between these Values are the double Bytes

	/** Set Bits for the first Character of a triple Set   */
	final static public int tripleSet_ = 0xE0;

	// after this Values are the triple Bytes

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Converts the specified Unicode Character into the given Byte Array.
	  * one, two or three Bytes are filled in the Byte Array, depending on the Value.
	  * The Number of Bytes filled is returned by this Function.
	  *
	  * @param c - the Character.
	  * @param b - the Array of Bytes with minimum Length 3.
	  */
	public static byte Unicode2Byte(char c, int[] b) throws IOException {
		if (c < UTF8_1) { //write a single Character
			b[0] =  c; return 1; }
		if (c < UTF8_2) { //write two Characters
			b[0] = (c & inner_Mask) | inner_Set_;
			b[1] = (c >> 6) | doubleSet_;
			return 2; }
//		} else { //write three Characters
			b[0] = (c        & inner_Mask) | inner_Set_;
			b[1] =((c >>= 6) & inner_Mask) | inner_Set_;
			b[2] = (c >>  6) | tripleSet_;
			return 3; }
//		}

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Index to the Array used for Conversion of Character to Byte	*/
	int index = 3;

	/** Array used for Conversion of Character to Byte	*/
	int[] byt = new int[index];

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	protected FilterUTF8ToByte(IStreamOutByte streamOut_) {
		super(streamOut_); }

	/** Constructor	 */
	protected FilterUTF8ToByte(OutputStream streamOut_) {
		super(streamOut_); }

	/** Constructor	 */
	protected FilterUTF8ToByte(IStreamIn_Byte streamIn_) {
		super(streamIn_); }

	/** Constructor	 */
	protected FilterUTF8ToByte(InputStream streamIn_) {
		super(streamIn_); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface StreamOutByte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Reads the next Unicode Character from this Input streamIO.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * @return the next Byte determined from the Input Character.
	  * In particular, an IOException may be thrown if the Input streamIO has been closed.
	  */
	public int read() throws IOException {
		if (--index >= 0) {
			return byt[index]; }
		int c = streamIn.read();  //read the next Character and convert it into Bytes:
		if (c == EOF) {
			return EOF; }
			return byt[index = Unicode2Byte((char) c, byt)-1]; }

	/**
	  * Writes the specified Unicode Character to this Output streamIO.
	  * The general contract for write is that one, two or three Bytes
	  * are written to the Output streamIO, depending on the Value.
	  * The Bytes to be written are determined by the two lowest-order Bytes
	  * of the argument b. The 16 high-order bits of b are ignored.
	  *
	  * @param b - the Character.
	  * @throws IOException - if an I/O error occurs.
	  * In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int c) throws IOException {
		int len = Unicode2Byte((char) c, byt);
		while (--len >= 0) {
			streamOut.write(byt[len]); }
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterUTF8ToByte.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

