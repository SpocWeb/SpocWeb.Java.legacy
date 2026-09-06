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
 * Represents a single MIDI Track Chunk ("MTrk") read from a File, sized to hold its raw Event Data as an unparsed Byte Array.
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
 * mtime: 2026-09-05T10:22:41Z
 * digest: 8a4964081ce0acacfeb0bc62a7dbcb830ad415d931717727d8fec7294e0d7fd9
 * stale: false
 * tags: [code/midi_playback]
 * concepts: [MIDI Track]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
public class MidiChunk
extends FileChunk {

	/** Header Prefix indicating a MIDI File Track Chunk  	*/
	final static public String TRACK_HEADER = "MTrk";

	/** Data about MIDI Events 	 */
	final public byte[] events;

	/** Reads the next "MTrk" Chunk from the Stream and copies its raw Content into {@link #events}, without parsing individual MIDI Events.
	 * @param streamIn_ the DataInput Implementation to use
	 * @throws IOException
	 */
	public MidiChunk(final BigEndianReader streamIn_) throws IOException {
		super(streamIn_, TRACK_HEADER);
		events = new byte[chunkSize];
		streamIn.readFully(events); //advance past this Chunk, so the next Header is read from the right Offset
	}

}
