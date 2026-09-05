package streamIO.integer.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Decodes the Bytes coming through this Input or Output streamIO
  * by converting their Values from a URL Encoding.
  *
  * URL Encoding is used mostly to append Parameters to an HTTP POST Request, 
  * but also to encode the HTTP Entity Body, so CRs are prevented 
  * which would end the HTTP Conversation prematurely. 
  * The Parameters start with a Question Mark '?'
  * The Parameters are separated by an Ampersand '&'
  * key and Value are separated by an Equal Sign '='
  *
  * ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through '9' remain the same.
  * The plus sign '+' is converted into a space character ' '.
  * remaining characters (inclusive Space) are represented by 3-character strings
  * 	which begin with the percent sign "%xy",
  * 	where xy is the two-digit hexadecimal representation
  * 	of the lower 8-bits of the character in the Order High Byte / Low Byte.
  * When the Content is guaranteed not to containg higher Values than 0x80, 
  * (e.g. on base64 encoded Contents) some Servers abbreviate CR and LF by %d and %a. 
  *
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2002-02-17, 12;08;22<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:37:23Z
  * digest: 5f90850b1a3bf39c700e5c4aac3432e4c5100be2c1743596f5d00d0c2879a9d0
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterUrlDecode
extends FilterByte {
	
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	protected FilterUrlDecode(final IStreamIn_Byte streamIn_) {
		super(streamIn_); }

	/** Constructor	 */
	protected FilterUrlDecode(final InputStream streamIn_) {
		super(streamIn_); }

	/** Constructor	 */
	protected FilterUrlDecode(final IStreamOutByte streamOut_) {
		super(streamOut_); }

	/** Constructor	 */
	protected FilterUrlDecode(final OutputStream streamOut_) {
		super(streamOut_); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Reads the specified byte to this Input streamIO.
	  * The general contract for read is that two Bytes are read from the Input streamIO.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public int read() throws IOException {
		int c = streamIn.read();
		switch (c) {
			case  FilterUrlEncode.UrlBackSlashReplace:
				return FilterUrlEncode.UrlBackSlashOriginal;
			case  FilterUrlEncode.UrlSpaceReplace:
				return FilterUrlEncode.UrlSpaceOriginal;
			case  FilterUrlEncode.UrlEscapeChar: //read the HexCode for the next two Bytes
				char hi = (char) streamIn.read();
				char lo = (char) streamIn.read();
				return FilterBinHex2Byte.char2Byte(hi, lo);
//			default : //leave the Character
		}
		return c; }

	/** Cached first Character of a twoByte Sequence	*/
	private byte c1 = -2;

	/**
	  * Writes the specified byte to this Output streamIO.
	  * The general contract for write is that one to three Bytes
	  * are written to the Output streamIO.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(final int Value) throws IOException {
		// TODO: LOGIC: this checks Value against UrlSpaceReplace ('+') to decide whether to
		// enter the two-byte hex-escape state, but the escape marker is '%'
		// (FilterUrlEncode.UrlEscapeChar), not '+' - unlike read() above, which correctly
		// switches on UrlEscapeChar. As written, write() never decodes a real "%XY" escape
		// sequence (its '%' passes straight through unchanged) and instead misinterprets a
		// literal '+' as the start of a hex pair, consuming and misdecoding the next two
		// characters. It also never translates '+' back into a space. Should check
		// `Value == FilterUrlEncode.UrlEscapeChar` here, with '+' handled as its own case
		// that writes UrlSpaceOriginal directly.
		switch (c1) {
			case -2: //not in Escape Sequence
				if (Value == FilterUrlEncode.UrlSpaceReplace) {
					c1 = -1; return; } //Escape Character
				streamOut.write(Value); return; //normal Character
			case -1: //High Byte of a Hex Pair
				c1 = (byte) Value;
			default: //Low  Byte of a Hex Pair
				streamOut.write(FilterBinHex2Byte.char2Byte((char) c1, (char) Value));
				c1 = -2;
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * filters the given Bytes by sending them through an Encoder. 
	 * @param bytes
	 * @return the encoded String
	 * @throws IOException
	 */
	public static  String FILTER(final String arg) throws IOException {
		return new String(FILTER(arg.getBytes()));
	}

	/**
	 * filters the given Bytes by sending them through an Encoder. 
	 * @param bytes
	 * @return a new Array containing the encoded Characters. 
	 * @throws IOException
	 */
	public static byte[] FILTER(final byte[] bytes) throws IOException {
		final FilterUrlDecode decoder = new FilterUrlDecode(new ByteArrayInputStream(bytes)); 
		final ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length*2);
		decoder.stream(out); 
		decoder.close(); 
		return out.toByteArray();
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterBinHex2Byte.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}

