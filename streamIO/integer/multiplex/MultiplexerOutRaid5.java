/*
 * Created on 23.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer.multiplex;

import java.io.IOException;

import streamIO.integer.IStreamOutByte;

/**
 * @author heuerm
 *
 * @see streamIO.object.MultiplexerOut which is effectively a RAID0 (Striping)
 * @see streamIO.Object.Enumerator.Container.EventMultiCaster 
 * 		which mirrors Streams in RAID1 Fashion
 * RAID 3 is a simpler Version with a fixed Parity Stream, 
 * 		which unfortunately has to take full Load. 
 * RAID 5 uses the next Round Robin Stream to store the Parity
 * 		thus distributing the Load evenly.  
 * 
 * The RAID 5 Way to increase both writing Speed AND preserve most Capacity is 
 * to write each Block twice: 
 * once unchanged and once XORed with another block on a different Disk. 
 * By distributing the XORed Blocks to all Disks no single Bottleneck appears. 
 */
public class MultiplexerOutRaid5 
extends MultiplexerOutRaid0 {

	public static void main(final String[] args) throws IOException {
		if (args.length == 0)
			DeMultiplexerIn_Raid5.testIt(); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** current Output Value */
	protected int currItem; 

	/** Flag for the current Output  */
	protected boolean currItemSet; 

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

	/**
	 * @param _forwards
	 */
	public MultiplexerOutRaid5(IStreamOutByte[] _forwards) {
		super(_forwards);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** @see streamIO.integer.IStreamOutByte#write(int)	 */
	private Exception writeTolerantly(final int b, final Exception previous) throws IOException {
		try { //quite Expensive to enter a Catch Block ...
			super.write(b);
			return previous; 
		} catch (Exception x) { //...but even more expensive to throw and catch an Exception. 
			//Log.L(x);
			if (previous == null)
			return x; 
			throw new IOException("Double Failure:\nfirst:"+previous+"\nsecond:"+x);
		}
	}
	
	/** write the last Byte out	 */
	public void close() throws IOException {
		if (currItemSet)
			write(-1);
		super.close();
	}
	
	/** @see streamIO.integer.IStreamOutByte#write(int)	 */
	public void write(final int b) throws IOException {
		//Log.N("Value:"+b);
		if (currItemSet = !currItemSet) {
			currItem = b; 
			return;
		}
		Exception previous = null;
		previous = writeTolerantly(  currItem, previous); 
		previous = writeTolerantly(b         , previous); 
		previous = writeTolerantly(b^currItem, previous); //as Parity
	}

}
