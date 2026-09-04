package streamIO.real.random;

import streamIO.IReSetAble;
import streamIO.integer.random.ARandomInt;
import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.real.FilterInMul;
import streamIO.real.IStreamIn_Float;
import function.ICountAble;
import function.byref.combinatoric.ProbFuncs;
import function.derive.ring.body.GammaLn;

/** Returns random integer Numbers distributed in a Poisson fashion
  * i.e. p(k) = EW^k/(k!*exp(EW)) = ProbFuncs.pPoisson()
  *
  * This is the Probability of k Poisson Events happening
  * within a Time Interval EW given the (fractional) Average Event Rate EW.
  * This is related to the Gamma Distribution, that returns the waiting Time
  * for the k-th Poisson Event.
  * k takes only integer Values, so the Poisson Distribution
  * consists of Delta Peaks at the Integer Numbers,
  * still the Rejection Method can be used, by spreading the Peaks.
  *
  * For EW > 12 the Distribution is Bell shaped (with Steps),
  * so you can use the Lorentz Distribution for Rejection,
  * For small EW, random Exponential Waiting Times are generated,
  * until the given Time EW is exceeded.
  */
public class RandomPoisson
extends ARandomInt {

	/**Mean Value of this Poisson Distribution
	 * also for Transformation of the Lorentz Distribution	 */
	protected double EW;

	/**Variance Factor for Transformation of the Lorentz Distribution	 */
	protected double sq;

	/** Local Reference to the Random Number Generator, returning [0,1)	 */
	protected IStreamIn_Float ran;

	/** Local Reference to the Lorentz Random Number Generator	 */
	protected IStreamIn_Float ranLorentz;

	/**Flag, indicates whether the Number is to be found by Iteration
	 * or analytically from the Rejection Method.	 */
	protected boolean smallXM;

	/**Rejection Value for Lorentz Distribution (no negative Values!)	 */
	protected double minLorentz;

	/**Scaling Factor	 */
	protected double g;

	/**Logarithm of the Mean Value	 */
	protected double LnXm;

	/**Public Method for other Classes to determine the maximum Value	 */
//	public double getMaxValue() { return Long.MAX_VALUE; }

	/**Constructor that takes a Random Number Generator	 */
	protected RandomPoisson() { super(Integer.MAX_VALUE); }

	/** Constructor that takes a uniform Random Number Generator
	  * and the Mean Value of Events happening within a Time Unit	 */
	public RandomPoisson(final IStreamIn_Bound_Int ran, final double EW) {
		super(Integer.MAX_VALUE);
		this.ran = new FilterInMul(ran, 1);
		this.EW = EW;
		if (smallXM = (EW < 12.0)) {
			g = Math.exp(-EW);
		} else {
			ranLorentz = new RandomLorentz(ran);
			sq = Math.sqrt(EW+EW);
			minLorentz = -EW/sq;
			LnXm = Math.log(EW);	//entspricht der Fakultaet
			g = EW * LnXm - GammaLn.GAMMA_LN(EW + ICountAble.ONE);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { //throws IOException {
		//try{ 
			this.ran.reSet();
			this.ranLorentz.reSet(); //TODO: 
		/*} catch (final IOException x) {
			throw new RuntimeException(x); 
		}*/
		return this; 
	}

	/**New Semantic: instead of returning to the indicated Position, 
	 * this Method sets the Seed Value.   
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public void reset(final int _seed) { //throws IOException {
		/*try{ 
			this.ran.reset(_seed);
			this.ranLorentz.reset(_seed); //TODO: 
		} catch (IOException x) {
			throw new RuntimeException(x); 
		}*/
	}

	/** Random single Precision Number	 */
	protected long nextLongInternal() { return nextIntInternal();}

	/** Random double Precision Number
	  * Poisson distributed random deviates (7.3)	 */
	protected int nextIntInternal() {
		int em;
		double y, fa;
		if (smallXM) {	//instead of adding up exponential Deviates,
			em = 0; //multiply uniform Deviates!
			if ((y = ran.nextFloat()) > g)
				do ++em; while ((y *= ran.nextFloat()) > g);
		} else {
			do {
				while ((y = ranLorentz.nextDouble()) < minLorentz);	//reject, if negative
				em = (int) (sq*y+EW); //Affine Transformation to Lorentz and Reduction to Integer Results
				fa = em*LnXm-GammaLn.GAMMA_LN(em + 1)-g;	//for large Numbers the Values for fa should be cached!
			} while (ran.nextFloat() > 0.9*(1.0+y*y)*Math.exp(fa));	//reject, if
		} return em; }	//Abstand der Vergleichsfunktion (Lorentz) zur gewuenschten Funktion : 0.9 damit der Wert nie 1 ueberschreitet


	/**Tests the Poisson Random Number Generator	 */
	final static public void testIt(IStreamIn_Bound_Int ran, float EW, int NPTS, int numBins) {
		final float[] compare = new float[numBins];
		for (int i = numBins; --i >= 0;) { 
			compare[i] = (float) ProbFuncs.pPoisson(EW, (byte) i); }
		TestRandom.TEST_RANDOM(new RandomPoisson(ran, EW), compare, numBins, NPTS, "Poisson(" + EW + ")");
	}
//	    UNTIL (fa > 0.11) OR (EinsK1*Ran <= (1+Sqr(g))*Exp (fa))

/*	public static void main(final String[] args) {
		final int N = 20; 
		final int NPTS = 100000;
		final int ISCAL = 200;
		final int LLEN = 50;

		char txt[LLEN+1];
		long idum=(-13);
		int i,j,k,klim,dist[N+1];
		float xm,dd;

		for (j=0;j<=N;j++) dist[j]=0;
		do {
			printf("Mean of Poisson distribution (0.0<x<%d.0) ",N);
			printf("- Negative to end:\n");
			scanf("%f",&xm);
		} while (xm > N);
		if (xm < 0.0) break;
		for (i=1;i<=NPTS;i++) {
			j=(int) (0.5+poidev(xm,&idum));
			if ((j >= 0) && (j <= N)) ++dist[j];
		}
		
		
		printf("Poisson-distributed deviate, mean %5.2f of %6d points\n",
			xm,NPTS);
		printf("%5s %8s %10s\n","x","p(x)","graph:");
		for (j=0;j<=N;j++) {
			dd=(float) dist[j]/NPTS;
			for (k=0;k<=LLEN;k++) txt[k]=' ';
			klim=(int) (ISCAL*dd);
			if (klim > LLEN) klim=LLEN;
			for (k=1;k<=klim;k++) txt[k]='*';
			txt[LLEN]='\0';
			printf("%6d %8.4f   %s\n",j,dd,txt);
		}
	}
*/
}
