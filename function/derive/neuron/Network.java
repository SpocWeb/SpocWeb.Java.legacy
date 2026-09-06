package function.derive.neuron;

import function.AFunction;
import function.derive.IFloatDeriveAble;

/** Network of a concatenated List of Slabs, which could again be Networks
  *
  * This Network processes both digital and continuous Data.
  * Multilayer Networks are for supervised Learning
  *	and can remember any Separation if the inner Layers are large enough.
  *	They minimize (O'-O)^2 with O[j] = f(W[j][i]*I[i]) # W[j][i]*I[i]
  *	Learning Rule: dW[i,j] = � * I[i]*(O'-O)[j]*f'(W*I) # �*f'(W*I) * I�(I-WI)
  *	Operation: Presentation of a (noisy) Input restores the learned Output
  *
  * This is a quite expensive, but also universal Tool to reproduce or generalize.
  * On Success, you can go back to a simpler Model with fewer Layers or Neurons
  * or even a functional Representation.
  * It can be used to memorize Patterns, if the same Vector is presented
  * to both Input and Output.
  *
  * Input Coding:
  * Any Transformation on the Input or Output Data that makes the Range
  * and even the Distribution on it more similar should be done before!
  * (Translation, Rotation, Skewing) because Networks have O(exp) Overhead.
  * Also the Relevance of certain Input dimensions must be checked
  * -either before building the Network
  * -or by watching the Weights, which approximate 0 for irrelevant Data.
  * Rules are:
  * -use as few Input Channels as possible
  * -but resolve dense Encodings like ASCII,
  *		because otherwise the Network has to do this
  * -try to pre- categorize the Input and represent Input that should lead
  *		to a similar Result similar and different, if the Result is different.
  *		e.g. rasterize continuous Values and represent each Raster
  *		by a digital Input Field.
  *		The Raster can be unevenly spaced and even overlapping (coarse coding),
  *		which leads to a more robuts System (Translation etc.) .
  * -the Hamming Distance is the deciding Criterion for Networks
  * -use continuous Input only on Data with an inherent Order.
  *	this reduces the Network complexity very much, but makes it less stable,
  *	but sometimes this is the only way to process lots of Input!
  *
  * Output Coding:
  * -used for continuous Measures like 'Quality', but less robust
  * -each Neuron represents a certain Concept. The Result is usually not digital
  *	neither in this nor the other Neurons.
  *	Therefore usually the Output Neurons with  maximum Activation is taken
  *	and converted to digital Data using a 2 (true/false)
  *	or a 3 Schema (true / unknown / false) (Winner takes all).
  *
  * Training:
  * The Data should be presented in a pseudo random Order.
  * This reduces the Possibility of artificial oscillations in Minimum Search.
  *
  * Parameters:
  * Beta determines the Speed of Learning, but
  * -if it is too large, Oscillations are possible.
  * -if it is too small the Network becomes slow and fosters local Minima.
  * Alpha determines the Momentum and Dissipation
  * -with high Betas also choose high Alphas
  * -for complicated Structures with certain Exceptions reduce Alpha
  *	or present these Exceptions together.
  * Usually you start with high Alphas and Betas and reduce them on convergence,
  * similar to simulated Annealing.
  *
  * Noise:
  * -external Noise: cannot be avoided, creates Noise esp. on continuous Input!
  * -internal Noise: generated artificially to remove local Minima
  *	or increase Robustness
  *
  * Watching the Convergence, especially:
  * -the Deviations from the Result: if the Results stay away from a good Solution
  *	there may be too few inner Neurons
  * -the Weight Changes:
  * -the
  *
  * Dimensioning of the Network:
  * A single last Layer can only separate a linear Attribute.
  * 	Several Attributes are resolved by one Neuron per Attribute.
  * Two Layers can resolve any convex Attribute,
  * 	with binary Input coding it can resolve any possible Separation.
  * 	Several Attributes may require contradictive linear Attributes,
  * 	which requires more Neurons in the first Layer.
  * Three Layers can resolve any Separation.
  * 	This can be proven in a constructive Way by joining convex Attributes.
  * 	Even Inclusions can be represented like this,
  * 	so you never need more than three Layers,
  * 	but re- using already trained Networks or those specialized
  * 	on a certain Input can be used to further structure Problems.
  *
  * 	The Number of Neurons on the second Layer are determined
  * 	by the Number of convex Attributes that have to be joined.
  *
  * 	The Number of Neurons on the third Layer are determined
  * 	by the Number of convex Attributes that have to be joined.
  *
  * More Neurons lead to a more filigran Separation of the Result Space,
  * which opens more possibilities for Adaption (higher Probability to find the Minimum)
  * but it also defeats the Purpose of Abstraction and removing Noise.
  * This Tradeoff between Reproduction and Generalization cannot be resolved.
  *
  * Calculating the Number of necessary Neurons requires deep Problem Understanding
  * and defeats the Purpose of sub-symbolic Processing. Possible Strategies:
  * Trial and Error: Oscillation or never reaching the Target: too few Neurons
  * 				 Very good Result for known Training Cases, but
  * 				 very bad Results for new Cases: too many Neurons
  * Heuristic Rules: Most base their Numbers on the Input Dimension
  * 				 and use a Factor of 1.5 to 2.
  *  Calculation: from Training Data the Number of necessary Neurons can be calculated
  * 			 by counting the XOR Violations on the possible Separations of Input Space,
  *				 but there are two Reasons that make this very expensive:
  *				1) the exhaustiveness of the training data cannot be proven
  *				2) Since for each test you need 4 Vectors out of n,
  *					the Number of tests results in n!/(4!*(n-4)!) = n(n-1)(n-2)(n-3)/24
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:31:36Z
  * digest: e8b8287181cdc6affede637caeb37eb14cef5ca7d8d7b8be15ac2f23f5f0faa5
  * stale: false
  * tags: [code/neural_network, code/backpropagation]
  * concepts: [Neural Networks, Multilayer Perceptron]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  * 	 */
