package sound;

import java.io.IOException;

import streamIO.integer.AStreamIn_Int;
import streamIO.integer.encoding.BigEndianReader;

/**
 * 
 * @author heuerm
 *
 */
public class WaveStreamIn 
extends AStreamIn_Int {
	
	public final BigEndianReader StreamIn; 
	
	/** The actual Channel to retrieve 	 */
	public final int Channel; 
	
	/** Reference to the current Wave Format Chunk 	*/	
	final public WaveFormatChunk Format; 
	
	/** the current Position in the Stream in Samples	 */
	private int Position; 
	
	/** Reference to the current Wave Data Chunk 	*/	
	final public FileChunk Data;
	
	/**
	 * Initializing Constructor
	 * @param streamIn the Input Stream to read from
	 * @param numChannels the Number of Channels in this Stream
	 * @param channel the Channel to retrieve 
	 */
	public WaveStreamIn (WaveFormatChunk format, FileChunk data, int channel) {
		this.StreamIn = format.streamIn; 
		this.Format = format; 
		this.Data = data; 
		this.Channel = channel % format.NumChannels; 
		try {
			StreamIn.skipBytes(channel*(Format.BitsPerSample/8));
		} catch (IOException x) {
			
		}
	}

	public long availAble() {
		return Data.chunkSize / Format.FrameSize - Position; 
	}

	public long getMaxMarkSize() { return 0; }

	/**Public Method for other Classes to determine the minimum Value from the Stream	 */
	public double getMinDouble() { return Integer.MIN_VALUE; }

	public byte getOrder() { return 0; }

	public long getPosition() { return Position; }

	protected long nextLongInternal() {
		try {
			++Position; 
			//StreamIn.skipBytes(Format.FrameSize-(Format.BitsPerSample/8));
			if (Format.BitsPerSample == 8) {
				StreamIn.skipBytes(Format.FrameSize-1);
				return StreamIn.readUnsignedByte() - 128; //the average Value is 128 
				/* byte ret = StreamIn.readByte(); 
				if (ret < 0)
					return ret + 128; 
				return ret - 128; */ 
			} 
			if (Format.BitsPerSample == 16) {
				StreamIn.skipBytes(Format.FrameSize-2);
				return StreamIn.readShort();
			} 
			if (Format.BitsPerSample == 24) { //TODO: read only 3 Bytes!
				StreamIn.skipBytes(Format.FrameSize-3);
				return StreamIn.readInt();
			} 
		} catch (IOException x) {
		}
		return EOF; 
	}

}
