/*
 * File Name: ISimplePalette.java
 * Created on: 11.12.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Title: ISimplePalette<p>
 * Description:
 * Defines the Interface for a Function that maps an integer Value to a Color Object.
 * This could also be named IColorFunction
 *
 * Known Implementations: 
 * @see graphic.PaletteRGB
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface ISimplePalette {

	/**
	 * @param c index of the Color
	 * @return the selected Color determined by this Palette
	 */
	public Color getColor(final int c);	//

}
