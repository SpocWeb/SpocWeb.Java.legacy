package stringOp;

import streamIO.integer.pipe.PipeByte;
import stringOp.parser.MathParser;
import stringOp.search.RegExp;
import stringOp.search.SearcherBM;
import stringOp.search.SearcherRK;
import stringOp.search.StrSearcher;
import function.byref.ByRefChar;

/**This class can take a variable number of parameters on the command
 * line. Program execution begins with the main() method. The class
 * constructor is not invoked unless an object of type 'Class1'
 * created in the main() method.	 */
public class testString {

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		PipeByte.testIt();
		RegExp.testIt();
		MathParser.testIt();
		// TODO: Add initialization code here
		String sList	= "A STRING SEARCHING EXAMPLE CONSISTING OF MANY CHARACTERS...";
		String sPattern = "STING";
		Object[] List	= ByRefChar.String2ByRefChar(sList);
		Object[] Pattern= ByRefChar.String2ByRefChar(sPattern);
		StrSearcher S1 = new StrSearcher(Pattern);
		SearcherBM  S2 = new SearcherBM (Pattern);
		SearcherRK  S3 = new SearcherRK (Pattern);
		System.out.println("Position of '" + Pattern + "' in '" + List + "' \n = "
						   + " correct: " + sList.indexOf(sPattern) + " \n = "
						   + StrSearcher.dumbIndexOf (Pattern, List)
						   + S1.indexOf(List) + " \n = "
						   + S2.indexOf(List) + " \n = "
						   + S3.indexOf(List) + " \n = "
						   ); }

	/**Searching in a String can be done more or less effective,
	 * depending on the way you encode the Pattern and the List.
	 * If you can form a large Alphabet,
	 * you can easily compare large sets of the equivalent Pattern
	 * expressed in a small alphabet.
	 * This is the same Effect as with Compressing Data,
	 * which replaces the Alphabet by a new one
	 * that uses less bits for the most frequent Characters (Huffman Encoding)
	 * or uses a Flag to indicate Repetitions (Run Length Encoding) 	 */

}
