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
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public class MidiFile extends FileChunk {

	/** Header Prefix indicating a WAVE File 	*/
	final static public String MIDI_HEADER = "MThd"; 
	
	/** 
	 * 0––single track.Only one track to worry about. 
	 * 1––multiple tracks, synchronous.Several tracks, all starting at the same time.
	 * 2––multiple tracks, asynchronous.Several tracks, potentially starting at different times.
	 */
	final public short trackTypes; 

	/** The Number of Tracks 	 */
	final public short numTracks; 

	/** The Number of Ticks per Quarter Note 	 */
	final public short ticksPerQuarter; 

	private MidiChunk[] tracks; 
	
	/**
	 * @param streamIn_
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
