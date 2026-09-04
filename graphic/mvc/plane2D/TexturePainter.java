/*
 * File Name: TexturePainter.java
 * Created on: 08.01.2004
 *
 */
package graphic.mvc.plane2D;

import graphic.IGraphText;
import graphic.math3D.Body3DPainter;
import graphic.mvc.BaseApplet;
import graphic.mvc.IActiveCanvas;
import graphic.mvc.ICanvas;
import math.vector.VectorFloat;
import streamIO.Log;

/**
 * Title: TexturePainter<p>
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
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class TexturePainter 
extends Body3DPainter {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(TexturePainter.class, 1); 

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////

	/** The TexturedBody Object to draw	 */
	public TexturedBody body;

	/**
	 * @param canvas_
	 */
	public TexturePainter(final ICanvas canvas_) {
		super(canvas_);
	}

	/**
	 * @param canvas_
	 */
	public TexturePainter(final IActiveCanvas canvas_) {
		super(canvas_);
	}

	/** @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	public void draw(IGraphText gText) {
		//super.draw(gText);
		L.n(".", 0);
		boolean wasNull = false; //prevent Recursion 
		if (gText == null) { //all this is only for asynchronous Painting!
			wasNull = true;
			c3D = null; //calc new Coord System
			body2D = null;
			//VectorFloat.zeroAt(standPoint); //jump INTO the SkyBox 
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
		body.map(c3D).draw(gText);
		//ms3D.drawBones(gText, c3D); 
		//gText.flush();
		if (wasNull) canvas.repaint(); //to flush the Memory Image to the Screen
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	static final String DEFAULT_GRAPHICS1 = "E:/MHeuer/Databases/POLYEDER/Man/Man";
	static final String DEFAULT_GRAPHICS2 = "E:/MHeuer/graphics/textures/skyBoxes/SkyBox";
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws Exception {
		if (args.length == 0) {
			args = new String[] { DEFAULT_GRAPHICS2 }; }
		L.n("Testing ").l(TexturePainter.class).l(" with Graphic in ").l(args[0]);
		//L.traceStack(); //just for testing... 
		final BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		final TexturePainter painter = new TexturePainter(canvas); //unbuffered
		//final TexturePainter painter = new TexturePainter(new BufferedPainter(canvas)); //Frame();
		painter.body = new TexturedBody(args[0]);
		canvas.show();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
	//	Body3DGraph Body3DG = new Body3DGraph("E:\\Personal\\Databases\\POLYEDER\\Helicopter");
		testIt(args); }
	
}
