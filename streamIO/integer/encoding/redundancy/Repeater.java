/*
 * Created on 14.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.encoding.redundancy;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterOutByte;

/**
 * Adds Redundancy to a Stream of Bytes
 * by repeating a Group an uneven Time (typ. 3 times). 
 * This allows the corresponding Depeater Class to correct Transmission Errors. 
 * This can be used both in Presence of Gaussian Noise 
 * as well as total Interruptions, 
 * as long as the Block Size is longer than the Interruption. 
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
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:40:37Z
 * digest: f49209d5d63e91347d58ba8738b3604921d40b36e2fa1b9bc32cd7a48538d130
 * stale: false
 * tags: [code/error_correction, code/convolutional_encoding]
 * concepts: [Forward Error Correction Codecs - Repetition and Convolutional Encoding]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Repeater 
extends FilterOutByte {
	
	/** Buffer to cache the Data until it is ready to be resent	 */
	final byte[] buf; 
	
	/** current position in the Buffer	 */
	int pos = -1; 
	
	/**
	 * Creates a filter that buffers up to {@code bufferSize} bytes before repeating them.
	 * @param _streamOut
	 */
	public Repeater(final IStreamOutByte _streamOut, final int bufferSize) {
		super(_streamOut);
		buf = new byte[bufferSize];
	}

	/**
	 * Creates a filter that buffers up to {@code bufferSize} bytes before repeating them.
	 * @param _streamOut
	 */
	public Repeater(final OutputStream _streamOut, final int bufferSize) {
		super(_streamOut);
		buf = new byte[bufferSize]; 
	}
	
	/**
	  * Writes the specified byte to this output stream three times.
	  * The first time is done right away to increase Performance. 
	  * The second and third time are done when the Buffer is full. 
	  *
	  * @param b - the byte to write.
	  */
	public void write(final int b) throws IOException {
		//if (b < 0) //close the Stream, flush the Buffer
		if (++pos < buf.length) {
			super.write(b); //write the first Iteration directly 
			buf[pos] = (byte) b; 
		} else {
			for(int i = 2; --i >= 0;) 
				write(buf);
			pos = -1; write(b); 
		}
	}
	
	/** flushes the whole Buffer and closes the downStream	 */
	public void close() throws IOException {
		super.flush(); //flush the OutStream to indicate the End
		for(int i = 2; --i >= 0;) 
			write(buf, 0, pos+1); 
		super.close(); //close the OutStream 
	}
	
	/**
	 * 
	 */
	//protected Repeater() { super(); }
	
	/** Currently runs no demonstration; present as an entry point for manual testing. */
	public static void main(final String[] args) throws Exception {
	}
}
