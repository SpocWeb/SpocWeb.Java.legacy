/*
 * Created on 14.04.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.encoding.redundancy;

import java.io.IOException;
import java.io.OutputStream;

import streamIO.Assert;
import streamIO.StringBufferOutputStream;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.filter.FilterByte;
import streamIO.integer.filter.FilterOutByte;
import streamIO.integer.random.BitNoise;
import streamIO.integer.random.RandomQuick;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Undoes the Operations of the Repeater Class 
 * and uses the Redundancy in the Stream to eliminate Transmission Errors. 
 * The Majority of Bits set at a certain Position 
 * determines the Result taken. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see streamIO.integer.encoding.redundancy.Repeater 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public class Depeater 
extends FilterOutByte {
	
	/** either store only the erroneous Positions or better: the second sweep!	 */
	final byte[][] buf; 
	
	/** Reference to the current Buffer, 
	 * to avoid Indexing for Performance Reasons
	 */
	byte[] currBuf; 
	
	/** the current Sweep of Data starting with 0	 */
	int sweep; 
	
	/** the Length of the Buffer to use	 */
	int length; 
	
	/** the current position in the Buffer	 */
	int pos = -1; 
	
	/** the Number of Bytes in the Buffer, that were already propagated after the second Pass, 
	 * due to identity with the first Pass.	 */
	int alreadyWritten; 
	
	/**
	 * @param _streamOut
	 * @param bufferSize
	 */
	public Depeater(final IStreamOutByte _streamOut, final int _bufferSize) {
		super(_streamOut);
		buf = new byte[2][length = _bufferSize];
		currBuf =  buf[0]; 
	}
	
	/**
	 * @param _streamOut
	 * @param bufferSize
	 */
	public Depeater(final OutputStream _streamOut, final int _bufferSize) {
		super(_streamOut);
		buf = new byte[2][length = _bufferSize]; 
		currBuf =  buf[0]; 
	}
	
	/**
	  * Writes the specified byte to the Output Stream, 
	  * after it received it three times. 
	  * @param b - the byte to write.
	  */
	public void write(final int b) throws IOException {
		if (++pos < length) { //buf[0].//buf[sweep].
			if  ((alreadyWritten == pos) && (sweep == 1) && (b == buf[0][pos])) {
				++alreadyWritten; super.write(b); //start writing in the 2nd Pass! 
			} else 
				currBuf[pos] = (byte) b; 
		} else {
			if (++sweep < buf.length) {
				currBuf = buf[sweep]; pos = -1; 
				write(b); 
			} else { //third row... output the Data
				final int ndx = pos-length; 
				if (ndx >= length) { //start new
					currBuf = buf[sweep = 0]; pos = -1; alreadyWritten = 0; 
					write(b); 
					return; 
				}
				if(ndx >= alreadyWritten) {
					if ((buf[0][ndx] == b) ||
						(buf[1][ndx] == b))
						super.write(b); 
					else if  (buf[0][ndx] == buf[1][ndx]) //not necessary 
						super.write(buf[0][ndx]); //when already written in 2nd Pass. 
					else if (throwExceptionIfAllBytesDifferent) 
						throw new IOException("All three Bytes are different!"); 
					else //recover individual Bits
						super.write(MAJORITY_VOTE(buf[0][ndx], buf[1][ndx], b)); 
				}
			}
		}
	}
	
	/** Flag whether to throw an Exception if all three Bytes are different. 
	 * alternatively the Bytes are merged Bit by Bit. 
	 */
	public boolean throwExceptionIfAllBytesDifferent = false;  
	
	/** Exploits the Redundancy of three times the same Value to eliminate individual Bit Flips. 
	 * The Number of Bits to compare is defaulted to 31. 
	 * @return a Number with the Bits at every Position set 
	 * exactly if two or three of the incoming Values' Bits are set.  
	 */
	public static final int MAJORITY_VOTE(final int b1, final int b2, final int b3) {
		return MAJORITY_VOTE(b1, b2, b3, (byte) 0); }
	
	/** Exploits the Redundancy of three times the same Value to eliminate individual Bit Flips. 
	 * @param numBits the Number of Bits to compare, defaulted to 31, 
	 * since the last Bit is not considered here (Overflow etc.)  
	 * @return a Number with the Bits at every Position set 
	 * exactly if two or three of the incoming Values' Bits are set.  
	 */
	public static final int MAJORITY_VOTE(final int b1, final int b2, final int b3, byte numBits) {
		if (numBits <= 0)
			numBits  = 31; 
		int mask0 = 1; 
		int mask1 = 2; 
		int ret  = ((b1&1) + (b2&1) + (b3&1)) & mask1; //0; 
		for(;--numBits > 0;) {
			mask0 = mask1; mask1 <<= 1; 
			ret += ((b1&mask0) + (b2&mask0) + (b3&mask0)) & mask1;
		}
		return ret >> 1; }
	
	/** indicates the End of Transmission	 */
	public void flush() { length = pos+1; }
	
	///////////////////////////////////////////////////////////////////////////
	/// 
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) throws Exception {
		testIt(); 
	}
	
	public static void testIt() throws Exception {
		testStream(); 
		testMajorityVote(29734); 
	}
	
	public static void testStream() throws Exception {
		final StringBuffer str = new StringBuffer("Hello World!"); 
		testStream(str.toString()); str.setLength(str.length()-1); 
		testStream(str.toString()); str.setLength(str.length()-1); 
		testStream(str.toString()); str.setLength(str.length()-1); 
		testStream(str.toString()); str.setLength(str.length()-1); 
	}
	
	public static void testStream(final String str) throws Exception {
		final StringBufferOutputStream os = new StringBufferOutputStream(); 
		final Depeater Depeater = new Depeater((IStreamOutByte)os, 3); 
		final FilterByte noisy  = new FilterByte(Depeater, 
								  new BitNoise(RandomQuick.RANDOM.randomize(), 29, 8)); 
		final Repeater Repeater = new Repeater(noisy, 3); 
		for(int i = -1; ++i < str.length();) 
			Repeater.write(str.charAt(i)); 
		Repeater.close(); 
		Assert.EQUALS(str, os.toString()); 
	}
	
	public static void testMajorityVote(final int num) {
		Assert.EQUALS(num, MAJORITY_VOTE(num, num, num)); 
		Assert.EQUALS(num, MAJORITY_VOTE(num, num, 0)); 
		Assert.EQUALS(num, MAJORITY_VOTE(num ^ 128, num, num ^ 1)); 
	}
	
}
