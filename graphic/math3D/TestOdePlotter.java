/*
 * File Name: TestOdePlotter.java
 * Created on: 09.01.2004
 *
 */
package graphic.math3D;

import function.derive.ring.Function2ODE;
import function.vector.IBinaryOpFloat;
import function.vector.IFloatVectorField;
import function.vector.OdeHeight;
import function.vector.fChargeField;
import graphic.IGraphText;
import graphic.math2D.Raster;
import graphic.mvc.BaseApplet;
import graphic.mvc.IActiveCanvas;
import graphic.mvc.ICanvas;
import graphic.mvc.IPainter;

import java.awt.Color;
import java.awt.Rectangle;

import math.matrix.MatrixDouble;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.Log;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.StepRK;

/**
 * Title: TestOdePlotter<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
 * mtime: 2026-09-05T10:13:18Z
 * digest: 7bf5de9661991cfe6b362288809afbad894d6f33f38b749e92609fdc9ee27248
 * stale: false
 * tags: [code/testing, code/numerical_integration]
 * concepts: [ODE Plotter Demo/Test Harness]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class TestOdePlotter 
implements IPainter {

	/** Logger to be used for Output 	 */
	final static public Log L = new Log(Body3DPainter.class, 1); 

	/** Default Starting Vector for the ViewPoint */
	protected static final double[] VP = {0.5, 0.5, 0.5};

	/** Default Starting Vector for the StandPoint */
	protected static final double[] SP = {2, 4, 6};

	/** The Definition of the Multipol 	*/
	static final double[][] POSITIONS = fChargeField.QuadPol; //DiPol; //
	static final double[] CHARGES = fChargeField.cQuadPol; //cDiPol; //

	/////////////////////////////////////////////////////////////////////////////////////

	protected float[] standPoint = VectorFloat.COPY(SP);

	/** Vector for the ViewDirection */
	protected float[] direction = VectorFloat.COPY(VP);

	protected float[] viewPoint = direction; 

	/** initializing Constructor 
	 * @param canvas_ The Canvas to draw to, 
	 * used for actively retrieving the Graphics Context. 
	 */
	public TestOdePlotter(final ICanvas canvas_) {
		this.canvas = canvas_;
		if (canvas_ instanceof IActiveCanvas) {
			addControllers((IActiveCanvas) canvas_); 
		}
	}
	
	/** Reference for triggering the repaint	 */
	protected ICanvas canvas; 
	
	/** initializing Constructor 
	 * @param canvas_ The Canvas to draw to, 
	 * used for actively retrieving the Graphics Context. 
	 */
	public TestOdePlotter(final IActiveCanvas canvas_) {
		canvas = canvas_;
		canvas_.addPainter(this);
		addControllers(canvas_);
	}

	protected void addControllers(final IActiveCanvas canvas_) {
		Body3DPainter.addDefaultCoord3DControllers(this, viewPoint, standPoint, canvas_);
	}
	
	/** 
	 * paints the Hyperplanes of an ODE 
	 * @see graphic.mvc.IPainter#draw(graphic.IGraphText)
	 */
	public void draw(final IGraphText gText) {
		if (gText == null) {
			canvas.repaint(); return; }
//		double.setAccuracy(10);
		final Rectangle bounds = gText.getClipBounds();
//		g.clearRect(Bounds.x, Bounds.y, Bounds.x+Bounds.width, Bounds.y+Bounds.height);
		Coordinates3D  c3D = new Coordinates3D(standPoint, bounds, direction);

		final float xMin = -1.5f;
		final float xMax = +1.5f;
		final float yMin = -1.5f;
		final float yMax = +1.5f;
		final float zMin = -1.5f;
		final float zMax = +1.5f;

		//draw Raster
		float[] fRasterX = Raster.proposeRaster(xMin, xMax);
		float[] fRasterY = Raster.proposeRaster(yMin, yMax);
		float[] fRasterZ = Raster.proposeRaster(zMin, zMax);
//		Fig.drawRaster(RasterX, RasterY);
		//draw Coordinate Axis
/*		Point2D Origin = C2D.map(MathGraph2.proposeOrigin(xMin, xMax),
								 MathGraph2.proposeOrigin(yMin, yMax));
		g2D.P.x = Origin.x;
		g2D.P.y = Bounds.y+Bounds.height;
		Fig.drawArrow(0,  -Bounds.height);
		g2D.P.x = Bounds.x;
		g2D.P.y = Origin.y;
		Fig.drawArrow(     Bounds.width, 0);
*/		float[][] raster = new float[3][];
		raster[0] = VectorFloat.COPY(fRasterX);
		raster[1] = VectorFloat.COPY(fRasterY);
		raster[2] = VectorFloat.COPY(fRasterZ);
		//Create the Field Function
		double[][] Pos = MatrixDouble.COPY(POSITIONS); 
		double[][] Points = MatrixDouble.COPY(Pos); 
		double[] Charges = VectorDouble.COPY(CHARGES);
		for (int i = Charges.length; --i > 0; ) {
			Charges[i] = Charges[0]; } //Chreate a symmetric MultiPol
		IFloatVectorField Field = new fChargeField(Points, Charges);
		IBinaryOpFloat Force = new Function2ODE(Field);

		//draw Vectors on the Raster
		float[] out = new float[3];
		Object[] V = VectorFloat.SAMPLE(Field, raster, out);
//		VectorFloat.mulAt(V, -0.1);
		gText.setColor(Color.RED);
		Raster.rasterFigure(new PlotVector(gText, c3D), raster, V);

		//integrate the ODE
//		setAccuracyBits(10);
		double[] Start = {0, 0.5};
		double[] y = VectorDouble.COPY(Start);
		float Step = 0.1f;
		double x    = 0;
		AStepper stepper = new StepRK(Step, x, y, Force);
		//Coordinates3D.Plot (new OdePlotter(g2D, C3D, Stepper, Bounds), raster, null);

		//integrate the HyperPlane
		stepper = new StepRK(Step, x, y, new OdeHeight(Force, 0, 2));
//		Coordinates3D.Plot (new OdePlotter(g2D, c3D, Stepper, Bounds), Raster, null);
		//integrate it the other way
		double xC = 0;
		double[] yC = {0, 0 };	//needs a float[] here...
		//use the time dependant form
		stepper = new StepRK(Step, xC, VectorDouble.COPY(yC), new OdeHeight(Field, 0, 2));	//...because StepConstant uses it
		//use the time-independent form
		//Stepper = new StepConstant(Field, new StepRKQ(Step, xC, VectorFloat.copy(yC), null), 0, 2);	//...because StepConstant uses it
		gText.setColor(Color.BLUE);
		Raster.rasterFigure (new OdePlotter(gText, c3D, stepper, bounds), raster, null);
		
		//3rd way to integrate a time- independent Force Field visually very nicely!
//		Stepper = new StepRK(Step, );
//		HyperPlanePlotter PHP = new HyperPlanePlotter(g2D, c3D, Force, Step);
		HyperPlanePlotter hyperPlotter = new HyperPlanePlotter(gText, c3D, Force, Step);
		hyperPlotter.MaxSteps =  20;
		//Choosing a symmetric Starting Point properly makes the Graphics more symmetric too
		gText.setColor(Color.BLACK);
		double[] StartPoint = {0, 0.2, 0.2};
		hyperPlotter.moveTo(VectorFloat.COPY(StartPoint), null);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + TestOdePlotter.class.getName());
		//L.traceStack(); //just for testing... 
		final BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		new TestOdePlotter(canvas); //unbuffered
		//final TestOdePlotter painter = new TestOdePlotter(new BufferedPainter(canvas)); //Frame();
		canvas.show();
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		testIt(args); }
	
}
