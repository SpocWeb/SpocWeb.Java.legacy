/**
 * File  Name: FindValueTester.java
 * Created on: 27.10.2002
 */
package streamIO.object.enumer.container;

import streamIO.copy.monoid.Association;
import tester.ITester;

/** This Helper Class is a ITester for a certain Object
  * in the Value Property of the Association used in
  * @see isReflexive() and other testing Routines for Relations.
  * @see isRightTotal()
  *
  * This ITester is sufficient, because
  * ...it is fast enough due to the Preselection by the Association's key
  * ...the Test for the key is not necessary (already done in the Iterator)
  *
  * Actually the Test is superfluous,
  * because the Relation is now implemented using a nested Set / Set Combination.
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: ddaab633887103b495f16bf62ed8207ebb942952c90d03ecb420c49a11f9eb83
  * stale: false
  * -->
  */
class FindValueTester
implements ITester {

	/**Stores the right side of the Mapping for a certain Association	 */
	protected Object Value;

	/**Switches between searching for Equality or Non-Equality	 */
	protected boolean inverse;

	/** Tests whether the given {@link Association}'s value matches (or, when {@link #inverse}
	 * is set, does not match) this Tester's stored {@link #Value}.
	 * @param Assoc the Association whose value is compared, must be castable to {@link Association}
	 * @return true when the Association's value equals {@link #Value} (negated if {@link #inverse}) */
	public boolean test(Object Assoc) {	//rely on the Item being an Association!
		Association ass = (Association) Assoc;
		return inverse  ^ (
			(ass.val   ==   Value) ||
			 ass.val.equals(Value)); }

}
