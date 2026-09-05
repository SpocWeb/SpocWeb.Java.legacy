/*
 * File Name: Frame2D.java
 * Created on: 30.05.2003
 *
 */
package graphic.mvc.Point2D;

import graphic.IGraphText;
import graphic.Point2D;
import graphic.VectorPoint2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.ICanvas;
import graphic.mvc.IController;
import graphic.mvc.IFocusPainter;
import graphic.mvc.IPainter;

import java.awt.Color;

import streamIO.Log;

/**
 * Title: Point2DPainter
 * <p>
 * Purpose: Painter Object, receives or generates a Viewer Window to output the
 * Result of it's Instructions.
 * 
 * This Class can display and stretch a given Set of Points. Holds a Collection
 * of mathematical Objects (Points, Graphs etc.) as the Model and recalculates
 * the actual Representation. Also enables Shifting and Enlarging / Shrinking.
 * 
 * This allows to separate the Events and Representation from the actual Model
 * Data, which are a minimum Interface.
 * 
 * When the Data is modified and the repaint() Method called, the Changes are
 * reflected.
 * 
 * Design Decisions / Implementation Details: Using an Applet rather than a
 * Frame allows to embed it into other Swing Containers.
 * 
 * Stores the Point Labels separate from the Points.
 * 
 * Instead of deriving from Frame, which brings with it a lot of Complexity, an
 * Alternative would have been a Class that holds bot the Data and the Frame and
 * delegates only the most important Methods. Even more, a View and a Controller
 * Interface and Frame-specific Classes could have been designed and dynamically
 * assembled in a third Helper Object with a very simplistic Interface. The
 * Controller should properly subscribe to the Frame's Events. The View should
 * abstract from the concrete Implementation.
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Copyright: Copyright (c) Matthias Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 10-26-2002, 12:47 PM
 * <p>
 * 
 * @author mheuer
 * @version 1.0
 *  
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:46:21Z
 * digest: 9dba3823381794e873b63f2270ca822ac5cce85f6da1c3134ac4c3c9b0dcd845
 * stale: false
 * tags: [code/gui, code/graphics]
 * concepts: [Point2D Painter]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Point2DPainter 
implements IPainter, IFocusPainter {

	/** streamIO for Logging */
	private static final Log L = new Log(Point2DPainter.class, 1);

	/** Color of the Graph's Points */
	public static Color colorPoint = Color.BLACK; // BLACK;

	/** Color of the Focus Point */
	public static Color colorFocusPoint = Color.RED;

	/** Radius to point and draw, usually View-Specific */
	public char pointRadius = 5;

	/** Flag to draw the physical Coordinates	 */ 
	public boolean showCoords = true; 
	
	/** Paints the Target Cross around the given Point */
	final static public void paintPoint(final IGraphText g,
			final Point2D mapPoint, final String upperLabel, final int pointRadius, 
			final StringBuffer lowerLabel) {
		final int x = mapPoint.getX(); 
		final int y = mapPoint.getY(); 
		L.n("paintPoint(").l(x).l(",").l(y).l(")").l(upperLabel);
		g.drawLine(   x - pointRadius, y, x + pointRadius, y);
		g.drawLine(x, y - pointRadius, x, y + pointRadius);
		if (upperLabel != null) 
			g.drawString(upperLabel, x+pointRadius, y-pointRadius);
		if (lowerLabel != null) 
			g.drawString(lowerLabel, x+pointRadius, y+pointRadius);
	}
	
	/** Paints the Focus Rectangle around the given Point */
	final static public void paintFocus(final IGraphText g,
			final Point2D mapPoint, final int pointRadius) {
		if (mapPoint == null) 
			return;
		final int x = mapPoint.getX(); 
		final int y = mapPoint.getY(); 
		g.moveTo  (x - pointRadius, y - pointRadius);
		g.drawLine(x - pointRadius, y + pointRadius);
		g.drawLine(x + pointRadius, y + pointRadius);
		g.drawLine(x + pointRadius, y - pointRadius);
		g.drawLine(x - pointRadius, y - pointRadius);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/** Returns the current focus/point drawing radius.
	 * @see graphic.mvc.IFocusPainter#getPointRadius() */
	public char getPointRadius() {
		return pointRadius;
	}

	/** Lightweight State shared between View and Controller */
	public int focusPointIndex = -1;

	/** Returns the index of the point that currently has focus, or -1 when none does.
	 * @see graphic.mvc.IFocusPainter#getFocusIndex() */
	public int getFocusIndex() {
		return focusPointIndex;
	}

	/** Sets which point index currently has focus.
	 * @see graphic.mvc.IFocusPainter#setFocusIndex(int) */
	public void setFocusIndex(int index) {
		focusPointIndex = index;
	}

	/** Finds the point nearest the given screen position without changing the current focus.
	 * @see graphic.mvc.IFocusPainter#getFocusIndex(int, int) */
	public int getFocusIndex(int x, int y) {
		return model.mapPoints.findIndexOfLastNeighbour(x, y, pointRadius);
	}

	/** Moves focus to the point nearest the given screen position.
	 * @see graphic.mvc.IFocusPainter#setFocusIndex(int, int) */
	public int setFocusIndex(int x, int y) {
		focusPointIndex = getFocusIndex(x, y);
		return focusPointIndex;
	}

	/** Reference to the basic Model */
	final public Point2DModel model;

	/** Reference to the Canvas to be painted to */
	protected ICanvas canvas;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, public ones, then private ones (not in
	// Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Creates a painter over a new, empty {@link Point2DModel}.
	 * @param canvas_
	 *            the Canvas to draw to
	 */
	public Point2DPainter(final ICanvas canvas_) { //
		this(new Point2DModel(), canvas_);
	}

	/** Creates a painter over a new model seeded with the given points.
	 * @param points
	 *            List of Points to construct the Model from
	 */
	public Point2DPainter(final VectorPoint2D points, final ICanvas canvas) { //
		this(new Point2DModel(points), canvas);
	}

	/** Creates a painter over the given model and canvas, subscribing itself to both so it
	 * repaints on model changes and canvas paint events.
	 * @param model_
	 *            the general Point2D Model
	 * @param common_
	 *            the view-specific Model, shared between Painter and all
	 *            Controllers
	 * @param canvas_
	 *            the Canvas to draw to
	 */
	public Point2DPainter(final Point2DModel model_, final ICanvas canvas_) { //
		L.n("new").l(getClass());
		this.canvas = canvas_;
		this.model = model_;
		model.addPainter(this); //to receive ModelChange Events
		if (canvas_ instanceof IController)
			((IController) canvas_).addPainter(this); 
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : Graphics Model Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : View Methods
	////////////////////////////////////////////////////////////////////////////

	/** indirectly triggers the repaint of the Canvas */
	public void repaint() {
		if (canvas == null)
			return;
		canvas.repaint(); //triggers the repaint of the whole Canvas
		/*
		 * final IGraphImage g = canvas.getIGraphImage(); if (g != null) {
		 * paintFrame(g); }
		 */
	}

	/** displays the Canvas */
	public void show() {
		repaint();
	}

	/**
	 * Actual painting Model Routine... delegated to from the paint()Event
	 * Method
	 */
	protected void paintPoints(final IGraphText g) {
		//MatrixFloatStreamIn stream = new MatrixFloatStreamIn(points);
		//for (float[] point; null != (point = stream.nextVector());) {
		g.setColor(colorPoint);
		for (int i = model.mapPoints.getInt(); --i >= 0;) {
			paintPoint(g, i);
		}
	}

	/** Paints the Target Cross around the given Point */
	protected void paintPoint(final IGraphText g, final int i) {
		final Point2D point = model.mapPoints.getPointAt(i);
		if (point == null) 
			return;
		if (i == focusPointIndex) {
			g.setColor(colorFocusPoint);
			paintFocus(g, point, pointRadius);
		} else {
			g.setColor(colorPoint);
		}
		paintPoint(g, point, model.getLabel(i), pointRadius, getCoordString(i));
	}
	
	/** Builds the on-screen coordinate label for the point at the given index, when
	 * {@link #showCoords} is enabled.
	 * @return a StringBuffer containing a Representation of the Coordinates.  */
	public StringBuffer getCoordString(final int i) {
		if (!showCoords)
			return null; 
		final Point2D point = model.mapPoints.getPointAt(i);
		return new StringBuffer(10).append(point.getX()).append(',').append(point.getY()); 
	}
	
	/** paint Routine */
	public void draw(final IGraphText gText) {
		if (gText == null) {
			repaint();
		} else {
			paintPoints(gText);
		}
	}

	/** Viewer-specific Controllers are being instantiated and subscribed */
	public void addDefaultControllers(final IController controller) {
		final Point2DMouseController mouseController = new Point2DMouseController(
				model, this);
		controller.addKeyListener(new Point2DKeyController(model, this));
		controller.addMouseListener(mouseController);
		controller.addMouseMotionListener(mouseController);
		controller.addPainter(this);
		//model.addRepaintListener(this);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class */
	public static void testIt() throws Exception {
		final BaseApplet applet = new BaseApplet();
		final Point2DPainter painter = new Point2DPainter(applet); //
		painter.addDefaultControllers(applet); //make it active
		painter.model.addPoint(100, 100, "UntenLinks" );
		painter.model.addPoint(100, 200, "ObenLinks" );
		painter.model.addPoint(200, 200, "ObenRechts" );
		painter.model.addPoint(200, 100, "UntenRechts");
		painter.model.addPoint(150, 250, "ObenMitte");
		//applet.show();
		painter.show();
	}

	/**
	 * The main entry point for the application.
	 * 
	 * @param args
	 *            Array of parameters passed to the application via the command
	 *            line.
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length == 0)
			testIt();
	}

}