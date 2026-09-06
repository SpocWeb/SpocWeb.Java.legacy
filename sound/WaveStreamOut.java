package sound;

import java.io.IOException;

import streamIO.integer.IStreamOutInt;
import streamIO.integer.encoding.BigEndianWriter;

/**
 * Writes a single-Channel (mono) PCM WAV File: on construction it writes the RIFF/WAVE Header,
 * the "fmt " Chunk, and the "data" Chunk Header (with a Size computed from {@code numSamples}
 * up front), after which Samples are appended one by one via {@link #addInt(int)}.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:24:03Z
 * digest: 6180a829999cc6f229e4cc4d054921a25d6c2fc6c0fbe28be20a42fd6d4b39b4
 * stale: false
 * tags: [code/audio, code/media_playback]
 * concepts: [WAV Writer]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
public class WaveStreamOut
//extends AStreamOutByte
implements IStreamOutInt {

	/** WAV Format Tag value for uncompressed Pulse Code Modulation 	 */
	public static final int FORMAT_PCM = 1;

	/** 8, 16 or 24 	 */
	public final int BitsPerSample;

	/** The underlying Writer used to emit the WAV File's Bytes. */
	private final BigEndianWriter writer;

	/** Opens the File and writes the RIFF/WAVE Header, "fmt " Chunk and "data" Chunk Header.
	 * @param filePath the Path of the WAV File to create
	 * @param sampleRate the Sampling Frequency in Hz
	 * @param bitsPerSample the Sample Resolution: 8, 16 or 24
	 * @param numSamples the total Number of Samples that will be written, used to size the "data" Chunk up front
	 * @throws IOException
	 */
	public WaveStreamOut(final String filePath, int sampleRate, int bitsPerSample, int numSamples) throws IOException {
		int bytesPerSample = bitsPerSample/8; 
		int dataLength = numSamples*bytesPerSample; 
		
		this.BitsPerSample = bitsPerSample; 
		this.writer = new BigEndianWriter(filePath); 
		writer.writeBytes(WaveFile.RIFF_HEADER); 
		writer.writeInt(dataLength+12+24+8); 
		writer.writeBytes(WaveFile.WAVE_HEADER); 
		
		int numChannels = 1; 
		int frameSize = numChannels * bytesPerSample;
		int bytesPerSec = frameSize * sampleRate;  
		writer.writeBytes(WaveFormatChunk.FORMAT_HEADER); 
		writer.writeInt(16); 
		writer.writeShort(FORMAT_PCM); 
		writer.writeShort(numChannels);
		writer.writeInt(sampleRate); 
		writer.writeInt(bytesPerSec); 
		writer.writeShort(frameSize); 
		writer.writeShort(bitsPerSample); 
		
		writer.writeBytes(WaveFile.DATA_HEADER); 
		writer.writeInt(dataLength); 
	}

	/** Closes the underlying File. */
	public void close() throws IOException { writer.close(); }

	/** Appends one Sample, encoded according to {@link #BitsPerSample}.
	 * @param b the Sample Value to write
	 * @return this Instance, for Chaining
	 */
	public IStreamOutInt addInt(int b) {
		try {
		if (BitsPerSample == 8)
			writer.writeByte(b);
		else if (BitsPerSample == 16)
			writer.writeShort(b);
		else if (BitsPerSample == 24) { //3 Bytes, low Word first, matching the Writer's little endian Order
			writer.writeShort(b & 0xFFFF);
			writer.writeByte((b >> 16) & 0xFF); }
		} catch (IOException x) {
			throw new RuntimeException(x);
		}
		return this;
	}

	/** Appends one Sample truncated to {@code int}, via {@link #addInt(int)}.
	 * @param b the Sample Value to write
	 * @return this Instance, for Chaining
	 */
	public IStreamOutInt addLong(long b) { return addInt((int)b); }
	
}
