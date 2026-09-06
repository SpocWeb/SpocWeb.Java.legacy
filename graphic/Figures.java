package graphic;

import java.awt.Color;
import java.awt.Rectangle;

/**The geometric Figures that are not likely substituted by Calls to System Functions,
 * because they are too special, are collected here:
 * Arrows, Coordinate Systems with optional Ticks and Rasters,
 * Triangles, Squares, Rounded Rectangles, Balls with 3D Effect
 * Bezier Splines
 * @see Bar3D for pseudo 3D Bars.
 *
 * Design Decisions:
 * Parameters that are rarely changed are separated out of the Call Interface
 * and put into Instance Variables.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:05:24Z
 * digest: 9de35de3fa6dc76ec41c2a657aa6df380ca64050751522ad360723b3cdc19982
 * stale: false
 * tags: [code/graphics, code/geometry]
 * concepts: [Special-Purpose Shape Drawing]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
final public class Figures {

	/**This is the Length of the Arrow Feathers
	 * in Relation to the Length of the whole Arrow multiplied by 256
	 */
	public static int ArrowLength = 25;	//gives 13/256 = 1/20th

	/**This is the Waviness for the drawFracLine() Routine.
	 * It determines it's fractal Dimension.
	 * With Waviness = 0 the Dimension is 1,
	 * with Waviness = 2 the Dimension is 2
	 * Larger Values lead to an infinite Recursion
	 *
	 * Defined in this Class to avoid constant handing down during Recursion.
	 */
	public double Waviness = 1.0;

	/**Initializing Constructor	 */
	public Figures(IGraphShape g_){ g = g_;}

	/**Reference to the Graphics Context	 */
	protected IGraphShape g;

	//////////////////////
	//	Draw Methods	//
	//////////////////////

	/**Draws a pseudo 3D Ball with integer Radius R around P1 in the given Color.
	 * @param g   the Graphics Context to draw into
	 * @param P1  the Center of the Ball
	 * @param R   the Radius of the Ball
	 * @param col the Color  of the Ball. If null, the current Color is used.
	 */
	final static public void Ball3D(IGraphShape g, Point2D P1, int R, Color col) {
		int r = Math.abs(R);
		g.fillEllipse (P1, 1 + r);	//ensure that at least a Point is drawn
		while (0 < (r /= 2)) { // /=3)) { //also looked good...
			P1.setX(P1.getX() - (r / 2)); // -=r; //for /=3 this was sufficient!
			P1.setY(P1.getY() - (r / 2));
			if (col == null) {
				col =  g.getColor(); }
			Color c1 = col.brighter();
//			if (col.equals(c1)) {
//				System.out.println(c1.toString() + " is the brighter of " + col.toString()); }
			col = c1; //place brighter Values top left to simulate 3D
			g.setColor(col);
			g.fillEllipse (P1, r);	//ensure that at least a Point is drawn
		}
	}


	/**Draws a fractal Line between P0 and P1.
	 * The Dimension is determined by Waviness within [0,2]
	 */
	public void drawFracLine (Point2D P0, Point2D P1) {
		g.moveTo(P0); drawFracLine(P1);}

	/**Draws a fractal Line between the current Point P and P1.
	 * The Dimension is determined by Waviness within [0,2]
	 */
	public void drawFracLine (Point2D P1) {
		Point2D Knick = new Point2D(g.getPosition()); Knick.subAt  (P1);
		if ((Math.abs(Knick.getX()) + Math.abs(Knick.getY())) <= 3) { //Frac_Skala) Values < 3 result in too recursive Structures
			g.drawLine(P1); return; } //break Recursion
		Point2D Mitte = new Point2D(g.getPosition()); Mitte.middleAt(P1);
		double rnd = (Math.random()-0.5) * Waviness;
		int tmp = (int)(Knick.getX() * rnd);
		Knick.setX((int)(Knick.getY() * rnd));
		Knick.setY(-tmp);		//This new Point lies in the orthogonal Direction
		Mitte.addAt(Knick);	//from the Middle between the Start and the End.
		drawFracLine (Mitte); //Recursion
		drawFracLine (P1); }

	/**Arrow from P in Direction L.
	 * Next Ending Point is P+L */
	public void drawArrow(Point2D P, Point2D L) {
		g.moveTo(P); drawArrow(L);}

	/**Arrow from the current Location P in Direction L.
	 * Next Ending Point is P+L */
	public void drawArrow(Point2D L) { drawArrow(L.getX(), L.getY()); }

	/**Arrow from the current Location P in Direction L.
	 * Next Ending Point is P+L 
	 */
	public void drawArrow(int Lx, int Ly) {
		drawArrow(g.getPosition(), Lx, Ly);
	}

	/**Arrow from the current Location P in Direction L.
	 * Next Ending Point is P+L 
	 */
	public void drawArrow(int x, int y, int Lx, int Ly) {
		int xc = ((Lx-Ly) * ArrowLength) >> 8;
		int yc = ((Lx+Ly) * ArrowLength) >> 8;
		int Bx = x + Lx;
		int By = y + Ly;
		g.drawLine (x, y, Bx, By);
		g.drawLine (Bx-xc, By-yc);
		g.drawLine (Bx-yc, By+xc);
		g.drawLine (Bx, By);
	}

	/**Arrow from the current Location P in Direction L.
	 * Next Ending Point is P+L 
	 */
	public void drawArrow(Point2D B, int Lx, int Ly) {
		drawArrow(B.getX(), B.getY(), Lx, Ly);
	}

	/** draws a Triangle, but only if it's Orientation is positive.      */
	public void drawOrientedTriangle(Point2D P0, Point2D P1, Point2D P2) {
		Point2D[] Tri = new Point2D[3];
		if (P0.AreaTriangle(P1, P2) < 0) return;
		Tri[0] = P0;
		Tri[1] = P1;
		Tri[2] = P2;
		g.fillPolygon(Tri);
	}

	/**Draws a Square by calculating an artificial Midpoint and drawing three triangles.
	 * Normally you should better create this Midpoint in the original Coordinate System,
	 * but this is an approximation anyway and even correct for planar Projection.
	 *
	 * This is used e.g. for filling irregular "Squares" with Gradient Colors
	 * resulting from planar Projection.
	 * Additionally the Orientation of each Triangle is evaluated.
	 */
	public void drawSquare(Point2D P1, Point2D P2, Point2D P3, Point2D P4) {
		Point2D P0 = new Point2D(P1);
		P0.addAt(P2);
		P0.addAt(P3);
		P0.addAt(P4);
		P0.setX(P0.getX() >> 2);
		P0.setY(P0.getY() >> 2); Point2D[] Tri = new Point2D[3];Tri[0] = P0;
		if (P0.AreaTriangle(P1, P2) > 0) {Tri[1] = P1; Tri[2] = P2; g.fillPolygon(Tri);}
		if (P0.AreaTriangle(P2, P3) > 0) {Tri[1] = P2; Tri[2] = P3; g.fillPolygon(Tri);}
		if (P0.AreaTriangle(P3, P4) > 0) {Tri[1] = P3; Tri[2] = P4; g.fillPolygon(Tri);}
		if (P0.AreaTriangle(P4, P1) > 0) {Tri[1] = P4; Tri[2] = P1; g.fillPolygon(Tri);}
	}

	//////////////////////
	//	Fill Methods	//
	//////////////////////

	/**
	 * Generates and Paints the Bezier Spline
	 * given by the Lead Points in [Px, Py].
	 */
	public void  Bezier  (int[] PX, int[] PY, int numInter) {
//		Point2D Pz = new Point2D();
		int[] QX = new int[PX.length];
		int[] QY = new int[PY.length];
		int[] RX = new int[PX.length];
		int[] RY = new int[PY.length];
		int[] Pt;
		g.moveTo((int) PX[0], (int) PY[0]);
		int i = 0; while (++i < numInter) {
			System.arraycopy(PX, 0, QX, 0, PX.length); //{Werte-Tabelle wiederherstellen}
			System.arraycopy(PY, 0, QY, 0, PX.length);
			int M = PX.length; while (--M >= 1) {
				int j = -1; while (++j < M) {
					RY[j] = (int) (QY[j] + i*(QY[j+1] - QY[j])/numInter); //{y-Werte}
					RX[j] = (int) (QX[j] + i*(QX[j+1] - QX[j])/numInter); //{x-Werte}
				}
				Pt = RY; RY = QY; QY = Pt; //{Statt umzukopieren einfach Zeiger tauschen !}
				Pt = RX; RX = QX; QX = Pt;
			}
			g.drawLine ((int) QX[0], (int) QY[0]);
		}
		g.drawLine ((int) PX[PX.length-1], (int) PY[PX.length-1]);
	}

	/**Generates the Bezier Spline Value in t given by the Lead Values in PX.
	 * The Array PX is destroyed. If you want to keep it, you have to restore it.
	 * If you combine this with the respective Interpolations in other Dimensions,
	 * you get a Line with minimum Bending. */
	public double Bezier2  (double[] PX, double t) {
		double[] Pt,RX = new double[PX.length],
					QX = PX;
//		QX = new double[PX.length];
//		System.arraycopy(PX, 0, QX, 0, PX.length); //{Werte-Tabelle wiederherstellen}
		int M = PX.length; while (--M >= 1) {
			int j = -1; while (++j < M)
				RX[j] = QX[j] + t*(QX[j+1] - QX[j]); //{x-Werte}
			Pt = RX; RX = QX; QX = Pt; //{Statt umzukopieren einfach Zeiger tauschen !}
		}
		return QX[0]; }

	/**Generates a Bezier-Spline Interpolation with Degree Grad of the given Polygon.	 */
	public void  Bezier (Point2D[] Fuehrung, int Grad, boolean glatt) {
		int[] PY, PX;
		int Aufloesung; //, Groesse, M;
		Point2D Pz, PZ1 = null, PZ2;
//		Point2D PH = new Point2D();
		if (++Grad > Fuehrung.length) Grad = Fuehrung.length; //{=> #Punkte}
		PY = new int[Grad+1];
		PX = new int[Grad+1];
		Pz = Fuehrung[0]; //{erster Zwischen-Punkt = Start-Punkt}
		int Anzahl = 1;
		while (Anzahl < Fuehrung.length) {	//Build a Polygon from the first Points
			Aufloesung = 0;
			if (Fuehrung.length < Anzahl + Grad) {
				Grad = Fuehrung.length-Anzahl;
				PY = new int[Grad+1];
				PX = new int[Grad+1];
			}
			PZ2 = Pz; //{erster Punkt evtl. ein Zwischenpunkt zum stetigen Anschluss}
			int i = -1; while (++i < Grad) { //{zwei Punkte zu wenig aufstellen}
				PZ1 = PZ2; PZ2 = Fuehrung[Anzahl++];
				PX[i] = PZ1.getX();
				PY[i] = PZ1.getY();
				Aufloesung += Math.abs (PZ1.getX() - PZ2.getX()) + Math.abs (PZ1.getY() - PZ2.getY()); }
			if (glatt && (Anzahl < Fuehrung.length)) { //{letzte Abfrage kann entfallen,spart aber bei Grad = 1}
				Pz = PZ1.getLocation(); Pz.middleAt (PZ2); Anzahl--;} //{mittleren Punkt erzeugen fuer diff.bare Uebergaenge}
			else Pz = PZ2;
			PX[i] = Pz.getX();	//{letzter Punkt von 0..Grad evtl. ein Zwischen-Punkt}
			PY[i] = Pz.getY();	//{fuer bessere Ueberdeckung !}

			//call to the Bezier Routine
			Bezier(PX, PY, Aufloesung >> 1);
		}
	}

	/**Generates a Base-Spline Interpolation with Degree Grad of the given Polygon.	 */
