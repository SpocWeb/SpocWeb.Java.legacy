package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Encodes the Characters coming through this Output streamIO
  * by converting those Values above a certain Threshold (usually 128)
  * into a Hexadecimal Encoding.
  *
  * An encoding scheme that converts binary data into ASCII characters.
  * Any file, whether it be a graphics file, a text file, or a binary executable file,
  * can be converted to BinHex.
  * This format is particularly valuable for transferring files
  * from one platform to another because nearly all computers can handle ASCII files.
  * In fact, many e-mail programs include a BinHex encoder and decoder
  * for sending and receiving attachments.
  * BinHex is an especially common format for Macintosh files.
  * Encoded files usually have a .HQX extension.
  *
  * A hqx file begins with a description which should be ignored
  * by the decoder (and generally left blank by encoding software).
  * The hqx file proper consists of the sequence:
  * <start-of-line>(This<spc>file<spc>must<spc>be<spc>converted<spc>with
  * <spc>BinHex<spc>4.0)<return>:<hqx-file>:<return>
  *
  * When encoding, a <return> should be inserted after every 64 characters.
  * The first character should follow immediately after the first colon
  * (without a <return>), and the first line should be 64 characters long,
  * (unless its also the last line obviously) including the colon.
  * The final colon should go on the same line as the last character
  * and there should be a return after it.
  * Thus, the final line must be between 2 and 65 (inclusive) characters long.
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
  * mtime: 2026-09-05T21:36:15Z
  * digest: 10518c654e0b81448833575ed2a7303122aef8bc195e9e78809cf469e9dba5d9
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterChar2BinHex
extends FilterByte {

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Flag used to indicate whether the High Nibble or the Low Nibble of the Bytes
	 * are transferred first.
	 */
	protected boolean bigEndian;

	/**
	 * Value from which on the Characters coming in are encoded.
	 */
	protected int minCoded = 0x80;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected FilterChar2BinHex(IStreamOutByte streamOut_, boolean bigEndian_) {
		super(streamOut_);
		this.bigEndian = bigEndian_; }

	/** Empty Constructor	 */
	protected FilterChar2BinHex(OutputStream streamOut_, boolean bigEndian_) {
		super(streamOut_);
		this.bigEndian = bigEndian_; }

	/** Empty Constructor	 */
	protected FilterChar2BinHex(IStreamIn_Byte streamIn_, boolean bigEndian_) {
		super(streamIn_);
		this.bigEndian = bigEndian_; }

	/** Empty Constructor	 */
	protected FilterChar2BinHex(InputStream streamIn_, boolean bigEndian_) {
		super(streamIn_);
		this.bigEndian = bigEndian_; }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface StreamOutByte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	//Flag Values for n1
	private static final int FlagEmpty = -2;
	private static final int FlagEOF = -1;

	/** Cache for the Second Character in read()
	  * Not used in write()
	  * Since only '0'-'F' are contained,
	  * negative Values act as Flags 	*/
	private int n1 = FlagEmpty;

	/**
	  * Reads a Character byte from this Input streamIO.
	  * The general contract for read is that one Byte is read from the Input streamIO.
	  * The Characters returned are the two lowest-order Nibbles of the Byte.
	  * The 24 high-order bits of the Byte read are ignored.
	  *
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public int read() throws IOException {
		if (n1 != FlagEmpty) { return n1; }
		int b;
		b =  streamIn.read(); 
		if (b == EOF) {
			return n1 = FlagEOF; }
		int n0;
		n0 = FilterByte2BinHex.HEX_CODES[FilterByte2BinHex.NIBBLE & (b>>4)];
		n1 = FilterByte2BinHex.HEX_CODES[FilterByte2BinHex.NIBBLE &  b    ];
		if (!bigEndian) {
			int tmp = n0; n0 = n1; n1 = tmp; }
		return n0; }

	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that two Bytes are written to the Output streamIO.
	  * The Bytes to be written are the two lowest-order Nibbles of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int b) throws IOException {
		char n0 = FilterByte2BinHex.HEX_CODES[FilterByte2BinHex.NIBBLE & (b>>4)];
		char n1 = FilterByte2BinHex.HEX_CODES[FilterByte2BinHex.NIBBLE &  b    ];
		if (!bigEndian) {
			char tmp = n0; n0 = n1; n1 = tmp; }
		super.write(n0);
		super.write(n1);
		streamOut.write(n0);
		streamOut.write(n1); 
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterChar2BinHex.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}

