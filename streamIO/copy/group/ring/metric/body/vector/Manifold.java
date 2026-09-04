package streamIO.copy.group.ring.metric.body.vector;

import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.AIntRing;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.Interpolator;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.monoid.integer.Permutation;
import function.IFunction;
import function.byref.ByRefInt;

/**
  * Title: noname2<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
/** This Class defines Methods that mix the Elements.
  *
  */
public class Manifold
extends Tensor { //AManifold {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constructor	*/
	public Manifold(int Grad, IIntRing element) {

	}

	/** @return a new Tensor S that consists of as many Dimensions as the Raster has.
	  *
	  * Recursive static Helper Method to sample a Function on a multidimensional Raster.
	  * Similar to the Plot Routine, but hands over S and an Index,
	  * instead of the Item of S directly, because the Tensor is being populated!
	  *
	  * In each Recursion a new Dimension V[dim] is looped through it's Raster.
	  * At dim = -1 the Function is evaluated at V and put into the Tensor S
	  * at the Position Index.
	  * Note: the Argument to f is a Vector V
	  *
	  * The Algorithm is the same as in 'MathGraph3.Coordinates3D.PlotOrdered'
	  * or 'MathGraph3.Coordinates3D.Plot' */
	public static void sample(IFunction f, Tensor  V, Tensor S, int Index, Manifold[] Raster, int dim) {
		if (dim < 0) {
			S.a[Index] = (IIntRing) f.Map(V); return; }
		int i = Raster [dim].getDim();
		Tensor T;
		if (Index < 0) {T = S; T.setDim(i, false, false);}
		else S.a[Index] = (T = new Manifold(i, Raster[0].a[0]));//, i));
		do 		//Loop descending, makes check faster
		{	//T is a helper Tensor to collect the Result
			V.a [dim] = Raster[dim].a[i];
			sample (f, V, T, i, Raster, dim-1);
		} while (--i >= 0);
	}

	/** @return the Product of all Powers of the Items in x
	  * with the Indices = Prod(x [i]^n [i])
	  * @see Permutation.Multi_Fakt
	  * @see Permutation.Multi_ABS
	  */
	public IIntRing Multi_Pow  (Permutation P) {
		if (P.getDim() != mDim) throw new AbstractMethodError();
		IIntRing Prod = (IIntRing) Carry.one();
		int i = -1;
		while (++i <= mDim)
			Prod.mulAt(a[i].Pow(P.a[i]));
		return Prod; }

	/** @return  the Sum of all Elements in the Tensor	 */
	public IIntRing Sum() {
		int j = mDim;
		AIntRing Sum = (AIntRing) a[j].copy();
		while (--j >= 0)
			Sum.addAt(a[j]);
		return Sum; }

	/** @return  the Product of all Elements in the Tensor	 */
	public IIntRing Prod() {
		int j = mDim;
		IIntRing Prod = (IIntRing) a[0].copy();
		while (--j >= 0)
			Prod.mulAt(a[j]);
		return Prod; }

	//////////////////////////////////
	//	Differential Operations:	//
	//////////////////////////////////

	/**Level of Differentiation, used to determine, to which point the original Vector waa valid	 */
	private int diffLevel = 0;

	/**Factorial of diffLevel, used to scale the upper Values,
	 * so they can directly be used for inter/extrapolation	 */
	private ByRefInt factorial = new ByRefInt(1);

	/**Flag to indicate periodic Bounds	 */
	public boolean periodic;

	/**Returns the Derivate Vector of this Manifold in Place:
	 * derive(i)= dy(i)/dx(i) = (a(i) - a(i+1))/(x(i) - x(i+1))
	 * The Derivative Vector has one Item less than the original Vector.
	 * For complete Reversibility the last Item is preserved.
	 *
	 * The Argument is not the X Vector, but the inverse of it's difference Vector.
	 * That way multiple derivations can re-use this Vector.
	 *
	 * This Operation can be iterated as often as wanted,
	 * but the number of Items in the Vector decreases until zero is reached.	 */
	public Manifold deriveAt(Manifold invDiffX) {
		return (Manifold) diffAt().mulAt(invDiffX); }

	/**Returns the Derivate Vector of this Manifold in Place:
	 * derive(i)= dy(i)/dx(i) = (a(i) - a(i+1))/(x(i) - x(i+1))
	 * The Derivative Vector has one Item less than the original Vector.
	 * For complete Reversibility the last Item is preserved.
	 *
	 * The Argument is not the X Vector, but it's difference Vector.
	 *
	 * This Operation can be iterated as often as wanted,
	 * but the number of Items in the Vector decreases until zero is reached.	 */
	public Manifold integrateAt(Manifold diffX) {
		return(Manifold)
			 ((Manifold)mulAt(diffX)).summAt();}

	/**Returns the full Difference Vector of this Manifold in Place
	 * The full Difference Vector consists of all Derivatives.
	 * It can be used to calculate inter- and extrapolations with Horner(). 	 */
	public Manifold fullDiffAt() {
//		int i = 0;
//		ByRefInt fact = new ByRefInt(1);
		while (mDim >= 0) {
			diffAt(); } //a[mDim+1].divAt(fact); fact.Value*=(++i);}
//		a[0].divAt(fact);
		return this; }

	/**Returns the full Difference Vector of this Manifold in Place
	 * The full Difference Vector consists of all Derivatives.
	 * It can be used to calculate inter- and extrapolations. 	 */
	public Manifold fullDiff() { return ((Manifold)copy()).fullDiffAt(); }

	/**Returns the Difference Vector of this Manifold in Place: diff(i)= a(i) - a(i+1)
	 * The Difference Vector has one Item less than the original Vector.
	 * For complete Reversibility the last Item is preserved.	 */
	public Manifold diffAt() {
		if (mDim < 0) return this;
		IIntRing tmp2 = a[0];
		int i = 0; while (++i <= mDim)
			tmp2.subAt(tmp2 = a[i]); //== a[i].subAt(a[i+1]);
		tmp2.divAt(factorial); mDim--;	//divide the last Item by the Factorial, so it can be used directly
		factorial.Value *= ++diffLevel;
		return this; }

	/**Returns the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	 * This is the reverse Operation to diffAt().
	 * The Integral has one Item more than this Vector.
	 * This last Item is new and initialized to zero, if it was not preserved
	 * from a previous diff Operation or initialized before.
	 * If you want to start Integration from a certain Value,
	 * it is faster to modify this start Value by modifying the last Item.	 */
	public Manifold summAt() {
		boolean startIs0;
		IIntRing tmp1, tmp2;
		setDim(mDim+1, true, false);	//preserve the higher Items, but don't set them to zero.
		tmp1 = a[mDim]; if (startIs0 = (tmp1 == null)) tmp1 = (IIntRing) a[0].zero();
		else if (diffLevel > 0) {factorial.Value /= diffLevel--; tmp1.mulAt(factorial);}
		int i = mDim;
		while (--i >= 0) {
			tmp2 = tmp1; tmp1 = a[i];	//a[i].addAt(a[i+1]);
			if (! startIs0) {tmp1.addAt(tmp2); startIs0 = false;}	//Optimization, if the first Item is zero
		}
		return this; }

	/**Returns the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	 * This is the reverse Operation to diffAt().
	 * The Integral has one Item more than this Vector.
	 * This last Item is new and initialized to zero, if it was not preserved
	 * from a previous diff Operation.	 */
//	public Manifold summ() {return ((Manifold) copy()).summAt();}

	/**Returns the Difference Vector of this Manifold: diff(i)= a(i+1) - a(i)
	 * The Difference Vector has one Item less than the original Vector.	 */
//	public Manifold diff() {return ((Manifold) copy()).diffAt();}

	/**Adds a Point (y0) to the Manifold. If the Manifold has been differentiated,
	 * all Points are differentiated 	 */
	public Manifold addAtPoint(IIntRing y0) {
		mDim += diffLevel;
		setDim (mDim + 1, true, false);
		IGroup tmp2, tmp1 = (a[mDim] = (AIntRing) y0.copy());
		int i = diffLevel;
		while (--i > 0) {tmp2 = tmp1; tmp1 = a[i].subAt(tmp2);}
		mDim -= diffLevel;
		return this; }

	/**Adds a Point (y0, x0) to the Difference Vector.
	 * The x Coordinate is given implicitly by the inverse Coordinate Differences
	 * in invDiffX. 	 */
	public Manifold addAtPoint(IIntRing y0, IIntRing x0, Manifold x) {
		x.addAtPoint(x0);
		mDim += diffLevel;
		setDim (mDim + 1, true, false);
		ISemiGroupM tmp2, tmp1 = a[mDim] = (AIntRing) y0.copy();
		int i = diffLevel;
		while (--i > 0) {tmp2 = tmp1; tmp1 = ((IGroupM) a[i].subAt(tmp2)).divAt(x.a[i]);}
		mDim -= diffLevel;
		return this; }

	/**Calculates the Value of this Manifold at the Point x,
	 * using the already calculated Differences at equidistant Points.
	 * Gives best results, if the Manifold has been differenced all through,
	 * because only the higher Coefficients are used.
	 * This is well suited for the repetitive Calculation of interpolating Values,
	 * but for a single interpolated Value, it is better to use Inter/Extrapolation
	 * with either Polynomial or Rational Functions.
	 * The Division by the factorials is done once, when this function is differenced! 	 */
	public IIntRing Horner(IIntRing x, IIntRing x0, IIntRing h) {
		int i = mDim; 	//coordinate independent transformed coordinate!
		ByRefInt j = new ByRefInt(i);	//Because of using backward differences, I have to use (x0-x)
		IIntRing t		= (IIntRing)((IGroupM)x0.sub(x)).divAt(h);
		if (i >= 0) x.addAt(j);
		IIntRing Result = (IIntRing) a[++i].copy();
		j.Value = diffLevel;
		while (--j.Value > 0)
		{((IGroup)Result.mulAt(t.inc())).addAt(a[++i]); }
		return Result; }

	/**Constructor building the Interpolation Polynom
	 * from the Samples given in this Manifold and y.	 */
	public Interpolator Interpolator(Manifold y_) {
		return new Interpolator (a, y_.a, mDim); }


}
