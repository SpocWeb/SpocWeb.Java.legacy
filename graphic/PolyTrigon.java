package graphic;

import java.awt.Color;

/**Class to enable rapid trigonometric calculation in the integer range.  */
public class PolyTrigon {
	
	final static public int FullDegree = 360;
	final static public int East  =   0;
	final static public int North = FullDegree >> 2;
	final static public int West  = FullDegree >> 1;
	final static public int South = 3*North;

	/**Angle Extent of the full Circle (0 - 360 Degrees)	 */
	final static public Point2D FullCircle =  new Point2D (0, FullDegree);

	/**Scaling Factor for the fast Sinus Calculations	 */
	final static public short SinusFactor = 16384;

	/**Tabelle von 16384*Sinus von Winkeln zwischen 0 und 90 (= 91 Werte) Grad} */
	final static public short [] Sinus =
					{	    0,  286,  572,  857, 1143, 1428, 1713, 1997, 2280, 2563,
						 2845, 3126, 3406, 3686, 3964, 4240, 4516, 4790, 5063, 5334,
						 5604, 5872, 6138, 6402, 6664, 6924, 7182, 7438, 7692, 7943,
						 8192, 8438, 8682, 8923, 9162, 9397, 9630, 9860,10087,10311,
						10531,10749,10963,11174,11381,11585,11786,11982,12176,12365,
						12551,12733,12911,13085,13255,13421,13583,13741,13894,14044,
						14189,14330,14466,14598,14726,14849,14968,15082,15191,15296,
						15396,15491,15582,15668,15749,15826,15897,15964,16026,16083,
						16135,16182,16225,16262,16294,16322,16344,16362,16374,16382,
						SinusFactor};

	/**Returns a fast integer Sine of the angle x in Degrees.	 */
	final static public int ISin (int x) {
		if (Math.abs(x) > FullDegree) x %= FullDegree;	//Modulo Operation is really expensive!
		if (x <  East ) x += FullDegree;				//Maybe rather use iterated Addition!
		if (x <= North) return -Sinus [     x];
		if (x <= West ) return -Sinus [West-x];
		if (x <= South) return +Sinus [     x];
		                return +Sinus [FullDegree-x]; }

	/**Returns a fast integer Cosine of the angle x in Degrees.	 */
	final static public int ICos    (int x) {
		return -ISin (x+North); }

	/**Returns fast integer Sine and Cosine of the angle x in Degrees.	 */
	final static public Point2D ICosSin(final int W) {
		return ICosSin(W, null); }

	/**Returns fast integer Sine and Cosine of the angle x in Degrees.	 */
	final static public Point2D ICosSin(int W, Point2D X) {
		if (X == null) {
			X =  new Point2D(); }
		final boolean negative = (W < 0); if (negative) W = -W;		//Modulo Operation is really expensive!
//		if (W > FullDegree) W %= FullDegree; //Vorzeichen der y-Koordinaten umgekehrt !}
		while(W  > FullDegree) {
			  W -= FullDegree; } //Vorzeichen der y-Koordinaten umgekehrt !}
		if (W <= North  ) {X.setY(-Sinus [      W]); X.setX(+Sinus [North-W]);} else {W -= North;
		if (W <= North  ) {X.setY(-Sinus [North-W]); X.setX(-Sinus [      W]);} else {W -= North;
		if (W <= North  ) {X.setY(+Sinus [      W]); X.setX(-Sinus [North-W]);} else {W -= North;
						   X.setY(+Sinus [North-W]); X.setX(+Sinus [      W]);} } };
		if (negative) X.setY(-X.getY()); //{Sinus invertieren, Cosinus bleibt}
		return X; }

	/**Returns the Radius at Angle W to the Border of the Ellipse with Radiusses R.	 */
	final static public Point2D EllipseRadius(final int W, final Point2D R) {  //
		return EllipseRadius(W, R, null); }

	/**Returns the Radius at Angle W to the Border of the Ellipse with Radiusses R.	 */
	final static public Point2D EllipseRadius(final int W, final Point2D R, Point2D X) {  //Klammern weglassen,wenn direkt in IEllRec verwendet}
		final Point2D ret = ICosSin(W, X);
		ret.x *= R.x; ret.x /= SinusFactor; //statt 14-mal rechts zu schieben einfach 2* links schieben und höheres Byte nehmen
		ret.y *= R.y; ret.y /= SinusFactor; //14-mal rechts schieben ist sogar falsch, da es bei negativen Zahlen eins zuviel liefert!
		return ret; }

	/**Helper Routine, translates a Polygon by a Distance in M	 */
	final static public void addAt(Point2D[] Pt, Point2D M) {
		int i = -1; while (++i < Pt.length) Pt[i].addAt(M); }

