package function.derive.ring.body.vector;

import function.IFunction;
import function.derive.Const;
import function.derive.IDeriveAble;
import function.derive.ring.Sum;

/*
import BaseCopy.*;
import IFunction.*;
import RingFuncs.*;
*/
/**Extends Sum to process partial Derivation, Integration and Inversion.
 * Keeps a Cache of partial Derivatives analogous to Sum.  */
final public class SumPartial
extends Sum
implements IPartialDerive {

	/**Constructor taking the two Summands	 */
	public SumPartial(IFunction Summand1, IFunction Summand2) {
		super(Summand1, Summand2); }

	//////////////////////////
	//	partial Functions:	//
	//////////////////////////

	/**Size of the Cache for Derivatives	 */
	protected int MaxDim = 0;
//	protected IPartialDerive Derivative	[] = new IPartialDerive[MaxDim];
	protected IDeriveAble	Derivative	[] = new IPartialDerive[MaxDim];
	protected IPartialDerive Integral	[] = new IPartialDerive[MaxDim];
	protected IPartialDerive Inverse		[] = new IPartialDerive[MaxDim];

	/**Ensures Space for the given Number of Dimensions	 */
	public int ensureCapacity(int Dim) {
		if (Dim <= MaxDim) return MaxDim;
		IPartialDerive[] tmp;
		tmp = new IPartialDerive[MaxDim]; System.arraycopy(Derivative, 0, tmp, 0, Dim); Derivative	= tmp;
		tmp = new IPartialDerive[MaxDim]; System.arraycopy(Integral	, 0, tmp, 0, Dim); Integral		= tmp;
		tmp = new IPartialDerive[MaxDim]; System.arraycopy(Inverse	, 0, tmp, 0, Dim); Inverse		= tmp;
		return (Dim = MaxDim); }

//	public IPartialDerive Derivative(int Dim)
	/**Returns the partial Derivative of this Function in Direction Dim	 */
	public IDeriveAble getDerivative(int Dim) {
		ensureCapacity(Dim+1);
		if (Derivative[Dim] != null) return Derivative[Dim];
		Derivative[Dim] = ((IPartialDerive) Summand1).getDerivative(Dim);
		if ((Summand2 != null) && !(Summand2 instanceof Const))
			if (!((Summand2 instanceof Dimension) &&	//optimization to avoid Additions of Zero
				(((Dimension) Summand2).Dim != Dim)))	//which of course are also simplified!!!
				Derivative[Dim] = new SumPartial (Derivative[Dim],
							 ((IPartialDerive) Summand2).getDerivative(Dim));
		return Derivative[Dim]; }

	/**Returns the partial Integral of this Function in Direction Dim	 */
	public IPartialDerive getIntegral(int Dim) {	//
		ensureCapacity(Dim+1);
		if (Integral[Dim] != null) return Integral[Dim];
		Integral[Dim] = ((IPartialDerive) Summand1).getIntegral(Dim);
		if ((Summand2 != null) && !(Summand2 instanceof Const)) {
			Integral[Dim] = new SumPartial (Integral[Dim],
						 ((IPartialDerive) Summand2).getIntegral(Dim));
			((SumPartial) Integral[Dim]).Derivative[Dim] = this;	//provide the link back to the Derivative
		}
		return Integral[Dim]; }

	/**Returns the partial Inverse Function to this one in Direction Dim
	 * i.e. the Function that returns the identical Mapping,
	 * if concatenated with this Function (at least locally)	 */
	public IPartialDerive getInverse(int Dim) {
		ensureCapacity(Dim+1);
		if (Inverse[Dim] != null) return Inverse[Dim];
		//Implement Inverse...
		if (Inverse[Dim] != null) return Inverse[Dim];
		throw new AbstractMethodError(); }


}
