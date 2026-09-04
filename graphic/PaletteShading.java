/*
 * File Name: PaletteShading.java
 * Created on: 11.12.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Title: PaletteShading<p>
 * Description:
 * Defines a Palette based on one Color 
 * The returned Color is derived by shading the Color.
 * The Color can be changed dynamically. 
 * Typically used for Phong Shading on the Graphics Level. 
 *
 * Known SubClasses: <none>
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
final public class PaletteShading implements ISimplePalette {

	/** the different Color Components */
	private int r;
	private int g;
	private int b;
	private int a;
	
	/** Factor to divide the multiplied Color with */
	private int maxShade;
	
	/** The Color this Palette is based on
	 * Can be used to exchange the Color in the Background, 
	 * without having to access the actual Painter
	 */
	public synchronized void setColor(final Color color, final int maxShade_) {
		maxShade = maxShade_;
		int rgb = color.getRGB();
		b = rgb & 0xFF; rgb >>= 8;
		g = rgb & 0xFF; rgb >>= 8;
		r = rgb & 0xFF; rgb >>= 8;
		a = rgb;
	}

	/** initializing Constructor
	 * 
	 * @param color
	 * @param maxShade
	 */
	public PaletteShading(final Color color, final int maxShade) {
		setColor(color, maxShade);		
	}

	/** @see graphic.ISimplePalette#getColor(int)	 */
	public synchronized Color getColor(final int c) {
		return new Color((r*c)/maxShade, (g*c)/maxShade, (b*c)/maxShade, a);
	}

}
