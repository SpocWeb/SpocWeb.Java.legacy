package streamIO.copy.group.ring.metric.body.vector;

import function.IIOrderAble;
import function.byref.ByRefInt;
import function.byref.ByRefObject;
import graphs.ICopy;

import java.security.InvalidParameterException;

import streamIO.AReSetAble;
import streamIO.IIStreamIn;
import streamIO.copy.ICopyAble;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.Interpolator;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.groupM.IGroupM;
import streamIO.copy.groupM.ISemiGroupM;
import streamIO.copy.order.IOrder;

/** 
 * Regular Vectors consist of unrelated Items (incompatible Domains) 
 * then there are Vectors you can rotate between (compatible Domains) 
 * then come (Meta-) Vectors that have the same Domain in each Dimension
 * (Function Space, Samplings).  
 * 
 * Extends the Tensor class with Methods that mix the Dimensions:
 * -Differential Operations
 * -statistical Operations
 * -Sorting
 * 
 * Polynoms redefine Multiplications,
 * because they are Algebras on their Arguments.
 * * Addition like Vectors
 * * but Convolution for a Product, which is commutable!
 * * Mapping defined
 * 
 * Matrix extends Tensor by defining
 * * Addition like Vectors
 * * Multiplication with a Scalar
 * * a linear Mapping for Tensor Arguments
 * * Concatenation of this Mapping is not commutable
 * 
 * known Subclasses: 
 * @see streamIO.copy.group.ring.metric.body.vector.VectorDbl
 */
