package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.IODE;
import function.IFloatFunction;
import function.IFunction;
import function.derive.AFuncRel;
import function.vector.IBinaryOpFloat;
import function.vector.IFloatVectorField;

/**ODE Definition for a Time-invariant Function y' = f(x,y) = f(y),
 * The Derivative is not time(x)-dependent and described as a Vector Function
 */
final public class Function2ODE
extends AFuncRel //ByRefFunc //only for the Value Member, not quite right!
implements IODE, IBinaryOpFloat {

	/** Cache for the Float Function 	 */
	protected IFloatVectorField VFktn;

	/**Constructor with the Function as Parameter	 */
	public Function2ODE(IFloatVectorField VFktn_) { this.VFktn = VFktn_; }

	/** Cache for the Float Function 	 */
	protected IFloatFunction Fktn;

	/**Constructor with the Function as Parameter	 */
	public Function2ODE(IFloatFunction Fktn_) { this.Fktn = Fktn_; }

	/**Constructor with the Function as Parameter	 */
	public Function2ODE(IFunction Fktn) { super(Fktn); }

	/**Implementation of the Function returning the Derivative	 */
	public void Funktion (IIntRing x, IIntRing y, IIntRing dydx) {
		dydx.copyAt(Value.Map(y)); }

	/**Returns the 1st Derivative in x of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public double Funktion(double x, double y) {
		return Fktn.Map(y); }

	/**Returns the 1st Derivative in x of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @return The Derivative at (x,y)
	 */
	public float Funktion(float x, float y) {
		return Fktn.Map(y); }

	/**Returns the 1st Derivative in x of all Coordinates of this Function at Point y
	 * For Time invariant Differential Equations x could be skipped.
	 * @param x The x Position at which to evaluate the DGL
	 * @param y The y Position at which to evaluate the DGL
	 * @param dydx The Derivative at (x,y)
	 */
	public void Funktion(double x, double[] y, double[] dydx) {
		VFktn.map(y, dydx); }

}
