/*
 * File Name: testMathGraph2.java
 * Created on: 13.12.2003
 *
 */
package graphic.math3D;

import function.IFloatFunction;
import function.Projections;
import function.derive.ring.body.Brillouin;
import function.derive.ring.body.Cosinus;
import function.derive.ring.body.Exponential;
import function.derive.ring.body.Logarithm;
import function.derive.ring.body.Sinus;
import function.vector.IFloatVectorField;
import graphic.Body2D;
import graphic.IGraphShape;
import graphic.IGraphText;
import graphic.Marker;
import graphic.PaletteRGB;
import graphic.Point2D;
import graphic.PolyTrigon;
import graphic.Polygon2D;
import graphic.mvc.BaseApplet;
import graphic.mvc.IPainter;
import graphic.mvc.plane2D.MatrixShort;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.StreamTokenizer;

import math.vector.VectorFloat;
import math.vector.VectorShort;

/**
 * Title: testMathGraph2<p>
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
 * mtime: 2026-09-05T12:43:54Z
 * digest: a555733308787286d2372e2b3e51ad11c8708389026984a8ad574214b255f5b9
 * stale: false
 * tags: [code/testing]
 * concepts: [3D Graph Demo/Test Harness]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class testMathGraph2
//	extends Body3DPainter
implements IPainter { //

	/**
	 * Default Constructor; all state is initialized by field defaults.
	 */
	public testMathGraph2() {
	}

	/** Drive for the Data */
	final static public String DataDrive = "E:\\";

	/** Base Folder for the Data */
	final static public String DataFolder = DataDrive + "MHeuer\\Databases\\";

	/** Path to the FixStar Map */
	final static public String AstroPath = DataFolder + "Astro\\";

	/** FileName of the FixStar List */
	final static public String StarFile = "FIXSTERN";

	protected Body3D Body3DG = null;

	protected Rectangle Bounds;

	Coordinates2D_ C2D = null;

	protected IFloatVectorField Project;

	/** Drawing the Stars read from a File
		 * @param g2D Graphics Context
		 */
	protected void testDrawStars(final IGraphShape g2D) {
			paintRaster(g2D);	//
	//		FileName = AstroPath;
			try{
	//			ST = new StreamTokenizer(new FileInputStream(FileName));
	//			ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
	//			Body3DG = new Body3DGraph(ST, true, true, true);
				Body3DG =  new Body3D(AstroPath + StarFile);
	//			new ResultSetSep(AstroPath + StarPicFile), -1, false, PointOffset, null); //throws SQLException {
	//		}catch (Exception t) {g.drawString(t.toString(), 100, 100);}
			}catch (Exception t) {
				System.out.println(t.toString());
				t.printStackTrace();
			}
			C2D = new Coordinates2D_(-180, +190, -90, +90, Bounds);
	/*		float[][] Pts = Body3DG.getPointsAt();
			Project.Map(Pts, Pts);	//scale the Points to the +- Pi Range
	*/		Body2D B2D = Body3DG.getBody2D(C2D);
			B2D.mark(new Marker(g2D, Marker.StarMarker), false, false);
			B2D.drawWire(g2D, true, false);
		}

	/** Paints a Test Line Raster with -Pi to Pi	 */
	protected void paintRaster(final IGraphShape g2D) {
	//		C2D = new Coordinates2D_(-180, +180, -90, +90, Bounds);
			C2D = new Coordinates2D_(-Math.PI, +Math.PI, -1.572f, +1.572f, Bounds);
			Project = new Projections(Projections.Deg2Rad);
			float[] Net = {-180, -90};
			float[] NetPt  = VectorFloat.COPY(Net);
			float[] NetPt2 = VectorFloat.COPY(NetPt);
			NetPt[0] = Net[0]-10;
			int l = -19;
			while (++l <= 18) {
				NetPt[0] += 10;
				NetPt[1] = Net[1]-10;
				int b = -10;
				while (++b <= 9) {
					NetPt[1] += 10;
					VectorFloat.COPY(NetPt, NetPt2);
					NetPt2 = Project.map(NetPt2, NetPt2);
					Point2D Pt = C2D.mapPt(NetPt2);
					if (b > -9){ 
						g2D.drawLine(Pt);
					} else { 
						g2D.moveTo(Pt); //
					}
				}
			}
	
	
			NetPt[1] = Net[1]-10;
			l = -10;
			while (++l <= 9) {
				NetPt[1] += 10;
				NetPt[0]  = Net[0]-10;
				int b = -19;
				while (++b <= 18) {
					NetPt[0] += 10;
					VectorFloat.COPY(NetPt, NetPt2);
					NetPt2 = Project.map(NetPt2, NetPt2);
					Point2D Pt = C2D.mapPt(NetPt2);
					if (b > -18) { 
						g2D.drawLine(Pt);
					 } else { 
						g2D.moveTo(Pt); //
					 }
				}
			}
		}

	/** Path to the Africa Map */
	public static String AfricaPath = DataFolder + "Maps\\Pol\\EUROPA0.POL";

	/**Testing the Raster and Mollweide Projection on a Map.
		 * doesn't work because reading the File failed.
		 * @param g2D
		 */
	protected void testMollweide(final IGraphText g2D) {
			StreamTokenizer ST;
			String FileName;
			paintRaster(g2D);
			Polygon2D P2D = null;
			Polygon3D P3D = null;
			FileName = AfricaPath;
	//		Project = new Projections(Projections.Deg2Rad); // .Mollweide);
	//		Coordinates2D_
			C2D = new Coordinates2D_(-180, +190, -90, +90, Bounds);
			try {
				ST = new StreamTokenizer(new FileInputStream(FileName));
				ST.eolIsSignificant(true);	//Otherwise it doesn't work Row-based!
				while (ST.ttype != StreamTokenizer.TT_EOF) {
					P3D = new Polygon3D(ST, true);
					P2D = P3D.getPolygon2D(C2D);
					P2D.draw(g2D, false);
					g2D.drawRect(P2D.getExtent());
	/*				float[][] Pts = P3D.getPointsAt();
					Project.Map(Pts, Pts); //test the Projections...
	*/			}
			}catch (Throwable t) { //expected for Files with
				System.out.println("Read Error in " + FileName + " Error: " + t.toString());
	//			g2D.drawString(t.toString(), 100, 100);
	//			t.printStackTrace();
			}
		}
	
	/**
	 * Plots some one dimensional Functions in 2D Coordinates
	 */
	public void paintFPlots2D(final IGraphText g2D) {
//		Image img2 = img; if (img2 == (img =  g2D.doubleBuffer(this, img))) return;
		g2D.setColor(Color.red); plotFunction(g2D, Sinus.SINUS);
//		plotFunction(g2D, new CosinusMinus1());
		g2D.setColor(Color.green); plotFunction(g2D, Cosinus.Cosinus);
//		plotFunction(g2D, new CosHMinus1());
//		plotFunction(g2D, Sinus.Sinc);
//		plotFunction(g2D, Langevin.Langevin);
//		IDeriveAble airy = Sinus.getAiry(new BodyDouble(10));
		double large;
		large = 1; g2D.setColor(Color.yellow); plotFunction(g2D, new Brillouin(large));
		large = 2; g2D.setColor(Color.yellow); plotFunction(g2D, new Brillouin(large));
		large = 4; g2D.setColor(Color.yellow); plotFunction(g2D, new Brillouin(large));
//		plotFunction(g2D, airy);
		g2D.setColor(Color.blue ); plotFunction(g2D, Exponential.EXPONENTIAL);
		g2D.setColor(Color.black); plotFunction(g2D, Logarithm.LOGARITHM);	//no scaling for negative x
		paintRaster(g2D);
	}

	/**
	 * Plots the given one dimensional Function in 2D Coordinates
	 * Called by paintFPlots2D
	 */
	private void plotFunction(final IGraphText g2D, final IFloatFunction f) {	//Uses g2D and MathGraph2
		float xMin = (float) (-3.0*Math.PI);	//define the x-Range and Function
		float xMax = (float) (+3.0*Math.PI);	//define the x-Range and Function
		Coordinates2D_ CD = new Coordinates2D_(xMin, xMax, xMin, xMax, Bounds);	//define the Coordinate System
		float[] fRaster = CD.MapX.createFineRaster(); //get the fine Raster
		float[][] Raster = { VectorFloat.COPY(fRaster), null };
		Raster[1] =  VectorFloat.SAMPLE(f, Raster[0]);
//		float[] Sample = new float[](f, Raster);	//Sample the Function on the fine Raster
//		float yMin = (float) -1.0;	//define the x-Range and Function
//		float yMax = (float) +1.0;	//define the x-Range and Function
		float[] MinMax = VectorFloat.MIN_MAX_VAL(Raster[1]); //returns the Indices of the Minimum and Maximum Values!
		CD = new Coordinates2D_(xMin, xMax, MinMax[0], MinMax[1], Bounds);	//define the Coordinate System
		Polygon3D P3D = new Polygon3D(Raster, false); //Display the Sample.
		P3D.getPolygon2D(CD).draw(g2D, false);	//you can save calculating the x Values here.
//		MathGraph2.createRaster
	}
	
	/** tests drawing a Triangle with continuous Scalar Value 	*/
	private static final void testFillScalarTriangle(final IGraphShape gText) {
		final short[][] triangle 
		= { {  0,  0,  0,  0,250},
			{ 50,200,250,  0,  0},
			{200, 50,  0,250,  0}
		};
		//gText.fillTriangle(triangle);
		final PaletteRGB palette = new PaletteRGB(); palette.colorOffset = 2;
		gText.fillPolygon(triangle, palette);
		gText.drawLine(triangle[0], triangle[1], palette);
	}

	private static void testDrawColorHexaGon(final IGraphShape gText) {
		final Point2D r = new Point2D(100, 100);
		final int numColors = 6; 
		final short[][] hexaGon = new short[numColors+2][5]; 
		PolyTrigon.RegPoly(PaletteRGB.HUE_COLORS, numColors, r, hexaGon);
		VectorShort.COPY_AT(hexaGon[numColors], hexaGon[0]); 
		MatrixShort.ADD_AT(hexaGon, new short[] { 200, 200});
		final PaletteRGB palette = new PaletteRGB(); palette.colorOffset = 2;
		gText.fillPolygon(hexaGon, palette);
	}
	
	/** tests drawing a Triangle with continuous Scalar Value 	*/
	final static public short[][] COLORED_POLYGON(final Polygon polygon, final Color[] colors) {
		return COLORED_POLYGON(polygon.npoints, polygon.xpoints, polygon.ypoints, colors); }
	
	/** tests drawing a Triangle with continuous Scalar Value 	*/
	final static public short[][] COLORED_POLYGON(final int numPoints, final int[] x, final int[] y, final Color[] colors) {
		final short[][] poly = new short[numPoints][5];
		for(int i = numPoints; --i >= 0;) {
			final short[] colPoint = poly[i];
			colPoint[0] = (short) x[i]; 
			colPoint[1] = (short) y[i]; 
			colPoint[2] = (short) colors[i].getRed(); 
			colPoint[3] = (short) colors[i].getGreen();
			colPoint[4] = (short) colors[i].getBlue();
		}
		return poly; 
	}

	/** Paints the scalar-triangle and color-hexagon test figures.
	 * @see graphic.mvc.IPainter#draw(graphic.IGraphText)	 */
	public void draw(final IGraphText gText) {
		testFillScalarTriangle(gText);
		testDrawColorHexaGon(gText);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) {
		BaseApplet.display(new testMathGraph2());
	}

}
