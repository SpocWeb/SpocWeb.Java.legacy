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
	
	/**
	 * @param streamIn_
	 * @throws IOException
	 */
	public WaveFile(BigEndianReader streamIn_) throws IOException {
		super(streamIn_, WAVE_HEADER); 
		Format = new WaveFormatChunk(streamIn); 
		data = new FileChunk(streamIn, DATA_HEADER); 
	}
	
	public WaveStreamIn getStream(int channel) {
		return new WaveStreamIn(Format, data, channel); 
	}
	
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
			streamOut.close(); 
		}
		//return 0; 
	}
	
}
