package streamIO.copy.group.ring.metric.body;

import streamIO.copy.group.ring.AIntRing;

/**Default implementation for the methods of a {@link Body}, since the only difference to a
 * Ring is that the Body is topologically closed, i.e. every Limit of a Series is a Member
 * of the Set.
 *
 * <p>Algebraic Body (M,+,*): Set of Objects, where
 * 1) (M,+,0) form a commutative Group
 * 2) (M,*,1) form a (commutative) Group
 * 3) the Distributive Laws apply: a*(b+c)=a*b+a*b und (a+b)*c =a*c+b*c
 *
 * <p>A Body has all Capabilities of a Ring and more.
 * It can be Proved, that :
 *
 * <p>The Body is rather used as a metric Body, because of it's topological Properties.
 *
 * <!-- docstate
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * -->
 */
public abstract class ABody
extends AIntRing
implements Body
{

}
