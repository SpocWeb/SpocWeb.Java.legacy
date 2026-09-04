package graphic.math3D;

import graphic.AGraph2D;

import java.awt.Color;

/**Helper Class that encapsulates Drawing a Polygon in 2 or 3 Dimensions.
 * Caches the mapped Points for a faster Redraw.  */
public class PolygonPlot {

	/**palette for Scalar Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	public Color[] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	/**Storage for the Points	 */
	public float[][] Pts;

	/**Constructor, taking the Graphics- and the Coordinate System.
	 * The float[] dV contains the extents of the Histograms in x,y Direction
	 * and the Footing of the Histogram (if 0 is not wanted).	 */
	public PolygonPlot(AGraph2D g2D, ICoordMapper CD_, float[][] Pts_) {
		Pts = Pts_; }

}
