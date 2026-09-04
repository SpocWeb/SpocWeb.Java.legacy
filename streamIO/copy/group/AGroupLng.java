package streamIO.copy.group;

/**Adds the Capability to add long Numbers directly */
public abstract class AGroupLng
extends AGroup
implements ILngGroup {

	/**Addition of a long Number in Place: += arg	 */
	public ILngGroup add (long arg) { return ((ILngGroup) copy()). addAt(arg); }

	/**Subtraction of a long Number in Place: -= arg	 */
	public ILngGroup subt(long arg) { return ((ILngGroup) copy()).subAt(arg); }

}
