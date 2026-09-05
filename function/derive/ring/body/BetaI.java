package function.derive.ring.body;

//import java.io.*;
//import Stream.Copy.*;
import math.vector.VectorString;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.body.ABodyDouble;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import function.AFunction;
import function.ICountAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;

/**This Class encapsulates the incomplete Beta Function:
 *
 * BetaI(x, a,b) = (Int[0,x] t^(a-1)*(1-t)^(b-1))/Beta(a,b)
 *
 * It is BetaI(0, a, b) = 0 and BetaI(1, a, b) = 1
 * as well as BetaI(a,b,x) = 1 - BetaI(b,a,1-x) (Symmetry)
 * 
 * This Function rises from (0,0) to (1,1) 
 * with BetaI(x = a/(a+b)) = 0.5
 * 
 * This Function is used to calculate several Functions for Likelihood: 
 * Student's t 
 * Fisher's Z
 * The Power Series doesn't converge well, so use the Continued Fraction.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:42:06Z
 * digest: 25dbf3cc5f692ba08200beaa031cc1b6733c7a78e68ceb29a68738f1d61e9b51
 * stale: false
 * tags: [code/numerical_integration, code/mathematical_function]
 * concepts: [Statistical Distributions, Incomplete Beta Function]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class BetaI
extends AFunction
//implements deriveAble	//IPartialDerive //
{
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(BetaI.class);

	////////////////////////////////////////////////////////////////////////////

	/**continuous Parameter, not changeable from outside	 */
	protected MetricBody a;

	/**continuous Parameter, not changeable from outside	 */
	protected MetricBody b;

	/**Initializing Constructor	 */
	public BetaI(Object a, Object b) {
		this.a = (MetricBody) ((MetricBody) a).copy();	//Create Copy to keep TX short and avoid Side Effects!
		this.b = (MetricBody) ((MetricBody) b).copy();	//Create Copy to keep TX short and avoid Side Effects!
	}

	/**This Function represents the BetaI Function.  */
	public Object Map (Object arg)	{ return BETA_I(a, b, (MetricBody) arg); }

	/**This Function represents the BetaI Function.  */
	public double Map (double arg)	{
		return BETA_I(ByRefDouble.GET_DOUBLE(a),
					 ByRefDouble.GET_DOUBLE(b), arg); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**KettenBruch fuer unvollst. Beta-Fktn	 */
	final static public MetricBody BETA_I_KB (MetricBody a, MetricBody b, MetricBody x) {
		MetricBody app, Hilf;
		MetricBody bpp, tmp;
		MetricBody am = (MetricBody) ((MetricBody) a.newInstance()).oneAt();
		MetricBody bm = (MetricBody) ((MetricBody) a.newInstance()).oneAt();
		MetricBody f  = (MetricBody) ((MetricBody) a.newInstance()).oneAt();
		MetricBody fa  = (MetricBody) a.newInstance();
		MetricBody Quadrat = (MetricBody) a.add(b);
		MetricBody Faktor  = (MetricBody) a.succ();
		MetricBody sx = (MetricBody) a.pred();
		MetricBody ax = (MetricBody) a.copy();
					a = (MetricBody) a.copy();
					b = (MetricBody) b.copy();
//		MetricBody bx = (MetricBody) b.copy();
		MetricBody g = (MetricBody) ((MetricBody) x.div(Faktor).mulAt(Quadrat)).ResidAt();
		for (ByRefInt Z1 = new ByRefInt(0); ++Z1.Value <= ByRefInt.MAX_ITER;) {
			Hilf = (MetricBody) ((IGroupM)b.dec()).div(((IGroupM)sx.addAt(ICountAble.Two)).mul//At
															  (ax.addAt(ICountAble.Two)))
												 .mulAt(Z1)
												 .mulAt(x);
			am.mulAt(Hilf); am.addAt(f);
			bm.mulAt(Hilf); bm.addAt(g);
			a.inc(); //casting is even slower than not reusing the Return Value
			Hilf =	(MetricBody) a.neg();
			Hilf.divAt(ax.mul(Faktor.addAt(ICountAble.Two)));
			Hilf.mulAt(Quadrat.inc()).mulAt(x);
			app = (MetricBody) ((IGroup) f.mul(Hilf)).addAt(am);
			bpp = (MetricBody) ((IGroup) g.mul(Hilf)).addAt(bm);
			tmp = fa; fa = f; f = tmp;
			am.divAt(bpp);
			bm.divAt(bpp);
			f = (MetricBody) app.div(bpp);
			if (Z1.Value == 1) g.oneAt();
			if(f.AbsDist(fa).notMoreThan(f.mulAbsAccuracy())) return f;
		}
		throw new AbstractMethodError();
	}

	/**This Function implements the BetaI Function.  */
	final static public MetricBody BETA_I (final MetricBody g, final MetricBody x, final MetricBody y) {
		MetricBody summe = (MetricBody) x.newInstance();
		if(g.negative() || (g.isMoreThan (ICountAble.One)))
			throw new AbstractMethodError("Unvollstaendige Beta-Funktion nur fuer 0..1 definiert!");
		if(g.isZero  () ||  g.isOne()) summe.zeroAt();   //Vorfaktor des KettenBruchs
		else summe = ((MetricBody)((IGroup)	g.ln  ().mulAt(x)).subAt(GammaLn.BetaLn(x,y)).addAt
					(((MetricBody) g.Resid()).lnAt().mulAt(y))).expAt();
		if (g.notMoreThan(((MetricBody) x.succ()).divAt(x.add(y).addAt(ICountAble.Two))))
			return	  (MetricBody) summe.divAt(x).mulAt(BETA_I_KB (x,y,g)); //{KettenBruch direkt auswerten}
			return	  (MetricBody)
					 ((MetricBody) summe.divAt(y).mulAt(BETA_I_KB (y,x,(MetricBody)
																	g.Resid())))
																	 .ResidAt(); //{erst SymmetrieTransformation}
	}

	/** KettenBruch fuer unvollst. Beta-Fktn	 
	 * Evaluates continued Fraction for incomplete Beta Function by modified Lentz's Method. 
	 * @param a must be > 0
	 * @param b must be > 0
	 * @param x from [0,1] 
	 * @return the Beta Function 
	 */
	final static public double BETA_I_KB(final double a, final double b, final double x) {
		final double qab=a+b;
		final double qap=a+1;
		final double qam=a-1;
		double c=1;
		double d=1/(1-qab*x/qap); //d=1/d;
		double ret=d;	//ret *= d*c;
		for (int m=0; ++m <= ByRefInt.MAX_ITER; ) {
			final int m2=2*m;
			{ //even Step of the Recurrence 
				final double aa=m*(b-m)*x/((qam+m2)*(a+m2));
				d=1+aa*d;
				c=1+aa/c;
			}
			d=1/d;
			ret *= d*c;
			{ //odd Step of the Recurrence
				final double aa = -(a+m)*(qab+m)*x/((a+m2)*(qap+m2));
				d=1+aa*d;
				c=1+aa/c;
			}
			d=1/d;
			final double del=d*c;
			ret *= del;
			if(Math.abs(del-1) <= ByRefDouble.DOUBLE_ACCURACY) {
				break; }
		}
		//if (m > MAXIT) nrerror("a or b too big, or MAXIT too small in betacf");
		return ret;
	}
	
	/**Computes the regularized (normed) Incomplete Beta Function.
	 * @return the Incomplete Beta Function normed by Beta(a,b):
	 *
	 *						1		x
	 *   BetaI(g,x,y) :=--------- *Int t^(a-1)*(1-t)^(b-1)
	 *                  Beta(a,b)	0
	 * For every choice of a and b this Function rises smoothly
	 * from (0, 0) to (1, 1)
	 */
	final static public double BETA_I (final double x, final double a, final double b) {
		if ((x < 0) || 
			(x > 1)) {
			throw new AbstractMethodError("Unvollstaendige Beta-Funktion nur fuer 0..1 definiert! x="+x); } 
		final double betaLn = GammaLn.BetaLn(a,b); //Vorfaktor des KettenBruchs
		final double logFactor = a*Math.log(x) -betaLn + b*Math.log(1-x); 
		final double factor = Math.exp(logFactor);
		if (x <= ((1+a) / (a+b+2))) {
			return	   factor*BETA_I_KB(a, b,   x)/a; } //{KettenBruch direkt auswerten}
			return 1 - factor*BETA_I_KB(b, a, 1-x)/b;   //{erst SymmetrieTransformation}
	}
	
	/**Computes the two-tailed Acceptance Probability for Student's t Test.
	 * @return the Probability to accept the Hypothesis for a Variable with Student's t Distribution.
	 * Actually Student's t Distribution converges to the normal Gaussian Distribution with growing Degrees of Freedom.
	 * Student's t is used for small Sample Sizes (DoF < 30) in which the Central Limit Theorem does not (yet) apply.
	 */
	final static public double PROBABILITY_STUDENT_T(final double degreesOfFreedom, final double t) {
		return BetaI.BETA_I(degreesOfFreedom/(degreesOfFreedom+t*t), 0.5*degreesOfFreedom, 0.5); }

	/**Computes the Acceptance Probability for Fisher's F Test.
	 * @return the Probability to accept the Hypothesis for a Variable with Fisher's F Distribution	 */
	final static public double PROBABILITY_FISHER_F(final double df1, final double df2, final double f) {
		return BetaI.BETA_I(df2/(df2+df1*f), 0.5*df2, 0.5*df1); }

	/**Computes the cumulative Binomial Distribution Probability via its Beta Function Relation.
	 * @return the Probability to accept the Hypothesis for a Variable with Binomial Distribution	 */
	final static public double PROBABILITY_BINOMIAL_CUM(final double n, final double k, final double p) {
		return BetaI.BETA_I(p, k, n-k+1); }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Values of the incomplete BetaI Function: {a, b, x, BetaI(x)}	 */
	private static final float[][]
		ValuesBetaI  = {
			{ 0.5f,  0.5f, 0    , 0         },
			{ 0.5f,  0.5f, 0.01f, 0.0637686f},
			{ 0.5f,  0.5f, 0.10f, 0.2048328f},
			{ 0.5f,  0.5f, 0.50f, 0.5f      },
			{ 0.5f,  0.5f, 1    , 1         },
			{ 1.0f,  0.5f, 0    , 0         },
			{ 1.0f,  0.5f, 0.01f, 0.0050126f},
			{ 1.0f,  0.5f, 0.10f, 0.0513167f},
			{ 1.0f,  0.5f, 1    , 1         },
			{ 1.0f,  1.0f, 0    , 0         },
			{ 1.0f,  1.0f, 1    , 1         },
			{ 1.0f,  1.0f, 0.5f , 0.5f      },
			{ 5.0f,  5.0f, 0    , 0         },
			{ 5.0f,  5.0f, 0.5f , 0.5f      },
			{ 5.0f,  5.0f, 1    , 1         },
			{10.0f, 10.0f, 0    , 0         },
			{10.0f, 10.0f, 1    , 1         },
			{10.0f,  0.5f, 0    , 0         },
			{10.0f,  0.5f, 0.90f, 0.1516409f},
			{10.0f,  0.5f, 1    , 1         },
			{10.0f,  5.0f, 0.50f, 0.0897827f},
			{10.0f,  5.0f, 1    , 1         },
			{10.0f, 10.0f, 0.5f , 0.5f      },
			{20.0f,  5.0f, 0    , 0         },
			{20.0f,  5.0f, 1    , 1         },
			{20.0f,  5.0f, 0.80f, 0.4598773f},
			{20.0f, 10.0f, 0.60f, 0.2146816f},
			{20.0f, 10.0f, 0.80f, 0.9507365f},
			{20.0f, 20.0f, 0.50f, 0.5000000f},
			{20.0f, 20.0f, 0.60f, 0.8979414f},
			{30.0f, 10.0f, 0.70f, 0.2241297f},
			{30.0f, 10.0f, 0.80f, 0.7586405f},
			{40.0f, 20.0f, 0    , 0         },
			{40.0f, 20.0f, 0.70f, 0.7001783f},
			{40.0f, 20.0f, 1    , 1         }
		};

	/**Tests the incomplete Beta Function	 */
	public static void testBetaI() {
		L.n("Testing the incomplete BetaI Function():");
		L.n(VectorString.FORMAT("a", -8) +
			VectorString.FORMAT("b", -8) +
			VectorString.FORMAT("x", -8) +
			VectorString.FORMAT("Expected", -22) +
			VectorString.FORMAT("BetaI(a,b,x)", -22));
		for (int i = BetaI.ValuesBetaI.length; --i >= 0;) {
			final float[] xyPair = BetaI.ValuesBetaI[i];
			final double result = BetaI.BETA_I(xyPair[2], xyPair[0], xyPair[1]); 
			L.n(ByRefDouble.FORMAT(xyPair[0] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[1] , - 8, 2) +
				ByRefDouble.FORMAT(xyPair[2] , - 8, 7) +
				ByRefDouble.FORMAT(xyPair[3] , -22, 7) +
				ByRefDouble.FORMAT(result, -22, 7));
			Assert.EQUALS(xyPair[3], (float) result);
		}
		L.readString(); 
	}

	/**Tests all Methods of this Class	 */
	private static final void testBetaI2() throws Exception {
		final ABodyDouble a = new BodyDouble();
		final ABodyDouble b = new BodyDouble();
		final ABodyDouble x = new BodyDouble();
		L.n("Testing the incomplete BetaI Function() with MetricBody Instances:");
		L.n(VectorString.FORMAT("a", 8) +
			VectorString.FORMAT("b", 8) +
			VectorString.FORMAT("x", 8) +
			VectorString.FORMAT("Expected", 22) +
			VectorString.FORMAT("BetaI(a,b,x)", 22));
		for (int i = BetaI.ValuesBetaI.length; --i >= 0; ) {
			final float[] xyPair = BetaI.ValuesBetaI[i];
			final Object result = BETA_I(x, a, b); 
			L.n(ByRefDouble.FORMAT(a.value = xyPair[0], - 8, 2) +
				ByRefDouble.FORMAT(b.value = xyPair[1], - 8, 2) +
				ByRefDouble.FORMAT(x.value = xyPair[2], - 8, 7) +
				ByRefDouble.FORMAT(          xyPair[3], -22, 7) +
				ByRefDouble.FORMAT(((ABodyDouble)result).value, -22, 7));
			Assert.EQUALS(result, new Double(xyPair[3]));
		}
		L.readString();
	}

	/** tests all Methods of this Class 	 */
	public static void testIt() throws Exception {
		testBetaI();
		testBetaI2();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(); }
	
}
