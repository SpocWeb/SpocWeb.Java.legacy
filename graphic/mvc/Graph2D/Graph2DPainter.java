/*
 * File Name: Frame2DGraph.java
 * Created on: 07.06.2003
 *
 */
package graphic.mvc.Graph2D;

import graphic.Figures;
import graphic.IGraphText;
import graphic.Point2D;
import graphic.VectorPoint2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.ICanvas;
import graphic.mvc.IController;
import graphic.mvc.Point2D.Point2DKeyController;
import graphic.mvc.Point2D.Point2DPainter;
import graphs.Edge;
import graphs.IEdgeStreamIn;
import graphs.SparseMatrix;

import java.awt.Color;

import math.vector.VectorObject;
import streamIO.Log;

/**
 * Title: Graph2DPainter<p>
 * Description:
 * Painter Object, receives or generates a Viewer Window 
 * to output the Result of it's Instructions.  
 * 
 * This Class can display and stretch a given Set of Points and Edges. 
 * Holds a Collection of mathematical Objects (Points, Graphs etc.) as the Model 
 * and recalculates the actual Representation. 
 * Also enables Shifting and Enlarging / Shrinking. 
 * 
 * This allows to separate the Events and Representation 
 * from the actual Model Data, which are a minimum Interface.  
 * 
 * Design Decisions / Implementation Details:
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
public class Graph2DPainter 
extends Point2DPainter {

	/** streamIO for logging the Progress of Convergence */
	final static public Log L = new Log(Graph2DPainter.class, 0);
	
	/** Color of reverted Edges with negative Weight */
	//public static Color colorReverse = Color.BLUE; // LIGHT_GRAY; 

	/** Color of reverted Edges with negative Weight */
	//public static Color colorReverseFocus = Color.GREEN;
	
	/** Color of the Graph's Edges */
	public static Color colorEdge = Color.BLUE; //
	
	/** Color of the Edges to the FocusPoint */
	public static Color colorFocusEdge = Color.GREEN; //
	
	////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	////////////////////////////////////////////////////////////////////////////////
	
	/** empty Constructor, creates new, empty Model
	 */
	public Graph2DPainter(final ICanvas canvas) {
		this(canvas, new Graph2DModel()); 
	}
	
	/**
	 * @param edges
	 */
	public Graph2DPainter(final ICanvas canvas, final SparseMatrix edges) {
		this(canvas, new Graph2DModel(new VectorPoint2D(edges.getInt()), edges));
	}
	
	/**
	 * @param points
	 */
	public Graph2DPainter(
	final ICanvas canvas, 
	final VectorPoint2D points, 
	final SparseMatrix edges) {
		this(canvas, new Graph2DModel(points, edges));
	}
	
	/**
	 * @param points
	 */
	public Graph2DPainter(
	final ICanvas canvas, 
	final VectorPoint2D points, 
	final SparseMatrix edges, 
	final VectorObject labels) {
		this(canvas, new Graph2DModel(points, edges, labels));
	}
	
	/**
	 * @param model_
	 */
	/*public Graph2DPainter(final Graph2DModel model_) {
		super(model_);
	}*/
	
	/**
	 * @param canvas_
	 * @param model_
	 * @param common_
	 */
	public Graph2DPainter(
		final ICanvas canvas_,
		final Graph2DModel model_) {
		super(model_, canvas_);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Controller Method Overrides
	////////////////////////////////////////////////////////////////////////////////
	
	/** Actual painting Model Routine... 
	 * delegated to from the paint()Event Method 	 
	 */
	final static public void paintEdge(final Figures g, final Point2D p1, final Point2D p2) {
		g.drawArrow(p1, p2.getX() - p1.getX(), p2.getY() - p1.getY());
	}
	
	/** Actual painting Model Routine... 
	 * delegated to from the paint()Event Method 	 
	 */
	protected void paintEdge(final IGraphText g, final Figures figures, final Edge edge) {
		if (g == null)
			return; 
		final Point2D startPt, endPt;
		if (edge.weight >= 0) { //revert negative Weight Paths!!!
			if (edge.key == focusPointIndex) {
				g.setColor(colorFocusEdge); //figures.g.setColor(colorFocus);
			} else {
				g.setColor(colorEdge); //figures.g.setColor(colorGraph);
			}
			startPt = model.mapPoints.getPointAt(edge.key);
			endPt = model.mapPoints.getPointAt(edge.val);
		} else {
			if (edge.key == focusPointIndex) {
				g.setColor(colorFocusEdge.brighter()); //figures.g.setColor(colorReverseFocus);
			} else {
				g.setColor(colorEdge.brighter()); //figures.g.setColor(colorReverse);
			}
			startPt = model.mapPoints.getPointAt(edge.val);
			endPt = model.mapPoints.getPointAt(edge.key);
		}
		if ((startPt != null) && (endPt != null)) {
			paintEdge(figures, startPt, endPt);
			g.drawString(
				model.getLabel(edge.typ),
				startPt.middle(endPt));
		}
	}
	
	//	protected int filter = -1; 
	
	/** Actual painting Model Routine... 
	 * delegated to from the paint()Event Method 	 
	 */
	protected void paintEdges(final IGraphText g) {
		final Figures figures = new Figures(g);
		final IEdgeStreamIn stream = ((Graph2DModel)model).edges.EdgeIterator();
		//stream.setEdgeFilter(filter); 
		for (Edge edge; null != (edge = stream.nextEdge());) {
			paintEdge(g, figures, edge);
		}
	}
	
	public void draw(final IGraphText gText) {
		paintEdges(gText);
		super.draw(gText); //since the Points are smaller, they are painted last. 
		//this.points.setAt(Raster.generateGraph(edges, 2));
	}
	
	/** Viewer-specific Controllers are being instantiated and subscribed  */
	public void addDefaultControllers(final IController controller) {
		final Graph2DMouseController mouseController = new Graph2DMouseController(model, this);
		controller.addKeyListener(new Point2DKeyController(model, this));
		controller.addMouseListener(mouseController);
		controller.addMouseMotionListener(mouseController);			
		controller.addPainter(this);
	}
		
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt()
	throws Exception {
		final BaseApplet applet = new BaseApplet(); 
		final Graph2DPainter painter = new Graph2DPainter(applet);
		painter.addDefaultControllers(applet);
	
		painter.model.addPoint(100, 100); //, "UntenLinks" );
		painter.model.addPoint(100, 200); //, "ObenLinks"  );
		painter.model.addPoint(200, 200); //, "ObenRechts" );
		painter.model.addPoint(200, 100); //, "UntenRechts");
		painter.model.addPoint(150, 250); //, "ObenMitte");
		painter.show();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt();
	}

}
