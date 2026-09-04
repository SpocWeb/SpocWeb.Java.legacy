package stringOp.parser;

import graphs.PairVal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 * Rewritten Scanner that makes use of a pushback-Stream. 
 * The Advantage of a PushBack Stream is that 
 * -it can easily be implemented using a cache-Filter 
 * -it encapsulates the Stream State in the Stream itself, 
 *  so no second Variable needs to be considered. 
 * 
 * Old Version!!!
 * Generic universal optimized and simple Scanner Class for LL(1) Grammars
 * that accumulates Strings until one of a String of Separators is encountered.
 * TODO: The Rest of the Scanner must be adjusted to the IStreamIn Interface!
 * 
 * Returns the Index of the found Character (>= 0)
 * A single Escape Character can be defined that prevents reading the Separator.
 * Additionally Comment Characters for single Lines '#' and multiple Lines
 * like the StreamTokenizer features are useful, but not implemented.
 *
 * Different from the Iterator Interface, this Scanner Class
 * already reads the first Item into it's cache, because the LL(1) Grammars
 * need to know the next Element.
 * You cannot and needn't implement loops like 'while (nextItem() != EOL)...'
 * because the flow of Parsing depends on the nextItem.
 *
 * (nextToken() and thisToken())
 * and Parsers for the basic primitive Types:
 * Number(), Integer() and Hex() expressed as long
 * Real() expressed as double
 * Identifier(), Label() as String
 * Character(not enclosed in '')
 * String	(not enclosed in "")
 * Use RingFuncs.FuncParser for mathematical Expressions.
 *
 * 'nextItem()' is a Parser for nested (,) Structures with optional Tag Names:
 * equivalent to simple XML (w/o Attributes)
 * structure: TagName(Value1, ..., ValueN)TagName
 *
 * 'Element(int Level)' is a Parser for simple nested Separator Grammars
 * without Tag Names, also usable for 'INI' and 'Properties' Files
 * (which are, by the way, not nested).
 *
 * A Scanner does not yet attribute Meaning to single Characters
 * like a Tokenizer does (Separators, WhiteSpace, Digits etc.)
 *
 * No Backtracking / pushBack() necessary, because of LL(1) Grammar.
 * Design Decisions:
 * 		Bottom-Up Implementation.
 * 		Put all Parsing Routines into this Class to save Classes.
 * 		The StringBuffer is not public, because clearing and reading can be
 * 		very well encapsulated.
 *
 * @deprecated
 * @see streamIO.object.parser.EscapeStreamIn is newer and replaces the Assembling part of this Scanner
 * @see streamIO.object.parser.InputStream2StreamIn, a high-Performance Parser
 */
