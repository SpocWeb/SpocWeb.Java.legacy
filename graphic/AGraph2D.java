package graphic;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

import math.matrix.MatrixInt;
import math.vector.VectorShort;

/**
 * This Graphics Object implements most of the Methods itself,
 * because today most Graphics Devices bring along optimized Versions
 * of these Routines, that would rather be used than theses here.
 * 
 * So the Owner of these Routines is the Graphics Device
 * rather than the Graphical Object, which uses the Device to draw itself.
 * 
 * The setPixel and drawLine Routines work with several possibleFilters:
 *	-clipping
 *	-Patterns
 *	-Color Patterns
 * Therefore I could have implemented them with Pipes.
 * 
 * Design Decisions:
 * drawLine(Point2D) is sensitive to 'null' and breaks drawing a closed Line.
 * This allows for data driven breaking of Lines,
 * elegant Code in Loops and is more error tolerant. 
 * 
 * Setting the current Color to null skips painting and should be optimized 
 * at any Level of Painting (Pixel, Line, Areas etc.)! 
 * 
 * With the many Parameters of Graphics, Textures and Font, 
 * it is necessary to find a good API that separates frequent from relatively static Parameters
 * and puts the static Parameters into the State of the Graphics Device 
 * avoiding long, slow and unreadable Parameter Lists, 
 * but requiring an occasional setting of a Property. 
 * 
 * The current Position will frequently vary but is still retained 
 * to allow for relative Drawing like in Turtle Graphics. 
 * Current Font, Brush, Color and Texture are also retained. 
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:19:08Z
 * digest: b6956ff6f7cdbd8acae17bd7e2e40f888e423c552bd244e5f9b48add6c23b838
 * stale: false
 * tags: [code/graphics, code/rasterization]
 * concepts: [Polygon/Line Rasterization Base Class]
 * facets: {layer: infrastructure, status: broken, complexity: high}
 * -->
 */
