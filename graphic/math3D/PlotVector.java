package graphic.math3D;

import graphic.Figures;
import graphic.IGraphShape;
import graphic.Point2D;

import java.awt.Color;

import math.vector.VectorFloat;

/**
 * Encapsulates Drawing Vector Arrows in 2 or 3 Dimensions
 * Is to be called by the recursive Rastering Routine.
 * It gives a rastered View to the Vector Field e.g. of ODEs 	 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: bfcf629c2bc35bbe17281fb397428edd5e5ec50e88a8693e880ed58b12e80b21
 * stale: false
 * tags: [code/3d_rendering, code/geometry]
 * concepts: [Vector Field Plot Element]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class PlotVector
	extends ASpatial {

	/**Switches the Coloring of the Scalar Balls on	 */
	public boolean ColorMode;

	/**Scaling for the Color the Arrows are drawn, using ColorMode = true. */
	public float ColorFactor = 1;

	/**palette for Scalar Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	public Color[] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	private Figures Arrows;

	/**Graphics Context to point to	 */
	protected IGraphShape g;

	/**Reference to the Coordinate System for Conversion	 */
	protected ICoordMapper CD;

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing.  */
	public PlotVector(IGraphShape g2D, ICoordMapper CD) {
		this.CD = CD; g = g2D; Arrows = new Figures(g2D);}

	/**Draws the (assumed) Vector dV at the Position V
	 * with the Parameters given in the Constructor	 */
	public void moveTo(float[] V, Object dV) {
		float[] DV = (float[]) dV;
		Point2D P1 = new Point2D();
		Point2D P2 = CD.mapPt(V, DV, P1);	//here is a slight optimization, because in 2 Dimensions, the Dimensions stay
		if (ColorMode) {
			int c = (int) (VectorFloat.NORM_ABS(DV)*ColorFactor);
			if (Palette == null) {
				g.setColor(new Color(c + ColorOffset));
			} else {
				g.setColor(Palette [(c + ColorOffset) % Palette.length]); }
		}
		Arrows.drawArrow(P1, P2.subAt(P1));
	}

}
