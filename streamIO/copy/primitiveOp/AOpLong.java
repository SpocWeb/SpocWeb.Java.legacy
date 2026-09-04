package streamIO.copy.primitiveOp;

import function.AOrderAble;

/**Abstract Class that implements most of the Methods of OpLong
 * by calling Methods from IOpLong.
 * Actually these Classes are deprecated,
 * because Operations with double Arguments are added to the Real Interface.
 * Also Operations with long Arguments are added to the IIntRing Interface */
public abstract class AOpLong
extends AOrderAble
implements IOpLong {

	/**Empty Constructor	 */
	protected AOpLong() { }

	/**Initializing Constructor	 */
	protected AOpLong(long arg) { this(); copyAt(arg); }

	/**Maximum in Place: 	*/
	public IOpLong Max	(long arg) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).MaxAt(arg); }

	/**Minimum in Place: 	*/
	public IOpLong Min	(long arg) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).MinAt(arg); }

	/**Addition of a long Number in Place: += arg	 */
	public IOpLong add	(long arg) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).addAt(arg); }

	/**Subtraction of a long Number in Place: -= arg	 */
	public IOpLong subt(long arg) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).subAt(arg); }

	/**Multiplication of a long Number in Place: *= arg	 */
	public IOpLong mul	(long arg) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).mulAt(arg); }

	/**Division of a long Number in Place: /= arg	 */
	public IOpLong div	(long arg) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).divAt(arg); }



	/**Multiplication with an Integer Power of 2 in Place:	 */
//	public OpLong mul2PowAt(int n);

	/**Multiplication with an Integer Power of 2:	 */
	public IOpLong mul2Pow  (int n) { return ((IOpLong) copy()).mul2PowAt(n); }

	/**Returns true when this Object is positive: > 0	 */
//	public boolean positive() { return grtr(0); }

	/**Returns true when this Object is negative: < 0	 */
//	public boolean negative() { return less(0); }

	/**Setting to 0 in Place: = 0	 */
	public boolean isZero() { return equals(0); }

	/**Setting to 1 in Place: = 1	 */
	public boolean isOne() { return equals(1); }

	/**Setting to 0 in Place: = 0	 */
	public IOpLong zeroAt() { copyAt(0); return this; }

	/**Setting to 1 in Place: = 1	 */
	public IOpLong  oneAt() { copyAt(1); return this; }

	//These Operations can be defined independent from any Operand Type,
	//since they only operate on an Object self.
	//They could be put into a separate Interface, but what would that help
	//except for more casting?

	/**Negation in Place: = -x	 */
//	public OpLong negAt();

	/**Negation: -x	 */
	public IOpLong neg() { return ((IOpLong) copy()).negAt(); }

	/**Inversion in Place: = 1/x	 */
//	public OpLong invAt();

	/**Inversion: 1/x	 */
	public IOpLong inv() { return ((IOpLong) copy()).invAt(); }

	/**Setting to 0 in Place: = 0	 */
	public IOpLong zero() { return ((IOpLong) copy()).zeroAt(); }

	/**Setting to 1 in Place: = 1	 */
	public IOpLong one() { return ((IOpLong) copy()).oneAt(); }

	/**Multiplication by 2 in Place: *= 2	 */
//	public OpLong dblAt();

	/**Multiplication by 3 in Place: *= 3	 */
//	public OpLong trplAt();

	/**Multiplication by 4 in Place: *= 4	 */
//	public OpLong quadAt() { dblAt(); return dblAt(); }

	/**Division by 2 in Place: /= 2	 */
//	public OpLong halfAt();

	/**Division by 3 in Place: /= 3	 */
//	public OpLong thirdAt();

	/**Division by 4 in Place: /= 4	 */
//	public OpLong quarterAt() { halfAt(); return halfAt(); }

	/**Multiplication by 2 in Place: *= 2	 */
	public IOpLong dbl() { return ((IOpLong) copy()).dblAt(); }

	/**Multiplication by 3 in Place: *= 3	 */
	public IOpLong trpl() { return ((IOpLong) copy()).trplAt(); }

	/**Multiplication by 4 in Place: *= 4	 */
	public IOpLong quad() { return ((IOpLong) copy()).quadAt(); }

	/**Division by 2 in Place: /= 2	 */
	public IOpLong half() { return ((IOpLong) copy()).halfAt(); }

	/**Division by 3 in Place: /= 3	 */
	public IOpLong third() { return ((IOpLong) copy()).thirdAt(); }

	/**Division by 4 in Place: /= 4	 */
	public IOpLong quarter() { return ((IOpLong) copy()).quarterAt(); }

	/**Square: ^2	 */
	public IOpLong sqr() { return ((IOpLong) copy()).sqrAt(); }

	/**Cubic: ^3	 */
	public IOpLong cbc() { return ((IOpLong) copy()).cbcAt(); }

	/**Quadratic: ^4	 */
	public IOpLong qad() { return ((IOpLong) copy()).qadAt(); }

	/**Multiplication by 4 in Place: *= 4	 */
	public IOpLong quadAt() { dblAt(); return dblAt(); }

	/**Division by 4 in Place: /= 4	 */
	public IOpLong quarterAt() { halfAt(); return halfAt(); }

	/**Quadratic in Place: ^= 4	 */
	public IOpLong qadAt() { sqrAt(); return sqrAt(); }

	/**  Linear Mapping: x * a + y			*/
	public IOpLong Lin			(long a, long y) {
		return ((IOpLong) copy(Integer.MAX_VALUE)).LinAt(a, y); }

}
