package streamIO.copy.group.ring;

import streamIO.copy.group.IGroup;

/** This class inter / extrapolates the recursively added Values of
  * x and y Coordinates to 0 (Zero)
  *
  * It uses either rational or polynomial Interpolation,
  * where the first one is more flexible in the Presence of Poles.
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: c1176c5e06f4f2416b6034738fd3ba11f5c4c6233940c981f0c6b0a2ac222c9e
  * stale: false
  * tags: [code/ring_theory, code/ode_solver]
  * concepts: [Ring Algebra and ODE Solvers]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  * This Interpolator can be reset after work to calculate another Interpolation */
public class Extrapolator {

	/** Minimum Number of Points necessary for Extrapolation	 */
	public static int MinGrad = 2;

	/** Maximum Number of Points used for Extrapolation	 */
	public static int MaxGrad = 6;

	/** Default Value chosen by the constructors, if nothing specified	 */
	public static boolean rational = true;

	/** Default Value chosen by the constructors, if nothing specified	 */
	public static int Power = 1;	//1 == (linear Convergence)

	/** Degree of convergence (linear, square or higher)	 */
	protected int nPow;

	/** Flag for boolean Interpolation	 */
	protected boolean bolRatio;

	/** current maximum Dimension	 */
	protected int mDim;

	/** x Data	 */
	protected IIntRing[] xDat;

	/** y Data	 */
	protected IIntRing[] yDat;

	/** latest Result of the Extrapolation, used to store the Result
	  * and for retrieving the previous result on rational Extrapolation.	 */
	protected IIntRing alt;

	/** Helper Variable, used for convergence Testing	 */
	protected IIntRing Hilf;

	/** Full Constructor	 */
	public Extrapolator (int MaxGrad_, int Power, boolean rational) {
		if  (MaxGrad_ < MaxGrad) MaxGrad_ = MaxGrad;	//At least so many Items...
		else MaxGrad  = MaxGrad_;
		xDat = new AIntRing[++MaxGrad_];
		yDat = new AIntRing[  MaxGrad_];
		reset (Power, rational);
	}

	/** Constructor, defaults the Convergence Power 	 */
	public Extrapolator (int Power, boolean rational) { this(MaxGrad, Power, rational); }

	/** Constructor, defaults the 'rational' Switch 	 */
	public Extrapolator (int MaxGrad, int Power) { this(MaxGrad, Power, rational); }

	/** Constructor, defaults the 'rational' Switch 	 */
	public Extrapolator (int Power) { this(MaxGrad, Power, rational); }

	/** Empty Constructor, defaults all. 	 */
	public Extrapolator () { this(MaxGrad, Power, rational); }

	/** Reset the Extrapolator, so a new Extrapolation can be performed.	 */
	public void reset(int Power, boolean rational) {
        mDim = -1; nPow = Power; bolRatio = rational; alt = null; }

	/** Reset the Extrapolator, so a new Extrapolation can be performed.	 */
	public void reset(int Power) { reset(Power, rational); }

	/** Reset the Extrapolator, so a new Extrapolation can be performed.	 */
	public void reset(boolean rational) { reset(Power, rational); }

	/** Reset the Extrapolator, so a new Extrapolation can be performed.	 */
	public void reset()	{ reset(Power, rational); }

