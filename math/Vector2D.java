package math;

import math.vector.VectorDouble;
import function.byref.ByRefDouble;

/**
 * Represents a 2D Vector holding its Coordinates in a shared Array, interpretable under
 * two Coordinate Systems, plus static helper Methods for Intervals and 2x2 linear algebra.
 *
 * <p>Rectangular:	a[0] = x, a[1] = y
 * Cylindric:	a[0] = r, a[1] = phi
 *
 * @see math.VectorDouble which implements most of this Functionality
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:48:10Z
 * digest: d85756be64cee3a4493516eaafb2ec977ede292a7d4c3c3621f257eb25e2313a
 * stale: false
 * tags: [code/vector_math, code/2d_geometry]
 * concepts: [2D Vector]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
 */
final public class Vector2D {

////////////////////////////////////////////////////////////////////////////////
//  static Methods for 2Dim Vectors:
////////////////////////////////////////////////////////////////////////////////

	/**
	 * x is checked to be in this Interval and if not, the Interval is widened.
	 * ret is chosen to be a 2D Array to allow returning it ByRef.  */
	final static public double[] MaxInterval(double[] ret, double x) {
		     if (x < ret[0]) ret[0] = x; //{ELSE brauchbar nachdem mindestens Min = Max ist}
		else if (x > ret[1]) ret[1] = x; //{also nicht f?r das erste Mal !}
		return ret; }

	/** x is checked to be in this Interval and if not, the Interval is widened.	 */
	final static public void MaxInterval(double[] lower, double[] upper, double[] x, int len) {
		while (--len >= 0) {
			if (lower[len] > x[len]) {
				lower[len] = x[len]; continue; } //{ELSE brauchbar nachdem mindestens Min = Max ist}
			if (upper[len] < x[len]) {
				upper[len] = x[len]; } //{ELSE brauchbar nachdem mindestens Min = Max ist}
		}
	}

	/**Orders an Interval given in this 2D Vector,
	 * so that a[0] < a[1].	 */
	final static public double[] orderInterval (double[] ret) {
		if (ret[0] > ret[1]) {
			double tmp = ret[0]; ret[0] = ret[1]; ret[1]= tmp;}
		return ret; }

	/**Returns a Value that determines, if x is contained in this Interval:
	 * -2 for x    < a[0] < a[1]
	 * -1 for x    = a[0] < a[1]
	 *  0 for a[0] < x    < a[1]
	 * +1 for a[0] < a[1] = x
	 * +2 for a[0] < a[1] < x
	 */
	final static public int containsX (double x, double left, double right) {
		return Sign (x-left)+Sign (x-right); }

	/**
	 * Returns a Value that determines,
	 * in what Way two ordered Intervals overlap:
	 * -2 for x    < a[0] < a[1]
	 * -1 for x    = a[0] < a[1]
	 *  0 for a[0] < x    < a[1]
	 * +1 for a[0] < a[1] = x
	 * +2 for a[0] < a[1] < x
	 */
	final static public int Overlap  (double l1, double r1, double l2, double r2) {
		return Sign (r1-l2)+Sign (l1-r2); }

	/**
	 * Returns a Value that determines more closely than Overlap(),
	 * in what Way two Intervals overlap:
	 * -10 for B.a[0] < B.a[1] <   a[0] <   a[1]
	 * - 6 for B.a[0] < B.a[1] =   a[0] <   a[1]
	 * - 5 for B.a[0] = B.a[1] =   a[0] <   a[1]
	 * - 2 for B.a[0] <   a[0] < B.a[1] <   a[1]
	 * - 1 for B.a[0] =   a[0] < B.a[1] <   a[1]
	 *   0 for   a[0] < B.a[0] < B.a[1] <   a[1]
	 * + 2 for B.a[0] <   a[0] < B.a[1] =   a[1]
	 * + 3 for   a[0] = B.a[0] <   a[1] = B.a[1]	i.e Identical Intervals
	 * + 4 for   a[0] < B.a[0] <   a[1] = B.a[1]
	 * + 5 for   a[0] < B.a[0] =   a[1] = B.a[1]
	 * + 6 for B.a[0] <   a[0] <   a[1] < B.a[1]
	 * + 7 for   a[0] = B.a[0] <   a[1] < B.a[1]
	 * + 8 for   a[0] < B.a[0] <   a[1] < B.a[1]
	 * + 9 for   a[0] < B.a[0] =   a[1] < B.a[1]
	 * +10 for   a[0] <   a[1] < B.a[0] < B.a[1]	 */
	final static public int Equality (double l1, double r1, double l2, double r2) {
		return (containsX (l1, r1, r2) << 2) + containsX (l1, r1, l2);}

///////////////////////////////////////////////////////////////////////////////////
//  Matrices and Determinants
///////////////////////////////////////////////////////////////////////////////////