public abstract class AManifold
extends ATensor
implements IManifold {

	///////////////////////////////////////////////////////////////////////////
	//  static statistical Methods
	///////////////////////////////////////////////////////////////////////////

	// @see AStreamIn.sortDegree() the for calculating the Percentage of ascending and descending Items.

	//to check Uniqueness you have to use a HashContainer or sort the Items

	//to check Overlap or Correlation or coVariance you have to create a Relation and check whether it is left or right unique

	//real Data introduces Fuzzyness into any boolean Property: Uniqueness, Monotony, Transitivity etc.


	//to normalize the Data you have to find out Minimum and Maximum (for equal)
	//or the Average (for gaussian)

	//Type Info

	//Null / not Null

	//Require only IOrderAble, Order, ByRefInt

	/** @return the Minimum of all Elements in this streamIO Min(i, x[i])
	  * This Implementation creates a Copy of the minimum Element of the streamIO */
	public static IOrder MinCopy(final IIStreamIn str) {
		AReSetAble.TRY_TO_RESET(str, "");
		IOrder Min = (IOrder) ((ICopy) str.nextItem()).Copy(); //
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			Min.MinAt(xi); } //MinAt is more effective than creating new Copies
		return Min;	}

	/** @return the Minimum of all Elements in this streamIO Min(i, x[i])
	  * This Implementation creates a Copy of the maximum Element of the streamIO */
	public static IOrder MaxCopy(final IIStreamIn str) {
		AReSetAble.TRY_TO_RESET(str, "");
		IOrder Max = (IOrder) ((ICopy) str.nextItem()).Copy(); //
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			Max.MaxAt(xi); }
		return Max;	}

	/** @return the Minimum and Maximum of all Elements in this streamIO Min(i, x[i])
	  * Requires only 1/3 of the Comparisons when using Min and Max
	  * This Implementation reuses the Elements of the streamIO
	  * Except for ByRefObject all Interfaces are already defined in AStreamIn  */
	final static public IIOrderAble MinMax(final IIStreamIn str, final ByRefObject Max_) {
		AReSetAble.TRY_TO_RESET(str, "");
		IIOrderAble Min, x1, x2;
		IIOrderAble Max = Min = (IIOrderAble) str.nextItem(); //
		while (EOI != (x1 = (IIOrderAble) str.nextItem()) ||  str.isValid()) {
			if(EOI == (x2 = (IIOrderAble) str.nextItem()) && !str.isValid()) {
				x2 = x1; //end Case for even Number of Items
			} else if (x2 ==x1) throw new InvalidParameterException("Cannot use this Method on Streams reusing the same Object!");   //
			if (x1.isLessThan(x2)) {
				if (  x1.isLessThan(Min)) Min = x1;
				if (! x2.isLessThan(Max)) Max = x2;
			}else{
				if (  x2.isLessThan(Min)) Min = x2;
				if (! x1.isLessThan(Max)) Max = x1;
			}
		}
		Max_.Value = Max;
		return Min;	}

	/** @return the Minimum of all Elements in this streamIO Min(i, x[i])
	  * Requires only 1/3 of the Comparisons when using Min and Max
	  * creates a Copies of the first Item
	  * and uses them to store the Minimum and Maximum
	  * But is not apted to process Streams that reuse the same Object! */
	public static IOrder MinMaxCopy(final IIStreamIn str, ByRefObject Max_) {
		AReSetAble.TRY_TO_RESET(str, "");
		IOrder Min = (IOrder) ((ICopy) str.nextItem()).Copy(); //
		IOrder Max = (IOrder) Min.Copy(); //
		Object x1;
		Object x2;
		while (EOI != (x1 = str.nextItem()) || str.isValid()) {
			x1 = ((ICopy) x1).Copy();
			if(IIStreamIn.EOI == (x2 = str.nextItem()) && !str.isValid()) {
				x2 = x1; }//end Case for even Number of Items
			if (((IOrder)x1).isLessThan(x2)) {
				Min.MinAt(x1);
				Max.MaxAt(x2);
			}else{
				Min.MinAt(x2);
				Max.MaxAt(x1);
			}
		}
		Max_.Value = Max;
		return Min;	}

	//the following Methods require IRing Operations

	/** @return the Product of all Elements in this streamIO Prod(i, x[i])
	  * All Elements of this streamIO should be positive.
	  * Multiplication does not create extinction.
	  */
	public static ISemiGroupM Prod(final IIStreamIn str, ByRefInt i) {
		AReSetAble.TRY_TO_RESET(str, "");
		if (i != null) i.Value = 1;
		ISemiGroupM ret = (ISemiGroupM) ((ICopy) str.nextItem()).Copy(); //subt(str.nextItem());
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
			ret.mulAt(xi); }
		return ret;	}

	//Declaration of Ring and Tensor is premature, moved this Method from Package Permutation to Vector

	/** @return the Sum of all Elements in this streamIO Sum(i, x[i])
	  * This is half as expensive as the renormed Sum, but may incur Extinction of Digits.
	  * Any Sum should be normed to a certain Origin (should lie in the Range):
	  * Sum(i, x[i]) == Sum(i, x[i] - x0) + n*x0
	  * where the first Sum would be very large and prone to Extinction
	  *  and the second Sum would ideally be Zero (when x0 is the Average).
	  */
	public static IGroup Sum(final IIStreamIn str, ByRefInt i) {
		AReSetAble.TRY_TO_RESET(str, "");
		IGroup ret = (IGroup) ((ICopy) str.nextItem()).Copy(); //subt(str.nextItem());
		if (i != null) i.Value = 1;
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
			ret. addAt(xi); } //double as fast
		return ret;	}

	/** @return the Sum of all Elements in this streamIO renormed by x0: Sum(i, x[i] - x0)
	  * This is double as expensive as the simple Sum, but avoids Extinction of Digits.
	  * Any Sum with many Elements should be normed to a certain Origin (should lie in the Range):
	  * Sum(i, x[i]) == Sum(i, x[i] - x0) + n*x0
	  * where the first Sum would be very large and prone to Extinction
	  *  and the second Sum would ideally be Zero (when x0 is the Average).
	  * The relative Accuracy of the Sum is usually the same as of the individual Elements,
	  * but the absolute Accuracy decreases with the Number of Elements,
	  * which is not a Problem for short Sums.
	  */
	public static IGroup Sum(final IIStreamIn str, ByRefInt i, Object x0) {
		AReSetAble.TRY_TO_RESET(str, "");
		if (i != null) i.Value = 1;
		IGroup ret =((IGroup) str.nextItem()).sub(x0); //
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
//			ret.addAt(((Group) x0).subt(xi); } //continually creates and destroys Objects!
			ret. addAt(xi); //double as expensive
			ret.subAt(x0); }
