package streamIO.copy.group;

/**Adds the Capability to add double Numbers directly */
public abstract class AGroupDbl
extends AGroupLng
implements IDblGroup {

	/**Addition of a double Number: + arg	 */
	public IDblGroup add	(double arg) { return ((IDblGroup) copy()). addAt(arg); }

	/**Subtraction of a double Number: - arg	 */
	public IDblGroup sub(double arg) { return ((IDblGroup) copy()). addAt(arg); }

}
