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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:16Z
 * digest: 5374854bde555d80614dd8e379843f85c82183d4d55bdc182a9d2fd58c5657b8
 * stale: false
 * tags: [code/binary_reader, code/file_parsing]
 * concepts: [RIFF Chunk]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
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

	/** Initializing Constructor that skips to the first Chunk of any Type, without Verification.
	 *
	 * @param streamIn_ the DataInput Implementation to use
	 * @throws IOException
	 */
	public FileChunk(final BigEndianReader streamIn_) throws IOException {
		this(streamIn_, null);
	}

	/** Initializing Constructor
	 *
	 * @param streamIn_ the DataInput Implementation to use
	 * @param chunkType_ the Type to verify. If null, no Verification takes place!
	 * @throws IOException
	 */
	public FileChunk(final BigEndianReader streamIn_, final String chunkType_
	) throws IOException {
		this.streamIn = streamIn_;
		findChunk(chunkType_);
	}

	/** Reads the next 4 ASCII Characters from the Stream, treating them as a Chunk Type Name.
	 * @return the 4-Character Chunk Type Name
	 */
	public String readChunkTyp() throws IOException { return readChunkTyp(null); }

	/** Reads the next 4 ASCII Characters into the given Buffer (or a new 4-Byte Buffer if null), treating them as a Chunk Type Name.
	 * @param buffer the Buffer to reuse, or null to allocate a new 4-Byte Buffer
	 * @return the 4-Character Chunk Type Name
	 */
	public String readChunkTyp(byte[] buffer) throws IOException {
		if (buffer == null)
			buffer = new byte[4];
		streamIn.readFully(buffer);
		return new String(buffer);
	}

	/** Scans forward through the Stream, Chunk by Chunk, until a Chunk of the given Type is found (or any Chunk, if null),
	 * skipping the Contents of every non-matching Chunk it passes.
	 * @param chunkType_ the Type to search for; if null, the first Chunk encountered is accepted
	 */
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
