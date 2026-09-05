package graphic.implement;

import graphic.Graph2D;
import graphic.Point2D;

import java.awt.Color;
import java.awt.Graphics;

/**Generates Pixels, Lines and Areas with Colors given by a random Selection
 * from the palette. This avoids Moirees and too regular Textures on the Screen.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 96879795a2fa66471f1bafe3ff733fb4eb8aa0e8d6d3d40223c6607339c5c392
 * stale: false
 * tags: [code/graphics, code/algorithm]
 * concepts: [Random/Anti-Moire Color Strategy]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class RandomColor
	extends Graph2D
{
	/**palette, from which the random Color is chosen	 */
	public Color[] Palette;

	/**Initializing Constructor	with Default Clipping Area  */
	public RandomColor(Graphics g){super (g);}

	/**Constructor that defines a Clipp�ng Area		 */
	public RandomColor(Graphics g_, Point2D ClipTL_, Point2D ClipBR_){
		super (g_, ClipTL_, ClipBR_); }

	/**Sets a Pixel in the current Color at the current Position	 	 */
	public void setPixel() {
		if (Palette != null)
			g.setColor (Palette[(int) (Math.random()*Palette.length)]);
		super.setPixel();
	}

}
