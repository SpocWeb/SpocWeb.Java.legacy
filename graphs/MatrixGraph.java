package graphs;

import function.byref.ByRefInt;
import math.matrix.MatrixFloat;
import math.vector.HunterFloat;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.IIterAble;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.exception.OperationNotSupported;
import tester.ITester;

/**Read-only Iterator Class for this Type of Container. 
 * The Items can neither be connected, nor can they modify the Graph itself. 
 * This Iterator is quite similar to the HashTable Iterator.
 * This is one of the many Examples where a Nested structure requires a nested Iterator 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: dce809f172c7ea2c1573c5b8be086eba6a5bc838f735406c1dae41e1e422077b
 * stale: false
 * tags: [code/adjacency_matrix, code/graph_iteration]
 * concepts: [Adjacency Matrix Edge Stream]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
class AdjMatrixEdgeStream 
extends AEdgeStreamIn {

	/** Current Row of the Matrix	 */
	protected float[] currRow;

	/**Next Record, necessary,
	 * because otherwise available() cannot return accurate Values 	 */
	//	protected ListEdge nextEdge;

	/**Local Reference to the Adjacency Matrix	 */
	protected MatrixGraph am;

	/**Initializing Constructor	 */
	protected AdjMatrixEdgeStream(MatrixGraph am_) {
		am = am_;
		reSet();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the number of Nodes in the underlying MatrixGraph.
	 * @see graphs.IEdgeStreamIn#getNumNodes()	 */
	public int getNumNodes() { return am.getInt(); }
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() {
		currEdge.key = 0; //Key is rarely set!
		currEdge.val = -1; //Value is set before the Beginning
		currRow = am.a[0];
		return this;
	}
	
	/**TODO: Empty Dummy Implementation	 */
	public Object removeNext() { throw new OperationNotSupported(); }
	//		return null; }
	
	/** @return the current Node from which the nextItem() emanates */
	//	public int currNode() { return currLine; }
	
	/** returns the currently available Records	 */
	public long availAble() { return 1; }
	
	/** returns the Order of the Items in this List, in this case 'no Order'	 */
	public byte getOrder() { return ORDER_NONE; }
	
	/** removes the current Record, and returns it 	*/
	public Object removeCurrent() throws OperationNotSupported { //ByRefLong available) {
		throw new OperationNotSupported(); }
	
	/** Returns the linear Position within the flattened Matrix, computed as the sum of the lengths of preceding Rows plus the current column.
	 * @see graphs.AEdgeStreamIn#getPosition()	 */
	public long getPosition() {
		long ret = 0;
		for(int i = currEdge.key; --i >= 0;)
			ret += am.a[i].length;
		return ret+currEdge.val; }

	/** Advances to and returns the next Edge, scanning across the current Row and then down subsequent Rows of the Matrix.
	 * @return the next Record, (returning the currently available Records) 	*/
	public Edge nextEdge() { //
		if (++currEdge.val  < currRow.length) { //Items left in the Row...
			currEdge.weight = currRow[currEdge.val]; //
			return currEdge;
		}
		if (++currEdge.key < am.a.length) { //Rows left...
			currRow = am.a[currEdge.key];
			currEdge.val = -1;
			return nextEdge();
		}
		return null;
	} //the Weight is not being transmitted!!!
	
}

