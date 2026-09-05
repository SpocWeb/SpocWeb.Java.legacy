package function.byref.combinatoric;

import streamIO.Assert;
import streamIO.Log;

/**Calculates and stores the double Factorial for each number: n!
 * The Caching ensures a fast access to previously used Faculties.
 * Design Decisions: Using double instead of Long, because the range is larger.
 * short	:  8
 * integer	: 12
 * long		: 20, because n!! ~ n^n
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:18Z
 * digest: be3b47561a8f89eb26fc2d3f72be9106090d1b951972687d8093641fb80e11a2
 * stale: false
 * tags: [code/combinatorics, code/special_function]
 * concepts: [Combinatorics]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * n!! = dblFact(n) = n * dblFact(n-2); Fact(0) = Fact(1) = 1;	 */
public class DblFactorial {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(0);

	/**Cache of previously calculated Numbers	 */
	private static double[] Values = {1, 1};

	/**Maximum Number used yet	 */
	private static int maxValues = Values.length-1;

	/**Publicly accessible Bernoulli Number Function.
	 * Call this once with the highest argument needed
	 * to speed up calculation.	 */
	public static Double Value (int n) {
		return new Double(value(n));}

	/**Publicly accessible Factorial Function.
	 * Call this once with the highest argument needed
	 * to speed up calculation.	 */
	public static double value(int n) {
		if (n <= maxValues) return Values[n];	//Fast return
		//Resizing the Cache Array
		double[] tmp = new double[n+1];
		System.arraycopy(Values, 0, tmp, 0, maxValues+1); Values = tmp;
		//Loop for Calculation
//		double oldValue;// = Values[maxValues-1];
//		double newValue = Values[maxValues  ];
		while (++maxValues <= n)
			Values[maxValues] = Values[maxValues-2] * maxValues;
		return Values[--maxValues];
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(); }
	
	/**Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		L.n("Teste den Algorithmus zur Erzeugung der Doppel-Fakult�ten :");
		Assert.EQUALS(1, value(0), "0!!");
		Assert.EQUALS(1, value(1), "!!");
		Assert.EQUALS(2, value(2), "2!!");
		Assert.EQUALS(3, value(3), "3!!");
		Assert.EQUALS(8, value(4), "4!!");
		Assert.EQUALS(15, value(5), "5!!");
		Assert.EQUALS(5.10117543936e13, value(26), "26!!");
		L.readString(); 
	}

}
