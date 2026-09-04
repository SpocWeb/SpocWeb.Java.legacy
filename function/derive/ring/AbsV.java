package function.derive.ring;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.IScalarMetric;
import function.derive.ADeriveAble;
import function.derive.CCountAble;

/**Implements a non continuous Version of the AbsV Function,
 * which returns the absolute Value of the Argument.
 * For Vectors this Function returns the Metric. */
public class AbsV
extends ADeriveAble {

	/**Local Reference to the single Instance	 */
	final static public AbsV AbsV = new AbsV();

	/**private Constructor for Singleton Implementation	 */
	private AbsV() {
		//there is no simple Representation
		Inverse = this; //The Inverse is only partially defined, no Function
		setDerivative(Sign.Sign);
		setIntegral(new Prod(Sign.Sign, Square.SQUARE)); //
	}

	/**This Function represents the AbsV Function.
	 * It always returns the AbsV of the Argument.  */
	public Object Map (Object arg) {
//		return ((MetricIRing) arg).AbsV();
		if (((IScalarMetric)arg).negative())
			return CCountAble._One;
			return CCountAble. One; }

	/**This Function represents the AbsV Function.
	 * It always returns the AbsV of the Argument.  */
	public Object MapAt (Object arg) {
		return ((IMetricIRing) arg).AbsVAt(); }

	/**This Function represents the AbsV Function.
	 * It always returns the AbsV of the Argument.  */
	public double Map (double arg) { return Math.abs(arg); }

	/**This Function represents the AbsV Function.
	 * It always returns the AbsV of the Argument.  */
	public float  Map (float  arg) { return Math.abs(arg); }

}
