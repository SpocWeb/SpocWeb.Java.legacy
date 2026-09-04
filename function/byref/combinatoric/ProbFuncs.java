package function.byref.combinatoric;

import math.vector.VectorInt;
import math.vector.VectorString;
import streamIO.Assert;
import streamIO.Log;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefDouble;
import function.byref.ByRefInt;
import function.byref.TestByRef;
import function.derive.ring.body.BetaI;
import function.derive.ring.body.GammaLn;
import function.derive.ring.body.GammaP;
import function.derive.ring.body.Gauss;

/**Defines many Functions describing (accumulated) Probabilities
 * Most of these Functions are (unfortunately) also defined in BodyFuncs
 * That was the Reason, why most Constants were defined in IMeasurAble!
 */
public class ProbFuncs {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(ProbFuncs.class, 0);

	/////////////////////////////////////////////////////////////////////////////////////
	//	discrete Probability Distributions	
	/////////////////////////////////////////////////////////////////////////////////////

	/**Returns the Probability of a geometric Distribution,
	 * i.e._the Probability to have to perform n Tests
	 * to succeed exactly in the End:  P (n) = p * (1-p)^(n-1)
	 * with constant Probability p for Success in each Test.
	 * This is the special Case of the Binomial Distribution with n, k = 1
	 * and without the Binomial Factor which would allow Success
	 * at any Position of the n Tests.	 */
	final static public double pGeo     (int n, double p) {
		if (n < 1) return ICountAble.ZERO;
		return	 p * ByRefDouble.POW((ICountAble.ONE-p), n-1); }

	/**Returns the accumulated geometric Distribution,
	 * i.e._the Probability to have to perform at most n Tests
	 * to succeed exactly in the End:  P (n) = 1 - p^n	 */
	final static public double pGeoCum  (final int n, final double p) {
		return ICountAble.ONE-ByRefDouble.POW(p, n);}

	/**Returns the Probability of a Binomial Distribution with
	 * n : Number of independent Tries
	 * p : Probability for a Hit
	 * k : Number of Hits
	 * pBin(n,p,k) = Combination (n,k) * p^k * q^(n-k)	 */
	final static public double pBin     (final int n, final byte k, final double p) {
		return CombiFuncs.Combination (n,k) * ByRefDouble.POW(p,k) * ByRefDouble.POW(ICountAble.ONE-p,n-k);}

	/**Returns the accumulated Binomial Distribution for small k and n,
	 * i.e._the Probability to draw at most k Items with n Draws
	 *
	 * pBinCum (n,p,k) = Sum(i=0..k, pBin (n,p,i))
	 *
	 * There is also an analytic Approximation via Integral for this Sum
	 * that holds for large n and k: pBinCum,	it pays off for n > 12	 */
	final static public double pBinCum  (final int n, int k, double p) {
		double g;
		final boolean small_k;
		if (small_k = (k+k <= n))			  
			g = ICountAble.ONE-	    p ;	//instead of another call to this function
		else {
			if (k == n) return ICountAble.ONE; 
			p = ICountAble.ONE-(g = p); k = n-k-1;
		}	//pBinCum (n, 1-p,n-k-1)
		p *= (n+1)/g;
		double factor = ByRefDouble.POW(g, n);   /*fuer Rekursionsformel*/
		double sum = factor; g = ICountAble.ONE-ICountAble.ONE/g;
		int i = 0; while (++i <= k) {
			factor *= (g+p/i);
			sum += factor;
		}
		if (small_k)	
			return		          sum;
			return ICountAble.ONE-sum; }

	/**Analytic Approximation for the accumulated Binomial Distribution
	 * using an Integral instead of the Sum that holds for large n and k:	 */
	public double pBinCum (final double n, final double k, final double p) {
		return ICountAble.ONE-BetaI.BETA_I (p,k+ICountAble.ONE,n-k);}  /*(k+1) statt k wegen Integral-Anfang*/

	/**Hypergeometric Distribution:
	 * Returns the Probability of drawing k Hits in a Sample of n Items
	 * drawn from N Items containing K Hits.	 */
	final static public double pHyp    (final int N, final int K, final byte n, final byte k) {
		return  CombiFuncs.Combination (  K,         k) *
				CombiFuncs.Combination (N-K,(byte)(n-k)) /
				CombiFuncs.Combination (N  ,       n  ); }

