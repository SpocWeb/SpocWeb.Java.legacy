package function.derive.neuron;

//import Functions.ByRef.ByRefInt;

import graphs.MatrixGraph;
import math.matrix.MatrixFloat;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.Log;
import streamIO.integer.random.RandomFast;
import streamIO.real.FilterIn_Float2Array;
import tester.ITester;

/**
 * Represents a Kohonen Layer with 1D, 2D, 3D or variable Topology.
 * The Kohonen Network minimizes the same Function as the Perceptron:
 * -but it implements competitive Learning by introducing a topological Function
 *  for Interaction between Neurons (Neighbors) and can create associative Maps.
 * -Learning Rule:  dW[i,j] = �*(I[i]-W[i,j]) for the maximum j and it's Neighbors. 
 *
 * The Kohonen Layer can be used as a self organizing Map,
 * optionally normalizes the Data and activates only the winning Neuron
 * and the Neurons in it's 'Neighborhood' which is defined by the Topology.
 * So far 1-dim, 2-dim and 3-dim Topologies on the Output Vector are defined.
 * 
 * If the Neighbourhood is 0, you have a 'normal' Classificator, i.e.
 * the Network creates Concepts of the Input classes and classifies any Input
 * by how close it matches the existing Bins, represented by the Weights.
 * One single Neuron becomes the Specialist for a Class ("Winner takes all" Principle)
 * 
 * With a Neighbourhood the neighboring Nodes also profit from a close Match.
 * One Effect is that the resulting Bins are Neighbours.
 * The other Effect is that a discrete Topology can be defined
 * and the Binning is tied to that Topology.
 * 
 * An Input Layer is not needed, this Network usually works as single Layer.
 * Similar to Layers and their Generalize- Reproduce Dilemma,
 * the Kohonen Network has the Stability- Plasticity Dilemma.
 * 
 * There is no explicit Training Mode, the Network uses all Input Data for Classification,
 * only Learning Factor and Neighborhood Size have to be reduced for Convergence.
 * 
 * The Topology determines the Update Area
 * and thus allows the Map to organize the Parameters of it's Winning Nodes
 * according to the Values of the Data's Dimensions.
 * 
 * There is a great Similarity to Search Algorithms,
 * only with Searching the Match Vector is defined by the User (a Human),
 * whereas here the Vectors are calculated from the Input Values.
 * The Topology supports the Forming of Structures analogous to the Input.
 *
 * Many Problems in metric Spaces can be treated using this Algorithm:
 * - (k-) nearest Neighbor Search: Classification & Interpolation & Pattern Recognition
 * - Euklidean minimum Spanning Tree: Clustering & Travelling Salesman Problem
 * - Triangulation Problem: finite Elements Method
 *
 * Voronoi Diagram and it's dual the Delauney Triangulation:
 * Given a Set of Neurons with their Location Vector.
 * The receptive Field of a certain Neuron is defined as the Hypervolume
 * for which this Neuron is the "winner", i.e. it is "closest"
 * (according to some Norm or Metric) to each Point in this Hypervolume
 * compared to all other Neurons in the Set of Neurons.
 * The Voronoi Diagram (VD) consists of all the receptive Fields
 * of the Set of Neurons considered and fills the whole R^D with
 * (irregular) Polyhedrons.
 *
 * The Delauney Triangulation (DT) is a Line Graph in R^D
 * which can be constructed by connecting each two Neurons that share a HyperPlane
 * (The degenerated Case that they share only a HyperLine or HyperPoint
 *  is not considered!)
 * The DT is exactly what is constructed in the Connections Matrix.
 * Given the DT most Problems in R^D can be solved in linear Time O(N).
 * The Edges of the minimum Spanning Tree are a Subset of the DT
 * and can be found in O(N) instead of O(N log N).
 * The k nearest Neighbor can be found in O(log N) instead of O(N).
 *
 * The topology preserving Mapping can be used in different ways:
 * -To visualize lower dimensional Manifolds in high dimensional Spaces
 * -To abstract Information from highly dimensional Input Data and thus
 * -To more effectively process highly dimensional Input Data
 * -To orchestrate multiple Degrees of Freedom (e.g. Robots) effectively
 *     for a lower dimensional (1-dim) Trajectory
 *
 * Topology / Dimension Preservation is defined by
 * both the Mapping and it's Inverse are topologically equivalent.
 * Topologically equivalent ("continuous") mappings are defined by the fact that
 * the Mapping of any (new) Point/Set introduced "between" (Triangular Inequation) two Points
 * is also "between" the Mappings of the latter two Points.
 * In topological preserving Mappings this is true for both the Mapping and it's Inverse.
 *
 * The Importance of "continuousness" is for one thing it's defining Property,
 * the triangular Inequality, but also the Fact that together with this comes
 * the Mapping of (lower dimensional) Submanifolds is also continuous, i.e.
 * the Mapping lies within the Mapping of the Bordering Manifolds.
 *
 * A Torus is a topologically twofold connected Area, i.e. for any two Points
 * there are two ways to connect them
 * which cannot be continuously transformed into each other
 * (one Path around the inner Radius and one around the outer Radius).
 *
 * Special Cases:
 * In euklidean Metric Each Edge of the DT is perpendicular
 * to a corresponding HyperPlane of the VD.
 *
 * In R^2 with D=2 (planar Case) the Euler Formula for Polygons applies,
 * so both the DT and the VD have at most 3N-6 Points
 * and thus can be stored and transformed in linear Time.
 *
 * Convergence:
 * Convergence is automatically controlled by the Neuron with the minimum Wins.
 * The Learning Factor beta is decreased with the Minimum Wins of all Neurons.
 * Any other decrease of the Learning Factor is only counterproductive!
 * With random Data Distribution:
 * Interestingly the Beginning of Convergence e.g. for R^4 with 4*4*4*4 = 256 Neurons
 * is quite constant, starts at 60.000 Iterations and ends with 80.000 #Neighbors = 11.5.
 * Possibly the Neuron Gas with Boltzman Distribution is faster in Convergence?
 * Convergence in R^3 with 6*6*6 = 216 Neurons starts with 47.000 Iterations
 * and ends with 69.000 and an Average # of Neighbors of 8.
 * Convergence in R^2 with 15*15 = 225 Neurons starts with 61.000 Iterations
 * and ends with 90.000 and an Average # of Neighbors of .
 *
 *
 * Design Decisions:
 * instead of Maximum Norm = max(|x[i]|)
 * these Algorithms can use the Absolute Norm = sum(|x[i]|)
 * to determine the Update Range around the Maximum,
 * although the first one is much easier to program,
 * because it ranges rectangularly from max-n to max+n and thus forms a rectangular Area
 * with 2N*2N = 4NN Elements.
 * The Absolute Norm forms a Diamond Area with N*N+(N-1)*(N-1) = 2N*N - 2N + 1 == 2N(N-1) +1
 * Elements, about half of the Elements of the rectangular Area but rising as fast!
 *
 * Neuron Gas as Implementation Alternative:
 * Boltzmann Distribution of the Neurons' Aktivation:
 * e*exp(-i/l) => e*u^i mit u = exp(-1/l)
 *
 * e = 0.3  => 0.05
 * l = 0.2N => 0.01
 * T = 0.1N => 2N
 * with N = #Neuronen
 *
 * extremely small Factor in the End with exp(-1/0.01) = exp(-100) no Coupling anymore
 * large Factor in the Beginning with N=200 e.g. exp(-1/100) = exp(-0.01) = 0.99
 * thus very high (indirect) "Coupling" between all Neurons at the Start
 * (the more Neurons, the higher the Coupling)!!!
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:32:23Z
 * digest: 546e21eaea244e2e7d703b6f6a7366a89654f780cb7eaf6aaf05a7693aa4c94b
 * stale: false
 * tags: [code/neural_network, code/numerical_algorithm]
 * concepts: [Self-Organizing Maps, Unsupervised Learning, Topology Preservation]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Kohonen
extends ASlab {
	
	private static final Log L = new Log(Kohonen.class, 1); 
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants, Variables, Defaults
	////////////////////////////////////////////////////////////////////////////
	
	/** Initial Learning Factor Default */
	public static float LEARN_INITIAL = 0.3f;
	
	/** Final Learning Factor Default */
	public static float LEARN_FINAL = 0.01f;
	
	////////////////////////////////////////////////////////////////////////////
	//  static Methods to model incoming Data with a Kohonen Map
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Builds up and returns the Kohonen Map for an Input streamIO of Vectors.
	 * This Routine works unsupervised and returns
	 * the full abstracted topological Mapping of the Input Data.
	 * The expected Result is a Binning of the Data
	 * into the given Number of Dimensions and Neurons per Dimension
	 *
	 * Type 0 demonstrates the Ability of the Map to adapt to the Problem
	 * and group Data into Bins as well as to define the Bins in Parallel!
	 * This can be used to automatically Cluster Data in any Dimensions
	 * without having to do it manually!
	 *
	 * @param type Depending on the Type one of the following Backpropagation Mechanisms is being used:
	 * 0 : variable Dimension symmetric Topology Representing Network (TRN, faster Convergence)
	 * 1 : 1-Dim Topology Kohonen SOM using Abs Norm in the Target Space
	 * 2 : 2-Dim Topology Kohonen SOM using Abs Norm in the Target Space
	 * 3 : 3-Dim Topology Kohonen SOM using Abs Norm in the Target Space
	 * 4 : variable Topology Kohonen SOM using Max Norm in the Target Space
	 * 5 : variable Dimension asymmetric Topology Representing Network (TRN)
	 *
	 * @param dim The Number of Dimensions of the Input Data
	 *  If not positive, it is derived from the first Input Vector.
	 * @param Neurons The Number of Neurons per Dimension (also for TRN).
	 * @param data A streamIO of float[] Vectors (can be the same float[]).
	 * @param breakIteration A ITester called regularly
	 *  to allow stopping the Algorithm synchronously by returning 'true'.
	 *  The Kohonen Object returned is handed over to this 'Test()' Method,
	 *  so it can check for anything concerning the Map.
	 *  @see KohonenTester uses this to print out the Progress to the Screen.
	 *
	 * To stop Processing asynchronously, just set the Flag from a different Thread.
	 */
	public static Kohonen MODEL_DATA(final int type, final int dim, final int numNeuronsPerDim,
			final IIStreamIn data, final ITester breakIteration) {
		return modelData(type, dim, numNeuronsPerDim, numNeuronsPerDim, data, breakIteration); }

	/**Builds a new {@link Kohonen} Map of the given Dimensions and trains it for {@code numKSteps} Steps.
	 * Overload of {@link #MODEL_DATA(int, int, int, IIStreamIn, ITester)} that lets the caller choose
	 * the Number of training Steps independently of the Number of Neurons per Dimension.
	 * @return the trained Kohonen Map	 */
	public static Kohonen modelData(final int type, final int dim, final int numNeuronsPerDim,
			final int numKSteps, final IIStreamIn data, final ITester breakIteration) {
		int  inputDim = dim;  //Number of Dimensions
		int outputDim = 1; //Number of Output Classes, should be an Integer raised to the Power of the Input Dim!
		final int[] numBins = new int[dim]; // {OutputDim}; //1-dim Distribution with 10 Bins.
		for(int d = dim; --d >= 0;) {
			numBins[d] = numNeuronsPerDim;
			outputDim *= numNeuronsPerDim; }
		final Kohonen Kohonen = new Kohonen(inputDim, outputDim);
		Kohonen.numBins = numBins;
		Kohonen.randomizeWeights(); //
		return RE_MODEL_DATA(type, Kohonen, numNeuronsPerDim, numKSteps, data, breakIteration); }

	/**
	 * Builds up and returns the Kohonen Map for an Input streamIO of Vectors.
	 * This Routine works unsupervised and returns
	 * the full abstracted topological Mapping of the Input Data.
	 * The expected Result is a Binning of the Data
	 * into the given Number of Dimensions and Neurons per Dimension
	 * 
	 * Type 0 demonstrates the Ability of the Map to adapt to the Problem
	 * and group Data into Bins as well as to define the Bins in Parallel!
	 * This can be used to automatically Cluster Data in any Dimensions
	 * without having to do it manually!
	 * 
	 * @param type Depending on the Type one of the following Backpropagation Mechanisms is being used:
	 * 0 : variable Dimension symmetric Topology Representing Network (TRN, faster Convergence)
	 * 1 : 1-Dim Topology Kohonen SOM using Abs Norm in the Target Space
	 * 2 : 2-Dim Topology Kohonen SOM using Abs Norm in the Target Space
	 * 3 : 3-Dim Topology Kohonen SOM using Abs Norm in the Target Space
	 * 4 : variable Topology Kohonen SOM using Max Norm in the Target Space
	 * 5 : variable Dimension asymmetric Topology Representing Network (TRN)
	 *
	 * @param dim The Number of Dimensions of the Input Data
	 *  If not positive, it is derived from the first Input Vector.
	 * @param numNeurons The Number of Neurons per Dimension (also for TRN).
	 * @param data A streamIO of float[] Vectors (can be the same float[]).
	 * @param breakIteration A ITester called regularly
	 *  to allow stopping the Algorithm synchronously by returning 'true'.
	 *  The Kohonen Object returned is handed over to this 'Test()' Method,
	 *  so it can check for anything concerning the Map.
	 *  @see breakIteration used to print out the Progress to the Screen.
	 *
	 * To stop Processing asynchronously, just set the Flag from a different Thread.
	 */
	public static Kohonen RE_MODEL_DATA(final int type, final Kohonen kohonen, final int numNeuronsPerDim,
			final IIStreamIn data, final ITester breakIteration) {
		return RE_MODEL_DATA(type, kohonen, numNeuronsPerDim, numNeuronsPerDim, data, breakIteration); }

	/**Continues training the given {@link Kohonen} Map for {@code numKSteps} Steps.
	 * Overload of {@link #RE_MODEL_DATA(int, Kohonen, int, IIStreamIn, ITester)} that lets the
	 * caller choose the Number of training Steps independently of the Number of Neurons per Dimension.
	 * @return the further trained Kohonen Map	 */
	public static Kohonen RE_MODEL_DATA(final int type, final Kohonen kohonen, final int numNeuronsPerDim,
			final int numKSteps, final IIStreamIn data, final ITester breakIteration) {
		int neighbor = numNeuronsPerDim / 2;
		float learn      = LEARN_INITIAL; //initial Value...
		float learnFinal = LEARN_FINAL; //initial Value...
		float learnFactor= (float) Math.pow (learnFinal/learn, 1.0/numKSteps); //gradually reduce the Value downto the wanted final Alpha...
		int counter = numKSteps;
		int outputDim = kohonen.weights.length;
//		int numInner = 100; //...100 Repetitions, this should cover the Area / all Input Values
//		int Count  =  0, i;
//		int CountNoChange =  0;
//		int Count1 = 10000; //do at most 10000 Iterations of...
		float deltaOld, delta = 0; //the Correction is a better Measure than the Maximum Deviation!
//		float Max1Old = 0;
//		float Max2Old = 0;
		while (true) { //(--Count1 >= 0) {
			int Count2 = outputDim; //O(N) to allow Update on every Neuron (on Average)!
			deltaOld = delta; delta = 0; //the Correction is a better Measure than the Maximum Deviation!
			while (--Count2 >= 0){ //let all N Input Values run through
				final float[] Input = (float[]) data.nextItem(); // = new float[InputDim]; //
				final int win = kohonen.getWinner(Input); 
				L.l("best fitting (2) Neuron(s)").l(win); 
				switch(type) {
					case 0: delta += kohonen.backProp (learn); break;	// symmetric free Topology
					case 1: delta += kohonen.backProp (learn, neighbor); break;	//1D: Neurons
					case 2: delta += kohonen.backProp (learn, neighbor, numNeuronsPerDim); break;	//2D: Neurons*Neurons
					case 3: delta += kohonen.backProp (learn, neighbor, numNeuronsPerDim, numNeuronsPerDim); break;	//2D: Neurons*Neurons*Neurons
					case 4: delta += kohonen.backProp (learn, neighbor, kohonen.numBins); break;	//generic nDim fixed Topology Algorithm
					case 5: delta += kohonen.backPropAsym(learn); break; //asymmetric free Topology
					default: break;
				}
			}
			if (type == 0) {  //for large minCountWins alpha could decrease radically!!!
				float progress = kohonen.calcProgress();
				if (progress > 0.995f) {
					progress = 0.995f; }
			//	alpha = 0.5 downto 0.01
			//	alpha = 1.0f/(2 + Kohonen.minCountWins); // numCountWins
				learn = 0.7f*(1-progress); //*(1-Progress);
				L.n("New LearnFactor: ").l(learn);
				if ((--counter <= 0) && //this is not sufficient for Convergence!
					(learn <= 1.5*kohonen.ThresholdBeta)) { //This is not reliable for large NumKSteps
					L.n("Counter at Zero!");
					MatrixFloat.COPY_LOWER_TO_UPPER(kohonen.connections);
					break; }//0.3 .. 0.01 == 3 .. 100
/*				if (((Max1Old + Max1)/Math.abs(Max1Old - Max1) > 2*Math.sqrt(numInner)) && //Test both Conditions separately!!!
					((Max2Old + Max2)/Math.abs(Max2Old - Max2) > 2*Math.sqrt(numInner))) {
					if (++CountNoChange > 4) { //OutputDim) { //allow the Dimensions to settle down
						break; } //Neurons have settled down!
				} else {
					if (--CountNoChange < 0) { //undo previous Counting...
						  CountNoChange = 0; }
				}
				Max1Old = Max1;
				Max2Old = Max2;
*/			} else { //"normal" Iteration with reducing Alphas...
				if ((delta <= deltaOld) && (delta > deltaOld*(1-learn/2))) {
					if (learnFinal > (learn *= learnFactor)) { //final Factor
						break; } // and reduce the gain, alpha to enforce Convergence!
					--neighbor; //gradually reduce the neighborhood size from
				}
			}
			if (breakIteration != null) {
				try { //for asynchronous Termination
					if (breakIteration.test(kohonen)) { //for synchronous Termination after the Test
						L.n("Tester requested Break in Iteration !");
						break; }
				} catch (final Throwable t) { //catch all Exceptions, because after all...
					t.printStackTrace(); //it is only a Subroutine!
				}
			}
		}
		return kohonen; } //not necessary. because returning anyway!
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	/** Number of Elements in the different Dimensions of this multidimensional Kohonen Network.
	  * It should be Weight.length == Prod(numBins)
	  */
	protected int[] numBins;

	/** Determines whether
	  * * Vectors are normalized (for equisized Dimensions) or
	  * * absolute Differences are calculated (for huge Differences in Dimensions)
	  * In general the Input Data should be scaled to fit in the Hyperarea [0..1]^N
	  */
	public boolean scale;

	/**
	  * List of Connections c[i,j] expressed as full Adjacency Matrix
	  * with the Age as Weight for the dynamic Calculation of the Dimension.
	  * Connections whose Age is above a certain Threshold are removed / not considered.
	  *
	  * Not transient, because it reflects the Map, together with the Weights.
	  * Opposite to the Adjacency Matrix, the Connection Matrix
	  * does not reflect the Distance between the Elements,
	  * but the multiplicative Inverse, it's Closeness.
	  * To use this Matrix for Graph Operations just take all it's Values 1/x
	  * The Matrix must be initialized to 1 which is done in randomizeWeights.
	  */
	protected float [][] connections;
