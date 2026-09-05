package streamIO.copy.group.ring.metric.body.complex;

import streamIO.Log;
import streamIO.copy.group.IGroup;
import streamIO.copy.group.ring.metric.AMetricIRing;
import streamIO.copy.group.ring.metric.IMetricIRing;
import streamIO.copy.group.ring.metric.body.BodyDouble;
import streamIO.copy.group.ring.metric.body.MetricBody;
import streamIO.copy.groupM.IGroupM;
import function.IMeasurAble;
import function.byref.ByRefLong;
import function.byref.TestByRef;

/**This Class contains some Integral Functions
 * that appear in Fourier Transformations:
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:18:15Z
 * digest: c3b07a4d58320f8f0a2c27883e02c71ac77e831e0879c4344533e79bdcb8a3dd
 * stale: false
 * tags: [code/complex_numbers, code/fourier_transform]
 * concepts: [Complex Number Arithmetic and Fourier Transform]
 * facets: {layer: domain, status: legacy, complexity: high}
 * -->
 * e.g. e^x/x and e^x/SqRt(x)*/
public class FourierFuncs {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(FourierFuncs.class);
	
	////////////////////////////////////////////////////////////////////////////
	
	/**Complete elliptic Integral
	 *					infin                             Pi/2
	 * cEIn (k,p,a,b) = |       (a+b*x^2) dx              |        (a*cos^2(t)+b*sin^2(t)) dt
	 * 					|------------------------------ = |-------------------------------------------
	 * 					0 (1+px^2)\/((1+x^2)(1+k^2*x^2))  0 (cos^2 t+p*sin^2 t)\/(cos^2 t+k^2*sin^2 t)
	 *
	 * cEIn (k,p=1,a,b) = EIn (y=infin,k,a,b)	 */
	public static MetricBody cElliptIn (MetricBody k, MetricBody p, MetricBody a, MetricBody b) {
		if (k.isZero()) return null;	//no elliptic Integral for k == 0
		MetricBody f;
		MetricBody g;
		MetricBody Faktor;
		MetricBody Quadrat = (MetricBody) k.AbsV();
		MetricBody fa = (MetricBody) Quadrat.copy();
		MetricBody sx = (MetricBody) fa.one();
		if (p.positive()) {p = (MetricBody) p.SqRt(); b = (MetricBody) b.div(p);}
		else {
			f = (MetricBody) Quadrat.sqr();
			Faktor = (MetricBody) f.Resid();
			g = (MetricBody) p.Resid();
			f.subAt(p);
			Faktor.mulAt(b.sub(a.mul(p)));
			p =  (MetricBody)
				((MetricBody) f.div(g)).SqRtAt();
			((IGroupM)a.subAt(b)).divAt(g);
			b = (MetricBody) ((IGroup) Faktor.div(((IGroup) g.sqr().mulAt(p)).addAt(a.mul(p)))).negAt();
		}
		while (true) {
			f = (MetricBody) a.copy();
			a.addAt(b.div(p));
			g = (MetricBody) fa.div(p);
			b.addAt(f.mul(g));
			b.dblAt();
			p.addAt(g);
			g.copyAt(sx);
			sx.addAt(Quadrat);
			MetricBody Accuracy = (MetricBody) g.mulAbsAccuracy();
			if (g.AbsDist(Quadrat).notMoreThan(Accuracy))
				return (MetricBody)
						 ((IGroupM)
						 ((IGroup )a.mulAt(sx)).addAt(b)).divAt
						(((IGroupM)p.addAt(sx)).mulAt(sx)).mulAt(IMeasurAble.PiHalf);
			Quadrat = (MetricBody) fa.SqRt().dblAt();
			fa = (MetricBody) Quadrat.mul(sx);
		}
	}

