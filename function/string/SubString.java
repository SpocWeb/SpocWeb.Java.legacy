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
	
	/**
	 * @param start the first Character to select 
	 * @param stop  the first Character to select NOT
	 */
	public SubString(final int _start, final int _stop) {
		this.start = _start; 
		this.stop  = _stop; 
	}
	
	/** @see function.string.AStringFunction#Map(java.lang.String)	 */
	public String Map(final String arg) {
		if (stop == Integer.MAX_VALUE)
			return arg.substring(start); 
		return arg.substring(start, stop);
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(final String[] args) {
	}
	
}
