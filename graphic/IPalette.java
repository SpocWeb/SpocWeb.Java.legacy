/*
 * File Name: IPalette.java
 * Created on: 29.12.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Determines the color of a pixel from arbitrarily complex parameters encoded
 * in a {@code short[]} array.
 *
 * <p>This can contain e.g. a simple index, RGB values, u,v coordinates of a
 * texture mapping, normals or their cosines for Phong or Gouraud shading,
 * z-values for fog simulation or ambient light, etc.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see graphic.ISimplePalette takes only a simple integer parameter
 * @see graphic.PaletteRGB a known implementation
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:49:39Z
 * digest: 403d4c4bedd687a711844c351121c8759aab401f1cd50193c3a00aafe1bd3dbc
 * stale: false
 * tags: [code/color_palette]
 * concepts: [Color Palette Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IPalette {

	/**
	 * Derives a color from an arbitrarily complex encoded parameter set.
	 *
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
