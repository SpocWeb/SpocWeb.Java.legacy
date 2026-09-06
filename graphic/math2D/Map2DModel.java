/*
 * File Name: Map2DModel.java
 * Created on: 06.12.2003
 *
 */
package graphic.math2D;

import graphic.Point2D;
import graphic.VectorPoint2D;
import graphic.mvc.Graph2D.Graph2DModel;
import graphs.SparseMatrix;

import java.awt.Rectangle;

import math.matrix.MatrixFloat;
import math.vector.VectorObject;

/**
 * Holds the original 2D point/edge data alongside its mapped {@link Point2D} positions and
 * the {@link Coordinates2D} transform used to derive them, allowing points to be added and
 * moved interactively via the GUI.
 *
 * <p>Title: Map2DModel<p>
 * Description:
 * Model holding both the original float[][] Data and the mapped Point2Ds.
 * Also the Coordinate Trafo is stored here,
 * to allow for adding new Points and moving the Points via the GUI.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link Coordinates2D} | Maps raw points into display coordinates; stored as {@link #coordTrafo}. |
 * | {@link MatrixFloat} | Backing store for the raw point data ({@link #points}). |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see Coordinates2D the coordinate transform this model stores
 * @see MatrixFloat the backing store for raw point data
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:46:15Z
 * digest: 7b8c895665b46e590f6ca1d88d70c61752b7a86cf4a3c2d5006cdb7ff59b553b
 * stale: false
 * tags: [code/view_model]
 * concepts: [2D Graph Model]
 * facets: {layer: domain, status: broken, complexity: medium}
 * -->
 */
