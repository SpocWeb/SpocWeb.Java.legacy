package streamIO.copy.primitiveOp;

/**This Interface is definitely implemented for a mutable Class
 * that also implements IMeasurAble.
 * But there are classes that define these Ops and are not measurable, like Vectors.
 * But it is also implemented by all Vector Interval and ComplexDbl Classes.
 * But what is the benefit of this Interface, except for Unification?
 * These Constructs must be orderAble due to their comparability with double Numbers.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 5b0bc6bb2ac790adeda3079bb822f7448935f2249c3329d021c7656534cdb93c
 * stale: false
 * tags: [code/arithmetic_operation, code/in_place_operation, code/numeric_comparison]
 * concepts: [Primitive Numeric Operations]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IIOpDouble {

	/**Copy in Place: =	*/
	public IOpDouble copyAt(double arg); //

	/**Check for equality: ==	*/
	public boolean equals(double arg);

	/**Comparison with a double Number: < arg	 */
	public boolean less(double arg);

	/**Comparison with a double Number: > arg	 */
	public boolean grtr(double arg);

	/**Maximum in Place: 	*/
	public IOpDouble MaxAt (double arg);

	/**Minimum in Place: 	*/
	public IOpDouble MinAt (double arg);

	/**Addition of a double Number in Place: += arg	 */
	public IOpDouble addAt(double arg);

	/**Subtraction of a double Number in Place: -= arg	 */
	public IOpDouble subAt(double arg);

	/**Multiplication of a double Number in Place: *= arg	 */
	public IOpDouble mulAt(double arg);

	/**Division of a double Number in Place: /= arg	 */
	public IOpDouble divAt(double arg);

}
