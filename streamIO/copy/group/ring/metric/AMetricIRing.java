package streamIO.copy.group.ring.metric;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.AIntRing;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.order.IOrder;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;

/**
 * Default Implementation of the Algebraic Integrity Ring with strict Order
 * (M,+,-,0,*,/,1,>): Set of Objects with inner Operations +,-,*,/ and binary Relation >
 * with: 1) (M,+,-,0,*,/,1) form an Algebraic Integrity Ring 2) > is a strict Order
 * relation No new operations are defined, but both Interfaces are integrated into one.
 * Design Decisions: The following Methods are left open, since they are pure virtual
 * Methods: addAt, subAt, mulAt, divAt, less, maxValueAt
 */
public abstract class AMetricIRing extends AIntRing implements IMetricIRing {

	/**
	 * Switches Simplifying of the Results and Arguments on or off. It is typically
	 * switched on, since you can not expect the result to be a real, integer or of less
	 * Degree than the argument. With the following Objects it controls the following:
	 * Fraction : shortening (except where it is necessary) Complex : Checking for real
	 * Result or argument Polar : Checking for real Result or argument Polynom : Checking
	 * for Zero in the upper Coefficients
	 */
	protected static boolean bolLazySimplify = true;

	/** Super Class to pass the methods to for Delegation */
	private IScalarMetric sMetric;

	/** Super Class to pass the methods to for Delegation */
	private IWellOrder sWellOrder;

	/**
	 * This Constructor is only used in Initialize and Terminate of abstract Classes and
	 * should normally be marked as 'protected' or 'friend', but all these Routines are
	 * not within one Package. It is needed for the Child Classes to call and replace Self
	 * by the Child Object with it's overloaded Methods.
	 */
	protected AMetricIRing() {
		sWellOrder = new AWellOrder(this); //
		sMetric = new AScalarMetric(this); //
		// BaseAccuracy = (AMetricIRing) zero(); //leads to an unterminated Recursion!
	}

	// ////////////////
	// Delegation: //
	// ////////////////

	// ////////////////
	// WellOrder //
	// ////////////////

	/** Sets and returns the minimum Value for this Class in Place. */
	public IWellOrder minValueAt() {
		return sWellOrder.minValueAt();
	}

	/** Returns the minimum Value for this Class. */
	public IWellOrder minValue() {
		return sWellOrder.minValue();
	}

	/** Returns the maximum Value for this Class. */
	public IWellOrder maxValue() {
		return sWellOrder.maxValue();
	}

	/** Returns the minimum absolute Value for this Class. */
	public IWellOrder minAbsValue() {
		return sWellOrder.minAbsValue();
	}

	/** Returns the minimum absolute Value (greater than Zero) for this Class in Place. */
	public IWellOrder minAbsValueAt() {
		return sWellOrder.minAbsValueAt();
	}

	/** Returns the Representation of Infinity for this Class. */
	public IWellOrder Infinity() {
		return sWellOrder.Infinity();
	}

	/** Returns the Representation of Infinity for this Class in Place. */
	public IWellOrder InfinityAt() {
		return sWellOrder.InfinityAt();
	}

	/** Returns the Representation of -Infinity for this Class. */
	public IWellOrder NegInfinity() {
		return sWellOrder.NegInfinity();
	}

	/** Returns the Representation of -Infinity for this Class in Place. */
	public IWellOrder NegInfinityAt() {
		return sWellOrder.NegInfinityAt();
	}

	/** Returns the Representation of an invalid Number for this Class. */
	public IWellOrder NaN() {
		return sWellOrder.NaN();
	}

	/** Returns the Representation of an invalid Number for this Class in Place. */
	public IWellOrder NaNAt() {
		return sWellOrder.NaNAt();
	}

	/** Returns the Representation of Infinity for this Class. */
	public boolean isInfinite() {
		return sWellOrder.isInfinite();
	}

	/** Returns the Representation of an invalid Number for this Class. */
	public boolean isNaN() {
		return sWellOrder.isNaN();
	}

	// ////////////////////////////////
	// Replication IWellOrder: //
	// ////////////////////////////////

	/** Returns the maximum Value (less than Infinity) for this Class in Place. */
	// public WellOrder maxValueAt()

	// ////////////////////////////
	// Order, ScalarMetric
	// ////////////////////////////
	/** Returns 'true' when this is a positive Number. */
	public boolean positive() {
		return sMetric.positive();
	}

	/** Returns 'true' when this is a negative Number. */
	public boolean negative() {
		return sMetric.negative();
	}

	/** Returns true, if the arg has the opposite Zchn to this Number */
	public boolean changeZchn(Object arg) {
		return sMetric.changeZchn(arg);
	}

	/** Returns true, if the arg has the opposite Zchn to this Number */
	public boolean changeSign(Object arg) {
		return sMetric.changeSign(arg);
	}

	/** Returns the sign of this Number in Place */
	public IMetricIRing SignAt() {
		return sMetric.SignAt();
	}

	/** Returns the sign of this Number */
	public int Sign() {
		return sMetric.Sign();
	}

	/** Returns the Sign of this Number, but also 1 for 0 */
	public int Zchn() {
		return sMetric.Zchn();
	}

	/** Returns the Sign of this Number in Place, but also 1 for 0 */
	public IMetricIRing ZchnAt() {
		return sMetric.ZchnAt();
	}

	/**
	 * Returns the Position of this Number relative to arg: -1 for smaller, otherwise +1
	 */
	public int Position(Object arg) {
		return sMetric.Position(arg);
	}

	// public MetricIRing Position(Object arg) { return sMetric.Position(arg); }

	/**
	 * Returns the Position of this Number relative to arg in Place: -1 for smaller,
	 * otherwise +1
	 */
	public IMetricIRing PositionAt(Object arg) {
		return sMetric.PositionAt(arg);
	}

	/**
	 * Returns the exact Position of this Number relative to arg: -1 for smaller, 0 for
	 * equal, otherwise +1
	 */
	public int compareTo(Object arg) {
		return sMetric.compareTo(arg);
	}

