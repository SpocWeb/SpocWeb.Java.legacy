/*
 * File Name: EdgeStream.java
 * Created on: 30.05.2003
 *
 */
package graphs;

import streamIO.object.IStreamIn;

/**
 * Title: IEdgeStreamIn<p>
 * Description:
 * Purpose:
 *
 * Defines the Interface for a Class 
 * that delivers a streamIO of Edge Objects. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubInterfaces: <none>
 *
 * Known Uses: 
 * @see graphs.AdjListEdgeStream
 * @see graphs.AdjMatrixEdgeStream
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IEdgeStreamIn 
extends IStreamIn {
	
	/**
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
	 * @return the current Filter Node
	 */
	public int getEdgeFilter(); 
	
	/** 
	 * sets the Filter so the Iterator returns only Items from this Node
	 * @param Value
	 */
	public void setEdgeFilter(int Value);
	
	/**
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
