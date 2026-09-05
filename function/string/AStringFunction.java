/*
 * Created on 02.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.string;

import math.vector.VectorString;
import function.AFunction;

/**
 * Abstract Base Class for most String Functions. 
 * 
 * Defines some StringFunction Singletons and lots of static Methods 
 * and Constants to convert Strings, Encodings and write structured Text like XML. 
 * 
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:43:14Z
 * digest: d76ff8ecddbf2b888af28c46ba8b933108be2ffb145892d11916071d944c41a5
 * stale: false
 * tags: [code/string_transform, code/function_contract]
 * concepts: [String Transform Function]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public abstract class AStringFunction 
extends AFunction 
implements IStringFunction {

	/** Maps a String argument to its result, per the concrete String function. */
	public abstract String Map(final String arg);

	/** Converts {@code arg} to a String and delegates to {@link #Map(String)}.
	 * @see function.IFunction#Map(java.lang.Object)	 */
	public Object Map(final Object arg) { return Map(arg.toString()); }
	
	///////////////////////////////////////////////////////////////////////////
	/// static Members
	///////////////////////////////////////////////////////////////////////////
	
	/** Singleton of the stateless IStringFunction converting an Object into a String 	 */
	final static public IStringFunction TO_STRING = new AStringFunction() {
		public String Map(final String arg) { return arg; } 
		public Object Map(final Object arg) { return arg.toString(); } 
	};

	/** Singleton of the stateless IStringFunction converting a String into upper Case	 */
	final static public IStringFunction TO_UPPER = new AStringFunction() {
		public String Map(final String arg) { return arg.toUpperCase(); } };

	/** Singleton of the stateless IStringFunction converting a String into lower Case	 */
	final static public IStringFunction TO_LOWER = new AStringFunction() {
		public String Map(final String arg) { return arg.toLowerCase(); } };

	/** Singleton of the stateless IStringFunction converting a String into Proper Case	 
	 * i.e. each first Character after a Space is capitalized. 
	 */
	final static public IStringFunction TO_PROPER = new AStringFunction() {
		public String Map(final String arg) {
			final int len = arg.length();  
			final StringBuffer ret = new StringBuffer(len);
			boolean wasSpace = true; //start with a capital Letter
			for(int i = -1; ++i < len;) {
				final char chr = arg.charAt(i); 
				if (wasSpace) {
					ret.append(Character.toUpperCase(chr));
				} else {
					ret.append(Character.toLowerCase(chr));
				}
				wasSpace = Character.isSpaceChar(chr);
			}
			return ret.toString(); 
		} 
	};

	/** Singleton of the stateless IStringFunction converting a hungarian String into Camel Case
	 * i.e. the Character after an Underscore is capitalized
	 */
	final static public IStringFunction TO_CAMEL = new AStringFunction() {
		public String Map(final String arg) {
			final int len = arg.length();
			final StringBuffer ret = new StringBuffer(len);
			for(int i = -1; ++i < len;) {
				final char chr = arg.charAt(i);
				if (chr != '_') {
					ret.append(Character.toLowerCase(chr));
				} else {
					// TODO: LOGIC: if '_' is the last character of arg, arg.charAt(++i) reads one
					// past the end of the string and throws StringIndexOutOfBoundsException.
					// Reachable whenever the input (hungarian-notation) string ends with an
					// underscore, e.g. TO_CAMEL.Map("FOO_").
					ret.append(Character.toUpperCase(arg.charAt(++i)));
				}
			}
			return ret.toString();
		}
	};

	/** Singleton of the stateless IStringFunction converting a String into hungarian Notation
	 * i.e. each lower Case Character is converted to upper Case 
	 * and each Upper Case Character is converted into an Underscore plus this Character 
	 */
	final static public IStringFunction TO_HUNGAR = new AStringFunction() {
		public String Map(final String arg) {
			final int len = arg.length();  
			final StringBuffer ret = new StringBuffer(len);
			for(int i = -1; ++i < len;) {
				final char chr = arg.charAt(i); 
				if (Character.isUpperCase(chr)) {
					ret.append('_'); }
				ret.append(Character.toUpperCase(chr));
			}
			return ret.toString(); 
		} 
	};
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	/// brief, common German Special Character Encoding
	///////////////////////////////////////////////////////////////////////////////////////////////

	/** Strings encoding all Latin-1 Special Characters into brief, 
	 * well known or readable 2-ANSI-Character Substitutes 
	 * that don't look to disturbing in Text, 
	 * also avoiding reserved XML or Separator Characters 
	 * like tab, comma or '{' etc. 
	 * This is as brief as UTF-8 for Latin-1 
	 * but still readable and editable by 7-Bit Editors 
	 * and thus also avoids any Necessity to decode it again
	 * (unlike hex or decimal or Entity Encoding). 
	 */
	private static final String[][] UMLAUT_CHAR_STRINGS = 
			{ { ";", ".," }
			, { "\"", "\"\"" }
			, { "\t", "    " }
			, { "�", "(c)" }
			, { "�", "(r)" }
			, { "�", "acute" }
			, { "�", "//" }
			, { "�", "\\\\" }
			, { "�", "!i" }
			, { "�", "?i" }
			, {
				"�", "`A" }
			, { "�", "`a" }
			, { "�", "/A" }
			, { "�", "/a" }
			, { "�", "^A" }
			, { "�", "^a" }
			, { "�", "~A" }
			, { "�", "~a" }
			, { "�", "Ae" }
			, { "�", "ae" }
			, { "�", "Ao" }
			, { "�", "ao" }
			, { "�", "AE" }
			, { "�", "aE" }
			, { //all "A" Variations
				"�", ",C" }
			, { "�", ",c" }
			, { "�", "ETH" }
			, { "�", "eth" }
			, { "�", "`E" }
			, { "�", "`e" }
			, { "�", "/E" }
			, { "�", "/e" }
			, { "�", "^E" }
			, { "�", "^e" }
			, { "�", "EE" }
			, { "�", "eE" }
			, { //all "E" Variations
				"�", "`I" }
			, { "�", "`i" }
			, { "�", "/I" }
			, { "�", "/i" }
			, { "�", "^I" }
			, { "�", "^i" }
			, { "�", "II" }
			, { "�", "iI" }
			, { //all "N" Variations
				"�", "~N" }
			, { "�", "~n" }
			, { //all "O" Variations
			    "�", "`O" }
			, { "�", "`o" }
			, { "�", "/O" }
			, { "�", "/o" }
			, { "�", "^O" }
			, { "�", "^o" }
			, { "�", "~O" }
			, { "�", "~o" }
			, { "�", "Oe" }
			, { "�", "oe" }
			, { "�", "/O" }
			, { "�", "/o" }
			, { //all "O" Variations
				"�", "`U" }
			, { "�", "`u" }
			, { "�", "/U" }
			, { "�", "/u" }
			, { "�", "^U" }
			, { "�", "^u" }
			, { "�", "Ue" }
			, { "�", "ue" }
			, { //all "U" Variations
				"�", "`Y" }
			, { "�", "`y" }
			, { "�", "yy" }
			, { "�", "THORN" }
			, { "�", "thorn" }
			, { "�", "sz" } //instead of "ss" which makes the previous Vowel pronounced brief
			, { "�", "sect" }
			, { "�", "para" }
			, { "�", "micro" }
			, { "�", "brvbar" }
			, { "�", "+-" }
			, { "�", "middot" }
			, { "�", ".." }
			, { "�", "cedil" }
			, { "�", "ordf" }
			, { "�", "ordm" }
			, { "�", "not" }
			, { "�", "shy" }
			, { "�", "macr" }
			, { "�", "deg" }
			, { "�", "sup1" }
			, { "�", "sup2" }
			, { "�", "sup3" }
			, { "�", "frac14" }
			, { "�", "frac12" }
			, { "�", "frac34" }
			, { "�", "times" }
			, { "�", "divide" }
			, { //Currencies
				"�", "Cent" }
			, { "�", "Pound" }
			, { "�", "Ccy" }
			, { "�", "Yen" }
			, { "�", "Euro" } 
	}; //also replacing Semicolons and Tabs to be able to use them as Separators!
	
	/** Characters that are to be encoded by Strings
	 * This Array contains most of the printable ANSI (Latin-1) Characters.
	 */
	private static final char[] GERMAN_CHARS = VectorString.CHAR_AT(UMLAUT_CHAR_STRINGS, 0, 0); //
	
	/** Inverse of the XML Entity Encoding  */
	private static final String[] GERMAN_CHAR_STRINGS_INVERSE =
		Char2String.INVERSE(GERMAN_CHARS, VectorString.COLUMN(UMLAUT_CHAR_STRINGS, 1));
	
	/** XML Encoding of the complete Latin-1 Character Set
	 * The Function returns the Encoding String or null.
	 */
	final static public Char2String GERMAN_CHAR_ENCODER = new Char2String(GERMAN_CHAR_STRINGS_INVERSE);
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	/// XML Encoding of the basic XML Markup Characters
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Character starting an XML String Encoding
	 * Starting with the same Character considerably simplifies and speeds up Parsing!
	 */
	final static public char XML_ENTITY_START = '&';

