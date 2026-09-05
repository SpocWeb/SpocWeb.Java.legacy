/**
 * File  Name: VectorString.java
 * Created on: 18.12.2002
 */
package math.vector;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import streamIO.Assert;
import streamIO.Log;
import streamIO.copy.ICopyAble;
import streamIO.integer.IStreamIn_Byte;
import function.IMeasurAble;
import function.byref.ByRefChar;

/**
 * Growable, index-addressable array of {@link String} elements, paired with a large static
 * library of String/StringBuffer helpers: parsing, splitting, padding/aligning, trimming,
 * escaping, case conversion, and array/matrix operations (column, transpose, rotate).
 *
 * <p>Title: VectorString
 * <p>
 * Description: Purpose: Purpose / Responsibilities of this Class
 * Defines static Methods to treat Vectors and Arrays with Strings (and Objects).
 * Additional static Methods to enhance String or StringBuffer Functionality.
 * Since String is equally universal as Object (toString() is always present as Method),
 * it is used most frequently in Parsing, Encoding, Representing etc.
 * Design Decisions / Implementation
 * Details: Known SubClasses: <none> Known Uses: <none> Copyright: Copyright (c) Matthias
 * Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 10-26-2002, 12:47 PM
 * <p>
 * @author mheuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:55:55Z
 * digest: 1bff658a632c7ee9809d41d7fb845896dd96c84c39f66ce0d20ffce7dc676efd
 * stale: false
 * tags: [code/growable_array, code/string_parsing, code/string_formatting]
 * concepts: [Growable String[] Vector with String Utility Library]
 * facets: {layer: utility, status: broken, complexity: high}
 * -->
 */
