package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.IGroup;
import function.IFloatFunction;
import function.IFunction;
import function.IInvertAble;
import function.IMeasurAble;
import function.derive.ADeriveAble;
import function.derive.CCountAble;
import function.derive.CMeasurAble;
import function.derive.Cat;
import function.derive.Const;
import function.derive.IDeriveAble;

/**This Class encapsulates the Diff Function: Difference = Minuend - Subtrahend
 * It adds two Functions given in the Constructor.
 * It also implements the Derivation after the Diff Rule.
 * It reduces Complexity by eliminating Subtrahends of 0 and null.
 * The Class could be easily split up into Diff and DiffDerive,
 * but then you could not reuse the Optimizations in ADeriveAble.  */
public class Diff
extends ADeriveAble {

	/**First Argument: Minuend
	 * Difference = Minuend - Subtrahend	 */
	public IFunction Minuend;

	/**Second Argument: Subtrahend
	 * Difference = Minuend - Subtrahend	 */
	public IFunction Subtrahend;

	/**Constructor taking the two Arguments: Minuend - Subtrahend	 */
	public Diff(IFunction Minuend, IFunction Subtrahend) {
		if  (Minuend == null) {Minuend = Subtrahend; Subtrahend = null;}
		this.Subtrahend = Subtrahend;
		this.Minuend = Minuend;
	}

	/**This Function encapsulates the Diff Function.
	 * It adds the Results of the two Summand Functions.	 */
	public Object Map (Object arg) {
		Object tmp = Minuend.Map(arg);
		if (Subtrahend != null) ((IGroup) tmp).subAt(Subtrahend.Map(arg));	//Subtrahend assumed to 0
		return tmp;		//the following is equivalent, but harder to realize with a non C Language
	}

	/**This Function encapsulates the Prod Function.
	 * It multiplies the Results of the two Factor Functions.	 */
	public double Map (double arg) {
		double tmp = ((IFloatFunction) Minuend   ).Map(arg);
		if (Subtrahend == null) return tmp;
		return tmp - ((IFloatFunction) Subtrahend).Map(arg);	//Subtrahend assumed to 0
	}

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() {
		String Return = Minuend.toString();
		if (Subtrahend != null) Return = "((" + Return + ")-(" + Subtrahend.toString() + "))";
		return Return; }

	/**Compares two Objects for equality.
	 * <p>
	 * The <code>equals</code> method implements an equivalence relation:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any reference Value <code>x</code>,
	 * <code>x.equals(x)</code> should return <code>true</code>.
	 * <li>It is <i>symmetric</i>: for any reference values <code>x</code> and
	 * <code>y</code>, <code>x.equals(y)</code> should return
	 * <code>true</code> if and only if <code>y.equals(x)</code> returns
	 * <code>true</code>.
	 * <li>It is <i>transitive</i>: for any reference values <code>x</code>,
	 * <code>y</code>, and <code>z</code>, if <code>x.equals(y)</code>
	 * returns  <code>true</code> and <code>y.equals(z)</code> returns
	 * <code>true</code>, then <code>x.equals(z)</code> should return
	 * <code>true</code>.
	 * <li>It is <i>consistent</i>: for any reference values <code>x</code>
	 * and <code>y</code>, multiple invocations of <code>x.equals(y)</code>
	 * consistently return <code>true</code> or consistently return
	 * <code>false</code>.
	 * <li>For any reference Value <code>x</code>, <code>x.equals(null)</code>
	 * should return <code>false</code>.
	 * </ul>
	 * <p>
	 * The equals method for class <code>Object</code> implements the most
	 * discriminating possible equivalence relation on objects; that is,
	 * for any reference values <code>x</code> and <code>y</code>, this
	 * method returns <code>true</code> if and only if <code>x</code> and
	 * <code>y</code> refer to the same object (<code>x==y</code> has the
	 * Value <code>true</code>).
	 *
	 * @param   obj   the reference object with which to compare.
	 * @return  <code>true</code> if this object is the same as the obj
	 * argument; <code>false</code> otherwise.
	 * @see     java.lang.Boolean#hashCode()
	 * @see     java.util.Hashtable
	 * @since   JDK1.0 	 */
	final public boolean equals  (Object arg) {
		if (!(arg instanceof Diff)) return false;
		Diff arg_ = (Diff) arg;
		return Minuend	 .equals(arg_.Minuend) &&
			   Subtrahend.equals(arg_.Subtrahend); }

	/**Returns an alternative Representation that is easier to simplify
	 * Rules implemented here:
	 * Functions are Subtracted directly, if possible
	 * (determined by canProcess(), if they are compatible)
	 * Difference = Minuend - Subtrahend
	 * a - 0 = a
	 * 0 - a = -a	 */
	public IFunction simplify() {	//The Sequence of Operations is critical!
		if  (Minuend	!= null) Minuend	= Minuend	.simplify();
		if  (Subtrahend != null) Subtrahend	= Subtrahend.simplify();
		if ((Subtrahend	== null) ||
			(Subtrahend	== CCountAble.Zero) ||
			 CCountAble.Zero.equals(Subtrahend)) return Minuend;	//a - 0 == a
		if ((Minuend	== null) ||
			(Minuend	== CCountAble.Zero) ||
			 CCountAble.Zero.equals(Minuend)) return new CatDerive(Neg.NEG, (IDeriveAble) Subtrahend);	//0 - a = -a
		if  (Minuend.equals(Subtrahend)) return CCountAble.Zero;	//a - a == 0
		if ((Subtrahend instanceof IMeasurAble) &&
			(Minuend    instanceof IMeasurAble))  //can subtract both directly
			return new CMeasurAble(((IMeasurAble)    Minuend).getDouble() -
									((IMeasurAble) Subtrahend).getDouble());
		Cat CC;
		if  (Subtrahend instanceof Cat)
			if ((CC = (Cat) Subtrahend).outer() instanceof Neg)
				return new Sum(Minuend, CC.inner());	//a - -b = a + b
/*		if  (Minuend instanceof Cat) //either this Conversion or the one in CatDerive
			if ((CC = (Cat) Minuend).outer instanceof Negative)	//better to leave Summands alone,
				return new Sum(Subtrahend, CC.inner);	//-b -a = -(a + b)	//because you can commute them
*/		if  (Minuend	instanceof IGroup) {
			if (((IGroup)Minuend	).isZero())	//0 - a = -a
				return new CatDerive(Neg.NEG, (IDeriveAble) Subtrahend);
			if	(Minuend	.canProcess(Subtrahend	))
				return (IFunction) ((IGroup) Minuend	).subAt(Subtrahend);
		}
		if  (Subtrahend	instanceof IGroup) {
			if (((IGroup)Subtrahend).isZero()) return Minuend;		//a - 0 = a
			if (Subtrahend	.canProcess(Minuend		))
				return (IFunction) ((IGroup) Subtrahend).subAt(Minuend).negAt();
		}
		return this; }

	/**Returns the Inverse Function to this one:
	 * The Inverse of a Difference is not well defined,
	 * since there are two Operands.
	 * If one Operand is constant, you can invert it globally
	 * If both Operands are monotonous you can invert it locally!	 */
	public IInvertAble getInverse() {
		if (Inverse != null) return Inverse;
		if (Minuend instanceof Const)	//both implementations are equivalent!!!
//			Inverse = new CatDerive(((IInvertAble) Subtrahend).getInverse(),
//									   new Diff(Minuend, Identity.Identity));
			Inverse = new CatDerive(((IInvertAble) Subtrahend).getInverse(),
					  new CatDerive(new AddAt(Minuend.Map (null)), Neg.NEG));
		if (Subtrahend instanceof Const)
			Inverse = new CatDerive(((IInvertAble) Minuend).getInverse(),
									   new AddAt(Subtrahend));
		if (Inverse != null) return Inverse;
		throw new AbstractMethodError(); }

	/**Returns the full Derivative of this Function.
	 * Possible Simplification already implemented here,
	 * although you could also use simplify() to do that
	 * after Differentiation.	 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative;	//Caching
		IDeriveAble dM = ((IDeriveAble) Minuend   ).getDerivative();
		IDeriveAble dS = ((IDeriveAble) Subtrahend).getDerivative();
		if (dS == CCountAble.Zero)  Derivative = dM; else
		if (dM == CCountAble.Zero)  Derivative = new CatDerive(Neg.NEG, dS); else
									Derivative = new Diff(dM, dS);
		Derivative.setIntegral(this);	//provide the link back to the Integral
		return Derivative; }

	/**Returns the Integral.	 */
	public IDeriveAble getIntegral() {
		if (Integral != null) return Integral;
		Integral =	((IDeriveAble) Minuend	 ).getIntegral();
		if (Subtrahend != null) {
			Integral = (IDeriveAble)
						new Diff(Integral,
					((IDeriveAble) Subtrahend).getIntegral());
		} Integral.setDerivative(this);	//provide the link back to the Derivative
		return Integral; }

}
