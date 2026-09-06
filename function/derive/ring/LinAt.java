package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.IRing;
import streamIO.copy.groupM.IGroupM;
import function.IFunction;
import function.IInvertAble;
import function.byref.ByRefDouble;
import function.derive.AStatic;

/**This Class encapsulates the Affine Function for Stretching and Shifting.
 * LinAt(a, b) = a*x + b
 * It can be used to stretch the Argument Range of a concatenated Function.
 * If b is 'null', it is assumed to 0 and no Shifting takes place.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:34:14Z
 * digest: 18ddc8ea919bde4f41eb76bc9dd7f8c5e1037068b1aa53eafeff05d7deaf6bf8
 * stale: false
 * tags: [code/mathematical_function, code/derivable_function_contract]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * If a is 'null', it is assumed to 1 and no Stretching takes place.	 */
public class LinAt
extends AStatic
{ //implements Operator { //ADeriveAble {

	/**Parameter for Shifting.
	 * If a is 'null', it is assumed to 0 and no Shifting takes place.	 */
	protected Object a;

	/**Parameter for Stretching.
	 * If b is 'null', it is assumed to 1 and no Stretching takes place.	 */
	protected Object b;

	/**Empty Constructor	 */
//	public LinAt(){}

	/**Initializing Constructor	 */
	public LinAt(Object a_, Object b_) { this(a_, b_, null); }

	/**Initializing Constructor	 */
	public LinAt(Object a_, Object b_, IInvertAble Inverse) {
		if (a_ instanceof IFunction) throw new AbstractMethodError();
		if (b_ instanceof IFunction) throw new AbstractMethodError();
		a = a_; //.copy();
		b = b_; //.copy();
	 	//Since a and b are fixed, they can simply be divided again:
	 	//x*a + b = y <=> x = (y-b)/a = y* (1/a) + (-b/a)	 */
		a_ = ((IGroupM) a ).inv();
		b_ = ((IGroup ) b ).neg();
			 ((IGroupM) b_).mulAt(a_);
		if (Inverse == null)
			Inverse = new LinAt(a_, b_, this);
		setInverse   (Inverse);
		setDerivative(new Algebra(a));
		this.simple = new CatDerive(new AddAt(b), new MulAt(a)); //these can be simplified again!
		//the Integral can be calculated from the simple Representation
		//Homogeneous: Int(H*x) = H*Int(x)	= H*x^2/2
		//Watch out: a*x^2 + 2b*x + c != (a*x+b)^2 = a^2 * x^2 + 2a*b*x + b^2
	}

	//	Interface IFunction

	/**This Function encapsulates the shifting / adding Function: a*x+b
	 * It multiplies the Argument and adds a Constant to it.
	 * It can be used to shift and stretch the Argument Range of a concatenated Function.	 */
	public Object Map (Object arg) {
		if (a == null) return AddAt.ADD_AT(arg, b);	//this is already done in LinAt() for all concrete Classes: BodyDouble, RingLong, Fraction, gAdic, Polynom, Tensor and Complex
		if (b == null) return MulAt.MUL_AT(arg, a);	//this is already done in LinAt() for all concrete Classes: BodyDouble, RingLong, Fraction, gAdic, Polynom, Tensor and Complex
		return ((IRing) arg).LinAt(a, b); }

	//	Interface IFloatFunction

	/**This Function represents the linear Function: a*x+b  */
	public double Map(double x) { return x*ByRefDouble.GET_DOUBLE(a) +
		ByRefDouble.GET_DOUBLE (b); }

	/**Returns the Derivative of the Function	 */ //relies on assumptions.
	public double getDerivative(double x) { return ByRefDouble.GET_DOUBLE(a); }

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double FuncDerive (double x, ByRefDouble Derivative) {
		return x * (Derivative.Value = ByRefDouble.GET_DOUBLE(a)) +
			ByRefDouble.GET_DOUBLE(b); } //Map(x); }

	/**Returns the textual "b + a*" representation of this affine Function.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString()	{ return b + " + " + a + "*";}

}
