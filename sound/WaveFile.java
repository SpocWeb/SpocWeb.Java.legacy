/*
 * File Name: WaveFile.java
 * Created on: 03.01.2004
 *
 */
package sound;

import java.io.IOException;

import streamIO.integer.IStreamIn_Int;
import streamIO.integer.encoding.BigEndianReader;
import streamIO.real.StreamOutPlotter;
import stringOp.parser.IIStreamIn_Int;

/**
 * Title: WaveFile<p>
 * Description:
 * Class to encapsulate the Functionality to read and write 
 * Windows *.WAV Files
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
 * mtime: 2026-09-05T10:23:22Z
 * digest: 0f3c876d65d87f1ef31ee6fae9548fb8d266f72de4c8e6d2be6134027858b360
 * stale: false
 * tags: [code/audio, code/file_parsing]
 * concepts: [WAV Container]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class WaveFile 
extends RiffFile {
	
	/** Header Prefix indicating a WAVE File 	*/
	final static public String WAVE_HEADER = "WAVE"; 
	
	/** Header Prefix indicating a WAVE File Data Chunk  	*/
	final static public String DATA_HEADER = "data";
	
	/** Reference to the current Wave Format Chunk 	*/	
	final public WaveFormatChunk Format; 
	
	/** Reference to the current Wave Data Chunk 	*/	
	final public FileChunk data; 
	
	/** Reads the RIFF/WAVE Header followed by the "fmt " and "data" Chunks.
	 * @param streamIn_ the DataInput Implementation to use
	 * @throws IOException
	 */
	public WaveFile(BigEndianReader streamIn_) throws IOException {
		super(streamIn_, WAVE_HEADER);
		Format = new WaveFormatChunk(streamIn);
		data = new FileChunk(streamIn, DATA_HEADER);
	}

	/** Creates a Stream over one Channel's Samples in {@link #data}.
	 * @param channel the Channel Index to retrieve
	 * @return a new {@link WaveStreamIn} positioned at the start of the given Channel
	 */
	public WaveStreamIn getStream(int channel) {
		return new WaveStreamIn(Format, data, channel);
	}

	/** Demo Entry Point: reads each given WAV File's Channel 0, prints it to a Plotter, and (if no Arguments were given) re-encodes it to a "*.wav" Copy.
	 * @param args Paths of WAV Files to read; if empty, a hard-coded sample Path is used and re-written to a Copy
	 */
	public static void main(String[] args) throws IOException {
		String outPath = null; 
		if (args.length <= 0) {
			args = new String[]{"C:\\_\\_\\My Media\\Media\\Computer\\WAV\\AAH.WAV"};
			outPath = args[0]+".wav"; 
		}
		for(int i = args.length; --i>=0;) {
			System.out.println(args[i]); 
			WaveFile file = new WaveFile(new BigEndianReader(args[i])); 
			IStreamIn_Int stream = file.getStream(0);
			int maxVal = 1 << (file.Format.BitsPerSample -1); 
			StreamOutPlotter plotter = new StreamOutPlotter(System.out, -maxVal, maxVal, -1, true);
			WaveStreamOut streamOut = (outPath == null) ? null : 
				new WaveStreamOut(outPath
					, file.Format.SamplesPerSec
					, file.Format.BitsPerSample
					, file.data.chunkSize/(file.Format.FrameSize*file.Format.NumChannels)); 
			for(int value;(IIStreamIn_Int.EOF != (value = stream.nextInt())) || stream.isValid(); ) {
				plotter.addFloat(value); 
				//System.out.print(stream.getPosition()); System.out.print(": "); System.out.println(value);
				if (streamOut != null)
					streamOut.addInt(value);
			}
			if (streamOut != null)
				streamOut.close();
		}
		//return 0; 
	}
	
}
