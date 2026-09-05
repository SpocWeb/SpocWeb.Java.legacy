package graphs;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import stringOp.HeapByIndex;
import stringOp.Huffman;

/**Command-line entry point that runs the self-tests of several unrelated Classes
 * ({@link SparseMatrix}, {@link MatrixGraph}, {@link DisJointSet} and two Classes
 * outside this Package) in sequence; kept separate from those Classes' own testIt()
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:42:34Z
 * digest: 49b364a9d8448f92ff4acee01bff747c1c4ce7a78ad00055901d45e75589b448
 * stale: false
 * tags: [code/graph_data_structure]
 * concepts: [Scratch Test Class]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 * Methods just to bundle them into one runnable Program.	 */
public class testGraph {

	/** Tests the Formatting of Numbers according to different Locales
	 *
	 * <!-- docstate
	 * tags: [code/graph_data_structure]
	 * concepts: [Scratch Test Method]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void testNumberFormat() {
		// normally we would have a GUI with a menu for this
		Locale[] locales = NumberFormat.getAvailableLocales();
		double myNumber = -1234.56;
		NumberFormat form;
		// just for fun, we print out a number with the locale number, currency
		// and percent format for each locale we can.
		for (int j = 0; j < 3; ++j) {
			System.out.println("FORMAT");
			for (int i = 0; i < locales.length; ++i) {
				if (locales[i].getCountry().length() == 0) {
					// skip language-only
					continue; }
				System.out.print(locales[i].getDisplayName());
				switch (j) {
				default: form = NumberFormat.getInstance(locales[i]); break;
				case 1:  form = NumberFormat.getCurrencyInstance(locales[i]); break;
				case 0:  form = NumberFormat.getPercentInstance(locales[i]); break;
				} try {
					System.out.print(": " + ((DecimalFormat)form).toPattern()
								 + " -> " + form.format(myNumber));
				} catch (IllegalArgumentException iae) {
				} try { System.out.println(" -> " + form.parse(form.format(myNumber)));
				} catch (ParseException pe) { }
			}
		}
		form = new DecimalFormat("000");
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * <!-- docstate
	 * tags: [code/graph_data_structure]
	 * concepts: [Scratch Test Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception	{
		SparseMatrix.testIt();
		MatrixGraph.testIt();
//		char x = '\u221E';
		Huffman    .testIt();
		DisJointSet.testIt();
		HeapByIndex   .testIt();
//		testNumberFormat();
	}
}
