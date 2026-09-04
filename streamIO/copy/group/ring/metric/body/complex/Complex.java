package streamIO.copy.group.ring.metric.body.complex;

import streamIO.IDeserializer;
import streamIO.copy.CCopyAble;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.AIntRing;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.integer;
import streamIO.copy.group.ring.metric.CMetricIRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.group.ring.metric.body.AMetricBody;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.derive.CCountAble;

//TODO: Optimization: Delegate ALL Operations to Value, not only the Basic ones!
//TODO: Optimization: Cache the Results of Tests like isZero() isOne() etc.

/**Concrete final Class to define Complex Numbers of arbitrary Types.
 * Complex Numbers from a Metric Body form a non-metric algebraic complete Body.
 *
 * Design Decisions:
 * Chose MetricIRing instead of AMetricBody as the Constituents,
 * because all basic operations are allowed on them,
 * but they can also contain basic Integer Types, and so allow for Optimizations.
 *
 * The Behavior of Complex Numbers can be controlled by boolean static Variables:
 * -A Complex will always be checked for zero imaginary Part after any Operation.
 *
 * The Methods cjg(), cjgAt() and isComplex() are all implemented in Integer.
 * The Methods mulAtCjg, mulCjg, divCjg, divAtCjg
 *		should maybe also be implemented there, so they can be called
 *		e.g. in Matrix Multiplication
 *		where they would save some negAt() Operations.
 *
 * This Class is practically completely copied from Interval!
 * Any change here should also be done in Interval!
 *
 * Making Real and Imag protected Variables
 * prevents direct Modification from the Outside.
 * Indirect Modification is not possible, if using Constants as Elements.
 * This makes it possible to create real Complex Constants!	 */
