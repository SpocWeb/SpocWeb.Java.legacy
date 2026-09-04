package tester.process;

import streamIO.AStreamOut;
import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IStreamOut;
import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;

/**
 * StreamProcessor.java
 * Processes an Input Object streamIO
 * and writes it to an Output Object streamIO.
 * Either in single Step or in streaming Mode.
 *
 * Design Decisions:
 * Instead of deriving from Automaton, it could also use an Automaton.
 * There doesn't seem to be much of a Difference
 *
 * Created on 26. Mai 2001, 16:07
 *
 * @author  Matthias Heuer
 * @version
 */
public class StreamProcessor
extends Automaton
implements IAvailAble, Runnable {
	
	////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//	Members
	////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the InPut Object streamIO 	*/
	protected IIStreamIn In;
	
	/** Reference to the OutPut Object streamIO 	*/
	protected IStreamOut Out;
	
	/** Switch between continuous and single Step Mode
	  * Made public to allow for concurrent Alteration
	  */
	public boolean singleStep;
	
	////////////////////////////////////////////////////////////////////////////
	//	Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructor taking the Transition Function and the Output Function
	  * as well as the InPut- and OutPut- streamIO.
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public StreamProcessor(Operator Lambda, Operator Trans, IIStreamIn In, IStreamOut Out) {
		super(Lambda, Trans);
		this.Out= Out;
		this.In = In;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Single Step Operation of this Automaton using the Input and Output Streams.
	  * Performs both State Transition and calculation of the Output Function.
	  * If the Transition Function is null, the identical Mapping is assumed.
	  * If the Output Function is null, the State is returned. 	 */
	public Object nextItem() {
		final Object ret = Map(In.nextItem());
		Out.addItem(ret);
		return ret; }
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number.
	  */
	public long availAble() { return ((IAvailAble)In).availAble(); }
	
	/** @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return ((IAvailAble)In).availAble(); }

	/** @see streamIO.IIStreamIn#isEmpty()	 */
	public boolean isValid() { return In.isValid(); }
	
	/** Streams the whole Contents of the IStreamIn through the Automaton
	  * and into the Object Output streamIO.
	  * @see BackTracker.operate
	  * @see StreamProcessor.run
	  * @see ProcessorRunner.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening 	 */
	public void run() { //as long as there is Data...
		while ((IIStreamIn.EOI != nextItem()) || In.isValid())
			if (singleStep)
				break;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests all Methods of this Class	 */
	public static void testIt(String[] args) {
		System.out.println("Testing " + StreamProcessor.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }
	
}
