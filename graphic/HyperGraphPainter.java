/*
 * File Name: FrameHyperGraph.java
 * Created on: 13.07.2003
 *
 */
package graphic;

import graphic.mvc.BaseApplet;
import graphic.mvc.ICanvas;
import graphic.mvc.Graph2D.Graph2DModel;
import graphic.mvc.Graph2D.Graph2DPainter;
import graphs.Edge;
import graphs.IEdgeStreamIn;
import graphs.SparseGraph;
import graphs.SparseMatrix;

import java.awt.Rectangle;

import math.vector.VectorFloat;
import streamIO.Log;

/**
 * Title: HyperGraphPainter<p>
 * Description:
 *
 * This Painter paints only Portions of a Grap
 * in the Shape of a Hyperbolic Graph with a single Node in the Center 
 * and the next Nodes arranged around it. 
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
public class HyperGraphPainter extends Graph2DPainter {

	/** streamIO for logging the Progress of Convergence */
	public static Log L = new Log(0); 

	/** current Node in the Center	 */
	int currNode = 0; 

	///////////////////////////////////////////////////////////////////////////
	/// Constructors
	///////////////////////////////////////////////////////////////////////////

	/** empty Constructor	 */
	public HyperGraphPainter(final ICanvas canvas) { super(canvas); }

	/**
	 * @param points
	 * @param edges
	 */
	public HyperGraphPainter(final VectorPoint2D points, final SparseMatrix edges, final ICanvas canvas) {
		super(canvas, points, edges);
	}

	/**
	 * @param edges
	 */
	public HyperGraphPainter(final SparseMatrix edges, final ICanvas canvas) {
		super(canvas, edges);
	}

	/**
	 * @param model_
	 * @param common_
	 * @param canvas_
	 */
	public HyperGraphPainter(final Graph2DModel model_, final ICanvas canvas_) {
		super(canvas_, model_);
	}

	///////////////////////////////////////////////////////////////////////////
	/// Methods
	///////////////////////////////////////////////////////////////////////////

	/**
	 * paints the single given Node at the given Position with the Neighbors arranged around in Distance r
	 * @param gText
	 * @param Node
	 * @param x
	 * @param y
	 */
	protected void calcAndPaintNode(final IGraphText gText, final int Node, final int middleX, final int middleY, final int r) {
		L.n("calcAndPaintNode Node="+Node);
//		Arrays.fill(mapPoints.items, null);
		if (currNode != Node) { 
			if (currNode >= 0) { //move away past Node, so it is not being focused on accidently!
				model.mapPoints.setAt(currNode, (Point2D) null);//.x = Integer.MAX_VALUE; 
			}
			currNode = Node;
		}
		Figures figures = new Figures(gText);
		IEdgeStreamIn stream = ((Graph2DModel)model).edges.EdgeIterator();
		stream.setEdgeFilter(currNode);
		float max = Float.NEGATIVE_INFINITY; 
		int i = 0; 
		for (Edge edge; null != (edge = stream.nextEdge());) {
			++i;
			if (max < Math.abs(edge.weight)) {
				max = Math.abs(edge.weight); 
			}
		}
		Point2D origin = new Point2D(middleX, middleY); 
		model.mapPoints.setAt(Node, origin);
		System.out.println("origin="+origin);
		paintPoint(gText, Node); 
		final float angle = 2*((float)Math.PI)/i; 
		final float dr =r/(max+max); 
		float[] coords = new float[2]; 
		stream.reSet(); 
		i = -1; 
		for (Edge edge; null != (edge = stream.nextEdge());) {
			coords[0] = Math.abs(edge.weight)*dr; 
			coords[1] = ++i*angle;
			VectorFloat.POLAR_2_RECT_AT(coords);
			Point2D point = new Point2D(origin.getX() + (int) coords[0], origin.getY() + (int) coords[1]); 
			model.mapPoints.setAt(edge.val, point); //enlarges itemCount!
			System.out.println("calc. mapPoints.items["+edge.val+"]"+point);
			paintPoint(gText, edge.val);
			paintEdge(gText, figures, edge); 
		}
		L.n("calcAndPointNodes");
	}

	/**
	 * completely reqrites painting the whole Frame
	 * paints only the Edges around the Node with the Focus
	 * @param gText
	 */
	public void draw(final IGraphText gText) {
		//super.paintFrame(gText); //don't perform the regular Painting! 
		Rectangle middle = gText.getClipBounds(); //canvas.getBounds(); // 
		//lastBounds = middle; 
		middle.width>>=1;
		middle.height>>=1;
		middle.x+=middle.width;
		middle.y+=middle.height;
		int node = (focusPointIndex >= 0)? focusPointIndex : currNode;  
		L.n("focusPointIndex"+focusPointIndex+" node"+node);
		calcAndPaintNode(gText, node, middle.width, middle.height, Math.min(middle.height, middle.height));
	}

	///////////////////////////////////////////////////////////////////////
	/// static main() and testing Methods
	///////////////////////////////////////////////////////////////////////

	public static void testIt() {
		final SparseMatrix matrix = SparseGraph.getSedgewick29_1().getAntiSymmetric();
		final BaseApplet applet = new BaseApplet(); 
		HyperGraphPainter painter = new HyperGraphPainter(matrix, applet);
		painter.addDefaultControllers(applet);
		painter.show();
	}

	public static void main(String[] args) {
		testIt();
	}

}
