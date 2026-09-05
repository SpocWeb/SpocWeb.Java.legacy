/*
 * File Name: MidiFile.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: MidiFile<p>
 * Description:
 * Reads a Standard MIDI File ("MThd" Header plus a Sequence of "MTrk" Track Chunks):
 * parses the Track Type, Track Count and Ticks-per-Quarter-Note from the Header,
 * then reads each declared Track as a {@link MidiChunk}.
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
 * mtime: 2026-09-05T10:22:51Z
 * digest: c3ce69791da3ef0e5264fffe7a35b369ccdbdef2fb5871d2f81e2c946ba3c0ac
 * stale: false
 * tags: [code/midi_playback, code/file_parsing]
 * concepts: [Standard MIDI File]
 * facets: {layer: domain, status: broken, complexity: medium}
 * -->
 */
public class MidiFile extends FileChunk {

	/** Header Prefix indicating a WAVE File 	*/
	final static public String MIDI_HEADER = "MThd"; 
	
	/** 
	 * 0��single track.Only one track to worry about. 
	 * 1��multiple tracks, synchronous.Several tracks, all starting at the same time.
	 * 2��multiple tracks, asynchronous.Several tracks, potentially starting at different times.
	 */
	final public short trackTypes; 

	/** The Number of Tracks 	 */
	final public short numTracks; 

	/** The Number of Ticks per Quarter Note 	 */
	final public short ticksPerQuarter; 

	private MidiChunk[] tracks; 
	
	/** Reads the "MThd" Header (Track Type, Track Count, Ticks per Quarter Note) followed by that many "MTrk" Track Chunks.
	 * @param streamIn_ the DataInput Implementation to use
	 * @throws IOException
	 */
	public MidiFile(final BigEndianReader streamIn_) throws IOException {
		super(streamIn_, MIDI_HEADER);
		trackTypes = streamIn.readShort(); 
		numTracks = streamIn.readShort();  
		ticksPerQuarter = streamIn.readShort(); 
		tracks = new MidiChunk[numTracks]; 
		for (int i = -1; ++i < numTracks; ) {
			tracks[i] = new MidiChunk(streamIn); 
		}
	}

}
