package graphic.math3D;

import function.IMeasurAble;
import graphic.IGraphShape;
import graphic.Point2D;
import graphic.ScalarPlotNew;

import java.awt.Color;

/**
 * Helper Routine to be called by the recursive Rastering Routine.
 * The Planes are filled with an interpolated Coloring determined by the palette. 	 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 192611d1dd35096220e5610c4f447927de0eaa31b84a6aa6ed5304eb561e9034
 * stale: false
 * tags: [code/3d_rendering, code/chart_rendering]
 * concepts: [Scalar-Colored Plane Plotter]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class ScalarPlanePlotter
	extends PlanePlotter {

	/**x Coordinates to hand over to the ScalarPlotNew Methods	 */	
	private int x[] = new int[4];
	/**y Coordinates to hand over to the ScalarPlotNew Methods	 */	
	private int y[] = new int[4];
	/**z Coordinates to hand over to the ScalarPlotNew Methods	 */	
	private int z[] = new int[4];

	/**Helper Class implementing the Scalar Plot.	 */
	private ScalarPlotNew SP;

	/**Cache for the z Value */
	private int[] zP;

	/** Constructor, the palette can be null.	 */
	public ScalarPlanePlotter(final IGraphShape g2D, final ICoordMapper C3D_, final int NumXItems, final Color[] Palette) {
		super(g2D, C3D_, NumXItems); SP = new ScalarPlotNew(g2D, Palette); zP = new int[NumXItems+2]; }

	/**draws the Vector dV at the Position V with the Parameters given in the Constructor
	 * fillMode decides whether a Middle Point is used to fill the Polygons.
	 * The Planes are filled with an interpolated Coloring determined by the palette. 	 */
	public void moveTo(final float[] V, final Object S) {
		final Point2D PIndex;
//		V.letGrad(2, true, false);
		V[2] = ((IMeasurAble) S).getFloat();
		int c = (int) (ColorFactor*V[2]);
		final Point2D PNew = C3D.mapPt(V);
		if ((PIndex = P[xIndex]) != null) {
			x[0] = x[1]; x[1] =  PIndex.getX(); x[3] = x[2]; x[2] = PNew.getX();
			y[0] = y[1]; y[1] =  PIndex.getY(); y[3] = y[2]; y[2] = PNew.getY();
			z[0] = z[1]; z[1] =zP[xIndex]; z[3] = z[2]; z[2] = c;
		}
		if ((xIndex > 0) && (yIndex > 0) && connect) {	//connect it to the previous Points
			if (this.fillMode)	SP.ScalarMidPolygon	(x, y, z);
			else				SP.ScalarPolygon	(x, y, z);	//2-dim: Needs caching of 1 Row and 1 Point, compare to 3-dim: 1 Plane, 1 Row and 1 Point
			g.setColor(Color.black); g.drawPolygon	(x, y, true);	//draw the Frame around the Polygon.
		}
		 P[xIndex  ] = PNew; connect = true;
		zP[xIndex++] = c;
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sample Plane plot only of S filled with fSinProd = sin(x)*sin(y) 
	 * on (x,y) Range with Coloring! 
	 * @param g2D
	 * @param S
	 * @param R
	 * @param Raster2
	 * @param Palette1
	 */
	public void testScalarPlanePlot(
		IGraphShape g2D,
		Object S,
		double[][] R,
		float[][] Raster2,
		Color[] Palette1) {
		ScalarPlanePlotter	SPP = new ScalarPlanePlotter(g2D, C3D, R[0].length, Palette1);
		SPP.ColorFactor= 255;
		SPP.fillMode = true;
		((Coordinates3D) C3D).rasterOrdered(SPP, Raster2, (Object[]) S); //hand over the Raster for Painting
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ScalarPlanePlotter.class.getName());
		
		//testScalarPlanePlot(g2D, S, R, Raster2, Palette1); break;
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws Exception {
		testIt(args); 
	}
	
}
