package function.derive.neuron;

import math.matrix.MatrixFloat;
import function.AFunction;

/**
  * Contains all the Variables and Methods shared
  * between a normal Layer and a Kohonen Layer
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:31:20Z
  * digest: 51e49a08e51df1c924c5ebccfbc501bb5cc0e6bdd61b64de66d2ede870e24d14
  * stale: false
  * tags: [code/neural_network, code/backpropagation]
  * concepts: [Neural Networks]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class ASlab
extends AFunction
implements ISlab {
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants, Variables, Defaults
	////////////////////////////////////////////////////////////////////////////
	
	/**Default Start Value for the Momentum Factor	 */
	public static float DEFAULT_MOMENTUM = 0.9f;

	/**Default Start Value for the Learning Factor	 */
	public static float DEFAULT_BETA = 0.6f;
	
	////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Weight Matrix stored in a simple Array for faster Access
	  * using a single Index from both Sides (Input Values and Error)	 */
	protected float[][] weights;

	/** Delta Weight Matrix stored in a simple Array for faster Access
	  * using a single Index from both Sides (Input Values and Error)
	  * Contains the last Changes in the Weight to simulate an Impulse.	 */
	protected float[][] dWeight;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor,
	  * also randomizes the Weights.	 */
	public ASlab(final  int inputDim, final int outputDim) {
		weights	= new float[inputDim][outputDim];
		dWeight = new float[inputDim][outputDim];
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Randomizes all the Weights of this Slab
	  * by initializing it with Weights uniformly distributed between [-1, +1]
	  * Assumes a rectangular Array. 	 */
	public void randomizeWeights() { MatrixFloat.RANDOMIZE_AT_1_1(weights); }

	/** Trains the Network to the expected Output for the given Input
	  * Defaults the Values for the Learning and Momentum Factor	 */
	public static float[] setOutput(final Slab slab, final float[] inputs, final float[] expected) {
		return setOutput(slab, inputs, expected, DEFAULT_MOMENTUM, DEFAULT_BETA); }

	/** Trains the Network to the expected Output for the given Input
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error in Place
	  * Also updates all the Weights of this Layer
	  * Learning Law: WeightChange = beta * OutputError * Input	 */
	public static float[] setOutput(final Slab slab, final float[] inputs, final float[] expected, final float momentum, final float beta) {
		final float[] Outputs = slab.getOutput(inputs);
		for (int j = Outputs.length; --j >= 0;)
			expected[j] -= Outputs[j];	//Calculate the Output Errors
		slab.backProp(expected, momentum, beta);	//perform the back Propagation
		return Outputs; }	//returns the real Output
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IFunction: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**Performs a forward Propagation of the given Input Array and returns the Output.
	 * @return arg mapped by this Object: this.Map(arg) == this�arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object Map (final Object arg) { return getOutput((float[]) arg); }
	
}
