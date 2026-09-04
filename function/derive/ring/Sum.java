package function.derive.ring;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
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

/**This Class encapsulates the Sum Function: Sum = Summand1 + Summand2
 * It adds two Functions given in the Constructor.
 * It also implements the Derivation after the Sum Rule.
 * It reduces Complexity by eliminating Summands of 0 and null.	 */
public class Sum
extends ADeriveAble {

	/** First Summand	 */
	public IFunction Summand1;

	/** Second Summand	 */
	public IFunction Summand2;

	/** Constructor taking the two Summands	 */
	public Sum(IFunction Summand1, IFunction Summand2) {
		if  (Summand1 == null) {Summand1 = Summand2; Summand2 = null;}
		this.Summand1 = Summand1;
		this.Summand2 = Summand2;
	}

	/** This Function encapsulates the Sum Function.
	  * It adds the Results of the two Summand Functions.	 */
	public Object Map (Object arg) {
		Object tmp = Summand1.Map(arg);
		if (Summand2 != null) ((ISemiGroup) tmp).addAt(Summand2.Map(arg));	//Summand2 assumed to 0
		return tmp; }	//the following is equivalent, but harder to realize with a non C Language

	/** This Function encapsulates the Prod Function.
	  * It multiplies the Results of the two Factor Functions.	 */
	public double Map (double arg) {
		double tmp = ((IFloatFunction) Summand1).Map(arg);
		if (Summand2 == null) return tmp;
		return tmp * ((IFloatFunction) Summand2).Map(arg);	//Summand2 assumed to 0
	}

	/** Compares two Objects for equality.
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
		if (!(arg instanceof Sum)) return false;
		Sum arg_ = (Sum) arg;
		return Summand1.equals(arg_.Summand1) &&
			   Summand2.equals(arg_.Summand2); }

	/** @return  The string representation of the Function.
	  * @since   JDK1.0	 */
	public String toString() {
		String Return = Summand1.toString();
		if (Summand2 != null) Return = "((" + Return + ")+(" + Summand2.toString() + "))";
		return Return; }

	/** Returns an alternative Representation that is easier to simplify
	  * Rules implemented here:
	  * Functions are Added directly, if possible
	  * a + 0 = 0 + a = a
	  * a + -b = -b + a = a - b	 */
	public IFunction simplify() {	//The Sequence of Operations is critical!
		if  (Summand1 != null) Summand1 = Summand1.simplify();
		if  (Summand2 != null) Summand2 = Summand2.simplify();
		if ((Summand1 == null) ||
			(Summand1 == CCountAble.Zero) ||
			 CCountAble.Zero.equals(Summand1)) return Summand2;	//0 + b = b
		if ((Summand2 == null) ||
			(Summand2 == CCountAble.Zero) ||
			 CCountAble.Zero.equals (Summand2)) return Summand1;	//a + 0 = a
		if ((Summand1 instanceof IGroup) && (((IGroup)Summand1).isZero())) return Summand2;//a + 0 = a
		if ((Summand2 instanceof IGroup) && (((IGroup)Summand2).isZero())) return Summand1;//0 + a = a
		if ((Summand1 instanceof IMeasurAble) &&
			(Summand2 instanceof IMeasurAble))
			return new CMeasurAble(((IMeasurAble) Summand1).getDouble() +
								   ((IMeasurAble) Summand2).getDouble());
		Cat CC;
		if  (Summand1 instanceof Cat)
			if ((CC = (Cat) Summand1).outer() instanceof Neg)
				return new Diff(Summand2, CC.inner());	//a + -b = a - b
		if  (Summand2 instanceof Cat)
			if ((CC = (Cat) Summand2).outer() instanceof Neg)
				return new Diff(Summand1, CC.inner());	//-b + a = a - b
		//-a + -b = - (a+b)
		if  (Summand1.canProcess(Summand2) &&
			(Summand1 instanceof ISemiGroup)) return (IFunction) ((ISemiGroup) Summand1).addAt(Summand2);
		if  (Summand2.canProcess(Summand1) &&
			(Summand2 instanceof ISemiGroup)) return (IFunction) ((ISemiGroup) Summand2).addAt(Summand1);
		return this; }

	/**Returns the Inverse Function to this one:
	 * The Inverse of a Sum is not well defined,
	 * since there are two Operands.
	 * If one Operand is constant, you can invert it globally
	 * If both Operands are monotonous you can invert it locally!	 */
	public IInvertAble getInverse()	{
		if (Inverse != null) return Inverse;
		if (Summand1 instanceof Const) //y = C + f(x) <=> x = fI(y - C)
			Inverse = new CatDerive(((IInvertAble) Summand2).getInverse(),
									   new AddAt(((IGroup) Summand1.Map (null)).neg()));
		if (Summand2 instanceof Const) //y = f(x) + C <=> x = fI(y - C)
			Inverse = new CatDerive(((IInvertAble) Summand1).getInverse(),
									   new AddAt(((IGroup) Summand2.Map (null)).neg()));
		if (Inverse != null) return Inverse;
		throw new AbstractMethodError(); }

	/**Returns the full Derivative of this Function
	 * Uses the Additivity: (f()+g())' = f'() + g'()	 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative;
		IDeriveAble df1 = ((IDeriveAble) Summand1).getDerivative();
		IDeriveAble df2 = ((IDeriveAble) Summand2).getDerivative();
		if (df1 == CCountAble.Zero) Derivative = df2; else
		if (df2 == CCountAble.Zero) Derivative = df1; else
									Derivative = new Sum (df1, df2);
		Derivative.setIntegral(this);
		return Derivative; }

	/**Returns the Integral.
	 * Uses the Additivity: Int[f()+g()] = F() + G()	 */
	public IDeriveAble getIntegral() {
		if (Integral != null) return Integral;
							  Integral = 		 ((IDeriveAble) Summand1).getIntegral();
		if (Summand2 != null) Integral = new Sum(((IDeriveAble) Summand2).getIntegral(), Integral);
		Integral.setDerivative(this);
		return Integral; }

}
