package graphic;

import graphic.example.Apple;
import graphic.example.Plasma;

/**
  * Defines a function that computes an integer value for a given {@link Point2D}.
  *
  * <p>Implemented by point-sampling routines such as {@link Plasma} and {@link Apple},
  * and refined incrementally by {@link IRaster}.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-07-2002, 09:55 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see Plasma a known implementor
  * @see Apple a known implementor
  * @see IRaster refines this function over a raster
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:47:38Z
  * digest: 640c2d04a78c81ff824ac1c2b9b3678d5c05a26a6adeefdc26efb27d9dbbe3a5
  * stale: false
  * tags: [code/geometry, code/point_normal_calculation]
  * concepts: [Point Function Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IPoint2DFunction {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes this function's value at the given point.
	 *
	 * @return the Value for the given Point
	 */
	public int getValue(Point2D SF);

}