	// public MetricIRing compareTo(Object arg) { return sMetric.compareTo(arg); }

	/**
	 * Returns the exact Position of this Number relative to arg in Place: -1 for smaller,
	 * 0 for equal, otherwise +1
	 */
	public IMetricIRing compareToAt(Object arg) {
		return sMetric.compareToAt(arg);
	}

	/** Returns this Number multiplied by the Sign of arg */
	public IMetricIRing mulSign(Object arg) {
		return sMetric.mulSign(arg);
	}

	/** Returns this Number multiplied in Place by the Sign of arg */
	public IMetricIRing mulSignAt(Object arg) {
		return sMetric.mulSignAt(arg);
	}

	/** Returns this Number multiplied by the Zchn of this Number */
	public IMetricIRing mulZchn(Object arg) {
		return sMetric.mulZchn(arg);
	}

	/** Returns this Number multiplied in Place by the Zchn of this Number */
	public IMetricIRing mulZchnAt(Object arg) {
		return sMetric.mulZchnAt(arg);
	}

	/** Returns this Number set to the Sign of arg */
	public IMetricIRing setSign(Object arg) {
		return sMetric.setSign(arg);
	}

	/** Returns this Number set in Place to the Sign of arg */
	public IMetricIRing setSignAt(Object arg) {
		return sMetric.setSignAt(arg);
	}

	/** Returns this Number multiplied by the Zchn of arg */
	public IMetricIRing setZchn(Object arg) {
		return sMetric.setZchn(arg);
	}

	/** Returns this Number set in Place to the Zchn of arg */
	public IMetricIRing setZchnAt(Object arg) {
		return sMetric.setZchnAt(arg);
	}

	/** absolute Value: |x| */
	public IScalarMetric AbsV() {
		return sMetric.AbsV();
	}

	/**
	 * absolute Value in Place: |x| Should be redefined for Vectors, Complex etc., because
	 * this Definition is not valid for that!
	 */
	public IScalarMetric AbsVAt() {
		return sMetric.AbsVAt();
	}

	/**
	 * Absolute Value-Norm: Special Case of the p-Norm for p = 1 This norm is the fastest
	 * to chalculate
	 */
	public IMetricIRing AbsV_Norm() {
		return (IMetricIRing) AbsV();
	}

	/**
	 * Euklidische Norm Special Case of the p-Norm for p = 2 Rotation Invariant for
	 * cartesian Systems.
	 */
	public IMetricIRing Norm() {
		return SqrNorm().SqRt();
	}

	// all other Norms are defined by the Norms and Metrices

	/** Square of the absolute Value: |x|^2 */
	// public SemiGroupM SqrAbsV() { return sMetric.SqrAbsV (); }
	/**
	 * Square of the absolute Value in Place:|x|^2 Should be redefined for Vectors,
	 * Complex etc., because this Definition is not valid for that!
	 */
	// public SemiGroupM SqrAbsVAt() { return sMetric.SqrAbsVAt(); }
	/**
	 * absolute Distance: |x| Should be redefined for Vectors, Complex etc., because this
	 * Definition is not valid for that!
	 */
	public IScalarMetric AbsDist(Object arg) {
		return sMetric.AbsDist(arg);
	}

	/**
	 * absolute Distance in Place: |x| Should be redefined for Vectors, Complex etc.,
	 * because this Definition is not valid for that!
	 */
	public IScalarMetric AbsDistAt(Object arg) {
		return sMetric.AbsDistAt(arg);
	}

	/** Square of the absolute Distance: |x|^2 */
	// public SemiGroupM AbsSqrDist (Object arg) { return sMetric.AbsSqrDist (arg); }
	/** Square of the absolute Distance in Place:|x|^2 */
	// public SemiGroupM AbsSqrDistAt (Object arg) { return sMetric.AbsSqrDistAt(arg); }

	/**
	 * p-Metric: Defined as Sum(|x|^p)^1/p Generic Norm: the other Norms are Special
	 * Cases: In 1-dimensional Spaces all Norms fall together.
	 */
	public IMetricIRing p_Dist(Object arg, double p) {
		return sMetric.p_Dist(arg, p);
	}

	/**
	 * Absolute Value-Metric: Special Case of the p-Metric for p = 1
	 */
	public IMetricIRing AbsV_Dist(Object arg) {
		return sMetric.AbsV_Dist(arg);
	}

	/**
	 * Maximums-Metric Special Case of the p-Metric for p -> Infinity
	 */
	public IMetricIRing Max_Dist(Object arg) {
		return sMetric.Max_Dist(arg);
	}

	/**
	 * Euklidean Metric Special Case of the p-Metric for p = 2 Rotation Invariant for
	 * cartesian Systems.
	 */
	public IMetricIRing Dist(Object arg) {
		return sMetric.Dist(arg);
	}

	/**
	 * (Euklidische Metric)^2 Special Case of the p-Metric for p = 2 Rotation Invariant
	 * for cartesian Systems.
	 */
	public IMetricIRing SqrDist(Object arg) {
		return sMetric.SqrDist(arg);
	}

	/** between: returns True, when 'Self' is between arg1 and arg2 */
	public boolean isBetween(Object arg1, Object arg2) {
		return sMetric.isBetween(arg1, arg2);
	}

	/** greater: '>' Returns True, when 'Self' > arg */
	public boolean isMoreThan(Object arg) {
		return sMetric.isMoreThan(arg);
	}

	/** greater or equal: '>=' Returns True, when 'Self' >= arg */
	public boolean notLessThan(Object arg) {
		return sMetric.notLessThan(arg);
	}

	/** less or equal: '<=' Returns True, when 'Self' <= arg */
	public boolean notMoreThan(Object arg) {
		return sMetric.notMoreThan(arg);
	}

	/** Returns the Maximum of both Operands */
	public IOrder Max(Object arg) {
		return sMetric.Max(arg);
	}

	/** Returns the Minimum of both Operands */
	public IOrder Min(Object arg) {
		return sMetric.Min(arg);
	}

