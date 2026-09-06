/*
 * File Name: BigEndianReader.java
 * Created on: 14.12.2003
 *
 */
package streamIO.integer.encoding;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a {@link DataInput} to read Intel-native (little-endian) primitive values,
 * assembling each multi-byte value least-significant-byte first.
 * <p>
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:31:44Z
 * digest: fad034d0251ff62da5e9e4226ed870df5f1d161f754cfa42b2f43c6608e37fe8
 * stale: false
 * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
 * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class BigEndianReader 
//extends DataInputStream //final, cannot be overridden
implements DataInput 
{

	/** Number of Bits in a Byte */
	final static public byte NUM_BITS_BYTE = 1 << 3;
	
	/** the maximum Value of an unsigned Short */
	final static public int MAX_UNSIGNED_BYTE = 1 << NUM_BITS_BYTE ; //-((int)Byte.MIN_VALUE)-Byte.MIN_VALUE; //1<<8

	/** the Stream to read from */
	private final DataInput stream;

	/** Initializing Constructor 
	 * 
	 * @param filePath the Path to the File Object to use
	 */
	public BigEndianReader(final String filePath) throws FileNotFoundException {
		this(new File(filePath)); 
	}

	/** Initializing Constructor 
	 * 
	 * @param file the File Object to use
	 */
	public BigEndianReader(final File file) throws FileNotFoundException {
		this(new FileInputStream(file)); 
	}

	/** Initializing Constructor 
	 * 
	 * @param streamIn_ the InputStream Implementation to use
	 */
	public BigEndianReader(final InputStream streamIn_) {
		this((DataInput) new DataInputStream(streamIn_)); 
	}

	/** Initializing Constructor 
	 * 
	 * @param stream_ the Stream to read from 
	 */
	public BigEndianReader(final DataInput stream_) {
		this.stream = stream_; 
	}

	/** Fills the whole array from the underlying stream.
	 * @see java.io.DataInput#readFully(byte[])	 */
	public void readFully(final byte[] b) throws IOException {
		stream.readFully(b);
	}

	/** Fills the given slice of the array from the underlying stream.
	 * @see java.io.DataInput#readFully(byte[], int, int)	 */
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		stream.readFully(b, off, len);
	}

	/** Skips over the given number of bytes of input.
	 * @see java.io.DataInput#skipBytes(int)	 */
	public int skipBytes(final int n) throws IOException {
		return stream.skipBytes(n); }

	/** Reads one byte and interprets it as a boolean.
	 * @see java.io.DataInput#readBoolean()	 */
	public boolean readBoolean() throws IOException {
		return stream.readBoolean(); }

	/** Reads and returns one signed byte.
	 * @see java.io.DataInput#readByte()	*/
	public byte readByte() throws IOException {
		return stream.readByte(); }

	/** Reads one byte and returns it as an unsigned value in {@code [0, 255]}.
	 * @see java.io.DataInput#readUnsignedByte()	 */
	public int readUnsignedByte() throws IOException {
		int ret = readByte(); 
		if (ret < 0) {
			ret += MAX_UNSIGNED_BYTE; }
		return ret; }

	/** Reads two bytes, low byte first, and returns them as a signed 16-bit value.
	 * @see java.io.DataInput#readShort()	 */
	public short readShort() throws IOException {
		return (short)(readUnsignedByte()+(readByte()<<NUM_BITS_BYTE)); }

	/** Number of Bits in a Short / Word */
	final static public byte NUM_BITS_SHORT = NUM_BITS_BYTE << 1;
	
	/** the maximum Value of an unsigned Short */
	final static public int MAX_UNSIGNED_SHORT = 1 << NUM_BITS_SHORT; //-((int)Short.MIN_VALUE)-Short.MIN_VALUE; //

	/** Reads two bytes and returns them as an unsigned 16-bit value.
	 * @see java.io.DataInput#readUnsignedShort()	 */
	public int readUnsignedShort() throws IOException {
		int ret = readShort();
		if (ret < 0) {
			ret += MAX_UNSIGNED_SHORT; }
		return ret;
//		return readUnsignedByte()+(readUnsignedByte()<<8); //works too
	}

	/** Reads two bytes and returns them as a char.
	 * @see java.io.DataInput#readChar()	*/
	public char readChar() throws IOException {
		return (char) readUnsignedShort(); }

	/** Reads four bytes, low word first, and returns them as a signed 32-bit value.
	 * @see java.io.DataInput#readInt()	 */
	public int readInt() throws IOException {
		return readUnsignedShort()+(readShort()<<NUM_BITS_SHORT); }

	/** Number of Bits in a Short / Word */
	final static public byte NUM_BITS_INT = NUM_BITS_SHORT << 1;

	/** the maximum Value of an unsigned Short */
	final static public long MAX_UNSIGNED_INT = 1L<<NUM_BITS_INT;//-((long)Integer.MIN_VALUE)-Integer.MIN_VALUE; //

	/** Reads four bytes and returns them as an unsigned 32-bit value, widened into a long.
	 * @see java.io.DataInput#readInt()	 */
	public long readUnsignedInt() throws IOException {
		long ret = readInt();
		if (ret < 0) {
			ret += MAX_UNSIGNED_INT; }
		return ret;
		//return readUnsignedShort()+(readShort()<<16);
	}

	/** Reads eight bytes, low int first, and returns them as a signed 64-bit value.
	 * @see java.io.DataInput#readLong()	 */
	public long readLong() throws IOException {
		return readUnsignedInt()+(((long) readInt())<<NUM_BITS_INT); }

	/** Reads four bytes and reinterprets them as an IEEE 754 float.
	 * @see java.io.DataInput#readFloat()		*/
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt()); }

	/** Reads eight bytes and reinterprets them as an IEEE 754 double.
	 * @see java.io.DataInput#readDouble()	 */
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong()); }

	/** Reads a line of text from the underlying stream.
	 * @see java.io.DataInput#readLine()	 */
	public String readLine() throws IOException {
		return stream.readLine(); }

	/** Reads a string encoded in modified UTF-8.
	 * @see java.io.DataInput#readUTF()	 */
	public String readUTF() throws IOException {
		return stream.readUTF(); }
	
}
