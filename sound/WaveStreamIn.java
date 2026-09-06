package sound;

import java.io.IOException;

import streamIO.integer.AStreamIn_Int;
import streamIO.integer.encoding.BigEndianReader;

/**
 * Adapts one Channel of a WAV {@link WaveDataChunk}'s interleaved Sample Data (as described by a {@link WaveFormatChunk})
 * into a sequential {@code int}-valued Sample Stream, skipping over the other Channels' Bytes in each Frame.
 *
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:24:56Z
 * digest: 6858d125b359ccf5692b340b2de029ddf2fcd8eae5d014f19acd4410dae93e9d
 * stale: false
 * tags: [code/audio, code/media_playback]
 * concepts: [PCM Sample Stream]
 * facets: {layer: domain, status: broken, complexity: medium}
 * -->
 */
public class WaveStreamIn
extends AStreamIn_Int {

	/** Reference to the underlying Reader, shared with {@link #Format}'s Stream 	*/
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
		} catch (IOException x) { //the Stream would be left at an unknown Offset: fail loudly
			throw new RuntimeException(x);
		}
	}

	/** Computes the Number of Samples remaining on this Channel, from the total Data Chunk Size and Frame Size.
	 * @return the Number of Samples not yet read */
	public long availAble() {
		return Data.chunkSize / Format.FrameSize - Position;
	}

	/** Mark/reset is not supported by this Stream.
	 * @return always 0 */
	public long getMaxMarkSize() { return 0; }

	/**Public Method for other Classes to determine the minimum Value from the Stream	 */
	public double getMinDouble() { return Integer.MIN_VALUE; }

	/** No byte-order Flag is tracked by this Stream.
	 * @return always 0 */
	public byte getOrder() { return 0; }

	/** Returns the current Read Position.
	 * @return the current Position in the Stream, in Samples */
	public long getPosition() { return Position; }

	/** Reads and decodes the next Sample for {@link #Channel} according to {@link Format}'s {@code BitsPerSample}, advancing {@link #Position} and skipping the other Channels' Bytes in the Frame.
	 * @return the decoded Sample Value, or {@link #EOF} on an I/O Error */
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
			if (Format.BitsPerSample == 24) { //3 Bytes, low Word first, matching the Reader's little endian Order
				StreamIn.skipBytes(Format.FrameSize-3);
				return StreamIn.readUnsignedShort() + (StreamIn.readByte() << 16);
			}
		} catch (IOException x) {
		}
		return EOF; 
	}

}
