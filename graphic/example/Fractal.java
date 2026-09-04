package graphic.example;

import graphic.AGraph2D;
import graphic.IRaster;
import graphic.JavaGraphic;
import graphic.PaletteRGB;
import graphic.Point2D;
import graphic.ScalarPlotNew;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;

/**This Class has no state and therefore only static Methods.
 * It is a Container for some fractal Methods.
 *
 * Backtracking the Turtle works, due to no rounding errors,
 * still it is faster to just buffer the old Location.
 */
final public class Fractal
extends Frame { //Applet {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Size of the Graphic Area     */
	final static public int WIDTH = 1024;

	/** Size of the Graphic Area     */
	final static public int HEIGHT = 768;

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @return the first Number for which (1 >> Octave) > num
	 * used in Plasma and in Fourier Transformation
	 */
	final static public byte Octave(int num) {
		if (num < 0 ) num = -num;
		byte Octave = 0; int Mask = 1;
		while (Mask < num) { Mask <<=1; Octave++; } ;
		return Octave; }
	
	/**Rasters a 2-dimensional integer Array using the given Painter to calculate the Values.
	 * This Algorithm can easily be extended to work in any number of Dimensions.
	 * @return the range of Values (Minimum in x, Maximum in y)
	 */
	protected static final Point2D raster2D (final int [][] picture, final IRaster painter) {
		final Point2D Width = new Point2D (
				picture   .length,
				picture[0].length);
		final Point2D curr = new Point2D();	//
		final Point2D MnMx = new Point2D(Integer.MAX_VALUE, Integer.MIN_VALUE);	//Min and Max Value
		int SR = 1 << ((Width.getX() > Width.getY()) ?  	//Step Raster...
			Fractal.Octave(Width.getX()) : //always an integer Power of 2
			Fractal.Octave(Width.getY()));
		do {
			int Mask = SR-1; SR >>= 1;
			painter.setRaster(SR, Mask, picture);
			curr.setX(0);
			while (curr.getX() < picture.length) {
				curr.setY(0);
				while (curr.getY() < picture [0].length) {
					SET_RASTER2D_PIXEL(picture, painter, curr, MnMx, Mask);
					curr.setY(curr.getY() + SR);
				}
				curr.setX(curr.getX() + SR);
			}
		}
		while (SR > 1);   //{das immer weiter verfeinert wird}
		return MnMx; }

	/** @see #raster2D(int[][], IRaster) uses this Method exclusively to set the Value of a Pixel	 */
	private static void SET_RASTER2D_PIXEL(
		final int[][] picture,
		final IRaster painter,
		final Point2D curr,
		final Point2D mnMx,
		final int mask) {
		if ( ((curr.getX() | curr.getY()) & mask) != 0 ) { //{Punkte,die schon gemalt wurden, auslassen
			final int Farbe = painter.getValue(curr);
			picture[curr.getX()][curr.getY()] = Farbe;	//Modulo Operation is incredibly expensive!
			if (mnMx.getX() > Farbe) { //calculate Minimum
				mnMx.setX(Farbe); }
			if (mnMx.getY() < Farbe) { //and Maximum
				mnMx.setY(Farbe); } //of the Color Range!
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Handle to a Graphics Context for the fractal Turtle Graphics	 */
	protected AGraph2D g;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor for the Graphics Context of this Frame */
	public Fractal() { }

	/** Constructor for an external Graphics Context */
	public Fractal(AGraph2D g_) { this.g = g_; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, public ones, then private ones (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Dragon Curve without shortening,
	 * Length is the Segment Length.	 */
	public void Dragon1 (int Grad, int Length) {
		int angle;
		if (Grad == 0) { g.forwd (Length); return; }
		if (Grad  > 0) { angle = +90;       --Grad; }
		else		   { angle = -90; Grad = -Grad-1; }
		Dragon1 (+Grad, Length); g.turn (angle);
		Dragon1 (-Grad, Length);
	}

	/**Dragon Curve with shortening,
	 * Length is the total Length.	 */
	public void  Dragon (int Grad, int Length) {
		int angle;
		if (Grad == 0)	{ g.forwd (Length); return;}
		if (Grad  > 0)	{ angle = +90;       --Grad;}
		else			{ angle = -90; Grad = -Grad-1;}
		Length >>= 1;
		Dragon (+Grad, Length); g.turn (angle);
		Dragon (-Grad, Length);
	}

	/**Hilberts Kurve with Dimension 1.5	 */
	public void Hilbert (int Grad, int Length) {
		int angle;
		if (Grad == 0) { return; }
		if (Grad > 0)	{angle = +90; --Grad;}
		else			{angle = -90; ++Grad;}
		g.turn (-angle);Hilbert (-Grad,Length);	g.forwd (Length);
		g.turn (+angle);Hilbert (+Grad,Length);	g.forwd (Length);
						Hilbert (+Grad,Length);
		g.turn (+angle);      					g.forwd (Length);
						Hilbert (-Grad,Length);
		g.turn (-angle);
	}

	/**Kochs Kurve (Snowflake) with Dimension Log(3)5 =1.46
	 * The Length is the overall Length of the Curve.	 */
	public void Koch    (int Grad, int Length) {
		if (--Grad < 0) {
			g.forwd (Length); return; }
		Length /= 3;
		Koch (Grad ,Length); g.turn (-60);
		Koch (Grad ,Length); g.turn (120);
		Koch (Grad ,Length); g.turn (-60);
		Koch (Grad ,Length);
	}

	/**Koch's rectangle,
	 * Length is the total Length of the Figure	 */
	public void RecKoch (int Grad, int Length) {
		if (--Grad < 0) {
			g.forwd (Length); return; }
		Length /= 3;
		RecKoch (Grad,Length); g.turn (-90);
		RecKoch (Grad,Length); g.turn (+90);
		RecKoch (Grad,Length); g.turn (+90);
		RecKoch (Grad,Length); g.turn (-90);
		RecKoch (Grad,Length);
	}

	/**C- Curve
	 */
	public void C_Kurve (int Grad, int Length) {
		if (--Grad < 0) {
			g.forwd (Length); return; }
		C_Kurve (Grad,Length); g.turn (+90);
		C_Kurve (Grad,Length); g.turn (-90);
	}

	/**binaerer Baum	 */
	public void Tree    (int Grad, int Length, int angle, int Decr) {
		--Grad; // < 0 ) { return; }
		Length -= Decr;					//{K = Succ(+Random (Decr);}
		g.forwd (+Length);
		g.turn  (+angle ); if (Grad >= 0) { Tree(Grad, Length, angle, Decr); }
		g.turn  (-angle
			     -angle ); if (Grad >= 0) { Tree(Grad, Length, angle, Decr); }
		g.turn  (+angle );
		g.forwd (-Length);
	}

	/**randomized Tree (Baum)
	 * with a Length, that is randomly varied, but a fixed opening Angle.  	 */
	public void Gras    (int Grad, int Length, int Variation, int angle) {
		--Grad; // < 0 ) { return; }
		Point2D buffer;
		int Strecke;		Strecke = (Length + (int) (Variation*Math.random ())) << 1;
		g.turn  (-angle  ); buffer = g.P.getLocation();
		g.forwd	(+Strecke); if (Grad >= 0) { Gras (Grad,Length,Variation,angle); }
/*		g.forwd	(-Strecke); */ g.P = buffer; //backtrack works, but this is faster
		g.turn	( angle
				 +angle  ); Strecke = (Length + (int) (Variation*Math.random ())) << 1;
		g.forwd	(+Strecke); if (Grad >= 0) { Gras (Grad,Length,Variation,angle); }
		g.forwd	(-Strecke);
		g.turn	(-angle  );
	}

	/**iteriertes (3,4,5) 90ø-Dreieck	 */
	public void Euklid  (int Grad, int Length) {
		final int angle = 37; //{angle in the 3,4,5 - Triangle (Dreieck})
		int D5, Kurz;
		Point2D buffer;
		--Grad; // < 0 ) { return; }
		D5 = Length / 5; Kurz = D5*3;
		g.forwd (Length); g.turn (-90        );
		g.forwd (Length); g.turn (    - angle); buffer = g.P.getLocation();
		g.forwd (Kurz  ); g.turn (180        ); if (Grad >= 0) { Euklid (Grad  , Kurz); }
/*		g.forwd (Kurz  );					*/  g.P = buffer; //backtrack works, but this is faster
						  g.turn ( 90 + angle);
		g.forwd (Length); g.turn (180 - angle); if (Grad >= 0) { Euklid (Grad  , Kurz + D5); }
		                  g.turn ( 90 + angle);
		g.forwd (Length); g.turn (-90        );
	}

	/**Die Peano-Kurve ist fogendermassen aufgebaut :   - 3
	 * Durch das Aufsplitten jeder Teil-Strecke       2|5|4
	 * in eine weitere Figur entsteht die fraktale   1-----9
	 * Struktur.Ob eine Seite aufzusplitten ist       6|7|8
	 * oder nicht,gibt das Feld 'Split' an.             -
	 * So kann man stufenweise Kurven der Dimensionen Log(3) (1..9) erzeugen.
	 * Length is the total Length of the curve. 	 */
	public void Peano   (int Grad, int Length, int Split) {
		final int[] angle = {	-90,+90,+90,
								+90,-90,-90,
								-90,+90,+00};
		if (--Grad < 0) {
			g.forwd (Length); return; }
		Length /= 3;
		int Mask = Split;
		int Z1 = -1; while (++Z1 < 9) {
			if	 ((Mask & 1) == 1) Peano (Grad, Length, Split);
			else {g.trail = false; g.forwd (Length); g.trail = true;}
			g.turn (angle [Z1]); Mask >>= 1;
		}
	}

	/**ArrowHead Line
	 * Length is the total Length of the curve. 	 */
	public void   Arrow     (int Grad, int Length) {
		if (Grad == 0) {
			g.forwd (Length); return; }
		int Winkel;
		Length >>= 1;
		if (Grad > 0)	{Winkel = +60; --Grad;}
		else			{Winkel = -60; ++Grad;}
		g.turn (+Winkel); Arrow (-Grad, Length);
		g.turn (-Winkel); Arrow (+Grad, Length);
		g.turn (-Winkel); Arrow (-Grad, Length);
		g.turn (+Winkel);
	}

	/**
	 * Arrow Line with constant predefined Length,
	 * Length is the Segment Length
	 */
	public void  Arrow1     (int Grad, int Length) {
		if (Grad == 0) {
			g.forwd (Length); return; }
		int Winkel;
		if (Grad > 0)	{Winkel = +60; --Grad;}
		else			{Winkel = -60; ++Grad;}
		g.turn (+Winkel); Arrow1 (-Grad, Length);
		g.turn (-Winkel); Arrow1 (+Grad, Length);
		g.turn (-Winkel); Arrow1 (-Grad, Length);
		g.turn (+Winkel);
	}

	/**
	 * ArrowHead Curve,
	 * Length is the total Length which is recursively reduces.
	 */
	public void  Arrow2 (int Grad, int Length) {
		if (--Grad < 0) {
			g.forwd (Length); return; }
		Length >>= 1;
		Arrow2 (Grad,Length); g.turn (+120);
		Arrow2 (Grad,Length); g.turn (-120);
		Arrow2 (Grad,Length); g.turn (-120);
		Arrow2 (Grad,Length); g.turn (+120);
		Arrow2 (Grad,Length);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Component: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/** Parameters for AppleMan and interesting Julia Plots */
	final static public double[][] JuliaParams = {
		null,
		{-0.194, -0.6557},
		{ 0.320,  0.0430}
		};

	/**
	 * Fundamental Painting Method for Frames and Applets
	 */
	public void paint(Graphics g) {
		System.out.println("Before painting");
		ScalarPlotNew SP = new ScalarPlotNew(new JavaGraphic(g));
		double[] StartValue = {-2, -1};
		double[] StepWidth  = {2.5/WIDTH, 2.0/HEIGHT};
		double[] JuliaParam = JuliaParams[0];
		int MaxDepth = 200;
		boolean retSize = true; //false; //true;
		final Apple apple = new Apple(StartValue, StepWidth, JuliaParam, MaxDepth, retSize);
		Point2D Width  = new Point2D(WIDTH, HEIGHT);
		Point2D Offset = new Point2D();
		boolean fillBlock = true;
		SP.Palette = PaletteRGB.CYCLE_PALETTE(MaxDepth+1);
		SP.refineRaster2D(Width, Offset, apple, fillBlock);
		System.out.println("After  painting");
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**Demonstrates filling an Area with a random Plasma Pattern/Texture.	 */
	final static public void paintPlasma(final ScalarPlotNew SP) {
		int [][] Picture = new int[400][400];
		Point2D MnMx;
		MnMx = raster2D (Picture, new Plasma(300, true));
//		MnMx = Plasma(300, Picture, true); //FarbStep, Pic, Decr. Flag
		SP.Palette = PaletteRGB.CYCLE_PALETTE(MnMx.getY()-MnMx.getX()+1);
		SP.ColorOffset = -MnMx.getX();
		SP.paintPicture (new Point2D(200, 200), Picture);	//
//		SP.refinePicture(new Point2D(200, 200), Picture, true);
	}

	/** test Output of fractal Curves  */
	public static void paintFractals(AGraph2D g2D) { //fractal Curves
		Fractal fr = new Fractal(g2D);
		g2D.P.setX(500);
		g2D.P.setY(350);
		g2D.trail = true;
		g2D.setColor(Color.black);
		fr.Dragon (13, 3 << 13);
		fr.Dragon1(13, 3);
		fr.C_Kurve(10, 3);
		fr.Euklid(9, 125);
		fr.Gras(3, 10, 8, 15);
		fr.Hilbert(7,2);
		fr.Koch(4, 320);
		fr.Peano(3, 320, 1+2+4+8+16+32+64+128+256+512);
		fr.Arrow (6, 1 << 8);
		fr.Arrow1(6, 3);
		fr.Arrow2(5,1 << 7);
		fr.RecKoch(4, 400);
		fr.Tree (4, 20, 15, 2);
	}

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Fractal.class.getName());
		Fractal f = new Fractal(); //Frame();
		f.setSize(WIDTH, HEIGHT);
		f.show();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
