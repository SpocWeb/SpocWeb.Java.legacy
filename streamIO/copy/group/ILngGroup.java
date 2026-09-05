package streamIO.copy.group;

/**Adds the Capability to add long Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 5c1c5a3067fff539948c8d1116f71fb31d92c9ac9b8cd61b00bedbb357a7d7b1
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public interface ILngGroup
extends IGroup {

	/**Addition of a long Number in Place: += arg	 */
	public ILngGroup add (final long arg);

	/**Subtraction of a long Number in Place: -= arg	 */
	public ILngGroup subt(final long arg);

	/**Addition of a long Number in Place: += arg	 */
	public ILngGroup addAt(final long arg);

	/**Subtraction of a long Number in Place: -= arg	 */
	public ILngGroup subAt(final long arg);

}