//	protected double[][] Connections;

	/** Counter how often this Neuron was updated
	  * should be about the Number of Points, so every Point had a Chance to be updated! */
	protected int[] numWins;

	////////////////////////////////////////////////////////////////////////////////////
	/// Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor,
	  * also randomizes the Weights.
	  *
	  * @param inputDim		the Number of Dimensions of the  Input Vector
	  * @param outputDim	the Number of Dimensions of the Output Vector for this Network
	  * 					this is actually the Number of Bins expected
	  * 					and should be at least a Magnitude less
	  * 					than the Number of Input Data.
	  * 					The Midpoint Vector of each Bin is stored in the Weights.
	  * @param Neighborhood	The
	  */
	public Kohonen(final int inputDim, final int outputDim) {
		super(outputDim, inputDim);	//The Kohonen Map doesn't use a Switch Function!
		numWins     = new int  [outputDim];
		connections = new float[outputDim][outputDim];
/*		connections = new float[outputDim][]; //constructing an asymmetric Matrix to save Space.
		for (int i = connections.length; --i >= 0;) //faster Convergence when using symmetric Adjacency Matrix
			connections[i] = new float[i+1]; 
*/	}

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Randomizes all the Weights of this Slab
	  * by initializing it with Weights uniformly distributed between [-1, +1]	 */
	public void randomizeWeights() {
		//instead of randomizing, try to put all Weights on a straight Plane through the Data Domain...
/*		int j, dim = Weights[0].length;
		float len = j = Weights.length; //this should reduce Crossings!
		while (--j >= 0) { //but actually still 1 or 2 Crossings remain!
			int d = dim;   //these should be 'cut' open (for 1 dim) and reconnected
			while (--d >= 0) { //(make an || out of X !) by reversing the inner Points.
				Weights[j][d] = (float)(VectorDouble.Random1_1()*8.5 + j)/len; } }
		//detecting and removing higher dimensional Crossings is much more complicated!!
*/		super.randomizeWeights();
		//normalize the Weight vectors to unit length
		//a weight vector is the set of weights for a given output
		if ((weights.length > 1) && scale) { 	//1-dim Vectors need not be normalized!
			for(int j = weights.length; --j >= 0;) {
				VectorFloat.NORMALIZE_AT(weights[j]); }
		}
		numBackProps = 0;
		initConnections(); }

	/**
	 * fill the whole Connection Matrix with 1.0f
	 */
	public void initConnections() {
		for(int i = connections.length; --i >= 0;) {
			numWins[i] = 0;
			final float[] row = connections[i];
			for (int j = i; --j >= 0;) 
				row[j] = 1; 
			//for (int j = len; --j > i;) 
			//	row[j] = 0; 
		}
	}
	
	/**Reports the size of this Kohonen Map.
	 * @return the Number of Neurons / Weights in this Network */
	public int getNumWeights() { return weights.length; }
	
	/** Make it public for access by other Programs
	 *  Too dangerous to destroy the structure let alone the Data. */
	protected float[][] getWeights() { return weights; }
	
	/** Make it public for access by other Programs
	 *  Instead of returning the whole Array at once,
	 *  allow only to get it by Neuron.
	 *  This prevents structural Tempering with this Object,
	 *  it only allows Modification of the Contents.
	 */
	public float[] getWeight(final int i) {
		if ((i >= 0) &&
			(i < weights.length)) {
			return weights[i]; }
		return null; }

	/** Requests the last Output
	  * This Result may have been modified by external Code.	 */
	public float[] getOutput() { return weights[maxIndex1]; }

	/** Maximum Index of the last getWinner() Method 	 */
	protected transient int maxIndex1;

	/** second Maximum Index of the last getWinner() Method
	  * Used for creating the Topology on the Fly 	 */
	protected transient int maxIndex2;

	/** second Maximum Value of the last getWinner() Method
	  * Used for checking the convergence of the Topology 	 */
	protected transient float currMin1;	//

	/** Maximum Value of the last getWinner() Method
	  * Used for checking the convergence of the Topology 	 */
	protected transient float currMin2;	//

	/**
	  * Requests the Output for this Input.
	  * The Kohonen Function determines the Weight Vector
	  * with minimum Difference to the Input Vector.
	  * instead of the maximum Scalar Product between Input and Output Vector.
	  * It works for both normed and not normed Vectors.
	  * Does only a forward 'Propagation' and returns the Result.
	  * With the found Winner perform a "Back-Propagation"
	  * by making the Winner Node (and it's Neighbors) even closer to the Input.
	  * @return the Index of the winning Neuron
	  */
	public float[] getOutput(final float[] input) {
		return weights[getWinner(input)]; }

	/**
	  * Requests the Output Neuron for this Input.
	  * The Kohonen Function determines the Weight Vector
	  * with minimum Difference to the Input Vector.
	  * instead of the maximum Scalar Product between Input and Output Vector,
	  * because it doesn't deal with normed Data, correcting only the Phase.
	  * It works for both normed and not normed Vectors.
	  * Does only a forward 'Propagation' and returns the Result.
	  * Perform a "Back-Propagation" With the Winner found,
	  * by moving the Winner Node (and it's Neighbors) even closer to the Input.
	  *
	  * Also calculates the second Winner for the determining the dynamic Topology of the TRN!
	  *
	  * @return the Index of the winning Neuron
	  * @param input Vector to search the Winner for...
	  */
	public int getWinner(final float[] input) {
		currMin1 = currMin2 = Float.POSITIVE_INFINITY;	//Initialize Search for the Minimum
		int i = weights.length; //go through all Columns
		while (--i >= 0) { //since ALL Distances are calculated anyway,
			float Sum = (float) VectorFloat.DIFF_NORM_ABS(input, weights[i], dWeight[i]);
			if (currMin2 > Sum) { //smaller than the 2nd smallest
				if (currMin1 > Sum) { //smaller than the smallest
					currMin2 = currMin1 ; currMin1 = Sum;
					maxIndex2= maxIndex1; maxIndex1= i;
				} else { //could also use the Neuron Gas Algorithm,
					currMin2 = Sum; //but that requires N* more Calculations
					maxIndex2= i; } //and doesn't really improve Accuracy
			}
		}
		return maxIndex1; }

	/**
	  * The (constant) Learning Factor for the current Update Sweep
	  * cached and set for the immediate updateElement() Calls.
	  */
	protected transient float learn;

	/**
	  * The Sum of all Changes (in AbsNorm) for the current Update Sweep
	  * cached and summed up in the immediate updateElement() Calls.
	  *
	  * Can also be described by currMin1 * beta and currMin2 * beta
	  */
