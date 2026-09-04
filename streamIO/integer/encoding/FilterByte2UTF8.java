package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Title: FilterByte2UTF8<p>
  * Description:
  * Encodes the Characters (int) coming through this Output or Input streamIO
  * by converting their Values from/into UTF-8 Encoding.
  *
  * Unicode takes 16 Bits instead of 8 Bits (ASCII).
  * UTF-8 can compensate this Overhead completely, i.e. be 50% shorter,
  * and be editable by simple Byte Editors,
  * when the Character range is 0-127, because these are identical to the Unicode Encoding.
  * For the other Character 2 or 3 Bytes are used, which increases Size by at most 50%.
  *
  * Encoding is organized so that Reading can start at any Position
  * and Errors or wrong Encoding are very reliably detected
  * by recognizing the first Character of a new 1, 2 or 3 Byte Sequence.
  *
  * From   | To     |Bits| Hex Sequence      | Bit Sequence
  * -------+--------+----+-------------------+---------------------------
  * \u0000 | \u007F |  7 | 0x-7x             | 0xxxxxxx
  * \u0080 | \u07FF | 11 | Cx-Dx 8x-Bx       | 110xxxxx 10xxxxxx
  * \u0800 | \uFFFF | 16 | Ex    8x-Bx 8x-Bx | 1110xxxx 10xxxxxx 10xxxxxx
  *
  * The first Character of a Sequence of... 
  * 1 Byte starts with 0x to 7x, so it can be readily recognized as single Character
  * 2 Byte starts with Cx or Dx, so it can be recognized as double Character
  * 3 Byte starts with Ex, so it can be recognized as triple Character
  * No Sequence starts with an Fx Byte.
  * Inner Bytes always start with 10xx, i.e. 8x, 9x, Ax oder Bx,
  * no matter if they are Part of a 2 Byte or 3 Byte Sequence
  * and thus can be readily recognized and skipped as inner Characters.
  * Additionally invalid Characters can be easily detected!
  *
  * Of course you cannot use UTF-8 for a Database,
  * because the variable Length disallows fast skipping of Records.
  *
  * Known SubClasses:
  *
  * similar Classes:
  * @see java.io.OutputStreamWriter that converts a Unicode Character streamIO into different Encodings
  * @see  java.io.InputStreamReader that converts a Unicode Character streamIO from different Encodings
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FilterByte2UTF8
extends FilterByte {

	/**
	  * Constant indicating for fillByte() that the first Byte is an inner Unicode Byte
	  * and has to be ignored and replaced by the next Byte
	  */
	final static public int READ_SAME = -2;

	/**
	  * Constant indicating for fillByte() that the Unicode Character is not complete yet
	  * and that the next Byte has to be read.
	  */
	final static public int READ_NEXT = -3;

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Stateless Multistep Operation. All State is kept in the given Parameters.
	  * Tries to convert the given Bytes up to the given Length to a Character.
	  * Returns the Character if that was possible.
	  *
	  * The general contract for read is that two Bytes are read from the Input streamIO.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the Input streamIO has been closed.
	  * @return the Character assembled from the Bytes read from the Input streamIO
	  * 	-1 if EOF was encountered
	  * 	-2 if an inner Character was encountered and the first Character has to be re-read
	  * 	-3 if another Character is necessary.
	  * Von    | Bis    |Bits| Hex Sequence      | Bit Sequence
	  * -------+--------+----+-------------------+---------------------------
	  * \u0000 | \u007F |  7 | 0x-7x             | 0xxxxxxx
	  * \u0080 | \u07FF | 11 | Cx-Dx 8x-Bx       | 110xxxxx 10xxxxxx
	  * \u0800 | \uFFFF | 16 | Ex    8x-Bx 8x-Bx | 1110xxxx 10xxxxxx 10xxxxxx
	  */
	public static int fillByte(int[] n, int len) {
		if (len == 0) {
			if        (n[0] == EOF) { return EOF; } //read the first Character, skip all inner Characters
			if       ((n[0] >= FilterUTF8ToByte.inner_Set_) &&
				      (n[0] <  FilterUTF8ToByte.doubleSet_)) {
//				throw new IllegalStateException("starting in the middle of a Stream! "); }
				return READ_SAME; }
			if        (n[0] <  FilterUTF8ToByte.inner_Set_) {
				return n[0]; } //single Character
		} else if(len == 1) {
			if        (n[0] <  FilterUTF8ToByte.tripleSet_) { //double Character
			return  (((n[0] &  FilterUTF8ToByte.tripleMask) << 6) |
					  (n[1] &  FilterUTF8ToByte.inner_Mask)); }
		} else {
			return ((((n[0] &  FilterUTF8ToByte.tripleMask) << 6) |
					  (n[1] &  FilterUTF8ToByte.inner_Mask))<< 6) |
					  (n[2] &  FilterUTF8ToByte.inner_Mask);
		} return READ_NEXT; }

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	protected FilterByte2UTF8(IStreamIn_Byte streamIn_) {
		super(streamIn_); }

	/** Constructor	 */
	protected FilterByte2UTF8(InputStream streamIn_) {
		super(streamIn_); }

	/** Constructor	 */
	protected FilterByte2UTF8(IStreamOutByte streamOut_) {
		super(streamOut_); }

	/** Constructor	 */
	protected FilterByte2UTF8(OutputStream streamOut_) {
		super(streamOut_); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface IStreamIn_Byte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Cache Array for collecting Bytes to form a UniCode Character */
	private int index = -1;

	/** Cache Array for collecting Bytes to form a UniCode Character */
	private int[] n = new int[3];

	/**
	  * Writes the specified UTF Byte to this Output streamIO.
	  *
	  * @param b - the UTF Byte to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the Output streamIO has been closed.
	  */
	public void write(int b) throws IOException {
		n[++index] = b;
		int ret;
		switch (ret = fillByte(n, index)) {
		case EOF      : index = -1; break;
		case READ_SAME: index--   ; break;
		case READ_NEXT:             break;
		default:        index = -1;
//			super.write(ret);
			streamOut.write(ret); 
		}
	}

	/**
	  * Reads the next Character from this Input streamIO of Bytes.
	  * The general contract for read is that two Bytes are read from the Input streamIO.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * @return the Character assembled from the Bytes read from the Input streamIO
	  * 	In particular, an IOException may be thrown if the Input streamIO has been closed.
	  * Von    | Bis    |Bits| Hex Sequence      | Bit Sequence
	  * -------+--------+----+-------------------+---------------------------
	  * \u0000 | \u007F |  7 | 0x-7x             | 0xxxxxxx
	  * \u0080 | \u07FF | 11 | Cx-Dx 8x-Bx       | 110xxxxx 10xxxxxx
	  * \u0800 | \uFFFF | 16 | Ex    8x-Bx 8x-Bx | 1110xxxx 10xxxxxx 10xxxxxx
	  */
	public int read() throws IOException {
		int ret, chr;
		do {
			n[++index] = chr = streamIn.read(); 
			switch (ret = fillByte(n, index)) {
			case EOF      : index = -1; break;
			case READ_SAME: index--   ; break;
			case READ_NEXT:             break;
			default:        index = -1; return ret;
			}
		} while (chr != EOF);
		throw new IOException("Sream ended before Character!"); }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterByte2UTF8.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

