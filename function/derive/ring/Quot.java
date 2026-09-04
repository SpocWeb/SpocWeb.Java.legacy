package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.groupM.IGroupM;
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

/**This Class encapsulates the Quotient Function: Quotient = Dividend / Divisor
 * It multiplies two Functions given in the Constructor.
 * It also implements the Derivation after the Product Rule.
 * It reduces Complexity by eliminating Factors of 1 and null.	 */
public class Quot
extends ADeriveAble {

	/**Dividend
	 * Quotient = Dividend / Divisor	 */
	public IFunction Dividend;

	/**Divisor
	 * Quotient = Dividend / Divisor	 */
	public IFunction Divisor;

	/**Constructor taking the two Factors	 */
	public Quot(IFunction Dividend, IFunction Divisor) {
		if ((Dividend == null)
//		||	(Factor1 == One.One)	//using null instead of One could lead to misunderstandings!
								){Dividend = Divisor; Divisor = null;}
		this.Dividend = Dividend;
		this.Divisor  = Divisor;
	}

	/**This Function encapsulates the Sum Function.
	 * It adds the Results of the two Factor Functions.	 */
	public Object Map (Object arg) {
		Object tmp = Dividend.Map(arg);
		if (Divisor != null) ((IGroupM) tmp).divAt(Divisor.Map(arg));	//Divisor assumed to 1
		return tmp;		//the following is equivalent, but harder to realize with a non C Language
//		if (Divisor == null) return Dividend.Map(arg);	//Divisor assumed to 1
//		return ((SemiGroupM) Dividend.Map(arg)).mulAt(Divisor.Map(arg));
	}

	/**This Function encapsulates the Prod Function.
	 * It multiplies the Results of the two Factor Functions.	 */
	public double Map (double arg) {
		double tmp = ((IFloatFunction) Dividend).Map(arg);
		if (Divisor == null) return tmp;
		return tmp / ((IFloatFunction) Divisor ).Map(arg);	//Divisor assumed to 1
	}

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() {
		String Return = Dividend.toString();
		if (Divisor != null) Return = "((" + Return + ")/(" + Divisor.toString() + "))";
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
		if (!(arg instanceof Quot)) return false;
		Quot arg_ = (Quot) arg;
		return Dividend.equals(arg_.Dividend) &&
			   Divisor .equals(arg_.Divisor); }

	/**Returns an alternative Representation that is easier to simplify
	 * Rules implemented here:
	 * Quotient = Dividend / Divisor:
	 * Functions are Divided directly, if possible
	 * a / 1 =  a
	 * a /-1 = -a
	 *  1/ a =  a^-1
	 * -1/ a = -a^-1 */
	public IFunction simplify() {	//The Sequence of Operations is critical!
		if (Dividend!= null) Dividend= Dividend.simplify();
		if (Divisor != null) Divisor = Divisor .simplify();
		if((Dividend== null) ||
		   (Dividend== CCountAble.One) ||
			CCountAble.One.equals (Dividend))
			return new CatDerive(Inv.INV, (IDeriveAble) Divisor); //1 / b = b^-1
		if((Divisor == null) ||
		   (Divisor == CCountAble.One) ||
			CCountAble.One.equals (Divisor)) return Dividend;	//a / 1 = a
		if (Dividend.equals(Divisor)) return CCountAble.One;	//a / a = 1
		if((Divisor  instanceof IMeasurAble) &&
		   (Dividend instanceof IMeasurAble))  //can divide both directly
			return new CMeasurAble(((IMeasurAble) Dividend).getDouble() /
									((IMeasurAble) Divisor ).getDouble());
		if (Dividend instanceof Cat) {	//not necessary to use Algebra.convertArg(),
			Cat cDividend = (Cat) Dividend; //since Algebra.equals() resolves that
			if (cDividend.outer() ==  Inv.INV) 	//(1/b)/a = 1 /(a*b)
				return	new CatDerive(Inv.INV,	//we don't do -b - a = -(a+b)!!!
						new Prod (Divisor,			//which is the equivalent
								 cDividend.inner()));
			if (cDividend.outer().equals(Neg.NEG))	//(-a)/b = -(a/b)
				return new CatDerive(Neg.NEG,
									 new Quot( cDividend.inner(),
												Divisor));
		}
		if  (Divisor instanceof Cat)	//Dividend/Divisor
		{	//not necessary to use Algebra.convertArg(), since Algebra.equals() resolves that
			Cat cDivisor = (Cat) Divisor;
			if (cDivisor.outer().equals(Inv.INV))		//a /(1/b) = a * b
				return new Prod(Dividend, cDivisor.inner());
			if (cDivisor.outer() ==  Neg.NEG)	//a / (-b) = -(a/b)
				return new CatDerive(Neg.NEG,
									 new Quot(Dividend,
											 cDivisor.inner()));
		}

		if (Dividend instanceof IGroupM) {
			if (((IGroupM)Dividend).isOne())	//1 / a = a^-1
				return	new CatDerive(Inv.INV, (IDeriveAble) Divisor);
			if (Dividend == CCountAble._One) 	//-1 / a = -1/a
				return	new CatDerive(Neg.NEG,
						new CatDerive(Inv.INV, Divisor));
			if (Dividend.canProcess(Divisor))
				return (IFunction) ((IGroupM) Dividend	).divAt(Divisor);
		}

		if (Divisor  instanceof IGroupM) {
			if (((IGroupM)Divisor ).isOne()) return Dividend;	//a / 1 =  a
			if (Divisor == CCountAble._One) 	//a /-1 = -a
				return new CatDerive(Neg.NEG, Dividend);
			if  (Divisor.canProcess(Dividend))
				return (IFunction) ((IGroupM) Divisor	).divAt(Dividend).invAt();
		}
		return this; }

	/**Returns the Inverse Function to this one:
	 * The Inverse of a Quotient is not well defined,
	 * since there are two Operands.
	 * If one Operand is constant, you can invert it globally
	 * If both Operands are monotonous you can invert it locally!	 */
	public IInvertAble getInverse() {
		if (Inverse != null) return Inverse;
		if (Dividend instanceof Const)	//both implementations are equivalent!!!
//			Inverse = new CatDerive(((IInvertAble) Divisor).getInverse(),
//									   new Quot(Dividend, Identity.Identity));
			Inverse = new CatDerive(((IInvertAble) Divisor).getInverse(),
					  new CatDerive(new MulAt(Dividend.Map(null)), Inv.INV));
		if (Divisor instanceof Const)
			Inverse = new CatDerive(((IInvertAble) Dividend).getInverse(),
									new MulAt(Divisor));
		if (Inverse != null) return Inverse;
		throw new AbstractMethodError();
	}

	/**Returns the full Derivative of this Function
	 * Uses the Quotient Rule: (f/g)' =(f'*g-f*g')/g^2		 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative;
		IDeriveAble r = ((IDeriveAble) Divisor ).getDerivative();
		IDeriveAble d = ((IDeriveAble) Dividend).getDerivative();
		if (r == CCountAble.Zero) Derivative = new Quot (d, Divisor); else
		if (d == CCountAble.Zero)
		Derivative = new CatDerive (Neg.NEG,
					 new Prod(Dividend,
					 new Quot(r,
					 new CatDerive(Square.SQUARE,  (IDeriveAble) Divisor)))); else
		Derivative = new Quot
					(new Diff
					(new Prod(Divisor	, d),
					 new Prod(Dividend	, r)),
					 new CatDerive(Square.SQUARE,  (IDeriveAble) Divisor));
		return Derivative; }

	/**Returns the Integral	 */
	public IDeriveAble getIntegral() {	//TODO: implement this
		if (Integral != null) return Integral;
//		if (Dividend instanceof Const) return new Quot(Dividend,	((IDeriveAble) Divisor ).getIntegral());	//WRONG!!!
		if (Divisor  instanceof Const){setIntegral(new Quot(		((IDeriveAble) Dividend).getIntegral(), Divisor)); return Integral; }
/*		if (Dividend == ((IDeriveAble) Divisor).getDerivative()) //Int[f'/f] = ln(f())
			return	new CatDerive(BodyFuncs.Logarithm.Logarithm,
									 (IDeriveAble) Divisor);
*/		if (Integral != null) return Integral;
		throw new AbstractMethodError();
	}

}
