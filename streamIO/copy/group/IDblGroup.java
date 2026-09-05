package streamIO.copy.group;

/**Adds the Capability to add double Precision Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 6a79dd9fc12940e58ee375cbc3121297e713da8edd8d293b3af2b52efe23c39c
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface IDblGroup
extends ILngGroup {

	/**Addition of a double Number: + arg	 */
	public IDblGroup add  (final double arg);

	/**Subtraction of a double Number: - arg	 */
	public IDblGroup sub  (final double arg);

	/**Addition of a double Number in Place: += arg	 */
	public IDblGroup addAt(final double arg);

	/**Subtraction of a double Number in Place: -= arg	 */
	public IDblGroup subAt(final double arg);

}
