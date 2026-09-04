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
 * Title: WaveReader<p>
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
public class WaveReader 
implements IIntFunction {

	static final String filePath = "F:/Media/WAV/PICCOLO.WAV"; 
	static final String copyPath = "F:/Media/WAV/PICCOLO.COPY.WAV"; 
	static final File file = new File(filePath); 

	
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
	 * @see function.IIntFunction#Map(long)
	 */
	public long Map(final long value) {
		return 0;
	}

	/**
	 * @see function.IIntFunction#Map(int)
	 */
	public int Map(final int value) {
		double val = 127*Math.sin(value*Math.PI/32); 
		return (int) val;
	}
	
}
