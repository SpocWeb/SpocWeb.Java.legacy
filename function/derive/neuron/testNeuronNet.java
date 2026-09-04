package function.derive.neuron;

/** This class tests all the Methods of the neuronal Network
  * and can take a variable number of parameters on the command line.
  * Program execution begins with the main() method.
  * The class constructor is not invoked unless an object of type 'testNeuronNet'
  * created in the main() method.
  *
  * Hetero Associative Networks:
  * -Perceptron: single Layer connecting Input and Output
  *  can remember only linear Criteria.
  * -Multilayer Networks are for supervised Learning
  *  and can remember any Separation if only the inner Layers are large enough.
  *  They minimize (O'-O)^2 with O[j] = f(W[j][i]*I[i]) # W[j][i]*I[i]
  *  Learning Rule: dW[i,j] = ß * I[i]*(O'-O)[j]*f'(W*I) # ß*f'(W*I) * I°(I-WI)
  *  Operation: Presentation of a (noisy) Input restores the learned Output
  *
  * -Kohonen Network minimizes the same Function as the Perceptron:
  *  implements competitive Learning by introducing a topological Function
  *  for Interaction between Neurons. (can create associative Maps)
  *  Learning Rule:  dW[i,j] = ß*(I[i]-W[i,j]) for the maximum j and Neighbors
  *
  * Hetero Associative Networks:
  * -Hopfield minimizes the same Function, it is:	W[i,j] = W[j,i]
  *  since with O' = I: (O'-O)^2 # (I[j] - W[j][i]*I[i])^2
  *  							= 2I^2 - 2IWI = 2I^2(1-cos(I - W[i]))
  *  Operation: Initialize known Cells, activate other Cells until Minimum reached.
  *  Learning rule: dW[i,j] = ß * I[i]*I[j] (Hebb Rule)
  *  Use: restores a learned Pattern from only part of it.
  *
  * -BAM (bidirectional associative Memory) is a certain Operation Mode
  *  of the Hopfield Network:
  *  Use: To reproduce Associations between noisy Input and Output Data
  *  Learning Rule:  dW[i,j] = ß*I[i]*I[j]
  *  Operation: Input and Output Vector activate each other iteratively
  *  			until the Originally learned Vectors appear again.
  *
  *
  * A FAM (Fuzzy Associative Memory) is a BAM where the Scalar Product is replaced by
  */
public class testNeuronNet {

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		Layer	.testIt();
		Network	.testIt();
		Kohonen	.testIt();
		KohonenGraph.testIt();
	}

}
