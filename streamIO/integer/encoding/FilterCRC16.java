package streamIO.integer.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.Assert;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;

/**
  * This class calculates the 16-bit CRC of a file or string.
  *
  * Checksums are often used to verify the integrity of data.
  * For example, tape backup software can verify that a backed up file is identical
  * to the original file by comparing their checksums.
  *
  * The 16-bit CRC (Cyclic Redundancy Check) is a specific kind of checksum.
  * This checksum computes a number based on the value and position
  * of individual bits in a block of data.
  * This is accomplished using a polynomial of the form
  *    bitn-1 * xn-1 + bitn-2 * xn-2 + ... + bit0 * x0
  * where n is the number if bits processed.
  * This polynomial is then divided by the polynomial
  *    x32 + x26 + x23 + x22 + x16 + x12 + x11 + x10 + x8 + x7 + x5 + x4 + x2 + x1+ 1
  * For example, suppose we wanted to calculate the CRC value of the letter a.
  * The binary representation of a is 1100001.
  * Therefore, the numerator polynomial would be
  *     1x7 + 1x6 + 0x5 + 0x4 + 0x3 + 0x2 + 0x1 + 1x0
  * This would then be divided by the divisor polynomial, resulting in the equation:
  *
  *                                      1x7 + 1x6 + 1x0
  * CRC = _____________________________________________________________________________
  *       x32 x26 + x23 + x22 + x16 + x12 + x11 + x10 + x8 + x7 + x5 + x4 + X2 + X1 + 1
  *
  * This operation would be repeated for each byte we wanted to process.
  * The initial CRC value is zero, and is accumulated for each byte processed.
  * After all bytes have been processed, the resultant CRC is the CRC for the whole block of data.
  *
  * The 16-bit CRC is generally accurate for files up to 4k in size.
  * For larger files the 32-bit CRC should be used.
  *
  * Note: In the above polynomials, x is a magic number.
  * The mathematics for deriving x are beyond the scope of this documentation.
  * Please see below for more information.
  * "File Verification Using CRC: 32-bit Cyclic Redundency Check."
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-02-24, 12;46;10<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:36:06Z
  * digest: 541d300ecc45909b00f71d44a31989d265fdba8dd7d41eac139dfe0b987dee69
  * stale: false
  * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
  * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterCRC16
extends FilterByte {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** Private class-specific variables
	  * The table of the precalculated CRC values
	  */
	private static final int[] CRC16 = {
		0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7,
		0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF,
		0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7, 0x62D6,
		0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE,
		0x2462, 0x3443, 0x0420, 0x1401, 0x64E6, 0x74C7, 0x44A4, 0x5485,
		0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D,
		0x3653, 0x2672, 0x1611, 0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4,
		0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC,
		0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823,
		0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A, 0xB92B,
		0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12,
		0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79, 0x8B58, 0xBB3B, 0xAB1A,
		0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41,
		0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49,
		0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70,
		0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78,
		0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E, 0xE16F,
		0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067,
		0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D, 0xD31C, 0xE37F, 0xF35E,
		0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256,
		0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D,
		0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
		0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C,
		0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615, 0x5634,
		0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB,
		0x5844, 0x4865, 0x7806, 0x6827, 0x18C0, 0x08E1, 0x3882, 0x28A3,
		0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A,
		0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92,
		0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9,
		0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1,
		0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9, 0x9FF8,
		0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0};

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Comments  : This procedure calculates the new CRC based on the current CRC,
	  *             and the byte value
	  * Parameters: lngCRC - The current CRC value
	  *             bytByte - The byte value to lookup
	  * Returns   : The calculated cumulative CRC value
	  */
	public static int updateCRC16(int CRC, byte bytByte) {
		int tempCRC;
		//Calculate the new CRC value, Masking off all bits over 65536
		tempCRC = CRC << 8;
		return 0xFFFF & (tempCRC ^ CRC16[(CRC >> 8) ^ bytByte]); } //Return the value

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** the current CRC Value	*/
	protected int CRC;

	/** Returns the current CRC Value	*/
	public int getCRC() { return CRC; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor taking a IStreamOutByte	 */
	public FilterCRC16(IStreamOutByte streamOut) {
		super(streamOut); }

	/**
	 * Initializing Constructor delegating to an OutputStream
	 * This is usually not necessary,
	 * because the OutputStream can be directly subclassed
	 * and declared to implement IStreamIn_Byte!
	 */
	public FilterCRC16(OutputStream streamOut) {
		super(streamOut); }

	/** Initializing Constructor taking a IStreamIn_Byte 	 */
	public FilterCRC16(IStreamIn_Byte streamIn_) {
		super(streamIn_); }

	/**
	 * Initializing Constructor delegating to an InputStream
	 * This is usually not necessary,
	 * because the InputStream can be directly subclassed
	 * and declared to implement IStreamIn_Byte!
	 */
	public FilterCRC16(InputStream streamIn_) {
		super(streamIn_); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface StreamOutByte: abstract Methods
////////////////////////////////////////////////////////////////////////////////

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
	public void write(int b) throws IOException {
		CRC = updateCRC16(CRC, (byte) b); //super.write(b);
		streamOut.write(b); }

////////////////////////////////////////////////////////////////////////////////
//  Interface IStreamIn_Byte: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Reads the next byte of data from the input stream.
	  * The value byte is returned as an int in the range 0 to 255.
	  * If no byte is available because the end of the stream has been reached,
	  * the value EOF is returned.
	  * This method blocks until input data is available,
	  * the end of the stream is detected, or an exception is thrown.
	  *
	  * A subclass must provide an implementation of this method.
	  *
	  * @return the next byte of data, or EOF if the end of the stream is reached.
	  * @throws IOException - if an I/O error occurs.
	  */
	public int read() throws IOException {
		int ret; // = super.read();
		ret = streamIn.read();
		if (ret >= 0) {
			CRC = updateCRC16(CRC, (byte) ret); }
		return ret; } //

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + FilterCRC16.class.getName());
		String strTest = "One small step for [a] man; one giant leap for mankind";
		FilterCRC16 enc = new FilterCRC16(System.out);
		enc.write(strTest); //
//		enc.close();    //
		Assert.EQUALS(9029, enc.getCRC()); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