	/**Returns the accumulated Probability to draw at most k Hits
	 * in n Draws with a hypergeometric Distribution,
	 * i.e. from N Items containing K Hits.	 */
	final static public double pHypCum (byte N, byte K, byte n, byte k) {
		boolean large_k;
		if (large_k = (k+k > n)) {         /*statt eines weiteren Aufrufs*/
			if (n == k) return ICountAble.ONE; 
			K = (byte)(N-K); k = (byte)(n-k-1); }/* = pBinCum (n,ICountAble.ONE-p,Pred (n-k))*/
		final int M_L		= N-K;
		final int M_L_n	= M_L-n;
		int Z2;
		double Faktor;
		if (M_L_n < 0) {
				Z2 = -M_L_n;
			int Z3 = n-Z2;
			if (k >= Z2) Faktor =	CombiFuncs.Combination (  K, (byte) Z2) *
			  						CombiFuncs.Combination (M_L, (byte) Z3) /
			  						CombiFuncs.Combination (N  , n );
			else Faktor = 0.0;
			K -= Z2; n = (byte) Z3;
		} else {
			Z2 = 0; Faktor = CombiFuncs.Variation (N-K, n) /
							 CombiFuncs.Variation (N  , n);} /*n! kuerzt sich weg, Variation reicht*/
		double Sum = Faktor; ++K; ++n;
		while (++Z2 <= k) {
			Faktor *= ((double) --K)*--n/(Z2*(M_L_n + Z2));
			Sum += Faktor;
		}
		if (large_k) return  ICountAble.ONE - Sum;
					return					Sum; }
	
	/**Returns the Probability for k Hits
	 * when only the Expectation Value n*p is known.
	 * (Limit of the Binomial Distribution for n->Infinity).
	 * p(k) = EW^k/(k!*exp(EW))
	 * A Poisson Distribution is generated by RandomPoisson.
	 * This is the Probability of k Poisson Events happening
	 * within a Time Interval T given the (Fractional) Average Event Rate EW.
	 * This is related to the Gamma Distribution, that returns the waiting Time
	 * for the k-th Poisson Event. 	 */
	final static public double pPoisson(final double EW, final byte k) {
		return Math.exp(k*Math.log(EW) - EW)/CombiFuncs.Fact(k);}
	
	/**Returns the accumulated Probability for at most k Hits
	 * when only the Expectation Value EW = n*p is known.
	 * (Limit of the Binomial Distribution for n->Infinity).
	 * This discrete Sum is very accurate for small k.	 */
	final static public double pPoissonCum(final double EW, int k) {
		double factor;
		double sum = factor = Math.exp(-EW);	//Probability for no Event
		while (--k > 0) {
			factor *= EW/k;
			sum += factor;
		}
		return sum; }
	
	/**Returns the accumulated Probability for 0 up to k-1 Hits
	 * when only the Expectation Value EW = n*p is known.
	 * (see Numerical Recipes 2nd Ed. p.221 (6.2.15)
	 * (Limit of the Binomial Distribution for n->Infinity).
	 * This is using an Integral instead of the Sum
	 * and should be used for large k.	 */
	final static public double pPoissonCum  (final double EW, final double k) {
		ByRefDouble GammaLn = new ByRefDouble();
		return 1.0 - GammaP.GAMMA_P(EW, k, GammaLn); }
	
	/**Gamma Probability:
	 * This is the Poisson Probability for continuous k-1 = a
	 */
	final static public double pGamma       (final double x, final double a) {
		return Math.exp((a-ICountAble.ONE) * Math.log(x) - x - GammaLn.GAMMA_LN(a));}
	
	/**Beta Probability:
	 * This is the Continuation for the Probability to get
	 * k = a-1 Hits and l = b-1 Misses in a Sample of Size n = k+l
	 */
	final static public double pBeta        (final double x, final double a, final double b) {
		return Math.exp((a-ICountAble.ONE) * Math.log(              x) +
						(b-ICountAble.ONE) * Math.log(ICountAble.ONE-x) -
					 GammaP.BetaLn(a,b));}
	
	/**Returns the X^2 Probability:
	 * 				n-1  -x
	 * p (X^2,N) = x   *e  / (2*Gamma (n))
	 *
	 * with	x = X^2/2	the Half Width of the observed Distribution
	 * and	n = N/2		the Half Sample Size
	 */
	final static public double pChiSqr(double cs, double ny) {
		cs *= IMeasurAble.HALF;
		ny *= IMeasurAble.HALF;
		return Math.exp((ny-ICountAble.ONE) * Math.log(cs) - cs - IMeasurAble.LN2 - GammaLn.GAMMA_LN(ny)); }
	
