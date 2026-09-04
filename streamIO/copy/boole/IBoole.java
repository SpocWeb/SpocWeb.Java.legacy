package streamIO.copy.boole;

/**This Class defines the basic Interface for a Boolean Algebra.
 * The two basic Operations AND and OR are connected by the Distributive Laws
 * and both use the same inverse: NOT
 * but it leads only to the other operation's neutral Element,
 * instead of to this operations's neutral Element.
 *
 * The Axioms are:
 * Existence of False and True: a AND True  = a         a OR False = a
 * Komplementaryness:           a AND NOT a = False     a OR NOT a = True
 * the latter is only true in boolean Logic,
 * in ternary or even continuous Logic it is only
 * null >= a AND NOT a >= False     null <= a OR NOT a <= True
 *
 * The following Laws apply:    NOT True    = False     NOT False  = True
 *
 * The Definition can be extended to Vectors of Boolean Elements,
 * which allows for operations on large sets of Elements,
 * in which each one acts independently (Manifold, not Vector or Polynom).
 * In a binary Representation AND and OR can be defined by MUL and ADD,
 * but without Carry Bit:
 * a AND b =		a*b
 * a OR  b = a + b -a*b
 *   NOT a = 1 - a
 * This is possible for continuous a and b from [0..1] too (see Statistics),
 * but only if a and b are Probabilities of completely independent Events.
 * Otherwise the Distributive and other Laws don't hold anymore:
 * p(A AND A) = a AND a = a != a*a
 * p(A OR  A) = a OR  a = a != a + a - a*a
 *
 * There is a Representation where the Distributive Laws still apply
 * for a and b from [0..1], but the Definition of the Inverse is a Problem:
 * This is realized in
 * a AND b = Min (a, b)
 * a OR  b = Max (a, b)
 * a AND a = a OR a = a, so also the Distributive Laws apply!
 * BUT:
 * a AND NOT a = 1 != a or 1-a
 *
 */
public interface IBoole
extends ILattice {

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	Boole FalseAt();

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return !a
	  * NOT a = true <=> (a = false)
	  * This Operation cannot be implemented by infinite Sets,
	  * Therefore you need other means to define some Operations.	 */
	Boole NOTat	();

}
