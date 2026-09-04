/*
 * File Name: OpenByteArrayOutputStream.java
 * Created on: 02.12.2003
 *
 */
package technology.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Title: OpenByteArrayOutputStream<p>
 * Description:
 * Purpose:
 * Opens up the super Class to save copying the inner Buffer. 
 * Also holds some static Methods for fast Streaming. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * @see com.ctp.soap.proxy.AAttributedStream
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class OpenByteArrayOutputStream extends ByteArrayOutputStream {

	/** Default Chunk Size of the STREAM() Methods */
	final static public int DEFAULT_CHUNK_SIZE = 4096;

	/** Default Size of the byte[] Buffer */
	final static public int DEFAULT_BUFFER_SIZE = 4096;

	/**Streams the whole InputStream Chunk-wise into the OutputStream */
	final static public long STREAM(InputStream in, OutputStream out) throws IOException {
		return STREAM(in, out, new byte[DEFAULT_CHUNK_SIZE]); }

	/**Streams the whole InputStream Chunk-wise into the OutputStream */
	final static public long STREAM(InputStream in, OutputStream out, int ChunkSize) throws IOException {
		return STREAM(in, out, new byte[ChunkSize]); }

	/**Streams the whole InputStream Chunk-wise into the OutputStream */
	final static public long STREAM(InputStream in, OutputStream out, final byte[] chunk) throws IOException {
		long ret = 0;
		for (int size; 0 <= (size = in.read(chunk)); ret += size) {
			out.write (chunk, 0, size); } //Stop when the last Chunk was not full
		out.flush();
		return ret; }

	/** inherited Constructor	 */
	public OpenByteArrayOutputStream() { super(DEFAULT_BUFFER_SIZE); }

	/** inherited Constructor	 
	 * @param size the initial Size of the Buffer
	 */
	public OpenByteArrayOutputStream(final int size) { super(size); }

	/**
	 * Accessor to the Buffer to save copying it 
	 * @return the internal byte[] Buffer filled with the current sData
	 */
	public byte[] getBuffer() {
		return this.buf;
	}

}