	/**Returns the Probability for the accumulated X^2 Statistics:
	 *               n |y[i]-y(x[i])|2
	 *         X^2:=Sum|------------|
	 *              i=1|     V[i]   |
	 * i.e: the Probability that the X^2 with N (real, i.e.(M-1) with Samples M)
	 * Degrees of Freedom indicates Similarity of Sample and Model Distribution.
	 * For a fixed Value x[i] = x and y(x[i]) = y, the Mean, as well as
	 * V[i] = V is the Variance of y.
	 */
	final static public double pChiSqrKum(final double cs, final double ny) {
		return 1-GammaP.PROBABILITY_CHI_SQR(ny, cs); }
	
	/**Returns the accumulated Probability that an observed ChiSqr
	 * is less than the given ChiSqr
	 * when n is the (integer) Number of Degrees of Freedom.
	 * (see Numerical Recipes 2nd Ed. p.221 (6.2.18).	 */
	final static public double pChiSqrCum  (final double chiSqr, final double n) {
		return GammaP.PROBABILITY_CHI_SQR(n, chiSqr); }
	
	/**Returns Student's t Distribution with n = N/2:
	 *
	 * 1/p (t,N) = SqRt(N)*B(u,1/2) * (1+t^2/N)^(N+^1/2)
	 *
	 */
	final static public double pStudent(final double t, final double ny) {
		final double f = IMeasurAble.HALF*(ny+ICountAble.ONE);	//Pot2MulI (f,-1);
		final double g = IMeasurAble.HALF* ny;	//Pot2MulI (g,-1);
		return	Math.exp(GammaLn.GAMMA_LN(f) - GammaLn.GAMMA_LN(g) - f * Math.log(ICountAble.ONE+ByRefDouble.SQR(t)/ny))
			  / Math.sqrt(IMeasurAble.PI*ny); }
	
	/**Returns the symmetric accumulated Student's t Distribution with n = N/2 i.e:
	 * the Probability, that the bilateral Deviation t from T is only random.
	 * (Hypothesis: both Distributions are the same)
	 */
	final static public double pStudentSym    (final double t, final double ny) {
		return BetaI.BETA_I (ny/(ny+ByRefDouble.SQR(t)), ny * IMeasurAble.HALF ,IMeasurAble.HALF);}
	
	/**Returns the accumulated Student's t Distribution with n = N/2 i.e:
	 * the Probability of two Samples having the normed Difference of Means
	 * t:=(M1-M2)/V with the (common) Variance V with Ny Degrees of Freedom
	 * are in fact derived from the same Distribution.
	 * (Hypothesis: M1 = M2, V1 = V2 = V).
	 */
	final static public double pStudentKum    (final double t, final double ny) {
		double f = pStudentSym (t,ny)*IMeasurAble.HALF;
		if (t < 0)	return ICountAble.ONE-f;
					return f; }
	
	/**Returns the Fisher f Distribution:
	 * 					  n1  n2		 n1
	 * p(v, N1, N2) = 2 N1  N2			f
	 *				  ---------- * --------------
	 *				  Beta(n1,n2)  (f*N1+N2)^n1+n2
	 * with n? = N?/2
	 */
	final static public double pFisher     (final double v, final double ny1, final double ny2) {
//		double fa = ny1*v;
		double f  = ny1*IMeasurAble.HALF;	//Pot2MulI (Ny1,-1);
		double g  = ny2*IMeasurAble.HALF;	//Pot2MulI (Ny2,-1);
		return Math.exp( f*(Math.log(v  )  +
							Math.log(ny1)) +
						 g* Math.log(ny2)  -
						(f + g)
							*Math.log(ny1*v+ny2) -
						GammaP.BetaLn(f,g)); }
	
	/**Returns the cumulated Fisher F Distribution i.e:
	 * the Probability that the Ratio of the Variances is F = Var1 / Var2
	 * when Var1 < Var2 ist (Null-Hypothese).	 */
	final static public double pFisherFKum (final double V, double ny1, double ny2) {
		ny1*=IMeasurAble.HALF;
		ny2*=IMeasurAble.HALF;
		return BetaI.BETA_I (ny2/(ny2+ny1*V),ny2,ny1); }
	
	/**Returns the Fisher z Distribution for f = e^2z <=> z = Ln (SqRt(f)) i.e:
	 */
	final static public double pFisherF    (final double v, final double ny1, final double ny2) {
		return pFisher (v,ny1,ny2)/v; }
	
	/**Returns the Fisher Z Distribution i.e:
	 * the Distribution of the Ratio of the Variances of two Distributions
	 * z = Ln (SqRt(f)) = Ln (V1) - Ln (V2).
	 */
	final static public double pFisherZ    (final double z, final double ny1, final double ny2) {
		double tmp = pFisher (Math.exp(z+z),ny1,ny2);
		return tmp+tmp; }	//Pot2MulAt(,1);
	
