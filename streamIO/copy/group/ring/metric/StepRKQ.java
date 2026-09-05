package streamIO.copy.group.ring.metric;

import math.vector.VectorDouble;
import streamIO.copy.ACopyAble;
import streamIO.copy.group.ring.AStepper;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import streamIO.copy.group.ring.StepRK;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.vector.IBinaryOpFloat;

/**Integrates the given ODE in (x,y) and controls the Step Width
 * by using the Runge-Kutta Formula with Step Size h and h/2,
 * evaluating and eliminating the Difference.
 *
 * Step() performs a single Step and controls it's width.
 * Run () performs as many Steps as necessary to reach a certain x Value.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:08:24Z
 * digest: 3266cd78c00c46d781fc0ccea0299bcb9b9ee80eac1d17e59a2630b31fe97b64
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class StepRKQ
extends StepRK {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Correction between the 1 Step and the 2 Step Integration is 1/15 th	 */
	protected static ByRefDouble correction = new ByRefDouble(-1.0/15);

	/**Security gap for reducing the Step Size	 */
	protected static ByRefDouble O_9 = new ByRefDouble(0.9);  //{Security Reduction of Step Size}

	/**Exponent for  growing the Step Size = 1/5	 */
	protected static double pGrow  = -0.20;  //{Exponenten zur Schrittweitenkontrolle,R_K_F,R_K_D;s. Text}

	/**Exponent for reducing the Step Size = 1/4	 */
	protected static double pShrnk = -0.25;  //{Schrittweitenreduzierung bei Misserfolg}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Scaling Vector to adjust for different Sizes in some Dimensions  */
	public IIntRing yScale;

	/**Factor for reducing the Step Size	 */
	protected ByRefDouble StepFactor = new ByRefDouble();

	/**Old x Value, if the Step Size was too large	 */
	protected IIntRing xOld;

	/**Old y Value, if the Step Size was too large	 */
	protected IIntRing yOld;

	/**New y Value, from the first whole Step	 */
	protected IIntRing yNew;

	/**Old y Value, if the Step Size was too large	 */
	protected double[] yvOld;

	/**New y Value, from the first whole Step	 */
	protected double[] yvNew;

	/** Desired absolute Accuracy in Stepping     */
	public double accuracy;

	/** Scaling Vector to adjust for different Sizes in some Dimensions  */
	public double[] yScale_;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructor with all necessary Parameters  */
	public StepRKQ(final IIntRing stepSize_, final IIntRing x, final IIntRing y, final IODE f) {
		super(stepSize_, x, y, f);
		xOld = (IIntRing) x.newInstance();
	}
	
	/**Constructor for a single-Dimensional System, additionally setting the Accuracy.
	 * @param stepSize_
	 * @param x
	 * @param y
	 * @param f
	 */
	public StepRKQ(double stepSize_, double x, double y, final double accuracy_, IBinaryOpFloat f) {
		super(stepSize_, x, y, f);
		this.accuracy = accuracy_;
	}

	/** Constructor with all necessary Parameters  */
	public StepRKQ(final double stepSize_, final double x, final double[] y, final double accuracy_, final IBinaryOpFloat f) {
		super(stepSize_, x, y, f);
		this.accuracy = accuracy_;
	}
	
	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(final IIntRing y) {
		super.Init(y);
		yOld = (IIntRing) y.newInstance();	//new Tensor(); yOld.Carry = x; yOld.letGrad(y.getDim(), true, false);
		yNew = (IIntRing) y.newInstance();	//new Tensor(); yNew.Carry = x; yNew.letGrad(y.getDim(), true, false);
	}
	
	/**Initializes the Stepper to new Coordinates and a new Differential Equation
	 * The single Constructor of the Stepper should have the same signature!	 */
	public void Init(final double[] y) {
		super.Init(y);
		yvOld = new double[y.length];
		yvNew = new double[y.length];
	}
	
	/**Performs several Steps with a variable Step Size up to the Target	 */
	public IIntRing run (final IIntRing target) {
		return AStepperQ.runVariableX(this, target); }
	
	/**
	 * Performs several Steps with the given Step Size, which is subject to change (e.g. by Quality Control).
	 * @return the last taken StepSize.
	 */
	public double run (final double target) { 
		return AStepperQ.runVariableX(this, target); }
	
	/**
	 * Runge-Kutta-Schritt 5.Ordnung durch Schrittweitenkontrolle
	 * mit Beobachtung des lokalen Abbruchfehlers
	 * zur Loesung der DGL in (x,Y),die durch f bestimmt wird.
	 * hTry wird versucht. hNext ist eine Vorschlag fuer den naechsten Schritt.
	 * hDid ist die benutzte Schrittweite und (x,Y) werden durch die neuen Werte ersetzt.
	 *
	 * Laeuft viel effektiver mit einer veraenderten Runge-Kutta-Routine,
	 * die die Ableitung am ersten Ort nicht immer berechnet !	 */
	public IIntRing step(final IIntRing hTry) {
		IMetricIRing yTmp;
		yOld.copyAt(y);
		xOld.copyAt(x);
		boolean tooLarge;
		do {
			IIntRing h2 = hTry.half();	//half Steps Size
			replace = false; super.step (hTry); //{ein grosser Schritt MIT Berechnung der Ableitung}
			replace = true ;			 yTmp = (IMetricIRing) v3; v3 = yNew; yNew = yTmp; //cache the new y Value
			derive  = false; super.step (h2);	//1st derivative still in V1, don't recalculate again
			derive  = true ; super.step (h2);	//{zwei halbe Schritte}
								yTmp = (IMetricIRing) yNew.subAt(y);		//fine tune hTry!
			if (yScale != null) yTmp = (IMetricIRing) ((IMetricIRing) yTmp.copy()).mMulAt(yScale);	//Scale it for different Dimensions in one DGL
			double d = ((IMeasurAble) ((IMetricIRing)yTmp.AbsV()).divAccuracyAt()).getDouble();		//{auf Genauigkeit normieren}
			if (tooLarge = (d >  3.5)) {	//9 equivalents to halving the StepSize, 3.5 to 1.5 it
				//adjustments in Stepsize of less than 1,5 don't pay off, when dealing with simple DGLs
				y.copyAt(yOld);
				x.copyAt(xOld);	//restore the old Values
				if (d > 4500.0)	hTry.thirdAt().thirdAt();	//Max. 9 times smaller
				else {StepFactor.Value = Math.exp (pShrnk*Math.log (d)-0.1);
								hTry.mulAt(StepFactor);}	//{sonst Korrektur minimal !}
			} else if	(d <  0.1)	//0.02 equivalents to doubling the StepSize, 0.1 to 1.5
				 if (d <  0.0006)hTry.quadAt();		//Max. 4 times larger, due to cancellation
				 else { StepFactor.Value = Math.exp (pGrow*Math.log (d)-0.1);
								hTry.mulAt(StepFactor);}
			else;				//hTry.mulAt(O_9);	//no change, avoid unnecessary calculations
		} while (tooLarge);
		y.addProdAt (correction, yNew);        //{Abschneidefehler 5.Ordnung ausraeumen}
		return y; }	//The result is typically much more accurate, because the 5th Order Term is also eliminated.
	
	/**
	 * Runge-Kutta-Schritt 5.Ordnung durch Schrittweitenkontrolle
	 * mit Beobachtung des lokalen Abbruchfehlers
	 * zur Loesung der DGL in (x,Y),die durch f bestimmt wird.
	 * hTry wird versucht
	 * (x,Y) werden durch die neuen Werte ersetzt.
	 * @return die benutzte Schrittweite ist auch eine Vorschlag fuer den naechsten Schritt.
	 *
	 * Laeuft viel effektiver mit einer veraenderten Runge-Kutta-Routine,
	 * die die Ableitung am ersten Ort nicht immer berechnet !
	 */
	public double stepVector(double hTry) {
		double[] yTmp;
		VectorDouble.COPY(yv, yvOld);
		double xOld_ = xd;
		boolean tooLarge;
		do {
			double h2 = hTry * 0.5;	//half Steps Size
			replace = false; super.stepVector(hTry); //{ein grosser Schritt MIT Berechnung der Ableitung}
			replace = true ; yTmp = v3d; v3d = yvNew; yvNew = yTmp; //cache the new y Value
			derive  = false; super.stepVector(h2);	//1st derivative still in V1, don't recalculate again
			derive  = true ; super.stepVector(h2);	//zwei halbe Schritte
				yTmp = VectorDouble.SUB_AT(yvNew, yv);	//fine tune hTry!
			if (yScale != null) {	//Scale it for different Dimensions in one DGL
				yTmp = VectorDouble.MUL(yTmp, yScale_); } //Mul, because YNew is used for Correction below!
			double d = VectorDouble.NORM_ABS(yTmp) / accuracy;		//{auf Genauigkeit normieren}
			if (tooLarge = (d >  3.5)) {	//9 equivalents to halving the StepSize, 3.5 to 1.5 it
				//adjustments in Stepsize of less than 1,5 don't pay off, when dealing with simple DGLs
				VectorDouble.COPY(yvOld, yv);
				xd = xOld_;	//restore the old Values
				if (d > 4500.0) { hTry /= 9;	//Max. 9 times smaller
				} else { 	//{sonst Korrektur minimal !}
					hTry *= (StepFactor.Value = Math.exp(pShrnk*Math.log (d)-0.1)); }
			} else if (d <  0.1) {	//0.02 equivalents to doubling the StepSize, 0.1 to 1.5
				if (d <  0.0006) { hTry *= 4;		//Max. 4 times larger, due to cancellation
				} else {
					hTry *= (StepFactor.Value = Math.exp (pGrow*Math.log (d)-0.1)); }
			} else ;				//hTry.mulAt(O_9);	//no change, avoid unnecessary calculations
		} while (tooLarge);
		VectorDouble.ADD_PROD_AT(yv, yvNew, correction.Value);  //{Abschneidefehler 5.Ordnung ausraeumen}
		return hTry; }	//The result is typically much more accurate, because the 5th Order Term is also eliminated.
	
	/**
	 * Runge-Kutta-Schritt 5.Ordnung durch Schrittweitenkontrolle
	 * mit Beobachtung des lokalen Abbruchfehlers
	 * zur Loesung der DGL in (x,Y),die durch f bestimmt wird.
	 * hTry wird versucht
	 * (x,Y) werden durch die neuen Werte ersetzt.
	 * @return die benutzte Schrittweite ist auch eine Vorschlag fuer den naechsten Schritt.
	 *
	 * Laeuft viel effektiver mit einer veraenderten Runge-Kutta-Routine,
	 * die die Ableitung am ersten Ort nicht immer berechnet !
	 */
	public double stepScalar(double hTry) {
		double yTmp_;
		double yOld_ = yd;
		double xOld_ = xd;
		boolean tooLarge;
		do {
			final double h2 = hTry * 0.5;	//half Steps Size
			replace = false; super.stepScalar(hTry); //{ein grosser Schritt MIT Berechnung der Ableitung}
			replace = true ; yTmp_ = yLast; //cache the new y Value
			derive  = false; super.stepScalar(h2);	//1st derivative still in V1, don't recalculate again
			derive  = true ; super.stepScalar(h2);	//zwei halbe Schritte
				yTmp_ -= yd;	//fine tune hTry!
	//		if (yScale != null) {	//Scale it for different Dimensions in one DGL
	//			yTmp *= yScale_; }
			final double d = Math.abs(yTmp_) / accuracy;		//{auf Genauigkeit normieren}
			if (tooLarge = (d >  3.5)) {	//9 equivalents to halving the StepSize, 3.5 to 1.5 it
				//adjustments in Stepsize of less than 1,5 don't pay off, when dealing with simple DGLs
				yd = yOld_;
				xd = xOld_;	//restore the old Values
				if (d > 4500.0) { 
					hTry /= 9;	//Max. 9 times smaller
				} else { 	//sonst Korrektur minimal !
					hTry *= (StepFactor.Value = Math.exp(pShrnk*Math.log (d)-0.1)); }
			} else if (d <  0.1) {	//0.02 equivalents to doubling the StepSize, 0.1 to 1.5
				if (d <  0.0006) { 
					hTry *= 4;		//Max. 4 times larger, due to cancellation
				} else {
					hTry *= (StepFactor.Value = Math.exp (pGrow*Math.log (d)-0.1)); }
			} //else ; //hTry.mulAt(O_9);	//no change, avoid unnecessary calculations
		} while (tooLarge);
		yd += yTmp_ * correction.Value;  //{Abschneidefehler 5.Ordnung ausraeumen}
		return hTry; }	//The result is typically much more accurate, because the 5th Order Term is also eliminated.

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Method to test all Implementations in this class.	 */
	public static void testIt() {
		//Testing single Step of Runge-Kutta Method with Quality Control
		System.out.println("Testing StepRKQ (Runge-Kutta):");
		//super.testIt();	//not possible
		final double accuracy = 0.00001f; 
		AStepper.testFloat  (new StepRKQ(0, 0, 0, accuracy, null), accuracy); //for explicitness qualified by AStepper
		AStepper.testVector (new StepRKQ(0, 0, new double[0], accuracy, null), accuracy); //for explicitness qualified by AStepper
		if (null != streamIO.copy.ACopyAble.testInstance) {
			final IIntRing xl = (IIntRing) ACopyAble.testInstance.copy();
			AStepper.testIntRing(new StepRKQ(xl, xl, xl, null)); //for explicitness qualified by AStepper
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
