package streamIO.copy.group;

/**Adds the Capability to add double Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 6725ce8bc31b1c49029b10e5ccaf7e77f1e6fe7d115a925079bcf882f017b1cf
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AGroupDbl
extends AGroupLng
implements IDblGroup {

	/**Addition of a double Number: + arg	 */
	public IDblGroup add	(double arg) { return ((IDblGroup) copy()). addAt(arg); }

	/**Subtraction of a double Number: - arg	 */
	public IDblGroup sub(double arg) { return ((IDblGroup) copy()). addAt(arg); }

}
