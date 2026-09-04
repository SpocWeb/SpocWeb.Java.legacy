/*
 * File Name: SparseGraph.java
 * Created on: 06.11.2003
 *
 */
package graphs;

import function.PrintOp;
import function.byref.ByRefFloat;
import function.byref.ByRefInt;
import graphic.math2D.Map2DPainter;
import graphic.mvc.BaseApplet;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Arrays;

import math.matrix.MatrixFloat;
import math.matrix.MatrixInt;
import math.vector.VectorInt;
import streamIO.Assert;
import streamIO.Log;
import streamIO.StreamOutPrimitive;
import streamIO.object.parser.jdbc.ResultSetSep;
import streamIO.real.StreamOutPlotter;
import stringOp.HeapByIndex;
import tester.ITester;
import tester.TesterEquals;

/**
 * Title: SparseGraph
 * <p>
 * Extends SparseMatrix by adding numerous Graph Methods. Additional State / Member
 * Variables are added only for cacheing the Graph Operation Results. A dense Matrix is
 * represented best in O(#Vertices²) using a 2D Table (CrossTab Query Layout) A sparse
 * Matrix can be represented in O(#Edges) either relationally as a List of Triples
 * (y,x,a[y,x]) or as an Adjacency List with one Row per Line y: x1, a[y,x1], x2, a[y,x2],
 * ... A sparse Graph with Default Weights can be represented as a List of Pairs (y,x)
 * which is the relational Representation or as an Adjacency List with one Row per Line y:
 * x1, x2, .... The Vertices / Nodes are not modeled directly, but mapped to the Integer
 * Numbers 0..V-1. The Value of the Coefficients a[i,j] represent the Cost and can be
 * found in adj[i], if you find an Element j A Converter could map the Strings read to an
 * Index and thus provide for a bidirectional Mapping between the File and the internal
 * Representation. This Class is very much used in
 * @see streamIO.Object.Enumerator.Container.Relation and
 * @see streamIO.Object.Enumerator.Container.Function Purpose: Adds the Methods for Graphs
 *      to the Super Class SparseMatrix. Known SubClasses: <none> Known Uses: <none>
 *      Similar Classes:
 * @see graphs.MatrixGraph which stores the Graph in a 2D Matrix and thus is best used for
 *      dense Graphs with O(V²) Edges. Copyright: Copyright (c) Matthias Heuer
 *      <p>
 *      Company: personal
 *      <p>
 *      Created on 10-26-2002, 12:47 PM
 *      <p>
 * @author mheuer
 * @version 1.0 TODO: since Graphs are more general than Matrices, you would normaly
 *          derive SparseMatrix from SparseGraph. But only if you would also replace the
 *          float Numbers indicating the Weight / Distance by Objects.
 * Clustering and Sequencing are not quite the same: 
 * Sequencing tries to minimize the Elements above the Diagonal 
 * Clustering tries to minimize the Distance from  the Diagonal 
 */
