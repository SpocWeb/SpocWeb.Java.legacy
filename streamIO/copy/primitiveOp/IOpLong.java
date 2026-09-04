package streamIO.copy.primitiveOp;

import streamIO.copy.ICopyAble;
import function.IOrderAble;

/**This Interface adds all Methods to the IOpLong
 * that can be indirectly defined by IOpLong */
public interface IOpLong
extends IIOpLong, ICopyAble, IOrderAble {

	/**Maximum in Place: 	*/
	public IOpLong Max (long arg);

	/**Minimum in Place: 	*/
	public IOpLong Min (long arg);

	/**Addition of a long Number in Place: += arg	 */
	public IOpLong add (long arg);

	/**Subtraction of a long Number in Place: -= arg	 */
	public IOpLong subt(long arg);

	/**Multiplication by a long Number in Place: *= arg	 */
	public IOpLong mul (long arg);

	/**Division by a long Number in Place: /= arg	 */
	public IOpLong div (long arg);

	/**Multiplication with an Integer Power of 2 in Place:	 */
	public IOpLong mul2PowAt(int n);

	/**Multiplication with an Integer Power of 2:	 */
	public IOpLong mul2Pow  (int n);

	/**Returns true when this Object is positive: > 0	 */
	public boolean positive();

	/**Returns true when this Object is negative: < 0	 */
	public boolean negative();

	/**Setting to 0 in Place: = 0	 */
	public boolean isZero();

	/**Setting to 1 in Place: = 1	 */
	public boolean isOne();

	/**Setting to 0 in Place: = 0	 */
	public IOpLong zeroAt();

	/**Setting to 1 in Place: = 1	 */
	public IOpLong oneAt();

	//These Operations can be defined independent from any Operand Type,
	//since they only operate on an Object self.
	//They could be put into a separate Interface, but what would that help
	//except for more casting?

	/**Negation in Place: = -x	 */
	public IOpLong negAt();

	/**Negation: -x	 */
	public IOpLong neg();

	/**Inversion in Place: = 1/x	 */
	public IOpLong invAt();

	/**Inversion: 1/x	 */
	public IOpLong inv();

	/**Setting to 0 in Place: = 0	 */
	public IOpLong zero();

	/**Setting to 1 in Place: = 1	 */
	public IOpLong one();

	/**Multiplication by 2 in Place: *= 2	 */
	public IOpLong dblAt();

	/**Multiplication by 3 in Place: *= 3	 */
	public IOpLong trplAt();

	/**Multiplication by 4 in Place: *= 4	 */
	public IOpLong quadAt();

	/**Division by 2 in Place: /= 2	 */
	public IOpLong halfAt();

	/**Division by 3 in Place: /= 3	 */
	public IOpLong thirdAt();

	/**Division by 4 in Place: /= 4	 */
	public IOpLong quarterAt();

	/**Multiplication by 2: * 2	 */
	public IOpLong dbl();

	/**Multiplication by 3: * 3	 */
	public IOpLong trpl();

	/**Multiplication by 4: * 4	 */
	public IOpLong quad();

	/**Division by 2: / 2	 */
	public IOpLong half();

	/**Division by 3: / 3	 */
	public IOpLong third();

	/**Division by 4: / 4	 */
	public IOpLong quarter();

	/**Square in Place: ^= 2	 */
	public IOpLong sqrAt();

	/**Cubic in Place: ^= 3	 */
	public IOpLong cbcAt();

	/**Quadratic in Place: ^= 4	 */
	public IOpLong qadAt();

	/**Square: ^2	 */
	public IOpLong sqr();

	/**Cubic: ^3	 */
	public IOpLong cbc();

	/**Quadratic: ^4	 */
	public IOpLong qad();

	/**  Linear Mapping in Place: x*=a + y	*/
	public IOpLong LinAt		(long a, long y);

	/**  Linear Mapping: x * a + y			*/
	public IOpLong Lin		(long a, long y);

}
