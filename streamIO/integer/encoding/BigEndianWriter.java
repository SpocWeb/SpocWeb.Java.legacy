package streamIO.integer.encoding;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

	public void close() throws IOException { this.stream.close(); }
	
	public void write(int arg0) throws IOException { stream.write(arg0); }

	public void write(byte[] arg0) throws IOException { stream.write(arg0); }

	public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		stream.write(arg0, arg1, arg2); 
	}

	public void writeBoolean(boolean arg0) throws IOException {
		stream.writeBoolean(arg0); 
	}

	public void writeByte(int arg0) throws IOException { stream.writeByte(arg0); }

	public void writeBytes(String arg0) throws IOException { stream.writeBytes(arg0); }

	public void writeChar(int arg0) throws IOException { stream.writeChar(arg0); }

	public void writeChars(String arg0) throws IOException { stream.writeChars(arg0); }

	public void writeDouble(double arg0) throws IOException {
		long bits = Double.doubleToLongBits(arg0); 
		writeLong(bits); }

	public void writeFloat(float arg0) throws IOException {
		int bits = Float.floatToIntBits(arg0); 
		writeInt(bits); }

	public void writeShort(int arg0) throws IOException {
		writeByte((arg0 & 0xFF)); 
		writeByte((arg0 >> 8) & 0xFF); 
	}

	public void writeInt(int arg0) throws IOException {
		writeShort((arg0 & 0xFFFF)); 
		writeShort((arg0 >> 16) & 0xFFFF); 
	}

	public void writeLong(long arg0) throws IOException {
		writeInt((int)(arg0 & 0xFFFFFFFF)); 
		writeInt((int)(arg0 >> 32) & 0xFFFFFFFF); 
	}

	public void writeUTF(String arg0) throws IOException { stream.writeUTF(arg0); }

}
