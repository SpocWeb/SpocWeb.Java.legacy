package function.derive.neuron;

import function.IFunction;

/** Abstract Interface for a Layer or a whole neuronal Network	 */
public interface ISlab
extends IFunction {

	/** Randomizes all the Weights of this Slab
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * This is necessary to start self Organization
	  * without predefining the Weights explicitly. 	 */
	public void randomizeWeights();

	/**Requests the last Output
	 * This Result may have been modified by external Code.	 */
	public float[] getOutput();

	/**Requests the Output for this Input
	 * Does only a forward Propagation and returns the Result.	 */
	public float[] getOutput(float[] Input);

}
