package graphic.math3D;

import graphic.AGraph2D;
import graphic.Point2D;
import math.vector.VectorFloat;

/**Used for drawing Points as well as their Projections to the Coordinate System.
 * The Points are buffered, so the Projections can be interconnected.
 * This class can also be used to draw a 1 dimensional Parameter Plot.
 */
public class ProjectPlot {

	/**caching the Projection Points to connect those	 */
	private Point2D[] P;

	/**caching the Original Points to connect those	 */
	private Point2D P0;

	/**Reference to the Coordinate System for Conversion	 */
	private ICoordMapper CD;

	/**Graphics Context to point to	 */
	private AGraph2D g;

	/**Constructor, taking the Color Range and the Radius Factor,
	 * that control the coloring as well as the Sizing. 	 */
	public ProjectPlot(AGraph2D g2D, ICoordMapper CD_, float[] Origin) {
		CD = CD_; g = g2D; O = VectorFloat.COPY(Origin); }

	/**The Origin defining the Projection Planes	 */
	private float[] O;

	/**Switches the projection to each Dimension on	 */
	public boolean[] project;

	/**Switches the Connection in each Dimension on	 */
	public boolean[] connect;

	/**Draws the float[] V and optionally connects it to the previous Point.
	 * When V is null, the connection is also being broken.
	 */
	public Point2D drawPoint (float[] V, boolean connect) {
		Point2D P1 = CD.mapPt(V);
		if (connect)g.drawLine (P0, P1);
		else		g.setPixel (P1);
		P0 = P1;
		int i = -1;
		while (++i < V.length-1)
			if (project[i]) {	//Loop through all Dimensions
				float tmp = V[i]; V[i] = O[i]; //and calculate the Coordinates.
				P1 = CD.mapPt(V);
				if (this.connect[i] && (P[i] != null))
					 g.drawLine (P[i],	P1);
				else g.setPixel (		P1);
				V[i] = tmp;
				P[i] = P1;
			}
		return P1; }

}
