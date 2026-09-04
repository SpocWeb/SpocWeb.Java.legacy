package streamIO.copy.group.ring.metric.body.vector;

import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.Interpolator;

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
public interface IManifold
extends ITensor {

	/** @return the Difference Vector of this Manifold in Place: diff(i)= a(i) - a(i+1)
	  * The Difference Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.	 */
	public IManifold diffAt();

	/** @return the integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	  * This is the reverse Operation to diffAt().
	  * The Integral has one Item more than this Vector.
	  * This last Item is new and initialized to zero, if it was not preserved
	  * from a previous diff Operation or initialized before.
	  * If you want to start Integration from a certain Value,
	  * it is faster to modify this start Value by modifying the last Item.	 */
	public IManifold summAt();

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
	public IManifold deriveAt(IManifold invDiffX);

	/** @return the Derivate Vector of this Manifold in Place:
	  * derive(i)= dy(i)/dx(i) = (a(i) - a(i+1))/(x(i) - x(i+1))
	  * The Derivative Vector has one Item less than the original Vector.
	  * For complete Reversibility the last Item is preserved.
	  *
	  * The Argument is not the X Vector, but it's difference Vector.
	  *
	  * This Operation can be iterated as often as wanted,
	  * but the number of Items in the Vector decreases until zero is reached.	 */
	public IManifold integrateAt(IManifold diffX);

	/** @return the full Difference Vector of this Manifold in Place
	  * The full Difference Vector consists of all Derivatives.
	  * It can be used to calculate inter- and extrapolations with Horner(). 	 */
	public IManifold fullDiffAt();

	/** @return the full Difference Vector of this Manifold in Place
	  * The full Difference Vector consists of all Derivatives.
	  * It can be used to calculate inter- and extrapolations. 	 */
	public IManifold fullDiff();

	/** @return the Integrated Vector of this Manifold in Place: int(i)= a(i) + a(i+1)
	  * This is the reverse Operation to diffAt().
	  * The Integral has one Item more than this Vector.
	  * This last Item is new and initialized to zero, if it was not preserved
	  * from a previous diff Operation.	 */
	public IManifold summ();

	/** @return the Difference Vector of this Manifold: diff(i)= a(i+1) - a(i)
	  * The Difference Vector has one Item less than the original Vector.	 */
	public IManifold diff();

	/** Adds a Point (y0) to the Manifold. If the Manifold has been differentiated,
	  * all Points are differentiated 	 */
	public IManifold addPointAt(IIntRing y0);

	/** Adds a Point (y0, x0) to the Difference Vector.
	  * The x Coordinate is given implicitly by the inverse Coordinate Differences
	  * in invDiffX. 	 */
	public IManifold addPointAt(IIntRing y0, IIntRing x0, IManifold x);

	/** Calculates the Value of this Manifold at the Point x,
	  * using the already calculated Differences at equidistant Points.
	  * Gives best results, if the Manifold has been differenced all through,
	  * because only the higher Coefficients are used.
	  * This is well suited for the repetitive Calculation of interpolating Values,
	  * but for a single interpolated Value, it is better to use Inter/Extrapolation
	  * with either Polynomial or Rational Functions.
	  * The Division by the factorials is done once, when this function is differenced! 	 */
	public IIntRing Horner(IIntRing x, IIntRing x0, IIntRing h);

	/** @return a new Interpolation Polynom
	  * from the Samples given in this Manifold and y.	 */
	public Interpolator Interpolator(IManifold y_); 

}
