package streamIO.copy.primitiveOp;

import function.IMeasurAble;

/** Abstract Class that implements most of the Methods of OpDouble
  * by calling Methods from intOpDouble
  * Actually these Classes are deprecated,
  * because Operations with double Arguments are added to the Real Interface.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:15:56Z
  * digest: 841fd9a098ba1a4deff12630b268c4a73d41989a3ce12fd268042ab37dfca0d1
  * stale: false
  * tags: [code/abstract_base, code/arithmetic_operation, code/deprecated_api]
  * concepts: [Primitive Numeric Operations, Measurable Values]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
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
	// TODO: LOGIC: no-op - both parameters a and y are ignored and the object is returned unchanged, silently failing to perform the linear mapping the contract promises.
	public IOpLong LinAt		(long a, long y) {
		return this; }

}
