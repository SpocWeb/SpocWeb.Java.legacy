package streamIO.integer.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.AStreamOut;
import streamIO.Log;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;
import tester.process.StreamProcessor;

/**
 * ByteStreamThread is a Thread
 * which asynchronously copies it's input to it's output
 * and terminates when the Input streamIO is empty (EOF = -1).
 * 
 * It also has all synchronous static Methods to stream Contents 
 * from a ByteStream_In to a ByteStreamOut.  
 *
 * This is especially necessary, when remoting a Process,
 * because otherwise forwarding both Input and Output streamIO
 * would block each other. 
 * <!-- docstate
 * tags: [code/pipe_abstraction, code/pipe_implementation]
 * concepts: [In-Memory Producer-Consumer Byte Pipes]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class ByteStreamerThread 
extends Thread {

	/**The main entry point for the application.
	 * Streams each File named into the Output streamIO,
	 * which could be piped into another File, thus appending all Files.
	 * @param args Array of File Names passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		for (int i = args.length; --i >= 0;) {
			//Log.N("Streaming:'" + args[i] + "'");
			java.io.FileInputStream fis = new java.io.FileInputStream(args[i]);
			STREAM(fis, System.out);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Constant defining the Default Buffer Size  */
	private static final int BUFFER_SIZE_DEFAULT = 4096;

	////////////////////////////////////////////////////////////////////////////
	/// #region : static main() Method (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Streams the WHOLE InputStream byte-wise into the OutputStream
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening 	 */
	final static public long STREAM(final InputStream in, final OutputStream out) 
	throws java.io.IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0;
		for (int val; ((val = in.read()) != IStreamIn_Byte.EOF) || (in.available() >= 0); ++ret ) {
			out.write (val); }
		out.flush();
		return ret; }

	/** Streams the WHOLE InputStream byte-wise into the OutputStream
	  * @see BackTracker.operate
	  * @see ProcessorRunner.run
	  * @see StreamProcessor.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening 	 */
	final static public long STREAM(final IStreamIn_Byte in, final IStreamOutByte out) 
	throws IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0; //works!!!
		for (int val; ((val = in.read()) != IStreamIn_Byte.EOF) || (in.available() >= 0); ++ret ) {
			out.write (val); }
		out.flush();
		return ret; }

	/** Streams the WHOLE InputStream byte-wise into the OutputStream
	 * Surround it with a while (available() >= 0) when a read() Method returns -1 intermediately 
	 * @see BackTracker.operate
	 * @see ProcessorRunner.run
	 * @see StreamProcessor.run
	 * @see LimitedSizeOutputStream.stream for Bytes
	 * @see AContainer.copyAt for creating Copies of the Items
	 * @see AStreamOut.stream for fast streaming
	 * @see AStreamOut.add for flattening 	 */
	final static public long STREAM(final IStreamIn_Byte in, final OutputStream out) 
	throws java.io.IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0; //doesn't work!
		for (int val; ((val = in.read()) != IStreamIn_Byte.EOF) || (in.available() >= 0); ++ret ) {
			out.write (val); }
		out.flush();
		return ret; }

	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * Surround it with a while (available() >= 0) when a read() Method returns 0 intermediately 
	 */
	final static public long STREAM(final InputStream in, final OutputStream out
			, final int ChunkSize) throws IOException {
		return STREAM(in, out, new byte[ChunkSize]); }

	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * Surround it with a while (available() >= 0) when a read() Method returns 0 intermediately 
	 */
	final static public long STREAM(final InputStream in, final OutputStream out
		, final byte[] chunk) throws IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0;
		for (int size; 0 < (size = in.read(chunk)); ret += size) {
			out.write (chunk, 0, size); } //Stop when the last Chunk was not full
		out.flush();
		return ret; }

	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * Surround it with a while (available() >= 0) when a read() Method returns 0 intermediately 
	 */
	final static public long STREAM(final IStreamIn_Byte in, final IStreamOutByte out
			, final int ChunkSize) throws IOException {
		return STREAM(in, out, new byte[ChunkSize]); }
	
	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * Surround it with a while (available() >= 0) when a read() Method returns 0 intermediately 
	 */
	final static public long STREAM(final IStreamIn_Byte in, final IStreamOutByte out
			, final byte[] val) throws IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0;
		for (int size; 0 < (size = in.read(val)); ret+=size ) {
			out.write (val, 0, size); } //Stop when the last Chunk was not full
		out.flush(); //flushing could also be done later...
		return ret; }
	
	/**Streams the WHOLE InputStream byte-wise into the OutputStream
	 * Surround it with a while (available() >= 0) when a read() Method returns -1 intermediately 
	 * @see BackTracker.operate
	 * @see ProcessorRunner.run
	 * @see StreamProcessor.run
	 * @see LimitedSizeOutputStream.stream for Bytes
	 * @see AContainer.copyAt for creating Copies of the Items
	 * @see AStreamOut.stream for fast streaming
	 * @see AStreamOut.add for flattening 	 */
	final static public long STREAM(final InputStream in, final IStreamOutByte out) 
	throws IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0; 
		for (int val; ((val = in.read()) != IStreamIn_Byte.EOF) || (in.available() >= 0); ++ret ) {
			out.write (val); }
		out.flush();
		return ret; }
	
	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * Surround it with a while (available() >= 0) when a read() Method returns -1 intermediately 
	 */
	final static public long STREAM(final IStreamIn_Byte in, final OutputStream out
			, final int ChunkSize) throws IOException {
		return STREAM(in, out, new byte[ChunkSize]); }

	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * Surround it with a while (available() >= 0) when a read() Method returns -1 intermediately 
	 */
	final static public long STREAM(final IStreamIn_Byte in, final OutputStream out
			, final byte[] buffer) throws IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0;
		for (int size; 0 < (size = in.read(buffer)); ret+=size ) {
			out.write (buffer, 0, size); } //Stop when the last Chunk was not full
		out.flush();
		return ret; }

	/**Streams the whole InputStream Chunk-wise into the OutputStream (buffered) 
	 * Surround it with a while (available() >= 0) when a read() Method returns -1 intermediately 
	 */
	final static public long STREAM(final InputStream in, final IStreamOutByte out
			, final int ChunkSize) throws IOException {
		return STREAM(in, out, new byte[ChunkSize]); }

	/**Streams the whole InputStream Chunk-wise into the OutputStream (buffered) 
	 * Surround it with a while (available() >= 0) when a read() Method returns -1 intermediately 
	 */
	final static public long STREAM(final InputStream in, final IStreamOutByte out
			, final byte[] buffer) throws IOException {
		if ((in == null) || (out == null)) 
			return 0; 
		long ret = 0;
		for (int size; 0 < (size = in.read(buffer)); ret+=size ) {
			out.write (buffer, 0, size); } //Stop when the last Chunk was not full
		out.flush();
		return ret; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Input streamIO to read from  */
	private final InputStream in;
	
	/** Reference to the Output streamIO to write to  */
	private final OutputStream out;
	
	/** Reference to the Input streamIO to read from  */
	private final IStreamIn_Byte iIn;
	
	/** Reference to the Output streamIO to write to  */
	private final IStreamOutByte iOut;
	
	/** Buffer Size to speed up reading   */
	private final int bufferSize;
	
	/** transient Value for the Number of Values streamed	*/
	private long numTransferred = 0;
	
	/** Flag to indicate the Command to close the Stream after Streaming	*/
	public boolean closeStreamOutOnFinish;
	
	/** transient Value for the Number of Values streamed	*/
	public long getNumTransferred () {
		return numTransferred; 
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, for Inputstream
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final InputStream in, final OutputStream out) {
		this(in, out, "", BUFFER_SIZE_DEFAULT); }

	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final InputStream in, final OutputStream out, final int bufferSize) {
		this(in, out, "", bufferSize); }

	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final InputStream in, final OutputStream out, final String name) {
		this(in, out, name, BUFFER_SIZE_DEFAULT); }

	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final InputStream in, final OutputStream out, final String name, final int bufferSize) {
		super(name);
		this.iIn = null;
		this.iOut = null;
		this.in = in;
		this.out = out;
		this.bufferSize = bufferSize;
		setPriority(Thread.MAX_PRIORITY - 1); //blocked anyway...
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, for IStreamXxxByte
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final IStreamIn_Byte in, final IStreamOutByte out) {
		this(in, out, "", BUFFER_SIZE_DEFAULT); }

	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final IStreamIn_Byte in, final IStreamOutByte out, final int bufferSize) {
		this(in, out, "", bufferSize); }

	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final IStreamIn_Byte in, final IStreamOutByte out, final String name) {
		this(in, out, name, BUFFER_SIZE_DEFAULT); }

	/**
	 * Set up for copy.
	 * @param name Name of the thread
	 * @param in streamIO to copy from
	 * @param out streamIO to copy to
	 * @param bufferSize The Size of the Buffer to use
	 */
	public ByteStreamerThread(final IStreamIn_Byte in, final IStreamOutByte out, final String name, final int bufferSize) {
		super(name);
		this.in = null;
		this.out = null;
		this.iIn = in;
		this.iOut = out;
		this.bufferSize = bufferSize;
		setPriority(Thread.MAX_PRIORITY - 1); //blocked anyway...
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Runnable: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Copy all Input into the Output.
	 */
	public void run() {
		try {
			numTransferred = 
				STREAM(iIn, iOut, bufferSize) +
				STREAM(iIn,  out, bufferSize) +
				STREAM( in, iOut, bufferSize) +
				STREAM( in,  out, bufferSize);
		} catch (IOException exc) { //possibly report the Failure to a registered Reporter
			logException(exc);
		}
		if (closeStreamOutOnFinish) {
			//in.close(); //by Contract the close() Method on InputStreams does nothing, except for releasing Resources! 
			try { if ( out != null)  out.close(); 
			} catch (IOException exc) { //possibly report the Failure to a registered Reporter
				logException(exc);
			}
			try { if (iOut != null) iOut.close(); 
			} catch (IOException exc) { //possibly report the Failure to a registered Reporter
				logException(exc);
			}
		}
	}
	
	private void logException(Exception exc) {
		Log.ERR.n("Child Streaming Thread '"+this.getName()+"':"+exc);
		exc.printStackTrace(Log.ERR);
		if (numTransferred > 0)
			numTransferred = -numTransferred; //indicate the Failure
	}

}
