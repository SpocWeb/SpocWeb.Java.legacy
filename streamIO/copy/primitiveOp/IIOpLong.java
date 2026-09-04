package streamIO.copy.primitiveOp;

/**This Interface is definitely implemented for a mutable Class
 * that also implements IMeasurAble.
 * But there are classes that define these Ops and are not measurable, like Vectors.
 * But it is also implemented by all Vector Interval and ComplexDbl Classes.
 * But what is the benefit of this Interface, except for Unification?
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
