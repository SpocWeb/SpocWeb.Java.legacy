package streamIO.copy.group.ring.metric.body;

import streamIO.IDeserializer;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.integer;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.ICountAble;
import function.IMeasurAble;

/**Concrete fincal Class to define Fractions of arbitrary Types.
 * Fractions from a Metric Integrity Ring form a Metric Body.
 *
 * Design Decisions:
 * Default Type for this Class is RingLong, but it can work with any other Class.
 *
 * The Behavior of Fractions can be controlled by boolean static Variables:
 * -The Denominator of Fraction will always be positive.
 * -A Fraction will always be shortened after any single Operation.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T21:00:02Z
 * digest: 3b427498675f7efef18391a3bee458b1f45d3680af1489bfb629d48e901b68a4
 * stale: false
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
final public class Fraction
extends AMetricBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**The Denominator of this Fraction.
	 * It is made public for easier use and higher Performance.	 */
	public IMetricIRing Denominator;

	/**The Numerator of this Fraction.
	 * It is made public for easier use and higher Performance.	 */
	public IMetricIRing Numerator;

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public Fraction(Fraction arg)
	{
		Numerator   = (IMetricIRing) arg.Numerator  .copy();
		Denominator = (IMetricIRing) arg.Denominator.copy();
	}

	/**Constructor that takes two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation.	 */
	public Fraction(Object Numerator_, Object Denominator_)
	{
		Numerator   = (IMetricIRing) ((ICopyAble)Numerator_  ).copy();
		Denominator = (IMetricIRing) ((ICopyAble)Denominator_).copy();
	}

	/**Constructor that takes a long as Input for the Numerator.
	 * The Denominator is set to one().	 */
/*	public Fraction(long Numerator_)
	{
		Numerator = new RingLong(Numerator_);
		Denominator = (MetricIRing) Numerator.one();	//Choose the same type -> faster
	}
*/
	/**Helper Routine to convert to MetricIRing from any other Type.
	 * Uses RingLong as the default Type.	 */
