package graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
  * Title: Bar3D<p>
  * Description:
  * Methods to quickly draw 2D and pseudo 3D Bars.
  * @see Graphics.Figures for
  * Arrows, Coordinate Systems with optional Ticks and Rasters,
  * Triangles, Squares, Rounded Rectangles, Balls with 3D Effect
  * Bezier Splines etc.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-05-2002, 09:45 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Bar3D {

////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////

	/**Extension Heigt of the 3DBar,
	 * this Height could be made dependant on the Height or Width of the Bar,
	 * but that would again be dependent on whether it is a vertical or horizontal Bar. */
	public static Point2D Bar3DWidth = new Point2D(20, 10);

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/**Determines, if the Top of a Bar is painted or not.	 */
	public boolean Bar3DTop = true;

	/**Determines, if the Side of a Bar is painted or not.	 */
	public boolean Bar3DSide = true;

	/**Determines, if the Bar appears raised or inset.	 */
	public boolean Bar3DRaised = true;

	/**Reference to the Graphics Context	 */
	protected AGraph2D g;

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Initializing Constructor	 */
	public Bar3D(AGraph2D g_) { g = g_; }

	//////////////////////
	//	Draw Methods	//
	//////////////////////

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void draw3DBar(int x0, int y0, int x1, int y1) {
		g.P.setX(x0); g.P.setY(y0);
		draw3DBar(x1, y1); }

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void draw3DBar(Point2D P0, Point2D P1) {
		g.P.setLocation(P0); draw3DBar(P1);}

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void draw3DBar(Point2D P1) {
		draw3DBar(P1.getX(), P1.getY());}

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void draw3DBar(int x1, int y1)
	{	//To draw the right perspective, these Relations have to be fulfilled.
		int xOut = 0, x0 = g.P.getX(); if (x1 < x0) {x0 = x1; x1 = g.P.getX();}
		int yOut = 0, y0 = g.P.getY(); if (y1 > y0) {y0 = y1; y1 = g.P.getY();}

		g. drawRect (x1, y1);	//empty Bar
		if (Bar3DSide || Bar3DTop) {
			xOut =	x1 + Bar3DWidth.getX();
			yOut =	y1 - Bar3DWidth.getY();
		}
		if (Bar3DSide) {
		g. drawLine (x1, y0, xOut,		y0 - Bar3DWidth.getY());//
		g.drawVLine (					yOut); }			//
		if (Bar3DTop) {
		g. drawLine (x0, y1, x0 + Bar3DWidth.getX(), yOut);			//
		g.drawHLine (		 xOut);}									//
		if (Bar3DSide || Bar3DTop) { g. drawLine (x1, y1); }
	}

	//////////////////////
	//	Fill Methods	//
	//////////////////////

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void fill3DBar(int x0, int y0, int x1, int y1) {
		g.P.setX(x0); g.P.setY(y0);
		fill3DBar(x1, y1); }

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void fill3DBar(Point2D P0, Point2D P1) {
		g.P.setLocation(P0); fill3DBar(P1); }

	/**Draws a Bar with Border and pseudo 3D extensions. 	 */
	public void fill3DBar(Point2D P1) { fill3DBar(P1.getX(), P1.getY()); }

	/**Draws a filled Histogram Bar with Border and pseudo 3D extensions. 	 */
	public void fill3DBar(int x1, int y1) {
		Color[] c = PaletteRGB.SHADING_PALETTE(g.getColor());
		int ya;
		int [] x = null, y = null;
		if (Bar3DSide || Bar3DTop)
		{	//To draw the right perspective, these Relations have to be fulfilled.
			if (x1 < g.P.getX()) {ya = x1; x1 = g.P.getX(); g.P.setX(ya); }
			if (y1 > g.P.getY())	{ya = y1; y1 = g.P.getY(); g.P.setY(ya); }
			x = new int [4];
			y = new int [4];
			x[0] =  x1; x1 = g.P.getX(); //Store the original Coordinates for the Sides
			y[0] =  y1; y1 = g.P.getY();
			x[1] = x[0] + Bar3DWidth.getX();
			y[1] = y[0] - Bar3DWidth.getY();
			g.fillRect  (x[0], y[0]);	//empty Bar
		} else {
			g.fillRect  (x1, y1); return; }	//empty Bar

		if (Bar3DSide) {
			x[2] = x[1]; y[3] = y1;
			x[3] = x[0]; y[2] = y1 - Bar3DWidth.getY();
			g.setColor(Bar3DRaised ? c[1] : c[2]);	//for the Side
			g.fillPolygon(x, y); }

		if (Bar3DTop) {
			y[2] = y[1]; x[3] = x1;
			y[3] = y[0]; x[2] = x1 + Bar3DWidth.getX();
			g.setColor(Bar3DRaised ? c[2] : c[1]);	//for the Top
			g.fillPolygon(x, y); }
		g.setColor(c[0]);	//to go on...
	}

	/** Middle of each Histogram Bar in 1/256th 	 */ public int Middle =   0;
	/** Width  of each Histogram Bar in 1/256th 	 */ public int Width  = 128;
	/** Depth  of each Histogram Bar in 1/256th 	 */ public int Depth  = 128;
	/** Height of each Histogram Bar in 1/256th 	 */ public int Height =  64;

	/**Draws a Histogram from the Y Values, that are already converted to Target Coordinates.
	 * Middle and Width determine the Position of the Bar and the Width of the Bar
	 * relative to the Distance of the Bars.
	 * Depth determines the depth of the Bars.
	 * @param Y List of  Values to be displayed in
	 * @param y0 Zero Line for the Histogram.
	 * @param vertical flips between a vertical or horizontal Histogram.
	 * @param filled optionally fills the Bars or draws only Wires
	 * @param ThreeD optionally displays the Bars in pseudo 3D
	 * @param Tops optionally leaves off the 3D Tops to enable Stacking
	 */
	public void Histogram (int[] Y, int y0,
		boolean vertical, boolean filled, boolean ThreeD, boolean Tops) {
		Rectangle R = g.getClipBounds();	//Returns 32767 as width and height!
		int Dist;  //{Damit das gesamte Bild draufpasst ! sonst auch Pred (Spalten) gebraeuchlich}
		if (vertical) {  Dist = R.width /Y.length;
		} else {         Dist = R.height/Y.length; }
		int width	 = ((Dist *Width ) >>8);
		int middle	 = ((Dist *Middle) >>8);
		Bar3DWidth.setX(((width*Depth ) >>8));
		Bar3DWidth.setY(((width*Height) >>8));
		Point2D P0;
		Point2D P1;
		if (vertical) {
			P0 = new Point2D(R.x + middle - Dist, y0); //R.y + R.height);
			P1 = new Point2D(P0.getX() + width, 0);
		} else {
			P0 = new Point2D(0, middle-Dist + Bar3DWidth.getY());
			P1 = new Point2D(y0, P0.getY() + width);	//(R.x, ...);
		}
		Bar3DTop  = (Tops || !vertical);
		Bar3DSide = (Tops ||  vertical);
		int i = -1; while (++i < Y.length) {
			if (vertical) { P0.setX(P0.getX() + Dist); P1.setX(P1.getX() + Dist); P1.setY(Y[i]);
			} else {        P0.setY(P0.getY() + Dist); P1.setY(P1.getY() + Dist); P0.setX(Y[i]); }
			if (ThreeD) {
				if (filled) { fill3DBar (P0, P1);
				}   else    { draw3DBar (P0, P1); }
			} else {
				if (Bar3DTop) {
					if (filled) { g.fill3DRect (P0, P1, Bar3DRaised);
					}   else    { g.draw3DRect (P0, P1, Bar3DRaised); }
				} else {
					if (filled) { g.fillRect (P0, P1);
					}   else    { g.drawRect (P0, P1); }
				}
			}
		}
	}
}
