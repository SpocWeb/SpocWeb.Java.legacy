package graphs;

import function.byref.ByRefDouble;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Arrays;

import math.matrix.MatrixDouble;
import math.vector.AVector;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.Assert;
import streamIO.IIStreamIn;
import streamIO.IIterAble;
import streamIO.IReSetAble;
import streamIO.Log;
import streamIO.exception.BaseException;
import streamIO.exception.OperationNotSupported;

/** Implements a contiguous 2D dynamic Linked List (for Cols) 
 * and dynamic Length (for Rows) Representation of sparse Matrices 
 * which can also be used for Graphs (Tree, Forest, Polygons or general Network)
 * represented by float Numbers stored in a dynamic ListItem Vector. 
 * It (de-)serializes to a similar Row-wise Representation, 
 * not to a relational Representation. 
 * Of course, instead of a linked List, a dynamic Array would have been possible 
 * and instead of an Array of Objects, two dynamic Arrays of int and double 
 * would be more efficient, since it saves the Pointer and the Object, 
 * but it is not as extensible to other Properties. 
 * A Normalization of this Representation would sort the Elements by ascending Column Index, 
 * additionally applying an Aggregation Funktion (typ. Min, but Sum or Count could also be possible).  
 * 
 * Contains Methods for sparse Matrix Multiplication. 
 * Matrix Division etc. must be implemented by Multiplication with the Inverse Matrix. 
 * Another way to speed up Matrix Multiplication is splitting up a Matrix 
 * into a Grid of Sub-Matrices in a way that many of them are Zero or Identity Matrices. 
 * A full Matrix is always the most expensive Representation for a (linear) Operation.   
 * 
 * @see graphs.SparseGraph subclasses this Class to add Graph Methods. 
 * In this Class there are mostly Methods to construct, traverse 
 * and (linear) map Graphs as well as to calculate Norm, Diagonal 
 * and solve for the Inverse Mapping (which is typically not as sparse anymore) 
 *
 * Undirected Graphs are implemented using two directed Edges.
 * It is well apted for sparse Graphs,
 * because most Operations are of Order O(V+E) and not O(V*V)
 *
 * This class contains Methods to work out Equivalence Relations (find
 * and (strict) order Relations (grtr(), less(), equals(), Distance(), sort())
 *
 * The relational Storage of a sparse Matrix (key, typ, val, weight) is briefest, 
 * but does not allow for direct Navigation unless sorted (by Key) 
 * or supplied with an Index, but that still requires some Searching. 
 * The sparse Matrix allows Navigation in the Direction of the Edges 
 * which is sufficient for most Algorithms. 
 * It can be implemented either using a linked List (which allows for Extensions like type & weight) 
 * or a dynamic Array (but with Extensions this becomes as expensive as linked Lists). 
 * The sparse Transpose can be used when direct Access to the incoming Edges is needed. 
 * The full Matrix is small for dense Matrices (> N²/2), 
 * because Row and Column are given implicitly by their Position.  
 * 
 * If one of the two Columns is unique in this List, it is a Tree or Forest,
 * i.e. it has no Cycles and no Diamonds.
 * When a Graph is a Tree or Forest, the Nodes of the Inverse
 * have only single or no Children (Leafs). This is necessary but not sufficient!
 * Any Forest can be converted into a Tree by introducing an artificial Root Node.
 *
 * Theory:
 * Three Types of directed Graphs are fundamental:
 * hierarchical: no Diamonds, every Node has a unique Path (bijective Relation of Name and Node)
 * 			A Hierarchy can be ensured by specifying only a single Parent for each Node.
 * 			A Consequence is that any strict Hierarchy cannot have more than (N-1) Edges
 * 			  thus this Property can be tested in O(min(N, E)) Steps!
 * 			The inverse Graph of a Hierarchy has at most a single Edge for each Node
 * 			and there must be at least one Node with no Parent, becoming a/the Root!
 * 	single Root: exactly one Node with no Parent
 * 	Forest:    more than one Node with no Parent
 * acyclic: Nodes don't have a unique Path (injective), Diamonds exist, but no Cycles
 * 			An acyclic Graph can be ensured on Construction
 * 				by recursively checking the Parent Nodes (Ancestors) up to the Root
 * 				for not being identical with this Node!
 * cyclic: Cycles exist, shortest Paths may still be well defined, but not 'longest'
 * negative Weight cyclic: Cycles with mixed Weights prevent Definition of shortest / longest Paths.
 *
 * Non-negative Weight Graphs can define a Metric on the Set of Nodes,
 * if the given Weights fulfill the Triangle Inequation.
 * The full Metric can also be calculated in Hull().
 *
 * Breadth Search for minimum Paths measured in # of Nodes passed
 * returns the shortest equiweight Paths between Edges.
 * Depth Search returns the Node Types and topological Sortings.
 * For weighted Paths with continuous Distances a[i,j]
 * the Priority Search returns the shortest weighted Paths
 * or the minimum Spanning Tree between Edges.
 *
 * All Spanning Tree, shortest Paths and topological Sort Results
 * can be given by an Enumeration of the Nodes.
 * The Spanning Tree of the transitive Hull or the Neighbors Form of a Graph
 * should result in the same Result.
 *
 * Degenerated Structures:
 * Degenerated Diamond: only 3 Nodes, this doesn't add Value to the Model and should be eliminated
 * Degenerated Cycles:
 * a) Node connected to itself
 * b) undirected Connection between two Nodes
 * c) minimal 3 Node Cycle
 *
 * The Difference between Shortest Paths and Spanning Tree can be demonstrated
 * by a simple geometric Triangle Example (three Nodes, three Edges):
 * 1->2(1), 2->3(1), 1->3(1.4142...)
 * Shortest Paths are 1->2, 1->3 with a total Length of 2
 * Spanning Tree  is  1->2, 2->3 with a total Length of 2.4142 (instead of 3)
 *
 * Problems addressed:
 * *Disjoint Set Methods like lastItemXxx() see also Class Equivalence
 * *Identifying connected Components (CC) of undirected Graphs
 *  and strongly connected Components of directed Graphs,
 *  which allows for simplifying these Graphs.
 *  Cycles can be identified, because they create (strongly) CC.
 * *Creating a simplified DAG (simple) and sorting it topologically (sort) as well
 *  as defining qualitative (non-connex) Order Relations and quantitative
 *  direct (EdgeWeight) and indirect shortest Edges between Nodes (Distance()
 *  equiweight w. Weight == 1, and single Source shortest Paths using visitMinimum()).
 * *visitMinimum can also find the minimum Spanning Tree.
 *
 * Some Facts on shortest Path Algorithms:
 * *The Single Source Shortest Path Algorithm for an Origin can be turned into a
 *  Single Destination Shortest Path Algorithm by reversing each Edge.
 *  For undirected Graphs both are the same.
 * *There is no Algorithm asymptotically faster to return a shortest Connection
 *  between only two Nodes (although on average they are stopped in Half the Time)
 * *Negative Weights are no Problem as long as they don't lead to
 *  negative Weigth Cycles, because those could be cycled indefinitely.
 *  To find the shortest Paths for negative Weights without negative Cycles
 *  use the Bellman-Ford Algorithm, although it is slower O(V*E) than Dijkstra's.
 * *By inverting or negating the Weights, also maximum Distances can be found.
 *  This can be used to construct the Generational Set of Paths (Inverse to the Hull)
 * *Relaxation Algorithms like Dijkstras rely on the Fact that a preliminary
 *  d(x,v) must be corrected if d(x,u) + d(u,v) is shorter.
 *  It runs in O(E*log(V)) when the Priority Queue is implemented as a Heap
 *  and can be sped up using a Fibonacci Heap.
 * *If a DAG is topologically sorted (O(V+E)), the shortest Paths can be derived
 *  by relaxing only the Connections along the Sort Order which also takes O(V+E).
 * *For finding the critical Path through a Pert Chart
 *  use Milestones as Vertices and Jobs as Edges with Durations as the Weight.
 *  Although it seems more natural to define the Inverse.
 *
 * TODO:
 * *Change the Default Weight from Infinity to 1.0!
 * *Mapping Vertices to Edges and Edges to Vertices is done
 *  by generating the inverse Graph.
 * *Checking whether this is not a Tree, is done
 *  by generating the reverse Graph and checking
 *  whether all Nodes have only 0 or 1 Children.
 * *Using the VectorInt Object (faster) instead of a Linked List 
 *  to implement that new Nodes can be added dynamically. 
 */
