package function.derive.ring.body;

import java.io.IOException;

import streamIO.Log;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.body.Body;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;
import function.derive.ADeriveAble;
import function.derive.ring.Algebra;
import function.derive.ring.CatDerive;
import function.derive.ring.LinAt;
import function.derive.ring.MulAt;
import function.derive.ring.Prod;
import function.derive.ring.Quot;
import function.derive.ring.SqRt;
import function.derive.ring.Square;
import function.derive.ring.Succ;

//TODO: not properly defined yet!

/**This Class encapsulates the real Elliptic Integral (ElliptInt) Function
 * of the 2nd Kind
 *					y                              arctan y
 *  EIn (y,k,a,b) = |       (a+b*x^2) dx             |          a+b*tan^2 t
 *					|---------------------------- =  |dt ---------------------------
 *					0 (1+x^2)\/((1+x^2)(1+k^2x^2))   0  \/((1+tan^2 t)(1+k^2tan^2 t))
 *
 * For large y this Integral is dominated by ???
 * For small y this Integral approximates ???
 *
 * the Derivative:
 *        (a+b*x^2) dx
 * ----------------------------
 * (1+x^2)\/((1+x^2)(1+k^2x^2))
 *
 *
 * See Numerical Recipes 2nd Ed. p  ()	 */
