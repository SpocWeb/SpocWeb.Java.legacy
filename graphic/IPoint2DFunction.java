package graphic;

import graphic.example.Apple;
import graphic.example.Plasma;

/**
  * Title: IRaster<p>
  * Description:
  * Defines the Interface for calculating the Values on a refining Raster.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  * @see Plasma
  * @see Apple
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-07-2002, 09:55 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IPoint2DFunction {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * @return the Value for the given Point
	 */
	public int getValue(Point2D SF);

}

