/*
 * File Name: IPalette.java
 * Created on: 29.12.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Title: IPalette<p>
 * Description:
 * Allows to determine the Color of a Pixel 
 * based on arbitrary complex Parameters encoded in a short[] Array. 
 * This can contain e.g.
 * - a simple Index
 * - RGB Values
 * - u,v Coordinates of a Texture Mapping 
 * - Normals or their Cosinusses for Phong or Gouraud Shading 
 * - z-Values for Fog Simulation or ambient Light
 * - etc. 
 *
 * @see graphic.ISimplePalette which takes only a simple integer Parameter
 *
 * Known Implementations: 
 * @see graphic.PaletteRGB
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IPalette {

	/**
	 * @param c a complex Parameter could consist of
	 * - a simple Index
	 * - RGB Values
	 * - u,v Coordinates of a Texture Mapping 
	 * - Normals or their Cosinusses for Phong or Gouraud Shading 
	 * - z-Values for Fog Simulation or ambient Light
	 * - Positions and Distances to the Light Source for Shading 
	 * - etc. 
	 * @return the selected Color determined by this Palette
	 */
	public Color getColor(final short[] c);	//

}