	/**Returns the cumulated Fisher Z Distribution	 */
	public double pFisherZKum (final double z, final double ny1, final double ny2) {
		return pFisherFKum (Math.exp(z+z),ny1,ny2); }
	
	//////////////////////////////////////////////
	//	continuous Probability Distributions	//
	//////////////////////////////////////////////

	/**Returns the normed x for Probability Calculation of Distributions
	 * with Mean m and 'Width'(Variance) s:	x'=(x-m)/s
	 * p	(x)	= p		(x')/s
	 * pCum	(x)	= pCum	(x')	 */
	final static public double pNorm (final double x, final double m, final double s) { 
		return (x-m)/s; }

	/**Returns the Probability of the Lorentz Distribution
	 * with Mean 0 and 'Width'(Variance) 1:	p(x) = 1/(IMeasurAble.PI(1+x^2))	 */
	final static public double pLorentz   (final double x) {
		return ICountAble.ONE/(IMeasurAble.PI*(ICountAble.ONE+ByRefDouble.SQR(x))); }

	/**Returns the accumulated Probability of the normed Lorentz Distribution
	 * with Mean 0 and 'Width'(Variance) 1:	p(x) = 1/(IMeasurAble.PI(1+x^2))	 */
	final static public double pLorentzCum(final double x) {
		return Math.atan(x)/IMeasurAble.PI+IMeasurAble.HALF; }

	/**Calculates the Kolmogorov-Smirnov Distribution as Power Series:
	 *				 Infin   j+1  -2(j*x)^2     +Infin   j  -2(j*x)^2
	 * pKvSv(x) = 2 * Sum  (-)   e       = 1 -  Sum   (-)  e
	 *				 j = 1                    j=-Infin
	 * 
	 * converges very badly at x == 0
	 * since exp(-2(j*x)^2) = exp((-2*x^2)*(j^2)) = u^(j^2)
	 * mit u = exp(-2*x^2), also: 
	 * u^1-u^4+u^9-u^16+u^25
	 */
	final static public double pKvSv(final double x) {
		if (x == ICountAble.ZERO) { 
			return ICountAble.ZERO; } 
		final double xSqr = x*x;
		final double exp  = Math.exp(-(xSqr+xSqr));
		final double sqrExp = exp*exp;
		double sum = exp;
		double expj2 = exp; //exp(j*j)
		double exp2j = sqrExp; //exp(j+j)
		for (int j = 1; ++j < ByRefInt.MAX_ITER; ) { //MaxIter sollte < 256 sein,wegen Quadrierung von j
			expj2 *= -exp2j*exp; //
			final double gjj = expj2*(j*j);
			sum += gjj;
			if (Math.abs(gjj) <= sum*ByRefDouble.DoubleAccuracy) { //sum ist stets positiv
				return x*sum*8; }
			exp2j *= sqrExp; //j^2+2jx+1 
		}
		return ICountAble.ZERO; }

	/**Calculates the Kolmogorov-Smirnov Statistics as Power Series i.e:
	 * the Probability for the maximum Deviation D in the cumulated Distributions
	 * being x = D*SqRt(N), when both Distributions are the same.
	 * (Hypothesis : Sample is taken from this Distribution)
	 * 
	 *				 Infin   j+1  -2(j*x)^2     +Infin   j  -2(j*x)^2
	 * pKvSv(x) = 2 * Sum  (-)   e       = 1 -  Sum   (-)  e
	 *				 j = 1                    j=-Infin
	 *
	 * converges very badly at x == 0
	 */
	final static public double pKvSvCum(final double x) {
		if (x == ICountAble.ZERO) {
			return ICountAble.ONE; } 
		final double xSqr = x*x;
		final double exp  = Math.exp(-(xSqr+xSqr));
		final double sqrExp = exp*exp;
		double sum = exp;
		double expj2 = exp;
		double exp2j = sqrExp;
		for (int j = 0; ++j < ByRefInt.MAX_ITER;) { //CountDown sollte < 256 sein,wegen Quadrierung von j
			expj2 *= -exp2j*exp;
			sum += expj2;
			if (Math.abs(expj2) <= sum*ByRefDouble.DoubleAccuracy) {    //f ist stets positiv
				return sum+sum; }
			exp2j *= sqrExp;
		}
		return ICountAble.ONE;  } //too close to 0 to converge

