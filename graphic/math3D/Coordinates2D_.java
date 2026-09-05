package graphic.math3D;

import graphic.Point2D;
import graphic.math2D.Coordinates2D;

import java.awt.Rectangle;

/**
 * Adapter from Coordinates2D to the 'ICoordMapper' Interface.
 * Extends the 'Coordinates2D' Class with Routines
 * that accept float[]s as Arguments
 *
 * An affine Coordinate Mapping is given by two Vectors,
 * which characterize the Mapping of an Interval to another.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 35bb23fae4f7957d1d20dcd8fd9214732158bf77cc9e77899c771336e2c2f216
 * stale: false
 * tags: [code/coordinate_transform]
 * concepts: [2D Coordinate Transform Variant]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class Coordinates2D_
extends Coordinates2D
implements ICoordMapper {

	/** Initializing Constructor	 */
	public Coordinates2D_(
		float xMin, float xMax,
		float yMin, float yMax, Rectangle Target) {
		super(xMin, xMax, yMin, yMax, Target);}

	/** Initializing Constructor	 */
	public Coordinates2D_(
		double xMin, double xMax,
		double yMin, double yMax, Rectangle Target) {
		super(xMin, xMax, yMin, yMax, Target);}

	/** Extension to allow for 2D float[]s to be handed over	 */
	public Point2D mapPt(float[] V) {
		return super.mapPt(V[0], V[1]);}

	/** Extension to allow for 2D float[]s to be handed over	 */
	public Point2D mapPt(double[] V) {
		return super.mapPt(V[0], V[1]);}

	/** Extension to map complete Arrays of 2D float[]s	 */
	public Point2D[] mapPt(double[][] V) {
		int Length = V.length;
		Point2D[] Return = new Point2D[Length];
		while (--Length >= 0)
			Return[Length] = super.mapPt(
				V[Length][0],
				V[Length][1]);
		return Return; }


	/** Extension to map complete Arrays of 2D float[]s	 */
	public Point2D[] mapPt(float[][] V) {
		int Length = V.length;
		Point2D[] Return = new Point2D[Length];
		while (--Length >= 0)
			Return[Length] = super.mapPt(
				V[Length][0],
				V[Length][1]);
		return Return; }

	/**Maps the Arrow (Start, Start+delta)
	 * to the 2-dimensional Points (StartPt, map())
	 * Design Decision:
	 * This Method saves one Translation in the Origin Space
	 */
	public Point2D mapPt(float[] Start, float[] delta, Point2D StartPt) {
		StartPt.setLocation(mapPt(Start));	//Using the Scale saves doing the full Calculation two Times!
		return new Point2D	(StartPt.getX() + (int) (delta[0]*MapX.getScale()),
							 StartPt.getY() + (int) (delta[1]*MapY.getScale()));
	}

	/**Maps the Arrow (Start, Start+delta)
	 * to the 2-dimensional Points (StartPt, map())
	 * Design Decision:
	 * This Method saves one Translation in the Origin Space
	 */
	public Point2D mapPt(double[] Start, double[] delta, Point2D StartPt) {
		StartPt.setLocation(mapPt(Start));	//Using the Scale saves doing the full Calculation two Times!
		return new Point2D	(StartPt.getX() + (int) (delta[0]*MapX.getScale()),
							 StartPt.getY() + (int) (delta[1]*MapY.getScale()));
	}

}
