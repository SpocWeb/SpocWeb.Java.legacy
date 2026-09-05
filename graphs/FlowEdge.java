/*
 * Created on 11.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package graphs;

/**
 * extends the ListEdge with a Value for the Flow Throughput 
 * and a Reference to the Transposed Edge to set the reverse Flow 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 7d7f03b6823cd42f940978c7950d0c6a5e3f438c0ac09ea5c095b7843dc090cb
 * stale: false
 * tags: [code/graph_edge]
 * concepts: [Flow-Network Edge]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class FlowEdge 
extends SparseEdge {
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Flow through this Connection when determining Flow Networks	 */
	public float flow;
	
	/** Link to the transposed Edge for Flow Calculations, 
	 * actually this is not much of an Optimization, 
	 * since you still have to search for one of both Edges	 */
	public FlowEdge trp;
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a FlowEdge to the given node with the given weight and type, and no successor Edge.
	 * @param node
	 * @param weight_
	 * @param type_
	 */
	public FlowEdge(final int node, final float weight_, final int type_) {
		super(node, weight_, type_); }

	/**
	 * Creates a FlowEdge to the given node with the given weight, default type, and no successor Edge.
	 * @param node
	 * @param weight_
	 */
	public FlowEdge(final int node, final float weight_) { super(node, weight_); }

	/**
	 * Creates a FlowEdge to the given node, chained before the given successor Edge.
	 * @param node
	 * @param next
	 */
	public FlowEdge(final int node, final SparseEdge next) { super(node, next); }

	/**
	 * Creates a FlowEdge to the given node with the given weight, chained before the given successor Edge.
	 * @param node_
	 * @param next_
	 * @param weight_
	 */
	public FlowEdge(final int node_, final SparseEdge next_, final float weight_) {
		super(node_, next_, weight_); }

	/**
	 * Creates a FlowEdge to the given node with the given weight and type, chained before the given successor Edge.
	 * @param node_
	 * @param next_
	 * @param weight_
	 * @param type_
	 */
	public FlowEdge(final int node_, final SparseEdge next_, final float weight_, final int type_) {
		super(node_, next_, weight_, type_); }
	
}