//		ret.addAt(x0.mulAt(i.Value)); //return the original Sum
/*		ret.divAt(i.Value);
		ret.addAt(x0); //return the Average
*/		return ret;	}

	/** @return the Squared Sum of all Elements in this streamIO renormed by x0 Sum(i, (x[i] - x0)^2)
	  * This is double as expensive as the simple Squared Sum, but avoids Extinction of Digits.
	  * Any Sum should be normed to a certain Origin (should lie in the Range):
	  *    Sum(i, (x[i] - X )^2)               (with x0 == X)
	  * == Sum(i, (x[i] - x0)^2) - n*(x0-X)^2
	  * == Sum(i,  x[i]^2)       - n*    X ^2  (with x0 == 0)
	  * where the first Sum is minimal
	  * 	  the third Sum would be VERY large and prone to Extinction
	  *  and the second Sum would be smaller and ideally Zero (when x0 is the Average)
	  */
	public static IGroup SqrSum(final IIStreamIn str, ByRefInt i, Object x0) {
		AReSetAble.TRY_TO_RESET(str, "");
		if (i != null) i.Value = 1;
		IGroup x0_ = (IGroup ) x0;
		IGroup ret = (IGroup )
			 ((IGroupM) x0_.sub(str.nextItem())).sqrAt(); //subt(str.nextItem());
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
			ret.addAt(((IGroupM) x0_.sub(xi)).sqrAt()); }
		return ret;	}

	/** @return the Sum of all Elements in this streamIO
	  * This is half as expensive as the renormed Sum, but may incur Extinction of Digits.
	  * Any Sum should be normed to a certain Origin (should lie in the Range):
	  *    Sum(i, (x[i] - X )^2)               (with x0 == X)
	  * == Sum(i, (x[i] - x0)^2) - n*(x0-X)^2
	  * == Sum(i,  x[i]^2)       - n*    X ^2  (with x0 == 0)
	  * where the first Sum would be very large and prone to Extinction
	  *  and the second Sum would ideally be Zero (when x0 is the Average).
	  */
	public static IGroup SqrSum(final IIStreamIn str, ByRefInt i) {
		AReSetAble.TRY_TO_RESET(str, "");
		if (i != null) i.Value = 1;
		IGroup ret = (IGroup) ((IGroupM) str.nextItem()).sqr(); //subt(str.nextItem());
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
			ret.addAt(((IGroupM) xi).sqrAt()); }
		return ret;	}

	/** @return the Sum of all Elements in this streamIO
	  * Additionally SqrSum is being calculated on the fly.
	  * This is half as expensive as the renormed Sum, but may incur Extinction of Digits.
	  * Any Sum should be normed to a certain Origin (should lie in the Range):
	  *    Sum(i, (x[i] - X )^2)               (with x0 == X)
	  * == Sum(i, (x[i] - x0)^2) - n*(x0-X)^2
	  * == Sum(i,  x[i]^2)       - n*    X ^2  (with x0 == 0)
	  * where the first Sum would be very large and prone to Extinction
	  *  and the second Sum would ideally be Zero (when x0 is the Average).
	  */
	public static IGroup SumSqrSum(final IIStreamIn str, ByRefInt i, Object x0, IGroup SqrSum) {
		AReSetAble.TRY_TO_RESET(str, "");
		if (i != null) i.Value = 1;
		IGroup x0_ =    (IGroup ) x0;
		IGroup ret =   ((IGroup )str.nextItem()).sub(x0); //
		SqrSum.copyAt(((IGroupM)ret).sqr()); //
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
			IGroupM df = (IGroupM) x0_.sub(xi);
			ret   .subAt(df);
			SqrSum. addAt(df.sqrAt()); }
		return ret;	}

	/** @return the Sum of all Elements in this streamIO
	  * Additionally SqrSum is being calculated on the fly.
	  * This is half as expensive as the renormed Sum, but may incur Extinction of Digits.
	  * Any Sum should be normed to a certain Origin (should lie in the Range):
	  *    Sum(i, x[i]) == Sum(i, x[i] - x0) + n*x0
	  *    Sum(i, (x[i] - X )^2)               (with x0 == X)
	  * == Sum(i, (x[i] - x0)^2) - n*(x0-X)^2
	  * == Sum(i,  x[i]^2)       - n*    X ^2  (with x0 == 0)
	  * where the first Sum would be very large and prone to Extinction
	  *  and the second Sum would ideally be Zero (when x0 is the Average).
	  */
	public static IGroup Moments(final IIStreamIn str, ByRefInt i, Object x0, IGroup Variance, IGroup Skewness, IGroup Kurtosis) {
		AReSetAble.TRY_TO_RESET(str, "");
		if (i != null) i.Value = 1;
		ISemiGroupM df, sqr;
		IGroup x0_ = (IGroup ) x0;
		IGroup ret =((IGroup )str.nextItem()).sub(x0); //
		df = (IGroupM) ret.copy();
		Variance.copyAt(sqr =  df.sqr()); //
		Skewness.copyAt(df .mulAt(sqr ));
		Kurtosis.copyAt(sqr.sqrAt());
		Object xi;
		while (EOI != (xi = str.nextItem()) || str.isValid()) {
			if (i != null) ++i.Value;
			df  = (IGroupM) x0_.sub(xi);
			sqr = df.sqr();
			ret     .subAt(df);
			Variance. addAt(sqr);
			Skewness.subAt(df.mulAt(sqr));
			Kurtosis. addAt(sqr.sqrAt()); }
		return ret;	}

	//the following Methods require MetricIRing

	/** @return the Index of the Element that came closest to x0 in this streamIO
	  * x0 returns the Distance to the closest Match in Place. */
	public static int search(final IIStreamIn str, final Object x0, final boolean copy) {
		AReSetAble.TRY_TO_RESET(str, "");
		IMetricIRing xi;
		IMetricIRing d1 = (IMetricIRing) str.nextItem();
		if (d1 == IIStreamIn.EOI)
			return -1;
		if (copy) {
			d1 = (IMetricIRing) d1.sub(x0);
		}else{ d1.subAt(x0); }
		d1.AbsVAt();
		int i, ret = i = 0;
		while (EOI != (xi = (IMetricIRing) str.nextItem()) || str.isValid()) {
			if (copy) {
				xi = (IMetricIRing) xi.sub(x0);
			}else{ xi.subAt(x0); } ++i;
			if (xi.AbsVAt().isLessThan(d1)) {
				d1.copyAt(xi); ret = i; } //d1 = xi;
		} ((ICopyAble) x0).copyAt(d1);
		return ret;	}

	//////////////////////////////
	//	Sampling of a Function	//
	//////////////////////////////

	/** Generates a Manifold by sampling f across x	 */
