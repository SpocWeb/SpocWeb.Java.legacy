package streamIO.copy.group.ring;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ACopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.groupM.IGroupM;
import function.AFunction;
import function.IMeasurAble;

/**
 * This Class implements a polynomial Interpolator
 * that interpolates a Function dependent on a one-dimensional Variable.
 * Don't need to use an Array of Interpolators to interpolate a Vector Function,
 * rather use Vectors at the y Positions. 
 * 
 * Also contains Methods for cubic Spline Interpolation.  
 * 
 * TODO: For multidimensional Interpolation, you can iterate 1-dim Interpolations 
 * along the Dimensions, e.g. for interpolating a Matrix along two Dimensions:  
 * interpolate Vectors along the first Dimension and then 
 * interpolate the Scalars along the second Dimension. 
 * 
 * TODO: For multidimensional Spline Interpolation, you can iterate 1-dim Interpolations 
 * 
 * Similar Classes: 
 * @see math.InterpolDouble
 * 
 */
public class Interpolator
extends AFunction {

	final static public double DOUBLE_OVERFLOW = Math.sqrt(IMeasurAble.DOUBLE_OVERFLOW);
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Interpolator.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////
	// Polynom Spline Methods
	/////////////////////////////////////////////////////////////////////////////////////

	/** SPLINE  construct a cubic spline (3.3)  
	 * called only once to generate the Spline Coefficients in Place. 
	 * Solves a special tridiagonal linear System of Equations. 
	 * @param x x-Values (sorted ascending) 
	 * @param y y-Values at the given x-Values
	 * @param start Begin(inclusive) to consider 
	 * @param stop  End  (exclusive) to consider 
	 * @param ypStart 1st Derivative at the start Point (0)
	 * @param ypStop  1st Derivative at the stop  Point (x.length-1)
	 * @param y2
	 */
	final static public void PREPARE_SPLINE(final double x[], final double y[], final double yp0, final double ypLast, final double y2[]) {
		PREPARE_SPLINE(x, y, 0, x.length, yp0, ypLast, y2); }

	/** SPLINE  construct a cubic spline (3.3)  
	 * called only once to generate the Spline Coefficients in Place. 
	 * Solves a special tridiagonal linear System of Equations. 
	 * @param x x-Values (sorted ascending) 
	 * @param y y-Values at the given x-Values
	 * @param start Begin(inclusive) to consider 
	 * @param stop  End  (exclusive) to consider 
	 * @param ypStart 1st Derivative at the start Point 
	 * @param ypStop  1st Derivative at the stop  Point 
	 * @param y2
	 */
	final static public void PREPARE_SPLINE(final double x[], final double y[], final int start, final int stop, final double ypStart, final double ypStop, final double y2[]) {
		final int n = stop;
		final double[] u=new double[n]; //1..n-1
		if (ypStart > DOUBLE_OVERFLOW) {
			y2[start]=u[start]=0;
		} else {
			y2[start] = -0.5;
			u[start]=(3/(x[start+1]-x[start]))*((y[start+1]-y[start])/(x[start+1]-x[start])-ypStart);
		}
		for (int i=start+1; i < n; i++) {
			final double sig=(x[i]-x[i-1])/(x[i+1]-x[i-1]);
			final double p=sig*y2[i-1]+2;
			y2[i]=(sig-1)/p;
			u[i]=(y[i+1]-y[i])/(x[i+1]-x[i]) - (y[i]-y[i-1])/(x[i]-x[i-1]);
			u[i]=(6*u[i]/(x[i+1]-x[i-1])-sig*u[i-1])/p;
		}
		final double qn, un; 
		if (ypStop > DOUBLE_OVERFLOW)
			qn = un = 0;
		else {
			qn=0.5;
			un=(3/(x[n]-x[n-1]))*(ypStop-(y[n]-y[n-1])/(x[n]-x[n-1]));
		}
		y2[n]=(un-qn*u[n-1])/(qn*y2[n-1]+1);
		for (int k=n-1; k >= start; k--) {
			y2[k]=y2[k]*y2[k+1]+u[k]; } 
	}

	/** SPLINT  cubic spline interpolation (3.3)  
	 * called iteratively for each Interpolation
	 * 
	 * @param xa x-Values (sorted ascending) 
	 * @param ya y-Values at the given x-Values
	 * @param y2a 2nd Derivatives at the given x-Values
	 * @param x the Value at which to interpolate
	 * @return y, the interpolated Value for x 
	 */ 
	final static public double INTERPOL_SPLINE(final double[] xa, final double[] ya, final double[] y2a, final double x) {
		return INTERPOL_SPLINE(xa, ya, y2a, 0, xa.length, x); }

	/** SPLINT  cubic spline interpolation (3.3)  
	 * called iteratively for each Interpolation
	 * 
	 * @param xa x-Values (sorted ascending) 
	 * @param ya y-Values at the given x-Values
	 * @param y2a 2nd Derivatives at the given x-Values
	 * @param start Begin(inclusive) to consider 
	 * @param stop  End  (exclusive) to consider 
	 * @param x the Value at which to interpolate
	 * @return y, the interpolated Value for x 
	 */ 
	final static public double INTERPOL_SPLINE(final double[] xa, final double[] ya, final double[] y2a, final int start, final int stop, final double x) {
		//find the appropriate Interval of Size 1 by BiSection 
		int klo=start;
		int khi=stop-1;
		while (khi-klo > 1) {
			int k=(khi+klo) >> 1;
			if (xa[k] > x) khi=k;
			else klo=k;
		}
		final double h=xa[khi]-xa[klo];
		if (h == 0) {
			throw new RuntimeException("Bad xa input to routine splint"); }
		//calculate the Spline Value  
		final double a=(xa[khi]-x)/h;
		final double b=(x-xa[klo])/h;
		return a*ya[klo]+b*ya[khi]+((a*a*a-a)*y2a[klo]+(b*b*b-b)*y2a[khi])*(h*h)/6;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Degree of the Polynom 	 */
	private int mDim = -1;

	/** x-Positions of the Samples */
	private IIntRing[] x;

	/** y-Positions of the Samples as divided Differences */
	private IIntRing[] t;

	/** Coefficients of the Interpolation Polynom	 */
	private IIntRing[] a;

	private void allocate(int n) {
		this.t = new IIntRing[n];
		this.x = new IIntRing[n];
		this.a = new IIntRing[n];
	}

	/**Empty Constructor building an empty Interpolation Polynom of Length 7.	 */
	public Interpolator()	{ allocate (7); }

	/**Constructor building an empty Interpolation Polynom of the given Length.	 */
	public Interpolator(final int length)	{ allocate (length); }

	/**Constructor building the Interpolation Polynom
	 * from the Samples given in x and y up to the length n.	 */
	public Interpolator(final IIntRing[] x_, final IIntRing[] y_) {
		this (x_, y_, Math.min(x_.length, y_.length));}

	/**Constructor building the Interpolation Polynom
	 * from the Samples given in x and y up to the length n.	 */
	public Interpolator(final IIntRing[] x_, final IIntRing[] y_, int n) {
		if (n > y_.length) n = y_.length;
		if (n > x_.length) n = x_.length;
		allocate (n);
		int i = -1;	//i == mDim!
		while (++i < n) { 
			addPoint(x_[i], y_[i]);} 
	}

	/**Add another Sample Point to the Interpolation Polynom.	 */
	public void addPoint(final IIntRing x_, final IIntRing y_) {
		int k = ++mDim;
		if (k >= x.length) {	//redimension the Arrays
			int n2 = x.length << 1;	//double the Size
			t = AIntRing.ArrayCopy (t, n2);
			x = AIntRing.ArrayCopy (x, n2);
			a = AIntRing.ArrayCopy (a, n2);
		}

		final IIntRing x_i = x [mDim] = (IIntRing) x_.copy();
		IIntRing t_i = t [mDim] = (IIntRing) y_.copy();
		while (--k >= 0) { 
			t_i = (IIntRing) ((IGroupM)t[k].subAt(t_i)).divAt(x[k].sub(x_i)); } 
		a[mDim]= (IIntRing) t[0].copy();
	}

	/**Value of this Interpolation Polynom at Position arg	 */
	public Object Map(final Object arg) { return Horner ((IIntRing) arg); }

	/** evaluates the Interpolation at the given Position using the Horner Scheme */
	public IIntRing Horner(final IIntRing arg) {
		int i = mDim;
		IIntRing ret = (IIntRing) a[i].copy();
		while (--i >= 0) {
			((IGroup) ret.mulAt(arg.sub(x[i]))).addAt(a[i]); } 
		return ret; }


	/////////////////////////////////////////////////////////////////////////////////////
	//	Testing	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	private static final void testInterpolator() {	//So far you can test only polynomial and rational Functions
		//Polynoms are interpolated exactly
		L.n("Testing Interpolator:");
		L.n("Interpolating the following Polynom to x = 0");
		if (ACopyAble.testInstance == null) {
			ACopyAble.testInstance = new BodyDouble(); }
		IIntRing x = (IIntRing) ACopyAble.testInstance.copy();
		IIntRing y = (IIntRing) x.copy();

		double[] x1 = {0, 1, 3};
		double[] y1 = {1, 3, 2};
		//The Numbers are derived from ???
//		double[] y2 = {57.28996163, 28.63625328, 19.08113669, 14.30066626, 11.43005230};
//		double[] x2 = {-1.5, -0.5, 0.5, 1.5, 2.5};

		Interpolator interpol =  new Interpolator(3);
		L.n("X = (" + streamIO.AStreamOut.ARRAY_TO_STRING(x1, ",") + ")");
		L.n("Y = (" + streamIO.AStreamOut.ARRAY_TO_STRING(y1, ",") + ")");
		L.n("Value of the Interpolating Polynom at the Seeding Points: ");
		//Add all the points to the Polygon
		for (int i = x1.length; --i >= 0; ) {
			x.copyAt(new Double(x1[i]));
			y.copyAt(new Double(y1[i]));
			interpol.addPoint(x,y);
			L.n("x=" + x + " ; y=" + interpol.Map(x));
			Assert.EQUALS(y, interpol.Map(x)); 
		}
		//Test, if the old values are still interpolated
		for (int i = x1.length; --i >= 0; ) {
			x.copyAt(new Double(x1[i]));
			L.n("x=" + x + " ; y=" + interpol.Map(x));
			Assert.EQUALS(interpol.Map(x), new Double(y1[i])); 
		}

		//rational Functions not at all...
	}

	/** tests Interpolation via Spline Polynom(s) 	 */
	private static final void testInterpolSpline() {
		final int NP = 10; 
		int i,nfunc;
		double f,x,y,yp0,yp1,ypn;

		final double[] xa= new double[1+NP];
		final double[] ya= new double[1+NP];
		final double[] y2= new double[1+NP];
		for (nfunc=1;nfunc<=2;nfunc++) {
			if (nfunc == 1) {
				L.n("\nsine function from 0 to pi\n");
				for (i=0; i<=NP; i++) {
					xa[i]=i*IMeasurAble.PI/NP;
					ya[i]=Math.sin(xa[i]);
				}
				yp0=Math.cos(xa[0]);
				yp1=Math.cos(xa[1]);
				ypn=Math.cos(xa[NP]);
			} else if (nfunc == 2) {
				L.n("\nexponential function from 0 to 1\n");
				for (i=0; i<=NP; i++) {
					xa[i]=1.0*i/NP;
					ya[i]=Math.exp(xa[i]);
				}
				yp0=Math.exp(xa[0]);
				yp1=Math.exp(xa[1]);
				ypn=Math.exp(xa[NP]);
			} else {
				break;
			}
			L.l(yp1); 
			/* Call spline to get second derivatives */
			PREPARE_SPLINE(xa, ya, 0, NP, yp0, ypn, y2);
			/* Call splint for interpolations */
			L.n("x").l("\tf(x)").l("\tinterpolation");
			for (i=0;i<=10;i++) {
				if (nfunc == 1) {
					x=(-0.05+i/10.0)*IMeasurAble.PI;
					f=Math.sin(x);
				} else {
					x = -0.05+i/10.0;
					f=Math.exp(x);
				}
				y = INTERPOL_SPLINE(xa, ya, y2, x);
				L.n().l(x).l(f).l(y);
				Assert.EQUALS(f, y, 0, 1e-4); //
			}
			L.n("\n***********************************\n");
			L.n("Press RETURN\n");
			L.readString();
		}
	}

	/** tests generating the Spline Interpolation Polynom(s) 	 */
	private static final void testSpline() {
		final int N = 20; 
		int i;
		double yp1,ypn;
		
		final double[] x=new double[1+N];
		final double[] y=new double[1+N];
		final double[] y2=new double[1+N];
		L.n("second-derivatives for sin(x) from 0 to pi");
		/* Generate array for interpolation */
		for (i=0; i<=20; i++) {
			x[i]=i*IMeasurAble.PI/N;
			y[i]=Math.sin(x[i]);
		}
		/* calculate 2nd derivative with spline */
		yp1=Math.cos(x[1]);
		ypn=Math.cos(x[N]);
		PREPARE_SPLINE(x,y,1,N,yp1,ypn,y2);
		/* test result */
		L.n("spline"+"\tactual");
		L.n("\tangle").l("\t2nd deriv").l("\t2nd deriv");
		for (i=1; i<=N; i++) {
			final double ddy = -Math.sin(x[i]);
			L.n().l(x[i]).l(y2[i]).l(ddy); 
			Assert.EQUALS(y2[i], ddy, .002, .002); //x[1], x[1]);
		} 
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) throws Exception {
		L.n("Testing " + Interpolator.class.getName());
		testInterpolator(); 
		testInterpolSpline(); 
		testSpline(); 
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(args);
	}

}
