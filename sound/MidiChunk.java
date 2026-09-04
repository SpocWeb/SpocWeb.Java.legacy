/*
 * File Name: MidiChunk.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: MidiChunk<p>
 * Description:
 * Reads and writes MIDI Chunks from / to a File 
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
public class MidiChunk 
extends FileChunk {

	/** Header Prefix indicating a MIDI File Track Chunk  	*/
	final static public String TRACK_HEADER = "MTrk";
	
	/** Data about MIDI Events 	 */
	final public byte[] events;
	
	/**
	 * @param streamIn_
	 * @param chunkType_
	 * @throws IOException
	 */
	public MidiChunk(final BigEndianReader streamIn_) throws IOException {
		super(streamIn_, TRACK_HEADER);
		events = new byte[chunkSize];
	}

}
