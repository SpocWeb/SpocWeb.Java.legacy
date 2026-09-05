package streamIO.copy.group.ring.metric;

/**IWellOrder:
 * Interface for a Class whose Objects are well and connex ordered
 * by Relations ">"resp."<".
 * Connex means that these Relations are defined for any two Elements.
 * I.e. there is a largest Element, which is also the maximum Element.
 * In Addition there are maximum and minimum Values for this Class.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 058bc364b39b2fb23d7b39cbee07acfa96c9bf768d53693a5a81c7fdf336ecf8
 * stale: false
 * tags: [code/metric_space, code/root_finding, code/numerical_integration, code/big_integer_arithmetic]
 * concepts: [Metric Spaces - Root Finding and Numerical Integration]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */

public interface IIWellOrder {

	/**Sets and returns the maximum Value for this Class in Place.	 */
	public IWellOrder maxValueAt();

}
