/*
 * File Name: Point2DModel.java
 * Created on: 06.12.2003
 *
 */
package graphic.mvc.Point2D;

import graphic.Point2D;
import graphic.VectorPoint2D;
import graphic.mvc.AModel;
import math.vector.VectorObject;
import streamIO.Log;

/**
 * Title: Point2DModel<p>
 * Description:
 * Model used by the Point2DPainter to read Data from.
 * Modified by the Point2DKeyController Instance. 
 * 
 * Design Decisions: 
 * Model/Document is separated from View 
 * to allow for several Views of the same Model/Document. 
 * To allow for different Controllers (Mouse, Keyboard) also separate those from the View, 
 * but this requires additional State that cannot be stored in the Model. 
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
public class Point2DModel 
extends AModel {
	
	/** streamIO for Logging */
	private static final Log L = new Log(Point2DModel.class, 1);
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param labels List of Labels
	 * @param i the Index of the Point
	 * @return the Label of the given Point or a Number when no Label is given
	 */
	final static public String GET_LABEL(final VectorObject labels, final int i) {
		Object label = labels.getAt(i);
		if (label != null) 
			return label.toString();
		return "#" + String.valueOf(i);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Cache for the mapped Points with integer Coordinates */
	final public VectorPoint2D mapPoints; // = new VectorPoint2D();
	
	/** List of Model Objects to hold 
	 * Can hold individual Points together with their Formatting 
	 * or whole Matrices representing Sets of Points 
	 * or Graphs consisting of Points and Edge Lists resp. Adjacency Matrices. 
	 */
	protected final VectorObject pointLabels; // = new ArrayList();
	
	/**
	 * @param labels List of Labels
	 * @param i the Index of the Point
	 * @return the Label of the given Point or a Number when no Label is given
	 */
	public String getLabel(final int i) {
		return GET_LABEL(pointLabels, i);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors 
	/////////////////////////////////////////////////////////////////////////////////////

	/** empty Constructor */	
	public Point2DModel() {
		mapPoints   = new VectorPoint2D();
		pointLabels = new VectorObject ();
	}
	
	/** empty Constructor */	
	public Point2DModel(final int capacity) {
		mapPoints   = new VectorPoint2D(capacity);
		pointLabels = new VectorObject (capacity);
	}
	
	/** Constructor 
	 * @param mapPoints_ Point Coordinates to use
	 */	
	public Point2DModel(final VectorPoint2D _mapPoints) {
		this.mapPoints   = _mapPoints; 
		this.pointLabels = new VectorObject(_mapPoints != null ? _mapPoints.getInt() : 10);
	}
	
	/** Constructor 
	 * @param mapPoints_ Point Coordinates to use
	 */	
	public Point2DModel(final VectorPoint2D _mapPoints, final VectorObject _pointLabels) {
		this.mapPoints = _mapPoints; 
		this.pointLabels = _pointLabels; 
	}
	
	/** Constructor 
	 * @param mapPoints_ Point Coordinates to use
	 */	
	public Point2DModel(final VectorObject _pointLabels) {
		this.mapPoints = new VectorPoint2D(_pointLabels.getInt()); 
		this.pointLabels = _pointLabels; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Model Methods for adding a Point 
	/////////////////////////////////////////////////////////////////////////////////////

	/** Moves either the FocusPoint or the Canvas (all Points)
	 * 
	 * @param dx absolute Distance to move by 
	 * @param dy absolute Distance to move by 
	 * @param dz is being ignored, used by Sublasses
	 */
	protected void moveBy(final int dx, final int dy, final int dz, final int focusPointIndex) {
		L.n("moveBy(").l(dx).l(",").l(dy).l(")");
		if (focusPointIndex >= 0) {
			mapPoints.getPointAt(focusPointIndex).addAt(dx, dy);
			//loses the Information about the original Location.
		} else { //movingCanvas
			mapPoints.addAt(new Point2D(dx, dy));
		}
		repaint(); //TODO: this is incorrect, only a single View needs to be updated!
	}

	/** Remove or add Points at the double Clicked Position	*/
	public void removePoint(int index) {
		mapPoints.setAt(index, (Point2D) null);
	}
	
	/**
	 * Adds the given Point to the Point List at the specified Position
	 * @param position the Index to add the Point at
	 * @param point the Point Object to add
	 * @param label the Label to use for this Point
	 */
	public void addPoint(
		final int position,
		final Point2D point,
		final Object label) {
		mapPoints  .setAt(position, point);
		pointLabels.setAt(position, label);
	}

	/**
	 * Adds the given Point to the Point List at the specified Position
	 * @param position the Index to add the Point at
	 * @param point the Point Object to add
	 * @param label the Label to use for this Point
	 */
	public void addPoint(
		final int position,
		final int[] point,
		final Object label) {
		mapPoints  .setAt(position, point);
		pointLabels.setAt(position, label);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 */
	public void addPoint(final Point2D point) {
		addPoint(point, null);
	}

	/**
	 * Adds the given Point to the Point List at the specified Position
	 * @param position
	 * @param point
	 * @param label
	 */
	public void addPoint(
		final int position,
		final int x,
		final int y,
		final Object label) {
		mapPoints  .setAt(position, x, y);
		pointLabels.setAt(position, label);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 */
	public void addPoint(final int x, int y) {
		addPoint(x, y, null);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 * @param label
	 */
	public void addPoint(final int x, final int y, final Object label) {
		mapPoints  .addItem(x, y);
		pointLabels.addItem(label);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 * @param label
	 */
	public void addPoint(final Point2D point, final Object label) {
		mapPoints  .addItem(point);
		pointLabels.addItem(label);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 * @param label
	 */
	public void addPoint(final int[] point, final Object label) {
		mapPoints  .addItem(point);
		pointLabels.addItem(label);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param point
	 */
	public void addPoint(final int[] point) {
		addPoint(point, null);
	}

	/** 
	 * Adds the given Point to the Point List
	 * @param position
	 * @param point
	 */
	public void addPoint(final int position, final int[] point) {
		addPoint(position, point, null);
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
		Point2DPainter.main(args);
	}

}