	/** Returns the Maximum of both Operands in Place */
	public IOrder MaxAt(Object arg) {
		return sMetric.MaxAt(arg);
	}

	/** Returns the Minimum of both Operands in Place */
	public IOrder MinAt(Object arg) {
		return sMetric.MinAt(arg);
	}

	/**
	 * This is the Base Accuracy that is used for convergence checks. It should be
	 * initialized on first Instantiation of a Class Member.
	 */
	public static int BaseAccuracyBits; // for Double => 10 Digits

	/**
	 * This is the Base Accuracy that is used for convergence checks. It should be
	 * initialized on first Instantiation of a Class Member.
	 */
	public static int MaxAccuracyBits; // for Double => 16 Digits

	/** Maximum Number of Iterations in analytical Calculations */
	public static int MaxIteration;

	/**
	 * This is the Base Accuracy that is used for convergence checks. It should be
	 * initialized on first Instantiation of a Class Member.
	 */
	public static Object BaseAccuracy;

	/**
	 * This is the Inverse of the Base Accuracy that is used for convergence checks. It
	 * should be initialized on first Instantiation of a Class Member.
	 */
	public static Object BaseAccuracyInv;

	/**
	 * This is the Maximum Accuracy that is used for convergence checks. It should be
	 * initialized on first Instantiation of a Class Member.
	 */
	public static Object MaxAccuracy;

	/**
	 * This is the Inverse of the Base Accuracy that is used for convergence checks. It
	 * should be initialized on first Instantiation of a Class Member.
	 */
	public static Object MaxAccuracyInv;

	/**
	 * Returns the Accuracy to compare it directly with Coefficienst for Convergence Check
	 * of Functions with O(1)
	 */
	public Object Accuracy() {
		return ACCURACY();
	} // could be static

	/**
	 * Returns the Inverse of the Accuracy to compare it directly with Coefficienst for
	 * Convergence Check of Functions with O(1)
	 */
	public Object AccuracyInv() {
		return ACCURACY_INV();
	} // could be static

	/** Sets the Number of significant Bits for Accuracy */
	public void setMaxAccuracyBits(int AccBits) { // could be static
		SET_MAX_ACCURACY_BITS(AccBits);
	}

	/**
	 * Returns the Accuracy to compare it directly with Coefficienst for Convergence Check
	 * of Functions with O(1)
	 */
	public static Object ACCURACY() {
		return BaseAccuracy;
	} //

	/**
	 * Returns the Inverse of the Accuracy to compare it directly with Coefficienst for
	 * Convergence Check of Functions with O(1)
	 */
	public static Object ACCURACY_INV() {
		return BaseAccuracyInv;
	} //

	/** Sets the Number of significant Bits for Accuracy */
	public static void SET_MAX_ACCURACY_BITS(int AccBits) { //
		double Acc = Math.exp(AccBits * IMeasurAble.LN2);
		MaxAccuracyBits = AccBits;
		MaxIteration = MaxAccuracyBits << 1;
		MaxAccuracy = new ByRefDouble(1.0 / Acc);
		MaxAccuracyInv = new ByRefDouble(Acc);
	}

	/** Sets the Number of significant Bits for Accuracy */
	public void setAccuracyBits(int AccBits) { // could be static
		SET_ACCURACY_BITS(AccBits);
	}

	/** Sets the Number of significant Bits for Accuracy */
	public static void SET_ACCURACY_BITS(int AccBits) { // could be static
		double Acc = Math.exp(AccBits * IMeasurAble.LN2);
		BaseAccuracyBits = AccBits;
		BaseAccuracy = new ByRefDouble(1.0 / Acc);
		BaseAccuracyInv = new ByRefDouble(Acc);
	}

	/** Returns the Number of significant Bits for Accuracy */
	public int getAccuracyBits() {
		return GET_ACCURACY_BITS();
	} // could be static

	/** Returns the Number of significant Bits for Accuracy */
	public static int GET_ACCURACY_BITS() {
		return BaseAccuracyBits;
	} // could be static

	/** Returns the Number of significant Bits for Accuracy */
	public int getMaxAccuracyBits() {
		return GET_MAX_ACCURACY_BITS();
	} // could be static

	/** Returns the Number of significant Bits for Accuracy */
	public static int GET_MAX_ACCURACY_BITS() {
		return MaxAccuracyBits;
	} // could be static

	// TODO: initialize the BaseAccuracy, BaseAccuracyInv, MaxAccuracy, MaxAccuracyInv

	/** Static Initializer for the Default Accuracies */
	static {
		SET_ACCURACY_BITS(IMeasurAble.FLOAT_MANTISSA_BITS);
		SET_MAX_ACCURACY_BITS(IMeasurAble.DOUBLE_MANTISSA_BITS);
	}

	/**
	 * Returns the Square Root of the MaxValue in Place. Since MaxValue is a Constant,
	 * this should be redefined in each concrete Child Class.
	 */
	public IMetricIRing SqRtMaxValueAt() {
		maxValueAt();
		SqRtAt();
		return this;
	}

	/**
	 * Returns the Square Root of the MaxValue in Place. Since MaxValue is a Constant,
	 * this should be redefined in each concrete Child Class.
	 */
	public IMetricIRing SqRtMaxValue() {
		return ((IMetricIRing) maxValue()).SqRtAt();
	}

	/**
	 * Multiplies the absolute Value of this Number by the Accuracy in Place in the most
	 * effective Way
	 */
	public IMetricIRing mulAbsAccuracyAt() {
		mulAt(BaseAccuracy);
		AbsVAt();
		return this;
	}

	/**
	 * Multiplies the absolute Value of this Number by the Accuracy in the most effective
	 * Way
	 */
	public IMetricIRing mulAbsAccuracy() {
		return ((IMetricIRing) copy()).mulAbsAccuracyAt();
	}

	/** Multiplies this Number by the Accuracy in Place in the most effective Way */
	public IMetricIRing mulAccuracyAt() {
		mulAt(BaseAccuracy);
		return this;
	}

