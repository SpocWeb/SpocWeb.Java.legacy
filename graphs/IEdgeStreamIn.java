/*
 * File Name: EdgeStream.java
 * Created on: 30.05.2003
 *
 */
package graphs;

import streamIO.object.IStreamIn;

/**
 * Defines the Interface for a Class that delivers a streamIO of {@link Edge} objects,
 * e.g. one Row of an adjacency Matrix or List at a time.
 * @see graphs.MatrixGraph.AdjMatrixEdgeStream
 * @see graphs.SparseEdgeStream
 * @author mheuer
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:42:22Z
 * digest: 3996df9a3ff9ba62c75d0704221cc07bd47274ba646a22db6469a4d29626f903
 * stale: false
 * tags: [code/graph_edge, code/graph_iteration]
 * concepts: [Edge Stream Interface]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public interface IEdgeStreamIn 
extends IStreamIn {
	
	/**
	 * Returns the current Number of Nodes in this Graph.
	 * @return the current Number of Nodes in this Graph
	 */
	public int getNumNodes();
	
	/**
	 * null indicates the End of the List. 
	 * @return the current Edge from the List
	 */
	public Edge currEdge(); 
	
	/**
	 * null indicates the End of the List. 
	 * @return the next Edge from the List
	 */
	public Edge nextEdge(); 
	
	/**
	 * Returns the Node this stream is currently filtered to.
	 * @return the current Filter Node
	 */
	public int getEdgeFilter();
	
	/** 
	 * sets the Filter so the Iterator returns only Items from this Node
	 * @param Value
	 */
	public void setEdgeFilter(int Value);
	
	/**
	 * Generates an iterative force-directed layout for len randomly placed Nodes in nDim dimensions.
	 * @param iter streamIO of to loop over until Convergence is achieved, calling refineGraph()
	 * @return a Proposal for the nDim Coordinates of the Nodes
	 *  this can directly be used to create a Wire3D Object in 2D
	 *  which can then be mapped onto a Wire2D using an ICoordMapper Trafo and painted.
	 */
	public float[][] generateGraphics(final int len, final int nDim);
	
	/**
	 * returns a Proposal for the Coordinates of the Nodes
	 * @param ret the Return Value; the given Coordinates are used as initial Guess.
	 * @return the given Parameter ret filled with the proposals for the Coordinates. 
	 */
	public float[][] generateGraphics(final float[][] ret); 
	
}
