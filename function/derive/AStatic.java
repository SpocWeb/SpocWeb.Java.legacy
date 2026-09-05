package function.derive;

import function.IFloatFunction;
import function.IFunction;

/**
  * AStatic.java
  * This abstract Class is the Base Class for
  * which have a simpler Representation to be used for Simplification.
  *
  * Known SubClasses: AFuncRel, AddAt, MulAt, LinAt
  *
  * Created on 28. Dezember 2000, 16:13
  *
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: f030b5a08ec082b14ee8ef857d26d4019af5b8c160b0bbc5b3d4782296804254
  * stale: false
  * tags: [code/function_composition, code/mathematical_function]
  * concepts: [Function Algebra, Simplification]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public abstract class AStatic
extends ADeriveAble {

	/** Creates new AStatic Instance,
	 *  made protected as Preparation for the Singleton */
	protected AStatic () { }

	/**Class that can be processed by this Function
	 * If more than a single Class can be processed (apart from the Inverse),
	 * the Method 'canProcess()' has to be overwritten.
	 * This is a bit of a Memory Overhead, because this is an Instance Variable */
	protected Class ProcessAble; // = java.lang.Object.class;  //global 'true'

	/**Returns true, when this Class can operate on Arguments of this Type
	 * This Function makes sense at this Level,
	 * because here there is always the Alternative
	 * not to operate on the Constants,
	 * but to operate on the Functions and operate the Results on evaluation.
	 * Returns false by Default,
	 * because most simple function are not even Algebras.	 */
	public boolean canProcess(Object arg) {
		if (ProcessAble == null) return false;
		return ProcessAble.isInstance(arg); }

	/**Alternative Representation that is 'simplify()'ed
	 * or can be simplified more easily.
	 * This is a bit of a Memory Overhead, because this is an Instance Variable */
	protected IFunction simple =  this;

	/** Returns an alternative Representation that is 'simplified'	 */
	public IFunction simplify() { return simple; }

	/** Returns the Mapping of the Argument	 */
	public Object Map(Object arg) { return simple.Map (arg); }

	/**Returns an alternative Representation that is 'simplified'	 */
	public double Map(double arg) { return ((IFloatFunction) simple).Map (arg); }

}
