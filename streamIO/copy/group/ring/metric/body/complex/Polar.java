package streamIO.copy.group.ring.metric.body.complex;

import streamIO.IDeserializer;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import streamIO.copy.group.ring.metric.IWellOrder;
import streamIO.copy.group.ring.metric.body.AMetricBody;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;

/**Concrete final Class to define Complex Numbers of arbitrary Types
 * in Polar Representation.
 * Complex Numbers from a Metric Body form a non-metric algebraic complete Body.
 *
 * Design Decisions:
 * Chose absMetricIRing instead of AMetricBody as the Constituents,
 * because all basic operations are allowed on them,
 * but they can also contain basic Integer Types, and so allow for Optimizations.
 *
 * The Behavior of Complex Numbers can be controlled by boolean static Variables:
 * -A Complex will always be checked for zero imaginary Part after any Operation.
 */
final public class Polar
extends AMetricBody {

	//////////////////////////////
	//	interface IComplex	//
	//////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	public IIntRing cjgAt() { ang.negAt(); return this;}

	/**Overrides the false Value from the Implementation in absComplex	 */
	public boolean isComplex() { return true;}

	/**Used, because the constructor can only be called as the very first command.	 */
	private void copyAtPol(Polar arg) {
		r   = (IMetricIRing) arg.r  .copy();
		ang = (IMetricIRing) arg.ang.copy();
	}

	/**Used, because the constructor can only be called as the very first command.	 */
	private void copyAtCmp(Complex arg)
	{
		r   = arg.Norm();
		ang = (IMetricIRing)	((MetricBody)arg.Imag).ArcTg(arg.Real);
	}

	/**Used, because the constructor can only be called as the very first command.	 */
	private void copyAtReal(Object arg)
	{
		if		(arg instanceof Complex)copyAtCmp(((Complex)arg));
		else if (arg instanceof Polar)	copyAtPol(((Polar)	arg));
		else
		{
			r   = (IMetricIRing) ((ICopyAble) arg).copy();
			ang = (IMetricIRing) r.zero();	//Choose the same type -> faster
		}
	}

	/**Empty Constructor (for newInstance Method).
	 * Does not create Dummy Objects for it's Constituents.
	 * So those Objects are not well-defined, but contain Null Pointers.	 */
	protected Polar()
	{
//		BaseAccuracyInv = SqRtMaxValue();
//		BaseAccuracy = BaseAccuracyInv.inv();
//		r   = new BodyDouble();
//		ang = new BodyDouble();
	}

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public Polar(Polar arg)	{copyAtPol(arg);}

	/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
	 * Uses the Copy Constructors of the Constituents.	 */
	public Polar(IMetricIRing r_, IMetricIRing ang_)
	{
		r	= (IMetricIRing) r_	.copy();
		ang = (IMetricIRing) ang_.copy();
	}

	/**Constructor that takes a complex Argument and converts it to Polar Coordinates,
	 * in which Multiplication and Exponential Functions are easier to calculate.	 */
	public Polar(Complex arg) { copyAtCmp((Complex) arg);}

	/**Constructor that takes any Argument and converts it to Polar Coordinates.
	 * When the argument is real, the angle is set to 0.	 */
	public Polar(Object arg)
	{
		if		(arg instanceof Complex)copyAtCmp((Complex)	arg);	//this((Complex) arg);
		else if (arg instanceof Polar)	copyAtPol((Polar)	arg);	//this((Polar)   arg);
		else							copyAtReal(arg);		//Real Argument
	}

	/**absolute Value: |x| == r
	 * Returns/Sets the Radius r,
	 * i.e. the length of the Polar Representation	 */
	public IMetricIRing r;	//Re

	/**Returns/Sets the 'arg' (Argument),
	 * i.e. the angle of the corresponding Polar Representation	 */
	public IMetricIRing ang;	//Im

	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument TODO: correct this!
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt() { return this; }

	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument TODO: correct this!
	 * and is equal to a mathematical integer. 	 */
	public IIntRing IntAt() { return this; }

	/**less: '<' Returns True, when 'Self' < arg	*/
	public boolean isLessThan(Object arg) { return r.isLessThan(arg); }

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public IWellOrder maxValueAt() { r.maxValueAt(); return this; }

	/**Carry the Overflow through the g-adic Representation.	 */
	public void addCarry() { }

	/**Returns the Value raised by one g-Adic Position	 */
	public IIntRing toUpperAt() { return this;}

	/**Complement in Place: ~=	*/
	public IIntRing CmplAt() { throw new AbstractMethodError();}

	/**Multiplication or Division in Place: *= , /=	 */
	public IGroupM MulDivAt(Object arg, boolean mul)
	{
		if (arg instanceof Polar)
		{
			Polar arg_ = ((Polar)arg);
			if (!(bolLazySimplify && arg_.ang.isZero()))
			{
				if (mul) r.mulAt (arg_.r);
				else     r.divAt (arg_.r);
				if (!(bolLazySimplify && ang.equals(arg_.ang)))  // check, if it is a real Result:
					if (mul) ang.addAt (arg_.ang);
					else     ang.subAt(arg_.ang);
			}
			else	//Real Argument in complex disguise, no real Result
			{
				if (mul) r.mulAt(arg_.r);
				else     r.divAt(arg_.r);
			}
		}
		else if  (arg instanceof Complex) 	//Complex Argument, no real Result
		{
			return MulDivAt(new Polar(((Complex) arg)), mul);
		}
		else 	//Real Argument, no real Result
		{
			if (mul) r.mulAt(arg);
			else     r.divAt(arg);
		}
		return this;
	}

	/**Division in Place: /=	 */
	public IGroupM divAt(Object arg)	{return MulDivAt (arg, false);}

	/**Multiplication in Place: *=	 */
	public ISemiGroupM mulAt(Object arg)	{return (ISemiGroupM) MulDivAt (arg, true);}

	/**Helper Routine to convert to Complex from any other numeric Type:
	 * RingLong, Number or countable.
	 * Uses ASemiGroup.getLong to do that.
	 * Using this Helper Routine generates Overhead,
	 * because the special optimizations for integer Values are not considered.
	 */
	private final Polar convertArg (Object arg)
	{return (arg instanceof Polar)? (Polar) arg : new Polar(arg);}

	/**Complement to Copy.
	 * Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value
	 * and returns itself for further use.	 */
	public ICopyAble copyAt(Object arg, int Depth)
	{
		Polar tmp = convertArg(arg);
		r  .copyAt(tmp.r	,Depth);
		ang.copyAt(tmp.ang	,Depth);
		return this;
	}

	/**Creates an uninitalized new Instance of it's class.
	 * This can in VB also be achieved by 'CreateObjectFromInstance',
	 * which may be slower.	 */
	public ICopyAble newInstance()
	{
		Polar tmp = new Polar();
		tmp.r	= (IMetricIRing) ((ICopyAble)r  ).newInstance();
		tmp.ang = (IMetricIRing) ((ICopyAble)ang).newInstance();
		return tmp;
	}

	/**Does a shallow Copy of the Argument.
	 * I.e. both Instances will share their inner Components.	 */
	public ICopyAble shallowCopyAt(Object arg)
	{	//don't rely on the Argument being a Polar
//		super.shallowCopyAt(arg);	//not necessary, since all these Fields apply only to Integers.
		Polar tmp = convertArg(arg);
		r   = tmp.r;
		ang = tmp.ang;
		return this;
	}

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
	public int hashCode() { return r.hashCode() + ang.hashCode() ;}

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
		return "(" + r + ", " + ang + ")"; }
