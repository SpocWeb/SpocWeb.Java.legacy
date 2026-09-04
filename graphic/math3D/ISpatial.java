package graphic.math3D;

/**
 * Interface that defines Methods of Figures that need to be rastered 
 * and drawn into a GUI or placed into or evaluated at a Position. 
 */
public interface ISpatial {

	/**
	 * Draws the given Value S (as an Object it may be a Scalar or a Vector)
	 * at the given Vector Position V.
	 */
	public void moveTo(final float[] V, final Object S);

	/**
	 * Draws the given Value S (as an Object it may be a Scalar or a Vector)
	 * at the given Vector Position V.
	 * @param start Flag whether this is the first Call on Rastering in the given Dimension
	 */
	public void moveTo(final float[] V, final Object S, final boolean[] start);

	/**
	 * Draws the given Value S (as an Object it may be a Scalar or a Vector)
	 * at the given Vector Position V.
	 * @param index a Multi-Index into a Raster
	 */
	public void moveTo(float[]  V, Object S, int[] Index);

	/**
	 * Resets the given Coordinate of the Algorithm.
	 * Corresponds to setting the Flag start[dim] = true
	 */
	public void reSet(int dim);

}
