package streamIO.copy.groupM;

/**Adds the Capability to multiply and divide long Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 8463f44a9a71adb3a7ee4ae8dcb989e5577f07ee570dfe612fd34144260afb9f
 * stale: false
 * tags: [code/multiplicative_group]
 * concepts: [Algebraic Group, Multiplicative Structure]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface ILngGroupM
extends IGroupM {

	/**Multiplication by a long Number: * arg	 */
	public ILngGroupM mul (long arg);

	/**Division by a long Number: / arg	 */
	public ILngGroupM div (long arg);

	/**Multiplication of a long Number in Place: *= arg	 */
	public ILngGroupM mulAt(long arg);

	/**Division of a long Number in Place: /= arg	 */
	public ILngGroupM divAt(long arg);

}
