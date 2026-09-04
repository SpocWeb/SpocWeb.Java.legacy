package streamIO.copy.groupM;

/**Adds the Capability to multiply and divide double Numbers directly */
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
