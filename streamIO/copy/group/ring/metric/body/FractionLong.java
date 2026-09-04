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
import streamIO.exception.OperationNotSupported;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;

/**Concrete fincal Class to define Fractions of arbitrary Types.
 * Fractions from a Metric Integrity Ring form a Metric Body.
 *
 * Design Decisions:
 * Default Type for this Class is RingLong, but it can work with any other Class.
 *
 * The Behavior of Fractions can be controlled by boolean static Variables:
 * -The Denominator of Fraction will always be positive.
 * -A Fraction will always be shortened after any single Operation.
 */
final public class FractionLong
extends AMetricBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**The Denominator of this Fraction.
	 * It is made public for easier use and higher Performance.	 */
	public long Denominator; // = 0; //not necessary

	/**The Numerator of this Fraction.
	 * It is made public for easier use and higher Performance.	 */
	public long Numerator; // = 1; //not necessary

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public FractionLong(FractionLong arg) {
		Numerator   = arg.Numerator  ;
		Denominator = arg.Denominator;
	}

	/**Constructor that takes two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation.	 */
	public FractionLong(Object Numerator_, Object Denominator_) {
		Numerator   = ByRefLong.TO_LONG(  Numerator_);
		Denominator = ByRefLong.TO_LONG(Denominator_);
	}

	/**Constructor that takes two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation.	 */
	public FractionLong(Object Numerator_) {
		Numerator   = ByRefLong.TO_LONG(  Numerator_);
		Denominator = 1;
	}

	/**Constructor that takes a long as Input for the Numerator.
	 * The Denominator is set to one().	 */
	public FractionLong(long Numerator_, long Denominator_) {
		Numerator   = Numerator_;
		Denominator = Denominator_;
	}

	/**Constructor that takes a long as Input for the Numerator.
	 * The Denominator is set to one().	 */
	public FractionLong(long Numerator_) {
		Numerator = Numerator_;
		Denominator = 1;	//Choose the same type -> faster
	}

	/**Constructor that takes any Object as Input for the Numerator.
	 * So far rounds to the nearest Integer.
	 * The Denominator is set to one().
	 * WIP: In the Future choose the nearest Fraction.	 */