	/**ellipt. Umkehrfunktionen
	 *  Ist u = F1 (y,k),dann ist sn (u,k) = sin x , cn^2 + sn^2 = 1 , k^2*sn^2 + dn^2 = 1
	 *  fuer k = 0 ist sn = sin , cn = cos und dn = 1.
	 *  Zusammenhang mit Legendre-elliptischen Integralen :
	 *
	 *				x								x    ________________
	 *   F1 (k,x) = |        dt           F2 (k,x) =| \/(1-k^2*Sin^2 t) dt
	 *				| -----------------				|
	 *				0  \/(1-k^2*Sin^2 t)			0
	 *            = EIn (y,j,1,1)					= EIn (y,j,1,j^2)
	 *
	 *			mit y = tan x und k^2 + j^2 = 1	 */
	public static void ElliptFn (MetricBody u, MetricBody k2, MetricBody sn, MetricBody cn, MetricBody dn) {
		final int MaxGrad = 13;	//caching the Values
		MetricBody[] em = new MetricBody[MaxGrad+1];
		MetricBody[] en = new MetricBody[MaxGrad+1];
		if (k2.isZero()) {
			sn = u.TanH();
			((MetricBody)
			((MetricBody)
			((MetricBody)
			cn.copyAt(sn)).sqrAt()).ResidAt()).SqRtAt(); //{schneller als Neuberechnung von Eins/CosH (u);}
			dn.copyAt(cn);
		} else {
			boolean B_Hilf;
			MetricBody Summe = null;
			if (B_Hilf = k2.negative()) {
				Summe = (MetricBody) k2.Resid();
				((MetricBody) k2.divAt(Summe)).negAt();
				Summe.SqRtAt();
				u.mulAt(Summe);
			}
			MetricBody Faktor = null;
			MetricBody Hilf	= (MetricBody) u.one();
			int Z1 = 0; while (++Z1 <= MaxGrad) {
//				int Z3 = Z1;
				em [Z1] = Hilf;
				en [Z1] = (MetricBody) k2.SqRt();
				Faktor = (MetricBody)
						((MetricBody) Hilf.add(k2)).halfAt();
				if (Hilf.AbsDist(k2).notMoreThan(Hilf.mulAbsAccuracy())) break;
				k2.mulAt(Hilf);
				Hilf = Faktor;
			}
			u.mulAt(Faktor);
			cn = u.Cos_Sin(sn); dn.oneAt();
			if (! sn.isZero()) {
				Z1++;
				Hilf = (MetricBody) cn.div(sn);
				Faktor.mulAt(Hilf);
				while (--Z1 >= 1) {
					MetricBody Skalar = em [Z1];
					Hilf.mulAt(Faktor);
					Faktor.mulAt(dn);
					dn = (MetricBody)
						((MetricBody) en [Z1].addAt(Hilf)).divAt(Hilf.addAt(Skalar));
					Hilf = (MetricBody) Faktor.divAt(Skalar);
				}
				cn =  (MetricBody)
					 ((MetricBody)
					 ((MetricBody) Faktor.sqr()).inc()).SqRtAt().invAt();
				if (sn.negative()) cn.negAt(); sn = cn;
				cn =  (MetricBody) Faktor.mul(sn);
			}
			if (B_Hilf) {dn.swap(cn); sn.divAt(Summe);}
		}
	}

	/**The complex Fresnel Function is defined by:
	 * F (x)= C (x)+i*S (x) = Int[0,x] (e^it)/SqRt(t)
	 *		= Int[0,x] Cos(t)/SqRt(t) + i Sin(t)/SqRt(t)	 */
	public static Complex Fresnel   (MetricBody arg) {
		Complex Result;
		boolean bolNeg;
		if (bolNeg = arg.negative()) arg.negAt();
//		if (x < FastNull) return zero();
//		else
		if (((IMeasurAble) arg).getDouble() < 2.0) { 
		   	Result = FresnelPR(arg); //Beide Potenzreihen gleichzeitig ausfuehren
		} else {
		   	Result = FresnelKB(arg);  //Kettenbruchentwicklung auswerten
		}
		if (bolNeg) Result.negAt();
		return Result; }