//		return Starter + r.toString() + Separator + ang.toString() + Stopper; }

	/**Fills this Instance with the Contents read from the String.	 */
	public ICopyAble fromStreamAt(IDeserializer arg)
		throws java.io.IOException {
		r  .fromStreamAt(arg);
		ang.fromStreamAt(arg);
/*		String[] List = parseList(arg);
		r  .fromStringAt(List[0]);
		ang.fromStringAt(List[1]);
*/		return this;
	}

	//////////////////////////////////
	//	Replication ISemiGroup:
	//////////////////////////////////

	/**Addition: +
	 * There is an easy way to calculate the r of the Result,
	 * but none to calculate the angle of the Result,
	 * so the operation is performed in Complex Types.	 */
	public ISemiGroup add(Object arg)
	{return new Complex(this).addAt(arg);}

	/**Subtraction: -
	 * There is an easy way to calculate the r of the Result,
	 * but none to calculate the angle of the Result,
	 * so the operation is performed in Complex Types.	 */
	public IGroup sub(Object arg)
	{return new Complex(this).subAt(arg);}

	/**Addition in Place: +=
	 * There is an easy way to calculate the r of the Result,
	 * but none to calculate the angle of the Result,
	 * so the operation is performed in Complex Types
	 * and then transformed back.
	 * r^2 = r1^2 + r2^2 + 2 r1*r2*cos(a1-a2)	 */
	public ISemiGroup addAt(Object arg)	{return (ISemiGroup) shallowCopyAt(add(arg));}

	/**Subtraction in Place: -=
	 * There is an easy way to calculate the r of the Result,
	 * but none to calculate the angle of the Result,
	 * so the operation is performed in Complex Types
	 * and then transformed back.
	 * r^2 = r1^2 + r2^2 - 2 r1*r2*cos(a1-a2)	 */
	public IGroup subAt(Object arg)	{return (IGroup) shallowCopyAt(sub(arg));}

	//	Optimizations:	//

	/**absolute Value: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsV()	{return (IScalarMetric) r.copy();}//.AbsV();}	//r is assumed as always positive!

	/**absolute Value in Place: |x|
	 * Returns the fastest Norm, which is the AbsV_Norm	 */
	public IScalarMetric AbsVAt() { ang.zeroAt(); return (IScalarMetric)r;}//.AbsV();}	//r is assumed as always positive!

	/**(Euklidische Norm)^2
	 * Special Case of the p-Norm for p = 2
	 * Rotation Invariant for cartesian Systems.	 */
	public IMetricIRing SqrNorm() {return (IMetricIRing)r.sqr();}

	/**Double in Place: x+=x
	 * The angle stays the same!	 */
	public ISemiGroup dblAt () { r.dblAt();return this;}

	/**Triple in Place: x+=2x	*/
	public ISemiGroup trplAt () { r.trplAt();return this;}

	/**Returns the Constant Pi = 3.14159265359... in Place
	 * This is half the Quotient of Circumference and Radius of any circle.	 */
	public MetricBody piAt() { ((MetricBody)r).piAt(); ang.zeroAt(); return this;}

}
