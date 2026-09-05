/*
 * Created on 02.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.string;

/**
 * Generic parameterized SubString Function. 
 * Can be specialized to a Right or Left String Function.  
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:43:36Z
 * digest: 204b07024f34ef4796f32ec599a522086d9fa8172de3a695cbdc4102c9ff1d8a
 * stale: false
 * tags: [code/string_transform, code/function_contract]
 * concepts: [String Transform Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class SubString 
extends AStringFunction {
	
	/** the first Character to select */ 
	final int start;
	/** the first Character to select NOT. 
	 * Defaulted to a very large value */ 
	final int stop; 
	
	/**
	 * selects the whole Rest of a String
	 * @param start the first Character to select 
	 */
	public SubString(final int _start) { this(_start, Integer.MAX_VALUE); }
	
	/** Creates a function selecting the substring from {@code _start} up to (not including)
	 * {@code _stop}.
	 * @param start the first Character to select
	 * @param stop  the first Character to select NOT
	 */
	public SubString(final int _start, final int _stop) {
		this.start = _start;
		this.stop  = _stop;
	}

	/** Returns the configured substring of {@code arg}.
	 * @see function.string.AStringFunction#Map(java.lang.String)	 */
	public String Map(final String arg) {
		if (stop == Integer.MAX_VALUE)
			return arg.substring(start);
		return arg.substring(start, stop);
	}

	///////////////////////////////////////////////////////////////////////////

	/** Unused entry point; this class has no self-test. */
	public static void main(final String[] args) {
	}
	
}
