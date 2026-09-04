package streamIO.copy.group;

/**Adds the Capability to add double Precision Numbers directly */
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