//	protected transient float delta;

	/**
	  * Performs the Backpropagation of the Error for a single Node.
	  * Used for both the 1D and 2D Back Propagation.
	  * Learning Law: dWeight = beta * (input-weight)
	  *
	  * Also updates all the Weights of this Layer by
	  * calculating the Norm of the Weights Vector
	  * to avoid Winners just due to the sheer Size of their Vectors.
	  *
	  * @return the Difference Vector applied
	  */
	protected float updateElement(final int j) {
//		++CountUpdate[j];
//		++BackPropCounter;
		if ((j < 0) || 
			(j >= weights.length)) 
			return 0; 
		boolean normalize = (weights.length > 1) && scale;
		float[] v = VectorFloat.addProdAt(weights[j], learn, dWeight[j]);
		if (normalize) //1-dim Vectors should not be normalized!
			VectorFloat.NORMALIZE_AT(v); 
		return learn*math.vector.VectorFloat.NORM_ABS(dWeight[j]); } //can also be described by currMin1 and currMin2

	/**
	  * Size of the Neighborhood
	  * cached for the backProp() and backPropMax() Calls
	  * Not serialized, but also not 'volatile'
	  */
	protected transient int Neighborhood;
	
	/**
	  * Size of the Lattice, cached for the backPropMax() Calls
	  * Not serialized, but also not 'volatile'
	  */
	protected transient int[] NumBins;
	
	/**
	  * Recursive Back Propagation with Maximum Norm
	  * Trains the Network to these OutputErrors = Expected - Winner
	  * for the cached Input.
	  * This Routine defines the Topology.
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * (input-weight)
	  * TODO: the Gain beta should be decreased with the Distance of the Neurons
	  * to the winning Neuron.
	  */
	protected float backProp(int dim, final int max, final int size) {
		final int NextSize= size * NumBins[--dim]; //the Scale with which to move in the next Iteration
		final int StepMax = size *(Neighborhood+1); //one earlier and one later
		int NextMax = max - StepMax;
		int Max_Max = max + StepMax;
		if (Max_Max > weights.length) {
			Max_Max = weights.length; }
		float ret = 0;
		while ((NextMax += size) < Max_Max) { //compensates Start and End Offset
			if (NextMax < 0) //skip, out of Range!
				continue; //does 'continue' test the Condition?
			ret += (dim > 0) 
			? backProp(dim, NextMax, NextSize)
			: updateElement(NextMax); 
		}
		return ret; }

	/**
	  * Recursive Back Propagation with Maximum Norm
	  * Trains the Network to these OutputErrors = Expected - Winner
	  * for the cached Input.
	  * This Routine defines an arbitrary x-dimensional Topology.
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * (input-weight)
	  *
	  * @return the Winner Vector.
	  */
	public float backProp(final float beta, int Neighborhood_, final int[] NumBins) {
		++numBackProps;
		if (Neighborhood_ < 0) {
			Neighborhood_ = 0; }
		this.Neighborhood = Neighborhood_;
		this.NumBins = NumBins;
		this.learn = beta;
		return backProp(NumBins.length, maxIndex1, 1); }

	/// The Alternative was to use the isomorphically equivalent
	/// Approximation of Infinity by incrementing the Weight starting with 0 or the
	/// Approximation of 0 by multiplying the Weight starting with 1

	/** Trains the Network to these OutputErrors = Expected - Winner
	 * for the last Input using an Adjacency Matrix.
	 * This Routine defines it's own Topology.
	 * Does only the backward Propagation, call the getWinner() or getOutput() Methods before.
	 * Updates all the Weights of this Layer
	 * Learning Law: dWeight = beta * (input-weight)
	 *
	 * Creates / deletes Connections.
	 *
	 * The Code commented out indicates an Alternative to count the Failures
	 * when a longer Lifetime is needed than float can hold. (larger Number of Neurons)
	 * Another Alternative is to use double instead of float for the Connection Matrix!
	 *
	 * @return the Winner Vector.
	 */
	public float backPropAsym(final float beta) {
		//Winner and 2nd Winner are in maxIndex1 and maxIndex2
		float[] conn = connections[maxIndex1];
		++numBackProps;
		++numWins[maxIndex1];
		float sqrt = (float) Math.sqrt (beta);
		conn[maxIndex1] = sqrt; //1.0f; //multiply the previous betas up
		conn[maxIndex2] = beta;
		float ret = 0;
		int i = conn.length;
		while (--i >= 0) { //played around with the aging Algorithm...
			if (ThresholdBeta < (this.learn = (float) (conn[i] *= sqrt))) { //significant Neighborhood
//			if (ThresholdBeta < (this.beta = (float) (tmp = conn[i]* sqrt))) { //significant Neighborhood
//				conn[i] = tmp;
				ret += updateElement(i); //strong Elements have to 'create' their Neighborhood!
			} else { //start Counting, otherwise the float Type cannot hold Ages!!!
//				--conn[i];
			}
		}
		return ret; }

	/**
	  * Threshold Learning Factor from which on the Connection is no longer considered
	  * and the Neuron not updated.
	  */
	public float ThresholdBeta = 0.01f;

	/** Trains the Network to these OutputErrors = Expected - Winner
	  * for the last Input using an Adjacency Matrix.
	  * This Routine defines it's own Topology.
	  * Does only the backward Propagation, call the getWinner() or getOutput() Methods before.
	  * Updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * (input-weight)
	  *
	  * Creates / deletes Connections.
	  *
	  * @return the Winner Vector.
	  */
	public float backProp(final float beta) {
		//Winner and 2nd Winner are in maxIndex1 and maxIndex2
		float[] conn = connections[maxIndex1];
		float SqRtBeta = (float) Math.sqrt (beta);
		++numBackProps;
		++numWins[maxIndex1];
		conn[maxIndex1] = SqRtBeta; //1.0f; //multiply the previous betas up
		if (maxIndex2 < maxIndex1) {
			conn[maxIndex2] = beta; //increase the Value.
		} else {
			connections[maxIndex2][maxIndex1] = beta;
		}
		float ret = 0;
		int i = conn.length;
		while (--i >= 0) {
			if (i < maxIndex1) { //make the Matrix symmetric => more Updates, but also more Aging => faster Forming!
				if (ThresholdBeta < (this.learn = (float)(conn[i]*=SqRtBeta))) { //significant Neighborhood
					ret += updateElement(i); } //strong Elements have to 'create' their Neighborhood!
			} else {
				if (ThresholdBeta < (this.learn = (float)(connections[i][maxIndex1] *=SqRtBeta))) { //significant Neighborhood
					ret += updateElement(i); } //strong Elements have to 'create' their Neighborhood!
			}
		}
		return ret; }

	/** Trains the Network to these OutputErrors = Expected - Winner
	  * for the cached Input.
	  * This Routine defines a 1D Topology.
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * (input-weight)
	  *
	  * TODO: the Gain beta should be decreased with the Distance of the Neurons
	  * to the winning Neuron.
	  * @return the Winner Vector.
	  */
	public float backProp(final float beta, int neighborhood) {
		//No change if input and weight vectors are aligned
		//only update those outputs that are within a neighborhood's distance from the last winner
		++numBackProps;
		if (neighborhood < 0) {
			neighborhood = 0; }
		this.learn = beta;
		int startIndex = maxIndex1 - neighborhood;
		if (startIndex < 0)
			startIndex = 0; //limited at the Border
		int stopIndex = maxIndex1 + neighborhood +1;
		if (stopIndex > weights.length)
			stopIndex = weights.length; //limited at the Border
		int j = stopIndex;
		float ret = 0;
		while (--j >= startIndex) {
			ret += updateElement(j); }
		return ret; }

	/**
	  * Trains the Network to these OutputErrors = Expected - Winner
	  * for the cached Input.
	  * This Routine defines a 2D Topology.
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * (input-weight)
	  *
	  * TODO: the Gain beta should be decreased with the Distance of the Neurons
	  * to the winning Neuron.
	  * @return the Winner Vector.
	  */
	public float backProp(float beta, int Neighborhood, int NumCols) {
		//No change if input and weight vectors are aligned
		//only update those outputs that are within a neighborhood's distance from the last winner
		//This Neighborhood is now defined in 2 Dimensions with the given NumCols
		++numBackProps;
		this.learn = beta;
		if (Neighborhood < 0) {
			Neighborhood = 0; }
		int xNeighborhood;
		int maxRow = maxIndex1 / NumCols; //Row    of the Maximum
		int	maxCol = maxIndex1 % NumCols; //Column of the Maximum
		int x, y; //
		int NumRows = weights.length / NumCols;
		float ret = 0;
		y = NumRows;
		while (--y >= 0) { //loop through the Rows
			xNeighborhood = Neighborhood - Math.abs(y - maxRow);
			if (xNeighborhood < 0) { //introduce AbsMetric instead of MaxMetric     (# grows 2*N^2),
				continue; } //otherwise I could loop rectangularly (top. equiv., but # grows 4*N^2)
			x = NumCols; //# of Columns
			while (--x >= 0) { //loop through the Columns
				if (Math.abs(x - maxCol) > xNeighborhood) {
					continue; }
				ret += updateElement(x + y * NumCols);
			}
		}
		return ret; }

	/**
	  * Trains the Network to these OutputErrors = Expected - Winner
	  * for the cached Input.
	  * This Routine defines a 3D Topology.
	  * Does both a forward and a backward Propagation.
	  * Calculation of the Backpropagated Error
	  * Also updates all the Weights of this Layer
	  * Learning Law: dWeight = beta * (input-weight)
	  *
	  * TODO: the Gain beta should be decreased with the Distance of the Neurons
	  * to the winning Neuron.
	  * @return the Winner Vector.
	  */
	public float backProp(float beta, int Neighborhood, int NumCols, int NumRows) {
		//No change if input and weight vectors are aligned
		//only update those outputs that are within a neighborhood's distance from the last winner
		//This Neighborhood is now defined in 2 Dimensions with the given NumCols
		++numBackProps;
		this.learn = beta;
		int xNeighborhood;
		int yNeighborhood;
		if (Neighborhood < 0) {
			Neighborhood = 0; }
		int PlnSize= NumRows  * NumCols; //Size of one Tensor Plane
		int maxPln = maxIndex1/ PlnSize; //Plane  of the Maximum
		int maxClRw= maxIndex1% PlnSize;
		int maxRow = maxClRw  / NumCols; //Row    of the Maximum
		int	maxCol = maxClRw  % NumCols; //Column of the Maximum
		int NumPlns= weights.length / PlnSize; //
		int x, y, z; //
		float ret = 0;
		z = NumPlns; //
		while (--z >= 0) { //loop through the Planes
			yNeighborhood = Neighborhood - Math.abs(z - maxPln);
			if (yNeighborhood < 0) {
				continue; } 	//introduce AbsMetric instead of MaxMetric
			y = NumRows; //# of Rows
			while (--y >= 0) { //loop through the Rows
				xNeighborhood = Neighborhood - Math.abs(y - maxRow);	//introduce AbsMetric instead of MaxMetric
				if (xNeighborhood < 0) {
					continue; }
				x = NumCols; //# of Columns
				while (--x >= 0) { //loop through the Columns
					if (Math.abs(x - maxCol) > xNeighborhood) {
						continue; }
					ret += updateElement(x + NumCols*(y + NumRows*z));
				}
			}
		}
		return ret; }

	/**
	 * Can be used to determine the average Dimension of the given Map, should be
	 *  ~ 2 for 1 dimensional Data
	 *  ~ 4 for 2 dimensional Data
	 *  ~ 7 for 3 dimensional Data etc.
	 *
	 * counts the Number of Neighbors that survived the Aging Test.
	 *
	 * TODO: Could be optimized for a symmetric Matrix.
	 * @return the average Number of Neighbors
	 *
	 */
	public float NumNeighbors() {
//		float[] row; //Factor of 0.5 below due to the SqRt of beta in update()! Another Factor of 0.5 for clear Demarcation
		final double threshold = Math.pow (ThresholdBeta, 0.25/ThresholdBeta); //all others should have died away!
		int count = 0; 
		for (int i = connections.length; --i >= 0; ) {
			count += NumNeighbors(i, threshold); }
		return ((float) count)/connections.length; } //(count-len)/len; } //account for the Diagonal!

	/**
	 * Can be used to determine the Dimension of the given Point, should be
	 *  ~ 2 for 1 dimensional Data
	 *  ~ 4 for 2 dimensional Data
	 *  ~ 7 for 3 dimensional Data etc.
	 *
	 * counts the Number of Neighbors that survived the Aging Test.
	 *
	 * Could be optimized for a symmetric Matrix.
	 * @return the Number of Neighbors of the given Neuron
	 */
	public int NumNeighbors(final int neuron) {
		return NumNeighbors(neuron, Math.pow (ThresholdBeta, 0.25/ThresholdBeta)); }

	/**
	 * Can be used to determine the Dimension of the given Point, should be
	 *  ~ 2 for 1 dimensional Data
	 *  ~ 4 for 2 dimensional Data
	 *  ~ 7 for 3 dimensional Data etc.
	 *
	 * counts the Number of Neighbors that survived the Aging Test.
	 *
	 * Could be optimized for a symmetric Matrix.
	 * @return the Number of Neighbors of the given Neuron
	 */
	public int NumNeighbors(final int neuron, final double threshold) {
		final float[] row = connections[neuron];
		int ret = -1; //don't count this Point! Subtract it right away!
		for (int j = row.length; --j >= 0;) {
//			if (row[j] != 0) {
			if (row[j] > threshold) 
				++ret; 
		}
		return ret; }

	/**
	 * Cached Result of the last Progress Calculation
	 */
	protected float progress;

	/**
	 * To force Calculation of the actual Value, call
	 * @see calcProgress()
	 * @return the current Progress of the Approximation
	 */
	public float getProgress() { return progress; } //

	/**Reports how many Back-Propagations have run since the last Weight Randomization.
	 * @return the current Counter of BackPropagations for the Approximation
	 */
	public float getBackPropCounter() { return numBackProps; } //

	/**
	 * Counter for the Number of Back Propagations since the last Randomization.
	 * This Counter is equal to the Sum of all CountWins,
	 * when TRN is used (Type == 0)
	 */
	protected int numBackProps;

	/**
	 * Cached Number of Neurons that have at least once won.
	 */
	protected int numCountWins;

	/**
	 * Minimum Number of Wins for all Neurons
	 */
	protected int minCountWins;

	/**
	  * Calculates Progress Indicators for the
	  * Counter for the least updated Neuron
	  * and for the Number of Neurons updated at all.
	  * @return the Progress of Convergence between [0.0, 1.0].
	  * Crosses 0.5 (50%) when the Process has finished ordering the Topology
	  * and enters the Specialization Phase on the specific Vectors of the Neurons.
	  * The latter Phase is usually faster than the first one.
	  */
	public float calcProgress () {
		numCountWins = 0;
		minCountWins = Integer.MAX_VALUE;
		int i;
		float len = i = numWins.length;
		while (--i >= 0) {
			int count;
			if((count = numWins[i]) > 0) {
				numCountWins++; }
			if (minCountWins > count) {
				minCountWins = count; }
		} //return minCountWins.Value > 0; } //already started to converge
		return progress =
			numCountWins / (2*len) + //50% Number of Bins that have already won/#Bins
			minCountWins*ThresholdBeta*3 / 4.0f; } //50% Winnings of the Minimum Bin

	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Tests the Kohonen Map for purely random Input from [0,1]^Dim with Absolute Norm.
	  * The expected Result is a roughly equidistributed Binning in the same Range.
	  * This is an especially hard Problem for the Map,
	  * because it has to separate Data that is equally distributed and not structured further!
	  * 
	  * This Routine feeds the Data, prints the average Number of Neighbors
	  * and the Number of Iterations necessary for Convergence.
	  */
	public static void testxD(final int type, final int dim, final int numNeurons,
			final double expectedAvgNumNeurons) {
		L.n("\n\nTesting ").l(dim).l(" Dimensional Data with a Kohonen Map:");
		FilterIn_Float2Array FloatStream = new FilterIn_Float2Array(new RandomFast(), dim, false);
		final Kohonen Kohonen = MODEL_DATA(type, dim, numNeurons, FloatStream, new KohonenTester());
		L.n("End after Cycle#").l(Kohonen.numBackProps);
		L.n("Weights:\n").l(Kohonen);
		if (type == 0) { //for fixed Model Dimensions testing makes no Sense!
			L.n("Test the Dimension of some random Points");
			final float avgNumNeighbors = Kohonen.NumNeighbors(); 
			L.n("Average # of Neighbors:").l(avgNumNeighbors);
			Assert.EQUALS(expectedAvgNumNeurons, avgNumNeighbors, 2./numNeurons); 
			//L.n("# of Neighbors for each Point:");
			//for (int i = Kohonen.connections.length; --i >= 0; ) 
			//	L.l(Kohonen.NumNeighbors(i)); 
			L.n("Calculated Dimensions of all individual Points:");
			//Using the Dimension Algorithm of MatrixGraph doesn't help much,
			//because the Distance Information is too sparse
			MatrixFloat.LOG_AT(Kohonen.connections); //results mostly in
			MatrixFloat.NEG_AT(Kohonen.connections); //Distance[i,j] = Infinity
			MatrixFloat.FILL_DIAG_AT(Kohonen.connections, 0, false); //NAN = Infinity / Infinity
			for(int i = Kohonen.connections.length; --i >= 0;) {
				//Fitting doesn't work, since the Distance Matrix is not konnex / connected 
				L.l(MatrixGraph.DIMENSION_BY_FIT(Kohonen.connections, i));
			}
			L.n();
		}
		Assert.GET_AVAILABLE();
	}

	/**
	 * Tests all the Methods of this Class
	 */
	public static void testIt() {
		//ByRefInt.BXP(dim)
		testxD(0, 1, 121, 2); //225^1 = 225 //higher Numbers in a single Dimension don't converge!!!
		testxD(0, 2,  15, 5.5); // 15^2 = 225 //5.58 since the densest Mesh around each Point in a Plane is made of hexagons 6*(1-4/(15*2)=5,2 
		testxD(0, 3,   6,10.1); //  6^3 = 216 //the densest Mesh in 3D is ??? 9.74
		testxD(0, 4,   4,13.2); //  4^4 = 256 //15.1   
		testxD(1, 1, 121, 2); //225^1 = 225 //higher Numbers in a single Dimension converge only locally, only piecewise!!!
		testxD(2, 2,  15, 5.4); // 15^2 = 225
		testxD(3, 3,   6, 8); //  6^3 = 216
		testxD(4, 1, 121, 2); //225^1 = 225 //
		testxD(4, 2,  15, 4); // 15^2 = 225
		testxD(4, 3,   6, 8); //  6^3 = 216
		testxD(4, 4,   4,16); //  4^4 = 256
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws java.io.IOException {
		testIt(); }

}

