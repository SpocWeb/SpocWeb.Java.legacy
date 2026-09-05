package streamIO.copy.primitiveOp;

/**This Interface is definitely implemented for a mutable Class
 * that also implements IMeasurAble.
 * But there are classes that define these Ops and are not measurable, like Vectors.
 * But it is also implemented by all Vector Interval and ComplexDbl Classes.
 * But what is the benefit of this Interface, except for Unification?
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 567e796b1cc13906e478a0dc6116357cd0f38c2228734c4022819fe7cb1e47fe
 * stale: false
 * tags: [code/arithmetic_operation, code/in_place_operation, code/numeric_comparison]
 * concepts: [Primitive Numeric Operations]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * These Constructs must be orderAble due to their comparability with long Numbers.  */
public interface IIOpLong {

	/**Copy in Place: =	*/
	public IOpLong copyAt(long arg); //

	/**Check for equality: ==	*/
	public boolean equals(long arg);

	/**Maximum in Place: 	*/
	public IOpLong MaxAt (long arg);

	/**Minimum in Place: 	*/
	public IOpLong MinAt (long arg);

	/**Addition of a long Number in Place: += arg	 */
	public IOpLong addAt(long arg);

	/**Subtraction of a long Number in Place: -= arg	 */
	public IOpLong subAt(long arg);

	/**Multiplication of a long Number in Place: *= arg	 */
	public IOpLong mulAt(long arg);

	/**Division of a long Number in Place: /= arg	 */
	public IOpLong divAt(long arg);

}
