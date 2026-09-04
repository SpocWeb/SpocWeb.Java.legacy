package function.derive.neuron;

import java.util.Iterator;

import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import streamIO.AStreamOut;
import streamIO.Assert;
import function.byref.ByRefDouble;
import function.derive.IFloatDeriveAble;

/** Single Layer of a Network consisting of a Matrix of Weights
  * for the Mapping of the Input Vector to the Output Vector
  *
  * As preprocessing for Input and Output Values it is a good idea
  * to scale one of them to fit the Range of the other or, even better,
  * to scale one of them so the Frequencies on the Range are the same.
  *
  * Alternatively both Input and Output Vectors can be normed to the Unit SuperCube
  */
public class Layer
extends ASlab
implements Slab {

	/** Caches the last Input Vector, so it doesn't have to be passed again	 */
	protected float[] InputCache;

	/** Switch Function of this Layer's Neurons	 */
	public IFloatDeriveAble Switch;

	/** Matrix stored in a simple Array for faster Access
	  * using a single Index from both Sides (Input Values and Error)	 */
	protected float[] Threshs;

	/** The array of outputs, reused for returning the Result.	 */
	protected float[] Outputs;

	/** The Cache of the Net Activity at each Neuron, used for back Propagation.	 */
	protected float[] NetAct;

	/**Initializing Constructor,
	 * also randomizes the Weights.	 */
	public Layer(int InputDim, int OutputDim, IFloatDeriveAble Switch_) {
		super(InputDim, OutputDim);
		Threshs	= new float[OutputDim];
		Outputs	= new float[OutputDim];
		NetAct 	= new float[OutputDim];	//Caching the Net Activities for BackPropagation!
		randomizeWeights();
		Switch = Switch_;
	}

	/** Randomizes all the Weights of this Slab
	  * by initializing it with Weights uniformly distributed between [-1, +1]	 */
	public void randomizeWeights() {
//		super.randomizeWeights(); //???
		int j = Threshs.length;
		while (--j >= 0)
			Threshs[j] = (float) ByRefDouble.RANDOM_1_1();//0;
	}

	/**Requests the last Output
	 * This Result may have been modified by external Code.	 */
	public float[] getOutput() { return Outputs; }

	/** Requests the Output for this Input
	  * This is a Scalar Product between the Input Vector and the Weight Matrix
	  * appended by a Switching Function applied to each Coordinate.
	  * Does only a forward Propagation and returns the Result.	 */
	public float[] getOutput(float[] Input) {
		InputCache = Input;
		int j = Outputs.length;
		while (--j >= 0) {
			float Sum = Threshs[j]; //NetAct[j] == Threshs[j] - Input*Weights
			int i = Input.length;
			while (--i >= 0) //Calculate the Net Activity
				Sum += Input[i]*weights[i][j];	//Scalar Product
			if (Switch != null) Outputs[j] = Switch.Map(NetAct[j] = Sum);	//use the switch function
		}
		return Outputs; }

	/** Trains the Network to these OutputErrors = Expected - Outputs
	  * for the cached Input, which is replaced by the BackPropErrors.
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * OutputError * Input + alpha * dWeightOld
	  * @param  OutputErrors the Differences between the desired and the actual Output
	  * @param  beta  is the Learning Factor.
	  * @param  alpha creates Inertia to speed up Convergence.
	  * @return the BackPropagation Errors for use on the next Layer.  */
	public float[] backProp(float[] OutputErrors, float alpha, float beta) {
		boolean Moment = (alpha != 0.0f);
		int i = -1;	//== Input.length;
		while (++i < weights.length) {
			float Delta_i = 0.0f;
			float oldInput = InputCache[i];
			int j = Outputs.length;
			while (--j >= 0) {
				float OutputError;
				if (i == 0)	//on the first time rescale the Deltas
					Threshs [j] +=	OutputError = (OutputErrors[j] *= beta * Switch.getDerivative(NetAct[j]));
				else				OutputError =  OutputErrors[j];
				Delta_i	+= weights[i][j] *	OutputError;
				OutputError *= oldInput;
				if (Moment)
					dWeight	[i][j] =  (OutputError += alpha * dWeight [i][j]);	//use the Momentum
				weights	[i][j] += OutputError;
			}	//now multiply by derivative of the sigmoid squashing function, which is just the input*(1-input)
			InputCache	[i] = Delta_i / beta;
		}
		return InputCache; }

	/**Trains the Network to these OutputErrors = Expected - Outputs
	 * for the cached Input (relies on the last Input to be used!).
	 * Defaults the Values for the Learning and Momentum Factor	 */
	public float[] backProp(float[] OutputErrors) {
		return backProp(OutputErrors,  DEFAULT_MOMENTUM, DEFAULT_BETA);}

////////////////////////////////////////////////////////////////////////////
//	static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

//  Test Data

	/** Input for testing on boolean Functions	 */
	final static public float[][] Input = {{0,0},
										   {0,1},
										   {1,0},
										   {1,1}};

	/** OR Function for testing on linear Separability, this is easy	 */
	final static public float[][] Output_OR = {	{0},
												{1},
												{1},
												{1}};

	/** XOR Function for testing on nonlinear Separability,
	  * for this you need a 2 Layer Network!
	  */
	final static public float[][] OutputXOR = {	{0},
												{1},
												{1},
												{0}};

//  Test Methods

	/**Tests a Configuration of Layers of Neurons, with a given boolean Function	 */
	public static void testConfig(Slab Network, float[][] testValues
			, boolean separable, int convergesInLoopNo) {
		int Count = 0, i = 1, N = 20;
		for(int Count1 = N; --Count1 >= 0;){
			for(int Count2 = 100; --Count2 >= 0;){
				for(i = Input.length; --i >= 0;)
					ASlab.setOutput(Network, (float[])    Input [i].clone(),
					  						 (float[])testValues[i].clone(), 0.9f, 0.6f);	//learning Parameter
				++Count;
			}
			double average = MatrixFloat.SUM(testValues)/(testValues.length*testValues[0].length); 
			for(i = Input.length; --i >= 0;) {
				float[] expected = testValues[i]; 
				float[] actual = Network.getOutput((float[]) Input[i].clone()); 
				System.out.print("Input : "); AStreamOut.ARRAY_TO_STREAM(System.out, Input	[i], ",");
				System.out.print("Should: "); AStreamOut.ARRAY_TO_STREAM(System.out, expected, ",");
				System.out.print("Actual: "); AStreamOut.ARRAY_TO_STREAM(System.out, actual, ",");
				System.out.println(Count);
				if (separable) {
					Assert.EQUALS(expected, actual, 0, 1.0/Math.max(1, convergesInLoopNo+N-Count1)); //0.05);
				} else {
					for (int j = 0; j < actual.length; j++) {
						Assert.EQUALS(average, actual[j], 0, 1.0/Math.max(1, convergesInLoopNo+N-Count1));
					}// System.out.println(); 
				}
			}
		}
	}

	/**Tests a single Layer of Neurons, once with a linear seperable OR,
	 * once with a linear non separable XOR Function	 */
	public static void testIt() {
		//demonstrate that a single Input Layer CAN NOT separate XOR Input
		testConfig(new Layer(2, 1, Sigmoid.Sigmoid), Output_OR, true, 6);
		testConfig(new Layer(2, 1, Sigmoid.Sigmoid), OutputXOR, false, 0);
	}

}
