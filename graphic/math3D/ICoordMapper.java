package graphic.math3D;

import graphic.Point2D;

/**Interface that defines a Mapping Method from a float[] to 2D Coordinates.
 * Also calculating the Distance has to happen here!  */
public interface ICoordMapper {

	/**Maps the float[] to a 2-dimensional Point	 */
	public Point2D mapPt(float[] V);

	/**Maps the float[] to a 2-dimensional Point	 */
	public short[] map(float[] V);

	/**Maps the float[]s to 2-dimensional Points.
	 * Should also calculate the Sequence of Points.	 */
	public Point2D[] mapPt(float[][] V);

	/**Maps the Arrow (Start, Start+delta)
	 * to the 2-dimensional Points (StartPt, map())
	 * There is some Optimization in doing this both in 2D and 3D (planar) */
	public Point2D mapPt(float[] Start, float[] delta, Point2D StartPt);

	/**Maps the float[] to a 2-dimensional Point	 */
	public Point2D mapPt(double[] V);

	/**Maps the float[]s to 2-dimensional Points.
	 * Should also calculate the Sequence of Points.	 */
	public Point2D[] mapPt(double[][] V);

	/**Maps the Arrow (Start, Start+delta)
	 * to the 2-dimensional Points (StartPt, map())
	 * There is some Optimization in doing this both in 2D and 3D (planar) */
	public Point2D mapPt(double[] Start, double[] delta, Point2D StartPt);

}
