package streamIO.real;

/** Interface for an Output streamIO of Float Numbers
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 2924a0a8134cccdaac73ae7934d1330d35952a49e71713ed5f1efdecc6b04d61
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Float Stream Output Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IStreamOutFloat {

	/** adds a single float Value to this Output streamIO
	 * @param value the Value to add 
	 * @return this Stream to allow for simple concatenated adding 
	 * or to indicate any Signals (like in @see streamIO.real.detector.DetectorThreshold)
	 * otherwise null or any lower Level Signals are returned. 
	 * An Exception would not be productive, because it skips ANY following PostProcessing! 
	 * On the other Hand this Model is too simple, because  
	 * a) it requires to test for the returned Object 
	 * b) higher-Level Events overwrite lower-Level Events 
	 * so it should rather be implemented using a Subscription Model.  
	 */
	public IStreamOutFloat addFloat(final float value); //{ return this; }
	
	/** adds a single double Value to this Output streamIO */
	public IStreamOutFloat addDouble(final double value); //{ return this; }

}
