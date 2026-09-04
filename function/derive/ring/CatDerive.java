package function.derive.ring;

import function.IFunction;
import function.IInvertAble;
import function.derive.CCountAble;
import function.derive.Cat;
import function.derive.Const;
import function.derive.IDeriveAble;

/**Extends the Concatenation Function by Derivation Capabilities */
public class CatDerive
extends Cat
implements IDeriveAble {

	/**Constructor for a concatenated Function	 */
	public CatDerive(IFunction outer, IFunction inner) {
		super(outer, inner);
		if (inner == null) throw new AbstractMethodError();
		if (outer == null) throw new AbstractMethodError();
	}

	/**Returns the Inverse Function to this one:
	 * (f(g))^-1 = g^-1(f^-1)
	 * Since it is not always possible and potentially complicated,
	 * the Result is only calculated on Demand and cached.	 */
	public IInvertAble getInverse() {
		if (_Inverse == null)
		_Inverse = new CatDerive((IDeriveAble) ((IInvertAble) inner).getInverse(),
								 (IDeriveAble) ((IInvertAble) outer).getInverse());
		return _Inverse; }

	/**Cache for the Derivative	 */
	protected IDeriveAble Derivative;

    /**Sets the Derivative from outside
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setDerivative(IDeriveAble Derivative) {
		if (this.Derivative   ==   Derivative) return; //prevent Recursion
        if (this.Derivative   !=   null)
		if(!this.Derivative.equals(Derivative)) throw new IllegalStateException();
        	this.Derivative    =   Derivative;
			this.Derivative.setIntegral (this); } //close the Reference

	/**Returns the full Derivative of this Function,
	 * including an inner Function: f(g)' = g'*f'(g)	 */
	public IDeriveAble getDerivative() {
		//calculate Derivative using the Chain Rule: F(g())' = g'()*F'(g())
		if (Derivative != null) return Derivative;
		Derivative = ((IDeriveAble) inner).getDerivative();
		if (outer != null) {	//ignore constant Factors (can be expressed as Multiplication by a costant Factor)
			IDeriveAble outD = ((IDeriveAble) outer).getDerivative();
			if (outD instanceof Const)	//(C(g))' = C*g'
				 Derivative = new Prod  (Derivative, (IDeriveAble) outD);
			else Derivative = new Prod  (Derivative,	//f(g)' = g'*f'(g)
							  new CatDerive(outD, (IDeriveAble) inner));
		} Derivative.setIntegral(this);
		return Derivative; }

    /**Local Storage for the Integral to be set by setIntegral()     */
    protected IDeriveAble Integral;

    /**Sets the Integral from outside
     * This can be done only once, after that an IllegalStateException is thrown.     */
	public void	setIntegral(IDeriveAble Integral) {
        if (this.Integral == Integral) return; //prevent Recursion
        if (this.Integral != null)
		if(!this.Integral.equals(Integral)) throw new IllegalStateException();
        	this.Integral  = Integral;
			this.Integral.setDerivative (this);  }

	/**Returns the Integral
	 * Uses the Chain Rule for affine inner Functions: Int[f(g())] = F(g())/g'	 */
	public IDeriveAble getIntegral() {
		if (Integral != null) return Integral;

		//calculate Integral using the Chain Rule: F(g()) = Int[g'()*F'(g())]
		//linear outer Transformation is transparent
		IDeriveAble outD = ((IDeriveAble) outer).getDerivative();
		if (outD.getDerivative() == CCountAble.Zero) {
			setIntegral(new CatDerive((IDeriveAble) outer,
									 ((IDeriveAble) inner).getIntegral()));
			return Integral; }
		//if g' is a constant Function, this is possible, otherwise it gets very hard!

		//linear inner Transformation can be expressed as Multiplication by a costant Factor
		//(f(g())'/g'() = f(g()) This solves all linear Transformations.
		IDeriveAble inD = ((IDeriveAble) inner).getDerivative();
		if (inD.getDerivative() == CCountAble.Zero) {
			setIntegral(new Quot(	//F(g)'/g' = f(g)
						new CatDerive (((IDeriveAble) outer).getIntegral(),
										(IDeriveAble) inner), inD));
			return Integral; }
		if (Integral == null) throw new AbstractMethodError();
		return Integral; }

	/**Returns an alternative Representation that is easier to simplify
	 * Rules implemented here:
	 * Const(f(x)) = Const(x) = Const
	 * Factors (*2,*3, /2, /3, *-1) are multiplied directly, if possible
	 * f(f^-1) = id		including --a = a and  1 / (1 / a) = a and others.
	 * for   even Functions, inner Signs are eliminated
	 * for uneven Functions, inner Signs are moved outside
	 * concatenated Powers are one Power with multiplied Exponents
	 * multiplied Powers with the same Base are one Power with added Exponents	 */
	public IFunction simplify() {	//Constant inner Functions result in a constant Function!
		IFunction Result;
		if ((Result = super.simplify()) != this) return Result;
		if (outer instanceof Const) return outer;	//Constants needn't be simplified more!
		if (inner instanceof Const)
			return new Const(outer.Map(inner.Map(null)));	//Constants needn't be simplified more!
		Result = null;
		//Simplification Rules: see above
		if  (outer instanceof Cat) {	//not necessary to use Algebra.convertArg(), since Algebra.equals() resolves that
			Cat cOuter = (Cat) outer;
			if (cOuter.outer() == Neg.NEG)//(-a)(b) = -a(b)	//get the Sign out of the Cat
				Result = new CatDerive(Neg.NEG,
						 new CatDerive((IDeriveAble) cOuter.inner(),
									   (IDeriveAble)        inner)); else
			if (cOuter.outer() == Inv.INV)	//(1/a)(b) = 1/(a(b))	//get the Inverse out of the Cat
				Result = new CatDerive(Inv.INV,
						 new CatDerive( (IDeriveAble) cOuter.inner(),
										(IDeriveAble)        inner));
		} else	//-(a-b) = (b-a)	//get the Negative out of the Difference
		if (outer == Neg.NEG){
		if (inner instanceof Diff) {	//-(a-b) = (b-a)
			Diff DF = (Diff) inner;
			Result = new Diff(  (IDeriveAble) DF.Subtrahend,
								(IDeriveAble) DF.Minuend);
		}
/*		if (inner instanceof Sum)	//either this Conversion, or the one in Diff
		{	//-(a+b) = -a-b = -b-a
			Sum SM = (Sum) inner;
			if (SM.Summand1 instanceof
			Result = new Sum ((IDeriveAble) SM.Summand1,
									(IDeriveAble) SM.Summand2);
		}*/}
		if (outer == Inv.INV ) {
		if (inner instanceof Quot) {	//1/(a/b) = (b/a)
			Quot QT = (Quot) inner;
			Result = new Quot((IDeriveAble) QT.Divisor,
									(IDeriveAble) QT.Dividend);
		}
/*		if (inner instanceof Sum)	//either this Conversion, or the one in Diff
		{	//1/(a*b) = (1/a)/b = (1/b)/a
			Prod PD = (Prod) inner;
			if (PD.Factor1 instanceof
			Result = new Prod((IDeriveAble) SM.Factor1,
									(IDeriveAble) SM.Factor2);
		}*/}
		if (Result == null) Result = super.simplify();	//apply the Rules of the Parent
		return Result; } //.simplify();	} //simplify the Result even more.

}
