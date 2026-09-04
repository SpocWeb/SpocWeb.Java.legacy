/*
 * Created on 15.06.2005
 *
 */
package graphs;

import java.io.IOException;
import java.io.StreamTokenizer;

import math.vector.VectorFloat;
import streamIO.IIStreamIn;
import streamIO.Log;
import streamIO.integer.random.RandomDiscrete;
import streamIO.integer.random.RandomQuick;
import streamIO.real.IStreamIn_Float;

/**
 * @author heuerm
 *
 */
public abstract class AGraph 
implements IGraph {
	
	/** Logger for this Class	 */
	private static final Log L = new Log(AGraph.class, 0);
	
	/** Default Value for a not specified Edge Weight	 */
	final static public float DEFAULT_WEIGHT = 1;
	//Float.POSITIVE_INFINITY; //Integer.MAX_VALUE-1;
	
	/** Default Type for unspecified new Edges */
	final static public int DEFAULT_TYPE = 0;
	
	/** Constant denoting the FALSE Value in ternary Logic	 */
	final static public byte FALSE = 0;
	
	/** Constant denoting the undefined UNDEF Value in ternary Logic and Comparison
	  * This is a fundamental State denoting Contradiction
	  * or just Meaninglessness of the Criterion or Question	 */
	final static public byte UNDEF = 1;
	
	/** Constant denoting the TRUE Value in ternary Logic	 */
	final static public byte TRUE = 2;
	
	///////////////////////////////////////////////////////////////////////////
	/// static Methods
	///////////////////////////////////////////////////////////////////////////
	
	/** Tests whether sub is a Subgraph of sup
	 * The Term is extended so that Graphs with the same Edges but smaller weights 
	 * are also assumed to be SubGraphs.  
	 * @param sup the supposed Super Graph 
	 * @param sub the supposed SubGraph 
	 * @param allowSuperShorter Flag to allow the Edges of the supposed SuperGraph to be shorter. 
	 * @return the Number of Nodes checked, negative if the last Check failed. 
	 */
	final static public int IS_SUB_Graph(final IGraph sup, final IGraph sub, boolean allowSuperShorter) {
		int ret = 0; 
		final IEdgeStreamIn subIter = sub.EdgeIterator(); 
		for (Edge subNode; (subNode = subIter.nextEdge()) != IIStreamIn.EOI;) {
			++ret; 
			//if (curr.key == curr.value) { continue; }
			final float supWeight = sup.getWeight(subNode.key, subNode.val, subNode.typ); 
			if (allowSuperShorter) {
				if (supWeight > subNode.weight)
					return -ret; 
			} else {
				if (supWeight != subNode.weight)
					return -ret; 
			}
		}
		return ret; 
	}
	
	/** 
	 * a faster Method is to count, mark or remove all found Elements. 
	 * @param g1
	 * @param g2
	 * @return true if both Graphs are identical.
	 */
	final static public boolean EQUALS(final IGraph g1, final IGraph g2) {
		//return IS_SUB_Graph(g1, g2) && IS_SUB_Graph(g2, g1); 
		return g1.getInt() == IS_SUB_Graph(g1, g2, false); 
	}
	
	/** 
	 * loads the Matrix from the given streamIO. 
	 * This is slightly less effective than deriving it from SparseMatrix or MatrixGraph, 
	 * because the constant current Key cannot be exploited.  
	 * @param iter_ the Iterator delivering the Edge Objects 
	 * @param _max the maximum allowed Value to accept this Edge, ignores large Distances
	 * @param _min the minimum allowed Value to accept this Edge, 
	 * used to prevent either large negative Distances (_min < 0) 
	 * or rather small Values (_min > 0)
	 * or negative Values (_min == 0)
	 */
	final static public void ADD_EDGES(final IGraph graph, final IEdgeStreamIn iter_, 
			final double _min, final double _max, final boolean transposed) {
		final boolean useAbsolute = (_min > 0); 
		for (Edge curr; (curr = iter_.nextEdge()) != IIStreamIn.EOI;) {
			//if (curr.key == curr.value) { continue; }
			final float val = (useAbsolute ? Math.abs(curr.val) : curr.val); 
			if ((val < _min) || (val > _max)) {
				L.n("addEdges(): Filtered out: Weight out of Range:").l(val);
				continue;
			}
			if (transposed) {
				graph.addEdge(curr.val, curr.key, false, curr.weight);
			} else {
				graph.addEdge(curr.key, curr.val, false, curr.weight);
			}
		}
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute, if _min > 0) Value falls between the Interval [_min, _max]. 
	 * 
	 * Useful to make a full Graph sparse by eliminating all infinite Edges. 
	 * 
	 * @param _weights the full Matrix of Weights for the Graph / Matrix  
	 * @param _symmetric when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param _max Maximum Value to accept in this Representation
	 * used to render Graphs sparse (ignore very large Distances). 
	 * @param _min Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the _weights are used. 
	 */
	final static public void ADD_EDGES(final IGraph graph, 
		final float[][] _weights,
		final boolean _symmetric,
		final double _min,
		final double _max) {
		final boolean useAbsolute = (_min > 0); 
		for (int i = _weights.length; --i >= 0; ) {
			final float[] weightsi = _weights[i];
			//use the lower Triangle
			for (int j = (_symmetric ? i : weightsi.length); --j >= 0; ) {
				final float val = (useAbsolute ? Math.abs(weightsi[j]) : weightsi[j]); 
				if ((val < _min) || (val > _max)) {
					L.n("addEdges(): Filtered out: Weight out of Range:").l(val);
					continue;
				}
				graph.addEdge(i, j, !_symmetric, weightsi[j]);
			}
		}
	}
	
	/** Adds all the Edges whose Value multiplied by Limit is larger than 1
	 * from the given full Matrix Representation to the Graph
	 * 
	 * Useful to make a full Matrix sparse by eliminating all small Values. 
	 * 
	 * @param _weights the full 2D Matrix of Weights for the Graph / Matrix  
	 * @param _symmetric when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated everywhere!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param _max Maximum Value to accept in this Representation
	 * used to render Graphs sparse (ignore very large Distances). 
	 * @param _min Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 */
	final static public void ADD_EDGES(final IGraph _graph, 
		final double[][] _weights,
		final boolean _symmetric,
		final double _min,
		final double _max) {
		final boolean useAbsolute = (_min > 0); 
		for (int i = _weights.length; --i >= 0; ) {
			final double[] weights_i = _weights[i];
			//use the lower Triangle
			for (int j = (_symmetric ? i : weights_i.length); --j >= 0; ) {
				final double val = (useAbsolute ? Math.abs(weights_i[j]) : weights_i[j]); 
				if ((val < _min) || (val > _max)) {
					L.n("addEdges(): Filtered out: Weight out of Range:").l(val);
					continue;
				}
				_graph.addEdge(i, j, !_symmetric, (float) weights_i[j]);
			}
		}
	}
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * If the Graph has more than 2 Items, it overrides the given Parameter!
	  */
	final static public void ADD_EDGES(final IGraph graph, final int[][] edges, final boolean _directed) {
		int Weight = 1;
		final boolean hasDirect = edges[0].length > 2;
		final boolean hasWeight = edges[0].length > 3;
		for (int i = edges.length; --i >= 0;) {
			final boolean directed = (hasDirect ? (edges[i][2] != 0) : _directed);
			if (hasWeight) {
				Weight = edges[i][3]; } 
			graph.addEdge(edges[i][0], edges[i][1], directed, Weight);
		}
	}
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * @param offset the Offset to be subtracted from the Points, 
	  * resulting in the Points starting a 0, usually 'A'=64
	  */
	final static public void ADD_EDGES(final IGraph graph, 
			final char[][] edges, final char offset, boolean directed) {
		ADD_EDGES(graph, edges, offset, directed, edges.length);
	}
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * @param offset the Offset to be subtracted from the Points, 
	  * resulting in the Points starting a 0, usually 'A'=64
	  */
	final static public void ADD_EDGES(final IGraph graph, 
			final char[][] edges, final char offset, boolean directed, 
			final int stop) {
		ADD_EDGES(graph, edges, offset, directed, 0, stop);
	}
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * @param offset the Offset to be subtracted from the Points, 
	  * resulting in the Points starting a 0, usually 'A'=64
	  */
	final static public void ADD_EDGES(final IGraph graph, 
			final char[][] edges, final char offset, boolean directed, 
			final int start, final int stop) {
		int weight = 1;
		boolean hasWeight = edges[0].length > 2;
		boolean hasDirect = edges[0].length > 3;
		for (int i = stop; --i >= start;) {
			if (hasWeight) {
				weight = edges[i][2]; } 
			if (hasDirect) {
				directed = (edges[i][3] != 0);} 
			graph.addEdge(
				edges[i][0] - offset,
				edges[i][1] - offset,
				directed,
				weight);
		}
	}
	
	/** Adds the Edges from the given File to the Graph
	  * Additional Parameters are the Default Directedness and the Default Weight
	  * If reverse is true, the Direction of the Graph is reversed!
	  */
	final static public void ADD_EDGES(final IGraph graph, 
		final StreamTokenizer st,
		boolean directed,
		float weight,
		final boolean reverse)
		throws IOException {
		st.eolIsSignificant(true); //one Edge per Row
		while (StreamTokenizer.TT_EOF != st.ttype) {
			while (StreamTokenizer.TT_EOL != st.ttype)
				st.nextToken(); //read until the End of the previous Line
			if (StreamTokenizer.TT_NUMBER != st.nextToken())
				continue;
			final int Source = (int) st.nval;
			//if the Line starts with Characters, skip it
			if (StreamTokenizer.TT_NUMBER != st.nextToken())
				continue;
			final int Target = (int) st.nval;
			//if the Line starts with Characters, skip it
			if (StreamTokenizer.TT_NUMBER == st.nextToken()) {
				directed = (0 != st.nval);
				//if the Line starts with Characters, skip it
				if (StreamTokenizer.TT_NUMBER != st.nextToken()) 
					weight = (float) st.nval;
					//if the Line starts with Characters, skip it
			}
			if (reverse) {
				graph.addEdge(Target, Source, directed, weight);
			} else {
				graph.addEdge(Source, Target, directed, weight);
			}
		}
	}
	
	/** can be used to fill any IGraph with random Edges. 
	 * 
	 * @param graph the Graph to fill 
	 * @param density the Density (i.e. average # of Connections of any Node, aka Erdoes Number)
	 */
	final static public void FILL_RANDOM_GRAPH (
			final IGraph graph, final float density, 
			final boolean directed) {
		graph.clear(); //could as well start with a new Graph. 
		final int len = graph.getInt();
		for(int i = (int) (len*density); --i >= 0;) { //randomly filling is typically less effective! 
			final int row = (int)(RANDOM.nextDouble()*len); 
			final int col = (int)(RANDOM.nextDouble()*len);
			//graph.setEdge(row, col, directed); //for Matrix
			graph.addEdge(row, col, directed); //for sparse Graphs, needs renormalization
		}
	}
	
	/**<pre>
	 * Generates a scale-free undirected Graph with the specified #of Nodes and Connections. 
	 * Scale-free Graphs have NO typical # of Connections per Node (Fan-In). 
	 * They typically have some "Hubs" with very many Connections. 
	 * The Number of Connections per Node n is random and distributed like 1/n^k 
	 * with k typically between 2 and 3 (Parameter exp)
	 * (for higher k typically a single Node will monopolize all Connections) 
	 * 
	 * For generating directed scale-free Graphs efficiently 
	 * the Fan-In could be stored in another Array at the Root of every Node. 
	 * 
	 * These Networks develop naturally due to...
	 * -the transient Nature of 'Growing' Processes 
	 * -the Preference of each individual Node to connect to the most prominent Nodes
	 * 
	 * Properties of Scale-free Graphs: 
	 * -robust against random Attacks (due to the Fact that Hubs are rare) 
	 * -fragile with targeted Attacks on the Hubs. 
	 * 
	 * Real-Life Examples are: 
	 * -Actors in Movies (k=3)
	 * -US electricity Network (k=4)
	 * -Scientific Citations (k=3)
	 * -Pareto Distribution of People in Cities  (k=2..3)
	 * -Routers in the World Wide Web 
	 * -Hyper-Links in the WWW and in Wikipedia 
	 * -social Networks between Humans 
	 * -Business Cooperations 
	 * -EMail Connections 
	 * -Quotations in Scientific Publications
	 * -Interactions of Molecule Types in Bacteria Metabolisms 
	 * 
	 * Graphs with e.g. geographical or spatial Constraints are typically NOT Scale-free: 
	 * -Train Connections 
	 * -Power Lines 
	 * 
	 * </pre>
	 * @param graph the Graph to populate
	 * @param numNodes the Number of Nodes to add
	 * @param numEdges the Number of Edges to add
	 * @param exp Exponent to increase Concentration: 
	 * between 0 (purely random) and 3 (strong Concentration)
	 * above 2 is interesting, above three results in an isolated Centre. 
	 * @param randomizeNumEdges Flag to use numEdges only as maximum #Edges
	 */
	final static public void FILL_SCALE_FREE_GRAPH(final IGraph graph, final int numNodes, 
			final double numEdges, final double exp, final boolean randomizeNumEdges) {
		FILL_SCALE_FREE_GRAPH(graph, numNodes, numEdges, exp, false, randomizeNumEdges, false, false); 
	}

	/** The Generator to use within this Class	 */
	public static IStreamIn_Float RANDOM = RandomQuick.RANDOM;  
	
	/**<pre>
	 * Generates a scale-free Graph with the specified #of Nodes and average #of Connections. 
	 * Scale-free Graphs have no typical # of Connections per Node (Fan-In). 
	 * They typically have some "Hubs" with very many Connections. 
	 * The Number of Connections per Node n is random and distributed like 1/n^k 
	 * with k typically between 2 and 3 (Parameter exp)
	 * (for higher k typically a single Node will monopolize all Connections) 
	 * 
	 * For generating directed scale-free Graphs efficiently 
	 * the Fan-In could be stored in another Array at the Root of every Node. 
	 * 
	 * These Networks develop naturally due to...
	 * -the transient Nature of Growing Processes 
	 * -the Preference of each individual Node to connect to the most prominent Nodes
	 * 
	 * Properties of Scale-free Graphs: 
	 * -robust against random Attacks (due to the Fact that Hubs are rare) 
	 * -fragile with targeted Attacks on the Hubs. 
	 * 
	 * Real-Life Examples are: 
	 * -Routers in the World Wide Web 
	 * -Hyper-Links in the WWW 
	 * -social Networks between Humans 
	 * -Business Cooperations 
	 * -EMail Connections 
	 * -Quotations in Scientific Publications
	 * -Interactions of Molecule Types in Bacteria Metabolisms 
	 * 
	 * Graphs with e.g. geographical or spatial Constraints are typically not Scale-free: 
	 * -Train Connections 
	 * -Power Lines 
	 * 
	 * </pre>
	 * @param graph the Graph to populate
	 * @param numNodes the Number of Nodes to add
	 * @param numEdges the Number of Edges to add
	 * @param exp Exponent between 0 (purely random) and 3 (strong Concentration) 
	 * above 2 is interesting, above 3 results in a single Centre. 
	 * @param randomizeNumEdges Flag to use numEdges only as maximum #Edges
	 */
	final static public void FILL_SCALE_FREE_GRAPH(final IGraph graph, 
			final int numNodes, final double numEdges, final double exp, 
			final boolean directed,  
			final boolean randomizeNumEdges, final boolean clear, 
			final boolean useRandomWeights) { 
		if (clear) 
			graph.clear(); 
		for (int row = numNodes; --row >= 0;) {
			graph.setEdge(row, row, true); } //fill the Diagonal
		for (int row = numNodes; --row >= 0;) {
			//optimization: reuse the same Probabilities for one Sweep! 
			final float[] probs = graph.getFanOut(); //VectorFloat.COPY(graph.getOutDegree()); //assumes undirected Graph, see below, otherwise use getFanIn()...
			VectorFloat.POW_AT(probs, exp);
			final RandomDiscrete ran = new RandomDiscrete(RANDOM, probs); 
			double edge = numEdges;
			if (randomizeNumEdges) 
				edge *= RANDOM.nextDouble(); 
			for (; --edge >= 0;) { 
				//...since with symmetric / undirected Graphs Fan-In is Fan-Out
				final int col = ran.nextInt(); 
				if (useRandomWeights)
					graph.setEdge(row, col, directed, (float)RANDOM.nextDouble(), true); //undirected Graph, see above
				else 
					graph.setEdge(row, col, directed, true); //undirected Graph, see above
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// Member Implementations based on the Interface
	///////////////////////////////////////////////////////////////////////////
	
	/** Adds all the Edges to the Graph
	  * @param edges Array with Rows {Origin, Target, Weight} 
	  * @param directed when true, only the lower Triangle is used.
	  */
	public void addEdges(final char[][] edges, final boolean directed) {
		for (int i = edges.length; --i >= 0;) {
			char[] edge = edges[i];	
			addEdge(edge[0] - 'A', edge[1] - 'A', directed, (float) edge[2]);
		}
	}
	
	/** 
	 * loads the Matrix from the given streamIO. 
	 * This is slightly less effective than deriving it from SparseMatrix or MatrixGraph, 
	 * because the constant current Key cannot be exploited.  
	 * @param iter_ the Iterator delivering the Edge Objects 
	 * @param _max the maximum allowed Value to accept this Edge, ignores large Distances
	 * @param _min the minimum allowed Value to accept this Edge, 
	 * used to prevent either large negative Distances (_min < 0) 
	 * or rather small Values (_min > 0)
	 * or negative Values (_min == 0)
	 */
	//public void addEdges(final StreamIn iter_, final double _min, final double _max, boolean transposed) {
	//	addEdges((IEdgeStreamIn) iter_, _min, _max, transposed);	}
	
	/** 
	 * loads the Matrix from the given streamIO. 
	 * This is slightly less effective than deriving it from SparseMatrix or MatrixGraph, 
	 * because the constant current Key cannot be exploited.  
	 * @param iter_ the Iterator delivering the Edge Objects 
	 * @param _max the maximum allowed Value to accept this Edge, ignores large Distances
	 * @param _min the minimum allowed Value to accept this Edge, 
	 * used to prevent either large negative Distances (_min < 0) 
	 * or rather small Values (_min > 0)
	 * or negative Values (_min == 0)
	 */
	public void addEdges(final IEdgeStreamIn iter_, final double _min, final double _max, final boolean transposed) {
		ADD_EDGES(this, iter_, _min, _max, transposed); 
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute if _min > 0) Value falls between the Interval [_min, _max]. 
	 * 
	 * @param _weights the full Matrix of Weights for the Graph / Matrix  
	 * @param _symmetric when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param _min Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the _weights are used. 
	 */
	public void addEdges(
		final float[][] _weights,
		final boolean _symmetric,
		final double _min) {
		addEdges(_weights, _symmetric, _min, Float.POSITIVE_INFINITY); 
	}
	
	/** Adds all the Edges to the Graph
	 * whose (absolute if _min > 0) Value falls between the Interval [_min, _max]. 
	 * 
	 * @param _weights the full Matrix of Weights for the Graph / Matrix  
	 * @param _symmetric when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param _max Maximum Value to accept in this Representation
	 * used to render Graphs sparse (ignore very large Distances). 
	 * @param _min Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 * If positive, absolute Values of the _weights are used. 
	 */
	public void addEdges(
		final float[][] _weights,
		final boolean _symmetric,
		final double _min,
		final double _max) {
		ADD_EDGES(this, _weights, _symmetric, _min, _max);
	}
	
	/** Adds all the Edges to the Graph
	 * whose Value multiplied by Limit is larger than 1
	 * 
	 * @param _weights the full Matrix of Weights for the Graph / Matrix  
	 * @param _symmetric when true, only the lower Triangle L is used 
	 * and the Matrix is assumed to be symmetric. 
	 * (The lower Triangle is always preferable, because it can be truncated!)
	 * The same can be achieved by truncating the Matrix. 
	 * @param _max Maximum Value to accept in this Representation
	 * used to render Graphs sparse (ignore very large Distances). 
	 * @param _min Minimum Value to accept in this Representation, 
	 * used to render Matrices sparse (ignore very small Values). 
	 */
	public void addEdges(
		final double[][] _weights,
		final boolean _symmetric,
		final double _min,
		final double _max) {
		ADD_EDGES(this, _weights, _symmetric, _min, _max); 
	}
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * If the Graph has more than 2 Items, it overrides the given Parameter!
	  */
	public void addEdges(final int[][] edges, final boolean _directed) {
		ADD_EDGES(this, edges, _directed); 
	}
	
	/** Adds all the Edges to the Graph
	  * The Sequence of Elements is assumed to be:
	  * Source, Target, [directed], [Weight]
	  * @param offset the Offset to be subtracted from the Points, 
	  * resulting in the Points starting a 0, usually 'A'=64
	  */
	public void addEdges(final char[][] Edges, final char offset, final boolean directed) {
		ADD_EDGES(this, Edges, offset, directed); 
	}
	
	/** Adds the Edges from the given File to the Graph
	  * Additional Parameters are the Default Directedness and the Default Weight
	  * If reverse is true, the Direction of the Graph is reversed!
	  */
	public void addEdges(
		final StreamTokenizer st,
		boolean directed,
		float weight,
		final boolean reverse)
		throws IOException {
		ADD_EDGES(this, st, directed, weight, reverse); 
	}
	
	/** Tests whether sub is a Subgraph of sup
	 * The Term is extended so that Graphs with the same Edges but smaller weights 
	 * are also assumed to be SubGraphs.  
	 * @param sup the supposed Super Graph 
	 * @param sub the supposed SubGraph 
	 * @return true if sub is a SubGraph of sup. 
	 */
	public boolean isSubGraph(final IGraph sup) {
		return AGraph.IS_SUB_Graph(this, sup, true) >= 0; 
	}
	
	/** Tests whether sub is a Subgraph of sup
	 * The Term is extended so that Graphs with the same Edges but smaller weights 
	 * are also assumed to be SubGraphs.  
	 * @param sup the supposed Super Graph 
	 * @param sub the supposed SubGraph 
	 * @return true if sub is a SubGraph of sup. 
	 */
	public boolean equals(final IGraph sup) {
		return AGraph.EQUALS(this, sup); 
	}
	
}
