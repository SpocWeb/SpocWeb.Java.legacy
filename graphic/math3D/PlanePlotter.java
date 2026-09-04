package graphic.math3D;

import function.IMeasurAble;
import graphic.IGraphShape;
import graphic.Point2D;

import java.awt.Color;

/**
 * Stateful Helper Class that encapsulates Drawing a Series of Planes in 3 Dimensions.
 * Called by the recursive Plot Routine in Raster.
 * Implements the ISpatial Interface for this!
 */
public class PlanePlotter
	extends ASpatial {

	/**Switches the Coloring of the Planes on	 */
	public boolean ColorMode;

	/**Switches filling of the Planes on	 */
	public boolean fillMode = true;

	/**Scaling for the Color the Arrows are drawn, using ColorMode = true. */
	public float ColorFactor = 1;

	/**palette for Scalar Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	public Color[] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	/**Graphics Context to point to	 */
	protected IGraphShape g;

	/**Reference to the Coordinate System for Conversion	 */
	protected ICoordMapper C3D;	//only for 3D Representations!

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing. 	 */
	public PlanePlotter(IGraphShape g2D, ICoordMapper C3D_, final int NumXItems) {
		C3D = C3D_; g = g2D; P = new Point2D[NumXItems+2]; }

	/**Cache for the old Points	 */
	protected Point2D[] P;

	/**Four Point Polygon for painting the Plane directly	 */
	protected Point2D[] Plane = new Point2D[4];

	/**Index of the current x Position	 */
	protected int xIndex;

	/**Index of the current y Position	 */
	protected int yIndex;

	/**Indicator for connecting the next Point	 */
	protected boolean connect;

	/**Resets the given Coordinate of the Algorithm	 */
	public void reSet(int dim) {
		switch (dim) {
			case -3: break;	//should not happen
			case -2:  xIndex = 0; yIndex = -1; break;	//Resetting the (x) Row.
			case -1: connect = false; break;	//Resetting the (x) Row.
			case 0: xIndex = 0; yIndex++; break;	//Resetting the (x) Row.
			case 1: xIndex = 0; yIndex = -1; break;	//resetting the whole Plot Action, must happen last!
			case 2: break;	//should not happen
		}
	}

	/** draws the Vector dV at the Position V with the Parameters given in the Constructor	 */
	public void moveTo(float[] V, Object S) {
//		V.letGrad(2, true, false);
		V[2] = ((IMeasurAble) S).getFloat();
		if (ColorMode) {
			int c = (int) (ColorFactor*V[2]);
			if (Palette == null) {
				g.setColor(new Color(c + ColorOffset));
			} else {
				g.setColor(Palette [(c + ColorOffset) % Palette.length]); }
		}
		Point2D PNew = C3D.mapPt(V);
		
		//Connect this Point with the other points buffered.
		//For that you have to know the current Index and the total Number of Rasterpoints.
		//A fragile way is to count and rely on a rollover every N Items
		//A more stable way would be to indicate the rollover by a boolean
		//or even give the Index with every call. But this would require the Interface to change.
		Plane[0] = Plane[1]; Plane[1] = P[xIndex];
		Plane[3] = Plane[2]; Plane[2] = PNew;
		if ((xIndex > 0) && (yIndex > 0) && connect) { //connect it to the previous Points
			Color c = null;
			if (fillMode) {
				c = g.getColor(); g.fillPolygon(Plane);
					g.setColor(Color.black);
			}
			g.drawPolygon(Plane, true);
			if (fillMode) g.setColor (c);
		}	//2-dim: Needs caching of 1 Row and 1 Point, compare to 3-dim: 1 Plane, 1 Row and 1 Point
		P[xIndex++] = PNew; connect = true;
	}

}