	/** Multiplies this Number by the Accuracy in the most effective Way */
	public IMetricIRing mulAccuracy() {
		return ((IMetricIRing) copy()).mulAccuracyAt();
	}

	/**
	 * Divides the absolute Value of this Number by the Accuracy in Place in the most
	 * effective Way
	 */
	public IMetricIRing divAbsAccuracyAt() {
		AbsVAt();
		divAt(BaseAccuracy);
		return this;
	}

	/**
	 * Divides the absolute Value of this Number by the Accuracy in the most effective Way
	 */
	public IMetricIRing divAbsAccuracy() {
		return ((IMetricIRing) copy()).divAbsAccuracyAt();
	}

	/** Divides this Number by the Accuracy in Place in the most effective Way */
	public IMetricIRing divAccuracyAt() {
		divAt(BaseAccuracy);
		return this;
	}

	/** Divides this Number by the Accuracy in the most effective Way */
	public IMetricIRing divAccuracy() {
		return ((IMetricIRing) copy()).divAccuracyAt();
	}

	/**
	 * Returns true when 'this' is similar to arg, i.e. this is about the same as arg. The
	 * usual Criterion is that |this-arg| <= Accuracy*(|this| + |arg|)
	 */
	public boolean isSimilar(IMetricIRing arg) {
		IMetricIRing cmp = (IMetricIRing) AbsV();
		cmp.addAt(arg.AbsV());
		return AbsV_Dist(arg).isLessThan(cmp.mulAccuracyAt());
	}

	/**
	 * Returns the Square Root of this: x^.5 Iterated Taylor Development at (x=1),
	 * converges fastest for 0.5 < x <=1 This makes it the fastest Algorithm for float
	 * Point Representation: Divide the Exponent by two (multiply by 0.707... for a
	 * Remainder) and use the Mantissa only to calculate the Square Root. it does not
	 * converge for complex x, because there we use only the real Impl. Does not converge
	 * very well for 0 < x < 0.5! y = (x+d)^2 = x^2 + 2xd + d^2 # x^2 + 2xd => d #
	 * (y-x^2)/2x The Alternative is to use (1-x)^-1 = (|x|^-1)*(1 + B + B^2 + B^3 + ...)
	 * with B = (1-x/|x|) but this is only possible for multidimensional Matrices, since
	 * x/|x| is 1 otherwise!
	 */
	public IMetricIRing SqRt() { // Here the EPS Variable is of no use, because there
									// is a better optimization!
		if (isZero()) return this; // necessary, when acc == 0, because otherwise the
									// loop is nearly infinite!
		IMetricIRing old;
		IMetricIRing SqRt_ = (IMetricIRing) one(); // don't assume, that this is a real
													// Value!
		IMetricIRing acc = (IMetricIRing) AbsV();
		acc.mulAt(BaseAccuracy); // always positive, usually 0 !
		int CountDown = MaxIteration;
		do {
			old = SqRt_;
			SqRt_ = (IMetricIRing) old.add(div(old));
			SqRt_.halfAt(); // x+y/2x
			old.subAt(SqRt_);
		} while ((--CountDown > 0) && acc.isLessThan(old.AbsVAt())); // AbsVAt
																		// possible, but
																		// leads to
																		// Problems for
																		// Complex Numbers
		return SqRt_;
	}

	/**
	 * Returns the Cubic Root of this: x^=.333333333333333 Iterated Taylor Development at
	 * (x=1), converges fastest for 0.5 < x <=1 does it converge for complex x??? Not
	 * necessary, because there we use only the real impl. (x+d)^3 = x^3 + 3dx^2 + 3xd^2 +
	 * d^3 does not converge very well!
	 */
	public IMetricIRing CbcRt() {
		// boolean neg = negative(); //does not help, Iteration switches signs irregularly
		// MetricIRing old;
		IMetricIRing SQR;
		IMetricIRing Eps;
		IMetricIRing CbcRt_ = (IMetricIRing) newInstance();
		CbcRt_.oneAt(); // one() creates a Constant!
		IMetricIRing acc = (IMetricIRing) AbsV();
		acc.mulAt(BaseAccuracy); // usually 0 !
		do {
			// old = CbcRt_;
			SQR = (IMetricIRing) CbcRt_.sqr();
			Eps = (IMetricIRing) sub(SQR.mul(CbcRt_));
			Eps.divAt(SQR.trplAt());
			// System.out.println (CbcRt_ + " + " + Eps);
			CbcRt_.addAt(Eps);
		} // x+(y-x^3)/3x^2
		while (acc.isLessThan(Eps.AbsV())); // also AbsVAt possible, but that would return
											// a Complex for Complex Arguments.
		return CbcRt_;
	}

	/**
	 * Returns the Cubic Root of this in Place: x^=.333333333333333 Iterated Taylor
	 * Development at (x=1), converges fastest for 0.5 < x <=1 does it converge for
	 * complex x??? Not necessary, because there we use only the real impl. Does not
	 * converge very well for 0 < x < 0.5!
	 */
	public IMetricIRing CbcRtAt() {
		return (IMetricIRing) shallowCopyAt(CbcRt());
	}

	/**
	 * Returns the Square Root of this in Place: x^=.5 Iterated Taylor Development at
	 * (x=1), converges fastest for 0.5 < x <=1 does it converge for complex x??? Not
	 * necessary, because there we use only the real impl. Does not converge very well for
	 * 0 < x < 0.5!
	 */
	public IMetricIRing SqRtAt() {
		return (IMetricIRing) shallowCopyAt(SqRt());
	}

	/**
	 * Declaration of mMulAt(), which is intended for Manifold Multiplication Already used
	 * by StepRKQ. Delegated to mulAt()
	 */
	public IMetricIRing mMulAt(Object arg) {
		return (IMetricIRing) mulAt(arg);
	}

	/**
	 * Solves the Square Equation with three Parameters: a*x^2 + b*x + c = 0 x1 and x2 are
	 * ByRef Parameters that contain the two Solutions. The Return Value determines
	 * whether there is a real Solution or not.
	 */
	public boolean SolveSqr3(IMetricIRing a, IMetricIRing b, IMetricIRing c,
			IMetricIRing x1, IMetricIRing x2) {
		return SolveSqr2((IMetricIRing) b.div(a), (IMetricIRing) c.div(a), x1, x2);
	}

