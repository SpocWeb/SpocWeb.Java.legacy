package streamIO.copy.primitiveOp;

/**This Interface adds all Methods to the intOpDouble
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 986668d3d2265053c03fc4ee0f921ca912570d6749d665c15b9a824108631edc
 * stale: false
 * tags: [code/arithmetic_operation, code/in_place_operation, code/numeric_interface]
 * concepts: [Primitive Numeric Operations]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * that can be indirectly defined by intOpDouble */
public interface IOpDouble
extends IIOpDouble, IOpLong {

	/**Maximum in Place: 	*/
	public IOpDouble Max	(double arg);

	/**Minimum in Place: 	*/
	public IOpDouble Min	(double arg);

	/**Addition of a double Number in Place: += arg	 */
	public IOpDouble add	(double arg);

	/**Subtraction of a double Number in Place: -= arg	 */
	public IOpDouble subt(double arg);

	/**Multiplication of a double Number in Place: *= arg	 */
	public IOpDouble mul	(double arg);

	/**Division of a double Number in Place: /= arg	 */
	public IOpDouble div	(double arg);

	/**  Linear Mapping in Place: x*=a + y	*/
	public IOpDouble LinAt		(double a, double y);

	/**  Linear Mapping: x * a + y			*/
	public IOpDouble Lin			(double a, double y);

}
