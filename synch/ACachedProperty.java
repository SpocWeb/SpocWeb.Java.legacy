package synch;

import graphs.ICValue;
import knowledge.DirtyFlag;

/**A class that represents a cached Property.
 * It is typically used for derived Properties
 * that are complicated to calculate,
 * so their Value is rather cached than recalculated every time.
 * The Object has to implement the Calculation Routine
 * recalc() as a Callback that is called as soon as the Property is invalidated.
 * Since Java doesn't have Function Pointers,
 * this allows for only a single cached Property per Class
 *
 * Instead of changing the cached Value directly, it has to be invalidated
 * or modified directly, although even the Possibility is dangerous.
 *
 * The structure is very close to class 'writeOnceProperty'
 * but you cannot derive one from the other because both have
 * a public Method (setValue() and invalidate()) that has to be made private.
 *
 * This is much easier and safer to handle in Visual Basic,
 * because you can implement the cached Values in their Property Get Routines directly
 * without having to create this artificial Class.
 * The VB Implementation is easier to read, maintain and safer,
 * because it uses static local Variables in the Property Get Routines
 * for the boolean Flag and the actual Value!
 * You can usually get along without the boolean Flag
 * by just using 'null' as an Indicator for an invalid Value. */
public class ACachedProperty
extends DirtyFlag
implements ICValue {

	/**Public Method to invalidate the Property	 */
	public void invalidate() { dirty = true; }

	/**The actual cached Value	 */
	private Object Cache; // = null;

	/**Method has to be implemented to recalculcate or change the cached Value	 */
	public  Object reCalc(){ return null; }
//abstract
	/**Returns the Cache directly,
	 * dangerous, because it might be modified!
	 * Should only be used by the Classes having this Property.	 */
	public Object getVal() {
		if (dirty) { Cache = reCalc(); dirty = false; }
		return Cache; }

}
