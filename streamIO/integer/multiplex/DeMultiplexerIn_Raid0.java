/*
 * Created on 23.01.2005
 *
 * de-multiplexes this Input stream from a List of Input Streams 
 */
package streamIO.integer.multiplex;

import java.io.IOException;

import streamIO.Assert;
import streamIO.IReSetAble;
import streamIO.integer.AStreamIn_Byte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.StreamIn_Arithmetic;
import streamIO.integer.pipe.ByteStreamerThread;
import streamIO.integer.pipe.MonitorByte;
import tools.IOError;

/**
 * The DeMultiplexerInRaid0 is derived from the abstract Base Class AStreamIn_Byte
 * and de-multiplexes this Input stream from a List of Input Streams 
 * in a Round Robin Fashion. 
 * This is the RAID 0 Way to increase both writing Speed AND Capacity. 
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
 * @see Merger, which merges two sorted Input Streams into a new one.
 * Any StreamIn can also be used as a DeMultiplexer
 * by just having several Processes, Threads etc. writing to it
 *
 * Created on 26. Mai 2001, 22:08
 *
 * @author  Matthias Heuer
 * @version
 */
public class DeMultiplexerIn_Raid0 
extends AStreamIn_Byte {

	/**
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
	final static public MonitorByte[] getMonitors(final int numStreams, final long timeOutWrite, final long timeOutRead) {
		final MonitorByte[] monitors = new MonitorByte[numStreams]; 
		for (int i = 0; i < monitors.length; i++) 
			monitors[i] = new MonitorByte(timeOutWrite, timeOutRead); 
		return monitors; 
	}
	
	/**
	 * Tests correct De-Multiplexing 
	 * by concurrently reading from and writing to the multiplexed Streams. 
	 * @param args
	 */
	protected static void testMultiplexing
			( IStreamOutByte multiplexer
			, IStreamIn_Byte deMultiplexer) throws IOException {
		final int startValue = -126; //must be uneven!
		final int stop_Value =  127; //must be uneven!
		ByteStreamerThread streamer = new ByteStreamerThread(new StreamIn_Arithmetic(startValue, stop_Value), multiplexer);
		streamer.closeStreamOutOnFinish = true;
		streamer.start(); //start writing from the Stream into the Monitor
		//now read from the Monitor with some Delay...
		for(int val, i = startValue; i < stop_Value; ++i) {
			val = deMultiplexer.read(); 
			Assert.EQUALS(i, val); 
		}
	}
	
	/**
	 * Tests correct De-Multiplexing 
	 * by concurrently reading from and writing to the multiplexed Streams. 
	 * @param args
	 */
	public static void testIt() throws IOException {
		final MonitorByte[] monitors = getMonitors(10, 1000, 1000); 
		testMultiplexing (
				new   MultiplexerOutRaid0(monitors),
				new DeMultiplexerIn_Raid0(monitors)); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the actual Output Streams */
	protected final IStreamIn_Byte[] sources;
	
	/** current Output Object, originally defined in AStreamIn */
	protected int currItem;
	
	/** Number of the current Input stream */
	protected int currInStream;
	
	/** Number of the marked Input stream */
	protected int markInStream;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////
	
	/** Creates new DeMultiplexerIn */
	public DeMultiplexerIn_Raid0(final IStreamIn_Byte[] _sources) { 
		this.sources = _sources; }
	
	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the (minimum) Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 *
	 * Nearly equivalent is currItem != null
	 * (when the Container does not contain null Entries, like e.g. HashTables)
	 * @see streamIO.integer.IStreamIn_Byte#available()
	 */
	public int available() throws IOException {
		if (currItem < 0) 
			return -1;
		return sources[currInStream].available(); }
	
	/**Returns the next Object (Parent) of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 * @see streamIO.integer.IStreamIn_Byte#read()
	 */
	public int read() throws IOException {
		if(++currInStream >= sources.length) {
			 currInStream  = 0; } //Modulus Increment
		return currItem = sources[currInStream].read();
	}
	
	/** @see streamIO.integer.IStreamIn_Byte#close()	 */
	public void close() throws IOException {
		for (int i = sources.length; --i >= 0; )
			sources[i].close();
	}
	
	/** @see streamIO.integer.IStreamIn_Byte#mark(int)	 */
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
	
	/** @see streamIO.integer.IStreamIn_Byte#reSet(long)	 */
	public long reSet(long position) { //throws IOException {
		final long roundlimit = position / sources.length;
		for (int i = sources.length; --i >= 0; )
			sources[i].reSet(roundlimit);
		try {
			for(long i = position; --i >= roundlimit*sources.length; ) 
				read(); 
		} catch (final IOException x) {
		    throw new IOError(x); 
		}
		return position; 
	}
	
	/**@see streamIO.IMarkAble#getMaxMarkSize() 
	 * @see streamIO.integer.IStreamIn_Byte#getMaxMarkSize()	 */
	public long getMaxMarkSize() {
	    long minSupported = Long.MAX_VALUE; 
		for (int i = sources.length; --i >= 0; ) {
		    final long nextSupported = sources[i].getMaxMarkSize(); 
		    if (minSupported > nextSupported)
		        minSupported = nextSupported; 
		}
		return minSupported; 
	}
	
	/** @see streamIO.integer.IStreamIn_Int#getOrder()	 */
	public byte getOrder() { return ORDER_NONE; }

	////////////////////////////////////////////////////////////////////////////
	//  Optimization:
	////////////////////////////////////////////////////////////////////////////

	/** current Output Object, originally defined in AStreamIn */
	public int currInt() { return currItem; }

	/** @see streamIO.integer.AStreamIn_Byte#getPosition()	 */
	public long getPosition() {
		final long ret = sources[currInStream].getPosition()*sources.length+currInStream; 
		return ret;
	}

}
