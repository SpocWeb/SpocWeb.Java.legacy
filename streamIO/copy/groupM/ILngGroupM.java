package streamIO.copy.groupM;

/**Adds the Capability to multiply and divide long Numbers directly */
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