	/*   Faktor :=Exp (-Pot2Mul (Sqr (x),1));
		 Quadrat:=Sqr (Faktor);
		 f:=Faktor;g:=Faktor;fa:=Quadrat;
		 FOR j:=2 TO CountDown DO {CountDown sollte < 256 sein,wegen Quadrierung}
		  BEGIN
		   g:=-fa*g*Faktor;Skalar:=g*Sqr (j);
		   f:=f+Skalar;
		   IF ABS (Skalar) < Pot2Mul (f,Genauigkeit)    {f ist stets positiv}
			THEN BEGIN Pot2MulI (f,3);pKvSv:=f*x;Exit END
			ELSE fa:=fa*Quadrat
		  END;

	/**Returns the Value of the normed Exponential-Distribution
	 * with Mean m and Standard-Deviation m :	p(x) = 1 - exp(-x/m)	 */
	final static public double pExpKum(final double x, final double m) {
		return 1 - Math.exp(-x/m); }

	/**Returns the Value of the normed accumulated Exponential-Distribution
	 * with Mean m and Standard-Deviation m :	p(x) = exp(-x/m)/m	 */
	final static public double pExp(final double x, final double m)	{ return Math.exp(-x/m)/m; }

	/**Returns the Value of the normed accumulated Exponential-Distribution
	 * with Mean 1 and Standard-Deviation 1 :	p(x) = 1 - exp(-x)	 */
	final static public double pExpKum(final double x) { return 1 - Math.exp(-x); }

	/**Returns the Value of the normed Exponential-Distribution
	 * with Mean 1 and Standard-Deviation 1 :	p(x) = exp(-x)	 */
	final static public double pExp(final double x) { return Math.exp(-x); }

	/////////////////////////////////////////////////////////////////////////////////////

	/**Returns the incomplete Zeta Function, for n > 1 defined by:
	 * Zeta (x) :=	Sum(n = 1..Infin, 1/n^x)
	 */
	final static public double Zeta(double x) {
		int Z1;
		double f, df;
		if (x > ICountAble.THREE)    /*willkuerliche Grenze zwischen Potenz-Reihe und Summation*/
		{	//plain Summation, fast Convergence of Sum
			Z1 = 2;
			f = 1.0; x = -x;	//negative Powers
			do f += (df = Math.pow(Z1++, x));
			while (df > ByRefDouble.DoubleAccuracy); //f hier stets > 1, df stets positiv
			return f; }
		double g = 1-x;
		if (g > 0) {
			f = Math.sin(x*IMeasurAble.PI_HALF)*Math.pow(IMeasurAble.TWO_PI,-g)*GammaLn.Gamma(g)*Zeta(g);
			return f+f; }
		f = -1/g + IMeasurAble.EULER_C;
		double Hilf = g;
		Z1 = 0;
		while (++Z1 < IMeasurAble.CoeffZeta.length) {
			f += (df = IMeasurAble.CoeffZeta[Z1]*Hilf); Hilf *= g/Z1;
			if (Math.abs(df) <= Math.abs(f)*ByRefDouble.DoubleAccuracy) {
				return f; } 
		}
		throw new AbstractMethodError(); }

	/**Returns the incomplete Zeta Function, for n > 0 and -1 <= x <= 1
	 * defined by:
	 *				Infin	x^k
	 * ZetaI (n,x):= Sum	---
	 *				 k=1	k^n
	 *
	 * for n > 1 it is: lim (x -> 1)  ZetaI (n,x) = Zeta (n)
	 */
	final static public double ZetaI    (final double n, final double x) {
		int Z1 = 2;
		double df, f, g = f = x;
		double sx = -x;
		do {
			g *= x;
			f += (df = g*Math.pow(Z1++,sx));
		}while (Math.abs(f-df) > f*ByRefDouble.DoubleAccuracy); /*f hier stets > 1*/
		return f; }

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests the Error Function by a fast Chebyshev Approximation	 */
	public static void testErFC() {
		L.n("Testing Error Function Gamma():");
		L.n(VectorString.FORMAT("x", -5) +
			VectorString.FORMAT("Expected", -12) +
			VectorString.FORMAT("pGaussCum(x)", -12) +
			VectorString.FORMAT("pGaussCum(x)", -12));
		for (int i = TestByRef.ValuesErrFc.length; --i >= 0;) {
			final float[] xyPair = TestByRef.ValuesErrFc[i];
			final float expected = (xyPair[1]+1)/2;
			final float resultDouble = (float) Gauss.pGaussCum(xyPair[0]*IMeasurAble.SQRT2);
			final float result = Gauss.pGaussCum(xyPair[0]);
			L.n(ByRefDouble.FORMAT( xyPair[0], -6, 2) +
				ByRefDouble.FORMAT(expected, -12, 7) +
				ByRefDouble.FORMAT(resultDouble, -12, 7) +
				ByRefDouble.FORMAT(result, -12, 7));
			Assert.EQUALS(expected, resultDouble);
			Assert.EQUALS(expected, result);
		}
		L.readString(); 
	}