public class Network
extends AFunction
implements Slab {

	/**Linear List of Slabs	 */
	protected Slab[] Layers;

	/**Initializing Constructor,
	 * the Slabs are handed over and can either be Layers or Networks.	 */
	public Network(Slab[] Slabs){Layers = Slabs;}	//I use the clone Method too rarely! I rather use copy(), because clone() creates a shallow Copy

	/**Initializing Constructor,
	 * the Layers are created with the given Dimensions
	 * The Layers are using the same Switch Function.	 */
	public Network(int[] LayerSizes, IFloatDeriveAble Switch_) {
		int i = LayerSizes.length;
		int out, in  = LayerSizes[--i]; Layers = new Layer[i];
		while (--i >= 0) {
			out = in; in = LayerSizes[i];
			Layers[i] = new Layer(in, out, Switch_); }	//Create the Layer
	}

	/**Randomizes all the Weights of this Slab
	 * by initializing it with Weights uniformly distributed between [-1, +1]	 */
	public void randomizeWeights() {
		int i = Layers.length;
		while (--i >= 0)
			Layers[i].randomizeWeights();
	}

	/**Requests the last Output
	 * This Result may have been modified by external Code.	 */
	public float[] getOutput() {
		return Layers[Layers.length-1].getOutput(); }

	/**Performs a forward Propagation of the given Input Array through every Layer and returns the Output.
	 * @return arg mapped by this Object: this.Map(arg) == this�arg
	  * This is the Function working on 'arg' defined by the implementing Class.
	  * The Class implementing this Method is the means of exchanging this Operation.	 */
	public Object Map (Object arg) {
		return (float[]) getOutput((float[]) arg); }

	/**Requests the Output for this Input
	 * Does only a forward Propagation and returns the Result.	 */
	public float[] getOutput(float[] Input) {
		int i = -1;
		while (++i < Layers.length)	//no need to hand over the Result to the next Layer, because encoded and fixed.
			Input = Layers[i].getOutput(Input); //polymorphic function, Parameters are handed over implicitly!
		return Input; }

	/** Trains the Network to these OutputErrors = Expected - Outputs
	  * for the cached Input.
	  * Does both a forward and a backward Propagation through all Layers.
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * OutputError * Input + alpha * dWeightOld
	  * @param  OutputErrors the Differences between the desired and the actual Output
	  * @param  beta  is the Learning Factor.
	  * @param  alpha creates Inertia to speed up Convergence.
	  * @return the BackPropagation Errors for use on the next Layer.  */
	public float[] backProp(float[] OutputErrors, float alpha, float beta) {
		int i = Layers.length;
		while (--i >= 0)
			OutputErrors = Layers[i].backProp(OutputErrors, alpha, beta);
		return OutputErrors; }

	/**Trains the Network to these OutputErrors = Expected - Outputs
	 * for the cached Input (relies on the last Input to be used!).
	 * Defaults the Values for the Learning and Momentum Factor	 */
	public float[] backProp(float[] OutputErrors) {
		return backProp(OutputErrors,  ASlab.DEFAULT_MOMENTUM, ASlab.DEFAULT_BETA);}


////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) {
		testIt(); }

	/** First Layer of the 2 Layer Network	 */
	final static public int[] First_Layer = {2, 2};

	/** Second Layer of the 2 Layer Network	 */
	final static public int[] singleLayer = {2, 1};

	/** Both Layers of the 2 Layer Network	 */
	final static public int[] doubleLayer = {2, 2, 1};

	/**
	  * Tests a single Layer of Neurons,
	  * once with a linear seperable OR, (
	  * once with a linear non separable XOR Function
	  * Then it tests a 2 Layer Network with differing Numbers of Neurons in their Layers.
	  */
	public static void testIt() {
		//demonstrate that a double Input Layer CAN separate XOR Input
		Layer.testConfig(new Network(doubleLayer, Sigmoid.Sigmoid), Layer.Output_OR, true, 3);
		Layer.testConfig(new Network(doubleLayer, Sigmoid.Sigmoid), Layer.OutputXOR, true, -12);
		Slab[] Slabs = new Slab[2];
		Slabs[0] = new Layer  (2, 2       , Sigmoid.Sigmoid);
		Slabs[1] = new Layer  (2, 1       , Sigmoid.Sigmoid); Layer.testConfig(new Network(Slabs), Layer.OutputXOR, true, -3);
		Slabs[0] = new Network(First_Layer, Sigmoid.Sigmoid);
		Slabs[1] = new Network(singleLayer, Sigmoid.Sigmoid); Layer.testConfig(new Network(Slabs), Layer.OutputXOR, true, -3);
	}

}