/**
 * Class for Testing the intermediate Progress of the Kohonen Approximation.
 * Handed over is the actual Kohonen Object with the full Information about the Approximation.
 * Although this is a very broad Interface!
 *
 * Used internally in testIt() to print the Progress to the Screen.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:32:23Z
 * digest: 65a4caef85d3b4e1b0b917c9a637629949b21586d03fec9fe6eb150c1c6290b8
 * stale: false
 * tags: [code/neural_network, code/testing]
 * concepts: [Self-Organizing Map Test Harness]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
class KohonenTester
implements ITester {

	private static final Log L = new Log(KohonenTester.class, 1); 
	
	/**This is the Test working on 'arg' defined by the implementing Class.
	 * The Class implementing this Method is the means of exchanging this Operation.
	 * @param  arg	The Object being 'tested'
	 * @return 	'true' or 'false' depending on the ITester and the Parameter 'arg'	 */
	public boolean test(final Object arg) {
		final Kohonen Kohonen = (Kohonen) arg;
		L.n("Testing Progress:");
		L.l("Loops:").l(Kohonen.numBackProps);
		L.l("Progress:").l(Kohonen.progress);
//		L.l("LearnFactor:").l(Kohonen.beta);
//		L.l("maxDelta:").l(Max1/numInner); //even with amortized Maxima
//		L.l("maxDelta:").l(Max2/numInner); //the Value of currMax determines the
//		Kohonen.printWeights(Kohonen);
		//Random Deviations from the average maximum Distance
		//are assumed to be regularly distributed so they reduce like SqRt(numInner)
		return false; }

}
