package streamIO.copy.primitiveOp;

/**Abstract Class that implements most of the Methods of OpDouble
 * by calling Methods from intOpDouble
 * Actually these Classes are deprecated,
 * because Operations with double Arguments are added to the Real Interface. */
public abstract class AOpDouble
extends AOpLong
implements IOpDouble {

	/**Empty Constructor	 */
	protected AOpDouble () { }

	/**Initializing Constructor	 */
	protected AOpDouble (double arg) { this(); copyAt(arg); }

	/**Maximum in Place: 	*/
	public IOpDouble Max	(double arg) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).MaxAt(arg); }

	/**Minimum in Place: 	*/
	public IOpDouble Min	(double arg) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).MinAt(arg); }

	/**Addition of a double Number in Place: += arg	 */
	public IOpDouble add	(double arg) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).addAt(arg); }

	/**Subtraction of a double Number in Place: -= arg	 */
	public IOpDouble subt(double arg) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).subAt(arg); }

	/**Multiplication of a double Number in Place: *= arg	 */
	public IOpDouble mul	(double arg) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).mulAt(arg); }

	/**Division of a double Number in Place: /= arg	 */
	public IOpDouble div	(double arg) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).divAt(arg); }

	/**Copy in Place: =	*/
	public IOpLong copyAt(long arg) { return copyAt(arg); }

	/**Check for equality: ==	*/
	public boolean equals(long arg) { return equals(arg); }

	/**Comparison with a long Number: < arg	 */
	public boolean less(long arg) { return less(arg); }

	/**Comparison with a long Number: > arg	 */
	public boolean grtr(long arg) { return grtr(arg); }

	/**Maximum in Place: 	*/
	public IOpLong MaxAt (long arg) { return MaxAt(arg); }

	/**Minimum in Place: 	*/
	public IOpLong MinAt (long arg) { return MinAt(arg); }

	/**Addition of a long Number in Place: += arg	 */
	public IOpLong addAt(long arg) { return addAt(arg); }

	/**Subtraction of a long Number in Place: -= arg	 */
	public IOpLong subAt(long arg) { return subAt(arg); }

	/**Multiplication of a long Number in Place: *= arg	 */
	public IOpLong mulAt(long arg) { return mulAt(arg); }

	/**Division of a long Number in Place: /= arg	 */
	public IOpLong divAt(long arg) { return divAt(arg); }

	/**  Linear Mapping: x * a + y			*/
	public IOpDouble Lin		(double a, double y) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).LinAt(a, y); }

	/**  Linear Mapping in Place: x*=a + y	<=> x*=a; x+=y; */
	public IOpLong LinAt		(long a, long y) {
		return LinAt(a, y); }

}
