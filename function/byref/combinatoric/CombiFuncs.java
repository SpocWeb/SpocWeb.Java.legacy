package function.byref.combinatoric;

import streamIO.AStreamOut;
import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.monoid.integer.Permutation;
import streamIO.copy.shift.SwapAble;
import function.ICountAble;
import function.IMeasurAble;
import function.byref.ByRefByte;
import function.byref.ByRefDouble;
import function.byref.ByRefLong;

/**Performant Implementation of the combinatoric Functions.
 * They are also implemented in an abstract Way in AIntegrityRing,
 * AMetricIRing and as Function Classes in RingFuncs. 
 * 
 * Permutation[] perms = Permutation.Permutations(5);
 * 
 * These Implementations are even faster, if done in Assembler,
 * which has been done for Variationen, Kombinationen, pBinCum, pHypCum, pPoissonCum,
 * pLorentz, pLorentzCum, pNormLtz, pNormLtzCum, pNormalCum, Bessel,
 * pKvSv, pKvSvCum, pNormExp, pNormExpCum, pExpCum, pExp
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 6bc34ff54e0266c7f4baa454fd115f765c933b761e2562ecefde9385d53ea2ca
 * stale: false
 * tags: [code/combinatorics, code/special_function]
 * concepts: [Combinatorics]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
final public class CombiFuncs {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	private static final Log L = new Log(CombiFuncs.class, 0);
	
	///////////////////////////////////////////////////////////////////////////
	
	/**Calculates the Variation(n,k) = n!/(n-k)! = n(n-1)...(n-k+1)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITH considering Sequence and 
	 * WITHOUT returning the Elements into the Bin.
	 * @see Permutation#Variations(int, int) to actually generate all Variations 
	 */
	final static public long Variation(final int n, final byte k) {
		return VariCombi(n, k, (ByRefLong) null);}
	
	/**Calculates the Variation(n,k) = n!/(n-k)! = n(n-1)...(n-k+1)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITH considering Sequence and 
	 * WITHOUT returning the Elements (for large and Fractional n).	 
	 */
	final static public double Variation(final double n, final byte k) {
		return VariCombi(n, k, null);}
	
	/**Returns the Variation(n,k) and the Factorial of k,
	 * which enables the Calculation of the Combination(n,k).
	 *
	 * Recursive Calculation of the Combination kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1 for the Factorial
	 * and calculates the Variation and the Factorial of k.
	 * The Combination can be calculated by dividing the Variation
	 * by the Factorial of k.
	 * With VariCombi both the Variation Var (this, k) =
	 * and the Combination Comb (this, k) = Var(this, k)/k!
	 * can be calculated.
	 * "this" may be real, but k has to be integer.
	 * Otherwise you have to use the Gamma Function with Gamma(n+1) = n!. 
	 */
	final static public long VariCombi(final int n, byte k, final ByRefLong fact) {
		final boolean calcFact = (fact != null);
		if (k == 0) { if (calcFact) fact.Value = 1; return 1; }
		long prod;
		long vari = prod = n;
		if (calcFact) fact.Value = k;
		while (--k > 0) { //positive()) {
			prod *= --vari;					//multiplication with 1 unnecessary
			if (calcFact) fact.Value *= k;	//multiplication with 1 unnecessary also!
		}
		return prod; }

	/**Returns the Variation(n,k) and the Factorial of k,
	 * which enables the Calculation of the Combination(n,k).
	 * (for large and Fractional n)
	 *
	 * Recursive Calculation of the Combination kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1 for the Factorial
	 * and calculates the Variation and the Factorial of k.
	 * The Combination can be calculated by dividing the Variation
	 * by the Factorial of k.
	 * With VariCombi both the Variation Var (this, k) =
	 * and the Combination Comb (this, k) = Var(this, k)/k!
	 * can be calculated.
	 * "this" may be real, but k has to be integer.
	 * Otherwise you have to use the Gamma Function with Gamma(n+1) = n!. 	 */
	final static public double VariCombi(final double n, byte k, final ByRefDouble faculty) {
		if (k == 0) {
			if (faculty != null) faculty.Value = ICountAble.ONE; 
			return ICountAble.ONE; }
		double prod;
		double vari = prod = n;
		if (faculty != null) faculty.Value = k;
		while (--k > 0) { //positive()) {
			prod *= --vari;					//multiplication with 1 unnecessary
			if (faculty != null) faculty.Value *= k;	//multiplication with 1 unnecessary also!
		}
		return prod; }

	/**Calculates the Combination(n,k) = n!/(k!*(n-k)!)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITHOUT considering the Sequence and WITHOUT returning the Elements.
	 *
	 * Thus it represents the Number of possible k-Subsets of an n-Set
	 *
	 * These Numbers appear as Binomial (Polynomial) Coefficients
	 * when factoring out (a+b)^n = Sum(k=0..n, Comb(n,k)*a^k*b^(n-k))
	 * It is also used with real n for calculating the Power Series
	 * of "small" Disturbances: (1+-x)^n = 1 +- nx + ... + Comb(n,k)(+-x)^k
	 *
	 * This Calculation is optimized only in MetricIRing,
	 * because Comb(n, k) == Comb (n, n-k).
	 * The only Problem is that for large n and k
	 * the Division takes place after the Calculation of Vari(n, k),
	 * which may result in an Overflow.	 
	 * 
	 * @see Permutation#Combinations(SwapAble, int) to actually generate all Combinations 
	 */
	final static public long Combination(final byte n, final byte k) {
		final ByRefLong Fact = new ByRefLong();
		if (n < k + k) //with long Numbers the Division had to be singled out.
			return VariCombi(n, (byte) (n - k), Fact)/Fact.Value;
			return VariCombi(n,				k , Fact)/Fact.Value; } 	//"else" is unnecessary!

	/**Calculates the Combination(n,k) = n!/(k!*(n-k)!)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITHOUT considering the Sequence and WITHOUT returning the Elements.
	 *
	 * Thus it represents the Number of possible k-Subsets of an n-Set
	 *
	 * These Numbers appear as Binomial (Polynomial) Coefficients
	 * when factoring out (a+b)^n = Sum(k=0..n, Comb(n,k)*a^k*b^(n-k))
	 * It is also used with real n for calculating the Power Series
	 * of "small" Disturbances: (1+-x)^n = 1 +- nx + ... + Comb(n,k)(+-x)^k
	 *
	 * This Calculation is optimized only in MetricIRing,
	 * because Comb(n, k) == Comb (n, n-k).
	 * The only Problem is that for large n and k
	 * the Division takes place after the Calculation of Vari(n, k),
	 * which may result in an Overflow.	 
	 */
	final static public double Combination(final double n, final byte k) {
		final ByRefDouble Fact = new ByRefDouble();
		if ((n < k + k) && (n == Math.floor(n))) //with long Numbers the Division had to be singled out.
			return VariCombi(n, (byte) (n - k), Fact)/Fact.Value;
			return VariCombi(n,				k,  Fact)/Fact.Value;	//"else" is unnecessary!
	}

	/**Returns the Number of Possibilities to draw all n of n Elements
	 * and putting them into m Subsets of Size K[i]
	 * WITHOUT considering Sequence and 
	 * WITHOUT returning the Elements.
	 * I.e. n = Sum(i = 0..m, K[i]) and PermW (n,K) = n! / (K1!K2!...Km!)
	 *
	 * These Numbers appear as Polynomial (Binomial) Coefficients
	 * when factoring out
	 * (b1+b2+..+bm)^n = Sum((K1+K2+...+Km)==n, Comb(n,K)*b1^K1*b2^K2*...*bm^Km
	 *
	 * This makes Comb(n,k) == PermW({k, n-k}) only a special Case.	 
	 */
	final static public double PermW (final byte[] k) {
		byte sum = 0;
		double prod = ICountAble.ONE;
		for(int i = k.length; --i >= 0;) {
			prod *= Fact (k[i]);
			sum += k[i];
		}
		return Fact(sum)/prod; }

	/**Calculates the Variation with Repetition of Items(n,k) = n^k
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITH considering the Sequence and 
	 * WITH returning the Elements.	 
	 */
	final static public long VarRep	(final byte n, final byte k) {
		return ByRefByte.POW(n, k); }

	/**Calculates the Variation with Repetition of Items(n,k) = n^k
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITH considering the Sequence and 
	 * WITH returning the Elements.	 
	 * @see to 
	 */
	final static public double VarRep(final int n, final int k) {
		return ByRefDouble.POW(n, k); }

	/**Calculates the Number of Combinations with Repetition of Items(n,k)
	 * == Combination without Repetition of Items(n+k-1,k)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITHOUT considering the Sequence and 
	 * WITH returning the Elements. 
	 * @see streamIO.vector.CombinationsRepeating#CombRep(byte, byte) 
	 * generates the actual Combinations with repeating Values 
	 * or as a continuous Stream. 
	 */
	final static public long CombRep(final byte n, final byte k) {
		return Combination ((byte) (n+k-1), k); }
	
	/**Calculates the Number of Combinations with Repetition of Items(n,k)
	 * == Combination without Repetition of Items(n+k-1,k)
	 * This is the number of Samples with Size k from a Set of n Elements,
	 * WITHOUT considering the Sequence and WITH returning the Elements.
	 * (for large and Fractional n)	
	 */
	final static public double CombRep(final int n, final byte k) {
		return Combination (n+k-1, k); }
	
	/**Calculates the Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n! = Fact(n) = n * Fact(n-1); Fact(0) = 1;
	 *
	 * Because the Factorial can also be defined for Fractions,
	 * this is not yet defined in long.
	 *
	 * For real Numbers use the Gamma Function: Gamma (n+1) = n!
	 * This Funtion cannot be calculated this way,
	 * so the Arguments is limited to byte Numbers.	 
	 */
	final static public long Fact(byte arg) {
		if (arg <= 1 ) return 1;	//check for the special Case
		if (arg >  20) throw new AbstractMethodError("Overflow with arg="+arg);	// 21! > 2^63
		long prod = arg;
		while ((--arg) > 1)	//grtr(one))
			prod *= arg;
		return prod; }

	/**Calculates the Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n! = Fact(n) = n * Fact(n-1); Fact(0) = 1;
	 *
	 * Because the Factorial can also be defined for Fractions,
	 * this is not yet defined in long.
	 *
	 * For real Numbers use the Gamma Function: Gamma (n+1) = n!
	 * This Funtion cannot be calculated this way,
	 * so the Arguments is limited to int Numbers.	 */
	final static public double Fact(int arg) {
		if (arg <= 1) return 1;	//check for the special Case
		double prod = arg;
		while ((--arg) > 1)	//grtr(one))
			prod *= arg;
		return prod; }

	/**Calculates the Double Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	final static public long dblFact(byte arg) {
		if (arg == 0) return 1;	//check for the special Case
		long prod = arg;
		while ((arg -= 2) > 1)	//grtr(one))
			prod *= arg;	//The Test in this Loop is optimized in absMetricIRing!
		return prod; }

	/**Calculates the Double Factorial of this integer number in Place.
	 * The Definition is recursive:
	 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
	final static public double dblFact(int arg) {
		if (arg == 0) return 1;	//check for the special Case
		double prod = arg;
		while ((arg -= 2) > 1)	//grtr(one))
			prod *= arg;	//The Test in this Loop is optimized in absMetricIRing!
		return prod; }

	/**Calculation of fact using VariCombi
	 * Recursive Calculation of Factorial kills the Stack.
	 * Iterative Solution, gives 0! = 0, 1! = 1	 */
