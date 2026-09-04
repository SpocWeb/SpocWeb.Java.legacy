package graphic.math3D;

//import java.io.IOException;
import function.derive.Identity;
import function.vector.OdeLorentz;
import function.vector.fProduct;
import function.vector.fSinProd;
import graphic.Body2D;
import graphic.IGraphShape;
import graphic.IGraphText;
import graphic.PaletteRGB;
import graphic.Point2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.ICanvas;
import graphic.mvc.IController;
import graphic.mvc.IPainter;
import graphic.mvc.KeyCounter;

import java.awt.Color;
import java.awt.Event;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import math.matrix.MatrixFloat;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.copy.group.ring.StepRK;

/**This class reads PARAM tags from its HTML host page and sets
 * the color and label properties of the applet.
 * Program execution begins with the init() method.
 * 
 * 
 */
public class TestMathGraph3 
//extends Body3DPainter
implements IPainter { //Applet {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	final static public int TEST_LORENTZ = 9;
	
	private static final int TEST_SCANNING = 6;

	/** Default Starting Vector for the ViewPoint */
	protected static final double[] VP = {0.5, 0.5, 0.5};

	/** Default Starting Vector for the StandPoint */
	protected static final double[] SP = {2, 4, 6};

	protected float[] standPoint = VectorFloat.COPY(SP);

	protected float[] viewPoint = new float[3];

	/** Vector for the ViewDirection */
	protected float[] direction = VectorFloat.COPY(VP);

	/** The Body to paint */
	protected Body3D Body3DG = null;
	
	/** Could be an ICoordinates Object,
	 * except for the ViewPoint, which is relevant for 
	 * ordered Painting.  
	 */ 
	protected Coordinates3D C3D;

	/** The Painting Bounds, only used once */	
	protected Rectangle Bounds;

	/** The Canvas to draw to */	
	final ICanvas canvas; 
	
	/** initializing Constructor 
	 * @param canvas_ The Canvas to draw to 
	 */
	public TestMathGraph3(final ICanvas canvas_) {
		this.canvas = canvas_;
		state = new KeyCounter(KeyEvent.VK_ENTER, -10); 
		state.addPainter(this);
		init();
	}
	
	private static final int numLorentz = 999;

	/**Coordinates for the Lorentz Polygon	 */
	private float[][] LorentzPolygon = new float[numLorentz][];

	double[] start = { 0.1,  0.1,   0.1};
	double[] stand = {50.0, 75.0, 200.0};
	double[] visier= { 0.0,  0.0,  40.0};
//	double tmp = new double();

	float[] Start = VectorFloat.COPY(start);
	float[] Stand = VectorFloat.COPY(stand);
	float[] Visier= VectorFloat.COPY(visier);

	Image img;
	
	/** @see graphic.mvc.IPainter#paintFrame(graphic.IGraphText)	 */
	public void draw(IGraphText gText) {
		if (gText == null) {
			canvas.repaint(); 
			return;
			//gText = canvas.getIGraphImage();
		}
		Bounds = gText.getClipBounds();
		try {
			paintTestGraphic(gText);
			//testFillScalarTriangle(gText);
		}catch (Throwable t) {
			System.out.println(t);
			t.printStackTrace();
		}
	}

	/**
	 * adds the State Counter as a Key Listener
	 */
	public void addDefaultControllers(final IController controller) {
		Body3DPainter.addDefaultCoord3DControllers(this, viewPoint, standPoint, controller);
		controller.addKeyListener(state);
	}

	protected KeyCounter state;

	////////////////////////////////////////////////////////////////////////////
	/// #region : IPainter Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** The entry point for the applet. 	 */
	public void init() {
		// TODO: Add any constructor code after initForm call.
//		double.setAccuracy(10);
		double[] y = VectorDouble.COPY(Start);
		double Step = 0.01;
		double x	= 0;
		StepRK Stepper = new StepRK(Step, x, y, new OdeLorentz());
		int n = numLorentz;
		while (--n >= 0) {
			Stepper.stepFloat();
			LorentzPolygon[n] = VectorFloat.COPY(Stepper.yv);
		}
	}

	int Body3DGcounter;

	/**
	 *
	 */
	void paintTestGraphic(final IGraphText g2D) {
//		int i = -1; while (++i < 3) VP[i] += 0.1;
//		float[] Location = new float[](VP);

		Object S = null;
		Column3D  Col = null;
		Figures3D F3D = null;

		//Scalar Function on a Raster
		//A Collection of Rasters in x, y, z Direction is not a float[]!!!
		double[][] R = {{0.0, 0.2, 0.4, 0.6, 0.8, 1.0},
						{0.0, 0.2, 0.4, 0.6, 0.8, 1.0},
						{0.0, 0.2, 0.4, 0.6, 0.8, 1.0}};

		float[][] Raster3 = null;
		float[][] Raster2 = null;
		float[] V1 = null;
		float[] V2 = null;
		float[] One = null;

		double[]v1 = {0, 0, 0};
		double[]v2 = {1, 1, 1};
		V1	= VectorFloat.COPY(v1);
		V2	= VectorFloat.COPY(v2);

		if ((state.counter < 8) || (state.counter > TEST_LORENTZ)) {
			direction = VectorFloat.NEG(standPoint);	//always look at the Origin.
			C3D = new Coordinates3D(standPoint, direction, Bounds);

			Col = new Column3D (g2D, C3D);
			F3D = new Figures3D(g2D, C3D);

			One	= VectorFloat.COPY(v2);
			g2D.setColor(Color.gray);
			V2[1]  = V1[1];
			V2[2]  = V1[2];
			V2[0] *= 1.1;

			PlotVector VP = new PlotVector(g2D, C3D);

			VP.moveTo (V1, V2); V2[1] = V2[0]; V2[0] = V1[0];
			VP.moveTo (V1, V2); V2[2] = V2[1]; V2[1] = V1[1];
			VP.moveTo (V1, V2);

			Raster3		= new float[3][];
			Raster3[0]	= VectorFloat.COPY(R[0]);
			Raster3[1]	= VectorFloat.COPY(R[1]);
			Raster3[2]	= VectorFloat.COPY(R[2]);
			Raster2		= new float[2][];
			Raster2[0]	= Raster3[0];
			Raster2[1]	= Raster3[1];

		}

		Color[] Palette1 = null;
		if (state.counter < 2) {
			Palette1 = PaletteRGB.CYCLE_PALETTE(255);
			int i = 0; while ((i += 20) < 255) Palette1[i] = Color.black;
			S = VectorFloat.SAMPLE(fSinProd.fSinProd, Raster2);	//Sample a scalar Function on the Raster.
		}

		/** A planar Square for testing of 'Ribbon' */
		double[][]squarePoints = {{0,0,0},
							{0,1,0},
							{1,1,0},
							{1,0,0}};
		int[][] squarePlanes = {{0,1,2,3}};
		double[][]lTriAngle =  {{-4,-4, 0},
								{+4,-4, 0},
								{ 0,+4, 0}};

		double[][]sTriAngle =  {{-1,-1, 0},
								{+1,-1, 0},
								{ 0,+1, 0}};
		switch (state.counter) {
		case -10: testTube(lTriAngle, sTriAngle); break; //
		case -9: testRibbonOpen(squarePoints); break; //
		case -8: testRibbonClosed(squarePoints); break; //
		case -7: testExtrusion(squarePoints, squarePlanes); break; //
		case -6: //testMollweide(g2D); break; 
				 //Testing the Platonic Bodies...
		case -4: Body3DG = new Body3D(MatrixFloat.COPY(Platonic.PointsTetrahedron),  Platonic.PlanesTetrahedron , true); break; //works
		case -5: Body3DG = new Body3D(MatrixFloat.COPY(Platonic.PointsCube),         Platonic.PlanesCube        , true); break; //works
		case -3: Body3DG = new Body3D(MatrixFloat.COPY(Platonic.PointsDodecahedron), Platonic.PlanesDodecahedron, true); break; //works
		case -2: Body3DG = new Body3D(MatrixFloat.COPY(Platonic.PointsIcosahedron),  Platonic.PlanesIcosahedron , true); break; //works
		case -1: Body3DG = new Body3D(MatrixFloat.COPY(Platonic.PointsRauthedron),   Platonic.PlanesRauthedron  , true); break; //works
		case  0: testPlanePlot(g2D, S, R, Raster2, Palette1); break;
		case  1: testScalarPlanePlot(g2D, S, R, Raster2, Palette1); break;
		case  2: testHistoPlot(g2D, Raster2); break;
		case  3: testScalarPointPlot(g2D, Raster3); break;
		case  4: testVectorPlot(g2D, Raster3); break;
		case 5: //Draw the Unity Cube //also the different Coloring simulating Light!
				Col.fillColumn3D(V1, One);	//Draw a grey Box in the unit Cube
				break;
		case TEST_SCANNING: testScanning(Col, F3D, V1, One); break;
		case 7: testDrawMenger(g2D); break;
		case 8: drawLorentzDynamic(g2D); break;
		case TEST_LORENTZ: testDrawLorentzPoly(g2D, V1, V2); break;
		case 10: break;//testDrawStars(g2D); 
		default: if (Body3DGcounter == state.counter) break; //prevent reloading each time
			Body3DGcounter = state.counter;
			Body3DG = Body3DPainter.loadPolyhedron(state.counter); break;
		}

		if ((state.counter > 10) || (state.counter < 0)) {
			Body2D body2D = Body3DG.getBody2D(C3D);
			body2D.drawKonvex(g2D, true, true);
		}
	}

	private void testDrawLorentzPoly(IGraphShape g2D, float[] V1, float[] V2) {
		Figures3D F3D;
		//c3D = new Coordinates3D(Stand, Bounds, V1);//Visier);
		F3D = new Figures3D(g2D, C3D);
		VectorFloat.MUL_AT (V2, 40);
		F3D.drawPoint(V2, V1);
//		Column3D Col3D = new Column3D(g2D, c3D);
//		Col3D.drawColumn3D(Stand, Visier);
		int n = numLorentz;
		Point2D yP = C3D.mapPt(LorentzPolygon[numLorentz-1]);
		g2D.moveTo(yP); //Move to the Starting Point
		while (--n > 0) {
			g2D.drawLine(yP = C3D.mapPt(LorentzPolygon[n])); } 
	}

	/** draws a Lorentz Curve
	 * @param g2D
	 */
	private void drawLorentzDynamic(IGraphShape g2D) {
		//double.setAccuracy(10);
		Coordinates3D  C3D = new Coordinates3D(VectorFloat.COPY(Stand), Bounds, VectorFloat.COPY(Visier));
		double[] y = VectorDouble.COPY(Start);
		double Step = 0.01;
		double x	= 0;
		StepRK Stepper = new StepRK(Step, x, y, new OdeLorentz());
		Point2D yP = C3D.mapPt(y);
		g2D.moveTo(yP);
		int n = 1000;
		while (--n > 0) {
			Stepper.stepFloat();//Step();
			yP = C3D.mapPt(Stepper.yv);
			g2D.drawLine(yP);
			//Plot_Int (Ableitungen,StartV,t_Bereich);
		}
	}

	/** Draw a Menger Sponch 
	 * @param g2D
	 */
	private void testDrawMenger(IGraphShape g2D) {
		int Fine = 27+1;//81+1; //242+1;//729+1;
		int[] MaxIndex = {Fine,Fine,Fine};
		float[][] Rstr3 = new float[3][];
		float d = 1.0f/ Fine;
		float o = 0.0f;
		Rstr3[2] = Rstr3[1] = Rstr3[0] = VectorFloat.RASTER(o, d, Fine);
		VoxelPlot drwV = new VoxelPlot(g2D, C3D, MaxIndex);
		drwV.ColorFactor= 255;
		Color[][] Palette = {{Color.red}, {Color.green}, {Color.blue}};
		drwV.	Palette = Palette;
		drwV.	Radius	= 100;
//		c3D.rePlotIntersection = true;
		C3D.rasterOrdered(drwV, Rstr3, null);
//		c3D.rePlotIntersection = false;
	}

	float[] scanPoint  = VectorFloat.COPY(SP);

	/** Draw only the selected Helper Lines of a Scan Point: 
	 * 
	 * @param Col
	 * @param F3D
	 * @param V1
	 * @param One
	 */
	private void testScanning(Column3D Col, Figures3D F3D, float[] V1, float[] One) {
		int [] project = {3, 3, 3}; //the project Parameter determines which Helper Lines are displayed!
		Col.drawColumn3D(V1, One);	//Draw a WireFrame Box in the unit Cube
		Col.drawPoint(V1, One, scanPoint, 7);
		F3D.drawPoint(V1, One, scanPoint, project);
	}

	/** Sample Vector plot only on (x,y,z) Range 
	 * 
	 * @param g2D
	 * @param Raster3
	 */
	private void testVectorPlot(IGraphText g2D, float[][] Raster3) {
		Object S;
		S = VectorFloat.SAMPLE(Identity.IDENTITY, Raster3, null);	//Sample a scalar Function on the Raster.
//		S.mulAt(new ByRefDouble (0.125));
		PlotVector drawV = new PlotVector(g2D, C3D);
		drawV.ColorFactor= 768;
		drawV.  Palette = PaletteRGB.CYCLE_PALETTE(255);
		drawV.ColorMode = true;
		C3D.rasterOrdered(drawV, Raster3, (Object[]) S);  //hand over the Raster for Painting
	}

	/**	Sample Scalar plot of on (x,y,z) Range  
	 * @param g2D
	 * @param Raster3
	 */
	private void testScalarPointPlot(IGraphText g2D, float[][] Raster3) {
		Object S;
//		S = VectorFloat.sample(fSinProd.fSinProd, Raster3);	//Sample a scalar Function on the Raster.
		S = VectorFloat.SAMPLE(fProduct.fProduct, Raster3);	//Sample a scalar Function on the Raster.
		//S.mulAt(new ByRefDouble (255));
		ScalarPointPlot draw = new ScalarPointPlot(g2D, C3D);
		draw.ColorFactor= 255;
		draw.  Palette	= PaletteRGB.CYCLE_PALETTE(255);
		draw.	Radius	= 100;
		draw. SizeMode	= true;
		draw.ColorMode	= true;
		C3D.rasterOrdered(draw, Raster3, (Object[]) S); //hand over the Raster for Painting
	}

	/** Sample of S filled with fSinProd = sin(x)*sin(y) 
	 * as a colored Histogram plot on (x,y) Range 
	 * @param g2D
	 * @param Raster2
	 */
	private void testHistoPlot(IGraphText g2D, float[][] Raster2) {
		Object S = VectorFloat.SAMPLE(fSinProd.fSinProd, Raster2);	//Sample a scalar Function on the Raster.
		double[] dv = {0.1, 0.1, 0.0};
		float [] dV = VectorFloat.COPY(dv);
		HistoPlot drwH = new HistoPlot(g2D, C3D, dV);
		drwH.ColorFactor= 255;
		drwH.  Palette	= PaletteRGB.CYCLE_PALETTE(255);
		drwH.ColorMode	= true;
		C3D.rasterOrdered(drwH, Raster2, (Object[]) S); //hand over the Raster for Painting
	}

	/**
	 * Sample Plane plot only of S filled with fSinProd = sin(x)*sin(y) 
	 * on (x,y) Range with Coloring! 
	 * @param g2D
	 * @param S
	 * @param R
	 * @param Raster2
	 * @param Palette1
	 */
	private void testScalarPlanePlot(
		IGraphText g2D,
		Object S,
		double[][] R,
		float[][] Raster2,
		Color[] Palette1) {
		ScalarPlanePlotter	SPP = new ScalarPlanePlotter(g2D, C3D, R[0].length, Palette1);
		SPP.ColorFactor= 255;
		SPP.fillMode = true;
		C3D.rasterOrdered(SPP, Raster2, (Object[]) S); //hand over the Raster for Painting
	}

	/**
	 * Sample Plane plot of S filled with fSinProd = sin(x)*sin(y) 
	 * on the (x,y) Range
	 * @param g2D
	 * @param S
	 * @param R
	 * @param Raster2
	 * @param Palette1
	 */
	private void testPlanePlot(
		IGraphShape g2D,
		Object S,
		double[][] R,
		float[][] Raster2,
		Color[] Palette1) {
		PlanePlotter drwP = new PlanePlotter(g2D, C3D, R[0].length);
		drwP.ColorFactor= 255;
		drwP.fillMode = true;
		drwP.Palette  = Palette1;
		C3D.rasterOrdered(drwP, Raster2, (Object[]) S); //hand over the Raster for Painting
	}

	/** Testing the Extrusion Mechanism: extruding a filled Square Plane
	 * 
	 * @param Points
	 * @param Planes
	 */
	private void testExtrusion(double[][] Points, int[][] Planes) {
		final Body3D Body2D = new Body3D(MatrixFloat.COPY(Points), Planes, false);
		final Body3D Body3D = Body2D.extrude(1);
		Body3DG = Body3D;
	}

	/**
	 * Testing the Ribbon Mechanism: extruding a closed SquareLine
	 * @param Points
	 */
	private void testRibbonClosed(final double[][] Points) {
		final Polygon3D Poly = new Polygon3D(MatrixFloat.COPY(Points));
		Poly.periodic = true;
		final Body3D Body3D = Poly.Ribbon(0.2f);
		Body3DG = Body3D;
	}

	/**
	 * Testing the Ribbon Mechanism: extruding an open SquareLine
	 * @param Points
	 */
	private void testRibbonOpen(final double[][] Points) {
		final Polygon3D Poly = new Polygon3D(MatrixFloat.COPY(Points));
		Poly.periodic = false;
		final Body3D Body3D = Poly.Ribbon(0.2f);
		Body3DG = Body3D;
	}

	/** Testing the Tube Mechanism: 
	 * rotating a small Triangle around a large Triangle!
	 * @param lTriAngle
	 * @param sTriAngle
	 */
	private void testTube(double[][] lTriAngle, double[][] sTriAngle) {
		Polygon3D lTri = new Polygon3D(MatrixFloat.COPY(lTriAngle)); lTri.periodic = true;
		Polygon3D sTri = new Polygon3D(MatrixFloat.COPY(sTriAngle)); sTri.periodic = true;
		final Body3D Body3D = lTri.Tube(sTri.getPointsAt(), null);
		Body3DG = Body3D;
	}

		//draw Raster
/*		float[] fRasterX = MathGraph2.proposeRaster(xMin, xMax);
		float[] fRasterY = MathGraph2.proposeRaster(yMin, yMax);
		int[] RasterX = C2D.MapX.map(fRasterX);
		int[] RasterY = C2D.MapY.map(fRasterY);
		Fig.drawRaster(RasterX, RasterY);
		//draw Coordinate Axis
		Point2D Origin = C2D.map(MathGraph2.proposeOrigin(xMin, xMax),
								 MathGraph2.proposeOrigin(yMin, yMax));
		g2D.P.x = Origin.x;
		g2D.P.y = Bounds.y+Bounds.height;
		Fig.drawArrow(0,  -Bounds.height);
		g2D.P.x = Bounds.x;
		g2D.P.y = Origin.y;
		Fig.drawArrow(     Bounds.width, 0);
		float[][] Raster = new float[][2];
		Raster[0] = new BodyManifold(fRasterX);
		Raster[1] = new BodyManifold(fRasterY);
		//draw Vectors on the Raster
		double[] Pt = {0.5};
		float[] [] Points = new float[][2];
		float [] Charges = new float[2];
		Points [0] = new float[](Pt); Pt[0] = - Pt[0];		//empty float[] == Origin
		Points [1] = new float[](Pt);		//empty float[] == Origin
		Charges[0] = new double(+0.5);	//Unit Charge
		Charges[1] = new double(-0.5);	//Unit Charge
		intFunction Field = new fChargeField(Points, Charges);	//Static Force Field
		intDGL Force = new Function2ODE(Field);	//Blow Up the Force Field to a (time) dependent Field to integrate
		float[] V = new float[](Field, Raster);
		V.mulAt(new ByRefDouble(-0.1));
		Coordinates3D.Plot (new PlotVector2D(g2D, C2D), Raster, V);	//Plot the Vector Field
		//integrate the ODE
		double.setAccuracy(10);
		double[] Start = {0, 0.5};
		double Step = new double(0.01);
		double x	= new double(0);
		float[] y = new float[](Start);
		//Plot the Trajectories
		absStepper Stepper; Stepper = new StepRKQ(Step, x, y, Force);
		Coordinates3D.Plot (new OdePlotter(g2D, C2D, Stepper, Bounds), Raster, null);
		//Use the Wrapper to integrate on Equipotential Lines.
		Stepper = new StepRKQ(Step, x, y, new OdeHeight(Force, 0, 1));
		double xC = new double(0);
		double yC = new double(0);
		Stepper = new StepRKQ(Step, xC, new float[](yC, 1), null);//needs a float[] here,
		Stepper = new StepConstant(Field, Stepper, 0, 1);	//because StepConstant uses it
		Coordinates3D.Plot (new OdePlotter(g2D, C2D, Stepper, Bounds), Raster, null);
*/

	/**
	 * Moves the viewPoint or the State with key Presses.
	 */
	public boolean keyDown(Event evt, int key) {
		switch (state.counter) {
		case 8:
			switch (key) {	//Move the Stand Point
				case KeyEvent.VK_ENTER: ++state.counter; Body3DG = null; break;	//
				case KeyEvent.VK_PAGE_UP: Stand[2]+=2; break;	//
				case KeyEvent.VK_PAGE_DOWN: Stand[2]-=2; break;	//
				case KeyEvent.VK_UP: Stand[1]+=2; break;	//
				case KeyEvent.VK_DOWN: Stand[1]-=2; break;	//
				case KeyEvent.VK_LEFT: Stand[0]-=2; break;	//
				case KeyEvent.VK_RIGHT: Stand[0]+=2; break;	//
				default: return true;
			}
			break;
		case TEST_LORENTZ: //
			Projection P = C3D.getProjector();
			float StepP = +4.0f;
			float StepM = -4.0f;
			float To    = +0.1f;
			float Fro   = -0.1f;
			if ((evt.modifiers & Event.SHIFT_MASK) != 0)
			{	//Modify the Look Vector by rotating it.
				//Also the Target Vector has to be modified!
			switch (key) {	//rotate the
				case KeyEvent.VK_ENTER: ++state.counter; Body3DG = null; break;	//
				case KeyEvent.VK_PAGE_UP: P.rotateView(To , 2); break;	//
				case KeyEvent.VK_PAGE_DOWN: P.rotateView(Fro, 2); break;	//
				case KeyEvent.VK_UP: P.rotateView(To , 0); break;	//
				case KeyEvent.VK_DOWN: P.rotateView(Fro, 0); break;	//
				case KeyEvent.VK_LEFT: P.rotateView(To , 1); break;	//
				case KeyEvent.VK_RIGHT: P.rotateView(Fro, 1); break;	//
			}
			}
			else if ((evt.modifiers & Event.CTRL_MASK) != 0) {	//Modify the Start Position
			switch (key) {
				case KeyEvent.VK_ENTER: ++state.counter; Body3DG = null; break;	//
				case KeyEvent.VK_PAGE_UP: P.a[0][2] += 2; break;	//
				case KeyEvent.VK_PAGE_DOWN: P.a[0][2] -= 2; break;	//
				case KeyEvent.VK_UP: P.a[0][1] += 2; break;	//
				case KeyEvent.VK_DOWN: P.a[0][1] -= 2; break;	//
				case KeyEvent.VK_LEFT: P.a[0][0] -= 2; break;	//
				case KeyEvent.VK_RIGHT: P.a[0][0] += 2; break;	//
			}
			P.inValidate();	//1st Position and Width have changed!
			} else {
			switch (key) {
				case KeyEvent.VK_ENTER: ++state.counter; Body3DG = null; break;	//
				//moving the View Point without changing the Rotation Vector!
				case KeyEvent.VK_PAGE_UP: VectorFloat.addProdAt(P.a[0], StepP, P.getRot()[2]); break; //P.recalc(false); break;	//
				case KeyEvent.VK_PAGE_DOWN: VectorFloat.addProdAt(P.a[0], StepM, P.getRot()[2]); break; //P.recalc(false); break;	//
				case KeyEvent.VK_UP: P.rotateView(To , 0); break;	//
				case KeyEvent.VK_DOWN: P.rotateView(Fro, 0); break;	//
				case KeyEvent.VK_LEFT: P.rotateView(To , 1); break;	//
				case KeyEvent.VK_RIGHT: P.rotateView(Fro, 1); break;	//
				default: return true; }
			}
			break;
		default:
		}
		//this.repaint();
		return true; 
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + testMathGraph2.class.getName());
		BaseApplet canvas = new BaseApplet();
		canvas.setSize(BaseApplet.WIDTH, BaseApplet.HEIGHT);
		TestMathGraph3 f = new TestMathGraph3(canvas); //Frame();
		f.addDefaultControllers(canvas);
		canvas.show();
		//f.paintFrame(canvas.getIGraphImage());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.FileNotFoundException, java.io.IOException, java.sql.SQLException {
	//	Body3D Body3DG = new Body3D("E:\\Personal\\Databases\\POLYEDER\\Helicopter");
		testIt(args); }
	
}
