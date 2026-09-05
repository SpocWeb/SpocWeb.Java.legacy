package streamIO.copy.group.ring.metric.body.complex;

import streamIO.copy.group.ring.IComplex;
import streamIO.copy.group.ring.IIntRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.body.CBody;

/**The Advantage of these Constants is that they can be very quickly checked
 * for equality enabling considerable savings in Operations by using
 * the fast Pointer Comparison opposed to the slow Float Point Comparison.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 22b8fa0164602b93d7c57cf77573fcc0048660640a026184a9405621654083e5
 * stale: false
 * tags: [code/complex_numbers, code/fourier_transform]
 * concepts: [Complex Number Arithmetic and Fourier Transform]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 */
public class CComplex
extends CBody
//implements IComplex //has moved to Ring and doesn't contain the following Methods...
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**Initializing Constructor		 */
	public CComplex(Complex cnst) { super(cnst); }

	/**Multiplies the Complex Number by i or divides it by -i in Place:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90�	 */
	public IIntRing mulIAt() { throw new AbstractMethodError(strConst); }

	/**Multiplies the Complex Number by i or divides it by -i:
	 * i.e. Im <= Re and Re <= -Im, which is a Rotation by +90�	 */
	public IIntRing MulI() { return ((IComplex) inner).MulI(); }

	/**Divides the Complex Number by i or multiplies it by -i in Place:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90�	 */
	public IIntRing divIAt() { throw new AbstractMethodError(strConst); }

	/**Divides the Complex Number by i or multiplies it by -i:
	 * i.e. Im <= -Re and Re <= Im, which is a Rotation by -90�	 */
	public IIntRing DivI() { return ((IComplex) inner).DivI(); }

	/**Addition of the conjugate complex argument in Place: +=	 */
	public IIntRing addAtCjg(Object arg) { throw new AbstractMethodError(strConst); }

	/**Addition of the conjugate complex argument: +=	 */
	public IIntRing addCjg(Object arg) { return ((IComplex) inner).addCjg(arg); }

	/**Subtraction of the conjugate complex argument in Place: -=	 */
	public IIntRing subAtCjg(Object arg) { throw new AbstractMethodError(strConst); }

	/**Subtraction of the conjugate complex argument: -=	 */
	public IIntRing subtCjg(Object arg) { return ((IComplex) inner).subtCjg(arg); }

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulAtCjg(Object arg) { throw new AbstractMethodError(strConst); }

	/**Multiplication by the conjugate complex argument in Place: *=	 */
	public IIntRing mulCjg(Object arg) { return ((IComplex) inner).mulCjg(arg); }

	/**Returns the largest (closest to positive infinity) value,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing Floor() { return ((IMetricIRing) inner).Floor(); }

	/**Returns the largest (closest to positive infinity) value in Place,
	 * that is not greater than the argument
	 * and is equal to a mathematical integer. 	 */
	public IMetricIRing FloorAt() { throw new AbstractMethodError(strConst); }

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divCjg(Object arg) { return ((IComplex) inner).divCjg(arg); }

	/**Division by the conjugate complex argument in Place: /=
	 * obige Implementation vermeidet Genauigkeitsverlust und einen �berlauf durch die Quadrierung
	 * und spart au�erdem effektiv 2 Sqr und wendet nur 1 Vergleich mehr an als andere.	 */
	public IIntRing divAtCjg(Object arg) { throw new AbstractMethodError(strConst); }

}
