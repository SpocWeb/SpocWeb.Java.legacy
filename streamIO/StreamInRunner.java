package streamIO; //Testers.Process;

import streamIO.integer.filter.LimitedSizeOutputStream;
import streamIO.object.backTrack.BackTracker;
import streamIO.object.enumer.container.AContainer;
import tester.process.StreamProcessor;

//import Stream.IStreamIn;

/** Abstract Processor just performing the nextItem() Operation
  * in either single Step or until no Items are available anymore. */
public class StreamInRunner
implements Runnable {

	/**
	  * Streams the whole Contents of the IStreamIn through the Automaton and into the Object Output streamIO.
	  * @see BackTracker.operate
	  * @see StreamProcessor.run
	  * @see ProcessorRunner.run
	  * @see LimitedSizeOutputStream.stream for Bytes
	  * @see AContainer.copyAt for creating Copies of the Items
	  * @see AStreamOut.stream for fast streaming
	  * @see AStreamOut.add for flattening 	 */
	public static void stream(IIStreamIn In, boolean streamNulls, boolean singleStep, IIStreamOut Out ) {
		for(Object obj; (IIStreamIn.EOI != (obj = In.nextItem())) || In.isValid();) {
			if (streamNulls || (Out != null)) {
				Out.addItem(obj); }
			if (singleStep ) {
				break; }
		}
	}

	/** Switch between continuous and single Step Mode
	  * Made public to allow for concurrent Alteration
	  */
	public boolean singleStep;

	/** Flag to filter out Nulls
	  * Made public to allow for concurrent Alteration
	  */
	public boolean streamNulls;

	/** Reference to the actual Processor performing the Operations. */
	protected IIStreamIn mProcessor;

	/**
	 * Single Step Operation of this Processor Automaton using the Input and Output Streams.
	 * Performs both State Transition and calculation of the Output Function.
	 * If the Transition Function is null, the identical Mapping is assumed.
	 * If the Output Function is null, the State is returned.
	 */
	public StreamInRunner(IIStreamIn P) {
		mProcessor = P; }

	/**
	 * Streams the whole Contents of the IStreamIn through the Automaton and into the Object Output streamIO.
	 * This is analogous to the stream() Method in Class 'LimitedSizeOutputStream'
	 */
	public void run() { stream(mProcessor, streamNulls, singleStep, null); } //Runs the Operation of the Processor once or iterated...

}
