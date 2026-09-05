/*
 * File Name: TexturePalette.java
 * Created on: 29.12.2003
 *
 */
package graphic;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Determines the color of a pixel from the local u,v coordinates of a bitmap.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see MultiTexture maintains several TexturePalette objects
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:49:50Z
 * digest: b74b20a31fc3381fc202c9089861ab3d9cacfd59608e534834604648dbddc4c3
 * stale: false
 * tags: [code/color_palette]
 * concepts: [Texture-Mapped Palette]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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

	/**
	 * Looks up the texture pixel at this palette's configured u,v columns
	 * of {@code c} and returns it as a fully opaque color.
	 *
	 * @see graphic.IPalette#getColor(short[])
	 */
	public Color getColor(short[] c) {
		return new Color(texture.getRGB(c[xCol], c[yCol]), true); 
	}

}
