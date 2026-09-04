package function.derive.ring.body.vector;

import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.group.ring.metric.body.vector.Tensor;
import streamIO.copy.groupM.IGroupM;
import function.AFunction;
import function.IMeasurAble;

/**Returns the Product of the Sinusses of all Coordinates times Pi,
 * i.e. the full Period fits into the unit Circle. (Example Function)  */
public class fSinProd
extends AFunction {

	public Object Map(Object arg) {
		Tensor V = (Tensor) arg;
		int Length = V.getDim();
		int i = 0;
		IGroupM Prod =  ((MetricBody) V.a[0].mul(IMeasurAble.pi)).sin();
		while (++i < Length)
			Prod.mulAt(((MetricBody) V.a[i].mul(IMeasurAble.pi)).sin());
		return  Prod; }

}