/**
 * Fixed Size Matrix Representation (Adjacency Matrix)  
 * of dense (un-)directed Graphs (Trees or Forests). 
 * Also well usable for Flow Problems, 
 * because both the Edge and it's Transpose can be quickly addressed. 
 * TODO: for variable-length Matrices use a Vector of VectorInt.  
 * 
 * The Vertices are not modeled as Vectors directly,
 * but mapped to the Integer Numbers 0..V-1.
 * The Value of the Coefficients a[i,j] represent the Cost
 * of the Connection from Vertex i to Vertex j with an Offset of -1.
 * This is only the Result of the int Representation of $FFFF as -1
 * and not a large positive Number
 * where the Value would represent the Win (= negative Cost).
 * A Cost of =0 (False)	means a simple no Connection.
 * A Cost of >0			means an expensive no Connection (useless).
 * A Cost of -1 (True)	means direct Connection.
 *
 * You cannot process Graphs with both negative and positive Cost,
 * because then you could generate indefinite maximum / minimum Values
 * by just running around the same Cycle (if the Graph contains one).
 *
 * Most Operations are of Order O(V^2),
 * so this Representation is apted best for full Graphs
 * like e.g. euklidean Graphs which are also symmetric.
 *
 * TODO: add Methods that check for a Metric
 * (Triangle Inequation and positive Definiteness)
 *
 * TODO: add Methods that create a Metric. 
 * TODO: add Methods that generate a random undirected Graph. 
 * For a random (undirected) Graph with a typical Scale of n and v Vertices, 
 * just add n*v random Connections (Edges) by setting their Weight to -1 randomly, 
 * which could be done by just iterating through the possible Edges.  
 * 
 * Generating a scale-free Graph requires to keep track of the Vertex Order, 
 * possibly using Bubble Sort, since the Vertices typically only swap their Places. 
 * A Scale free Graph is matched very well by a sparse Graph, 
 * whereas a scaled Graph can be represented both by a dense or a sparse Matrix. 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 030876dd8eaecb39209fc4f0d8e0e4e92ba77709616c45a19c85d1eae9b12ede
 * stale: false
 * tags: [code/adjacency_matrix, code/dense_graph]
 * concepts: [Adjacency-Matrix Graph]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class MatrixGraph 
extends AGraph
implements IIterAble, IGraph {
	
	/** Reference to the Logger for this Class	 */
	private static final Log L = new Log(MatrixGraph.class, 0); 
	
	/**Value representing no Connection between Vertices, typically Infnity	 */
	final static public float FALSE = Float.POSITIVE_INFINITY; // (float) 1.0/0;
	//	final static public int False = 0;
	
	/**Value representing a  Connection between Vertices, typically 0	 */
	final static public float TRUE = 0; //not needed, except for initialization!
	//	final static public int True = ~False;	//not needed, except for initialization!
	
	/** Marker Value for q[] in visit() and Search() for not discovered Nodes.
	  * @see #position Non-negative Values denote a 'black' (finished) Node.
	  * simple negative Values denote a 'GREY' (discovered, but not finished) Node.
	  */
	final static public int WHITE = Integer.MIN_VALUE;

	/** Marker Value in visit() and Search() for discovered, but not finished Nodes.
	  * @see #position Non-negative Values denote a 'black' (finished) Node.
	  */
	//final static public int GREY = -1;
	
	/** Marker Value for q[] in visit() and Search() for finished Nodes.
	  * @see #position Non-negative Values denote a 'black' (finished) Node.
	  */
	//final static public int BLACK = Integer.MAX_VALUE;
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Value representing no Connection between Vertices, typically Infnity	 */
	final public float False; 
	
	/**Value representing a  Connection between Vertices, typically 0	 */
	final public float True; 
	
	/** The Relation / Edge Type reflected by this Matrix 	*/
	public int typ; 
	
	/**Matrix containing the Cost / Weight of the Connections
	 * Has to be ordered and an additive Group,
	 * because used in calculating the minimum Path etc.	 */
	final float[][] a;
	
	/**
	 * returns the internal Representation of the Graph as a full Matrix. 
	 * @return the internal Representation of the Graph as a full Matrix. 
	 */
	public float[][] getList() { return a; }
	
	/** Working Area; contains the weights of the last Operation. 	 */
	private final float[] weights;
	
	/** contains the Path taken, i.e. the Sequence of the Vertices found in the Search 
	 * with non-positive Values indicating the Root of a new Search Tree. 
	 * Elements appear in Sequence when part of the same Subtree.
	 * Negative Elements indicate the Start of a new disconnected Subtree.
	 * @see #position contains the Inverse Permutation.
	 */
	private final int[] sequence;
	
	/** contains the inverse Path, i.e. Vertex Numbers in their Visit Order
	 * generated by Depth or Breadth Search.
	 * Used as Marker for the visited Nodes containing the Values WHITE, GREY (any other Value) or BLACK
	 * simple negative Values denote a 'GREY' (discovered, but not finished) Node.
	 * Non-negative Values denote a 'black' (finished) Node.
	 * On Return, negative Elements indicate the Start of a new disconnected Subtree. 
	 * @see #sequence contains the Inverse Permutation.
	 */
	private final int[] position;
	
	/**Scalar containing the current Counter	 */
	private int counter;
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Stack (LIFO) or Queue (FIFO) for the current Operation,
	 * since the required Space is limited to the Number of Vertices.	 */
	private final int[] ifo;
	
	/**StackPointer,  last Element of IFO	 */
	private int sp = -1;
	
	/**QueuePointer, first Element of IFO	 */
	private int qp = -1;
	
	/** 
	 * push the given Item onto the Stack
	 * @param value the Value to push onto the Stack
	 */
	protected final void push(final int value) { ifo[++sp] = value; }
	
	/**
	 * pull a Value from the Stack or Queue
	 * @param stack Flag whether to use a Stack or a Queue
	 * @return the value pulled
	 */
	protected final int pull(final boolean stack) { return ifo[stack ? sp-- : ++qp]; }
	
	/**
	 * pop a Value from the Stack
	 * @return the last value pushed onto the Stack.  
	 */
	protected final int pop() { return ifo[sp--]; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Returns an Iterator to the Elements of the List. 	 */
	public IIStreamIn Iterator() { return new AdjMatrixEdgeStream(this); }
	
	/**Returns an Iterator to the Elements of the List. 	 */
	public IEdgeStreamIn EdgeIterator() { return new AdjMatrixEdgeStream(this);	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**Constructor sets the number of Vertices
	 * and allocates the Space for the Matrix
	 * @see #setEdge(int, int, boolean, boolean, float) to set the individual Connections later 
	 */
	public MatrixGraph(final int numVertex) {
		this(numVertex, TRUE, FALSE);
	}
	
	/**Constructor sets the number of Vertices
	 * and allocates the Space for the Matrix
	 * The individual Connections can be set later using setEdge()	 */
	public MatrixGraph(final int numVertex, boolean inverseWeights) {
		this(numVertex, 
				inverseWeights ? (float) Math.exp(-TRUE ) : TRUE, 
				inverseWeights ? (float) Math.exp(-FALSE) : FALSE);
	}
	
	/**Constructor sets the number of Vertices
	 * and allocates the Space for the Matrix
	 * The individual Connections can be set later using setEdge()	 */
	public MatrixGraph(final int numVertex, final float initDiag, final float initNonDiag) {
		this.True  = initDiag; 
		this.False = initNonDiag; 
		a       = new float [numVertex][numVertex];
		weights = new float [numVertex + 1];
		sequence   = new int[numVertex];
		position   = new int[numVertex];
		ifo = new int[numVertex];
		if ((initDiag != 0) || (initNonDiag != 0)) 
			clear(); 
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final SparseMatrix AL) {
		this(AL.getInt());
		setEdges(AL.EdgeIterator());
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final SparseMatrix AL, final float initDiag, final float initNonDiag) {
		this(AL.getInt(), initDiag, initNonDiag);
		setEdges(AL.EdgeIterator());
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final MatrixGraph AM) {
		this(AM.getInt());
		setEdges(AM.EdgeIterator());
	}
	
	/**Constructor, allocates the Space for the full euklidean Adjacency Matrix
	  * generated from all the Distances between the given Points.
	  * Calculates all possible Distances from all Points to all others.
	  * @param truncate the Distance at which the Distance is set to Infinity
	  */
	public MatrixGraph(final float[][] vectorsOrDistances, final boolean vectors) {
		this(vectorsOrDistances.length);
		if (vectors) { //calculate all Distances
			MatrixFloat.DIST_MATRIX(a, vectorsOrDistances);
		} else {
			System.arraycopy(vectorsOrDistances, 0, a, 0, vectorsOrDistances.length);
		}
	}
	
	/** Initializing Constructor taking the Number of Nodes
	  * and the Edges in the Order:
	  * {{Start, Stop, directed when != 0, Weight}, ...}
	  * additional individual Connections can be set later using addEdge()	 */
	public MatrixGraph(
		final int numNodes_,
		final int[][] edges_,
		boolean directed) {
		this(numNodes_);
		addEdges(edges_, directed);
	}
	
	/** Initializing Constructor taking the Number of Nodes
	  * and the Edges in the Order:
	  * {{Start, Stop, directed when != 0, Weight}, ...}
	  * additional individual Connections can be set later using addEdge()	 */
	public MatrixGraph(
		final int numNodes_,
		final char[][] edges_,
		boolean directed) {
		this(numNodes_);
		addEdges(edges_, directed);
	}
	
	/** Initializing Constructor taking the Number of Nodes
	  * and the Edges in the Order:
	  * {{Start, Stop, directed when != 0, Weight}, ...}
	  * additional individual Connections can be set later using addEdge()	 */
	public MatrixGraph(
		final int numNodes_,
		final char[][] edges_,
		char offset,
		boolean directed) {
		this(numNodes_);
		addEdges(edges_, offset, directed);
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute if min_ > 0) Value falls between the Interval [min_, max_]. 
	 * 
	 * @param weights_ the full Matrix of Weights for the Graph / Matrix  
	 * @param symmetric_ when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param max_ Maximum Value to accept in this Representation
	 * used to render Graphs sparse (ignore very large Distances). 
	 * @param min_ Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the weights_ are used. 
	 */
	public MatrixGraph(
		double[][] weights_,
		boolean symmetric_,
		double min_,
		double max_) {
		this(weights_.length);
		addEdges(weights_, symmetric_, min_, max_);
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute if min_ > 0) Value falls between the Interval [min_, max_]. 
	 * 
	 * @param weights_ the full Matrix of Weights for the Graph / Matrix  
	 * @param symmetric_ when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param min_ Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the weights_ are used. 
	 */
	public MatrixGraph(
		double[][] weights_,
		boolean symmetric_,
		double min_) {
		this(weights_, symmetric_, min_, Float.POSITIVE_INFINITY);
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute if min_ > 0) Value falls between the Interval [min_, max_]. 
	 * 
	 * @param weights_ the full Matrix of Weights for the Graph / Matrix  
	 * @param symmetric_ when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param max_ Maximum Value to accept in this Representation
	 * used to render Graphs sparse (ignore very large Distances). 
	 * @param min_ Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the weights_ are used. 
	 */
	public MatrixGraph(
		float[][] weights_,
		boolean symmetric_,
		double min_,
		double max_) {
		this(weights_.length);
		addEdges(weights_, symmetric_, min_, max_);
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute if min_ > 0) Value falls between the Interval [min_, max_]. 
	 * 
	 * @param weights_ the full Matrix of Weights for the Graph / Matrix  
	 * @param symmetric_ when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param min_ Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the weights_ are used. 
	 */
	public MatrixGraph(
		float[][] weights_,
		boolean symmetric_,
		double min_) {
		this(weights_, symmetric_, min_, Float.POSITIVE_INFINITY);
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final IEdgeStreamIn edges) {
		this(edges, false); }
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final IEdgeStreamIn edges, boolean transpose) {
		this(edges.getNumNodes());
		addEdges(edges, -1e10, 1e10, transpose);
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final MatrixGraph al_, boolean transpose) {
		this(al_.EdgeIterator(), transpose); }
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final MatrixGraph AM, final double min_, final double max_, boolean transposed) {
		this(AM.getInt());
		addEdges(AM.EdgeIterator(), min_, max_, transposed);
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public MatrixGraph(final MatrixGraph AM, final double min_, final double max_) {
		this(AM, min_, max_, false);
	}
	
	/** Initializing Constructor taking a List of Vectors. 
	 * calculates the full Matrix of Distances from these. 
	 * @param Limit The Factor to multiply the Weights with before comparing them to 1
	 *        Only Connections larger than 1 are being converted!
	 * 
	 * @param coords_ the Coordinates of the Points to construct the Distance Matrix 
	 * @param maxDist the maximum Distance to add to the SparseMatrix. 
	 * setting this to less than @see Double#POSITIVE_INFINITY 
	 * helps to render the Matrix sparse and speeds up local Searches. 
	 * This has the same Effect as splitting up the Problem into Sub-Problems. 
	 */
	public MatrixGraph(final float[][] coords_, final double maxDist) {
		this(coords_.length);
		float[] iPoint; //, jPoint;
		int i = coords_.length;
		while (--i >= 0) {
			iPoint = coords_[i];
			for (int j = i; --j >= 0;) {
				final double dist = Math.sqrt(VectorFloat.DIST_SQR(iPoint, coords_[j]));
				if (dist >= maxDist) {
					continue; }
				addEdge(i, j, false, (float) dist);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Accessor Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Could also be called length() for Consistency, but that would be less descriptive!  
	 * @return the Number of Nodes in this Graph  */
	public int getInt() { return a.length; }
	
	/** Returns the Weight between _start and _end if _typ matches this Matrix's Edge Type, otherwise positive Infinity.
	 * @see graphs.IGraph#getWeight(int, int, int)	 */
	public float getWeight(final int _start, final int _end, final int _typ) {
		if (this.typ != _typ)
			return Float.POSITIVE_INFINITY;
		return a[_start][_end];
	}

	/** Returns the Weight of the directed Edge between _start and _end, ignoring Edge Type.
	 * @see graphs.IGraph#getWeight(int, int)	 */
	public float getWeight(final int _start, final int _end) { return a[_start][_end]; }
	
	/**Dynamically add/remove an Edge to/from the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge */
	public IGraph addEdge(final int start, final int end, final boolean directed) {
		return addEdge(start, end, directed, True); }
	
	/** Adds a (directed) Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param destination Target Node of this Edge
	 * @param directed
	 * @param weight
	 */
	public IGraph addEdge(
			final int origin,
			final int destination,
			final boolean directed,
			final float weight, 
			final int type) { //is ignored!
		return addEdge(origin, destination, directed, weight); 
	}
	
	/** Adds a (directed) Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param destination Target Node of this Edge
	 * @param directed
	 * @param weight
	 */
	public IGraph addEdge(
			final int origin,
			final int destination,
			final float weight, 
			final int type) { //is ignored!
		if (type != this.typ) 
			throw new RuntimeException("This Graph's Type "+this.typ+
					" does not match the requested Edge Type: "+type);
		return addEdge(origin, destination, weight); 
	}
	
	/**Dynamically add an Edge to the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * Always chooses the shorter Edge, if the Edge already was defined.
	 * This generic Method could be shared between Matrix and Vector Representation. 
	 */
	public IGraph addEdge(final int start, final int end, final float weight) {
		//adding a shorter Connection overrides => normalized!
		setEdge(start, end, false, weight); 
		return this; 
	}
	
	/**Dynamically add an Edge to the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * Always chooses the shorter Edge, if the Edge already was defined.
	 * This generic Method could be shared between Matrix and Vector Representation. 
	 */
	public IGraph addEdge(final int start, final int end, final boolean directed, final float weight) {
		//adding a shorter Connection overrides => normalized!
		setEdge(start, end, directed, weight, false);
		return this; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// adding Flow Edges
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param values contains Originating, Target Node and Weight of this Edge
	 */
	public IGraph addFlowEdges(final char[][] values, final char offset) {
		return addFlowEdges(values, offset, 0, values.length); }
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param values contains Originating, Target Node and Weight of this Edge
	 */
	public IGraph addFlowEdges(final char[][] values, final char offset, final int start, final int stop) {
		for(int i = stop; --i >= start;)
			addFlowEdge(values[i], offset); 
		return this; }
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param values contains Originating, Target Node and Weight of this Edge
	 */
	public IGraph addFlowEdge(final char[] values, final char offset) { 
		return addFlowEdge(values[0] - offset, values[1] - offset, values[2]); }
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param target Target Node of this Edge
	 */
	public IGraph addFlowEdge(final int origin, final int target) { 
		return addFlowEdge(origin, target, 1); }
	
	/** Adds a Flow Edge to the Graph together with it's Transpose. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param target Target Node of this Edge
	 * @param capacity the Flow Capacity of this Edge
	 */
	public IGraph addFlowEdge(
		final int origin,
		final int target,
		//final int typ,
		final float capacity) { //null instead of getRootEdge(key) because it is set anyway below...
		addEdge(origin, target,  capacity);
		addEdge(target, origin, -capacity);
		return this; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Counter for the Edges 
	 * Maintaining this Counter is delicate, 
	 * since the Graph can be updated anytime! 	 */
	private int numEdges;
	
	/** Counter for the Edges 
	 * Maintaining this Counter is delicate, 
	 * since the Graph can be updated anytime! 	 */
	public int getNumEdges() { return numEdges; }
	
	/**Adds or sets the Edge Cost in the Graph 
	 * no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param start the starting Vertex
	 * @param end the End Vertex 
	 * @param weight the weight to assign 
	 */
	public float setEdge(final int start, final int end, final float weight) {
		return setEdge(start, end, false, weight); }
	
	/** Sets the Edge between start and end to the default Weight, overriding per the given flags.
	 * @see graphs.IGraph#setEdge(int, int, boolean, boolean)	 */
	public float setEdge(final int start, final int end, final boolean directed, final boolean override) {
		return setEdge(start, end, directed, DEFAULT_WEIGHT, override); }
	
	/**Adds or sets the Edge Cost in the Graph 
	 * no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param start
	 * @param end
	 * @param override Flag whether to override the Weights of existing Connections 
	 * or to merge them according to minimum Length
	 * @param weight
	 */
	public float setEdge(final int start, final int end, final boolean override, final float weight) {
		//if (weight < 0) weight = -weight;	//prevent negative Cost
		final float[] a_start = a[start]; //Speed Optimization 
		final float ret = a_start[end]; 
		final boolean increment = (False == ret); 
		if (override||increment|| (weight < ret)) { //could be made more generic, also to avoid renormalization in sparse Graphs!
			if (increment)
				++numEdges; 
			a_start[end] = weight;
		}
		return ret; 
	}
	
	/**Dynamically add/remove an Edge to/from the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge */
	public float setEdge(final int start, final int end, final boolean directed) {
		return setEdge(start, end, directed, True); }

	/** Sets the (undirected) Edge between start and end to the given Weight, subject to override.
	 * @see graphs.IGraph#setEdge(int, int, boolean, float, boolean)	 */
	public float setEdge(final int start, final int end, final float weight, final boolean directed) {
		return setEdge(start, end, false, weight, directed); }

	/** Sets the Edge between start and end to the given Weight, mirroring it to the reverse Edge unless directed.
	 * @see graphs.IGraph#setEdge(int, int, boolean, float, boolean)	 */
	public float setEdge(final int start, final int end, final boolean directed, final float weight, final boolean override) {
		float ret = setEdge(start, end, override, weight); 
		if (directed)
			return ret;
		ret = Math.min(ret, setEdge(end, start, override, weight)); 
		return ret; 
	}
	
	/**Adds or sets the Edge Cost in the Graph 
	 * but only if the Default Value is smaller than before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 */
	public float setEdge(final int key, final int val) {
		return setEdge(key, val, false, True); }
	
	///////////////////////////////////////////////////////////////////////////
	
	/** adds all (directed) Edge Objects from the given Stream 
	 * @param iter Stream of Edge Objects
	 */
	public int setEdges(final IEdgeStreamIn iter) {
		int ret = 0; 
		for (Edge curr; IIStreamIn.EOI != (curr = iter.nextEdge());) {
			setEdge(curr.key, curr.val, true, curr.weight);
			++ret; 
		}
		return ret; 
	}
	
	//////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////
	
	/**  
	 * clears this Matrix from all Connections
	 */
	public void clear() { fill(True, False); }

	/** fill the Matrix and it's Diagonal 	*/
	public void fill(final float initDiag, final float initNonDiag) { 
		fill((float)initNonDiag);
		for (int i = a.length; --i >= 0; ) {
			a[i][i] =  initDiag; 
		/*	final float[] ai = a[i]; //initialize the whole Matrix, O(V^2)
			for (int j = ai.length; --j >= 0; ) {
				ai[j] = (float)((i == j) ? initDiag : initNonDiag); } 
		*/}
		//		while (--numVertex >= 0)
		//			a[numVertex][numVertex] = True;	//Initialize the Diagonal, linear
	}
	
	/** Returns the Fan-In (sum of incoming Edge Weights) of the given Node.
	 * @see graphs.IGraph#getFanIn(int)	 */
	public double getFanIn(final int node) {
		return MatrixFloat.COL_SUM(this.a, node); }

	/** Returns the Fan-Out (sum of outgoing Edge Weights) of the given Node.
	 * @see graphs.IGraph#getFanOut(int)	 */
	public double getFanOut(final int node) {
		return MatrixFloat.ROW_SUM(this.a, node); }

	/** Returns the Fan-In (sum of incoming Edge Weights) of every Node.
	 * @see graphs.IGraph#getFanIn()	 */
	public float[] getFanIn() {
		return MatrixFloat.COL_SUM(this.a); }

	/** Returns the Fan-Out (sum of outgoing Edge Weights) of every Node.
	 * @see graphs.IGraph#getFanOut()	 */
	public float[] getFanOut() {
		return MatrixFloat.ROW_SUM(this.a); }

	/** fills the Matrix with the given Value 
	 * (although this does not really make sense) 
	 * @param value
	 */
	public void fill(final float value) {
		MatrixFloat.FILL(a, value); 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Graph Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Calculates the minimum Paths from each Node to every other Node.
	 * The transitive Hull of this (directed) Graph is calculated on the fly,
	 * by inserting a new direct Connection for any found indirect Connection
	 * resp. shorter indirect Connection (according to the Triangle Inequation):
	 * a[x,z] = min{y| a[x,y] + a[y,z]}
	 * Since the Hull is typically very dense, you better use the Adjacency Matrix.
	 * 
	 * This transitive Hull is equivalent to the Min,+ Matrix Product 
	 * 
	 * The inverse Operation is calculating the nearest Neighbors
	 * The Spanning Tree is even sparser than the nearest Neighbors,
	 * but loses the Dimension Information!
	 * @return the Matrix containing the Nearest Parent of each Element for later use.
	 */
	public void transitHullAt() {
		/** Flag for the finished Calculation of the transitive Hull	 */
		//for (boolean finished = false; !finished; ) {
		//	finished = true; 
			//the first Sweep should be sufficient according to Sedgewick
			for(int x = a.length; --x >= 0; ) {
				final float[] X = a[x];
				for(int y = a.length; --y >= 0; ) {
					final float dxy; 
					if ((dxy = X[y]) != False) { //for False, because it would lead to a large d anyway!
						final float[] Y = a[y]; //you could skip the Test
						for (int z = a.length; --z >= 0;) {
							final float d; 
							if (X[z] >(d = dxy + Y[z])) {
								X[z] = d;
								//finished = false;
							}
						}
					}
				}
			}
		//}
	}

	/**
	 * Performs a Depth or Breadth Search by traversing all partial Trees.
	 * Uses visit() and local Variables, so it must be synchronized!
	 * p[] contains the Positions of the Vertices, the Inverse Permutation q
	 * contains the Sequence of Visits. Negative Values indicate a new search Tree.
	 *
	 * For an undirected Tree, you have to reset p[] on each call of visit(),
	 * because you cannot always reach all Positions of a connected Component
	 * from any item of this Component.  	 */
	public void traverse(final boolean depthFirst) {
		initSearch(); 
		for(int i = a.length; --i >= 0;) {
			if (position[i] == WHITE) {
				visit(depthFirst, i);
				position[i] = -position[i]; //mark the Beginning of a new Subtree (= new disconnected Component for undirected Graphs)
			}
		}
	}

	/**Iterative Visitor through all Nodes of a partial Tree with k as a Root.
	 * With Depth = true a Depth Search is performed,
	 * otherwise a Breadth Search is done (cannot be done recursively).
	 * p[] contains the Positions of the Vertices on the way,
	 * the Inverse Permutation q contains the Sequence of Visits.
	 * Negative Values indicate a new search Tree 	 */
	protected void visit(final boolean depthFirst, int start) {
		push(start); //cache the Counter for marking the Beginning of a new Subtree;
		do {
			final int k = pull(depthFirst); //pop(k) / get(k)
			final int currDepth = position[k] - 1; //using a manual Stack
			sequence[++counter] = k;   //generate Permutation...
			position[k] = counter; //...and Inverse
			//L.l(((char) (k + 'A')));
			for (int i = a.length; --i >= 0;)
				if (a[k][i] != False)
					if (position[i] == WHITE) {
						position[i]  = currDepth; //GREY; //mark them as 'GREY' i.e. negative Values, BEFORE putting them into the Stack/Queue. This forfeits the Use for detecting Cycles, but allows for detecting Diamonds.
						push(i);
					} //
		} while (sp != qp);
	}
	
	/** 
	 * Returns the Equivalence Representatives for the strongly connected Components.
	 * A Graph with as many Components as Elements is hierarchical, 
	 * but this can be tested faster by checking whether the InDegree is <= 1
	 * 
	 * @return the Representative Node for the Strongly Connected Component that each Node belongs to.  
	 */
	public int[] stronglyConnectedComponents() { return numFragments(true); } //, null, null, false); } //
	
	/**Gives out the the Joint Vertices of a Graph.
	 * The Array p[] contains the Number of Components that a Vertex separates.
	 * Any Problem that can be broken up into Subproblems and be recombined,
	 * can be split up at these Vertices,processed separately and be recombined
	 * Uses visitRec and local Variables, so it must be synchronized!
	 * @return p the Number of Components resulting from removing the respective Node. 	
	 */
	public int[] numFragments(final boolean directed) {
		initSearch(); 
		for (int i = a.length; --i >= 0; ) {
			if (position[i] == WHITE) { //if the Arrays started at 1 and not 0!
				final int k = visitRec(i, directed);
				if ((k == position[i]) && (!directed)) //The highest Vertex was reached...
					sequence[i]--;  //...actually no Joint, but nearly...
			} //...decrease the Number of connected Components.
		}
		return sequence;
	}
	
	/** initializes Stack, Queue and Permutations 	 */
	private void initSearch() {
		counter = sp = qp = -1; //Initialize Stack / Queue
		for (int i = a.length; --i >= 0; ) {
			position[i] = WHITE; 
			sequence[i] = 0;
		} //
	}

	/**Recursive Visitor, used by an Iterator to operate on the Graph.
	 * Performs a Depth Search by traversing partial Trees with k as Root.
	 * Code has been added to give out the Joint Vertices of an undirected Graph.
	 * A Vertex is a Joint when you cannot reach a Position above this Vertex
	 * moving down (along) the Search Tree.
	 *
	 * A similar Algorithm can be used to give out
	 * the strongly connected Components of a directed Graph.
	 *
	 * Joints are very useful for partitionling a Problem into SubProblems,
	 * which can then be solved independently.
	 * E.g. all the Problems solved in this Class can be broken up
	 * into solving the same Problem in the subtrees, if you know the Joints.
	 *
	 * An undirected Graph without Joints is twofold connected,
	 * since you can always find at least two Paths between two Points.
	 *
	 * If a Joint Vertex separates n Subtrees, it appears (n-1) times in the List.
	 * The Roots of the Search Trees (first call of visitRec by SearchRec)
	 * have to be tested separately! 	 */
	protected int visitRec(final int start, final boolean directed) {
		position[start] = ++counter;
		push(start);
		float[] K = a[start];
		int m, min = counter; //for Joint Search
		for (int i = a.length; --i >= 0; ) {
			if (K[i] != False) {
				if (position[i] == WHITE) { 
					m = visitRec(i, directed); //all the following added because of Joint Search
					if ((m >= position[start]) && (!directed)) { //no higher Point could be reached, ...
						++sequence[start];
						//L.l(((char) (start + 'A')));
					} //this is a Joint Point, except if this is not called recursively from visitRec.
				} else
					m = position[i]; //i == t^.v
				if (m < min)
					min = m; //new higher (earlier) Vertex can be reached.
			}
		}
		if ((min == position[start]) && directed) {
			int j;
			do {
				sequence[j = pop()] = start; //mark the Belonging to a strongly connected Component
				position[j] = a.length + 1; //BLACK
				//L.l((char) (j + 'A'));
			} while (j != start);
			//L.n();
		}
		return min;
	}
	
	/**
	 * Searches a RoundTrip by creating the minimum Spanning Tree
	 * and traversing it visiting each node only once.
	 * The Traversal is double as long as the Length of the minimum Spanning Tree,
	 * which has the minimum Length possible.
	 * If you skip already visited Points, the Length gets shorter,
	 * so this gives a Roundtrip that is guaranteed to be between L and 2L.
	 * Returns the Permutation of Points in Order of Visit.	 */
	public void RoundTrip2() {
		minimumDistanceOrSpan(null, -1, -1, null, 0); //The Tree is built up in p, which contains the Parents of all Nodes.
		//The Problem here is that you have only Pointers to the Parents
	}

	/**Searches a RoundTrip by a greedy Algorithm that chooses the Node
	 * that is nearest to the last one, but not visited yet.
	 * Returns the Permutation of Points in Order of Visit.
	 * This does not give the best Roundtrip.
	 * Another Algorithm chooses the best Point to add anywhere
	 * on the existing RoundTrips.	 */
	/*	public float RoundTrip() {
		float Length = 0;
		int i = a.length; while (--i >= 0) IFO[i] = 0;	//Flag for being visited.
		int j = a.length; while (--j >  0)
		{	//find the Node nearest to j
			int k = -1;
			float min = Float.POSITIVE_INFINITY;
			i = j; while (--i >= 0)	//only for symmetric Matrices, otherwise (e.g. directed) not possible.
				if (IFO[k]== 0) if (min > a[i][j]) {min = a[i][j]; k = i;}
			p[j] = k; q[k] = j; IFO[k] = 1;
			Length += min;
		}
		p[0] = a.length-1;	//closing the Circle.
		return Length;
	}
	*/
	
	/** 
	 * returns the maximized flows between all Vertices 
	 * for single Source and Sink. 
	 * For Sinks or Sources with limited Capacity 
	 * simply add an unlimited Sink or Source Vertex 
	 * and an outgoing / incoming Edge with this Capacity. 
	 * For multiple Sinks or Sources just add a SuperSink or SuperSource
	 * and outgoing / incoming Edges with infinite Capacity.
	 * Due to the Symmetry of the Problem, 
	 * it can be solved with only half the Matrix.  
	 * @param start the single Source Node 
	 * @param stop  the single Sink / Drain Node 
	 * @param flow an (optional, null allowed) initial Guess for the Flow
	 * @param limit the Algorithm terminates when the Increment is smaller than this Value 
	 * @return flow or a new Array with maximized Flows. 
	 */
	final public float[][] maximumFlow(final int start, final int stop, float[][] flow, final float limit) {
		if (flow == null)
			flow = new float[a.length][a.length];
		//determine max. Weight to avoid rounding Errors on Subtraction
		final float maxWeight = VectorFloat.MAX_VAL(MatrixFloat.MAX(weights, a)); 
		for(;;) {
			final int[] parents = minimumDistanceOrSpan(weights, start, stop, flow, maxWeight);
			final float increment = maxWeight-weights[stop];
			if (increment <= limit)
				return flow; 
			for(int y = stop, x = parents[stop]; y != start; 
					y = x   , x = parents[x]) 
				flow[y][x] = -(flow[x][y] += increment); //addressing the Transpose Edge is the Reason for choosing a Matrix Representation
		} 
	}
	
	/**
	 * Searches the minimum Spanning Tree or the shortest Paths 
	 * with continuous Distances defined by the Edge-Weights.
	 * The Priority Queue is implemented as an unsorted Array in Val,
	 * because this is most convenient to combine the loops 
	 * for searching and updating the Priorities.
	 *
	 * A negative Sign indicates that the node belongs to the Priority Queue,
	 * a positive Value in Val[] denotes a Node in the tree
	 * with either it's Distance from the nearest Tree Element or from the Root.
	 * The Tree is built up in p, which contains the Parents of nodes.
	 * Iterative Visitor through all Nodes of a partial Tree with k as a Root.
	 * Since Checking all nodes is a O(V^2) Operation anyway,
	 * the Priority Queue is maintained in an unsorted Array with O(V) Search.
	 * @param start the Node to calculate Distances for; if negative, calculates a Spanning Tree 
	 * @param weights optional (null allowed) Array to be filled with the actual Distances 
	 */
	public int[] minimumDistanceOrSpan(final int start) {
		return minimumDistanceOrSpan(weights, start, -1, null, 0); }
	
	/**
	 * Searches the minimum Spanning Tree or the shortest Paths 
	 * with continuous Distances defined by the Edge-Weights.
	 * The Priority Queue is implemented as an unsorted Array in Val,
	 * because this is most convenient to combine the loops 
	 * for searching and updating the Priorities.
	 *
	 * A negative Sign indicates that the node belongs to the Priority Queue,
	 * a positive Value in Val[] denotes a Node in the tree
	 * with either it's Distance from the nearest Tree Element or from the Root.
	 * The Tree is built up in p, which contains the Parents of nodes.
	 * Iterative Visitor through all Nodes of a partial Tree with k as a Root.
	 * Since Checking all nodes is a O(V^2) Operation anyway,
	 * the Priority Queue is maintained in an unsorted Array with O(V) Search.
	 * @param start the Node to calculate Distances for; if negative, calculates a Spanning Tree 
	 * @param weights optional (null allowed) Array to be filled with the actual Distances 
	 */
	public int[] minimumDistanceOrSpan(final int start, final float[] weights) {
		return minimumDistanceOrSpan(weights, start, -1, null, 0); }
	
	/**
	 * Searches the minimum Spanning Tree or the shortest Paths 
	 * with continuous Distances defined by the Edge-Weights.
	 * The Priority Queue is implemented as an unsorted Array in Val,
	 * because this is most convenient to combine the loops 
	 * for searching and updating the Priorities.
	 *
	 * A negative Sign indicates that the node belongs to the Priority Queue,
	 * a positive Value in Val[] denotes a Node in the tree
	 * with either it's Distance from the nearest Tree Element or from the Root.
	 * The Tree is built up in p, which contains the Parents of nodes.
	 * Iterative Visitor through all Nodes of a partial Tree with k as a Root.
	 * Since Checking all nodes is a O(V^2) Operation anyway,
	 * the Priority Queue is maintained in an unsorted Array with O(V) Search.
	 * @param start the Node to calculate Distances for; if negative, calculates a Spanning Tree 
	 * @param stop 	the Node to stop Calculation at if encountered. 
	 * Set to negative Values except for Flow Calculations.  
	 * @param maxWeight the maximum Value of the Weight to offset the Flow Calculation. 
	 * @return a Path or Tree in the Graph, defined by a List of Parent Nodes
	 */
	protected int[] minimumDistanceOrSpan(float[] weights, int start, final int stop, final float[][] flow, final float maxWeight) {
		if (weights == null) 
			weights = this.weights; 
		final boolean minPaths; 
		if (start < 0) { 
			start = 0; 
			minPaths = false;
		} else 
			minPaths = true;
		weights[a.length] = -False; 
		for(int i = a.length+1; --i >= 0;) //also set a Sentinel to the maximum Value
			weights[i] = Float.NEGATIVE_INFINITY; //-False; // initializing to negative Values to indicate all are in the PQueue
		weights [start] = maxWeight*1.1f; //Distance a[i,i] = 0, the actual Value is irrelevant, but it should reflect the Result!
		sequence[start] = -1; //no Parent for the Start Node
		for(int   min = start; min != a.length;) {//min contains the Index of the minimum
			final int   prevMin = min; min  = a.length; 
			final float prevVal = (weights[prevMin] = -weights[prevMin]); //add the minimum Node to the Tree...
			final float[] a_prev = a[prevMin]; //...by negating it's Distance (and setting it to 0 if it was not considered)
			for(int t = a.length; --t >= 0; ) { //loop through the not connected Nodes t
				//if((t == k) || (t == start)) //seemed to be necessary for Flow Calculation...
				//	continue; //...but wasn't actually
				float w_t = weights[t]; 
				if   (w_t >= 0) //already visited, skip it
					continue; 
				//Priority contains the current maximum as a negative Value, min is it's Index
				final float priority; 
				if (flow != null) {
					float residual = -flow[prevMin][t]; //Residual Flow 
					if(a_prev[t] > 0)
						residual += a_prev[t]; //always chooose the Edges with the largest Residual
					priority = Math.min(residual, maxWeight-prevVal) - maxWeight; //Residual is limited
				} else if (minPaths)
					 priority = -(a_prev[t]+prevVal); //or the current (summed up) Length of the Path
				else priority = - a_prev[t]; //the Weight of the Edge = distance from the tree
				if (w_t < priority) { //update the Distances = Priorities
					weights [t] = (w_t = priority); //searches for the Maximum
					sequence[t] = prevMin; //for now... can later be updated
				} //
				if (w_t > weights[min]) {
					min = t; //Minimum Search on opposite Sign!
					if ((t == stop) && (w_t > 0))
						return sequence; 
				}
			}
		}  
		return sequence;
	}
	
	/**
	 * Searches the minimum Spanning Tree or the shortest Paths 
	 * with continuous Distances defined by the Edge-Weights.
	 * The Priority Queue is implemented as an unsorted Array in Val,
	 * because this is most convenient to combine the loops 
	 * for searching and updating the Priorities.
	 *
	 * A negative Sign indicates that the node belongs to the Priority Queue,
	 * a positive Value in Val[] denotes a Node in the tree
	 * with either it's Distance from the nearest Tree Element or from the Root.
	 * The Tree is built up in p, which contains the Parents of nodes.
	 * Iterative Visitor through all Nodes of a partial Tree with k as a Root.
	 * Since Checking all nodes is a O(V^2) Operation anyway,
	 * the Priority Queue is maintained in an unsorted Array with O(V) Search.	 
	 */
	public void visitMinimumOld(final boolean Path, final float[] weights) {
		int i = a.length;
		weights[i] = -False; //maximum Value, Sentinel
		while (--i >= 0) 
			weights[i] = Float.NEGATIVE_INFINITY; //initializing to indicate all are in the PQueue
		float Priority;
		int min = 0;
		weights[0] = 0; //Distance = 0
		do { //loop through the nodes k
			int k = min;
			min = a.length; //add the minimum Node to the Tree
			float[] K = a[k]; //min contains the Index of the minimum
			float T, V = (weights[k] = -weights[k]);
			//by inverting it's Distance (and setting it to 0 if it was not considered)
			//			int t = -1;	while(++t < a.length)	//loop through the not connected Nodes t
			int t = a.length;
			while (--t >= 0) //loop through the not connected Nodes t
				if ((T = weights[t]) < 0) //still in the Queue, consider it
					{ //Priority contains the current maximum, min is it's Index
					Priority = -K[t]; //the Weight of the Edge = distance from the tree
					if (Path)
						Priority -= V; //or the current (summed up) Length of the Path
					if (T < Priority) //if the Value falls, because of a new new TreeNode
						{
						weights[t] = (T = Priority);
						sequence[t] = k;
					} //
					if (T > weights[min])
						min = t; //Minimum Search on opposite Sign!
				}
		}
		while (min != a.length);
	}
	
	/**Returns a String Representation of this Object	 */
	public String toString() {
		java.text.NumberFormat form = new java.text.DecimalFormat("+00;-00");
		StringBuffer S = new StringBuffer();
		int i = -1;
		while (++i < a.length) {
			int j = -1;
			while (++j < a.length) {
				if (a[i][j] == False)
					S.append("   ,");
				else
					S.append(form.format(a[i][j])).append(','); //",\t");
			}
			S.append('\n');
		}
		return S.toString();
	}
	
	/**
	 * Determines the Degree of each Node in an undirected Graph,
	 * i.e. the Number of Edges to this Node.
	 * This is necessary to test whether an 'Euler Circle' can be defined,
	 * which visits all Nodes once and returns to the Starting Point.
	 * This is possible exactly if all Nodes have an even Degree
	 * (here only one half of the Edges are counted!)
	 * For directed Graphs there are much more rigid restrictions! 	 */
	public int[] getOutDegree() {
		final int[] ret = new int[a.length]; 
		for (int i  = a.length; --i >= 0; )
			 ret[i] = getOutDegree(i) ; 
		return ret; 
	}
	
	/**
	 * Determines the Degree of each Node in an undirected Graph,
	 * i.e. the Number of Edges to this Node.
	 * This is necessary to test whether an 'Euler Circle' can be defined,
	 * which visits all Nodes once and returns to the Starting Point.
	 * This is possible exactly if all Nodes have an even Degree
	 * (here only one half of the Edges are counted!)
	 * For directed Graphs there are much more rigid restrictions! 	 */
	public int getOutDegree(final int i) {
		int ret = 0; 
		final float[] ai = a[i]; 
		for (int j = ai.length; --j >= 0; ) //
			if (ai[j] != False) //don't consider the Weight!
				++ret; 
		return ret; 
	}
	
	/**
	 * Determines the Degree of each Node in an undirected Graph,
	 * i.e. the Number of Edges to this Node.
	 * This is necessary to test whether an 'Euler Circle' can be defined,
	 * which visits all Nodes once and returns to the Starting Point.
	 * This is possible exactly if all Nodes have an even Degree
	 * (here only one half of the Edges are counted!)
	 * For directed Graphs there are much more rigid restrictions! 	 */
	public int[] getInDegree() {
		final int[] ret = new int[a.length]; 
		for (int i  = a.length; --i >= 0; ) {
			//ret[i] = getInDegree(i) ; //slightly less effective!
			final float[] ai = a[i]; 
			for (int j = ai.length; --j >= 0; ) //
				if (ai[j] != False) //don't consider the Weight!
					++ret[j]; //slightly more effective!
		}
		return ret; 
	}
	
	/**
	 * Determines the Degree of each Node in an undirected Graph,
	 * i.e. the Number of Edges to this Node.
	 * This is necessary to test whether an 'Euler Circle' can be defined,
	 * which visits all Nodes once and returns to the Starting Point.
	 * This is possible exactly if all Nodes have an even Degree
	 * (here only one half of the Edges are counted!)
	 * For directed Graphs there are much more rigid restrictions! 	 */
	public int getInDegree(final int j) {
		int ret = 0; 
		for (int i = a.length; --i >= 0; ) //
			if (a[i][j] != False) //don't consider the Weight!
				++ret; 
		return ret; 
	}
	
	/**
	 * Determines the Degree of each Node in an undirected Graph,
	 * i.e. the Number of Edges to this Node.
	 * This is necessary to test whether an 'Euler Circle' can be defined,
	 * which visits all Nodes once and returns to the Starting Point.
	 * This is possible exactly if all Nodes have an even Degree
	 * (here only one half of the Edges are counted!)
	 * For directed Graphs there are much more rigid restrictions! 	 */
	public int getDegree(final int col) { return getOutDegree(col); }
	
	/**
	 * Determines the Degree of each Node in an undirected Graph,
	 * i.e. the Number of Edges to this Node.
	 * This is necessary to test whether an 'Euler Circle' can be defined,
	 * which visits all Nodes once and returns to the Starting Point.
	 * This is possible exactly if all Nodes have an even Degree
	 * (here only one half of the Edges are counted!)
	 * For directed Graphs there are much more rigid restrictions! 	 */
	public int[] getDegree() {
		for (int i = a.length; --i >= 0; ) 
			position[i] = 0;
		for (int j = a.length; --j >= 0; ) 
			for (int i = a.length; --i > j; ) //test only half the
				if (a[i][j] != False) //consider the Weight!
				{
					++position[i];
					++position[j];
				}
		return position;
	}
	
	/** Returns the Length of the Path from the Start to the End Node 
	 * along the Path given by the resp. Parents. 
	 * 
	 * @param parents the Result of minimumDistanceTree, a List of parent nodes 
	 * @param start the Start Node to calculate Distance for 
	 * @param end the End Node to calculate Distance for 
	 * @return the Length of the Path
	 */
	public float getLength(final int[] parents, final int start, final int end) {
		float ret = 0;
		for(int i = end; i != start; i = parents[i]) 
			ret += getWeight(parents[i], i);
		return ret;
	}
	
	/**
	 * Returns 
	 * either the Sum of the Path-Lengths along parents from the start to all other Nodes
	 * or the overall Length of the Spanning Tree defined by parents
	 * 
	 * @param parents List of Parent Nodes; the Spanning Tree or Shortest Paths Result 
	 * @param startNode if negative, returns the Spanning Tree Size, otherwise the Path Length Sum
	 * @return either the Sum of the Path-Lengths along parents from the start to all other Nodes
	 * or the overall Length of the Spanning Tree defined by parents
	 */
	public float getLength(final int startNode, final int[] parents) {
		float ret = 0;
		for (int i = this.getInt(); --i >= 0;) { //Loop over all valid Nodes / Links
			if (startNode >= 0) 
				ret += getLength(parents, startNode, i);
			else if (parents[i] >= 0)
				ret += getWeight(parents[i], i); //TODO: 2005-05-31 Parameters were swapped, check whether this is correct!
		}
		return ret;
	}
	
	/**
	  * Tests the Graph for redundant, degenerated Diamonds.
	  * While Diamonds are not allowed in Inheritance, because they are hard to Handle,
	  * they are necessary and the Problems can be avoided using Interfaces.
	  *
	  * This generates something close to the Nearest Neighbor Matrix
	  * where only the 'nearest' Neighbors are kept.
	  * This is still not usable for the Calculation of Dimension,
	  * because 'Neighbors' will be kept that are far away (disconnected).
	  * To eliminate these, calculate the arithmetic Average of the Distances
	  * (do this with the Hull, because the Data Base is broader then)
	  * and remove all Connections that are 'considerably' (e.g. 2 times) longer.
	  *
	  * This was used to make the genls Relation of the Cycorp Ontology
	  * non-redundant, using (B genls C) && (A genls B) => (A genls C)
	  * and eliminating the right genls Relation,
	  * but only in this degenerated Case.
	  * A full or extended Diamond Shape formed by 4 or more Nodes
	  * with at least one extra Node on each Diamond Side is not redundant,
	  * because these extra Nodes add Information and structure.
	  *
	  * The Test for degenerated Diamonds is also easier to implement.
	  * You have to test whether any of the DIRECT Parent Nodes
	  * appears in the Parents of any of the other Parent Nodes.
	  *
	  * Presuming the Graph has no cycles, this is a simple Recursion
	  * without the Need for marking the Elements already visited
	  * (although this could be done for Optimization!)
	  *
	  * The Inverse of eliminateDiamonds() is generating the Hull()
	  */
	//	public void eliminateDiamonds(PrintStream out) { } //TODO:

	/**
	 * Calculates the Nearest Neighbors Matrix from the diamond free Matrix.
	 *
	 * The Nearest Neighbors Matrix is considerably fuller
	 * than the Spanning Tree, but contains Metric Information.
	 * This allows to find the Dimension of a Point
	 * using the Method @see Dimension()
	 *
	 * Actually the Dimension can be found more robustly
	 * by using the Increase in Neighbors at Increase in Sphere Radius.
	 */
	/*	public void nearestNeighbors() {
			int j, i = a.length; //for each Point:
			float[] row;
			while (--i >= 0) {
				float tmp, Sum = 0;
				row = a[i];
				j = a.length; //calculate the harmonic Average of the Distances (without 0!)
				while (--j >= 0) {
					if (0 < (tmp = row[j]))
					Sum += 1/tmp;
				}
			//and remove those that are considerably larger.
			}
		}
	*/
	
	/**
	 * Returns the topological Dimensions of any Point N of this Graph.
	 * The Dimension is based on the Assumption that all Nodes of the Graph are
	 * distributed equally in the given Space.
	 * To achive a statistically meaningful Result, a certain amount of Nodes is needed,
	 * because for one thing the Influence of random Effects grows
	 * and on the other Hand the Dimension is not defined on discrete sets.
	 * @param n the Vertex to calculate the Dimension for 
	 */
	public double getDimension(final int n) { return DIMENSION(a, n); }
	
	/**
	 * Returns the topological Dimensions of any Point N of this Graph 
	 * using a non-parametric Linear Fit.
	 * The Dimension is based on the Assumption that all Nodes of the Graph are
	 * distributed equally in the given Space.
	 * To achive a statistically meaningful Result, a certain amount of Nodes is needed,
	 * because for one thing the Influence of random Effects grows
	 * and on the other Hand the Dimension is not defined on discrete sets.
	 * @param n the Vertex to calculate the Dimension for 
	 */
	public double getDimensionByFit(final int n) { return DIMENSION_BY_FIT(a, n); }
	
	/**
	 * Returns the topological Dimension around the given Point
	 * when applied to the full Distances MatrixGraph.
	 * The Number Of Neighbors grows 
	 * like a Power Function with the Distance and 
	 * like an Exponential with the Dimension.
	 * This Way to calculate Dimension is derived from from analyzing fractal Mappings,
	 * which measures how the Length / Area / Volume etc. (here: the Number of Nodes)
	 * of a Set increases as the Scale considered shrinks / grows.
	 * The Dimension is based on the Assumption that all Nodes of the Graph are
	 * distributed equally in the given Space.
	 * To achieve a meaningful Result, a certain amount of Nodes is needed,
	 * because for one thing the Influence of random and Border Effects grows
	 * and on the other Hand the Dimension is not defined on discrete sets.
	 * Generally the Dimension of a SubsetSet is always at most the Dimension of it's Set.
	 * The Dimension of a fractal Subset can be fractal
	 * and even reach the Dimension of the embedding Set (Peano Curve).
	 *
	 * Assumption: The Density rho = rho(x,y,...,z) is uniform (not true for most Graphs!!!)
	 * N = rho * V = rho * r^d for any Sphere with Radius r.
	 * Thus d = log(N1/N2) / log(r1/r2) and with N1 and N2 fixed = 10 and 20:
	 * d = log(.5)/log(r1/r2);
	 * @param a the full Matrix of all (euklidean) Distances between all Vertices  
	 * @param n the Vertex to calculate the Dimension for 
	 */
	final static public double DIMENSION(final float[][] a, final int n) {
		return DIMENSION(a[n], null); }
	
	/** Returns the topological Dimension around Vertex n, using a non-parametric linear fit over Vertex n's row of the full Distance Matrix a.
	 * @param a the full Matrix of all (euklidean) Distances between all Vertices
	 * @param n the Vertex to calculate the Dimension for
	 */
	final static public double DIMENSION_BY_FIT(final float[][] a, final int n) {
		return DIMENSION_BY_FIT(a[n], null); }
	
	/** cached Result of the read-only Array of Logarithms of Integer Numbers	 */
	protected static float[] logIntegers = new float[0];
	
	/**
	 * Returns the topological Dimension around the given Point
	 * when applied to the full Distances MatrixGraph 
	 * using a non-parametric Linear Fit.
	 * The Number Of Neighbors grows 
	 * like a Power Function with the Distance and 
	 * like an Exponential with the Dimension.
	 * This Way to calculate Dimension is derived from from analyzing fractal Mappings,
	 * which measures how the Length / Area / Volume etc. (here: the Number of Nodes)
	 * of a Set increases as the Scale considered shrinks / grows.
	 * The Dimension is based on the Assumption that all Nodes of the Graph are
	 * distributed equally in the given Space.
	 * To achieve a meaningful Result, a certain amount of Nodes is needed,
	 * because for one thing the Influence of random and Border Effects grows
	 * and on the other Hand the Dimension is not defined on discrete sets.
	 * Generally the Dimension of a SubsetSet is always at most the Dimension of it's Set.
	 * The Dimension of a fractal Subset can be fractal
	 * and even reach the Dimension of the embedding Set (Peano Curve).
	 *
	 * Assumption: The Density rho = rho(x,y,...,z) is uniform (not true for most Graphs!!!)
	 * N = rho * V = rho * r^d for any Sphere with Radius r.
	 * Thus d = log(N1/N2) / log(r1/r2) and with N1 and N2=2*N1 fixed:
	 * d = log(.5)/log(r1/r2);
	 * @param distances the List of all (euklidean) Distances to the other Vertices  
	 */
	final static public float DIMENSION_BY_FIT(final float[] _distances, float[] workArea) {
		//if (index == null)
		//	index  = new int[_distances.length]; 
		if (logIntegers.length < _distances.length) { //== null) {
			final float[] tmp = new float[_distances.length]; 
			System.arraycopy(logIntegers, 0, tmp, 0, logIntegers.length); 
			for (int i = tmp.length; --i >= logIntegers.length;)
				tmp[i] = (float) Math.log(i);
			logIntegers = tmp; 
		}
		workArea = VectorFloat.LOG(_distances, workArea); //TODO: returns wrong Dimensions!  
		//Arrays.sort(workArea, 0, _distances.length); 
		HunterFloat.SORT  (workArea, _distances.length-1, 0); 
		int start = 1; 
		VectorFloat.ADD_AT(workArea, -workArea[start]); //workArea[0] = 0 typically, workArea[1] scales the whole
		int stop = _distances.length; 
		while(workArea[++start] == 0); //avoid 0s in the Fit!
		while(workArea[--stop ] == Float.POSITIVE_INFINITY); //avoid Infinitys in the Fit!
		final float[] sig = workArea; //VectorFloat.INV(workArea); //null; //weigh the Points by their Distance!
		final float[][] a_b = new float[1][2]; 
		//TODO: fitting Interval [start, stop] is still not well defined; due to sharp geometric Features like Edges... 
		//...the Scatter Graph is fragmented into several parallel Lines (same slope=return Value though!) which have to be fitted individually. 
		final double chiSqr = VectorFloat.LINEAR_FIT(workArea, logIntegers, sig, a_b, start, stop); 
		return a_b[0][1];
	}
	
	/**
	 * Returns the topological Dimension around the given Point
	 * when applied to the full Distances MatrixGraph.
	 * The Number Of Neighbors grows 
	 * like a Power Function with the Distance and 
	 * like an Exponential with the Dimension.
	 * This Way to calculate Dimension is derived from from analyzing fractal Mappings,
	 * which measures how the Length / Area / Volume etc. (here: the Number of Nodes)
	 * of a Set increases as the Scale considered shrinks / grows.
	 * The Dimension is based on the Assumption that all Nodes of the Graph are
	 * distributed equally in the given Space.
	 * To achieve a meaningful Result, a certain amount of Nodes is needed,
	 * because for one thing the Influence of random and Border Effects grows
	 * and on the other Hand the Dimension is not defined on discrete sets.
	 * Generally the Dimension of a SubsetSet is always at most the Dimension of it's Set.
	 * The Dimension of a fractal Subset can be fractal
	 * and even reach the Dimension of the embedding Set (Peano Curve).
	 *
	 * TODO: perform a linear Regression through the Distance Function of the Nodes. 
	 *
	 * Assumption: The Density rho = rho(x,y,...,z) is uniform (not true for most Graphs!!!)
	 * N = rho * V = rho * r^d for any Sphere with Radius r.
	 * Thus d = log(N1/N2) / log(r1/r2) and with N1 and N2=2*N1 fixed:
	 * d = log(.5)/log(r1/r2);
	 * @param distances the List of all (euklidean) Distances to the other Vertices  
	 */
	final static public double DIMENSION(final float[] distances, float[] workArea) {
		workArea = VectorFloat.COPY(distances, workArea); 
		//select two meaningful Numbers n1 and n2 for Interpolation
		//e.g. N/10 and N/5 or 10 and 20...
		//...get the according Statistic...
		//...compare the Distances of the given Numbers.
		final int n1 = 20; 
		final int n2 = 40; 
		final int n3 = 60; 
		//make a Copy to prevent Modification and to speed up Sorting.
		//VectorFloat.QuickSort(arr);  //sort the Array...
		//...find the first Gap (for equidistributed Data)
		//To calculate the Statistic...
		//...exploit the Fact that the Array is already half-sorted...
		//...after finding the larger Value
		final int i3 = HunterFloat.GET_STATISTIC_POS(distances, n3);
		final double v3 = distances[i3]; //Get the Values immediately...
		final int i2 = HunterFloat.GET_STATISTIC_POS(distances, n2, 0, i3 - 1);
		final double v2 = distances[i2]; //...because the Array is further modified!
		final int i1 = HunterFloat.GET_STATISTIC_POS(distances, n1, 0, i2 - 1);
		final double v1 = distances[i1]; 
		final double numerator2 = Math.log(n3 / (double) n2); 
		final double numerator1 = Math.log(n2 / (double) n1); 
		final double denominator2 = Math.log(v3 / v2);
		final double denominator1 = Math.log(v2 / v1);
		final double ret1 = numerator1/denominator1;
		final double ret2 = numerator2/denominator2;
		final double ret = 0.5 * (ret1 + ret2); //use the Average
		//start with the smallest nonzero Distance, increase the Scale
		//and measure how many Neighbors fall into this Sphere
		return ret;
	}

	/**
	 * Returns the topological Dimensions of any Point when given only the nearest Neighbors. 
	 * The Topological Dimension is dim(A) = 1+dim(Border(A))
	 * and thus recursively down until A consists only of isolated Points
	 * that have no Border, because they don't have an Environment Filter
	 * thus dim(Border(A)) = dim(0) = 0
	 * Hard to define a real "Gap" or Border in discrete Spaces though, 
	 * very dependent on the local Environment and thus not usable.  
	 * @see #DIMENSION(float[][], int) is both faster and more accurate. 
	 * @see #DIMENSION_BY_FIT(float[][], int) is both faster and more accurate. 
	 * 
	 * @see function.derive.neuron.Kohonen#modelData(int, int, int, int, streamIO.IStreamIn, ITester)
	 * Works excellently with the Matrices returned by the TRN Process in Kohonen.
	 * Returns the exact minimum Dimension for any given Point, e.g. for a 3D Cube:
	 * 0 for the Corners
	 * 1 for the Edges
	 * 2 for the Sides and
	 * 3 for the Points in the inner Volume!
	 *
	 * The Dimension of a Subset is always at most the Dimension of it's Set.
	 * The Dimension of a fractal Subset can be fractal
	 * and even reach the Dimension of the embedding Set (Peano Curve).
	 * 
	 * @param a square Matrix of relative Distances 
	 * @param p the Point to calculate the Dimension for
	 * @return the integer Dimension of the given point  
	 */
	final static public int DIMENSION_OLD(final float[][] a, final int p) {
		final float[] dist = new float[a.length]; // 
		java.util.Arrays.fill(dist, 1); //start with the full Set of Points as Neighbours //or use inverse distance Logic to save Initialization.
		int ret = -1; //start with no Dimension //increase # Dimensions with each loop
		for (int next = p; next >= 0; ++ret) { //start with the current Point	
			final int curr = next; 
			final float[] a_curr = a[curr];
			float min1, min2 = min1 = Float.POSITIVE_INFINITY; //TODO: set the critical Value here!
			final int next2 = next = -1;
			for(int i = dist.length; --i >= 0;) { //do the join manually, don't rely on Sets.
				final float dist_i = (dist[i] *= a_curr[i]); //Multiply the Distances up (sum the Log. Distances)...
				if (dist_i > 0) {
					//actually the Minimum of the Join will be the Minimum > 0 of dist.
					if (min1 > dist_i) { //Problem hier: es gibt 2 und mehr Nachbarn mit gleichem Abstand!
						min1 = dist_i; //min Distance from 1st AND 2nd AND ... Point!
						next = i; //remember the last Point for the next Dim.
					} //find the closest Point...
					if (true) 
						L.l(curr).l(next2).l(min2);
					/*					
					if (min2 > d) {
						if (min1 > d) { //Problem hier: es gibt 2 und mehr Nachbarn mit gleichem Abstand!
							min2 = min1; min1 = d; //min Distance from 1st AND 2nd AND ... Point!
							next2= next; next = i;   //remember the last Point for the next Dim.
						} else {
							min2 = d;
							next2= i;
						}
					}
					*/
				}
			}
		} //(min2 / min1 < 2);//Points within the single and double minimum Distance
		return ret;
	}
	
	/** Refines the given initial Coordinates into a Layout Proposal based on this Graph's Edge Distances.
	 * @return a Proposal for the Coordinates into the given Matrix of the Nodes
	 * based on the Distances.
	 * The given Coordinates are used as initial Guess. */
	public float[][] generateGraphics(final float[][] startPoints) {
		return this.EdgeIterator().generateGraphics(startPoints);
	}

	/** Generates a Layout Proposal with randomly initialized nDim-dimensional Coordinates for the Nodes of this Graph.
	 * @return a Proposal for the nDim Coordinates of the Nodes  */
	public float[][] generateGraphics(final int nDim) {
		return this.EdgeIterator().generateGraphics(this.getInt(), nDim);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests the Calculation of the Dimension 
	 * 
	 * @param dim the Number of Dimensions to model
	 * @param numNeurons the Number of Neurons to use per Dimension 
	 */
	public static void testDimension(final byte dim, final int numNeurons) {
		L.n("\nTesting Calculation of Dimensions with " + dim + "-dimensional Data:");
		//generate a "dense" Set of Vectors in R^1, R^2 or R^3
//		Number of Output Classes, should be an Integer raised to the Power of the Input Dim!
		int outputDim = ByRefInt.POW(numNeurons, dim); 
		final float[][] vectors = new float[outputDim][dim];
		//how to map the recursive Definition of Dimensions to the Sequence of a Vector?
		//This is e.g. done in the Cross Product of infinite Sets in
		//@see Stream.Object.Cantor
		final float scale = 1f / numNeurons;
		for(int i = outputDim; --i >= 0;) { //e.g. by fractal mapping of Intervals to Dimensions.
			//generate the Coordinates from i directly:
			final float[] vector = vectors[i];
			int v = i;
			for (int d = dim; --d >= 0;) {
				vector[d] = (v % numNeurons) * scale;
				//Vektor[d] = (float) Math.random ();
				v /= numNeurons;
			}
		}
		//create the full Adjacency Matrix with all Distances
		MatrixGraph AM = new MatrixGraph(vectors, true);
		double sum1 = 0; 
		double sum2 = 0; 
		//calculate the Dimension around each Point
		for(int i = outputDim; --i >= 0;) { //e.g. by fractal mapping of Intervals to Dimensions.
			final double d1 = AM.getDimension(i);
			sum1+=Math.abs(d1-dim);
			L.l(d1);
			final double d2 = AM.getDimensionByFit(i);
			sum2+=Math.abs(d2-dim);
			L.l("/"+d2);
		}
		L.n("Sum of Differences").l(sum1); 
		L.n("Sum of Fit-Differences").l(sum2); 
		Assert.EQUALS(dim, AM.getDimension(outputDim/2), 1/Math.sqrt(outputDim), "choose the most middle Point");
		if (dim < 3)
			Assert.EQUALS(dim, AM.getDimensionByFit(outputDim/2), 1/Math.sqrt(outputDim), "choose the most middle Point");
	}
	
	/** Tests the Graph Algorithms	 */
	public static void testUnDirectedGraphs() {
		final MatrixGraph AM = new MatrixGraph(13); //for   directed Graphs
		AM.addEdges(SparseGraph.Sedgewick_29_1, false);
		AM.traverse(true); //Searches through the whole tree and gives out the Subtrees
		Assert.EQUALS(new int[] {7, 8, 9, 11, 12, 10, -6, 5, -4, 1, 2, 3, 0}, AM.position);
	}
	
	/** Tests the Graph Algorithms	 */
	public static void testDirectedGraphs() {
		L.n("Testing Directed Graph Algorithms:");		 
		final MatrixGraph AM = new MatrixGraph(13); //for   directed Graphs
		AM.addEdges(SparseGraph.Sedgewick_31_1a, true);
		AM.addEdges(SparseGraph.Sedgewick_32_1, true);
		final int[] expected = { 12, 1, 12, 4, 4, 4, 12, 8, 8, 12, 10, 12, 12 };
		final int[] connComp = AM.stronglyConnectedComponents();
		Assert.EQUALS(expected, connComp, "Strongly connected Components");
		L.n("Which represents the following four Groups: ").n(
			"(B=1), (K=10), (D,E=4,F=5),(A,C,G,J,L,M=12),(H,I=8))");
		L.n(AM);
	}
	
	/** tests Minimum Path and Span for undirected Graphs		 */
	private static void testMinPathAndSpan() {
		final MatrixGraph AM = new MatrixGraph(13); //for undirected Graphs
		final boolean directed = false; 
		AM.addEdges(SparseGraph.Sedgewick_31_1b, directed);
		AM.addEdges(SparseGraph.Sedgewick_31_1a, directed);
		L.n("Graph: ").l(AM);
		
		final int[] minTreeExpected = new int[] { //All valid Alternatives!
		//		-1, 0, 1, 1, 5, 3, 4, 8, 10, 6, 9,  5, 11	
		//		-1, 0, 1, 5, 5, 0, 4, 8, 10, 6, 9, 12,  9
		//		-1, 0, 1, 5, 5, 0, 4, 8, 10, 6, 9,  5, 11
				-1, 0, 1, 5, 6, 0, 9, 8, 10, 12, 9, 5, 11	
		};
		testMinDistanceOrSpan(AM,             minTreeExpected               , 16, -1); 
		testMinDistanceOrSpan(AM, SparseGraph.minDistanceFrom0Sedgewick_31_1, 55, 0); 
		testMinDistanceOrSpan(AM, SparseGraph.minDistanceFrom4Sedgewick_31_1, 38, 4); 
	}
	
	/**
	 * @param AM
	 * @param minDistanceExpected
	 * @param minPathsLength
	 * @param startNode
	 */
	private static void testMinDistanceOrSpan(final MatrixGraph AM, 
			final int[] minDistanceExpected, 
			final int minPathsLength, final int startNode) {
		final String description = "minimal "+((startNode < 0) ? "Spanning Tree" : "Paths to Node "+startNode); 
		L.n("Testing the Calculation of "+description);
		final int[] minDistanceResult = AM.minimumDistanceOrSpan(startNode);
		Assert.EQUALS(minDistanceExpected, minDistanceResult, description);
		Assert.EQUALS(minPathsLength, VectorFloat.SUM(AM.weights, minDistanceExpected.length));
		final float pathsLength = AM.getLength(startNode, minDistanceResult);
		Assert.EQUALS(minPathsLength, pathsLength, "Total Length of the "+description);
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// testing Maximum Flow Algorithm
	///////////////////////////////////////////////////////////////////////////
	
	/** Origin/Target/Capacity triples for a 4-Node Flow test Graph (A-B-D and A-C-D, plus a small C-B cross Edge). */
	final static public char[][] FLOW_GRAPH_1 = {
			{'A', 'B', (char) 1000},
			{'A', 'C', (char) 1000},
			{'B', 'D', (char) 1000},
			{'C', 'D', (char) 1000},
			{'C', 'B', (char)    1}
	};

	/** Expected resulting Flow Matrix (net Flow between Node pairs) for {@link #FLOW_GRAPH_1}. */
	final static public float[][] FLOW_EXPECTED_1 = {
			{0, 1000, 1000, 0},
			{-1000, 0, 0, 1000},
			{-1000, 0, 0, 1000},
			{0, -1000, -1000, 0}
	};

	/** Builds {@link #FLOW_GRAPH_1}, computes the maximum Flow from Node 0 to 3, and asserts it matches {@link #FLOW_EXPECTED_1}. */
	final static public void testMaximumFlow1() {
		final MatrixGraph graph = new MatrixGraph(4, 0, 0); 
		graph.addFlowEdges(FLOW_GRAPH_1, 'A'); 
		final float[][] flow = graph.maximumFlow(0, 3, null, 0);
		Assert.EQUALS(FLOW_EXPECTED_1, flow);
	}
	
	/** Origin/Target/Capacity triples for a 6-Node Flow test Graph (two parallel paths A-B/C-D/E-F). */
	final static public char[][] FLOW_GRAPH_2 = {
			{'A', 'B', (char) 6},
			{'A', 'C', (char) 8},
			{'B', 'D', (char) 6},
			{'B', 'E', (char) 3},
			{'C', 'D', (char) 3},
			{'C', 'E', (char) 3},
			{'D', 'F', (char) 8},
			{'E', 'F', (char) 6}
	};

	/** Expected resulting Flow Matrix (net Flow between Node pairs) for {@link #FLOW_GRAPH_2}. */
	final static public float[][] FLOW_EXPECTED_2 = {
			{ 0, 6, 6, 0, 0, 0},
			{-6, 0, 0, 3, 3, 0},
			{-6, 0, 0, 3, 3, 0},
			{ 0,-3,-3, 0, 0, 6},
			{ 0,-3,-3, 0, 0, 6},
			{ 0, 0, 0,-6,-6, 0}
	};

	/** Builds {@link #FLOW_GRAPH_2}, computes the maximum Flow from Node 0 to 5, and asserts it matches {@link #FLOW_EXPECTED_2}. */
	final static public void testMaximumFlow2() {
		final MatrixGraph graph = new MatrixGraph(6, 0, 0); 
		graph.addFlowEdges(FLOW_GRAPH_2, 'A'); 
		final float[][] flow = graph.maximumFlow(0, 5, null, 0);
		Assert.EQUALS(FLOW_EXPECTED_2, flow);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Tests all Methods of this Class	 */
	public static void testIt() {
		L.n("Testing MatrixGraph:");
		testDimension((byte)1, 100);
		testDimension((byte)2, 16);
		testDimension((byte)3, 8);
		testMaximumFlow2(); 
		testMaximumFlow1(); 
		testUnDirectedGraphs();
		testDirectedGraphs();
		testMinPathAndSpan(); 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws java.io.IOException {
		testIt();
	}

}
