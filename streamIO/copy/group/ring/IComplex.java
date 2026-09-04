package streamIO.copy.group.ring;

/**Defines the Operations for Complex Conjugation for the IIntRing already,
 * because it is used in Tensor Arithmetic */
public interface IComplex {

	/**Returns the conjugate Complex Number:
	 * i.e. the imaginary Part flips it's sign.	 */
	IIntRing cjg();

	/**Returns the conjugate Complex Number in Place:
	 * i.e. the imaginary Part flips it's sign.	 */
	IIntRing cjgAt();

	/**Addition of the conjugate complex argument in Place: +=	 */
	IIntRing addAtCjg(Object arg);

	/**Addition of the conjugate complex argument: +=	 */
	IIntRing addCjg(Object arg);

	/**Subtraction of the conjugate complex argument in Place: -=	 */
	IIntRing subAtCjg(Object arg);

	/**Subtraction of the conjugate complex argument: -=	 */
	IIntRing subtCjg(Object arg);

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	IIntRing mulAtCjg(Object arg);

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	IIntRing mulCjg(Object arg);

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen ‹berlauf durch die Quadrierung
	 * und spart auﬂerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	IIntRing divCjg(Object arg);

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen ‹berlauf durch die Quadrierung
	 * und spart auﬂerdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	IIntRing divAtCjg(Object arg);

	/**Multiplies the Complex Number by i or divides it by -i in Place:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90∞	 */
	IIntRing mulIAt();

	/**Multiplies the Complex Number by i or divides it by -i:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90∞	 */
	IIntRing MulI();

	/**Divides the Complex Number by i or multiplies it by -i in Place:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90∞	 */
	IIntRing divIAt();

	/**Divides the Complex Number by i or multiplies it by -i:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90∞	 */
	IIntRing DivI();

	/**Testing Method, should be static or directly tested on the Types.
	 * Normally there are only these two Representations: Complex and Polar
	 * Determines, whether special treatment (e.g. Complex Conjugation)
	 * is necessary on this Object with certain Operations. 	 */
	boolean isComplex();

}
