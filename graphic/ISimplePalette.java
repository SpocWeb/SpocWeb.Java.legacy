/*
 * File Name: ISimplePalette.java
 * Created on: 11.12.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Maps an integer color index to a {@link Color} object.
 *
 * <p>Also known conceptually as an IColorFunction.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see graphic.PaletteRGB a known implementation
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:47:34Z
 * digest: 53de7ab98f8b45dc69aa228718f5db7d5d223549e4ba901e26912a1b16cc9ca6
 * stale: false
 * tags: [code/color_palette]
 * concepts: [Simple Color Palette Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface ISimplePalette {

	/**
	 * Maps the given index to a color.
	 *
	 * @param c index of the Color
	 * @return the selected Color determined by this Palette
	 */
	public Color getColor(final int c);	//

}
