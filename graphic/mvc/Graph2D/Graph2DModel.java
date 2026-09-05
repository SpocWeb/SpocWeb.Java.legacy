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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:51:08Z
 * digest: 08b6753a90adc6f8362b903da0d9e83f1d5631ce9e29203acb5d5a27b1a4a592
 * stale: false
 * tags: [code/model_state_management, code/interactive_editing]
 * concepts: [2D Graph Editing Model]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
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
	 * Returns the labels mapped to this graph's edge types.
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
	
	/** Creates an empty graph with no points or edges yet. */
	public Graph2DModel() {
		this(new SparseMatrix(), new VectorObject());
	}

	/** Creates an empty graph sized for the given expected number of points/edges.
	 * @param capacity
	 */
	public Graph2DModel(final int capacity) {
		this(new SparseMatrix(capacity));
	}

	/** Creates a graph from the given point labels, with a new empty edge set sized to match.
	 * @param _pointLabels
	 */
	public Graph2DModel(final VectorObject _pointLabels) {
		super(_pointLabels);
		this.edges = new SparseMatrix(_pointLabels.getInt());
		this.edgeLabels = new VectorObject(_pointLabels.getInt());
	}

	/** Creates a graph from the given edges and edge labels, sizing the point storage to
	 * match the edge set.
	 */
	public Graph2DModel(final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_edges.getInt());
		this.edges = _edges;
		this.edgeLabels = _edgeLabels;
	}

	/** Creates a graph from the given edges, with a new empty edge-label list sized to match. */
	public Graph2DModel(final SparseMatrix _edges) {
		this(_edges, new VectorObject(_edges.getInt()));
	}

	/** Creates a graph from the given points, with new empty edges and edge labels.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints) {
		this(_mapPoints, new SparseMatrix(), new VectorObject(_mapPoints.getInt()));
	}

	/** Creates a graph from the given points and point labels, with new empty edges and
	 * edge labels.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels) {
		this(_mapPoints, _pointLabels, new SparseMatrix(), new VectorObject(_mapPoints.getInt()));
	}

	/** Creates a graph from the given points and edges, with a new empty edge-label list.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final SparseMatrix _edges) {
		this(_mapPoints, _edges, new VectorObject(_mapPoints.getInt()));
	}

	/** Creates a graph from the given point labels and edges, with a new empty edge-label list.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorObject _pointLabels, final SparseMatrix _edges) {
		this(_pointLabels, _edges, new VectorObject(_edges.getInt()));
	}

	/** Creates a graph from the given points, point labels and edges, with a new empty
	 * edge-label list.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels, final SparseMatrix _edges) {
		this(_mapPoints, _pointLabels, _edges, new VectorObject(_mapPoints.getInt()));
	}

	/** Creates a graph from the given points, edges and edge labels.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorPoint2D _mapPoints, final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_mapPoints);
		this.edges = _edges;
		this.edgeLabels = _edgeLabels;
	}

	/** Creates a graph from the given point labels, edges and edge labels.
	 * @param mapPoints_
	 */
	public Graph2DModel(final VectorObject _pointLabels, final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_pointLabels);
		this.edges = _edges;
		this.edgeLabels = _edgeLabels;
	}

	/** Creates a graph from the given points, point labels, edges and edge labels.
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
