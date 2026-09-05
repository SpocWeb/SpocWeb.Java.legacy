package math.algorithm;

import streamIO.copy.groupM.IGroupM;

/**
 * Determines the optimal Bracketing of Matrices.
 *
 * Bracketing is important to minimize the Number of complex Operations
 * i.e. Multiplication plus Addition.
 * Multiplying a [k,l] Matrix with an [l,m] Matrix costs k*l*m Operations.
 * Multiplying three Matrices A*B*C costs differently based on Bracketing:
 * [a,b]*[b,c]*[c,d] = ([a,b]*[b,c])*[c,d] = [a,b]*([b,c]*[c,d]) = [a,d]
 * the first  Bracketing requires (a*b*c) + (a*c*d) = ac*(b+d)
 * the second Bracketing requires (a*b*d) + (b*c*d) = bd*(a+c)
 * If now c is considerably larger than b,
 * the first Bracketing is considerably more expensive than the second.
 * The Ratio is ac*(b+d)/bd*(a+c) = c/b * a(b+d)/d(a+c)
 *
 * This is an Example of a greedy Algorithm,
 * that tries to find the global Optimum
 * by splitting the Problem up into smaller Problems,
 * determining the local Optimum,
 * and combining it to a global Optimum.
 * Those Multiplications with the highest Dimensions are performed first!
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: 20efe5449b74c915c0965597e47eba4d46e197dc3f846efaf6c8fdf2707e18f3
 * stale: false
 * tags: [code/dynamic_programming]
 * concepts: [Matrix Chain Bracketing]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Bracketing {

	/**Contains the intermediate Results of Cost Calculation
	 * only the upper Diagonal is used.
	 * You have to store the previous Results,
	 * so a 1-dim Array is not sufficient.
	 */
	private long cost[][];

	/** Keeps track of the Decisions on the best Bracketing	 */
	private int[][] best;

	/** Number of Operators	 */
	//private int N;

	/** Initializing Constructor, takes the Vector containing the Dimensions.	 */
	public Bracketing(int[] r) { //Dimensions
		int N = r.length-1;
		int i = r.length;
		best = new int [i][i];
		cost = new long[i][i];
		int j = 0;
		while (++j < N) {	//calculate cost of optimal Multiplication of Mi*Mi+1*...*Mi+j
			i = 0;
			while (++i <= N-j) {	//calculate cost of Multiplication of Mi*Mi+1*...*Mk-1
				int k = i; long min = Long.MAX_VALUE;
				while (++k <= i+j) {	//and Mk*Mk+1*...*Mi+j for all k between i and i+j
					long t = cost[i][k-1] + cost[k][i+j] + r[i-1]*r[k-1]*r[i+j];
					if (t < min) {min = t; best[i][i+j] = k;}
				}
				cost[i][i+j] = min;
			}
		}
	}

	/** Returns the Bracketing as a Description.	 */
	public void order(StringBuffer B, int i, int j) {
		if (i == j) {
			B.append((char)('A' + i - 1));
		} else {
			B.append('(');
			order(B, i, best[i][j]-1);
			order(B,    best[i][j], j);
			B.append(')');
		}
	}

	/** Performs the optimized Multiplication.	 */
	public IGroupM Multiply(IGroupM[] M) { return MulRec(M, 1, M.length); }

//Diese Prozedur loest das Problem der Klammerung auf iterative Weise mit einem Aufwand von O (     Anzahl^3)
	/** Recursively performs the optimized Multiplication.	 */
	private IGroupM MulRec(IGroupM[] M, int i, int j) {
		if (i == j) return M[i-1]; //Aufwand fuer eine einzelne Matrix = 0
		else return (IGroupM)
			MulRec(M, i, best[i][j]-1).mul(
			MulRec(M,    best[i][j], j));
	}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) { //throws java.io.IOException {
	testIt(args); }

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Bracketing.class.getName());
		int[] Dimensions = {4,2,3,1,2,2,3};
		Bracketing B = new Bracketing(Dimensions);
		StringBuffer SB = new StringBuffer();
		B.order(SB, 1, Dimensions.length-1);
		System.out.println(SB);
	}

}
