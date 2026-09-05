package graphic;

/**Interface for setting a Marker on a Graphics Device.
 * The Markers know their device and use their Methods.
 *
 * Design Decisions:
 * Because the Number of Markers could become quite large
 * and should be extensible, but also other Factors (Fone, LineStyle etc.)
 * can be modified, this has been separated out of the Graph2D Class,
 * but uses it (otherwises huge number of small classes as Combinations).
 * I didn't make it an interface, because it would have only one Method
 * => many small classes, not nice.
 * Instead I went the parameterized way, more like functional programming
 * and put a select Statement in between.
 * People who want to extend Marker just add their Methods and Constants
 * and extend the Dispatcher, using Delegation for the old Methods.
 *
 * Maybe later I will use this one as a pure Dispatcher without implementing
 * any of the Markers and define an Interface instead.
 *
 * Because Markers are used only here, it has it's own Method
 * of displaying Polygons.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:06:18Z
 * digest: 7f3e9372626a5460beb008a0e69dc4050fa694058f04c8790f4b038e1ba3baf2
 * stale: false
 * tags: [code/chart_rendering, code/graphics]
 * concepts: [Point Marker Drawing]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class Marker {
	//List of all Constants for the Marker Types

	/** Selects {@link #PixelMarker()}: a single pixel. */
	final static public int  PixelMarker =  0;
	/** Selects {@link #CrossMarker()}: an X-shaped cross. */
	final static public int  CrossMarker =  1;
	/** Selects {@link #PlusMarker()}: a + shape. */
	final static public int   PlusMarker =  2;
	/** Selects {@link #CPlusMarker()}: a + shape inside a circle. */
	final static public int  CPlusMarker =  3;
	/** Selects {@link #StarMarker()}: a star (*) shape. */
	final static public int   StarMarker =  4;
	/** Selects {@link #SGridMarker()}: a small grid/asterisk shape. */
	final static public int  SGridMarker =  5;
	/** Selects {@link #SquareMarker()}: a square outline. */
	final static public int SquareMarker =  6;
	/** Selects {@link #WindowMarker()}: a square with a crosshair. */
	final static public int WindowMarker =  7;
	/** Selects {@link #FenceMarker()}: a fence-like shape. */
	final static public int  FenceMarker =  8;
	/** Selects {@link #RhombMarker()}: a rhombus outline. */
	final static public int  RhombMarker =  9;
	/** Selects {@link #RauteMarker()}: a rhombus filled with a plus sign. */
	final static public int  RauteMarker = 10;
	/** Selects {@link #leftMarker()}: a triangle pointing left. */
	final static public int   leftMarker = 11;
	/** Selects {@link #rightMarker()}: a triangle pointing right. */
	final static public int  rightMarker = 12;
	/** Selects {@link #BFlyMarker()}: a butterfly shape. */
	final static public int   BFlyMarker = 13;
	/** Selects {@link #DownMarker()}: a triangle pointing down. */
	final static public int   DownMarker = 14;
	/** Selects {@link #UpTriMarker()}: a triangle pointing up. */
	final static public int  UpTriMarker = 15;
	/** Selects {@link #HGlassMarker()}: an hourglass shape. */
	final static public int  HGlassMarker = 16;
	/** Selects {@link #CircleMarker()}: a circle or ellipse outline. */
	final static public int CircleMarker = 17;
	/** Selects {@link #Y_ORU_Marker()}: a Y-shaped marker, open right/up. */
	final static public int Y_ORU_Marker = 18;
	/** Selects {@link #Y_LOR_Marker()}: a Y-shaped marker, left/open right. */
	final static public int Y_LOR_Marker = 19;
	/** Selects {@link #Y_OLU_Marker()}: a Y-shaped marker, open left/up. */
	final static public int Y_OLU_Marker = 20;
	/** Selects {@link #Y_LUR_Marker()}: a Y-shaped marker, left/up/right. */
	final static public int Y_LUR_Marker = 21;
	/** Selects {@link #CCrossMarker()}: an X shape inside a circle. */
	final static public int CCrossMarker = 22;
	/** Number of defined marker type constants; not itself a dispatchable marker. */
	final static public int  CountMarker = 23;