	/** Adding a pair of Values to the Extrapolator.
	  * These Values are changed during Calculation, so better make copies beforehand.
	  * The Array is enlarged automatically, when the maximum Degree is reached.	 */
	public boolean addPoint(IIntRing x, IIntRing y) {
     //{Das Feld wird von hinten aufgefuellt und berechnet}
//		if (Compare == null) Compare = ((MetricIRing) y).mulAbsAccuracy();
		if (++mDim == yDat.length) { 	//enlarge the arrays
			int n2 = yDat.length << 1;
			xDat = AIntRing.ArrayCopy (xDat, n2);
			xDat = AIntRing.ArrayCopy (xDat, n2); }
		IIntRing RZ1, RZ3 = (xDat[mDim] = (IIntRing) x.copy());	//{ganzzahlige Potenzierung!}
		IIntRing RZ2, RZ4 = (yDat[mDim] = (IIntRing) y.copy());
		if (nPow > 1) RZ3.PowAt(nPow);
		IIntRing f_alt, Faktor;
		int Z = mDim; Hilf = null;
		while (--Z >= 0) {
			RZ1 = xDat[Z];
			RZ2 = yDat[Z];
			Faktor = (IIntRing) RZ1.div (RZ3);	//{ganzzahlige Potenzierung auch hier anwendbar!}
			Hilf   = (IIntRing) RZ4.sub(RZ2);
			if (Hilf.isZero())
            	RZ2.copyAt(RZ4);//Z = -1;	//don't stop the Iteration
            else { 	//same y Value! => no correction
				if (bolRatio) {
					f_alt = RZ4;
					if (Z < mDim-1) f_alt.sub(alt);	//instead of setting alt to 0!
					if (f_alt.isZero()) Hilf.zeroAt();
					else Faktor.mulAt(((IIntRing) Hilf.div(f_alt)).ResidAt());	} 	//Residual: 1-x
//				if (! (Faktor.isZero()))    //same x Value! shouldn't arrive here, because y Value should be the same!
					((IGroup)RZ2.copyAt(RZ4)).addAt (Hilf.divAt(Faktor.dec()));
//				else throw new AbstractMethodError();
			}
			alt = (RZ4 = RZ2); //{Speicherung fuer...
		}  //{ a) naechste rationale Extrapol., b) Vergleich mit vorheriger Extr.}
		if (mDim < MinGrad) return false; //{faengt bei 0 an,aber rationale Extrapol. von 0 aus fuehrt auf 0!!!}
		return (mDim == MaxGrad); }

	/** Returns the Value of the Extrapolation to 0	 */
	public IIntRing getValue()	{return yDat[0];}


	//////////////
	//	Testing	//
	//////////////

	/** Method to test all Implementations in this class.	 */
	public static void testIt() { 	//Extrapolator not inherited from Copy, doesn't know testInstance!
		System.out.println("Testing Extrapolator:");
		System.out.println("Interpolating the following Polynom to x = 0");
		IIntRing test1 = (IIntRing) streamIO.copy.ACopyAble.testInstance.copy();
		IIntRing test2 = (IIntRing) test1.copy();
//		double[] x1 = {0.0, 1.0, 3.0};
//		double[] y1 = {1.0, 3.0, 2.0};

//		Interpolator Int =  BodyDouble.Interpolator(x1, y1, 3);
//		System.out.println("Value of the Interpolating Polynom: " + Int.map(new BodyDouble(2.0)));

		//The Numbers are derived from ???
		double[] y2 = {57.28996163, 28.63625328, 19.08113669, 14.30066626, 11.43005230};
		double[] x2 = {-1.5, -0.5, 0.5, 1.5, 2.5};

		System.out.println("X = (" + streamIO.AStreamOut.ARRAY_TO_STRING(x2, ",") + ")");
		System.out.println("Y = (" + streamIO.AStreamOut.ARRAY_TO_STRING(y2, ",") + ")");
		System.out.println("Correct Value :" + 22.9037655484);

		IIntRing x = test1;
		IIntRing y = test2;
		Extrapolator Ext = new Extrapolator(5, 1, false);
		int i = -1;
		while (++i < x2.length) {
			x.copyAt(new Double(x2[i]));	//Math.random());
			y.copyAt(new Double(y2[i]));	//Math.cos(x.Value));
			Ext.addPoint(x, y);
			System.out.println("Value of the polynomial Extrapolation: " + Ext.getValue()); }

		Ext.reset(1, true);
		i = -1;
		while (++i < x2.length) {
			x.copyAt(new Double(x2[i]));	//Math.random());
			y.copyAt(new Double(y2[i]));	//Math.cos(x.Value));
			Ext.addPoint(x, y);
			System.out.println("Value of the rational Extrapolation: " + Ext.getValue()); }
	}

}