/*	final static public long fact() {
		return VariCombi((MetricIRing) this.copy(), null);}
*/
	//alternative Implementations:
/*		long Prod = (long)copy();
		long Fact = (long)copy();
		while (((MetricIRing)Fact.dec()).positive()) Prod.mulAt(Fact);	//multiplication with 1 unnecessary
		return Prod; }

	return   (long)mul(	//Recursive Solution
			((long)pred()).fact()); }
*/

	/**Recursive Calculation of Double Factorial kills the Stack.
	 * Iterative Solution, gives 0!! = 0, 1!! = 1	 */
/*	final static public long dblFact() {
		long Prod = (long)copy();
		long Fact = (long)copy();
		while (((MetricIRing)Fact.dec().dec()).positive()) Prod.mulAt(Fact);	//multiplication with 1 unnecessary
		return Prod; }

	//	return (long)mul(	//Recursive Solution
	//		  ((long)pred()).dblFact()); }
*/
	
	/**Returns the Fibonacci Number defined by the linear Recursion:
	 * F[n+2] = F[n+1] + F[n] with the Start Values F[0]:=0 and F[1]:=1.
	 * The Values can be calculated by solving the Matrix Equation
	 * for the EigenValues. The 2nd EigenValue is smaller than 0.5
	 * and is dominated by  the 1st EigenValue.	 */
	final static public double Fibonacci(final int n) {
		return Math.rint(Math.exp(n*IMeasurAble.LNONEGOLDEN)/IMeasurAble.SQRT5); }
	
	////////////////////////////////////////////////////////////////////////////////////
	//  Testing
	////////////////////////////////////////////////////////////////////////////////////
	
	private static void testPermRep() {
		L.n("Testing Permutations with Repetions:");
		final byte[] P1 = {3, 2, 1};
		Assert.EQUALS(60, PermW (P1), AStreamOut.ARRAY_TO_STRING(P1, ", "));
		L.readString();
	}
	
	private static void testPermutation() {
		L.n("Testing Permutations :");
		Assert.EQUALS(1, Fact((byte) 0), "Fact(0)");
		Assert.EQUALS(1, Fact((byte) 1), "Fact(1)");
		Assert.EQUALS(2, Fact((byte) 2), "Fact(2)");
		Assert.EQUALS(6, Fact((byte) 3), "Fact(3)");
		Assert.EQUALS(4.03291461126606e26, Fact(26), "Fact(26)");
	}
	
	private static void testCombRep() {
		byte k = (byte) (4 + (byte) ByRefDouble.RANDOM(5));
		L.n ("Teste Kombinationen with Repetions:");
		for (byte i = 0; i <= k; ++i) {
			final long expected = Fact((byte)(k+i-1)) / (Fact(i)*Fact((byte)(k-1)));
			Assert.EQUALS(expected, CombRep(k,i), "CombRep("+k+","+i+")"); 
		}
		L.readString();
	}
	
	private static void testCombination() {
		byte k = (byte) (10 + (byte) ByRefDouble.RANDOM(11));
		L.n ("Testing Combination:");
		for(byte i = 0; i <= k; ++i) {
			final long expected = Fact(k) / (Fact((byte) (k-i))*Fact(i));
			Assert.EQUALS(expected, Combination(k,i), "Comb("+k+","+i+")"); 
		}
		L.readString();
	}
	
	private static void testVarRep() {
		byte k = (byte) (5 + (byte) ByRefDouble.RANDOM(6));
		L.n ("Testing Variation with Repetions:");
		for (byte i = 0; i <= k; ++i) {
			final long expected = (long) Math.pow(k,i);
			Assert.EQUALS(expected, VarRep(k,i), "VarRep("+k+","+i+")"); 
		}
		L.readString();
	}
	
	private static void testVariation() {
		final byte k = (byte) (10 + (byte) ByRefDouble.RANDOM(11));
		L.n("Testing Variation:");
		for (byte i = 0; i <= k; ++i) {
			final long expected = Fact(k) / Fact((byte) (k-i));
			Assert.EQUALS(expected, Variation(k,i), "Var("+k+","+i+")"); 
		}
		L.readString();
	}
	
	/**Tests the Geometric Distribution and it's Integral	 */
	public static void testFibonacci() {
		L.n("Teste Fibonacci (Generator fuer die Fibonacci-Zahlen) :");
		int i = 0;
		int j = 0, tmp;
		for (int Z1 = -2; Z1 <= 20; ++Z1) {
			if (Z1 == 1) i = 1;
			else {tmp = i; i = j; j = tmp; i += j;}
			Assert.EQUALS(i, Fibonacci (Z1), "Fib("+Z1+")");
		}
		L.readString();
	}
	
	/**Tests all Methods of this Class	 */
	public static void testIt() {
		testFibonacci();
		testVariation();
		testVarRep();
		testCombination();
		testCombRep();
		testPermutation();
		testPermRep();
		//final Permutation[] perms = Permutation.Permutations(5);
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) {
		testIt(); }
	
}