	/**Tests the Poisson Distribution and it's Integral	 */
	public static void testPoissonCum() throws java.io.IOException {

//		final int nPts  = 20;
//		final int iScal = 50;

//		double aLam, aVal, EW, Summe;
//		int j,k;
//		Txt : PACKED ARRAY [1..iScal] OF Char;
//		double x1,x2;

		L.n ("Teste Cumulierte Poisson-Wahrscheinlichkeiten :");
		L.n ("Beide Werte sollten etwa uebereinstimmen !");
		L.n ("1.Wert :  exakte Summe   2.Wert :  Integral");
		for (int i = 1; i <= 20; ++i) {
/*			k = i;
			x1 = pPoissonCum  (10, k);
			x2 = pPoissonCumS (10, k);
			System.out.print (AOrderAble.format(i ,  -2) + "  1.Wert : " +
				   AOrderAble.format(x1, -15) + "  2.Wert : " +
				   AOrderAble.format(x2, -15) + "  Abw% : ");
			if (x2 > 0) L.n (AOrderAble.format((x1/x2-1)/Prozent, -10, 8) + "%");
			else L.n();
			L.readString(); 
			L.n ("  1.Wert : " + AOrderAble.format(pPoissonCum (Random, 1000), -15) +
								"  2.Wert : " + AOrderAble.format(pPoissonCumS(Random, 1000), -15));
			L.readString(); 
			L.n ("Teste Poisson-Wahrscheinlichkeiten :");
			L.n ("Bitte geben Sie den Erwartungswert an (0 ..",nPts,") :");
			do System.in.read(EW); while ((EW < 0) || (EW > nPts));
			L.n ("Wahrscheinlichkeits-Funktion fuer Poisson-Verteilung");
			L.n();
			L.n(AOrderAble.format("k", -2) +
				AOrderAble.format("Wert : ", -10) +
				AOrderAble.format("Graph :", -13));
			for (i = 0; i <= nPts; ++i) {
				aVal = pPoisson (EW,i);	//plotting the Function
				for (j = 1; j <= iScal; ++j) {
					if (j <= Round (iScal*aVal))
						 Txt [j] = "*";
					else Txt [j] = " ";
				}
				L.n(AOrderAble.format(i, -2) +
					AOrderAble.format(aVal, -10, 6) +
					AOrderAble.format("", 5) + Txt);
			}
		}
		L.readString(); 
		L.n ("Wahrscheinlichkeits-Funktion fuer kumulierte Poisson-Verteilung");
		L.n();
		L.n(AOrderAble.format("k", 2) +
			AOrderAble.format("Wert : ", 10) +
			AOrderAble.format("Soll : ", 10) +
			AOrderAble.format("Graph :", 13));
		for (i = 0; i <= nPts; ++i) {
			aVal = pPoissonCum (EW,i);
			for (j = 1; j <= iScal; ++j) {
				if (j <= Round (iScal*aVal))
					 Txt [j] = "*";
				else Txt [j] = " ";
			}
			Summe = 0;
			for (j = 0; j <= i; ++j) Summe = Summe+pPoisson (EW,j);
			L.n(AOrderAble.format(i, -2) +
				AOrderAble.format(aVal , -10, 6) +
				AOrderAble.format(Summe, -10, 6) +
				AOrderAble.format("", 5) + Txt);
*/		}
		L.readString(); 
	}

