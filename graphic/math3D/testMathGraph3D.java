package graphic.math3D;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StreamTokenizer;

import math.matrix.MatrixFloat;
import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.copy.group.ring.StepRK;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import function.IFloatFunction;
import function.IFunction;
import function.Projections;
import function.derive.IDeriveAble;
import function.derive.Identity;
import function.derive.ring.body.Brillouin;
import function.derive.ring.body.Cosinus;
import function.derive.ring.body.Langevin;
import function.derive.ring.body.Sinus;
import function.vector.OdeLorentz;
import function.vector.fProduct;
import function.vector.fSinProd;
import graphic.AGraphText;
import graphic.Body2D;
import graphic.PaletteRGB;
import graphic.Point2D;
import graphic.Polygon2D;
/**
  * Title: testMathGraph3D.java<p>
  * Description:
  * Older Class for testing MathGraph3
  * replaced by TestMathGraph3
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-28-1999, 12:52 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T12:43:56Z
  * digest: 9e1011bc96149b47b55fc3c33048149d63790d3ec049d4cfb92a82662664b585
  * stale: false
  * tags: [code/testing, code/3d_rendering]
  * concepts: [3D Graph Demo/Test Harness]
  * facets: {layer: test, status: legacy, complexity: low}
  * -->
  */
public class testMathGraph3D {

	private static int state = -10;

	/**The main entry point for the application; runs a fixed series of function and
	 * body plot demos selected by {@link #state}.
	 *
	 * <!-- docstate
	 * tags: [code/testing]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * @param args Array of parameters passed to the application via the command line.	 */
	public static void main(String[] args) {
		plotFunction(Sinus.SINUS);
		plotFunction(Cosinus.Cosinus);
//		plotFunction(Cosinus.CosinusMinus1);
		plotFunction((IFloatFunction) Langevin.Langevin);
		IDeriveAble airy = Sinus.getAiry(new BodyDouble(10));
		plotFunction((IFloatFunction) airy);
		double large;
		large = 1; plotFunction(new Brillouin(large));
		large = 2; plotFunction(new Brillouin(large));
		large = 4; plotFunction(new Brillouin(large));
		while (++state <= 10) {
			plot(); }
	}

	/** 
	 * @param f
	 */
	private static void plotFunction(final IFloatFunction f) {	//Uses g2D and MathGraph2
		Rectangle Bounds = new Rectangle(0, 0, 9, 9);
		float xMin = (float) (-1.5*Math.PI);	//define the x-Range and Function
		float xMax = (float) (+1.5*Math.PI);	//define the x-Range and Function
		Coordinates2D_ CD = new Coordinates2D_(xMin, xMax, xMin, xMax, Bounds);	//define the Coordinate System
		float[] fRaster = CD.MapX.createFineRaster(); //get the fine Raster
		float[][] Raster = { fRaster, null};
		Raster[1] =  VectorFloat.SAMPLE(f, Raster[0]);
//		float[] Sample = new float[](f, Raster);	//Sample the Function on the fine Raster
//		float yMin = -1;	//define the x-Range and Function
//		float yMax = +1;	//define the x-Range and Function
		float[] MinMax = VectorFloat.MIN_MAX_VAL(Raster[1]);
		CD = new Coordinates2D_(xMin, xMax, MinMax[0], MinMax[1], Bounds);	//define the Coordinate System
		Polygon3D P3D = new Polygon3D(Raster); //Display the Sample.
		P3D.getPolygon2D(CD);	//you can save calculating the x Values here.
		System.out.println(Raster[1]);
	}

