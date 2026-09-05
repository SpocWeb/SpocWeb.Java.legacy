package streamIO.copy.primitiveOp;

/**Abstract Class that implements most of the Methods of OpDouble
 * by calling Methods from intOpDouble
 * Actually these Classes are deprecated,
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:15:54Z
 * digest: 32058e54c71db15ba039d5edeb6f606d385dba57c139ec09fb4770587e4451ca
 * stale: false
 * tags: [code/abstract_base, code/delegation, code/deprecated_api]
 * concepts: [Primitive Numeric Operations, Deprecated API]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
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
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args instead of delegating to a double-based operation; any caller of copyAt(long) triggers a StackOverflowError.
	public IOpLong copyAt(long arg) { return copyAt(arg); }

	/**Check for equality: ==	*/
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of equals(long) triggers a StackOverflowError.
	public boolean equals(long arg) { return equals(arg); }

	/**Comparison with a long Number: < arg	 */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of less(long) triggers a StackOverflowError.
	public boolean less(long arg) { return less(arg); }

	/**Comparison with a long Number: > arg	 */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of grtr(long) triggers a StackOverflowError.
	public boolean grtr(long arg) { return grtr(arg); }

	/**Maximum in Place: 	*/
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of MaxAt(long) triggers a StackOverflowError.
	public IOpLong MaxAt (long arg) { return MaxAt(arg); }

	/**Minimum in Place: 	*/
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of MinAt(long) triggers a StackOverflowError.
	public IOpLong MinAt (long arg) { return MinAt(arg); }

	/**Addition of a long Number in Place: += arg	 */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of addAt(long) triggers a StackOverflowError.
	public IOpLong addAt(long arg) { return addAt(arg); }

	/**Subtraction of a long Number in Place: -= arg	 */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of subAt(long) triggers a StackOverflowError.
	public IOpLong subAt(long arg) { return subAt(arg); }

	/**Multiplication of a long Number in Place: *= arg	 */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of mulAt(long) triggers a StackOverflowError.
	public IOpLong mulAt(long arg) { return mulAt(arg); }

	/**Division of a long Number in Place: /= arg	 */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args; any caller of divAt(long) triggers a StackOverflowError.
	public IOpLong divAt(long arg) { return divAt(arg); }

	/**  Linear Mapping: x * a + y			*/
	public IOpDouble Lin		(double a, double y) {
		return ((IOpDouble) copy(Integer.MAX_VALUE)).LinAt(a, y); }

	/**  Linear Mapping in Place: x*=a + y	<=> x*=a; x+=y; */
	// TODO: LOGIC: infinite recursion - calls itself with the same signature/args instead of delegating to the long-based ops (mulAt/addAt); any caller triggers a StackOverflowError.
	public IOpLong LinAt		(long a, long y) {
		return LinAt(a, y); }

}
