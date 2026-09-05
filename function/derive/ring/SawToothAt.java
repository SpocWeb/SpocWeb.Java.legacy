package function.derive.ring;

import streamIO.copy.group.ring.IIntRing;
import function.AFunction;

/**This Class encapsulates the periodic Trunc()/SawTooth() Function.
 * It maps the Argument to the Range [0, 1)
 * It can be used to make another Function periodic in this Range.
 * If you need a different Range of Periodicity, subtract e.g. 0.5
 * and stretch the Range.
 * Concatenated Functions that use SawTooth for Periodicity
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 5b3dde82e69faa8284290b1277620d9117ac1e88fb769acf01ff7a28496e400a
 * stale: false
 * tags: [code/mathematical_function]
 * concepts: [Function Algebra, Periodic Functions]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
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
