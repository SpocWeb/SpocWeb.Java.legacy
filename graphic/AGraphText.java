package graphic;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;

import streamIO.Log;

/**
  * Renders text as vector polygons using a built-in bitmap-derived font,
  * independent of AWT's {@link Font} mechanism, plus word-wrapping helpers.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-03-2002, 11:44 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see AGraph2D
  * @see GraphTextMetrics metrics matching this class's built-in font
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T12:10:36Z
  * digest: 7e139bdd2d8d74ab996480f461611541bca81d03b3f7d4e0b9ec814de10ef04a
  * stale: false
  * tags: [code/bitmap_font_rendering, code/graphics]
  * concepts: [Vector Font Rendering Base Class]
  * facets: {layer: infrastructure, status: legacy, complexity: medium}
  * -->
  */
public abstract class AGraphText 
extends AGraph2D 
implements IGraphText {
	
	/** Logging channel for this class. */
	private static final Log L = new Log(AGraphText.class, 0);

	/**
	 * Builds a scaled copy of the default font's polygon description.
	 *
	 * @param SizeX
	 * @param SizeY
	 * @return a new Copy of the Default Font scaled by the given integer Scales
	 */
	public static int[][][][] getFont(final int SizeX, int SizeY) {
		int[][][][] Font = (int[][][][]) DEFAULT_FONT_DESCR.clone(); //new int[i][][][];
		int i = Font.length;
		while (--i >= 0) {
			int[][][] tmp = Font[i] = (int[][][]) Font[i].clone();
			int j = tmp.length;
			while (--j >= 0) {
				tmp[j] = sizePolygon(tmp[j], SizeX, SizeY);
			}
		}
		return Font;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * TODO: initialize the Metrics Object, possibly make it nonstatic
	 */
	private FontMetrics fontMetrics = DEFAULT_FONT_METRICS; 

	/**
	 * Returns the metrics for the currently selected bitmap font.
	 *
	 * @return a FontMetrics Object allowing to determine the String Sizes with a given Font
	 */
	public FontMetrics getFontMetrics() { return fontMetrics; }

	/**
	 * Array to contain the Descriptions of the Characters.
	 * The Characters are formatted in a fixed Size Font counted from Top Left.
	 * They should be scaled by a Factor of at least 2 to keep Spaces
	 * Numbers are described as their 7 Segment Equivalents
	 * Characters are subjected to a Raster of 4*6 Points.
	 * The Characters are represented as a single Polygon,
	 * although this is ineffective and prevents XOR Draw (drawMode)
	 */
	private int[][][][] font = DEFAULT_FONT; //getFontPolygons();

	/** Replaces the font's polygon description, unless {@code font} is null. */
	public void setFont(int[][][][] font) {
		if (font != null)
			this.font = font;
	}

	/**
	 * Sets the Default Font with the given Scaling Factors in X and Y Direction
	 * @param scaleX_
	 * @param scaleY_
	 */
	public void setFont(int scaleX_, int scaleY_) {
		fontMetrics = new GraphTextMetrics(scaleX_, scaleY_);
		font = getFont(scaleX_, scaleY_);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	///#region: Member Methods
	
	/** Conversion factor from bitmap-font units to points; currently identity (1). */
	final static public float FACTOR_BMP_2_PPT = 1;
	
	/**
	 * draws the given Text into this Graphics Context using the current Font. 
	 * Unfortunately testing the String Length creates a lot of String Garbage. 
	 * @param text the Text to draw
	 * @param x the left Bound of the Text 
	 * @param y the vertical start Position of the Text 
	 * @param columnWidth the maximum allowed Width of the Text.
	 * @param align determines Alignment: 
	 * negative: left  aligned 
	 * positive: right aligned 
	 * zero: centered 
	 * @param seps optional (null allowed) String containing the Separator Characters
	 * If no Separators are given, splits the Rows at individual Characters 
	 * @param singleRowHeight optional (null allowed)
	 * On Return it is replaced with the actual maximum Width used and the Height of a single Row.  
	 * @return
	 */
	public int drawString(String text, final int x, int y, int columnWidth, int align, final String seps, int[] singleRowHeight)
	{ 
		if (text.length() <= 0)	
			return 0; 
		String separators = seps; 
		if ((separators != null) && (separators.indexOf(text.charAt(text.length()-1)) < 0))
			text += separators.charAt(0); //append a Sentinel Separator to stay within the loop
		
		if (singleRowHeight == null)
			singleRowHeight = new int[2]; 
		
		columnWidth*=FACTOR_BMP_2_PPT;
		//Bitmap bmp = new Bitmap(BITMAP_SIZE, BITMAP_SIZE);
		//Graphics g = Graphics.FromImage(bmp);
		
		final FontMetrics fontMetrics = this.getFontMetrics(); 
		singleRowHeight[0] = fontMetrics.getHeight(); //"aA\tgMF", //use a good Example String
		int numRows = 1; //always at least a single Row...
		// Calculate own values
		float totalWidth = fontMetrics.stringWidth(text); //getLineMetrics(); .getStringBounds(); //these account for Anti-Aliasing
		
		//fits into a single row, dont even start the Algorithm
		if (totalWidth < columnWidth)
			return 1;
		
		int maxWidth = 0;
		int lastRowStart = 0;
		int lastWordStart = 0; 
		int currWordStart = 0; 
		int lastWidth = 0;
		int currWidth = 0; 
		
		L.n("Starting to calculate Height for String '"+text); 
		for (int i=1; i <text.length(); i++)
		{ //loop to process separate Words...
			if((separators != null) && (separators.indexOf(text.charAt(i)) < 0))
				continue; //Loop up to the next Separator
			
			L.n("Separator found at:"+i); 
			final String testString = text.substring(lastRowStart, i-lastRowStart);
			final int testWidth = fontMetrics.stringWidth(testString);
			
			lastWordStart = currWordStart; currWordStart = i; 
			lastWidth = currWidth; currWidth = testWidth; 
			
			// if toMeasure doesn't fit into a single row...
			if (testWidth > columnWidth) { L.n("Larger than a single row!"); 
				if (lastRowStart < lastWordStart) { 
					L.n("Previous Separator in same Row exists!");
					final String s = text.substring(lastRowStart, lastWordStart);
					int start; 
					if (align < 0)
						start = x; 
					else if (align > 0)
						start = x+columnWidth-lastWidth; 
					else
						start = x+(columnWidth-lastWidth)/2; 
					this.drawString(s, start, y);
					/*
					ownSize = g.MeasureString(s, f, width); // cut the words...
					if (ownSize.Height > singleRowHeight) {
						separators = null;
						i = lastRowStart;
						continue;
					}*/
					lastRowStart = lastWordStart; //+1; to skip the Separator
					/*
					i = lastRowStart;
					if (separators == null) {
						lastRowStart--; //when Character-wise, don't skip the Separator
						currWordStart = lastRowStart;
					}
					*/
					if (maxWidth < lastWidth)
						maxWidth = lastWidth;
					++numRows; y+=singleRowHeight[0]; 
					separators = seps; //(try to) go on word-wise (again)...
				}
				else 
				{
					L.n("No previous Separator in same Row"); 
					L.n("Extra long Word, go back and increment the String Character-wise"); 

					//(reducing could potentially take very much longer)
					i = currWordStart = lastRowStart; //lastWordIndex+1; 
					separators = null;
				} 
			}
		}
		if (singleRowHeight.length > 1)
			singleRowHeight[1] = maxWidth; 
		return numRows;
	}
	
	/**Draws a Character at the current Position.
	 * The current Position is moved to the Top of the next Character.	 
	 */
	public void drawChar(final char c) {
		Point2D Pos = this.P.getLocation();
		int[][][] tmp = font[c];
		int i = -1;
		while (++i < tmp.length) {
			drawPolygon(movePolygon(tmp[i], Pos.getX(), Pos.getY()), false);
		}
		this.P.setX(Pos.getX() + font[0][0][0][0]);
		this.P.setY(Pos.getY());
	}

	/**Draws a Character at the specified Position	 */
	public void drawChar(final char c, final Point2D P) {
		this.P = P;
		drawChar(c);
	}
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final StringBuffer S) { drawString(0, S, S.length()); }
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final String S) { drawString(S, 0, S.length()); }
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final StringBuffer S, final int start) {
		drawString(start, S, S.length()); }
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final String S, final int start) {
		drawString(S, start, S.length()); }
		
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final int start, final StringBuffer S, final int stop) {
		for(int i = start-1; ++i < stop;) 
			drawChar(S.charAt(i));
	}
	
	/**Draws a String at the current Position
	 * The current Position is moved to the Top of the next String.	 */
	public void drawString(final int start, final String S, final int stop) {
		for(int i = start-1; ++i < stop;) 
			drawChar(S.charAt(i));
	}
	
	/**Draws a String at the specified Position	
	 * 
	 * @param S String to draw
	 * @param Pt Position to draw at
	 */
	public void drawString(final String S, final Point2D Pt) {
		this.P.copyAt(Pt);
		drawString(S);
	}
	
	/**Draws a String at the specified Position	 */
	public void drawString(final String S, final int x, final int y) {
		P.setLocation(x, y);
		drawString(S);
	}
	
	/**Draws a String at the specified Position	 */
	public void drawString(final StringBuffer S, final int x, final int y) {
		P.setLocation(x, y);
		drawString(S);
	}
	
	/**Marks each Point with it's Number	 */
	public void drawPointNumbers(final Point2D[] Points) {
		for(int i = Points.length; --i >= 0; ) {
			drawString(String.valueOf(i), Points[i]);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods from java.awt.Graphics
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Draws the given byte range, interpreted as Latin-1 text, starting at
	 * the given position.
	 *
	 * @see graphic.IGraphics#drawBytes(byte[], int, int, int, int)
	 */
	public void drawBytes(final byte[] data, final int offset, final int length, final int x, final int y) {
		P.setLocation(x, y);
		for(int i = -1; ++i < length; ) {
			int chr = data[offset+i];
			if (chr < 0) { chr += 256; }
			drawChar((char) chr);
		}
	}

	/**
	 * Draws the given character range starting at the given position.
	 *
	 * @see graphic.IGraphics#drawChars(char[], int, int, int, int)
	 */
	public void drawChars(final char[] data, final int offset, final int length, final int x, final int y) {
		P.setLocation(x, y);
		for(int i = -1; ++i < length; ) 
			drawChar(data[offset+i]);
	}

	/**
	 * Draws the attributed text starting at the given position.
	 *
	 * @see graphic.IGraphics#drawString(int, java.text.AttributedCharacterIterator, int)
	 */
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		P.setLocation(x, y);
		for(char c = iterator.first(); CharacterIterator.DONE != (c = iterator.next()); ) {
			drawChar(c);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	protected AGraphText() {}

	/** Constructor that defines a Clipping Area	 */
	protected AGraphText(Point2D ClipTL_, Point2D ClipBR_) {
		super(ClipTL_, ClipBR_);
	}

	/////////////////////////////////////////////////////////////////////////////////
	/// Methods from java.awt.Graphics
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * This bitmap-font renderer has no {@link Font}; always returns null.
	 *
	 * @see graphic.IGraphics#getFont()
	 */
	public Font getFont() { return null; }

	/**
	 * Not supported; always throws, since this class draws its own bitmap
	 * font rather than an AWT {@link Font}.
	 *
	 * @throws RuntimeException always
	 * @see graphic.IGraphics#setFont(java.awt.Font)
	 */
	public void setFont(final Font font) { throw new RuntimeException("Not supported!"); }

	/**
	 * Returns the current bitmap font's metrics, ignoring {@code f}.
	 *
	 * @see graphic.IGraphics#getFontMetrics(java.awt.Font)
	 */
	public FontMetrics getFontMetrics(final Font f) { return fontMetrics; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Array to contain the Descriptions of the Characters.
	 * The Characters are formatted in a fixed Size Font counted from Top Left.
	 * They should be scaled by a Factor of at least 2 to keep Spaces between Lines.
	 * Numbers are described as their 7 Segment Equivalents.
	 * Characters are subjected to a Raster of 4*6 Points 
	 * with 1 Pixel Underline.
	 * The Characters no longer represented as a single Polygon,
	 * because this is ineffective and prevents XOR Draw (drawMode)
	 * The structure of the Font Description is:
	 * 0. Number of Character
	 * 1. Polygon 
	 * 2. & 3. x/y Coordinate Pairs
	 */
	private static int[][][][] DEFAULT_FONT_DESCR = //new int [][][]
	{ //000 up to 31 == 19 are nonprintable Characters
		{ { { 4, 0 } } }, //000
			{ {} }, //001
			{ {} }, //002
			{ {} }, //003
			{ {} }, //004
			{ {} }, //005
			{ {} }, //006
			{ {} }, //007
			{ {} }, //008
			{ {} }, //009
			{ {} }, //010
			{ {} }, //011
			{ {} }, //012
			{ {} }, //013
			{ {} }, //014
			{ {} }, //015
			{ {} }, //016
			{ {} }, //017
			{ {} }, //018
			{ {} }, //019
			{ {} }, //020
			{ {} }, //021
			{ {} }, //022
			{ {} }, //023
			{ {} }, //024
			{ {} }, //025
			{ {} }, //026
			{ {} }, //027
			{ {} }, //028
			{ {} }, //029
			{ {} }, //030
			{ {} }, //031
			{ {} }, //032	Space
			{ { { 1, 0 }, { 1, 3 } }, { { 1, 4 } } }, //033	!
			{ { { 0, 0 }, { 0, 1 } }, { { 1, 0 }, { 1, 1 } } }, //034	"
			{ { { 0, 3 }, { 1, 0 } }, { { 1, 3 }, { 2, 0 } },
					{ { 0, 1 }, { 2, 1 } }, { { 0, 2 }, { 2, 2 } } }, //035	#
			{
					{ { 1, 0 }, { 1, 4 } },
					{ { 0, 3 }, { 1, 4 }, { 2, 3 }, { 0, 1 }, { 1, 0 },
							{ 2, 1 } } }, //036	$
			{ { { 0, 0 }, { 0, 1 }, { 1, 1 }, { 1, 0 }, { 0, 0 } },
					{ { 2, 4 }, { 1, 4 }, { 1, 3 }, { 2, 3 }, { 2, 4 } },
					{ { 0, 3 }, { 2, 1 } } }, //037	%
			{ {} }, //038	&
			{ { { 1, 0 }, { 1, 1 } } }, //039	'
			{ { { 2, 0 }, { 1, 1 }, { 1, 3 }, { 2, 4 } } }, //040	(
			{ { { 0, 0 }, { 1, 1 }, { 1, 3 }, { 0, 4 } } }, //041	)
			{ { { 0, 2 }, { 2, 2 } }, { { 1, 1 }, { 1, 3 } },
					{ { 0, 1 }, { 2, 3 } }, { { 0, 3 }, { 2, 1 } } }, //042	*
			{ { { 0, 2 }, { 2, 2 } }, { { 1, 1 }, { 1, 3 } } }, //043	+
			{ { { 1, 4 }, { 1, 5 } } }, //044	,
			{ { { 0, 2 }, { 2, 2 } } }, //045	-
			{ { { 1, 4 } } }, //046	.
			{ { { 0, 4 }, { 2, 0 } } }, //047	/
			{ { { 0, 0 }, { 2, 0 }, { 2, 4 }, { 0, 4 }, { 0, 0 } } }, //048	0
			{ { { 2, 0 }, { 2, 4 } } }, //049	1
			{ { { 0, 0 }, { 2, 0 }, { 2, 2 }, { 0, 2 }, { 0, 4 }, { 2, 4 } } }, //050	2
			//		{{{0,0},{2,0},{2,2},{0,2},{2,2},{2,4},{0,4}}},	//051	3	'painted in a single draw
			{ { { 0, 0 }, { 2, 0 }, { 2, 2 }, { 2, 4 }, { 0, 4 } },
					{ { 2, 2 }, { 0, 2 } } }, //051	3	'painted in two Draws
			//		{{{0,0},{0,2},{2,2},{2,0},{2,4}}},	//052	4	'painted in a single draw
			{ { { 0, 0 }, { 0, 2 }, { 2, 2 } }, { { 2, 0 }, { 2, 4 } } }, //052	4	'painted in two Draws
			{ { { 0, 4 }, { 2, 4 }, { 2, 2 }, { 0, 2 }, { 0, 0 }, { 2, 0 } } }, //053	5
			{ { { 0, 2 }, { 2, 2 }, { 2, 4 }, { 0, 4 }, { 0, 0 }, { 2, 0 } } }, //054	6
			{ { { 0, 0 }, { 2, 0 }, { 2, 4 } } }, //055	7
			//		{{0,0},{0,4},{2,4},{2,0},{0,0},{0,2},{2,4}},	//056	8	'painted in a single draw
			{ { { 0, 0 }, { 0, 4 }, { 2, 4 }, { 2, 0 }, { 0, 0 } },
					{ { 0, 2 }, { 2, 2 } } }, //056	8	'painted in two Draws
			{ { { 0, 4 }, { 2, 4 }, { 2, 0 }, { 0, 0 }, { 0, 2 }, { 2, 2 } } }, //057	9
			{ { { 1, 2 } }, { { 1, 4 } } }, //058	:
			{ { { 1, 2 } }, { { 1, 4 }, { 1, 5 } } }, //059	;
			{ { { 2, 1 }, { 0, 2 }, { 2, 3 } } }, //060	<
			{ { { 0, 1 }, { 2, 1 } }, { { 0, 2 }, { 2, 2 } } }, //061	=
			{ { { 0, 1 }, { 2, 2 }, { 0, 3 } } }, //062	>
			{ { { 0, 1 }, { 1, 0 }, { 2, 1 }, { 1, 2 }, { 1, 3 } },
					{ { 1, 4 } } }, //063	?
			{ { { 1, 1 }, { 1, 2 }, { 2, 2 }, { 2, 1 }, { 1, 0 }, { 0, 1 },
					{ 0, 3 }, { 1, 4 }, { 2, 3 } } }, //064	@
			//		{{0,4},{0,3},{1,0},{2,3},{2,4},{2,3},{0,3}},	//065	A	'painted in a single draw
			{ { { 0, 4 }, { 0, 3 }, { 1, 0 }, { 2, 3 }, { 2, 4 } },
					{ { 2, 3 }, { 0, 3 } } }, //065	A	'painted in two Draws
			//		{{0,0},{2,1},{0,2},{2,3},{0,4},{0,0}},	//066	B, kind of edgy
			{ { { 0, 0 }, { 1, 0 }, { 2, 1 }, { 1, 2 }, { 0, 2 }, { 1, 2 },
					{ 2, 3 }, { 1, 4 }, { 0, 4 }, { 0, 0 } } }, //066	B, rounder, but 4 Points more
			{ { { 2, 1 }, { 1, 0 }, { 0, 1 }, { 0, 3 }, { 1, 4 }, { 2, 3 } } }, //067	C
			//		{{0,0},{2,2},{0,4},{0,0}},	//068	D, kind of edgy
			{ { { 0, 0 }, { 1, 0 }, { 2, 1 }, { 2, 3 }, { 1, 4 }, { 0, 4 },
					{ 0, 0 } } }, //068	D, rounder, but 4 Points more
			//		{{2,0},{0,0},{0,2},{2,2},{0,2},{0,4},{2,4}},	//069	E, single Draw
			//		{{2,0},{0,0},{0,2},{2,1},{0,2},{0,4},{2,4}},	//069	E, with shorter middle Line, single Draw
			{ { { 2, 0 }, { 0, 0 }, { 0, 4 }, { 2, 4 } },
					{ { 0, 2 }, { 1, 2 } } }, //069	E, with shorter middle Line
			//		{{2,0},{0,0},{0,2},{2,2},{0,2},{0,4}},	//070	F, single Draw
			//		{{2,0},{0,0},{0,2},{2,1},{0,2},{0,4}},	//070	F, with shorter middle Line, single Draw
			{ { { 2, 0 }, { 0, 0 }, { 0, 4 } }, { { 0, 2 }, { 1, 2 } } }, //070	F, with shorter middle Line
			{ { { 2, 1 }, { 1, 0 }, { 0, 1 }, { 0, 3 }, { 1, 4 }, { 2, 3 },
					{ 2, 2 }, { 1, 2 } } }, //071	G
			{ { { 0, 0 }, { 0, 4 } }, { { 2, 0 }, { 2, 4 } },
					{ { 0, 2 }, { 2, 2 } } }, //072	H
			{ { { 1, 0 }, { 1, 4 } } }, //073	I
			{ { { 2, 0 }, { 2, 3 }, { 1, 4 }, { 0, 3 } } }, //074	J
			{ { { 0, 0 }, { 0, 4 } }, { { 2, 0 }, { 0, 2 }, { 2, 4 } } }, //075	K
			{ { { 0, 0 }, { 0, 4 }, { 2, 4 } } }, //076	L
			{ { { 0, 4 }, { 0, 0 }, { 1, 2 }, { 2, 0 }, { 2, 4 } } }, //077	M
			{ { { 0, 4 }, { 0, 0 }, { 2, 4 }, { 2, 0 } } }, //078	N
			{ { { 0, 1 }, { 1, 0 }, { 2, 1 }, { 2, 3 }, { 1, 4 }, { 0, 3 },
					{ 0, 1 } } }, //079	O
			{ { { 0, 4 }, { 0, 0 }, { 1, 0 }, { 2, 1 }, { 1, 2 }, { 0, 2 } } }, //080	P
			{
					{ { 0, 1 }, { 1, 0 }, { 2, 1 }, { 2, 3 }, { 1, 4 },
							{ 0, 3 }, { 0, 1 } }, { { 1, 3 }, { 2, 4 } } }, //081	Q
			{ { { 0, 4 }, { 0, 0 }, { 1, 0 }, { 2, 1 }, { 1, 2 }, { 0, 2 } },
					{ { 1, 2 }, { 2, 4 } } }, //082	R
			{ { { 0, 4 }, { 1, 4 }, { 2, 3 }, { 0, 1 }, { 1, 0 }, { 2, 0 } } }, //083	S
			{ { { 1, 0 }, { 1, 4 } }, { { 0, 0 }, { 2, 0 } } }, //084	T
			{ { { 0, 0 }, { 0, 3 }, { 1, 4 }, { 2, 3 }, { 2, 0 } } }, //085	U
			{ { { 0, 0 }, { 1, 4 }, { 2, 0 } } }, //086	V
			{ { { 0, 0 }, { 0, 4 }, { 1, 2 }, { 2, 4 }, { 2, 0 } } }, //087	W
			{ { { 0, 0 }, { 2, 4 } }, { { 0, 4 }, { 2, 0 } } }, //088	X
			{ { { 1, 2 }, { 1, 4 } }, { { 0, 0 }, { 1, 2 }, { 2, 0 } } }, //089	Y
			{ { { 0, 0 }, { 2, 0 }, { 0, 4 }, { 2, 4 } } }, //090	Z
			{ { { 2, 4 }, { 1, 4 }, { 1, 0 }, { 2, 0 } } }, //091	[
			{ { { 0, 0 }, { 2, 4 } } }, //092	\
			{ { { 0, 0 }, { 1, 0 }, { 1, 4 }, { 0, 4 } } }, //093	]
			{ { { 0, 1 }, { 1, 0 }, { 2, 1 } } }, //094	^
			{ { { 0, 4 }, { 2, 4 } } }, //095	_
			{ { { 0, 0 }, { 1, 1 } } }, //096	`
			{ { { 0, 2 }, { 1, 1 }, { 2, 1 }, { 2, 4 }, { 1, 4 }, { 0, 3 },
					{ 1, 2 }, { 2, 2 } } }, //097	a
			{ { { 0, 0 }, { 0, 4 }, { 1, 4 }, { 2, 3 }, { 1, 2 }, { 0, 2 } } }, //098	b
			{ { { 2, 2 }, { 1, 2 }, { 0, 3 }, { 1, 4 }, { 2, 4 } } }, //099	c
			{ { { 2, 0 }, { 2, 4 }, { 1, 4 }, { 0, 3 }, { 1, 2 }, { 2, 2 } } }, //100	d
			{ { { 0, 2 }, { 2, 2 }, { 1, 1 }, { 0, 2 }, { 0, 3 }, { 1, 4 },
					{ 2, 3 } } }, //101	e
			{ { { 2, 1 }, { 1, 0 }, { 1, 4 } }, { { 1, 2 }, { 2, 2 } } }, //102	f
			{ { { 0, 4 }, { 1, 5 }, { 2, 5 }, { 2, 2 }, { 1, 2 }, { 0, 3 },
					{ 1, 4 }, { 2, 4 } } }, //103	g
			{ { { 0, 0 }, { 0, 4 } },
					{ { 0, 2 }, { 1, 2 }, { 2, 3 }, { 2, 4 } } }, //104	h
			{ { { 1, 2 }, { 1, 4 } }, { { 1, 1 } } }, //105	i
			{ { { 1, 2 }, { 2, 2 }, { 2, 4 }, { 1, 5 } }, { { 1, 1 } } }, //106	j
			{ { { 0, 0 }, { 0, 4 } }, { { 2, 2 }, { 0, 3 }, { 2, 4 } } }, //107	k
			{ { { 1, 0 }, { 1, 4 }, { 2, 4 } } }, //108	l
			{ { { 0, 4 }, { 0, 2 }, { 2, 2 }, { 2, 4 } },
					{ { 1, 2 }, { 1, 4 } } }, //109	m
			{ { { 0, 4 }, { 0, 2 }, { 1, 2 }, { 2, 3 }, { 2, 4 } } }, //110	n
			{ { { 0, 3 }, { 1, 2 }, { 2, 3 }, { 1, 4 }, { 0, 3 } } }, //111	o
			{ { { 0, 4 }, { 1, 4 }, { 2, 3 }, { 1, 2 }, { 0, 2 }, { 0, 5 } } }, //112	p
			{ { { 2, 4 }, { 1, 4 }, { 0, 3 }, { 1, 2 }, { 2, 2 }, { 2, 5 } } }, //113	q
			{ { { 0, 4 }, { 0, 2 }, { 1, 2 }, { 2, 3 } } }, //114	r
			{ { { 0, 4 }, { 1, 4 }, { 2, 3 }, { 0, 3 }, { 1, 2 }, { 2, 2 } } }, //115	s
			{ { { 1, 0 }, { 1, 4 }, { 2, 4 } }, { { 0, 1 }, { 2, 1 } } }, //116	t
			{ { { 0, 2 }, { 0, 3 }, { 1, 4 }, { 2, 4 }, { 2, 2 } } }, //117	u
			{ { { 0, 2 }, { 1, 4 }, { 2, 2 } } }, //118	v
			{ { { 0, 2 }, { 0, 4 }, { 2, 4 }, { 2, 2 } },
					{ { 1, 2 }, { 1, 4 } } }, //119	w
			{ { { 0, 4 }, { 2, 2 } }, { { 0, 2 }, { 2, 4 } } }, //120	x
			{ { { 0, 3 }, { 1, 4 } }, { { 2, 3 }, { 0, 5 } } }, //121	y
			{ { { 0, 2 }, { 2, 2 }, { 0, 4 }, { 2, 4 } } }, //122	z
			{ { { 2, 0 }, { 1, 1 }, { 1, 3 }, { 2, 4 } },
					{ { 0, 2 }, { 1, 2 } } }, //123	{
			{ { { 1, 0 }, { 1, 4 } } }, //124	|
			{ { { 0, 0 }, { 1, 1 }, { 1, 3 }, { 0, 4 } },
					{ { 1, 2 }, { 2, 2 } } }, //125	}
			{ { { 0, 1 }, { 1, 0 }, { 2, 1 }, { 3, 0 } } }, //126	~
			{ {} }, //127
			{ {} }, //128
			{ {} }, //129
			{ {} }, //130
			{ {} }, //131
			{ {} }, //132
			{ {} }, //133
			{ {} }, //134
			{ {} }, //135
			{ {} }, //136
			{ {} }, //137
			{ {} }, //138
			{ {} }, //139
			{ {} }, //140
			{ {} }, //141
			{ {} }, //142
			{ {} }, //143
			{ {} }, //144
			{ {} }, //145
			{ {} }, //146
			{ {} }, //147
			{ {} }, //148
			{ {} }, //149
			{ {} }, //150
			{ {} }, //151
			{ {} }, //152
			{ {} }, //153
			{ {} }, //154
			{ {} }, //155
			{ {} }, //156
			{ {} }, //157
			{ {} }, //158
			{ {} }, //159
			{ {} }, //160
			{ {} }, //161
			{ {} }, //162
			{ {} }, //163
			{ {} }, //164
			{ {} }, //165
			{ {} }, //166
			{ { { 2, 1 }, { 1, 0 }, { 0, 1 }, { 0, 2 }, { 2, 3 } },
					{ { 0, 3 }, { 1, 4 }, { 2, 3 }, { 2, 2 }, { 0, 1 } } }, //167	�
			{ {} }, //168
			{ {} }, //169
			{ {} }, //170
			{ {} }, //171
			{ {} }, //172
			{ {} }, //173
			{ {} }, //174
			{ {} }, //175
			{ {} }, //176
			{ {} }, //177
			{ {} }, //178
			{ {} }, //179
			{ {} }, //180
			{ {} }, //181
			{ {} }, //182
			{ {} }, //183
			{ {} }, //184
			{ {} }, //185
			{ {} }, //186
			{ {} }, //187
			{ {} }, //188
			{ {} }, //189
			{ {} }, //190
			{ {} }, //191
			{ {} }, //192
			{ {} }, //193
			{ {} }, //194
			{ {} }, //195
			{ { { 0, 4 }, { 0, 3 }, { 1, 0 }, { 2, 3 }, { 2, 4 } },
					{ { 2, 3 }, { 0, 3 } }, { { 0, 0 } }, { { 2, 0 } } }, //196	�	'painted in two Draws
			{ {} }, //197
			{ {} }, //198
			{ {} }, //199
			{ {} }, //200
			{ {} }, //201
			{ {} }, //202
			{ {} }, //203
			{ {} }, //204
			{ {} }, //205
			{ {} }, //206
			{ {} }, //207
			{ {} }, //208
			{ {} }, //209
			{ {} }, //210
			{ {} }, //211
			{ {} }, //212
			{ {} }, //213
			{
					{ { 0, 1 }, { 1, 0 }, { 2, 1 }, { 2, 3 }, { 1, 4 },
							{ 0, 3 }, { 0, 1 } }, { { 0, 0 } }, { { 2, 0 } } }, //214	�
			{ {} }, //215
			{ {} }, //216
			{ {} }, //217
			{ {} }, //218
			{ {} }, //219
			{ { { 0, 1 }, { 0, 3 }, { 1, 4 }, { 2, 3 }, { 2, 1 } },
					{ { 0, 0 } }, { { 2, 0 } } }, //220	�
			{ {} }, //221
			{ {} }, //222
			{ { { 0, 4 }, { 0, 1 }, { 1, 0 }, { 2, 1 }, { 1, 2 }, { 2, 3 },
					{ 1, 4 } } }, //223	�
			{ {} }, //224
			{ {} }, //225
			{ {} }, //226
			{ {} }, //227
			{
					{ { 0, 2 }, { 1, 1 }, { 2, 1 }, { 2, 4 }, { 1, 4 },
							{ 0, 3 }, { 1, 2 }, { 2, 2 } }, { { 0, 0 } },
					{ { 2, 0 } } }, //228	�
			{ {} }, //229
			{ {} }, //230
			{ {} }, //231
			{ {} }, //232
			{ {} }, //233
			{ {} }, //234
			{ {} }, //235
			{ {} }, //236
			{ {} }, //237
			{ {} }, //238
			{ {} }, //239
			{ {} }, //240
			{ {} }, //241
			{ {} }, //242
			{ {} }, //243
			{ {} }, //244
			{ {} }, //245
			{ { { 0, 3 }, { 1, 2 }, { 2, 3 }, { 1, 4 }, { 0, 3 } },
					{ { 0, 1 } }, { { 2, 1 } } }, //246	�
			{ {} }, //247
			{ {} }, //248
			{ {} }, //249
			{ {} }, //250
			{ {} }, //251
			{ { { 0, 2 }, { 0, 3 }, { 1, 4 }, { 2, 4 }, { 2, 2 } },
					{ { 0, 1 } }, { { 2, 1 } } }, //252	�
			{ {} }, //253
			{ {} }, //254
			{ {} }, //255
	};
	
	/** Default bitmap font, scaled by a factor of 2 in each direction. */
	protected final static int[][][][] DEFAULT_FONT = getFont(2,2);

	/** Metrics matching {@link #DEFAULT_FONT}. */
	protected final static FontMetrics DEFAULT_FONT_METRICS = new GraphTextMetrics(2, 2);

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws java.io.IOException {
		testIt(args);
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + AGraphText.class.getName());
	}

	/** Output of Characters	 */
	public static void testPaintChars(Frame f, Graphics g) {
		//		final float xMin = -1;
		//		final float xMax = +1;
		//		final float yMin = -1;
		//		final float yMax = +1;

		int w = 100;
		int h = 100;
		int pix[] = new int[w * h];
		int index = 0;
		for (int y = 0; y < h; y++) {
			int red = (y * 255) / (h - 1);
			for (int x = 0; x < w; x++) {
				int blue = (x * 255) / (w - 1);
				pix[index++] = (255 << 24) | (red << 16) | blue;
			}
		}
		Image img = f.createImage(w, h); //new MemoryImageSource(w, h, pix, 0, w));
		//java.awt.Graphics
		try {
			img.getGraphics();
		} catch (Exception e) {
			System.out.println(e.getMessage() + e.toString());
		}

		//		Rectangle Bounds = g.getClipBounds();
		//		g.clearRect(Bounds.x, Bounds.y, Bounds.x+Bounds.width, Bounds.y+Bounds.height);
		AGraphText g2D = new JavaGraphic(g);
		//		Figures Fig = new Figures(g2D);

		String loCase = "abcdefghijklmnopqrstuvwxyz���";
		String upCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ���";
		String numbers = "012345678 9:;,.#'!�$%&/\\(){}[]=?<>^~*+-_ ";
		String testCase = "AAABBBBaaabbb";

		g2D.P.setX(100);
		g2D.P.setY(100);
		g2D.drawString(loCase);
		g2D.P.setX(100);
		g2D.P.setY(200);
		g2D.drawString(upCase);
		g2D.P.setX(100);
		g2D.P.setY(300);
		g2D.drawString(testCase);
		g2D.P.setX(100);
		g2D.P.setY(400);
		g2D.drawString(numbers);

		g2D.setFont(5, 5);

		g2D.P.setX(100);
		g2D.P.setY(100);
		g2D.drawString(loCase);
		g2D.P.setX(100);
		g2D.P.setY(200);
		g2D.drawString(upCase);
		g2D.P.setX(100);
		g2D.P.setY(300);
		g2D.drawString(testCase);
		g2D.P.setX(100);
		g2D.P.setY(400);
		g2D.drawString(numbers);
	}

}

/**
 * {@link FontMetrics} for {@link AGraphText}'s built-in bitmap font: every
 * character has the same fixed advance and ascent, scaled by a constant
 * factor in each direction.
 *
 * @see AGraphText
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:10:36Z
 * digest: 4a13afbef640130b63c283b402762d6068f4814e14302d0aa3b3012109543dce
 * stale: false
 * tags: [code/bitmap_font_rendering]
 * concepts: [Font Metrics Holder]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
class GraphTextMetrics
extends FontMetrics {

	/** Required by {@link java.io.Serializable}; this class is never serialized meaningfully. */
	private static final long serialVersionUID = 1L;

	/** Total Width of a Character */
	private static final int X_SCALE = 4;

	/** Total Height of a Character */
	private static final int Y_SCALE = 6;

	/** Character advance width, {@code X_SCALE} scaled by the constructor's x factor. */
	private int scaleX;

	/** Character ascent, {@code Y_SCALE} scaled by the constructor's y factor. */
	private int scaleY;

	/** Creates metrics for a font scaled by the given integer x/y factors. */
	protected GraphTextMetrics(int scaleX_, int scaleY_) {
		super(null);
		scaleX = scaleX_ * X_SCALE;
		scaleY = scaleY_ * Y_SCALE;
	}

	/**
	 * Returns the fixed character advance width.
	 *
	 * @see java.awt.FontMetrics#getMaxAdvance()
	 */
	public int getMaxAdvance() {
		return scaleX;
	}

	/**
	 * Returns the width of the given character range: a fixed per-character
	 * advance times the character count.
	 *
	 * @see java.awt.FontMetrics#charsWidth(char[], int, int)
	 */
	public int charsWidth(char[] data, int off, int len) {
		return (len-off)*scaleX;
	}

	/**
	 * Returns the fixed character advance width, ignoring {@code ch}.
	 *
	 * @see java.awt.FontMetrics#charWidth(char)
	 */
	public int charWidth(char ch) {
		return scaleX;
	}

	/**
	 * Returns the fixed character ascent.
	 *
	 * @see java.awt.FontMetrics#getAscent()
	 */
	public int getAscent() {
		return scaleY;
	}

	/**
	 * Returns zero; this bitmap font has no inter-line leading.
	 *
	 * @see java.awt.FontMetrics#getLeading()
	 */
	public int getLeading() {
		return 0;
	}

}