    /**Returns the Polygon of a circle or ellipse that fits within the
     * rectangle specified by the <code>Line2D</code> argument.
     * <p>
     * @param       L the <i>Diagonal</i> through the upper left
     *                     and lower right corner of the oval.
     * @see         java.awt.Graphics#fillOval
     * @since       JDK1.0
     */
    final static public Point2D[] Ellipse(Line2D L) {
		return Ellipse(L.getCenter(), L.getWidth()); }

	/**Returns the Polygon of an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	final static public Point2D[] Ellipse (int r) {
		return Ellipse (new Point2D(r, r)); }

	/**Returns the Polygon of an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	final static public Point2D[] Ellipse (final Point2D M, final int r) {
		return Ellipse (M, new Point2D(r, r)); }

	/**Returns the Polygon of an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	final static public Point2D[] Ellipse  (final Point2D M, final Point2D R) {
		Point2D[] Ell = Ellipse (R);
		addAt(Ell, M);
		return Ell;	}

	/**Returns the Polygon of an Ellipse with Center in M, Radiuses R
	 * and the Start and End Angles in W
	 * exploits the 8 fold Symmetry: Allows for full Optimization.	 */
	final static public Point2D[] Ellipse  (final Point2D R) { 	//
		int n = 1 + (Math.abs(R.getX()) + Math.abs(R.getY())) >> 1;	//number of Points proportional to Radius: economic!
		Point2D[] Arc = RegPoly (n, R, new Point2D(0, 90));
		n = Arc.length;
		int m = n << 1;
		int l = m << 1;
		Point2D[] Ell = new Point2D [l];
		System.arraycopy (Arc, 0, Ell, 0, n);
		int i =-1; while (++i < n) {
			Point2D P = Ell[i];
			Ell [m - i -1] = new Point2D(-P.getX(), +P.getY());
			Ell [m + i   ] = new Point2D(-P.getX(), -P.getY());
			Ell [l - i -1] = new Point2D(+P.getX(), -P.getY()); }
		return Ell; 
	}
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners
	 * between the Start and End Angles in W. */
	final static public short[][] RegPoly(final Color[] colors, final Point2D R, final Point2D W) {
		return RegPoly(colors, colors.length, R, W); }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners	*/
	final static public short[][] RegPoly(final Color[] colors, final Point2D R) {
		return RegPoly(colors, colors.length, R, null, null); }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners	*/
	final static public short[][] RegPoly(final Color[] colors, final Point2D R, short[][] ret) {
		return RegPoly(colors, colors.length, R, null, ret); }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners	*/
	final static public short[][] RegPoly(final Color[] colors, final int length, final Point2D R, short[][] ret) {
		return RegPoly(colors, length, R, null, ret); }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners
	 * between the Start and End Angles in W. */
	final static public short[][] RegPoly(final Color[] colors, final int n, final Point2D R) {
		return RegPoly(colors, n, R, (short[][]) null); }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners
	 * between the Start and End Angles in W. */
	final static public short[][] RegPoly(final Color[] colors, int n, final Point2D R, final Point2D W) {
		return RegPoly(colors, n, R, W, null); }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners
	 * between the Start and End Angles in W. */
	final static public short[][] RegPoly(final Color[] colors, int n, final Point2D R, final Point2D W, short[][] ret) {
		if (ret == null) {
			ret =  new short[n][5];	} //reserve one Point for the Sector
		int ang;
		int dist; 
		if (W == null) {
			ang = 0;
			dist = FullDegree;
		} else {
			ang = W.getX(); if (W.getX() > W.getY()) { W.setX(W.getY()); W.setY(ang); ang = W.getX(); }
			dist = W.getY() - W.getX();
		}
		int inc;
		if (n > dist) { inc = 1; n = dist; }
		else {inc = dist/n; n = dist / inc;}	//letzten Teil exakt zeichnen} nicht notwendig, da nicht genau genug!
		final Point2D pt = new Point2D();
		for (int i = -1; ++i < n;){ 
			EllipseRadius (ang, R, pt); 
			COLORED_POINT(pt, colors[i], ret[i]);
			ang += inc;
		}  //in inc-Grad-Schritten, don't have to test, if (i > W.y), because it is not so exact anyway.
//		Pt[n] = Pt[i].getLocation();	//Close the Polygon, not necessary
		return ret; 
	}
	
	/** 
	 * @param pt the Point to convert 
	 * @param col the Color to convert 
	 * @return a new short[] filled with the Coordinates and Color 
	 */
	final static public short[] COLORED_POINT(final Point2D pt, final Color col) {
		return COLORED_POINT(pt, col, null); }
	