	/**Tests the Hypergeometric Distribution and it's Integral	 */
	public static void testHypCum() throws java.io.IOException {
		final byte nPts  = 20;
		final byte iScal = 50;
		final byte numItems = 40;

		double aVal, Summe;
//		double x1, x2;

		L.n ("Teste Cumulierte Hypergeometrische Wahrscheinlichkeiten :");
		double EW = 0.3;
		final byte numTreffer = (byte) Math.round((float) (EW*numItems));
//		L.n ("Bitte geben Sie den Anteil der richtigen 'Kugeln' an :");
//		do System.in.read(EW); while ((EW < 0) || (EW > 1));
		L.n("kumulierte Wahrscheinlichkeits-Funktion fuer Hypergeometrische Verteilung ");
		L.n("mit " + numItems + " Kugeln,davon " + numTreffer + " Treffer, von denen " + nPts + " gezogen werden. ");
		L.n(VectorString.FORMAT("k", 2) +
			VectorString.FORMAT("Wert  :", 10) +
			VectorString.FORMAT("Soll  :", 10) +
			VectorString.FORMAT("Graph :", 13));
		Summe = 0;
		for (byte i = 0; i <= nPts; ++i) {
//			double tmp;
			Summe += pHyp   (numItems, numTreffer, nPts, i);
			aVal =   pHypCum(numItems, numTreffer, nPts, i);
			String Txt = "";
			for (int j = 1; j <= iScal; ++j) {
				if (j <= Math.round(iScal*aVal)) 
					Txt +=  "*";
			}
			L.n(VectorInt.FORMAT(i, -2) +
				ByRefDouble.FORMAT(aVal , -10, 6) +
				ByRefDouble.FORMAT(Summe, -10, 6) +
				VectorString.FORMAT("", 5) + Txt);
		}
		L.readString(); 
		L.n ("Teste Hypergeometrische Wahrscheinlichkeiten :");
		L.n ("Wahrscheinlichkeits-Funktion fuer Hypergeometrische Verteilung");
		L.n();
		L.n(VectorString.FORMAT("k", 2) +
			VectorString.FORMAT("Wert  :", 10) +
			VectorString.FORMAT("Graph :", 13));
		for (byte i = 0; i <= nPts; ++i) {
			aVal = pHyp (numItems, Math.round((float) (EW*numItems)),nPts,i);
			String Txt = "";
			for (int j = 1; j <= iScal; ++j) 
				if (j <= Math.round((float) (iScal*aVal))) 
					Txt +=  "*";
			L.n(VectorInt.FORMAT(i, -2) +
				ByRefDouble.FORMAT(aVal, -10, 6) +
				VectorString.FORMAT("", 5) + Txt);
		}
		L.readString(); 
	}

	/**Tests the Binomial Distribution and it's Integral	 */
	public static void testBinCum() throws java.io.IOException {
		final int nPts  = 20;
		final int iScal = 50;

		double aVal, Summe;
		double x1, x2 = 0;

		L.n ("Teste Cumulierte Binomial-Wahrscheinlichkeiten :");
		L.n ("Beide Werte sollten etwa uebereinstimmen !");
		L.n ("   1.Wert :  exakte Summe    2.Wert :      Integral       Abweichung in %");
		for (int i = 1; i <= 20; ++i) {
			int k = 4*i;
			x1 = pBinCum  (100, k, IMeasurAble.HALF);
//			x2 = pBinCumS (100, k, IMeasurAble.HALF);
			System.out.print(	VectorInt.FORMAT(i , - 2   ) + "  1.Wert : " +
								ByRefDouble.FORMAT(x1, -15, 8) + "  2.Wert : " +
								ByRefDouble.FORMAT(x2, -15, 8) + "  Abw% : ");
			if (x2 > 0) L.n(ByRefDouble.FORMAT((x1/x2-1)/IMeasurAble.PERCENT, -10, 8));
			else		L.n();
		}
		L.n ("   1.Wert : " + ByRefDouble.FORMAT(pBinCum(100, 100, Math.random()), -15) +
							"   2.Wert : ");// + AOrderAble.format(pBinCumS (100, 100, Math.random()), -15));
		L.readString(); 
		L.n ("Teste Binomial-Wahrscheinlichkeiten :");
		double EW = 6;
//		L.n ("Bitte geben Sie den Erwartungswert an (0 .. " + nPts + ") :");
//		do System.in.read (EW); while ((EW < 0) || (EW > nPts));
		L.n ("Wahrscheinlichkeits-Funktion fuer Binomial-Verteilung");
		L.n();
		L.n(VectorString.FORMAT("k", 2) +
			VectorString.FORMAT("Wert  :", 10) +
			VectorString.FORMAT("Graph :", 13));
		for (byte i = 0; i <= nPts; ++i) {
			String Txt = "";
			aVal = pBin (nPts, i, EW/nPts);
			for (int j = 1; j <= iScal; ++j) 
				if (j <= Math.round(iScal*aVal)) 
					Txt += "*";
			L.n(VectorInt.FORMAT(i, -2) +
				ByRefDouble.FORMAT(aVal, -10, 6) +
				VectorString.FORMAT("", 5) + Txt);
		}
		L.readString(); 
		L.n ("Wahrscheinlichkeits-Funktion fuer kumulierte Binomial-Verteilung");
		L.n();
		L.n(VectorString.FORMAT("k", 2) +
			VectorString.FORMAT("Wert  :", 10) +
			VectorString.FORMAT("Soll  :", 10) +
			VectorString.FORMAT("Graph :", 13));
		Summe = 0;
		for (byte i = 0; i <= nPts; ++i) {
			String Txt = "";
			aVal = pBinCum (nPts, i, EW/nPts);
			for (int j = 1; j <= iScal; ++j) 
				if (j <= Math.round(iScal*aVal)) 
					Txt += "*";
			Summe += pBin (nPts, i, EW/nPts);
			L.n(VectorInt.FORMAT(i, -2) +
				ByRefDouble.FORMAT(aVal , -10, 6) +
				ByRefDouble.FORMAT(Summe, -10, 6) +
				VectorString.FORMAT("", 5) + Txt);
		}
		L.readString(); 
	}

