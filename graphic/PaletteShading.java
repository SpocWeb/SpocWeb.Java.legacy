/*
 * File Name: PaletteShading.java
 * Created on: 11.12.2003
 *
 */
package graphic;

import java.awt.Color;

/**
 * Derives shaded variants of a single, dynamically replaceable base color.
 *
 * <p>Typically used for Phong shading on the graphics level.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see ISimplePalette
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:50:12Z
 * digest: 5d6a9746f6b4342a47a494c37b52b6516aa8537295c668cc6caf5ae173c44add
 * stale: false
 * tags: [code/color_palette]
 * concepts: [Shading Palette]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
final public class PaletteShading implements ISimplePalette {

	/** Red component of the base Color. */
	private int r;
	/** Green component of the base Color. */
	private int g;
	/** Blue component of the base Color. */
	private int b;
	/** Alpha component of the base Color. */
	private int a;

	/** Factor to divide the multiplied Color with */
	private int maxShade;

	/**
	 * Replaces the Color this palette shades from, so the Color can be
	 * exchanged in the background without having to access the actual
	 * painter.
	 *
	 * @param color the new base Color
	 * @param maxShade_ the new shading divisor, see {@link #maxShade}
	 */
	public synchronized void setColor(final Color color, final int maxShade_) {
		maxShade = maxShade_;
		int rgb = color.getRGB();
		b = rgb & 0xFF; rgb >>= 8;
		g = rgb & 0xFF; rgb >>= 8;
		r = rgb & 0xFF; rgb >>= 8;
		a = rgb;
	}

	/**
	 * Creates a palette shading the given base color down to {@code maxShade}
	 * discrete levels.
	 *
	 * @param color the base Color
	 * @param maxShade the shading divisor, see {@link #maxShade}
	 */
	public PaletteShading(final Color color, final int maxShade) {
		setColor(color, maxShade);
	}

	/**
	 * Scales the base color's RGB components by {@code c / maxShade}, keeping
	 * the original alpha.
	 *
	 * @see graphic.ISimplePalette#getColor(int)
	 */
	public synchronized Color getColor(final int c) {
		// TODO: LOGIC: division by maxShade with no guard against maxShade == 0;
		// setColor()/the constructor accept any int, and a caller passing
		// maxShade_ == 0 here throws ArithmeticException on every getColor() call.
		return new Color((r*c)/maxShade, (g*c)/maxShade, (b*c)/maxShade, a);
	}

}
