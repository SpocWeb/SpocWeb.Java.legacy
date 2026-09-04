package streamIO.copy.group.ring.metric;

import math.vector.VectorDouble;
import streamIO.copy.ACopyAble;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.IMeasurAble;
import function.vector.IBinaryOpFloat;

/**Integrates the given ODE in (x,y) using the Runge-Kutta-Fehlberg Formula.
 *
 * Step() performs a single Step with variable width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 */
public class StepRKF
extends StepRKQ { //AStepper { //

	/**Local Helper Vectors, contains intermediate Results	 */
	protected IIntRing dy, yS;

	/**Local Helper Vectors, contains intermediate Results	 */
	protected double[] dY, YS;

	public void Init(IIntRing Step, IIntRing x_, IIntRing y_, IODE f_) {
		super.Init(Step, x_, y_, f_);
		dy = (IIntRing) y_.newInstance();
		yS = (IIntRing) y_.newInstance();
		derive = true;	//recalculate the Derivative on next Step!
	}	//Allocate Space to save iterative Allocation and Destruction!

	public void Init(double Step, double x_, double[] y_, IBinaryOpFloat f_) {
		super.Init(Step, x_, y_, f_);
		dY = new double[y_.length];
		YS = new double[y_.length];
		derive = true;	//recalculate the Derivative on next Step!
	}	//Allocate Space to save iterative Allocation and Destruction!

	/**
	 * @param stepSize_
	 * @param x
	 * @param y
	 * @param accuracy_
	 * @param f
	 */
	public StepRKF(double stepSize_, double x, double y, double accuracy_, IBinaryOpFloat f) {
		super(stepSize_, x, y, accuracy_, f); }

	/**
	 * @param stepSize_
	 * @param x
	 * @param y
	 * @param accuracy_
	 * @param f
	 */
	public StepRKF(double stepSize_, double x, double[] y, double accuracy_, IBinaryOpFloat f) {
		super(stepSize_, x, y, accuracy_, f); }

	/** Constructor with all necessary Parameters  */
	public StepRKF(IIntRing Step, IIntRing x, IIntRing y, IODE f) {
		super(Step, x, y, f); }

	private static final int H2106 = 2106;
	private static final int H0533 =  533;
	private static final int H_189 = -189;
	private static final int H0729 =  729;
	private static final int H0540 =  540;
	private static final int H1600 = 1600;
	private static final int H0214 =  214;
	private static final int H0027 =   27;
	private static final int H0650 =  650;
	private static final int H_078 =  -78;
	private static final int H0800 =  800;
	private static final int H0891 =  891;

	private static final Integer h2106 = new Integer(H2106);
	private static final Integer h0533 = new Integer(H0533);
	private static final Integer h_189 = new Integer(H_189);
	private static final Integer h0729 = new Integer(H0729);
	private static final Integer h0540 = new Integer(H0540);
	private static final Integer h1600 = new Integer(H1600);
	private static final Integer h0214 = new Integer(H0214);
	private static final Integer h0027 = new Integer(H0027);
	private static final Integer h0650 = new Integer(H0650);
	private static final Integer h_078 = new Integer(H_078);
	private static final Integer h0800 = new Integer(H0800);
	private static final Integer h0891 = new Integer(H0891);

	/**Performs one Runge Kutta Fehlberg Step with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * V1 retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)	 */
	public IIntRing step(final IIntRing h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation
//  	do {	//This would require buffering the original Values!
		if (derive)	{derive = false;						 f.Funktion (x, y, dy); }	//{dy = F (x,y)}	//Derivative doesn't have to be calculated next time!
			IIntRing H1 = (IIntRing) h.quarter( );	//dy=F (x,yS)
			IIntRing H2 = (IIntRing) h.div(h2106);
			yS.copyAt (y );	y .addProdAt(H2.mul(h0533), dy);	//neues y:
			v2.copyAt (yS);	v2.addProdAt(H1			  , dy); f.Funktion ((IIntRing) x.add(H1), v2, v1);	//V1=F (x+h/4,yS+h/4*dy)
			H1 = (IIntRing) h.div(h0800);
			v3.copyAt (yS);	v3.addProdAt(H1.mul(h_189), dy);	//neues y: 729 - 189 = 540 !
							v3.addProdAt(H1.mul(h0729), v1); f.Funktion ((IIntRing) x.add(H1.mul(h0540)), v3, v2);	//V2=F (x+h*27/40,yS-h*189/800*dy+h*729/800*V1)
							y .addProdAt(H2.mul(h1600), v2);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
			H2 = (IIntRing) h.div(h0891);
			w .copyAt (yS);	w .addProdAt(H2.mul(h0214), dy);	// 891 = 214 + 27 + 650
							w .addProdAt(H2.mul(h0027), v1);	//W gleichzeitig fuer naechsten Schritt!
							w .addProdAt(H2.mul(h0650), v2); f.Funktion ((IIntRing) x.addAt(h), w, v4);	//V4=F (x+h,W+h*214/891*V4+h/33*V1+h*650/891*V2)
							y .addProdAt(h .div(h_078), v4);	//y=y+V1+V2+V3  W ist neuer Startvektor,y wird uebergeben und verglichen
			double d = ((IMeasurAble) ((IMetricIRing)((IScalarMetric)w).AbsV_Dist (y)).divAccuracyAt()).getDouble();		//{auf Genauigkeit normieren}
			if (d > 0.0006)		//Vorschlag fuer naechste Schrittweite
			{StepFactor.Value = Math.exp (-Math.log (d)/3-0.1); h.mulAt(StepFactor); }	//wegen FixPunkt 1
			else h.quadAt();	//at Maximum make h four times larger.
//  	} while (d > 1.0);
		IIntRing yTmp;
		yTmp =  y;  y =  w;  w = yTmp;	//W and V4 contain the Function and it's correct Derivative
		yTmp = dy; dy = v4; v4 = yTmp;	//store them in Y and dy for reuse on next Iteration!
		return y; } //instead return a slightly wrong y

	/**Performs one Runge Kutta Fehlberg Step with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * V1 retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)	 */
	public double stepVector(double h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation (see bottom)
//  	do {	//This would require buffering the original Values!
		if (derive) { derive = false;                         fd.Funktion(xd, yv, dY); }	//{dy = F (x,y)}	//Derivative doesn't have to be calculated next time!
			double H1 = h *0.25;	//dy=F (x,yS)
			double H2 = h / H2106;
			VectorDouble.ADD_PROD  (YS , yv , H2 * H0533, dY );	//neues y:
			VectorDouble.ADD_PROD  (v2d, YS, H1        , dY ); fd.Funktion(xd + H1, v2d, v1d);	//V1=F (x+h/4,yS+h/4*dy)
			H1 = h / H0800;
			VectorDouble.ADD_PROD  (v3d, YS, H1 * H_189, dY );	//neues y: 729 - 189 = 540 !
			VectorDouble.ADD_PROD_AT(v3d,     H1 * H0729, v1d); fd.Funktion(xd + H1 * H0540, v3d, v2d);	//V2=F (x+h*27/40,yS-h*189/800*dy+h*729/800*V1)
			VectorDouble.ADD_PROD_AT( yv ,     H2 * H1600, v2d);	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
			H2 = h / H0891;
			VectorDouble.ADD_PROD  ( wd, YS, H2 * H0214, dY );	// 891 = 214 + 27 + 650
			VectorDouble.ADD_PROD_AT( wd,     H2 * H0027, v1d);	//W gleichzeitig fuer naechsten Schritt!
			VectorDouble.ADD_PROD_AT( wd,     H2 * H0650, v2d); fd.Funktion(xd += h, wd, v4d);	//V4=F (x+h,W+h*214/891*V4+h/33*V1+h*650/891*V2)
			VectorDouble.ADD_PROD_AT( yv ,     h  / H_078, v4d);	//y=y+V1+V2+V3  W ist neuer Startvektor,y wird uebergeben und verglichen
			double d = VectorDouble.DIST_ABS(wd, yv) / accuracy;		//{auf Genauigkeit normieren}
			if (d > 0.0006) {	//Vorschlag fuer naechste Schrittweite
					h *= (StepFactor.Value = Math.exp (-Math.log (d)/3-0.1)); 	//wegen FixPunkt 1
			} else {h *= 4; }	//at Maximum make h four times larger.
//  	} while (d > 1.0);
		double[] yTmp;
		yTmp =  yv;  yv =  wd;  wd = yTmp;	//W and V4 contain the Function and it's correct Derivative
		yTmp = dY; dY = v4d; v4d = yTmp;	//store them in Y and dy for reuse on next Iteration!
		return h; }

	/** Cached Value for the Derivative at y     */
	private double dy_;

	/**Performs one Runge Kutta Fehlberg Step with given Width h.
	 * The starting Point (x, y) is modified to the new Point.
	 * V1 retains the Derivative in this Point,
	 * so it's calculation can be saved when resetting to the old Point (x, y)	 */
	public double stepScalar(double h)	//{R_K_S nur wegen der Schrittweiten-Kontrolle !}
	{	//V1 and V4 keep their Values for another Calculation (see bottom)
	//	do {	//This would require buffering the original Values!
		double V1_, V4_;
		if (derive) { derive = false;          dy_ = fd.Funktion(xd, yd); }	//Derivative doesn't have to be calculated next time!
			double H1 = h *0.25;	//dy=F (x,yS)
			double H2 = h / H2106;
			double YS = yd + H2 * H0533 * dy_;	//neues y:
			double V2_= YS + H1         * dy_; V1_ = fd.Funktion(xd + H1, V2_);	//V1=F (x+h/4,yS+h/4*dy)
			H1 = h / H0800;
			double V3_= YS + H1 * H_189 * dy_;	//neues y: 729 - 189 = 540 !
			       V3_ +=    H1 * H0729 * V1_; V2_ = fd.Funktion(xd + H1 * H0540, V3_);	//V2=F (x+h*27/40,yS-h*189/800*dy+h*729/800*V1)
			yd += H2 * H1600 * V2_;	//bei Veraenderung in F : Kopiere (yS.Vektor,V3.Vektor,Gr);F (x,V3,dy);
			H2 = h / H0891;
			double W_ = YS + H2 * H0214 * dy_;	// 891 = 214 + 27 + 650
			W_ += H2 * H0027 * V1_;	//W gleichzeitig fuer naechsten Schritt!
			W_ += H2 * H0650 * V2_;            V4_ = fd.Funktion(xd += h, W_);	//V4=F (x+h,W+h*214/891*V4+h/33*V1+h*650/891*V2)
			yd += h  / H_078 * V4_;	//y=y+V1+V2+V3  W ist neuer Startvektor,y wird uebergeben und verglichen
			double d = Math.abs(W_ - yd) / accuracy;		//{auf Genauigkeit normieren}
			if (d > 0.0006) {	//Vorschlag fuer naechste Schrittweite
					h *= (StepFactor.Value = Math.exp (-Math.log (d)/3-0.1)); 	//wegen FixPunkt 1
			} else {h *= 4; }	//at Maximum make h four times larger.
	//	} while (d > 1.0);
		 yd =  W_; 	//W and V4 contain the Function and it's correct Derivative
		dy_ = V4_; 	//store them in Y and dy for reuse on next Iteration!
		return h; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		//Testing single Step of Runge-Kutta Method with Quality Control
		System.out.println("Testing StepRKQ (Runge-Kutta):");
		//super.testIt();	//not possible
		final double accuracy = 0.00001f;
		AStepper.testFloat(new StepRKF(0, 0, 0, accuracy, null), accuracy);
		//for explicitness qualified by AStepper
		AStepper.testVector(new StepRKF(0, 0, new double[0], accuracy, null), accuracy);
		//for explicitness qualified by AStepper
		if (null != streamIO.copy.ACopyAble.testInstance) {
			final IIntRing xl = (IIntRing) ACopyAble.testInstance.copy();
			AStepper.testIntRing(new StepRKF(xl, xl, xl, null));
			//for explicitness qualified by AStepper
		}
	}

	/**
	 *The main entry point for the application.
	 * @param args Array of parameters passed to the application via the command line.
	 */
	public static void main(final String[] args) { //throws java.io.IOException {
		testIt();
	}
	
}