	/**The complex Fresnel Function is calculated by a Potency Sequence.
	 * The Argument must be positive.	 */
	private static Complex FresnelPR   (MetricBody x) {
		Complex S = new Complex(x);
		IMetricIRing Accuracy= (IMetricIRing) x.mul(AMetricIRing.BaseAccuracy);	//speeds up testing
		IMetricIRing Faktor	= (IMetricIRing) x.piHalf().mulAt(x.sqr());	//*x2
		IMetricIRing Summe	= (IMetricIRing) x.zero();
		IMetricIRing Term	= (IMetricIRing) x.copy();
		ByRefLong n = new ByRefLong(1);
		ByRefLong k = new ByRefLong(0);
		boolean Sign= true;
		boolean odd	= true;
		do {
			n.Value+=2; k.Value++;
			Term.divAt(k).mulAt(Faktor);
			if (Sign)	Summe. addAt(Term.div(n));
			else 		Summe.subAt(Term.div(n));
			if (odd){S.Imag = Summe; Summe = S.Real; Sign = !Sign;}
			else	{S.Real = Summe; Summe = S.Imag;}
			odd = !odd;
		} while (Accuracy.isLessThan(Term));	//Since Summe is of Order 1
		return S; }

	/**The complex Fresnel Function is calculated by a continued Fraction	 */
	private static Complex FresnelKB   (MetricBody x) {
		MetricBody Faktor = (MetricBody) x.piHalf().mulAt(x.sqr());
		Complex S = new Complex(x, ((MetricBody) x.newInstance()).zeroAt());
		S.Real = (IMetricIRing)Faktor.Cos_Sin(S.Imag);
		MetricBody PiX2 = (MetricBody)Faktor.dbl();
		MetricBody my4 = (MetricBody)x.four();
		Complex b = new Complex(x.one(), PiX2.neg());
		Complex c = new Complex(x.Infinity(), x.zero());
		Complex d = (Complex) b.inv();
		Complex h = (Complex) d.copy();
		Complex del;
		long n = 1;
		ByRefLong a = new ByRefLong(0);
		do{
			a.Value = -(n++)*(n++);
			b.Real.addAt(my4);
			((IGroupM)((IGroup)d.mulAt(a)).addAt(b)).invAt();
			c.invAt(); ((IGroup) c.mulAt(a)).addAt(b);
			h.mulAt(del = (Complex)c.mul(d));
		}while (((Complex)del.pred()).AbsV().isMoreThan(AMetricIRing.BaseAccuracy));
		h.mulAt(new Complex(x,x.neg()));
		return (Complex) new Complex(x.OneHalf(), x.OneHalf()).mulAt(((IGroup)b.one()).subAt(S.mulAt(h))); }

	/**Tests the Fresnel Function	 */
	public static void testFresnel() {
		final double [][] fresnelValues = {{ 0.1, 0.0999975, 0.0005236},
						   { 1.0, 0.7798934, 0.4382591},
						   { 1.5, 0.4452612, 0.6975050},
						   { 2.0, 0.4882534, 0.3434157},
						   { 5.0, 0.5636312, 0.4991914},
						   {20.0, 0.4999873, 0.4840845}};
		L.n("Testing Fresnel()");
		for (int i = fresnelValues.length; --i >= 0; ) {
			final MetricBody x = new BodyDouble(fresnelValues[i][0]);
			final MetricBody y = Fresnel(x);
			L.n("x = " + x + " Expected: (" + fresnelValues[i][1] + ";" + fresnelValues[i][2] + ") actual: " + y);
			//Assert.EQUALS(y, new Double(fresnelValues[i][2]));
		}
	}