//	final static public char XML_ENTITY_START = XML_CHR_AMPERSAND;

	/** Character ending an XML String Encoding
	 * Ending with the same Character considerably speeds up Parsing!
	 */
	final static public char XML_ENTITY_STOP = ';';

	//Significant Characters in Markup Language:

	//Attributes

	/** Quote Character of an XML Attribute	 */
	final static public char XML_CHR_APOSTROPH = '\'';

	/** Alternative Quote Character of an XML Attribute	 */
	final static public char CHR_QUOTE = '"';

	/** XML entity name for a double quote, without the leading/trailing '&amp;'/';'. */
	final static public String XML_STR_QUOTE     = "quot";
	/** XML entity name for an apostrophe, without the leading/trailing '&amp;'/';'. */
	final static public String XML_STR_APOSTROPH = "apos";
	
//	final static public String XML_ENTITY_QUOTE = XML_ENTITY_START+XML_STR_QUOTE    +XML_ENTITY_STOP;
//	final static public String XML_ENTITY_APOS  = XML_ENTITY_START+XML_STR_APOSTROPH+XML_ENTITY_STOP;
	
	/** Strings encoding Characters
	 * This Array contains only the minimum Entities
	 * required for XML Markup,
	 * without the leading and trailing Characters "&...;".
	 */
	private static final String[][] ARR_XML_ENTITY_STRINGS = { 
		{ "<", "lt"  }, 
		{ "&", "amp" }, 
		{"\"", XML_STR_QUOTE}, 
		{"\'", XML_STR_APOSTROPH } //not necessary when Quotes are chosen by Default 
	//	{ ">", "gt"  }, //not really necessary 
	}; //basic XML Entities
	
	/** Characters that are to be encoded by Strings
	 * This Array contains most of the printable ANSI (Latin-1) Characters.
	 */
	private static final char[] ARR_XML_ENTITY_CHARS = VectorString.CHAR_AT(ARR_XML_ENTITY_STRINGS, 0, 0); //
	
	/** Inverse of the XML Entity Encoding  */
	private static final String[] ARR_XML_ENTITY_STRINGS_INVERSE =
		Char2String.INVERSE(ARR_XML_ENTITY_CHARS, VectorString.COLUMN(ARR_XML_ENTITY_STRINGS, 1));
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	/// XML Encoding of all ANSI Latin-1 Characters
	///////////////////////////////////////////////////////////////////////////////////////////////

	/** Strings encoding Characters
	 * This Array contains Entities
	 * for all 96 = 0x60 printable ISO8859-1 (ANSI Latin-1) Characters above 0x80
	 * plus the Encodings for the basic XML Delimiter Characters.
	 * without the leading and trailing Characters "&...;".
	 * Unicode contains
	 * ASCII   from 0x00 to 0x7F
	 * Latin-1 from 0x80 to 0xFF
	 * 
	 * �����׾���������������������������������������������������������������������������������������><&
	 * 
	 * These Encodings are READABLE and can be maintained in ANSI using 7 Bit Editors, 
	 * but they require 6 Bytes Storage compared to 2 Bytes in UTF-8, 
	 * they are variable Length  
	 * AND they need these Entities to be declared in the according DTD! 
	 * Decimal Entity Encoding requires only 5 and Hex only 4 Bytes; 
	 * UTF-8 only needs 1.5 Bytes but is unreadable.
	 * UTF-16 needs 2 Bytes and is fixed Length and thus easier to pre-process. 
	 * Another Alternative is to rewrite some Umlaut Characters 
	 * by simple readable Double-Byte Sequences which is done in  
	 */
	private static final String[][] ARR_LATIN1_ENTITY_STRINGS = 
		{ {" ", "nbsp"} //,{"<", "lt"    }, {"&", "amp"   }, {">", "gt"    }, {"\"", "quot" }, {"\'", "apos" } //basic XML Entities
		, { "�", "copy" }
		, {"�", "reg" }
		, {"�", "acute" }
		, {"�", "laquo" }
		, {"�", "raquo" }
		, {"�", "iexcl" }
		, {"�", "iquest" }
		, {
			"�", "Agrave" }
		, { "�", "agrave" }
		, { "�", "Aacute" }
		, { "�", "aacute" }
		, { "�", "Acirc" }
		, { "�", "acirc" }
		, { "�", "Atilde" }
		, { "�", "atilde" }
		, { "�", "Auml" }
		, { "�", "auml" }
		, { "�", "Aring" }
		, { "�", "aring" }
		, { "�", "AElig" }
		, { "�", "aelig" }
		, { //all "A" Variations
			"�", "Ccedil" }
		, { "�", "ccedil" }
		, { "�", "ETH" }
		, { "�", "eth" }
		, { "�", "Egrave" }
		, { "�", "egrave" }
		, { "�", "Eacute" }
		, { "�", "eacute" }
		, { "�", "Ecirc" }
		, { "�", "ecirc" }
		, { "�", "Euml" }
		, { "�", "euml" }
		, { //all "E" Variations
			"�", "Igrave" }
		, { "�", "igrave" }
		, { "�", "Iacute" }
		, { "�", "iacute" }
		, { "�", "Icirc" }
		, { "�", "icirc" }
		, { "�", "Iuml" }
		, { "�", "iuml" }
		, { //all "I" Variations
			"�", "Ntilde" }
		, { "�", "ntilde" }
		, { "�", "Ograve" }
		, { "�", "ograve" }
		, { "�", "Oacute" }
		, { "�", "oacute" }
		, { "�", "Ocirc" }
		, { "�", "ocirc" }
		, { "�", "Otilde" }
		, { "�", "otilde" }
		, { "�", "Ouml" }
		, { "�", "ouml" }
		, { "�", "Oslash" }
		, { "�", "oslash" }
		, { //all "O" Variations
			"�", "Ugrave" }
		, { "�", "ugrave" }
		, { "�", "Uacute" }
		, { "�", "uacute" }
		, { "�", "Ucirc" }
		, { "�", "ucirc" }
		, { "�", "Uuml" }
		, { "�", "uuml" }
		, { //all "U" Variations
			"�", "Yacute" }
		, { "�", "yacute" }
		, { "�", "yuml" }
		, { "�", "THORN" }
		, { "�", "thorn" }
		, { "�", "szlig" }
		, { "�", "sect" }
		, { "�", "para" }
		, { "�", "micro" }
		, { "�", "brvbar" }
		, { "�", "plusmn" }
		, { "�", "middot" }
		, { "�", "uml" }
		, { "�", "cedil" }
		, { "�", "ordf" }
		, { "�", "ordm" }
		, { "�", "not" }
		, { "�", "shy" }
		, { "�", "macr" }
		, { "�", "deg" }
		, { "�", "sup1" }
		, { "�", "sup2" }
		, { "�", "sup3" }
		, { "�", "frac14" }
		, { "�", "frac12" }
		, { "�", "frac34" }
		, { "�", "times" }
		, { "�", "divide" }
		, { "�", "cent" }
		, { "�", "pound" }
		, { "�", "curren" }
		, { "�", "yen" }
		, {"�", "euro" } //Currencies
	};
	/* {
	//{" ", "nbsp"  }, //0xA0
	{"�", "iexcl" }, {"�", "cent"  }, {"�", "pound" }, {"�", "curren"},
	{"�", "yen"   }, {"�", "brvbar"}, {"�", "sect"  }, {"�", "uml"   },
	{"�", "copy"  }, {"�", "ordf"  }, {"�", "laquo" }, {"�", "not"   },
	{"�", "shy"   }, {"�", "reg"   }, {"�", "macr"  }, {"�", "deg"   },
	{"�", "plusmn"}, {"�", "sup2"  }, {"�", "sup3"  }, {"�", "acute" },
	{"�", "micro" }, {"�", "para"  }, {"�", "middot"}, {"�", "cedil" },
	{"�", "sup1"  }, {"�", "ordm"  }, {"�", "raquo" }, {"�", "frac14"},
	{"�", "frac12"}, {"�", "frac34"}, {"�", "iquest"}, {"�", "Agrave"},
	{"�", "Aacute"}, {"�", "Acirc" }, {"�", "Atilde"}, {"�", "Auml"  },
	{"�", "Aring" }, {"�", "AElig" }, {"�", "Ccedil"}, {"�", "Egrave"},
	{"�", "Eacute"}, {"�", "Ecirc" }, {"�", "Euml"  }, {"�", "Igrave"},
	{"�", "Iacute"}, {"�", "Icirc" }, {"�", "Iuml"  }, {"�", "ETH"   },
	{"�", "Ntilde"}, {"�", "Ograve"}, {"�", "Oacute"}, {"�", "Ocirc" },
	{"�", "Otilde"}, {"�", "Ouml"  }, {"�", "times" }, {"�", "Oslash"},
	{"�", "Ugrave"}, {"�", "Uacute"}, {"�", "Ucirc" }, {"�", "Uuml"  },
	{"�", "Yacute"}, {"�", "THORN" }, {"�", "szlig" }, {"�", "agrave"},
	{"�", "aacute"}, {"�", "acirc" }, {"�", "atilde"}, {"�", "auml"  },
	{"�", "aring" }, {"�", "aelig" }, {"�", "ccedil"}, {"�", "egrave"},
	{"�", "eacute"}, {"�", "ecirc" }, {"�", "euml"  }, {"�", "igrave"},
	{"�", "iacute"}, {"�", "icirc" }, {"�", "iuml"  }, {"�", "eth"   },
	{"�", "ntilde"}, {"�", "ograve"}, {"�", "oacute"}, {"�", "ocirc" },
	{"�", "otilde"}, {"�", "ouml"  }, {"�", "divide"}, {"�", "oslash"},
	{"�", "ugrave"}, {"�", "uacute"}, {"�", "ucirc" }, {"�", "uuml"  },
	{"�", "yacute"}, {"�", "thorn" }, {"�", "yuml"  } //0xFF
	{"<", "lt"    }, {"&", "amp"   }, {">", "gt"    }, {"\'", "quot" }, {"\"", "apos" },//basic XML Entities
	}; */

	/** Characters that are to be encoded by Strings
	 * This Array contains most of the printable ANSI (Latin-1) Characters.
	 */
	private static final char[] ARR_LATIN1_ENTITY_CHARS = VectorString.CHAR_AT(ARR_LATIN1_ENTITY_STRINGS, 0, 0); //

	/** Inverse of the XML Entity Encoding  */
	private static final String[] ARR_LATIN1_ENTITY_STRINGS_INVERSE =
		Char2String.INVERSE(ARR_LATIN1_ENTITY_CHARS, VectorString.COLUMN(ARR_LATIN1_ENTITY_STRINGS, 1));

	/** Inverse of the XML Encoding  */
	final static public String[] GET_ARR_LATIN1_ENTITY_STRINGS_INVERSE() {
		return VectorString.COPY(ARR_LATIN1_ENTITY_STRINGS_INVERSE);
	}

	/** Inverse of the XML Encoding for Latin-1  */
	final static public String[] GET_ARR_XML_ENTITY_STRINGS_INVERSE() {
		return VectorString.COPY(ARR_XML_ENTITY_STRINGS_INVERSE);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Basic XML Encoder
	////////////////////////////////////////////////////////////////////////////////

	/** Function encapsulating the XML Encoding
	 * @return the XML Encoding, null otherwise.
	 */
	final static public String XML_ENTITY_STRING(char character) {
		return Char2String.LOOKUP(ARR_XML_ENTITY_STRINGS_INVERSE, character);
	} //not necessary, but possible to use the Decoder

	/** Function encapsulating the XML Decoding */
	final static public char XML_ENTITY_CHAR(String entity) {
		return XML_ENTITY_DECODER.Map(entity).charAt(0);
	}

	/** XML Encoding of the complete Latin-1 Character Set
	 * The Function returns the Encoding String or null.
	 */
	final static public Char2String XML_ENTITY_ENCODER = new Char2String(ARR_XML_ENTITY_STRINGS_INVERSE);

	/** XML Decoding of the complete Latin-1 Character Set
	 * The Function returns Strings of Length 1 containing the Character or null.
	 */
	final static public StringFunction XML_ENTITY_DECODER = new StringFunction(ARR_XML_ENTITY_STRINGS, 0, 1);

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Latin-1 (ISO-8859-1) XML Encoder f�r
	////////////////////////////////////////////////////////////////////////////////

	/** Function encapsulating the XML Encoding
	 * @return the XML Encoding, null otherwise.
	 */
	final static public String LATIN1_ENTITY_STRING(char character) {
		return Char2String.LOOKUP(ARR_LATIN1_ENTITY_STRINGS_INVERSE, character);
	} //not necessary, but possible to use the Decoder

	/** Function encapsulating the XML Decoding */
	final static public char LATIN1_ENTITY_CHAR(String entity) {
		return LATIN1_ENTITY_DECODER.Map(entity).charAt(0);
	}
	
	/** XML Encoding of the complete Latin-1 Character Set
	 * The Function returns the Encoding String or null.
	 */
	final static public Char2String LATIN1_ENTITY_ENCODER = new Char2String(ARR_LATIN1_ENTITY_STRINGS_INVERSE);
	
	/** XML Decoding of the complete Latin-1 Character Set
	 * The Function returns Strings of Length 1 containing the Character or null.
	 */
	final static public StringFunction LATIN1_ENTITY_DECODER = new StringFunction(ARR_LATIN1_ENTITY_STRINGS, 0, 1);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	//some Strings for Testing
	static final String STR_TEST_HUNGAR = "A STR_TEST_HUNGARIAN"; 
	static final String STR_TEST_CAMEL  = "a strTestHungarian"; 
	static final String STR_TEST_UPPER  = "A STRTESTHUNGARIAN"; 
	static final String STR_TEST_LOWER  = "a strtesthungarian"; 
	static final String STR_TEST_PROPER = "A Strtesthungarian"; 
	
	/** small Helper Method to avoid circular References
	 * @see streamIO.Assert
	 */
	static final void ASSERT_EQUALS(final Object expected, final Object actual) {
		if (expected == actual) {
			return; }
		if ((expected != null) && expected.equals(actual)) {
			return; }
		throw new RuntimeException("Expected to be equal:\n"+expected+"\n"+actual); 
	}
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws java.io.IOException {
		System.out.println("Testing " + StringFunction.class.getName());
		ASSERT_EQUALS(STR_TEST_LOWER , StringFunction.TO_CAMEL .Map(STR_TEST_CAMEL)); 
		ASSERT_EQUALS(STR_TEST_HUNGAR, StringFunction.TO_HUNGAR.Map(STR_TEST_CAMEL)); 
		ASSERT_EQUALS(STR_TEST_LOWER , StringFunction.TO_LOWER .Map(STR_TEST_CAMEL)); 
		ASSERT_EQUALS(STR_TEST_PROPER, StringFunction.TO_PROPER.Map(STR_TEST_CAMEL)); 
		ASSERT_EQUALS(STR_TEST_UPPER , StringFunction.TO_UPPER .Map(STR_TEST_CAMEL)); 
		System.out.println("Careful: DOS (OEM) Output differs significantly from Windows (ANSI = Latin-1)");
		System.out.println("Number of Encodings:" + ARR_LATIN1_ENTITY_CHARS.length);
		System.out.println("�=" + '�' + "=" + (int) '�');
		java.io.FileOutputStream stream = new java.io.FileOutputStream("C:/test.txt");
		for (int i = ARR_LATIN1_ENTITY_CHARS.length; --i >= 0;) {
			stream.write(ARR_LATIN1_ENTITY_CHARS[i]);
			if (ARR_LATIN1_ENTITY_CHARS[i] > 255) {
				throw new java.io.IOException("Larger than 255!");
			}
			System.out.println(i + ":" + (int) ARR_LATIN1_ENTITY_CHARS[i] + "='" + ARR_LATIN1_ENTITY_CHARS[i] + "'");
		}
		String[] inverse =
			Char2String.INVERSE(ARR_LATIN1_ENTITY_CHARS, VectorString.COLUMN(ARR_LATIN1_ENTITY_STRINGS, 1));
		stream.write(13);
		stream.write(10);
		for (int i = 127; ++i < 256;) {
			stream.write((byte) i);
			if (inverse[i] == null) {
				System.out.println(i + ": has no Encoding! '" + (char) i + "'");
			}
		}
		stream.close();
	}

	/** The main entry point for the application; runs {@link #testIt()}. */
	public static void main(final String[] args) throws Exception {
		testIt();
	}

}