public class Map2DModel
extends Graph2DModel {
	
	/** The Coordinate System used */
	protected Coordinates2D coordTrafo;
	// = new Coordinates2D(-3.15f, +3.15f, -1.6f, +1.6f, g.getClipBounds());
	
	/** List of Model Objects to hold 
	 * Can hold individual Points together with their Formatting 
	 * or whole Matrices representing Sets of Points 
	 * or Graphs consisting of Points and Edge Lists resp. Adjacency Matrices. 
	 */
	final public MatrixFloat points = new MatrixFloat();
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates an empty map model with no points, edges or labels.
	 */
	public Map2DModel() { super(); }

	/**
	 * Creates a map model initialized with the given edge matrix.
	 * @param edges_
	 */
	public Map2DModel(final SparseMatrix edges_) { super(edges_); }

	/**
	 * Creates a map model initialized with the given edges and their labels.
	 * @param _edges
	 * @param _edgeLabels
	 */
	public Map2DModel(final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_edges, _edgeLabels);
	}

	/**
	 * Creates a map model initialized with the given mapped points.
	 * @param mapPoints_
	 */
	public Map2DModel(final VectorPoint2D mapPoints_) { super(mapPoints_); }

	/**
	 * Creates a map model initialized with the given mapped points and edges.
	 * @param mapPoints_
	 * @param edges_
	 */
	public Map2DModel(final VectorPoint2D mapPoints_, final SparseMatrix edges_) {
		super(mapPoints_, edges_);
	}

	/**
	 * Creates a map model initialized with the given mapped points, edges and edge labels.
	 * @param _mapPoints
	 * @param _edges
	 * @param _labels
	 */
	public Map2DModel(final VectorPoint2D _mapPoints, final SparseMatrix _edges,
			final VectorObject _edgeLabels) {
		super(_mapPoints, _edges, _edgeLabels);
	}

	/**
	 * Creates a map model initialized with the given mapped points and their labels.
	 * @param _mapPoints
	 * @param _pointLabels
	 */
	public Map2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels) {
		super(_mapPoints, _pointLabels);
	}

	/**
	 * Creates a map model initialized with the given mapped points, point labels and edges.
	 * @param _mapPoints
	 * @param _pointLabels
	 * @param _edges
	 */
	public Map2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels,
			final SparseMatrix _edges) {
		super(_mapPoints, _pointLabels, _edges);
	}

	/**
	 * Creates a map model initialized with the given mapped points, point labels, edges
	 * and edge labels.
	 * @param _mapPoints
	 * @param _pointLabels
	 * @param _edges
	 * @param _edgeLabels
	 */
	public Map2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels,
			final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_mapPoints, _pointLabels, _edges, _edgeLabels);
	}

	/**
	 * Creates a map model with initial capacity for the given number of points.
	 * @param capacity
	 */
	public Map2DModel(final int capacity) { super(capacity); }

	/**
	 * Creates a map model initialized with the given point labels.
	 * @param _pointLabels
	 */
	public Map2DModel(final VectorObject _pointLabels) { super(_pointLabels); }

	/**
	 * Creates a map model initialized with the given point labels and edges.
	 * @param _pointLabels
	 * @param _edges
	 */
	public Map2DModel(final VectorObject _pointLabels, final SparseMatrix _edges) {
		super(_pointLabels, _edges);
	}

	/**
	 * Creates a map model initialized with the given point labels, edges and edge labels.
	 * @param _pointLabels
	 * @param _edges
	 * @param _edgeLabels
	 */
	public Map2DModel(final VectorObject _pointLabels,
			final SparseMatrix _edges, final VectorObject _edgeLabels) {
		super(_pointLabels, _edges, _edgeLabels);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Adds the given labeled Point to the Point List at the specified Position
	 * @param position
	 * @param point
	 * @param label
	 */
	public void addPoint(
		final int position,
		final float[] point,
		final String label_) {
		points.setAt(position, point);
		super.addPoint(new Point2D(), label_);
	}
	
	/**
	 * Adds the given Point to the Point List at the specified Position
	 * @param position
	 * @param point
	 * @param label
	 */
	public void addPoint(
		final int position,
		final float x,
		final float y,
		final String label_) {
		points.setAt(position, new float[] { x, y });
		final Point2D point = (coordTrafo != null) ? coordTrafo.mapPt(x, y) : new Point2D();
		super.addPoint(position, point, label_);
	}
	
	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 * @param _label optional (null allowed) Label for this Point. 
	 */
	public void addPoint(final float x, final float y, final String _label) {
		points.addItem(new float[] { x, y });
		final Point2D point = (coordTrafo != null) ? coordTrafo.mapPt(x, y) : new Point2D();
		super.addPoint(point, _label);
	}
	
	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 */
	public void addPoint(final double x, final double y) {
		addPoint(x, y, null); 
	}
	
	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 * @param _label optional (null allowed) Label for this Point. 
	 */
	public void addPoint(final double x, final double y, final String _label) {
		points.addItem(new float[] {(float) x, (float) y });
		final Point2D point = (coordTrafo != null) ? coordTrafo.mapPt(x, y) : new Point2D(); 
		super.addPoint(point, _label);
	}
	
	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 * @param _label optional (null allowed) Label for this Point. 
	 */
	public void addPoint(final float[] point, final String _label) {
		points.addItem(point);
		final Point2D mapped = (coordTrafo != null) ? coordTrafo.mapPt(point) : new Point2D();
		super.addPoint(mapped, _label);
	}
	
	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 */
	public void addPoint(final float[] point) {
		addPoint(point, null);
	}
	
	/** 
	 * Adds the given Point to the Point List
	 * @param position
	 * @param point
	 */
	public void addPoint(final int position, final float[] point) {
		addPoint(position, point, null);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Graphics Model Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** Whether the X and Y scale are kept equal by squaring the target bounds in {@link #getTrafo(Rectangle)}. */
	final public boolean equiScale = true;
	
	/** setter for the Coordinate Transformation
	 * 
	 * @param trafo a Coordinate Transformation 
	 */
	public void setTrafo(final Coordinates2D trafo) { this.coordTrafo = trafo; }
	
	/** Factory Method for late Initialization
	 * 
	 * @param g Graphics Context, clips only to the Repaint Area!
	 * @return a Coordinate Transformation for g
	 */
	public Coordinates2D getTrafo(final Rectangle lastBounds) {
		if (coordTrafo != null) {
			return coordTrafo;
		}
		if (points.getInt() == 0) {
			return null;
		}
		float[] min = points.Min();
		float[] max = points.Max();
		if (equiScale) {
			lastBounds.height =
				lastBounds.width = (lastBounds.height + lastBounds.width) >> 1;
		}
		this.coordTrafo =
			new Coordinates2D(min[0], max[0], min[1], max[1], lastBounds); //fits the Graph exactly into the Frame
		return coordTrafo;
	}
	
	/**
	 * Calculates the 2D integer Positions for all Points of the Graph 
	 * dynamically determines the Trafo to fit all Points into the given Bounds. 
	 * @param g
	 */
	protected void mapPoints(final Rectangle lastBounds) {
		this.lastBounds = lastBounds;
		getTrafo(lastBounds);
		mapPoints.setSize(points.getInt());
		calcPoints();
	}
	
	Rectangle lastBounds;
	
	/**
	 * Calculates the 2D integer Positions for all Points of the Graph 
	 * @param g
	 */
	protected void calcPoints() {
		coordTrafo.mapPt(mapPoints.getList(), points.getList(), points.getInt());
	}
	
	/** 
	 * move the Points or a single Point by the given Steps (in mapped Coordinates)
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param focusPointIndex
	 */
	protected void moveBy(final int dx, final int dy, final int dz, final int focusPointIndex) {
		if (coordTrafo == null) return;
		//super.moveBy(dx, dy, dz); //moves the mapped Points, but doesn't change the Coordinates
		boolean changed = false;
		if (dz != 0) {
			double factor = dz * 0.1;
			coordTrafo.scaleAt(1 - factor);
			coordTrafo.moveAt(//keep the Center where it is. 
			lastBounds.width / 2 / coordTrafo.getScaleX() * factor,
			lastBounds.height / 2 / coordTrafo.getScaleY() * factor);
			changed = true;
		}
		if ((dx != 0) || (dy != 0)) {
			if (focusPointIndex >= 0) {
				final float[] point = points.getVectorAt(focusPointIndex);
				point[0] += dx * coordTrafo.MapX.getStep();
				point[1] += dy * coordTrafo.MapY.getStep();
			} else { //move the whole Canvas
				coordTrafo.moveAt(dx * coordTrafo.MapX.getStep(), dy * coordTrafo.MapY.getStep());
			}
			changed = true;
		} 
		if (changed) {
			calcPoints(); //
			repaint(); //TODO: this is incorrect, only a single View needs to be updated!
		}
	}
	
}
