package streamIO.copy.group;

/**Adds the Capability to add long Numbers directly */
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
