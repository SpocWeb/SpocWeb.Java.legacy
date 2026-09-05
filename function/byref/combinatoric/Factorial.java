package function.byref.combinatoric;

import streamIO.Assert;
import streamIO.Log;

/**Calculates and stores the Factorial for each number.
 * The Caching ensures a fast access to previously used Faculties.
 * Design Decisions: Using double instead of Long, because the range is larger.
 * short	:  8
 * integer	: 12
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:59:46Z
 * digest: 961dc71d6f19f4205b37911e74d71033dc4cffd7a080d277a2f9459faf5deeba
 * stale: false
 * tags: [code/combinatorics, code/special_function]
 * concepts: [Combinatorics]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 * long		: 20, because n! ~ n^n  */
public class Factorial {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Factorial.class, 0);

	////////////////////////////////////////////////////////////////////////////

	/**Cache of previously calculated Numbers	 */
	private static double[] Values = {1};

	/**Maximum Number used yet	 */
	private static int maxValues = Values.length-1;

	/**Publicly accessible Bernoulli Number Function.
	 * Call this once with the highest argument needed
	 * to speed up calculation.	 */
	public static Double Value (final int n) { return new Double(value(n)); }

	/**Publicly accessible Factorial Function.
	 * Call this once with the highest argument needed
	 * to speed up calculation.	 */
	public static double value(final int n) {
		if (n <= maxValues) return Values[n];	//Fast return
		//Resizing the Cache Array
		double[] tmp = new double[n+1];
		System.arraycopy(Values, 0, tmp, 0, maxValues+1); Values = tmp;
		//Loop for Calculation
		double Value = Values[maxValues];
		while (++maxValues <= n)
			Values[maxValues] = (Value *= maxValues);
		maxValues--; return Value;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**Tests {@link #value(int)} against known Factorial Values.	 */
	public static void testIt() throws Exception {
		L.n("Teste den Algorithmus zur Erzeugung der Fakult�ten :");
		Assert.EQUALS(1, value(0), "0!");
		Assert.EQUALS(1, value(1), "1!");
		Assert.EQUALS(2, value(2), "2!");
		Assert.EQUALS(6, value(3), "3!");
		Assert.EQUALS(24, value(4), "4!");
		Assert.EQUALS(4.03291461126606e26, value(26), "26!");
		Assert.EQUALS(26, value(26)/value(25), "26!/25!");
		L.readString(); 
	}

	/**The main entry point for the application.
	 * Prints out the Factorial of the Value passed via the Command Line, 
	 * otherwise performs the self-test.
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		if (args.length > 0) {
			System.out.println(value(Integer.parseInt(args[0])));
		} else {
			testIt(); 
		}
	}
	
}
