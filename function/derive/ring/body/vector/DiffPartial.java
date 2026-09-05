package function.derive.ring.body.vector;

import function.IFunction;
import function.derive.Const;
import function.derive.IDeriveAble;
import function.derive.ring.Diff;

/**
  * Extends Diff to process partial Derivation, Integration and Inversion.
  * Keeps a Cache of partial Derivatives analogous to Sum.
  * Relies on / Checks for 'Dimension' to do the partial Operations.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: df61794bb77f652a670f6d9f20119f6e872f25cd5fc5fc52ff2de9d66c691b45
  * stale: false
  * tags: [code/differential_integration]
  * concepts: [Partial Derivatives]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
final public class DiffPartial
extends Diff
implements IPartialDerive {

	/**Constructor taking the two Summands	 */
	public DiffPartial(IFunction Minuend, IFunction Subtrahend) {
		super(Minuend, Subtrahend); }

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
		if (Dim <= MaxDim) {
			return MaxDim; }
		IPartialDerive[] tmp;
		tmp = new IPartialDerive[MaxDim]; System.arraycopy(Derivative, 0, tmp, 0, Dim); Derivative	= tmp;
		tmp = new IPartialDerive[MaxDim]; System.arraycopy(Integral	, 0, tmp, 0, Dim); Integral		= tmp;
		tmp = new IPartialDerive[MaxDim]; System.arraycopy(Inverse	, 0, tmp, 0, Dim); Inverse		= tmp;
		return (Dim = MaxDim);
	}

//	public IPartialDerive Derivative(int Dim)
	/**Returns the partial Derivative of this Function in Direction Dim	 */
	public IDeriveAble getDerivative(int Dim) {	//
		ensureCapacity(Dim);
		if (Derivative[Dim] != null) return Derivative[Dim];
		Derivative[Dim] = ((IPartialDerive) Minuend).getDerivative(Dim);
		if ((Subtrahend != null) && !(Subtrahend instanceof Const)) //TODO: define this by Derivative == 0
			if ((!(Subtrahend instanceof Dimension) &&	//optimization to avoid Additions of Zero
				(((Dimension) Subtrahend).Dim != Dim))) //which of course are also simplified!!!
				Derivative[Dim] = new DiffPartial (Derivative[Dim],
							 ((IPartialDerive) Subtrahend).getDerivative(Dim));
		return Derivative[Dim];	}

	/**Returns the partial Integral of this Function in Direction Dim	 */
	public IPartialDerive getIntegral(int Dim) {	//
		ensureCapacity(Dim);
		if (Integral[Dim] != null) return Integral[Dim];
		Integral[Dim] = ((IPartialDerive) Minuend).getIntegral(Dim);
		if ((Subtrahend != null) && !(Subtrahend instanceof Const)) {
			Integral[Dim] = new DiffPartial (Integral[Dim],
						 ((IPartialDerive) Subtrahend).getIntegral(Dim));
			((DiffPartial) Integral[Dim]).Derivative[Dim] = this;	//provide the link back to the Derivative
		}
		return Integral[Dim]; }

	/**Returns the partial Inverse Function to this one in Direction Dim
	 * i.e. the Function that returns the identical Mapping,
	 * if concatenated with this Function (at least locally)	 */
	public IPartialDerive getInverse(int Dim) {
		ensureCapacity(Dim);
		if (Inverse[Dim] != null) return Inverse[Dim];
		//Implement Inverse...
		if (Inverse[Dim] != null) return Inverse[Dim];
		throw new AbstractMethodError();
	}

}