/*	public Manifold(intFunction f,  Manifold x) {	//preserve Internals of x0
		super(x);
		letGrad(x.mDim , false, false);
		int i = -1; while (++i <= mDim)
						a[i] = (IIntRing) f.map(x.a[i]); }

	/** Generates a Manifold by sampling f across X	 */
/*	public Manifold(Manifold X, intFunction f) {
		super(X);
		letGrad(X.getDim(), false, false);
		int i = -1; while (++i <= mDim) a[i] = (IIntRing) f.map(X); }
*/
	/** Samples the Function f on the Interval [x0, x0+Grad*dx]	 */
/*	public Manifold(intFunction f, SemiGroup x0, Object dx, int Grad) {	//preserve Internals of x0
		super(x0);
		letGrad(Grad , false, false); x0 = (SemiGroup) x0.copy();
		int i = -1; while (++i <= mDim) {a[i] = (IIntRing) f.map(x0); x0.addAt(dx);}
	}

	/** Creates an equidistant Raster on the Interval [x0, x0+Grad*dx]	 */
/*	public Manifold(SemiGroup x0, Object dx, int Grad) {
		super(x0);
		letGrad(Grad, false, false);
		IIntRing tmp = a[0] = (IIntRing) x0.copy();
		int i = 0; while (++i <= mDim) {tmp = a[i] = (IIntRing) tmp.add(dx);}
	}

	//////////////////////////////////
	//	Differential Operations:	//
	//////////////////////////////////

	/** Constructor building the Interpolation Polynom
	  * from the Samples given in this Manifold and y.	 */
	public abstract Interpolator Interpolator(IManifold y_); // {
//		return new Interpolator (a, y_.a, mDim); }

	/** @return the Difference Vector of this Manifold in Place: diff(i)= a(i) - a(i+1)
	  * The Difference Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.	 */
	public abstract IManifold diffAt(); // {
