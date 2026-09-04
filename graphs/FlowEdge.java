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
	 * @param node
	 * @param weight_
	 * @param type_
	 */
	public FlowEdge(final int node, final float weight_, final int type_) {
		super(node, weight_, type_); }
	
	/**
	 * @param node
	 * @param weight_
	 */
	public FlowEdge(final int node, final float weight_) { super(node, weight_); }
	
	/**
	 * @param node
	 * @param next
	 */
	public FlowEdge(final int node, final SparseEdge next) { super(node, next); }
	
	/**
	 * @param node_
	 * @param next_
	 * @param weight_
	 */
	public FlowEdge(final int node_, final SparseEdge next_, final float weight_) {
		super(node_, next_, weight_); }
	
	/**
	 * @param node_
	 * @param next_
	 * @param weight_
	 * @param type_
	 */
	public FlowEdge(final int node_, final SparseEdge next_, final float weight_, final int type_) {
		super(node_, next_, weight_, type_); }
	
}
