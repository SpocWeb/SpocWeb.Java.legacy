/*
 * File Name: Body3DPainter.java
 * Created on: 13.12.2003
 *
 */
package graphic.math3D;

import graphic.Body2D;
import graphic.IGraphText;
import graphic.mvc.BaseApplet;
import graphic.mvc.IActiveCanvas;
import graphic.mvc.ICanvas;
import graphic.mvc.IController;
import graphic.mvc.IPainter;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.InputEvent;

import math.vector.VectorFloat;
import streamIO.Log;

/**
 * Title: Body3DPainter<p>
 * Description:
 * Painter Object, receives or generates a Viewer Window 
 * to output the Body3D Model referenced.  
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
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class Body3DPainter 
implements IPainter {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(Body3DPainter.class, 1); 

	/** Default Starting Vector for the ViewPoint */
	protected static final double[] VP = {2, 4, 6};

	/** Viewer-specific Controllers are being instantiated and subscribed  */
	final static public void addDefaultCoord3DControllers(final IPainter painter, final float[] viewPoint, final float[] standPoint, final IController controller) {
		controller.addPainter(painter); //IController Interface
		final FloatPointKeyController viewPointCtrl = new FloatPointKeyController(viewPoint); 
		controller.addKeyListener(viewPointCtrl);
		viewPointCtrl.addPainter(painter);
		viewPointCtrl.modifier = InputEvent.SHIFT_DOWN_MASK;
		final FloatPointKeyController standPointCtrl = new FloatPointKeyController(standPoint); 
		controller.addKeyListener(standPointCtrl);
		standPointCtrl.addPainter(painter);
		//standPointCtrl.modifier = InputEvent.ALT_DOWN_MASK; //no Modifier!
		//"old way of moving": rotating about the Origin 
		final RotationMouseController rotPointCrtl = new RotationMouseController(standPoint, painter);
		controller.addMouseListener(rotPointCrtl);
		controller.addMouseMotionListener(rotPointCrtl);
	}
	
	protected final float[] rotation = { 1, 0, 0}; 

	final static public float PI_HALF = (float) Math.PI/2;

	protected final float[] rotLimits = { 1, -PI_HALF-PI_HALF, PI_HALF-0.02f}; 

	/** Viewer-specific Controllers are being instantiated and subscribed  */
	public void addDefaultControllers(final IController controller) {
		addDefaultCoord3DControllers(this, viewPoint, standPoint, controller); 
		//"new way of moving": rotating about yourself
/*		final TranslationMouseController transPointCrtl = 
		  new TranslationMouseController (rotation, rotLimits, this);
		controller.addMouseListener(transPointCrtl);
		controller.addMouseMotionListener(transPointCrtl);
		transPointCrtl.dimensions[0] = 1; //move phi 
		transPointCrtl.dimensions[1] = 2; //move theta
*/	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** Vector for the Standpoint	 */
	protected float[] standPoint = VectorFloat.NEG(VP);

	/** Vector for the viewpoint	 */
	protected float[] viewPoint = new float[3];

	/** Vector for the ViewDirection */
	protected float[] direction = VectorFloat.COPY(VP);

	/** Could be an ICoordinates Object,
	 * except for the ViewPoint, which is relevant for 
	 * ordered Painting.  
	 */ 
	protected Coordinates3D c3D;

	/** The 3D Body to paint */
	public Body3D body3DG = null;
	
	/** The 2D Body (derived from the 3D Body) to paint */
	public Body2D body2D = null;
	
	/** The Canvas to draw to */	
	protected final ICanvas canvas; 

	/** The Painting Bounds, only used once */	
	protected Rectangle Bounds;

	/////////////////////////////////////////////////////////////////////////////////////
	/// Constructors
	/////////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor, only passive Painter
	 */
	public Body3DPainter() {
		this(null);
	}

	/** initializing Constructor 
	 * @param canvas_ The Canvas to draw to, 
	 * used for actively retrieving the Graphics Context. 
	 */
	public Body3DPainter(final ICanvas canvas_) {
		this.canvas = canvas_;
		if (canvas instanceof IActiveCanvas) {
			addDefaultControllers((IActiveCanvas) canvas);
		}
	}
	
	/** initializing Constructor 
	 * @param canvas_ The Canvas to draw to, 
	 * used for actively retrieving the Graphics Context. 
	 */
	public Body3DPainter(final IActiveCanvas canvas_) {
		this.canvas = canvas_;
		addDefaultControllers(canvas_);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	public void draw(final IGraphText gText) {
		preparedraw(gText);
		if (gText == null) {
			return; }
		try {
			drawBody2D(gText, body2D);
		}catch (Throwable t) {
			System.out.println(t);
			t.printStackTrace();
		}
	}
	
	//IGraphText cache;  //Cacheing doesn't work!
	
	/** @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	protected void preparedraw(IGraphText gText) {
		if (gText == null) {
			c3D = null; //calc new Coord System
			body2D = null;
			//direction = VectorFloat.subt(viewPoint, standPoint); //always look at the Origin!
			direction = VectorFloat.POLAR_2_RECT_AT(VectorFloat.COPY(rotation)); 

			VectorFloat.STREAM(rotation, System.out);
			System.out.print(" mapped to  "); 
			VectorFloat.STREAM(direction, System.out);
			System.out.println(); 
			//if (cache != null) {
				//gText = cache; 
				canvas.repaint(); return; //Cacheing doesn't work!
			//} else {
			//	gText = canvas.getIGraphImage(); //doesn't really work :-(
			//}
		}
		//cache = gText; 
		if (c3D == null) { //calculate only once...
			Bounds = gText.getClipBounds();
			c3D = new Coordinates3D(standPoint, direction, Bounds);
		}
		if (body3DG != null) { //load only once...
			gText.setColor(Color.gray);
			if (body2D == null) {
				if (body3DG == null) {
					return; } 
				body2D = body3DG.getBody2D(c3D);
			} 
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Base Folder for the Polyhedrons */
	final static public String PolyederPath = testMathGraph2.DataFolder + "POLYEDER\\";

	/** loading Polyhedrons
	 * @param g2D the Graphics Context to use. 
	 */
	final static public Body3D loadPolyhedron(final int counter) {
		Body3D Body3DG = null;
		final String Path = PolyederPath;
		String FileName;
		switch (counter) {
			case 10: FileName = "Man";			break;
			case 11: FileName = "Ship";			break;
			case 12: FileName = "STEGOSAU";		break;
			case 13: FileName = "TRICERAT";		break;
			case 14: FileName = "Helicopter";	break;
			default: FileName = String.valueOf(counter - 15).trim();
		}
		FileName = Path + FileName; // + ".POL";
		//g.drawString(FileName, 100,100);//Bounds.x, Bounds.y);
		System.out.println(FileName);
		try {
			Body3DG = new Body3D(FileName);
			switch (counter) {
			case 11: Body3DG.scalePoints(1f/30 ); break;
			case 12:
			case 13: Body3DG.scalePoints(1e-5f); break;
			case 14: Body3DG.scalePoints(1e-1f); break;
			}
//		} catch (FileNotFoundException t) {
//			++state;
		} catch (Exception t) {
			String str = t.toString();
			System.out.println(str);
			t.printStackTrace(System.err);
//			g.drawString(str, 100, 200);
		}
		return Body3DG;
	}

	/** draws the given Body in the given Graphics Context 
	 * 
	 * @param g2D Graphics Context
	 * @param body2D the Body to draw
	 */
	private static void drawBody2D(final IGraphText g2D, final Body2D body2D) {
		if (g2D == null) {
			return; }
		body2D.borderColor = Color.black;
		//draw it from back to front
//		body2D.setSequence(Body3.PlaneSequence((float[])c3D.getProjection()[0]));
		body2D.drawKonvex(g2D, true, true);	//filled Planes
//		body2D.drawKonvex(g2D, true, false);	//wire Model
		g2D.setColor(Color.black);
//		body2D.drawWire(g2D);	//instead, use the Border Coloring of drawConvex
		//g2D.setFont(AGraphText.getFont(4,4));
//		g2D.drawPointNumbers(body2D.getPoints());
		g2D.setColor(Color.red);
//		g2D.drawPointNumbers(Body3.getMids(c3D));
		g2D.setColor(Color.green);
//		F3D.drawArrow(Body3.getMidsAt()  , Body3.getPlaneNormsAt());
		g2D.setColor(Color.blue);
//		F3D.drawArrow(Body3.getPointsAt(), Body3.getPointNormsAt());
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws Exception {
		L.n("Testing " + Body3DPainter.class.getName());
		final BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		final Body3DPainter painter = new Body3DPainter(canvas); //Frame();
		painter.body3DG = loadPolyhedron(16); 
		canvas.show();
		//f.paintFrame(canvas.getIGraphImage());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
	//	Body3DGraph Body3DG = new Body3DGraph("E:\\Personal\\Databases\\POLYEDER\\Helicopter");
		testIt(args); }
	
}