/*	public FractionLong(Object Numerator_) {
		if (Numerator_ instanceof FractionLong) {
			Numerator   = (long) ((FractionLong) Numerator_).Numerator  .copy();
			Denominator = (long) ((FractionLong) Numerator_).Denominator.copy();
		} else {
			Numerator   = (long) ((CopyAble) Numerator_).copy();
			Denominator = (long) Numerator.one();	//Choose the same type -> faster
		}
	}

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.	 */
	protected FractionLong() {
//		Numerator   = 0; //not necessary
		Denominator = 1;
	}

	/**Switches Shortening of Results after each Operation by 'shortenAt()' off.
	 * For a better performance, it is typically switched on.
	 * To get shortened Results, rather shorten regularly on End-Results.	 */
	public static boolean doSimplify = true; //false;

	/**Switches Checking for a positive Sign of the Denominator of the Result off.
	 * Since this is only needed for a faster Sign Check,
	 * and Comparison Operations, it is typically switched off,	 */
	public static boolean doSigning    = true; // false;

	/** Flag to indicate that a Rounding has taken place
	  * and this Number is no longer exact!
	  * @return the Number of Digits lost.  */
	public static int isRounded;

	/**Shortens the Fraction by dividing Numerator and Denominator by their 'ggT'
	 * This is necessary on all Operations:
	 * Addition, Subtraction, Multiplication and Division.
	 *
	 * This implementation returns an Integer if possible.
	 * It throws an Accuracy Exception, if the Values become too large,
	 * and no shortening is possible without losing Accuracy.
	 * In this case shortening is done by dividing both Numerator and denominator in half.
	 * When this also fails, it returns 'Infinity'.	 */
	public FractionLong shortenAt() {	//Shorten the Fraction
		if (doSimplify) {
			long H1 = ByRefLong.GGT_CLASSIC(Denominator,Numerator);
			if (Math.abs(H1) != 1) { //Numerator and Denominator have no ggT, except for 1.
				Numerator   /= H1;		  //finding out the ggT is already expensive
				Denominator /= H1; 	//further Savings justify this test.
			}
		}
		return shortRoundAt(); }

	/** This Method shortens a Fraction, but creates rounding Errors,
	  * by iteratively dividing both Numerator and Denominator by two.
	  * It is only possible for Scalar Numbers, not e.g. Polynoms.	 */
	public FractionLong shortRoundAt() {
		long H1 = Math.abs(Denominator)+Math.abs(Numerator);
		while (H1 > Integer.MAX_VALUE) { //axValue) {	//raise an exception for rounding Errors.
			H1         >>= 1;
			Numerator  >>= 1;
			Denominator>>= 1;
			++isRounded;
		}
		return this; }

	/**Checks for a positive Sign of the Denominator of the Result.
	 * When all Arguments are correctly signed, this Check
	 * becomes necessary only on Division and Inversion.
	 * Since this is only needed for a faster Sign Check,
	 * and Comparison Operations, it is typically switched off,
	 * because checks for the Sign occur much less frequent
	 * than these basic Operations that would need this Check.	 */
	public FractionLong correctSignAt() {	//Shorten the Fraction
		if (doSigning && (Denominator < 0)) { //
			Denominator = -Denominator;
			Numerator   = -  Numerator;
		} return this; }

	/**Setting to 0 in Place:	 */
	public IGroup zeroAt() {
		Numerator   = 0;
		Denominator = 1;	//not necessary, unless it is also zero
		return this; }

	/**Setting to 1 in Place:	 */
	public IGroupM oneAt() {
		Numerator  = 1;
		Denominator= 1;
		return this; }

	/**Testing for 0:	 */
	public boolean isZero() {
		return (Numerator   == 0) &&
		       (Denominator != 0); }

	/**Returns the largest (closest to positive infinity) Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 *
	 * Sets the Fraction, but returns an Integer, this saves time in further Calculations!	 */
	public IMetricIRing FloorAt() {
		Numerator /= Denominator;	//ignore the Remainder
		Denominator = 1;
		return this; }

	/**Returns the integer Part of the Value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 *
	 * Sets the Fraction, but returns an Integer, this saves time in further Calculations!	 */
	public IIntRing IntAt() {
		Numerator /= Denominator;	//TODO: find out how div rounds... ignore the Remainder
		Denominator = 1;
		return this; }

	/**@return the largest (closest to positive infinity) value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer.
	 *
	 * Returns an Integer, not a Fraction,
	 * this also saves time in further Calculations!	 */
	public IMetricIRing Floor() {
		return new FractionLong(Denominator / Numerator); }	//ignore the Remainder

	/**less: '<' Returns True, when 'Self' < arg	 */
	public boolean isLessThan (Object arg) {	//Works only for positive Denominators!!!
		if (arg instanceof FractionLong) {
			FractionLong arg_ = (FractionLong) arg;
			return Numerator  *arg_.Denominator <
			       Denominator*arg_.Numerator; }
		    return Numerator < Denominator * ByRefDouble.GET_DOUBLE(arg); }	//Integer Argument

	/**Returns the maximum Value for this Class.
	 * Returns +Infinity = 1/0 for Fractions.	 */
	public IWellOrder maxValueAt() {
		Numerator  = 1;
		Denominator= 0;
		return this; }

	/**Helper Routine to convert to Fraction from any other numeric Type:
	 * RingLong, Number or ICountAble.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
	private final FractionLong convertArg (Object arg) {
		return (arg instanceof FractionLong) ?
				   (FractionLong)arg :
				new FractionLong(arg); }

	//////////////////////////////
	//	interface intComplex	//
	//////////////////////////////

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() { return this; }

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar	 */
	public boolean isComplex() { return false; }

	//	interface Group, GroupM etc.	//

	/**Addition in Place: +=	 */
	public ISemiGroup  addAt(Object arg) { return addSubAt(arg,true ); }

	/**Subtraction in Place: -=	 */
	public IGroup     subAt(Object arg) { return addSubAt(arg,false); }

	/**Addition or Subtractin in Place: +=, -=	 */
	public IMetricIRing addSubAt(Object arg, boolean add) {	//watch out for Integer Arguments:
		if (arg == null) return this;
		if (arg instanceof FractionLong) {
			FractionLong arg_ = (FractionLong) arg;
			long H1 = Denominator * arg_.Numerator;
			Numerator   *= arg_.Denominator;
			if (add) Numerator += H1;	//Flag für Addition/Subtraktion
			else	 Numerator -= H1;
			Denominator *= arg_.Denominator;
		} else {	//Integer Argument
			if (add) Numerator += Denominator*ByRefLong.TO_LONG(arg);	//Flag für Addition/Subtraktion
			else	 Numerator -= Denominator*ByRefLong.TO_LONG(arg);
		}
		if (doSimplify) return shortenAt();
		return this; }

	/**Multiplication in Place: *=	 */
	public ISemiGroupM mulAt(Object arg) {
		if (arg == null) return this;
		if (arg instanceof FractionLong) {	//Check if the Argument is a scalar, before converting it to a Fraction
			FractionLong arg_ = (FractionLong) arg;
			Numerator   *= arg_.  Numerator;
			Denominator *= arg_.Denominator;
		} else {
			Numerator   *= ByRefLong.TO_LONG(arg); }	//Integer Argument
		if (doSimplify) return shortenAt();
		return this; }

	/**Division in Place: /=	 */
	public IGroupM divAt(Object arg) {
		if (arg == null) return this;
		if (arg instanceof FractionLong) {	//Check if the Argument is a scalar, before converting it to a Fraction
			FractionLong arg_ = (FractionLong) arg;
			Denominator *= arg_.  Numerator;
			Numerator   *= arg_.Denominator;
		} else {
			Denominator *= ByRefLong.TO_LONG(arg); }	//Integer Argument
		if (doSigning ) correctSignAt();	//This and invAt are the only Operation where this could happen.
		if (doSimplify) return shortenAt();
		return this; }

	/**Inversion in Place: 1/x	 */
	public IGroupM invAt() {
		long tmp = Numerator;
		Numerator   = Denominator;
		Denominator = tmp;
		if (doSigning ) correctSignAt();	//This and divAt are the only Operations where this could happen.
		return this; }

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public byte	  getByte() {
		long   ret = Numerator / Denominator;
		if ((ret > Byte.MAX_VALUE) ||
		    (ret < Byte.MIN_VALUE))
		    throw new OperationNotSupported();
		return (byte) ret; }

	/** Returns the Object Value represented by an 16 Bit Integer	 */
	public short getShort() {
		long   ret = Numerator / Denominator;
		if ((ret > Short.MAX_VALUE) ||
		    (ret < Short.MIN_VALUE))
		    throw new OperationNotSupported();
		return (short) ret; }

	/** Returns the Object Value represented by an 32 Bit Integer	 */
	public int	   getInt() {
		long   ret = Numerator / Denominator;
		if ((ret > Integer.MAX_VALUE) ||
		    (ret < Integer.MIN_VALUE))
		    throw new OperationNotSupported();
		return (int) ret; }

	/** Returns the Object Value represented by an 64 Bit Integer	 */
	public long   getLong() {
		long   ret = Numerator / Denominator;
		return ret; }

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() {
		return ((double) Numerator) / Denominator; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign
	 */
	public float   getFloat() {
		return ((float) Numerator) / Denominator; }

	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		return new FractionLong(); }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.	 */
	public ICopyAble copyAt(Object arg, int Depth) {
//		super.CopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		if (--Depth < 0) return this;
		FractionLong tmp = convertArg(arg);
		Denominator = tmp.Denominator;
		Numerator   = tmp.  Numerator;
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
	public int hashCode() { return (int) (Denominator + Numerator); }

	/**Decides, whether DisplayThreshold is used to determine,
	 * how the Fraction is formatted.	 */
	public static boolean bolDisplayThreshold;

	/**Decides, how the Fraction is formatted.
	 * When the Denominator is larger than this Threshold,
	 * the Fraction is converted to a Float.	 */
	public static long DisplayThreshold;

	public static String Starter = "(";
	public static String Stopper = ")";
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
		if (bolDisplayThreshold && (DisplayThreshold <= Math.abs(Denominator)))
//			return Numerator.toString();
			return String.valueOf(((double) Numerator) / Denominator);
			return Starter + Numerator + Separator + Denominator + Stopper; }

	/**Parses the String with the full Description to a Fraction Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(IDeserializer arg) throws java.io.IOException {
//		Numerator  .fromStreamAt(arg);	//TODO:
//		Denominator.fromStreamAt(arg);
		return this; }

	//////////////////////
	//	Optimizations:	//
	//////////////////////

	/** Negation in Place: -	 */
	public IGroup negAt	   () { Numerator = -Numerator; return this; }

	/** Double in Place: x+=x == x*=2	*/
	public ISemiGroup dblAt	   () { Numerator <<= 1; return this; }

	/** Triple in Place: x+=x+x == x*=3	*/
	public ISemiGroup trplAt	   () { Numerator  *= 3; return this; }

	/** Quadruple in Place: x+=x; x+=x  == x*=4 	*/
	public ISemiGroup quadAt	   () { Numerator <<= 2; return this; }

	public integer dec() { Numerator -= Denominator; return this; }
	public integer inc() { Numerator += Denominator; return this; }

	/**Residual in Place: 1-x	 */
	public integer ResidAt() {
		Numerator = Denominator - Numerator; return this; }

	public boolean negative() {
		if (doSigning)
			return Numerator < 0;
			return (Denominator != 0) &&
				   (Numerator   != 0) && (
				   (Numerator   <  0) !=
				   (Denominator <  0)); }
	public boolean positive() {
		if (doSigning)
			return Numerator > 0;
			return (Denominator != 0) &&
				   (Numerator   != 0) && (
				   (Numerator   <  0) !=
				   (Denominator <  0)); }

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm
	 */
	public IScalarMetric AbsVAt()  {
		Numerator   = Math.abs(Numerator);
		if (doSigning)
			return this;
		Denominator = Math.abs(Denominator);
			return this; }

	public IIntRing   twoAt() { Numerator = 2; Denominator = 1; return this; }
	public IIntRing threeAt() { Numerator = 3; Denominator = 1; return this; }
	public IIntRing  fourAt() { Numerator = 4; Denominator = 1; return this; }

	/**Half    in Place: x/=2	*/	public IIntRing    halfAt() { Denominator <<= 1; return this; }
	/**Third   in Place: x/=3	*/	public IIntRing   thirdAt() { Denominator  *= 3; return this; }
	/**Quarter in Place: x/=4	*/	public IIntRing quarterAt() { Denominator <<= 2; return this; }

	public ISemiGroupM sqrAt() {
		Numerator  *=  Numerator;	//This may incur some optimization
		Denominator*=Denominator;
		return shortRoundAt(); }	//No ggT for Numerator or Denominator => only shortening.

	public ISemiGroupM cbcAt() {
		Numerator  *= Numerator   * Numerator  ;	//This may incur some optimization
		Denominator*= Denominator * Denominator;
		return shortRoundAt();	} //No ggT for Numerator or Denominator => only rounding.

	/**Standard Implementation: a/b-c/d == 0
	 * Faster: a*d == c*b
	 * even faster: a == c && b == d
	 */
	public boolean equals(Object arg) {
		if (arg instanceof FractionLong) {
			FractionLong arg_ = (FractionLong) arg;
			return Numerator  *arg_.Denominator ==
				   Denominator*arg_.Numerator; }
			return Numerator == Denominator * ByRefLong.TO_LONG(arg); }	//Integer Argument

	//left out: even, isOdd, ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt


/*	public FractionLong MakeRational (double x) { //für schnellere Integer-Berechnungen
		IF Math.abs(x) > 1 {
			Denominator = MaxRational;
			Numerator   = Round (NumeratorZ/ABS (x));IF Negativ (x) THEN
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

	final static long Conversion = 1000000000;

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		Numerator   = (long)(Conversion*IMeasurAble.PI);
		Denominator =        Conversion;
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
		Nenner aus dem Quadrat (xý-2px+(pý+qý)) und der Zaehler aus dem
		Linearfaktor (A+Bx);hier wird zuerst A und dann B angegeben.

PROCEDURE PartialBruch (R1 : Rational;VAR N : cNullstelle;VAR P1 : Polynom;
                                      VAR Koeff : Vektor);
 Nenner1.Grad:=2;Nenner1.a^[2]:=Eins;
 P_DVN (R1.Zaehler,R1.Nenner,P1,R1.Zaehler);
 NullSuche (R1.Nenner,N);
 Z2:=Succ (R1.Zaehler.Grad);
 Kopiere (R1.Zaehler.a,b.Matrix,Z2*SizeOf (Real)); {Z„hler ?bertragen}
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
