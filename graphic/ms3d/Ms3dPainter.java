/*
 * File Name: Ms3dPainter.java
 * Created on: 17.12.2003
 *
 */
package graphic.ms3d;

import graphic.IGraphText;
import graphic.IPalette;
import graphic.TexturePalette;
import graphic.math3D.Body3DPainter;
import graphic.mvc.BaseApplet;
import graphic.mvc.BufferedPainter;
import graphic.mvc.IActiveCanvas;
import graphic.mvc.ICanvas;
import graphic.mvc.plane2D.MatrixShort;
import graphic.mvc.plane2D.VectorPolygon;

import java.awt.Canvas;
import java.io.IOException;

import math.vector.VectorFloat;
import math.vector.VectorShort;
import streamIO.Log;

/**
 * Painter Object, receives or generates a Viewer Window
 * to output the MilkShape3D Model referenced.
 *
 * <p>Title: Ms3dPainter<p>
 * Description:
 * Painter Object, receives or generates a Viewer Window
 * to output the MilkShape3D Model referenced.
 *
 * This allows to separate the Events and Representation
 * from the actual Model Data, which are a minimum Interface.
 *
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * <h2>Collaborators</h2>
 *
 * | Type | Relationship |
 * |---|---|
 * | {@link Ms3d} | Model this painter maps to 2D and draws. |
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see Ms3d the model this painter draws
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:53:07Z
 * digest: 76f390e46ed1517600f7b70fec5fdbbc8751d36be18913556b03dfcad52b7275
 * stale: false
 * tags: [code/3d_rendering]
 * concepts: [MS3D Model Renderer]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class Ms3dPainter
extends Body3DPainter {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(Ms3d.class, 1); 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** The MilkShape3D Object to draw	 */
	public Ms3d ms3D;

	/**
	 * Creates a painter for the given canvas.
	 * @param canvas_ the Canvas to paint to
	 */
	public Ms3dPainter(final ICanvas canvas_) {
		super(canvas_);
	}

	/**
	 * Creates a painter for the given active (event-driven) canvas.
	 * @param canvas_ the Canvas to paint to
	 */
	public Ms3dPainter(final IActiveCanvas canvas_) {
		super(canvas_);
	}

	/** Maps the model's vertices and triangles into a 2D polygon set and paints it, along with the bones.
	 * @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	public void draw(IGraphText gText) {
		//super.draw(gText);
		L.n(".", 0);
		boolean wasNull = false; //prevent Recursion 
		if (gText == null) { //all this is only for asynchronous Painting!
			wasNull = true;
			c3D = null; //calc new Coord System
			body2D = null;
			//direction = VectorFloat.subt(viewPoint, standPoint); //always look at the Origin!
			direction = VectorFloat.POLAR_2_RECT_AT(VectorFloat.COPY(rotation));
			gText = canvas.getIGraphImage(); //

			VectorFloat.STREAM(rotation, System.out);
			System.out.print(" mapped to  "); 
			VectorFloat.STREAM(direction, System.out);
			System.out.println(); 
		}
		preparedraw(gText); //also sets C3D
		if (gText == null) {
			return; }
		final short[][] vertices = new short[ms3D.verticesC.length][3] ;
		c3D.map(vertices, ms3D.verticesC);
		final IPalette[] palette = new IPalette[ms3D.textures.length];
		for (int i = palette.length; --i >= 0; ) {
			palette[i] = new TexturePalette(ms3D.textures[i].textureImg, 3, 4);
		}
		final VectorPolygon vp = new VectorPolygon(ms3D.triangles.length);
		for (int i = ms3D.triangles.length; --i >= 0; ) {
			final Ms3dTriangle triangle = ms3D.triangles[i];
			final short[][] polygon = constructPolygon(vertices, triangle);
			final MatrixShort tri = new MatrixShort(polygon, false, true); 
			tri.palette = palette[triangle.texture]; 
			vp.addItem(tri); 
			//tri.numPlane = i; //just for Debugging
		}
		//body2D.getVectorPolygon(true).draw(gText);
		vp.zOrder = true;
		vp.draw(gText);
		ms3D.drawBones(gText, c3D); 
		//gText.flush();
		if (wasNull) canvas.repaint(); //to flush the Memory Image to the Screen
	}
	
	/** constructs a Polygon from the given mapped Vertices and Triangle Objects  
	 * 
	 * @param vertices the mapped Vertices. possibly containing nulls due to Coordinates out of Bounds. 
	 * @param triangle the Triangle to use to construct the Polygon 
	 * @return a new Polygon or null, if any of the Points was not mappable
	 */
	final static public short[][] constructPolygon(final short[][] vertices, final Ms3dTriangle triangle) {
		final short[][] polygon = new short[3][5];
		for (int j = polygon.length; --j >= 0; ) {
			final int vertexNum = triangle.vertices[j]; 
			final short[] vertex = vertices[vertexNum];
			if (vertex == null) { //unmappable Vertices
				return null; } //should lead to unmappable Planes
			final float[] texCoords = triangle.textureCoords[j];
			VectorShort.COPY_AT(polygon[j], vertex);
			polygon[j][3] = (short) (texCoords[0]*256); //TODO: Texture Size hardcoded for now
			polygon[j][4] = (short) (texCoords[1]*256);
			//polygon[j][5] = (short) vertexNum; //just for Debugging
		}
		return polygon;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws java.io.IOException {
		L.n("Testing " + Ms3dPainter.class.getName());
		//L.traceStack(); //just for testing... 
		final BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		//final Ms3dPainter painter = new Ms3dPainter(canvas); //unbuffered
		final Ms3dPainter painter = new Ms3dPainter(new BufferedPainter(canvas)); //Frame();
		painter.ms3D = new Ms3d("G:/CD/eBooks/3D_Models/Code/Chapter6/thug jump.ms3d");
		painter.body3DG = painter.ms3D.getBody3DG();
		painter.ms3D.loadTextures(new Canvas()); //canvas); 
		//painter.ms3D.stream("C:/man");
		canvas.show();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws IOException {
	//	Body3DGraph Body3DG = new Body3DGraph("E:\\Personal\\Databases\\POLYEDER\\Helicopter");
		testIt(args); }
	
}
