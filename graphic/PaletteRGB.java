package graphic;

import java.awt.Color;

/**
 * Holds a Palette, i.e. an Array of Colors
 * 
 * Also static Methods for Color Model Conversion,
 * good example for Coordinate Transformation in a 3 dimensional Space
 * between three orthogonal Coordinate Systems: 
 * 
 * Red
 * Green
 * Blue 
 * 
 * and 
 * Hue
 * Saturation
 * Brightness
 * 
 * or 
 * Cyan
 * Magenta 
 * Yellow 
 * 
 * HSB-Model; often used to select a 2D 'Color' with constant Brightness:
 * Hue is the weighted maximum color normed to [0,1) and cycling through.
 * Saturation is the maximum color difference normed to [0,1) with 0 being some Grey. 
 * Brightness is the maximum color Intensity (not normed to 1).
 * 
 * CMY-Model used for subtractive Color Mixing e.g. in Printers. 
 * Cyan    = (G+B)/2 <=> R = (M+Y-C)
 * Magenta = (B+R)/2 <=> G = (C+Y-M)
 * Yellow  = (R+G)/2 <=> B = (C+M-Y)
 *
 * All six Dimensions have an arbitrary Value Space,
 * but can be normed to [0,1), i.e. excluding 1.
 * 
 * Psycho-Physical Research has Evidence (Language, Indifference to Brightness)
 * for the following Color Base used in neural Processing: 
 * Black/White (Luminance) 
 * Red/Green 
 * Yellow/Blue 
 * 
 * Since only a few Colors can be consistently named strongly suggests 
 * that only a very small number of colors can be used effectively as category labels.
 * Additionally it is important that there be considerable luminance contrast 
 * in addition to color contrast, especially if the colored patterns are small, 
 * because the two Color Channels have only 1/3 Capacity of the Luminance. 
 * 
 * Both motion perception and stereo space perception 
 * as well as Perception of shape and form 
 * are primarily based on information from the Luminance Channel.
 * Thus Color should never be used for encoding Details, Shape, Motion or Text. 
 * 
 * Brown is a dark Red/Orange/Yellow but usually not seen as this; 
 * thus it is easily misconcieved as not belonging to an Orange Palette! 
 */
