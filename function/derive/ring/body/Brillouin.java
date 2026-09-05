package function.derive.ring.body;

//import Stream.Copy.*;
//import Functions.ByRef.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.AFunction;
import function.ICountAble;
import function.IFloatFunction;
import function.byref.ByRefFloat;

/**This Class encapsulates the Brillouin Function.
 * This Function is defined by
 * The Limit for n-> Infinity is the Langevin Function:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:42:07Z
 * digest: f1e5747d199ef9764d2b463275485f98f676ede0a3f18898d734ce123f15f3bd
 * stale: false
 * tags: [code/mathematical_function]
 * concepts: [Thermodynamics, Magnetism]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * L(x) = CotH(x) - 1/x and plays a Role in Thermodynamics of Solids	 */
public class Brillouin
extends AFunction //extends AFloatDeriveAble
implements IFloatFunction
{
	/**continuous Parameter changeable from outside	 */
	public MetricBody numSpins;
	
	/**continuous Parameter changeable from outside	 */
	public double numSpinsFloat;
	
	/**Constructor taking the Parameter j	 */
	public Brillouin(MetricBody num) { numSpins = num; }
	
	/**Constructor taking the Parameter j	 */
	public Brillouin(double num) { numSpinsFloat = num; }
	
    /**Reports that Brillouin imposes no particular Ordering on its Argument.
     * @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/**This Function represents the Brillouin Function.	 */
	public double Map (double arg) {
		return BRILLOUIN(numSpinsFloat, arg); }

	/**This Function represents the Brillouin Function.	 */
	public float Map (float arg) {
		return (float) BRILLOUIN(numSpinsFloat, arg); }

	/**This Function represents the Brillouin Function.	 */
	public Object Map (Object arg) {
		return BRILLOUIN(numSpins, (MetricBody) arg); }

	/**Brillouin Function:	lim (j -> infin) B (j,x) = L (x)
	 * The Limit of the Brillouin Function is the Langevin Function.	 */
	public static MetricBody BRILLOUIN (MetricBody x, MetricBody j) {
		MetricBody jj = (MetricBody)  j.dbl (); jj.invAt();
		MetricBody sx = (MetricBody) jj.succ();
		MetricBody g  = (MetricBody)  x.mul(j);
		x = (MetricBody) x.add(g);
		if (x.AbsV().isMoreThan(x.Accuracy())) { 		//conventional Calculation using ln()
			 sx.divAt(x.TanH()) ; sx.subAt
			(jj.divAt(g.TanH()));
			 return sx; }
			 sx.mulAt(Langevin.LANGEVIN(x)) ; sx.subAt
			(jj.mulAt(Langevin.LANGEVIN(g)));
			 return sx; }

	/**Brillouin Function:	lim (j -> infin) B (j,x) = L (x)
	 * The Limit of the Brillouin Function is the Langevin Function.	 */
	public static double BRILLOUIN (double x, double j) {
		double jj = ICountAble.ONE/(j+j);
		double sx = ICountAble.ONE + jj;
		double g  =  x * j;
		x += g;
		if (Math.abs(x) > ByRefFloat.FloatAccuracy) { 		//conventional Calculation using ln()
			 return sx/TanH.TanH.Map(x) - jj/TanH.TanH.Map(g); }
			 return sx*Langevin.LANGEVIN(x) - jj*Langevin.LANGEVIN(g); }

}