/*	public void  BSpline (Fuehrung : Polygon;Farbe,Grad : Word; {
		VAR M,MM,tI,Groesse,VGroesse,GGroesse,l : Word;
		    N1,N2,QX,QY : P_Real_P_Feld;
		    Summe,Aufloesung,ll : LongInt;
		    a,b,Rest,dt : Real;
		if ( Fuehrung.Anzahl > 0 ) {
		  Groesse = Fuehrung.Anzahl*SizeOf (Real);
		 GGroesse = Grad*SizeOf (Real);
		 VGroesse = Groesse+GGroesse << 1;
		 GetMem  (N1,VGroesse);
		 GetMem  (N2,VGroesse);
		 GetMem  (QX,VGroesse);
		 GetMem  (QY,VGroesse);
		 PZ1 = P_PointType (Fuehrung.Punkte);Pz = PZ1;
		 PZ2 = P_PointType (Fuehrung.Punkte);INC (PZ2);
		 RZ1 = P_Real (QX);
		 RZ2 = P_Real (QY);
		 Aufloesung = 0;Summe = 0;
		 FOR i = 1 TO Grad ) {
		   RZ1 = PZ1.x;INC (RZ1);
		   RZ2 = PZ1.y;INC (RZ2);
		 }
		 FOR i = 2 TO Fuehrung.Anzahl ) {
		   INC (Aufloesung,StreckenLaenge (PZ1,PZ2));
		   RZ1 = PZ1.x;INC (RZ1);INC (PZ2);
		   RZ2 = PZ1.y;INC (RZ2);INC (PZ1);
		 }
		 FOR i = 0 TO Grad ) {
		   RZ1 = PZ1.x;INC (RZ1);
		   RZ2 = PZ1.y;INC (RZ2);
		 }
		 INC (Fuehrung.Anzahl,Grad-2);
		 Aufloesung = Round (SqRt (Aufloesung*Grad)/Fuehrung.Anzahl);dt = Eins/Aufloesung;
		 Z4 = Succ (Grad)*SizeOf (Real); //{Start-Punkte der Summationen}
		 RZ1 = P_Real (QX);INC (Zeiger (RZ1).Offset,Z4);
		 RZ2 = P_Real (QY);INC (Zeiger (RZ2).Offset,Z4);
		 RZ3 = P_Real (N1);INC (Zeiger (RZ3).Offset,Z4);
		 RZ4 = P_Real (N2);INC (Zeiger (RZ4).Offset,Z4);
		 FOR tI = Succ (Grad) TO Fuehrung.Anzahl+Grad ) {
		   Rest = 0;
		   FOR ll = 1 TO Succ (Aufloesung) ) {
		     RZ3 = Eins;     //{mit konstantem B_Spline starten Grad = 0}
		     FOR MM = 1 TO Grad ) {
		     	//{N [i,Grad+1] = erster relevanter B_Spline der Ordnung MM}
		       i = tI-MM;f = Eins/MM;g = (Rest-Eins)*f;
		       RZ5 = RZ3;
		       RZ6 = RZ4;
		       FOR l = tI DOWNTO i ) { //{umgekehrt laufen lassen}
		         if (l <> tI) { INC (RZ5);b = RZ5*(Eins-g);DEC (RZ5) }
		         else b = Null;g = g+f;
		         if (l <> i) {           a = RZ5*      g ;DEC (RZ5) }
		         else a = Null;
		         RZ6 = a+b; DEC (RZ6);
		        }
		       Pt = RZ3;RZ3 = RZ4;RZ4 = Pt //{Statt umzukopieren oder sogar in Matrix abzulegen !}
		      }; //{allerdings koennte man die Matrix verwenden um verschiedene Splines aus denselben Daten zu erzeugen !}
		     Pa = Pz;
		     Pz.x = Round (Verkuerzung (RZ1,RZ3,Word (-SizeOf (Real)),Word (-SizeOf (Real)),Succ (Grad)));
		     Pz.y = Round (Verkuerzung (RZ2,RZ3,Word (-SizeOf (Real)),Word (-SizeOf (Real)),Succ (Grad)));
		     g.drawLine (Pa,Pz,Farbe,PutPixel));
		     Rest = Rest+dt;
		    }
		   INC (RZ1);
		   INC (RZ2);
		   INC (RZ3);
		   INC (RZ4);
		  }
		}
	}
*/
	/**Generates periodic Border-Conditions for Bezier- and Basis-Splines.
	 * By adding the same Point (Middle between first and last Point) twice,
	 * once at the Beginning, once at the End.
	 */
	public int[] periodicSpline (int[] Polygon) {
		if ((Polygon == null) || (Polygon.length == 0)) return Polygon;
		int[] Result = new int[Polygon.length+2];
		System.arraycopy (Polygon, 0, Result, 1, Polygon.length);
		Result[0] = (Polygon[0] + Polygon[Polygon.length-1]) >> 1;
		Result[Polygon.length+1] = Result[0];
		return Result;
	}

	/**Generates periodic Border-Conditions for Bezier- and Basis-Splines.
	 * By adding the same Point (Middle between first and last Point) twice,
	 * once at the Beginning, once at the End.
	 */
	public Point2D[] periodicSpline (Point2D[] Polygon) {
		if ((Polygon == null) || (Polygon.length == 0)) return Polygon;
		Point2D[] Result = new Point2D[Polygon.length+2];
		System.arraycopy (Polygon, 0, Result, 1, Polygon.length);
		Result[0] = Polygon[0].getLocation().middleAt(Polygon[Polygon.length-1]);
		Result[Polygon.length+1] = Result[0];
		return Result;
	}

	/**Draws the 2D Vector Array by putting an Arrow on every Grid Point. 	 */
	public void VectorGrid(int[] xP, int[] yP, int[][]xV, int[][]yV) {
		//first fill up all the Colummns
		int[] xc, yc;
		int i = -1;
		Point2D P = new Point2D();
		Point2D V = new Point2D();
		while (++i < xP.length) {
			int j = -1;
			P.setX(xP[i]);
			xc = xV[i];
			yc = yV[i];
			while (++j < yP.length) {
				P.setY(yP[j]);
				V.setX(xc[j]);
				V.setY(yc[j]);
				drawArrow(P, V);
			}
		}
	}

	/**Draws the Cartesian Cross through the Origin of the Ordinates.
	 * PM contains the Position of the Origin.	 */
	public void Ordinates2D (final int x0, final int y0) {
		final Point2D position = g.getPosition();
		final int posX = position.getX(); 
		final int posY = position.getY(); 
		final Rectangle rect = g.getClipRect(); 
		g.drawHLine (rect.x, rect.x+rect.width, y0);	//horiz. Line
		g.drawLine  (posX-RasterWidth, posY-RasterWidth);	//Arrow
		g.drawVLine (                  posY+RasterWidth+RasterWidth);
		g.drawLine  (posX+RasterWidth, posY-RasterWidth);
		g.drawVLine (x0, rect.y+rect.height, rect.y);	//vert. Line
		g.drawLine  (posX-RasterWidth, posY+RasterWidth);	//Arrow
		g.drawHLine (posX+RasterWidth+RasterWidth);
		g.drawLine  (posX-RasterWidth, posY-RasterWidth);
	}

	/** identical to the upper Method
	 * PM contains the Position of the Origin	 */
