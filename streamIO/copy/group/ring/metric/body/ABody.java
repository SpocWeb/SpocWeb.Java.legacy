package streamIO.copy.group.ring.metric.body;

import streamIO.copy.group.ring.AIntRing;

/**Default Implementation for the Methods of a Body.
 * The only Difference to a Ring is that the Body is topologically closed,
 * i.e. every Limit of a Series is a Member of the Set.
 *
 * Algebraic Body (M,+,*): Set of Objects, where
 * 1) (M,+,0) form a commutative Group
 * 2) (M,*,1) form a (commutative) Group
 * 3) the Distributive Laws apply: a*(b+c)=a*b+a*b und (a+b)*c =a*c+b*c
 *
 * A Body has all Capabilities of a Ring and more.
 * It can be Proved, that :
 *
 */

/**Default Constructor for the abstract Body Class
 * The Body is rather used as a metric Body,
 * <!-- docstate
 * tags: [code/rational_numbers, code/interval_arithmetic]
 * concepts: [Rational Numbers and Interval Arithmetic]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * because of it's topological Properties.  */
public abstract class ABody
extends AIntRing
implements Body
{

}