	/**Loest die Quadratischen Gleichungen a+bx+x^2 = 0 bzw. a+bx+cx^2 = 0
	 * true ,wenn die quadratische Gleichung loesbar ist.
	 * Dann enthaelt x1 die Betrags-kleinere Loesung und x2 die groessere.
	 * false,wenn die Gleichung nur 2 konjugiert komplexe Loesungen besitzt.
	 * Dann enthaelt x1 den Real-Teil und x2 den positiven Imaginaer-Teil.	 */
	final static public boolean SolveSqr2 (double a, double b, double c, double[] x) {
		return SolveSqr2 (a/c, b/c, x);}

	/**
	 * Loest die Quadratischen Gleichungen a+bx+x^2 = 0 bzw. a+bx+cx^2 = 0
	 * @return true ,wenn die quadratische Gleichung loesbar ist.
	 * Dann enthaelt x1 die Betrags-kleinere Loesung und x2 die groessere.
	 * false,wenn die Gleichung nur 2 konjugiert komplexe Loesungen besitzt.
	 * Dann enthaelt x1 den Real-Teil und x2 den positiven Imaginaer-Teil.
	 * Design Decisions:
	 * To return two Values an Array is used.
	 * No effort is made to avoid indexed Array Access although it is not fast.
	 */
	final static public boolean SolveSqr2 (double a, double b, double[] x) {
		b/=2;
		x[1] = b*b-a;
		x[2] = Math.sqrt(Math.abs(x[1]));
		if (x[1] < 0) {
			x[1] = -b; return false; }
		x[2] = Math.abs(b) + x[2]; //{Verfahren fuer hoehere Genauigkeit!!}
		if (b > 0) {
			x[2] = -x[2]; }
		x[1] = a/x[2];
		return true; }

	/**
	 * This is equivalent to the Area of the Parallelogram
	 * bounded by the two given Vectors!
	 * @return the Determinant of the Matrix given by the two Vectors
	 */
	final static public double DET2x2(double a11, double a12, double a21, double a22) {
		return a11*a22-a12*a21; }

	/**
	 * Returns the Eigenvalues and Determinant of the Matrix |d1,d2|
	 * in the Array handed over.
	 * @return true when the characteristic Equation could be solved for real Eigenvalues,
	 * false otherwise.
	 * @param EWDet contains Eigenvalues of the EigenValue Problem
	 * (and the Determinant when the Array is longer than 2)
	 */
	final static public boolean EW_2x2 (
		double a11, double a12, double a21, double a22,
		double[] EWDet) {
		double Det = a11*a22-a12*a21; //DET2x2(a11, a12, a21, a22),
		if (EWDet.length > 2) { EWDet[2] = Det; }
		return SolveSqr2 (Det, -a11 -a22, EWDet); }

	/**
	 * Eigenvalues and Determinant of the Matrix |d1,d2|
	 * @return the Determinant of the Eigenvalues
	 */
	final static public boolean EW_2x2 (double[] d0, double[] d1, double[] EWDet) {
		return EW_2x2(d0[0], d0[1], d1[0], d1[1], EWDet); }

	/**
	 * Eigenvalues and Determinant of the Matrix |d0,d1|
	 * @return the Determinant of the Eigenvalues
	 */
	public boolean EW_2x2 (
		Vector2D d1, Vector3D EWDet) {
		return EW_2x2(a, d1.a, EWDet.a); }

////////////////////////////////////////////////////////////////////////////
//  Member Variables
////////////////////////////////////////////////////////////////////////////

	/**
	 * Using an Array for greater Flexibility
	 * although Array Access incurs some Runtime Overhead
	 * compared to individual Elements
	 */
	public double a[] = new double[2];

////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/**Empty Constructor.	 */
	public Vector2D(){}

	/**Constructor that takes the x and y Value separately.	 */
	public Vector2D(final double x, final double y){ a[0]=x; a[1]=y; }

	/**Constructor that takes the first two Elements of an Array.	 */
	public Vector2D(final double x []){ a = x; }

	/**
	 * This Implementation uses 1 Comparison and 1 Division more
	 * than Norm2 = SqRt (Sqr (x)+Sqr (y)),
	 * but never exceeds the Range.
	 */
	public static double Norm2D (final double x, final double y) {
//		double AbsX;
//		double AbsY; //
		if ((Math.abs(x)) > (Math.abs(y))) {
			return x*Math.sqrt(1.0+ByRefDouble.SQR (y/x)); }
			return y*Math.sqrt(1.0+ByRefDouble.SQR (x/y)); }