final public class Complex
extends AMetricBody
implements IMeasurAble {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**The real Part of the Complex Number.	 */
	protected IMetricIRing Real;	//Re

	/**The imaginary Part of the Complex Number.	 */
	protected IMetricIRing Imag;	//Im

	/**Local Object for consistent Argument Treatment in convertArg(), leads to Recursion!	 */
	protected Complex arg_; // = new Complex();

	/**Returns the real Part of the Complex Number.	 */
	public IMetricIRing Real(){return Real;}

	/**Returns the imaginary Part of the Complex Number.	 */
	public IMetricIRing Imag(){return Imag;}

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt(){ Imag.negAt();return this; }

	/**Overrides the false Value from the Implementation in absComplex	 */
	public boolean isComplex(){ return true; }

	/**		Constants
	 * The Problem is that they could be modified by an ...At() Operation!
	 * They must never be used like this!	 */

	//Using Complex here instead of CComplex,
	//to be able to use these like Complex Numbers.
	//Complex cannot be modified directly since Real and Imag are protected.
	//Their indirect Modification is covered by using Constants.
	//The Alternative would be to introduce an Interface Complex
	//and rewrite the Complex Class to only use that one
	//with a Performance Penalty!

	/**This Constant indicates the Factor i	 */
	final static public Complex  I = new Complex(	BodyDouble.Zero,
													BodyDouble. One);
//	final static public CComplex  I = new CComplex(new Complex(	BodyDouble.Zero,
//																		BodyDouble. One));

	/**This Constant indicates the Factor -i	 */
	final static public Complex _I = new Complex(	BodyDouble.Zero,
													BodyDouble._One);

	/**This Constant represents  0	 */
	final static public Complex Zero = new Complex(	BodyDouble.Zero,
													BodyDouble.Zero);

	/**This Constant represents  1	 */
	final static public Complex  One = new Complex(	BodyDouble. One,
													BodyDouble.Zero);

	/**This Constant represents -1	 */
	final static public Complex _One = new Complex(	BodyDouble._One,
													BodyDouble.Zero);

	/**Switches Checking for real Results on or off.
	 * Since you can not expect to be a result real, it is typically switched on. 	 */
//	public static boolean bolLazySimplify = true; //false;


	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public Complex(Complex arg)	{	//copyAt(arg);	//...the same, only faster:
		Real = (IMetricIRing) arg.Real.copy();
		Imag = (IMetricIRing) arg.Imag.copy(); }

	/**Constructor that takes Constants as Input for real and imaginary Part.
	 * Those Objects should be of the same Type to speed up calculation
	 * or even enabling it (when e.g. one is not countable).
	 * This prevents the indirect Change of the Real and Imaginary Part,
	 * but that is due to Performance Reasons.
	 * Don't make Copies of these Elements,
	 * since these Copies are no Constants anymore!	 */
	public Complex(CMetricIRing Real_, CMetricIRing Imag_) {	//
		Real = Real_;
		Imag = Imag_; }

	/**Constructor that takes any Object as Input for real and imaginary Part.
	 * Those Objects should be of the same Type to speed up calculation
	 * or even enabling it (when e.g. one is not countable).	 */
	public Complex(Object Real_, Object Imag_) {
		boolean ReC;
		boolean ImC;
		Object buf = null;
		if (ReC = Real_ instanceof Complex) {
			buf = Imag_;
			Imag_ = ((Complex) Real_).Imag;
			Real_ = ((Complex) Real_).Real; }
		if (ImC = Imag_ instanceof Complex)	{
			buf = Real_;
			Real_ = ((Complex) Imag_).Real;
			Imag_ = ((Complex) Imag_).Imag; }
		Real = (IMetricIRing) ((ICopyAble)Real_).copy();
		Imag = (IMetricIRing) ((ICopyAble)Imag_).copy();
		if (ReC & ImC) throw new AbstractMethodError();
		if (ReC) Imag.addAt(buf);
		if (ImC) Real.addAt(buf); }

	/**Converts to rectangular Coordinates,
	 * in which Addition and Subtraction are easier to calculate.	 */
	public Complex(Polar arg) {
		Imag = (IMetricIRing) arg.r.newInstance();
		Real = (IMetricIRing)
				((MetricBody)arg.ang).Cos_Sin(((MetricBody)	Imag)).mulAt(arg.r);
															Imag  .mulAt(arg.r); }

	/**Constructor that takes any Object as Input for the real Part.
	 * The imaginary Part is set to 0.	 */
	public Complex(Object arg) {
		if (arg instanceof Complex) { //this((Complex) arg);
			Real = (IMetricIRing) ((Complex) arg).Real.copy();
			Imag = (IMetricIRing) ((Complex) arg).Imag.copy(); 
		} else if (arg instanceof ICopyAble) {
			Real = (IMetricIRing) ((ICopyAble) arg).copy();
			Imag = (IMetricIRing) Real.zero(); 	//Choose the same type -> faster
		}else {
			Real = new BodyDouble(ByRefDouble.GET_DOUBLE(arg));
			Imag = new BodyDouble(ICountAble.ZERO); 	//Choose the same type -> faster
		}
	}

	/**Constructor that takes a double as Input for the real Part.
	 * The imaginary Part is set to 0.
	 * The Types are defaulted to BodyDouble.	 */
	public Complex(double Real_) {
		Real = new BodyDouble(Real_);
		Imag = (IMetricIRing) Real.zero();	//Choose the same type -> faster
	}

	/**Constructor that takes a double as Input for the real Part.
	 * The imaginary Part is set to 0.
	 * The Types are defaulted to BodyDouble.	 */
	public Complex(double Real_, double Imag_) {
		Real = new BodyDouble(Real_);
		Imag = new BodyDouble(Imag_); }

	/**Constructor that takes an int as Input for the real Part.
	 * The imaginary Part is set to 0.	 */
/*	public Complex(int Real_) {
		Real = new RingLong(Real_);
		Imag = (MetricIRing) Real.zero();	//Choose the same type -> faster
	}
*/
	/**TODO: incorporate this,
	 * although it is very unlikely to calculate with complex Integers.	 */

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.
	 * This Constructor must not be public,
	 * because the Type of it's Parts must be defined!	 */
	protected Complex()	{
//		BaseAccuracyInv = SqRtMaxValue();
//		BaseAccuracy = BaseAccuracyInv.inv();
//		Real = new BodyDouble();
//		Imag = new BodyDouble();
	}

	/**Setting to 0 in Place:	 */
	public IGroup zeroAt() {
		Real.zeroAt();
		Imag.zeroAt();
		return this; }

	/**Setting to 1 in Place:	 */
	public IGroupM oneAt() {
		Real.oneAt ();
		Imag.zeroAt();
		return this; }

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() {
		((MetricBody)Real).piAt();
					 Imag .zeroAt();
		return this; }

	/**Testing for 0:	 */
	public boolean isZero(){return Real.isZero() &&
								   Imag.isZero(); }

	/**Setting to i:	 */
	public Complex i(){return ((Complex)newInstance()).iAt();}

	/**Setting to i in Place:	 */
	public Complex iAt() {
	    Real.zeroAt();
	    Imag. oneAt();
		return this; }

	//////////////////////////////////////
	//	Multiplication / Division by i	//
	//////////////////////////////////////

	/**Multiplies the Complex Number by i or divides it by -i:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90°	 */
	public IIntRing MulI(){return (Complex)((Complex)copy()).mulIAt();}

	/**Divides the Complex Number by i or multiplies it by -i:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90°	 */
	public IIntRing DivI(){return (Complex)((Complex)copy()).divIAt();}

	/**Divides the Complex Number by i or multiplies it by -i in Place:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90°	 */
	public IIntRing divIAt()
	{IMetricIRing tmp = Real; Real = Imag; Imag = (IMetricIRing) tmp.negAt(); return this;}

	/**Multiplies the Complex Number by i or divides it by -i in Place:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90°	 */
	public IIntRing mulIAt()
	{IMetricIRing tmp = Imag; Imag = Real; Real = (IMetricIRing) tmp.negAt(); return this;}

	//

	/**Returns the 'arg',
	 * i.e. the angle of the corresponding Polar Representation	 */
	public IMetricIRing arg(){return this;}    //uses ArcTg

	/**Converts to polar Coordinates,
	 * in which exponential Functions and Potency are easier to calculate.	 */
//	public Polar Polar(){return new Polar(this);}

	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt() {
		Real.FloorAt();
		Imag.FloorAt();
		return this; }

	/**Returns the integer Part of the Value in Place. 	 */
	public IIntRing IntAt() {
		Real.IntAt();
		Imag.IntAt();
		return this; }

	/**less: '<' Returns True, when 'Self' < arg
	 * By now: (a.re < b.re) && (a.im < b.im)
	 * Better: MaxMetric: max(a.re,a.im) < max(b.re,b.im)	 */
	public boolean isLessThan (Object arg) {
		convertArg(arg);
		return Real.Max(Imag).isLessThan(arg_.Real.Max(arg_.Imag)); }

	/**Returns +Infinity = 1/0	 */
	public IWellOrder maxValueAt() {
		Real.maxValueAt();
		Imag.maxValueAt();
		return this; }


	//////////////////////////////
	//	Arithmetic Operations	//
	//////////////////////////////

	/**Helper Routine to convert to Complex from any other numeric Type:
	 * RingLong, Number or countable.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.	 */
	private final void	  convertArg (final Object arg) {
		if (arg_ == null) {
			arg_ = new Complex(null, null); }
		if (arg instanceof Complex) {
			arg_.Real = ((Complex) arg).Real;
			arg_.Imag = ((Complex) arg).Imag;
		}else{
			arg_.Real = (IMetricIRing) arg;
			arg_.Imag = null;}
	}

	/**Counter for the Optimizations	 */
	public static int AddOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int AddOperations = 0;


	/**Counter for the Optimizations	 */
	public static int MulOptimizations = 0;

	/**Counter for the normal Operations	 */
	public static int MulOperations = 0;


	/**Addition in Place: +=
	 * assumes null to be 0	 */
	public ISemiGroup  addAt(Object arg)	{
//		++AddOperations;
//		++AddOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg == Complex.Zero) ||
			(arg == null) ) return this;
		if (arg instanceof Complex) {
			Real.addAt(((Complex)arg).Real);
			Imag.addAt(((Complex)arg).Imag);
			// check, if it is a real Result
			return this; }
		if (arg instanceof Polar) {
			addAt(new Complex(((Polar)arg)));
			return this; }
		Real.addAt(arg);	//real Argument
//		--AddOptimizations;
		return this; }

	/**Subtraction in Place: -=
	 * assumes null to be 0	 */
	public IGroup     subAt(Object arg) {
//		++AddOperations;
//		++AddOptimizations;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg == Complex.Zero) ||
			(arg == null) ) return this;
		if (arg instanceof Complex) {
			Real.subAt(((Complex)arg).Real);
			Imag.subAt(((Complex)arg).Imag);
			//TODO: check, if it is a real Result
			return this; }
		if (arg instanceof Polar) {
			subAt(new Complex(((Polar)arg)));
			return this; }
		Real.subAt(arg); //real Argument