	/**
	 * Solves the (normed) Square Equation with two Parameters: a*x^2 + b*x + c = 0 If a =
	 * null, the normed Square Equation is assumed. x1 and x2 are ByRef Parameters that
	 * contain the two Solutions. The Return Value determines whether there is a real
	 * Solution or not. If not, the real Part is returned in x1 and the imaginary Part in
	 * x2. If yes, x1 contains the larger absolute Value. This also results in higher
	 * Accuracy, because no cancelling takes place.
	 */
	public boolean SolveSqr2(IMetricIRing b, IMetricIRing c, IMetricIRing x1,
			IMetricIRing x2) {
		boolean im;
		x1.copyAt(b);
		x1.halfAt().negAt();
		x2.copyAt(b);
		x2.sqrAt();
		x2.subAt(c);
		if (im = x2.negative()) x2.negAt();
		x2.SqRtAt();
		if (im) return false;
		if (x1.positive())
			x1.addAt(x2); // {Verfahren fuer hoehere Genauigkeit: }
		else x1.subAt(x2); // x2+|b| is the larger absolute Value
		x2.copyAt(c);
		x2.divAt(x1); // x1*x2 = c
		return true;
	}

	/**
	 * Evaluates the continuous Fraction (1/(1+ 1/.../a[n])) Wertet die
	 * Kettenbruch-Entwicklung (1/(1/.../n)) aus.
	 */
	public static void contFraction(IIntRing[] Kette, IIntRing Numerator,
			IIntRing Denominator) {
		IIntRing Zaehler = (IIntRing) Denominator.newInstance();
		int Z1 = Kette.length;
		Denominator.copyAt(Kette[--Z1]);
		Numerator.oneAt();
		while (--Z1 >= 0) {
			Zaehler.copyAt(Numerator);
			Numerator.copyAt(Denominator);
			Denominator.mulAt(Kette[Z1]);
			Denominator.addAt(Zaehler);
		}
	}

	/**
	 * Generates the best rational Approximation of a Number using continuous Fractions
	 * (1/(1/.../n)) with Accuracy Check. Erzeugt die beste rationale Approximation einer
	 * Zahl durch fortgesetzte Kettenbruch-Entwicklung (1/(1/.../n)) mit laufender
	 * Genauigkeits-Kontrolle.
	 */
	public void rational(IIntRing GanzZahl, IIntRing Zaehler, IIntRing Nenner) { // Breaking
																					// up
																					// integer
																					// and
																					// fractional
																					// Part...
		GanzZahl.copyAt(this);
		GanzZahl.addAt(IMeasurAble.Half);
		GanzZahl.IntAt();
		// ((IIntRing)GanzZahl.copyAt(this)). IntAt();
		IMetricIRing x = (IMetricIRing) sub(GanzZahl); // x contains the Frac()
		IMetricIRing y = (IMetricIRing) x.inv(); // y is used to subsequently determine
													// the
		// Initializing Loop and saving the first Iteration
		IIntRing Zi = (IIntRing) Zaehler.newInstance();
		Zi.zeroAt();
		Zaehler.oneAt();
		IIntRing Ni = (IIntRing) Zaehler.newInstance();
		Ni.oneAt(); // because one() returns the Constant!
		IIntRing Zii = (IIntRing) Zaehler.newInstance();
		IIntRing Nii = (IIntRing) Zaehler.newInstance();
		IIntRing Z1 = (IIntRing) Zaehler.newInstance();
		Z1.copyAt(y);
		Z1.IntAt();
		Nenner.copyAt(Z1);
		IMetricIRing Accuracy = mulAbsAccuracy();
		while (((IMetricIRing) Accuracy.mul(Nenner))
				.notMoreThan(((IMetricIRing) ((IGroup) x.mul(Nenner)).subAt(Zaehler))
						.AbsVAt())) { // using Multiplication instead of Division takes
										// 1 Multiplication more,
			// but allows Z and N to stay integer.
			y.subAt(Z1);
			y.invAt();
			Nii.copyAt(Ni);
			Ni.copyAt(Nenner);
			Zii.copyAt(Zi);
			Zi.copyAt(Zaehler);
			Z1.copyAt(y);
			Z1.addAt(IMeasurAble.Half);
			Z1.IntAt(); // infinite Recursion
			Zaehler.mulAt(Z1);
			Zaehler.addAt(Zii); // {1 Multiplikation mehr,aber staendige Kontrolle der}
			Nenner.mulAt(Z1);
			Nenner.addAt(Nii); // {Genauigkeit,keine neue Schleife,kein Puffer}
		} // can use integer Numbers for most calculations.
	}

	/**
	 * binaerer ggT-Algorithmus, sehr viel schneller als normaler euklid. Algorithmus,
	 * aber nur für ganze Zahlen 'this' und y
	 */
	public IMetricIRing ggT2At(IMetricIRing y) {
		y = (IMetricIRing) y.AbsV(); // {damit der ungerade-ungerade-Fall zu KLEINEREN
										// Zahlen fuehrt}
		IMetricIRing Zaehler = (IMetricIRing) one();
		IMetricIRing x = this;// (IIntRing) copy();
		while (!equals(y)) {
			if (isOdd())
				if (y.isOdd()) {
					x.subAt(y);
					x.AbsVAt();
					x.halfAt();
				} else y.halfAt();
			else if (y.isOdd())
				x.halfAt();
			else {
				Zaehler.dblAt();
				x.halfAt();
				y.halfAt();
			}
		}
		return (IMetricIRing) mulAt(Zaehler);
	}

	// ////////////////////////////////////////////
	// Optimizations, only for real Numbers! //
	// ////////////////////////////////////////////

