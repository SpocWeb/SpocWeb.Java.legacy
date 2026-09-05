package graphic.math3D;

import graphic.Point2D;
import graphic.math2D.Raster;

import java.awt.Rectangle;

import math.matrix.MatrixFloat;
import math.vector.VectorDouble;
import math.vector.VectorFloat;

/**This Class encapsulates 3 dimensional Mapping to 2 Dimensions.
 * This Implementation is targeted for simple 2D and 3D Vectors,
 * but it uses the Methods from float[], because there the Algorithm
 * becomes much clearer.
 * Because the final 2D integer Translation is not incorporated there,
 * it is translated to the Center of the ViewPort here.
 *
 * Design Decisions:
 * The Distances of the Points are only calculated for projective Views,
 * they could, by setting calcZ to true in Projection.
 * The Distance to the Plane would then be rather
 * the Means of the Distances of the Points ( N) than
 * the Distances of the Means of the Points (3N, Middles).
 * But since the Middles are calculated mostly anyway
 * and their Distance is also easily calculated (AbsV)...
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: c0c91d6e296b87203d94eb9afba07b7ca5262575dd7c71e1d7503584e843d465
 * stale: false
 * tags: [code/coordinate_transform, code/3d_geometry]
 * concepts: [3D Coordinate Transform]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class Coordinates3D
	implements ICoordMapper {

	/**Projection Object, calculated from the Rotation Vector */
	private Projection projector;

	/**Location the ViewDirection points to on the Screen	 */
	private Point2D Origin;

	/**Boolean Switch for Indication of Projection	 */
	public boolean project;

	/**Contains the Inverse of last Mapping's z Coordinate, if project = true.	 */
	public float zCoordInv(){ return projector.zCoordInv; }

	/**Get Procedure, still dangerous, because you could modify P */
	public Projection getProjector() { return projector; }

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Constructor that takes the StandPoint, the ViewPoint and the Ranges.
	 * Projective Geometry is switched off, since the Scaling Factor is given
	 * by the Ratio of the Target RectAngle Diagonal
	 * and the maximum Extent of Objects within the Box given by the Line.	 */
	public Coordinates3D(float[] StandPoint, Rectangle Target, Line Origin, float[] ViewPoint) {
		this (StandPoint, VectorFloat.SUB(ViewPoint, StandPoint), Origin, Target);}

	/**Constructor that takes the current StandPoint, the Direction and the Ranges.
	 * Projective Geometry is switched on, since no original Length is specified.	 */
	public Coordinates3D(float[] StandPoint, float[] ViewDirection, Line Origin, Rectangle Target) {
		}/**TODO: Implement this	  */

	/**Constructor that takes the StandPoint, the ViewPoint and the Ranges.
	 * Projective Geometry is switched on, since no original Length is specified.
	 * Scaling fits the Diagonal of the Target RectAngle to the Distance to the ViewPoint.	 */
	public Coordinates3D(float[] StandPoint, Rectangle Target, float[] ViewPoint) {
		this (StandPoint, VectorFloat.SUB(ViewPoint, StandPoint), Target);}

	/**Constructor that takes the current StandPoint, the Direction and the Ranges.
	 * Projective Geometry is switched on, since no original Length is specified.
	 * Scaling fits the Diagonal of the Target RectAngle to the Distance to the ViewPoint.	 */
	public Coordinates3D(final float[] standPoint, final float[] viewDirection, final Rectangle Target) {
		project = true;
		projector = new Projection(standPoint, viewDirection, true,
			(Math.abs(Target.width) +
			Math.abs(Target.height) ) >> 2,
			0);
		Origin = new Point2D(
			Target.x + (Target.width  >> 1),	//Place the Direction
			Target.y + (Target.height >> 1));	//into the Middle of the ViewPort
	}


	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ICoordMapper: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**Maps the Coordinates to the Target Range.	 */
	/*public Point2D map(double x, double y, double z) {
		double[] V = {x, y, z};
		return map(V); }

	/**Maps the Coordinates to the Target Range.	 */
	/*public Point2D map(double[] v) {
		return map((float[]) v); }
	*/
	/**Maps the Arrow (Start, Start+delta)
	 * to the 2-dimensional Points (StartPt, map())	 */
	public Point2D mapPt(float[] Start, float[] delta, Point2D StartPt) {
		//maybe there is some Optimization here, but for now it seems too complicated
		StartPt.setLocation(mapPt(Start));
		return mapPt(VectorFloat.ADD(Start, delta)); }

	/**Maps the Arrow (start, start+delta)
	 * to the 2-dimensional Points (StartPt, map())	 */
	public Point2D mapPt(double[] start, double[] delta, Point2D StartPt) {
		//maybe there is some Optimization here, but for now it seems too complicated
		StartPt.setLocation(mapPt(start));
		return mapPt(VectorDouble.ADD(start, delta)); }

	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public Point2D mapPt(final float[] V) {
		return processMapPt(projector.map(V));	} //2-dimensional Projection

	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public Point2D mapPt(double[] V) {
		return processMapPt(projector.map(V));	} //2-dimensional Projection
	
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	private Point2D processMapPt(final float[] z) {
		if (z == null) {
			return null; }
		final Point2D pt = new Point2D();
		pt.setX(Origin.getX() + (int) z[0]);
		pt.setY(Origin.getY() - (int) z[1]);	//Account for the change in Direction
		return pt; }
	
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[][] map(final double[][] v) {
		return map(null, v);	} //2-dimensional Projection
				
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[][] map(final float[][] v) {
		return map(null, v);
	} 
			
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[][] map(short[][] ret, final double[][] v) {
		if (ret == null) {
			ret = new short[v.length][v[0].length]; }
		for (int i = ret.length; --i >= 0; ) { // 
			ret[i] = map(ret[i], v[i]); }
		return ret;	} //
		
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[][] map(short[][] ret, final float[][] v) {
		if (ret == null) {
			ret = new short[v.length][v[0].length]; }
		for (int i = ret.length; --i >= 0; ) { // 
			ret[i] = map(ret[i], v[i]); } //also feed back not mapped Points out of Range!
		return ret;	} //
		
	
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[] map(final double[] v) {
		return processMap(null, projector.map(v));	} //2-dimensional Projection
		
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[] map(final float[] v) {
		return processMap(null, projector.map(v));
	} 
	
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[] map(final short[] ret, final double[] v) {
		return processMap(ret, projector.map(v));	} //2-dimensional Projection
			
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	public short[] map(final short[] ret, final float[] v) {
		return processMap(ret, projector.map(v));
	} 
	
	/** Switches on Skipping Points that lie in the Viewer's Back	*/
	public boolean skipNegativePoints = false;
	
	/**Maps the Coordinates of the transformed Vector to the Target Range.
	 * All other mapping Methods are delegated to this one!
	 * The z Coordinate is mapped into Integer Range also!	 */
	private short[] processMap(short[] ret, final float[] z) {
		if (z == null) {
			return null; }
		//if (returnVector) V.shallowCopyAt(Z);	//return the transformed Vector in Place, not needed!
		if (skipNegativePoints && (z[2] <= 0)) { //ignore Values in the Back of the Viewer...
			return null; }
		z[2] = (float) Math.log(Math.abs(z[2])); //TODO: rescale with a representative Factor. 
		for (int i = 2; --i >= 0;) { //check whether all Coords... 
			final float scale = (Short.MAX_VALUE-1000)/Math.abs(z[i]);
			if (scale < 1) { //...are in Range
				if (skipNegativePoints) {
					return null; } //rather scale back:
				for (int j = 2; --j >= 0;) { //
					z[j]*=scale; }
			}
		}
		if (ret == null) {
			ret = new short[z.length]; }
		ret[0] = (short)(Origin.getX() + z[0]);
		ret[1] = (short)(Origin.getY() - z[1]);	//Account for the change in Direction
		for (int i = z.length; --i >= 2;) { //check whether in Range
			ret[i] = (short) z[i];	//Account for the change in Direction
		}
		//if (Math.abs(ret[0])+Math.abs(ret[1]) < 25) {
		//	System.out.println(); } //only for detecting pathological Situations!
		return ret; }
		
	/**Maps the Coordinates to the Target Range,
	 * the alternative to map the coordinates independently, is no longer valid.
	 * There is little optimization possible by integrating the Coordinates
	 * into a single float[].
	 * The Sequence is determined by the z-Distances to the ViewPoint.
	 * You could as well use the overall Distance,
	 * but the z-Distance is calculated anyway (on projective Mapping)	 */
	public Point2D[] mapPt(float[][] v) {
		int Length = v.length;
		Point2D[] ret = new Point2D[Length];
		while (--Length >= 0) {
			ret[Length] = mapPt(v[Length]); }
		return ret; }

	/**Maps the Coordinates to the Target Range,
	 * the alternative to map the coordinates independently, is no longer valid.
	 * There is little optimization possible by integrating the Coordinates
	 * into a single float[].
	 * The Sequence is determined by the z-Distances to the ViewPoint.
	 * You could as well use the overall Distance,
	 * but the z-Distance is calculated anyway (on projective Mapping)	 */
	public Point2D[] mapPt(double[][] v) {
		int Length = v.length;
		Point2D[] Return = new Point2D[Length];
		while (--Length >= 0)
			Return[Length] = mapPt(v[Length]);
		return Return; }

	/**Maps the Coordinates to the Target Range,
	 * the alternative to map the coordinates independently, is no longer valid.
	 * There is little optimization possible by integrating the Coordinates
	 * into a single float[]. */
	public Point2D[] mapPt(float[] x, float[] y, float[] z) {
		Point2D[] Pt = new Point2D[x.length];
		float[] V = new float[3];
		int i = -1;
		while (++i < x.length) {
			V[0] = x[i];
			V[1] = y[i];
			V[2] = z[i];
			Pt[i] = mapPt(V);
		}
		return Pt; }

	/////////////////////////////////////////////////////////////////////////////////////

	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 *
	 * The Difference to the Plot Routine is
	 * that it paints in the Direction to the ViewPoint,
	 * so the nearer Points hide the ones far out.
	 * @param Figure The Drawing Routine performed at each Position
	 * @param R the Raster, a List of Positions to process in each Dimension
	 * @param S optional, a nested Array of Values to be plotted.
	 */
	public void rasterOrdered(final ISpatial figure, final double[][] R, final Object[] S) {
		Raster.rasterOrdered (projector.getStart(), figure, MatrixFloat.COPY(R), S); }

	/**Paints a Raster of Points on the Raster contained in the Vectors of R;
	 *
	 * The Difference to the Plot Routine is
	 * that it paints in the Direction to the ViewPoint,
	 * so the nearer Points hide the ones far out.
	 * @param Figure The Drawing Routine performed at each Position
	 * @param R the Raster, a List of Positions to process in each Dimension
	 * @param S optional, a nested Array of Values to be plotted.
	 */
	final public void rasterOrdered(final ISpatial figure, final float[][] R, final Object[] S) {
		Raster.rasterOrdered(projector.getStart(), figure, R, S);}	//Could be static, if the ViewPoint would be supplied!

}
