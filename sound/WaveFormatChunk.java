/*
 * File Name: WaveFormatChunk.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.encoding.BigEndianReader;

/**
 * Title: WaveFormatChunk<p>
 * Description:
 * Reads the "fmt " Chunk of a WAV File, describing the Sample Encoding (Channel Count, Sample Rate, Bit Depth, Compression Tag) used by the following "data" Chunk.
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
 * mtime: 2026-09-05T10:23:32Z
 * digest: 30c3f74e44126b91bfc396952540a82f25c93df97df8c90fae255a6f667e6754
 * stale: false
 * tags: [code/audio, code/binary_file_format]
 * concepts: [WAV Format Chunk]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public class WaveFormatChunk 
extends FileChunk {

	/** Header Prefix indicating a WAVE File Chunk 	*/
	final static public String FORMAT_HEADER = "fmt "; 
	
	/** 1 for mono, 2 for stereo, more for Surround	 */
	public short NumChannels;
	
	/** The sampling frequency of the waveform. 
	 * Commonly used values are 11025, 22050, and 44100. 
	 * Frequencies other than the three common ones are allowed but are not encouraged. 
	 */
	public int SamplesPerSec;

	/** The average bytes per second for transferal of the waveform. 	 */
	public int AvgBytesPerSec;
	
	/** 'BlockAlign' The size of a single sample frame, containing one Sample for each Channel, in bytes. 
	 * This can also be calculated using the following formula: 
	 * wChannels * ceil(wBitsPerSample / 8) 
	 * 
	 * For a 16-bit mono waveform, the number would be 2; 
	 * for stereo 16-bit waveforms, it would be 4
	 */
	public short FrameSize;
	
	/** The Number of Bits per Sample: 8, 16 or 24
	 */
	public short BitsPerSample;
	
	/** This gives the WAV format category of the data chunk, 
	 * indicating the compression type. 
	 * If wFormatTag is 1, no compression is being used (normal Pulse Code Modulation). 
	 * If compression is used, additional information will be appended to the format 
	 * and in a fact chunk to allow for appropriate decompression. 
	 * 
	 * Sample Datenformate (Format Tag)
	 * ID    	Bezeichnung
	 * 0x0001 	PCM
	 * 0x0002 	MS ADPCM
	 * 0x0005 	IBM CVSD
	 * 0x0006 	ALAW
	 * 0x0007 	MULAW
	 * 0x0010 	OKI ADPCM
	 * 0x0011 	DVI/IMA ADPCM
	 * 0x0012 	MEDIASPACE ADPCM
	 * 0x0013 	SIERRA ADPCM
	 * 0x0014 	G723 ADPCM
	 * 0x0015 	DIGISTD
	 * 0x0016 	DIGIFIX
	 * 0x0017 	DIALOGIC OKI ADPCM
	 * 0x0020 	YAMAHA ADPCM
	 * 0x0021 	SONARC
	 * 0x0022 	DSPGROUP TRUESPEECH
	 * 0x0023 	ECHOSC1
	 * 0x0024 	AUDIOFILE AF36
	 * 0x0025 	APTX
	 * 0x0026 	AUDIOFILE AF10
	 * 0x0030 	DOLBY AC2
	 * 0x0031 	GSM610
	 * 0x0033 	ANTEX ADPCME
	 * 0x0034 	CONTROL RES VQLPC
	 * 0x0035 	CONTROL RES VQLPC
	 * 0x0036 	DIGIADPCM
	 * 0x0037 	CONTROL RES CR10
	 * 0x0038 	NMS VBXADPCM
	 * 0x0039 	CS IMAADPCM
	 * 0x0040 	G721 ADPCM
	 * 0x0050 	MPEG
	 * 0x0200 	CREATIVE ADPCM
	 * 0x0202 	CREATIVE FASTSPEECH8
	 * 0x0203 	CREATIVE FASTSPEECH10
	 * 0x0300 	FM TOWNS SND
	 * 0x1000 	OLIGSM
	 * 0x1001 	OLIADPCM
	 * 0x1002 	OLICELP
	 * 0x1003 	OLISBC
	 * 0x1004 	OLIOPR
	 */
	public short FormatTag;

	/** reads the Data from the given Stream 
	 * 
	 * @param reader
	 */	
	public void read(final BigEndianReader reader) throws IOException {
		FormatTag = reader.readShort();
		NumChannels = reader.readShort();
		SamplesPerSec = reader.readInt();
		AvgBytesPerSec = reader.readInt();
		FrameSize = reader.readShort(); 
		BitsPerSample = reader.readShort(); 
	}

	/** Reads the "fmt " Chunk Header, then its Fields via {@link #read(BigEndianReader)}.
	 * @param streamIn_ the DataInput Implementation to use
	 * @throws IOException
	 */
	public WaveFormatChunk(BigEndianReader streamIn_) throws IOException {
		super(streamIn_, FORMAT_HEADER);
		read(streamIn_); 
	}

}
