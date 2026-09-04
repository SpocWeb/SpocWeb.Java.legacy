package streamIO.integer.random;

import streamIO.real.FilterInMul;
import streamIO.real.random.RandomLorentz;
import streamIO.real.random.RandomPoisson;
import streamIO.real.random.TestRandom;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.combinatoric.ProbFuncs;
import function.derive.ring.body.GammaLn;

/** Returns integer random Numbers distributed in a Binomial fashion
  * i.e. p(j) = (n!/j!(n-j)!)*p^j*(1-p)^(n-j)
  * This is the Probability of i Poisson Events happening within
  * a Time Interval T.
  * This is related to the Gamma Distribution, that returns the waiting Time
  * for the i-th Poisson Event.
  * i takes only integer Values, so the Poisson Distribution
  * consists of Delta Peaks at the Integer Numbers,
  * still the Rejection Method can be used, by spreading the Peaks.
  *
  * For T > 12 the Distribution is Bell shaped, so you can use the Lorentz
  * Distribution for Rejection,
  * For small T, random Exponential Waiting Times are generated,
  * until the given Time T is exceeded.
  */
public class RandomBinomial
extends RandomPoisson {

	/**Number of Draws of this Distribution	 */
	protected final int n;

	/**Scaling Factor	 */
	protected double nFact;

	/**Flag, indicates whether the Number is to be found by Iteration
	 * or analytically from the Rejection Method.	 */
	protected final boolean smallN;

	/**Probability of this Distribution	 */
	protected final double p;

	/**Probability of this Distribution	 */
	protected double pLog;

	/**Complement of the Probability for this Distribution	 */
	protected double q;

	/**Complement of the Probability for this Distribution	 */
	protected double qLog;

	/**Flag, indicates whether the Probability should be inversed.	 */
	protected boolean largeP;

	/**Local Reference to the Random Number Generator	 */
	protected RandomPoisson ranPoisson;

	/**Rejection Value for Lorentz Distribution (no negative Values!)	 */
	protected double maxLorentz;

	/**Public Method for other Classes to determine the maximum Value	 */
	public long getMaxValue() { return n; }

	/**Constructor that takes a Random Number Generator	 */
	public RandomBinomial(final IStreamIn_Bound_Int ran, final int N, double p) {
		this.n = N;
		this.p = p;
		this.ran = new FilterInMul(ran, 1.0);
		if (smallN = N < 25) return;
		if (largeP = p > IMeasurAble.HALF) {
			p = ICountAble.ONE-p;	//Die Verteilung bleibt gleich, wenn man gleichzeitig m durch (n-m) ersetzt.
		} else if (smallXM = ((EW = N*p) < ICountAble.ONE)) {	//zu erzeugender Mittelwert
			ranPoisson = new RandomPoisson(ran, EW);	//use Poisson Distribution
		} else {	//use Lorentz Distribution
			ranLorentz = new RandomLorentz(ran);
			q = 1.0-p;
			sq = EW*q;
			sq = Math.sqrt(sq + sq);	//Variance
			minLorentz =   -EW /sq;
			maxLorentz = (n-EW)/sq;
			nFact = GammaLn.GAMMA_LN(n + 1);
			pLog = Math.log(p);
			qLog = Math.log(q);
		}
	}

	/**Random double Precision Number
	 * Poisson distributed random deviates (7.3)	 */
	protected long nextLongInternal() {
		int m;
		double y, fa;
		if (smallN) /*direkte Methode mit bis zu 25 Aufrufen von Ran*/
		{int i = n; m = 0; while (--i >= 0) if (ran.nextFloat() <= p) ++m;}
		else
		if (smallXM) { /*kleiner Erwartungswert => Poissonverteilung benutzen (vergl. Poidev)*/
			m = ranPoisson.nextInt();
		} else {  /*Zurueckweisungsmethode wieder mit Lorentz-Verteilung*/
			do {  /*Funktionen berechnen,falls sich N oder p geaendert haben*/
				do y = ranLorentz.nextDouble(); while  ((y < minLorentz) ||
														(y > maxLorentz) );	//reject, if negative
				m = (int) (sq*y+EW); //Affine Transformation to Lorentz and Reduction to Integer Results
				int n_m = n-m;
				fa = nFact	- GammaLn.GAMMA_LN(1 + m)
							- GammaLn.GAMMA_LN(1 + n_m)
							+ pLog * m
							+ qLog * n_m;	//for large Numbers the Values for fa should be cached!
			} while (ran.nextFloat() > 1.2*sq*(1.0+y*y)*Math.exp(fa));	//reject, if
			/*Verhaeltnis von Vergleich zu P (N,p,1.0-p); zurueck,ungefaehr 1.5 mal pro Aufruf*/
		}
		if (largeP) {
			   return n - m;
		}else{ return m; }

	}

	/**Tests the Binomial Random Number Generator	 */
	final static public void testIt(final IStreamIn_Bound_Int ran, final int N, final float p, final int NPTS, final byte numBins) {
		final float[] compare = new float[numBins];
		for (byte i = numBins; --i >= 0;) { 
			compare[i] = (float) ProbFuncs.pBin(N, i, p); }
		TestRandom.TEST_RANDOM(new RandomBinomial(ran, N, p), compare, numBins, NPTS, "Binomial(" + N + "," + p + ")");
	}

}
