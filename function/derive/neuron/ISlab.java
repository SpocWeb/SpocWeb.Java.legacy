package function.derive.neuron;

import function.IFunction;

/** Abstract Interface for a Layer or a whole neuronal Network
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 594e4e9a2f32a6d85eec97437226439cff44dfd2375fb0788ded61c471952c12
 * stale: false
 * tags: [code/neural_network, code/function_contract]
 * concepts: [Neural Networks]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
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