public class Scanner 
//implements IIStreamIn_Int 
{
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static Constants
	///////////////////////////////////////////////////////////////////////////////
	
	/**Error Message String Constant      */
	final static public String STR_ERR_EXPECTED = " Expected: ";
	
	/**Error Message String Constant      */
	final static public String STR_ERR_OCCURRED = " Occurred: ";
	
	/**Error Message String Constant      */
	final static public String STR_TOKEN = "Token ";
	
	/**Constant for the Escape Character '\'
	 * The Escape Character is removed skipped by the Scanner
	 * and the following Character taken literally. 	 */
	final static public char CHR_ESC = '\\';
	
	/**Constant for the single Quote Character '	 */
	final static public char CHR_QUOTE = '\'';
	
	/**Constant for the double Quote Character "	 */
	final static public char CHR_DBL_QUOTE = '\"';
	
	/**Constant for the Space Character ' '	 */
	final static public char CHR_SPACE = ' ';
	
	/**Constant for the Character '+'	 */
	final static public char CHR_PLUS = '+';
	
	/**Constant for the Character '-'	 */
	final static public char CHR_MINUS = '-';
	
	/**Constant for the Character '-'	 */
	final static public char CHR_DOT = '.';
	
	/**Constant for the Character '_'	 */
	final static public char CHR__ = '_';
	
	/**Constant for the Character '0'	 */
	final static public char CHR_0 = '0';
	
	/**Constant for the Character '9'	 */
	final static public char CHR_9 = '9';
	
	/**Constant for the Character 'A'
	 * implicit Assumption in HexNumber(): A < a	 */
	final static public char CHR_A = 'A';
	
	/**Constant for the Character 'E'	 */
	final static public char CHR_E = 'E';
	
	/**Constant for the Character 'F'	 */
	final static public char CHR_F = 'F';
	
	/**Constant for the Character 'Z'	 */
	final static public char CHR_Z = 'Z';
	
	/**Constant for the Character 'a'
	 * implicit Assumption in HexNumber(): A < a	 */
	final static public char CHR_a = 'a';
	
	/**Constant for the Character 'e'	 */
	final static public char CHR_e = 'e';
	
	/**Constant for the Character 'f'	 */
	final static public char CHR_f = 'f';
	
	/**Constant for the Character 'z'	 */
	final static public char CHR_z = 'z';
	
	/**Constant for the Character '$'	 */
	final static public char CHR_$ = '$';
	
	/**String to construct the Parsing Error Messages	 */
	final static public String strLetter = "Letter";
	
	/**String to construct the Parsing Error Messages	 */
	final static public String strDigit = "Digit";
	
	/**String to construct the Parsing Error Messages	 */
	final static public String strHex = "Hex";
	
	/**String to construct the Parsing Error Messages	 */
	final static public String strHexDigit = strHex + strDigit;
	
	/**String to construct the Parsing Error Messages	 */
	final static public String strExpected = " expected!";
	
	/**WHITESPACE Characters
	 * They act as separators, but are usually truncated
	 * The following Characters are considered as WhiteSpace by Java:
	 *
	 * '\t' \u0009 HORIZONTAL TABULATION
	 * '\n' \u000A NEW LINE
	 * '\f' \u000C FORM FEED
	 * '\r' \u000D CARRIAGE RETURN
	 * '  ' \u0020 SPACE
	 * Unicode space separator (category "Zs"), but is not a no-break space (\u00A0 or \uFEFF).
	 * Unicode line separator (category "Zl").
	 * Unicode paragraph separator (category "Zp").
	 * \u0009 '\t' HORIZONTAL TABULATION.
	 * \u000A '\n' LINE FEED.
	 * \u000B '  ' VERTICAL TABULATION.
	 * \u000C '\f' FORM FEED.
	 * \u000D '\r' CARRIAGE RETURN.
	 * \u001C '  ' FILE SEPARATOR.
	 * \u001D '  ' GROUP SEPARATOR.
	 * \u001E '  ' RECORD SEPARATOR.
	 * \u001F '  ' UNIT SEPARATOR.
	 * \u0020 '  ' SPACE
	 */
	final static public String WHITESPACE = "\t\n\f\r ";
	
	/**Indicator of the End of the Input streamIO.
	 * The Value is chosen to be the same as StreamTokenizer.TT_EOF for consistency
	 * and it corresponds to the Position in the Separator String (as a Watcher)
	 */
	final static public char SCN_CHR_EOF = (char) IIStreamIn_Int.EOF;
	
	/** Indicates the start Tag of a new Element, the Name of the Element is returned
	  * The Value corresponds to the Position in the Separator String */
	final static public int SCN_TAG_START = 0;
	
	/**Indicates the Stop Tag of an Element, the Name of the Element is returned
	  * The Value corresponds to the Position in the Separator String */
	final static public int SCN_TAG_STOP = 1;
	
	/**Indicates an Attribute Element using Apostrophe or Quote,
	 * Name and Value are returned in an Association Object	 */
	final static public int SCN_TAG_ATTRIBUTE = 2;
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static Variables
	///////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////
	//	Static Methods
	///////////////////////////////////////////////////////////////////////////////
	
	//these Methods complement the isSpace, isSpaceChar, isWhiteSpace and isIsoControl
	//in Class 'java.lang.Character'
	
	/** @return true only when this Character is a Letter	 */
	public static boolean IS_ALPHA_NUM(final int c) {
		return
			(c == '_')  ||
			IS_LETTER(c) ||
			IS_DIGIT(c); }
	
	/** @return true only when this Character is a Letter	 */
	public static boolean IS_LETTER(final int c) {
		return
			IS_LOWER_LETTER(c) ||
			IS_UPPER_LETTER(c); }
	
	/** @return true only when this Character is a lower Case Letter	 */
	public static boolean IS_LOWER_LETTER(final int c) {
		return (c >= CHR_a && c <= CHR_z); }
	
	/** @return true only when this Character is an upper Case Letter	 */
	public static boolean IS_UPPER_LETTER(final int c) {
		return (c >= CHR_A && c <= CHR_Z); }
	
	/**Function, returns true only when this Character is a Number	 */
	public static boolean IS_DIGIT(final int c) {
		return (c >= CHR_0 && c <= CHR_9); }
	
	/**Returns true, when c is a Hex Digit	 */
	public static boolean IS_HEX_DIGIT(final int c) {
		return (IS_DIGIT(c)	|| ((c >= CHR_A) && (c <= CHR_F))
							|| ((c >= CHR_a) && (c <= CHR_f)) ); }
	
	/** Flattens the nested Vector structure by removing empty Vectors
	  * and replacing Vectors with single Elements by the Elements themselves
	  * at a certain Level (cannot use the highest or lowest Level, too dangerous!).
	  */
	public static Vector FLATTEN_ALL(Vector arg) {
		for(int MaxDegree = maxDegree(arg); --MaxDegree >= 0;)
			FLATTEN(arg, MaxDegree);
		return arg; }
	
	/**Flattens the nested Vector structure by removing empty Vectors
	 * and replacing Vectors with single Elements by the Elements themselves
	 * at a certain Level (cannot use the highest or lowest Level, too dangerous!).
	 */
	public static Vector FLATTEN(final Vector arg, final int level) {
		for(int i = arg.size(); --i >= 0;) {
			final Object item;
			if ((item = arg.get(i)) instanceof Vector) {
				Vector curr = (Vector) item;
				if (level > 0) { FLATTEN(curr, level-1);
	            } else {
					int Size;
					if ((Size = curr.size()) == 0)
						arg.remove(i);
					else if (Size == 1)
						arg.set(i, curr.get(0)); }
			}
		}
		return arg; }
	
	/** Flattens the nested Vector structure by removing empty Vectors
	  * and replacing Vectors with single Elements by the Elements themselves
	  * at a certain Level (cannot use the highest or lowest Level, too dangerous!).
	  */
	public static Vector cleanAll(final Vector arg) {
		int MaxDegree = maxDegree(arg);
		while (--MaxDegree >= 0)
			clean(arg, MaxDegree);
		return arg; }
	
	/** Cleans the nested Vector structure by removing Vectors with single Elements
	  * at a certain Level (cannot use the highest or lowest Level, too dangerous!).
	  */
	public static Vector clean(final Vector arg, int Level) {
		Object Item; --Level;
		int i = arg.size();
		while (--i >= 0)
			if ((Item = arg.get(i)) instanceof Vector) {
				final Vector curr = (Vector) Item;
				if (Level > 0) FLATTEN(curr, Level);
				else {
					int Size;
					if ((Size = curr.size()) == 0)
						arg.remove(i);
					else if (Size == 1)
						arg.set(i, curr.get(0)); }
			} else if (Item instanceof String)
				if (((String) Item).length() == 0)
					arg.remove(i);
		return arg; }
	
	///////////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/**Switches the Construction of Expressions and Numbers on or off.	 */
	public boolean construct = true;
	
	/**Switch for the Escape Character '\'
	 * The Escape Character is removed skipped by the Scanner
	 * and the following Character taken literally. 	 */
	//public boolean escapeChars;
	
	/**Previous Character, used e.g. on empty XML Tags
	 * and for quick Grammar Verifications. 	 */
	public int prevChar;
	
	/**Current Character, since 'int' fully contains 'char'
	 * Could be public if several Characters would result in the same Token
	 * and the following Class was interested in the specific Character.
	 */
	protected int currChar;
	
	/**Current Position / Token of the Character in the Separator String
	 * It must be public, because the XML Scanner queries it frequently.
	 * It is best to store it here, because this is closer to the Source,
	 * although Integrity could be broken by writing it.
	 */
	public int currToken;
	
	/**	Reference to the Iterator giving the next Expression.
	  * This Iterator may already deliver fully parsed Objects like Numbers etc.	 */
	private InputStream IS;
	
	/**Switches the Input streamIO to a new streamIO.
	 * Synchronized, because the parsing Operation must first finish it's job.	 */
	/*public synchronized void setInput(InputStream IS) throws IOException {
		this.IS = IS; currChar = IS.read(); }
	
	/**Cache for the Separator String
	 * Not used, instead the more effective InverseSep Structure is used.
	 */
	//public String Separators;
	
	/** Inverse Mapping of the Separator Characters to their Position for faster parsing
	  * This saves a Scanning Loop. but is of little use otherwise.	 */
	private byte[] InverseSep;
	
	/** StringBuffer containing the current String. 	 */
	private StringBuffer Buffer = new StringBuffer();
	
	/** Returns the last retrieved Element
	  * made Public because of the frequent Access,
	  * although Integrity could be compromised by writing it.
	  */
	public String Result;
	
	/** Clears the Buffer containing the assembled previous Characters	 */
	public void clearString() {	Buffer.setLength(0); }
	
	/** Returns the String assembled from the last read Operation(s)
	  * and clears the Buffer to zero Length for the next Operation(s).	 */
	public String getResult() {
		 Result = Buffer.toString(); Buffer.setLength(0); return Result; }
	
	/** Initializing Constructor (Separators is max. 127 Characters long)	 */
	public Scanner(final InputStream IS, final String Separators) throws IOException {
		this.IS = IS;
		int cMax = 0;
		byte i = (byte) Separators.length();
		while (--i >= 0)
			if (cMax < Separators.charAt(i))
				cMax = Separators.charAt(i);
		InverseSep = new byte[cMax+1]; //new Arrays are already initialized!
	//	while (--cMax >= 0) //could use the new Array Methods.
	//		InverseSep[cMax] = (byte) -1;
		i = (byte) Separators.length();
		while (--i >= 0) //invert the mapping.
			InverseSep[Separators.charAt(i)] = (byte) (i+1);
		nextToken(); //read ahead the first Token.
	}
	
	/** Escape Character.
	  * Escapes the following Character to enable parsing.
	  * The Escape Character itself is filtered out, except if escaped!  */
	public char EscapeChar = (char) -2;
	
	/** reads the next Character, stores the previous one
	  * and optionally adds it to the StringBuffer.
	  * Filters out Escape Characters etc.
	  * Most simple Scanning Routine...
	  * This Sequence is correct, because the next Character is not decided on. 	 */
	public int Character() throws IOException {
		prevChar = currChar;
		if ((currChar = IS.read()) == EscapeChar)
			 Buffer.append((char) IS.read()); //return the Escape Char to prevent parsing the escaped Char
		else Buffer.append((char) currChar);
		return currChar; }
	
	/** Reads the streamIO up to the given Separator. 
	  * This is more effective than using the usual Token() Routine
	  * and ignoring all other Tokens but only apted for non-recursive Grammars.
	  * 'removeSeps' is ignored, because this is a protected Region.  */
	public int thisToken(final char Sep) throws IOException {
		Buffer.setLength(0);
		while ((currChar = IS.read()) != Sep) {
			if (currChar == IIStreamIn_Int.EOF ) {
				currToken = IIStreamIn_Int.EOF; break; }
			Buffer.append((char) currChar); }
		return currToken = InverseSep[Sep]-1; } //return the Correct Token
	
	/**If true, the Separators found on parsing, are removed from the Result */
	public boolean removeSeps = true;
	
	/** Returns the Position of the next found Separator in the Separator String.
	  * Reuses the Character() Method to test for the next Character
	  * this is slightly ineffective, because the Separator is added to the Buffer.
	  * But the Code Reuse is higher than with copying most of the Character() Method.
	  * This Routine could be sped up by indexing the Separator String (for long Separators)
	  * and by not calling the Character() Routine.	 */
	public int nextToken() throws IOException { //
		if (InverseSep.length == 0) return Character(); //cannot do a nextToken if the List of Separators is empty => Any Char is a Separator
		do { //read the next Character...
			if  ((currChar  = Character()) < 0) return currToken = currChar; //return EOF directly
		} while ((currChar  > InverseSep.length) || //not found, because larger than Array
				( currToken = InverseSep[currChar]) <= 0); //not found, because not EOF in Array
		if (removeSeps) Buffer.setLength(Buffer.length()-1); //remove the Separator from the Token
		return --currToken; } //currChar; }
	
	/*public int Token(String Separators) throws IOException { //
		while ((currToken = Separators.indexOf	(currChar = Character())) < 0)
			if (currChar < 0) return currChar; //return EOF directly
		Buffer.setLength(Buffer.length()-1); //remove the Separator from the Token
		return currToken; } //currChar; }
	*/
	
	/** Parser for very simple nested Separator Grammars without Tag Names,
	  * also usable for 'INI' and 'Properties' Files which are nested only once.
	  * (a,b,c)
	  *
	  * Uses the Vector Class to return the Result, because it's size is unknown yet.
	  * The same Implementation using a Container is done in absSet
	  * Recursively reads the next Element above this Level
	  * and collects the Sub- Elements in a Vector.
	  * Expects the first Element to be already read, so 'currToken' is already set.
	  * Doesn't proceed up to higher Levels, but just stops.
	  */
	public Vector readBag(final int Level) throws IOException {
		boolean read = false;
		Vector ret = new Vector();
		int Level1 = Level + 1;
		do {
			if  (read) nextToken(); read = true;
			if  (currToken <= Level)  //current Level is transferred as Parameter.
				 ret.addElement(getResult());
			else ret.addElement(readBag(Level1));
		} while (currToken >= Level); //using "currToken" to transfer actual Level
		return ret; }
	
	/**Determines, whether Information about the Length is stored in the Lists
	 * parsed by readRelation().	 */
	public boolean leadingKey = true;
	
	/**Determines, whether Information about the Length is stored in the Lists
	 * parsed by readRelation().	 */
	public boolean trailingKey = true;
	
	/**Switches between a strict Grammar where each List Item MUST be followed by a Delimiter
	 * and a relaxed one, where the last Delimiter can be skipped in readRelation().
	 */
	public boolean strictItem = true;
	
	/** Retrieves the next Item of a nested (,) structure with optional Tag Names:
	  * TagName(Value1, ..., ValueN)TagName
	  *
	  * Uses the Vector Class to return the Result, because it's size is unknown yet.
	  * This Grammar is in Fact equivalent to XML without Attributes.
	  * Just replace "<Tag>" by "Tag(" and "</Tag>" by ")Tag,"
	  * and the Result can be parsed by this Routine!  */
	public Vector readRelation(final Vector lList) throws IOException { //If no Start Tag: return the current Item
		while (currToken != SCN_TAG_STOP)
			lList.addElement(readPair()); //recursively read the inner Items...
		if (strictItem) {
	        clearString(); nextToken();
		} else {
	        lList.addElement(readPair()); //...read an unterminated Item (Tolerance...).
	    } //skip any untermiated Contents
		return lList; }
	
	/**Retrieves the next Item of a nested (,) structure with optional Tag Names:
	 * TagName(Value1, ..., ValueN)TagName
	 *
	 * Uses the Vector Class to return the Result, because it's size is unknown yet.
	 * This Grammar is in Fact equivalent to XML without Attributes.
	 * Just replace "<Tag>" by "Tag(" and "</Tag>" by ")Tag,"
	 * and the Result can be parsed by this Routine!  */
	public Object readPair() throws IOException { //If no Start Tag: return the current Item
		String Key = getResult(); //optionally accept a leading Key
		int prevToken = currToken; nextToken(); //read ahead on Stop Tokens, you lose the
		if (prevToken != SCN_TAG_START) { return Key; }	//closing or delimiting Tag...
		if (!leadingKey &&(Key.length() > 0)) throw new AbstractMethodError();
		
		final Vector lList = readRelation(new Vector());	 //flatten any Container with single Elements!?!
		
		if (!trailingKey) {
	        if (Buffer.length() > 0) throw new AbstractMethodError();
	    } else {
			if ((getResult() != Key) && leadingKey && !Result.equals(Key))
				throw new AbstractMethodError();
			Key = Result; }  //optionally accept a trailing Key
		nextToken();
		if ((Key != null) && (Key.length() > 0))
			return new PairVal(Key, lList);
		return lList; }
	
	/**Returns the Degree of the nested Vector structure
	 * This is the Degree of the Tensor,
	 * don't confuse this with the maximum Dimension of the Elements!
	 */
	public static int maxDegree(final Vector arg) {
		int Degree, max = 1;
		for(Iterator iter = arg.iterator(); iter.hasNext();) { //
			final Object item = iter.next(); //) != null) //Iterator.EOI)
			if (item instanceof Vector) //Problem: this doesn't work with Associations.
				if (max < (Degree = 1 + maxDegree((Vector) item)))
					max  = Degree;
		}
		return max; }
	
	/**Retrieves the next Item of a nested structure with optional Tag Names.
	 *
	 * This alternative Implementation is slightly more elegant,
	 * but doesn't read Elements ahead.
	 *
	public Object nextItem0() throws IOException { //If no Start Tag: return the current Item
		if (nextToken() != SCN_TAG_START) { return getResult(); }	//closing or delimiting Tag...
		String key = null;
		if (leadingKey) key = getResult(); //optionally accept a leading key
		else if (key.length() > 0) throw new AbstractMethodError();
		Vector lList = new Vector();	 //
		do {
			lList.addElement(nextItem0()); //recursively read the inner Items
		} while (currToken != SCN_TAG_STOP); //reads empty Elements on ,
		nextToken();
		if (trailingKey) {
			getResult();
			if (leadingKey) && (Result != key) && (!Result.equals(key))
				throw new AbstractMethodError();
			key = Result;
		}  //optionally accept a trailing key
		else if (Buffer.length() > 0) throw new AbstractMethodError();
		lList.remove(lList.size()-1);
		if ((key != null) && (key.length() > 0)) return new Association(key, lList);
		return lList; }
	*/
	
	//////////////////
	//	Testing		//
	//////////////////
	
	/**tests all Methods of this Class	 */
	public static void testIt() throws IOException {
		testPrimitives();
		testReadBag();
		testScanner();
		testReadRelation();
	}
	
	/**Separator String for Parsing with nn Grammar	 */
	final static public String STR_SEP_ROUND = "(),";
	
	/**Test String for Parsing 	 */
	private static final String tstReadRelation0 =
		"sin(alpha,beta,cos(ulme,exp(voyager,wolfram),xerxes),ypsilon)";
	
	/**Converted XML String without Attributes... */
	private static final
		String
		tstReadRelation1 =	"Icon(Test,Test2)Icon,Test3";//Icon(Test2)Icon";// +
	//						"Division(HiDiv)Division,Division(LoDiv)Division)Icon,";
	
	private static final
		String
		tstReadRelation2 =	"Icon(" +
							"Division(" +
								"Name(SOFTWARE,sdfg,)Name," + //strict Syntax, with trailing Delimiter
								"Established(1992,)Established," +
								"URL(http://www.icon-is.com/e/dev/sw/sw_main.asp,)URL," +
								"Manager(AFALK01,)Manager," +
								"Desc(Entwicklung von kundenspezifischen Softwarelösungen im technischen und wissenschaftlichen Bereich.,)Desc," +
								"Person(" +
									"ID(AFALK01,)ID," +
									"LastName(Falk)LastName," +
									"FirstName(Alexander)FirstName," +
									"PhoneExt(42,)PhoneExt," +
									"EMail(falk@icon.at,)EMail,)" +
								"Person," +
								"Person(ID(JLEGA01,)ID,LastName(Legat,)LastName,FirstName(Joachim,)FirstName,PhoneExt(54,)PhoneExt,EMail(legat@icon.at,)EMail,)Person,)Division," +
	//						"Division(Name(CD-ROM)Name,Established(1993)Established,URL(http://www.icon-is.com/d/dev/cd/cd_main.asp)URL,Manager(VGAVR01)Manager,Desc(Entwicklung von CD-ROM Datenbanken.)Desc,Person(ID(VGAVR01)ID,LastName(Gavrielov)LastName,FirstName(Vladislav)FirstName,PhoneExt(32)PhoneExt,EMail(gavrielov@icon.at)EMail,)Person,Person(ID(MPALL01)ID,LastName(Michael)LastName,FirstName(Pallinger)FirstName,PhoneExt(51)PhoneExt,EMail(pallinger@icon.at)EMail,)Person,)Division," +
	//						"Division(Name(HARDWARE)Name,Established(1994)Established,URL(http://www.icon-is.com/d/dev/hw/hw_main.asp)URL,Manager(TKEFE01)Manager,Desc(Entwicklung von kundenspezifischen mikroelektronischen Geräten.)Desc,Person(ID(TKEFE01)ID,LastName(Kefer)LastName,FirstName(Thomas)FirstName,Title(Dipl.-Ing.)Title,PhoneExt(41)PhoneExt,EMail(kefer@icon.at)EMail,)Person,)Division," +
	//						"Division(Name(ADMIN)Name,Desc(Buchhaltung und Sekretariat.)Desc,Person(ID(VAGGA01)ID,LastName(Aggarwal)LastName,FirstName(Veronika)FirstName,PhoneExt(21)PhoneExt,EMail(aggarwal@icon.at)EMail,)Person,)Division," +
						")Icon,)";
	
	/** Strings to parse with ascending Complexity  */
	private static final String[] tstReadRelation 
	= {tstReadRelation0, tstReadRelation1, tstReadRelation2}; 
	
	/**Tests the Item() Method,
	 * which doesn't ignore WhiteSpace and still collects it.   */
	public static void testReadRelation() throws IOException {
		StringBufferInputStream InS = new StringBufferInputStream(tstReadRelation[2]);
		Scanner Scannr = new Scanner(InS, STR_SEP_ROUND);
		Vector res = null;
		Scannr.strictItem = false;
		while (Scannr.currChar != IIStreamIn_Int.EOF) {
			res = (Vector) Scannr.readRelation(new Vector());
			cleanAll(res);
			FLATTEN_ALL(res);
			System.out.println(res.toString()); }
	}
	
	/**Test String for Parsing 	 */
	private static final String tstScanner =
		"a|b|c	1	/$/&/2	|u|v	3	4	";
	
	/**Test String for Parsing 	 */
	private static final String tstReadBag =
		"allah|b|c\n" +
		"\n" +	//generates empty Strings instead of Vectors, could be seen as Optimization
		"u|v\n" +
		"x|y|z\n" +
		"\n" +	//generates empty Strings instead of Vectors
		"g|h\n";
	
	/**Test String for Parsing 	 */
	private static final String[] testStrings = { tstScanner, tstReadBag};
	
	/**tests the Element() Method	 */
	public static void testReadBag() throws IOException {
		StringBufferInputStream InS = new StringBufferInputStream(tstReadBag);
		String Separators = "\n|";
		Scanner Scannr = new Scanner(InS, Separators); //"	|/");
	//	int Token;
		Vector res;
	//	Scannr.nextToken(); //reading the first Token is necessary for LL(1)
	//	res = Scannr.readBag(1); //) != Scanner.SCN_TAG_EOF)
		res = Scannr.readBag(0);
		res = Scannr.readBag(0);
		res = Scannr.readBag(0);
		res = Scannr.readBag(0);
		System.out.println(res);
	}
	
	/**tests all Methods of this Class	 */
	public static void testScanner() throws IOException {
		StringBufferInputStream InS = new StringBufferInputStream(testStrings[1]);
		String Separators = "\n|";
		Scanner Scannr = new Scanner(InS, Separators); //"	|/");
		while (Scannr.currToken != IIStreamIn_Int.EOF) {
			System.out.println(Scannr.getResult());
			System.out.println(Scannr.currToken);
			Scannr.nextToken(); }
	}
	
	//	The Scanner allows reading already Primitive Types
	
	
	///////////////////////////////////////////////////////////////////////////////
	//	now come the Parsing and their Testing Routines for primitive Types
	///////////////////////////////////////////////////////////////////////////////
	
	/**Reads Labels with consecutive Letters and Digits.
	 * Very simple Implementation due to the simple Alternative,
	 * not Automaton is necessary.
	 * The Optimization of having used the first Character
	 * to decide whether this is Label or not, is not exploited.	 */
	public String Label() throws IOException {
		while (IS_LETTER(currChar) || (IS_DIGIT(currChar))) 
			Character();
		return getResult(); }
	
	/**Reads an Integer Number with consecutive Digits and without Sign.
	 * Very simple Implementation due to the simple Alternative in the front,
	 * not Automaton is necessary.	 */
	public long Number() throws IOException {	//can I rely on the first Character being a Number? no!
		long currLong = 0; 	//Current Long Value. Intermediate Results
		while (IS_DIGIT(currChar)) {
			if (construct) 
				currLong = 10*currLong + (currChar-CHR_0); 
			Character(); }
		return currLong; }
	
	/**Reads Integers with consecutive Digits and optional Sign (extends Number).
	 * Very simple Implementation due to the simple Alternative in the front,
	 * not Automaton is necessary.	 */
	public long Integer() throws IOException	{
		final boolean negative = (!testChar(CHR_PLUS )) &&
						   ( testChar(CHR_MINUS));
		if (! IS_DIGIT(currChar)) 
			throw new AbstractMethodError(strDigit + strExpected);
		final long currLong = Number();	//Current Long Value. Intermediate Results
		if (construct && negative) 
			return -currLong;
		return currLong; }
	
	/**Checks for the Character and skips it.
	 * Throws an Exception, if it was not found.	 */
	public void checkChar(final char chk) throws IOException {
		if (currChar != chk) 
			throw new AbstractMethodError(chk + strExpected);
		Character(); }
	
	/**Tests for the Character and skips it, if found
	 * Returns true, if it was found.	 */
	public boolean testChar(final char chk) throws IOException	{
		if (currChar != chk) 
			return false;
		Character(); return true; }
	
	/**Tests for the Character converted into upper Case and skips it, if found
	 * Returns true, if it was found.	 */
	public boolean testUpperChar(final char chk) throws IOException {
		if (Character.toUpperCase((char) currChar) != chk) 
			return false;
		Character(); return true; }
	
	/**Reads a Hex Integer with consecutive Digits and without Sign.
	 * Very simple Implementation due to the simple Alternative in the front,
	 * not Automaton is necessary.	 */
	public long HexNumber() throws IOException {
		checkChar(CHR_$);
		long currLong = 0; 	//Current Long Value. Intermediate Results
		while (IS_HEX_DIGIT(currChar)) {	//Horner Scheme
			if (construct) currLong = (currLong << 4) +
									 ((currChar >= CHR_a)? //this Order assumes that
									  (currChar  - CHR_a + 10):
									 ((currChar >= CHR_A)? //CHR_a > CHR_A and
									  (currChar  - CHR_A + 10):
									  (currChar  - CHR_0))); //CHR_A > CHR_0
			Character();
		}
		return currLong; }
	
	/**Reads a Real Number with consecutive Digits and optional Sign and Exponent.
	 * Very simple Implementation due to the simple Alternative in the front,
	 * not Automaton is necessary.	 */
	public double Real() throws IOException {
		double currDouble = Integer(); 	//signed integer Part
		if (testChar(CHR_DOT)) { 	//Fractional Part
			int l = Buffer.length(); //cache the current Buffer Length
			long Frac = Number();
			if (construct && ((l = Buffer.length()-l) > 0))
				currDouble += Frac/Math.rint(Math.pow(10.0, l));
		}
		if (testChar(CHR_E) ||
			testChar(CHR_e) ) { 	//Fractional Part
			if (construct) currDouble *= Math.pow(10.0, Integer());
		}
		return currDouble; }
	
	/**Reads an Identifier consisting of consecutive Letters, Numbers and '_'	 */
	public String Identifier() throws IOException {
		if (! IS_LETTER(currChar)) 
			throw new AbstractMethodError("Letter expected");
		while(IS_LETTER(currChar) ||
			  IS_DIGIT (currChar) ||
			  (currChar == CHR__)) Character();
		return getResult(); }
	
	/**Reads consecutive Identifiers separated by '.' (CHR_DOT)
	 * used for Package.class	 */
	public String fullIdentifier() throws IOException {
		Identifier();
		while (testChar(CHR_DOT)) 
			Identifier();
		return getResult(); }
	
	/**Tests all Methods for parsing primitive Items in this Class	 */
	public static void testPrimitives() throws IOException {
		System.out.println("Testing Scanner:");
		String tmp = String.valueOf(Math.PI);
		Scanner P = new Scanner(new StringBufferInputStream(tmp), "");
		System.out.println("Soll: " + tmp + "	Ist: " + P.Real());
		tmp = String.valueOf(Math.PI * 1e-40);
		P = new Scanner(new StringBufferInputStream(tmp), "");
		System.out.println("Soll: " + tmp + "	Ist: " + P.Real());
		System.in.read(); System.in.read();
	}
	
}
