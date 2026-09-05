package graphic;

import graphic.example.Apple;
import graphic.example.Plasma;

/**
  * Extends {@link IPoint2DFunction} with a callback for progressively refining
  * raster resolutions, via {@link #setRaster(int, int, int[][])}.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-07-2002, 09:55 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see Plasma a known implementor
  * @see Apple a known implementor
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:47:48Z
  * digest: cccbead50b83d86f18c190bc344c1933c48069df78129f2f15503d766c6558cc
  * stale: false
  * tags: [code/geometry, code/2d_geometry]
  * concepts: [Raster Generation Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IRaster
extends IPoint2DFunction {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Notifies this raster that it was refined, so stateful calculation
	 * routines can resynchronize with the new resolution and pixel data.
	 */
	public void setRaster(int RasterSize, int Mask_, int[][] Picture); //Point2D Width_);

}

