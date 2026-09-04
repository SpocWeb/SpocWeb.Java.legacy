package function.derive.ring.body;

//import Stream.Copy.*;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.object.IStreamIn;
import function.AFunction;
import function.ICountAble;
import function.IFloatFunction;
import function.IFunction;
import function.byref.ByRefFloat;
import function.byref.ByRefLong;
import function.byref.combinatoric.Bernoulli;

/**This Class encapsulates the Langevin Function.
 * In Thermodynamics L and B are the average Magnetization:
 * L (     (mu*B)/(k*T)) with continuously turnable Dipols and
 * B (j, j*(mu*B)/(k*T)) with discrete (Drehimpuls-)Positions
 * mu is the magnetic Dipolmoment
 * B (1/2,x) == tanh x
 *
 * German:
 * In der Thermodynamik geben L und B die mittl. Magnetisierung an:
 * L (     (mu*B)/(k*T)) bei kontinuierlich drehbaren Dipolen und
 * B (j, j*(mu*B)/(k*T)) bei diskreten (Drehimpuls-)Stellungen
 * mu ist dabei das magnetische Dipolmoment
 * Weiterhin gilt: B (1/2,x) = tanh x
 */
public class Langevin
extends AFunction
implements IFloatFunction
{
    
	/**The single Instance of the Langevin Function.  */
	public static IFunction Langevin =  new Langevin();
	
	/**Private Constructor for the single Instance of the Langevin Function.  */
	private Langevin() {}
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_NONE; }
    
	/**This Function represents the Langevin Function.  */
	public Object Map (Object arg) { return LANGEVIN((MetricBody) arg); }
	
	/**This Function represents the Langevin Function.  */
	public double Map (double arg) { return LANGEVIN(arg); }
	
	/**This Function represents the Langevin Function.  */
	public float Map (float arg) { return (float) LANGEVIN(arg); }
	
	/**Langevin Function:	L (x) = CotH (x) - 1/x
	 * calculated as a Power Series.
	 * Uses the Bernoulli Sequence to do that.	 */
	final static public MetricBody LANGEVIN  (MetricBody arg) {
		MetricBody Accuracy = (MetricBody) arg.AbsV();	//Use the sign to save the abs() on testing
		if (Accuracy.isMoreThan(arg.Accuracy())) {		//conventional Calculation using ln()
			MetricBody ret = arg.TanH(); ret.invAt(); ret.subAt(arg.inv());
			 return ret; }
		MetricBody x       = (MetricBody) arg.dbl ();
		MetricBody Summe   = (MetricBody) arg.zero();
		MetricBody Quadrat = (MetricBody) x  .sqr ();
		MetricBody Faktor  = x;
		int Z1  = 1;
		ByRefLong Divisor = new ByRefLong();
		do {
			int Z2 = (Z1+1) << 1;
			MetricBody f = (MetricBody) Faktor.mul(new Double(Bernoulli.BERNOULLI(Z1)));	//uses the Array of Bernoulli Numbers
			Summe.addAt(f);
			if (Accuracy.notLessThan(f.AbsV())) return Summe;
			Divisor.Value = -Z2*(Z2-1);
			Faktor.divAt(Divisor).mulAt(Quadrat);
		} while (true);//(Z1  <= MaxBernoulli);
//		throw new AbstractMethodError();
	}

	/**Langevin Function:	L (x) = CotH (x) - 1/x
	 * calculated as a Power Series.
	 * Uses the Bernoulli Sequence to do that.	 */
	final static public double LANGEVIN  (double arg) {
		double Accuracy = Math.abs(arg);	//Use the sign to save the abs() on testing
		if (Accuracy > ByRefFloat.FloatAccuracy) {		//conventional Calculation using ln()
			return  ICountAble.ONE/TanH.TanH.Map(arg) -
					ICountAble.ONE/              arg; }
		double x       = arg + arg;
		double Summe   = ICountAble.ZERO;
		double Quadrat = x*x;
		double Faktor  = x;
		int Z1  = 1;
		do {
			int Z2 = (Z1+1) << 1;
			double f = Faktor * Bernoulli.BERNOULLI(Z1);	//uses the Array of Bernoulli Numbers
			Summe += f;
			if (Accuracy >= Math.abs(f)) return Summe;
			Faktor *= Quadrat / (-Z2*(Z2-1));
		} while (true);//(Z1  <= MaxBernoulli);
//		throw new AbstractMethodError();
	}

}