//		--AddOptimizations;
		return this; }

	/**Multiplication in Place: *=
	 * assumes null to be 1	 */
	public ISemiGroupM mulAt(Object arg) {
		//Check if the Argument is a scalar, before converting it to a Complex
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble. One) ||
			(arg == CCountAble. One) ||
			(arg == BodyDouble. One) ||
			(arg ==    Complex. One)) return this;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg ==    Complex.Zero) ||
			(arg == null) ) { zeroAt(); return this; }
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One) ||
			(arg ==    Complex._One)) { negAt(); return this; }
		if  (arg ==  I) { mulIAt(); return this; }
		if  (arg == _I) { divIAt(); return this; }
		if  (arg instanceof Complex) {
			Complex arg_ = (Complex) arg;
			Object tmp = Real.copy();
			Real.mulAt(arg_.Real); Real.subAt (arg_.Imag.mul(Imag));
			Imag.mulAt(arg_.Real); Imag. addAt (arg_.Imag.mul(tmp ));
			// check, if it is a real Result
		} else {	//Real Argument
			Real.mulAt(arg);
			Imag.mulAt(arg); }
//		--MulOptimizations;
		return this; }

	/**Division in Place: /=
	 * assumes null to be 1
	 * obige Implementation vermeidet Genauigkeitsverlust und einen Überlauf durch die Quadrierung
	 * und spart außerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IGroupM divAt(Object arg) {
//		++MulOperations;
//		++MulOptimizations;
		if ((arg ==  ICountAble. One) ||
			(arg == CCountAble. One) ||
			(arg == BodyDouble. One) ||
			(arg ==    Complex. One)) return this;
		if ((arg ==  ICountAble.Zero) ||
			(arg == CCountAble.Zero) ||
			(arg == BodyDouble.Zero) ||
			(arg ==    Complex.Zero) ||
			(arg == null) ) { InfinityAt(); return this; }
		if ((arg ==  ICountAble._One) ||
			(arg == CCountAble._One) ||
			(arg == BodyDouble._One) ||
			(arg ==    Complex._One)) { negAt(); return this; }
		if  (arg ==  I) return (IGroupM) divIAt();
		if  (arg == _I) return (IGroupM) mulIAt();
		if (arg instanceof Complex) {
			if (!(bolLazySimplify && ((Complex)arg).Imag.isZero())) {
				IGroupM Skalar = ((Complex)arg).Real.div(		     ((Complex)arg).Imag );    //|Skalar| < 1 ?
				Object Faktor = ((Complex)arg).Imag.add(Skalar.mul(((Complex)arg).Real));
				Object Helfer = Imag.copy();
				Imag.mulAt(Skalar); Imag.subAt(Real  ); Imag.divAt (Faktor);
				Real.mulAt(Skalar); Real. addAt(Helfer); Real.divAt (Faktor);
				if (!bolLazySimplify && Imag.isZero())  // check, if it is a real Result:
					return (IGroupM) Real;
				else;
			} else {	//Real Argument, no real Result
				Real.divAt(((Complex)arg).Real);
				Imag.divAt(((Complex)arg).Real); }
		} else {	//Real Argument, no real Result
			Real.divAt(arg);
			Imag.divAt(arg); }
//		--MulOptimizations;
		return this; }

	//These two implementations avoid the explicit Conjugation of the second argument
	//and thus save an additional call, negation and saving Operation.
	//They could be used in Matrix Operations
	//instead of the explicit Conjugation of the Tensor's Elements.
	//although it would be generally easier if the Conjugation took place once
	//and consciously chosen by the Programmer instead of implicitly.

	/**Addition of the conjugate complex argument in Place: +=	 */
	public IIntRing addAtCjg(Object arg) {
		//Check if the Argument is a scalar, before converting it to a Complex
		if (arg instanceof Complex) {
			 Complex tmp = (Complex) arg;
			 Real.addAt(tmp.Real);
			 Imag.subAt(tmp.Imag);	} // check, if it is a real Result
		else Real.addAt(arg);	//Real Argument
		return this; }

	/**Subtraction of the conjugate complex argument in Place: -=	 */
	public IIntRing subAtCjg(Object arg) {
		//Check if the Argument is a scalar, before converting it to a Complex
		if (arg instanceof Complex) {
			 Complex tmp = (Complex) arg;
			 Real.subAt(tmp.Real);
			 Imag. addAt(tmp.Imag);	} // check, if it is a real Result
		else Real.subAt(arg);	//Real Argument
		return this; }

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulAtCjg(Object arg) {	//Check if the Argument is a scalar, before converting it to a Complex
		if (arg instanceof Complex)	{
			Object  tmp  = Real.copy();
			Complex arg_ = (Complex) arg;
			Real.mulAt(arg_.Real); Real. addAt (arg_.Imag.mul(Imag));
			Imag.mulAt(arg_.Real); Imag.subAt (arg_.Imag.mul(tmp ));
			// check, if it is a real Result
		} else { 	//Real Argument
			Real.mulAt(arg);
			Imag.mulAt(arg); }
		return this; }

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen Überlauf durch die Quadrierung
	 * und spart außerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divAtCjg(Object arg) {
		if (arg instanceof Complex) {
			Complex arg_ = (Complex) arg;
			if (!(bolLazySimplify && ((Complex)arg).Imag.isZero())) {
				IGroupM Skalar = arg_.Real.div(			 arg_.Imag );    //|Skalar| < 1 ?
				Object Faktor = arg_.Imag.add(Skalar.mul(arg_.Real));
				Object Helfer = Imag.copy();
				Imag.mulAt(Skalar); Imag. addAt(Real  ); Imag.divAt(Faktor);
				Real.mulAt(Skalar); Real.subAt(Helfer); Real.divAt(Faktor);
//				if (!bolLazySimplify && Imag.isZero())  // check, if it is a real Result:
//					return (GroupM) Real;
//				else;
			} else {	//Real Argument, no real Result
				Real.divAt(arg_.Real);
				Imag.divAt(arg_.Real); }
		} else { 	//Real Argument, no real Result
			Real.divAt(arg);
			Imag.divAt(arg); }
		return this; }

	/**Inversion in Place: 1/x
	 * obige Implementation ist genauer und verhindert einen Überlauf, ersetzt aber effektiv
	 * 1 Division durch eine Inversion und 1 Vergleich	 */
	public IGroupM invAt() {		//loses the Reference to 'Real', because it doesn't use copyAt
		if (!(bolLazySimplify && Imag.isZero())) {
			IGroupM Skalar = Real.div(Imag);
			Imag.addAt(Real.mul(Skalar)); Imag.invAt(); Imag.negAt();
			Real = (IMetricIRing)Skalar; Real.mulAt(Imag); Real.negAt();
			return this; } 	// no check, if it is a real Result! not possible.
			return Real.invAt(); }


	//virtual Methods of Object

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.
	 * NewInstance also clones the Types, but does not initialize them!
	 * When overriding, use newInstance on all Components.	 */
	public ICopyAble newInstance() {
		return new Complex( Real.newInstance(),
							Imag.newInstance()); }

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.
	 * When overriding, use copyAt on all Constituents.
	 * It keeps the current reference, so any Argument has to be converted to Complex!	 */
	public ICopyAble copyAt(Object arg, int Depth) {
		//DeepCopy, ripples through, by using copyAt() on all Elements.
//		super.CopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		Imag.copyAt(arg_.Imag, --Depth);	//Imag = (Complex) arg_.Imag.copy();	//Doesn't matter,
		Real.copyAt(arg_.Real,   Depth);	//Real = (Complex) arg_.Real.copy();	//only for Performance!
		return this;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg) {
		//don't rely on the Argument being a Complex
//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		convertArg(arg);
		if ((Real instanceof CCopyAble) ||
			(Imag instanceof CCopyAble)) throw new AbstractMethodError(CCopyAble.strConst);
		Real = arg_.Real;
		Imag = arg_.Imag;
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
	public int hashCode() { return Imag.hashCode() ^ Real.hashCode(); }

	/**Separator String, see also in absCopyAble.Separator	 */
	public static String Separator = ";";

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
	public String toString() {
		return "(" + Real + ", " + Imag + ")";}
//		return Starter + Real.toString() + Separator + Imag.toString() + Stopper;}

	/**Parses the String with the full Description to a Complex Number.
	 * of two Object as Input for Denominator and Numerator.
	 * Those Objects should be of the same Type to speed up calculation. */
	public ICopyAble fromStreamAt(IDeserializer arg)
		throws java.io.IOException {
		Real.fromStreamAt(arg);
		Imag.fromStreamAt(arg);
/*		String[] List = parseList(arg);
		Real.fromStringAt(List[0]);
		Imag.fromStringAt(List[1]);
*/		return this;
	}


	//////////////////////
	//	Optimizations:	//
	//////////////////////

	//////////////////////
	//	Optimizations	//
	//////////////////////

	/**Returns -1:	*/	public IIntRing _one() {return _One;};
	/**Returns  0:	*/	public IGroup		 zero() {return Zero;};
	/**Returns  1:	*/	public IGroupM		  one() {return  One;};
//	/**Returns  2:	*/	public IIntRing  two() {return  Two;};

	/**Negation in Place: -	 */
	public IGroup negAt	   (){Real.negAt(); Imag.negAt(); return this;}

	/**Double in Place: x+=x == x*=2	*/
	public ISemiGroup dblAt (){Real.dblAt(); Imag.dblAt(); return this;}

	/**Triple in Place: x+=x+x == x*=3	*/
	public ISemiGroup trplAt(){Real.trplAt(); Imag.trplAt(); return this;}

	/**Decrements the Value by 1	 */
	public integer dec(){Real.dec(); return this;}

	/**Inrements the Value by 1	 */
	public integer inc(){Real.inc(); return this;}

	/**Residual in Place: 1-x	 */
	public integer ResidAt(){Real.ResidAt(); Imag.negAt(); return this;}

	//Only the real part counts, this is at least valid for the Calculation of ArcTan

	public boolean negative(){return (Real.negative());}// && Imag.negative());}
	public boolean positive(){return (Real.positive());}// && Imag.positive());}

	public IIntRing twoAt()  {Real.twoAt  (); Imag.zeroAt(); return this;}
	public IIntRing threeAt(){Real.threeAt(); Imag.zeroAt(); return this;}

	/**Half in Place: x/=2	*/
	public IIntRing halfAt (){Real.halfAt (); Imag.halfAt ();return this;}

	/**Third in Place: x/=3	*/
	public IIntRing thirdAt(){Real.thirdAt(); Imag.thirdAt();return this;}

	/**Square in Place: x^2 == x*=x
	 * (a+ib)^2 == a^2 + 2i ab - b^2 == (a+b)(a-b) + 2i(ab)	 */
	public ISemiGroupM sqrAt()
	{
		IMetricIRing Hilf = Real;    //no copy, because the value stays the same
		Real =   (IMetricIRing)
				((IMetricIRing) Real.sub(Imag)).mulAt(Real.add(Imag));
				((IMetricIRing) Imag.mulAt(Hilf)).dblAt();
		return this;	//No ggT for Real or Imag => only rounding.
	}

	/**Cubic in Place: x*=x^3
	 * (a+ib)^3 == a(a^2-3b^2)+ib(3a^2-b^2)	 */
	public ISemiGroupM cbcAt()
	{
		IMetricIRing reSqr = (IMetricIRing) Real.sqr();
		IMetricIRing imSqr = (IMetricIRing) Imag.sqr();
		Real.mulAt (				 reSqr			.sub  (imSqr.trpl()));
		Imag.mulAt (((IMetricIRing)reSqr.trplAt()).subAt(imSqr));
		return this;
	}

	/**Standard Implementation: a+ib-c+id == 0
	 * Faster: a == c && b == d
	 * but less correct when it comes to rounding!	 */
	public boolean equals(Object arg)
	{
		if (arg instanceof Complex)
			return Real.equals(((Complex)arg).Real) &&
				   Imag.equals(((Complex)arg).Imag);
		if (! Imag.isZero()) return false;
		return Real.equals(arg);
	}

	/**Checks if the Complex Number is even.
	 * i.e. both real and imaginary part are even	 */
	public boolean isEven() {return Real.isEven() && Imag.isEven();}

	/**Checks if the Complex Number is odd.
	 * i.e. both real and imaginary part are odd	 */
	public boolean isOdd () {return Real.isOdd () && Imag.isOdd ();}

	//left out: ModAtDivAt, ModlAt, kgV, ggT, IntAt == FloorAt

	/**Returns the Square Root of this: x^=.5	*/
	public IMetricIRing SqRt(){return ((IMetricIRing) copy()).SqRtAt();}

	/**Returns the Square Root of this in Place: x^=.5	*/
	public IMetricIRing SqRtAt() {
		if (Imag.isZero()) { 	//this case has to be considered separately!
			if (!Real.negative()) Real.SqRtAt();	//positive => real Root
			else
			{Imag.copyAt(Real.negAt()); Imag.SqRtAt(); Real.zeroAt();}	//negative => imag Root
			return this; }
		boolean rNeg; IMetricIRing f = (IMetricIRing)Real.half(); if (rNeg = Real.negative()) f.negAt();
		boolean iNeg; IMetricIRing g = (IMetricIRing)Imag.half(); if (iNeg = Imag.negative()) g.negAt();
		if (f.isLessThan(g)) {	//f and g are the absolute half Values
			g.divAt(f).sqrAt(); g.inc(); g.SqRtAt().inc(); f.mulAt(g); f.SqRtAt();
		} else {
			f.divAt(g); IMetricIRing tmp = f;
			f = (IMetricIRing) f.sqr();
			f.inc(); f.SqRtAt(); f.addAt(tmp); f.mulAt(g); f.SqRtAt(); }
		if (rNeg) //wählt immer die Lösung mit der kleinsten Phase aus !
		if (iNeg) {
			f.negAt();
		    Real = Imag; Real.divAt(f.dbl());
			Imag = f;
		} else {
		    Real = Imag; Real.divAt(f.dbl());
			Imag = f;
		} else {
		    Imag.divAt(f.dbl());
			Real = f; }
		return this; }

/*	Public Function cCbcRtAt() As clsComplex
    'uses the complex Exponential and Logarithmic Functions
'    Self = Pol(Self)
'    Self.re = Exp(Ln(Self.re) / Drei)
'    Self.im = Self.im / Drei
'    cCbcRt = Rec(Self)
	End Function
*/

	/**p-Norm: Defined as Sum(|x|^p)^1/p
	 * Generic Norm: the other Norms are Special Cases:
	 * In 1-dimensional Spaces all Norms fall together.	 */
	public IMetricIRing p_Norm (double p) {
		Double p_ = new Double(p);
		return	(IMetricIRing)
				 ((MetricBody)
					((MetricBody)Real.AbsV()).Pow(p_).addAt (
					((MetricBody)Imag.AbsV()).Pow(p_))).PowAt(new Double(ICountAble.ONE/p));
	}

	/**Maximums-Norm
	 * Special Case of the p-Norm for p -> Infinity	 */
	public IMetricIRing Max_Norm () {
		IMetricIRing r = (IMetricIRing)Real.AbsV();
		IMetricIRing i = (IMetricIRing)Imag.AbsV();
		return (r.isLessThan(i) ? i :r); }

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm()
	{return (IMetricIRing)Real.SqrNorm().addAt(Imag.SqrNorm());}

	/**absolute Value in Place:				 |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsVAt() {	//((CopyAble)Real).ShallowCopyAt(AbsV());
		((IGroup) Real.AbsVAt()).addAt(Imag.AbsVAt());
		Imag.zeroAt();	//still keep this a sensible Representation!
		return (IScalarMetric)Real;
//		return this;
	}

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsV()//{return (Group) ((MetricIRing)SqrAbsV()).SqRt();}
	{return (IScalarMetric)((IGroup)Real.AbsV()).addAt(Imag.AbsV());}

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry(){}

	//Complement, necessary for gAdic Calculation
	/**Complement in Place: ~=	*/	public IIntRing CmplAt(){throw new AbstractMethodError();}

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt(){throw new AbstractMethodError();}

	/**Chordaler Betrag: 0 < x < 1 streng monoton steigend.
	 * Der Chordale Betrag gibt die Höhe des Punktes
	 * auf der Riemannschen Zahlenkugel an.	 */
	public AIntRing chordal() {
		AIntRing tmp = (AIntRing) Norm();
		return (AIntRing) tmp.divAt(tmp.succ());
//		return AbsV().invAt().succ().invAt();
	}

	//////////////////////////////////
	//	Analytical Optimizations	//
	//////////////////////////////////

	/**Returns the exponential Function: e^x
	 * This is the Inverse to the natural Logarithm ln().	 */
	public MetricBody exp() {
		Polar p = new Polar ((IMetricIRing)((MetricBody)Real).exp(), Imag);
		return new Complex(p); }

	/**Returns the natural Logarithm of x: ln(x)
	 * This is the Inverse to the exponential Function exp(x).
	 * For Arguments x near 1 use lnXP1(x) to gain Accuracy.	 */
	public MetricBody ln() {
		Polar p = new Polar (this);
		return new Complex(((MetricBody)p.r).lnAt(),p.ang); }

	/**Returns the Sinus of the angle x: sin(x)	 */
	public MetricBody sin() {
		if (Imag.isZero()) return ((MetricBody) Real).sin();
		if (Real.isZero()) return new Complex(Real.zero(), ((MetricBody) Imag).SinH());
		Complex Sin =  new Complex();
		MetricBody S  = (MetricBody)Real.newInstance();
		MetricBody SH = (MetricBody)Real.newInstance();
		Sin.Imag = (IMetricIRing)((MetricBody)Real).Cos_Sin (S);
		Sin.Real = (IMetricIRing)((MetricBody)Imag).CosH_SinH (SH);
		Sin.Real.mulAt(S );	//im = cos (re)*sinh (im)
		Sin.Imag.mulAt(SH);	//re = sin (re)*cosh (im)
		return Sin; }

	/**Returns the Cosinus of the angle x: cos(x)	 */
	public MetricBody cos() {
		if (Imag.isZero()) return ((MetricBody) Real).cos();
		if (Real.isZero()) return ((MetricBody) Imag).CosH();
		Complex Cos =  new Complex();
		Cos.Imag		= (IMetricIRing)Real.newInstance();
		MetricBody CH	= (MetricBody) Real.newInstance();
		MetricBody SH	= (MetricBody) Real.newInstance();
		CH			=				((MetricBody)Imag).CosH_SinH (SH);
		Cos.Real	= (IMetricIRing)	((MetricBody)Real).Cos_Sin (Cos.Imag);
		((MetricBody)	Cos.Imag.mulAt(SH)).negAt();	//im =-sin (re)*sinh (im)
						Cos.Real.mulAt(CH);			//re = cos (re)*cosh (im)
		return Cos; }

	/**Returns the Sinus Hyperbolicus of this number: SinH(x)	 */
	public MetricBody SinH() {
		if (Imag.isZero()) return ((MetricBody) Real).SinH();
		if (Real.isZero()) return new Complex(Real.zero(), ((MetricBody) Imag).sin());
		Complex Sin =  new Complex();
		MetricBody S  = (MetricBody)Real.newInstance();
		MetricBody SH = (MetricBody)Real.newInstance();
		Sin.Imag = (IMetricIRing)((MetricBody)Real).CosH_SinH (SH);	//im = CosH (re)*sin (im)
		Sin.Real = (IMetricIRing)((MetricBody)Imag).Cos_Sin (S);		//re = SinH (re)*cos (im)
		Sin.Imag.mulAt(S);
		Sin.Real.mulAt(SH);
		return Sin; }

	/**Returns the Cosinus Hyperbolicus of this number: CosH(x)	 */
	public MetricBody CosH() {
		if (Imag.isZero()) return ((MetricBody) Real).CosH();
		if (Real.isZero()) return ((MetricBody) Imag).cos();
		Complex Cos =  new Complex();
		Cos.Imag		= (IMetricIRing)Real.newInstance();
		MetricBody CH	= (MetricBody) Real.newInstance();
		MetricBody SH	= (MetricBody) Real.newInstance();
		CH			=				((MetricBody)Imag).Cos_Sin (SH);
		Cos.Real	= (IMetricIRing)	((MetricBody)Real).CosH_SinH (Cos.Imag);
		Cos.Imag.mulAt(SH);	//im = SinH (re)*sin (im)
		Cos.Real.mulAt(CH);	//re = CosH (re)*cos (im)
		return Cos; }

	/**Returns the Tangens of the angle x: tan == sin / cos == sin/(1-sin^2)^1/2	*/
	public MetricBody tan() {
		if (Imag.isZero()) return ((MetricBody) Real).tan();
		if (Real.isZero()) return new Complex(Real.zero(), ((MetricBody) Imag).TanH());
		Complex Tan =  (Complex)dbl();
		MetricBody CH;
		CH	=	 ((MetricBody)Tan.Imag).CosH_SinH	(Tan.Imag);	//im = SinH(2im)/(cos (2re)+cosH (2im))
		CH.addAt(((MetricBody)Tan.Real).Cos_Sin		(Tan.Real));//re = sin (2re)/(cos (2re)+cosH (2im))
		Tan.divAt(CH);
		return Tan; }

	/**Returns the Tangens Hyperbolicus of this Number: TanH	*/
	public MetricBody TanH() {
		if (Imag.isZero()) return ((MetricBody) Real).TanH();
		if (Real.isZero()) return new Complex(Real.zero(), ((MetricBody) Imag).tan());
		Complex TanH =  (Complex)dbl();
		MetricBody C;
		C	=	((MetricBody)TanH.Imag).Cos_Sin	 (TanH.Imag);	//im = sin (2im)/(CosH(2re)+cos(2im))
		C.addAt(((MetricBody)TanH.Real).CosH_SinH(TanH.Real));	//re = SinH(2re)/(CosH(2re)+cos(2im))
		TanH.divAt(C);
		return TanH;
/*		Complex TanH =  new Complex();	//Gives the same Results, only slower!
		MetricBody C  = (MetricBody)Real.newInstance();
		MetricBody CH = (MetricBody)Real.newInstance();
		MetricBody S  = (MetricBody)Real.newInstance();
		MetricBody SH = (MetricBody)Real.newInstance();
		MetricBody H  = (MetricBody)Real.newInstance();
		MetricBody F  = (MetricBody)Real.newInstance();
		CH = ((MetricBody)Real).CosH_SinH (SH);
		C  = ((MetricBody)Imag).Cos_Sin   (S );
		H = (MetricBody)SH.div(CH); F = (MetricBody)C.sqr();
		TanH.Real = (MetricIRing)
					 ((MetricBody)
					  ((MetricBody)F.div(H)).subt(H.mul(F.pred()))).invAt();
		TanH.Imag = (MetricIRing)
					 ((MetricBody)
					  ((MetricBody)TanH.Real.mul(C).mul(S))).div(CH.mul(SH));
		return TanH;
*/	}

	/**Returns the Arcus Tangens of the Angle x: ArcTan(x)
	 * = -i/2*Ln ((1+i*c1)/(1-i*c1))*/
	public MetricBody ArcTan() {
		if (Imag.isZero()) return ((MetricBody) Real).ArcTan();
		if (Real.isZero() && (AbsV().notMoreThan(one())))
			return new Complex(Real.zero(), ((MetricBody) Imag).ArTanH());	//only for |x| <= 1
		Complex xm1 = new Complex (Imag.succ()	, Real.neg());
		Complex xp1 = new Complex (Imag.Resid()	, Real);
		((MetricBody)xp1.divAt(xm1)).lnAt().halfAt();
		xm1.Real = xp1.Imag;
		xm1.Imag = xp1.Real; xm1.Imag.negAt();
		return xm1; }

	/**Returns the Arcus Sinus of the Angle x: ArcSin(x)	 */
	public MetricBody ArcSin() {
		if (Imag.isZero() && (AbsV().notMoreThan(one()))) return ((MetricBody) Real).ArcSin();
		if (Real.isZero()) return new Complex(Real.zero(), ((MetricBody) Imag).ArSinH());
		return super.ArcSin(); }

	/**Returns the Arcus Cosinus of the Angle x: ArcCos(x)	 */
/*	public MetricBody ArcCos()//{return ((MetricBody)copy()).ArcCosAt();}
	{//no sense to define the Simplifications here, since the Definition and Value Range of Sin and Cos are restricted with real Numbers
		if (Imag.isZero()) return ((MetricBody) Real).ArcCos();
		if (Real.isZero()) return ((MetricBody) Imag).ArCosH();
		return super.ArcCos(); }
	*/

	/**Returns both the Sinus and Cosinus: Cos, Sin
	 * This is more efficient, because cos^2+sin^2 = 1	 */
	public MetricBody Cos_Sin(MetricBody Sin) {
		Complex Cos =  new Complex();
		MetricBody S  = (MetricBody)Real.newInstance();
		MetricBody SH = (MetricBody)Real.newInstance();
		((Complex)	Sin).Imag.copyAt(((MetricBody)Real).Cos_Sin (S));		//im = cos (re)*sinh (im)
		((Complex)	Sin).Real.copyAt(((MetricBody)Imag).CosH_SinH (SH));	//re = sin (re)*cosh (im)
					Cos .Imag = (IMetricIRing)((MetricBody)S.mul(SH)).negAt();			//im =-sin (re)*sinh (im)
					Cos .Real = (IMetricIRing)((Complex)Sin).Imag.
							mul(((Complex)Sin).Real);		//re = cos (re)*cosh (im)
		((Complex)	Sin).Imag.mulAt(SH);
		((Complex)	Sin).Real.mulAt(S);
		return Cos; }

	/**Returns both the Sinus and Cosinus Hyperbolicus: CosH, SinH
	 * This is more efficient, because cosH^2-sinH^2=1	 */
	public MetricBody CosH_SinH(Object SinH) {
		Complex CosH =  new Complex();
		MetricBody S  = (MetricBody)Real.newInstance();
		MetricBody SH = (MetricBody)Real.newInstance();
		((Complex)	SinH).Imag.copyAt(((MetricBody)Real).CosH_SinH (SH));	//im = cosh (re)*sin (im)
		((Complex)	SinH).Real.copyAt(((MetricBody)Imag).Cos_Sin (S));		//re = sinh (re)*cos (im)
					CosH .Imag = (IMetricIRing)SH.mul(S);					//im = sinh (re)*sin (im)
					CosH .Real = (IMetricIRing)
								((Complex)SinH).Imag.
							mul(((Complex)SinH).Real);		//re = cosh (re)*cos (im)
		((Complex)SinH).Imag.mulAt(S);
		((Complex)SinH).Real.mulAt(SH);
		return CosH; }

	//Taken out, because implemented by Norm now.

	/**Square of the absolute Value in Place: |x|^2		 */
//	public SemiGroupM SqrAbsVAt(){return (SemiGroupM)ShallowCopyAt(SqrAbsV());}

	/**Square of the absolute Value: |x|^2		 */
//	public SemiGroupM SqrAbsV()
//	{return (SemiGroupM) ((MetricIRing) Real.SqrAbsV()).addAt(Imag.SqrAbsV());}


	//////////////////////////////
	//	Interface countable		//
	//////////////////////////////

	//Taken out, because Information gets lost!

	/** Returns the Object Value represented by an 8 Bit Integer	 */
//	public byte   getByte(){return Real.getByte();}

	/** Returns the Object Value represented by an 16 Bit Integer	 */
//	public short getShort(){return Real.getShort();}

	/** Returns the Object Value represented by an 32 Bit Integer	 */
//	public int     getInt(){return Real.getInt();}

	/** Returns the Object Value represented by an 64 Bit Integer	 */
//	public long   getLong(){return Real.getLong();}

	//////////////////////////////
	//	Interface measurable	//
	//////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() {
		if (! Imag.isZero()) throw new AbstractMethodError();
		return ((IMeasurAble)Real).getDouble(); }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float   getFloat() {
		if (! Imag.isZero()) throw new AbstractMethodError();
		return ((IMeasurAble)Real).getFloat(); }

	/**Tests the Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		Complex test = new Complex(new BodyDouble(), new BodyDouble());
		testInstance = test;	//defined in absCopyAble to test the abstract Methods
		testComplex();
		tRcFunc();
		tExp();
		tLn();
		tSinCos();
	}


	//////////////
	//	Testing	//
	//////////////

	/**Tests the basic complex Methods	 */
	private static void testComplex() throws java.io.IOException {
//		double r = java.lang.Math.atan2(1,0);
		Complex c1 = new Complex(0.0);	//No empty Constructor allowed!
		Complex c2 = new Complex(0.0);	//Test empty Constructor
		MetricBody c3;	//Test empty Constructor
		c1.Real.copyAt(new Double(+1)); c1.Imag.copyAt(new Double(-1));
		c3 = (MetricBody) c1.copy();
		System.out.println ("Test von Copy   : Soll : (1,-1)  Ist : " + c3);
		c2.copyAt(c1);
		System.out.println ("Test von CopyAt : Soll : (1,-1)  Ist : " + c2);
		c3 = new Complex(new Polar (c1));
		System.out.println ("Test von REC/POL  : Soll : (1,-1)  Ist : " + c3);
		c1.Real.copyAt(new Double(1));c1.Imag.copyAt(new Double(2));
		c2.Real.copyAt(new Double(3));c2.Imag.copyAt(new Double(4));
		System.out.println ("Betrag : Soll : 7  Ist :" + c2.AbsV());
		c2.Real.swap(c2.Imag);
		System.out.println ("Betrag : Soll : 7  Ist :" + c2.AbsV());
		System.out.println ("Realteil : Soll : 4  Ist :" + c2.Real);
		System.out.println ("Imagteil : Soll : 3  Ist :" + c2.Imag);
		System.out.println ("Chordaler Betrag : Soll : " + 5.0/6 + "  Ist : " + c2.chordal());
		c2.Real.swap(c2.Imag);
//		AMetricBody x1; x1.add(c3);
		c3 = (MetricBody) c1.add (c2.sub (c1));
		System.out.println ("Test von ADD/SUB  : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) c2.div(c1).mul(c1);
		System.out.println ("Test von MUL/DVN  : Soll : (3,4)  Ist : (" + c3);
		c3 =  new Complex(0, -1);
		c3 = (MetricBody) c1.div (c3);
		System.out.println ("Test von DVN      : Soll : (-2,1) Ist : (" + c3);
		c3 = (MetricBody) c1.div (c1.one());
		System.out.println ("Test von DVN      : Soll : (1,2)  Ist : (" + c3);
		c3 = (MetricBody) ((IGroupM)c1.add(c2)).div(c1.add(c2));
		System.out.println ("Test von allem    : Soll : (1,0)  Ist : (" + c3);
		c3 = (MetricBody) c1.div(c2).div(c1.div(c2));
		System.out.println ("Test von allem    : Soll : (1,0)  Ist : (" + c3);
		c3 = (MetricBody) c2.inv().inv();
		System.out.println ("Test von INV      : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) c1.mul(c2).mul(c2.inv());
		System.out.println ("Test von MUL/INV  : Soll : (1,2)  Ist : (" + c3);
		System.in.read();
		c3 =  new Complex(0, -1);
		c3 = (MetricBody) c3.inv();
		System.out.println ("Test von INV      : Soll : (0,1)  Ist : (" + c3);
		c3 = (MetricBody) c3.one().inv();	//Converts to a Scalar, so the Conversion is
		System.out.println ("Test von INV      : Soll : (1,0)  Ist : " + c3);
		c3 = (MetricBody) c2.i().inv();
		System.out.println ("Test von INV      : Soll : (0,-1) Ist : (" + c3);
		c3 = (MetricBody) c2.SqRt().sqr();
		System.out.println ("Test von Sqr/SqRt : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) ((Complex)c2.sqr()).SqRt();
		System.out.println ("Test von Sqr/SqRt : Soll : (3,4)  Ist : (" + c3);
		c3 = (MetricBody) ((Complex)c2.cbc()).CbcRt();
		System.out.println ("Test von Cbc/CbcRt: Soll : (3,4)  Ist : (" + c3 + " oder um 120° verschoben");
		c3 = (MetricBody) c2.CbcRt().cbc();
		System.out.println ("Test von Cbc/CbcRt: Soll : (3,4)  Ist : (" + c3);
	};

	/**Tests the complex Exponential Function	 */
	private static void tExp() throws java.io.IOException {
		Complex c1 = new Complex(0.0);	//Test empty Constructor
		Complex c2 = new Complex(0.0);	//Test empty Constructor
//		Complex c3;	//Test empty Constructor
		System.out.println ("Test von Complex Exp :");
		c1.zeroAt();
		c2 = (Complex) c1.exp();
		System.out.println ("Soll : (1,0)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c1.oneAt();
		c2 = (Complex) c1.exp ();
		System.out.println ("Soll : (" + java.lang.Math.E + ";0)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c1.iAt();
		c2 = (Complex) c1.exp ();
		System.out.println ("Soll : (" + java.lang.Math.cos (1)+ ";" + java.lang.Math.sin (1) + ")  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
	};

	/**Tests the complex Logarithm Function	 */
	private static void tLn() throws java.io.IOException {
		Complex c1 = new Complex(0.0);	//Test empty Constructor
		Complex c2 = new Complex(0.0);	//Test empty Constructor
//		Complex c3;	//Test empty Constructor
		System.out.println ("Test von Ln :");
		c1.zeroAt();
		c2 = (Complex) c1.ln();
		System.out.println ("Soll : (" + java.lang.Double.NEGATIVE_INFINITY + ";0)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c1.oneAt();
		c2 = (Complex) c1.ln();
		System.out.println ("Soll : (0,0)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c1.iAt();
		c2 = (Complex) c1.ln();
		System.out.println ("Soll : (0;" + java.lang.Math.PI/2 + ")  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c1.Real.copyAt(new Double(3));c1.Imag.copyAt(new Double(4));
		c2 = (Complex) c1.ln();
		System.out.println ("Soll : (" + Math.log (5) + "," + 0.927295218001612 + ")  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
	};

	/**Tests the complex Sine and Cosine Function	 */
	private static void tSinCos() throws java.io.IOException {
		Complex c1 = new Complex(0.0);	//Test empty Constructor
		MetricBody c2 = new Complex(0.0);	//Test empty Constructor
//		MetricBody c3;	//Test empty Constructor
		System.out.println ("Test von SinH,CosH,TanH,CotH,ArSinH,ArCosH,ArTanH,ArCotH,Sin,Cos,Tan,Cot,ArSin,ArCos,ArTan,ArCot :");
		for (int Z1 = -2; ++Z1 <= +1;)
		{
			for (int Z2 = -2; ++Z2 <= +1;)
			{
				c1.Real = new BodyDouble (Z1);
				c1.Imag = new BodyDouble (Z2);
				c1.quarterAt();
				System.out.println ("Soll : " + c1);
				System.out.println ("SinH = " + (c2 = c1.SinH()) + "; ArSinH = " + (c2 = c2.ArSinH()) + "; SinH = " + c2.SinH());
				System.out.println ("CosH = " + (c2 = c1.CosH()) + "; ArCosH = " + (c2 = new Complex(c2).ArCosH()) + "; CosH = " + c2.CosH());
				System.out.println ("TanH = " + (c2 = c1.TanH()) + "; ArTanH = " + (c2 = c2.ArTanH()) + "; TanH = " + c2.TanH());
//				System.out.println ("CotH = " + (c2 = c1.CotH()) + "; ArCotH = " + (c2 = c2.ArCotH()) + "; CotH = " + c2.CotH());
				System.out.println ("ArSinH = " + (c2 = c1.ArSinH()) + "; SinH = " + (c2 = c2.SinH()) + "; ArSinH = " + c2.ArSinH());
				System.out.println ("ArCosH = " + (c2 = c1.ArCosH()) + "; CosH = " + (c2 = c2.CosH()) + "; ArCosH = " + new Complex(c2).ArCosH());
				System.out.println ("ArTanH = " + (c2 = c1.ArTanH()) + "; TanH = " + (c2 = c2.TanH()) + "; ArTanH = " + c2.ArTanH());
//				System.out.println ("ArCotH = " + (c2 = c1.ArCotH()) + "; CotH = " + (c2 = c2.CotH()) + "; ArCotH = " + c2.ArCotH());
				System.out.println ("Sin = " + (c2 = c1.sin()) + "; ArSin = " + (c2 = c2.ArcSin()) + "; Sin = " + c2.sin());
				System.out.println ("Cos = " + (c2 = c1.cos()) + "; ArCos = " + (c2 = new Complex(c2).ArcCos()) + "; Cos = " + c2.cos());
				System.out.println ("Tan = " + (c2 = c1.tan()) + "; ArTan = " + (c2 = c2.ArcTan()) + "; Tan = " + c2.tan());
//				System.out.println ("Cot = " + (c2 = c1.cot()) + "; ArCot = " + (c2 = c2.ArcCot()) + "; Cot = " + c2.cot());
				System.out.println ("ArcSin = " + (c2 = c1.ArcSin()) + "; Sin = " + (c2 = c2.sin()) + "; ArcSin = " + c2.ArcSin());
				System.out.println ("ArcCos = " + (c2 = c1.ArcCos()) + "; Cos = " + (c2 = c2.cos()) + "; ArcCos = " + new Complex(c2).ArcCos());
				System.out.println ("ArcTan = " + (c2 = c1.ArcTan()) + "; Tan = " + (c2 = c2.tan()) + "; ArcTan = " + c2.ArcTan());
//				System.out.println ("ArcCot = " + (c2 = c1.ArcCot()) + "; Cot = " + (c2 = c2.cot()) + "; ArcCot = " + c2.ArcCot());
				System.in.read(); System.in.read();
			}
		}
	};

	/**Tests the Complex Methods with real Arguments	 */
	private static void tRcFunc()
	{
		Complex c1 = new Complex(0.0);	//Test empty Constructor
		Complex c2 = new Complex(0.0);	//Test empty Constructor
		Complex c3;	//Test empty Constructor
		System.out.println ("Test von add, mul, div:");
		c1.Real.copyAt(new Double(3));c1.Imag.copyAt(new Double(2));
		c2 = (Complex) c1.add(new Double(7));
		System.out.println ("Soll : (10,2)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c2 = (Complex) c2.sub(new Double(7));
		System.out.println ("Soll : (3,2)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c1.Real.copyAt(new Double(4));c1.Imag.copyAt(new Double(3));
		c2 = (Complex) c1.mul(new Double(5));
		System.out.println ("Soll : (20,15)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c2 = (Complex) c2.div(new Double(5));
		System.out.println ("Soll : (4,3)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c3 = (Complex) c1.one().div(new Double(5));
		System.out.println ("Soll : ( 0.2, 0)  Ist : (" + c3.Real+ ";" + c3.Imag + ")");
		c3 = (Complex) c1.i().div(new Double(5));
		System.out.println ("Soll : ( 0, 0.2)  Ist : (" + c3.Real+ ";" + c3.Imag + ")");
		c1.Real.copyAt(new Double(0));c1.Imag.copyAt(new Double(1));
		c2 = (Complex) c1.mul(new Double(5));
		System.out.println ("Soll : (0,5)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		c2 = (Complex) c2.div(new Double(-5));
		System.out.println ("Soll : (0,-1)  Ist : (" + c2.Real+ ";" + c2.Imag + ")");
		//ToDo: Test SqRt()!!!
	};

}
