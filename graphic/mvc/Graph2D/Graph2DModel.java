/*
 * File Name: Graph2DModel.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc.Graph2D;

import graphic.VectorPoint2D;
import graphic.mvc.Point2D.Point2DModel;
import graphs.SparseMatrix;
import math.vector.VectorObject;

/**
 * Title: Graph2DModel<p>
 * Description:
 * Stores a Graph as a Set of Points (incl. Coordinates) plus a Set of Edges. 
 * 
 * Not usable for Planes, because these contain Sequence Information: 
 * about the Edges that form a closed Polygon. 
 * @see graphic.mvc.plane2D for this. 
 * 
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class Graph2DModel 
extends Point2DModel {
	
	/** Uses a SparseMatrix to store the Edges, delegates Storage to it */
	final public SparseMatrix edges; // = new SparseMatrix(10);
	
	/** Uses an SparseMatrix to stores the Edges, delegates to it */
/*	public void setEdges(SparseMatrix edges_) {
		this.edges = edges_;
		mapPoints.setCapacity(edges.getInt());
	}
*/	
	/** Uses an SparseMatrix to stores the Edges, delegates to it */
	public SparseMatrix getEdges() {
		return edges;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	/** List of Edge Labels to hold, mapped to the Edge Types. 
	 * dynamic Vector to be able to add EdgeLabel Types.  	 */
	final public VectorObject edgeLabels; 
	
	/**
	 * @return the List of Labels for the (int) Edge Types
	 */
	public VectorObject getEdgeLabels() {
		return edgeLabels;
	}
	
	/**
	 * @param string_ the List of Labels for the Edge Types
	 */
/*	public void setEdgeLabels(final VectorObject labels_) {
		edgeLabels = labels_;
	}
*/	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructor
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	public Graph2DModel() {
		this(new SparseMatrix(), new VectorObject());
	}
	
	/**
	 * @param capacity
	 */
	public Graph2DModel(final int capacity) {
		this(new SparseMatrix(capacity));
	}
	
	/**
	 * @param _pointLabels
	 */
	public Graph2DModel(final VectorObject _pointLabels) {
		super(_pointLabels);
		this.edges = new SparseMatrix(_pointLabels.getInt()); 
		this.edgeLabels = new VectorObject(_pointLabels.getInt());
	}
	
	/**
	 * 
	 */
	public Graph2DModel(final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_edges.getInt());
		this.edges = _edges; 
		this.edgeLabels = _edgeLabels;
	}
	
	/**
	 * 
	 */
	public Graph2DModel(final SparseMatrix _edges) {
		this(_edges, new VectorObject(_edges.getInt()));
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints) {
		this(_mapPoints, new SparseMatrix(), new VectorObject(_mapPoints.getInt()));
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels) {
		this(_mapPoints, _pointLabels, new SparseMatrix(), new VectorObject(_mapPoints.getInt()));
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final SparseMatrix _edges) {
		this(_mapPoints, _edges, new VectorObject(_mapPoints.getInt()));
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorObject _pointLabels, final SparseMatrix _edges) {
		this(_pointLabels, _edges, new VectorObject(_edges.getInt()));
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels, final SparseMatrix _edges) {
		this(_mapPoints, _pointLabels, _edges, new VectorObject(_mapPoints.getInt()));
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_mapPoints);
		this.edges = _edges; 
		this.edgeLabels = _edgeLabels;
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorObject _pointLabels, final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_pointLabels);
		this.edges = _edges; 
		this.edgeLabels = _edgeLabels;
	}
	
	/**
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels, final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_mapPoints, _pointLabels);
		this.edges = _edges; 
		this.edgeLabels = _edgeLabels;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	
	 */
	public static void main(final String[] args) throws Exception {
		Graph2DPainter.main(args);
	}

}
