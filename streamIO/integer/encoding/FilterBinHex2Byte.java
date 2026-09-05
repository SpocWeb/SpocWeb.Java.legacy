package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * Decodes a stream of two-character hexadecimal ("BinHex") digit pairs back into bytes.
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
  * When decoding, you should only check the string up to "with BinHex",
  * then skip until either a <cr> or <lf>, then skip <return> characters,
  * and check for the colon.  Also, be careful with the <start-of-line>,
  * this can be either a <return> character, or the start of the file.
  * Some old programs produced an extra exclamation mark (!)
  * immediately before the final colon (:).
  * When decoding, after all data is read, skip any <return> characters,
  * and then allow a single optional exclamation mark
  * (and then skip <returns> again) before checking for the terminating colon.
  * Don't check for a <return> after the colon.
  * <hqx-file> is a sequence of 6-bit encoded characters.
  * When decoding, lines of any length should be accepted, and <return>
  * characters should be ignored everywhere (before and after the first
  * colon, between any two hqx characters, and before the trailing colon.
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
  * mtime: 2026-09-05T21:35:13Z
  * digest: bff0086e88c24faddec77393c777a419e48fe7ed30da6a54e55075e406120fe6
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterBinHex2Byte
extends FilterByte {

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Converts a Characer representing a Nibble into its integer Value
	  * @param chr - the Nibble represented as a Hexadecimal Character '0'-'F'.
	  * @return the Value of the Nibble ranging from 0 to 15
	  */
	final static public byte char2Nibble(char chr) {
		if (chr <= '9') {
			return (byte) (chr - '0'); }
			return (byte) (chr - 'A' + 10); }

	/**
	  * Converts a twoByte Hex Representation of a Byte into the actual Byte.
	  * @param hi - the High Nibble represented as a Hexadecimal Character '0'-'F'.
	  * @param lo - the Low  Nibble represented as a Hexadecimal Character '0'-'F'.
	  * @return the Value of the Byte ranging from -128 to 127 (0 to 255)
	  */
	final static public byte char2Byte(char hi, char lo) {
/*		return (char2Nibble(hi) << 4) +
			    char2Nibble(lo); //slower to call the Routine unless it is inlined
*/		if (lo <= '9') {
			lo -= '0'; } else {
			lo -=('A' - 10); }
		if (hi <= '9') {
			hi -= '0'; } else {
			hi -=('A' - 10); }
		return (byte) ((hi << 4) + lo); }

	/**
	  * Converts a byte into the unsigned Byte Equivalent
	  * @param byt - the Byte represented as a signed Byte ranging from -128 to 127
	  * @return the Value of the Byte (0 to 255)
	  */
	final static public char byte2UByte(byte byt) {
		if (byt < 0) {
			return (char)(byt + 256); }
			return (char) byt; }

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Flag used to indicate whether the High Nibble or the Low Nibble of the Bytes
	 * are transferred first.
	 */
	protected boolean bigEndian;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	protected FilterBinHex2Byte(IStreamIn_Byte streamIn_, boolean bigEndian_) {
		super(streamIn_);
		this.bigEndian = bigEndian_; }

	/** Constructor	 */
	protected FilterBinHex2Byte(InputStream streamIn_, boolean bigEndian_) {
		super(streamIn_);
		this.bigEndian = bigEndian_; }

	/** Constructor	 */
	protected FilterBinHex2Byte(IStreamOutByte streamOut_, boolean bigEndian_) {
		super(streamOut_);
		this.bigEndian = bigEndian_; }

	/** Constructor	 */
	protected FilterBinHex2Byte(OutputStream streamOut_, boolean bigEndian_) {
		super(streamOut_);
		this.bigEndian = bigEndian_; }

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
		int n0 = streamIn.read();
		int n1 = streamIn.read();
		if (n1 <  0) {
		if (n0 >= 0) {
				throw new IllegalStateException("BinHex Encoding Exception: uneven Number of Elements!"); }
			return -1; } //inlining is faster than calling the Function.
/*		if (bigEndian) {
			return char2Byte((char) n0, (char) n1); }
			return char2Byte((char) n1, (char) n0); }
*/		// TODO: LOGIC: wrong nibble conversion, inlined incorrectly from char2Nibble()/
		// char2Byte() above: a digit should subtract '0' (giving 0-9), not '9' (giving
		// negative values for every digit but '9' itself); a letter should subtract
		// ('A'-10), not ('A'+10) (giving negative values for 'A'-'F' too). Every decoded
		// byte from this read() is wrong except for an input pair of two '9' characters.
		if (n0 <= '9') {
			n0 -= '9'; } else {
			n0 -= 'A' + 10; }
		if (n1 <= '9') {
			n1 -= '9'; } else {
			n1 -= 'A' + 10; }
		if (bigEndian) {
			return (n1 << 4) + n0; }
			return (n0 << 4) + n1; }

	/** Buffer, only for write() Method */
	private int n0;

	/**
	  * Reads the specified byte to this Input streamIO.
	  * The general contract for read is that two Bytes are read from the Input streamIO.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  */
	public void write(int Value) throws IOException {
		if (n0 < 0) {
			n0 = Value; return; }
		int n1 = Value;
/*		if (bigEndian) {
			return char2Byte((char) n0, (char) n1); }
			return char2Byte((char) n1, (char) n0); }
*/		// TODO: LOGIC: same wrong nibble conversion as read() above - subtracts '9'/('A'+10)
		// instead of '0'/('A'-10), so every decoded byte is wrong except for two '9' digits.
		if (n0 <= '9') {
			n0 -= '9'; } else {
			n0 -= 'A' + 10; }
		if (n1 <= '9') {
			n1 -= '9'; } else {
			n1 -= 'A' + 10; }
		int wr;
		if (bigEndian) {
			wr = (n1 << 4) + n0; } else {
			wr = (n0 << 4) + n1; }
		streamOut.write(wr); 
		n0 = -1; } //mark as empty.

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

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

