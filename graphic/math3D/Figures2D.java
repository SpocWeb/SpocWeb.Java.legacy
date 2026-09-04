package graphic.math3D;

import graphic.AGraph2D;
import graphic.Figures;
import graphic.Point2D;
/*
import Ring.*;
import Vector.*;
import ByRef.*;
import BaseCopy.*;
*/
/**Class drawing different Figures (Arrows etc.) */
public class Figures2D {

	/**Reference to the Coordinate System for Conversion	 */
	private Coordinates2D_ C2D;

	/**Graphics Context to point to	 */
	private AGraph2D g;

	/**Object to hand over the Draw Routine of an Arrow	 */
	private Figures Arrows;

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing.
	 */
	public Figures2D(AGraph2D g2D, Coordinates2D_ C2D_) {
		C2D = C2D_; g = g2D; Arrows = new Figures(g); }

	/**Draws the Vector dV at the Position V in the current Color	 */
	public Point2D drawArrow(float[] V, float[] dV)
	{	//The same code is replicated in VectorPlot, because it doesn't pay off to instantiate
		float[] DV = dV;
/*		if (ColorMode)
		{
			int c = ((countAble) ((GroupM)((MetricIRing)dV).AbsV()).mul(ColorFactor)).getInt();
			if (palette == null)g.setColor(new Color(c + ColorOffset));
			else				g.setColor(palette [(c + ColorOffset) % palette.length]);
		}
*/		Point2D P1 = C2D.mapPt(V);
		Point2D P2 = new Point2D((int) (DV[0]*C2D.MapX.getScale()),
								 (int) (DV[1]*C2D.MapY.getScale()));
		Arrows.drawArrow(P1, P2);	//Using the Scale saves doing the full Calculation two Times!
		return P2;
	}

}
