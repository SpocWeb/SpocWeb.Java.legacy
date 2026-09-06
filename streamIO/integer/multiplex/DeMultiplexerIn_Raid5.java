/*
 * Created on 23.01.2005
 *
 * de-multiplexes this Input stream from a List of Input Streams 
 * encoded in Raid 5 Fashion to be able to reconstruct the Stream 
 * from the others if one fails. 
 */
package streamIO.integer.multiplex;

import java.io.IOException;

import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.pipe.MonitorByte;

/**
 * The DeMultiplexerInRaid0 is derived from the abstract Base Class AStreamIn_Byte
 * and de-multiplexes this Input stream from a List of Input Streams 
 * in a Round Robin Fashion. 
 * The RAID 5 Way to increase both writing Speed AND preserve most Capacity is 
 * to write each Block twice: 
 * once unchanged and once XORed with another block on a different Disk. 
 * By distributing the XORed Blocks to all Disks no single Bottleneck appears. 
 * This controlled Round Robin Algorithm is only possible 
 * by actively reading from a Set of StreamIn Objects. 
 * Uncontrolled, passive Demultiplexing is performed by any IStreamOut, 
 * when any Number of Clients write to it concurrently. 
 * 
 * Instead of appending an (infinite) Number of finite Input Streams like in
 * @see Union, this Class interleaves a fixed Number (thus an Array!)
 * of (possibly infinite) Input Streams
 * which cannot be done recursively by (de-) multiplexing two Streams,
 * except for a binary Powers of streamIO Numbers:
 * merge A,B into C multiplexing two  Streams
 * merge X,Y into Z multiplexing two  Streams
 * merge C,Z into O multiplexing four Streams
 * etc. giving a,x,b,y,a,x,b,y,...
 *
 * or by accepting a mixed Frequency of Elements:
 * merge A,B into C multiplexing two  Streams
 * merge C,D into E multiplexing two  Streams giving a,d,b,d,a,d,b,d,...
 * 
 * @see streamIO.object.MultiplexerOut 
 * @see streamIO.object.Union which 
 *
 * @see streamIO.object.Merger, which merges two sorted Input Streams into a new one.
 * Any StreamIn can also be used as a DeMultiplexer
 * by just having several Processes, Threads etc. writing to it
 *
 * Created on 26. Mai 2001, 22:08
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * tags: [code/multiplexer, code/multiplexing, code/raid_encoding]
 * concepts: [RAID-Style Stream Multiplexing plus Markov/Viterbi Math]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class DeMultiplexerIn_Raid5 
extends DeMultiplexerIn_Raid0 {

	/** Runs {@link #testIt()} when invoked with no arguments.
	 * @param args
	 */
	public static void main(final String[] args) throws IOException {
		if (args.length == 0)
			testIt(); 
	}
	
	/**
	 * Tests correct De-Multiplexing 
	 * by concurrently reading from and writing to the multiplexed Streams. 
	 * @param args
	 */
	public static void testIt() throws IOException {
		System.out.println((int)(char) -50000);
		testIt( 5, -1, 1000, 1010); //
		testIt(-1, -1, 1000, 1000);
		testIt(-1,  5, 1010, 1000); 
		testIt( 5,  5, 1000, 1000);
		Log.N("Testing Error Detection:"); 
	}
	
	/**
	 * Tests correct De-Multiplexing 
	 * by concurrently reading from and writing to the multiplexed Streams. 
	 * @param args
	 */
	public static void testIt(
			final int in_Failure, final int outFailure, 
			final long timeOutWrite, final long timeOutRead) throws IOException {
		MonitorByte[] monitorsIn_;
		MonitorByte[] monitorsOut;
		monitorsIn_ = getMonitors(10, timeOutWrite, timeOutRead);
		monitorsOut = (MonitorByte[]) monitorsIn_.clone();  
		if (in_Failure >= 0) 
			monitorsIn_[in_Failure] = null; 
		if (outFailure >= 0) 
			monitorsOut[outFailure] = null; 
		Log.N("testing with missing In-Stream#"+in_Failure+" \n and Out-Stream#"+outFailure); 
		testMultiplexing(
				new   MultiplexerOutRaid5(monitorsOut), 
				new DeMultiplexerIn_Raid5(monitorsIn_)); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** next Output Object */
	protected int nextItem;

	/** next Output Object */
	protected boolean nextItemSet = true;

	/** marked Input stream */
	protected int markedNextItem;

	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////

	/** Creates new DeMultiplexerIn */
	public DeMultiplexerIn_Raid5(final IStreamIn_Byte[] _sources) { 
		super(_sources); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 * @see streamIO.integer.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		if (nextItemSet = !nextItemSet) 
			return currItem = nextItem;
		int thisItem;
		int parity;
		try { thisItem = super.read();
		} catch (Exception x) { //second Exception is not caught!
			//Log.L(x); 
			nextItem = super.read(); 
			parity   = super.read(); 
			return this.currItem = nextItem ^ parity; //restore thisItem  
		}
		try { nextItem = super.read(); 
		} catch (Exception x) { //second Exception is not caught!
			//Log.L(x); 
			parity   = super.read(); 
			this.nextItem = thisItem ^ parity; //restore nextItem  
			return this.currItem = thisItem; 
		}
		try { parity   = super.read(); 
		} catch (Exception x) { 
			//Log.L(x); 
			return this.currItem = thisItem; 
		} //first Exception is ignored!
		if ((thisItem ^ nextItem) != parity)
			throw new IOException("Transmission Error: Expected:"+parity+" actual:"+(thisItem ^ nextItem));
		return this.currItem = thisItem; 
	}

	/** Marks every source Stream, dividing the read limit evenly across them.
	 * @see streamIO.integer.IStreamIn_Byte#mark(int)	 */
	public void mark(final int readlimit) {
		markInStream = currInStream; 
		final int roundlimit = 1 + (readlimit / sources.length);
		for (int i = sources.length; --i >= 0; )
			sources[i].mark(roundlimit);
	}

	/**Resets the Iterator to the given Position
	 * counted from the last marked Position.	 */
	public IReSetAble reSet() { //throws IOException {
		for (int i = sources.length; --i >= 0; )
			sources[i].reSet();
		currInStream = markInStream; 
		return this; 
	}

}
