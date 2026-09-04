package streamIO.object;

import streamIO.AStreamOut;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;

/**
 * MultiplexerOut.java 
 * The MultiplexerOut is derived from the abstract Base Class AStreamOut
 * and multiplexes this Output StreamIO to a List of Output Streams 
 * in a Round Robin Fashion (RAID0, Striping), 
 * which can be used to speed up Processing in parallel Processors. 
 * This controlled Round Robin Algorithm is only possible 
 * by actively writing to a Set of StreamIn Objects. 
 * Uncontrolled, passive Multiplexing is performed by any IStreamIn, 
 * when any Number of Clients concurrently read from it.
 * 
 * This is equivalent to the Division of a Set by another Set, 
 * creating Pairs of (Object, StreamOut) 
 *
 * Any StreamOut can also be used as a DeMultiplexer
 * by just connecting several Processes, Threads etc. to it
 * 
 * @see streamIO.Object.DeMultiplexerIn which is an Input Stream 
 * that collects it's Elements from a Set of Streams in Round Robin Fashion. 
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
 */
public class MultiplexerOut
extends AStreamOut {

	////////////////////////////////////////////////////////////////////////////
	//  Members
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the actual Output Streams */
	protected final IStreamOut[] forwards;

	/** Number of the current Output streamIO */
	protected int currStreamOut;

	////////////////////////////////////////////////////////////////////////////
	//  Constructor
	////////////////////////////////////////////////////////////////////////////

	/** Creates new FilterStore */
	public MultiplexerOut (final IStreamOut[] Forwards) { this.forwards = Forwards; }

	////////////////////////////////////////////////////////////////////////////
	//  Delegation
	////////////////////////////////////////////////////////////////////////////

	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * If the StreamOut had an available() Method to determine it's Capacity,
	  * you could distribute the Load before blocking 	  */
	public IIStreamOut addItem(Object arg) {
		boolean loop = false;
		while (true) { //try it until it reaches a full loop!
			if (++currStreamOut >= forwards.length) {
				if (loop) {
					return null; } //after a full loop, give it up!
				loop = true; 
				currStreamOut = 0; }//Modulus Increment
			if (null != forwards[currStreamOut].addItem(arg))
				return this; }
	}

}
