package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.IGroup;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
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

/**This Class encapsulates the Prod Function: Product = Factor1 * Factor2
 * It multiplies two Functions given in the Constructor.
 * It also implements the Derivation after the Product Rule.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:35:20Z
 * digest: 59c3e8d2c9164db372ffb31ec67c2b0b90302aab2ce9d3e59416c1a54f70d603
 * stale: false
 * tags: [code/function_composition, code/derivative_calculation]
 * concepts: [Function Algebra, Product Rule]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * It reduces Complexity by eliminating Factors of 1 and null.	 */
public class Prod
extends ADeriveAble {

	/** First Factor	 */
	public IFunction Factor1;

	/** Second Factor	 */
	public IFunction Factor2;

	/**Constructor taking the two Factors	 */
	public Prod(IFunction Factor1, IFunction Factor2) {	//if, then Factor2 is null
		if ((Factor1 == null)
//		||	(Factor1 == One.One)	//using null instead of One could lead to misunderstandings!
								){Factor1 = Factor2; Factor2 = null;}
		this.Factor1 = Factor1;
		this.Factor2 = Factor2;
	}

	/**This Function encapsulates the Sum Function.
	 * It adds the Results of the two Factor Functions.	 */
	public Object Map (Object arg) {
		Object tmp = Factor1.Map(arg);
		if (Factor2 == null) return tmp;
		return ((ISemiGroupM) tmp).mulAt(Factor2.Map(arg));	//Factor2 assumed to 1
		//the following is equivalent, but harder to realize with a non C Language
//		if (Factor2 == null) return Factor1.Map(arg);	//Factor2 assumed to 1
//		return ((SemiGroupM) Factor1.Map(arg)).mulAt(Factor2.Map(arg));
	}

	/**This Function encapsulates the Prod Function.
	 * It multiplies the Results of the two Factor Functions.	 */
	public double Map (double arg) {
		double tmp = ((IFloatFunction) Factor1).Map(arg);
		if (Factor2 == null) return tmp;
		return tmp * ((IFloatFunction) Factor2).Map(arg);	//Factor2 assumed to 1
	}

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
		if (!(arg instanceof Prod)) return false;
		Prod arg_ = (Prod) arg;
		return Factor1.equals(arg_.Factor1) &&
			   Factor2.equals(arg_.Factor2); }

	/**Returns the textual "((Factor1)*(Factor2))" representation of this Product.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() {
		String Return = Factor1.toString();
		if (Factor2 != null) Return = "((" + Return + ")*(" + Factor2.toString() + "))";
		return Return; }

	/**Returns an alternative Representation that is easier to simplify
	 * Rules implemented here:
	 * Functions are Multiplied directly, if possible
	 * a * 1 = 1 * a = a
	 * (-1)*a = a * (-1) = -a
	 * a * 1/b = 1/b * a = a / b	 */
	public IFunction simplify()
	{	//The Sequence of Operations is critical!	//more special Cases in the Beginning...
		if  (Factor1 != null) Factor1 = Factor1.simplify();
		if  (Factor2 != null) Factor2 = Factor2.simplify();
		if ((Factor1 == null)  ||
		    (Factor1 == CCountAble.One) ||
			 CCountAble.One.equals (Factor1)) return Factor2;	//1 * b = b
		if ((Factor2 == null)  ||
		    (Factor2 == CCountAble.One) ||
			 CCountAble.One.equals (Factor2)) return Factor1;	//a * 1 = a
		if ((Factor1 instanceof IGroupM) && (((IGroupM)Factor1).isOne ())) return Factor2;//1*a=a
		if ((Factor2 instanceof IGroupM) && (((IGroupM)Factor2).isOne ())) return Factor1;//a*1=a
		if ((Factor1 instanceof IGroup ) && (((IGroup )Factor1).isZero())) return Factor1;//a*0=0
		if ((Factor2 instanceof IGroup ) && (((IGroup )Factor2).isZero())) return Factor2;//0*a=0
		if ((CCountAble._One == Factor1) ||
			 CCountAble._One.equals(Factor1)) return new CatDerive(Neg.NEG, (IDeriveAble) Factor2);//(-1)*a = -a
		if  (Factor2.equals(CCountAble._One)) return new CatDerive(Neg.NEG, (IDeriveAble) Factor1);//a*(-1) = -a
		if ((Factor1 instanceof IMeasurAble) &&
			(Factor2 instanceof IMeasurAble)) //multiply both Factors directly
			return new CMeasurAble( ((IMeasurAble) Factor1).getDouble() *
									((IMeasurAble) Factor2).getDouble());
		//a * 1/b = 1/b * a = a / b
		if  (Factor1 instanceof Cat) {	//not necessary to use Algebra.convertArg(),
			Cat cFactor1 = (Cat) Factor1; //since Algebra.equals() resolves that
			if (cFactor1.outer()  == Inv.INV) 	//(1/b)*a = a / b
				return new Quot(Factor2, cFactor1.inner());
			if (cFactor1.outer()  == Neg.NEG)	//(-a)*b = -(a*b)
				return new CatDerive(Neg.NEG,
										new Prod(cFactor1.inner(),
													   	Factor2));
		}
		if  (Factor2 instanceof Cat)
		{	//not necessary to use Algebra.convertArg(), since Algebra.equals() resolves that
			Cat cFactor2 = (Cat) Factor2;
			if (cFactor2.outer()  == Inv.INV)		//a * (1/b) = a/b
				return new Quot(Factor1, cFactor2.inner());
			if (cFactor2.outer()  == Neg.NEG)	//a * (-b) = -(a*b)
				return new CatDerive(Neg.NEG,
										new Prod(Factor1,
												cFactor2.inner()));
		}
		if  (Factor1.canProcess(Factor2) &&
			(Factor1 instanceof ISemiGroupM))
			return (IFunction)((ISemiGroupM) Factor1).mulAt(Factor2);
		if  (Factor2.canProcess(Factor1) &&
			(Factor2 instanceof ISemiGroupM))
			return (IFunction)((ISemiGroupM) Factor2).mulAt(Factor1);
		return this; }

	/**Returns the Inverse Function to this one:
	 * The Inverse of a Product is not well defined,
	 * since there are two Operands.
	 * If one Operand is constant, you can invert it globally
	 * If both Operands are monotonous you can invert it locally!	 */
	public IInvertAble getInverse()	{
		if (Inverse != null) return Inverse;
		if (Factor1 instanceof Const)
			Inverse = new CatDerive(((IInvertAble) Factor2).getInverse(),
									   new MulAt(((IGroupM) Factor1.Map (null)).inv()));
		if (Factor2 instanceof Const)
			Inverse = new CatDerive(((IInvertAble) Factor1).getInverse(),
									   new MulAt(((IGroupM) Factor2.Map (null)).inv()));
		if (Inverse != null) return Inverse;
		throw new AbstractMethodError(); }

	/**Returns the full Derivative of this Function
	 * Uses the Product Rule: (f*g)' = f'*g+f*g'		 */
	public IDeriveAble getDerivative() {
		if (Derivative != null) return Derivative;
		IDeriveAble df1 = ((IDeriveAble) Factor1).getDerivative();
		IDeriveAble df2 = ((IDeriveAble) Factor2).getDerivative();
		if (df1 == CCountAble.Zero) setDerivative(df2); else
		if (df2 == CCountAble.Zero) setDerivative(df1); else
									setDerivative(new Sum(new Prod(df1, Factor2),
														  new Prod(df2, Factor1)));
		return Derivative; }

	/**Returns the Integral	 */
	public IDeriveAble getIntegral() {	//TODO: implement partial Integration...
		if (Integral != null) return Integral;
		IDeriveAble dFactor1 = ((IDeriveAble) Factor1).getDerivative();
		IDeriveAble dFactor2 = ((IDeriveAble) Factor2).getDerivative();
		//Constant Factor: Int(a*f(x)) = a*Int(f(x))
		if  (dFactor1   ==   CCountAble.Zero) { setIntegral(new Prod(Factor1, ((IDeriveAble) Factor2).getIntegral()         )); return Integral; }
		if  (dFactor2   ==   CCountAble.Zero) { setIntegral(new Prod(         ((IDeriveAble) Factor1).getIntegral(), Factor2)); return Integral; }
		if ((dFactor2   ==   Factor1) ||
			(dFactor2.equals(Factor1))) { //Int[f'*f] = f()^2/2
			setIntegral(new CatDerive(CMeasurAble.Half,
						new CatDerive(Square.SQUARE, (IDeriveAble) Factor1)));
			return Integral; }
		if ((dFactor1   ==   Factor2) ||
			(dFactor1.equals(Factor2))) { //Int[f'*f] = f()^2/2
			setIntegral(new CatDerive(CMeasurAble.Half,
						new CatDerive(Square.SQUARE, (IDeriveAble) Factor2)));
			 return Integral; }
		//partial Integration...
		if (Integral != null) return Integral;
		throw new AbstractMethodError(); }

}
