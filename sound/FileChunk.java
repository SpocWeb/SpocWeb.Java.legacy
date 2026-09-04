/*
 * File Name: FileChunk.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: FileChunk<p>
 * Description:
 * Base Class for different Types of File Chunks
 *
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
public class FileChunk {

	/** Reference to the Reader Object to read the Data from  	 */
	final public BigEndianReader streamIn; 

	/** The nominal File Size (without 8 Byte RIFF Header)
	 * Normally the File Size is limited by 4 GByte, 
	 * so negative Values need to be wrapped around!
	 */
	public int chunkSize; 
	
	/** The nominal 4 ASCII Character Chunk Type Name. 
	 * e.g. RIFF etc. 
	 */
	public String chunkType; 

	/** Initializing Constructor 
	 * 
	 * @param streamIn_ the DataInput Implementation to use
	 * @param chunkType the Type to verify. If null, no Verification takes place! 
	 * @throws IOException
	 */
	public FileChunk(final BigEndianReader streamIn_) throws IOException {
		this(streamIn_, null); 
	}

	/** Initializing Constructor 
	 * 
	 * @param streamIn_ the DataInput Implementation to use
	 * @param chunkType the Type to verify. If null, no Verification takes place! 
	 * @throws IOException
	 */
	public FileChunk(final BigEndianReader streamIn_, final String chunkType_
	) throws IOException {
		this.streamIn = streamIn_;
		findChunk(chunkType_);
	}

	public String readChunkTyp() throws IOException { return readChunkTyp(null); }

	public String readChunkTyp(byte[] buffer) throws IOException {
		if (buffer == null) 
			buffer = new byte[4]; 
		streamIn.readFully(buffer);
		return new String(buffer); 
	}
	
	public void findChunk(final String chunkType_) throws IOException {
		final byte[] buffer = new byte[4]; 
		for(; ; ) {
			chunkType = readChunkTyp(buffer); 
			chunkSize = streamIn.readInt(); 
			if((chunkType_ == null) || 
				chunkType_.equals(chunkType)) 
				return; //chunkSize; 
			streamIn.skipBytes(chunkSize);
		}
	}

}
