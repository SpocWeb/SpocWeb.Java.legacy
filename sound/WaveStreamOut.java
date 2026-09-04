package sound;

import java.io.IOException;

import streamIO.integer.IStreamOutInt;
import streamIO.integer.encoding.BigEndianWriter;

public class WaveStreamOut 
//extends AStreamOutByte 
implements IStreamOutInt {

	public static final int FORMAT_PCM = 1;
	
	/** 8, 16 or 24 	 */
	public final int BitsPerSample; 
	
	private final BigEndianWriter writer; 
	
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

	public void close() throws IOException { writer.close(); }

	public IStreamOutInt addInt(int b) {
		try {
		if (BitsPerSample == 8)
			writer.writeByte(b); 
		else if (BitsPerSample == 16)
			writer.writeShort(b); 
		else if (BitsPerSample == 24)
			writer.writeInt(b); //TODO: write 3 Bytes
		} catch (IOException x) {
			throw new RuntimeException(x); 
		}
		return this;
	}

	public IStreamOutInt addLong(long b) { return addInt((int)b); }
	
}