	/**Tests the Geometric Distribution and it's Integral	 */
	public static void testGeoCum() throws java.io.IOException {
		final int nPts  = 20;
		final int iScal = 50;

		double aVal, Summe;
		int i, j;
//		double x1, x2;

		L.n ("Teste Cumulierte geometrische Wahrscheinlichkeiten :");
		double p = 0.3;
//		L.n ("Bitte geben Sie die Wahrscheinlichkeit an :");
//		do System.in.read (p); while ((p < 0) || (p > 1));
		L.n ("Wahrscheinlichkeits-Funktion fuer geometrische Verteilung");
		L.n();
		L.n(VectorString.FORMAT("k", 2) +
			VectorString.FORMAT("Wert  :", 10) +
			VectorString.FORMAT("Graph :", 13));
		for (i = 1; i <= nPts; ++i) {
			String Txt = "";
			aVal = pGeo (i, p);
			for (j = 1; j <= iScal; ++j) if (j <= Math.round(iScal*aVal)) Txt += "*";
			L.n(VectorInt.FORMAT(i, -2) +
				ByRefDouble.FORMAT(aVal, -10, 6) +
				VectorString.FORMAT("", 5) + Txt);
		}
		L.readString(); 
		L.n ("Wahrscheinlichkeits-Funktion fuer kumulierte geometrische Verteilung");
		L.n();
		L.n(VectorString.FORMAT("k", 2) +
			VectorString.FORMAT("Wert  :", 10) +
			VectorString.FORMAT("Soll  :", 10) +
			VectorString.FORMAT("Graph :", 13));
		Summe = 0.0;
		for (i = 1; i <= nPts; ++i) {
			String Txt = "";
			aVal = pGeoCum (i,p);
			for (j = 1; j <= iScal; ++j) if (j <= Math.round(iScal*aVal)) Txt += "*";
			Summe += pGeo (i, p);
			L.n(VectorInt.FORMAT(i, -2) +
				ByRefDouble.FORMAT(aVal , -10, 6) +
				ByRefDouble.FORMAT(Summe, -10, 6) +
				VectorString.FORMAT("", 5) + Txt);
		}
		L.readString(); 
	}
	
	/** test Data for checking the Functionality 	*/
	private static final double[][] pKvSvCum 
	= { {0.1,1.0000000020008424},
		{0.2,0.9999999998279857},
		{0.3,0.9999906942096127},
		{0.4,0.9971923267662545},
		{0.5,0.9639452436649004},
		{0.6,0.8642827790506034},
		{0.7,0.7112351950296901},
		{0.8,0.544142411574173},
		{0.9,0.3927307079406545},
		{1.0,0.26999967167735467},
		{1.1,0.17771819260640137},
		{1.2,0.11224966667072496},
		{1.3,0.06809222184476636},
		{1.4,0.03968187953811435},
		{1.5,0.02221796261652509},
		{1.6,0.011952043239196606},
		{1.7,0.006177430634444107},
		{1.8,0.0030676213475796976},
		{1.9,0.001463604837187344}
	};

	/** tests Kolmogorov-Smirnov Distribution 	 */
	public static void testKvSv() throws Exception {
		L.n(); 
		for (int i = pKvSvCum.length; --i >= 0; ) {
			Assert.EQUALS(pKvSvCum[i][1], pKvSvCum(pKvSvCum[i][0])); } 
	}

	/** tests Kolmogorov-Smirnov Distribution 	 */
	public static void testKvSvDiff() throws Exception {
		L.n(); 
		for (int i = pKvSvCum.length; --i >= 1; ) {
			//Assert.EQUALS(
			final double x = (pKvSvCum[i][0]+pKvSvCum[i-1][0])/2;
			final double diff1 = (pKvSvCum[i][1]-pKvSvCum[i-1][1])/(pKvSvCum[i][0]-pKvSvCum[i-1][0]);  
			final double diff2 = pKvSv(x); 
			L.n().l(x).l(diff1).l(diff2);
		} 
	}

	/** tests all Methods of this Class 	 */
	public static void testIt() throws Exception {
		testKvSvDiff();
		testKvSv();
		testErFC();
		testPoissonCum();
		testHypCum();
		testBinCum();
		testGeoCum();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		testIt(); }
	
}