	/**The complex CI_SI Function is calculated by a Power Series
	 * This Calculation is optimized for positive real Arguments less than 2
	 * See Numerical Recipes 2nd Ed. p257 (6.9.8)	 */
	private static Complex Ci_Si_PR (MetricBody arg) {
/*		if (arg.less(AMetricIRing.MaxAccuracy))
			//not necessary, because this special Case is contained below!
			return new Complex(arg.ln().addAt(new BodyDouble(TDouble.C)), arg);
*/		Complex S = new Complex(arg.zero());
		boolean Sign = true;
		boolean odd  = true;
		IMetricIRing Summe  = (IMetricIRing) arg.zero();
		MetricBody  Faktor = (MetricBody)  arg.one();
		MetricBody Term;
		ByRefLong k = new ByRefLong(0);
		do {
			k.Value++;
			Faktor.divAt(k).mulAt(arg);
			Term = (MetricBody) Faktor.div(k);
			if (Sign)	Summe.addAt (Term);
			else		Summe.subAt(Term);
			if (odd){S.Imag = Summe; Summe = S.Real; Sign = !Sign;}
			else	{S.Real = Summe; Summe = S.Imag;};
			odd = !odd;
		} while (Term.isMoreThan(AMetricIRing.BaseAccuracy));	//Since Summe is of Order 1
		S.Real.addAt(arg.ln().addAt(IMeasurAble.EulerC)); //Start mit diesem S fuehrt zum zu fruehen Abbruch und zu Rundungsfehlern}
		return S; }

	/**The complex Ci_Si Function is calculated by a continued Fraction
	 * This Calculation is optimized for positive real Arguments.
	 * The Number of Iterations must not exceed 20,
	 * because then Rounding Errors falsify the Result.
	 * Using modified Lentz's Method to evaluate continued Fraction.
	 * see Numerical Recipes 2nd Ed. p257 (6.9.9)	 */
	private static Complex Ci_Si_KB (MetricBody arg) {
		Complex S = new Complex();	//Create complex Number with Length 1 and Phase -arg
		S.Imag = (IMetricIRing) arg.newInstance();
		S.Real = (IMetricIRing) arg.Cos_Sin (S.Imag);
		S.Imag.negAt();
		MetricBody my2 = (MetricBody)arg.two();
		Complex	b = new Complex(arg.one(), arg.copy());
		Complex c = new Complex(arg.Infinity());
		Complex d = (Complex) b.inv();
		Complex h = (Complex) d.copy();
		Complex del;
		long i = 1;	ByRefLong a = new ByRefLong();
		do{
			a.Value = -i*(i++);
			b.Real.addAt(my2);
			((IGroupM)((IGroup)d.mulAt(a)).addAt(b)).invAt(); //d = 1/(b+a*d);c = b+a/c)	//Denominators cannot be Zero!
			c.invAt(); ((IGroup) c.mulAt(a)).addAt(b);	//invAt() returns a real, if possible
			h = (Complex) (del = (Complex) c.mul(d)).mul(h);
		}while (((Complex)del.pred()).AbsV().isMoreThan(AMetricIRing.BaseAccuracy));
		S.mulAt(h); S.Real.negAt();
		S.Imag.addAt(arg.piHalf());
		return S; }