	private static void plot() {
		final int numLorentz = 999;
//		float[][] LorentzPolygon = new float[numLorentz][];
		//Coordinates for the Lorentz Polygon
		IFunction Project = new Projections(Projections.Mollweide);	//Project);
		Body3D Body3 = null;
		AGraphText g2D = null;
		Rectangle Bounds = new Rectangle(0, 0, 100, 100);
		float[] Direction;
		double[] VP ={2.0, 4.0, 6.0}; // ={0.0, 0.0, 0.0}; for paint3
//		double[] SP ={0.5, 0.5, 0.5}; // ={5.0, 5.0, 5.0}; for paint3

		float[] ViewPoint  = VectorFloat.COPY(VP);
//		float[] standPoint = VectorFloat.copy(SP);;

		double[] start = { 0.1,  0.1,   0.1};
		double[] stand = {50.0, 75.0, 200.0};
		double[] visier= { 0.0,  0.0,  40.0};
	//	double tmp = );

		float[] Start = VectorFloat.COPY(start);
		float[] Stand = VectorFloat.COPY(stand);
		float[] Visier= VectorFloat.COPY(visier);

//		float[] scanPoint = VectorFloat.copy(SP);

		Coordinates3D  C3D = null;
		Coordinates2D_ C2D = null;

/*		double Angle = 0.01;
		double turnAngle;
		
		int xOld;
		int yOld;
*/
//		Bounds = g.getClipBounds();
//		g.clearRect(Bounds.x, Bounds.y, Bounds.x+Bounds.width, Bounds.y+Bounds.height);
//		g2D = new JavaGraphic(g);
//		g2D = new Graph2D(g);
//		Image img2 = img; if (img2 == (img =  g2D.doubleBuffer(this, img))) return;

//		int i = -1; while (++i < 3) VP[i] += 0.1;
//		float[] Location = new float[](VP);

		//Scalar Field Variable
		Object S = null;
		Figures3D F3D = null;

		//Scalar Function on a Raster
		//A Collection of Rasters in x, y, z Direction is not a Vector!!!
		float[][] R =  {{0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f},
						{0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f},
						{0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f}};

		float[] V1 = null;
		float[] V2 = null;
		float[] One = null;

		double[]v1 = {0, 0, 0};
		double[]v2 = {1, 1, 1};
		V1	= VectorFloat.COPY(v1);
		V2	= VectorFloat.COPY(v2);
		StreamTokenizer ST;

		float[][] Raster3D = new float[3][];
		float[][] Raster2D = new float[2][];
		if ((state < 8) || (state > 9)) {
			Direction = VectorFloat.NEG(ViewPoint);	//look at the Origin.
			C3D = new Coordinates3D(ViewPoint, Direction, Bounds);

//			Col = new Column3D (g2D, c3D);
//			F3D = new Figures3D(g2D, c3D);

			One	= VectorFloat.COPY(v2);
//			g2D.setColor(Color.gray);
			V2[1] = V1[1];
			V2[2] = V1[2];
			V2[0] *= 1.1;

/*			PlotVector VP = new PlotVector(g2D, c3D);

			VP.draw (V1, V2); V2[1] = V2[0]; V2[0] = V1[0];
			VP.draw (V1, V2); V2[2] = V2[1]; V2[1] = V1[1];
			VP.draw (V1, V2);
*/
			Raster3D[0]	= R[0];
			Raster3D[1]	= R[1];
			Raster3D[2]	= R[2];
			Raster2D[0]	= Raster3D[0];
			Raster2D[1]	= Raster3D[1];
		}

		Color[] Palette1 = null;
		if (state < 2) {
			Palette1 = PaletteRGB.CYCLE_PALETTE(255);
			int i = 0; while ((i += 20) < 255) Palette1[i] = Color.black;
			S = VectorFloat.SAMPLE(fSinProd.fSinProd, Raster2D);	//Sample a scalar Function on the Raster.
		}

		double[][]Points = {{0,0},
							{0,1},
							{1,1},
							{1,0}};
		int[][] Planes = {{0,1,2,3}};
		double[][]lTriAngle =  {{-4,-4},
								{+4,-4},
								{ 0,+4}};

		double[][]sTriAngle =  {{-1,-1},
								{+1,-1},
								{ 0,+1}};
		String FileName;
		Polygon3D Poly;
		switch (state) {
		case -10://Testing the Tube Mechanism:
				 Polygon3D lTri = new Polygon3D(MatrixFloat.COPY(lTriAngle)); lTri.periodic = true;
				 Polygon3D sTri = new Polygon3D(MatrixFloat.COPY(sTriAngle)); sTri.periodic = true;
				 Body3 = lTri.Tube(sTri.getPointsAt(), null);
				 break;
		case -9: Poly = new Polygon3D(MatrixFloat.COPY(Points));
				 Poly.periodic = false;
				 Body3 = Poly.Ribbon(0.2f);
				 break;
		case -8: Poly = new Polygon3D(MatrixFloat.COPY(Points));
				 Poly.periodic = true;
				 Body3 = Poly.Ribbon(0.2f);
				 break;
		case -7: Body3D Body2D = new Body3D(MatrixFloat.COPY(Points), Planes, false);
				 Body3 = Body2D.extrude(1);
				 break;
		case -6:
//				 paintRaster();
				 Polygon2D P2D = null;
				 Polygon3D P3D = null;
				 FileName = "G:\\Personal\\Databases\\Maps\\Pol\\AFRIKA0.POL";
				 try{
					ST = new StreamTokenizer(new FileInputStream(FileName));
					ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
					while (ST.ttype != StreamTokenizer.TT_EOF) {
						P3D = new Polygon3D(ST, true);
						float[][] Pts = P3D.getPointsAt();
						Project.Map(Pts);
						P2D = P3D.getPolygon2D(C2D);
						P2D.draw(g2D, false);
					}
				}catch (Exception t) {System.out.println (t);}
				 break;
		case -5: Body3 = new Body3D(MatrixFloat.COPY(Platonic.PointsCube        ), Platonic.PlanesCube        , true); break;
		case -4: Body3 = new Body3D(MatrixFloat.COPY(Platonic.PointsTetrahedron ), Platonic.PlanesTetrahedron , true); break;
		case -3: Body3 = new Body3D(MatrixFloat.COPY(Platonic.PointsDodecahedron), Platonic.PlanesDodecahedron, true); break;
		case -2: Body3 = new Body3D(MatrixFloat.COPY(Platonic.PointsIcosahedron ), Platonic.PlanesIcosahedron , true); break;
		case -1: Body3 = new Body3D(MatrixFloat.COPY(Platonic.PointsRauthedron  ), Platonic.PlanesRauthedron  , true); break;
		case  0:	//Sample Plane plot only on (x,y) Range
/*				PlanePlotter drwP = new PlanePlotter(g2D, c3D, R[0].length);
				drwP.ColorFactor= 255);
				drwP.fillMode = true;
				drwP.palette  = Palette1;
				c3D.PlotOrdered  (drwP, Raster2D, S);
*/				break;

		case 1:	//Sample Plane plot only on (x,y) Range
				ScalarPlanePlotter	SPP = new ScalarPlanePlotter(g2D, C3D, R[0].length, Palette1);
				SPP.ColorFactor= 255;
				SPP.fillMode = true;
				C3D.rasterOrdered(SPP, Raster2D, (Object[]) S);
				break;

		case 2:	//Sample Histogram plot of Scalar Field only on (x,y) Range
				S = VectorFloat.SAMPLE(fSinProd.fSinProd, Raster2D);	//Sample a scalar Function on the Raster.
				double[] dv = {0.1, 0.1, 0.0};
				float[] dV = VectorFloat.COPY(dv);
				HistoPlot drwH = new HistoPlot(g2D, C3D, dV);
				drwH.ColorFactor= 255;
				drwH.  Palette	= PaletteRGB.CYCLE_PALETTE(255);
				drwH.ColorMode	= true;
				C3D.rasterOrdered(drwH, Raster2D, (Object[]) S);
				break;

		case 3:
				//Sample Scalar plot on (x,y,z) Range
				S = VectorFloat.SAMPLE(fProduct.fProduct, Raster3D);	//Sample a scalar Function on the Raster.
				//S.mulAt(new ByRefDouble (255));
				ScalarPointPlot draw = new ScalarPointPlot(g2D, C3D);
				draw.ColorFactor= 255;
				draw.  Palette	= PaletteRGB.CYCLE_PALETTE(255);
				draw.	Radius	= 100;
				draw. SizeMode	= true;
				draw.ColorMode	= true;
				C3D.rasterOrdered  (draw, Raster3D, (Object[]) S);
				break;

		case 4: //Sample Vector plot only on (x,y,z) Range
				S = VectorFloat.SAMPLE(Identity.IDENTITY, Raster3D, null);	//Sample a scalar Function on the Raster.
//				S.mulAt(new ByRefDouble (0.125));
				PlotVector drawV = new PlotVector(g2D, C3D);
				drawV.ColorFactor= 768;
				drawV.  Palette = PaletteRGB.CYCLE_PALETTE(255);
				drawV.ColorMode = true;
				C3D.rasterOrdered  (drawV, Raster3D, (Object[]) S);
				break;

		case 5:	//Draqw the Unity Cube
//				Col.fillColumn3D(V1, One);	//Draw a grey Box in the unit Cube
				break;

		case 6:	//Draw all the Helper Lines of a Scan Point:
//				int [] project = {2, 2, 2};
//				Col.drawColumn3D(V1, One);	//Draw a WireFrame Box in the unit Cube
//				Col.drawPoint(V1, One, scanPoint, project);
//				F3D.drawPoint(V1, One, scanPoint, project);
				break;
		case 7:	//Draw a Menger Sponch
				int Fine = 27;//81; //242;//729;
				int[] MaxIndex = {Fine,Fine,Fine};
				Object[] Rstr3 = new Object[3];
				float d = 1.0f/Fine;
				float o = 0.0f;
				Rstr3[2] = Rstr3[1] = Rstr3[0] = VectorFloat.RASTER(o, d, Fine);
				VoxelPlot drwV = new VoxelPlot(g2D, C3D, MaxIndex);
				drwV.ColorFactor= 255;
				Color[][] Palette = {{Color.red}, {Color.green}, {Color.blue}};
				drwV.	Palette = Palette;
				drwV.	Radius	= 100;
//				c3D.rePlotIntersection = true;
//				c3D.PlotOrdered  (drwV, Rstr3, null);
//				c3D.rePlotIntersection = false;
				break;
		case 8: //tLorentz;
//				setAccuracyBits(10);
				C3D = new Coordinates3D(VectorFloat.COPY(Stand), Bounds, VectorFloat.COPY(Visier));
				double[] y = VectorDouble.COPY(Start);
				double Step = 1;
				double x    = 0;
				StepRK Stepper = new StepRK(Step, x, y, new OdeLorentz()); //TODO: was StepRKQ
				Point2D yP = C3D.mapPt(y);
//				g2D.P = yP;
				int n = 1000;
				while (--n > 0) {
					Stepper.step();
					yP = C3D.mapPt(Stepper.yv);
					g2D.drawLine(yP);
					//Plot_Int (Ableitungen,StartV,t_Bereich);
				}
				break;
		case 9: //tLorentz;
				//c3D = new Coordinates3D(Stand, Bounds, V1);//Visier);
				F3D = new Figures3D(g2D, C3D);
				VectorFloat.MUL_AT (V2, 40);
//				F3D.drawPoint(V2, V1);
//				Column3D Col3D = new Column3D(g2D, c3D);
//				Col3D.drawColumn3D(Stand, Visier);
				n = numLorentz;
//				yP = c3D.map(LorentzPolygon[numLorentz-1]);
//				g2D.P = yP;
//				while (--n > 0) g2D.drawLine(yP = c3D.map(LorentzPolygon[n]));
				break;
		case 10://Drawing the Stars
//				paintRaster();	//??
				FileName = "G:\\Personal\\Databases\\Astro\\FIXSTERN.DAT";
				try{
					ST = new StreamTokenizer(new FileInputStream(FileName));
					ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
					Body3 = new Body3D(ST, true, true, -1);
				}catch (Exception t) {System.out.println(t);}
				Object[] Pts = Body3.getPointsAt();
				Project.Map(Pts);	//scale the Points to the +- Pi Range
//				Body2D B2D = Body3.getBody2D(C2D);
//				B2D.mark(new Marker(g2D, Marker.StarMarker), false, false);
//				B2D.drawWire(g2D, true, false);
				break;
			default:
				if (Body3 != null) break;
				String Path = "E:\\Personal\\Databases\\POLYEDER\\";
				switch (state) {
				case 14: FileName = "Helicopter";	break;
				case 11: FileName = "Ship";			break;
				case 12: FileName = "STEGOSAU";		break;
				case 13: FileName = "TRICERAT";		break;
				default: FileName = String.valueOf(state - 15).trim();
				}
				FileName = Path + FileName + ".POL";
				try{
				ST = new StreamTokenizer(new FileInputStream(FileName));
				ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
				Body3 = new Body3D(ST, true, true, -1);
				switch (state) {
				case 10: Body3.scalePoints(1.0f/ 30); break;
				case 11: Body3.scalePoints(1.0f/200); break;
				}
				}catch (IOException t){
					t.printStackTrace(System.err); }
				break;
		}

		System.out.println(F3D);
		System.out.println(One); 

		if ((state > 9) || (state < 0)) {
			Body2D drawBody;
			if (Body3 != null) {
				drawBody = Body3.getBody2D(C3D);
				drawBody.borderColor = Color.black;
			//draw it from back to front
//				drawBody.setSequence(Body3.PlaneSequence((float[])c3D.getProjection()[0]));
//				drawBody.drawKonvex(g2D, true, true);	//filled Planes
//				drawBody.drawKonvex(g2D, true, false);	//wire Model
//				g2D.setColor(Color.black);
//				drawBody.drawWire(g2D);	//instead, use the Border Coloring of drawConvex
//				g2D.setFont(g2D.getFont(4,4));
//				g2D.drawPointNumbers(drawBody.getPoints());
//				g2D.setColor(Color.red);
//				g2D.drawPointNumbers(Body3.getMids(c3D));
//				g2D.setColor(Color.green);
//				F3D.drawArrow(Body3.getMidsAt()  , Body3.getPlaneNormsAt());
//				g2D.setColor(Color.blue);
//				F3D.drawArrow(Body3.getPointsAt(), Body3.getPointNormsAt());
			}
		}
	}
}