/*	private final MetricIRing convertScalar (Object arg)
	{return (arg instanceof MetricIRing)? (MetricIRing) arg : new RingLong(arg); }
*/
	/**Constructor that takes any Object as Input for the Numerator.
	 * So far rounds to the nearest Integer.
	 * The Denominator is set to one().
	 * WIP: In the Future choose the nearest Fraction.	 */
	public Fraction(Object Numerator_)
	{
		if (Numerator_ instanceof Fraction)
		{
			Numerator   = (IMetricIRing) ((Fraction) Numerator_).Numerator  .copy();
			Denominator = (IMetricIRing) ((Fraction) Numerator_).Denominator.copy();
		}
		else
		{
			Numerator   = (IMetricIRing) ((ICopyAble) Numerator_).copy();
			Denominator = (IMetricIRing) Numerator.one();	//Choose the same type -> faster
		}
	}

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.	 */
	protected Fraction()
	{
//		BaseAccuracyInv = SqRtMaxValue();
//		BaseAccuracy = BaseAccuracyInv.inv();
//		Numerator   = new RingLong();
//		Denominator = new RingLong();
	}

	/**Switches Shortening of Results after each Operation by 'shortenAt()' off.
	 * For a better performance, it is typically switched on.
	 * To get shortened Results, rather shorten regularly on End-Results.	 */
	public static boolean doSimplify = true; //false;

	/**Switches Checking for a positive Sign of the Denominator of the Result off.
	 * Since this is only needed for a faster Sign Check,
	 * and Comparison Operations, it is typically switched off,	 */
	public static boolean doSigning  = false;

	/**Shortens the Fraction by dividing Numerator and Denominator by their 'ggT'
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 *
	 * This implementation returns an Integer if possible.
	 * It throws an Accuracy Exception, if the Values become too large,
	 * and no shortening is possible without losing Accuracy.
	 * In this case shortening is done by dividing both Numerator and denominator in half.
	 * When this also fails, it returns 'Infinity'.	 */
	public IMetricIRing shortenAt() {	//Shorten the Fraction
		if (doSimplify) {
			IMetricIRing H1 = (IMetricIRing) Denominator.ggT(Numerator);
			if (! ((IMetricIRing)H1.AbsVAt()).isOne()) { //Numerator and Denominator have no ggT, except for 1.
				Numerator.divAt(H1);		  //finding out the ggT is already expensive
				if (Denominator.equals(H1)) { //But this finds out, whether an Integer appears.
					Denominator.oneAt();
					return Numerator; }
				else { Denominator.divAt(H1); }	//further Savings justify this test.
			}
		}
		return shortRoundAt(); }

	/**This Method shortens a Fraction, but creates rounding Errors,
	 * by iteratively dividing both Numerator and Denominator by two.
	 * It is only possible for Scalar Numbers, not e.g. Polynoms.	 */
	public IMetricIRing shortRoundAt() {
		if (Denominator instanceof IScalarMetric) {
			IMetricIRing H1 = (IMetricIRing)((IMetricIRing) Denominator.AbsV()).Max(Numerator.AbsV());
			while (H1.isMoreThan (Denominator.SqRtMaxValue())) {	//raise an exception for rounding Errors.
				H1         .halfAt();
				Numerator  .halfAt();
				Denominator.halfAt();
			}
		}
		return this; }

	/**Checks for a positive Sign of the Denominator of the Result.
	 * When all Arguments are correctly signed, this Check
	 * becomes necessary only on Division and Inversion.
	 * Since this is only needed for a faster Sign Check,
	 * and Comparison Operations, it is typically switched off,
	 * because checks for the Sign occur much less frequent
	 * than these basic Operations that would need this Check.	 */
	public IMetricIRing correctSignAt() {	//Shorten the Fraction
		if (doSigning && Denominator.negative()) { //
			Denominator.negAt();
			Numerator  .negAt(); }
		return this; }

	/**Setting to 0 in Place:	 */
	public IGroup zeroAt() {
		Numerator  .zeroAt();
		Denominator.oneAt();	//not necessary, unless it is also zero
		return this; }

	/**Setting to 1 in Place:	 */
	public IGroupM oneAt() {
		Numerator  .oneAt();
		Denominator.oneAt();
		return this; }

	/**Testing for 0:	 */
	public boolean isZero() { return Numerator.isZero() && ! Denominator.isZero(); }

	/**Returns the largest (closest to positive infinity) Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 *
	 * Sets the Fraction, but returns an Integer, this saves time in further Calculations!	 */
	public IMetricIRing FloorAt() {
		Numerator.divAt(Denominator);	//ignore the Remainder
		Denominator.oneAt();
		return Numerator; }

	/**Returns the integer Part of the Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 *
	 * Sets the Fraction, but returns an Integer, this saves time in further Calculations!	 */
	public IIntRing IntAt() {
		Numerator.divAt(Denominator);	//TODO: find out how div rounds... ignore the Remainder
		Denominator.oneAt();
		return Numerator; }

	/**Returns the largest (closest to positive infinity) value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 *
	 * Returns an Integer, not a Fraction,
	 * this also saves time in further Calculations!	 */
	// TODO: LOGIC: operands are swapped - this computes Denominator/Numerator (the reciprocal)
	// instead of Numerator/Denominator, unlike the equivalent FloorAt() above which correctly
	// divides Numerator by Denominator. Floor() therefore returns a wrong result for any Fraction
	// where Numerator != Denominator (same bug is copy-paste-duplicated in FractionLong.java).
	public IMetricIRing Floor() { return (IMetricIRing) Denominator.div(Numerator); }	//ignore the Remainder

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) {	//Works only for positive Denominators!!!
		if (arg instanceof Fraction) {
			return ((IMetricIRing)	Numerator  .mul(((Fraction)arg).Denominator))
					.isLessThan(			Denominator.mul(((Fraction)arg).Numerator  )); }
			return Numerator.isLessThan(Denominator.mul(arg)); }	//Integer Argument

	/**Returns the maximum Value for this Class.
	 * Returns +Infinity = 1/0 for Fractions.	 */
	public IWellOrder maxValueAt() {
		Numerator.oneAt();
		Denominator.zeroAt();
		return this; }

	/**Helper Routine to convert to Fraction from any other numeric Type:
	 * RingLong, Number or ICountAble.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
	private final Fraction convertArg (Object arg) {
		return (arg instanceof Fraction)? (Fraction) arg : new Fraction(arg); }

	//////////////////////////////
	//	interface intComplex	//
	//////////////////////////////

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() {
		Denominator	.cjgAt();
		Numerator	.cjgAt();
		return this; }

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar	 */
	public boolean isComplex() { return Numerator	 .isComplex() ||
									  Denominator.isComplex(); }

	//	interface Group, GroupM etc.	//

	/**Addition in Place: +=	 */
	public ISemiGroup  addAt(Object arg) { return addSubAt(arg,true ); }

	/**Subtraction in Place: -=	 */
	public IGroup     subAt(Object arg) { return addSubAt(arg,false); }

	/**Addition or Subtractin in Place: +=, -=	 */
	public IMetricIRing addSubAt(Object arg, boolean add) {	//watch out for Integer Arguments:
		if (arg == null) return this;
		if (arg instanceof Fraction) {
			ISemiGroupM H1 = Denominator.mul(((Fraction)arg).Numerator);
			Numerator  .mulAt(((Fraction)arg).Denominator);
			if (add) Numerator.addAt (H1);	//Flag f�r Addition/Subtraktion
			else	 Numerator.subAt(H1);
			Denominator.mulAt(((Fraction)arg).Denominator);
		} else {	//Integer Argument
			if (add) Numerator.addAt (Denominator.mul(arg));	//Flag f�r Addition/Subtraktion
			else	 Numerator.subAt(Denominator.mul(arg));
		}
		if (doSimplify) return shortenAt();
		return this; }

	/**Multiplication in Place: *=	 */
	public ISemiGroupM mulAt(Object arg) {
		if (arg == null) return this;
		if (arg instanceof Fraction) { //Check if the Argument is a scalar, before converting it to a Fraction
			Numerator  .mulAt(((Fraction)arg).Numerator);
			Denominator.mulAt(((Fraction)arg).Denominator);
		} else {
			Numerator  .mulAt(arg); }	//Integer Argument
		if (doSimplify) return shortenAt();
		return this; }

	/**Division in Place: /=	 */
	public IGroupM divAt(Object arg) {
		if (arg == null) return this;
		if (arg instanceof Fraction) {//Check if the Argument is a scalar, before converting it to a Fraction
			Denominator.mulAt(((Fraction)arg).Numerator);
			Numerator  .mulAt(((Fraction)arg).Denominator);
		} else {
			Denominator.mulAt(arg); }	//Integer Argument
		correctSignAt();	//This and invAt are the only Operation where this could happen.
		if (doSimplify) return shortenAt();
		return this; }

	/**Inversion in Place: 1/x	 */
	public IGroupM invAt() {
		IMetricIRing tmp = Numerator;
		Numerator   = Denominator;
		Denominator = tmp;
		correctSignAt();	//This and divAt are the only Operations where this could happen.
		return this; }

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public byte	  getByte() { return ((ICountAble) Numerator.div(Denominator)).getByte(); }

	/** Returns the Object Value represented by an 16 Bit Integer	 */
	public short getShort() { return ((ICountAble) Numerator.div(Denominator)).getShort(); }

	/** Returns the Object Value represented by an 32 Bit Integer	 */
	public int	   getInt() { return ((ICountAble) Numerator.div(Denominator)).getInt(); }

	/** Returns the Object Value represented by an 64 Bit Integer	 */
	public long   getLong() { return ((ICountAble) Numerator.div(Denominator)).getLong(); }

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() { return
		((IMeasurAble) Numerator  ).getDouble() /
		((IMeasurAble) Denominator).getDouble(); }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign
	 */
	public float   getFloat() { return
		((IMeasurAble) Numerator  ).getFloat() /
		((IMeasurAble) Denominator).getFloat(); }

	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		Fraction tmp = new Fraction();
		tmp.Numerator   = (IMetricIRing) ((ICopyAble)Numerator  ).newInstance();
		tmp.Denominator = (IMetricIRing) ((ICopyAble)Denominator).newInstance();
		return tmp; }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.	 */
	public ICopyAble copyAt(Object arg, int Depth) {
//		super.CopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		--Depth;
		Fraction tmp = convertArg(arg);
		((ICopyAble)Denominator).copyAt(tmp.Denominator	, Depth);
		((ICopyAble)Numerator  ).copyAt(tmp.Numerator	, Depth);
		return this; }

	/**Returns a hash code value for the object. This method is
	 * supported for the benefit of hashtables such as those provided by
	 * <code>java.util.Hashtable</code>.
	 * <p>
	 * The general contract of <code>hashCode</code> is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during
	 * an execution of a Java application, the <code>hashCode</code> method
	 * must consistently return the same integer. This integer need not
	 * remain consistent from one execution of an application to another
	 * execution of the same application.
	 * <li>If two objects are equal according to the <code>equals</code>
	 * method, then calling the <code>hashCode</code> method on each of the
	 * two objects must produce the same integer result.
	 * </ul>
	 *
	 * @return  a hash code value for this object.
	 * @see     java.lang.Object#equals(java.lang.Object)
	 * @see     java.util.Hashtable
	 * @since   JDK1.0	 */
	public int hashCode() { return Denominator.hashCode() + Numerator.hashCode() ; }

	/**Decides, whether DisplayThreshold is used to determine,
	 * how the Fraction is formatted.	 */
	public static boolean bolDisplayThreshold;

	/**Decides, how the Fraction is formatted.
	 * When the Denominator is larger than this Threshold,
	 * the Fraction is converted to a Float.	 */
	public static IMetricIRing DisplayThreshold;

	/**String prepended before the Fraction when formatted as text.	 */
	public static String Starter = "(";
	/**String appended after the Fraction when formatted as text.	 */
	public static String Stopper = ")";
	/**String placed between Numerator and Denominator when formatted as text.	 */
	public static String Separator = "/";

	/**Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommendedthat all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object.
	 *
	 * @return  a string representation of the object.
	 * @since   JDK1.0	 */
	public String toString() {	//for Serialization 'bolDisplayThreshold' must be false!
		if (bolDisplayThreshold && (DisplayThreshold.notMoreThan(Denominator.AbsV())))
//				return Numerator.toString();
				return String.valueOf(	((IMeasurAble)Numerator  ).getDouble() /
										((IMeasurAble)Denominator).getDouble());
		else	return Starter + Numerator.toString() + Separator + Denominator.toString() + Stopper;
	}


	/**Returns the given Object from it's textual Representation
	 * Switches off bolDisplayThreshold in between
	 * for full reconstruction of the Coefficients.	 */