	/** 
	 * @param pt the Point to convert 
	 * @param col the Color to convert 
	 * @param ret the Object to fill, can be null
	 * @return ret or a new short[] if null, filled with the Coordinates and Color 
	 */
	final static public short[] COLORED_POINT(final Point2D pt, final Color col, short[] ret) {
		if (ret == null) {
			ret = new short[5]; }
		ret[0] = (short) pt.x; 
		ret[1] = (short) pt.y; 
		ret[2] = (short) col.getRed(); 
		ret[3] = (short) col.getGreen();
		ret[4] = (short) col.getBlue();
		return ret; }
	
	/**Returns the Polygon of a regular Polygon (equal sides) with n Corners
	 * between the Start and End Angles in W. */
	final static public Point2D [] RegPoly(int n, final Point2D R, final Point2D W) {
		int ang = W.getX(); if (W.getX() > W.getY()) {W.setX(W.getY()); W.setY(ang); ang = W.getX();}
		int dist = W.getY() - W.getX();
		int inc, i = -1;
		if (n > dist) { inc = 1; n = dist;}
		else { inc = dist/n; n = dist / inc; }	//letzten Teil exakt zeichnen} nicht notwendig, da nicht genau genug!
		Point2D [] Pt = new Point2D [n+1];	//reserve one Point for the Sector
		do {Pt[++i] = EllipseRadius (ang, R); ang += inc;}  while (i < n);	//in inc-Grad-Schritten, don't have to test, if (i > W.y), because it is not so exact anyway.
//		Pt[n] = Pt[i].getLocation();	//Close the Polygon, not necessary
		return Pt; }

	/**Returns the Polygon of Arc with Radiuses R and the Start and End Angles in W	 */
	final static public Point2D[] Arc (Point2D R, Point2D W) {
		int n = 1 + (Math.abs(W.getY() - W.getX())*(Math.abs(R.getX()) + Math.abs(R.getY()))) / 179;	//number of Points proportional to Radius: economic!
		return RegPoly(n, R, W); }

	/**Returns the Polygon of an Arc with Center in M, Radiuses R
	 * and the Start and End Angles in W	 */
	final static public Point2D[] Arc (Point2D M, Point2D R, Point2D W) {
		Point2D[] Ell = Arc(R, W);
		addAt(Ell, M);
		return Ell; }

	/**Returns the Polygon of an Ellipse with Center in M, Radiuses R and the Start and End Angles in W	 */
	public static Point2D[] Sector (Point2D M, Point2D R, Point2D W) {
		Point2D[] Ell = Arc (M, R, W);		//the bow
		Ell[Ell.length-1].setLocation(M);	//connect it to the Center
		return Ell; }

	/**Returns the Polygon of a Rectangle with rounded borders of Radius r.	 */
	public static Point2D[] RoundRect (Line2D L, int r) {
		return RoundRect (L, new Point2D (r, r));} 	//use the same radius for both axes.

	/**Draws a Rectangle with rounded borders of Radius r.	 */
	final static public Point2D[] RoundRect (Line2D Li, Point2D R) {
		Point2D LL = Li.getStart();
		Point2D HH = Li.getStop ();
		int tmp; R.absVAt();	//R is positive, LL contains the lower and HH the higher Coordinates,
		if (LL.getX() > HH.getX()) {tmp = LL.getX(); LL.setX(HH.getX()); HH.setX(tmp);}
		if (LL.getY() > HH.getY()) {tmp = LL.getY(); LL.setY(HH.getY()); HH.setY(tmp);}
		LL. addAt(R);	//Reduce the Diameter
		HH.subAt(R);	//to make the Rectangle appear within
		Point2D LH = new Point2D(LL.getX(), HH.getY());
		Point2D HL = new Point2D(HH.getX(), LL.getY());

		Point2D[] Ell = Ellipse(R); int n = Ell.length >> 2;
		int i =-1; while (++i < n) {
			Ell [i			  ].addAt(HL);
			Ell [i + n		  ].addAt(LL);
			Ell [i + n + n	  ].addAt(LH);
			Ell [i + n + n + n].addAt(HH); }
		return Ell; }

	//////////////////////////////////
	//	Polygon conversion Routines	//
	//////////////////////////////////

	/**Converts a single Array with Point2D Elements
	 * into two Arrays with the x and y Coordinates.
	 * The Arrays have to be large enough.
	 */
/*	public static void convertPolygon(Point2D [] in, int [] xP, int [] yP) {
		int n = xP.length; if (n > yP.length) n = yP.length;
		Point2D [] Pt = new Point2D [n];
		int i = -1; while (++i < n) Pt[i] = new Point2D(xP[i], yP[i]); }
*/
	/**Converts two Arrays with the x and y Coordinates
	 * into a single Array with Point2D Elements.	 */
	public static Point2D [] convertPolygon(int [] xP, int [] yP) {
		int n = xP.length; if (n > yP.length) n = yP.length;
		Point2D [] Pt = new Point2D [n];
		int i = -1; while (++i < n) Pt[i] = new Point2D(xP[i], yP[i]);
		return Pt; }

}