	/**
	 * Calculates the Combination(n,k) = n!/(k!*(n-k)!) This is the number of Samples with
	 * Size k from a Set of n Elements, without considering the Sequence. It is also used
	 * with real n on calculating the Power Series of 'small' Disturbances: (1+-x)^m = 1 +-
	 * mx + ... + Comb(m,k)(+-x)^k This Calculation is optimized only in MetricIRing,
	 * because Comb(n, k) == Comb (n, n-k) (but only for integer n!!!). The only Problem
	 * is that for large n and k the Division takes place after the Calculation of Vari(n,
	 * k), which may result in an Overflow.
	 */
	public IIntRing Combination(IIntRing k) {
		AIntRing Fact = (AIntRing) k.newInstance();
		if (this.equals(this.IntAt()) && // only for integer Numbers
				(this instanceof IMetricIRing) && // only if a Metric is defined for
													// this or k
				isLessThan(k.dbl())) // with long Numbers the Division had to be
										// singled out.
			return (IIntRing) VariCombi((IMetricIRing) sub(k), Fact).divAt(Fact);
		return (IIntRing) VariCombi(k, Fact).divAt(Fact); // 'else' is unnecessary!
	}

	/**
	 * Calculates the Double Factorial of this integer number in Place. The Definition is
	 * recursive: n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;
	 */
	public IIntRing dblFactAt() {
		if (isZero()) return (IIntRing) oneAt(); // check for the special Case
		IIntRing one = (IIntRing) ((IIntRing) newInstance()).oneAt();
		IMetricIRing Factor = (IMetricIRing) copy();
		while (Factor.isMoreThan(one)) { // isZero() || Factor.isOne()))//
			mulAt(Factor.dec().dec());
		} // The Test in this Loop is optimized in AMetricIRing!
		return this;
	}

	/**
	 * p-Norm: Defined as Sum(|x|^p)^1/p Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.
	 */
	public IMetricIRing p_Norm(double p) {
		return (IMetricIRing) AbsV();
	}

	/**
	 * Maximums-Norm Special Case of the p-Norm for p -> Infinity
	 */
	public IMetricIRing Max_Norm() {
		return (IMetricIRing) AbsV();
	}

	/**
	 * (Euklidische Norm)^2 Special Case of the p-Norm for p = 2 Rotation Invariant for
	 * cartesian Systems.
	 */
	public IMetricIRing SqrNorm() {
		return (IMetricIRing) sqr();
	}

	/** Raised by an Integer Power of 2 in Place: x^=(2^n) */
	public ISemiGroupM Pow2PowAt(int n) {
		if (n >= 0) return super.Pow2PowAt(n);
		int i = 0;
		while (--i >= n)
			SqRtAt();
		return this;
	}

	/**
	 * Fast, but unsmooth, even discontinuous Representation of Delta as an asymmetric
	 * Rectangle Function. If H is null (not given), it is assumed to 1.
	 */
	public IMetricIRing Delta1(Object H) {
		return ((IMetricIRing) copy()).Delta1At(H);
	}

	/**
	 * Fast, but unsmooth, even discontinuous Representation of Delta as an asymmetric
	 * Rectangle Function. If H is null (not given), it is assumed to 1.
	 */
	public IMetricIRing Delta1At(Object H) {
		if (negative()) return (IMetricIRing) zeroAt();
		if (H != null) mulAt(H); // assume it to 1
		if (isMoreThan(ICountAble.One))
			zeroAt();
		else if (H != null)
			copyAt(H);
		else oneAt(); // assume it to 1
		return this;
	}

	/**
	 * Continuous Step Function, returns 1 for positive and 0 for negative Numbers. Is
	 * related to the Sign Function.
	 */
	public IMetricIRing Step1(Object H) {
		return ((IMetricIRing) copy()).Step1At(H);
	}

	/**
	 * Continuous Step Function, returns 1 for positive and 0 for negative Numbers. Is
	 * related to the Sign Function.
	 */
	public IMetricIRing Step1At(Object H) {
		if (negative()) return (IMetricIRing) zeroAt();
		if (H != null) mulAt(H); // assume it to 1
		if (isMoreThan(ICountAble.One)) oneAt(); // assume it to 1
		return this;
	}

	/**
	 * Discontinuous Step Function, returns 1 for positive and 0 for negative Numbers. Is
	 * related to the Sign Function.
	 */
	public IMetricIRing Step0() {
		return ((IMetricIRing) copy()).Step0At();
	}

	/**
	 * Discontinuous Step Function, returns 1 for positive and 0 for negative Numbers. Is
	 * related to the Sign Function.
	 */
	public IMetricIRing Step0At() {
		if (positive())
			oneAt();
		else zeroAt();
		return this;
	}

	// ////////////////////////////////////
	// Routines for new Result Types //
	// ////////////////////////////////////

	/**
	 * Returns the Remainder of the Division by Modulus in Place. The Remainder is
	 * centered around 0 and allows for Rescaling of periodic Functions. This is different
	 * to the Modl() Method, which returns the signed Modulus, which is mirrored around 0
	 * and not periodic. Calculating the Remainder by iteratively subtracting the Modulus
	 * is about 10 times faster and more accurate than calculating it by Division like in
	 * ModDivAt or ModAtDivAt.
	 */
	public IMetricIRing RemAt(Object Modulus) {
		subAt(((IMetricIRing) div(Modulus)).roundAt().mul(Modulus));
		return this;
	}

	/*
	 * bool neg = negative(); Modulus = ((MetricIRing) Modulus.AbsV(); //Leave the
	 * Original MIntRing& abs = ((MetricIRing) ((CopyAble) AbsVAt()).copy(); //Create a
	 * Copy int i = 1; while (++i <= 10) { if ((((MetricIRing)
	 * abs.subAt(Modulus)).negative()) break; else copyAt(abs); //use DeepCopy } if (i ==
	 * 11) subAt((((MetricIRing) divAt(Modulus)).Floor().mul(Modulus)); //explizit
	 * berechnen if (neg) return ((MetricIRing) negAt(); else return this; /**Returns the
	 * Remainder of the Division by Modulus. The Remainder is centered around 0.
	 */
	public IMetricIRing Rem(Object Modulus) {
		return ((IMetricIRing) copy()).RemAt(Modulus);
	}

