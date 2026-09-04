package function.derive.neuron;

/**Abstract Interface for a Layer or a whole neuronal Network
 * that can be trained using a Back Propagation Mechanism
 * given the Differences between the desired and the real Output.	 */
public interface Slab
extends ISlab {

	/** Trains the Network to these OutputErrors = Expected - Outputs
	  * for the cached Input (relies on the last Input to be used!).
	  * Defaults the Values for the Learning and Momentum Factor	 */
	public float[] backProp(float[] OutputErrors);

	/** Trains the Network to these OutputErrors = Expected - Outputs
	  * for the cached Input  (relies on the last Input to be used!).
	  * So the Process is:
	  * backProp(Desired - getOutput(Input)).
	  *
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * OutputError * Input + alpha * dWeightOld
	  * @param  OutputErrors the Differences between the desired and the actual Output
	  * @param  beta  is the Learning Factor.
	  * @param  momentum creates Inertia to speed up Convergence.
	  * @return the BackPropagation Errors for use on the next Layer.  */
	public float[] backProp(float[] outputErrors, float momentum, float beta);

}
