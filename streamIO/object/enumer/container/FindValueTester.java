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
  */
class FindValueTester
implements ITester {

	/**Stores the right side of the Mapping for a certain Association	 */
	protected Object Value;

	/**Switches between searching for Equality or Non-Equality	 */
	protected boolean inverse;

	public boolean test(Object Assoc) {	//rely on the Item being an Association!
		Association ass = (Association) Assoc;
		return inverse  ^ (
			(ass.val   ==   Value) ||
			 ass.val.equals(Value)); }

}