/**Maximal sind 20 Bit setzbar   _ _  => 2 bei 2-facher Symmetrie bleiben noch
 * fuer 2^20 = 1 Million        |X|X| => 7              2^10 = 1024 Zeichen
 * verschiedener Zeichen         -*-  => 2 bei 4-facher Symmetrie bleiben noch
 * der rechtsstehenden Form :   |X|X| => 7              2^ 5 =   32 Zeichen
 *                               - -  => 2              von denen 12 gut sind-
 */

	/**Default Type of the Marker, used on calls for Marker()	 */
	public int MarkerType;

	/**Local Transport for the Marker Size	 */
	protected Point2D R = new Point2D(5, 5);

	/**Reference to the Graphics Context	 */
	protected IGraphShape g;

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Initializing Constructor	 */
	public Marker(IGraphShape g_){g = g_;}

	/**Initializing Constructor	 */
	public Marker(IGraphShape g_, int MarkerType)
	{g = g_; this.MarkerType = MarkerType;}

	/**Initializing Constructor	 */
	public Marker(IGraphShape g_, int MarkerType, int Radius)
	{g = g_; this.MarkerType = MarkerType; R.setX(Radius); R.setY(Radius); }

	//////////////
	//	Methods	//
	//////////////

	/**This Methods draws the selected Marker at Point P with Radius R	 */
	public void mark(Point2D P, Point2D R_) {
		R = R_; mark (P);}		//Saves handing over the Parameters each time

	/**This Methods draws the selected Marker at Point P with Radius R	 */
	public void mark(Point2D P) { mark (P.getX(), P.getY()); }

	/**This Methods draws the selected Marker at Point P with Radius R	 */
	public void mark(int x, int y) {
		g.moveTo(x, y); mark(); }	//Saves handing over the Parameters each time

	/** This Methods draws the selected Marker at the current Point P with Radius R	 
	 * TODO: should be made polymorph 
	 */
	public void mark() {	//The actual Markers are not implemented here, although that may be possible
		switch (MarkerType)	//This is a drastic Decision that has to be undone,
		{					//when the Methods are separated out.
			case  PixelMarker:  PixelMarker(); break;
			case  CrossMarker:  CrossMarker(); break;
			case   PlusMarker:   PlusMarker(); break;
			case  CPlusMarker:  CPlusMarker(); break;
			case   StarMarker:   StarMarker(); break;
			case  SGridMarker:  SGridMarker(); break;
			case SquareMarker: SquareMarker(); break;
			case WindowMarker: WindowMarker(); break;
			case  FenceMarker:  FenceMarker(); break;
			case  RhombMarker:  RhombMarker(); break;
			case  RauteMarker:  RauteMarker(); break;
			case   leftMarker:   leftMarker(); break;
			case  rightMarker:  rightMarker(); break;
			case   BFlyMarker:   BFlyMarker(); break;
			case   DownMarker:   DownMarker(); break;
			case  UpTriMarker:  UpTriMarker(); break;
			case HGlassMarker:  HGlassMarker(); break;
			case CircleMarker: CircleMarker(); break;
			case Y_ORU_Marker: Y_ORU_Marker(); break;
			case Y_LOR_Marker: Y_LOR_Marker(); break;
			case Y_OLU_Marker: Y_OLU_Marker(); break;
			case Y_LUR_Marker: Y_LUR_Marker(); break;
			case CCrossMarker: CCrossMarker(); break;
//			case  CountMarker:  CountMarker(); break;
		}
	}

	/**Simply puts a Pixel at the current Position	 */
	public void PixelMarker() { g.setPixel (); }

	/**Draws a Marker in the Form of Circle	 */
	public void CircleMarker() {
		if (R.getX() == R.getY()) { 
			g.drawEllipse (g.getPosition(), R.getX()); 
		} else { 
			g.drawEllipse (g.getPosition(), R  ); 
		} 
	}

	/**Draws a Marker in the Form of a Window with a Cross	 */
	public void WindowMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.subAt(R); 
		g.drawHLine	(x+R.getX());
		g.drawVLine	(y+R.getY());
		g.drawHLine	(x-R.getX());
		g.drawVLine	(y-R.getY());
		g.drawVLine	(x    ,y-R.getY(), y+R.getY());
		g.drawHLine	(x+R.getX(), x-R.getX(),y    );
		gP.setX(x);
	}

	/**Draws a Marker in the Form of 	 */
	public void FenceMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.subAt(R); 
		g.drawHLine	(x+R.getX());
		g.drawVLine	(y+R.getY());
		g.drawHLine	(x-R.getX());
		g.drawVLine	(y-R.getY());
		g.drawLine	(x+R.getX(), y+R.getY());
		g.drawLine	(x+R.getX(), y-R.getY(), x-R.getX(), y+R.getY());
		g.moveTo(x, y);
	}

	/**Draws a Marker in the Form of 	 */
	public void Y_LUR_Marker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.subAt(R); 
		g.drawLine (x, y);
		g.drawVLine(y+R.getY());
		g.drawLine (x+R.getX(), y-R.getY(), x, y);
	}

	/**Draws a Marker in the Form of 	 */
	public void Y_ORU_Marker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.subAt(R); 
		g.drawLine (x, y);
		g.drawHLine(x+R.getX());
		g.drawLine (x-R.getX(), y+R.getY(), x, y);
	}

	/**Draws a Marker in the Form of 	 */
	public void Y_LOR_Marker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.addAt(R); 
		g.drawLine (x, y);
		g.drawLine (x-R.getX(), y+R.getY());
		g.drawVLine(x    , y-R.getY(), y);
	}

	/**Draws a Marker in the Form of 	 */
	public void Y_OLU_Marker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.addAt(R); 
		g.drawLine (x, y);
		g.drawLine (x+R.getX(), y-R.getY());
		g.drawHLine(x-R.getX(), x, y);
	}

	/**Draws a Marker in the Form of a Cross X	 */
	public void CrossMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.addAt(R); 
		g.drawLine (x-R.getX(), y-R.getY()); gP.setX(x + R.getX());
		g.drawLine (x-R.getX(), y+R.getY());
		g.moveTo(x, y);
	}

	/**Draws a Marker in the Form of a Plus Sign +	 */
	public void  PlusMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.x += R.x; 
		g.drawHLine (x-R.getX());
		g.drawVLine (x, y-R.getY(), y+R.getY());
		gP.y = y;
	}

	/**Draws a Marker in the Form of a Star *	 */
	public void StarMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.addAt(R); 
		g.drawLine (x-R.getX(), y-R.getY()); gP.x += R.x;
		g.drawLine (x-R.getX(), y+R.getY());
		g.drawHLine (x+R.getX(), x-R.getX(), y    );
		g.drawVLine (x    , y-R.getY(), y+R.getY());
		gP.y =y;
	}

	/**Draws a Marker in the Form of 	 */
	public void SGridMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.subAt(R); 
		g.drawLine (x+R.getX(), y+R.getY());	//Diag: /
		g.drawVLine(y-R.getY());			//		|
		g.drawLine (x-R.getX(), y+R.getY());	//Diag: \
		g.drawVLine(y-R.getY());			//		|
		g.drawHLine(x+R.getX()); gP.y += R.y;
		g.drawHLine(x-R.getX());
		g.drawHLine(x+R.getX(), x-R.getX(), y    );
		g.drawVLine(x    , y+R.getY(), y-R.getY());
		gP.y = y;
	}

	/**Draws a Marker in the Form of a Square	 */
	public void SquareMarker() {
		final Point2D gP = g.getPosition();
		int x = gP.getX(); 
		int y = gP.getY(); 
		gP.subAt(R); 
		g.drawHLine (x+R.getX());
		g.drawVLine (y+R.getY());
		g.drawHLine (x-R.getX());
		g.drawVLine (y-R.getY());
	}

	/**Draws a Marker in the Form of 	 */
	public void CPlusMarker() {
		final Point2D gP = g.getPosition();
		Point2D P = new Point2D(gP);
		gP.x -= R.x;
		g.drawHLine (P.getX()+R.getX());
		g.drawVLine (P.getX()    , P.getY()-R.getY(), P.getY()+R.getY());
		g.drawEllipse(P, R);
		g.moveTo(P);
	}

	/**Draws an X in a Circle.
	 * This is the first Marker that has no closed circumference	 */
	public void CCrossMarker() {
		final Point2D gP = g.getPosition();
		Point2D P = new Point2D(gP);
		gP.addAt(R);
		g.drawLine (P.getX()-R.getX(), P.getY()-R.getY()); gP.x = P.x + R.x;
		g.drawLine (P.getX()-R.getX(), P.getY()+R.getY());
		g.drawEllipse(P, R);
		g.moveTo(P);
	}

	/**Draws a Triangle pointing to the left	 */
	public void  leftMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; gP.x += R.x;
		final int y = gP.y;
		g.drawLine (x-R.getX(), y+R.getY());
		g.drawVLine(       y-R.getY());
		g.drawLine (x+R.getX(), y    );
		gP.x = x;
	}

	/**Draws a Triangle pointing to the right	 */
	public void rightMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; gP.x -= R.x;
		final int y = gP.y;
		g.drawLine (x+R.getX(), y+R.getY());
		g.drawVLine(       y-R.getY());
		g.drawLine (x-R.getX(), y    );
		gP.x = x;
	}

	/**Draws a Triangle pointing down	 */
	public void   DownMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; 
		final int y = gP.y; gP.y += R.y;
		g.drawLine (x+R.getX(), y-R.getY());
		g.drawHLine(x-R.getX());
		g.drawLine (x    , y+R.getY());
		gP.y = y;
	}

	/**Draws a Triangle pointing up	 */
	public void UpTriMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; 
		final int y = gP.y; gP.y -= R.y;
		g.drawLine (x+R.getX(), y+R.getY());
		g.drawHLine(x-R.getX());
		g.drawLine (x    , y-R.getY());
		gP.y = y;
	}

	/**Draws a Butterfly Shape	 */
	public void BFlyMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; 
		final int y = gP.y; 
		gP.subAt(R);
		g.drawVLine(       y+R.getY());
		g.drawLine (x+R.getX(), y-R.getY());
		g.drawVLine(       y+R.getY());
		g.drawLine (x-R.getX(), y-R.getY());
		g.moveTo(x, y);
	}

	/**Draws an Hourglass Shape	 */
	public void HGlassMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; 
		final int y = gP.y; 
		gP.subAt(R);
		g.drawHLine(x+R.getX());
		g.drawLine (x-R.getX(), y+R.getY());
		g.drawHLine(x+R.getX());
		g.drawLine (x-R.getX(), y-R.getY());
		g.moveTo(x, y);
	}

	/**Draws a rhombic Shape	 */
	public void RhombMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; gP.x += R.x;
		final int y = gP.y;
		g.drawLine (x    , y+R.getY());
		g.drawLine (x-R.getX(), y    );
		g.drawLine (x    , y-R.getY());
		g.drawLine (x+R.getX(), y    );
		gP.x = x;
	}

	/**Draws a rhombic Shape, filled with a Plus Sign	 */
	public void RauteMarker() {
		final Point2D gP = g.getPosition();
		final int x = gP.x; gP.x += R.x;
		final int y = gP.y;
		g.drawLine (x    , y+R.getY());
		g.drawLine (x-R.getX(), y    );
		g.drawLine (x    , y-R.getY());
		g.drawLine (x+R.getX(), y    );
		g.drawHLine(x-R.getX());
		g.drawVLine(x    , y+R.getY(), y-R.getY());
		gP.y = y;
	}

	/**This Routines takes two Vectors [xP,xP] and draws Markers on the Points:
	 * The Radiuses of the Markers are given by [xR,yR], otherwise by the given Radiuses.
	 * The Points are optionally connected and closed.	 */
	public void P2_Plot (int [] xP, int [] yP) {	//delegation to the Routine below
		P2_Plot(xP, yP, null, null, 0, 0, true, false, false, false); }

	/**This Routines takes two Vectors [xP,xP] and draws Markers on the Points:
	 * The Radiuses of the Markers are optionally given by [xR,yR],
	 * otherwise by the default Radiuses of this Instance.
	 * The Points are optionally connected and closed.
	 * Additionally the Lot for each Point can be drawn to the x or y Axis
	 * with the Coordinates y0 or x0 respectively.	 */
	public void P2_Plot (int [] xP, int [] yP, int [] xR, int [] yR, int x0, int y0,
						 boolean connect, boolean closed, boolean LotX, boolean LotY) {
		int i, n = xP.length; if (n > yP.length) n = yP.length; n--; //= min (x.length, y.length);
		if (closed)	 {g.moveTo(xP[n], yP[n]); i = -1;}
		else		 {g.moveTo(xP[0], yP[0]); i =  0;}
		while (++i <= n) {
			int x = xP[i];
			int y = yP[i];
			if (xR != null) R.setX(xR[i]);
			if (yR != null) R.setY(yR[i]);
			if (connect) {
				g.drawLine(x, y);
				mark();
			} else {
				mark(x, y); }
			if (LotX) g.drawVLine(x , y0, y);	//Keep (x,y) as EndPoints
			if (LotY) g.drawHLine(x0, x , y);	//to enable the connect on the next Point
		}
	}

	/**This Routines takes a Vector P[] and draws Markers on the Points:
	 * The Radiuses of the Markers are optionally given by R[],
	 * otherwise by the default Radiuses of this Instance.
	 * The Points are optionally connected and closed.
	 * Additionally the Lot for each Point can be drawn to the x or y Axis
	 * with the Coordinates y0 or x0 respectively.	 */
	public void P2_Plot (Point2D[] P, Point2D[] R, int x0, int y0,
						 boolean connect, boolean closed, boolean LotX, boolean LotY) {
		int i, n = P.length-1;
		if (closed)	 {g.moveTo(P[n]); i = -1;}
		else		 {g.moveTo(P[0]); i =  0;}
		while (++i <= n) {
			Point2D						Pt = P[i];
//			Point2D Rd; if (R != null)	Rd = R[i];
			if (connect) {
				g.drawLine(Pt);
				mark();
			} else {
				mark(Pt); }
			if (LotX) g.drawVLine(Pt.getX() ,    y0, Pt.getY());	//Keep (x,y) as EndPoints
			if (LotY) g.drawHLine(   x0, Pt.getX() , Pt.getY());	//to enable the connect on the next Point
		}
	}

}