final public class PaletteRGB 
implements IPalette, ISimplePalette {
	
	//	/**The col[0] Fraction of the Color */	public double col[0];
	//	/**The col[2] Fraction of the Color */	public double col[2];
	//	/**The col[1] Fraction of the Color */	public double col[1];
	
	//	public RGBColor(double red_, double blue_, double green_)
	//	{col[0] = red_; col[2] = blue_; col[1] = green_;}
	
	/**Palette for Scalar Plots. If the Palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	final public Color[] palette;
	
	/**Offset for the Colors of this Palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int colorOffset;
	
	/** Empty Constructor, 
	 * resulting in the Integer Value being directly converted into an RGB Value
	 */ 
	public PaletteRGB() { this.palette = null; }
	
	/** Initializing Constructor, defining the Palette	 
	 * @param palette_ directly setting the Palette
	 */ 
	public PaletteRGB(final Color[] palette_) {
		this.palette = palette_;
	}
	
	/** Initializing Constructor, defining a full Color Cycle Palette	 
	 * @param numCols Granularity of the Color Cycle
	 */ 
	public PaletteRGB(final int numCols) {
		this.palette = CYCLE_PALETTE(numCols);
	}
	
	/** Initializing Constructor, defining a Palette of Shading
	 * @param baseColor Color that is being shaded. 
	 * Should be a brilliant Color. 
	 */
	public PaletteRGB(final Color baseColor) {
		this.palette = SHADING_PALETTE(baseColor);
	}
	
	/**Returns the selected Color either determined by the Palette (if it is not null)
	 * or a new generated Color.	 */
	public Color getColor(final int c) {	//The same code is replicated in ScalarPlotNew of MathGraph2 and MathGraph3, because it doesn't pay off to instantiate
		if (palette == null) {
			return new Color(c + colorOffset); }	//PaletteRGB there and do a call to this routine for each Arrow
			return  palette[(c + colorOffset) % palette.length]; }
	
	/**
	 * @param c a complex Parameter could consist of
	 * - a simple Index
	 * - RGB Values
	 * - u,v Coordinates of a Texture Mapping 
	 * - Normals or their Cosinusses for Phong or Gouraud Shading 
	 * - z-Values for Fog Simulation or ambient Light
	 * - etc. 
	 * @return the selected Color determined by this Palette
	 */
	public Color getColor(final short[] c) {
		int red   = c[colorOffset+0]; //if (red   < 0) red   = 0; if (red   > 255) red   = 255;
		int green = c[colorOffset+1]; //if (green < 0) green = 0; if (green > 255) green = 255;
		int blue  = c[colorOffset+2]; //if (blue  < 0) blue  = 0; if (blue  > 255) blue  = 255;
		return new Color(red, green, blue);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Converts a string formatted as "rrggbb" to an awt.Color object	 */
	final static public Color getColor(final String rrggbb) {
		return new Color (
			Integer.parseInt(rrggbb.substring(0,2), 16),
			Integer.parseInt(rrggbb.substring(2,4), 16),
			Integer.parseInt(rrggbb.substring(4,6), 16)); }
	
	/**Index for the Red Color	 */	final public static int nRed   = 0;
	/**Index for the Green Color */	final public static int nGreen = 1;
	/**Index for the Blue Color	 */	final public static int nBlue  = 2;
	
	/**Index for the Hue		 */	final public static int nHue   = 0;
	/**Index for the Satururation*/	final public static int nSatur = 1;
	/**Index for the Brightness	 */	final public static int nBright= 2;
	
	/**Index for the Hue		 */	final public static int nCyan    = 0;
	/**Index for the Satururation*/	final public static int nMagenta = 1;
	/**Index for the Brightness	 */	final public static int nYellow  = 2;

	/** Definition of the Brown Color, since not defined in the Class Color 	 */ 	
	final static public Color BROWN = new Color(128, 128, 0);
	
	/** the first 6 Colors are pure, maximum Contrast Colors 
	 * also returned by the Hue Function at integer Arguments. 
	 * The following 6 Colors are those selected for further maximum Contrast.
	 *  
	 * Black and White (usually Background Colors) should be separated with a thin Circle
	 * from the Background. 	 */
	final static public Color[] HUE_COLORS = { 
		Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, 
		Color.PINK, Color.GRAY, Color.ORANGE, BROWN, Color.BLACK, Color.WHITE};
	
	/** 
	 * @param hue the Hue (cycling around [0,6))
	 * @return the brilliant Color for this Hue
	 */
	final static public Color HUE2COLOR(int hue) {
		if (hue < 0) {
			hue = 6-hue; }
		hue %= 6;
		return HUE_COLORS[hue]; }
	
    /**Converts the components of a color, as specified by the HSB
     * model, to an equivalent set of values for the RGB model.
     * <p>
     * The integer that is returned by <code>HSB2RGB</code> encodes the
     * value of a color in bits 0&endash;23 of an integer value, the same
     * format used by the method <code>getRGB</code>. This integer can be
     * supplied as an argument to the <code>Color</code> constructor that
     * takes a single integer argument.
     * @param     hue   the hue component of the color in the Range [0,6).
     * @param     saturation   the saturation of the color in the Range [0,1).
     * @param     brightness   the brightness of the color.
     * @return    the RGB value of the color with the indicated hue,
     *                            saturation, and brightness.
     * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
     * @since     JDK1.0     */
	final static public double[] HSB2RGB(double hue, double saturation, final double brightness) {
		hue/=6;
		final double[] col = new double[3];
		if (saturation == 0) {col[nRed] = col[nGreen] = col[nBlue] = brightness; }
		else {
			saturation *= brightness;	//only used in this way!
			final double h = (hue - Math.floor(hue)) * 6;	//determines two of three colors = 6 Combinations
			final int c = (int) java.lang.Math.floor(h);
			final double f = h - c;		//determines mixture of these two colors
			final double p = brightness - saturation;	//t + q = brightness + p
			final double q = brightness - saturation * f;
			final double t = brightness - q + p;
			switch (c) {	//cyclic Rotation possible
				case 0:	col[nRed] = brightness; col[nGreen] = t; col[nBlue] = p; break;
				case 1:	col[nGreen] = brightness; col[nRed] = q; col[nBlue] = p; break;
				case 2:	col[nGreen] = brightness; col[nBlue] = t; col[nRed] = p; break;
				case 3:	col[nBlue] = brightness; col[nGreen] = q; col[nRed] = p; break;
				case 4:	col[nBlue] = brightness; col[nRed] = t; col[nGreen] = p; break;
				case 5:	col[nRed] = brightness; col[nBlue] = q; col[nGreen] = p; break;
		    }
		}
		return col; }

	/**Converts the components of a color, as specified by the CMY
	 * model, to an equivalent set of values for the RGB model.
	 * <p>
	 * The integer that is returned by <code>CMY2RGB</code> encodes the
	 * value of a color in bits 0&endash;23 of an integer value, the same
	 * format used by the method <code>getRGB</code>. This integer can be
	 * supplied as an argument to the <code>Color</code> constructor that
	 * takes a single integer argument.
	 *
	 * C = (G+B)/2 <=> R = (M+Y-C)
	 * M = (B+R)/2 <=> G = (C+Y-M)
	 * Y = (R+G)/2 <=> B = (C+M-Y)
	 *
	 * @param  hue   the hue component of the color.
	 * @param  saturation   the saturation of the color.
	 * @param  brightness   the brightness of the color.
	 * @return the RGB value of the color with the indicated Hue, Saturation, and Brightness.
	 * @see    java.awt.Color#getRGB()
	 * @see    java.awt.Color#Color(int)
	 * @since  JDK1.0     */
	public static double[] CMY2RGB(final double cyan, final double magenta, final double yellow) {
		final double[] col = new double[3];
		col[nRed  ] = magenta + yellow  - cyan;
		col[nGreen] = cyan    + yellow  - magenta;
		col[nBlue ] = cyan    + magenta - yellow;
		return col; }

    /**Converts the components of a color, as specified by the CMY
	 * model, to an equivalent set of values for the RGB model.
	 * <p>
	 * The integer that is returned by <code>CMY2RGB</code> encodes the
	 * value of a color in bits 0&endash;23 of an integer value, the same
	 * format used by the method <code>getRGB</code>. This integer can be
	 * supplied as an argument to the <code>Color</code> constructor that
	 * takes a single integer argument.
	 *
	 * C = (G+B)/2 <=> R = (M+Y-C)
	 * M = (B+R)/2 <=> G = (C+Y-M)
	 * Y = (R+G)/2 <=> B = (C+M-Y)
	 *
	 * @param     hue   the hue component of the color.
	 * @param     saturation   the saturation of the color.
	 * @param     brightness   the brightness of the color.
	 * @return    the RGB value of the color with the indicated hue,
	 *                            saturation, and brightness.
	 * @see       java.awt.Color#getRGB()
	 * @see       java.awt.Color#Color(int)
	 * @since     JDK1.0     */
	public static double[] RGB2CMY(final double red, final double green, final double blue) {
		final double[] col = new double[3];
		col[nCyan   ] = green + blue;
		col[nMagenta] = blue  + red;
		col[nYellow ] = red   + green;
		return col; }

	/**Converts the components of a color, as specified by the RGB
	 * model, to an equivalent set of values for Hue, Saturation, and
	 * Brightness, the three components of the HSB model.
	 * The brightness is the maximum color Intensity and thus in [0,1).
	 * The Saturation is the maximum color difference normed to [0,1).
	 * The Hue is the weighted maximum color normed to [0,6) and cycling 
	 * between the primary Colors: 
	 * Alternatively it could cycle through 360° with a primary Color 
	 * every 60°. 
	 * Since Hue doesn't matter for Saturation = 0 or Brightness = 0 
	 * the Colors can be represented on a Single- or Double- Cone Surface.
	 * Alternatively they can be mapped to a Sphere.    
	 * <p>
	 * If the <code>hsbvals</code> argument is <code>null</code>, then a
	 * new array is allocated to return the result. Otherwise, the method
	 * returns the array <code>hsbvals</code>, with the values put into
	 * that array.
	 * @param     red   the col[0] component of the color.
	 * @param     green the col[1] component of the color.
	 * @param     blue  the col[2] component of the color.
	 * @param     hsbvals  the array to be used to return the
	 *                     three HSB values, or <code>null</code>.
	 * @return    an array of three elements containing the hue, saturation,
	 *                     and brightness (in that order), of the color with
	 *                     the indicated col[0], col[1], and col[2] components.
	 * @see       java.awt.Color#getRGB()
     * @see       java.awt.Color#Color(int)
	 * @since     JDK1.0     */
	public static double[] RGB2HSB(double red, double green, double blue) {
		double hue, saturation, brightness;	//get the maximum and minimum Color
		int nMax; double cMax, cMin;
		if (red > green) {cMax = red  ; nMax = nRed  ; cMin = green ;}
		else			 {cMax = green; nMax = nGreen; cMin = red;}
		if (blue > cMax) {cMax = blue ; nMax = nBlue ;} else
		if (blue < cMin) {cMin = blue ;}

		double[] hsbvals = new double[3];
		if (cMax == 0) return hsbvals;

		brightness = cMax;
		saturation = cMax - cMin;
		red	  -= cMax; red  /= saturation;
		green -= cMax; green/= saturation;
		blue  -= cMax; blue	/= saturation;
		saturation /=  brightness;
		switch (nMax) {
			case nRed:	hue =     green - blue ;   break;
			case nGreen:hue = 2 + blue  - red  ;   break;
//			case nBlue:
			default:	hue = 4 + red   - green;// break;
		}
		//hue /= 6; norming to [0,6]
		if (hue < 0) hue += 6;	//norming to [0,6]

		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals; }

	/**Reduction Factor for the Brightness. Divided by 256 to give the real factor	 */
	public static int factor = 180;

	/**Creates a Pair of darker and brighter Versions of the given color.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of the color to create a brighter version of the same color.
	 * Although <code>brighter</code> and <code>darker</code> are
	 * inverse operations, the results of a series of invocations of
	 * these two methods may be inconsistent because of rounding errors.
	 * @return     a new <code>Color</code> object, a brighter version of this color.
	 * @see        java.awt.Color#darker
	 * @since      JDK1.0
	 */
	public static Point2D SHADING_PALETTE(final int rgb) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >>  8) & 0xFF;
		int b =  rgb		& 0xFF;
		int lr =  (r << 8)  / factor; if (lr > 0xFF) lr = 0xFF; //Operation becomes signed
		int lg =  (g << 8)  / factor; if (lg > 0xFF) lg = 0xFF; //in the highest bit gets involved!
		int lb =  (b << 8)  / factor; if (lb > 0xFF) lb = 0xFF;
		int dr =  (r * factor) >>  8; if (dr > 0xFF) dr = 0xFF;
		int dg =  (g * factor) >>  8; if (dg > 0xFF) dg = 0xFF;
		int db =  (b * factor) >>  8; if (db > 0xFF) db = 0xFF;
		return new Point2D((lr << 16) | (lg << 8) | lb,
						   (dr << 16) | (dg << 8) | db); }

	/**Creates a simple Set of three darker and brighter Versions of the given color.
	 * A Palette is simply an Array of Colors. 
	 */
	public static Color[] SHADING_PALETTE(final Color c) {
		Color[] Return = new Color[3];
		Return[0] = c;
		Point2D DarkBright = PaletteRGB.SHADING_PALETTE(c.getRGB());
		Return[1] = new Color(DarkBright.getX());
		Return[2] = new Color(DarkBright.getY());
		return Return; }

	/**Generates a Palette of Length n rotating through all Colors
	 * at maximum Intensity and Contrast.
	 * A Palette is simply an Array of Colors. . */
	public static Color[] CYCLE_PALETTE(final int n) {
		int step = n/3;
//		int inc = 0x300/n;	//Full number of Colors is 255*3
		int r = 255;
		int g = 0;
		int b = 0;
		Color[] P = new Color[n];
		int i;
		i = -1; while (++i < step)	{P[i]			= new Color(r,g,b); g = (i<<8)/step; r = 255-g;}
		i = -1; while (++i < step)	{P[i+step]		= new Color(r,g,b); b = (i<<8)/step; g = 255-b;}
		i = -1; while (++i < step)	{P[i+step+step] = new Color(r,g,b); r = (i<<8)/step; b = 255-r;}
		return P; }

	/**Generates a Palette of Length n rotating through all Colors
	 * at maximum Intensity and Contrast.
	 * This Palette is not as brilliant as the upper one,
	 * because of integer Errors, but it is faster to create.
	 * A Palette is simply an Array of Colors. . */
	public static Color[] CYCLE_PALETTE_QUICK(int n) {
		int step = n/3;
		int inc = 0x300/n;	//Full number of Colors is 255*3
		int r = 255;
		int g = 0;
		int b = 0;
		Color[] P = new Color[n];
		int i = -1;
		while (++i < step)	{P[i] = new Color(r,g,b); r-=inc; g+= inc;} i--; step <<=1;
		while (++i < step)	{P[i] = new Color(r,g,b); g-=inc; b+= inc;} i--;
		while (++i < n)		{P[i] = new Color(r,g,b); b-=inc; r+= inc;}
		return P; }
		
}