public abstract class AGraph2D
extends AGraphTurtle //java.awt.Graphics	//Factory not under Control! 
implements IGraphShape {

	/** Flushes any asynchronously drawn Graphics 	 */
	//public void flush(){ }
	
	//Conversion Routines for Polygons.

	/**Converts a single Array with Point2D Elements
	 * into a 2Dim Array of Integers.
	 */
	public static int[][] convertPolygon(Point2D[] P, int Start) {
		int[][] ret = new int[2][P.length];
		int len = convertPolygon(P, Start, ret);
		if (len < P.length) { //a null encountered
			int[][] tmp = new int[2][len];
			System.arraycopy(ret[0], 0, tmp[0], 0, len);
			System.arraycopy(ret[1], 0, tmp[1], 0, len);
			return tmp;
		}
		return ret;
	}

	/**Converts an Array with Point2D Elements
	 * into a 2Dim Array of Integers 
	 * for driving the Standard Polygon Routines of java.awt.Graphics. 
	 * The first Null Element in the Array breaks the Polygon. 
	 * To start with a new Polygon, add the return Value to Start
	 * and call this Method again.  
	 * 
	 * @param P
	 * @param Start
	 * @param Points
	 * @return the number of Points read
	 */
	public static int convertPolygon(Point2D[] P, int Start, int[][] Points) {
		int Length = P.length;
		Point2D Pt;
		int j = -1;
		int i = Start - 1;
		while (++i < Length) {
			if ((Pt = P[i]) == null) {
				break;
			}
			Points[0][++j] = Pt.getX();
			Points[1][j] = Pt.getY();
		}
		return j + 1; //+1 to skip the null Element
	}

	/**Resizes the Polygon in Place by the given X and Y factors.	 */
	final static public int [][] sizePolygonAt(int [][] Polygon, int X, int Y) {
		int i = -1;
		// TODO: LOGIC: this checks "X != 1" twice instead of "X != 1 || Y != 1"
		// (contrast with movePolygonAt's correct "(X != 0) || (Y != 0)" just
		// below); when X == 1 but Y != 1, the whole condition is false and the
		// resize loop is skipped, so the Y scale factor is silently never
		// applied.
		if ((X != 1) ||
			(X != 1))
			while (++i < Polygon.length) {
				int [] tmp = Polygon[i];
				tmp[0]*=X;
				tmp[1]*=Y;
			}
		return Polygon; }

	/** moves the Polygon in Place	 */
	final static public int [][] movePolygonAt(int [][] Polygon, int X, int Y) {
		int i = -1;
		if ((X != 0) ||
			(Y != 0))
			while (++i < Polygon.length) {
				int [] tmp = Polygon[i];
				tmp[0]+=X;
				tmp[1]+=Y;
			}
		return Polygon; }

	/** moves the Polygon in Place	 */
	final static public int [][] movePolygon(int [][] Polygon, int X, int Y) {
		return movePolygonAt(MatrixInt.COPY_POLYGON(Polygon), X, Y); }

	/** moves the Polygon in Place	 */
	final static public int [][] sizePolygon(int [][] Polygon, int X, int Y) {
		return sizePolygonAt(MatrixInt.COPY_POLYGON(Polygon), X, Y);}

	////////////////////////////////////////////////////////////////////////////
	/// #region: Member Variables
	////////////////////////////////////////////////////////////////////////////

	/**Switches Clipping on	 */
	public boolean clip = true;

	/** Clipping Area: Top Left Border
	  * It is always TL.x < BR.x and TL.y < BR.y	 */
	protected final Point2D ClipTL = new Point2D();

	/** Clipping Area: Bottom Right Border
	  * It is always TL.x < BR.x and TL.y < BR.y	 */
	protected final Point2D ClipBR = new Point2D();

	/**Width of thick Lines, used by setThickPixel()	 */
	public int LineWidth = 0;

	/**Length of the Dashes in setDashPixel()	 */
	public int DashPeriod = 5;

	/**number of Bits in the Patterns (int assumed)	 */
	final static public int PenLength = 31;

	/**Pattern used to draw with a Pen,
	 * defaulted to the full Pen	 */
	public int PenPattern = GraphicPattern.FullPenPattern;

	/**Actual Pattern for this Line used to draw with a Brush,
	 * defaulted to the full Pen	 */
	protected int ActBrushPattern = GraphicPattern.FullPenPattern;

	/**Pattern used to draw with a Brush,
	 * defaulted to the full Brush.	 */
	public int[] BrushPattern = GraphicPattern.FullBrushPattern;	//new int[PenLength+1];	//full Color

	//protected Variables:

	/**Indicator of drawing a horizontal Step,
	 * used for drawing thick Lines
	 */
	protected boolean hStep = false;

	/**Indicator of drawing a vertical Step,
	 * used for drawing thick Lines
	 */
	protected boolean vStep = false;

	//	Constructors

	/**Empty Constructor	 */
	protected AGraph2D() {
		clip = false; //don't clip, this is done by the delegated Graphics Context!
	}

	/** Constructor that defines a Clipping Area	 */
	protected AGraph2D(Point2D ClipTL_, Point2D ClipBR_) {
		if ((ClipTL_ == null) ||
			(ClipTL_ == null)) { return; }
		ClipTL.copyAt(ClipTL_);
		ClipBR.copyAt(ClipBR_);
		int tmp; clip = true;	//this Sorting is necessary to speed up Clipping!
		if (ClipTL.x > ClipBR.x) { tmp = ClipTL.x; ClipTL.x = ClipBR.x; ClipBR.x =tmp; }
		if (ClipTL.y > ClipBR.y) { tmp = ClipTL.y; ClipTL.y = ClipBR.y; ClipBR.y =tmp; }
	}

	//////////////////////////
	//	SetPixel Routines	//
	//////////////////////////

	/**Sets a Pixel in the current Color at the current Position,
	 * but only if it is within the current clipping area	 */
	public void setClipPixel() {
		if ((!clip) || P.contained(ClipTL, ClipBR)) {
			setClippedPixel(col); 
		} 
	}

	/**Sets a Pixel within the current clipping areain the given Color at the current Position
	 * 
	 * @param col Color to set. 
	 */
	public abstract void setClippedPixel(final Color col);

	/**Sets a Pixel in the current Color at the current Position,
	 * but only if it is within the current clipping area	 */
	public void setPixel(final Color col) {
		setClippedPixel(col); 
	}

	/**Counts the Number of Pixels set. Used exclusively for setDashPixel */
	private int numPixels = 0;

	/**This Routine is only for user-defined Painting,
	 * because the functionality can be achieved by PenPattern.	 */
	public void setDashPixel() {
		if (++numPixels >= DashPeriod) { numPixels = 0; }	//saves Division Operation
		if (numPixels == 0) { setClipPixel(); }
	}

	/**Counts the Position of Pixels set.
	 * Used for Pens and Brushes, but also good for statistical Reasons 	 */
	private int mskPixel = 1;

	/**
	 * This Routine is only for user-defined Painting,
	 * because the simple test is implemented directly in drawLine
	 */
	public void setPenPatternPixel() {
		if ((mskPixel <<= 1) == 0) { mskPixel = 1; }	//saves Modulo Operation
		if ((mskPixel & PenPattern) != 0) setPixel();	//no clippling, this is done in the Line Routine!
	}

	/**
	 * This Routine is only for user-defined Painting,
	 * because the simple test is implemented directly in drawHLine
	 */
	public void setBrushPatternPixel() {
		ActBrushPattern = BrushPattern[P.y & PenLength];	//This is already done inthe HLine Routine!
		if ((P.x & ActBrushPattern) != 0) setPixel();		//no clippling, this is done in the HLine Routine!
	}

	/**This Routine is only for user-defined Painting.
	 * Sets a LineWidth Pixel in the current Color at the current Position	 */
	public void setThickPixel() {
		int x = P.x;
		int y = P.y;
		if (LineWidth > 0)	//This routine is for thick lines:
		if (vStep) drawHLine(	x-LineWidth, x+LineWidth, y	); else	//draw a horizontal Line of width
		if (hStep) drawVLine(x, y-LineWidth, y+LineWidth	); else	//draw a vertical   Line of width
					// TODO: LOGIC: the fourth argument (y1) is "x+LineWidth"
					// instead of "y+LineWidth"; the filled box's bottom edge
					// is computed from x instead of y, so the box is
					// mispositioned/mis-sized whenever x != y.
					fillRect(	x-LineWidth, y-LineWidth,
								x+LineWidth, x+LineWidth);	//draw a filled Box of the width
		P.x = x;
		P.y = y;
	}

	//////////////////////////////////////////
	//	drawHLine and drawVLine Routines:	//
	//////////////////////////////////////////

	/**Draws a horizontal Line within the Clipping Area using drawHLine() */
	public void drawClipHLine(int x1) {
		final int xt = x1; if (P.x > x1) { x1 = P.x; P.x = xt; }
		if (clip) {
			if(((P.y < ClipTL.y) == (P.y < ClipBR.y)) ||
				(P.x < ClipTL.x) == (x1  < ClipBR.x)) { P.x = xt; return; } //y coordinate is out of the Box
			if  (P.x < ClipTL.x)		P.x = ClipTL.x;
			if  (x1  > ClipBR.x)		x1  = ClipBR.x;
		}
		drawHLine(x1);
		P.x = xt;
	}

	/**Indicates filling of an Area	 */
	protected boolean filling = false;

	/**Draws a horizontal Line from the current x Position to the given x1 Position using setPixel(),
	 * considers ActBrushPattern, but does not clip anymore!
	 */
	public void drawHLine(int x1) {
		int xt = x1; if (P.x > x1) { x1 = P.x; P.x = xt; }
		int Mask = 0; P.x--;
		if (filling) {
			ActBrushPattern = BrushPattern[P.y & PenLength];
			Mask = 1 << (P.x & PenLength); }
		while (++P.x <= x1) {
			if (filling) {	//called from fillPolygon or fillRect
				if (Mask < 0) Mask = 1; else Mask <<=1;			//cyclic rolling
				if ((ActBrushPattern & Mask) != 0) setPixel();	//no clipping anymore!
			} else {	//normal horizontal Line
				if ((mskPixel <<= 1) == 0) mskPixel = 1;	//saves Modulo Operation
				if ((mskPixel & PenPattern) != 0) setPixel();	//no clipping anymore!
			}
		}
		P.x = xt;	//Now the Endpoint is the actual Coordinate
	}

	/**Draws a horizontal Line using drawClipHLine() */
	public void drawHLine(int x0, int x1, int y_) {
		P.x = x0; P.y = y_; drawClipHLine(x1); }

	/**Draws a horizontal Line using drawClipHLine() */
	public void drawHLine(Point2D P_, int x1) {
		P.setLocation(P_); drawClipHLine(x1); }

	/**Draws a vertical Line using setPixel(), also considers Clipping */
	public void drawVLine(int y1) {
		int yt = y1; if (P.y > y1) {y1 = P.y; P.y = yt;}
		if (clip) {
			if(((P.x < ClipTL.x) == (P.x < ClipBR.x)) ||
				(P.y > ClipBR.y) || (y1  < ClipTL.y)) {P.y = yt; return;} //x coordinate is out of the Box
			if  (P.y < ClipTL.y)		P.y = ClipTL.y;
			if  (y1  > ClipBR.y)		y1  = ClipBR.y;
		}
		P.y--; while (++P.y <= y1) {
			if ((mskPixel <<= 1) == 0) mskPixel = 1;	//saves Modulo Operation
			if ((mskPixel & PenPattern) != 0) setPixel();	//no clipping anymore!
		}
		P.y = yt;	//Now the Endpoint is the actual Coordinate
	}

	/**Draws a horizontal Line using drawVLine() */
	public void drawVLine(int x_, int y0, int y1) {
		P.x = x_; P.y = y0; drawVLine(y1); }

	/**Draws a horizontal Line using drawVLine() */
	public void drawVLine(Point2D P_, int y1) {
		P.setLocation(P_); drawVLine(y1); }


	//////////////////////
	//	Line Routines:	//
	//////////////////////


	/**Clips the Line between P1 and P2 only at P1
	 * using the current Clipping Area.
	 * Usually used on both P1 and P2 to clip a Line from both Sides!
	 * @param P1 is modified in Place
	 */
	public void clipP1(Point2D P1, Point2D P2) {
		if (P1.x != P2.x){
		if (P1.x < ClipTL.x) { P1.y = P2.y+(ClipTL.x-P2.x)*(P1.y-P2.y)/(P1.x-P2.x); P1.x = ClipTL.x; } else
		if (P1.x > ClipBR.x) { P1.y = P2.y+(ClipBR.x-P2.x)*(P1.y-P2.y)/(P1.x-P2.x); P1.x = ClipBR.x; } }
		if (P1.y != P2.y){
		if (P1.y < ClipTL.y) { P1.x = P2.x+(ClipTL.y-P2.y)*(P1.x-P2.x)/(P1.y-P2.y); P1.y = ClipTL.y; } else
		if (P1.y > ClipBR.y) { P1.x = P2.x+(ClipBR.y-P2.y)*(P1.x-P2.x)/(P1.y-P2.y); P1.y = ClipBR.y; } }
	}
	
	/**Draws a Line from L.getStart() to L.getStop() using setPixel()	 */
	public void drawLine(Line2D L) {
		drawLine(L.getStart(), L.getStop()); }

	/**Draws a Line from P1 to P2 using setPixel()	 */
	public void drawLine(Point2D P0, Point2D P1) {
		if (connect = (P0 != null)) {
			P.setLocation(P0); }
		drawLine(P1); }

	/**Draws a Line to the Point P1 using setPixel()
	 * Also takes 'null' as an Indicator for breaking the Line
	 * between the previous and the next Point.	 */
	public void drawLine(Point2D P1) {
		boolean old = connect;
		if (connect = (P1 != null)) {
			if (old) {
				drawLine (P1.x, P1.y);
			} else {
				P.setLocation(P1); }
		}
	}
	
	/**Draws a Line from (x0,y0) to (x1,y1) using setPixel()	 */
	public void drawLine(int x0, int y0, int x1, int y1) {
		P.x = x0; P.y = y0; drawLine(x1, y1); }

	/**Indicator whether a new Point should be connected to the old one.
	 * Corresponds to P != null in drawLine().	 */
	protected boolean connect = true;

	/**Draws a Line using setPixel()	 */
	public void drawLine(int x1, int y1) {
		if (connect) {
			int dx, dy, yStep, d;

			if (x1 == P.x) { drawVLine    (y1); return; }	//faster Routines
			if (y1 == P.y) { drawClipHLine(x1); return; }	//faster Routines

			Point2D Pt = new Point2D (x1, y1);

			if (Pt.x < P.x) //Linien werden unabh. von der Reihenfolge der Punkte gezeichnet
			{	//man kann P.x stets inkrementieren,statt etwas zu addieren
				Pt.x = P.x; P.x = x1;
				Pt.y = P.y; P.y = y1;
			}

			if (clip) {	//Clip both Ends of the Line!
				clipP1(P , Pt);
				clipP1(Pt, P ); }
			boolean bufClip = clip; clip = false;
			dx = Pt.x-P.x;	//positive!
			if (Pt.y > P.y) { dy = (Pt.y-P .y); yStep = +1; }
			else			{ dy = (P .y-Pt.y); yStep = -1; }
			d = dx-dy;
			while ((P.x != Pt.x) || (P.y != Pt.y)) {	//no clipping anymore!
				if ((mskPixel <<= 1) == 0) mskPixel = 1;	//saves Modulo Operation
				if (hStep = (d >= 0)) { P.x++     ; d -= dy; if ((mskPixel & PenPattern) != 0) setPixel ();}
				if (vStep = (d <  0)) { P.y+=yStep; d += dx; if ((mskPixel & PenPattern) != 0) setPixel ();}
			}
			clip = bufClip;
		}
		connect = true;
		P.x = x1;	//Now the Endpoint is the actual Coordinate
		P.y = y1;	//even if the Coordinates had been switched
	}
	
	/**Draws a filled fillRect with L as the Diagonal.	 */
	public void  fillRect (Line2D L) {
		fillRect(L.getStart(), L.getStop()); }
	
	/**Draws a filled fillRect from the current Position to (x1, y1).	 */
	public void  fillRect (Point2D P0, Point2D P1) {
		P.setLocation(P0); fillRect(P1); }
	
	/**Draws a filled fillRect from the current Position to (x1, y1).	 */
	public void  fillRect (Point2D P1) {
		fillRect (P1.x, P1.y); }
	
	/**Draws a filled fillRect from the current Position (P.x, P.y) to (x1, y1).	 */
	public void  fillRect (int x0, int y0, int x1, int y1) {
		P.x = x0; P.y = y0; fillRect(x1, y1); }
	
	/**Draws a filled fillRect from the current Position (P.x, P.y) to (x1, y1).	 */
	public void  fillRect (int x1, int y1) {
		int tmp;
		Point2D Pt = new Point2D (x1, y1);
//		if (clip)	//clipping pays off very well here, so always clip!
			clip(P ); //cliping the Points is sufficient!
			clip(Pt);
		boolean bufClip = clip; clip = false; filling = true; //Switch Clipping off temporarily
		if (P.y > Pt.y) { //Make P the smaller Coordinate
			tmp = Pt.y; Pt.y = P.y; P.y = tmp; }
		P.y--; tmp = P.x; //always start at the P.x Value...
		while (++P.y <= Pt.y) {
			P.x = tmp; drawHLine (Pt.x); }	//no clipping anymore
		clip = bufClip;	filling = false; //TODO: not Thread Safe!
	}

	/**Clips the point to the current Clip Range, used for Boxes and Rectangles.	 */
	private void clip(Point2D P1) {
//		if (ClipTL.x > ClipBR.x)
		if (P1.x < ClipTL.x) { P1.x = ClipTL.x; } else
		if (P1.x > ClipBR.x) { P1.x = ClipBR.x; }
		if (P1.y < ClipTL.y) { P1.y = ClipTL.y; } else
		if (P1.y > ClipBR.y) { P1.y = ClipBR.y; }
	}
	
	/**Draws a Rectangle with L as the Diagonal.	 */
	public void  drawRect (Line2D L) {
		drawRect(L.getStart(), L.getStop()); }
	
	/**Draws a Rectangle from (x0, y0) to (x1, y1).	 */
	public void  drawRect (int x0, int y0, int x1, int y1) {
		P.x = x0; P.y = y0; drawRect(x1, y1); }
	
	/**Draws a Rectangle from the current Position to (x1, y1).	 */
	public void  drawRect (final int x1, final int y1) {
		final int x0 = P.x; //Buffer the current Position, 
		final int y0 = P.y;	//because they get lost...
		drawClipHLine(x1); drawVLine(y1); drawClipHLine(x0); drawVLine(y0);	//...in the drawing action
	}
	
	/**Draws a Rectangle from (x0, y0) to (x1, y1).	 */
	public void  drawRect (final Point2D P0, final Point2D P1) { 
		P.setLocation(P0); drawRect(P1); }
	
	/**Draws a Rectangle from the current Position to (x1, y1).	 */
	public void  drawRect (final Point2D P1) {
		drawRect(P1.x, P1.y); }

	/** Draws a 3-D highlighted rectangle via {@link #Rect3D}.
	 * @see IGraphShape#draw3DRect(int, int, int, int, boolean)	 */
	public void draw3DRect(final int x, final int y, final int width, final int height, final boolean raised) {
		Rect3D(x, y, width, height, raised, false); }

	/** Draws a 3-D highlighted rectangle spanned by the two given points.
	 * @see IGraphShape#draw3DRect(int, int, int, int, boolean)	 */
	public void draw3DRect(final Point2D P0, final Point2D P1, final boolean raised) {
		Rect3D(P0.x, P0.y, P1.x-P0.x, P1.y-P0.y, raised, false); }

	/** Fills a 3-D highlighted rectangle via {@link #Rect3D}.
	 * @see IGraphShape#fill3DRect(int, int, int, int, boolean)	 */
	public void fill3DRect(final int x, final int y, final int width, final int height, final boolean raised) {
		Rect3D(x, y, width, height, raised, true); }

	/** Fills a 3-D highlighted rectangle spanned by the two given points.
	 * @see IGraphShape#fill3DRect(Point2D, Point2D, boolean)	 */
	public void fill3DRect(final Point2D P0, final Point2D P1, final boolean raised) {
		Rect3D(P0.x, P0.y, P1.x-P0.x, P1.y-P0.y, raised, true); }
	
	/**Paints a 3-D highlighted rectangle filled with the current color.
	 * The edges of the rectangle will be highlighted so that it appears
	 * as if the edges were beveled and lit from the upper left corner.
	 * The colors used for the highlighting effect will be determined from
	 * the current color.
	 * @param x the <i>x</i> coordinate of the rectangle to be filled.
	 * @param y the <i>y</i> coordinate of the rectangle to be filled.
	 * @param width the width of the rectangle to be filled.
	 * @param height the height of the rectangle to be filled.
	 * @param raised a boolean value that determines whether the
	 *        rectangle appears to be raised above the surface
	 *        or etched into the surface.
	 * @see   java.awt.Graphics#draw3DRect
	 * @since JDK1.0
	 */
	public void Rect3D(final int x, final int y, int width, int height, 
			final boolean raised, final boolean filled) {
		Color c = getColor();
		Point2D DarkBright = PaletteRGB.SHADING_PALETTE(c.getRGB());
		Color brighter = new Color(DarkBright.x);
		Color darker   = new Color(DarkBright.y);
		
		setColor(raised ? brighter : darker);
		if (filled)	{ 
			--height; --width; fillRect(x+1, y+1, x+width, y+height); }
		drawLine(x, y, x, y + height);
		drawLine(x + 1, y, x + width - 1, y);
		setColor(raised ? darker : brighter);
		drawLine(x + 1, y + height, x + width, y + height);
		drawLine(x + width, y, x + width, y + height - 1);
		setColor(c);
	}
	
	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public void drawPolygon (final int [] xP, final int [] yP, final boolean closed) {
		drawPolygon (xP, yP, closed, Math.min(xP.length, yP.length)); }
	
	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public void drawPolygon (final int [] xP, final int [] yP, final boolean closed, int n) {
		int i = n;
		if (n == 1) { setPixel(xP[0], yP[0]); return; }
		if (closed) { P.x = xP[  0]; P.y = yP[0]; }
		else		{ P.x = xP[--i]; P.y = yP[i]; }
		while (--i >= 0) {
			drawLine(xP[i], yP[i]); }
	}
	
	/** Draws the outline of the given AWT polygon.
	 * @see graphic.IGraphics#drawPolygon(java.awt.Polygon)	 */
	public void drawPolygon(final Polygon p) {
		drawPolygon(p.xpoints, p.ypoints, p.npoints); }

	/** Fills the given AWT polygon.
	 * @see graphic.IGraphics#fillPolygon(java.awt.Polygon)	 */
	public void fillPolygon(final Polygon p) {
		fillPolygon(p.xpoints, p.ypoints, p.npoints); }

	/** Draws an open polyline over the first {@code nPoints} coordinates.
	 * @see graphic.IGraphics#drawPolyline(int[], int[], int)	 */
	public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
		drawPolygon(xPoints, yPoints, false, nPoints); }

	/** Draws a closed polygon over the first {@code nPoints} coordinates.
	 * @see graphic.IGraphics#drawPolygon(int[], int[], int)	 */
	public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
		drawPolygon(xPoints, yPoints, true, nPoints); }
	
	/**Draws the Polygon and adds a closing Line2D, if wanted
	 * The Algorithm with null as Separator can be extended
	 * to the drawLine Routine, which would see null as an indicator
	 * not to connect to the last Point, but only set the Location!	 */
	public void drawPolygon (final Point2D [] Points, final boolean closed) {
		int i, n;
		if ((n = Points.length-1) == 0) {setPixel(Points[0]); return;}
		if (closed)	 {P.setLocation(Points[n]); i = -1;}
		else		 {P.setLocation(Points[0]); i =  0;}
		while (++i <= n) {
			drawLine  (Points[i]); }	//this works due to the 'null' Sensitivity of the drawLine Algorithm!
	}
	
	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public void drawPolygons (final Point2D[][] Polygons, final boolean closed) {
		int i = Polygons.length;
		while (--i >= 0) 
			drawPolygon(Polygons[i], closed); 
	}
	
	/**Draws those half of the Polygon Lines where the Indices are rising
	 * and adds a closing Line. 
	 * More efficient for connected Figures where each Side is shared by two Polygons 
	 * than to draw all Lines. 
	 */
	public void drawPolygon (final Point2D[] Points, final int[] Polygon, final boolean closed) {
		if ((Polygon == null) || 
			(Polygon.length < 1)) 
			return;
		int previous;
		int actual  = Polygon[0];	//close Polygon, subtract Offset
		for (int PLength = Polygon.length; --PLength >= 0;) {
			previous = actual;
			if ((actual = Polygon[PLength])  >  previous) {	//subtract Offset
				drawLine(Points[actual], Points[previous]); }
		}
	}
	
	/**Draws those half of the Polygons' Lines where the Indices are rising
	 * and adds a closing Line. More efficient for closed Bodies	 */
	public void drawPolygons (final Point2D[] Points, final int[][] Polygons, final boolean closed) {
		int i = Polygons.length;
		while (--i >= 0) {
			drawPolygon(Points, Polygons[i], closed); }
	}

	/**Draws the Polygon and adds a closing Line2D, if wanted	 */
	public void drawPolygon (final int [][] Pol, final boolean closed) {
		int i, n;
		if ((Pol == null) || (Pol.length == 0) || (Pol[0] == null)) { return; }
		if ((n = Pol.length-1) == 0) {setPixel(Pol[0][0], Pol[0][1]); return; }
		if (closed) { P.setLocation(Pol[n][0], Pol[n][1]); i = -1; }
		else		{ P.setLocation(Pol[0][0], Pol[0][1]); i =  0; }
		while (++i <= n) { drawLine(Pol[i][0], Pol[i][1]); }
	}

	/**Fills a whole Triangle (P0, P1, P2) row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void drawTriangle (final Point2D P1, final Point2D P2) {
		drawTriangle (P.x, P1.x, P2.x, P.y, P1.y, P2.y);}

	/**Fills a whole Triangle (P0, P1, P2) row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void drawTriangle (final Point2D P0, final Point2D P1, final Point2D P2) {
		drawTriangle (P0.x, P1.x, P2.x, P0.y, P1.y, P2.y);}

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))row-wise with the current Color.  */
	public void drawTriangle (final int x1, final int x2, final int y1, final int y2) {
		drawTriangle (P.x, x1, x2, P.y, y1, y2);}

	/**Draws a whole Triangle ((x0, y0), (x1, y1), (x2,y2)) with the current Color.  */
	public void drawTriangle (final int x0, final int x1, final int x2, final int y0, final int y1, final int y2) {
		P.x = x0; P.y = y0; drawLine (x1, y1); drawLine (x2, y2); drawLine (x0, y0);}

	/**Fills a whole Triangle (P0, P1, P2) row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void fillPolygon (final Point2D P1, final Point2D P2) {
		fillTriangle (P.x, P1.x, P2.x, P.y, P1.y, P2.y);}

	/**Fills a whole Triangle (P0, P1, P2) row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void fillTriangle (final Point2D P0, final Point2D P1, final Point2D P2) {
		fillTriangle (P0.x, P1.x, P2.x, P0.y, P1.y, P2.y);}

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void fillTriangle (final int x1, final int x2, final int y1, final int y2) {
		fillTriangle (P.x, x1, x2, P.y, y1, y2);}

	/**Fills a whole Triangle ((x0, y0), (x1, y1), (x2,y2))row-wise with the current Color.
	 * This is considerably faster than doing it with the fillPolygon Method,
	 * because it doesn't require solving linear Equations. */
	public void fillTriangle (int x0, int x1, int x2, int y0, int y1, int y2) {
		//Sort so that y0 < y1 < y2 
		if (y1 < y0) { { final int y = y1; y1 = y0; y0 = y; } { final int x = x1; x1 = x0; x0 = x; } }
		if (y2 < y0) { { final int y = y2; y2 = y0; y0 = y; } { final int x = x2; x2 = x0; x0 = x; } }
		if (y2 < y1) { { final int y = y2; y2 = y1; y1 = y; } { final int x = x2; x2 = x1; x1 = x; } }
		if (y2 < ClipTL.y) 
			return; 
		if (y0 > ClipBR.y)  
			return; 
		if (y2 == y0) 
			return; 
		//Clipping has to take Place on the Pixel Level for ScalarTriangle
		final int x3 = x0+(x2-x0)*(y1-y0)/(y2-y0); //this is the intermediate Point
		//setColor(Color.RED);
		fillTriangle(x0, y0, x1, y1, x3);
		drawHLine(x1, x3, y1);	//draw a horizontal Line2D!
		//setColor(Color.GRAY);
		fillTriangle(x2, y2, x1, y1, x3);
	}

	/** Fills a Triangle with the Line x1-x2 parallel to the y-Axis
	 * 
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param x2
	 */
	public void fillTriangle(final int x0, final int y0, 
			final int x1, final int y1, final int x2) {
		if (y0 == y1) {
			if (x0 != x1) {
				drawHLine(x0, x1, y0); }
			return; 
		}
		final int ySign; 
		if (y1 > y0) { //handle total Clipping
			ySign = 1;
			if (y0 > ClipBR.y) 
				return; 
			if (y1 < ClipTL.y) 
				return; 
		} else {
			ySign =-1;
			if (y1 > ClipBR.y) 
				return; 
			if (y0 < ClipTL.y) 
				return; 
		}
		final int dy = Math.abs(y1-y0);	//not negative!
		final boolean xSign1 = (x1 > x0); 
		final boolean xSign2 = (x2 > x0); 
		final int dx1 = Math.abs(x1-x0);
		final int dx2 = Math.abs(x2-x0);
		int d1 = dy-dx1; int Px1 = x0; //
		int d2 = dy-dx2; int Px2 = x0; //
		//Now paint from the upper Point to the Middle and from there to the End
		for (int y = y0; (y+=ySign) != y1; ) { //no partial clipping anymore! widen / narrow both Sides
			if ((d2-= dx2) <  0) { final int n2 = 1-d2/dy; Px2+= xSign2 ? n2 : -n2; d2 += n2*dy; } 
			if ((d1-= dx1) <  0) { final int n1 = 1-d1/dy; Px1+= xSign1 ? n1 : -n1; d1 += n1*dy; } 
			drawHLine(Px1, Px2, y);	//draw a horizontal Line2D!
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public void fillPolygon (final int [] xP, final int [] yP, 
			final Color borderColor, final Color innerColor, final Color backColor) {
		setColor (innerColor);
		fillPolygon (xP, yP, borderColor, backColor);
	}
	
	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public void fillPolygon (final int [] xP, final int [] yP, final Color borderColor) {
		fillPolygon (xP, yP, borderColor, getColor()); }
	
	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public void fillPolygon (final int [] xP, final int [] yP
			, final Color borderColor, final Color backColor) {
		fillPolygon (xP, yP, Math.min(xP.length, yP.length), backColor);
		if (borderColor == null) 
			return;
		Color Buffer = getColor();
		setColor (borderColor);
		drawPolygon (xP, yP, true);
		setColor (Buffer);
	}
	
	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public void fillPolygon (final int [] xP, final int [] yP) {
		fillPolygon(xP, yP, Math.min(xP.length, yP.length)); 
	}
	
	/**Fills the Polygon homogeneously with the current color	 
	 * This allows to avoid splitting the Polygon up into Triangles;  
	 * just count the Number of Intersecting Lines. 
	 * Uses the current Color for Front and Back Side of the Polygon. 
	 * @see graphic.IGraphics#fillPolygon(int[], int[], int)
	 */
	public void fillPolygon(final int[] xP, final int[] yP, final int nPoints) {
		fillPolygon(xP, yP, nPoints, getColor()); }
	
	/**Fills the Polygon homogeneously with the current color	 
	 * This allows to avoid splitting the Polygon up into Triangles;  
	 * just count the Number of Intersecting Lines. 
	 * By considering the Orientation of the Intersection, 
	 * even Front and Back can be distinguished by Color. 
	 * @param backColor different Color for the Back Side Areas of a twisted Polygon. 
	 * if null, the back Areas are not painted, allowing for Optimizations 
	 */
	public void fillPolygon(final int[] xP, final int[] yP, final int nPoints, final Color backColor) {
		final Color colCache = getColor(); 
		int y_min = yP[0];
		int y_max = y_min;
		for (int i = nPoints; --i > 0;) //Find the y-Range of the Polygon
		{	//for scanning the whole y Range with it 
			int Py = yP[i]; //2 Comparisons on Average!
			if (Py < y_min) y_min = Py; else
			if (Py > y_max) y_max = Py;
		}
		
		if ((ClipBR != null) && (ClipTL != null)) {  //clipping pays off very well here!
			if (y_max > ClipBR.y) y_max = ClipBR.y;
			if (y_min < ClipTL.y) y_min = ClipTL.y;
		}
		
		final int [] xi = new int[nPoints]; //for sorting the Coordinates
		final Point2D Pz = new Point2D();
		for (int Py = y_min; ++Py < y_max;) { //Zeilenweise Polygon fuellen, An Anfang und Ende keine echten Ueberschneidungen}
			int num = -1;	//
			int P1x = yP[0];
			int P1y = xP[0];
			for(int i = nPoints; --i >= 0;) {  //alle Kanten
				int P2x = xP[i]; 
				int P2y = yP[i]; 
				if ((P1y > Py) ^ (P2y > Py)) { //Schneidet die Kante aktuelle Zeile?
					Pz.x = P1x + (Py-P1y)*(P1x-P2x)/(P1y-P2y); //x-Koordinate des SchnittPunktes...
					Pz.x <<= 1;
					if (P1y > Py) //By considering the Orientation, 'Front' and 'Back' can be distinguished! 
						++Pz.x; //single least significant Bit is sufficient!
					++num; 
					int k = -1; do k++; while ((k != num) && (Pz.x > xi[k])); //...in geordnete Liste eintragen, linear Search used
					if (k < num) 
						System.arraycopy (xi, k, xi, k+1,(num-k)); //Platz schaffen
					xi[k] = Pz.x; //Punkt eintragen
				}
				P1x = P2x;
				P1y = P2y;
			}
			filling = true;
			//erst die vollst�ndige geordnete Liste von horizontalen Strecken zeichnen!
			for (int i = -1; ++i <= num; ) { //mit einem even-odd Algorithmus, der Zeilen bevorzugt!
				P.x = xi[i]; 
				final Color col = ((P.x & 1) == 1) ? colCache : backColor; 
				if (col == null) { ++i;
					continue; }
				setColor(col);
				P.y = Py; P.x >>= 1;
				drawClipHLine ((xi[++i] >> 1)-1); } //
			filling = false;	//immer noch Probleme, benachbarte Flaechen zu schliessen!
		} //
		setColor(colCache);
	}
	
	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public void fillPolygon (final Point2D [] P0, final Color borderColor, final Color innerColor, final Color backColor) {
		setColor (innerColor);
		fillPolygon (P0, borderColor, backColor);
	}
	
	/**Fills the Polygon and adds a BorderLine, if wanted	 */
	public void fillPolygon (final Point2D[] P0, final Color borderColor, final Color backColor) {
		fillPolygon (P0, backColor);
		if (borderColor == null) 
			return; 
		final Color buffer = getColor();
		setColor (borderColor);
		drawPolygon (P0, true);
		setColor (buffer);
	}
	
	/**Fills the Polygon homogeneously with the current color	 
	 * This allows to avoid splitting the Polygon up into Triangles;  
	 * just count the Number of Intersecting Lines. 
	 * By considering the Orientation of the Intersection, 
	 * even Front and Back can be distinguished by Color. 
	 */
	public void  fillPolygon (final Point2D[] P0) { fillPolygon (P0, getColor()); }
	
	/**Fills the Polygon homogeneously with the current color	 
	 * This allows to avoid splitting the Polygon up into Triangles;  
	 * just count the Number of Intersecting Lines. 
	 * By considering the Orientation of the Intersection, 
	 * even Front and Back can be distinguished by Color. 
	 * @param backColor different Color for the Back Side Areas of a twisted Polygon. 
	 * if null, the back Areas are not painted, allowing for Optimizations 
	 */
	public void  fillPolygon (final Point2D[] P0, final Color backColor) {
		final Color colCache = getColor(); 
		int y_min = P0[0].y;
		int y_max = y_min;
		for(int i = P0.length; --i > 0;) //Find the Polygon-Range in y coordinates.
		{	//for scanning the whole y Range with
			final Point2D Pt = P0[i];
			if (Pt.y < y_min) y_min = Pt.y; else
			if (Pt.y > y_max) y_max = Pt.y;
		};
		
		if (clip) {
			if (y_max > ClipBR.y) y_max = ClipBR.y;
			if (y_min < ClipTL.y) y_min = ClipTL.y;
		}
		
		final int [] xi = new int[P0.length];
		
		//Pt still points to the last Element
		final Point2D Pz = new Point2D();
		for (Pz.y = y_min; ++Pz.y < y_max;) { //Zeilenweise Polygon fuellen,An Anfang und Ende keine echten Ueberschneidungen
			int num = -1;	//
			Point2D PZ1 = P0[0];
			for (int i = P0.length; --i >= 0; ) { //alle Kanten
				final Point2D PZ2 = P0[i];
				if ((PZ1.y > Pz.y) ^ (PZ2.y > Pz.y)) {  //Schneidet die Kante aktuelle Zeile?
					Pz.x = PZ1.x + (Pz.y-PZ1.y)*(PZ1.x-PZ2.x)/(PZ1.y-PZ2.y); //x-Koordinate des SchnittPunktes
					Pz.x <<= 1;
					if (PZ1.y > Pz.y) //By considering the Orientation, 'Front' and 'Back' can be distinguished! 
						++Pz.x; //single least significant Bit is sufficient!
					++num; 
					int k = -1; do k++; while ((k != num) && (Pz.x > xi[k])); //SchnittPunkt in geordnete Liste eintragen, linear Search used
					if (k < num) 
						System.arraycopy (xi, k, xi, k+1,(num-k)); //Platz schaffen
					xi[k] = Pz.x;  //Punkt eintragen
				}
				PZ1 = PZ2;
			}
			filling = true;
			for (int i = -1; ++i <= num;) { //geordnete Liste von horizontalen Strecken zeichnen
				P.x = xi[i]; 
				final Color col = ((P.x & 1) == 1) ? colCache : backColor; 
				if (col == null) { ++i;
					continue; }
				setColor(col);
				P.y = Pz.y; P.x >>= 1; 
				drawClipHLine ((xi[++i] >> 1)-1); //moeglichst ein Algorithmus, der Zeilen bevorzugt
			} 
			filling = false;	//immer noch Probleme, benachbarte Flaechen zu schliessen!
		} //
		setColor(colCache);
	}
	
	//Ellipse and Circle Methods: all those Methods are delegated
	//to drawing the respective Polygons.

	/**Draws a circle or ellipse that fits within the
	 * rectangle specified by the <code>Line2D</code> argument.
	 * <p>
	 * @param L the <i>Diagonal</i> through the upper left
	 *                     and lower right corner of the oval.
	 * @see java.awt.Graphics#fillOval
	 * @since JDK1.0
	 */
	public void drawEllipse(Line2D L) {
		drawPolygon(PolyTrigon.Ellipse(L), true); }

	/**Draws an Ellipse with Center in 0 and Radiuses R  */
	public void drawEllipse (int r) {
		drawPolygon(PolyTrigon.Ellipse(r), true); }

	/**Draws an Ellipse with Center in M, Radius r
	 * and the Start and End Angles in W	 */
	public void drawEllipse (Point2D M, int r) {
		drawPolygon(PolyTrigon.Ellipse(M, r), true); }

	/**Draws an Ellipse with Center in M and Radiuses R  */
	public void drawEllipse  (Point2D M, Point2D R)
	{drawPolygon(PolyTrigon.Ellipse(M, R), true);}

	/**Draws an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void drawEllipse  (Point2D R) {
		drawPolygon(PolyTrigon.Ellipse(R), true);}

	/**Draws a regular Polygon with n Corners
	 * between the Start and End Angles in W.
	 */
	public void drawRegPoly(int n, Point2D R, Point2D W) {
		drawPolygon(PolyTrigon.RegPoly(n, R, W), true);}

	/**Draws an Arc with Radiuses R and the Start and End Angles in W	 */
	public void drawArc (Point2D R, Point2D W) {
		drawPolygon(PolyTrigon.Arc (R, W), true);}

	/**Draws an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void drawArc (Point2D M, Point2D R, Point2D W) {
		drawPolygon(PolyTrigon.Arc (M, R, W), true);}

	/**Draws an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	public void drawSector (Point2D M, Point2D R, Point2D W) {
		drawPolygon(PolyTrigon.Sector (M, R, W), true);}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void drawRoundRect (Line2D L, int r) {
		drawPolygon(PolyTrigon.RoundRect (L, r), true); }

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void drawRoundRect (Line2D Li, Point2D R) {
		drawPolygon(PolyTrigon.RoundRect (Li, R), true);}

	//filler Routines:

	/**Draws a circle or ellipse that fits within the
	 * rectangle specified by the <code>Line2D</code> argument.
	 * <p>
	 * @param L the <i>Diagonal</i> through the upper left
	 *                     and lower right corner of the oval.
	 * @see java.awt.Graphics#fillOval
	 * @since JDK1.0
	 */
	public void fillEllipse(Line2D L) {
		fillPolygon(PolyTrigon.Ellipse(L));}

	/**Draws an Ellipse with Center in 0 and Radiuses R  */
	public void fillEllipse (int r) {
		fillPolygon(PolyTrigon.Ellipse(r)); }

	/**Draws an Ellipse with Center in M, Radius r
	 * and the Start and End Angles in W	 */
	public void fillEllipse (Point2D M, int r) {
		fillPolygon(PolyTrigon.Ellipse(M, r));}

	/**Draws an Ellipse with Center in M and Radiuses R  */
	public void fillEllipse  (Point2D M, Point2D R) {
		fillPolygon(PolyTrigon.Ellipse(M, R));}

	/**Draws an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void fillEllipse  (Point2D R) {
		fillPolygon(PolyTrigon.Ellipse(R));}

	/**Draws a regular Polygon with n Corners
	 * between the Start and End Angles in W.
	 */
	public void fillRegPoly(int n, Point2D R, Point2D W) {
		fillPolygon(PolyTrigon.RegPoly(n, R, W));}

	/**Draws an Arc with Radiuses R and the Start and End Angles in W	 */
	public void fillArc (Point2D R, Point2D W) {
		fillPolygon(PolyTrigon.Arc (R, W));}

	/**Draws an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	public void fillArc (Point2D M, Point2D R, Point2D W) {
		fillPolygon(PolyTrigon.Arc (M, R, W));}

	/**Draws an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	public void fillSector (Point2D M, Point2D R, Point2D W) {
		fillPolygon(PolyTrigon.Sector (M, R, W));}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void fillRoundRect (Line2D L, int r) {
		fillPolygon(PolyTrigon.RoundRect (L, r));}

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	public void fillRoundRect (Line2D Li, Point2D R) {
		fillPolygon(PolyTrigon.RoundRect (Li, R));}


	//////////////////////////////////////////
	//	Methods from java.awt.Graphics :	//
	//////////////////////////////////////////

	/**Draws the outline of a circular or elliptical arc
	 * covering the specified rectangle.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends
	 * for <code>arcAngle</code> degrees, using the current color.
	 * Angles are interpreted such that 0&nbsp;degrees
	 * is at the 3&nbsp;o'clock position.
	 * A positive value indicates a counter-clockwise rotation
	 * while a negative value indicates a clockwise rotation.
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin
	 * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * <p>
	 * The resulting arc covers an area
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
	 * @param x the <i>x</i> coordinate of the
	 *        upper-left corner of the arc to be drawn.
	 * @param y the <i>y</i>  coordinate of the
	 *        upper-left corner of the arc to be drawn.
	 * @param width the width of the arc to be drawn.
	 * @param height the height of the arc to be drawn.
	 * @param startAngle the beginning angle.
	 * @param arcAngle the angular extent of the arc,
	 *        relative to the start angle.
	 * @see   java.awt.Graphics#fillArc
	 * @since JDK1.0
	 */
	public void drawArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle) {
		drawArc(
			new Point2D(x, y),
			new Point2D(width, height),
			new Point2D(startAngle, startAngle + arcAngle)); }

	/**Fills a circular or elliptical arc covering the specified rectangle.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends
	 * for <code>arcAngle</code> degrees.
	 * Angles are interpreted such that 0&nbsp;degrees
	 * is at the 3&nbsp;o'clock position.
	 * A positive value indicates a counter-clockwise rotation
	 * while a negative value indicates a clockwise rotation.
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin
	 * is (<i>x</i>,&nbsp;<i>y</i>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * <p>
	 * The resulting arc covers an area
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * by <code>height&nbsp;+&nbsp;1</code> pixels tall.
	 * @param x the <i>x</i> coordinate of the
	 *        upper-left corner of the arc to be filled.
	 * @param y the <i>y</i>  coordinate of the
	 *        upper-left corner of the arc to be filled.
	 * @param width the width of the arc to be filled.
	 * @param height the height of the arc to be filled.
	 * @param startAngle the beginning angle.
	 * @param arcAngle the angular extent of the arc,
	 *        relative to the start angle.
	 * @see   java.awt.Graphics#drawArc
	 * @since JDK1.0
	 */
	public void fillArc(int x, int y, int width, int height,
				 int startAngle, int arcAngle) {
		fillArc(
			new Point2D(x, y),
			new Point2D(width, height),
			new Point2D(startAngle, startAngle + arcAngle));}

	/**Draws the outline of an oval.
	 * The result is a circle or ellipse that fits within the
	 * rectangle specified by the <code>x</code>, <code>y</code>,
	 * <code>width</code>, and <code>height</code> arguments.
	 * <p>
	 * The oval covers an area that is
	 * <code>width&nbsp;+&nbsp;1</code> pixels wide
	 * and <code>height&nbsp;+&nbsp;1<code> pixels tall.
	 * @param x the <i>x</i> coordinate of the upper left
	 *        corner of the oval to be drawn.
	 * @param y the <i>y</i> coordinate of the upper left
	 *        corner of the oval to be drawn.
	 * @param width the width of the oval to be drawn.
	 * @param height the height of the oval to be drawn.
	 * @see   java.awt.Graphics#fillOval
	 * @since JDK1.0
	 */
	public void drawOval(int x, int y, int width, int height) {
		drawEllipse(
			new Point2D(x, y),
			new Point2D(width, height)); }

	/**Fills an oval bounded by the specified rectangle with the
	 * current color.
	 * @param x the <i>x</i> coordinate of the upper left corner
	 *        of the oval to be filled.
	 * @param y the <i>y</i> coordinate of the upper left corner
	 *        of the oval to be filled.
	 * @param width the width of the oval to be filled.
	 * @param height the height of the oval to be filled.
	 * @see   java.awt.Graphics#drawOval
	 * @since JDK1.0
	 */
	public void fillOval(int x, int y, int width, int height) {
		fillEllipse(new Point2D(x, y), new Point2D(width, height)); }

	/**Draws an outlined round-cornered rectangle using this graphics
	 * context's current color. The left and right edges of the rectangle
	 * are at <code>x</code> and <code>x&nbsp;+&nbsp;width</code>,
	 * respectively. The top and bottom edges of the rectangle are at
	 * <code>y</code> and <code>y&nbsp;+&nbsp;height</code>.
	 * @param x the <i>x</i> coordinate of the rectangle to be drawn.
	 * @param y the <i>y</i> coordinate of the rectangle to be drawn.
	 * @param width the width of the rectangle to be drawn.
	 * @param height the height of the rectangle to be drawn.
	 * @param arcWidth the horizontal diameter of the arc
	 *        at the four corners.
	 * @param arcHeight the vertical diameter of the arc
	 *        at the four corners.
	 * @see   java.awt.Graphics#fillRoundRect
	 * @since JDK1.0
	 */
	public void drawRoundRect(int x, int y, int width, int height,
		int arcWidth, int arcHeight) {
		drawRoundRect(new Line2D(new Point2D(x, y), new Point2D(width, height)), new Point2D(arcWidth, arcHeight));}

	/**Fills the specified rounded corner rectangle with the current color.
	 * The left and right edges of the rectangle
	 * are at <code>x</code> and <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>,
	 * respectively. The top and bottom edges of the rectangle are at
	 * <code>y</code> and <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>.
	 * @param x the <i>x</i> coordinate of the rectangle to be filled.
	 * @param y the <i>y</i> coordinate of the rectangle to be filled.
	 * @param width the width of the rectangle to be filled.
	 * @param height the height of the rectangle to be filled.
	 * @param arcWidth the horizontal diameter
	 *        of the arc at the four corners.
	 * @param arcHeight the vertical diameter
	 *        of the arc at the four corners.
	 * @see   java.awt.Graphics#drawRoundRect
	 * @since JDK1.0
	 */
	public void fillRoundRect(int x, int y, int width, int height,
		int arcWidth, int arcHeight) {
		fillRoundRect(new Line2D(new Point2D(x, y), new Point2D(width, height)), new Point2D(arcWidth, arcHeight));}

	/**Clears the whole Graphics Context with the current Color
	 * specified rectangle by filling it with the background color
	 * of the current drawing surface.
	 * This operation does not use the current paint mode.
	 * <p>
	 * Beginning with Java&nbsp;1.1, the background color
	 * of offscreen images may be system dependent. Applications should
	 * use <code>setColor</code> followed by <code>fillRect</code> to
	 * ensure that an offscreen image is cleared to a specific color.
	 * Clears the whole Graphics Context.
	 * @see java.awt.Graphics#fillRect(int, int, int, int)
	 * @see java.awt.Graphics#drawRect
	 * @see java.awt.Graphics#setColor(java.awt.Color)
	 * @see java.awt.Graphics#setPaintMode
	 * @see java.awt.Graphics#setXORMode(java.awt.Color)
	 * @since JDK1.0
	 */
	public void clear() {
		clearRect(ClipTL.x, ClipTL.y, ClipBR.x-ClipTL.x, ClipBR.y-ClipTL.y); }

	/** Sets the Background Color, only relevant to Clear Methods. */
	public Color BackColor = Color.black; //white; //black;

	/**Clears the specified rectangle by filling it with the background color
	 * of the current drawing surface.
	 * This operation does not use the current paint mode or Pattern or color.
	 * <p>
	 * Beginning with Java&nbsp;1.1, the background color
	 * of offscreen images may be system dependent. Applications should
	 * use <code>setColor</code> followed by <code>fillRect</code> to
	 * ensure that an offscreen image is cleared to a specific color.
	 * @param x the <i>x</i> coordinate of the rectangle to clear.
	 * @param y the <i>y</i> coordinate of the rectangle to clear.
	 * @param width the width of the rectangle to clear.
	 * @param height the height of the rectangle to clear.
	 * @see java.awt.Graphics#fillRect(int, int, int, int)
	 * @see java.awt.Graphics#drawRect
	 * @see java.awt.Graphics#setColor(java.awt.Color)
	 * @see java.awt.Graphics#setPaintMode
	 * @see java.awt.Graphics#setXORMode(java.awt.Color)
	 * @since JDK1.0
	 */
	public void clearRect(int x, int y, int width, int height) {
		Color bufColor = getColor(); setColor(BackColor);
		int [] bufBrush = BrushPattern; BrushPattern = GraphicPattern.FullBrushPattern;
		fillRect(x, y, x+width, y+height);
		BrushPattern = bufBrush;
		setColor(bufColor);
	}

	/**Returns the bounding rectangle of the current clipping area.
	 * The coordinates in the rectangle are relative to the coordinate
	 * system origin of this graphics context.
	 * @return the bounding rectangle of the current clipping area.
	 * @see java.awt.Graphics#getClip
	 * @see java.awt.Graphics#clipRect
	 * @see java.awt.Graphics#setClip(int, int, int, int)
	 * @see java.awt.Graphics#setClip(Shape)
	 * @since JDK1.1
	 */
	public Rectangle getClipBounds() {
		return new Rectangle(ClipTL.x, ClipTL.y, ClipBR.x-ClipTL.x, ClipBR.y-ClipTL.y); }

	/////////////////////////////////////////////////////////////////////////////////
	/// Implementation of most Methods of java.awt.Graphics
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Further Restricts the Clipping Area
	 * @see graphic.IGraphics#clipRect(int, int, int, int)
	 */
	public void clipRect(int x, int y, int width, int height) {
		clipRect(new Point2D(x, y), new Point2D(x+width, y+height));
	}

	/**
	 * Further Restricts the Clipping Area
	 * @see graphic.IGraphics#clipRect(int, int, int, int)
	 */
	public void clipRect(Point2D P1, Point2D P2) {
		clipP1(P1, P2); ClipTL.copyAt(P1);
		clipP1(P2, P1);	ClipBR.copyAt(P2);
	}

	/**
	 * Replaces the clipping area with the given rectangle.
	 * @see graphic.IGraphics#setClip(int, int, int, int)
	 */
	public void setClip(int x, int y, int width, int height) {
		ClipTL.setLocation(x, y);
		ClipBR.setLocation(x+width, y+height);
	}

	/**
	 * Returns the current clipping area as a {@link Rectangle}.
	 * @see graphic.IGraphics#getClip()
	 */
	public Shape getClip() {
		return new Rectangle(ClipTL.x, ClipTL.y, ClipBR.x-ClipTL.x, ClipBR.y-ClipTL.y);
	}

	/**
	 * Sets the clipping area to the given shape.
	 * @throws RuntimeException if the shape is not a {@link Rectangle}.
	 * @see graphic.IGraphics#setClip(java.awt.Shape)
	 */
	public void setClip(Shape clip) {
		if (clip instanceof Rectangle) {
			Rectangle rect = (Rectangle) clip; 
			clipRect(rect.x, rect.y, rect.width, rect.height); 
			return; 
		}
		throw new RuntimeException("Not implemented!"); 
	}

	/** Not implemented; always returns {@code null}.
	 * @see graphic.IGraphics#getClipBounds(java.awt.Rectangle)	 */
	public Rectangle getClipBounds(Rectangle r) {
		// TODO Auto-generated method stub
		return null;
	}

	/** Not implemented; always returns {@code null}.
	 * @see graphic.IGraphics#getClipRect()	 */
	public Rectangle getClipRect() {
		// TODO Auto-generated method stub
		return null;
	}

	/** Not implemented; always returns {@code false}.
	 * @see graphic.IGraphics#hitClip(int, int, int, int)	 */
	public boolean hitClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	///////////////////////////////////////////////////////////////////////////////////


	/** Not implemented; always throws {@link RuntimeException}.
	 * @throws RuntimeException always.
	 * @see graphic.IGraphics#translate(int, int)	 */
	public void translate(int x, int y) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented!");
	}

	///////////////////////////////////////////////////////////////////////////////////

	/**Universal Paint Routine... 
	 * handles Points, Lines, Triangles & Polygons without or with Color Info. 
	 * @see graphic.IGraphShape#fillPolygon(int[][], int)	 
	 */
	public void fillPolygon(final int[][] coloredPoints, final int nPoints) {
		switch (nPoints) {
			case 0 : break;
			case 1 : 
				if (coloredPoints[0].length > 2) { 
					setPixel(coloredPoints[0][0], coloredPoints[0][1], coloredPoints[0][2]); 
				} else {
					setPixel(coloredPoints[0][0], coloredPoints[0][1]); 
				}
			break;
			case 2 : drawLine(coloredPoints[0][0], coloredPoints[0][1], coloredPoints[1][0], coloredPoints[1][1]); 
				//if (PointsColors[0].length > 2) { 
			break;
			case 3 : drawTriangle(coloredPoints[0][0], coloredPoints[0][1], coloredPoints[1][0], coloredPoints[1][1], coloredPoints[2][0], coloredPoints[2][1]);
				//if (PointsColors[0].length > 2) { 
			break;
			default : //either copy over all Points...
				final int[] xPoints = new int[nPoints];
				final int[] yPoints = new int[nPoints];
				for (int i = nPoints; --i >= 0;) { //ignore the Colors!
					xPoints[i] = coloredPoints[i][0]; 
					yPoints[i] = coloredPoints[i][1]; 
				} //... and use the optimized Routine ...
				fillPolygon(xPoints, yPoints);  
				//...or rewrite the drawPolygon Routine
			break;
		} 

	}

	/** Fills the polygon defined by {@code PointsColors} via {@link #fillPolygon(int[][], int)}.
	 * @see graphic.IGraphShape#fillPolygon(int[][], int)	 */
	public void fillPolygon(final int[][] PointsColors) {
		fillPolygon(PointsColors, PointsColors.length);	}


	/////////////////////////////////////////////////////////////////////////////////////
	/// Implementation of continuous filling with arbitrary many Parameters for Interpolation!
	/////////////////////////////////////////////////////////////////////////////////////

	/** Draws a line between the two given (x, y, ...interpolated components) vectors,
	 * shading each pixel from the given palette.
	 * @see graphic.IGraphShape#drawLine(short[], short[], graphic.IPalette)	 */
	public void drawLine(short[] p0, short[] p1, IPalette palette) {
		if (p1[1] < p0[1]) { //Sort so that p0.y < p1.y 
			final short[] tmp = p1; p1 = p0; p0 = tmp; }
		if (p1[1] < ClipTL.y) { //basic Clipping... 
			return; } 
		if (p0[1] > ClipBR.y) { 
			return; } 
		if (p1[1] == p0[1]) {
			drawHLine(p0, p1, palette);
			return; } 
		//any other Clipping has to take Place on the Pixel Level for this Algorithm!
		final int dy = p1[1]-p0[1];	//not negative!
		final int[] dx = new int[p0.length]; 
		final boolean[] xSign = new boolean[p0.length];
		final int[] d  = new int[p0.length]; 
		final short[] px1 = new short[p0.length]; 
		final short[] px2 = new short[p0.length]; 
		for (int i = p0.length; --i >= 0; ) {
			if (i == 1) {
				continue; }
			final short x0i = p0[i];
			final int di = p1[i]-x0i;
			xSign[i] = (di > 0); 
			dx[i] = Math.abs(di);
			d[i] = dy-di; px1[i] = x0i; px2[i] = x0i; //
		}
		px1[1] = p0[1]; --px1[1]; //include the one Start-Pixel
		while(++px1[1] < p1[1]) { //no clipping anymore! yStep == 1	adding below because of the autoIncrement ++g.P.y
			VectorShort.COPY_AT(px2, px1);
			for (int i = p0.length; --i >= 0; ) {
				if (i == 1) {
					continue; }
				if ((d[i]-= dx[i]) <  0) { 
					final int n = 1-d[i]/dy; px1[i]+= xSign[i] ? n : -n; d[i]+= n*dy; } 
			}
			drawHLine(px1, px2, palette); //draw a horizontal Line!
		}
		setPixel(p1, palette); //Set the current Point to the End Point
	}

	/** Fills the polygon defined by the given vectors, splitting it into triangles
	 * fanned from the last point, shading each via the given palette.
	 * @see IGraphShape#fillPolygon(short[][], IPalette)	 */
	public void fillPolygon (final short [][]p, final IPalette palette) {
		switch (p.length) {
			case 0 : break; 
			case 1 : setPixel(p[0], palette); break; 
			case 2 : drawLine(p[0], p[1], palette); break; 
			case 3 : fillTriangle(p[0], p[1], p[2], palette); break; 
			default : //Draw a higher Polygon as a Series of Triangles...
				int i = p.length;
				short[] last = p[--i]; //...originating from the last Point...
				short[] first = p[--i]; //...which must be a Star Point
				short[] middle; 
				while(--i >= 0) {
					middle = first; first = p[i];
					fillTriangle(last, middle, first, palette); 
				}
		}
	}

	/** Fills the triangle spanned by the three given vectors row-wise, shading each
	 * pixel via the given palette.
	 * @see IGraphShape#fillTriangle(short[], short[], short[], IPalette)	 */
	public void fillTriangle (short[] p0, short[] p1, short[] p2, final IPalette palette) {
		//Sort so that p0.y < p1.y < p2.y 
		if (p1[1] < p0[1]) { final short [] tmp = p1; p1 = p0; p0 = tmp; } 
		if (p2[1] < p0[1]) { final short [] tmp = p2; p2 = p0; p0 = tmp; } 
		if (p2[1] < p1[1]) { final short [] tmp = p2; p2 = p1; p1 = tmp; } 
		if (p2[1] < ClipTL.y) { //basic Clipping...
			return; } 
		if (p0[1] > ClipBR.y) { 
			return; } 
		if (p2[1] == p0[1]) {
			return; } 
		//any other Clipping has to take Place on the Pixel Level for ScalarTriangle
		final int dy1 = p1[1]-p0[1]; 
		final int dy2 = p2[1]-p0[1]; 
		final short[] x3 = new short[p0.length];
		for (int i = x3.length; --i>= 0; ) {
			if (i == 1) { //Skip the y Component. 
				continue; }
			x3[i] = (short) (p0[i]+((p2[i]-p0[i])*dy1)/dy2); //this is the intermediate Point
		}
		x3[1] = p1[1];
		fillHTriangle(p0, p1, x3, palette);
		fillHTriangle(p2, p1, x3, palette);
		drawHLine(p1, x3, palette);	//draw a horizontal Line2D! modifies x3!
	}

	/** Fills the half-triangle with a horizontal side between x1 and x2, from x0,
	 * shading each pixel via the given palette.
	 * @see IGraphShape#fillHTriangle(short[], short[], short[], IPalette)	 */
	public void fillHTriangle(final short[] x0, final short[] x1, final short[] x2
	, final IPalette palette) {
		if (x0[1] == x1[1]) {
			if (x0 != x1) {
				drawHLine(x0, x1, palette); }
			return; 
		}
		final int ySign; 
		if (x1[1] > x0[1]) { //handle total Clipping
			ySign = 1;
			if (x0[1] > ClipBR.y) {
				return; }
			if (x1[1] < ClipTL.y) {
				return; }
		} else {
			ySign =-1;
			if (x1[1] > ClipBR.y) {
				return; }
			if (x0[1] < ClipTL.y) {
				return; }
		}
		final boolean[] xSign1 = new boolean[x0.length];
		final boolean[] xSign2 = new boolean[x0.length];
		final int[] dx1 = new int[x0.length]; 
		final int[] dx2 = new int[x0.length]; 
		final int[] d1  = new int[x0.length]; 
		final int[] d2  = new int[x0.length]; 
		final short[] px1 = new short[x0.length]; 
		final short[] px2 = new short[x0.length]; 
		final int dy = Math.abs(x1[1]-x0[1]);	//TODO: not negative! Check if abs() is still needed!
		for (int i = x0.length; --i >= 0; ) {
			if (i == 1) {
				continue; }
			final short x0i = x0[i];
			final int d1i = x1[i]-x0i;
			final int d2i = x2[i]-x0i;
			xSign1[i] = (d1i > 0); 
			xSign2[i] = (d2i > 0); 
			dx1[i] = Math.abs(d1i);
			dx2[i] = Math.abs(d2i);
			d1[i] = dy-d1i; px1[i] = x0i; //
			d2[i] = dy-d2i; px2[i] = x0i; //
		}
		//Now paint from the upper Point to the Middle and from there to the End
		for(px1[1] =  x0[1]; (px1[1]+=ySign) != x1[1]; ) { //no partial clipping anymore! widen / narrow both Sides
			px2[1] = px1[1];
			for (int i = x0.length; --i >= 0; ) {
				if (i == 1) {
					continue; }
				if ((d2[i]-= dx2[i]) <  0) { final int n2 = 1-d2[i]/dy; px2[i]+= xSign2[i] ? n2 : -n2; d2[i]+= n2*dy; } 
				if ((d1[i]-= dx1[i]) <  0) { final int n1 = 1-d1[i]/dy; px1[i]+= xSign1[i] ? n1 : -n1; d1[i]+= n1*dy; } 
			}
			//drawHLine(px1[0], px2[0], y); //just for testing...
			drawHLine(px1, px2, palette);	//draw a horizontal Line2D!
		}
	}
	
	/** Switches on drawing horizontal Lines instead of individual Pixels 	 */
	public boolean drawHLines = true; 
	
	/**Fills a whole horizontal Row with the interpolated Value between z0 and z1.
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'.
	 *
	 * Design Decisions:
	 * Instead of setting the Color each time it changes, the Changes are accumulated,
	 * until the x-Coordinate changes.
	 * Also the Changes in the x-Coordinate are collected until the Color changes.
	 */
	public void drawHLine (short[] x0, short[] x1, final IPalette palette) {
		if (x0[1] > ClipBR.y) {
			return; }
		if (x0[1] < ClipTL.y) {
			return; }
		if (x1[0] < x0[0]) { //so kann man x0 stets inkrementieren,statt etwas zu addieren
			short[] tmp = x1; x1 = x0; x0 = tmp;
		}
		final int dx = x1[0]-x0[0];	//not negative!
		final boolean[] zStep = new boolean[x0.length]; 
		final int[] dz = new int[x0.length]; 
		final int[] d = new int[x0.length]; 
		x0 = VectorShort.COPY(x0); //prevent Modification!
		boolean allSame = true; 
		for (int i = x0.length; --i >= 2; ) { //skip x and y Component
			zStep[i] = (x1[i] > x0[i]); 
			dz[i] = zStep[i] ? x1[i]-x0[i] : x0[i]-x1[i]; //not negative
			d[i] = dx-dz[i];
			if (dz[i] != 0) {
				allSame = false; } 
		}
		if (allSame) {
			drawHLine(x0, x1[0], palette);
			return; 
		}
		boolean color = false;
		short xLast = x0[0];	//Set the Start to the Start Point so that at least the last Draw Action
		while (x0[0] < x1[0]) { //no longer guaranteed to come along z0[0] AND z0 at the same time!
			boolean allPositive = true;
			for (int i = x0.length; --i >= 2; ) { //skip x and y Component
				if (d[i] <  0) { 
					final int n1 = 1-d[i]/dx; x0[i]+= zStep[i] ? n1 : -n1; 
					d[i]+=n1*dx; 
					color = true; 
					if (d[i] <  0) {
						allPositive = false; }
				}
			}
			if (allPositive) { //advance x by 1 only when ALL d are positive!
				for (int i = x0.length; --i >= 2; ) { //skip x and y Component
					d[i] -= dz[i]; }
				++x0[0]; 
				if (drawHLines) {
					if (!color) {	//Wait and accumulate Changes in Color and x-Coordinate
						continue; }
					color = false; 
					if (x0[0] > xLast) {
						this.drawHLine(x0, xLast, palette); 
					} else { //Optimization: for a single Pixel don't call drawLine!
						//setPixel(x0, y); //just for testing...
						setPixel(x0, palette);
					}	//
					xLast = x0[0];
				} else {
					setPixel(x0, palette);
				}
			}
		}
		this.drawHLine(x1, xLast, palette);
	}

	/**Fills a whole horizontal Row with the interpolated Value between z0 and z1.
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'.
	 *
	 * Design Decisions:
	 * Instead of setting the Color each time it changes, the Changes are accumulated,
	 * until the x-Coordinate changes.
	 * Also the Changes in the x-Coordinate are collected until the Color changes.
	 */
	public void drawHLine (final short[] x0, final short x1, final IPalette palette) {
		if (x0[1] > ClipBR.y) {
			return; }
		if (x0[1] < ClipTL.y) {
			return; }
		if (palette != null) { //allow it to work without Palette!
			setColor(palette.getColor(x0)); }
		drawHLine(x0[0], x1, x0[1]); 
	}
	
	/**Sets a Pixel with the given Coordinates and y Value 
	 * The y Coordinate is given by the current Draw Coordinate 'g.P.y'. 
	 * The Interpretation of the different Coordinates depends on the Context. 
	 *
	 * Design Decisions:
	 * The y Component is tracked separately Instead of in x0. 
	 * Also the Changes in the x-Coordinate are collected until one of the Components changes.
	 */
	public void setPixel (final short[]x0, final IPalette palette) {
		if (palette != null) { //allow it to work without Palette!
			setColor(palette.getColor(x0)); }
		setPixel(x0[0], x0[1]); 
	}
	
}
