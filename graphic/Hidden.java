package graphic;

import java.awt.Color;
import java.util.Arrays;

/**
 * Implements the Hidden Line Algorithm.
 * Drawing reserves a Space between the lowest and highest Coordinate.
 * If you draw from Front to Back, all hidden Lines and Pixels are not drawn.
 *
 * This is a Filter for the IGraph2DOut Interface.
 *
 * Design Decisions:
 * To avoid the costly indexed Array accesses,
 * temporary Min and Max Values are kept,
 * which are only updated when the x Coordinate changes.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:55:24Z
 * digest: 92f54315a926c97b63438daf7546f1d07ef8529d63248330e799b8c57468680b
 * stale: false
 * tags: [code/3d_rendering, code/geometry]
 * concepts: [Hidden Line Removal]
 * facets: {layer: domain, status: broken, complexity: medium}
 * -->
 */
final public class Hidden
extends AGraph2DOut {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Switches off updating the Buffer when setting Pixels	 */
	public boolean ignoreBuffer = true;
	
	/** the maximum x Coordinate, determines the Size of the Arrays */
	protected int XMax;

	/** lower Limits (per x Coordinate) for the Lines painted */
	protected int[] UG;

	/** upper Limits (per x Coordinate) for the Lines painted */
	protected int[] OG;

	/** Cached last Values for Optimizations */
	protected int TmpUG;
	/** Cached upper limit for {@link #TmpX}, see {@link #TmpUG}. */
	protected int TmpOG;
	/** x Coordinate the {@link #TmpUG}/{@link #TmpOG} cache currently applies to. */
	protected int TmpX;

	/** Reference to the Delegate Interface */
	protected IGraph2DOut GraphOut;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IGraph2DOut: Implementation by Delegation
	////////////////////////////////////////////////////////////////////////////
	
	/** Sets the color for the next painting Action */
	public void  setColor(final Color color_) {
		GraphOut.setColor(col = color_); 
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructor
	 * @param GraphOut_ the Graphics streamIO
	 * @param MaxX the maximum x Coordinate, determines the Array Sizes.
	 */
	public Hidden(final int MaxX, final IGraph2DOut GraphOut_) {
		this.GraphOut = GraphOut_;
		XMax = MaxX;
		UG = new int[XMax];
		OG = new int[XMax];
		reset(); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Resets the Buffers.
	 */
	public void reset() {
		if ((UG == null) || (OG == null)) 
			return;
		TmpX  = -1;
		TmpUG = Integer.MAX_VALUE;
		TmpOG = Integer.MIN_VALUE;
		Arrays.fill(UG, Integer.MAX_VALUE); //hopefully faster!
		Arrays.fill(OG, Integer.MIN_VALUE); //
/*		int i = -1;	while (++i < XMax) {
			UG [i] = Integer.MAX_VALUE;
			OG [i] = Integer.MIN_VALUE; }
*/	}

	/**
	 * Relaxation of the Borders
	 * by setting all Values to comparable Values as the one next to it.
	 * This is necessary to avoid abrupt Changes.
	 */
	public void relax() {
		int i = -1;
		while (++i < XMax) {	//1. Sweep: widen the Interval on the left Side
			if (UG[i] > UG[i+1]) { UG[i] = UG[i+1]; }
			if (OG[i] < OG[i+1]) { OG[i] = OG[i+1]; } }
		while (--i >= 0) {	//2. Sweep: widen the Interval on the right Side
			if (UG[i+1] > UG[i]) { UG[i+1] = UG[i]; }
			if (OG[i+1] < OG[i]) { OG[i+1] = OG[i]; } }
	}

	/**Update the temporary Coordinate with the accumulated temporary Borders
	 * and initialize the new temporary Coordinate x .	 */
	public void upDate  (final int x) {
		if (x == TmpX ) { return; }
		UG[TmpX] = TmpUG; TmpUG = UG[x];
		OG[TmpX] = TmpOG; TmpOG = OG[x];
		TmpX = x;
	}

	/**
	 * SetPixel Routine for the Hidden Line Algorithm.
	 * Conditionally sets the Pixel and updates the Buffer.
	 * @see graphic.IGraph2DOut#setPixel(java.awt.Color)
	 */
	public void setPixel(final Color color_) {
		setPixel(P.x, P.y, color_); 
	}

	/**
	 * SetPixel Routine for the Hidden Line Algorithm.
	 * Conditionally sets the Pixel and updates the Buffer.
	 * @see graphic.IGraph2DOut#setPixel(java.awt.Color)
	 */
	public void setPixel(final int x, final int y, final Color color_) {
		if (x >= XMax) { return; }
		if (x == TmpX) {
			if ((y <= TmpOG) &&
				(y >= TmpUG)) { return; }
		} else {
			if ((y <= OG[x]) &&
				(y >= UG[x])) { return; } }
		GraphOut.setPixel (x,y); 	//Paint the Pixel and update the Coordinates.
		if (ignoreBuffer) { return; }
		if (x != TmpX ) upDate (x);	//Change the temporary Coordinate
		if (y > TmpOG) { TmpOG = y; }
		if (y < TmpUG) { TmpUG = y; }
		GraphOut.setPixel(color_);
	}

}
