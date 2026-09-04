/*
 * File Name: TexturePalette.java
 * Created on: 29.12.2003
 *
 */
package graphic;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Title: TexturePalette<p>
 * Description:
 * Determines the Color of a Pixel, 
 * based on the local u,v Coordinates of a Bitmap
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see MultiTexture, which maintains several TextuePalette Objects 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class TexturePalette implements IPalette {

	/** Reference to a readable Image in Memory		 */
	private final BufferedImage texture; 

	/** Number of the x Column to read from 	*/
	private final int xCol; 
	/** Number of the y Column to read from 	*/
	private final int yCol; 

	/** initializing Constructor 
	 * 
	 * @param texture_ Reference to a readable Image in Memory
	 * @param xCol_ Number of the x Column to read from 
	 * @param yCol_ Number of the y Column to read from 
	 */
	public TexturePalette(final BufferedImage texture_, final int xCol_, final int yCol_) {
		this.texture = texture_; 
		this.xCol = xCol_; 
		this.yCol = yCol_; 
	}

	/** @see graphic.IPalette#getColor(short[])	 */
	public Color getColor(short[] c) {
		return new Color(texture.getRGB(c[xCol], c[yCol]), true); 
	}

}
