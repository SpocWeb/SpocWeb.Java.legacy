package function.derive.ring.body.vector;

import function.IFunction;
import function.derive.CCountAble;
import function.derive.IDeriveAble;
import function.derive.ring.CatDerive;

/**This Class adds partial derivability to the Cat Function.
 * It reduces Complexity by eliminating constant Factors on derivation.
 * Relies on / Checks for 'Dimension' to do the partial Operations.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: a6e1e964dc2fa08b316155deb3ec3a2027e7327db25680d9fa64bbcaab32921a
 * stale: false
 * tags: [code/function_composition, code/differential_integration]
 * concepts: [Partial Derivatives]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
final public class CatPartial
extends CatDerive
implements IPartialDerive {

	/**Constructor taking the two Factors	 */
	public CatPartial(IFunction outer, IFunction inner) {
		super(outer, inner); }

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
		tmp = new IPartialDerive[Dim]; System.arraycopy(Derivative, 0, tmp, 0, MaxDim); Derivative	= tmp;
		tmp = new IPartialDerive[Dim]; System.arraycopy(Integral  , 0, tmp, 0, MaxDim); Integral		= tmp;
		tmp = new IPartialDerive[Dim]; System.arraycopy(Inverse   , 0, tmp, 0, MaxDim); Inverse		= tmp;
		return (Dim = MaxDim); }

//	public IPartialDerive Derivative(int Dim)
	/**Returns the partial Derivative of this Function in Direction Dim	 */
	public IDeriveAble getDerivative(int Dim) {
		ensureCapacity(Dim+1);	//calculate Derivative using the Kettenregel: F(g())' = g'()*F'(g())
		if (Derivative[Dim] != null) return Derivative[Dim];
		Derivative[Dim] = ((IPartialDerive) inner).getDerivative(Dim);
		if (outer != null) { //ignore constant Factors (can be expressed as Multiplication by a costant Factor)
			IDeriveAble outD = Dimension.Derivative((IDeriveAble)outer, Dim);
			if (outD.getDerivative() == CCountAble.Zero)	//(C(g))' = C*g'
				 Derivative[Dim] =	new ProdPartial (Derivative[Dim], (IDeriveAble) outD);
			else Derivative[Dim] =	new ProdPartial (Derivative[Dim],	//f(g)' = g'*f'(g)
									new  CatPartial (outD, (IDeriveAble) inner));
		}
		return Derivative[Dim]; }

	/**Returns the partial Integral of this Function in Direction Dim	 */
	public IPartialDerive getIntegral(int Dim) { //
		ensureCapacity(Dim+1);
		if (Integral[Dim] != null) return Integral[Dim];

/*		//calculate Integral using the Chain Rule: F(g()) = Int[g'()*F'(g())]
		//linear outer Transformation is transparent
		IDeriveAble outD = ((IDeriveAble) outer).Derivative();
		if (outD.Derivative() == ConstCount.Zero) {
			setIntegral(new CatDerive((IDeriveAble) outer,
									 ((IDeriveAble) inner).getIntegral());
			return Integral; }
		//if g' is a constant Function, this is possible, otherwise it gets very hard!

		//linear inner Transformation can be expressed as Multiplication by a costant Factor
		//(f(g())'/g'() = f(g()) This solves all linear Transformations.
		IDeriveAble inD = ((IDeriveAble) inner).Derivative();
		if (inD.Derivative() == ConstCount.Zero) {
			setIntegral(new Quot(	//F(g)'/g' = f(g)
						new CatDerive (((IDeriveAble) outer).getIntegral(),
										(IDeriveAble) inner), inD);
			return Integral; }

*/		if (Integral[Dim] != null) return Integral[Dim];
		throw new AbstractMethodError();
	}

	/**Returns the partial Inverse Function to this one in Direction Dim
	 * i.e. the Function that returns the identical Mapping,
	 * if concatenated with this Function (at least locally)	 */
	public IPartialDerive getInverse(int Dim) {
		ensureCapacity(Dim+1);
		if (Inverse[Dim] != null) return Inverse[Dim];
		Inverse[Dim] = new CatPartial (
			(IDeriveAble) ((IPartialDerive) inner).getInverse(Dim),
			(IDeriveAble) ((IPartialDerive) outer).getInverse(Dim));
		return Inverse[Dim];	//an Inverse is always possible if both inverses exist.
	}

}
