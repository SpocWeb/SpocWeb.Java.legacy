package streamIO.copy.groupM;

/**Adds the Capability to multiply and divide double Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: b8c070dcce4ce3dcd33e2b25efc158aea9c19924ec2539d9efe90ea750b4997e
 * stale: false
 * tags: [code/multiplicative_group, code/numeric_comparison]
 * concepts: [Algebraic Group, Multiplicative Structure]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IDblGroupM
extends ILngGroupM {

	/**Multiplication by a double Number: * arg	 */
	public IDblGroupM mul  (final double arg);

	/**Division by a double Number: / arg	 */
	public IDblGroupM div  (final double arg);

	/**Multiplication by a double Number in Place: *= arg	 */
	public IDblGroupM mulAt(final double arg);

	/**Division by a double Number in Place: /= arg	 */
	public IDblGroupM divAt(final double arg);

}
