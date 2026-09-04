package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import function.AFunction;

/**This Class encapsulates the periodic Trunc()/SawTooth() Function.
 * It maps the Argument to the Range [0, 1)
 * It can be used to make another Function periodic in this Range.
 * If you need a different Range of Periodicity, subtract e.g. 0.5
 * and stretch the Range.
 * Concatenated Functions that use SawTooth for Periodicity
 * must use AFunction to delegate.	 */
public class SawToothAt
	extends AFunction
//	implements IDeriveAble
{
	/**This Function represents the periodic Trunc/SawTooth Function.
	 * It maps the Argument to the Range [0, 1)  */
	public Object Map (Object arg)
	{return ((IIntRing) arg).subAt(((IIntRing) arg).IntAt());}

}