public class ElliptInt
extends ADeriveAble	//IPartialDerive //
{
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(ElliptInt.class);

	////////////////////////////////////////////////////////////////////////////

	/**continuous Parameter, not changeable from outside	 */
	protected MetricBody k;

	/**continuous Parameter, not changeable from outside	 */
	protected MetricBody a;

	/**continuous Parameter, not changeable from outside	 */
	protected MetricBody b;

	static { //Initializer
	}

	/**Initializing Constructor	 */
	public ElliptInt(Object k, Object a, Object b) {
		this.k = (MetricBody) ((MetricBody) k).copy();	//Create Copy to keep TX short and avoid Side Effects!
		this.a = (MetricBody) ((MetricBody) a).copy();	//Create Copy to keep TX short and avoid Side Effects!
		this.b = (MetricBody) ((MetricBody) b).copy();	//Create Copy to keep TX short and avoid Side Effects!

		setDerivative(new Quot(              new CatDerive(new LinAt(b, a), Square.SQUARE),
					  new Prod(Algebra.xx_1, new CatDerive(SqRt.SQRT,
					  new Prod(Algebra.xx_1, new CatDerive(Succ.SUCC,
											 new MulAt(((IGroupM)k).sqr())))))));
	}

	/**This Function represents the Sinus Function.  */
	public Object Map (Object arg) { return ELLIPTIC_INTEGRAL((MetricBody) arg, k, a, b); }

	/**This Function represents the Sinus Function.  */
	public double Map (double arg) { return ELLIPTIC_INTEGRAL(arg,
		ByRefDouble.GET_DOUBLE(k),
		ByRefDouble.GET_DOUBLE(a),
		ByRefDouble.GET_DOUBLE(b)); }

	/**elliptic Integral of the 2nd Kind
	 *					y                              arctan y
	 *  EIn (y,k,a,b) = |       (a+b*x^2) dx             |          a+b*tan^2 t
	 *					|---------------------------- =  |dt ---------------------------
	 *					0 (1+x^2)\/((1+x^2)(1+k^2x^2))   0    \/((1+tan^2 t)(1+k^2tan^2 t))
	 * */
	final static public MetricBody ELLIPTIC_INTEGRAL(MetricBody y, MetricBody k, MetricBody a, MetricBody b)
	{	//several Factors could be precalculated and cached.
		if (k.isZero()) return null;	//no elliptic Integral for k == 0
		if (y.isZero()) return y;
//		cb = Genauigkeit+Integer (LongInt (Genauigkeit) >> 1);	//1.5 * Genauigkeit
		MetricBody Quadrat = (MetricBody) k.copy();
		MetricBody c = (MetricBody) y.sqr();
		MetricBody d = (MetricBody) c.succ();
		MetricBody Faktor =  (MetricBody)
						    ((MetricBody)
							((Body)
							((Body)Quadrat.sqr().mulAt(c)).inc()).divAt(d)).SqRt();
		d.invAt().mulAt(y);
		c = (MetricBody) d.div(Faktor.dbl());
		MetricBody Hilf = (MetricBody) a.sub(b);
		MetricBody sx = (MetricBody) a.copy();
	  ((MetricBody)a.addAt(b)).halfAt();
		MetricBody Skalar = (MetricBody)
						   ((MetricBody) y.inv()).AbsVAt();
		MetricBody f = (MetricBody) ((MetricBody) y.newInstance()).zeroAt();
		ByRefLong Z1 = new ByRefLong(0);
		MetricBody r = (MetricBody) ((MetricBody) y.newInstance()).oneAt();
		Quadrat.AbsVAt();
		while (true) {
			b.addAt(sx.mul(Quadrat));
			MetricBody e = (MetricBody) r.mul(Quadrat);
			MetricBody g = (MetricBody) e.div(Faktor);
			d.addAt(f.mul(g));
			f.copyAt(c);
			sx.copyAt(a);
			Faktor.addAt(g);
			((Body) c.addAt(d.div(Faktor))).halfAt();
			g.copyAt(r);
			r.addAt(Quadrat);
			((Body) a.addAt(b.div(r))).halfAt();
			Skalar.subAt(e.div(Skalar));
			if (Skalar.isZero()) Skalar = (MetricBody) e.SqRt().mulAccuracyAt();
			MetricBody Accuracy = (MetricBody) g.mulAbsAccuracy();
			if (g.AbsDist(Quadrat).notMoreThan(Accuracy)) break;
			Quadrat = (MetricBody) e.SqRt().dbl();
			Z1.Value <<= 1;
			if (Skalar.negative()) ++Z1.Value;
		}
		if (Skalar.negative()) ++Z1.Value;
		MetricBody e = (MetricBody)
					  ((IGroupM)
					  ((MetricBody) r.div(Skalar)).ArcTanAt().addAt(y.pi().mulAt(Z1))).divAt(r).mulAt(a);
		if (y.negative()) e.negAt();
		return (MetricBody) ((IGroup) c.mulAt(Hilf)).addAt(e); }

	/**elliptic Integral of the 2nd Kind
	 *					y                              arctan y
	 *  EIn (y,k,a,b) = |       (a+b*x^2) dx             |          a+b*tan^2 t
	 *					|---------------------------- =  |dt ---------------------------
	 *					0 (1+x^2)\/((1+x^2)(1+k^2x^2))   0    \/((1+tan^2 t)(1+k^2tan^2 t))
	 * */
	final static public double ELLIPTIC_INTEGRAL(double y, double k, double a, double b)
	{	//several Factors could be precalculated and cached.
		if (k == ICountAble.ZERO) return ICountAble.ZERO;	//no elliptic Integral for k == 0! TODO:
		if (y == ICountAble.ZERO) return y;
//		cb = Genauigkeit+Integer (LongInt (Genauigkeit) >> 1);	//1.5 * Genauigkeit
		double Quadrat = k;
		double c = y*y;
		double d = c + ICountAble.ONE;
		double Faktor = Math.sqrt((Quadrat*Quadrat*c  + ICountAble.ONE)/d);
		d = y/d;
		c = d/(Faktor+Faktor);
		double Hilf = a - b;
		double sx = a; a = IMeasurAble.HALF*(a + b);
		double Skalar = ICountAble.ONE/Math.abs(y);
		double f = ICountAble.ZERO;
		long Z1 = 0;
		double r = ICountAble.ONE;
		Quadrat = Math.abs(Quadrat);
		while (true) {
			b += sx * Quadrat;
			double e = r * Quadrat;
			double g = e / Faktor;
			d += f * g;
			f = c;
			sx = a;
			Faktor += g;
			c = IMeasurAble.HALF*(c+d/Faktor);
			g = r;
			r += Quadrat;
			a = IMeasurAble.HALF*(a + b/r);
			Skalar -= e / Skalar;
			if (Skalar == ICountAble.ZERO)
				Skalar =  ByRefDouble.MUL_ACCURACY(Math.sqrt(e));
			double Accuracy = ByRefDouble.MUL_ABS_ACCURACY(g);
			if (Math.abs(g - Quadrat) <= Accuracy) break;
			Quadrat = Math.sqrt(e)* ICountAble.TWO;
			Z1 <<= 1;
			if (Skalar < ICountAble.ZERO) ++Z1;
		}
		if (Skalar < ICountAble.ZERO) ++Z1;
		double e = (Math.atan(r / Skalar) + IMeasurAble.PI * Z1) * a / r;
		if (y < ICountAble.ZERO) e = -e;
		return c * Hilf + e; }

	final static public double [][] ValuesElliptInt = {
			{7.920841, 2.368900, 0.667193, 5.486636, 2.22190262041},
			{1.055195, 1.638578, 0.416069, 4.939865, 0.87082126246},
			{3.002419, 4.923194, 8.454750, 0.006682, 3.47340034309},
			{9.046349, 2.907049, 6.249388, 7.907540, 5.60571599832},
			{1.538278, 2.609869, 3.021798, 5.859919, 2.49451302067},
			{6.897015, 2.943992, 6.606239, 4.215869, 4.66751169197},
			{5.119719, 4.339126, 2.074272, 0.434918, 0.99274227096},
			{3.160050, 1.150061, 6.308981, 0.646321, 5.01268576382},
			{2.873443, 1.555546, 8.259732, 8.377333, 8.51971160283},
			{8.593059, 0.037591, 9.406861, 4.754159,18.03731663351},
			{9.703741, 2.307719, 2.751892, 1.238335, 2.07218199026},
			{4.118188, 0.750782, 7.341705, 6.717090,10.51816994251},
			{0.784675, 1.962689, 9.574056, 8.373047, 5.43972902448},
			{1.177939, 0.079235, 3.210206, 0.929670, 2.66695926111},
			{2.302275, 1.013283, 3.931196, 0.911011, 3.35093395811},
			{9.947147, 2.133705, 4.728793, 0.396376, 3.05444622014},
			{1.553283, 0.321615, 8.127578, 3.955491, 8.12036504757},
			{4.619238, 4.888860, 6.798970, 0.008925, 2.81551818449},
			{5.345483, 2.841729, 2.117618, 0.102778, 1.17730082562},
			{6.394912, 2.184548, 0.334446, 7.320561, 2.72291554970}
		};

	/**Tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		System.out.println("Testing ElliptInt()");
		System.out.println("x =		k=		a=		b=		cEl=	R=");
		int i = -1;
		while (++i < ValuesElliptInt.length) {
			final double[] test = ValuesElliptInt[i];
			final MetricBody x = new BodyDouble(test[0]);
			final MetricBody k = new BodyDouble(test[1]);
			final MetricBody a = new BodyDouble(test[2]);
			final MetricBody b = new BodyDouble(test[3]);
			final MetricBody R = new BodyDouble(test[4]);
			L.n(x + "	" + k + "	" + a + "	" + b + "	" + R + "	" + ELLIPTIC_INTEGRAL(x, k, a, b));
		}
		L.readString();
	}

	/**The main entry point for the application.
	 * Prints out the Factorial of the Value passed via the Command Line, 
	 * otherwise performs the self-test.
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length > 0) {
			//System.out.println(value(Integer.parseInt(args[0])));
		} else {
			testIt(); 
		}
	}
	
}