	/**
	 * Returns the Remainder and the Quotient of the Division by Modulus. The Remainder is
	 * periodic and centered around 0.
	 */
	public IMetricIRing RemAtDivAt(Object Modulus, ICopyAble Div) {
		((IMetricIRing) divAt(Modulus)).RemAtIntAt(Div).mul(Modulus);
		return this;
	}

	/**
	 * Returns the Remainder and the Quotient of the Division by Modulus. The Remainder is
	 * periodic and centered around 0.
	 */
	public IMetricIRing RemDivAt(Object Modulus, ICopyAble Div) {
		return ((IMetricIRing) copy()).RemAt(Modulus);
	}

	/**
	 * Returns the largest (closest to positive infinity) value, that is not greater than
	 * the argument and is equal to a mathematical integer.
	 */
	public IMetricIRing Floor() {
		return ((IMetricIRing) copy()).FloorAt();
	}

	/**
	 * Returns the smallest (closest to negative infinity) value in Place, that is not
	 * less than the argument and is equal to a mathematical integer.
	 */
	public IMetricIRing CeilAt() {
		inc();
		return FloorAt();
	}

	/**
	 * Returns the smallest (closest to negative infinity) value, that is not less than
	 * the argument and is equal to a mathematical integer.
	 */
	public IMetricIRing Ceil() {
		return ((IMetricIRing) copy()).CeilAt();
	}

	/**
	 * Returns the closest Integer to the argument in Place (as far as accuracy allows).
	 */
	public IMetricIRing roundAt() {
		addAt(IMeasurAble.Half);
		return FloorAt();
	}

	/** Returns the closest integer to the argument (as far as accuracy allows). */
	public IMetricIRing round() {
		return ((IMetricIRing) copy()).roundAt();
	}

	/**
	 * Returns the Fractional Part of this Number in Place, and the rounded integer Part
	 * in Object. The Result is from [-0.5;+0.5]
	 */
	public IMetricIRing RemAtIntAt(ICopyAble Int) {
		subAt(Int.copyAt(round(), 0));
		return this;
	}

	/**
	 * Returns the Fractional Part of this Number, and the integer Part in Object. The
	 * Result is from [-0.5;+0.5]
	 */
	public IMetricIRing RemIntAt(ICopyAble Int) {
		return ((IMetricIRing) copy()).RemAtIntAt(Int);
	}

	// ////////////////////////////////////
	// Routines for inPlace Calculations //
	// ////////////////////////////////////

	/**
	 * Returns the Fractional Part of this Number in Place, and the rounded integer Part
	 * in Object. The Remainder is centered around 0!
	 */
	public IMetricIRing roundAtIntAt(ICopyAble Int) {
		subAt(Int.shallowCopyAt(round()));
		return this;
	}

	/**
	 * Returns the Fractional Part of this Number, and the integer Part in Object. The
	 * Remainder is centered around 0!
	 */
	public IMetricIRing roundIntAt(ICopyAble Int) {
		return ((IMetricIRing) copy()).roundAtIntAt(Int);
	}

	/**
	 * Returns the n-th pythagorean Number Tripel (a,bc) defined by a^2 + b^2 = c^2 This
	 * is a Diophantean Equations of the Form a^l + b^m = c^n which has infinitely many
	 * integer Solutions. Fermat's famous conjecture is that the Equation a^n + b^n = c^n
	 * has no solution for n > 2. This has been proven empirically for n < 150000 Fermat
	 * claimed to have found an easy solution, but that is now believed to be a fault.
	 */
	public void PythagTripel(IMetricIRing a, IMetricIRing b, IMetricIRing c) {
		IMetricIRing m = this;// getLong();
		IMetricIRing Z1 = (IMetricIRing) dbl();
		Z1.addAt(IMeasurAble.Quarter);
		Z1.SqRtAt();
		Z1.addAt(IMeasurAble.Half);
		Z1.IntAt().inc();
		// long Z1 = 1 + (int) (Math.sqrt ((m << 1) + 0.25) + 0.5);
		IMetricIRing Z2 = (IMetricIRing) m.sub(Z1);
		Z2.addAt(ICountAble.Three); // {<Z1}
		a.copyAt(Z1);
		a.sqrAt();
		b.copyAt(Z2);
		b.sqrAt();
		c.copyAt(a);
		c.addAt(b); // (a+b)^2 = a^2 + b^2 + 2ab
		a.subAt(b);
		a.AbsVAt(); // {>0!}
		b.copyAt(Z1);
		b.mulAt(Z2);
		b.dblAt();
	}

	// ////////////
	// Testing //
	// ////////////

