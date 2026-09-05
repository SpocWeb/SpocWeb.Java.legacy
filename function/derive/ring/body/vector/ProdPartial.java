package function.derive.ring.body.vector;

import function.IFunction;
import function.derive.Const;
import function.derive.IDeriveAble;
import function.derive.ring.Prod;

/**This Class adds partial derivability to the Prod Function.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: a6e1e964dc2fa08b316155deb3ec3a2027e7327db25680d9fa64bbcaab32921a
 * stale: false
 * tags: [code/differential_integration]
 * concepts: [Partial Derivatives, Product Rule]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * It reduces Complexity by eliminating constant Factors on derivation.	 */
final public class ProdPartial
extends Prod
implements IPartialDerive {

	/**Constructor taking the two Factors	 */
	public ProdPartial(IFunction Factor1, IFunction Factor2) {
		super(Factor1, Factor2); }

	//////////////////////////
	//	partial Functions:	//
	//////////////////////////

	/**Size of the Cache for Derivatives	 */
	protected int MaxDim = 0;
	protected IPartialDerive Derivative	[] = new IPartialDerive[MaxDim];
	protected IPartialDerive Integral	[] = new IPartialDerive[MaxDim];
	protected IPartialDerive Inverse	[] = new IPartialDerive[MaxDim];

	/**Ensures Space for the given Number of Dimensions	 */
	public int ensureCapacity(int Dim) {
		if (Dim <= MaxDim) 
			return MaxDim;
		IPartialDerive[] tmp;
		tmp = new IPartialDerive[Dim]; System.arraycopy(Derivative, 0, tmp, 0, MaxDim); Derivative	= tmp;
		tmp = new IPartialDerive[Dim]; System.arraycopy(Integral  , 0, tmp, 0, MaxDim); Integral		= tmp;
		tmp = new IPartialDerive[Dim]; System.arraycopy(Inverse   , 0, tmp, 0, MaxDim); Inverse		= tmp;
		return (Dim = MaxDim); 
	}

//	public IPartialDerive Derivative(int Dim)
	/**Returns the partial Derivative of this Function in Direction Dim	 */
	public IDeriveAble getDerivative(int Dim) {
		ensureCapacity(Dim+1);
		if (Derivative[Dim] != null) return Derivative[Dim];
		IPartialDerive f1 = null;
		IPartialDerive f2 = null;
//		if (Factor1 != null) f1 = ((IPartialDerive) Factor1).Derivative();
//		if (Factor2 != null) f2 = ((IPartialDerive) Factor2).Derivative();
		if (! (Factor2 instanceof Const)) //TODO: instead test for a 0 Derivative
			if (!(Factor2 instanceof Dimension))	//optimization to avoid Additions of Zero
				f1 = new ProdPartial(Factor1, ((IPartialDerive) Factor2).getDerivative(Dim));
			else if (((Dimension) Factor2).Dim == Dim)	//which of course are also simplified!!!
				 f1 = (IPartialDerive) Factor1;
		if (! (Factor1 instanceof Const))
			if (!(Factor1 instanceof Dimension))	//optimization to avoid Additions of Zero
				f2 = new ProdPartial(Factor2, ((IPartialDerive) Factor1).getDerivative(Dim));
			else if (((Dimension) Factor1).Dim == Dim)	//which of course are also simplified!!!
				 f2 = (IPartialDerive) Factor2;
		if (f1 == null) Derivative[Dim] = f2; else
		if (f2 == null) Derivative[Dim] = f1; else
						Derivative[Dim] = new SumPartial(f1, f2);
		return Derivative[Dim];
	}

	/**Returns the partial Integral of this Function in Direction Dim	 */
	public IPartialDerive getIntegral(int Dim) {	//
		ensureCapacity(Dim+1);
		if (Integral[Dim] != null) return Integral[Dim];
		Integral[Dim] = ((IPartialDerive) Factor1).getIntegral(Dim);
		if ((Factor2 != null) && !(Factor2 instanceof Const))
			if (!((Factor2 instanceof Dimension) &&	//optimization to avoid Additions of Zero
				(((Dimension) Factor2).Dim != Dim))) {	//which of course are also simplified!!!
				Integral[Dim] = new SumPartial (Integral[Dim],
							 ((IPartialDerive) Factor2).getIntegral(Dim));
				((SumPartial) Integral[Dim]).Derivative[Dim] = this;	//provide the link back to the Derivative
			}
		if (Integral[Dim] != null) return Integral[Dim];
		throw new AbstractMethodError(); }

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
