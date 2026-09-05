/*
 * Created on 23.01.2005
 *
 * multiplexes this StreamOutByte to a List of Output StreamOutByte  
 */
package streamIO.integer.multiplex;

import java.io.IOException;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamOutByte;

/**
 * The MultiplexerOutRaid0 is derived from the abstract Base Class AStreamOutByte
 * and multiplexes this StreamOutByte to a List of Output StreamOutByte  
 * in a Round Robin Fashion (RAID0, Striping), 
 * which can be used to speed up Processing in parallel Processors. 
 * This is the RAID 0 Way to increase both writing Speed AND Capacity. 
 * This controlled Round Robin Algorithm is only possible 
 * by actively writing to a Set of StreamIn Objects. 
 * Uncontrolled, passive Multiplexing is performed by any IStreamIn, 
 * when any Number of Clients concurrently read from it. 
 * 
 * This is equivalent to the Division of a Set by another Set, 
 * creating Pairs of (Object, StreamOut) 
 *
 * Any StreamOut can also be used as a DeMultiplexer
 * by just connecting several Processes, Threads etc. to it, but the Demultiplexing is not controlled,
 * since some Streams could transport their Information faster than others.  
 * 
 * @see streamIO.Object.DeMultiplexerIn which is an Input Stream 
 * that collects it's Elements in a controlled Manner from a Set of Streams in Round Robin Fashion. 
 * 
 * Calling only one out of N Streams is a similar, but different Behavior to the
 * 
 * @see streamIO.Object.Enumerator.Container.EventMultiCaster
 * which calls ALL Elements of the List, but can be stopped.
 * Additionally the Observers there can be dynamically added / removed,
 * so the simple Array Implementation has to be replaced.
 * 
 * @see streamIO.integer.multiplex.MultiplexerOutRaid5 which implements a redundant System of Streams. 
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
public class MultiplexerOutRaid0 
extends AStreamOutByte {

	/** Runs {@link DeMultiplexerIn_Raid0#testIt()} when invoked with no arguments. */
	public static void main(final String[] args) throws IOException {
		if (args.length == 0)
			DeMultiplexerIn_Raid0.testIt();
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the actual Output Streams */
	protected final IStreamOutByte[] forwards;

	/** Number of the current Output streamIO */
	protected int currStreamOut;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////

	/** Creates new FilterStore */
	public MultiplexerOutRaid0 (final IStreamOutByte[] _forwards) { 
		this.forwards = _forwards; 
	}

	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** Writes the byte to the next Output Stream in Round-Robin order.
	 * @see streamIO.integer.IStreamOutByte#write(int)	 */
	public void write(final int b) throws IOException {
		if(++currStreamOut >= forwards.length) {
			 currStreamOut  = 0; }//Modulus Increment
		forwards[currStreamOut].write(b); 
	}

	/** Flushes every Output Stream, collecting (not stopping on) any Exceptions.
	 * @see streamIO.integer.IStreamOutByte#flush()	 */
	public void flush() throws IOException {
		String innerExceptions = ""; 
		for(int i = forwards.length; --i >= 0;) 
			try { //flush as much as possible! 
				forwards[i].flush();
			} catch (Exception x) {
				innerExceptions += x.toString()+"\n\n"; 
			}
		if (innerExceptions.length() > 0) 
			throw new IOException(innerExceptions);
	}

	/** Closes every Output Stream, collecting (not stopping on) any Exceptions.
	 * @see streamIO.integer.IStreamOutByte#close()	 */
	public void close() throws IOException {
		String innerExceptions = ""; 
		for(int i = forwards.length; --i >= 0;) 
			try { //flush as much as possible! 
				forwards[i].close(); 
			} catch (Exception x) {
				innerExceptions += x.toString()+"\n\n"; 
			}
		if (innerExceptions.length() > 0) 
			throw new IOException(innerExceptions);
	}

}
