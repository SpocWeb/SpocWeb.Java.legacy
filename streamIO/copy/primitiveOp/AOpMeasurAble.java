package streamIO.copy.primitiveOp;

import function.IMeasurAble;

/** Abstract Class that implements most of the Methods of OpDouble
  * by calling Methods from intOpDouble
  * Actually these Classes are deprecated,
  * because Operations with double Arguments are added to the Real Interface.
  */
public abstract class AOpMeasurAble
extends AOpDouble
implements IMeasurAble {

	///////////////////////////////////////////////////////////////////////////
	//	Constructors
	///////////////////////////////////////////////////////////////////////////

	/**Empty Constructor */
	public AOpMeasurAble() {}

	/**Initializing Constructor, just comfortable	 */
	public AOpMeasurAble(double Value_) { super(Value_); }

	///////////////////////////////////////////////////////////////////////////
	//	Methods
	///////////////////////////////////////////////////////////////////////////

	/**Negation in Place: = -x	 */
	public IOpLong negAt() { copyAt(-getDouble()); return this; }

	/**Inversion in Place: = 1/x	 */
	public IOpLong invAt() { copyAt(1.0/getDouble()); return this; }

	/**Multiplication by 2 in Place: *= 2	 */
	public IOpLong dblAt() { return addAt(getDouble()); }

	/**Multiplication by 3 in Place: *= 3	 */
	public IOpLong trplAt() { return mulAt(3); }

	/**Multiplication by 4 in Place: *= 4	 */
	public IOpLong quadAt() { return mulAt(4); }

	/**Division by 2 in Place: /= 2	 */
	public IOpLong halfAt() { return mulAt(0.5); }

	/**Division by 3 in Place: /= 3	 */
	public IOpLong thirdAt() { return divAt(3); }

	/**Division by 4 in Place: /= 4	 */
	public IOpLong quarterAt() { return mulAt(0.25); }

	/**Square in Place: ^= 2	 */
	public IOpLong sqrAt() { return mulAt(getDouble()); }

	/**Cubic in Place: ^= 3	 */
	public IOpLong cbcAt() { double tmp = getDouble(); return mulAt(tmp*tmp); }

	/**  Linear Mapping in Place: x*=a + y	*/
	public IOpLong LinAt		(long a, long y) {
		return this; }

}
