/*
 * File Name: WaveDataChunk.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: WaveDataChunk<p>
 * Description:
 * MetaData Class for a Wav File Data Frame.
 * The Frame has several (1 or 2, 5 on Surround) Channels
 * and each Channel is coded using 8, 16 or 24 Bits.
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
 * mtime: 2026-09-05T10:23:08Z
 * digest: 48eb140df3d8d7b35cc2ab9aa010051b81440d6f2ba606e46202ab94d39284fa
 * stale: false
 * tags: [code/audio, code/binary_data_reading]
 * concepts: [WAV Data Chunk]
 * facets: {layer: domain, status: broken, complexity: medium}
 * -->
 */
public class WaveDataChunk 
extends FileChunk {

	/** Header Prefix indicating a WAVE File Data Chunk  	*/
	final static public String DATA_HEADER = "data";
	
	/** the Data as a 16 Bit Stream 	 */
	final public int[] stream16; 
	
	/** the Data as an 8 Bit Stream 	 */
	final public byte[] stream_8; 

	/** Reads the "data" Chunk, storing its Samples either as raw Bytes ({@link #stream_8}, for 8- and 24-Bit Resolutions)
	 * or as a decoded Int Array ({@link #stream16}, for 16-Bit Resolution).
	 * @param streamIn_ the DataInput Implementation to use
	 * @param numBytesPerValue the Sample Resolution in Bytes: 1 (8 Bit), 2 (16 Bit) or 3 (24 Bit)
	 * @throws IOException
	 */
	public WaveDataChunk(final BigEndianReader streamIn_, final int numBytesPerValue) throws IOException {
		super(streamIn_, DATA_HEADER);
		switch (numBytesPerValue) {
			case 1:
				stream16 = null;
				stream_8 = new byte[chunkSize];
				streamIn.readFully(stream_8);
				break;
			case 2:
				stream16 = new int [chunkSize/numBytesPerValue];
				stream_8 = null;
				for (int i = -1; ++i < stream16.length; ) {
					stream16[i] = streamIn.readShort(); }
				break;
			case 3: //24 Bit
				stream16 = null;
				stream_8 = new byte[chunkSize]; 
				streamIn.readFully(stream_8); 
				break;
			default :
				throw new IOException("Not supported Bit Resolution:"+numBytesPerValue); 
		}
	}

}
