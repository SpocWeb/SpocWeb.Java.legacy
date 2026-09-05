package streamIO.copy.group;

/**Adds the Capability to add long Numbers directly
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: eb9f7fc3fcb8f44c77bc4be45b52e684a592c85cb27efe4353f7eda163602395
 * stale: false
 * tags: [code/group_algebra, code/date_time]
 * concepts: [Group/SemiGroup Algebra]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public abstract class AGroupLng
extends AGroup
implements ILngGroup {

	/**Addition of a long Number in Place: += arg	 */
	public ILngGroup add (long arg) { return ((ILngGroup) copy()). addAt(arg); }

	/**Subtraction of a long Number in Place: -= arg	 */
	public ILngGroup subt(long arg) { return ((ILngGroup) copy()).subAt(arg); }

}