/*		if (mDim < 0) return this;
		IIntRing tmp1, tmp2 = a[0];
		int i = 0; while (++i <= mDim) {
			(tmp1 = tmp2).subAt(tmp2 = a[i]); }	//== a[i].subAt(a[i+1]);
		tmp2.divAt(factorial); mDim--;	//divide the last Item by the Factorial, so it can be used directly
		factorial.Value *= ++diffLevel;
		return this; }

	/** @return  the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	  * This is the reverse Operation to diffAt().
	  * The Integral has one Item more than this Vector.
	  * This last Item is new and initialized to zero, if it was not preserved
	  * from a previous diff Operation or initialized before.
	  * If you want to start Integration from a certain Value,
	  * it is faster to modify this start Value by modifying the last Item.	 */
	public abstract IManifold summAt(); // {
/*		boolean startIs0;
		IIntRing tmp1, tmp2;
		letGrad(mDim+1, true, false);	//preserve the higher Items, but don't set them to zero.
		tmp1 = a[mDim]; if (startIs0 = (tmp1 == null)) tmp1 = (IIntRing) a[0].zero();
		else if (diffLevel > 0) {factorial.Value /= diffLevel--; tmp1.mulAt(factorial);}
		int i = mDim;
		while (--i >= 0) {
			tmp2 = tmp1; tmp1 = a[i];	//a[i].addAt(a[i+1]);
			if (! startIs0) {tmp1.addAt(tmp2); startIs0 = false;}	//Optimization, if the first Item is zero
		}
		return this; }

	/** @return the full Difference Vector of this Manifold in Place
	  * The full Difference Vector consists of all Derivatives.
	  * It can be used to calculate inter- and extrapolations with Horner(). 	 */
	public abstract IManifold fullDiffAt(); // {
/*		int i = 0;
		ByRefInt fact = new ByRefInt(1);
		while (mDim >= 0) diffAt(); //a[mDim+1].divAt(fact); fact.Value*=(++i);}
//		a[0].divAt(fact);
		return this; }

	/** Adds a Point (y0) to the Manifold.
	  * If the Manifold has been differentiated,
	  * all Points are differentiated 	 */
	public abstract IManifold addPointAt(IIntRing y0); // {
/*		mDim += diffLevel;
		letGrad (mDim + 1, true, false);
		Group tmp2, tmp1 = (a[mDim] = (IIntRing) y0.copy());
		int i = diffLevel;
		while (--i > 0) {tmp2 = tmp1; tmp1 = a[i].subAt(tmp2);}
		mDim -= diffLevel;
		return this; }

	/** Adds a Point (y0, x0) to the Difference Vector.
	  * The x Coordinate is given implicitly by the inverse Coordinate Differences
	  * in invDiffX. 	 */
	public abstract IManifold addPointAt(IIntRing y0, IIntRing x0, IManifold x); // {
/*		x.addAtPoint(x0);
		mDim += diffLevel;
		letGrad (mDim + 1, true, false);
		SemiGroupM tmp2, tmp1 = a[mDim] = (IIntRing) y0.copy();
		int i = diffLevel;
		while (--i > 0) {tmp2 = tmp1; tmp1 = ((GroupM) a[i].subAt(tmp2)).divAt(x.a[i]);}
		mDim -= diffLevel;
		return this; }

	/** Calculates the Value of this Manifold at the Point x,
	  * using the already calculated Differences at equidistant Points.
	  * Gives best results, if the Manifold has been differenced all through,
	  * because only the higher Coefficients are used.
	  * This is well suited for the repetitive Calculation of interpolating Values,
	  * but for a single interpolated Value, it is better to use Inter/Extrapolation
	  * with either Polynomial or Rational Functions.
	  * The Division by the factorials is done once, when this function is differenced! 	 */
	public abstract IIntRing Horner(IIntRing x, IIntRing x0, IIntRing h); // {
