package function.derive;

import function.IFunction;

/**AFuncRel
 * Allows ByRef Transport of Function Objects
 * and returns the Function Object's Value in Map().
 * So this is practically a (faster) Concatenation with the Identity Function.
 * Parameterizes the Dependencies and Relations between Functions
 * by listing Simple, Inverse, Derivative and Integral
 * The Implementation is the same as in ByRefFunc,
 * which does not provide for Derivative and Integral though.
 *
 * Created on 28. Dezember 2000, 16:29
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 941e66e9699bab302649c0d57c10e102052b070781e1318d15345e69316cb863
 * stale: false
 * tags: [code/function_wrapper, code/function_composition]
 * concepts: [Function Algebra]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class AFuncRel
extends AStatic {

	/**Actual Function that is evaluated on 'Map()'  */
	protected IFunction Value;

	/**Returns an alternative Representation that is 'simplified'	 */
	public Object Map(Object arg) { return Value.Map (arg); }

	/** Creates new AFuncRel */
	protected AFuncRel () { Value =  this; }

	/** Creates new AFuncRel */
	public AFuncRel (IFunction Value_) { Value = Value_; }

}
