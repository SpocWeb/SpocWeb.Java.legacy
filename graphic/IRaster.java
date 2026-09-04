package graphic;

import graphic.example.Apple;
import graphic.example.Plasma;

/**
  * Title: IRaster<p>
  * Description:
  * Defines the Interface for calculating the Values on a refining Raster.
  * namely the Method setRaster
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  * @see Plasma
  * @see Apple
  *
  * Known Uses:
  * @see 
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-07-2002, 09:55 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IRaster
extends IPoint2DFunction {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Called whenever the Raster is refined 
	 * to synchronize (stateful) Calculation Routines 
	 */
	public void setRaster(int RasterSize, int Mask_, int[][] Picture); //Point2D Width_);

}