	/**The complex Ci and Si Functions are defined by:
	 * F (x) =Ci(x)+i*Si(x) = Int[0,x] (e^it)/t = Int[0,x] (Cos(t)/t + i Sin(t)/t)
	 * This Calculation is optimized for positive real Arguments.
	 * For negative Arguments it is SI(-x)=-SI(x) and CI(-x)=CI(x)-i*Pi
	 * The Residuum i*Pi is not supplied, because it interferes with SI.
	 * See Numerical Recipes 2nd Ed. p 257 (6.9.8)	 */
	public static Complex Ci_Si (MetricBody arg) {
		Complex Result;
		boolean negative;	//create positive Values! Speeds up convergence Check too!
		if (arg.isZero())	return new Complex (arg.NegInfinity(), arg.zero());
		if (negative = arg.negative()) arg = (MetricBody) arg.neg();	//arg.AbsVAt();
		if (((IMeasurAble) arg).getDouble() >= 5.0)	//Threshold Value here dependent on wanted Accuracy (2 to 5).
			 Result = Ci_Si_KB(arg); //Kettenbruchentwicklung auswerten}
		else Result = Ci_Si_PR(arg); //Beide Potenzreihen gleichzeitig ausfuehren
		if (negative) Result.cjgAt();
		return Result; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests the complex CI and SI function	 */
	public static void testCi_Si() {
		L.n("Testing Ci, Si and Ci_Si");
		L.n("x=		Ci=			Si=			(Ci, Si)=		Ci=		Si=		Ei=		Ei=");
		float[] test;
		int i = -1;
		while (++i < TestByRef.ValuesCISIEI.length) {
			test = TestByRef.ValuesCISIEI[i];
			MetricBody x = new BodyDouble(test[0]);
//			MetricBody y = new BodyDouble();
			L.n(x + "	" + test[1] + "	" + test[2] + "	" + Ci_Si(x));
			// + "	" + Ci(x) + "	" + Si(x) + "	" + test[3] + "	" + Ei(x));
		}
	}

	/**Tests {@link #cElliptIn(MetricBody, MetricBody, MetricBody, MetricBody)} against a table
	 * of precomputed reference values, printing each case's expected and actual result.	 */
	public static void testCElliptIn() {
		double [][] test= {{1.075603, 0.579379, 1.431272, 2.983978,  4.55056913061},
						   {0.460754, 0.355149, 1.543104, 9.871250, 30.84605257102},
						   {0.315245, 0.485749, 8.064943, 4.150859, 21.49979234587},
						   {0.925200, 0.244658, 2.196268, 7.535595, 19.44413726990},
						   {1.018858, 0.553585, 6.522579, 8.966315, 16.53835314103},
						   {0.469345, 0.840512, 6.190532, 0.078919,  5.99582237027},
						   {0.716745, 0.605616, 9.185391, 1.944222, 11.65121543918},
						   {0.397413, 0.672441, 4.658608, 2.393465,  9.55308115675},
						   {0.977675, 0.178762, 0.393995, 3.089121,  8.66094338382},
						   {0.468789, 0.889865, 0.193939, 7.576698, 11.08105026306},
						   {0.827667, 1.090596, 1.645965, 9.076693,  8.98231312703},
						   {1.021117, 0.966230, 6.491470, 3.648387,  8.00908510433},
						   {0.606774, 0.351217, 7.447604, 8.347799, 28.80554470080},
						   {0.884011, 0.984586, 5.142964, 6.061129,  9.45224028021},
						   {0.342587, 0.856615, 7.217640, 7.278925, 20.01617029493},
						   {0.539958, 0.703571, 9.140016, 4.107049, 15.41131981924},
						   {0.425536, 0.472920, 4.963917, 1.963402, 10.47014604487},
						   {0.548949, 0.694340, 5.349351, 5.909150, 14.55850985184},
						   {1.013868, 0.124546, 0.714079, 3.769850, 13.07960302502},
						   {0.549021, 0.374118, 2.017796, 6.456034, 18.48696658106}};
		L.n("Testing cElliptIn()");
		L.n("k =		p=		a=		b=		cEl=	R=");
		for (int i = test.length; --i >= 0;) {
			final MetricBody k = new BodyDouble(test[i][0]);
			final MetricBody p = new BodyDouble(test[i][1]);
			final MetricBody a = new BodyDouble(test[i][2]);
			final MetricBody b = new BodyDouble(test[i][3]);
			final MetricBody R = new BodyDouble(test[i][4]);
			L.n(k + "	" + p + "	" + a + "	" + b + "	" + R + "	" + cElliptIn(k, p, a, b));
		}
	}

	/**Placeholder self-test for the elliptic function; not yet implemented.	 */
	public static void testElliptFn() { //TODO: implement the Test
	}

	/**Tests the Methods of this Class	 */
	public static void testIt() {
		testFresnel();
		testCi_Si();
		testElliptFn();
		testCElliptIn();
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
