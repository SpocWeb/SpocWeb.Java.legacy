package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ISemiGroup;
import function.ICountAble;
import function.IInvertAble;
import function.byref.ByRefDouble;
import function.derive.AStatic;
import function.derive.CCountAble;
import function.derive.Identity;

/**This Class encapsulates the Shifting / Adding by a fixed amount Function.
 * It adds a Constant to the Argument.
 * It can be used to shift the Argument Range of a concatenated Function.
 * If H is nothing, it is assumed to 0 and no Shifting takes place.	 */
public class AddAt
extends AStatic
{ //implements Operator { //ADeriveAble {

	/**Parameter for Shifting.
	 * If H is nothing, it is assumed to 0 and no Shifting takes place.	 */
	protected Object H;

	/**Static Operation of this Function	 */
	public static ISemiGroup ADD_AT(Object arg, Object Offset) {
		return ((ISemiGroup) arg).addAt(Offset); }

	//////////////////////
	//	Constructors	//
	//////////////////////

	/**Empty Constructor	 */
//	public AddAt(){}

	/**Initializing Constructor	 */
	public AddAt(Object Offset) { this(Offset, new AddAt(((IGroup) Offset).neg(), null)); }

	/**Initializing Constructor
     * prevents Recursion by taking the Inverse directly!
     */
	protected AddAt(Object Offset, IInvertAble Inverse) {
		H = Offset;
		this.simple = new Sum  (new Algebra(H), Identity.IDENTITY); //can be simplified more easily!
		if (Inverse == null)
			Inverse = new AddAt ( ((IGroup)Offset).neg(), this);
		setInverse   (Inverse); //new AddAt(((Group) H).neg())); //prevent Recursion by having a Constructor with Inverse!
		setDerivative(CCountAble.One); //both Integrals are valid!
		setIntegral  (new Sum (new MulAt(H), Square.xx_2)); // Int[H + x] = H*x + x^2/2
//		setIntegral  (new CatDerive(Square.xx_2, this);  // Int[H + x] = (H+x)^2/2
	}

	//	Interface IFunction

	/**This Function encapsulates the shifting / adding Function.
	 * It adds a Constant to the Argument.
	 * It can be used to shift the Argument Range of a concatenated Function.	 */
	public Object Map (Object arg)	{
		if (H == null) return arg;	//this is already done in addAt() for all concrete Classes: BodyDouble, RingLong, Fraction, gAdic, Polynom, Tensor and Complex
		return ((ISemiGroup) arg).addAt(H); }

	//	Interface IFloatFunction

	/**This Function represents the Square Function: x^2  */
	public double Map(double x) { return x + ByRefDouble.GET_DOUBLE(H); }

	/**Returns the Derivative of the Function	 */ //relies on assumptions.
	public double getDerivative(double x) { return ICountAble.ONE; }

	/** Calculates Function and Derivative at the same time,
	  * @return the Function Value directly and the Derivative ByRef	  */
	public double FuncDerive (double x, ByRefDouble Derivative) {
		Derivative.Value = ICountAble.ONE;
		return x + ByRefDouble.GET_DOUBLE(H); } //Map(x); }

	/**@return  The string representation of the Function.
	 * @since   JDK1.0	 */
	public String toString()	{ return "+" + H; }

}
