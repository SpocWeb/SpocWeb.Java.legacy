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
  */
public class Grammar 
extends Object {

	/** Stores the Productions: one String per Character (only ASCII) */
	protected String[] Productions = new String[Byte.MAX_VALUE+1];

	/** Creates new Grammar from the Productions given in the File. */
//    public Grammar (String Productions) { }

	public void addProduction( int X, String Evolution) {
		Productions[X] = Evolution; }

	public String evolve(String arg) {
		StringBuffer result = new StringBuffer();
		String tmp;
		int j, i = -1;
		int l = arg.length();
		while (++i < l) {
			if ((j = arg.charAt (i)) > Productions.length) {
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
