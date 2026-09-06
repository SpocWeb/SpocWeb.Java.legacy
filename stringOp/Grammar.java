package stringOp;

import java.io.IOException;

/**
  * Grammar.java
  * Recursively applies Productions (Mappings) to a String.
  * Doing this iteratively results in fractal Patterns.
  * The Inverse would be parsing the resulting String into it's original String
  * and the Productions applied, i.e. undoing the Mapping
  * This is possible, especially if it's an LL(1) Grammar.
  *
  * Working with Strings is a cheap shortcut
  * for working with Collections of Objects.
  *
  * Created on 16. Februar 2001, 21:39
  *
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:41:16Z
  * digest: a7b86e1595f96cb4f7b04c3343596dfe60cabbe468c381b0fcf22f10d2298ec3
  * stale: false
  * tags: [code/grammar_model, code/recursive_grammar]
  * concepts: [Grammar Evolution]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class Grammar 
extends Object {

	/** Stores the Productions: one String per Character (only ASCII) */
	protected String[] Productions = new String[Byte.MAX_VALUE+1];

	/** Creates new Grammar from the Productions given in the File. */
//    public Grammar (String Productions) { }

	/** Registers the Production (replacement String) to apply whenever Character X is encountered by {@link #evolve(String)}. */
	public void addProduction( int X, String Evolution) {
		Productions[X] = Evolution; }

	/** Applies one Generation of the registered Productions to arg: every Character with a registered Production
	 * is replaced by that Production's String, all other Characters are copied through unchanged.	 */
	public String evolve(String arg) {
		StringBuffer result = new StringBuffer();
		String tmp;
		int j, i = -1;
		int l = arg.length();
		while (++i < l) {
			if ((j = arg.charAt (i)) >= Productions.length) {
				result.append((char) j); continue; }
			if ((tmp = Productions[j]) == null) {
				result.append((char) j); continue; }
				result.append(tmp); }
		return result.toString(); }

	/**
	 * The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.
	 *
	 * Tests all Methods in the Property Package.
	 */
	public static void main (String[] args) throws IOException {
		Grammar g = new Grammar();
		g.addProduction ('A', "[&FB!A]////'[&FB!A]//////'[&FB!A]");
		g.addProduction ('B', "['''^^{-f+f+f-|-f+f+f}]");
		g.addProduction ('S', "FB");
		g.addProduction ('F', "S////F");
		String curr = "A";
		System.out.println(curr); curr = g.evolve(curr);
		System.out.println(curr); curr = g.evolve(curr);
		System.out.println(curr); curr = g.evolve(curr);
		System.in.read();
	}

}