/*		int i = mDim; 	//coordinate independent transformed coordinate!
		ByRefInt j = new ByRefInt(i);	//Because of using backward differences, I have to use (x0-x)
		IIntRing t		= (IIntRing)((GroupM)x0.subt(x)).divAt(h);
		if (i >= 0) x.addAt(j);
		IIntRing Result = (IIntRing) a[++i].copy();
		j.Value = diffLevel;
		while (--j.Value > 0) {
			((Group)Result.mulAt(t.inc())).addAt(a[++i]); }
		return Result; }

	/** @return the Derivate Vector of this Manifold in Place:
	  * derive(i)= dy(i)/dx(i) = (a(i) - a(i+1))/(x(i) - x(i+1))
	  * The Derivative Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.
	  *
	  * The Argument is not the X Vector, but the inverse of it's difference Vector.
	  * That way multiple derivations can re-use this Vector.
	  *
	  * This Operation can be iterated as often as wanted,
	  * but the number of Items in the Vector decreases until zero is reached.	 */
	public IManifold deriveAt(IManifold invDiffX) {
		diffAt().mulAt(invDiffX); return this; }

	/** @return the Derivate Vector of this Manifold in Place:
	  * derive(i)= dy(i)/dx(i) = (a(i) - a(i+1))/(x(i) - x(i+1))
	  * The Derivative Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.
	  *
	  * The Argument is not the X Vector, but it's difference Vector.
	  *
	  * This Operation can be iterated as often as wanted,
	  * but the number of Items in the Vector decreases until zero is reached.	 */
	public IManifold integrateAt(IManifold diffX) {
		return(IManifold)
			 ((IManifold)mulAt(diffX)).summAt();}

	/** @return the full Difference Vector of this Manifold in Place
	  * The full Difference Vector consists of all Derivatives.
	  * It can be used to calculate inter- and extrapolations. 	 */
	public IManifold fullDiff() { return ((IManifold)copy()).fullDiffAt(); }

	/** @return the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	  * This is the reverse Operation to diffAt().
	  * The Integral has one Item more than this Vector.
	  * This last Item is new and initialized to zero, if it was not preserved
	  * from a previous diff Operation.	 */
	public IManifold summ() {return ((IManifold) copy()).summAt();}

	/** @return the Difference Vector of this Manifold: diff(i)= a(i+1) - a(i)
	  * The Difference Vector has one Item less than the original Vector.	 */
	public IManifold diff() {return ((IManifold) copy()).diffAt();}


	//////////////
	//	Testing	//
	//////////////

	/**Testing this class	 */
	public static void testIt() throws Exception {
/*		double[] x1 = {0.0, 1.0, 3.0};
		double[] y1 = {1.0, 3.0, 2.0};

	/*		Manifold x = new Manifold(x1);
			Manifold y = new Manifold(y1);
			Manifold dx = (Manifold) x.diff().invAt();
			y.fullDeriveAt(x);
			y.Horner(new BodyDouble(2.0), x);
	*/
	/*		intFunction fktn = BodyFuncs.Cosinus.Cosinus;	//RingFuncs.IdentityCopy();	//RingFuncs.fSquare(); //IdentityCopy(); //Cosinus(); //CosHMinus1(); //CosinusMinus1(); //Cosinus(); //Sinus();
			Body.BodyDouble x0 = new Body.BodyDouble(0);//-Math.PI / 3);
			Body.BodyDouble ddx = new Body.BodyDouble(0.2);//Math.PI / 12);
			Manifold x = new Manifold(x0, ddx, 4);
			Manifold Sample = new Manifold(fktn, x);

			System.out.println(" Interpolation with equidistant Sample Points");
			System.out.println(" x = " + x);
			Manifold dx = (Manifold) x.diff();
			System.out.println(" dx = " + dx);
			System.out.println(" y = f (x) = " + Sample);
			Sample.fullDiffAt();
			System.out.println(" Values at the Sample Points");
			int i = -1;
			while (++i <= x.getDim()) {
				Body.BodyDouble z = (Body.BodyDouble) x.a [i];//new BodyDouble(Math.random());
				System.out.println(" z = " + z + "; f (z) = " + Sample.Horner(z, x0, ddx) + " == " + fktn.map(z)); }
			System.out.println(" Values at random Points");
			i = -1;
			while (++i <= x.getDim()) {
				Body.BodyDouble z = new Body.BodyDouble(Math.random());
				System.out.println(" z = " + z + "; f (z) = " + Sample.Horner(z, x0, ddx) + " == " + fktn.map(z)); }
	*/
	}

}