/*	final public String toText() {
		boolean buffer = bolDisplayThreshold;
		bolDisplayThreshold = false;
		String tmp = toText(this);
		bolDisplayThreshold = buffer;
		return tmp; }
	*/

	/**Parses the String with the full Description to a Fraction Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(IDeserializer arg) throws java.io.IOException {
		Numerator	.fromStreamAt(arg);
		Denominator	.fromStreamAt(arg);
		return this; }

	//////////////////////
	//	Optimizations:	//
	//////////////////////

	/**Negation in Place: -	 */
	public     IGroup negAt () { Numerator.negAt() ; return this; }

	/**Double in Place: x+=x == x*=2	*/
	public ISemiGroup dblAt () { Numerator. dblAt(); return this; }

	/**Triple in Place: x+=x+x == x*=3	*/
	public ISemiGroup trplAt() { Numerator.trplAt(); return this; }

	/** Quadruple in Place: x+=x; x+=x  == x*=4 	*/
	public ISemiGroup quadAt() { Numerator.quadAt(); return this; }

	/**Decrement in Place: x-=1	*/
	public integer dec() { Numerator.subAt(Denominator); return this; }
	/**Increment in Place: x+=1	*/
	public integer inc() { Numerator.addAt (Denominator); return this; }
	/**Residual in Place: 1-x	 */
	public integer ResidAt() {
		Numerator = (IMetricIRing)Denominator.sub(Numerator); return this; }

	/**Returns whether this Fraction is negative (Denominator is kept positive by convention).	 */
	public boolean negative() { return (Numerator.negative()); }
	/**Returns whether this Fraction is positive (Denominator is kept positive by convention).	 */
	public boolean positive() { return (Numerator.positive()); }

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm
	 */
	public IScalarMetric AbsVAt()  { Numerator.AbsVAt(); return this; }

	/**Returns the exact two in Place.	 */
	public IIntRing   twoAt() { Numerator.twoAt  (); Denominator.oneAt(); return this; }
	/**Returns the exact three in Place.	 */
	public IIntRing threeAt() { Numerator.threeAt(); Denominator.oneAt(); return this; }

	/**Half in Place : x/=2	*/	public IIntRing halfAt () { Denominator.dblAt (); return this; }
	/**Third in Place: x/=3	*/	public IIntRing thirdAt() { Denominator.trplAt(); return this; }

	public ISemiGroupM sqrAt() {
		Numerator  .sqrAt();	//This may incur some optimization
		Denominator.sqrAt();
		return (ISemiGroupM) shortRoundAt(); }	//No ggT for Numerator or Denominator => only rounding.

	/**Cubes this Fraction in Place, then shortens it (no ggT tracking, only rounding).	 */
	public ISemiGroupM cbcAt() {
		Numerator  .cbcAt();	//This may incur some optimization
		Denominator.cbcAt();
		return (ISemiGroupM) shortRoundAt();	} //No ggT for Numerator or Denominator => only rounding.

	/**Standard Implementation: a/b-c/d == 0
	 * Faster: a*d == c*b
	 * even faster: a == c && b == d
	 */
	public boolean equals(Object arg) {
		if (arg instanceof Fraction)
			return ((IMetricIRing)	Numerator  .mul(((Fraction)arg).Denominator))
					.equals(		Denominator.mul(((Fraction)arg).Numerator  ));
			return Numerator.equals(Denominator.mul(arg)); }	//Integer Argument

	//left out: even, isOdd, ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt


