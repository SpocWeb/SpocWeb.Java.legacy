package graphic;

import graphic.example.Fractal;

import java.awt.Color;

/**
  * Paints lines, squares, triangles and polygons with bilinearly
  * interpolated scalar color gradients, against the generic
  * {@link IGraphShape} interface rather than a concrete graphics class.
  *
  * <p>A palette is required for a smooth color gradient: interpolating RGB
  * values directly looks poor, so this class interpolates integer indices
  * (or brightness on one color) and delegates the actual color lookup to
  * a palette function.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-06-2002, 08:59 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see IGraphShape
  * @see ScalarPlot the AGraph2D-specific counterpart of this class
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T12:08:02Z
  * digest: 980690755fc2b7d9ddd2fd57556fa3e44bb78d0ee57f78cdc304a2aaf198d67a
  * stale: false
  * tags: [code/chart_rendering, code/line_rasterization]
  * concepts: [Scalar Color Interpolation Plot (Revised)]
  * facets: {layer: domain, status: broken, complexity: medium}
  * -->
  */
public class ScalarPlotNew {

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Last drawn left-edge scalar value, used by {@link #ScalarSquare} to detect an identical row worth copying. */
	private int z0Old = Integer.MIN_VALUE;
	/** Last drawn right-edge scalar value, used by {@link #ScalarSquare} to detect an identical row worth copying. */
	private int z1Old = Integer.MIN_VALUE;

	/**Reference to the Graphics Context	 */
	private IGraphShape g;

	/**palette for Scalar Plots. If the palette is empty (like at the Start),
	 * the Colors are generated from the Integer Ordinates directly.	 */
	public Color[] Palette;

	/**Offset for the Colors of this palette.
	 * This Offset is being added to each Ordinate giving the Index of the Color.	 */
	public int ColorOffset;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Cache for the current Color.
	 * Problem: is it is not called
	 * whenever setColor is called on g directly
	 */
	protected int oldColor = 12345678;

	/**
	 * Sets the given Color either from the palette
	 * or directly from the Value if palette is null.
	 * @return true, when the Value has changed.
	 */
	public boolean setPaletteColor(int ColorNum) {
		if (oldColor == ColorNum) { //save some setColor calls...
			return false; } //although it doesn't help much with Fractals
			oldColor  = ColorNum;
		if (Palette == null) {
			g.setColor(ColorNum + ColorOffset);
		} else {
			g.setColor(Palette [Math.abs((ColorNum + ColorOffset) % Palette.length)]); }
		return true; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializing Constructor taking the Graphics Context 	 */
	public ScalarPlotNew(IGraphShape g_) { this(g_, null); }

	/**Initializing Constructor, palette can be null,
	 * then the Color will be generated from the Value directly.
	 */
	public ScalarPlotNew(IGraphShape g_, Color[] Palette_) {
		this.Palette = Palette_;
		this.g = g_; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**Fills a whole horizontal Row with the interpolated Value between z0 and z1.
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'.
	 *
	 * Design Decisions:
	 * Instead of setting the Color each time it changes, the Changes are accumulated,
	 * until the x-Coordinate changes.
	 * Also the Changes in the x-Coordinate are collected until the Color changes.
	 */
	public void ScalarRow (int x0, int x1, int z0, int z1, int y) {
		boolean zStep, color = false;
		int dx, dz, d;
		setPaletteColor(z0);
		if (z1 == z0) {
			g.drawHLine(x0, x1, y); return; }	//faster Routines, don't use Clipping
		if (x1 < x0) //Linien werden unabh. von der Reihenfolge der Punkte gezeichnet
		{	//man kann x0 stets inkrementieren,statt etwas zu addieren
			d = x1; x1 = x0; x0 = d;
			d = z1; z1 = z0; z0 = d;
		}
		dx = x1-x0;	//positive!
		dz = (zStep = (z1 > z0)) ? z1-z0 : z0-z1;
		d = dx-dz; int x = x0;	//Set the Start to the Start Point so that at least the last Draw Action
		while (x0 < x1) //no longer guaranteed to come along x0 AND z0 at the same time!
		{	//no clipping anymore!
			if (d <  0) { //
				int n1 = 1- d/dx; z0+= zStep ? n1 : -n1; d += n1*dx; color = true; }
			if (d >= 0) {
				x0++; d -= dz;
				if (color) {	//Wait and accumulate Changes in Color and x-Coordinate
					setPaletteColor(z0);
					if (x0 > x+1) {
						g.drawHLine(x, x0, y); 
					} else {
						g.setPixel(x0, y); }	//Optimization: for a single Pixel don't call drawLine!
					x = x0; color = false;
				}
			}
		}
		g.drawHLine(x, x1, y);
		//g.drawHLine(x1);
	}

	/**Fills a whole Rectangle ((x0, y0), (x1, y1))with the interpolated Values
	 * between z00, z01, z10 and z11, which lie (TL, TR, BL, BR). */
	public void ScalarSquare (int x0, int x1, int y0, int y1, int z00, int z01, int z10, int z11) {
		int dy, dz0, dz1, z0Step, z1Step, d0, d1;

		if (y1 < y0) { //Linien werden unabh. von der Reihenfolge der Punkte gezeichnet
			//man kann y0 stets inkrementieren,statt etwas zu addieren
			d0 =  y1;  y1 =  y0;  y0 = d0;
			d0 = z01; z01 = z00; z00 = d0;
			d0 = z11; z11 = z10; z10 = d0;
		}

		dy = y1-y0;	//positive!
		if (z01 > z00)	{dz0 = (z01-z00); z0Step = +1;}
		else			{dz0 = (z00-z01); z0Step = -1;}
		if (z11 > z10)	{dz1 = (z11-z10); z1Step = +1;}
		else			{dz1 = (z10-z11); z1Step = -1;}
		d0 = dy-dz0;
		d1 = dy-dz1;
		while (y0 < y1) { //no longer guaranteed to come along y0 AND z0 at the same time!
			//no clipping anymore!
			if (d0 <  0) { z00+=z0Step; d0 += dy; }
			if (d1 <  0) { z10+=z1Step; d1 += dy; }
			if((d0 >= 0)
			&& (d1 >= 0)){
				y0++;   d0 -= dz0;
				int y = y0;	d1 -= dz1;
				if ((z0Old == z00) &&
					(z1Old == z10) &&
					(z00   != z10)) {
					((Graph2D)g).g.copyArea(x0, y0-1, x1-x0, 1, 0, 1);
				} else {
					ScalarRow (x0, x1, z00, z10, 0, 0, y); }
				z0Old =  z00;
				z1Old =  z10; }
		}
	}

	/**Paints the 2D rastered Scalar Values.
	 * Values between the Points are interpolated.
	 *
	 * The Picture is painted columnwise.
	 *
	 * Design Decisions:
	 * For the Interpolation, a bilinear one is chosen,
	 * and implemented based on the Line Drawing Algorithm for integer Numbers.
	 * 
	 * @param xP the rastered x Values
	 * @param yP the rastered y Values
	 * @param zP the rastered z Values over the Raster
	 */
	public void ScalarGrid(int[] xP, int[] yP, int[][]zP) {
		//first fill up all the Colummns
		int[] c0, c1;
		int x0, x1, y0, y1, z00, z01, z10, z11;
		int x = 0; x1 = xP[0]; c1 = zP[0];
		while (++x < zP.length) {
			c0 = c1; c1 = zP[x]; z01 = c0[0];
			x0 = x1; x1 = xP[x]; z11 = c1[0];
			int y = 0; y1 = yP[0];
			while (++y < yP.length) {
				y0 = y1; y1 = yP[y];
				z00 = z01; z01 = c0[y];
				z10 = z11; z11 = c1[y];
				ScalarSquare(x0, x1, y0, y1, z00, z01, z10, z11);
			}
		}
	}


	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))
	 * with the interpolated Values of z0, z1 and z2.
	 * This is also sufficient for Phong shading, where the Cosines of the Normal Vectors
	 * at the Corners are interpolated.
	 * Of course you have to break up larger Polygons into Triangles,
	 * which only works for convex Polygons.
	 * This is derived from the fillTriangle Routine from 'Graph2D.AGraph2D'
	 * and considerably easier to extend to Polygon Filling. */
	public void ScalarTriangle (int x0, int x1, int x2,
								int y0, int y1, int y2,
								int z0, int z1, int z2) {
		int tmp;	 //Sort so that y0 is th upper and y2 is the lower Point
		if (y1 < y0) {tmp = y1; y1 = y0; y0 = tmp; tmp = x1; x1 = x0; x0 = tmp; tmp = z1; z1 = z0; z0 = tmp;}
		if (y2 < y0) {tmp = y2; y2 = y0; y0 = tmp; tmp = x2; x2 = x0; x0 = tmp; tmp = z2; z2 = z0; z0 = tmp;}
		if (y2 < y1) {tmp = y2; y2 = y1; y1 = tmp; tmp = x2; x2 = x1; x1 = tmp; tmp = z2; z2 = z1; z1 = tmp;}
		//Clipping has to take Place on the Pixel Level for ScalarTriangle
		int dy1 = y1-y0;	//not negative!
		int dy2 = y2-y0; if (dy2 == 0) return;	//positive! otherwise just a horizontal Line!
		int dy3 = y2-y1;	//not negative!
		int dx1, dx2, dx3, dz1, dz2, dz3;
		boolean xStep1, xStep2, xStep3, zStep1, zStep2, zStep3;
		dx1 = (xStep1 = (x1 > x0)) ? x1-x0 : x0-x1;
		dx2 = (xStep2 = (x2 > x0)) ? x2-x0 : x0-x2;
		dx3 = (xStep3 = (x2 > x1)) ? x2-x1 : x1-x2;
		dz1 = (zStep1 = (z1 > z0)) ? z1-z0 : z0-z1;
		dz2 = (zStep2 = (z2 > z0)) ? z2-z0 : z0-z2;
		dz3 = (zStep3 = (z2 > z1)) ? z2-z1 : z1-z2;
		int d1  = dy1-dx1; int Px1 = x0;
		int d2  = dy2-dx2; int Px2 = x0;
		int d3  = dy3-dx3; int Px3 = x1;
		int dd1 = dy1-dz1; int Pz1 = z0;
		int dd2 = dy2-dz2; int Pz2 = z0;
		int dd3 = dy3-dz3; int Pz3 = z1;
		int y = y0; //Set one Pixel
		while(++y < y2)		//Now paint from the upper Point to the Middle and from there to the End
		{	//no clipping anymore! yStep == 1
			d1 -= dx1; dd1-= dz1;	//added because of the autoIncrement ++y
			d2 -= dx2; dd2-= dz2;
			if (d2 <  0) {int n2 = 1- d2/dy2; Px2+= xStep2 ? n2 : -n2; d2 += n2*dy2;}
			if (dd2<  0) {int n2 = 1-dd2/dy2; Pz2+= zStep2 ? n2 : -n2; dd2+= n2*dy2;} if ((y != y1)	&& (dy1 != 0)) {
			if (d1 <  0) {int n1 = 1- d1/dy1; Px1+= xStep1 ? n1 : -n1; d1 += n1*dy1;}
			if (dd1<  0) {int n1 = 1-dd1/dy1; Pz1+= zStep1 ? n1 : -n1; dd1+= n1*dy1;} }
			else {xStep1 = xStep3; dx1 = dx3; Px1 = Px3; d1  = d3 ; dy1 = dy3;
				  zStep1 = zStep3; dz1 = dz3; Pz1 = Pz3; dd1 = dd3;}	//Change the Step Sizes in x, because now we go back to x3
			ScalarRow(Px1, Px2, Pz1, Pz2, y);	//draw a horizontal Line!
		}
	}

	/**Fills a whole Polygon (x[i], y[i]) with the interpolated Values of z[i].
	 * This is also sufficient for Phong shading, where the Cosine of the Normal Vectors
	 * at the Corners are interpolated.
	 * Breaks up Polygons into Triangles,
	 * which only works for convex Polygons*/
	public void ScalarPolygon (int[] x, int[] y, int[] z) {
		int m = 0, n = 1; 	//do a Loop over all Points with Index > 0
		while (++n < x.length) {
			++m;
			ScalarTriangle (
				x[0], x[m], x[n],
				y[0], y[m], y[n],
				z[0], z[m], z[n]); }
	}

	/**Fills a whole Polygon (x[i], y[i]) with the interpolated Values of z[i], 
	 * by using a calculated middle Point as a Center.
	 * This suffices also for warped Squares. 
	 * This is also sufficient for Phong shading, where the Cosine of the Normal Vectors
	 * at the Corners are interpolated.
	 * Breaks up larger Polygons into Triangles,
	 * which only works for convex Polygons*/
	public void ScalarMidPolygon (int[] x, int[] y, int[] z) {
		int xm = x[0];
		int ym = y[0];
		int zm = z[0];
		int m = 0; //Create the middle Point with a Loop over all Points
		while (++m < x.length) {
			xm += x[m];
			ym += y[m];
			zm += z[m]; }
		xm/=x.length;
		ym/=x.length;
		zm/=x.length;
		int n = -1; m = x.length-1;
		while (++n < x.length) {
			ScalarTriangle (
				xm, x[m], x[n],
				ym, y[m], y[n],
				zm, z[m], z[n]); m = n; }
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/** Paints the Picture of Integer Values with 1:1 Mapping of Values and Pixels 
	 *  The Picture is painted columnwise.
	 */
	public void paintPicture(final Point2D Offset, final int[][]zP) {
		for (int x = -1; ++x < zP.length; ) {
			final int[] column = zP[x];
			for (int y = -1; ++y < zP[0].length;) {
				g.setPixel(Offset.x+x, Offset.y+y, column[y]);
			}
		}
	}

	/**Draws a Line (any angle) with the interpolated Value between both Points.
	 * @see AGraph2D#drawTriangle(int, int, int, int) 
	 */
	public void ScalarLine (int x0, int x1, int y0, int y1, int z0, int z1) {
		int tmp;	 //Sort so that y0 is th upper and y1 is the lower Point
		if (y1 < y0) {tmp = y1; y1 = y0; y0 = tmp;
					  tmp = x1; x1 = x0; x0 = tmp;
					  tmp = z1; z1 = z0; z0 = tmp;}
		//Clipping has to take Place on the Pixel Level for ScalarTriangle
		int dy1 = y1-y0;	//not negative!
		int dx1, dz1;
		boolean xStep1, zStep1;
		dx1 = (xStep1 = (x1 > x0)) ? x1-x0 : x0-x1;
		dz1 = (zStep1 = (z1 > z0)) ? z1-z0 : z0-z1;
		int d1  = dy1-dx1; int Px1 = x0, Px2 = x0;
		int dd1 = dy1-dz1; int Pz1 = z0, Pz2 = z0;
		int y = y0-1; //include this one Pixel
		while(++y < y1)		//Now paint from the upper Point to the Middle and from there to the End
		{	//no clipping anymore! yStep == 1	adding below because of the autoIncrement ++g.P.y
			d1 -= dx1; dd1-= dz1;	//The Processing is moved over to the ScalarRow Routine!
			if (d1 <  0) {int n1 = 1- d1/dy1; Px2+= xStep1 ? n1 : -n1; d1 += n1*dy1;}
			if (dd1<  0) {int n1 = 1-dd1/dy1; Pz2+= zStep1 ? n1 : -n1; dd1+= n1*dy1;}
			ScalarRow(Px1, Px2, Pz1, Pz2, 0, 0, y); Px1 = Px2; Pz1 = Pz2;	//draw a horizontal Line!
		}
		g.setPixel(x1, y1, z1); //Set the current Point to the End Point
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**Fills a whole horizontal Row with the interpolated Value
	 * between z0 and z1 (Color) and between u0 and u1 (any other Value).
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'.
	 *
	 * Design Decisions:
	 * Instead of setting the Color each time it changes, the Changes are accumulated,
	 * until the x-Coordinate changes.
	 * Also the Changes in the x-Coordinate are collected until the Color changes.
	 */
	public void ScalarRow	(int x0, int x1, int z0, int z1, int u0, int u1, int y) {
		boolean color = false;
		int dx, dz, du, zStep, uStep, zd, ud, d;
		setPaletteColor(z0);
		if (z1 == z0) { g.drawHLine(x0, x1, y); return; }	//faster Routines, don't use Clipping
		if (x1 <  x0) { //Linien werden unabh. von der Reihenfolge der Punkte gezeichnet
			//man kann x0 stets inkrementieren,statt etwas zu addieren
			d = x1; x1 = x0; x0 = d;
			d = z1; z1 = z0; z0 = d;
			d = u1; u1 = u0; u0 = d;
		}
		dx = x1-x0;	//positive!
		if (z1 > z0){dz = (z1-z0); zStep = +1;}
		else		{dz = (z0-z1); zStep = -1;}
		if (u1 > u0){du = (u1-u0); uStep = +1;}
		else		{du = (u0-u1); uStep = -1;}
		zd = dx-dz; ud = dx-du;
		g.setPixel(x0, y, z0); //Set the Start to the Start Point so that at least the last Draw Action
		while (x0 < x1) { //no longer guaranteed to come along x0 AND z0 at the same time!
			//no clipping anymore!
			if (ud <  0) {u0+=uStep; ud += dx;}
			if (zd <  0) {z0+=zStep; zd += dx; color = true;}
			if (zd >= 0) {x0++	   ; zd -= dz; ud -= du;
				if (color) { 	//only zd (Color) decides about stepping forward!
					setPaletteColor(z0);
					// TODO: LOGIC: calls the single-arg drawHLine(x0), which
					// draws from the graphics context's *current* x position -
					// but unlike the 6-arg ScalarRow overload above, this method
					// never tracks or sets a "segment start" x via g.P.x (it
					// only ever calls the stateless g.setPixel(x, y, z) form).
					// The resulting line is drawn from whatever x position g
					// happens to hold, not from this segment's actual start.
					g.drawHLine(x0);
					color = false;
				}
			}
		}
		g.drawHLine(x1);
	}

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))
	 * with the interpolated Values of z0, z1 and z2.
	 * This is also sufficient for Phong shading, where the Cosines of the Normal Vectors
	 * at the Corners are interpolated.
	 * Of course you have to break up larger Polygons into Triangles,
	 * which only works for convex Polygons.
	 * This is derived from the fillTriangle Routine from 'Graph2D.AGraph2D'
	 * and considerably easier to extend to Polygon Filling. */
//	Public void ScalarTriangle (int x0, int x1, int x2, int y0, int y1, int y2, int z0, int z1, int z2) {
//		ScalarTriangle (x0, x1, x2, y0, y1, y2, z0, z1, z2, 0, 0, 0);}

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))
	 * with the interpolated Values of z0, z1 and z2.
	 * This is also sufficient for Phong shading, where the Cosines of the Normal Vectors
	 * at the Corners are interpolated.
	 * A second Value u can be interpolated concurrently. 
	 * You have to break up larger Polygons into Triangles,
	 * which only works for convex Polygons.
	 * This is derived from the fillTriangle Routine from 'Graph2D.AGraph2D'
	 * and considerably easier to extend to Polygon Filling. */
	public void ScalarTriangle (int x0, int x1, int x2,
								int y0, int y1, int y2,
								int z0, int z1, int z2,
								int u0, int u1, int u2) {
		int tmp;	 //Sort so that y0 is th upper and y2 is the lower Point
		if (y1 < y0) {tmp = y1; y1 = y0; y0 = tmp; tmp = x1; x1 = x0; x0 = tmp; tmp = z1; z1 = z0; z0 = tmp;}
		if (y2 < y0) {tmp = y2; y2 = y0; y0 = tmp; tmp = x2; x2 = x0; x0 = tmp; tmp = z2; z2 = z0; z0 = tmp;}
		if (y2 < y1) {tmp = y2; y2 = y1; y1 = tmp; tmp = x2; x2 = x1; x1 = tmp; tmp = z2; z2 = z1; z1 = tmp;}
		//Clipping has to take Place on the Pixel Level for ScalarTriangle
		int dy1 = y1-y0;	//not negative!
		int dy2 = y2-y0; if (dy2 == 0) return;	//positive! otherwise just a horizontal Line!
		int dy3 = y2-y1;	//not negative!
		int dx1, dx2, dx3,
			dz1, dz2, dz3,
			du1, du2, du3,
			xd1, xd2, xd3,
			zd1, zd2, zd3,
			ud1, ud2, ud3,
			Px1, Px2, Px3,
			Pz1, Pz2, Pz3,
			Pu1, Pu2, Pu3;
		boolean xStep1, xStep2, xStep3,
				zStep1, zStep2, zStep3,
				uStep1, uStep2, uStep3;
		dx1 = (xStep1 = (x1 > x0)) ? x1-x0 : x0-x1;
		dx2 = (xStep2 = (x2 > x0)) ? x2-x0 : x0-x2;
		dx3 = (xStep3 = (x2 > x1)) ? x2-x1 : x1-x2;
		dz1 = (zStep1 = (z1 > z0)) ? z1-z0 : z0-z1;
		dz2 = (zStep2 = (z2 > z0)) ? z2-z0 : z0-z2;
		dz3 = (zStep3 = (z2 > z1)) ? z2-z1 : z1-z2; //if (C3){
		du1 = (uStep1 = (u1 > u0)) ? u1-u0 : u0-u1;	//Leave this Initialization in,
		du2 = (uStep2 = (u2 > u0)) ? u2-u0 : u0-u2; //because it
		du3 = (uStep3 = (u2 > u1)) ? u2-u1 : u1-u2;
		boolean C3 = (du1 != 0) || (du2 != 0);//  || (du3 != 0);	//because of Transitivity!
		ud1 = dy1-dz1; Pu1 = u0;
		ud2 = dy2-dz2; Pu2 = u0;
		ud3 = dy3-dz3; Pu3 = u1;//}
		xd1 = dy1-dx1; Px1 = x0;
		xd2 = dy2-dx2; Px2 = x0;
		xd3 = dy3-dx3; Px3 = x1;
		zd1 = dy1-dz1; Pz1 = z0;
		zd2 = dy2-dz2; Pz2 = z0;
		zd3 = dy3-dz3; Pz3 = z1;
		int y = y0; //Set one Pixel
		while(++y < y2) {	//Now paint from the upper Point to the Middle and from there to the End
			//no clipping anymore! yStep == 1
			xd1 -= dx1; xd2 -= dx2;
			zd1 -= dz1; zd2 -= dz2; if (C3){
			ud1 -= du1; ud2 -= du2;
			if (ud2 <  0) {int n2 = 1- ud2/du2; Pu2+= uStep2 ? n2 : -n2; ud2+= n2*du2;} }
			if (xd2 <  0) {int n2 = 1- xd2/dy2; Px2+= xStep2 ? n2 : -n2; xd2+= n2*dy2;}
			if (zd2 <  0) {int n2 = 1- zd2/dy2; Pz2+= zStep2 ? n2 : -n2; zd2+= n2*dy2;} if ((y != y1) && (dy1 != 0)) {
			if (xd1 <  0) {int n1 = 1- xd1/dy1; Px1+= xStep1 ? n1 : -n1; xd1+= n1*dy1;}
			if (zd1 <  0) {int n1 = 1- zd1/dy1; Pz1+= zStep1 ? n1 : -n1; zd1+= n1*dy1;} if (C3)
			if (ud1 <  0) {int n1 = 1- ud1/du1; Pu1+= uStep1 ? n1 : -n1; ud1+= n1*du1;} }
			else {	xStep1 = xStep3; dx1 = dx3; Px1 = Px3; xd1 = xd3; dy1 = dy3;
					zStep1 = zStep3; dz1 = dz3; Pz1 = Pz3; zd1 = zd3; if (C3){	//Change the Step Sizes in x, because now we go back to x3
					uStep1 = uStep3; du1 = du3; Pu1 = Pu3; ud1 = ud3;} }
			ScalarRow(Px1, Px2, Pz1, Pz2, Pu1, Pu2, y);	//draw a horizontal Line!
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////


	/**
	 * This Picture is iteratively refined by a 2 dimensional Block Routine.
	 * This allows for a rapid first impression of the Picture.
	 */
//	public void refineRaster2D(Point2D Offset, int[][]zP, Color[] palette, int ColorOffset, boolean fillBlock) {

	/**
	 * This Picture is iteratively refined by a 2 dimensional Block Routine.
	 * This allows for a rapid first impression of the Picture.
	 * @return the Minimum and Maximum Value of the given Function.
	 */
	public Point2D refineRaster2D(Point2D widths, Point2D offset, IPoint2DFunction painter, boolean fillBlock) {
		final Point2D SF  = new Point2D();	//Coordinate for the Painter
//		final Point2D WW  = new Point2D(zP.length, zP [0].length);	//Width
		final Point2D PP  = new Point2D();	//PP = SF + Offset Coordinate for the Screen
		final Point2D end = new Point2D();	//
		final Point2D stop= new Point2D(offset); stop.addAt(widths);	//The lower Bound of the Rectangle, should be treated by the Clip Algorithm
		final Point2D mnMx= new Point2D(Integer.MAX_VALUE, Integer.MIN_VALUE);	//Min and Max Value
		int SR = 1 << ((widths.x > widths.y) ? 	//Step Raster...
			Fractal.Octave(widths.x) : //always an integer Power of 2
			Fractal.Octave(widths.y));
		while (SR > 1) {   //{das immer weiter verfeinert wird}
			final int mask = SR-1; SR >>= 1;
//			((IRaster) painter).setRaster(SR, Mask, Picture);
			SF.x = 0; PP.x = offset.x; //full nested x*y Loop
			while (SF.x < widths.x) {
				SF.y = 0; PP.y = offset.y;
//				((IRaster) painter).startRow(); column = zP[SF.x];
				if (fillBlock) {
					end.x = PP.x + SR;
					if (end.x > stop.x) {
						end.x = stop.x; }
						end.x--; }
				while (SF.y < widths.y) { 
					if ( ((SF.x | SF.y) & mask) != 0 ) { //{Punkte,die schon gemalt wurden,auslassen}
//						Picture[SF.x][SF.y] =
						paintRasterBlock(painter, fillBlock, SF, PP, end, stop, mnMx, SR);
					}
					SF.y+=SR;
					PP.y+=SR;
				}
				SF.x+=SR;
				PP.x+=SR;
			}
		}
		return mnMx; 
	}

	/**
	 * Paints one raster block: samples {@code painter} at {@code SF}, tracks
	 * the min/max color seen in {@code mnMx}, and either fills the block's
	 * rectangle or sets a single pixel.
	 *
	 * @see #refineRaster2D(Point2D, Point2D, IPoint2DFunction, boolean) uses this exclusively
	 * to paint a single Rectangle
	 */
	private void paintRasterBlock(final IPoint2DFunction painter, final boolean fillBlock, final Point2D SF,
		final Point2D PP, final Point2D end, final Point2D stop, final Point2D mnMx, final int SR) {
		final int newColor = painter.getValue(SF) + ColorOffset;	//Modulo Operation is expensive!
		if (mnMx.x > newColor) { //calculate Minimum
			mnMx.x = newColor; }
		if (mnMx.y < newColor) { //and Maximum
			mnMx.y = newColor; } //of the Color Range!
		setPaletteColor(newColor);
		if (fillBlock) {
			end.y = PP.y + SR;
			if (end.y > stop.y) {
				end.y = stop.y; }
				end.y--;
			g.fillRect(PP, end);
		} else { //only set individual Pixels
			g.setPixel(PP); }
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + ScalarPlotNew.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

	/**
	 * Demonstrates filling an Area with an interpolated Color
	 * indicating another Dimension e.g. the Height.
	 */
	public static void paintScalarObjects(AGraph2D g, Color[] Palette) {
		ScalarPlotNew SP = new ScalarPlotNew(g, Palette);
		g.P.y = 200;
		SP.ScalarRow     (  0, 600,   0, 299, 200);	//continuous Coloring of a horizontal Line
		SP.ScalarLine    (  0, 600, 100, 700, 0, 299);	//continuous Coloring of a Line
		SP.ScalarSquare  (  0, 600, 100, 700, 0, 240, 240, 299); 	//continuous Coloring of a Square
		SP.ScalarTriangle(200, 100, 300, 200, 300, 400, 200, 0, 299);	//This is the colorful Triangle ;-)

		int[] xPt = new int[14];
		int[] yPt = new int[14];
		int[][]zP = new int[xPt.length][yPt.length];
		int Z1 = -1;
		while (++Z1 < xPt.length) {
			Palette[Z1*20]=new Color(0);	//use black to indicate Lines of equal Height
			xPt[Z1]=Z1*50;
			yPt[Z1]=Z1*50;
			int Z2 = -1;
			while (++Z2 < yPt.length) zP[Z1][Z2] = (int)(150*(1+Math.sin(Z1*0.5)*Math.sin(Z2*0.5)));
		}
		SP.ScalarGrid(xPt, yPt, zP);	//This is the large colorful SinX*SinY Scalar Plot!
	}

}
