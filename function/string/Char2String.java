package function.string;

import math.vector.VectorChar;
import math.vector.VectorString;

/**
  * Maps single characters (or the character at a fixed position of a String) to their encoded
  * String replacement, via a direct array lookup rather than a hash table.
  *
  * Title: Char2String<p>
  * Description:
  * Purpose:
  * Maps Characters to Strings (Encoding) very fast.
  * Also maps Strings to Strings by the Character at the given Position. 
  * 
  * Design Decisions / Implementation Details:
  * @see function.string.StringFunction which maps Strings to Strings, 
  * but can also map individual Characters to Strings using a HashMap, thus slower. 
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-21-2002, 04:54 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:43:43Z
  * digest: 4e1d690f1ceba860b900c76a79c9d2727bb21a542594ac3a96199de14560725a
  * stale: false
  * tags: [code/string_transform, code/function_contract]
  * concepts: [String Transform Function]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Char2String
extends AStringFunction {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * returns the String from the given Array of Strings selected by the given chr.
	 * This Function avoids the IndexOutOfBoundsException
	 * by checking the Bounds before returning the Result.
	 * @return the String if chr is contained in _strings, null otherwise
	 */
	final static public String LOOKUP(final String[] _strings, final int chr) {
		if ((chr < 0) ||
			(chr >= _strings.length)) {
			return null; }
		return _strings[chr]; }

	/**
	 * This Function could be sped up by inverting the Mapping,
	 * creating a large String[] Array with null mostly anywhere.
	 * Possibly even faster would be to skip the Test for null
	 * and fill the Array completely with the correct 1-char Strings.
	 * @return the String if chr is contained in _chars, null otherwise
	 */
	final static public String LOOKUP(final char[] _chars, final String[] _strings, final int chr) {
		for (int i = _chars.length; --i >= 0;) {
			if (chr == _chars[i]) {
				return _strings[i]; }
		}
		return null; }

	/** Inverts the Mapping _chars => _strings
	  * @return an Array with ret[_chars[i]] == _strings[i]
	  */
	public static String[] INVERSE(final char[] _chars, final String[] _strings) {
		return INVERSE(_chars, _strings, VectorChar.Max(_chars)); }
	
	/** Inverts the Mapping _chars => _strings
	  * @return an Array with ret[_chars[i]] == _strings[i]
	  */
	public static String[] INVERSE(final char[] _chars, final String[] _strings, final int maxChar) {
	    final String[] ret = new String[maxChar+1];
//		for(i=MaxChar; --i >= 0;) {
//			ret[i] = Character.toString(MaxChar); }
		for(int i = _chars.length; --i >= 0;) {
			ret[_chars[i]] = _strings[i]; }
		return ret; }
	
	/** Inverts the Mapping _chars => _strings
	  * @return an Array with ret[_chars[i]] == _strings[i]
	  */
	public static String[] INVERSE(final String[] _chars, final int pos, final String[] _strings) {
		char[] charArray = VectorString.CHAR_AT(_chars, pos);
		return INVERSE(charArray, _strings, VectorChar.Max(charArray)); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Position of the Character in the arg String being encoded 	 */
	protected int pos;
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'encoding' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the inverse(!) Encoding Array used for encoding the incoming Bytes	 */
	protected String[] encoding;
	
	/** Returns the encoding registered for character code {@code i}, or null if none.
	 * @return The inverse Encoding Array used for encoding the incoming Bytes	 */
	public String getEncoding(final int i) { return LOOKUP(encoding, i); }
	
	/**not published, since it could be modified 
	 * @return The inverse Encoding Array used for encoding the incoming Bytes	 */
	public String[] getencoding() { return encoding; }
	
	/** Sets The inverse Encoding Array used for encoding the incoming Bytes	 */
	public void setencoding(final String[] encoding_) {
		this.encoding = encoding_; }
	
	/** Sets The inverse Encoding Array used for encoding the incoming Bytes	 */
	public void setEncoding(final int i, final String _encoding) {
		if (i >= encoding.length) {
			if  (_encoding == null) 
				return; 
			final String[] newEncoding = new String[i+1];
			System.arraycopy(encoding, 0, newEncoding, 0, encoding.length); 
			encoding = newEncoding; 
		}
		encoding[i] = _encoding; }
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor	 */
	public Char2String(final String[] encoding_, final int pos_) {
		this.encoding = encoding_;
		this.pos = pos_;
	}
	
	/** Constructor	 */
	public Char2String(final String[] encoding_) {
		this(encoding_, 0);
	}
	
	/** Constructor	 */
	public Char2String(final char[] _chars, final String[] _strings, final int pos_) {
		this.encoding = INVERSE(_chars, _strings);
		this.pos = pos_;
	}

	/** Constructor	 */
	public Char2String(final char[] _chars, final String[] _strings) {
		this(_chars, _strings, 0); 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Looks up the encoding registered for character code {@code chr}.
	 * @return the Mapping of arg by this Function, null otherwise 	 */
	public String Map(final int chr) {
		return LOOKUP(encoding, chr); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStringFunction: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**Maps the Character of arg at the Position set in the Constructor to a String 
	 * @return the Mapping of arg by this Function, null otherwise 	 */
	public String Map(final String arg) {
		return LOOKUP(encoding, arg.charAt(pos)); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Char2String.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) { //throws java.io.IOException {
		testIt(args); }
	
}
