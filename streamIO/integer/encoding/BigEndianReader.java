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
 * Title: BigEndianReader<p>
 * Description:
 * A DataReader Filter for Intel-specific Big-Endian File- and Memory- Formats
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

	/** @see java.io.DataInput#readFully(byte[])	 */
	public void readFully(final byte[] b) throws IOException {
		stream.readFully(b);
	}

	/** @see java.io.DataInput#readFully(byte[], int, int)	 */
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		stream.readFully(b, off, len);
	}

	/** @see java.io.DataInput#skipBytes(int)	 */
	public int skipBytes(final int n) throws IOException {
		return stream.skipBytes(n); }

	/** @see java.io.DataInput#readBoolean()	 */
	public boolean readBoolean() throws IOException {
		return stream.readBoolean(); }

	/** @see java.io.DataInput#readByte()	*/
	public byte readByte() throws IOException {
		return stream.readByte(); }

	/** @see java.io.DataInput#readUnsignedByte()	 */
	public int readUnsignedByte() throws IOException {
		int ret = readByte(); 
		if (ret < 0) {
			ret += MAX_UNSIGNED_BYTE; }
		return ret; }

	/** @see java.io.DataInput#readShort()	 */
	public short readShort() throws IOException {
		return (short)(readUnsignedByte()+(readByte()<<NUM_BITS_BYTE)); }

	/** Number of Bits in a Short / Word */
	final static public byte NUM_BITS_SHORT = NUM_BITS_BYTE << 1;
	
	/** the maximum Value of an unsigned Short */
	final static public int MAX_UNSIGNED_SHORT = 1 << NUM_BITS_SHORT; //-((int)Short.MIN_VALUE)-Short.MIN_VALUE; //

	/** @see java.io.DataInput#readUnsignedShort()	 */
	public int readUnsignedShort() throws IOException {
		int ret = readShort();
		if (ret < 0) {
			ret += MAX_UNSIGNED_SHORT; }
		return ret;
//		return readUnsignedByte()+(readUnsignedByte()<<8); //works too
	}

	/** @see java.io.DataInput#readChar()	*/
	public char readChar() throws IOException {
		return (char) readUnsignedShort(); }

	/** @see java.io.DataInput#readInt()	 */
	public int readInt() throws IOException {
		return readUnsignedShort()+(readShort()<<NUM_BITS_SHORT); }

	/** Number of Bits in a Short / Word */
	final static public byte NUM_BITS_INT = NUM_BITS_SHORT << 1;
	
	/** the maximum Value of an unsigned Short */
	final static public long MAX_UNSIGNED_INT = 1<<NUM_BITS_INT; //-((long)Integer.MIN_VALUE)-Integer.MIN_VALUE; //

	/** @see java.io.DataInput#readInt()	 */
	public long readUnsignedInt() throws IOException {
		long ret = readInt();
		if (ret < 0) {
			ret += MAX_UNSIGNED_INT; }
		return ret;
		//return readUnsignedShort()+(readShort()<<16);
	}

	/** @see java.io.DataInput#readLong()	 */	 
	public long readLong() throws IOException {
		return readUnsignedInt()+(readInt()<<NUM_BITS_INT); }

	/** @see java.io.DataInput#readFloat()		*/
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt()); }

	/** @see java.io.DataInput#readDouble()	 */
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong()); }

	/** @see java.io.DataInput#readLine()	 */
	public String readLine() throws IOException {
		return stream.readLine(); }

	/** @see java.io.DataInput#readUTF()	 */
	public String readUTF() throws IOException {
		return stream.readUTF(); }
	
}