	/**Converts the polar Representation of a 2D Vector
	 * to it's rectangular Representation.	 */
	public Vector2D Polar2Rect() {
		double[] ret = new double[2]; 
		ByRefDouble.COS_SIN(a[1], ret);
		VectorDouble.MUL_AT(ret, a[0]);
		return new Vector2D(ret);
	/*	return new Vector2D(a[0]*Math.cos(a[1]),
							a[0]*Math.sin(a[1]));
	*/}

	/**Converts the rectangular Representation of a 2D Vector
	 * to it's polar Representation.	 */
	public Vector2D Rect2Polar() {
		return new Vector2D(Norm2D(a[0],a[1]),
							Math.atan2(a[0],a[1]));}

	/**
	 * Gives the 3D Coordinates for a Place on the Unit Sphere
	 * given by the Coordinates in this Vector2D.
	 */
	public Vector3D UnitSphaeric2Rect() {
		Vector3D tmp = new Vector3D();
		tmp.a [2] = Math.sin(a [1]);
		tmp.a [1] = Math.cos(a [1]);
		tmp.a [0] = tmp.a [1]*Math.cos(a [0]);
		tmp.a [1]*=           Math.sin(a [0]);
		return tmp; }

	/**x is checked to be in this Interval and if not, the Interval is widened.	 */
	public Vector2D MaxInterval(double x) {
		if (a[0] > x) {
			a[0] = x;   return this; } //brauchbar nachdem mindestens Min = Max ist!
		if (a[1] < x) { //{also nicht fuer das erste Mal !}
			a[1] = x; } return this; }

	/**Orders an Interval given in this 2D Vector,
	 * so that a[0] < a[1].	 */
	public void orderInterval () {
		if (a[0] > a[1]) {
			double tmp = a[0]; a[0] = a [1]; a[1]= tmp;}
	}

	/**Checks if the Interval contains x.
	 * This Implementation is unsymmetric, always a[0] < a[1] assumed
	 * there is no fast correct Solution, only Compromises !}	 */
	public boolean contains (double x) {
		return (a[0] <= x) ^ (a[1] < x); }

	/**Returns the Sign of x as an integer, i.e.
	 * -1 for negative x
	 *  0 for x == 0
	 * +1 for positive x	 */
	final static public int Sign(double x) {
		return (x > 0) ? 1 : (x < 0) ? -1 : 0; }

	/**Returns a Value that determines, if x is contained in this Interval:
	 * -2 for x		< a[0]	< a[1]
	 * -1 for x		= a[0]	< a[1]
	 *  0 for a[0]	< x		< a[1]
	 * +1 for a[0]	< a[1]	= x
	 * +2 for a[0]	< a[1]	< x	 */
	public int containsX (double x) {
		return Sign (x-a[0])+Sign (x-a[1]); }

	/**Returns a Value that determines, in what Way two Intervals overlap:
	 * -2 for x		< a[0]	< a[1]
	 * -1 for x		= a[0]	< a[1]
	 *  0 for a[0]	< x		< a[1]
	 * +1 for a[0]	< a[1]	= x
	 * +2 for a[0]	< a[1]	< x	 */
	public int Overlap  (Vector2D B) {
		return Sign (a[1]-B.a[0])+Sign (a[0]-B.a[1]); }

	/**Returns a Value that determines more closely, in what Way two Intervals overlap:
	 * -10 for B.a[0] < B.a[1] <   a[0] <   a[1]
	 * - 6 for B.a[0] < B.a[1] =   a[0] <   a[1]
	 * - 5 for B.a[0] = B.a[1] =   a[0] <   a[1]
	 * - 2 for B.a[0] <   a[0] < B.a[1] <   a[1]
	 * - 1 for B.a[0] =   a[0] < B.a[1] <   a[1]
	 *   0 for   a[0] < B.a[0] < B.a[1] <   a[1]
	 * + 2 for B.a[0] <   a[0] < B.a[1] =   a[1]
	 * + 3 for   a[0] = B.a[0] <   a[1] = B.a[1]	i.e Identical Intervals
	 * + 4 for   a[0] < B.a[0] <   a[1] = B.a[1]
	 * + 5 for   a[0] < B.a[0] =   a[1] = B.a[1]
	 * + 6 for B.a[0] <   a[0] <   a[1] < B.a[1]
	 * + 7 for   a[0] = B.a[0] <   a[1] < B.a[1]
	 * + 8 for   a[0] < B.a[0] <   a[1] < B.a[1]
	 * + 9 for   a[0] < B.a[0] =   a[1] < B.a[1]
	 * +10 for   a[0] <   a[1] < B.a[0] < B.a[1]	 */
	public int Equality (Vector2D B) {
		return (containsX (B.a[1]) << 2) + containsX (B.a[0]);}

	/**Calculates the Determinant of the Matrix given by the two Vectors	 */
	public double DET2x2(Vector2D du) {
		return DET2x2(a[0], a[1], du.a[0], du.a[1]); }

}
