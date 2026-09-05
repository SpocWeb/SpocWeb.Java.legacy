/*
 * File Name: WaveReader.java
 * Created on: 18.04.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package technology.sound;

import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import streamIO.integer.StreamIn_Arithmetic;
import streamIO.integer.adapter.StreamIn_ByteToInputStream;
import streamIO.integer.filter.FilterIn_Byte;
import function.IIntFunction;

/**
 * Demonstrates reading a WAV file with the Java Sound API and re-synthesizing a sine-wave
 * WAV file of raw PCM samples produced through {@link IIntFunction#Map(int)}.
 *
 * <p>Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:11:09Z
 * digest: 6356a8972f22a64c6b4e3863dcfbee58e39a9e2af31725b6b6234756587968d6
 * stale: false
 * tags: [code/audio]
 * concepts: [WAV File Reader]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class WaveReader 
implements IIntFunction {

	static final String filePath = "F:/Media/WAV/PICCOLO.WAV"; 
	static final String copyPath = "F:/Media/WAV/PICCOLO.COPY.WAV"; 
	static final File file = new File(filePath); 

	
	/**
	 * Reads the sample WAV file and writes a re-synthesized sine-wave copy alongside it.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) throws Exception {
		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
		AudioInputStream stream    = AudioSystem.getAudioInputStream(file);
		float sampleRate = 22000; 
		int sampleSizeInBits = 8; 
		int channels = 1; 
		int frameSize = 1;
		float frameRate = 22000;
		boolean bigEndian = false; 
		AudioFormat outFormat = new AudioFormat
		( AudioFormat.Encoding.PCM_SIGNED, sampleRate  
		, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
		//AudioFileFormat.Type outType = AudioFileFormat.Type.WAVE;
		long length = 200000; 
		InputStream outBytes 
		= new StreamIn_ByteToInputStream
		( new FilterIn_Byte
		( new StreamIn_Arithmetic(0, Long.MAX_VALUE, 1)
		, new WaveReader())); 
		AudioInputStream outStream = new AudioInputStream(outBytes, outFormat,length);
		AudioSystem.write(outStream, fileFormat.getType(), new File(copyPath)); 
		
		//AudioFormat format = stream.getFormat();
		int avail = stream.available();
		byte[] data = new byte[avail]; 
		stream.read(data); //Bulk Read is considerably faster!
/*		for(int val; (val = stream.read()) >= 0;) { //cannot read a single byte if frame size != 1
			System.out.println(val); 
		}
*/	}

	/**
	 * Always returns 0; the long-valued overload of this function is unused by this class.
	 *
	 * @see function.IIntFunction#Map(long)
	 */
	public long Map(final long value) {
		return 0;
	}

	/**
	 * Returns a sine-wave PCM sample, scaled to the range -127..127, for the given input index.
	 *
	 * @see function.IIntFunction#Map(int)
	 */
	public int Map(final int value) {
		double val = 127*Math.sin(value*Math.PI/32); 
		return (int) val;
	}
	
}
