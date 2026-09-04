/*
 * File Name: Frame2DMap.java
 * Created on: 07.06.2003
 *
 */
package graphic.math2D;

import graphic.IGraphText;
import graphic.VectorPoint2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.ICanvas;
import graphic.mvc.IController;
import graphic.mvc.Graph2D.Graph2DPainter;
import graphic.mvc.Point2D.Point2DKeyController;
import graphs.SparseMatrix;
import math.vector.VectorObject;

/**
 * Title: Map2DPainter<p>
 * Description:<p>
 * A Painter that is able to display, translate, scale 
 * and edit a mapped Model consisting of 
 * a Set of 2D Points in @see math.MatrixFloat Coordinates and 
 * a Set of Edges in @see graphs.SparseMatrix Representation. 
 * @see graphic.math3D.Wire3D is isomorphic to this Model  
 * 
 * Allows to dynamically re-calculate the Points when the Edges have changed! 
 * 
 * Design Decisions / Implementation Details:<p>
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
public class Map2DPainter 
extends Graph2DPainter {
	
	/**
	 * @param canvas
	 * @param points
	 * @param edges
	 * @param labels
	 */
	public Map2DPainter(final ICanvas canvas, final VectorPoint2D _points,
			final SparseMatrix _edges, final VectorObject _edgeLabels) {
		this(canvas, new Map2DModel(_points, _edges, _edgeLabels)); 
	}
	
	/**
	 * @param points
	 * @param edges
	 */
	public Map2DPainter(final ICanvas canvas) {
		this(canvas, new Map2DModel());
	}
	
	/**
	 * Allows to dynamically re-calculate the Points when the Edges have changed! 
	 * 
	 * @param edges
	 * @param points
	 */
	public Map2DPainter(final ICanvas canvas, final SparseMatrix edges) {
		this(canvas, new VectorPoint2D(), edges);
	}
	
	/**
	 * @param points
	 * @param edges
	 */
	public Map2DPainter(final ICanvas canvas, final VectorPoint2D points) {
		this(canvas, points, new SparseMatrix());
	}
	
	/**
	 * @param points
	 * @param edges
	 */
	public Map2DPainter(final ICanvas canvas, final VectorPoint2D points, final SparseMatrix edges) {
		this(canvas, new Map2DModel(points, edges)); 
	}
	
	/**
	 * @param model_
	 * @param common_
	 * @param keyListener
	 * @param mouseListener
	 * @param mouseMotionListener
	 */
	public Map2DPainter(final ICanvas canvas_, final Map2DModel model_) {
		super(canvas_, model_);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//  Controller Method Overrides
	////////////////////////////////////////////////////////////////////////////////
	
	/** cached Sizes */
	//protected Rectangle lastBounds;
	
	//protected int lastNumPoints; 
	
	//protected int lastNumEdges; 
	
	/** actual Painting Routine 
	 * generates the Positions on the Fly when the Edges have changed! 
	 * 
	 */
	public void draw(final IGraphText gText) {
		//if((lastNumPoints != model.mapPoints.getInt()) ||
		//   (lastNumEdges  != ((Map2DModel) model).edges .getInt())) { 
		//    lastNumPoints  = ((Map2DModel) model).points.getInt();
		final Map2DModel mapModel = (Map2DModel) model; 
		if ((gText != null) && (model.mapPoints.getInt() != mapModel.edges.getInt())) {
			if (mapModel.edges.getInt() > 0) {
				final float[][] points = mapModel.edges.generateGraph(2); 
				mapModel.points.setAt(points);
			}
			mapModel.mapPoints(gText.getClipBounds()); // canvas.getBounds());
		}
		super.draw(gText);
	}
	
	/** @return a StringBuffer containing a Representation of the Coordinates.  */
	public StringBuffer getCoordString(final int i) {
		if (!showCoords)
			return null; 
		final Map2DModel mapModel = (Map2DModel) model; 
		final float[] point = mapModel.points.getVectorAt(i);
		return new StringBuffer(20).append(point[0]).append(',').append(point[1]); 
	}
	
	/** Viewer-specific Controllers are being instantiated and subscribed 
	 * 
	 * @param controller typically the BaseApplet which provides Events for these Controllers
	 */
	public void addDefaultControllers(final IController controller) {
		//super.addDefaultControllers();
		final Map2DMouseController mouseController = new Map2DMouseController(model, this);
		controller.addKeyListener(new Point2DKeyController(model, this));
		controller.addMouseListener(mouseController);
		controller.addMouseMotionListener(mouseController);			
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt()
	throws Exception {
		SparseMatrix edges = new SparseMatrix();
		BaseApplet applet = new BaseApplet(); 
		final Map2DPainter painter = new Map2DPainter(applet);
		painter.addDefaultControllers(applet);
		edges.addEdge(0, 1, false, 1);
		edges.addEdge(1, 2, 1);
		edges.addEdge(2, 3, 1);
		edges.addEdge(3, 0, 1);
		//f.addEdge(2, 0, 1.41); //still leaves 2 Alternatives...
		//f.addEdge(3, 1, 1.41); //fully determined now! Except for ObenMitte
		//model.setEdges(edges);

		/*
		applet.addPoint(100, 100); //, "UntenLinks" );
		applet.addPoint(100, 200); //, "ObenLinks"  );
		applet.addPoint(200, 200); //, "ObenRechts" );
		applet.addPoint(200, 100); //, "UntenRechts");
		applet.addPoint(150, 250); //, "ObenMitte");
		*/
		painter.show(); //the Applet calculates the Positions automatically
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		testIt();
	}
	
}
