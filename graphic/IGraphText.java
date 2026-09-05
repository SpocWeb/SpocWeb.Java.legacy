/*
 * File Name: IGraphText.java
 * Created on: 14.09.2003
 *
 */
package graphic;

import java.awt.Font;
import java.awt.FontMetrics;
import java.text.AttributedCharacterIterator;

/**
 * Extends {@link IGraphShape} with text- and font-related drawing methods,
 * mirroring the relevant part of {@link java.awt.Graphics}'s interface.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 * @see IGraphShape
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:53:17Z
 * digest: c21eb45016475d99242557348a6d3eb8b97c128e247664e9f3ad15b9f369c3c2
 * stale: false
 * tags: [code/graphics]
 * concepts: [Text Rendering Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IGraphText extends IGraphShape {
	
	/**Draws a Character at the current Position.
	 * The current Position is moved to the Top of the next Character.	 
	 */
	public void drawChar(final char c);
	
	/**Draws a Character at the specified Position	 */
	public void drawChar(final char c, final Point2D P);
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final String S);
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final StringBuffer S, final int start);
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final String S, final int start);
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final int start, final StringBuffer S, final int stop);
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final int start, final String S, final int stop);
	
	/**
	 * Draws the buffer's text at the given position.
	 *
	 * @see java.awt.Graphics#drawString(java.lang.String, int, int)
	 */
	public abstract void drawString(StringBuffer str, int x, int y);

	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final StringBuffer S);
	
	/**Draws a String at the specified Position	
	 * 
	 * @param S String to draw
	 * @param Pt Position to draw at
	 */
	public void drawString(final String S, final Point2D Pt);

	/////////////////////////////////////////////////////////////////////////////////////
	///#region: Methods from @see java.awt.Graphics 
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the font currently used for drawing text.
	 *
	 * @see java.awt.Graphics#getFont()
	 */
	public abstract Font getFont();

	/**
	 * Sets the font subsequently used for drawing text.
	 *
	 * @see java.awt.Graphics#setFont(java.awt.Font)
	 */
	public abstract void setFont(Font font);

	/**
	 * Returns the font metrics for the given font.
	 *
	 * @see java.awt.Graphics#getFontMetrics(java.awt.Font)
	 */
	public abstract FontMetrics getFontMetrics(Font f);

	/**
	 * Returns the font metrics for the current font.
	 *
	 * @see java.awt.Graphics#getFontMetrics()
	 */
	public abstract FontMetrics getFontMetrics();

	/**
	 * Draws the string at the given position.
	 *
	 * @see java.awt.Graphics#drawString(java.lang.String, int, int)
	 */
	public abstract void drawString(String str, int x, int y);

	/**
	 * Draws the attributed text at the given position.
	 *
	 * @see java.awt.Graphics#drawString(java.text.AttributedCharacterIterator, int, int)
	 */
	public abstract void drawString(AttributedCharacterIterator iterator, int x, int y);

	/**
	 * Draws the given byte range, interpreted as Latin-1 text, at the given
	 * position.
	 *
	 * @see java.awt.Graphics#drawBytes(byte[], int, int, int, int)
	 */
	public abstract void drawBytes(byte[] data, int offset, int length, int x, int y);

	/**
	 * Draws the given character range at the given position.
	 *
	 * @see java.awt.Graphics#drawChars(char[], int, int, int, int)
	 */
	public abstract void drawChars(char[] data, int offset, int length, int x, int y);

}