/*	PROCEDURE MakeRational (x : Real;VAR Z,N : Integer);
	BEGIN {f?r schnellere Integer-Berechnungen}
	 IF ABS (x) > Eins
	  THEN BEGIN Z:=+MaxRational;N:=Round (Z/ABS (x));IF Negativ (x) THEN
	             Z:=-MaxRational END
	  ELSE BEGIN N:=+MaxRational;Z:=Round (N*x) END
	END;
*/

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry() { }

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt() { throw new AbstractMethodError(); }

	/**Complement in Place: ~=
	 * necessary for gAdic Calculation	*/
	public IIntRing CmplAt() { throw new AbstractMethodError(); }

	final static double  Conversion = 1000000000;
	final static Long LngConversion = new Long((long)Conversion);

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		Numerator  .shallowCopyAt(new Double(Conversion*Math.PI));
		Denominator.shallowCopyAt(LngConversion);
		return this; }

//	public SemiMonoid mapAt(SemiMonoid arg) { arg.copyAt(this); return arg; }

	/*	Breaking up a rational Function (Polynom) in a whole Polynom
		and partial Fractions (Partialbruch-Zerlegung).

		Liefert in P1 das ganze Polynom aus R1,die Nullstellen des Nenners
		und die Koeffizienten der Partialbrueche,die zu diesen Nullstellen
		korrespondierend auftreten.
		Bei mehrfachen Nullstellen ist der p.Koeffizient der Reihe derjenige,
		der zum Nenner (x-xo) mit der Potenz p+1 gehoert.
		Bei konjugiert komplexen Nullstellen (p+i*q,p-i*q) besteht der
		Nenner aus dem Quadrat (x�-2px+(p�+q�)) und der Zaehler aus dem
		Linearfaktor (A+Bx);hier wird zuerst A und dann B angegeben.

PROCEDURE PartialBruch (R1 : Rational;VAR N : cNullstelle;VAR P1 : Polynom;
                                      VAR Koeff : Vektor);
 Nenner1.Grad:=2;Nenner1.a^[2]:=Eins;
 P_DVN (R1.Zaehler,R1.Nenner,P1,R1.Zaehler);
 NullSuche (R1.Nenner,N);
 Z2:=Succ (R1.Zaehler.Grad);
 Kopiere (R1.Zaehler.a,b.Matrix,Z2*SizeOf (Real)); {Z�hler ?bertragen}
 Loesche (@b.Matrix^[Z2],(R1.Nenner.Grad-R1.Zaehler.Grad)*SizeOf (Real)); {ZeilenMatrix unpassend}
 FOR Z1:=1 TO R1.Nenner.Grad DO    {echt gebrochen rational !}
  BEGIN
   IF (Z1 = 1) OR NOT cAbout (NS,N.a^[Z1]) THEN Kopiere (R1.Nenner.a,H1.a,Z5);
   NS:=N.a^[Z1];
   IF     (ABS (NS.im) <= Pot2Mul (ABS (NS.re),Genauigkeit))
   OR (cBetABS (NS)    <= FastNull)
    THEN
     BEGIN
      Rund:=Redukt (H1,NS.re);  {Rund ist ein Dummy}
      Kopiere (H1.a,@M.Matrix^[Pred (Z1)*M.Spalten],H1.Grad*SizeOf (Real))
     END
    ELSE
     BEGIN
      Nenner1.a^[0]:=cSqrAbs (NS);Nenner1.a^[1]:=-Pot2Mul (NS.re,1); {Rest schon oben}
      P_DIV (H1,Nenner1,H1);
      Kopiere (H1.a,@M.Matrix^[Pred (Z1)*M.Spalten ],H1.Grad*SizeOf (Real));
      Kopiere (H1.a,@M.Matrix^[Succ (Z1 *M.Spalten)],H1.Grad*SizeOf (Real));
     END
  END;
 G_J_Algorithmus;

	 */
}