final public class SparseGraph 
extends SparseMatrix 
implements ITester { // only for
	// Testing for
	// Hamilton
	// Cycles!

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing */
	private static final Log L = new Log(SparseGraph.class, 0);

	/** @see #weights Flag for a not yet visited and thus not considered Vertex */
	protected static final float NOT_VISITED = Float.POSITIVE_INFINITY; // Integer.MAX_VALUE-1;

	/**
	 * Marker Value for q[] in visit() and Search() for not discovered Nodes.
	 * @see #_Position Non-negative Values denote a 'black' (finished) Node. simple
	 *      negative Values denote a 'GREY' (discovered, but not finished) Node.
	 */
	final static public int WHITE = Integer.MIN_VALUE;

	/**
	 * Marker Value in visit() and Search() for discovered, but not finished Nodes.
	 * @see #_Position Non-negative Values denote a 'black' (finished) Node.
	 */
	final static public int GREY = -1;

	/**
	 * Marker Value for q[] in visit() and Search() for finished Nodes.
	 * @see #_Position Non-negative Values denote a 'black' (finished) Node.
	 */
	final static public int BLACK = Integer.MAX_VALUE;

	// ///////////////////////////////////////////////////////////////////////////////////
	// / Member Variables
	// ///////////////////////////////////////////////////////////////////////////////////

	/** Object used for ITester Callbacks */
	protected final ByRefInt curr = new ByRefInt();

	/**
	 * Contains the weights of the last Operation, transient. There is a Value for each
	 * Node plus a Sentinel Value: Val[0]..Val[NumNodes]
	 */
	protected float[] weights;

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * contains the Path taken, i.e. the Sequence of the Vertices found in the Search with
	 * non-positive Values indicating the Root of a new Search Tree. Elements appear in
	 * Sequence when part of the same Subtree. Negative Elements indicate the Start of a
	 * new disconnected Subtree. Also used to collect the # of Fragments on the Search for
	 * Joints
	 * @see #_Position contains the Inverse Permutation.
	 */
	protected int[] _Sequence;

	/**
	 * contains the inverse Path, i.e. Vertex Numbers in their Visit Order generated by
	 * Depth or Breadth Search. Used as Marker for the visited Nodes containing the Values
	 * WHITE, GREY (any other Value) or BLACK simple negative Values denote a 'GREY'
	 * (discovered, but not finished) Node. Non-negative Values denote a 'black'
	 * (finished) Node.
	 * @see #_Sequence contains the Inverse Permutation.
	 */
	protected int[] _Position;

	/** global Counter for Depth and Breadth Search. */
	protected int counter;

	// /////////////////////////////////////////////////////////////////////////

	/** Contains the Path for the topological Sort of this Graph. */
	protected int[] topSort;

	/** current Counter for filling topSort. */
	protected int topCount;

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Represents the Stack (LIFO) or Queue (FIFO) for the current Operation only
	 * (transient), since the Size is limited to the Number of Vertices.
	 */
	protected int[] ifo;

	/** StackPointer, last Element of IFO */
	protected int sp = -1;

	/** QueuePointer, first Element of IFO */
	protected int qp = -1;

	/**
	 * push the given Item onto the Stack
	 * @param value the Value to push onto the Stack
	 */
	protected final void push(final int value) {
		ifo[++sp] = value;
	}

	/**
	 * pull a Value from the Stack or Queue
	 * @param stack Flag whether to use a Stack or a Queue
	 * @return the value pulled
	 */
	protected final int pull(final boolean stack) {
		return ifo[stack ? sp-- : ++qp];
	}

	/**
	 * pop a Value from the Stack
	 * @return the last value pushed onto the Stack.
	 */
	protected final int pop() {
		return ifo[sp--];
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// / Accessor Methods
	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Permutation (int-Vector) containing the Sequence (Permutation) of the Nodes
	 * generated by Depth Search. The Elements are traversed in this Order. This is the
	 * Inverse Permutation to getPositions().
	 */
	public int[] getPath() {
		return _Sequence;
	}

	/**
	 * Permutation (int-Vector) containing the Positions (Permutation) of the Nodes
	 * generated by Depth Search. This is the Inverse Permutation to getPath().
	 */
	public int[] getPositions() {
		return _Position;
	}

	/**
	 * Trims the capacity of this SparseMatrix to be the SparseMatrix's current size. An
	 * application can use this operation to minimize the storage of a SparseMatrix. Could
	 * also be named setInt or setSize, but these Names are already in Use.
	 */
	final public synchronized void trimToSize(final int size) {
		super.trimToSize(size);
		_Sequence = VectorInt.RESIZE(_Sequence, size, itemCount);
		_Position = VectorInt.RESIZE(_Position, size, itemCount);
		topSort = VectorInt.RESIZE(topSort, size, itemCount);
		weights = new float[size + 1]; // VectorFloat.resize(Val, size+1); //last one is
										// a Sentinel for visitMinimum
		ifo = new int[size]; // VectorInt .resize(IFO, size ); //only temporarily used!
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// / Constructors
	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default Value for the Flag to enlarge the Array Sizes to Target Nodes / Columns
	 * from which no Edges originate. Slight Overhead for Matrix and Graph Operations, but
	 * usually necessary for Completeness (but not Correctness or Operation) of Graph
	 * Return Values.
	 */
	public static boolean ENLARGE_TO_DRAIN_END_NODES = true;

	/**
	 * @param NumNodes
	 * @param _capacityIncrement
	 */
	public SparseGraph(final int _numNodes, final int _capacityIncrement) {
		super(_numNodes, _capacityIncrement);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * 
	 */
	public SparseGraph() {
		super();
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param NumNodes
	 */
	public SparseGraph(final int _numNodes) {
		super(_numNodes);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param numNodes_
	 * @param edges_
	 * @param directed
	 */
	public SparseGraph(final int numNodes_, final int[][] edges_, final boolean directed) {
		super(numNodes_, edges_, directed);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param numNodes_
	 * @param edges_
	 * @param directed
	 */
	public SparseGraph(final int numNodes_, final char[][] edges_, final boolean directed) {
		super(numNodes_, edges_, directed);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param numNodes_
	 * @param edges_
	 * @param offset
	 * @param directed
	 */
	public SparseGraph(final int numNodes_, final char[][] edges_, final char offset,
			final boolean directed) {
		super(numNodes_, edges_, offset, directed);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param weights_
	 * @param symmetric_
	 * @param min_
	 * @param max_
	 */
	public SparseGraph(final double[][] weights_, final boolean symmetric_,
			final double min_, final double max_) {
		super(weights_, symmetric_, min_, max_);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param weights_
	 * @param symmetric_
	 * @param min_
	 */
	public SparseGraph(final double[][] weights_, final boolean symmetric_,
			final double min_) {
		super(weights_, symmetric_, min_);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param weights_
	 * @param symmetric_
	 * @param min_
	 * @param max_
	 */
	public SparseGraph(final float[][] weights_, final boolean symmetric_,
			final double min_, final double max_) {
		super(weights_, symmetric_, min_, max_);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param weights_
	 * @param symmetric_
	 * @param min_
	 */
	public SparseGraph(final float[][] weights_, final boolean symmetric_,
			final double min_) {
		super(weights_, symmetric_, min_);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param AL
	 */
	public SparseGraph(final SparseMatrix AL, final boolean transposed) {
		super(AL, transposed);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param coords_
	 * @param maxDist
	 */
	public SparseGraph(final float[][] coords_, final double maxDist) {
		super(coords_, maxDist);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param AM
	 * @param Min
	 * @param Max
	 */
	public SparseGraph(final MatrixGraph AM, final double Min, final double Max,
			boolean transposed) {
		super(AM, Min, Max, transposed);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	/**
	 * @param AM
	 * @param Min
	 * @param Max
	 */
	public SparseGraph(final MatrixGraph AM, final double Min, final double Max) {
		super(AM, Min, Max);
		enlargeToDrainEndNodes = ENLARGE_TO_DRAIN_END_NODES;
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// / Graph Methods
	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return true when the Graph is hierarchic A Graph is hierarchic (a Forest), if
	 *         every Node has at most one Parent and a Tree if only a single Root Node has
	 *         no Parents (Forest can be converted into a Tree by adding an artificial
	 *         Root Node) A Hierarchy is a DAG, but unlike with DAGS the Path to any Node
	 *         is unique. Testing this is an O(E*E) Algorithm but could also be performed
	 *         by checking the Fan-In of each Node (which can be done using the
	 *         Transpose).
	 */
	public boolean isHierarchic() {
		final int[] fanIn = getInDegree();
		for (int i = fanIn.length; --i >= 0;)
			if (fanIn[i] > 1) return false;
		return true;
		/*
		 * if (isHierarchic == UNDEF) traverse(true, null, null, null); return
		 * (isHierarchic == TRUE);
		 */
	}

	/**
	 * Calculates the shortest (minimum) Paths. This Graph Property also works for
	 * negative Weights (only in Directed Acyclic Graphs (DAGs))) and allows considerable
	 * Optimizations compared to the Bellman-Ford Algorithm, resulting in a linear O(V+E)
	 * Time! This Algorithm can also be used to determine the Generating Graph, the
	 * Inverse Operation to the Hull, which consists of all direct Graphs with Weights.
	 * The Generating Graph consists of the longest Paths to the Root. By making all
	 * Weights negative and equal -1 this Minimization yields the Generating Graph. TODO:
	 * cannot be called twice! TODO: die Funktion ist fragwürdig!!! sie geht z.B. gar
	 * nicht auf _startVertex ein!
	 * @return the shortest Paths to the given Vertex given by Pointers to their resp.
	 *         Parent Node in sequence
	 * @see value contains the Weights of the shortest Paths as a Side Effect
	 */
	public int[] shortestPathsInDag(final int _startVertex) {
		/*
		 * initSearch(); //it is quite intuitive to start sorting with the Start Vertex,
		 * visit(true, StartVertex, null, null); //but it is actually not necessary.
		 * doSearch(true, null, null, null); //topological Sorting in p, Inverse in q.
		 * //why does it indicate cycles?
		 */
		traverse(true, null, null, null); // takes the sorted Path
		final int[] p = _Sequence;
		_Sequence = new int[p.length];
		initSingleSource(_startVertex); // initialize Weights & Paths
		// int u, i = p.length;
		// while (--i >= 0) { //for each Vertex u against topological Sort Order...
		for (int u, i = -1; ++i < p.length;) {
			// for each Vertex u in topological Sort Order...
			for (SparseEdge nd = rootNodes[u = p[i]]; nd != null; nd = nd.next) {
				final int v = nd.val; // for each connected Vertex v
				final float testVal = weights[u] + nd.weight;
				if (weights[v] > testVal) { // if the concatenated Distance is shorter...
					weights[v] = testVal; // ...use it.
					_Sequence[v] = u;
					_Position[u] = v; // not needed here!
					// the Shortest Paths are created by p pointing to their Parent
				}
			}
		}
		return _Sequence;
	}

	/**
	 * (Strongly, for directed Graphs) Connected Components are Sets of Vertices that can
	 * be reached from each other, i.e. u->v and v->u From the Definition above follows
	 * directly: A Relation and it's Inverse have exactly the same strongly connected
	 * Components. Strongly Connected Components are important * for partitioning a
	 * Problem into Subproblems, because all Cycles are contained within a Component. *
	 * for dynamic (continuous) Processes, because there is no Transition between
	 * Components For undirected Graphs this defines an 'Equivalence' Relation and can be
	 * calculated faster using this class. The Component Graph is a DAG containing one
	 * Node per Component and one Edge for all Connections between Elements of two
	 * Components. It shows the overall structure of a Problem, whereas the Components
	 * show the Substructure. For undirected Graphs, Decomposition into Components can be
	 * done much faster using 'Equivalence'
	 * @return the Number of (strongly) Connected Components in this Graph. The Component
	 *         of each Node, counting from 0, is stored in sequence[] afterwards.
	 */
	public int numComponents() {
		int ret = -1;
		traverse(true, null, null, null);
		for (int i = -1; ++i < _Sequence.length;) {
			if (_Position[_Sequence[i]] <= 0) ++ret; // returns the Number of negative
													// Numbers
			_Sequence[i] = ret;
		} // indicating the Number of Restarts in the Graph
		return ret + 1;
	}

	/**
	 * The Array size is the total Number of Components.
	 * @return the (strongly) connected Components, one Array of indices per Component
	 */
	public int[][] getComponents() {
		// final int numComponents = numComponents();
		final int[][] arr = new int[itemCount][];
		final int[] tmp = new int[itemCount];
		int j = -1;
		int cmp = -1;
		traverse(true, null, null, null);
		// TODO: check why not numFragments() is used here!!! What is the Difference???
		for (int i = -1; ++i < _Sequence.length;) {
			if (_Position[_Sequence[i]] <= 0) { // new conn. Component starts here
				arr[cmp] = new int[++j]; // write out the old conn. Component
				System.arraycopy(tmp, 0, arr[cmp], 0, j);
				++cmp; // returns the Number of negative Numbers
				j = -1;
			}
			tmp[++j] = i;
			// sequence[i] = cmp; //not necessary
		} // indicating the Number of Restarts in the Graph
		arr[cmp] = new int[++j];
		System.arraycopy(tmp, 0, arr[cmp], 0, j);
		final int[][] ret = new int[++cmp][];
		System.arraycopy(arr, 0, ret, 0, cmp);
		return ret;
	}

	/** Initialize the Search Process for a single or all Paths */
	protected void initSearch() {
		counter = qp = sp = -1; // Initialize Stack / Queue and Counter
		// topCount = itemCount;
		for (int i = itemCount; --i >= 0;) {
			_Position[i] = WHITE; // mark them 'WHITE'
			_Sequence[i] = 0;
		}
	}

	/** Returns the maximum Depth of the Tree starting at 'Root' */
	public int getDepth(final int Root) {
		initSearch();
		visit(false, Root, null, null);
		return counter;
	}

	// Sorting and Order Relations Start

	/**
	 * TODO: equivalent to !isDAG()
	 * @return true when the directed Graph has Cycles
	 */
	/*
	 * public boolean hasCycles() { if (hasCycles == UNDEF) stronglyConnectedComponents();
	 * return (hasCycles == TRUE); }
	 */

	/**
	 * Returns true only if this Graph is a DAG. equivalent to !hasCycles()
	 * @return true when the directed Graph has no Cycles (i.e. is a DAG, a Directed
	 *         Acyclic Graph) Performs a Topological Sort as a Side Effect. A Hierarchy is
	 *         also a DAG, but with the additional Restriction that the Path to any Node
	 *         is unique resp. every Node has a single Parent. A DAG is equivalent to the
	 *         Depth Search having no back Edge and starting from a suitable Root. The
	 *         Sort order can be retrieved in p resp. q by using getPositions() resp.
	 *         getPath(). The Sort Order is from Top to the Elements Any Graph can be
	 *         converted into a DAG by defining Elements within a Cycle as equivalent.
	 *         simplify() returns such a DAG that can be sorted and associated to the old
	 *         Graph using getPath(). A Set of Vertices can be ordered in 1 Dimension, if
	 *         it contains no cycles and all Edged are directed, i.e. the Depth Search
	 *         returns no Back Edges, i.e. the Graph is a DAG The Sort Order is usually
	 *         not unique. The Order relation is also defined by grtr(), less() and
	 *         equals(). When a Graph is not a DAG, but directed, the Elements of a Cycle
	 *         can be defined as a equivalent, representing a single Node and thus a new
	 *         Graph can be defined which is a DAG.
	 */
	public boolean isDAG() {
		final int[] dagPath = getSortSequence(); // invert the Permutation
		// to be able to quickly determine the Rank
		return isDAG(dagPath);
	}

	private boolean isDAG(final int[] dagPath) {
		final int[] dagPositions = VectorInt.INVERSE(dagPath);
		final IEdgeStreamIn iter = EdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			if (edge.val < itemCount)
				if (dagPositions[edge.key] > dagPositions[edge.val]) return false;
		}
		return true;
	}

	/**
	 * Performs a Topological Sort: Returns true only if this Graph is hierarchic. This is
	 * stricter than having no cycles (i.e. being a DAG) A DAG is equivalent to the Depth
	 * Search having no back Edge and starting from a suitable Root. The Sort order can be
	 * retrieved in p resp. q by using getPositions() resp. getPath(). The Sort Order is
	 * from Top to the Elements Any Graph can be converted into a DAG by defining Elements
	 * within a Cycle as equivalent. simplify() returns such a DAG that can be sorted and
	 * associated to the old Graph using getPath(). A Set of Vertices can be ordered in 1
	 * Dimension, if it contains no cycles and all Edged are directed, i.e. the Depth
	 * Search returns no Back Edges, i.e. the Graph is a DAG The Sort Order is usually not
	 * unique. The Order relation is also defined by grtr(), less() and equals(). When a
	 * Graph is not a DAG, but directed, the Elements of a Cycle can be defined as a
	 * equivalent, representing a single Node and thus a new Graph can be defined which is
	 * a DAG.
	 * @return an Array with the topological Sort Order filled up to itemCount.
	 */
	public int[] getSortSequence() {
		traverse(true, null, null, null);
		return topSort;
	}

	/**
	 * sets the position and it's Inverse, sequence so that it reflects the topological
	 * Order.
	 */
	public void sort() {
		getSortSequence();
		// position = topSort;
		for (int i = _Position.length; --i >= itemCount;)
			_Position[i] = _Sequence[i] = i;
		for (int i = itemCount; --i >= 0;) {
			_Sequence[i] = topSort[i];
			_Position[topSort[i]] = i;
		}
	}

	/**
	 * Performs a Depth or Breadth Search by traversing ALL partial Trees. Uses visit()
	 * and local Variables, so it must be synchronized! Is an O(E) Algorithm. Breadth
	 * Search returns the shortest(not metric) equiweight Paths between Edges. Depth
	 * Search returns Node Types and topological Sortings. When testVisit returns true,
	 * the recursion is stopped, When testStart returns true, the Iteration is stopped.
	 * p[] contains the Path taken, q[] contains inverse Path, i.e. the Position of the
	 * Vertices in the Search with non-positive Values indicating the Start of a new
	 * Search Tree. The Number of connected Components can be derived from Both Searches
	 * (for undirected Graphs, directed Graphs have only strongly connected Components) by
	 * counting the Elements <= 0 in q[]. The connected Components are directly yielded
	 * using Connected()
	 * @param performDepthSearch Flag whether to use Depth or Breadth Search
	 * @param testTreeRoot Test to perform when starting a new Tree of the Forest.
	 * @param testDiscover Test to perform when discovering a Node in a Tree.
	 * @param testOnBranch Test to perform before branching from a Node.
	 * @return the Sequence of Nodes encountered.
	 */
	public int[] traverse(final boolean performDepthSearch, final ITester testTreeRoot,
			final ITester testDiscover, final ITester testOnBranch) {
		initSearch();
		for (int i = topCount = itemCount; --i >= 0;)
			if (_Position[i] == WHITE) { // found new disconnected Component
				if (testTreeRoot != null) {
					curr.Value = i;
					if (testTreeRoot.test(curr)) break;
				}
				if (performDepthSearch)
					visitRecursive(i, testOnBranch, testDiscover, null);
				else visit(performDepthSearch, i, testOnBranch, testDiscover);
				_Position[i] = -_Position[i]; // mark the Beginning of a new Subtree, q is
											// written in visit(), so it can be set only
											// here!
			}
		// making it negative interfered with detecting Cycles!
		// detecting Cycles didn't work anyway!
		/*
		 * if (depth) { if (isHierarchic == UNDEF) { //if nothing resets this Flag, it is
		 * hieararchic isHierarchic = TRUE; } }
		 */// hierarchic
		return _Sequence;
	}

	// //////////////////////////////////////////////////////////////////////////

	/**
	 * @return a new, simplified Graph ("Component Graph") consisting only of Component
	 *         Nodes. For undirected Graphs this is the List of connected Component
	 *         Representatives. For directed Graphs this is the List of strongly connected
	 *         Component Representatives and the unidirectional Relations between them.
	 *         This Graph is a directed hierarchic Graph (DAG) and can be used to simplify
	 *         Problems. Uses visitRec() indirectly and local Variables, so it must be
	 *         synchronized!
	 */
	public SparseGraph simplify() {
		// not interested in Fragments here, but in strongly connected Groups!
		final int[] connComp = this.stronglyConnectedComponents();
		// List of connected Components is already what you need...
		// Create a new Adjacency List...
		final SparseGraph ret = new SparseGraph(itemCount);
		// ...loop over all Edges, map both Start and End to the new Connected Components
		for (int j = itemCount; --j >= 0;) { // TODO: should use an Iterator here!
			// any Node represents a Connection, no matter which Weight.
			final int startMap = connComp[j];
			for (SparseEdge t = rootNodes[j]; t != null; t = t.next) {
				final int endMap = connComp[t.val];
				if (startMap != endMap) { // rule out Edges within same Component
					// return only the shortest Connections...
					final SparseEdge e = ret.getEdge(startMap, endMap);
					if (e == null)
						ret.addEdge(startMap, endMap, t.weight);
					else if (e.weight > t.weight) e.weight = t.weight;
				}// ...because you only want the Distance to the Hull!
			} // count In- and Out- Degree.
		}
		return ret;
	} // return both the new Adjacency List and the Mapping!

	/**
	 * Returns the Equivalence Representatives for the strongly connected Components. A
	 * Graph with as many Components as Elements is hierarchical, but this can be tested
	 * faster by checking whether the InDegree is <= 1
	 * @return the Representative Node for the Strongly Connected Component that each Node
	 *         belongs to.
	 */
	public int[] stronglyConnectedComponents() {
		return numFragments(true, null, null, false);
	} //

	/**
	 * Returns the Number of additional Fragments (disconnected Components) resulting from
	 * the Deletion of the respective Node. Values of 1 indicate an inner Node of a
	 * strongly connected Component. Values of 2 or more indicate 'Joint' Nodes that link
	 * strongly connected Component. (AKA 'articulation points') These Joints I.e. those
	 * Vertices, that connect two Subgraphs are important, because they can be used to
	 * integrate two or more local Solutions, which can be found by assuming the Values of
	 * the Joint Nodes. An undirected Graph without Joints (Value > 0) is called doubly
	 * connected, and cannot be affected heavily by removing a single Node, i.e. any
	 * Vertex can be deleted without the Graph to become fragmented.
	 * @return the Number of Fragments (additional to the existing (undirected) Graph)
	 *         resulting from the Deletion of the respective Vertex.
	 */
	public int[] numFragments() {
		return numFragments(null);
	} //

	/**
	 * Returns the Number of additional Fragments (disconnected Components) resulting from
	 * the Deletion of the respective Node. Values of 1 indicate an inner Node of a
	 * strongly connected Component. Values of 2 or more indicate 'Joint' Nodes that link
	 * strongly connected Component. (AKA 'articulation points') These Joints I.e. those
	 * Vertices, that connect two Subgraphs are important, because they can be used to
	 * integrate two or more local Solutions, which can be found by assuming the Values of
	 * the Joint Nodes. An undirected Graph without Joints (Value > 0) is called doubly
	 * connected, and cannot be affected heavily by removing a single Node, i.e. any
	 * Vertex can be deleted without the Graph to become fragmented.
	 * @return the Number of Fragments (additional to the existing (undirected) Graph)
	 *         resulting from the Deletion of the respective Vertex.
	 */
	public int[] numFragments(final SparseGraph collectBridges) {
		return numFragments(false, null, collectBridges, false);
	} //

	/**
	 * For undirected Graphs, returns how many Fragments would result if any of the Nodes
	 * was deleted. Values of 1 indicate an inner Node of a strongly connected Component.
	 * Values of 2 or more indicate 'Joint' Nodes that link strongly connected Component.
	 * These Joints I.e. those Vertices, that connect two Subgraphs are important, because
	 * they can be used to integrate two or more local Solutions, which can be found by
	 * assuming the Values of the Joint Nodes. A doubly connected Graphs has no Joints,
	 * and thus is very robust; i.e. any Vertex can be deleted without the Graph to become
	 * fragmented. The strongly connected Vertices of a directed Graph, resp. the
	 * connected Vertices of an undirected Graph are returned in this.p for Evaluation.
	 * For an undirected Graph: Connected(false) returns the Increment in disconnected
	 * Components resulting from the potential Removal of this Vertex. An undirected Graph
	 * without Joints (Value > 0) is called doubly connected, and cannot be affected
	 * heavily by removing a single Node. For a directed Graph: Connected(true) returns
	 * the Equivalence Representatives for the strongly connected Components. A Graph with
	 * as many Components as Elements is hierarchical. Uses visitRec() and local
	 * Variables, so it must be synchronized!
	 * @return for directed Graphs: the Representative Node for the Strongly Connected
	 *         Component that each Node belongs to. for undirected Graphs: the Number of
	 *         Fragments (additional to the existing (undirected) Graph) resulting from
	 *         the Deletion of the respective Vertex. This Number can be -1 for already
	 *         isolated Nodes 0 for inner Nodes of doubly connected Components and higher
	 *         for Joint Nodes with rising Criticality!
	 */
	protected int[] numFragments(final boolean directed,
			final SparseGraph[] doublyConnectedComponents,
			final SparseGraph collectBridges, final boolean copy) {
		final int numVertices = getNumVertices(); // itemCount //use ALL Vertices...
		// ret[i] = 0; //initially not part of any Group! //Java Arrays are already
		// initialized...
		initSearch();
		// position is used to mark the visited Nodes
		for (int i = numVertices; --i >= 0;)
			//
			if (_Position[i] == WHITE) {
				// make sure that visitRec ist called before evaluating q.
				final int k = visitRec(i, -1, directed, doublyConnectedComponents,
						collectBridges, copy);
				// postprocess the Result for undirected Graphs:
				if ((k == _Position[i]) && !directed) // The highest Vertex was
														// reached...
					--_Sequence[i]; // ...actually no Joint, but nearly...
			} // ...decrease the Number of connected Components.
		return _Sequence;
	}

	/**
	 * Recursive Visitor for Depth Search to identify directed Cycles or doubly connected
	 * Components. Performs a Depth Search by traversing partial Trees with 'start' as
	 * Root. Code has been added to give out the Joint ('articulation point') Vertices. A
	 * Vertex is a Joint when you cannot reach a Position above this Vertex moving down
	 * (along) the Search Tree. Joints are Nodes with one or more Bridge Edges, i.e. Edges
	 * resulting in an Isolation of a SubGraph. By removing all Bridges (including their
	 * Transpose), the Graph is decomposed into it's biConnected Components. Joints are
	 * very useful for partitionling an undirected Problem into SubProblems, which can
	 * then be solved independently. E.g. all the Problems solved in this Class can be
	 * broken up into solving the same Problem in the subtrees, if you know the Joints. An
	 * undirected Graph without Joints is at least twofold connected, since you can always
	 * find at least two Paths between two Points. If a Joint Vertex separates n Subtrees,
	 * it appears (n-1) times in the List. The Roots of the Search Trees (first call of
	 * visitRec by Connected()) have to be tested separately!
	 * @param collectBridges collects all Bridge Edges, otherwise they remain in this
	 *            Graph. If null, Bridges are marked by complementing their Type (=~Type),
	 *            which makes it negative, but is completely reversible. These Edges are
	 *            removed from this Graph to isolate the Components.
	 */
	protected int visitRec(final int start, final int parent, final boolean directed,
			final SparseGraph[] biConnGraphs, final SparseGraph collectBridges,
			final boolean copy) {
		int currCounter, min = _Position[start] = currCounter = ++counter; // increment
																			// unique
																			// Level //for
																			// Joint
																			// Search
		if (directed) push(start); // put the Item on a Stack...
		// loop though the outgoing Edges of start
		for (SparseEdge next, prev = null, curr = rootNodes[start]; curr != null; prev = curr, curr = ((next != null) || (curr == null))
				? next
				: curr.next) {
			next = null; // add the Subnodes to the Stack...
			if (!directed && (curr.val == parent)) // skip direct Back Edges...
				continue; // ...to be able to distinguish Bridges
			final int m;
			SparseGraph target = null;
			if (_Position[curr.val] == WHITE) { // recursively go down...
				m = visitRec(curr.val, start, directed, biConnGraphs, collectBridges,
						copy);
				if (!directed) { // evaluate the Cycles encountered
					if (m >= _Position[start]) { // no higher Point could be reached, ...
						++_Sequence[start];
						// ++sequence[start]; //...add to the Number of disconnected
						// Components.
						if (m >= _Position[start]) { // this is a Bridge Edge
							if (null == (target = collectBridges)) { // only mark the
																		// Edges as
																		// Bridges...
								final SparseEdge trp = getEdge(curr.val, start);
								curr.typ = ~curr.typ;
								if (trp != null) trp.typ = ~trp.typ; // only MARK the
																		// Edges as
																		// Bridges...
							} // ~ is reversible
						} //
					} //
					if ((biConnGraphs != null) && (currCounter >= m)) {// (min >= m)) {
						target = biConnGraphs[m]; // ...add this Edge and it's Transpose
													// to the SubComponent
						if (target == null)
							target = biConnGraphs[m] = new SparseGraph(numVertices);
						// buildUpInnerDoublyConnectedComponents();
					}
				}
			} else { // encountered Grey or Black Node...
				final int p = _Position[curr.val];
				if (curr.val < itemCount) // consider reduced List Size due to
											// Optimization...
					m = p;
				else m = min; // keep min for the Nodes at the End of the List with no
								// originating Edges
				if (null != biConnGraphs) target = biConnGraphs[min]; /*
																		 * if
																		 * ((doublyConnectedComponents !=
																		 * null) &&
																		 * !directed)
																		 * //mark this
																		 * Node as
																		 * Representative...
																		 * if
																		 * (sequence[counter] ==
																		 * 0) { //...for
																		 * collecting
																		 * Edges into this
																		 * doubly
																		 * connected
																		 * Component
																		 * sequence[counter] =
																		 * 1+start;
																		 * //sequence[p] =
																		 * ... =curr.val;
																		 * //after Return
																		 * (subtract 1,
																		 * see above). }
																		 */
			}
			if (target != null) { // happens only with undirected Graphs when both
									// doublyConnectedComponents or collectJoints are
									// given
				if (prev != null) {
					prev.next = next = curr.next;
				} else {
					rootNodes[start] = next = curr.next;
				} // move the current Edge and it's Transpose to the Target Graph
				target.addEdgeWoTranspose(start, curr);
				// by removing the Transpose right away the Graph gets too fragmented!!!
				final SparseEdge trp = removeEdge(curr.val, start);
				target.addEdgeWoTranspose(curr.val, trp);
				curr = prev;
			}
			if (min > m) {
				if (min < currCounter) JOIN(biConnGraphs, min, m); // prevent that
																	// Components are
																	// joined across a
																	// Joint!
				min = m; // new higher (earlier) Vertex can be reached...
			}
		} // ...until the top Item is found
		// when Cycle is closed AND searching for directed Cycles, give out all
		// encountered Items from the Stack
		if ((min == _Position[start]) && directed) { // only useful for directed Graphs:
			int j;
			do { // pop all encountered Vertices...
				_Sequence[j = pop()] = start; // mark the Belonging to a strongly
												// connected Component (i.e. Cycle)
				_Position[j] = BLACK; // itemCount + 1; //mark as visited
			} while (j != start); // ...until ending up at the start Vertex
		}
		return min;
	}

	/**
	 * joins the old Graph in the given Array with the new Graph and cleans up...
	 * @see #visitRec(int, int, boolean, int[], SparseGraph[], SparseGraph) uses this
	 * @param doublyConnectedComponents
	 * @param _old
	 * @param _new
	 */
	private static final void JOIN(final SparseGraph[] doublyConnectedComponents,
			final int _old, final int _new) {
		if (doublyConnectedComponents == null) return;
		if (doublyConnectedComponents[_old] == null) return;
		if (doublyConnectedComponents[_new] == null)
			doublyConnectedComponents[_new] = doublyConnectedComponents[_old];
		else doublyConnectedComponents[_new].join(doublyConnectedComponents[_old]);
		doublyConnectedComponents[_old] = null;
	}

	/**
	 * tried to recursively build up also the inner Doubly Connected Components private
	 * void buildUpInnerDoublyConnectedComponents() { int j = -1; for (int i = counter+1;
	 * --i >= 0;) if (sequence[i] != 0) { //should better use a Stack Pointer for this
	 * than to search from the TOP j = i; break; } if (j >= 0) { final int i =
	 * sequence[j]-1; //added 1, see below if (sequence[start] == i) sequence[j] =
	 * sequence[i] = 0; target = doublyConnectedComponents[i]; if (target == null) target =
	 * doublyConnectedComponents[i] = new SparseGraph(); } if (j > currCounter)
	 * sequence[j] = 0; }
	 */

	/** value uses solely for detecting the shortest Hamilton Cycle */
	protected float minLength;

	/** @see tester.ITester#test(java.lang.Object) */
	public boolean test(final Object arg) {
		final SparseEdge curr = (SparseEdge) arg;
		if (curr.val == 0) if (counter == getInt() - 1) {
			final float length = weights[_Sequence[counter]] + curr.weight;
			L.n("Found Hamilton-Cycle of Length:").l(length).l(_Sequence);
			if (minLength > length) {
				minLength = length;
				System.arraycopy(_Sequence, 0, ifo, 0, _Sequence.length);
				// return true;
		}
	}
		return false;
	}

	public void HamiltonCycle() {
		initSearch();
		minLength = Float.MAX_VALUE;
		// topCount = itemCount;
		boolean ret = visitRecursive(0, null, null, this);
	} //

	/**
	 * generic recursive Visitor for Depth Search using Tester instances to stop on
	 * Discovery or on Finishing a Node.
	 * @see SparseGraph#visit(boolean, int, ITester, ITester) which can also perform Depth
	 *      Search, but only using a Tail Recursion. A Stack can NOT be used, if
	 *      Processing is distributed like in the following Example: void test (int i) {
	 *      A(i); test(i+1); B(i); } Only if ALL local Variables are pushed onto the
	 *      Stack, processing can continue after popping up. But this requires several
	 *      Stacks or an individual Structure to store all local Variables on.
	 * @param start The Node to start searching from
	 * @param testOnBranch optional Test Method called before branching from start Node
	 * @param testDiscover optional Test Method called on discovering new Nodes
	 * @param testOnCycle optional Test Method called on encountering an already found
	 *            Node
	 * @return true when the Test at discovering a Node or the Test at finishing a Node
	 *         succeeded
	 */
	private boolean visitRecursive(final int start, final ITester testOnBranch,
			final ITester testDiscover, final ITester testOnCycle) {
		// position is used in a triple way: Value 'WHITE' denotes non- visited Nodes
		// positive Values of position denote the Position in the Search
		// negative Values of position are the Depth Count (initialized here)
		_Sequence[++counter] = start; // generate inverse Permutation to track Start of
										// new Subtrees, not really necessary!
		_Position[start] = counter; // generate Permutation to track the Path (mark them
									// 'black')
		if (testOnBranch != null) {
			curr.Value = start;
			if (testOnBranch.test(curr)) return true;
		}
		for (SparseEdge t = rootNodes[start]; t != null; t = t.next) {
			// visit all neighboring nodes and put them into the Store
			if (_Position[t.val] == WHITE) { // use only WHITE Nodes (not visited yet, not
											// GREY or black Nodes).
				_Position[t.val] = GREY; // mark them as 'GREY' i.e. negative Values,
										// BEFORE putting them into the Stack/Queue. This
										// forfeits the Use for detecting Cycles, but
										// allows for detecting Diamonds.
				weights[t.val] = weights[start] + t.weight; // accumulate the Path Length
				if ((testDiscover != null) && // Simultaneously remember the Depth
						testDiscover.test(t)) return true;
				visitRecursive(t.val, testOnBranch, testDiscover, testOnCycle); // push(i);
			} else { // either GREY or even Black; has been visited already...
				// isHierarchic = FALSE; //doesn't depend on c.
				if ((testOnCycle != null) && // curr.Value = t.val;
						testOnCycle.test(t)) // curr));
					return true;
			} // because then the encountered Node could be from a previous walk.
		} // This is the PostProcessing that cannot be easily performed...
		if (testOnCycle != null) {
			_Sequence[counter--] = 0; // generate inverse Permutation to track Start of
										// new Subtrees, not really necessary!
			_Position[start] = WHITE; // mark them WHITE again as if they were not
										// visited.
		} else {
			topSort[--topCount] = start; // ...when using a simple Stack!
		}
		return false;
	} //

	/**
	 * @return the Diameter of a connected Component is the Maximum Distance between two
	 *         Elements of the tree. Depth <= Diameter <= 2*Depth It can be calculated as
	 *         the Depth of the Search Tree
	 */
	/*
	 * public int Diameter(int Root) { initSearch(); visit(false, Root, null); return
	 * Counter; }
	 */

	/**
	 * Iterative Visitor through all Edges of one partial Tree with k as a Root. This is
	 * an O(?) Algorithm. With Depth = true a Depth Search is performed, otherwise a
	 * Breadth Search is done using IFO (cannot be done recursively). Depth Search returns
	 * the topological Sort in p Breadth Search returns the (discrete) shorted Paths in p
	 * p[] contains the Path taken, q[] contains inverse Path, i.e. the Position of the
	 * Vertices in the Search with non-positive Values indicating a new Search Tree
	 */
	protected boolean visit(final boolean depthFirst, final int start,
			final ITester testFinish) {
		return visit(depthFirst, start, testFinish, null);
	}

	/**
	 * Iterative Visitor through all Edges of one partial Tree with k as a Root. This is
	 * an O(?) Algorithm. With Depth = true a Depth Search is performed, otherwise a
	 * Breadth Search is done using IFO (cannot be done recursively). Depth Search returns
	 * the topological Sort in p Breadth Search returns the (discrete) shorted Paths in p
	 * p[] contains the Path taken, q[] contains inverse Path, i.e. the Position of the
	 * Vertices in the Search with non-positive Values indicating a new Search Tree
	 * @return true when
	 */
	protected boolean visit(final boolean depthFirst, final int start) {
		return visit(depthFirst, start, null, null);
	}

	/**
	 * Iterative Visitor through all Edges of one partial Tree with k as a Root. Called
	 * for each new Subtree. This is an O(E) Algorithm. Switches between Depth and Breadth
	 * Search using IFO. With Depth = true a Depth Search is performed, otherwise a
	 * Breadth Search is done using IFO (cannot be done recursively). Depth Search returns
	 * a Characterization of the Nodes in p Breadth Search returns the (discrete) shorted
	 * Paths in p
	 * @see #visitRec(int, boolean, int[]) which performs a Depth Search using Recursion,
	 *      since this non-recursive Depth Search has no "return Processing", i.e. it is a
	 *      tail Recursion and cannot be used to calculate e.g. the topological Sort.
	 * @see #_Sequence contains the inverse Path, i.e. Vertex Numbers in their Visit Order
	 * @see #_Position contains the Path taken, i.e. the Position of the Vertices in the
	 *      Search with non-positive Values indicating the Root of a new Search Tree
	 * @return true when testFinish becomes true, false otherwise
	 */
	public boolean visit(final boolean depthFirst, final int start,
			final ITester testFinish, final ITester testDiscover) {
		// q is used in a triple way: Value 'WHITE' denotes non- visited Nodes
		// positive Values of q denote the Position in the Search
		// negative Values of q are the Depth Count (initialized here)
		push(start);
		_Position[start] = 0;
		do { // 'Recursion' via Stack resp. Queue for Depth resp. Breadth Search
			final int k = pull(depthFirst); // pop(k) at the Beginning is a Problem,
											// because it is overwritten!
			final int currDepth = _Position[k] - 1; // using a manual Stack
			_Sequence[++counter] = k; // generate inverse Permutation to track Start of
										// new Subtrees, not really necessary!
			_Position[k] = counter; // generate Permutation to track the Path (mark them
									// 'black')
			if (testFinish != null) {
				curr.Value = k;
				if (testFinish.test(curr)) return true;
			}
			if (visitNeighbors(depthFirst, testDiscover, currDepth, k)) { return true; }
		} while (sp != qp);
		// while there are Elements in the Store (Queue or Stack)
		return false;
	}

	/**
	 * @see #visit(boolean, int, ITester, ITester) which uses this method exclusively to
	 *      test all Neighbors of Node k
	 * @param depthFirst
	 * @param testDiscover Tester to find Elements
	 * @param currDepth
	 * @param k the Node to test all Neighbors for
	 * @return
	 */
	private boolean visitNeighbors(final boolean depthFirst, final ITester testDiscover,
			final int currDepth, final int k) {
		for (SparseEdge t = rootNodes[k]; t != null;) {
			// visit all neighboring nodes and put them into the Store
			if (_Position[t.val] == WHITE) { // WHITE Nodes are not visited yet
				_Position[t.val] = currDepth; // GREY; //mark them as 'GREY' i.e.
												// negative Values, BEFORE putting them
												// into the Stack/Queue. This forfeits the
												// Use for detecting Cycles, but allows
												// for detecting Diamonds.
				if (testDiscover != null) {
					curr.Value = t.val;
					// Simultaneously remember the Depth for the Distance() Function!
					if (testDiscover.test(curr)) { return true; }
				} // remember for further Processing
				push(t.val);
				// } else { //has been visited already...
				// if (depthFirst) { //marking new SubTrees w. negative Values is not
				// working here, only in the calling Routine!
				// if (c < 0) { //LightGREY Node => Diamond Edge in Depth Search!
				// if (c < -TREE_START) { //GREY Node => back Edge in Depth Search! =>
				// Cycle
				// isHierarchic = FALSE; //doesn't depend on c.
				// } else { acyclic = -1; }
				// } //but only if it has no new Starting Points,
			} // because then the encountered Node could be from a previous walk.
			t = t.next;
		}
		return false;
	} //

	/**
	 * returns all discrete Distances from the given Starting Point
	 * @param start the Point to calculate all discrete Distances (i.e. #Edges without
	 *            considering their Weight) from the given Starting Point
	 * @return all discrete Distances from the given Starting Point
	 */
	public int[] getDiscreteDistances(final int start) {
		initSearch();
		visit(false, start, null, null);
		return _Position;
	}

	/**
	 * @return the number of Nodes between i and j in the Direction of the Relation or -1
	 *         if there is no Connection. Uses visit() and local Variables, so it must be
	 *         synchronized! Breadth Search returns the shorted Paths in p and stops as
	 *         soon as the Result is found! sequence[] contains the Path taken (not useful
	 *         with Depth Search), positions[] contains inverse Path, i.e. the Position of
	 *         the Vertices in the Search with non-positive Values indicating a new Search
	 *         Tree
	 */
	public int getDiscreteDistance(final int start, final int stop) { // boolean
																		// depthFirst) {
		if (start == stop) return _Sequence[start] = 0;
		// special Case not handled by Recursion
		initSearch();
		if (visit(false, start, null, new TesterEquals(new ByRefInt(stop))))
			return -_Position[stop];
		// Counter; // Counter only gives the Position in the Search, not the absolute
		// Depth!
		return -1;
	} // StackPointer SP gives the Distance on Depth Search, but not on Breadth Search.

	// /////////////////////////////////////////////////////////////////////////
	// Sorting and Order Relations Start
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return true, when the Stop Node lies in the Direction of the Relation as seen from
	 *         the Start Node ( Start > Stop ). This is only valid, if the Graph doesn't
	 *         contain cycles, so it defines an Order Relation and can be topologically
	 *         sorted. Uses visit() and local Variables, so it must be synchronized!
	 *         Breadth Search returns the shorted Paths in p p[] contains the Path taken,
	 *         q[] contains inverse Path, i.e. the Position of the Vertices in the Search
	 *         with non-positive Values indicating a new Search Tree
	 */
	public boolean grtr(final int start, final int stop) {
		return getDiscreteDistance(start, stop) > 0;
	}

	/**
	 * @return true, when the Stop Node lies in the Direction of the Relation as seen from
	 *         the Start Node ( Start < Stop ). This is only valid, if the Graph doesn't
	 *         contain cycles, so it defines an Order Relation and can be topologically
	 *         sorted. Uses visit() and local Variables, so it must be synchronized!
	 *         Breadth Search returns the shorted Paths in p p[] contains the Path taken,
	 *         q[] contains inverse Path, i.e. the Position of the Vertices in the Search
	 *         with non-positive Values indicating a new Search Tree
	 */
	public boolean less(final int start, final int stop) {
		return getDiscreteDistance(stop, start) > 0;
	}

	/**
	 * @return true, when the Stop Node lies in the Direction of the Relation as seen from
	 *         the Start Node ( Start < Stop ). This is only valid, if the Graph doesn't
	 *         contain cycles, so it defines an Order Relation and can be topologically
	 *         sorted. Uses visit() and local Variables, so it must be synchronized!
	 *         Breadth Search returns the shorted Paths in p p[] contains the Path taken,
	 *         q[] contains inverse Path, i.e. the Position of the Vertices in the Search
	 *         with non-positive Values indicating a new Search Tree
	 */
	public boolean equals(final int start, final int stop) {
		if (start == stop) return true;
		return ((getDiscreteDistance(stop, start) >= 0) && (getDiscreteDistance(start,
				stop) >= 0));
	}

	// /////////////////////////////////////////////////////////////////////////
	// Sorting and Order Relations Stop
	// Equivalence Relation Operations Start
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the (current) Root(s) of this Element when defining Equivalences. This is
	 *         the fastest way, but it does not reduce the needed time for the next Search
	 *         like the other Implementations.
	 */
	public int lastItemFast(int x) {
		int p;
		// Node currNode; //this would make it a 2D Recursion not returning a unique, but
		// multiple Roots!
		while ((p = rootNodes[x].val) >= 0)
			// != x) //this test is for the Root pointing to itself!
			x = p;
		return x;
	}

	/**
	 * @return the (current) Root of this Element when defining Equivalences. Reduces the
	 *         Height of the Tree by one on every Level. Uses two indexed accesses, so it
	 *         is very fast too!
	 */
	public int lastItem(int x) {
		int pp, p;
		if ((p = rootNodes[x].val) < 0) return x;
		while ((pp = rootNodes[p].val) >= 0) {
			// && (pp != x)) //this test is for both the Root pointing to itself and
			// weighted Roots!
			rootNodes[x].val = pp;
			x = p;
			p = pp;
		}
		return p;
	}

	/**
	 * @return the (current) Root of this Element when defining Equivalences. Reduces the
	 *         Height of the Tree to one on every Element of the Search Tree. Uses
	 *         Recursion, so it is slower than lastItem on the first Operation, but since
	 *         replaceAt() is an expensive Operation, the savings are great!
	 */
	public int lastItemTotal(final int x) {
		int pp, p;
		if ((p = rootNodes[x].val) < 0) return x;
		rootNodes[x].val = (pp = lastItemTotal(p));
		// find the root and set it to ALL Elements on the way back.
		return pp;
	}

	/**
	 * @return true, if key and Value are equivalent, considering all Relations as
	 *         Equivalence Relations.
	 */
	public boolean isEquivalent(final int Key, final int Value) {
		return isEquivalent(Key, Value, false);
	}

	/**
	 * Checks both Elements for Equivalence by comparing their Roots. After this they are
	 * optionally united using the gathered Information.
	 */
	public boolean isEquivalent(int x, int y, final boolean union) {
		final boolean ret = ((x = lastItem(x)) == (y = lastItem(y))) || (x == y);
		if (union && (!ret)) rootNodes[x].val = y; // or parent[y] = x;
		return ret;
	}

	/**
	 * Checks both Elements for Equivalence by comparing their Roots. After this they are
	 * optionally united using the gathered Information. The union is done in a balanced
	 * way, storing the Number of child Elements as negative Values in parent[]. This
	 * leads to a reduced average time on the next Search.
	 */
	public boolean isEquivalentAVG(int x, int y, boolean union) {
		final boolean ret = ((x == y) || (x = lastItem(x)) == // x = lastItemFast(x);
		(y = lastItem(y))); // y = lastItemFast(y);
		if (union && !ret) { // concatenate both trees so the resulting Tree is wider.
			if (rootNodes[x].val < rootNodes[y].val) {
				rootNodes[x].val += rootNodes[y].val - 1;
				rootNodes[y].val = x;
			} else {
				rootNodes[y].val += rootNodes[x].val - 1;
				rootNodes[x].val = y;
			}
		}
		return ret;
	}

	/**
	 * Checks both Elements for Equivalence by comparing their Roots. After this they are
	 * optionally united using the gathered Information. The union is done in a balanced
	 * way, storing the Height of the Subtree as negative Values in parent[]. This leads
	 * to a reduced maximum time on the next Search (worst case).
	 */
	public boolean isEquivalentMAX(int x, int y, final boolean union) {
		final boolean ret = ((x == y) || (x = lastItem(x)) == // x = lastItemFast(x);
		(y = lastItem(y))); // y = lastItemFast(y);
		if (union && (x != y)) {
			// concatenate both trees so the resulting Tree is smaller.
			if (rootNodes[x].val < rootNodes[y].val) {
				--rootNodes[x].val;
				rootNodes[y].val = x;
			} else {
				--rootNodes[y].val;
				rootNodes[x].val = y;
			}
		}
		return ret;
	}

	// ////////////////////////////
	// Connected Components
	// ////////////////////////////

	/**
	 * Returns the maximum Numbers of Edges for the given Number of Nodes and Components.
	 * If the Number of Edges exceeds it, the Number of Components is smaller.
	 */
	public static int MAX_NUM_EDGES(int nodes, final int components) {
		nodes -= components;
		return nodes * (nodes - 1) >> 1;
	}

	// ////////////////////
	// Minimum Trees
	// ////////////////////

	/**
	 * Calculates the transitive Hull, the Inverse Operation to the Generating Graph,
	 * which consists of all Nodes directly connected with Weights. For a singly connected
	 * Graph rather use the MatrixGraph Representation, because the Hull will be complete
	 * (connects ALL Nodes with ALL others) and it's Calculation is one Magnitude O(V)
	 * faster. Non-negative Weight Graphs can define a Metric on the Set of Nodes, if the
	 * Weights fulfill the Triangle Inequation. This Metric is calculated in Hull(). The
	 * Hull can be calculated by following each Path and counting up the Weights along the
	 * way. If a shorter direct Connection exists, use that one. The Inverse of generating
	 * the Hull() is eliminateDiamonds() But the actual Inverse is constructed by
	 * transitiveReduction.
	 * @param considerDist Flag whether to ???
	 */
	public void createHull(final boolean considerDist) {
		// isHierarchic = UNDEF; //-1; //The Hull is never herarchic, but contains
		// Diamonds.
		boolean changed;
		do {
			changed = false;
			for (int i = itemCount; --i >= 0;) { // for each Vertex i
				changed |= createHull(i, i, 0, true, considerDist);
			} //
		} while (changed);
	}

	/**
	 * recursive Generation of the transitive Hull / Closure. Stops when there are no
	 * further Edges or the Edges become longer than the existing ones.
	 * @return true, when the Graph has changed, because it may be necessary to repeat the
	 *         Generation Process until the Graph is stable. The Inverse Operation of
	 *         generating the transitive Hull() is eliminateDiamonds() but only a partial
	 *         Inverse the actual Inverse is called transitive Reduction. A (non-optimal)
	 *         Reduction can be constructed by -converting all strong connected Components
	 *         into simple Cycles -replacing all Connections to any Element of the
	 *         strongly connected Component by a single Connection to the Representative
	 *         resulting in the shortes overall Distance.
	 * @param startNode
	 * @param currNode the Node to create the Closure for
	 * @param dist
	 * @param start
	 * @param considerDist Flag whether to ???
	 * @return 
	 */
	protected boolean createHull(final int startNode, final int currNode,
			final float dist, final boolean start, final boolean considerDist) {
		boolean ret = false;

		// not caching the Start Position here! Prevents duplicate adding e.g. 762
		// SparseEdge StartNd = Nodes[StartNode]; //
		// boolean start = (StartNode == Curr_Node);

		// for all connected Nodes...
		for (SparseEdge currNd = rootNodes[currNode]; currNd != null;) {
			// construct the TestDistance for a new Connection
			final float newDist = dist + currNd.weight; // distance to Curr_Node
			SparseEdge testNd = rootNodes[startNode];
			while (testNd != null) { // search for an existing, shorter Connection
				if (currNd.val == testNd.val) {
					if (testNd.weight > newDist) {
						// found existing longer Path...
						testNd.weight = newDist;
						createHull(startNode, testNd.val, newDist, false, considerDist);
						// recursively go on...
						if (considerDist) 
							ret = true;
					} else if (start) { // when starting, no longer Path is found first!
						ret = ret
								|| createHull(startNode, testNd.val, newDist, false,
										considerDist);
						// recursively go on...
					}
					break; // found a shorter or longer Path
				}
				testNd = testNd.next;
			}
			if (testNd == null) {
				// Didn't exist yet... prepend the new Connection SparseEdge
				testNd = (rootNodes[startNode] = new SparseEdge(currNd.val,
						rootNodes[startNode], newDist));
				L.n("createHull").l(startNode).l(" -> ").l(currNd.val);
				// and recursively go on...
				createHull(startNode, testNd.val, newDist, false, considerDist);
				ret = true;
			}
			currNd = currNd.next;
		}
		return ret;
	}

	/**
	 * Tests the Graph for redundant, degenerated Diamonds. While Diamonds are not allowed
	 * in Inheritance, because they are hard to Handle, they are necessary and the
	 * Problems can be avoided using Interfaces. A Problem could arise, because Distances
	 * are not considered! This generates something close to the Nearest Neighbor Matrix
	 * where only the 'nearest' Neighbors are kept. This is still not usable for the
	 * Calculation of Dimension, because 'Neighbors' will be kept that are far away
	 * (disconnected). To eliminate these, calculate the arithmetic Average of the
	 * Distances (do this with the Hull, because the Data Base is broader then) and remove
	 * all Connections that are 'considerably' (e.g. 2 times) longer. This was used to
	 * make the genls Relation of the Cycorp Ontology non-redundant, using (B genls C) &&
	 * (A genls B) => (A genls C) and eliminating the right genls Relation, but only in
	 * this degenerated Case. A full or extended Diamond Shape formed by 4 or more Nodes
	 * with at least one extra Node on each Diamond Side is not redundant, because these
	 * extra Nodes add Information and structure. The Test for degenerated Diamonds is
	 * also easier to implement. You have to test whether any of the DIRECT Parent Nodes
	 * appears in the Parents of any of the other Parent Nodes. Presuming the Graph has no
	 * cycles, this is a simple Recursion without the Need for marking the Elements
	 * already visited (although this could be done for Optimization!) The Inverse of
	 * eliminateDiamonds() is generating the Hull()
	 */
	public void eliminateDiamonds(final PrintStream out) {
		int[] Cache = new int[testCacheSize];
		// use the Maximum expected Number of isA Relations here!!!
		int j, i = -1; // Nodes.length;
		// while (--i >= 0) {
		while (++i < itemCount) {
			j = 0;
			SparseEdge ed = rootNodes[i];
			while (ed != null) {
				Cache[j++] = ed.val;
				ed = ed.next;
			}
			// for each Parent...
			int parentsLength = j;
			while (--j >= 0) { // recursively walk up it's tree
				parentsLength = eliminateDiamonds(i, Cache[j], Cache, parentsLength);
			} // and search for occurrences of the other Parents
			while (--parentsLength >= 0) {
				out.println(i + "\t" + Cache[parentsLength]);
			}
		}
		out.flush();
	}

	/**
	 * Tests the Graph for redundant degenerated Diamonds. While Diamonds are not allowed
	 * in Programming, because they are hard to Handle, they are necessary and the
	 * Problems can be avoided using Interfaces. This was used to make the genls Relation
	 * of the Cycorp Ontology non redundant, using (B genls C) && (A genls B) => (A genls
	 * C) and eliminating the right genls Relation, but only in this degenerated Case. A
	 * full or extended Diamond Shape formed by 4 or more Nodes with at least one extra
	 * Node on each Diamond Side is not redundant, because these extra Nodes add
	 * Information and structure. The Test for degenerated Diamonds is also easier to
	 * implement. You have to test whether any of the DIRECT Parent Nodes appears in the
	 * Parents of any of the other Parent Nodes. Presuming the Graph has no cycles, this
	 * is a simple Recursion without the Need for marking the Elements already visited
	 * (although this could be done for Optimization!) The Inverse of eliminateDiamonds()
	 * is generating the Hull()
	 */
	protected int eliminateDiamonds(final int Origin, final int parent,
			final int[] parents, int parentsLength) {
		SparseEdge nd = rootNodes[parent];
		while (nd != null) {
			int i = parentsLength;
			while (--i >= 0) {
				// tests whether any of the Nodes appears in the other List.
				if (nd.val == parents[i]) { //
					L.n("Found degenerated Diamond: eliminate direct Parent ").l(nd.val)
							.l(" from Node ").l(Origin);
					parents[i] = parents[--parentsLength];
					// eliminate the Element right away!
				}
			}
			parentsLength = eliminateDiamonds(Origin, nd.val, parents, parentsLength);
			nd = nd.next;
		}
		return parentsLength;
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Size of the Cache for the Methods eliminateDiamonds() and eliminateEdges()
	 */
	public static int testCacheSize = 9;

	/**
	 * Tests the Edges from the given File with the Graph. Gives out only those Edges
	 * which are not redundant, when the Graph is interpreted as a transitive Relation
	 * This Method was used to make the isA Relation of the Cycorp Ontology
	 * redundancy-free, using (a isA A) && (A genls B) => (a isA B) and eliminating the
	 * right isA Relation.
	 */
	public static void ELIMINATE_EDGES(final SparseGraph AL, final StreamTokenizer st,
			final PrintStream PS, final boolean reverse) throws IOException {
		int Source, Target;
		int SourceID = -1;
		int SourceCnt = -1;
		int[] Cache = new int[testCacheSize];
		// use the Maximum expected Number of isA Relations here!!!
		st.eolIsSignificant(true);
		while (StreamTokenizer.TT_EOF != st.ttype) {
			while (StreamTokenizer.TT_EOL != st.ttype)
				st.nextToken(); // read until the End of the Line
			// L.n("EOL:").l(st.ttype);
			if (StreamTokenizer.TT_NUMBER != st.nextToken()) continue;
			Source = (int) st.nval;
			// if the Line starts with Characters, skip it
			if (StreamTokenizer.TT_NUMBER != st.nextToken()) continue;
			Target = (int) st.nval;
			// if the Line starts with Characters, skip it
			++SourceCnt;
			if (SourceID != Source) {
				while (--SourceCnt >= 0) {
					PS.println(SourceID + "\t" + Cache[SourceCnt]);
				} // write the Cache out to the Output Stream.
				SourceID = Source;
				SourceCnt = 0;
			}
			int j = SourceCnt;
			Cache[SourceCnt] = Target;
			while (--j >= 0) { // test the new one against the previous ones
				if (AL.directDistance(Target, Cache[j]) < Float.POSITIVE_INFINITY) {
					L.n("Found redundant Classification of Object ");
					L.l(SourceID).l(" : 1) ").l(Target).l(" 2) ").l(Cache[j]);
					if (!reverse) Cache[j] = Target;
					--SourceCnt;
				} else if (AL.directDistance(Cache[j], Target) < Float.POSITIVE_INFINITY) {
					L.n("Found redundant Classification of Object ");
					L.l(SourceID).l(" : 1) ").l(Target).l(" 2) ").l(Cache[j]);
					if (reverse) Cache[j] = Target;
					--SourceCnt;
				}
			}
		}
		PS.flush();
	}

	/** @return the directed Distance between the Source and the Target Node */
	protected float directDistance(final int source, final int target) {
		SparseEdge testNd = rootNodes[source];
		// not cacheing the Start Position! Prevents duplicate adding e.g. 762
		while (testNd != null) { // search for an existing Connection
			if (target == testNd.val) { return testNd.weight; }
			testNd = testNd.next;
		}
		return Float.POSITIVE_INFINITY;
	}

	/**
	 * Initializes the Weights and Paths for solving Single Source Problems
	 */
	protected void initSingleSource(final int _startVertex) {
		for (int i = itemCount; --i >= 0;) {
			weights[i] = NOT_VISITED;
			_Sequence[i] = _Position[i] = -1;
		}
		weights[_startVertex] = 0; // Mark the Start Vertex
	}

	/**
	 * Relaxes by testing whether the edge (u,v) with weight w(u,v) can improve the
	 * shortest Path by going through u. Weights for solving Single Source Problems
	 * @return true, when a real Relaxation took place
	 */
	protected boolean relax(final int u, final SparseEdge nd) {
		float testWeight;
		int v = nd.val;
		if (weights[v] > (testWeight = weights[u] + nd.weight)) {
			weights[v] = testWeight;
			_Sequence[v] = u;
			_Position[u] = v;
			return true;
		}
		return false;
	}

	/**
	 * Searches the minimum Spanning Tree OR the shortest Paths using a
	 * @see HeapIndx This is both done using a "greedy" Strategy: for the shortest
	 *      Spanning Tree: add the Node with the shortest Distance to ANY Node (Prims
	 *      Algorithm) for the shortest Paths: add the Node with the shortest Distance to
	 *      the LAST Node (Dijkstras Algorithm) the shortest Path to any Node runs through
	 *      the shortest Paths to all previous Nodes. The Priority Queue is maintained as
	 *      an unsorted Array in Val, because this is most convenient to combine the loops
	 *      for the Priorities. Performance for very large Graphs can be increased using a
	 *      Fibonacci Heap as the Priority Queue A negative Sign indicates that the node
	 *      belongs to the Priority Queue, a positive Value denotes a Node in the tree.
	 *      The Tree is built up in p, which contains the Parents of nodes. Iterative
	 *      Visitor through all Nodes of a partial Tree with k as a Root. This
	 *      Implementation uses an O(lb E) Implementation for finding the next Node,
	 *      compared to visitMinimum2() using O(N). Uses visitMin() and the HeapIndx Class
	 *      (to implement a Heap structure) Afterwards: val[] contains the
	 *      Distances/Lengths, so that the Sum in val is the Size of the Spanning Tree
	 *      resp. the Sum of all Lengths to each Node. sequence[] contains the Indices of
	 *      the respective Parent Node TODO: doesn't calculate the Spanning Tree properly!
	 *      (see testMin())
	 * @return The List of respective Parent IDs for the Nodes of the Spanning Tree or the
	 *         shortest Path.
	 */
	/*
	 * public int[] visitMinimum1(final boolean Path) { final HeapIndx HP = new
	 * HeapIndx(itemCount); for (int i = itemCount; --i >= 0;) { value[i] = -NOT_VISITED; }
	 * //for (int i = Nodes.length; --i >= 0) { if (Val[i]== -NotVisited) { visitMin(i,
	 * HP, Path); }} //Tree starts at the last Node! for (int i = -1; ++i < itemCount;) {
	 * if (value[i] == -NOT_VISITED) { visitMin(i, HP, Path);} //Tree starts at the first
	 * Node! } return sequence; }
	 */

	/**
	 * Recursive Visitor, used by Iterator 'visitMinimum' to operate on the Graph.
	 * Performs a Search by traversing partial Trees with l as Root. The Nodes of the
	 * Search Trees have to be tested separately!
	 */
	/*
	 * protected void visitMin( final int l, final HeapIndx HP, final boolean Path) {
	 * //The Heap's update Method reduces the Priority if (HP.update(new
	 * ByRefFloat(-NOT_VISITED), l + 1)) //HeapEntry(l,NotVisited), NotVisited))
	 * sequence[l] = -1; do { //as long as there are Elements in the Heap L.n(HP); int k =
	 * HP.get() - 1; //((HeapEntry)HP.get()).Node; if((value[k] = -value[k]) ==
	 * NOT_VISITED) value[k] = 0; SparseEdge t = rootNodes[k]; do //for each Node
	 * connected to Node k... if (value[t.val] < 0) { float Priority = -t.weight; //the
	 * Weight of the Edge = distance from the tree if (Path) Priority -= value[k]; //the
	 * current (summed up) Length of the Path if (HP.update(new ByRefFloat(Priority),
	 * t.val + 1)) { //new HeapEntry(t.Node, Priority), t.Node)) value[t.val] = Priority;
	 * sequence[t.val] = k; positions[k] = t.val; } } while ((t = t.next) != null); }
	 * while (!HP.isZero()); }
	 */

	/**
	 * generates the maximum Set of Tupels containing one of each of the given disjoint
	 * Sets
	 * @param pairings[#][origin, target] each possible Pairing Each Set can (should)
	 *            restart it's Numbering from 0.
	 * @see Pairing contains Methods to calculate the maximum Pairing given full Rankings.
	 * @return an Array with the Length of the first Set with the Values set for the
	 *         second Set.
	 */
	final static public int[] MAX_PAIRING(final int[][] pairing) {
		final int[][] sortedByLast = MAX_TUPEL(new int[][][]{pairing});
		final int[] ret = new int[1 + MatrixInt.MAX(sortedByLast, 0)];
		VectorInt.FILL_AT(ret, -1);
		for (int i = sortedByLast.length; --i >= 0;) {
			final int[] pair = sortedByLast[i];
			ret[pair[0]] = pair[1];
		} // makes sense only for Pairs, not for N-Tupels!
		return ret;
	}

	/**
	 * generates the maximum Set of Tupels containing one of each of the given disjoint
	 * Sets
	 * @param pairings[mapping][#][origin, target]: each possible Pairing, organizes by
	 *            the mapping for identifying the disjoint Sets Each Set can (should)
	 *            restart it's Numbering from 0.
	 * @see Pairing contains Methods to calculate the maximum Pairing given full Rankings.
	 * @return an Array of Tupels with the Items Number from each Set
	 */
	final static public int[][] MAX_TUPEL(final int[][][] pairings) {
		// determine the max. Index of each disjoint Set
		final int[] maxIndex = new int[pairings.length + 2];
		for (int i = pairings.length; --i >= 0;)
			maxIndex[i + 1] = MatrixInt.MAX(pairings[i], 0);
		// need the last Max only for correct Graph Sizing
		maxIndex[pairings.length + 1] = MatrixInt.MAX(pairings[pairings.length - 1], 1);
		maxIndex[0] = 0;
		VectorInt.ADD_AT(maxIndex, 1);
		VectorInt.SUMM_F_AT(maxIndex);
		// construct a sparse Flow Graph from these, offsetting the Indices by the max.
		// Indices.
		final SparseGraph graph = new SparseGraph(1 + maxIndex[maxIndex.length - 1]);
		// add a Source and Edges to the first Set
		for (int i = maxIndex[1]; --i >= 1;)
			graph.addFlowEdge(0, i);
		// add Edges from the previous to the next Set
		for (int i = pairings.length; --i >= 0;) {
			final int[][] pairing = pairings[i];
			final int offset0 = maxIndex[i];
			final int offset1 = maxIndex[i + 1];
			for (int j = pairing.length; --j >= 0;) {
				final int[] pair = pairing[j];
				graph.addFlowEdge(pair[0] + offset0, pair[1] + offset1);
			}
		}
		// add a Target and Edges from the last Set
		final int lastIndex = maxIndex[maxIndex.length - 1];
		for (int i = maxIndex[maxIndex.length - 2] - 1; ++i < lastIndex;)
			graph.addFlowEdge(i, lastIndex);
		// determine the maximum Flow
		final int[][] paths = new int[lastIndex][];
		graph.maxFlow(0, lastIndex, 0, paths);
		final int maxFlow = (int) graph.getTotalFlow(0);
		// construct and return the Mappings found.
		final int[][] ret = new int[maxFlow][1 + pairings.length];
		for (int k = maxFlow, i = paths.length; k > 0;) {
			final int[] parents = paths[--i];
			if (parents == null) continue;
			final int[] ret_k = ret[--k];
			for (int j = pairings.length, curr = lastIndex; (curr = parents[curr]) != 0; --j)
				ret_k[j] = curr - maxIndex[j];
		}
		return ret;
	}

	/** @return the maximum Weight of all Edges */
	final public float maxWeight() {
		return maxWeight(false);
	}

	/**
	 * @param min Flag whether to calculate the Minimum
	 * @return the maximum or minimum Weight of all Edges
	 */
	final public float maxWeight(final boolean min) {
		float maxWeight = min ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
		final SparseEdgeStream iter = SparseEdgeIterator();
		for (SparseEdge edge; null != (edge = iter.nextSparseEdge());)
			if ((maxWeight > edge.weight) == min) maxWeight = edge.weight;
		return maxWeight;
	}

	/**
	 * returns the maximized flows between all Vertices for single Source and Sink. For
	 * Sinks or Sources with limited Capacity simply add an unlimited Sink or Source
	 * Vertex and an outgoing / incoming Edge with this Capacity. For multiple Sinks or
	 * Sources just add a SuperSink or SuperSource and outgoing / incoming Edges with
	 * infinite Capacity. Due to the Symmetry of the Problem, it can be solved with only
	 * half the Matrix.
	 * @param start the single Source Node
	 * @param stop the single Sink / Drain Node
	 * @param flow an (optional, null allowed) initial Guess for the Flow
	 * @param limit the Algorithm terminates when the Increment is smaller than this Value
	 *            for integer Flows (or rationals scaled to integers) this can be 0
	 * @return flow or a new Array with maximized Flows.
	 */
	final public float maxFlow(final int start, final int stop, final float limit) {
		maxFlow(start, stop, limit, null);
		return getTotalFlow(start);
	}

	/**
	 * returns the maximized flows between all Vertices for single Source and Sink. For
	 * Sinks or Sources with limited Capacity simply add an unlimited Sink or Source
	 * Vertex and an outgoing / incoming Edge with this Capacity. For multiple Sinks or
	 * Sources just add a SuperSink or SuperSource and outgoing / incoming Edges with
	 * infinite Capacity. Due to the Symmetry of the Problem, it can be solved with only
	 * half the Matrix.
	 * @param start the single Source Node
	 * @param stop the single Sink / Drain Node
	 * @param flow an (optional, null allowed) initial Guess for the Flow
	 * @param limit the Algorithm terminates when the Increment is smaller than this Value
	 *            for integer Flows (or rationals scaled to integers) this can be 0
	 * @return flow or a new Array with maximized Flows.
	 */
	final public void maxFlow(final int start, final int stop, final float limit,
			final int[][] paths) {
		// determine max. Weight to avoid rounding Errors on Subtraction
		final float maxWeight = maxWeight();
		for (;;) {
			final int[] parents = minimumDistanceOrSpan(weights, start, stop, maxWeight);
			final float increment = maxWeight - weights[stop];
			if (increment <= limit) return;
			if (paths != null) paths[parents[stop]] = VectorInt.COPY(parents);
			for (int y = stop, x = parents[stop]; y != start; y = x, x = parents[x]) {
				final FlowEdge edge = (FlowEdge) getEdge(x, y);
				edge.trp.flow = -(edge.flow += increment); //
			}
		}
	}

	/**
	 * Since all Flows of an internal Node cancel each other, the absolute Value of each
	 * Flow is taken, so you have to half the value to get the actual Throughput.
	 * @param node the Vertex to calculate the total Flow for...
	 * @return the total Flow into and out of the given Vertex
	 */
	public float getTotalFlow(final int node) {
		float sum = 0;
		for (FlowEdge edge = (FlowEdge) rootNodes[node]; edge != null; edge = (FlowEdge) edge.next)
			sum += Math.abs(edge.flow);
		return sum;
	}

	/**
	 * Searches the minimum Spanning Tree or the shortest Paths with continuous Distances
	 * defined by the Edge-Weights. The Priority Queue is implemented as an unsorted Array
	 * in Val, because this is most convenient to combine the loops for searching and
	 * updating the Priorities. A negative Sign indicates that the node belongs to the
	 * Priority Queue, a positive Value in Val[] denotes a Node in the tree with either
	 * it's Distance from the nearest Tree Element or from the Root. The Tree is built up
	 * in p, which contains the Parents of nodes. Iterative Visitor through all Nodes of a
	 * partial Tree with k as a Root. Since Checking all nodes is a O(V^2) Operation
	 * anyway, the Priority Queue is maintained in an unsorted Array with O(V) Search.
	 * @param start the Node to calculate Distances for; if negative, calculates a
	 *            Spanning Tree
	 * @param weights optional (null allowed) Array to be filled with the actual Distances
	 */
	public int[] minimumDistanceOrSpan(final int start) {
		return minimumDistanceOrSpan(weights, start, -1, 0);
	}

	/**
	 * Searches the minimum Spanning Tree or the shortest Paths with continuous Distances
	 * defined by the Edge-Weights. The Priority Queue is implemented as an unsorted Array
	 * in Val, because this is most convenient to combine the loops for searching and
	 * updating the Priorities. A negative Sign indicates that the node belongs to the
	 * Priority Queue, a positive Value in Val[] denotes a Node in the tree with either
	 * it's Distance from the nearest Tree Element or from the Root. The Tree is built up
	 * in p, which contains the Parents of nodes. Iterative Visitor through all Nodes of a
	 * partial Tree with k as a Root. Since Checking all nodes is a O(V^2) Operation
	 * anyway, the Priority Queue is maintained in an unsorted Array with O(V) Search.
	 * @param start the Node to calculate Distances for; if negative, calculates a
	 *            Spanning Tree
	 * @param weights optional (null allowed) Array to be filled with the actual Distances
	 */
	public int[] minimumDistanceOrSpan(final int start, final float[] weights) {
		return minimumDistanceOrSpan(weights, start, -1, 0);
	}

	/**
	 * Searches the shortest Paths to the given Start Node or the minimum Spanning Tree if
	 * this Node is negative.
	 * @see #createHull(boolean) to calculate all shortest Paths between all Nodes. The
	 *      Priority Queue is implemented as an unsorted Array in Val, because this is
	 *      most convenient to combine the loops for the Priorities. A negative Sign
	 *      indicates that the node belongs to the Priority Queue, a positive Value
	 *      denotes a Node in the tree. The Tree is built up in p, which contains the
	 *      Parents of nodes. Iterative Visitor through all Nodes of a partial Tree with k
	 *      as a Root. This Implementation uses an O(N) Implementation for finding the
	 *      next Node, compared to visitMinimum() using O(E). Afterwards: val[] contains
	 *      the Distances/Lengths, so that the Sum in val is the Size of the Spanning Tree
	 *      resp. the Sum of all Lengths to each Node. sequence[] contains the Indices of
	 *      the respective Parent Node
	 * @param start the Start Node for the minimum Paths Search, if negative, searches for
	 *            the minimum Spanning Tree
	 * @return The List of the Parent Index for each Node of the Spanning Tree or the
	 *         shortest Path Graph.
	 */
	public int[] minimumDistanceOrSpan(float[] weights, int start, final int stop,
			final float maxWeight) {
		if (weights == null) weights = this.weights;
		final boolean minPaths;
		if (start < 0) {
			start = 0;
			minPaths = false;
		} else minPaths = true;

		for (int i = itemCount; --i >= 0;) {
			weights[i] = -NOT_VISITED;
			_Sequence[i] = -1;
		} // initializing to negative Values to indicate all are in the PQueue

		final HeapByIndex heap = new HeapByIndex(itemCount + 1);
		heap.insert(new ByRefFloat(weights[start] = 0), start);
		for (int i = -1; ++i < itemCount;) { // can start with any Node
			int prevMin = i;
			if (weights[prevMin] != -NOT_VISITED) // already visited, skip it
				continue;
			ByRefFloat prio; // = new ByRefFloat(NOT_VISITED);
			// if(prio != heap.update(prio, prevMin))
			// sequence[prevMin] = 0;
			do {
				prevMin = heap.get();
				float prevVal = weights[prevMin] = -weights[prevMin]; // mark as visited
				if (NOT_VISITED == prevVal) prevVal = weights[prevMin] = 0;
				for (SparseEdge currEdge = rootNodes[prevMin]; currEdge != null; currEdge = currEdge.next) {
					float currWeight = weights[currEdge.val];
					if (currWeight >= 0) // already visited, skip it
						continue;
					// Priority contains the current maximum, min is it's Index
					final float priority;
					if (currEdge instanceof FlowEdge) {
						FlowEdge flowEdge = (FlowEdge) currEdge;
						float residual = -flowEdge.flow; // Residual Flow
						if (currEdge.weight > 0) residual += currEdge.weight; 
						// always chooose the Edges with the largest Residual
						priority = Math.min(residual, maxWeight - prevVal) - maxWeight; // Residual
																						// is
																						// limited
					} else if (minPaths)
						priority = -(currEdge.weight + prevVal); 
					// or the current (summed up) Length of the Path
					else priority = -currEdge.weight; // the Weight of the Edge =
														// distance from the tree
					// Weight of the Edge == distance from the current Search tree
					// Weight of the Edge + current (summed up) Length of the Path ==
					// Distance from the Start Node
					prio = new ByRefFloat(priority);
					if (prio != heap.update(prio, currEdge.val)) { 
						// search for the maximum negative Value = minimum absolute Value
						weights[currEdge.val] = priority;
						_Sequence[currEdge.val] = prevMin;
					}
				}
			} while (!heap.isZero());
		} // stops when no other Minimum found.
		return _Sequence;
	}

	/**
	 * Searches the shortest Paths to the given Start Node or the minimum Spanning Tree if
	 * this Node is negative. Less effective Implementation, due to linear Search through
	 * the List of Priorities.
	 * @see #minimumDistanceOrSpan(int) which uses a Heap as a Priority Queue
	 * @see #createHull(boolean) to calculate all shortest Paths between all Nodes. The
	 *      Priority Queue is implemented as an unsorted Array in Val, because this is
	 *      most convenient to combine the loops for the Priorities. A negative Sign
	 *      indicates that the node belongs to the Priority Queue, a positive Value
	 *      denotes a Node in the tree. The Tree is built up in p, which contains the
	 *      Parents of nodes. Iterative Visitor through all Nodes of a partial Tree with k
	 *      as a Root. This Implementation uses an O(N) Implementation for finding the
	 *      next Node, compared to visitMinimum() using O(E). Afterwards: val[] contains
	 *      the Distances/Lengths, so that the Sum in val is the Size of the Spanning Tree
	 *      resp. the Sum of all Lengths to each Node. sequence[] contains the Indices of
	 *      the respective Parent Node
	 * @param start the Start Node for the minimum Paths Search, if negative, searches for
	 *            the minimum Spanning Tree
	 * @return The List of the Parent Index for each Node of the Spanning Tree or the
	 *         shortest Path Graph.
	 */
	public int[] minimumDistanceOrSpanOld(int start) {
		final boolean minPaths;
		if (start < 0) {
			start = 0;
			minPaths = false;
		} else minPaths = true;

		weights[itemCount] = -NOT_VISITED; // maximum Value, Sentinel
		for (int i = itemCount; --i >= 0;) {
			weights[i] = -NOT_VISITED;
			_Sequence[i] = -1;
		} // initializing to negative Values to indicate all are in the PQueue

		weights[start] = 0; // min contains the Index of the current minimum
		for (int min = start; min != itemCount;) { // can start with any Node
			final int prevMin = min;
			min = itemCount; // initialize Sentinel
			weights[prevMin] = -weights[prevMin]; // mark as visited
			// go through all connected Nodes and update the Distances...
			for (SparseEdge currEdge = rootNodes[prevMin]; currEdge != null; currEdge = currEdge.next) {
				float val = weights[currEdge.val];
				if (val >= 0) // already visited, skip it
					continue;
				// val contains the current maximum, min is it's index
				final float newLength = -(minPaths
						? weights[prevMin] + currEdge.weight
						: currEdge.weight);
				// Weight of the Edge == distance from the current Search tree
				// Weight of the Edge + current (summed up) Length of the Path == Distance
				// from the Start Node
				if (val < newLength) { // search for the maximum negative Value = minimum
										// absolute Value
					weights[currEdge.val] = val = newLength;
					_Sequence[currEdge.val] = prevMin; // position[oldMin] =
														// currEdge.val; //Inverse is not
														// defined, since no Permutation,
														// but a Tree!
				}
				if (weights[min] < val) min = currEdge.val;
			}
			// loop through the not connected Nodes t
			// 2nd Loop: linear Search through the Priority List for a the new minimum
			// Distance
			// TODO: for sparse Graphs this should be replaced by an updateable Priority
			// Queue
			for (int i = itemCount; --i >= 0;)
				if (weights[i] < 0) if (weights[min] < weights[i]) // Maximum negative
																	// Value => minimum
																	// absolute Value
					min = i;
			L.n("New Min:").l(prevMin).l(" New MinVal:").l(weights[min]);
		}
		// stops when no other Minimum found.
		return _Sequence;
	}

	/**
	 * Returns the Length of the Path from the Start to the End Node along the Path given
	 * by the resp. Parents.
	 * @param parents the Result of minimumDistanceTree, a List of parent nodes
	 * @param start the Start Node to calculate Distance for
	 * @param end the End Node to calculate Distance for
	 * @return the Length of the Path
	 */
	public float getLength(final int[] parents, final int start, final int end) {
		float ret = 0;
		for (int i = end; i != start; i = parents[i])
			ret += getWeight(parents[i], i);
		return ret;
	}

	/**
	 * Returns either the Sum of the Path-Lengths along parents from the start to all
	 * other Nodes or the overall Length of the Spanning Tree defined by parents
	 * @param parents List of Parent Nodes; the Spanning Tree or Shortest Paths Result
	 * @param startNode if negative, returns the Spanning Tree Size, otherwise the Path
	 *            Length Sum
	 * @return either the Sum of the Path-Lengths along parents from the start to all
	 *         other Nodes or the overall Length of the Spanning Tree defined by parents
	 */
	public float getLength(final int startNode, final int[] parents) {
		float ret = 0;
		for (int i = itemCount; --i > 0;) { // Loop over all valid Nodes / Links
			if (startNode >= 0)
				ret += getLength(parents, startNode, i);
			else ret += getWeight(parents[i], i); // TODO: 2005-05-31 Parameters were
													// swapped, check whether this is
													// correct!
		}
		return ret;
	}

	/** displays this Graph in a new Frame */
	public Map2DPainter display() {
		final BaseApplet applet = new BaseApplet();
		final Map2DPainter frame = new Map2DPainter(applet, this);
		frame.show(); //
		return frame;
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * returns a new Graph with ALL Edges from or to the given Node and removed from this
	 * Graph. By Joining both Graphs the original Graph can be restored.
	 * @see #removeNodes(int[]) which does the same more effectively for several Nodes.
	 * @see #join(SparseMatrix) for effectively joining the Edges back into this Graph.
	 */
	public SparseGraph removeNode(final int node) {
		final SparseGraph ret = new SparseGraph(numVertices); // AL.getInt();
		// first remove the outgoing Edges to quickly reduce Graph Size
		ret.rootNodes[node] = this.rootNodes[node];
		this.rootNodes[node] = null;
		ret.itemCount = ret.numVertices = node + 1;
		// then iterate through the remaining Edges to remove them
		final SparseEdgeStream iter = SparseEdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			if (edge.val != node) continue;
			final SparseEdge currEdge = iter.removeCurrEdge();
			currEdge.next = ret.rootNodes[edge.key]; // reuse the Edge...
			ret.addEdgeWoTranspose(edge.key, currEdge); // ...for the new Graph
		}
		return ret;
	}

	/**
	 * creates a Set of disconnected undirected Graphs in Place, resulting from
	 * strategically removing all Joints. This also works for directed Graphs! The Graphs
	 * can be used to identify previously doubly connected Components.
	 * @see #join(SparseGraph) is the inverse Operation.
	 * @return a new Graph containing the removed Edges (for later Addition) this Graph
	 *         retains the disconnected Subgraphs in one.
	 */
	public SparseGraph removeNodes(final int[] numFragments) {
		return removeNodes(numFragments, null); // numItems;
	}

	/**
	 * removes all Edges to and from the Nodes with negative Values in numFragments and
	 * collects them in the given Graph which can be null and is returned. The Graphs can
	 * be used to identify previously doubly connected Components.
	 * @see #join(SparseGraph) is the inverse Operation.
	 * @return a new Graph containing the removed Edges (for later Addition) this Graph
	 *         retains the leftover Edges.
	 */
	public SparseGraph removeNodes(final int[] numFragments, SparseGraph ret) {
		if (ret == null) ret = new SparseGraph(numVertices);
		// first remove the outgoing Edges to quickly reduce Graph Size
		for (int i = numFragments.length; --i >= 0;) {
			if (numFragments[i] <= 0) // Cycle- or isolated Node.
				continue;
			ret.rootNodes[i] = this.rootNodes[i];
			this.rootNodes[i] = null;
		}
		// then iterate through the remaining Edges to remove them
		final SparseEdgeStream iter = SparseEdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			if (numFragments[edge.val] <= 0) continue;
			final SparseEdge currEdge = iter.removeCurrEdge();
			ret.addEdgeWoTranspose(edge.key, currEdge); // reuse the Edge for the new
														// Graph
		}
		ret.itemCount = ret.numVertices = this.itemCount;
		return ret;
	}

	/**
	 * Moves the Edges from the jointGraph to this Graph, so that jointGraph is empty
	 * afterwards and no new Edge Objects have to be created.
	 * @see #removeNodes(int[]) is the inverse Operation.
	 */
	public void join(final SparseMatrix jointGraph) {
		// first try to add the outgoing Edges to quickly reduce Graph Size
		for (int i = jointGraph.getNumVertices(); --i >= 0;) {
			final SparseEdge root = jointGraph.rootNodes[i];
			if (root == null) //
				continue;
			if (rootNodes[i] == null) {
				rootNodes[i] = root;
				jointGraph.rootNodes[i] = null;
			}
		}
		// then iterate through the remaining Edges to move them
		final SparseEdgeStream iter = jointGraph.SparseEdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			SparseEdge currEdge;
			while (null != (currEdge = iter.removeCurrEdge())) { // remove all Edges
																	// from a Row
				currEdge.next = rootNodes[edge.key]; // reuse the Edge...
				rootNodes[edge.key] = currEdge; // ...for this Graph
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * returns the first Neighbor of the given Node
	 * @param key the Node to search any Neighbor for
	 * @param except the Node to except from the List of Neighbors
	 * @return the first Neighbor of the given Node key if it has only except as Neighbor
	 */
	public int getAnyNeighbor(final int key, final int except) {
		for (SparseEdge edge = rootNodes[key]; edge != null; edge = edge.next) {
			if (edge.val != except) {// prevent using a Joint belonging to both
										// Components
				return edge.val; // store a different Representative...
			}
		}
		return key;
	}

	/**
	 * returns the first Neighbor of the given Node
	 * @param key the Node to search any Neighbor for
	 * @param except the list of Nodes to except from the List of Neighbors are marked
	 *            with nonzero Values
	 * @return the first Neighbor of the given Node key if it has only excepts as
	 *         Neighbors
	 */
	public int getAnyNeighbor(final int key, final int[] except) {
		for (SparseEdge edge = rootNodes[key]; edge != null; edge = edge.next) {
			if (except[edge.val] == 0) {// prevent using a Joint belonging to both
										// Components
				return edge.val; // store a different Representative...
			}
		}
		return key;
	}

	/**
	 * creates a single, doubly connected Graph, which is robust to single Node Failures.
	 * When modeling Connections as Nodes and Nodes as Connections, this can also be used
	 * to make Edges robust.
	 * @param newWeight the Default Weight for Edges to create new
	 * @param newTyp the Type for newly created Edges
	 * @param directed Flag whether to add directed or undirected Edges.
	 */
	private void makeRobust(final float newWeight, // Weight for newly created Edges for
													// double Connection
			final int edgeTyp) {
		final SparseGraph bridges = new SparseGraph(numVertices);
		// final SparseGraph[] biConnGraphs = this.biConnectedGraphs(edgeTyp); //cannot
		// use this...
		final SparseGraph[] biConnGraphs = new SparseGraph[numVertices]; // ...because
																			// I need the
																			// #Fragments
																			// too!
		final int[] numFragments = numFragments(false, biConnGraphs, bridges, false);
		VectorInt.FILL_AT(_Position, -1); // use position, since sequence is returned by
											// numFragments!!!
		for (int next, last = next = -1, i = biConnGraphs.length; --i >= 0;) {
			final SparseGraph biConnGraph = biConnGraphs[i];
			if (biConnGraph == null) continue;
			// search for a non-Joint Peer to each Vertex
			for (int key = biConnGraph.numVertices; --key >= 0;) {
				final int neighbor = biConnGraph.getAnyNeighbor(key, numFragments);
				if (key != neighbor) _Position[key] = next = neighbor;
			}
			if (last >= 0) addEdge(last, next, true, newWeight, edgeTyp);
			last = next;
			join(biConnGraph); // re-join
		}
		// ...identify the remaining Components from the Bridges...
		final IEdgeStreamIn iter = bridges.SparseEdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			int val = _Position[edge.val];
			if (val < 0) val = bridges.getAnyNeighbor(edge.val, edge.key);
			int key = _Position[edge.key]; // choose a different Representative for each
											// Vertex.
			if (key < 0) key = bridges.getAnyNeighbor(edge.key, edge.val);
			addEdge(key, val, newWeight, edgeTyp);
		}
		join(bridges);
	}

	/**
	 * splits up this Graph into the doubly connected SubGraphs. The Connections between
	 * these SubGraphs are given by the Joints shared between Graphs. edgeTyp is defaulted
	 * to 0 a Copy is created and this Graph is left intact
	 * @return an Array with biConnected SubGraphs at the Positions of the SubGraph
	 *         Representatives.
	 */
	private SparseGraph[] biConnectedGraphs() {
		return biConnectedGraphs(0);
	}

	/**
	 * splits up this Graph into the doubly connected SubGraphs. The Connections between
	 * these SubGraphs are given by the Joints shared between Graphs.
	 * @param edgeTyp the edge Type to use for splitting
	 * @return an Array with biConnected SubGraphs at the Positions of the SubGraph
	 *         Representatives.
	 */
	private SparseGraph[] biConnectedGraphs(final int edgeTyp) {
		return biConnectedGraphs(edgeTyp, null, false);
	}

	/**
	 * splits up this Graph into the doubly connected SubGraphs. The Connections between
	 * these SubGraphs are given by the Joints shared between Graphs.
	 * @param edgeTyp the edge Type to use for splitting
	 * @param bridges when given, Bridges are moved there, otherwise they stay in this
	 *            Graph.
	 * @param copy Flag whether to create a Copy or destroy this Graph
	 * @return an Array with biConnected SubGraphs at the Positions of the SubGraph
	 *         Representatives.
	 */
	private SparseGraph[] biConnectedGraphs(final int edgeTyp, final SparseGraph bridges,
			final boolean copy) {
		final SparseGraph[] ret = new SparseGraph[numVertices];
		final int[] numFragments = numFragments(false, ret, bridges, copy); // null); //
		return ret;
	}

	/**
	 * splits up this Graph into the doubly connected SubGraphs. The Connections between
	 * these SubGraphs are given by the Joints shared between Graphs.
	 * @param edgeTyp the edge Type to use for splitting
	 * @param copy Flag whether to create a Copy or destroy this Graph
	 * @return an Array with doubly connected SubGraphs at the Positions of the SubGraph
	 *         Representatives.
	 */
	/*
	 * private SparseGraph[] splitDoublyConnectedGraphsOld(final int edgeTyp, final
	 * boolean copy) { //TODO: does not treat Node 'C' well, does not consider isolated
	 * Nodes. write a Test // //remove all Joints and collect them into jointGraph...
	 * final SparseMatrix jointGraph = disconnect(edgeTyp); //... //single out remaining
	 * Components final int[] sets = connectedComponentIndex(edgeTyp); final SparseGraph[]
	 * ret = connectedComponents(edgeTyp, sets, copy); //re-add/distribute the Joints to
	 * the respective Components for(int i = sets.length; --i >= 0;) { //first distribute
	 * the Edges coming FROM the Joints if (sets[i] >= 0) //this could also be integrated
	 * with the following Loop continue; //...but then you would have to test both Key and
	 * Val for being a Joint! for (SparseEdge next, edge = jointGraph.rootNodes[i]; edge !=
	 * null; edge = next) { next = edge.next; if (!edge.isTyp(edgeTyp)) // &&
	 * !Float.isInfinite(edge.weight)) continue; final int targetNode = sets[edge.val]; if
	 * (targetNode <= 0) continue; //TODO: isolated Node... ignored for now... final
	 * SparseGraph graph = ret[targetNode]; //need to check both the key and the val... if
	 * (copy) //directly reusing the SparseEdge Object destroys 'this' graph.addEdge(i,
	 * edge.val, edge.typ, edge.weight); else graph.addEdgeWoTranspose(i, edge); } if
	 * (copy) //join(jointGraph); rootNodes[i] = jointGraph.rootNodes[i];
	 * jointGraph.rootNodes[i] = null; //remove them from the Joint Graph } //now
	 * distribute the Edges going into the Joints final SparseEdgeStream iter =
	 * jointGraph.SparseEdgeIterator(); for(SparseEdge edge; null != (edge =
	 * iter.nextSparseEdge());) { if (!edge.isTyp(edgeTyp)) // &&
	 * !Float.isInfinite(edge.weight)) continue; final int key = iter.currEdge.key;
	 * //edge.key; final int keyIndex = sets[key]; //one of them is final SparseGraph
	 * graph = ret[keyIndex]; //need to check only the key ... //...since the all Edges
	 * outgoing of Joints are already distributed! if (copy) //directly reusing the
	 * SparseEdge Object destroys 'this' graph.addEdge(key, edge.val, edge.typ,
	 * edge.weight); else { iter.removeCurrEdge(); graph.addEdgeWoTranspose(key, edge); } }
	 * return ret; }
	 */

	// /////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the Equivalence Representatives for the connected Components in both
	 * directed and undirected Graphs. Implementation: uses the fast Equivalence Class
	 * Algorithm instead of a Graph Walk. Directed Edges are evaluated as if they were
	 * undirected.
	 * @return the Representative Node for the Connected Component that each Node belongs
	 *         to.
	 */
	public int[] connectedComponentIndex() { // final boolean removeChildCount) {
		return connectedComponentIndex(0);
	}// , removeChildCount); } //

	/**
	 * Returns the Equivalence Representatives for the connected Components in both
	 * directed and undirected Graphs. Implementation: uses the fast Equivalence Class
	 * Algorithm instead of a Graph Walk. Directed Edges are evaluated as if they were
	 * undirected.
	 * @return the Representative Node for the Connected Component that each Node belongs
	 *         to.
	 */
	public int[] connectedComponentIndex(final int edgeTyp) { // , final boolean
																// removeChildCount) {
		return connectedComponentIndex(edgeTyp, null); // , removeChildCount);
	} //

	/**
	 * Returns the Equivalence Representatives for the connected Components 
	 * in both directed and undirected Graphs. 
	 * Implementation: 
	 * uses the fast Equivalence Class Algorithm instead of a Graph Walk. 
	 * Directed Edges are evaluated as if they were undirected.
	 * @return the Representative Node for the Connected Component that each Node belongs
	 *         to.
	 */
	public int[] connectedComponentIndex(final int edgeTyp, int[] nodesToIgnore) { 
		// , final boolean removeChildCount) {
		final DisJointSet sets = new DisJointSet(this.getNumVertices());
		final IEdgeStreamIn iter = EdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			if (edge.isTyp(edgeTyp)) // && !Float.isInfinite(edge.weight))
				if ((nodesToIgnore == null)
						|| ((nodesToIgnore[edge.key] <= 0) && (nodesToIgnore[edge.val] <= 0)))
				// sets.equalsMAX(edge.key, edge.val, true);
					sets.equalsMAX(edge.val, edge.key, true);
		}
		return sets.getRoots();
	} //

	/**
	 * Returns the Equivalence Representatives for the connected Components in both
	 * directed and undirected Graphs. Implementation: uses the fast Equivalence Class
	 * Algorithm instead of a Graph Walk. Directed Edges are evaluated as if they were
	 * undirected.
	 * @return the Representative Node for the Connected Component that each Node belongs
	 *         to.
	 */
	public SparseGraph[] connectedComponents() {
		return connectedComponents(0, true);
	} //

	/**
	 * Returns the connected Components in both directed and undirected Graphs.
	 * Implementation: uses the fast Equivalence Class Algorithm instead of a Graph Walk.
	 * Directed Edges are evaluated as if they were undirected.
	 * @return a Subgraph for each Connected Component at the Index of the respective
	 *         Representative Node.
	 */
	public SparseGraph[] connectedComponents(final int edgeTyp, final boolean copy) {
		return connectedComponents(edgeTyp, connectedComponentIndex(edgeTyp), copy);
	} //

	/**
	 * Returns the connected Components in both directed and undirected Graphs.
	 * Implementation: uses the fast Equivalence Class Algorithm instead of a Graph Walk.
	 * Directed Edges are evaluated as if they were undirected.
	 * @return a Subgraph for each Connected Component at the Index of the respective
	 *         Representative Node.
	 */
	protected SparseGraph[] connectedComponents(final int edgeTyp, final int[] sets,
			final boolean copy) {
		final SparseGraph[] ret = new SparseGraph[sets.length];
		for (int i = sets.length; --i >= 0;) { // faster to do it in the Node Sweep!
			if (sets[i] < -1) { // leave Roots without Children alone!
				sets[i] = i; // (Roots contain the Cardinality as negative Numbers)
				ret[i] = new SparseGraph(getNumVertices());
			}
		}
		// distribute the SparseEdge Objects to the Components
		final SparseEdgeStream iter = SparseEdgeIterator();
		for (SparseEdge edge; null != (edge = iter.nextSparseEdge());) {
			if (!edge.isTyp(edgeTyp)) // && !Float.isInfinite(edge.weight))
				continue;
			final SparseGraph graph = ret[sets[edge.val]]; // need only to check the key
															// or the val, since all Edges
															// are within the same
															// Component!
			if (copy) // directly reusing the SparseEdge Object destroys 'this'
				graph.addEdge(iter.currEdge.key, // edge.key,
						edge.val, edge.typ, edge.weight);
			else {
				iter.removeCurrEdge();
				graph.addEdgeWoTranspose(iter.currEdge.key, edge);
			}
		}
		return ret;
	} //

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : simulated Annealing for Clustering
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the aggregated absolute Distances of the Elements from the Diagonal. For
	 * Elimination the unweighted Distances are more relevant to reduce Coupling.
	 * @see getEnergy for weighted absolute Distances
	 * @param _Position an optional (null allowed) Reordering of Rows and Columns
	 * @return the aggregated absolute Distances of the Elements from the Diagonal.
	 */
	public int[] getAbsDistances() {
		return getAbsDistances(null);
	} //

	/**
	 * Returns the aggregated absolute Distances of the Elements from the Diagonal by Distance. 
	 * For Elimination the unweighted Distances are more relevant to reduce Coupling.
	 * @see getEnergy for weighted absolute Distances
	 * @param _Position an optional (null allowed) Reordering of Rows and Columns
	 * @return the aggregated absolute Distances of the Elements from the Diagonal.
	 */
	public int[] getAbsDistances(final boolean usePositions) {
		return getAbsDistances(usePositions ? _Position : null);
	} //

	/**
	 * Returns the aggregated absolute Distances of the Elements from the Diagonal. 
	 * The unweighted Distances are more relevant to reduce Coupling.
	 * @see getEnergy for weighted absolute Distances
	 * @param position an optional (null allowed) Reordering of Rows and Columns
	 * @return the aggregated absolute Distances of the Elements from the Diagonal.
	 */
	public int[] getAbsDistances(final int[] position) {
		// since the Weights can be continuous, you cannot compress the Data by Grouping.
		// but to be able to eliminate Data, you have to bin Data anyway!
		// 
		final int[] ret = new int[itemCount];
		final IEdgeStreamIn iter = this.EdgeIterator();
		for (Edge edge; null != (edge = iter.nextEdge());) {
			final int diff = ((position == null)
					? (edge.key - edge.val)
					: (position[edge.key] - position[edge.val]));
			++ret[Math.abs(diff)]; //*edge.weight]; //Counts absolute Distances 
		}
		return ret;
	} //

	/**
	 * Returns the sum of the weighted absolute Distances of the Elements from the
	 * Diagonal. For combinatoric Diagonalization you don't need to know the absolute
	 * Cost, the relative Change is completely sufficient The Cost of every Cell is it's
	 * Weight multiplied with the Distance from the Diagonal determined by the Difference
	 * of Row and Column
	 * @return the sum of the weighted absolute Distances of the Elements from the
	 *         Diagonal.
	 */
	public double getEnergy() { return getEnergy(1); } //

	/**
	 * Returns the sum of the weighted absolute Distances of the Elements from the
	 * Diagonal. For combinatoric Diagonalization you don't need to know the absolute
	 * Cost, the relative Change is completely sufficient The Cost of every Cell is it's
	 * Weight multiplied with the Distance from the Diagonal determined by the Difference
	 * of Row and Column
	 * @param upperWeight relative Weight of the upper Diagonal. 
	 * Allows to smoothly switch between Blocking(1) and Sequencing(>N)
	 * @return the sum of the weighted absolute Distances of the Elements from the
	 *         Diagonal.
	 */
	public double getEnergy(double upperWeight) { return getEnergy(null, upperWeight); } //

	/**
	 * Returns the sum of the weighted absolute Distances of the Elements from the
	 * Diagonal. For combinatoric Diagonalization you don't need to know the absolute
	 * Cost, the relative Change is completely sufficient The Cost of every Cell is it's
	 * Weight multiplied with the Distance from the Diagonal determined by the Difference
	 * of Row and Column
	 * @param position optional (null allowed => Identity) Reordering of Rows and Columns
	 * @param upperWeight relative Weight of the upper Diagonal. 
	 * Allows to smoothly switch between Blocking(1) and Sequencing(>N)
	 * @return the sum of the weighted absolute Distances of the Elements from the
	 *         Diagonal.
	 */
	//public float getEnergy(final int[] position) { return getEnergy(position, 1); } //

	/**
	 * Returns the sum of the weighted absolute Distances of the Elements from the
	 * Diagonal. For combinatoric Diagonalization you don't need to know the absolute
	 * Cost, the relative Change is completely sufficient The Cost of every Cell is it's
	 * Weight multiplied with the Distance from the Diagonal determined by the Difference
	 * of Row and Column
	 * @param position optional (null allowed => Identity) Reordering of Rows (and Columns)
	 * @param upperWeight relative Weight of the upper Diagonal. 
	 * Allows to smoothly switch between Blocking(1) and Sequencing(>N)
	 * @return the sum of the weighted absolute Distances of the Elements from the Diagonal.
	 */
	public double getEnergy(final int[] position, final double upperWeight) {
		//float ret = 0; // [key][val] <=> [position[key]][position[val]]
		final IEdgeStreamIn iter = this.EdgeIterator();
		double forward = 0; 
		double backward = 0; 
		for (Edge edge; null != (edge = iter.nextEdge());) { 
			final int diff = ((position == null) 
					? (edge.key - edge.val) //row-col
					: (position[edge.key] - position[edge.val]));
			if (diff > 0) { //row > col == below Diagonal
				forward += edge.weight * diff;
			} else {
				backward += edge.weight * diff;
			}
		}
		return forward - backward*upperWeight;
	}

	/**
	 * The Cost of every Cell is it's Weight multiplied with the Distance 
	 * from the Diagonal i.e. the absolute Difference of Row and Column
	 * @param row1 first  Row to swap 
	 * @param row2 second Row to swap 
	 * @param upperWeight additional Weight Factor for Weights above the Diagonal 
	 * @return
	 * Don't need to know the absolute Cost, the relative Change is completely sufficient
	 */
	public double costOfRowSwap(final int row1, final int row2, final double upperWeight) {
		return costOfRowSwap(row1, row2, upperWeight, _Sequence, _Position);
	}

	/**
	 * The Cost of every Cell is it's Weight multiplied with the Distance 
	 * from the Diagonal i.e. the absolute Difference of Row and Column
	 * @param row1 first  Row to swap 
	 * @param row2 second Row to swap 
	 * @param upperWeight additional Weight Factor for Weights above the Diagonal 
	 * @param sequence optional (null allowed) Permutation to apply
	 * @param position optional (null allowed) inverse Permutation
	 * @return
	 * Don't need to know the absolute Cost, the relative Change is completely sufficient
	 */
	public double costOfRowSwap(final int row1, final int row2, final double upperWeight,
			final int[] sequence, final int[] positions) { // , final int n) {
		//return  costOfSwap(row1, upperWeight, sequence, positions) +
		//		costOfSwap(row2, upperWeight, sequence, positions); 
		return costOfRowMove(row1, row2, upperWeight, sequence, positions)
			 + costOfRowMove(row2, row1, upperWeight, sequence, positions);
	}
	

	/**
	 * The Cost of every Cell is it's Weight multiplied with the Distance 
	 * from the Diagonal i.e. the absolute Difference of Row and Column
	 * 
	 * @param row1 source Row to move from 
	 * @param row2 target Row to move to
	 * @param upperWeight additional weight Factor to Elements above the Diagonal  
	 * @param sequence optional (null allowed) Permutation to apply
	 * @param position optional (null allowed) inverse Permutation
	 * @return
	 * Don't need to know the absolute Cost, the relative Change is completely sufficient
	 */
	private double costOfRowMove(final int row1, final int row2, final double upperWeight,
			final int[] sequence, final int[] position) {
		if (row1 == row2)
			return 0; 
		
		double ret = 0;
		for (SparseEdge edge = this.rootNodes[sequence[row1]]; edge != null; edge = edge.next) {
			final int col = position[edge.val]; // i = _position[edge.key] == n1
			if (col == row1) { //
				continue; //Diagonal Elements swap Places, but keep Energy 0 
			}
			if (col == row2) { //Corner Elements swap Places (mirrored at Diagonal) 
				ret +=((row1 - col)*upperWeight)*Math.abs(edge.weight); //new-old Energy
				continue; //transposed Elements keep their Weights (when upperWeight == 1)
			}
			//Distance to Diagonal
			final double diff1 = (row1 > col) ? (row1 - col) : (col - row1)*upperWeight; 
			final double diff2 = (row2 > col) ? (row2 - col) : (col - row2)*upperWeight; 
			ret += Math.abs(edge.weight) * (diff2 - diff1); //new-old Energy 
		}
		return ret;
	}

	/**
	 * Don't need to know the absolute Cost, the relative Change is completely sufficient
	 * The Cost of every Cell is it's Weight multiplied with the Distance from the
	 * Diagonal determined by the Difference of Row and Column
	 * @param x
	 * @param n1 first  Row to swap 
	 * @param n2 second Row to swap 
	 * @param upperWeight additional Weight Factor for Weights above the Diagonal 
	 * @return
	 */
	public double costOfSwap(final int n1, final int n2, final double upperWeight) { // , final int n) {
		double rowCost =costOfRowSwap(n1, n2, upperWeight); 
		double colCost =costOfColSwap(n1, n2, 1/upperWeight)*upperWeight; 
		return rowCost + colCost; 
		//return costOfRowSwap(n1, n2, upperWeight) 
		//	 + costOfColSwap(n1, n2, upperWeight);
	}

	/**
	 * Don't need to know the absolute Cost, the relative Change is completely sufficient
	 * The Cost of every Cell is it's Weight multiplied with the Distance from the
	 * Diagonal determined by the Difference of Row and Column
	 * @param n1 first  Column to swap 
	 * @param n2 second Column to swap 
	 * @param upperWeight additional Weight Factor for Weights above the Diagonal 
	 * @return
	 */
	public double costOfColSwap(final int n1, final int n2, final double upperWeight) {
		return ((SparseGraph) trp()).costOfRowSwap(n1, n2, upperWeight, _Sequence, _Position);
	}

	/** Flag to test the Calculation of Permutation-Cost 	 */
	private static final boolean TESTING_DIAG = false; //true; //

	/**
	 * Diagonalization AND/OR Sequencing by permuting Rows and Columns either for Rows and Columns
	 * independently or coupled. Uses simulated Annealing. Sequence contains the Sequence /
	 * Permutation of the Rows. position contains the Indices / Positions of the Rows.
	 * @param sumNorm
	 * @return the relative Residuum of Energy from permuting the Indices. 
	 * A low Value indicates good Optimization. 
	 * A Value near 1 either indicates an extraordinarily good initial Permutation 
	 * or a non-optimizable Graph.   
	 */
	public double cluster(double upperWeight) {
		// Assert.A.FailureHandler = AStreamOut.DevNullOut;
		float ret = 0;
		VectorInt.IDENTITY(_Sequence);
		VectorInt.IDENTITY(_Position);
		final int numInner = itemCount * itemCount; //give the Chance to combine each with each other
		final int maxAccept = 10 * itemCount;
		//use the first Iterations 
		//to determine the appropriate Temperature.
		int numInits = MatrixFloat.NUM_ESTIMATE_MOVES; 
		final double initial = getEnergy(upperWeight); 
		float t = 0; // (initial) Temperature/Energy Estimate
		for (int numZeros = 0, j = 200; --j >= 0;) { // for cooling the System
			int numAccepted = 0;
			for (int k = numInner; --k >= 0;) { // choose two random Indices
				int n1 = (int) (itemCount * AGraph.RANDOM.nextDouble()); //
				int n2 = (int) ((itemCount - 1) * AGraph.RANDOM.nextDouble()); //
				if (n2 == n1) 
					++n2; // make sure they're not the same
				double de = costOfSwap(n1, n2, upperWeight); //
				if (--numInits > 0) //Initialization
					t += Math.abs(de); 
				else if (numInits == 0) { //Start
					t /= MatrixFloat.NUM_ESTIMATE_MOVES;
					L.n("Initial Temperature = Energy: ").l(t);
				} else if (MatrixFloat.ACCEPT_DELTA_ENERGY(de, t)) { //accept with Probability. 
					//In High-dimensional Spaces a simple Watermark also works!
					if (de != 0) { //0-Energy Swaps are always accepted
						ret += de;  //but not counted! 
						++numAccepted;
					}

					final double before = TESTING_DIAG ? getEnergy(_Position, upperWeight) : 0;
					final int swap1 = _Sequence[n1];
					_Sequence[n1] = _Sequence[n2];
					_Sequence[n2] = swap1;
					// final int swap2 = position[sequence[n1]]; position[sequence[n1]] =
					// position[sequence[n2]]; position[sequence[n2]] = swap2;
					_Position[_Sequence[n1]] = n1; // follow the Definition of Inverse													
					_Position[_Sequence[n2]] = n2; // directly
					if (TESTING_DIAG) {
						final double after = TESTING_DIAG ? getEnergy(_Position, upperWeight) : 0;
						Assert.EQUALS(after - before, de, 1e-3);
					}
				}
				if (numAccepted >= maxAccept) 
					break; // too many Changes; reduce Temperature
			}
			L.n("	numAccepted").l(numAccepted).l("	Temperature").l(t).l("	Energy").l(ret);
			if (numAccepted > 1) 
				t *= 0.9f; //reduce Temperature
			else if (++numZeros >= 2)
					break; // too few Changes, stop Iteration.
		}
		return 1+ret/initial;
	}

	/**
	 * prints this Graph into the given Stream.
	 * @param stream optional (null allowed) Stream to print to
	 */
	final public void printGraph(final StreamOutPrimitive stream) {
		printGraph(stream, 1, false, null);
	}

	/**
	 * tests statistical Diagonalization by permuting Rows and Columns either for Rows and
	 * Columns independently or coupled.
	 * @param stream the Stream to print to
	 * @param usePosition Flag whether to use the internal Reordering of the Items
	 * @param cutOffDistance the Distance at which to print Nonzero Elements differently.
	 */
	final public void printGraph(final StreamOutPrimitive stream, final double minWeight,
			final boolean usePosition, final String[] rowLabels) {
		printGraph(stream, usePosition, usePosition, minWeight, numVertices*minWeight, 1 + numVertices, false, true, rowLabels);
	}

	/**
	 * prints this Graph into the given Stream.
	 * @param stream the Stream to print to
	 * @param usePositionInRows Flag whether to use the internal Reordering of the Items in Row Labels
	 * @param usePositionInCols Flag whether to use the internal Reordering of the Items in Column Labels
	 * @param cutOffDistance the Distance at which to print Nonzero Elements differently.
	 * @param countOnTop Flag to print the Index at the Row Tops
	 * @param countAtSides Flag to print the Index at the Row Sides
	 * @param rowLabels optional (null allowed) Row Labels
	 */
	final public void printGraph(final PrintStream stream, 
			final boolean usePositionInRows, final boolean usePositionInCols, 
			final double minWeight, final double maxWeight, final int cutOffDistance,
			final boolean countOnTop, final boolean countAtSides, final String[] rowLabels) {
		final int digits = countAtSides ? 1 + ByRefInt.LOG(numVertices, 10) : 0; 
		final int initFactor = ByRefInt.POW(10, (byte) (digits - 1)); 
		final StringBuffer buf = new StringBuffer(numVertices + 2 * digits + 10); 
		addCountRows(stream, digits, numVertices, initFactor, usePositionInCols
			? _Sequence 
			: null, buf); 
		for (int i = -1; ++i < itemCount;) {
			buf.setLength(0); 
			final int row = usePositionInRows ? _Sequence[i] : i; 
			appendNumbering(buf, digits, initFactor, row); 
			fillLine(buf, row, usePositionInRows ? _Position : null, minWeight, maxWeight, cutOffDistance, countOnTop);
			appendNumbering(buf, digits, initFactor, row);
			if ((rowLabels != null) &&
				(rowLabels.length > row))
				buf.append(rowLabels[row]); 
			stream.println(buf); 
		}
		addCountRows(stream, digits, numVertices, initFactor, usePositionInCols
				? _Sequence 
				: null, buf);
	}

	/**
	 * @param stream
	 * @param digits
	 * @param initFactor
	 * @param buf
	 */
	private static void addCountRows(final PrintStream stream, final int digits,
			final int numVertices, final int initFactor, final int[] sequence,
			final StringBuffer buf) {
		buf.setLength(digits + numVertices);
		for (int d = digits; --d >= 0;)
			buf.setCharAt(d, '_');
		int factor = initFactor;
		for (int d = digits; --d >= 0;) {
			for (int i = numVertices; --i >= 0;) {
				final int col = sequence != null ? sequence[i] : i;
				buf.setCharAt(i + digits, (char) ('0' + (col / factor % 10)));
			}
			stream.println(buf);
			factor /= 10;
		}
	}

	/**
	 * @param buf
	 * @param digits
	 * @param initFactor
	 * @param value
	 */
	private static final void appendNumbering(final StringBuffer buf, final int digits,
			final int initFactor, int value) {
		int factor = initFactor;
		for (int d = digits; --d >= 0;) {
			buf.append((char) ('0' + (value / factor % 10)));
			factor /= 10;
		}
	}

	/**
	 * fills the given StringBuffer (or a newly created one) with the Values from this
	 * Graph.
	 * @param ret optional (null allowed) Buffer to fill
	 * @param row the Row to fill the Buffer for
	 * @param position optional reordering of this Graph
	 * @param minWeight the weight to mark with 0 
	 * @param cutOffDistance the i-j Distance to mark with a '#'
	 * @return the given or a new StringBuffer filled with
	 */
	final public StringBuffer fillLine(StringBuffer ret, final int row,
			final int[] position, final double minWeight, final double maxWeight, 
			final int cutOffDistance, final boolean countOnTop) {
		if (ret == null) ret = new StringBuffer(numVertices + 10);
		// else
		// ret.setLength(0);
		// clear Buffer
		final int offset = ret.length();
		for (int i = numVertices; --i >= 0;)
			ret.append(' ');
		// add Diagonal Counting
		final int i = Math.abs((position != null) ? position[row] : row);
		char diag = '0';
		final int col = _Sequence != null ? _Sequence[i] : i;
		int incr = col % 10;
		if (incr == 0) incr = (col / 10) % 10;
		diag += incr;
		if (!countOnTop) //set Diagonal Number beforehand
			ret.setCharAt(i + offset, diag);
		// fill the Edges
		for (SparseEdge edge = rootNodes[row]; edge != null; edge = edge.next) {
			// if (edge.weight == FALSE) continue;
			final int j = Math.abs((position != null) ? position[edge.val] : edge.val);
			char chr; 
			if (Math.abs(j - i) >= cutOffDistance) {
				if (cutOffDistance > 0)
					chr = '#'; //ignored Weight
				else {
					double ln = 9*Math.log(edge.weight / minWeight)/Math.log(maxWeight/ minWeight); 
					char lnInt = 
						(ln > 9) ? 9 : 
						(ln < 0) ? 0 : 
						(char) Math.round(ln); 					
					chr = (char)('0'+lnInt); 
				}
			} else if (edge.weight < minWeight)
				chr = '0'; 
			else 
				chr = '*'; 
			ret.setCharAt(j + offset, chr);
		}
		if (countOnTop) //set Diagonal Number afterwards
			ret.setCharAt(i + offset, diag);
		return ret;
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////
	// / Testing Methods
	// ///////////////////////////////////////////////////////////////////////////////////

	/** simple DAG for Testing */
	private static final int[][] DAG_EDGES_1 = {{ // 
			0, 1, 1, -1}, {0, 2, 1, -1}, {1, 2, 1, -1}}; // for undirected Graph

	/** complex DAG for Testing */
	private static final int[][] DAG_EDGES_2 = {
			{ // Origin, Target, directed, Weight
			0, 1, 1, 5}, {0, 2, 1, 3}, {1, 2, 1, 2}, {1, 3, 1, 6}, {2, 3, 1, 7},
			{2, 4, 1, 4}, {2, 5, 1, 2}, {3, 4, 1, -1}, { // negative Weight Edges make
															// Shortest Paths Calculation
															// questionable...
			3, 5, 1, 1}, { // ...but not for DAGs!!!
			// 5,1,1, 15}, { //makes this Graph cyclic!
					4, 5, 1, -2}}; // for undirected Graph

	/**
	 * Tests calculation of shortest Paths in a DAG Procedures for directed Graphs.
	 */
	public static void testShortestPathsInDags() {
		testShortestPathsInDags(6, DAG_EDGES_2);
		testShortestPathsInDags(3, DAG_EDGES_1);
	}; // for undirected Graph

	/**
	 * Tests calculation of shortest Paths in a DAG Procedures for directed Graphs.
	 */
	public static void testShortestPathsInDags(final int numVertices, final int[][] edges) {
		L.n("Testing Shortest Paths for directed Graphs", 1);
		final SparseGraph AL = new SparseGraph(numVertices, edges, true);
		final Map2DPainter frame = AL.display();

		Assert.IS_TRUE(AL.isDAG());
		/** The Result of searching the shortest Path */
		final int[] SHORTEST_PATH = {-1, -1, 1, 1, -1, -1};
		Assert.EQUALS(SHORTEST_PATH, AL.shortestPathsInDag(1), AL.getInt());
		// calculates the shortest Paths to the given Node
		final int[] NUM_FRAGMENTS = {0, 1, 2, 3, 4, 5};
		Assert.EQUALS(NUM_FRAGMENTS, AL.stronglyConnectedComponents(), AL.getInt());
		Assert.IS_TRUE(!AL.isHierarchic());
		L.n("Before creating the transitive Hull: " + AL);
		AL.createHull(true);
		L.n("After  creating the transitive Hull: " + AL);
		frame.repaint();
		// both Hulls should be identical. No easy Test though!
		// rather compare it to the corresponding MatrixGraph!
		final MatrixGraph mg = new MatrixGraph(numVertices, edges, true);
		mg.transitHullAt();
		Assert.IS_TRUE(mg.isSubGraph(AL));
		Assert.IS_TRUE(AL.isSubGraph(mg));
		// TODO: check for Equality of both Graphs //includes also check whether no Edges
		// have been left out!!!

		Assert.IS_TRUE(AL.isDAG());
		// Use negative Weights
		// Order topologically with B(==1) as the Starting Point.
		final int[] shortestPaths = AL.shortestPathsInDag(1);
		L.n(shortestPaths);
		// TODO: this fails...
		// Assert.EQUALS(SHORTEST_PATH, shortestPaths);
		// calculates the shortest Paths, should give the same Result.
		L.n(AL.stronglyConnectedComponents());
	}

	/**
	 * Tests calculation of shortest Paths in a DAG
	 */
	public static void testLargeGraph() throws FileNotFoundException, IOException {
		final int ThingID = 1662;
		final String GenlPath = "../../Databases/CycOnto/";
		// final String OutGenlFile = GenlPath + "Genl2.txt";
		final String GenlFile = GenlPath + "Genl.txt";
		final String IsA_File = GenlPath + "IsARedundant.txt";
		final String Out_File = GenlPath + "IsA2.txt";
		SparseGraph AL = new SparseGraph(2717);
		Reader is = new FileReader(GenlFile);
		StreamTokenizer st = new StreamTokenizer(is);
		AL.addEdges(new StreamTokenizer(is), true, -1, true); // false);
		is.close();

		// Eliminate degenerated Diamonds.
		// AL.eliminateDiamonds(new PrintStream(new FileOutputStream(OutGenlFile)));

		// Shortest Paths from Thing...
		// doesn't make sense, because multiple Inheritance is eliminated!
		Log.L.l(AL.shortestPathsInDag(ThingID)).n();
		int j = AL._Sequence.length;
		while (--j >= 0) {
			if (AL._Sequence[j] == ThingID) {
				L.l(j + ", ");
			}
		}

		// cleaning up the IsA Relations
		AL.createHull(false);
		// AL.Hull(true);

		is = new FileReader(IsA_File);
		st = new StreamTokenizer(is);
		ELIMINATE_EDGES(AL, st, new PrintStream(new FileOutputStream(Out_File)), true);

		int[] conn = AL.stronglyConnectedComponents();
		Log.L.l(conn);
		Log.L.l(AL._Sequence);
		int i = AL._Sequence.length;
		while (--i >= 0) {
			if (AL._Sequence[i] != i)
				L.n("Node " + i + " is equivalent to " + AL._Sequence[i]);
		}
		conn = AL.numFragments();
	}

	/**
	 * print the given Numbers As Chars
	 * @param values the
	 * @param offset
	 */
	final static public void printNumbersAsChars(int[] values, int offset) {
		for (int i = values.length; --i >= 0;) {
			L.l((char) (offset + i));
			L.l(values[i]);
		}
	}

	/**
	 * Tests all Methods of this Class with undirected Graphs These Examples are taken
	 * from the Book "Algorithms" by Robert Sedgewick
	 */
	public static void testUndirected() throws FileNotFoundException, IOException {
		L.n("Testing undirected Graphs:", 1);
		final ITester op = L.logs(0) ? new PrintOp(L) : null; // Apply the Print
																// Operation
		final SparseGraph AL = new SparseGraph(14, Sedgewick_29_1, 'A', false);
		L.n("Graph: ").n(AL);
		testConnCompUndirByDepth(AL, op);
		testConnCompUndirByBreadth(AL, op);
		// the same Subgraphs are also created on using only directed Edges!
		final SparseGraph directed = new SparseGraph(14, Sedgewick_29_1, 'A', true);
		final SparseGraph[] subGraphs = directed.connectedComponents();

		Assert.EQUALS(+0, AL.getDiscreteDistance('F' - 'A', 'F' - 'A'),
				"Distance between the same Node 'F'");
		Assert.EQUALS(+2, AL.getDiscreteDistance('B' - 'A', 'F' - 'A'),
				"Distance between Nodes 'B' and 'F' in the same Component");
		Assert.EQUALS(-1, AL.getDiscreteDistance('M' - 'A', 'F' - 'A'),
				"Distance between Nodes 'M' and 'F' in different Components");

		testDoublyConnectionUndir(AL);
	}

	/**
	 * Applications: a) Check for critical Joints which would split the Graph. b) Remove
	 * the (most critical) Joints, solve the resulting independent Problems and recombine
	 * the Solutions at the Joints.
	 */
	private static void testDoublyConnectionUndir(final SparseGraph AL) {
		// connect more Points
		AL.addEdges(Sedgewick_30_2b, 'A', false);
		L.n("\nGives out the Criticality of the Joint Vertices for an undirected Graph:");
		L.n("i.e. the Number of Components resulting from removing this Node");
		final int[] numFragments = AL.numFragments(); //
		L.n("Expected: HGAGJ");
		printNumbersAsChars(numFragments, 'A');
		final int[] expected = {1, 0, 0, 0, 0, 0, 2, 1, 0, 1, 0, 0, 0};
		Assert.EQUALS(expected, numFragments, expected.length,
				"#of Components resulting from removing the resp. Node.");
		L.n(numFragments);
		/*
		 * L.n("Connected(true) returns the connected Components ").l( "as Equivalence
		 * Classes"); Joints = AM.Connected(true); // i = Joints.length; while (--i >= 0)
		 * if (Joints[i] > 0){ L.l((char)('A'+i)); L.l(Joints[i]); } L.n();
		 * AStreamOut.ArrayToStream(L, AM.p , ", "); L.n(); // AStreamOut.ArrayToStream(L,
		 * Joints, ", "); L.n(); //
		 */
	}

	/**
	 * Application: Decompose a Graph into it's connected Components and solve the smaller
	 * Sub-Problems independently. Actually a faster way to determine connected Components
	 * is to determine Equivalence Classes.
	 * @see DisJointSet
	 * @see EquivalenceByParent
	 */
	private static void testConnCompUndirByBreadth(final SparseGraph AL, final ITester op) {
		L.n("\nBreadth Search through the whole undirected tree ").l(
				"results in the 'shortest' Distances (#Hops), ").l(
				"but also gives the (same) connected Components:").l(
				"(see Sedgewick 30: Connected Components)");
		AL.traverse(false, op, null, op);
		final int[] connComp = AL.getPositions();
		final int[] expected = new int[]{8, 11, 12, 9, 7, 10, -6, 5, -4, 2, 3, 1, 0};
		L.n(connComp); //
		Assert.EQUALS(expected, connComp, expected.length,
				"Sequence of Nodes separated by negative Nodes from new Components");
	}

	private static void testConnCompUndirByDepth(final SparseGraph AL, final ITester op) {
		L.n("Depth Search through the whole undirected tree").l(
				"gives out the Subtrees (connected Components):").l(
				"(see Sedgewick 30: Connected Components)");
		AL.traverse(true, op, null, op);
		final int[] connComp = AL.getPositions();
		final int[] expected = new int[]{10, 11, 12, 8, 7, 9, -6, 5, -4, 2, 3, 1, 0};
		// new int[] { 7, 11, 10, 9, 12, 8, -6, 5, -4, 1, 2, 3, 0 };
		Assert.EQUALS(expected, connComp, expected.length,
				"Sequence of Nodes separated by negative Nodes from new Components");
		final int[] roots = AL.connectedComponentIndex();
		final int[] expectedRoots = new int[]{
		// 1, -6, 1, 1, 1, 1, 1, 8, -2, 12, 12, 12, -4}; //when associating key-> value
				-6, 0, 0, 0, 0, 0, 0, -2, 7, -4, 9, 9, 9}; // when associating value ->
															// key
		Assert.EQUALS(expectedRoots, roots, "Connected Component Representatives. ");
		final SparseGraph[] subGraphs = AL.connectedComponents();
	}

	/**
	 * Linear List of Nodes to prove that each inner Node of a linear List is a Joint.
	 */
	static final int[][] linearList = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}};

	/**
	 * These Examples are taken from the Book "Algorithms" by Robert Sedgewick
	 */
	public static void testLinearListJoints() {
		L.n("Testing connected Components for a linear List:", 1);
		SparseGraph AL = new SparseGraph(13, linearList, false);
		final int[] joints = AL.numFragments(); //
		final int[] expected = new int[AL.numVertices];
		Arrays.fill(expected, 1, AL.numVertices - 1, 1);
		Assert.EQUALS(expected, joints, AL.numVertices,
				"every inner Node should be a Joint!");
		// printNumbersAsChars(joints, 'A');
	}

	public static void testStrongConnect() {
		L.n("Testing the Calculation of strongly Connected Components ", 1).l(
				"\n in directed Graphs:", 1);
		SparseGraph AM = new SparseGraph(13); // create a directed Graph
		AM.addEdges(Sedgewick_31_1a, true);
		AM.addEdges(Sedgewick_31_1c, true);
		AM.addEdges(Sedgewick_32_1, true);
		final int[] expected = {12, 1, 12, 5, 5, 5, 12, 8, 8, 12, 10, 12, 12};
		final int[] connComp = AM.stronglyConnectedComponents();
		Assert.EQUALS(expected, connComp, "Strongly connected Components");
		L.n("Which represents the following four Groups: ").n(
				"(B=1), (K=10), (D,E,F=5),(A,C,G,J,L,M=12),(H,I=8))");
		// gives out the strongly connected Vertices for directed Graphs
		final char[][] simplified = {
				{ // additional Edges for the directed Graph
				'I', 'M', (char) 3}, {'M', 'F', (char) 1}, {'M', 'B', (char) 1},
				{'M', 'K', (char) 1}};
		final SparseMatrix simpleExpected = new SparseMatrix(13, simplified, true);
		// create a directed Graph
		final SparseMatrix simple = AM.simplify(); // Simplify the Graph into
													// Subgraphs...
		L.n(simple);
		Assert.EQUALS(simpleExpected, simple); // requires the equals() Method to work!
	}

	public static void testDoublyConnect() {
		L.n("Testing the doubly connected Components of a Sample Graph:");
		SparseGraph AM = new SparseGraph(13); // create a directed Graph
		AM.addEdges(Sedgewick_31_1a, false);
		AM.addEdges(Sedgewick_31_1c, false);
		AM.addEdges(Sedgewick_32_1, false, Sedgewick_32_1.length - 2);
		// leave out the duplicate last two Edges!

		final SparseMatrix[] biConnectedGraphs = AM.biConnectedGraphs();
		for (int i = biConnectedGraphs.length; --i >= 0;)
			Assert.EQUALS(((i == 0) || (i == 2)), null != biConnectedGraphs[i]);
	}

	public static void testRobustness() {
		L.n("Testing the Robustness of a Graph made robust:");
		SparseGraph AM = new SparseGraph(13); // create a directed Graph
		AM.addEdges(Sedgewick_31_1c, false); // TODO: if the Sequence of this Line...
		AM.addEdges(Sedgewick_31_1a, false); // ...and this Line is swapped, the Test
												// fails!!!
		AM.addEdges(Sedgewick_32_1, false, Sedgewick_32_1.length - 2);
		// leave out the duplicate last two Edges!

		AM.makeRobust(1, 0);
		for (int j = 0, i = AM.numVertices; --i >= 0; j = i) {
			L.n("remove node ").l(i).l(" and prove that the Graph is still connected.");
			final SparseGraph remainder = AM.removeNode(i);
			final int[] cmpIndex = AM.connectedComponentIndex();
			int cmp = cmpIndex[j];
			if (cmp < 0) cmp = j;
			for (int k = AM.numVertices; --k >= 0;)
				if (k != i) if (cmpIndex[k] >= 0)
					Assert.EQUALS(cmp, cmpIndex[k]);
				else Assert.EQUALS(cmp, k);
			AM.join(remainder);
		}
	}

	final static public SparseMatrix getSedgewick29_1() {
		return new SparseMatrix(13, Sedgewick_29_1, 'A', true);
	}

	/**
	 * Initial Graph for the Sedgewick Tests: {Origin, Destination, Weight} used for
	 * undirected (29.1) and for directed, weighted Graphs (32.8)
	 */
	static final char[][] Sedgewick_29_1 = {{'A', 'B', 1}, {'A', 'C', 1}, {'L', 'M', 1},
			{'J', 'M', 2}, {'J', 'L', 3}, {'J', 'K', 1}, {'E', 'D', 2}, {'F', 'D', 1},
			{'H', 'I', 2}, {'A', 'F', 2}, // taking out these two Edges
			{'G', 'E', 1}, // breaks it up into 4 Components
			{'A', 'G', 4}, // taking out this isolates G completely!
			{'F', 'E', 2},};

	/**
	 * Additional Edges to Sedgewick_29_1 for the Sedgewick Test to make the Graph singly
	 * but not double connected (see Chapter 30). {Origin, Destination, Weight} used for
	 * undirected (30.2) and for directed, weighted Graphs (32.8)
	 */
	static final char[][] Sedgewick_30_2b = { // für zweifachen Zusammenhang
	{'G', 'C', 1}, {'G', 'H', 3}, {'J', 'G', 1}, {'L', 'G', 5},};

	/*
	 * //Setting up the Graph static final char[][] Sedgewick_31_1b = { //for undirected
	 * Graph {'A', 'G', (char) 6 }, {'B', 'D', (char) 2 }, {'B', 'E', (char) 4 }, {'B',
	 * 'C', (char) 1 }, {'C', 'E', (char) 4 }, {'E', 'L', (char) 4 }, {'F', 'L', (char) 2 },
	 * {'I', 'K', (char) 1 }, }; static final char[][] Sedgewick_31_1a = { //for un- and
	 * directed Graph {'A', 'B', (char) 1 }, {'A', 'F', (char) 2 }, {'D', 'F', (char) 1 },
	 * {'E', 'D', (char) 2 }, {'F', 'E', (char) 2 }, {'G', 'E', (char) 1 }, {'G', 'J',
	 * (char) 1 }, {'H', 'G', (char) 3 }, {'H', 'I', (char) 2 }, {'J', 'K', (char) 1 },
	 * {'J', 'L', (char) 3 }, {'J', 'M', (char) 2 }, {'L', 'G', (char) 5 }, {'M', 'L',
	 * (char) 1 }, };
	 */
	// Setting up the Graph
	static final char[][] Sedgewick_31_1d = { // for undirected Graph
	{'B', 'E', (char) 4}, {'F', 'L', (char) 2},};

	// Setting up the Graph
	static final char[][] Sedgewick_31_1b = { // for undirected Graph
	{'A', 'G', (char) 6}, {'B', 'C', (char) 1}, {'B', 'D', (char) 2},
			{'C', 'E', (char) 4}, {'I', 'K', (char) 1}, {'E', 'L', (char) 4},};

	static final char[][] Sedgewick_31_1c = { // 
	{'E', 'D', (char) 2}, {'G', 'J', (char) 1}, {'L', 'G', (char) 5},};

	static final char[][] Sedgewick_31_1a = { // for un- and directed Graph
	{'A', 'B', (char) 1}, {'A', 'F', (char) 2}, {'D', 'F', (char) 1},
			{'F', 'E', (char) 2}, {'G', 'E', (char) 1}, {'H', 'G', (char) 3},
			{'H', 'I', (char) 2}, {'J', 'K', (char) 1}, {'J', 'L', (char) 3},
			{'J', 'M', (char) 2}, {'M', 'L', (char) 1},};

	static final char[][] Sedgewick_32_1 = { // additional Edges for the directed Graph
	{'A', 'G', (char) 4}, {'C', 'A', (char) 1}, {'G', 'C', (char) 1},
			{'I', 'H', (char) 2}, // doppelt für gerichtete Graphen
			{'L', 'M', (char) 1}, // doppelt für gerichtete Graphen
	};

	static final int[] minSpanSedgewick_31_1 = {
	// -1, 0, 1, 1, 5, 3, 4, 8, 10, 6, 9, 5, 11 //valid Alternatives!
			// -1, 0, 1, 5, 5, 0, 4, 8, 10, 6, 9, 12, 9
			// -1, 0, 1, 5, 6, 0, 9, 8, 10, 12, 9, 5, 11
			// -1, 0, 1, 5, 5, 0, 4, 8, 10, 6, 9, 5, 11
			-1, 0, 1, 1, 3, 3, 4, 8, 10, 6, 9, 12, 9};

	static final int[] minDistanceFrom0Sedgewick_31_1 = {-1, 0, 1, 1, 5, 0, 4, 6, 10, 6,
			9, 5, 11};

	static final int[] minDistanceFrom4Sedgewick_31_1 = {5, 4, 4, 4, -1, 4, 4, 6, 10, 6,
			9, 4, 9};

	/** test the Calculation of minimum Paths / minimum Spanning tree */
	public static void testMinPathAndSpan() {
		L.n("Testing the Calculation of minimum Paths and Trees", 1).l(
				"\n in weighed undirected Graphs:", 1);
		final SparseGraph AM = new SparseGraph(13); // create undirected, connected Graph
		final boolean directed;
		AM.addEdges(Sedgewick_31_1b, directed = false);
		AM.addEdges(Sedgewick_31_1d, directed);
		AM.addEdges(Sedgewick_31_1a, directed);
		AM.addEdges(Sedgewick_31_1c, directed);
		L.n("Graph: ").l(AM);

		testMinDistanceOrSpan(AM, minDistanceFrom0Sedgewick_31_1, 55,
				0);
		testMinDistanceOrSpan(AM, minDistanceFrom4Sedgewick_31_1, 34,
				4);
		testMinDistanceOrSpan(AM, minSpanSedgewick_31_1, 16, -1);
		/*
		 * L.n("minimal Paths to the first Node (2nd Method)"); Assert.EQUALS(minPaths,
		 * AM.visitMinimum1(calcPaths)); pathsLength = AM.getLength(AM.sequence,
		 * calcPaths); L.n("Length:").l(pathsLength); Assert.EQUALS(minPathsLength,
		 * pathsLength); L.n("minimal Spanning Tree, the second way...");
		 * AM.visitMinimum1(calcPaths); pathsLength = AM.getLength(AM.sequence,
		 * calcPaths); L.n(AM.sequence, 1); L.n("Lengths:", 1).l(AM.value, 1); L.n("total
		 * Length:", 1).l(pathsLength, 1); //this fails! because visitMinimum1 does NOT
		 * calculate the min Span Tree! Assert.EQUALS(minTreeSize+1, pathsLength);
		 * //@TODO: this fails! The Size of the minimal Spanning Tree is not the same!
		 */
	}

	/**
	 * @param AM
	 * @param minDistanceExpected
	 * @param minPathsLength
	 * @param startNode
	 */
	private static void testMinDistanceOrSpan(final SparseGraph AM,
			final int[] minDistanceExpected, final int minPathsLength, final int startNode) {
		final String description = "minimal "
				+ ((startNode < 0) ? "Spanning Tree" : "Paths to Node" + startNode);
		L.n("Testing the Calculation of " + description);
		final int[] minDistanceResult = AM.minimumDistanceOrSpan(startNode);
		final float pathsLength = AM.getLength(startNode, minDistanceResult);
		Assert.EQUALS(minPathsLength, pathsLength, "Total Length of the " + description);
		if (minDistanceExpected != null)
			Assert.EQUALS(minDistanceExpected, minDistanceResult, description);
	}

	/**
	 * Tests the Generation of scale-free directed Graphs.
	 */
	public static void testGenerate() {
		L.n("testing Graph Generation");
		final int N = 800;
		final SparseGraph graph = new SparseGraph(N);
		// FILL_RANDOM_GRAPH(graph, 2, false);
		AGraph.FILL_SCALE_FREE_GRAPH(graph, N, 5, 2, true);
		final float[] fanIn = graph.getFanOut();
		L.n(fanIn);
	}

	/**
	 * Tests calculation of the topological Sort of a DAG Procedures for directed Graphs.
	 */
	public static void testDag() {
		final SparseGraph AL = new SparseGraph(13);
		AL.addEdges(Sedgewick_29_1, true);
		AL.addEdges(Sedgewick_30_2b, true);
		// AL must not be truncated, otherwise the last Item is not sorted!
		if (L.logs()) {
			L.println();
			AL.printGraph(L);
		}
		final int[] sorting = AL.getSortSequence();
		final int[] expected = {0, 1, 5, 9, 10, 11, 6, 4, 3, 7, 8, 2, 12};
		L.n(sorting);
		Assert.EQUALS(expected, sorting, AL.getInt());
		AL.sort();
		if (L.logs()) { // a DAG can be sorted so that ALL Nodes are above the Diagonal.
			L.println();
			AL.printGraph(L, 0, true, null);
		}
		Assert.IS_TRUE(AL.isDAG());
		AL.addEdge('G' - 'A', 0, true); // no longer a DAG!
		Assert.IS_TRUE(!AL.isDAG());
	}

	/**
	 * tests statistical Diagonalization by permuting Rows and Columns 
	 * either for Rows and Columns independently or coupled.
	 * @param sumNorm
	 */
	protected static final void testDiagonalization() {
		L.n("testing Graph Diagonalization");
		final int N = 80;
		final SparseGraph graph = new SparseGraph(N);
		// FILL_RANDOM_GRAPH(graph, 2, false);
		// graph.clear();
		boolean directed = false; //true; // 
		double avgNumEdges = directed ? 5 : 2.5; //
		double concentration = directed ? 2 : 1.5; // 
		AGraph.FILL_SCALE_FREE_GRAPH(graph, N, avgNumEdges, concentration, directed, true, false, true);
		// final int[] outDegree = graph.getOutDegree(); L.n(outDegree);
		final float[] fanOut = graph.getFanOut();
		L.n(fanOut);
		L.n("Test that identical Trafos don't change the Energy:");
		L.n("Before sorting:\n");
		L.n(graph).println();
		graph.printGraph(L); //, 0, false, rowLabels);
		
		String[] rowLabels = new String[N];
		for(int i = rowLabels.length; --i >= 0;) {
			rowLabels[i] = " Task#" + i; 
		}
		
		final double upperWeight = 1; //N; //2; // 
		//for (int i = -1; ++i < N;)
		//	Assert.EQUALS(0, graph.costOfSwap(i, i, upperWeight)); // This is trivial here!

		final double reduction = graph.cluster(upperWeight); 
		L.n("Overall Energy Reduction: ").l(reduction);
		// generate a Histogram of all Distances.
		final int[] distances = graph.getAbsDistances(true);
		// if (L.logs())
		// L.println(StreamOutPlotter.PLOT(distances, 3+ByRefInt.LOG(N, 10), 0, N));
		VectorInt.SUMM_F_AT(distances);
		int maxDistance = distances[distances.length - 1]; 
		if (L.logs()) {
			L.println("Distances from the Diagonal Histogram: ");
			L.println(StreamOutPlotter.PLOT(distances, 3 + ByRefInt.LOG(N, 10), distances[0], maxDistance)); // N));
		}
		// Eliminate the outermost Edges to get a simplified Graph.
		final int limitCount = maxDistance * 4 / 5; 
		// cut off the lower 20%
		int maxCol = 0;
		while (distances[++maxCol] < limitCount)
			;
		L.n("After  sorting and Elimination:").println(); // L.n(graph);
		int cutOffDistance = 0; //0 => no Cutoff
		double minWeight = .2; 
		graph.printGraph(L, true, false, minWeight, distances.length, cutOffDistance, true, true, rowLabels); //
		// Determine the Intervals around each Diagonal Element:
		final int[] subMatrices = graph.getSubMatrices(true, minWeight, maxCol);
		L.n("subMatrices: ").l(subMatrices).println();
		VectorInt.DIFF_AT(subMatrices);
		final int[] subMatrixSizes = VectorInt.COUNT(subMatrices, VectorInt.MAX(
				subMatrices, 0, subMatrices.length - 1));
		L.n(subMatrixSizes).println();
		if (L.logs()) {
			L.println(); 
			L.println("SubMatrix Size Histogram: ");
			L.println(StreamOutPlotter.PLOT(subMatrixSizes, 3 + ByRefInt.LOG(N, 10), 0,
					graph.itemCount)); // N));
		}
		// To avoid outermost Edges to influence Diagonalization too much,
		// don't use squared Metric,
		// but only absolute Distance or even a very slowly growing Metric.
	}

	/**
	 * Precondition for a successful Split 
	 * is a reordering of the Matrix with nearly minimum Energy.
	 * @param usePositions Flag to apply the internal Reordering 
	 * @param maxDist the right and left Limit to consider when searching for Blocks
	 * @param minWeight the minimum Weight to be considered for the Blocks   
	 * @return an Array with the Indices of newly starting SubMatrices (SubMatrix-Grouping).
	 */
	public int[] getSubMatrices(final boolean usePositions, 
			final double minWeight, final int maxDist) {
		final int[] tmp = new int[numVertices+1];
		int interval = -1; //grow the Interval...
		int max = Integer.MIN_VALUE; //...starting with the top-left Element...
		for (int i = -1; ++i < numVertices;) { // ...until two Intervals don't overlap
			if (max < i) // start a new Interval
				tmp[++interval] = i;
			final int row = usePositions ? _Sequence[i] : i;
			final int tmp1 = this.getMaxNeighbor(row, 
					usePositions ? _Position : null, minWeight, i + maxDist);
			if (max < tmp1) // the Transpose of a reordered Matrix 
				max = tmp1; // has the same Reordering!!!
			
			final int tmp2 = trp().getMaxNeighbor(row, 
					usePositions ? _Position : null, minWeight, i + maxDist);
			if (max < tmp2) 
				max = tmp2;
		}
		if (tmp[  interval] < numVertices) 
			tmp[++interval] = numVertices;
		final int[] ret = new int[++interval];
		System.arraycopy(tmp, 0, ret, 0, interval);
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////
	// / testing Maximum Flow Algorithm
	// /////////////////////////////////////////////////////////////////////////

	final static public void testMaximumFlow1() {
		final SparseGraph graph = new SparseGraph(4);
		graph.addFlowEdges(MatrixGraph.FLOW_GRAPH_1, 'A');
		final float totalFlow = graph.maxFlow(0, 3, 0);
		Assert.EQUALS(2000, totalFlow);
		// Assert.EQUALS(FLOW_EXPECTED_1, flow);
	}

	final static public void testMaximumFlow2() {
		final SparseGraph graph = new SparseGraph(6);
		graph.addFlowEdges(MatrixGraph.FLOW_GRAPH_2, 'A');
		final float totalFlow = graph.maxFlow(0, 5, 0);
		Assert.EQUALS(12, totalFlow);
		// Assert.EQUALS(FLOW_EXPECTED_2, flow);
	}

	final static public void testMaxPairing() {
		final int[][] pairing = {{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 1}, {1, 5}, {2, 2},
				{2, 3}, {2, 4}, {3, 0}, {3, 1}, {4, 3}, {4, 4}, {4, 5}, {5, 2}, {5, 4},
				{5, 5},};
		final int[] expected = {2, 1, 3, 0, 5, 4};
		final int[] result = MAX_PAIRING(pairing);
		Assert.EQUALS(expected, result);
	}

	final static public void testHamiltonCycle() {
		final SparseGraph graph = new SparseGraph(14);
		graph.addEdges(Sedgewick_31_1a, false);
		graph.addEdges(Sedgewick_31_1b, false);
		graph.HamiltonCycle();
	}

	// /////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class */
	public static void testIt() throws FileNotFoundException, IOException {
		L.n(" Testing ", 1).l(SparseMatrix.class.getName(), 1);
		testDiagonalization();
		testHamiltonCycle();
		testMaxPairing();
		testMaximumFlow1();
		testMaximumFlow2();
		testDag();
		testGenerate();
		testDoublyConnect();
		testUndirected();
		testMinPathAndSpan();
		testShortestPathsInDags();
		testStrongConnect();
		// testLargeGraph(); //process the Cycorp Ontology
		testLinearListJoints();
		testRobustness();
	}

	/**
	 * The main entry point for the application.
	 * @param args Array of TAB File Paths for a Table with a Relation on itself. 
	 */
	public static void main(final String[] args) throws Exception {
		SparseGraph sg1 = new SparseGraph(); 
		sg1.addEdge(0, 1); 
		int[] id = {0,1}; 
		int[] perm = {1,0}; 
		sg1._Position = id; 
		sg1._Sequence = id; 
		double e1 = sg1.getEnergy(id, 80);
		double e2 = sg1.getEnergy(perm, 80);
		double de = sg1.costOfSwap(1, 0, 80); 
		Assert.EQUALS(e2 - e1, de); 
		
		if (args.length == 0) {
			testIt(); return; 
		}
		// input is relational although this increases Throughput
		// but it imposes least Constraints on the Client 
		// and thus also allows it to operate streaming. 
		// print out the smallest Spanning Tree, the shortest Paths
		SparseGraph sg = new SparseGraph();
		StringIndex sx = new StringIndex(); 
		for (int i = -1; ++i < args.length;) {
			ResultSetSep rs = new ResultSetSep(args[i]); 
			for(;rs.next();) {
				String src = rs.getString(0); 
				String dst = rs.getString(1); 
				String weight = rs.getString(2); 
				double wt = 1; 
				try {
					wt = Double.parseDouble(weight);
				} catch(Exception x) {}
				int srcIdx = sx.set(src); 
				int dstIdx = sx.set(dst); 
				sg.addEdge(srcIdx, dstIdx, wt); //, typ)
			}
		}
		if (L.logs()) {
			L.println();
			sg.printGraph(L);
		}
		final int[] sequence = sg.getSortSequence();
		boolean isDag = sg.isDAG(sequence); 
		if (isDag) {
			String[] sorted = new String[sequence.length];   
			for (int i = -1; ++i < sequence.length;)
				System.out.println(sorted[i] = sx.UnMap(sequence[i]));
			System.out.println(); 
			String[] codes = sx.getList(); 
			//construct a Tree using an in-order Walk
			for(int i = -1; ++i < sequence.length;) { 
				int pos = sequence[i]; 
				sg.PrintInOrderWalk(System.out, pos, codes, null, null); 
			}
			return; 
			//SparseEdgeStream iter = sg.SparseEdgeIterator(); 
		}

		//try to reorder the Graph to diagonalize it. 
		double upperWeight = 2; 
		final double reduction = sg.cluster(upperWeight); 
		
		final int[] distances = sg.getAbsDistances(true); 
		VectorInt.SUMM_F_AT(distances); 
		// Eliminate the outermost Edges (lower 20%) to get a simplified Graph.
		final int limitCount = distances[distances.length - 1] * 4 / 5; // 
		int limit = 0;
		while (distances[++limit] < limitCount);
		double minWeight = .1; 
		sg.printGraph(System.out, true, true, minWeight, minWeight*distances.length, limit, true, true, null); //
		
		System.out.println("Transitive Hull:"); 
		sg.createHull(true); 
		sg.printGraph(System.out, true, true, 1, distances.length, 1000, true, true, null); //
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// #region Tree Printout
	///////////////////////////////////////////////////////////////////////////
	
	/** Default Value for the Indent String prepended to the Line	 */
	public static String INDENT_DEFAULT = "\t"; 
	
	/** Default Value for the Start String	 */
	public static String DEFAULT_START = ""; 

	/** Alternative Start String Value for a Tree-like Structure	 */
	public static String INDENT_FIRST = "+---"; 
	
	/** Alternative Start String Value for a Tree-like Structure	 */
	public static String INDENT_FIRST_1 = "+———"; 
	
	/** Alternative Indent String Value for a Tree-like Structure	 */
	public static String INDENT_NEXT = "|   "; 
	
	/**
	 * prints an In-Order Tree-Walk to the given Stream using the given 
	 * @param codes optional (null allowed) Codes to print; if null the Indices are printed. 
	 * @param ps the Stream to write to
	 * @param start the start Position to start from  
	 * @param prefix optional (null defaults to the Tab Character) Prefix for the Line 
	 * @param indent optional (null defaults to empty) String to prepend on Iteration 
	 * @param codes optional (null defaults to the Position) List of Strings to recode the Positions. 
	 */
	public void PrintInOrderWalk(PrintStream ps, int start, String[] codes, String prefix, String indent) {
		if (codes[start] == null)
			return; 
		if (indent == null)
			indent = INDENT_DEFAULT; //INDENT_NEXT; //
		if (prefix == null)
			prefix = DEFAULT_START; //INDENT_FIRST_1; // 
		SparseEdge root = rootNodes[start]; 
		ps.print(prefix); 
		if (codes == null) 
			ps.println(start); 
		else {
			ps.println(codes[start]); codes[start] = null; //track which have been visited.  
		}
		String newPrefix = indent + prefix; 
		for(;root != null; root = root.next) {
			PrintInOrderWalk(ps, root.val, codes, newPrefix, indent); 			
		}
	}
}
