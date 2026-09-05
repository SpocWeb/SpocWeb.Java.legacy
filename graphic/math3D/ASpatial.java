package graphic.math3D;

/**
 * Abstract Class that defaults some Methods of the ISpatial Interface.
 * Mostly needed Simplifications for the ISpatial Interface.
 * The Methods of ASpatial are mostly called recursively
 * to loop through Rasters in different Dimensions.
 * With known Index, the Parameters Start and even the float[] V
 * can be determined (with Index = 0 or max and the Raster R).
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: c59475f71809f5bfd76bcdc107833e67f861731c5cb6454e496d48b8def0ca10
 * stale: false
 * tags: [code/geometry, code/3d_geometry]
 * concepts: [3D Spatial Object Base Class]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public abstract class	ASpatial
	implements			ISpatial {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ISpatial: abstract Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Draws the given scalar Value S (as an Object it may even be a Vector)
	 * at the given Vector Position V.
	 */
	public abstract void moveTo (final float[] V, final Object S);

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ISpatial: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Draws the given scalar Value S (as an Object it may even be a Vector)
	 * at the given Vector Position V.
	 * @param start Flag whether this is the first Call on Rastering in the given Dimension
	 */
	public void moveTo (float[] V, Object S, boolean[] Start) {
		moveTo (V, S); }

	/**
	 * Draws the given scalar Value S (as an Object it may even be a Vector)
	 * at the given Vector Position V.
	 * @param index a Multi-Index into a Raster
	 */
	public void moveTo (float[] V, Object S, int[] index) {
		moveTo (V, S); }

	/**
	 * Resets the given Coordinate of the Algorithm.
	 * Corresponds to Start[dim] = true
	 */
	public void reSet(int dim) { }

}