	/** Method to test the Pythagorean Tripel Generation. */
	/*
	 * TODO private static void tPythagTripel() throws java.io.IOException {
	 * Body.BodyDouble i = new Body.BodyDouble(); Body.BodyDouble a = new
	 * Body.BodyDouble(); Body.BodyDouble b = new Body.BodyDouble(); Body.BodyDouble c =
	 * new Body.BodyDouble(); while (++i.Value <= 6) { i.PythagTripel(a, b, c);
	 * System.out.println(" a = " + a + "; a^2 = " + a.sqr() + "; b = " + b + "; b^2 = " +
	 * b.sqr() + "; c = " + c + "; c^2 = " + c.sqr()); } System.in.read();
	 * System.in.read(); } /**Method to test all Implementations in this class.
	 */
	public static void testIt() throws Exception {
		IMetricIRing test = (IMetricIRing) testInstance;
		IMetricIRing test1 = (IMetricIRing) testInstance.copy();
		System.out.println("Testing AMetricIRing:");

		tSqRt();
		tCbcRt();
		// Combinatoric Functions:
		test.zeroAt();
		System.out.println("Soll:  1	Ist: " + test.Fact());
		test.inc();
		System.out.println("Soll:  1	Ist: " + test.Fact());
		test.inc();
		System.out.println("Soll:  2	Ist: " + test.Fact());
		test.inc();
		System.out.println("Soll:  6	Ist: " + test.Fact());
		test.inc();
		System.out.println("Soll: 24	Ist: " + test.Fact());
		test.inc();
		test.copyAt(new Integer(26));
		System.out.println("Soll: 4.03291461126606e26 Ist: " + test.Fact());
		test.zeroAt();
		System.out.println("Soll:  1	Ist: " + test.dblFact());
		test.inc();
		System.out.println("Soll:  1	Ist: " + test.dblFact());
		test.inc();
		System.out.println("Soll:  2	Ist: " + test.dblFact());
		test.inc();
		System.out.println("Soll:  3	Ist: " + test.dblFact());
		test.inc();
		System.out.println("Soll:  8 	Ist: " + test.dblFact());
		test.inc();
		System.out.println("Soll: 15 	Ist: " + test.dblFact());
		test.inc();
		System.in.read();
		System.in.read();
		test.copyAt(new Integer(26));
		System.out.println("Soll: 5.10117543936e13 Ist: " + test.dblFact());

		IMetricIRing fact = (IMetricIRing) test.newInstance();
		test.oneAt();
		test1 = (IMetricIRing) test.trpl().dblAt();
		test1.addAt(test); // 2*3+1 = 7

		test.zeroAt();
		System.out.println("Soll:  1	Ist: " + test1.VariCombi(test, fact));
		test.inc();
		System.out.println("Soll:  (   7,   1)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();
		System.out.println("Soll:  (  42,   2)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();
		System.out.println("Soll:  ( 210,   6)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();
		System.out.println("Soll:  ( 840,  24)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();
		System.out.println("Soll:  (2520, 120)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();
		System.out.println("Soll:  (5040, 720)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();
		System.out.println("Soll:  (5040,5040)	Ist: (" + test1.VariCombi(test, fact)
				+ "," + fact + ")");
		test.inc();

		test.zeroAt();
		System.out.println("Soll:  0	Ist: " + test.SqRt());
		test.inc();
		System.out.println("Soll:  1	Ist: " + test.SqRt());
		test.inc();
		System.out.println("Soll:  1.414	Ist: " + test.SqRt());
		test.inc();
		System.out.println("Soll:  1.732	Ist: " + test.SqRt());
		test.inc();
		System.out.println("Soll:  2	Ist: " + test.SqRt());
		test.inc();

		test.zeroAt();
		System.out.println("Soll:  0	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1.26	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1.442	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1.587	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1.710	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1.817	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  1.913	Ist: " + test.CbcRt());
		test.inc();
		System.out.println("Soll:  2	Ist: " + test.CbcRt());
		test.inc();

		// MetricIRing y = new MetricIRing(3);
		// x.copyAt (y.SqRt());// 1,732050807569
		test.negAt();
		System.out.println("Soll: 15 	Ist: " + test.mulAccuracy());
		System.out.println("Soll: 15 	Ist: " + test.mulAbsAccuracy());
		System.out.println("Soll: 15 	Ist: " + test.divAccuracy());
		System.out.println("Soll: 15 	Ist: " + test.divAbsAccuracy());

		test.oneAt();
		test.inc();
		System.out.println("Soll: 16 = 1*2^(2^2) = 2^4	Ist: " + test.Pow2Pow(2));
	}

	private static void tSqRt() throws Exception {
		IMetricIRing test = (IMetricIRing) testInstance; // defined in ACopyAble to
															// test the abstract Methods
		IMetricIRing x1 = (IMetricIRing) test.newInstance();
		IMetricIRing Infin = (IMetricIRing) test.Infinity();
		ByRefDouble x2 = new ByRefDouble();

		System.out.println();
		System.out.println("Test von SqRt (Quadrat-Wurzel) :");
		System.out.println("Soll : " + Infin + "  Ist : " + Infin.SqRt() + "  "
				+ Infin.sqr());
		for (x2.Value = 0; ++x2.Value <= 20;) {
			x1.copyAt(x2);
			System.out.println("Soll : " + x1 + "  Ist : " + x1.SqRt().sqr() + " ; "
					+ ((IMetricIRing) x1.sqr()).SqRt());
		}
		System.in.read();
		System.out.println("Die Abweichungen sollen kleiner als " + BaseAccuracy
				+ " sein !");
		for (int Z1 = 1; ++Z1 < 20;) {
			x2.Value = 10 * Math.random();
			x1.copyAt(x2);
			System.out.println(((IMetricIRing) x1.SqRt().sqr()).sub(x1) + "	:	"
					+ ((IMetricIRing) x1.sqr()).SqRt().sub(x1));
		}
		System.in.read();
	}

	private static void tCbcRt() throws Exception {
		IMetricIRing test = (IMetricIRing) testInstance; // defined in ACopyAble to
															// test the abstract Methods
		IMetricIRing x1 = (IMetricIRing) test.newInstance();
		IMetricIRing Infin = (IMetricIRing) test.Infinity();
		ByRefDouble x2 = new ByRefDouble();

		System.out.println();
		System.out.println("Test von Cbc/CbcRt (Kubik-Wurzel) :");
		System.out.println("Soll : " + Infin + "  Ist : " + Infin.CbcRt() + " ; "
				+ Infin.cbc());
		for (x2.Value = 0; ++x2.Value <= 20;) {
			x1.copyAt(x2);
			System.out.println("Soll : " + x1 + "  Ist : " + x1.CbcRt().cbc() + " ; "
					+ ((IMetricIRing) x1.cbc()).CbcRt());
		}
		System.in.read();
		System.out.println("Die Abweichungen sollen kleiner als " + BaseAccuracy
				+ " sein !");
		for (int Z1 = 1; ++Z1 < 20;) {
			x2.Value = 100 * Math.random() - 50;
			x1.copyAt(x2);
			System.out.println(((IMetricIRing) x1.CbcRt().cbc()).sub(x1) + "	:	"
					+ ((IMetricIRing) x1.cbc()).CbcRt().sub(x1));
		}
		System.in.read();
	}

}
