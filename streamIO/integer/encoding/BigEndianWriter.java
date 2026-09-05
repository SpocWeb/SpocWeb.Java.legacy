package streamIO.integer.encoding;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a {@link DataOutputStream} to write Intel-native (little-endian) primitive values,
 * emitting each multi-byte value least-significant-byte first, the write-side counterpart
 * of {@link BigEndianReader}.
 *
 * @see BigEndianReader the matching reader for this byte order
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:32:10Z
 * digest: 20226b2f56587a1233a2224507ab05f0a6dd29a26e59a4e8b6decf042c7884a2
 * stale: false
 * tags: [code/stream_filter, code/base64_encoding, code/crc, code/xor_cipher]
 * concepts: [Byte/Character Re-Encoding Filters - Base64 BinHex URL/Entity Escaping CRC XOR]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class BigEndianWriter
implements DataOutput {

	/** the Stream to read from */
	private final DataOutputStream stream;

	/** Initializing Constructor 
	 * 
	 * @param filePath the Path to the File Object to use
	 */
	public BigEndianWriter(final String filePath) throws FileNotFoundException {
		this(new File(filePath)); 
	}

	/** Initializing Constructor 
	 * 
	 * @param file the File Object to use
	 */
	public BigEndianWriter(final File file) throws FileNotFoundException {
		this(new FileOutputStream(file)); 
	}

	/** Initializing Constructor 
	 * 
	 * @param streamIn_ the InputStream Implementation to use
	 */
	public BigEndianWriter(final OutputStream streamIn_) {
		this(new DataOutputStream(streamIn_)); 
	}

	/** Initializing Constructor 
	 * 
	 * @param stream_ the Stream to read from 
	 */
	public BigEndianWriter(final DataOutputStream stream_) {
		this.stream = stream_; 
	}

	/** Closes the underlying stream. */
	public void close() throws IOException { this.stream.close(); }

	/** Writes the low-order byte of the given value. */
	public void write(int arg0) throws IOException { stream.write(arg0); }

	/** Writes every byte of the given array. */
	public void write(byte[] arg0) throws IOException { stream.write(arg0); }

	/** Writes the given slice of the array. */
	public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		stream.write(arg0, arg1, arg2);
	}

	/** Writes one byte holding the given boolean. */
	public void writeBoolean(boolean arg0) throws IOException {
		stream.writeBoolean(arg0);
	}

	/** Writes the low-order byte of the given value. */
	public void writeByte(int arg0) throws IOException { stream.writeByte(arg0); }

	/** Writes the low-order byte of every character in the given string. */
	public void writeBytes(String arg0) throws IOException { stream.writeBytes(arg0); }

	/** Writes the given value as two bytes, low byte first. */
	public void writeChar(int arg0) throws IOException { stream.writeChar(arg0); }

	/** Writes every character of the given string as two bytes each, low byte first. */
	public void writeChars(String arg0) throws IOException { stream.writeChars(arg0); }

	/** Writes the given double as its 8-byte IEEE 754 bit representation. */
	public void writeDouble(double arg0) throws IOException {
		long bits = Double.doubleToLongBits(arg0);
		writeLong(bits); }

	/** Writes the given float as its 4-byte IEEE 754 bit representation. */
	public void writeFloat(float arg0) throws IOException {
		int bits = Float.floatToIntBits(arg0);
		writeInt(bits); }

	/** Writes the low 16 bits of the given value as two bytes, low byte first. */
	public void writeShort(int arg0) throws IOException {
		writeByte((arg0 & 0xFF));
		writeByte((arg0 >> 8) & 0xFF);
	}

	/** Writes the given value as four bytes, low word first. */
	public void writeInt(int arg0) throws IOException {
		writeShort((arg0 & 0xFFFF));
		writeShort((arg0 >> 16) & 0xFFFF);
	}

	/** Writes the given value as eight bytes, low int first.
	 * @param arg0 the value to write */
	public void writeLong(long arg0) throws IOException {
		writeInt((int)(arg0 & 0xFFFFFFFF));
		writeInt((int)(arg0 >> 32) & 0xFFFFFFFF);
	}

	/** Writes the given string in modified UTF-8. */
	public void writeUTF(String arg0) throws IOException { stream.writeUTF(arg0); }

}
