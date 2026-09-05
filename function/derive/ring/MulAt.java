package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import function.IInvertAble;
import function.byref.ByRefDouble;
import function.derive.AStatic;
import function.derive.Identity;

/**This Class encapsulates the Stretching / Multiplying Function.
 * It multiplies a Constant to the Argument.
 * It can be used to stretch the Argument Range of a concatenated Function.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:34:58Z
 * digest: b27c8188bbfb17a94316880cd22a12423f6530a7ac38c7b3e0094952e224ae10
 * stale: false
 * tags: [code/function_composition, code/derivable_function_contract]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * If H is nothing, it is assumed to 1 and no Stretching takes place.	 */
public class MulAt
extends AStatic
{ //implements Operator { //ADeriveAble {

	/**Parameter for Shifting.
	 * If H is nothing, it is assumed to 0 and no Shifting takes place.	 */
	protected Object H;

	/**Empty Constructor	 */
//	public MulAt(){}

	/**Initializing Constructor	 */
	protected static ISemiGroupM MUL_AT(Object arg, Object Factor) {
		return ((ISemiGroupM) arg).mulAt(Factor); }

	/**Initializing Constructor
     * prevents Recursion by taking the Inverse directly!
     */
	public MulAt(Object Factor, IInvertAble Inverse) {
		H = Factor; //Int[a*x]=a*Int[x]
		this.simple = new Prod(new Algebra(H), Identity.IDENTITY); //can be simplified more easily!
		if (Inverse == null)
			Inverse = new MulAt ( ((IGroupM)Factor).inv(), this);
		setInverse   (Inverse); //new MulAt(((GroupM) H).inv())); //infinite Recursion
		setDerivative(new Algebra(H));
		setIntegral  (new CatDerive(this, Square.xx_2)); //Identity.Identity.getIntegral()));
	}

	/**Initializing Constructor	 */
	public MulAt(Object Factor) { this(Factor, new MulAt(((IGroupM) Factor).inv(), null)); }

	//	Interface IFunction

	/**This Function encapsulates the shifting / adding Function.
	 * It adds a Constant to the Argument.
	 * It can be used to shift the Argument Range of a concatenated Function.	 */
	public Object Map (Object arg) {
		if (H == null) return arg;	//this is already done in MulAt() for all concrete Classes: BodyDouble, RingLong, Fraction, gAdic, Polynom, Tensor and Complex
		return ((ISemiGroupM) arg).mulAt(H);	}

	//	Interface IFloatFunction

	/**This Function encapsulates the shifting / adding Function.
	 * It adds a Constant to the Argument.
	 * It can be used to shift the Argument Range of a concatenated Function.	 */
	public double Map (double arg) {
		if (H == null) return arg;	//this is already done in MulAt() for all concrete Classes: BodyDouble, RingLong, Fraction, gAdic, Polynom, Tensor and Complex
		return arg * ByRefDouble.GET_DOUBLE(H); }

	/**Returns the Derivative of the Function	 */ //relies on assumptions.
	public double getDerivative(double x) { return ByRefDouble.GET_DOUBLE(H); }

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double FuncDerive (double x, ByRefDouble Derivative) {
		return x * (Derivative.Value = ByRefDouble.GET_DOUBLE(H)); } //Map(x); }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Object: Implementation
////////////////////////////////////////////////////////////////////////////

	/**Returns the multiplier prefixed with an asterisk.
	 * @return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString() { return "*" + H; }

	/**Compares this Function's multiplier Factor to another MulAt's.
	 * @return  true when this Function equals the given Parameter.
	 * @since   JDK1.0	 */
	public boolean equals(Object arg) {
		if (! (arg instanceof MulAt)) {
			return false; }
		return H.equals(((MulAt) arg).H); }

}
