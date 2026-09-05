package graphic.math3D;

import function.IMeasurAble;
import graphic.IGraphShape;

import java.awt.Color;

/**Helper Class that encapsulates Drawing a Vector Value in 3 Dimensions.
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 5eb1542c82861cd9f1f7709708e0de02a32a0feccacb57dc3673a53a1b03fbfd
 * stale: false
 * tags: [code/chart_rendering]
 * concepts: [Histogram Plotter]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class HistoPlot
	extends ASpatial {

	/**Switches the Coloring of the Bars on	 */
	public boolean ColorMode;

	/**Scaling for the Color the Bars are drawn when ColorMode = true. */
	public float ColorFactor = 1;

	/**palette for Scalar Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	public Color[] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	/**Graphics Context to paint to	 */
	private IGraphShape g;

	/**Reference to the Coordinate System for Conversion	 */
	private Coordinates3D CD;

	/**Drawing Routine for the Columns	 */
	private Column3D Col;

	/**Extent of the Columns, must not be null!	 */
	public float[] dV;

	/**Constructor, taking the Graphics- and the Coordinate System.
	 * The float[] dV contains the extents of the Histograms in x,y Direction
	 * and the Footing of the Histogram (if 0 is not wanted). 	 */
	public HistoPlot(IGraphShape g2D, Coordinates3D CD_, float[] dV_)
	{CD = CD_; g = g2D; Col = new Column3D(g2D, CD); dV = dV_;}

	/**Draws a Histogram at Position V with Height S and the Extents from dV.
	 * With ColorMode = true the Color is determined by S too. 	 */
	public void moveTo(float[] V, Object S) {
		float[] Z = new float[3];
		V[2] = ((IMeasurAble) S).getFloat();
		Z[2] = dV[2];
		Z[0] = dV[0] + V[0];
		Z[1] = dV[1] + V[1];
		if (ColorMode) {
			int c = (int) (ColorFactor * V[2]);
			if (Palette == null) {
				g.setColor(new Color(c + ColorOffset));
			} else {
				g.setColor(Palette [(c + ColorOffset) % Palette.length]); }
		}
		Col.fillColumn3D(V, Z);
	}

	/**Resets the given Coordinate of the Algorithm	 */
	public void reSet(int dim){}

}