public class SparseMatrix 
extends AVector 
implements IIterAble, IGraph { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger for Testing */
	private static final Log L = new Log(SparseMatrix.class, 1);
	
	///////////////////////////////////////////////////////////////////////////
	
	/** Flag whether Directed Cycles are allowed in constructing a Graph 	*/
	public boolean allowDirectedCycles = true; 
	
	/** List containing the Nodes of the Forest.
	  * Nodes[0]..Nodes[NumNodes-1] are the starting Points
	  * for linked Lists of all connected Nodes. 
	  * 
	  * @see AVector: This List is dynamically enlarged on demand 
	  * and thus cannot be final. 
	  */
	protected SparseEdge[] rootNodes;
	
	/** Cache for the cyclic Property,
	  * ternary Value: -1 = false, 0 = unknown, +1 = true
	  */
	//protected byte hasCycles = UNDEF;
	
	/** Cache for the hierarchic Property,
	  * ternary Value: -1 = false, 0 = unknown, +1 = true
	  */
	//protected byte isHierarchic = UNDEF;
	
	////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/** @see graphs.IGraph#clear()	 */
	public void clear() {
		//final int ret = this.itemCount; 
		Arrays.fill(rootNodes,null); 
		//return ret; 
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	
	/**Returns the (Out-)Degree of the Node j,
	 * i.e. the Number of Edges coming out of this Node.
	 * 
	 * @see SparseMatrix#getFanOut(int) which returns the Sum of the Weights
	 * 
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 * 
	 * An 'Euler Tour' in a directed Graph is a cycle
	 * that traverses each EDGE exactly once, but may visit a Node more often.
	 * A Graph has a closed (open) 'Euler Tour' exactly when it is one Component
	 * and each Node's Degree is even (except for two Nodes).
	 *
	 * This is used to solve the 'Koenigsberger' Problem.
	 * For directed Graphs there are much more rigid restrictions!
	 *
	 * This is related to the NP-complete 'Hamilton' Problem,
	 * where a Path is searched that visits each NODE only once.
	 * It is not related to the Traveling Salesman Problem (TSP)	
	 */
	public int getDegree(final int j) { return getDegree(j); }
	
	/**Returns the (Out-)Degree of the Node j,
	 * i.e. the Number of Edges coming out of this Node.
	 * 
	 * @see SparseMatrix#getFanOut(int) which returns the Sum of the Weights
	 * 
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 * 
	 * An 'Euler Tour' in a directed Graph is a cycle
	 * that traverses each EDGE exactly once, but may visit a Node more often.
	 * A Graph has a closed (open) 'Euler Tour' exactly when it is one Component
	 * and each Node's Degree is even (except for two Nodes).
	 *
	 * This is used to solve the 'Koenigsberger' Problem.
	 * For directed Graphs there are much more rigid restrictions!
	 *
	 * This is related to the NP-complete 'Hamilton' Problem,
	 * where a Path is searched that visits each NODE only once.
	 * It is not related to the Traveling Salesman Problem (TSP)	
	 */
	public int[] getDegree() { return getOutDegree(); }
	
	/**Returns the (Out-)Degree of the Node j,
	 * i.e. the Number of Edges coming out of this Node.
	 * 
	 * @see SparseMatrix#getFanOut(int) which returns the Sum of the Weights
	 * 
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 * 
	 * An 'Euler Tour' in a directed Graph is a cycle
	 * that traverses each EDGE exactly once, but may visit a Node more often.
	 * A Graph has a closed (open) 'Euler Tour' exactly when it is one Component
	 * and each Node's Degree is even (except for two Nodes).
	 *
	 * This is used to solve the 'Koenigsberger' Problem.
	 * For directed Graphs there are much more rigid restrictions!
	 *
	 * This is related to the NP-complete 'Hamilton' Problem,
	 * where a Path is searched that visits each NODE only once.
	 * It is not related to the Traveling Salesman Problem (TSP)	
	 */
	public int getOutDegree(final int j) {
		int ret = 0;
		for (SparseEdge t = rootNodes[j];	t != null; t = t.next) 
			++ret; //any Node represents a Connection, no matter which Weight.
		return ret;
	}
	
	/**Returns the (Out-)Degrees of all Nodes,
	 * i.e. the Number of Edges coming out of this Node.
	 * 
	 * @see SparseMatrix#getFanOut() which returns the Sum of the Weights
	 * 
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 * 
	 * An 'Euler Tour' in a directed Graph is a cycle
	 * that traverses each EDGE exactly once, but may visit a Node more often.
	 * A Graph has a closed (open) 'Euler Tour' exactly when it is one Component
	 * and each Node's Degree is even (except for two Nodes).
	 *
	 * This is used to solve the 'Koenigsberger' Problem.
	 * For directed Graphs there are much more rigid restrictions!
	 *
	 * This is related to the NP-complete 'Hamilton' Problem,
	 * where a Path is searched that visits each NODE only once.
	 * It is not related to the Traveling Salesman Problem (TSP)	
	 */
	public int[] getOutDegree() {
		final int[] ret = new int[itemCount];
		for (int i = ret.length; --i >= 0;)
			ret[i] = getOutDegree(i); 
		return ret;
	}
	
	/**
	 * Returns the In-Degree of the Node j,
	 * i.e. the Number of Edges going into of this Node. 
	 * For several In Degrees, it is better to create the Inverse (Transpose)
	 * of this Graph.
	 * 
	 * @see SparseMatrix#getFanIn(int) 
	 * which returns the Sum of the incoming Weights
	 *
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 *
	 * Graphs in which all Degrees are the same, are called 'regular'
	 * Of course: getInt() = sum(Degree()) = n*Degree() for regular Graphs.
	 */
	public int getInDegree(final int i) { return trp().getDegree(i); }
	
	/**
	 * Returns the In-Degree of the Node j,
	 * i.e. the Number of Edges going into of this Node.
	 * For all In Degrees, it is better to create the Inverse (Transpose)
	 * of this Graph.
	 * 
	 * @see SparseMatrix#getFanIn(int) which returns the Sum of the incoming Weights
	 *
	 * In an undirected Graph, the In- and Out-Degrees are the same,
	 * so it is sufficient to determine the Out-Degrees simply called "Degree".
	 *
	 * Graphs in which all Degrees are the same, are called 'regular'
	 * Of course: getInt() = sum(Degree()) = n*Degree() for regular Graphs.
	 */
	public int[] getInDegree() { 
		if (transposed != null)
			return transposed.getDegree(); 
		final int[] ret = new int[numVertices]; 
		for (int j = ret.length; --j >= 0; ) {
			for (SparseEdge t = rootNodes[j]; t != null; t = t.next) {
				//any Node represents a Connection, no matter which Weight.
				++ret[t.val];
			}
		}
		return ret; 
	}
	
	/** @see SparseGraph#getInDegree() which returns the # of incoming Edges, not their Weight
	 * @see graphs.IGraph#getFanIn()	 */
	public double getFanIn(final int node) { return trp().getFanOut(node); }
	
	/** @see SparseGraph#getOutDegree() which returns the # of outgoing Edges, not their Weight
	 * @see graphs.IGraph#getFanOut()	 */
	public double getFanOut(final int node) {
		double sum = 0; 
		for (SparseEdge edge = this.rootNodes[node]; edge != null; edge = edge.next) {
			sum += edge.weight; }
		return sum;
	}
	
	/** @see SparseGraph#getInDegree() which returns the # of incoming Edges, not their Weight
	 * @see graphs.IGraph#getFanIn()	 */
	public float[] getFanIn() { 
		if (transposed != null)
			return transposed.getFanOut(); 
		final float[] ret = new float[itemCount]; 
		for (int j = ret.length; --j >= 0; ) {
			for (SparseEdge t = rootNodes[j]; t != null; t = t.next) {
				//any Node represents a Connection, no matter which Weight.
				ret[t.val]+=t.weight;
			}
		}
		return ret;
	}
	
	/** @see SparseGraph#getOutDegree() which returns the # of outgoing Edges, not their Weight
	 * @see graphs.IGraph#getFanOut()	 */
	public float[] getFanOut() {
		final float[] ret = new float[itemCount]; 
		for (int j = ret.length; --j >= 0; ) 
			ret[j] = (float) getFanOut(j); 
		return ret;
	}
	
	/** returns that Neighbor of the given Node, 
	 * which has a maximum Value below the given Limit 
	 * 
	 * @param key the Key whose outgoing Nodes have to be searched. 
	 * @param position optional (null allowed => Identity) discrete distance weighting (reordering) Function
	 * @param minWeight the minimum Weight to consider 
	 * @param maxCol the maximum Column to consider
	 * @return the maximum Distance of all outgoing Edges below the Limit. 
	 */
	public int getMaxNeighbor(final int key, final int[] position, final double minWeight, final int maxCol) {
		int max = Integer.MIN_VALUE; 
		for (SparseEdge edge = rootNodes[key]; edge != null; edge = edge.next) {
			if (edge.weight < minWeight)
				continue; 
			
			final int col = (position != null) ? position[edge.val] : edge.val;
			if (col > maxCol) //since searching forward only...
				continue; 
			
			if (col <= key) //since searching forward only...
				continue; 
			
			if (max < col)
				max = col; 
		}
		return max;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Returns an Iterator to the Elements of the List. 	 */
	public IIStreamIn Iterator() {
		return new SparseEdgeStream(this);
	}
	
	/**Returns an Iterator to the Elements of the List. 	 */
	public IEdgeStreamIn EdgeIterator() {
		return new SparseEdgeStream(this); }
	
	public SparseEdgeStream SparseEdgeIterator() {
		return new SparseEdgeStream(this); }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : dynamic Array Size Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		if (!indexInRange(i)) 
			return null; 
		return rootNodes[i];
	}

	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  Item	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	public Object setAt(final int index, final Object value) {
		throw new RuntimeException("Not implemented!"); 
	}
	
	/**Returns the component at the specified index.
	 *
	 * @param	  index   an index into this Array.
	 * @return	 the component at the specified index.
	 * @exception  ArrayIndexOutOfBoundsException  if an invalid index was given.
	 */
	private synchronized SparseEdge getRootEdge(final int index) {
		if (indexInRange(index)) 
			return rootNodes[index];
		return null;
	}

	/**Sets (adds or replaces) the component at the specified index.
	 * All other components in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code>
	 * and less than the current size of the Container.
	 *
	 * @param	  Item	the component to set (add or replace).
	 * @param	  index   the index of the object to remove.
	 * @return	 the component replaced by 'Item'.
	 * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
	 * @see		java.util.Array#size()
	 */
	private void setRootEdge(final int index, final SparseEdge value) {
		if (!indexInRange(index)) 
			setSize(index + 1); 
		rootNodes[index] = value;
	}

	/**Trims the capacity of this SparseMatrix to be the SparseMatrix's current
	 * size. An application can use this operation to minimize the
	 * storage of a SparseMatrix.	 
	 */
	final public synchronized void trimToSize() { 
		if (itemCount < rootNodes.length) 
			trimToSize(itemCount); 
	}
	
	/**Trims the capacity of this SparseMatrix to be the SparseMatrix's current
	 * size. An application can use this operation to minimize the
	 * storage of a SparseMatrix.	 
	 * Could also be named setInt or setSize, but these Names are already in Use. 
	 */
	public synchronized void trimToSize(final int size) {
		final SparseEdge[] oldRootNodes = rootNodes; 
		rootNodes = new SparseEdge[size]; 
		if (itemCount > 0) //the Rest is left with "null"
			System.arraycopy(oldRootNodes, 0, rootNodes, 0, itemCount); 
	}
	
	/**Returns the current capacity of this SparseMatrix.
	 *
	 * @return  the current capacity of this SparseMatrix.	 */
	final public int getCapacity() { return rootNodes.length; }
	
	/**Increases the capacity of this SparseMatrix, if necessary, to ensure
	 * that it can hold at least the number of components specified by
	 * the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity.	 */
	final public synchronized int setCapacity(final int minCapacity) {
		final int oldCapacity = (rootNodes == null ? 0 : rootNodes.length);
		if (minCapacity <= oldCapacity) 
			return oldCapacity;
		trimToSize(minCapacity); 
		return minCapacity;
	}
	
	/**Sets the size of this SparseMatrix. 
	 * If the new size is greater than the current size, 
	 * new <code>null</code> items are added to the end of the SparseMatrix. 
	 * If the new size is less than the current size, all
	 * components at index <code>newSize</code> and greater are discarded.
	 *
	 * @param   newSize   the new size of this SparseMatrix.	 */
	final public synchronized void setSize(final int newSize) {
		if (newSize > itemCount) { //enlarge
			setCapacity(newSize);
		} else if (FREE_MEMORY_EARLY) { //Initialize the Elements out of Bounds to 'null' or 0
			Arrays.fill(rootNodes, newSize, itemCount, null); //
		} //clearing is not necessary...except to free Memory
		itemCount = newSize;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor taking the Number of Nodes
	 * The individual Connections can be set later using addEdge()	 
	 * Constructs an empty SparseMatrix with the specified initial capacity
	 * and capacity increment.
	 *
	 * @param   initialCapacity	 the initial capacity of the SparseMatrix.
	 * @param   capacityIncrement   the amount by which the capacity is
	 *							  increased when the SparseMatrix overflows.	 */
	public SparseMatrix(final int numNodes_, final int capacityIncrement_) {
		capacityIncrement = capacityIncrement_;
		setCapacity(numNodes_);
		//mEnum = new ArrayEnum(Items, ItemCount);
		//mEnum = new ArrayIterator(this); 
	} //
	
	/** Defaults the initial Capacity	 */
	public SparseMatrix() {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
	}
	
	/** 
	 * 
	 * @param NumNodes Preset of the minimum Number of Nodes. 
	 */
	public SparseMatrix(final int NumNodes) {
		this(NumNodes, DEFAULT_CAPACITY_INCR);
	}
	
	/** Initializing Constructor taking the Number of Nodes
	  * and the Edges in the Order:
	  * {{Start, Stop, directed when != 0, Weight}, ...}
	  * additional individual Connections can be set later using addEdge()	 */
	public SparseMatrix(
		final int numNodes_,
		final int[][] edges_,
		final boolean directed) {
		this(numNodes_);
		addEdges(edges_, directed);
	}
	
	/** Initializing Constructor taking the Number of Nodes
	  * and the Edges in the Order:
	  * {{Start, Stop, directed when != 0, Weight}, ...}
	  * additional individual Connections can be set later using addEdge()	 */
	public SparseMatrix(
		final int numNodes_,
		final char[][] edges_,
		final boolean directed) {
		this(numNodes_);
		addEdges(edges_, directed);
	}
	
	/** Initializing Constructor taking the Number of Nodes
	  * and the Edges in the Order:
	  * {{Start, Stop, directed when != 0, Weight}, ...}
	  * additional individual Connections can be set later using addEdge()	 */
	public SparseMatrix(
		final int numNodes_,
		final char[][] edges_,
		final char offset,
		final boolean directed) {
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
	public SparseMatrix(
		final double[][] weights_,
		final boolean symmetric_,
		final double min_,
		final double max_) {
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
	public SparseMatrix(
		final double[][] weights_,
		final boolean symmetric_,
		final double min_) {
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
	public SparseMatrix(
		final float[][] weights_,
		final boolean symmetric_,
		final double min_,
		final double max_) {
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
	public SparseMatrix(
		final float[][] weights_,
		final boolean symmetric_,
		final double min_) {
		this(weights_, symmetric_, min_, Float.POSITIVE_INFINITY);
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public SparseMatrix(final IEdgeStreamIn edges) {
		this(edges, false); }
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public SparseMatrix(final IEdgeStreamIn edges, boolean transpose) {
		this(edges.getNumNodes());
		addEdges(edges, -1e10, 1e10, transpose);
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public SparseMatrix(final SparseMatrix al_, boolean transpose) {
		this(al_.EdgeIterator(), transpose); }
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public SparseMatrix(final SparseMatrix al_) { this(al_, false); }
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public SparseMatrix(final MatrixGraph AM, final double min_, final double max_, boolean transposed) {
		this(AM.getInt());
		addEdges(AM.EdgeIterator(), min_, max_, transposed);
	}
	
	/**Constructor copying from an Adjacency List to initialize itself.	 */
	public SparseMatrix(final MatrixGraph AM, final double min_, final double max_) {
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
	public SparseMatrix(final float[][] coords_, final double maxDist) {
		this(coords_.length); 
		float[] iPoint; //, jPoint;
		int i = coords_.length;
		while (--i >= 0) {
			iPoint = coords_[i];
			for (int j = i; --j >= 0;) {
				final double dist = Math.sqrt(VectorFloat.DIST_SQR(iPoint, coords_[j]));
				if (dist >= maxDist) {
					continue; }
				addEdge(i, j, (float) dist);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * When the Graph is represented as a Matrix, 
	 * this would be the antisymmetric Part of it.
	 * This is quite useful in drawing the Graph, 
	 * because with directed noncyclic Graphs the whole Information is be retained 
	 * and still both the Edges going into a Node as well as those goint out 
	 * and also their Direction can be readily obtained from the Node itself. 
	 * 
	 * This is not necessary for the MatrixGraph Representation, 
	 * because there it is readily available by direct Addressing.   
	 * 
	 * With G being the Graph, S the symmetric Part and A the Antisymmetric Part the following holds:  
	 * S*= S
	 * A*=-A
	 *2G = S + A 
	 *2G*= S - A 
	 * S = G + G*
	 * A = G - G* 
	 * 
	 * @TODO: This can be made quite generic, because it only iterates through all Nodes. 
	 * @return this Graph made antisymmetric 
	 */
	public SparseMatrix getAntiSymmetric() {
		final SparseMatrix ret; 
		try {
			ret = (SparseMatrix) getClass().newInstance(); // new SparseMatrix();
		} catch (Exception x) {
			throw new BaseException(x);
		}
		final IEdgeStreamIn edges = this.EdgeIterator();
		for (Edge edge; null != (edge = edges.nextEdge());) {
			ret.addEdge(edge.key, edge.val,  edge.weight);
			ret.addEdge(edge.val, edge.key, -edge.weight);
		}
		return ret;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Adds a (directed) Edge with Default Weight to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin
	 * @param destination
	 * @param directed
	 */
	public IGraph addEdge(
		final int origin,
		final int destination,
		final boolean directed) {
		return addEdge(origin, destination, directed, AGraph.DEFAULT_WEIGHT);
	}

	/** Adds a (directed) Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param destination Target Node of this Edge
	 * @param directed Flag whether this Edge should be directed
	 * @param type the Type of the Edge
	 */
	public void addEdge(
		final int origin,
		final int destination,
		final boolean directed,
		final int type) {
			addEdge(origin, destination, directed, AGraph.DEFAULT_WEIGHT, type);
	}

	/** Adds a (directed) Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin
	 * @param destination
	 * @param directed
	 * @param weight
	 */
	public IGraph addEdge(
		final int origin,
		final int destination,
		final boolean directed,
		final float weight) {
		return addEdge(origin, destination, directed, weight, AGraph.DEFAULT_TYPE);
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
		final boolean directed,
		final float weight, 
		final int type) {
		addEdge(origin, destination, type, weight);
		if (directed) 
			return this; 
		addEdge(destination, origin, type, weight);
		return this; 
	}
	
	/////////////////////////////////////////////////////////////////
	/// adding directed Edges
	/////////////////////////////////////////////////////////////////

	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin
	 * @param destination
	 */
	public SparseMatrix addEdge(final int origin, final int destination) {
		return addEdge(origin, destination, AGraph.DEFAULT_TYPE, AGraph.DEFAULT_WEIGHT);
	}

	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin
	 * @param destination
	 * @param weight
	 */
	public SparseMatrix addEdge(
		final int origin,
		final int destination,
		final double weight) {
		return addEdge(origin, destination, AGraph.DEFAULT_TYPE, (float) weight);
	}

	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin
	 * @param destination
	 * @param weight
	 */
	public SparseMatrix addEdge(
		final int origin,
		final int destination,
		final int type) {
		return addEdge(origin, destination, type, AGraph.DEFAULT_WEIGHT);
	}

	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin
	 * @param destination
	 * @param weight
	 */
	public SparseMatrix addEdge(
		final int origin,
		final int destination,
		final float weight) {
		return addEdge(origin, destination, 0, (float) weight);
	}
	
	/**
	 * removes the given Edge
	 * @param origin
	 * @param destination
	 * @return the Edge removed 
	 */	
	public SparseEdge removeEdge(final int origin, final int destination) {
		return removeEdge(origin, destination, 0);
	}

	/**
	 * removes the given typed Edge
	 * @param origin
	 * @param destination
	 * @param type
	 * @return the Edge removed 
	 */	
	public SparseEdge removeEdge(final int origin, final int destination, final int type) {
		if (origin == destination) 
			return null; 
		SparseEdge prev, edge = getRootEdge(origin);
		if (edge == null) 
			return  null; 
		if ((edge.val == destination)) {// && (edge.type == type)) { //Sonderfall: erster Knoten
			setRootEdge(origin, edge.next);
			return edge; 
		}
		while(edge != null) {
			prev = edge; edge = edge.next;
			if ((edge.val == destination)) { // && (edge.type == type)) { //Regelfall
				prev.next = edge.next; 
				return edge; 
			}
		}
		return null; 
	}
	
	/** Counter for the Edges 
	 * Maintaining this Counter is delicate, 
	 * since the Graph can be updated anytime! 	 */
	private int numEdges;
	
	/** Counter for the Edges 
	 * Maintaining this Counter is delicate, 
	 * since the Graph can be updated anytime! 	 */
	public int getNumEdges() { return numEdges; }
	
	/** Counter for the Vertices, 
	 * since there may be more Vertices than itemCount, 
	 * if no Edges originate from them 
	 * and most Algorithms don't need these Elements to exist and run faster.  
	 */  
	protected int numVertices;
	
	/** Counter for the Vertices 
	 * since there may be more Vertices than itemCount, 
	 * if no Edges originate from them 
	 * and most Algorithms don't need these Elements to exist and run faster.  
	 */  
	public int getNumVertices() { return numVertices; }
	
	/** Flag to enlarge the Array Sizes to Target Nodes / Columns 
	 * from which no Edges originate. 
	 * Slight Overhead for Matrix and Graph Operations, 
	 * but usually necessary for Completeness (but not Correctness or Operation) 
	 * of Graph Return Values.   
	 */
	public boolean enlargeToDrainEndNodes = false; //true; 
	
	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param destination Target Node of this Edge
	 * @param weight Weight of this Edge
	 * @param type 
	 */
	public SparseMatrix addEdge(
		final int origin,
		final int destination,
		final int type,
		final double weight) {
		return addEdge(origin, destination, type, (float) weight); 
	}
	
	/**Dynamically add an Edge to the Graph.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * Always chooses the shorter Edge, if the Edge already was defined.
	 * This generic Method could be shared between Matrix and Vector Representation. 
	 */
	public IGraph addEdge(final int start, final int end, final float weight, final int typ) {
		return addEdge(start, end, typ, weight); 
	}
	
	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param origin Originating Node of this Edge
	 * @param destination Target Node of this Edge
	 * @param weight Weight of this Edge
	 * @param type 
	 */
	public SparseMatrix addEdge(
		final int origin,
		final int destination,
		final int type,
		final float weight) {
		if ((origin == destination) && (!allowDirectedCycles)) 
			throw new IllegalArgumentException(
				"Reflexive (and cyclic) Edge at Vertex " + origin);
		if (transposed != null) 
			transposed.addEdgeWoTranspose(destination, origin, type, weight);
		addEdgeWoTranspose(origin, destination, type, weight);
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
		final FlowEdge to  = new FlowEdge(target,  capacity);  
		final FlowEdge fro = new FlowEdge(origin, -capacity); 
		addEdgeWoTranspose(origin, to );  to.trp = fro; 
		addEdgeWoTranspose(target, fro); fro.trp =  to; 
		return this; 
	}
	
	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param key Originating Node of this Edge
	 * @param val Target Node of this Edge
	 * @param weight Weight of this Edge
	 * @param typ 
	 */
	protected void addEdgeWoTranspose(
		final int key,
		final int val,
		final int typ,
		final float weight) { //null instead of getRootEdge(key) because it is set anyway below...
		addEdgeWoTranspose(key, new SparseEdge(val, weight, typ));
		//incCounters(key, val);	
	}
	
	/** Adds a directed Edge to the Graph. 
	 * Does not prevent multiple Edges, but cyclic ones!
	 * 
	 * @param key Originating Node of this Edge
	 */
	protected void addEdgeWoTranspose(final int key, final SparseEdge edge) {
		final SparseEdge oldRoot; //Optimization, only single Index Check
		if (!indexInRange(key)) {
			setSize(key + 1); 
			oldRoot = null; 
		} else 
			oldRoot = rootNodes[key]; 
		rootNodes[key] = edge; edge.next = oldRoot; 
		//final ListEdge oldRoot = getRootEdge(key); setRootEdge(key, edge); 
		//incCounters(key, edge.val);	}
		//private final void incCounters(final int key, final int val) {
		++numEdges;
		//For Graph Algorithms: must consider pure Drains at the End of the List too.
		if (                            (itemCount <= key)) 
			setSize(key      + 1); // 
		//if (enlargeToDrainEndNodes && (itemCount <= destination)) 
		//	setSize(destination + 1); // 
		if (numVertices <= key) 
			numVertices  = key + 1; // 
		if (numVertices <= edge.val) 
			numVertices  = edge.val + 1; // 

	}

	/**Adds or sets the Edge Cost in the Graph 
	 * no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 * @param typ
	 * @param override Flag whether to override the Weights of existing Connections 
	 * or to merge them according to minimum Length
	 * @param weight
	 */
	public float setEdge(final int key, final int val, final boolean override, final float weight) {
		return setEdge(key, val, AGraph.DEFAULT_TYPE, override, weight); }
	
	/**Adds or sets the Edge Cost in the Graph 
	 * but only if the Default Value is smaller than before or override is set.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 * @param typ
	 * @param override Flag whether to override the Weights of existing Connections 
	 * or to merge them according to minimum Length
	 * @param weight
	 */
	public float setEdge(final int key, final int val, final boolean override) {
		return setEdge(key, val, AGraph.DEFAULT_TYPE, override, AGraph.DEFAULT_WEIGHT); }
	
	/**Adds or sets the Edge Cost in the Graph 
	 * but only if the Default Value is smaller than before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 */
	public float setEdge(final int key, final int val) {
		return setEdge(key, val, AGraph.DEFAULT_TYPE, false, AGraph.DEFAULT_WEIGHT); }
	
	/**Adds or sets the Edge Cost in the Graph 
	 * no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge 
	 * 
	 * @param key
	 * @param val
	 * @param typ
	 * @param override Flag whether to override the Weights of existing Connections 
	 * or to merge them according to minimum Length
	 * @param weight
	 */
	public float setEdge(final int key, final int val, final int typ, final boolean override, final float weight) {
		final SparseEdge edge = getEdge(key, val, typ); 
		//if (weight < 0) weight = -weight;	//prevent negative Cost
		if (null == edge) {
			addEdge(key, val, typ, weight);
			return Float.POSITIVE_INFINITY; //FALSE; //
		}
		final float ret = edge.weight; 
		if (override || 
			(edge.weight > weight)) //could be made more generic, also to avoid renormalization in sparse Graphs!
			 edge.weight = weight;
		return ret; }
	
	/** @see graphs.IGraph#setEdge(int, int, boolean, boolean)	 */
	public float setEdge(int start, int end, boolean directed, boolean override) {
		return setEdge(start, end, directed, AGraph.DEFAULT_WEIGHT, override); }
	
	/**Set the Edge Cost in the Graph no matter which Value was set before.
	 * When not directed both Directions are created.
	 * The Cost is defaulted to True to set the Edge */
	public float setEdge(final int start, final int end, final boolean directed, final float weight, final boolean override) {
		float ret = setEdge(start, end, override, weight); 
		if (directed)
			return ret;
		ret = Math.min(ret, setEdge(end, start, override, weight)); 
		return ret; 
	}
	
	//////////////////////////////////////////////////////////////////////
	
	/** Adds all the Edges to the Graph
	  * @param edges Array with Rows {Origin, Target, Weight} 
	  * @param directed when true, only the lower Triangle is used.
	  */
	public void addEdges(final char[][] _edges, final boolean _directed, final int start, final int stop) {
		AGraph.ADD_EDGES(this, _edges, 'A', _directed, start, stop); }
	
	/** Adds all the Edges to the Graph
	  * @param edges Array with Rows {Origin, Target, Weight} 
	  * @param directed when true, only the lower Triangle is used.
	  */
	public void addEdges(final char[][] _edges, final boolean _directed, final int stop) {
		AGraph.ADD_EDGES(this, _edges, 'A', _directed, stop); }
	
	/** Adds all the Edges to the Graph
	  * @param edges Array with Rows {Origin, Target, Weight} 
	  * @param directed when true, only the lower Triangle is used.
	  */
	public void addEdges(final char[][] _edges, final boolean _directed) {
		AGraph.ADD_EDGES(this, _edges, 'A', _directed);
	}
	
	/** 
	 * loads the Matrix from the given streamIO. 
	 * This is slightly less effective than deriving it from SparseMatrix or MatrixGraph, 
	 * because the constant current Key cannot be exploited.  
	 * @param iter_ the Iterator delivering the Edge Objects 
	 * @param max_ the maximum allowed Value to accept this Edge, ignores large Distances
	 * @param min_ the minimum allowed Value to accept this Edge, 
	 * used to prevent either large negative Distances (min_ < 0) 
	 * or rather small Values (min_ > 0)
	 * or negative Values (min_ == 0)
	 */
	//public void addEdges(final StreamIn iter_, final double min_, final double max_, boolean transposed) {
	//	addEdges((IEdgeStreamIn) iter_, min_, max_, transposed);	}
	
	/** 
	 * loads the Matrix from the given streamIO. 
	 * This is slightly less effective than deriving it from SparseMatrix or MatrixGraph, 
	 * because the constant current Key cannot be exploited.  
	 * @param iter_ the Iterator delivering the Edge Objects 
	 * @param max_ the maximum allowed Value to accept this Edge, ignores large Distances
	 * @param min_ the minimum allowed Value to accept this Edge, 
	 * used to prevent either large negative Distances (min_ < 0) 
	 * or rather small Values (min_ > 0)
	 * or negative Values (min_ == 0)
	 */
	public void addEdges(final IEdgeStreamIn _iter, final double _min, final double _max, final boolean _transposed) {
		AGraph.ADD_EDGES(this, _iter, _min, _max, _transposed); }
	
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
	public void addEdges(
		final float[][] weights_,
		final boolean symmetric_,
		final double min_) {
		addEdges(weights_, symmetric_, min_, Float.POSITIVE_INFINITY); }
	
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
	public void addEdges(
		final float[][] _weights,
		final boolean _symmetric,
		final double _min,
		final double _max) {
		AGraph.ADD_EDGES(this, _weights, _symmetric, _min, _max); }
	
	/** Adds all the Edges to the Graph
	 * whose Value multiplied by Limit is larger than 1
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
	 */
	public void addEdges(
		final double[][] _weights,
		final boolean _symmetric,
		final double _min,
		final double _max) {
		AGraph.ADD_EDGES(this, _weights, _symmetric, _min, _max); }
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * If the Graph has more than 2 Items, it overrides the given Parameter!
	  */
	public void addEdges(final int[][] Edges, final boolean _directed) {
		AGraph.ADD_EDGES(this, Edges, _directed); }
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * @param offset the Offset to be subtracted from the Points, 
	  * resulting in the Points starting a 0, usually 'A'=64
	  */
	public void addEdges(final char[][] Edges, final char offset, final boolean directed) {
		AGraph.ADD_EDGES(this, Edges, offset, directed); }
	
	/** Adds the Edges from the given File to the Graph
	  * Additional Parameters are the Default Directedness and the Default Weight
	  * If reverse is true, the Direction of the Graph is reversed!
	  */
	public void addEdges(
		final StreamTokenizer st,
		final boolean directed,
		final float weight,
		final boolean reverse)
		throws IOException {
		AGraph.ADD_EDGES(this, st, directed, weight, reverse); }
	
	/** Tests whether sub is a Subgraph of sup
	 * The Term is extended so that Graphs with the same Edges but smaller weights 
	 * are also assumed to be SubGraphs.  
	 * @param sup the supposed Super Graph 
	 * @param sub the supposed SubGraph 
	 * @return true if sub is a SubGraph of sup. 
	 */
	public boolean isSubGraph(final IGraph sup) {
		return AGraph.IS_SUB_Graph(this, sup, true) >= 0; }
	
	/** Tests whether sub is a Subgraph of sup
	 * The Term is extended so that Graphs with the same Edges but smaller weights 
	 * are also assumed to be SubGraphs.  
	 * @param sup the supposed Super Graph 
	 * @param sub the supposed SubGraph 
	 * @return true if sub is a SubGraph of sup. 
	 */
	public boolean equals(final IGraph sup) { return AGraph.EQUALS(this, sup); }
	
	///////////////////////////////////////////////////////////////////
	
	/** 
	 * Loops through all Nodes connected to the given End.
	 * Can be used to avoid double Edges or choose only the shortest Edge etc. 
	 * @param start End Node of the Edge
	 * @param stop Start Node of the Edge
	 * 
	 * @return the weight of the Edge in the Graph, defined by Start and End Point
	 * , if it exists. +Infinity otherwise.
	 */
	public float getWeight(final int start, final int stop) {
		return getWeight(start, stop, Float.POSITIVE_INFINITY); }
	
	/** 
	 * Loops through all Nodes connected to the given End.
	 * Can be used to avoid double Edges or choose only the shortest Edge etc. 
	 * @param start End Node of the Edge
	 * @param stop Start Node of the Edge
	 * 
	 * @return the weight of the Edge in the Graph, defined by Start and End Point
	 * , if it exists. +Infinity otherwise.
	 */
	public float getWeight(final int start, final int stop, final int _type) {
		return getWeight(start, stop, _type, Float.POSITIVE_INFINITY); }
	
	/** 
	 * Loops through all Nodes connected to the given Start Node.
	 * Can be used to avoid double Edges or choose only the shortest Edge etc. 
	 * @param start End Node of the Edge
	 * @param stop Start Node of the Edge
	 * 
	 * @return the weight of the Edge in the Graph, defined by Start and End Point
	 * , if it exists. +Infinity otherwise.
	 */
	public float getWeight(final int start, final int stop, final float _default) {
		return getWeight(start, stop, 0, _default); }
	
	/** 
	 * Loops through all Nodes connected to the given Start Node.
	 * Can be used to avoid double Edges or choose only the shortest Edge etc. 
	 * @param start End Node of the Edge
	 * @param stop Start Node of the Edge
	 * 
	 * @return the weight of the Edge in the Graph, defined by Start and End Point
	 * , if it exists. +Infinity otherwise.
	 */
	public float getWeight(final int start, final int stop, final int _type, final float _default) {
		if (start == stop)
			return 0; 
		final SparseEdge e = getEdge(start, stop, _type); 
		if (e != null) 
			return e.weight; 
		return _default;
	}
	
	/** 
	 * Loops through all Nodes connected to the given Start Node.
	 * Can be used to avoid double Edges or to choose only the shortest Edge 
	 * @param start End Node of the Edge
	 * @param stop Start Node of the Edge
	 * 
	 * @return the Edge in the Graph, defined by Start and End Point
	 * , if it exists. null otherwise.
	 */
	public SparseEdge getEdge(final int _start, final int _stop) {
		return getEdge(_start, _stop, 0); }
	
	/** 
	 * Loops through all Nodes connected to the given Start Node.
	 * Can be used to avoid double Edges or to choose only the shortest Edge 
	 * @param start End Node of the Edge
	 * @param stop Start Node of the Edge
	 * 
	 * @return the Edge in the Graph, defined by Start and End Point
	 * , if it exists. null otherwise.
	 */
	public SparseEdge getEdge(final int _key, final int _val, final int _typ) {
		for (SparseEdge e = getRootEdge(_key); e != null; e = e.next) {
			//Loops through all Nodes connected to the given End.
			if ((e.val == _val) && (e.isTyp(_typ)))
				return e; 
		}
		return null;
	}
	
	/**Returns a String Representation of this Object.
	 * Prints out this Forest, 	 */
	public String toString() { //normally only a loop of printing out each List.
		final StringBuffer S = new StringBuffer();
		for(int i = -1; ++i < itemCount;) {
			final SparseEdge t = rootNodes[i];
			if (t != null)
				S.append(i).append("->").append(t).append('\n');
		}
		return S.toString();
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : canonicalize the Graph, Check for Equality
	////////////////////////////////////////////////////////////////////////////

	/**
	 * normally you would have to ignore the exact Positions of the Nodes,
	 * but the general Equivalence of two Graphs is an NP Complete Problem. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		if (!(obj instanceof SparseMatrix)) 
			return false;
		return equals((SparseMatrix) obj);
	}

	/**
	 * normally you would have to ignore the exact Positions of the Nodes,
	 * but the general Equivalence of two Graphs is an NP Complete Problem. 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final SparseMatrix arg) {
		if (arg.itemCount != itemCount) 
			return false;
		//sort both Graphs canonically
		arg.sortEdges();
		this.sortEdges();
		//Check all Nodes for Equality
		for (int i = itemCount; --i >= 0;) 
			if (!SparseEdge.EQUALS(rootNodes[i], arg.rootNodes[i])) 
				return false;
		return true;
	}

	/**sorts all Edges	
	 * and thus makes the Graph canonical
	 * (eliminated duplicate Edges, chooses only the shorter Edges)
	 */
	public void sortEdges() {
		//remove unnecessary Nodes
		while (rootNodes[--itemCount] == null);
		++itemCount;
		//sort the Edges
		for (int i = itemCount; --i >= 0;) 
			sortEdge(i);
	}

	/** sorts the Edges originating from the given Node	 
	 * through Bubble Sort
	 */
	public void sortEdge(int nodeNr) {
		//for a linked List Bubble Sort is just right, although it is O(n²)
		while (sweepEdgesReordered(nodeNr));
	}

	/** 
	 * Single Sweep of the Bubble Sort Algorithm applied on the linked List. 
	 * @param nodeNr the Node whose Edges should be reordered. 
	 * @return true when the Edges have been reordered. 
	 */
	private boolean sweepEdgesReordered(int nodeNr) {
		SparseEdge prev = null, next;
		SparseEdge edge = rootNodes[nodeNr];
		if (edge == null) {
			return false;
		}
		boolean reordered = false;
		while (null != (next = edge.next)) {
			//Loop through all Edges from this Node.
			if (edge.val >= next.val) {
				if (edge.val == next.val) { //eliminate duplicate Edge
					if (edge.weight > next.weight) {
						edge.weight = next.weight;
					}
					edge.next = next.next;
				} else { //reorder Edges
					reordered = true;
					edge.next = next.next;
					next.next = edge;
					if (prev != null) {
						prev.next = next;
					} else { //special Case of reordering first two Nodes...
						rootNodes[nodeNr] = next;
					}
					//also swap 'edge' an 'next':
					//prev = edge; edge = next; next = prev;
					prev = next; //faster than first swapping then setting! 
					continue;
				}
			}
			prev = edge;
			edge = next;
		}
		return reordered;
	}

	////////////////////////////////////////////////////////////////////////////
	/// Matrix Methods: Transpose, Mapping, 
	////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Cache for the transposed Matrix, since it is Calculated in O(E) 
	 */
	protected SparseMatrix transposed; 
	
	/** Returns the Transposed Matrix (inverted Graph),
	  * in which the Direction of all Edges is reversed.
	  * This is equivalent to the Inverse Relation
	  * @see streamIO.Object.Enumerator.Container.Relation.
	  * An undirected Graph is identical to it's Inverse.
	  * 
	  * When the Inverse has only Nodes with 0 or 1 Children,
	  * this is a Tree (or Forest) and thus has no Diamods or Cycles
	  * (except for one large, which should be broken). 
	  * 
	  * Could also use the transposing Copy-Constructor (slightly slower). 
	  */
	public SparseMatrix trp() { //TODO: should use an Iterator here!
		if (transposed != null)
			return transposed; 
		synchronized(this) {
			transposed = new SparseGraph(itemCount);
			for(int j = itemCount; --j >= 0;) {
				//any Node represents a Connection, no matter which Weight.
				for (SparseEdge t = rootNodes[j]; t != null; t = t.next) 
					transposed.addEdge(t.val, j, true, t.weight);
				//count In- and Out- Degree.
			}
			transposed.transposed = this; 
		}
		return transposed;
	}

	/**	product of sparse math.matrix and full vector (2.7)
	 * A*x=b
	 * @param x
	 * @param b
	 */
	public double[] map(final double[] x) {
		final double[] ret = new double[x.length];
		mapInternal(x, ret);
		return ret; 
	}

	/**	product of sparse math.matrix and full vector (2.7)
	 * A*x=b
	 * @param x
	 * @param b
	 */
	public void map(final double[] x, final double[] b) {
		VectorDouble.FILL_AT(b, 0); //clear b
		mapInternal(x, b); 
	}

	/**	product of sparse math.matrix and full vector (2.7)
	 * A*x=b
	 * @param x
	 * @param b
	 */
	private void mapInternal(final double x[], final double b[]) {
		final IEdgeStreamIn stream = EdgeIterator();
		for (Edge edge; null != (edge = stream.nextEdge());) {
			b[edge.key] += edge.weight*x[edge.val];
		}
	}

	/**	product of transpose sparse math.matrix and vector (2.7)
	 * x*A=b
	 * @param x
	 * @param b
	 */
	double[] mapTrp(final double[] x){
		final double[] ret = new double[x.length];
		mapTrpInternal(x, ret);
		return ret; 
	}

	/**	product of sparse math.matrix and full vector (2.7)
	 * A*x=b
	 * @param x
	 * @param b
	 */
	public void mapTrp(final double[] x, final double[] b) {
		VectorDouble.FILL_AT(b, 0); //clear b
		mapTrpInternal(x, b); 
	}

	/**	product of transpose sparse math.matrix and vector (2.7)
	 * x*A=b
	 * @param x
	 * @param b
	 */
	void mapTrpInternal(final double[] x, final double[] b){
		final IEdgeStreamIn stream = EdgeIterator();
		for (Edge edge; null != (edge = stream.nextEdge());) {
			b[edge.val] += edge.weight*x[edge.key];
		}
	}

	/** used by {LINBCG} for sparse multiplication (2.7)
	 * 
	 * @param n
	 * @param x
	 * @param r
	 * @param transposed
	 */
	public void map(double[] x, double[] r, boolean transposed) {
		if (transposed) {
			mapTrp(x,r);
		} else {
			map(x,r);
		} 
	}

	/** Storage for the Diagonal, calculated on Demand	 */
	private float[] diagonal; 

	/**
	 * calculated on Demand
	 * @return the Diagonal of this Matrix
	 */
	public float[] getDiagonal() {
		if (diagonal != null) {
			return diagonal; }
		diagonal = new float[itemCount];
		for(int i=itemCount; --i >= 0; ) {
			diagonal[i] = getWeight(i, i, 0); 
		}
		return diagonal;
	}
	
	/** solves only for the Diagonal Elements; used by {LINBCG} for preconditioner (2.7)
	 * 
	 * @param n
	 * @param b
	 * @param x
	 * @param itrnsp
	 */
	void solveDiagonal(final double b[], final double x[], final int itrnsp) {
		for(int i=0; i<x.length; i++) {
			x[i]=(diagonal[i] != 0 ? b[i]/diagonal[i] : b[i]);
		} 
	}

	/**	@see #linbcg(double[], double[], int, double, int, int[], double[])} 
	 * uses this Function for calculating the vector norm (2.7)
	 * either euklidean (itol <= 3) or the Max-Norm, 	
	 */
	static double norm(final double sx[], final int testMethod) {
		if (testMethod <= 3) {
			double sumSqr = 0;
			for (int i=sx.length; --i>=0; ) {
				sumSqr += sx[i]*sx[i]; } 
			return Math.sqrt(sumSqr);
		} else {
			double maxAbs = 0; 
			for (int i=0; i<sx.length; i++) {
				if (maxAbs < Math.abs(sx[i])) {
					maxAbs = Math.abs(sx[i]); } 
			}
			return maxAbs; 
		}
	}

	/** biconjugate Gradient Solution of Sparse linear Systems A*x=y (2.7) for x.
	 * Iterative Approach only requiring O(N) Operations. 
	 * @param b right Side of the Equation
	 * @param x tentative Solution or 0 Vector
	 * @param testMethod Switch which Convergence Test to use: 1,2,3 or 4
	 * @param tol Tolerance for the Error
	 * @param itmax maximum Iterations
	 * @param iter Number of Iterations used
	 * @param err actual Error achieved
	 */
	void solveLinBCG(final double b[], final double x[], final int testMethod, final double tol,
	final int itmax, final int[] iter, final double[] err) {

		double ak,akden,bk,bkden,bknum,bnrm,dxnrm,xnrm,zm1nrm,znrm;
		double[] p, pp, r, rr, z, zz;
		bkden = 0; //@TODO: initialize properly...
		final int n = itemCount; 
		p =new double[n];
		pp=new double[n];
		r =new double[n];
		rr=new double[n];
		z =new double[n];
		zz=new double[n];
		getDiagonal(); //calculate the Diagonal..., not quite Thread-safe. 

		iter[0]=0;
		map(x,r,false);
		for (int j=0; j<n; j++) {
			r[j]=b[j]-r[j];
			rr[j]=r[j];
		}
		znrm=1.0;
		if (testMethod == 1) {
			bnrm=norm(b,testMethod);
		} else if (testMethod == 2) {
			solveDiagonal(b,z,0);
			bnrm=norm(z,testMethod);
		} else if (testMethod == 3 || testMethod == 4) {
			solveDiagonal(b,z,0);
			bnrm=norm(z,testMethod);
			solveDiagonal(r,z,0);
			znrm=norm(z,testMethod);
		} else throw new RuntimeException("illegal itol in linbcg");
		solveDiagonal(r,z,0);
		while (iter[0] <= itmax) {
			++iter[0];
			zm1nrm=znrm;
			solveDiagonal(rr,zz,1);
			bknum=0;
			for (int j=0; j<n; j++) {
				bknum += z[j]*rr[j]; } 
			if (iter[0] == 1) {
				for (int j=0; j<n; j++) {
					p[j]=z[j];
					pp[j]=zz[j];
				}
			} else {
				bk=bknum/bkden;
				for (int j=0; j<n; j++) {
					p[j]=bk*p[j]+z[j];
					pp[j]=bk*pp[j]+zz[j];
				}
			}
			bkden=bknum;
			map(p,z,false);
			akden=0; 
			for (int j=0; j<n; j++) {
				akden += z[j]*pp[j];
			}
			ak=bknum/akden;
			map(pp,zz,true);
			for (int j=0; j<n; j++) {
				x[j] += ak*p[j];
				r[j] -= ak*z[j];
				rr[j] -= ak*zz[j];
			}
			solveDiagonal(r,z,0);
			if (testMethod == 1 || testMethod == 2) {
				znrm=1.0;
				err[0]=norm(r,testMethod)/bnrm;
			} else if (testMethod == 3 || testMethod == 4) {
				znrm=norm(z,testMethod);
				/** Error Tolerance for biconjugate Gradient Solution: 1e-14  */ 
				if (Math.abs(zm1nrm-znrm) > ByRefDouble.DOUBLE_FULL_ACCURACY*znrm) {
					dxnrm=Math.abs(ak)*norm(p,testMethod);
					err[0]=znrm/Math.abs(zm1nrm-znrm)*dxnrm;
				} else {
					err[0]=znrm/bnrm;
					continue;
				}
				xnrm=norm(x,testMethod);
				if (err[0] <= 0.5*xnrm) {
					err[0] /= xnrm;
				} else {
					err[0]=znrm/bnrm;
					continue;
				}
			}
			L.n("iter=").l(iter[0]).l(" err=").l(err[0]);
			if (err[0] <= tol) {
				break; } 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : generating Coordinate Proposals from the Distances
	////////////////////////////////////////////////////////////////////////////
	
	/** @return a Proposal for the nDim Coordinates of the Nodes  */
	public float[][] generateGraph(final int dim) {
		return this.EdgeIterator().generateGraphics(numVertices, dim);
	}

	/** @return a Proposal for the nDim Coordinates of the Nodes  */
	public float[][] generateGraph(final float[][] _startPoints) {
		return this.EdgeIterator().generateGraphics(_startPoints);
	}

	////////////////////////////////////////////////////////////////////////////
	//Equivalence Relation Operations Start
	//Every Item must have only one 'Parent'!, i.e. equivalent Item
	//which is not guaranteed with general Graphs,
	//where each Item can have many Parents that are completely independent!
	//these Methods are also defined in Forest.Equivalence but not tested here!
	////////////////////////////////////////////////////////////////////////////
	

	////////////////////////////////////////////////////////////////////////////
	/// #region : Matrix Operations: Vector Mult. Matrix Mult. Transp. 
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Testing all Matrix Operations: Vector Mult. Matrix Mult. Transp. 
	////////////////////////////////////////////////////////////////////////////
	

	static final int NP=5;
	static final int NMAX = 2*NP*NP+1;
	
	static double ainit[][]={
		  {3,0,1,0,0,
		},{0,4,0,0,0,
		},{0,7,5,9,0,
		},{0,0,0,0,2,
		},{0,0,0,6,5}
		};
	
	static double cinit[][]={
		  {1.0,0.5,0.0,0.0,0.0,
		},{0.5,2.0,0.5,0.0,0.0,
		},{0.0,0.5,3.0,0.5,0.0,
		},{0.0,0.0,0.5,4.0,0.5,
		},{0.0,0.0,0.0,0.5,5.0}
		};
			
	static double binit[][]={
		  {1,1,0,0,0,
		},{1,2,1,0,0,
		},{0,1,3,1,0,
		},{0,0,1,4,1,
		},{0,0,0,1,5}
		};
	
	/** tests the Conversion of a square Matrix to a sparse one.	 */
	private static final void testSquare2sparse() {
		double[][] a=MatrixDouble.COPY(ainit);
		final SparseMatrix list = new SparseMatrix(a, false, 0.5);
		final MatrixGraph matrix = new MatrixGraph(list, 0, 0); 
		L.n("Original Matrix\n");
		for (int i=0; i<NP; i++) {
			for (int j=0; j<NP; j++) 
				L.l(a[i][j]); 
			L.n();
		}
		float[][] aa = matrix.a; 
		L.n("Reconstructed Matrix\n");
		for (int i=0; i<NP; i++) {
			for (int j=0; j<NP; j++) {
				L.l(aa[i][j]);} 
			L.n();
		}
		//Assert.EQUALS(aa, ainit);
		Assert.EQUALS(ainit, aa);
	}
	
	/** tests multiplying with the Matrix Transpose
	 */
	static void testMapTrp(){
		double[][] a;
		double[] ax, b;
		double x[]={1,2,3,4,5};
	
		ax =new double[NP];
		b  =new double[NP];;
		a=MatrixDouble.COPY(ainit);
		final SparseMatrix list = new SparseMatrix(a, false, 0.5);
		list.mapTrp(x,b);
		for (int i=0; i<ax.length; i++) {
			ax[i]=0;
			for (int j=0; j<a.length; j++) {
				ax[i] += a[j][i]*x[j]; } 
		}
		L.n("ax").l("result");
		for (int i=0; i<ax.length; i++) {
			L.n().l(ax[i]).l(b[i]); } 
		Assert.EQUALS(ax, b);
	}
	
	/**
	 * tests Mapping of a Vector from the right
	 */
	static void testMap() {
		double[][] a;
		double[] ax, b;
		double x[]={1,2,3,4,5};
	
		ax =new double[NP];
		b  =new double[NP];
		a=MatrixDouble.COPY(ainit);
		final SparseMatrix list = new SparseMatrix(a, false, 0.5);
		list.map(x,b);
		for (int i=0; i<ax.length; i++) {
			ax[i]=0; 
			for (int j=0; j<x.length; j++) {
				ax[i] += a[i][j]*x[j]; } 
		}
		L.n("ax").l("result");
		for (int i=0; i<ax.length; i++) {
			L.n().l(ax[i]).l(b[i]); } 
		Assert.EQUALS(ax, b);
	}

	/**
	 * tests the biconjugate Gradient Solution of Sparse Systems (2.7)
	 */
	static void testBCG() {
		for(int ITOL = 5; --ITOL > 0; ) {
			testBCG(ITOL);
		}
	}

	/**
	 * tests the biconjugate Gradient Solution of Sparse Systems (2.7)
	 *
	 */
	static void testBCG(final int ITOL) {
		//int NSIZE = 59;
		//int NP = 20;
		final double TOL = 1e-9; 
		final int ITMAX = 75;

		int[] iter = new int[1];
		double[] b, bcmp, x, err;
	
		b   = new double[NP];
		bcmp= new double[NP];
		x   = new double[NP];
		err = new double[1];
		for (int i=0; i<NP; i++) {
			x[i]=0;
			b[i]=0;
		}
		b[0]=3.0;
		b[NP-1] = -1.0;
		final SparseMatrix matrix = new SparseMatrix(ainit, false, 0.1);
		matrix.solveLinBCG(b,x,ITOL,TOL,ITMAX,iter,err);
		L.n("Estimated error:"+err[0]);
		L.n("Iterations needed:"+iter[0]);
		L.n("Solution vector:");
		for (int i=0; i<NP; i++) 
			L.l(x[i]); 
		L.n();
		matrix.map(x,bcmp);
		L.n("press RETURN to continue...\n");
		L.n("test of solution vector:\n");
		L.n("a*x \t b");
		for (int i=0; i<NP; i++) { 
			L.n(bcmp[i]+"\t"+b[i]);} 
		Assert.EQUALS(b, bcmp);
	}
	
/*	private static final void testConversion() {
		MatrixGraph math.matrix = null; 
		SparseMatrix list = new SparseMatrix(math.matrix, 0, Float.POSITIVE_INFINITY);
	}
*/	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 0)
			testIt();
		else {
			
		}
	}

	static void TestPerformance() {
		long then = System.currentTimeMillis();
		Pair[] arr = new Pair[10000];
		for (int j = 10000; --j >= 0; ) {
			for (int i = arr.length; --i >= 0; ) {
				arr[i] = new Pair(); //10 Seconds
			}
		}
		long now = System.currentTimeMillis();
		System.out.println("\nDiff: " + (now - then)/1000);
	}

	/**Tests all Methods of this Class	 */
	public static void testIt() throws FileNotFoundException, IOException {
		L.n(" Testing ", 1).l(SparseMatrix.class.getName(), 1);
		testSquare2sparse(); 
		testMap();
		testMapTrp();
		testBCG();
		TestPerformance(); 
	}

}

/**
 * Iterator Class for this Type of Container
 * This Iterator is quite similar to the Iterator of
 * @see streamIO.Object.Enumerator.Container.HashContainer 
 * because both iterate through a 2Dim structure.
 * This is one of the many Examples where a Nested structure requires a nested Iterator 
 */
final class SparseEdgeStream 
extends AEdgeStreamIn {
	
	/** Current Record, i.e. Edge from currLine 	 */
	protected SparseEdge currListEdge;
	
	/** previous Record, for removing the Edge 	 */
	protected SparseEdge prevListEdge;
	
	/**Next Record, necessary,
	 * because otherwise available() cannot return accurate Values 	 */
	//	protected ListEdge nextEdge;
	
	/**Local Reference to the Adjacency List being iterated over	 */
	protected SparseMatrix al;
	
	/**Initializing Constructor	 */
	SparseEdgeStream(final SparseMatrix al_) {
		al = al_;
		currEdge.key = -1;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Cannot position absolutely, only to the Beginning of outgoing Edges 
	 * @see graphs.AEdgeStreamIn#getPosition()	 */
	public long getPosition() { return currEdge.key; }
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public long reSet(long _position) { //throws NoSuchMethodException {
		final int pos = (int) _position; 
		currEdge.key = pos; //Key is rarely set!
		currListEdge = prevListEdge = null;
		return pos; 
	}
	
	/** Resets the Iterator to the last marked Position,
	  * done automatically on Instantiation
	  * By Default the Start of the Iterator is marked on Instantiation	 */
	public IReSetAble reSet() { //throws NoSuchMethodException {
		reSet(-1);
		return this;
	}
	
	/** returns the currently available Records	 */
	public long availAble() { return 1; }

	/** returns the Order of the Items in this List, in this case 0 = no Order	 */
	public byte getOrder() { return 0; }

	/** @see graphs.IEdgeStreamIn#getNumNodes()	 */
	public int getNumNodes() { return al.getInt(); }
	
	/** removes the current Edge, and returns it 	*/
	public Object removeCurrent() { return removeCurrEdge(); }
	
	/** removes the current Edge, and returns it 	*/
	public SparseEdge removeCurrEdge() { 
		if (currListEdge == null)
			return null;
		final SparseEdge ret = currListEdge; 
		if (prevListEdge != null)
			prevListEdge.next = currListEdge.next;
		else 
			al.rootNodes[currEdge.key] = currListEdge.next;
		currListEdge = currListEdge.next; //propagate right away... 
		return ret; //...to prevent currListEdge pointing to an outside Edge! 
	}
	
	/** Empty Dummy Implementation	 */
	public Object removeNext() {
		throw new OperationNotSupported();
	}
	
	/** 
	 * although it is a bit paranoid (private Method), currEdge.key is set here. 
	 * @param node the Node to get the Edges from 
	 * @return the first Edge originating from this Node 
	 */
	private SparseEdge getStartingListEdge(final int node) {
		return al.rootNodes[currEdge.key = node];
	}
	
	/** 
	 * although it is a bit paranoid (private Method), currEdge.key is set here. 
	 * @param node the Node to get the Edges from 
	 * @return the first Edge originating from this Node 
	 */
	private Edge getStartingEdge(final int node) {
		currListEdge = getStartingListEdge(node); 
		if (currListEdge == null) 
			return null;
		currEdge.val    = currListEdge.val; //new Integer(currEdge.Node);
		currEdge.typ    = currListEdge.typ; //
		currEdge.weight = currListEdge.weight; //
		return currEdge;
	}
	
	/** @return the next Record, (returning the currently available Records) 	*/
	public SparseEdge nextSparseEdge() { //
		if (currListEdge != null) { //regular Case within a Row 
			prevListEdge = currListEdge; 
			if(null !=(currListEdge = currListEdge.next)) 
				return currListEdge;
		}
		//move to a new Row...
		prevListEdge = null; 
		if (filter >= 0) {
			if (currEdge.key == filter)  
				return null; //at the End...
			return currListEdge = getStartingListEdge(filter); //not started..
		}
		while (++currEdge.key < al.getInt()) {
			currListEdge = getStartingListEdge(currEdge.key);
			if (currListEdge != null) 
				return currListEdge;
		}
		return null;
	}
	
	/** @return the next Record, (returning the currently available Records) 	*/
	public Edge nextEdge() { //
		final SparseEdge ret = nextSparseEdge(); 
		if (ret == null)
			return null; 
		currEdge.val    = currListEdge.val; //new Integer(currEdge.Node);
		currEdge.typ    = currListEdge.typ; //
		currEdge.weight = currListEdge.weight; //
		return currEdge;
	} //
	
}
