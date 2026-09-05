package function;

/**IOrderable:
 * Interface for a Class whose Objects have a strict Order Relation ">" resp. "<"
 * If a Set is not ordered completely, these Relations are not defined
 * for any two Elements. In this Case, both >= and <= give False.
 * When the Element is the same or equivalent, both >= and <= give True.
 * When the Element is comparable, only one gives True.
 *
 * Absolute Value is only important because of the Metric defined by "<".
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:12:24Z
 * digest: c4985a068122b3d0efde1e6f199d9d57d8c3a3477a1ae4348b979dc4560fe27f
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IIOrderAble {

	/** less: '<' Returns True, when 'Self' < arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	boolean isLessThan (Object arg);

}