/*	public void OrdinateTicks2D (final int x0, final int y0, final int[] RasterX) {
		final Point2D position = g.getPosition();
		final int posX = position.getX(); 
		final int posY = position.getY(); 
		final Rectangle rect = g.getClipRect(); 
		g.drawHLine (rect.x, rect.x+rect.width, y0);	//horiz. Line
		g.drawLine  (posX-RasterWidth, posY-RasterWidth);	//Arrow
		g.drawVLine (                   posY+RasterWidth+RasterWidth);
		g.drawLine  (posX+RasterWidth,	posY-RasterWidth);
		g.drawVLine (x0, rect.y+rect.height, rect.y);	//vert. Line
		g.drawLine  (posX-RasterWidth, posY+RasterWidth);	//Arrow
		g.drawHLine (posX+RasterWidth+RasterWidth);
		g.drawLine  (posX-RasterWidth, posY-RasterWidth);
	}
*/
	/**Width of the Raster Crosses in drawRaster()	 */
	public int RasterWidth = 5;

	/**Draws a 2D Raster of Crosses	 */
	public void drawRaster(int[] x, int[] y) {
		int A, E, M, Z;
		int i = -1; while (++i < x.length) {
			M = x[i]; E = M + RasterWidth; A = M - RasterWidth; //P.x = x[i];
			int j = -1; while (++j < y.length) {
				Z = y[j]; g.drawHLine(A, E, Z); g.drawVLine(M, Z-RasterWidth, Z+RasterWidth); } //P.y = y[j]; Mark (P,R); }
		}
	}

	/**Draws a 2D Raster of Lines or Crosses	 */
	public void drawRaster(int[] x, int[] y, boolean xLine, boolean yLine) {
		int A, E, i;
		if (!(xLine || yLine)) { drawRaster(x, y); return; }
		if (xLine) { A = y[0]; E = y[y.length-1];i = -1; while (++i < x.length) { g.drawVLine(x[i], A, E); } }
		if (yLine) { A = x[0]; E = x[x.length-1];i = -1; while (++i < y.length) { g.drawHLine(A, E, y[i]); } }
	}

	/**Draws the 2D Coordinate System for Polar Coordinates.
	 * Pz contains the Position of the Origin.
	 */
	public void R_System2 (Point2D Origin, int[] rx, int[] ry, int[] w) {
		int i = -1; Point2D R = new Point2D();
		while (++i < rx.length) {
			R.setX(rx[i]);
			R.setY(ry[i]);
			g.drawEllipse (Origin, R);
		}	//Now R contains the maximum Radius
		i = w.length;
		while (--i >= 0) {
			g.drawLine (Origin.getX() - R.getX(), Origin.getY() - R.getY(), Origin.getX() + R.getX(), Origin.getY() + R.getY());
		}
	}

}