public class VectorString extends AVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String[] TRUE = {"true", "1", "1.0", "yes", "ja", "da", "oui",
			"si", "wahr"};

	private static final String[] FALSE = {"false", "0", "0.0", "no", "nein", "njet",
			"non", "falsch"};

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// / Type Conversions with Defaults
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts a String into a boolean by checking only its first Character ('N', 'F' or
	 * '0' means false, anything else true).
	 * @return the String Value converted into a boolean the Default if String is null or
	 *         empty. Only the first Character is checked
	 */
	final static public boolean STRING2BOOLEAN(final String Value, final boolean Default) {
		if ((Value == null) || (Value.length() == 0)) { return Default; }
		int chr = Character.toUpperCase(Value.charAt(0));
		return (chr != 'N') && (chr != 'F') && (chr != '0');
	}

	/**
	 * Helper Method for decoding a boolean String
	 * @param strValue
	 * @return +/-1 for true/false and 0 if the String represents no known boolean Value
	 */
	final static public int STRING2BOOLEAN(final String strValue) {
		if (INDEX_OF(TRUE, strValue) >= 0) { return 1; }
		if (INDEX_OF(FALSE, strValue) >= 0) { return -1; }
		return 0;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parses {@code strValue} as a long, falling back to {@code Default} when null or empty.
	 * @return the String Value converted into an int the Default if String is null or
	 *         empty or not numeric.
	 */
	final static public long STRING2LONG(final String strValue, final long Default) {
		if ((strValue == null) || (strValue.length() == 0)) { return Default; }
		return Long.parseLong(strValue.trim());
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// / for SVG-Painter
	// ///////////////////////////////////////////////////////////////////////////////////

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public double[] STRING2DOUBLE(final String[] strValues) {
		double[] ret = new double[strValues.length];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = Double.parseDouble(strValues[i]);
		}
		return ret;
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public double STRING2DOUBLE(final String strValue,
			final double defaultValue) {
		return STRING2DOUBLE(strValue, defaultValue, 0);
	}

	/**
	 * Helper Method for parsing a String with absolute or Percentage Value
	 * @return 0 if null
	 */
	final static public double STRING2DOUBLE(final String strValue,
			final double defaultValue, final double defaultOffset) {
		if ((strValue == null) || (strValue.length() == 0))
			return defaultValue + defaultOffset; // 0;
		final int strLen_1 = strValue.length() - 1;
		if (strValue.charAt(strLen_1) == ByRefChar.CHR_PERCENT)
			return defaultOffset + defaultValue
					* Float.parseFloat(strValue.substring(0, strLen_1))
					* IMeasurAble.PERCENT;
		return Float.parseFloat(strValue); // absolute Value
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// static Helper Methods for parsing Strings
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public int INDEX_OF(final StringBuffer str, char value,
			boolean ignoreCase) {
		if (ignoreCase) value = Character.toUpperCase(value);
		for (int i = str.length(); --i >= 0;) {
			char chr = str.charAt(i);
			if (ignoreCase) chr = Character.toUpperCase(chr);
			if (chr == value) return i;
		}
		return -1;
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public int INDEX_OF(final Object[] values, final Object value,
			boolean useEquals) {
		if (value == null) {
			useEquals = false;
		}
		for (int i = values.length; --i >= 0;) {
			if ((values[i] == value) || (useEquals && value.equals(values[i]))) { return i; }
		}
		return -1;
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public int INDEX_OF(final String[] values, String value) {
		return INDEX_OF(values, value, null, false);
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public int INDEX_OF(final String[] values, String value,
			final String ignoreChars) {
		return INDEX_OF(values, value, ignoreChars, false);
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public int INDEX_OF(final String[] values, String value,
			boolean ignoreCase) {
		return INDEX_OF(values, value, null, ignoreCase);
	}

	/** Helper Method for decoding a String with absolute or Percentage Value */
	final static public int INDEX_OF(final String[] values, String value,
			final String ignoreChars, boolean ignoreCase) {
		if (value == null) {
			value = "";
		}
		for (int i = values.length; --i >= 0;) {
			if (EQUALS(value, values[i], ignoreChars, ignoreCase)) { return i; }
		}
		return -1;
	}

	/**
	 * Parses the given String and returns it in ret. Defaults the Delimiters to the White
	 * Space Characters.
	 * @param str String to be parsed
	 * @return an Array filled with the parsed Values
	 */
	final static public String[] SPLIT(final String str) {
		return SPLIT(str, IStreamIn_Byte.WHITESPACE);
	}

	/**
	 * Parses the given String and returns it in ret.
	 * @param str String to be parsed
	 * @param delims Characters to parse by
	 * @return an Array filled with the parsed Values
	 */
	final static public String[] SPLIT(final String str, final String delims) {
		final StringTokenizer tokenizer = new StringTokenizer(str, delims);
		final String[] ret = new String[tokenizer.countTokens()];
		if (ret.length != SPLIT(ret, tokenizer)) { throw new ArrayIndexOutOfBoundsException(
				"Error during Parsing!"); }
		return ret;
	}

	/**
	 * Parses the given String and returns it in ret.
	 * @param ret Container to hold the parsed Strings
	 * @param str String to be parsed
	 * @param delims Characters to parse by
	 * @return the Number of Elements filled (from 0 to n-1)
	 */
	final static public int SPLIT(final String[] ret, final String str) {
		return SPLIT(ret, str, IStreamIn_Byte.WHITESPACE);
	}

	/**
	 * Parses the given String and returns it in ret.
	 * @param ret Container to hold the parsed Strings
	 * @param str String to be parsed
	 * @param delims Characters to parse by
	 * @return the Number of Elements filled (from 0 to n-1)
	 */
	final static public int SPLIT(final String[] ret, final String str,
			final String delims) {
		return SPLIT(ret, new StringTokenizer(str, delims));
	}

	/**
	 * Parses the given String and returns it in ret.
	 * @param ret Container to hold the parsed Strings
	 * @param str String to be parsed
	 * @param delims Characters to parse by
	 * @return the Number of Elements filled (from 0 to n-1)
	 */
	private static final int SPLIT(final String[] ret, final StringTokenizer tokenizer) {
		for (int i = -1; ++i < ret.length;) {
			if (!tokenizer.hasMoreElements()) return i;
			ret[i] = tokenizer.nextToken();
		}
		return ret.length;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// static Helper Methods for operating on Strings
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Case sensitive comparison of two Strings after performing a filter on Whitespace.
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @param strForbiddenChars characters to filter from the strings
	 * @return true if the strings are equal after filter, false otherwise
	 */
	final static public boolean EQUALS(final String str1, final String str2) {
		return FIRST_DIFFERENCE(str1, str2, ByRefChar.WHITESPACE, false) >= 0;
	}

	/**
	 * compares two Strings after performing a filter on Whitespace
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @param ignoreCase Flag to ignore the Character Casing
	 * @return true if the strings are equal after filter, false otherwise
	 */
	final static public boolean EQUALS(final String str1, final String str2,
			final boolean ignoreCase) {
		return FIRST_DIFFERENCE(str1, str2, ByRefChar.WHITESPACE, ignoreCase) >= 0;
	}

	/**
	 * compares two Strings after performing a filter
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @param strIgnoreChars characters to filter from the strings
	 * @return true if the strings are equal after filter, false otherwise
	 */
	final static public boolean EQUALS(final String str1, final String str2,
			final String strIgnoreChars) {
		return FIRST_DIFFERENCE(str1, str2, strIgnoreChars, false) >= 0;
	}

	/**
	 * compares two Strings after performing a filter
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @param strIgnoreChars characters to filter from the strings
	 * @return true if the strings are equal after filter, false otherwise
	 */
	final static public boolean EQUALS(final String str1, final String str2,
			final String strIgnoreChars, final boolean ignoreCase) {
		return FIRST_DIFFERENCE(str1, str2, strIgnoreChars, ignoreCase) >= 0;
	}

	/**
	 * compares two Strings after performing a filter Of course, for iterated Comparisons
	 * it is faster to perform the Normalization Tasks once beforehand to both Arguments,
	 * instead of doing it for each Comparison!
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @param strIgnoreChars characters to filter from the strings
	 * @param ignoreCase Flag to switch on ignoring the Case
	 * @return the Position of the first Difference
	 */
	/*
	 * final static public int FIRST_DIFFERENCE(String str1, String str2, final String
	 * strIgnoreChars, final boolean ignoreCase){ if (ignoreCase) {
	 * str1=str1.toUpperCase(); str2=str2.toUpperCase(); } str1=TRIM_TO_CHARS(str1,
	 * strIgnoreChars).toString(); str2=TRIM_TO_CHARS(str2, strIgnoreChars).toString();
	 * return FIRST_DIFFERENCE(str1, str2); }
	 */

	/**
	 * compares two Strings and returns the Position of the first Difference Optionally
	 * the Strings should be trimmed first, e.g. by WhiteSpace and then cast to the same
	 * Case
	 * @param str1 first String to compare
	 * @param str2 second String to compare
	 * @return the Position of the first Difference (ignoring the Chars)
	 */
	final static public int FIRST_DIFFERENCE(final String s1, final String s2,
			final String strIgnoreChars, final boolean ignoreCase) {
		if (s1 == s2) { return -1; }
		if ((s1 == null) || (s2 == null)) { return 0; }
		for (int i1 = 0, i2 = 0;;) {
			int c1 = -1;
			while (++i1 < s1.length()) {
				if (strIgnoreChars == null) {
					break;
				}
				if (strIgnoreChars.indexOf(c1 = s1.charAt(++i1)) < 0) {
					break;
				}
			}
			int c2 = -1;
			while (++i2 < s2.length()) {
				if (strIgnoreChars == null) {
					break;
				}
				if (strIgnoreChars.indexOf(c2 = s2.charAt(++i2)) < 0) {
					break;
				}
			}
			if ((c1 < 0) != (c2 < 0)) { return s1.length() + s2.length(); }
			if (c1 < 0) { return -1; }
			if (c1 == c2) {
				continue;
			}
			if (!ignoreCase) { return i1; }
			if (Character.toUpperCase((char) c1) != Character.toUpperCase((char) c2)) { return i1; }
		}
	}

	/**
	 * Compares two same-case, unfiltered Strings position by position.
	 * @param s1 first String to compare
	 * @param s2 secnd String to compare
	 * @return the Position of the first Difference between s1 and s2
	 */
	final static public int FIRST_DIFFERENCE(final String s1, final String s2) {
		if (s1.equals(s2)) return -1;
		if (s1.length() > s2.length()) return s2.length();
		if (s1.length() < s2.length()) return s1.length();
		for (int i = -1; ++i < s1.length();)
			if (s1.charAt(i) != s2.charAt(i)) return i;
		return -1;
	}

	/**
	 * tolerant against Spaces or Attributes in the Element Name but not (yet) against
	 * nested Elements or CDATA Sections
	 * @param xml String that contains XML
	 * @param tagName Name of the Tag to use
	 * @return the Text of the first Tag, extracted by simple String Operations
	 */
	final static public String EXTRACT_XML_TAG(final String xml, final String tagName) {
		final int startTagStart = xml.indexOf("<" + tagName); // no Space allowed here!
		if (startTagStart < 0) { return null; } // no such Element
		final int startTagEnd = xml.indexOf('>', startTagStart); //
		if (startTagEnd < 0) { return null; } // no such Element
		if (xml.charAt(startTagEnd - 1) == '/') { return ""; } // empty Element
		final int endTagStart = xml.indexOf("</" + tagName, startTagEnd); //
		if (endTagStart < 0) { return null; } // no such Element
		final String ret = xml.substring(startTagEnd + 1, endTagStart); // this Line just
																		// for Debugging
		return ret; // should be optimized away...
	}

	/**
	 * Reads a named property, letting a matching JVM system property override the value
	 * found in {@code props}, and logs the resolved value to standard out.
	 * @param propName Name of the Property to read
	 * @param props Property Object to read from
	 * @param propDefault Default Value if the Property Name does not occur in the
	 *            Property Object
	 * @return the Property read from the Property Object, optionally overridden by the
	 *         System
	 */
	final static public String GET_PROPERTY(String propName, Properties props,
			String propDefault) {
		String ret = System.getProperty(propName, props
				.getProperty(propName, propDefault));
		System.out.println(propName + "=" + ret);
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// / Formatting Strings
	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * Formats (or truncates) the String to the given Length left aligned (right
	 * truncated) when the Length is positive right aligned (left truncated) when the
	 * Length is negative
	 */
	final static public String FORMAT(final String x, final int Length) {
		int add;
		if (Length >= 0) {
			if ((add = Length - x.length()) < 0) return x.substring(0, Length);
			return x + VectorString.Spaces.substring(0, add);
		} else {
			if ((add = Length + x.length()) > 0) return x.substring(add);
			return VectorString.Spaces.substring(0, -add) + x;
		}
	}

	/** Formats a Number by filling it into the Format String */
	final static public String ALIGN_RIGHT(int number, String format) {
		return ALIGN_RIGHT(Integer.toString(number).trim(), format);
	}

	/** Formats a Number by filling it into the Format String */
	final static public String ALIGN_RIGHT(int number, char filler, char length) {
		return ALIGN_RIGHT(number, FILLED(filler, length));
	}

	/** Formats a String by filling it into the Format String */
	final static public String ALIGN_RIGHT(String str, String format) {
		String strFileNr = format + str;
		// TODO: LOGIC: to right-align str within format's width, the intended slice is the last
		// format.length() characters of (format+str), i.e. substring(str.length()). Subtracting
		// format.length() again makes the start index negative whenever str is shorter than
		// format (the normal case this method exists for), throwing
		// StringIndexOutOfBoundsException instead of padding.
		return strFileNr.substring(str.length() - format.length());
	}

	/**
	 * Formats a String by filling it into a Format String of the given Length with the
	 * given Character
	 */
	final static public String FILLED(char filler, char length) {
		char[] chr = new char[length];
		Arrays.fill(chr, filler);
		return new String(chr);
	}

	/**
	 * Formats a String by filling it into a Format String of the given Length with the
	 * given Character
	 */
	final static public String ALIGN_RIGHT(String str, char filler, char length) {
		return ALIGN_RIGHT(str, FILLED(filler, length));
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// static Helper Methods for operating on StringBuffer
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * replaces the Characters in the given StringBuffer by Escape Sequences in the
	 * corresponding Array
	 * @param chars the Characters to replace
	 * @param strings the Strings replacing the Characters
	 */
	final static public StringBuffer REPLACE(StringBuffer SB, String chars,
			String[] substitutes) {
		for (int i = chars.length(); --i >= 0;) {
			char chr = chars.charAt(i);
			String str = Character.toString(chr);
			int index = SB.indexOf(str);// chr);
			while (index >= 0) {
				SB.replace(index, index + 1, substitutes[i]);
				index = SB.indexOf(str, index);// chr, index);
			}
		}
		return SB;
	}

	/**
	 * replaces the Characters in the given String by Escape Sequences in the
	 * corresponding Array
	 * @param chars the Characters to replace
	 * @param strings the Strings replacing the Characters
	 */
	final static public StringBuffer REPLACE(StringBuffer SB, String[] strings,
			String[] substitutes) {
		for (int i = strings.length; --i >= 0;) {
			String str = strings[i];
			int len = str.length();
			int index = SB.indexOf(str);
			while (index >= 0) {
				SB.replace(index, index + len, substitutes[i]);
				index = SB.indexOf(str, index);
			}
		}
		return SB;
	}

	/**
	 * replaces the Characters in the given String by Escape Sequences in the
	 * corresponding Array Due to the Change in Length either an Output streamIO or a
	 * StringBuffer can be used. The StringBuffer is usually better, because it allows
	 * granular Operations in RAM (may be even natively implemented!) and later Bulk IO
	 * Operations, although BufferedInputStream and BufferedOutputStream offer the same
	 * Functionality.
	 * @param esc the Escape Character to use
	 * @param chars the Characters to replace
	 * @param substitutes the Substitute Characters replacing the Characters
	 * @see parsing and escaping Separators!
	 */
	final static public StringBuffer REPLACE(StringBuffer SB, char esc, String chars) {
		for (int i = chars.length(); --i >= 0;) {
			char chr = chars.charAt(i);
			String str = Character.toString(chr);
			int index = SB.indexOf(str);// chr);
			while (index >= 0) {
				SB.insert(index, esc);
				index = SB.indexOf(str, index);// chr, index);
			}
		}
		return SB;
	}

	/**
	 * Concatenates the Prefix to the Suffix and stores the Result in the Return Array
	 * @param ret the Array to fill
	 * @param strPrefix first Component String
	 * @param strSuffix last Component String
	 * @return the Array ret containing the concatenated Strings
	 */
	final static public String[] CONCAT(final String[] ret, final String[] strPrefix,
			final String[] strSuffix) {
		for (int i = ret.length; --i >= 0;) {
			ret[i] = strPrefix[i] + strSuffix[i];
		}
		return ret;
	}

	/**
	 * Concatenates the Prefix to the Suffix and stores the Result in Place
	 * @param ret the Prefix Array to fill
	 * @param strSuffix the Suffix Strings
	 * @return the Array ret containing the concatenated Strings
	 */
	final static public String[] CONCAT_AT(final String[] ret, final String[] strSuffix) {
		for (int i = ret.length; --i >= 0;) {
			ret[i] += strSuffix[i];
		}
		return ret;
	}

	/**
	 * Concatenates the Prefix to the Suffix and stores the Result in Place
	 * @param ret the Prefix Array to fill
	 * @param strSuffix the Suffix String
	 * @return the Array ret containing the concatenated Strings
	 */
	final static public String[] CONCAT_AT(final String[] ret, final String strSuffix) {
		for (int i = ret.length; --i >= 0;) {
			ret[i] += strSuffix;
		}
		return ret;
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param ret the Array to fill
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @param fromEnd Flag to extract from the End
	 * @return the Array ret containing the subStrings
	 */
	final static public String SUBSTRING(final String str, final int begin,
			final int end, final boolean fromEnd) {
		if (fromEnd) {
			final int strLen = str.length();
			return str.substring(strLen - end, strLen - begin);
		}
		return str.substring(begin, end);
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param ret the Array to fill
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @param fromEnd Flag to extract from the End
	 * @return the Array ret containing the subStrings
	 */
	final static public String[] SUBSTRING(final String[] ret, final String[] strings,
			final int begin, final int end, final boolean fromEnd) {
		// String[] ret = new String[strings.length];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = SUBSTRING(strings[i], begin, end, fromEnd);
		}
		return ret;
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @return a new Array containing the subStrings
	 */
	final static public String[] SUBSTRING(final String[] strings, final int begin,
			final int end, final boolean fromEnd) {
		return SUBSTRING(new String[strings.length], strings, begin, end, fromEnd);
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @return a new Array containing the subStrings
	 */
	final static public String[] SUBSTRING_AT(final String[] strings, final int begin,
			final int end, final boolean fromEnd) {
		return SUBSTRING(strings, strings, begin, end, fromEnd);
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param ret the Array to fill
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @param fromEnd Flag to extract from the End
	 * @return the Array ret containing the subStrings
	 */
	final static public String SUBSTRING(final String str, final int begin,
			final boolean fromEnd) {
		if (fromEnd) {
			final int strLen = str.length();
			return str.substring(0, strLen - begin);
		}
		return str.substring(begin);
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param ret the Array to fill
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @param fromEnd Flag to extract from the End
	 * @return the Array ret containing the subStrings
	 */
	final static public String[] SUBSTRING(final String[] ret, final String[] strings,
			final int begin, final boolean fromEnd) {
		for (int i = ret.length; --i >= 0;) {
			ret[i] = SUBSTRING(strings[i], begin, fromEnd);
		}
		return ret;
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @return a new Array containing the subStrings
	 */
	final static public String[] SUBSTRING(final String[] strings, final int begin,
			final boolean fromEnd) {
		return SUBSTRING(new String[strings.length], strings, begin, fromEnd);
	}

	/**
	 * Performs the SubString Operation on each String and returns it in ret
	 * @param strings the Strings to extract from
	 * @param begin Begin Index
	 * @param end End-Index
	 * @return a new Array containing the subStrings
	 */
	final static public String[] SUBSTRING_AT(final String[] strings, final int begin,
			final boolean fromEnd) {
		return SUBSTRING(strings, strings, begin, fromEnd);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// static Helper Methods for operating on Strings (not in String, only in
	// StringBuffer)
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/** trims or pads the String to the given maximum Length */
	final static public String PAD(String str, final int length, final char filler,
			boolean padEnd) {
		int strLen = str.length();
		if (strLen > length) { throw new IndexOutOfBoundsException("String '" + str
				+ "' cannot be padded to Length=" + length); }
		StringBuffer SB = new StringBuffer(length);
		if (padEnd) {
			SB.append(str);
		}
		// TODO: LOGIC: this loop appends only (length - strLen - 1) filler characters, one short
		// of the (length - strLen) needed to reach the requested `length` - e.g. length=5,
		// strLen=2 appends 2 fillers instead of 3, so the returned String is 1 character shorter
		// than `length`, contradicting this method's contract.
		for (int i = length; --i > strLen;) {
			SB.append(filler);
		}
		if (!padEnd) {
			SB.append(str);
		}
		return SB.toString();
	}

	/**
	 * Finds the last suffix in {@code suffixes} that {@code str} ends with.
	 * @param str the String to test
	 * @param suffixes List of possible Suffixes
	 * @return the Index of the last matching Suffix
	 */
	final static public int ENDS_WITH(final String str, final String[] suffixes) {
		for (int i = suffixes.length; --i >= 0;)
			if (str.endsWith(suffixes[i])) return i;
		return -1;
	}

	// //////////////////////////////////////////////////////////////////////////
	// static Methods for testing a File-Name for a Suffix
	// //////////////////////////////////////////////////////////////////////////

	/** Case-insensitively tests whether a path or file name ends with the given suffix.
	 * @return true when the File ends with the given (in lower Case) Suffix */
	final static public boolean ENDS_WITH(String filePathOrName, String suffixToLower) {
		return filePathOrName.toLowerCase().endsWith(suffixToLower);
	}

	/** Case-insensitively tests whether the file's name ends with the given suffix.
	 * @return true when the File ends with the given (in lower Case) Suffix */
	final static public boolean ENDS_WITH(File file, String suffixToLower) {
		return file.getName().toLowerCase().endsWith(suffixToLower);
	}

	/**
	 * Tests whether {@code str} starts with {@code prefix}.
	 * @param str the String to test
	 * @param prefixes List of possible Prefixes
	 * @return the Index of the last matching Prefix
	 */
	final static public boolean STARTS_WITH(final StringBuffer str, final String prefix) {
		for (int i = prefix.length(); --i >= 0;) {
			if (str.charAt(i) != prefix.charAt(i)) return false;
		}
		return true;
	}

	/**
	 * Finds the last prefix in {@code prefixes} that {@code str} starts with.
	 * @param str the String to test
	 * @param prefixes List of possible Prefixes
	 * @return the Index of the last matching Prefix
	 */
	final static public int STARTS_WITH(final StringBuffer str, final String[] prefixes) {
		for (int i = prefixes.length; --i >= 0;) {
			if (STARTS_WITH(str, prefixes[i])) { return i; }
		}
		return -1;
	}

	/**
	 * Finds the last prefix in {@code prefixes} that {@code str} starts with.
	 * @param str the String to test
	 * @param prefixes List of possible Prefixes
	 * @return the Index of the last matching Prefix
	 */
	final static public int STARTS_WITH(final String str, final String[] prefixes) {
		for (int i = prefixes.length; --i >= 0;) {
			if (str.startsWith(prefixes[i])) { return i; }
		}
		return -1;
	}

	/** Inserts {@code string} into {@code str} at {@code insertPosition}. */
	final static public StringBuffer INSERT(String str, int insertPosition, String string) {
		return new StringBuffer(str).insert(insertPosition, string);
	}

	// return str.substring(0, insertPosition) + string + str.substring(insertPosition); }

	/** Deletes {@code numDeletes} characters from {@code str} starting at {@code deletePosition}. */
	final static public StringBuffer DELETE(String str, int deletePosition, int numDeletes) {
		return new StringBuffer(str).delete(deletePosition, numDeletes);
	}

	// return str.substring(0, i) + str.substring(i + j); }

	/** Deletes {@code numDeletes} characters at {@code deletePosition} and inserts {@code string} there. */
	final static public StringBuffer REPLACE(String str, int deletePosition,
			int numDeletes, String string) {
		return DELETE(str, deletePosition, numDeletes).insert(deletePosition, string);
	}

	/** Converts a Container of Strings into a String Array for faster Access */
	final static public String[] Collection2StringArray(Collection coll) {
		String[] ret = new String[coll.size()];
		int i = 0;
		Object item;
		Iterator iter = coll.iterator();
		// TODO: LOGIC: `++i` is a pre-increment, so the first element is written to ret[1],
		// leaving ret[0] permanently null, and the last element is written to ret[coll.size()],
		// which is out of bounds - this throws ArrayIndexOutOfBoundsException for any non-empty
		// collection. Should be `ret[i++]` (post-increment) to fill indices 0..size-1.
		while (iter.hasNext()) {
			ret[++i] = ((null == (item = iter.next())) ? null : item.toString());
		}
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// static Methods for copying from an Array to an Array or a Collection
	// (The latter is not supported Collection Classes!)
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/** Adds all Objects in the given Array to the given Container in the same Order */
	final static public Collection FILL_ALL(Collection coll, Object[] arr) {
		coll.clear();
		return ADD_ALL(coll, arr);
	}

	/** Adds all Objects in the given Array to the given Container in the same Order */
	final static public Collection ADD_ALL(Collection coll, Object[] arr) {
		int i = -1, len = arr.length;
		while (++i < len) {
			coll.add(arr[i]);
		}
		return coll;
	}

	/**
	 * Similar to System.arraycopy() and fill(), this Method copies the Source Array into
	 * the Destination Array, but can handle null or too small Source Arrays by filling
	 * the Destination with the given Default Values. This tolerant Initialization
	 * simplifies and speeds up later Processing, because Checks for incomplete Parameters
	 * can be omitted.
	 * @return true, when patching was necessary
	 */
	final static public String[] COPY_AND_FILL_UP(final String[] src, final int srcStart,
			String[] dst, final int dstStart, final int len, final String default_) {
		if (dst == null) dst = new String[len];
		COPY_AND_FILL_UP((Object[]) src, srcStart, dst, dstStart, len, default_);
		return dst;
	}

	/**
	 * Similar to System.arraycopy() and fill(), this Method copies the Source Array into
	 * the Destination Array, but can handle null or too small Source Arrays by filling
	 * the Destination with the given Default Values. This tolerant Initialization
	 * simplifies and speeds up later Processing, because Checks for incomplete Parameters
	 * can be omitted.
	 * @return true, when patching was necessary
	 */
	final static public boolean COPY_AND_FILL_UP(final Object[] src, final int srcStart,
			final Object[] dst, final int dstStart, final int len, final Object default_) {
		if (src == null) { // the Case that the Number Defaults doesn't match the Fields
							// is not tested (yet)
			java.util.Arrays.fill(dst, dstStart, len, default_); // append the CR/LF to
																	// the Defaults...
			return true;
		}
		int minLen = (src.length - srcStart < len) ? src.length - srcStart : len;
		if (minLen > 0) {
			System.arraycopy(src, srcStart, dst, dstStart, minLen);
		}
		if (minLen >= len) { return false; }
		java.util.Arrays.fill(dst, dstStart + minLen, len - minLen, default_);
		return true;
	}

	// //////////////////////////////////////////////////////////////////////////
	// / #region : static Methods for reversing the Characters in a Buffer
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Reverses the Contents of the given String
	 */
	final static public StringBuffer REVERSE(final StringBuffer in) {
		int i = in.length();
		StringBuffer ret = new StringBuffer(i);
		while (--i >= 0)
			ret.append(in.charAt(i));
		return ret;
	}

	/**
	 * Reverses the Contents of the given String
	 */
	final static public StringBuffer REVERSE(final String in) {
		int i = in.length();
		StringBuffer ret = new StringBuffer(i);
		while (--i >= 0)
			ret.append(in.charAt(i));
		return ret;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// / Expanding Character Vectors to String Vectors
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * appends the given Portion of the String to the StringBuffer.
	 * @param buf the Buffer to append to
	 * @param str
	 * @return null if the String doesn't contain any of the Separator Characters
	 */
	final static public StringBuffer APPEND(final String str, final int offSet,
			final int length) {
		return APPEND(null, str, offSet, length);
	}

	/**
	 * appends the given Portion of the String to the StringBuffer.
	 * @param buf the Buffer to append to
	 * @param str
	 * @return null if the String doesn't contain any of the Separator Characters
	 */
	final static public StringBuffer APPEND(StringBuffer buf, final String str,
			final int offSet, final int length) {
		if (buf == null) buf = new StringBuffer(str.length() + str.length());
		for (int i = -1; ++i < length;)
			buf.append(str.charAt(offSet + i));
		return buf;
	}

	/**
	 * Escapes every occurrence of a character from {@code separators} in {@code str} by
	 * duplicating it with {@code separators}' first character.
	 * @param str
	 * @param separators
	 * @return null if the String doesn't contain any of the Separator Characters
	 */
	final static public StringBuffer REPLACE_ALL(final String str, final String separators) {
		final char escapeChr = separators.charAt(0);
		StringBuffer ret = null;
		for (int len = str.length(), i = -1; ++i < len;) {
			final char chr = str.charAt(i);
			if (separators.indexOf(chr) >= 0) { // the actual Position doesn't matter
				// TODO: LOGIC: on first match this seeds `ret` via APPEND(str, 0, i - 1), which
				// copies only `i - 1` characters (0..i-2) instead of the `i` characters (0..i-1)
				// that actually precede this separator - the character immediately before the
				// first separator match is silently dropped from the result. When i == 0 (the
				// very first character is a separator), length -1 makes APPEND copy nothing,
				// which happens to be correct only in that one case.
				if (ret == null) ret = APPEND(str, 0, i - 1);
				ret.append(escapeChr);
			}
			if (ret != null) ret.append(chr);
		}
		return ret;
	}

	/**
	 * appends the Byte Array to a new StringBuffer
	 * @param str the Byte Array to append
	 * @return ret a new StringBuffer with str appended up to the first 0 Character.
	 */
	final static public StringBuffer TO_STRING(final byte[] str) {
		return TO_STRING(new StringBuffer(str.length), str);
	}

	/**
	 * appends the Byte Array to this StringBuffer
	 * @param ret the StringBuffer to append to
	 * @param str the Byte Array to append
	 * @return ret with str appended up to the first 0 Character.
	 */
	final static public StringBuffer TO_STRING(final StringBuffer ret, final byte[] str) {
		for (int i = -1; ++i < str.length;) {
			final byte b = str[i];
			if (b == 0) {
				break;
			}
			if (b < 0) {
				ret.append((char) 256 + b);
			} else {
				ret.append((char) b);
			}
		}
		return ret;
	}

	/**
	 * Converts each character of {@code strs} into its own single-character String.
	 * @return an Array of Strings with a single Character from the given Array each
	 */
	final static public String[] TO_STRING(char[] strs) {
		return TO_STRING(strs, null, 0);
	}

	/**
	 * Builds one String per element of {@code strs} by inserting that character into
	 * {@code container} at the given position.
	 * @return an Array of Strings resulting from the String container with the Character
	 *         inserted(!) at the given Position
	 */
	final static public String[] TO_STRING(final char[] strs, String container,
			final int pos) {
		if (container == null) {
			container = "";
		}
		final int len = container.length();
		char[] arr = new char[len + 1];
		container.getChars(0, pos, arr, 0);
		container.getChars(pos, len, arr, pos + 1);
		String[] ret = new String[strs.length];
		for (int i = strs.length; --i >= 0;) {
			arr[pos] = strs[i];
			ret[i] = new String(arr);
		}
		return ret;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// / Extracting Character Vectors from String Vectors
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Extracts the character at {@code pos} from every String in {@code strs}.
	 * @return the Column Vector at the given Position */
	final static public char[] CHAR_AT(final String[] strs, final int pos) {
		char[] ret = new char[strs.length];
		for (int i = strs.length; --i >= 0;) {
			ret[i] = strs[i].charAt(pos);
		}
		return ret;
	}

	/**
	 * Not necessary is a similar Routine that places the Index on the first Dimension,
	 * because CHAR_AT(strs[index], pos) can be used for that.
	 * @return the Characters at the given Position
	 */
	final static public char[] CHAR_AT(final String[][] strs, final int index,
			final int pos) {
		// return CHAR_AT(COLUMN(strs, index), pos); } //brief Implementation
		char[] ret = new char[strs.length]; // faster Implementation
		for (int i = strs.length; --i >= 0;) {
			ret[i] = strs[i][index].charAt(pos);
		}
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// / Selection of Values via (Multi-) Index
	// ///////////////////////////////////////////////////////////////////////////////////

	/**
	 * this is an Error-tolerant linear Mapping (Projection along the Dimension)
	 * @param a the Array to select the Value from
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop lower Bound (inclusive) for the Index
	 * @param start upper Bound (exclusive) for the Index
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public String GET_AT(final String[] a, final int index) {
		return GET_AT(a, index, 0);
	}

	/**
	 * this is an Error-tolerant linear Mapping (Projection along the Dimension)
	 * @param a the Array to select the Value from
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop lower Bound (inclusive) for the Index
	 * @param start upper Bound (exclusive) for the Index
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public String GET_AT(final String[] a, final int index, final int stop) {
		return GET_AT(a, index, "", stop);
	}

	/**
	 * this is an Error-tolerant linear Mapping (Projection along the Dimension)
	 * @param a the Array to select the Value from
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop lower Bound (inclusive) for the Index
	 * @param start upper Bound (exclusive) for the Index
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public String GET_AT(final String[] a, final int index,
			final String defaultValue) {
		return GET_AT(a, index, defaultValue, a.length);
	}

	/**
	 * this is an Error-tolerant linear Mapping (Projection along the Dimension)
	 * @param a the Array to select the Value from
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop lower Bound (inclusive) for the Index
	 * @param start upper Bound (exclusive) for the Index
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public String GET_AT(final String[] a, final int index,
			final String defaultValue, final int stop) {
		return GET_AT(a, index, defaultValue, stop, 0);
	}

	/**
	 * this is an Error-tolerant linear Mapping (Projection along the Dimension)
	 * @param a the Array to select the Value from
	 * @param index the Index to use
	 * @param defaultValue the Default Value, when the index is out of Bounds
	 * @param stop lower Bound (inclusive) for the Index
	 * @param start upper Bound (exclusive) for the Index
	 * @return the Value at the given Index (if in Bounds), the Default Value otherwise
	 */
	final static public String GET_AT(final String[] a, final int index,
			final String defaultValue, final int stop, final int start) {
		if ((index < start) || (index >= stop)) return defaultValue;
		return a[index];
	}

	// /////////////////////////////////////////////////////////////////////////
	// / Selection via Multi-Index
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * this is a linear Mapping (Projection along the Dimension) from integer Space into
	 * the real Numbers.
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) for the
	 *      same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.
	 * @return the selected Values of the given Vector, even with Dimension Mismatch.
	 */
	final static public String[] GET_AT(final String[] a, final VectorInt index) {
		return GET_AT(a, index.items, null, index.itemCount);
	}

	/**
	 * this is a linear Mapping (Projection along the Dimension) from integer Space into
	 * the real Numbers.
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) for the
	 *      same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.
	 * @return the selected Values of the given Vector, even with Dimension Mismatch.
	 */
	final static public String[] GET_AT(final String[] a, final VectorInt index,
			String[] ret) {
		return GET_AT(a, index.items, ret, index.itemCount);
	}

	/**
	 * this is a linear Mapping (Projection along the Dimension)
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) for the
	 *      same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.
	 * @return the selected Values of the given Vector, even with Dimension Mismatch.
	 */
	final static public String[] GET_AT(final String[] a, final int[] index) {
		return GET_AT(a, index, null);
	}

	/**
	 * this is a linear Mapping (Projection along the Dimension)
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) for the
	 *      same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.
	 * @return the selected Values of the given Vector, even with Dimension Mismatch.
	 */
	final static public String[] GET_AT(final String[] a, final int[] index,
			final String[] ret) {
		return GET_AT(a, index, ret, index.length);
	}

	/**
	 * this is a linear Mapping (Projection along the Dimension)
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) for the
	 *      same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.
	 * @return the selected Values of the given Vector, even with Dimension Mismatch.
	 */
	final static public String[] GET_AT(final String[] a, final int[] index,
			final String[] ret, int stop) {
		return GET_AT(a, index, ret, stop, 0);
	}

	/**
	 * this is a linear Mapping (Projection along the Dimension)
	 * @see streamIO.copy.monoid.integer.Permutation#map(int[], int, int[], int) for the
	 *      same Mapping by selecting the Columns.
	 * @param ret optional (null allowed) Array to take the Result.
	 * @return the selected Values of the given Vector, even with Dimension Mismatch.
	 */
	final static public String[] GET_AT(final String[] a, final int[] index,
			String[] ret, final int stop, final int start) {
		if ((ret == null) || (ret.length < stop)) ret = new String[stop];
		// else if (ret.length > stop) //rather leave the Values alone?!?
		// Arrays.fill(ret, stop, ret.length, 0);
		for (int i = stop; --i >= start;)
			ret[i] = (index[i] < a.length) ? a[index[i]] : "";
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// / Mapping Objects to Objects
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * creates or extends a Mapping between two Columns in the given Matrix
	 * @param mapping Array to fill from
	 * @param keyCol Column defining the Key
	 * @param valCol Column defining the Value
	 * @return a new HashMap filled with Pairs from the given Array
	 */
	final static public HashMap MAP(final Object[][] mapping, final int keyCol,
			final int valCol) {
		return MAP(new HashMap(mapping.length), mapping, keyCol, valCol);
	}

	/**
	 * creates or extends a Mapping between two Columns in the given Matrix
	 * @param ret The HashMap to fill / append to
	 * @param mapping Array to fill from
	 * @param keyCol Column defining the Key
	 * @param valCol Column defining the Value
	 * @return the given HashMap filled with Pairs from the given Array
	 */
	final static public HashMap MAP(final HashMap ret, final Object[][] mapping,
			final int keyCol, final int valCol) {
		return MAP(ret, mapping, keyCol, valCol, 0, mapping.length);
	}

	/**
	 * creates or extends a Mapping between two Columns in the given Matrix
	 * @param ret The HashMap to fill / append to
	 * @param mapping Array to fill from
	 * @param keyCol Column defining the Key
	 * @param valCol Column defining the Value
	 * @return a new HashMap filled with Pairs from the given Array
	 * @param startRow the first Row to import
	 * @param stopRow first Row not imported
	 * @return the given HashMap filled with Pairs from the given Array
	 */
	final static public HashMap MAP(final HashMap ret, final Object[][] mapping,
			final int keyCol, final int valCol, final int startRow, final int stopRow) {
		for (int i = stopRow; --i >= startRow;) {
			Object[] arr = mapping[i];
			// if (arr[keyCol] != null) { //automatically replace previous Mappings
			ret.put(arr[keyCol], arr[valCol]);
			// }
		}
		return ret;
	}

	/**
	 * Creates the Mapping: keys_ => vals_ by building up a HashMap It is better to keep
	 * the Relation, by always putting Key and Value into the same Row, instead to
	 * transposing and thus separating these.
	 * @param keys_ the Array of Keys
	 * @param vals_ the Array of Character Values
	 * @return a HashMap with get(keys_[i]) == vals_[i]
	 */
	final static public HashMap MAP(final Object[] keys_, final Object[] vals_) {
		int i = keys_.length;
		if (i != vals_.length) { throw new ArrayIndexOutOfBoundsException(
				"Array Sizes don't match: char[" + vals_.length + "], String["
						+ keys_.length + "]"); }
		HashMap ret = new HashMap(i);
		while (--i >= 0) {
			ret.put(keys_[i], vals_[i]);
		}
		return ret;
	}

	/**
	 * Creates the Mapping: keys_ => vals_ by building up a HashMap
	 * @param keys_ the Array of Keys
	 * @param vals_ the Array of Character Values
	 * @param container the String to insert the vals_ Characters into
	 * @param pos the at which to insert the vals_ Characters
	 * @return a HashMap with get(keys_[i]) == container+vals_[i]+container
	 */
	final static public HashMap MAP(final Object[] keys_, final char[] vals_,
			final String container, final int pos) {
		return MAP(keys_, TO_STRING(vals_, container, pos));
	}

	/**
	 * Creates the Mapping: keys_ => vals_ by building up a HashMap
	 * @param keys_ the Array of Keys
	 * @param vals_ the Array of Character Values
	 * @return a HashMap with get(keys_[i]) == vals_[i]
	 */
	final static public HashMap MAP(final Object[] keys_, final char[] vals_) {
		return MAP(keys_, vals_, null, 0);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// static Methods for creating and filling a StringBuffer from a Byte Array
	// this is slow and obsolete because of a String Constructor and Method getBytes()
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Converts a null-terminated byte array into a new StringBuffer of one char per byte.
	 * @return the StringBuffer filled with the Bytes of the Array. The String is
	 *         terminated by a 0 Byte or the end of the Array. The high Byte of the
	 *         Characters is set to 0.
	 */
	final static public StringBuffer toString(final byte[] bTmp) {
		return toString(bTmp, new StringBuffer());
	}

	/**
	 * Appends a null-terminated byte array into {@code SB} as one char per byte, reusing it
	 * after clearing its contents.
	 * @return the StringBuffer filled with the Bytes of the Array. The String is
	 *         terminated by a 0 Byte or the end of the Array. The high Byte of the
	 *         Characters is set to 0. This Method is similar to the String Constructor
	 *         taking a Byte Array, which is a one Step Operation though and probably
	 *         highly optimized!
	 */
	// TODO: LOGIC: the loop condition `tmp != 0` is checked using the *previous* iteration's
	// value (tmp starts at -1 and is only updated inside the append() argument), so a genuine
	// 0 byte at index 0 is appended as ' ' before the loop notices and stops on the next
	// iteration - the "terminated by a 0 Byte" contract in the Javadoc above is off by one byte.
	final static public StringBuffer toString(final byte[] bTmp, StringBuffer SB) {
		if (SB == null)
			SB = new StringBuffer(bTmp.length);
		else SB.setLength(0);
		int i, tmp = i = -1; //
		while ((++i < bTmp.length) && (tmp != 0)) { // is copying Bytes individually
													// faster than calling append?
			SB.append((char) (tmp = ((bTmp[i] + 256) & 0xFF)));
		} // not if inlining is done!
		return SB;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// / Capitalizing and trimming whole Arrays
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Liefert die Position des Feldes in der Liste durch eine lineare Suche.
	 * @param fieldNames Liste der Feldnamen, aus der die Position von 'fieldName'
	 *            ermittelt wird
	 * @param fieldName Name des zu suchenden Feldes
	 * @return die Position des Feldes in der Liste.
	 */
	final static public int FIND_FIRST(final String[] fieldNames, final String fieldName) {
		return FIND_FIRST(fieldNames, fieldName, false, false);
	}

	/**
	 * Liefert die Position des Feldes in der Liste durch eine lineare Suche.
	 * @param fieldNames Liste der Feldnamen, aus der die Position von 'fieldName'
	 *            ermittelt wird
	 * @param fieldName Name des zu suchenden Feldes
	 * @param trimFields switches trimming the Field Names
	 * @param ignoreCase switches ignoring the Cases of the Field Names
	 * @return die Position des Feldes in der Liste.
	 */
	final static public int FIND_FIRST(final String[] fieldNames, String fieldName,
			boolean trimFields, boolean ignoreCase) {
		if (ignoreCase) {
			fieldName = fieldName.toUpperCase();
		}
		if (trimFields) {
			fieldName = fieldName.trim();
		}
		for (int i = fieldNames.length; --i >= 0;) {
			String currField = fieldNames[i];
			if (currField == null) {
				if (fieldName == null) { return i; }
				continue;
			}
			if (trimFields) {
				currField = currField.trim();
			}
			if (ignoreCase) {
				currField = currField.toUpperCase();
			}
			if ((currField == fieldName) || // first test for Identity is faster!
					(currField.equals(fieldName))) { // (and safer for null Cases)
				return i;
			}
		}
		return -1; // not found
	}

	/**
	 * inserts the given Character into the given String at the given Positions
	 * @param str the String to be filled up
	 * @param Positions as they are in the original String in ascending Order!
	 * @param insert String to insert at the given Positions
	 * @return a Stringbuffer with chr inserted.
	 */
	final static public String INSERT_STRING(final String str, final int[] Positions,
			final String insert) {
		if (Positions.length == 0) { return str; }
		StringBuffer ret = new StringBuffer(str.length() + Positions.length
				* insert.length());
		int newPos = 0;
		for (int oldPos, i = -1; ++i < Positions.length;) {
			oldPos = newPos;
			newPos = Positions[i];
			ret.append(str.substring(oldPos, newPos));
			ret.append(insert);
		}
		ret.append(str.substring(newPos, str.length()));
		return ret.toString();
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// / Capitalizing and trimming whole Arrays
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/** trims the String first from WhiteSpace and then to the given maximum Length */
	final static public String TRIM_LENGTH(String str, final int length) {
		str = str.trim();
		int len = str.length();
		if (len > length) { return str.substring(0, length); }
		return str;
	}

	/**
	 * Normalizes the given String so that all WhiteSpace is replaced by a single Space.
	 * Leading and trailing Spaces are removed too! Additional normalization by converting
	 * into Upper Case is possible.
	 * @param str the String to trim
	 * @return a new StringBuffer filled with the conformant String.
	 */
	final static public StringBuffer NORMALIZE(final String str, final int changeCase,
			StringBuffer ret) {
		ret = TRIM_TO_CHARS(ret, str, ByRefChar.WHITESPACE, false, true, ' ', changeCase);
		// remove leading and trailing Spaces:
		final int last = ret.length() - 1;
		if (ret.charAt(last) == ' ') {
			ret.deleteCharAt(last);
		}
		if (ret.charAt(0) == ' ') {
			ret.deleteCharAt(0);
		}
		return ret;
	}

	/**
	 * Normalizes the given String so that all WhiteSpace is replaced by a single Space.
	 * Leading and trailing Spaces are removed too! Additional normalization by converting
	 * into Upper Case is possible.
	 * @param str the String to trim
	 * @param changeCase Flag whether to leave the Character Case (0)
	 * @return a new StringBuffer filled with the conformant String.
	 */
	final static public StringBuffer NORMALIZE(final String str, final int changeCase) {
		return NORMALIZE(str, changeCase, null);
	}

	/**
	 * Trims all White Space completely from the given String (replaces by nothing)
	 * @see #TRIM_TO_CHARS(String, String, boolean, boolean, char)
	 */
	final static public String TRIM_WHITESPACE(final String str) {
		return TRIM_CHARS(str, ByRefChar.WHITESPACE).toString();
	}

	/**
	 * Trims the given the given Characters from the String completely, with NO
	 * Replacement at all
	 * @param str the String to trim
	 * @param strForbiddenChars contains the forbidden Characters
	 * @return a new StringBuffer filled with the conformant String.
	 */
	final static public StringBuffer TRIM_CHARS(final String str,
			final String strForbiddenChars) {
		return TRIM_TO_CHARS(null, str, strForbiddenChars, false, false, (char) 0, 0);
	}

	/**
	 * Trims or replaces the given the given Characters from the String or vice versa,
	 * retains them when the Flag is set. The first and last Characters may only be
	 * replaced or collapsed, but not removed, so you possibly also have to trim the first
	 * and last Character.
	 * @param str the String to trim
	 * @param strForbiddenChars contains the allowed/forbidden Characters
	 * @param allowedCharsGiven switches between allowing and forbidding
	 * @param collapse switches on collapsing several forbidden Characters to one.
	 * @param replaceChar if != 0, replaces forbidden Characters with this one.
	 * @return a new StringBuffer filled with the conformant String.
	 */
	final static public StringBuffer TRIM_TO_CHARS(StringBuffer strBuf, final String str,
			final String strForbiddenChars, final boolean allowedCharsGiven,
			final boolean collapse, final char replaceChar, final int changeCase) {
		if (strBuf == null) strBuf = new StringBuffer(str.length());
		boolean replaced = false; // Flag for current Replacement Operation
		for (int i = -1; ++i < str.length();) {
			final char chr = str.charAt(i);
			final int ndx = strForbiddenChars.indexOf(chr);
			if ((ndx >= 0) == allowedCharsGiven) {
				if (changeCase > 0)
					strBuf.append(Character.toUpperCase(chr));
				else if (changeCase < 0)
					strBuf.append(Character.toLowerCase(chr));
				else strBuf.append(chr);
				replaced = false;
			} else {
				if (replaceChar > 0) {
					if (!replaced) {
						strBuf.append(replaceChar);
					}
					replaced = collapse;
				}
			}
		}
		return strBuf;
	}

	/**
	 * Capitalizes and trims all Elements of the Array
	 * @param arr the Array with the Strings to be trimmed and capitalized
	 * @param trim Flag to control Trimming
	 * @param toUpper Flag to control whether to generate capital or small Letters
	 * @return arr with the Strings replaced
	 */
	final static public String[] TRIM_CAPITALIZE_AT(final String[] arr,
			final boolean trim, final boolean toUpper) {
		for (int i = arr.length; --i >= 0;) {
			arr[i] = toUpper ? arr[i].toUpperCase() : arr[i].toLowerCase();
			if (!trim) continue;
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	/**
	 * Trims all Elements of the String Array
	 * @param arr the Array with the Strings to be trimmed
	 * @return arr with the Strings replaced
	 */
	final static public String[] TRIM_AT(final String[] arr) {
		int i = arr.length;
		while (--i >= 0)
			arr[i] = arr[i].trim();
		return arr;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////
	// / Matrix Trafos: extracting a Column
	// ////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Extracts column {@code col} from {@code strMatrix} as a new array.
	 * @return the Column at the given Position */
	final static public String[] COLUMN(String[][] strMatrix, int col) {
		String[] ret = new String[strMatrix.length];
		for (int i = strMatrix.length; --i >= 0;) {
			ret[i] = strMatrix[i][col];
		}
		return ret;
	}

	/** Transposes rows and columns of {@code matrix} into a new array.
	 * @return the Column at the given Position */
	final static public String[][] TRANSPOSE(String[][] matrix) {
		String[][] ret = new String[matrix[0].length][];
		for (int i = ret.length; --i >= 0;) {
			ret[i] = COLUMN(matrix, i);
		}
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// / Vector Methods
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	/** Rotates the array's elements left by one position, in place.
	 * @return the Permutation rotated left by 1 Element in Place */
	final static public String[] ROL(final String[] this_) {
		final int last = this_.length - 1;
		String tmp = this_[0];
		System.arraycopy(this_, 1, this_, 0, last);
		this_[last] = tmp;
		return this_;
	}

	/** Rotates the array's elements right by one position, in place.
	 * @return the Permutation rotated right by 1 Element in Place */
	final static public String[] ROR(final String[] this_) {
		final int last = this_.length - 1;
		String tmp = this_[last];
		System.arraycopy(this_, 0, this_, 1, last);
		this_[0] = tmp;
		return this_;
	}

	/**
	 * copies the Elements from the original into a new Array
	 * @param original Array to copy the Elements from
	 * @return a new Array with the Elements copied from the Original
	 */
	final static public String[] COPY(final String[] original) {
		return COPY(original, original.length);
	}

	/**
	 * copies the Elements from the original into a new Array
	 * @param original Array to copy the Elements from
	 * @param length The Length up to wich to copy the Elements.
	 * @return a new Array with the Elements copied from the Original up to length-1
	 */
	final static public String[] COPY(final String[] original, final int length) {
		final String[] ret = new String[length];
		System.arraycopy(original, 0, ret, 0, length);
		return ret;
	}

	/**
	 * copies the Elements from the original into a new Array
	 * @param original
	 * @return
	 */
	final static public void COPY_AT(final String[] ret, final String[] original) {
		System.arraycopy(original, 0, ret, 0, original.length);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : Variables
	// //////////////////////////////////////////////////////////////////////////////

	/** Backing Value Array for the float[] */
	protected String[] items;

	/** String of White Spaces to format Output */
	final static public String Spaces = "                                                                                ";

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : Accessor Methods (getXXX/isXXX/setXXX)
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the component at the specified index. Avoids null as opposed to
	 * @see #getAt(int)
	 * @param index an index into this Array.
	 * @return the component at the specified index.
	 * @exception ArrayIndexOutOfBoundsException if an invalid index was given.
	 */
	public synchronized String getStringSafeAt(final int index) {
		final String ret = getStringAt(index);
		if (ret == null) return "";
		return ret;
	}

	/** Returns the item at the given position, delegating to {@link #getStringAt(int)}.
	 * @return the item at the given Position as an Object */
	public Object getAt(final int i) {
		return getStringAt(i);
	}

	/**
	 * Returns the component at the specified index.
	 * @param index an index into this Array.
	 * @return the component at the specified index.
	 * @exception ArrayIndexOutOfBoundsException if an invalid index was given.
	 */
	public synchronized String getStringAt(final int index) {
		if (indexInRange(index)) return items[index];
		return null; // "";
	}

	/**
	 * Sets (adds or replaces) the component at the specified index. All other components
	 * in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and less than
	 * the current size of the Container.
	 * @param Item the component to set (add or replace).
	 * @param index the index of the object to remove.
	 * @return the component replaced by 'Item'.
	 * @exception ArrayIndexOutOfBoundsException if the index was invalid.
	 * @see java.util.Array#size()
	 */
	public Object setAt(final int index, final Object value) {
		return setAt(index, value.toString());
	}

	/**
	 * Sets (adds or replaces) the component at the specified index. All other components
	 * in this Container keep their <code>index</code>.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and less than
	 * the current size of the Container.
	 * @param Item the component to set (add or replace).
	 * @param index the index of the object to remove.
	 * @return the component replaced by 'Item'.
	 * @exception ArrayIndexOutOfBoundsException if the index was invalid.
	 * @see java.util.Array#size()
	 */
	public String setAt(final int index, final String value) {
		String ret = null;
		if (indexInRange(index))
			ret = items[index];
		else {
			if (value == null) return null; // save enlarging!
			setSize(index + 1);
		}
		items[index] = value;
		return ret;
	}

	/**
	 * Inserts the value at the specified index. All following value in this Container are
	 * shifted to the right.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and less than
	 * the current size of the Container.
	 * @param value the Value to insert.
	 * @param index the index of the value to insert at.
	 * @exception ArrayIndexOutOfBoundsException if the index was invalid.
	 */
	public void insertAt(final int index, final String value) {
		if (index >= itemCount) { //
			setAt(index, value);
		} else {
			setCapacity(++itemCount);
			System.arraycopy(items, index, items, index + 1, itemCount - index);
			items[index] = value;
		}
	}

	/**
	 * removes the Value at the specified index. All following components in this
	 * Container are shifted to the left.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and less than
	 * the current size of the Container.
	 * @param index the index of the object to remove.
	 * @return the value removed.
	 * @exception ArrayIndexOutOfBoundsException if the index was invalid.
	 */
	public String removeAt(final int index) {
		// TODO: LOGIC: `--itemCount` is evaluated unconditionally here; when index is out of
		// range (index > the pre-decrement itemCount), this still returns null below but
		// itemCount has already been permanently decremented, corrupting the vector's size even
		// though no element was removed.
		if (index > --itemCount) //
			return null;
		final String ret = items[index];
		System.arraycopy(items, index + 1, items, index, itemCount - index);
		return ret;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : Accessor Methods (getXXX/isXXX/setXXX)
	// / for multidimensional rectangular Arrays
	// //////////////////////////////////////////////////////////////////////////////

	/** Returns the value at the given row/column position of this rectangular array.
	 * @return the Value at the given Position */
	public String getAt(int Row, int Col) {
		return items[Row * dimFactors[0] + Col * dimFactors[1]];
	}

	/** sets the given Value */
	public void setAt(int Row, int Col, String Value) {
		items[Row * dimFactors[0] + Col * dimFactors[1]] = Value;
	}

	/** Returns the value at the given sheet/row/column position of this 3-dimensional array.
	 * @return the Value at the given Position */
	public String getAt(int Sheet, int Row, int Col) {
		return items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]];
	}

	/** sets the given Value */
	public void setAt(int Sheet, int Row, int Col, String Value) {
		items[Sheet * dimFactors[0] + Row * dimFactors[1] + Col * dimFactors[2]] = Value;
	}

	/** Returns the value at the position addressed by the given multi-index.
	 * @return the Value at the given Position */
	public String getAt(int[] Col) {
		return items[multiIndex(Col)];
	}

	/** sets the given Value */
	public void setAt(int[] Col, String Value) {
		items[multiIndex(Col)] = Value;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// / #region : Constructors, calling each other using this()/super()
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructs an empty VectorInt with the specified initial capacity and capacity
	 * increment.
	 * @param initialCapacity the initial capacity of the VectorInt.
	 * @param capacityIncrement the amount by which the capacity is increased when the
	 *            VectorInt overflows.
	 */
	public VectorString(int initialCapacity, int capacityIncrement_) {
		super();
		items = new String[initialCapacity];
		capacityIncrement = capacityIncrement_;
		// mEnum = new ArrayEnum(Items, ItemCount);
		// mEnum = new ArrayIterator(this);
	} //

	/**
	 * Constructs an empty VectorString with the specified initial capacity. Defaults the
	 * Capacity Increment to 'defaultCapacityIncr'.
	 * @param initialCapacity the initial capacity of the VectorString.
	 */
	public VectorString(int initialCapacity) {
		this(initialCapacity, DEFAULT_CAPACITY_INCR);
	}

	/**
	 * Constructs an empty VectorString. Defaults the initial Capacity to
	 * 'defaultCapacityInit'.
	 */
	public VectorString() {
		this(DEFAULT_CAPACITY_INIT);
	}

	/**
	 * Constructs an VectorString by copying from the given Object any Type. Defaults the
	 * Capacity Increment to 'defaultCapacityIncr'.
	 */
	public VectorString(Object arg) {
		this(DEFAULT_CAPACITY_INIT, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	/** Constructs an VectorString from the given Object. */
	public VectorString(Object arg, int capacityIncrement_) {
		this(DEFAULT_CAPACITY_INIT, capacityIncrement_);
		copyAt(arg);
	}

	/** Constructs an VectorString from the given Object. */
	public VectorString(String[] arg, int capacityIncrement_) {
		this(arg.length, capacityIncrement_);
		copyAt(arg);
	}

	/**
	 * Constructs an VectorString from the given Object and copies the Elements into this
	 * VectorString.
	 */
	public VectorString(String[] arg) {
		this(arg.length, DEFAULT_CAPACITY_INCR);
		copyAt(arg);
	}

	// //////////////////////////////////////////////////////////////////////////////
	// Methods for the dynamic 1dim Array Use
	// //////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the given Item to the End of the List optionally also enlarges the List.
	 */
	final public VectorString addItem(final String item) {
		setAt(itemCount, item);
		return this;
	}

	/**
	 * Copies the components of this VectorInt into the specified array. The array must be
	 * big enough to hold all the objects in this VectorInt.
	 * @param anArray the array into which the components get copied. Declared final,
	 *            because System.arraycopy is the fastest way.
	 */
	final public synchronized void copyInto(int[] anArray) {
		// TODO: LOGIC: `items` is a String[] here (unlike VectorInt, whose items are int[]);
		// copying it into an int[] destination via System.arraycopy throws ArrayStoreException
		// at runtime on every call once itemCount > 0. Apparently copy-pasted from VectorInt
		// without adjusting for VectorString's element type.
		System.arraycopy(items, 0, anArray, 0, itemCount);
		/*
		 * Object elementDataLocal[] = this.Items; for (int i = ItemCount; i-- > 0;)
		 * anArray[i] = elementDataLocal[i];
		 */
	}

	/**
	 * Copies the components of this VectorInt into the specified array. The array must be
	 * big enough to hold all the objects in this VectorInt.
	 * @param anArray the array into which the components get copied.
	 */
	final public synchronized int[] toArray() {
		int[] Return = new int[itemCount];
		// TODO: LOGIC: same ArrayStoreException hazard as copyInto(int[]) above: `items` is a
		// String[], not an int[], so this arraycopy throws at runtime once itemCount > 0.
		System.arraycopy(items, 0, Return, 0, itemCount);
		return Return;
	}

	/**
	 * Trims the capacity of this VectorInt to be the VectorInt's current size. An
	 * application can use this operation to minimize the storage of a VectorInt.
	 */
	final public synchronized void trimToSize() {
		int oldCapacity = items.length;
		if (itemCount < oldCapacity) {
			final String[] oldData = items;
			items = new String[itemCount];
			System.arraycopy(oldData, 0, items, 0, itemCount);
		}
	}

	/**
	 * Returns the current capacity of this VectorInt.
	 * @return the current capacity of this VectorInt.
	 */
	final public int getCapacity() {
		return items.length;
	}

	/**
	 * Increases the capacity of this VectorInt, if necessary, to ensure that it can hold
	 * at least the number of components specified by the minimum capacity argument.
	 * @param minCapacity the desired minimum capacity.
	 */
	final public synchronized int setCapacity(final int minCapacity) {
		final int oldCapacity = (items == null ? 0 : items.length);
		if (minCapacity <= oldCapacity) return oldCapacity;
		final int newCapacity = ENLARGED_CAPACITY(oldCapacity, capacityIncrement,
				minCapacity);
		final String[] oldData = items;
		items = new String[newCapacity];
		if (itemCount > 0) System.arraycopy(oldData, 0, items, 0, itemCount);
		return newCapacity;
	}

	/**
	 * Complement to Copy. Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value and returns itself for further use.
	 * When overriding, use copyAt on all Components. The Optimization here is that the
	 * Capacity can be ensured before and that additional Fields can be set.
	 */
	public VectorString copyAt(final String[] arg_) {
		itemCount = arg_.length;
		System.arraycopy(arg_, 0, items, 0, itemCount);
		return this;
	}

	/**
	 * Complement to Copy. Does a 'deepCopy', i.e. also inner Components are copied.
	 * Copies the Value of arg into it's own Value and returns itself for further use.
	 * When overriding, use copyAt on all Components. The Optimization here is that the
	 * Capacity can be ensured before and that additional Fields can be set.
	 */
	public ICopyAble copyAt(Object arg) {
		if (arg instanceof VectorString) {
			VectorString arg_ = (VectorString) arg;
			capacityIncrement = arg_.capacityIncrement;
			setCapacity(arg_.itemCount);
			itemCount = arg_.itemCount;
			System.arraycopy(arg_.items, 0, items, 0, itemCount);
		} else super.copyAt(arg); // no need to use a recursive DeepCopy like with
									// Tensor
		return this;
	}

	/**
	 * Does a shallow Copy of the Argument. I.e. both Instances will share their inner
	 * Components.
	 */
	public ICopyAble shallowCopyAt(Object arg) {
		if (arg instanceof VectorString) {
			VectorString arg_ = (VectorString) arg;
			capacityIncrement = arg_.capacityIncrement;
			itemCount = arg_.itemCount;
			items = arg_.items;
		} else super.copyAt(arg);
		return this;
	}

	/**
	 * Creates an uninitalized new Instance of it's class. This can in VB also be achieved
	 * by 'CreateObjectFromInstance', which may be slower. When overriding, use
	 * newInstance on all Components.
	 */
	public ICopyAble newInstance() {
		return new VectorString(items.length, capacityIncrement);
	}

	// //////////////////////////////////////////////////////////////////////////
	// / #region : static Testing and main() Methods (not in Interfaces)
	// //////////////////////////////////////////////////////////////////////////

	/** Tests the Trimming to a fixed Length */
	private static final void testTRIM() {
		final String str = "12345678";
		System.out.println(TRIM_LENGTH(str, 11));
		System.out.println(TRIM_LENGTH(str, 6));
	}

	/** Tests the Trimming to a fixed Length */
	private static final void testConcat() {
		final String strPrefix = null;
		final String strSuffix = "12345678";
		final String strResult = strPrefix + strSuffix;
		System.out.println(strResult);
	}

	/**
	 * Tests the Trimming to a fixed Length
	 */
	private static final void testNormalize() {
		final String str = "   Select  	 from \n  blabla \n where \r   ";
		Assert.EQUALS("select from blabla where", NORMALIZE(str, -1));
		Assert.EQUALS("SELECT FROM BLABLA WHERE", NORMALIZE(str, +1));
	}

	/** Tests all Methods of this Class */
	public static void testIt(String[] args) { // throws java.io.IOException {
		Log.N("Testing " + VectorString.class.getName());
		testConcat();
		testNormalize();
		testTRIM();
		System.out.println(VectorString.STRING2DOUBLE("98%", 100));
	}

	/**
	 * The main entry point for the application.
	 * @param args Array of parameters passed to the application via the command line.
	 */
	public static void main(String[] args) { // throws java.io.IOException {
		testIt(args);
	}

	/**
	 * Compares {@code str} against a byte buffer, treating each byte as an unsigned char code.
	 * @param str the String to compare
	 * @param buffer the bytes to compare
	 * @param length the Length to compare
	 * @return true when the Characters in the Buffer are the same as in the String
	 */
	final static public boolean EQUALS(final String str, final byte[] buffer,
			final int length) {
		for (int i = length; --i >= 0;) {
			int val = buffer[i];
			if (val < 0) {
				val += 256;
			}
			if (str.charAt(i) != val) { return false; }
		}
		return true;
	}

	/**
	 * Compares {@code str} against a byte buffer over their shorter common length.
	 * @param str
	 * @param buffer
	 * @return true when the String equals the Buffer
	 */
	final static public boolean EQUALS(final String str, final byte[] buffer) {
		return VectorString.EQUALS(str, buffer, Math.min(str.length(), buffer.length));
	}

	/**
	 * pases the Field List
	 * @param fields
	 * @return two Arrays of the same Size the first containing the Field Names the second
	 *         containing the Aliases
	 */
	final static public String[][] PARSE_2D(final String fieldList,
			final String sepOuter, final String sepInner, final boolean trim) {
		final String[] fields = fieldList.split(sepOuter);
		final String[] alias = new String[fields.length];
		for (int i = fields.length; --i >= 0;) {
			final String field = fields[i];
			final int pos = field.indexOf(sepInner);
			if (pos < 0)
				alias[i] = field;
			else {
				alias[i] = field.substring(0, pos);
				fields[i] = field.substring(pos + 1);
			}
			if (trim) {
				alias[i] = alias[i].trim();
				fields[i] = fields[i].trim();
			}
		}
		return new String[][]{fields, alias};
	}

	/** Finds the index of the last non-null element below {@code length}.
	 * @return the last Element of this Array that is not null */
	final static public int LAST_NOT_NULL(final Object[] a, int length) {
		while (--length >= 0) {
			if (a[length] != null) { return length; }
		}
		return -1;
	}

}
