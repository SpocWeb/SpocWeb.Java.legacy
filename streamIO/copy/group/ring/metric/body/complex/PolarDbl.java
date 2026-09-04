package streamIO.copy.group.ring.metric.body.complex;

import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.INorm;
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

/**Concrete final Class to define ComplexDbl Numbers of arbitrary Types
 * in Polar Representation.
 * ComplexDbl Numbers from a Metric Body form a non-metric algebraic complete Body.
 *
 * Design Decisions:
 * Chose AMetricIRing instead of AMetricBody as the Constituents,
 * because all basic operations are allowed on them,
 * but they can also contain basic Integer Types, and so allow for Optimizations.
 *
 * The Behavior of ComplexDbl Numbers can be controlled by boolean static Variables:
 * -A ComplexDbl will always be checked for zero imaginary Part after any Operation.
 */
final public class PolarDbl
extends AMetricBody {

//////////////////////////////
//	interface IComplex	//
//////////////////////////////

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**Returns the conjugate ComplexDbl Number in Place:
 * i.e. the imaginary Part flips it's sign.	 */
public IIntRing cjgAt(){ang = -ang; return this; }

/**Overrides the false Value from the Implementation in absComplex	 */
public boolean isComplex(){return true; }



/**Used, because the constructor can only be called as the very first command.	 */
private void copyAtPol(PolarDbl arg) {
	r   = arg.r  ;
	ang = arg.ang; }

/**Used, because the constructor can only be called as the very first command.	 */
private void copyAtCmp(ComplexDbl arg) {
	r   = Math.sqrt(arg.SqrNormDbl());
	ang = Math.atan2(arg.imag, arg.real); }

/**Used, because the constructor can only be called as the very first command.	 */
private void copyAtReal(Object arg) {
	if		(arg instanceof ComplexDbl)copyAtCmp(((ComplexDbl)arg));
	else if (arg instanceof PolarDbl)	copyAtPol(((PolarDbl)	arg));
	else {
		r   = ByRefDouble.GET_DOUBLE(arg);
		ang = ICountAble.ZERO; } 	//Choose the same type -> faster
}

/**Empty Constructor (for newInstance Method).
 * Does not create Dummy Objects for it's Constituents.
 * So those Objects are not well-defined, but contain Null Pointers.	 */
protected PolarDbl() {
//		BaseAccuracyInv = SqRtMaxValue();
//		BaseAccuracy = BaseAccuracyInv.inv();
//		r   = new BodyDouble();
//		ang = new BodyDouble();
}

/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
 * Uses the Copy Constructors of the Constituents.	 */
public PolarDbl(PolarDbl arg)	{copyAtPol(arg);}

/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
 * Uses the Copy Constructors of the Constituents.	 */
public PolarDbl(double r_, double ang_) {
	r	= r_  ;
	ang = ang_; }

/**Constructor that takes a complex Argument and converts it to Polar Coordinates,
 * in which Multiplication and Exponential Functions are easier to calculate.	 */
public PolarDbl(ComplexDbl arg){copyAtCmp((ComplexDbl) arg);}

/**Constructor that takes any Argument and converts it to Polar Coordinates.
 * When the argument is real, the angle is set to 0.	 */
public PolarDbl(Object arg) {
	if		(arg instanceof ComplexDbl)copyAtCmp((ComplexDbl)	arg);	//this((ComplexDbl) arg);
	else if (arg instanceof PolarDbl)	copyAtPol((PolarDbl)	arg);	//this((PolarDbl)   arg);
	else							copyAtReal(arg); } 		//Real Argument

/**absolute Value: |x| == r
 * Returns/Sets the Radius r,
 * i.e. the length of the Polar Representation	 */
public double r;	//Re

/**Returns/Sets the 'arg' (Argument),
 * i.e. the angle of the corresponding Polar Representation	 */
public double ang;	//Im

/**Returns the largest (closest to positive infinity) value in Place,
 * that is not greater than the argument
 * and is equal to a mathematical integer. 	 */
public IMetricIRing FloorAt(){return this;};

/**less: '<' Returns True, when 'Self' < arg	*/
public boolean isLessThan(Object arg){return r*r < ByRefDouble.GET_DOUBLE(((INorm)arg).SqrNorm());};

/**Sets and returns the maximum Value for this Class in Place.	 */
public IWellOrder maxValueAt(){r = Double.MAX_VALUE; return this;};

/**Carry the Overflow through the g-adic Representation.	 */
public void addCarry(){}

/**Returns the Value raised by one g-Adic Position	 */
public IIntRing toUpperAt(){return this;}

/**Complement in Place: ~=	*/
public IIntRing CmplAt(){throw new AbstractMethodError();}

/**Multiplication or Division in Place: *= , /=	 */
public IGroupM MulDivAt(Object arg, boolean mul) {
	if (arg instanceof PolarDbl) {
		PolarDbl arg_ = ((PolarDbl)arg);
		if (!(bolLazySimplify && (arg_.ang == ICountAble.ZERO))) {
			if (mul) r *= arg_.r;
			else     r /= arg_.r;
			if (!(bolLazySimplify && (ang == arg_.ang)))  // check, if it is a real Result:
				if (mul) ang += arg_.ang;
				else     ang -= arg_.ang;
		} else {	//Real Argument in complex disguise, no real Result
			if (mul) r *= arg_.r;
			else     r /= arg_.r; }
	}
	else if  (arg instanceof ComplexDbl) 	//ComplexDbl Argument, no real Result
	{
		return MulDivAt(new PolarDbl(((ComplexDbl) arg)), mul);
	} else { 	//Real Argument, no real Result
		if (mul) r *= ByRefDouble.GET_DOUBLE(arg);
		else     r /= ByRefDouble.GET_DOUBLE(arg); }
	return this; }

/**Division in Place: /=	 */
public IGroupM divAt(Object arg)	{return MulDivAt (arg, false);}

/**Multiplication in Place: *=	 */
public ISemiGroupM mulAt(Object arg)	{return (ISemiGroupM) MulDivAt (arg, true);}

/**Helper Routine to convert to ComplexDbl from any other numeric Type:
 * RingLong, Number or countable.
 * Uses ASemiGroup.getLong to do that.
 * Using this Helper Routine generates Overhead,
 * because the special optimizations for integer Values are not considered.
 */
private final PolarDbl convertArg (Object arg)
{return (arg instanceof PolarDbl)? (PolarDbl) arg : new PolarDbl(arg);}

/**Complement to Copy.
 * Does a 'deepCopy', i.e. also inner Components are copied.
 * Copies the Value of arg into it's own Value
 * and returns itself for further use.	 */
public ICopyAble copyAt(Object arg, int Depth) {
	PolarDbl tmp = convertArg(arg);
	r   = tmp.r;
	ang = tmp.ang;
	return this; }

/**Creates an uninitalized new Instance of it's class.
 * This can in VB also be achieved by 'CreateObjectFromInstance',
 * which may be slower.	 */
public ICopyAble newInstance() {return new PolarDbl(); }

/**Does a shallow Copy of the Argument.
 * I.e. both Instances will share their inner Components.	 */
public ICopyAble shallowCopyAt(Object arg)
{	//don't rely on the Argument being a Polar
//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
	PolarDbl tmp = convertArg(arg);
	r   = tmp.r;
	ang = tmp.ang;
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
public int hashCode(){return ((int) Double.doubleToLongBits(r)) ^
							 ((int) Double.doubleToLongBits(ang)) ;}

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
public String toString(){
	return "(" + r + ", " + ang + ")";}
//	return Starter + r + Separator + ang + Stopper;}

/**Fills this Instance with the Contents read from the String.	 */
public ICopyAble fromStreamAt(java.io.StreamTokenizer arg)
	throws java.io.IOException {
	//TODO
//	r   = Parsing.nextNumber(arg, false);
//	ang = Parsing.nextNumber(arg, false);
	return this; }

//////////////////////////////////
//	Replication ISemiGroup: 
//////////////////////////////////

/**Addition: +
 * There is an easy way to calculate the r of the Result,
 * but none to calculate the angle of the Result,
 * so the operation is performed in ComplexDbl Types.	 */
public ISemiGroup add(Object arg)
{return new ComplexDbl(this).addAt(arg);}

/**Subtraction: -
 * There is an easy way to calculate the r of the Result,
 * but none to calculate the angle of the Result,
 * so the operation is performed in ComplexDbl Types.	 */
public IGroup sub(Object arg)
{return new ComplexDbl(this).subAt(arg);}

/**Addition in Place: +=
 * There is an easy way to calculate the r of the Result,
 * but none to calculate the angle of the Result,
 * so the operation is performed in ComplexDbl Types
 * and then transformed back.
 * r^2 = r1^2 + r2^2 + 2 r1*r2*cos(a1-a2)	 */
public ISemiGroup addAt(Object arg)	{return (ISemiGroup) shallowCopyAt(add(arg));}

/**Subtraction in Place: -=
 * There is an easy way to calculate the r of the Result,
 * but none to calculate the angle of the Result,
 * so the operation is performed in ComplexDbl Types
 * and then transformed back.
 * r^2 = r1^2 + r2^2 - 2 r1*r2*cos(a1-a2)	 */
public IGroup subAt(Object arg)	{return (IGroup) shallowCopyAt(sub(arg));}

//	Optimizations:	//

/**absolute Value: |x|
 * Returns the fastest Norm, which is the AbsV_Norm	 */
public IScalarMetric AbsV()	{return new BodyDouble(r); }	//.AbsV();}	//r is assumed as always positive!

/**absolute Value in Place: |x|
 * Returns the fastest Norm, which is the AbsV_Norm	 */
public IScalarMetric AbsVAt(){ang = ICountAble.ZERO; return this; }		//.AbsV();}	//r is assumed as always positive!

/**(Euklidische Norm)^2
 * Special Case of the p-Norm for p = 2
 * Rotation Invariant for cartesian Systems.	 */
public IMetricIRing SqrNorm() {return new BodyDouble(r * r); }

/**Double in Place: x+=x
 * The angle stays the same!	 */
public ISemiGroup dblAt (){r += r; return this; }

/**Triple in Place: x+=2x	*/
public ISemiGroup trplAt (){r += r+r; return this; }

/**Returns the Constant Pi = 3.14159265359... in Place
 * This is half the Quotient of Circumference and Radius of any circle.	 */
public MetricBody piAt(){r = IMeasurAble.PI; ang = ICountAble.ZERO; return this; }

}
