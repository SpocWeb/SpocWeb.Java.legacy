package streamIO.object.integer;

import graphs.KeyValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import streamIO.object.parser.StreamOutXML;
import stringOp.parser.IIStreamIn_Int;
import stringOp.parser.Scanner;

/** This Class defines an Event Based Interface to SGML like Streams
  * similar to the SAX Interface.
  * Callbacks are called on
  * -entering and exiting a  Tag
  * -reading an Attribute (in contrast to the SAX Interface)
  *
  * The Protocol could be independent of the Grammar, like in yacc
  *
  * Although defaulting the Separator Characters to <\>, you can override them.
  *
  * @see Scanner Object is being used to parse the streamIO.
  * @see XMLInputStream uses this Class
  * @deprecated
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:56:21Z
  * digest: 535c4192e0a395aaae2db4c55d6ca4e24c05beb3055564edf0819fd1551849f7
  * stale: false
  * tags: [code/parsing, code/xml]
  * concepts: [XML/HTML Parsing]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class XMLScanner {

////////////////////////////////////////////////////////////////////////////
//  static Constants and Members
////////////////////////////////////////////////////////////////////////////

//Significant Characters in Markup Language:

/**Error Message String Constant      */
final static public String STR_ERR_GRAMMAR_ERROR = "Error in Grammar: ";

//Escaping

/**Escape Character for avoiding the XML_TAG_START "<"
 * Don't need to escape ">" or "/", because it always directly follows "<"
 * Use chr(ReplaceCharAsc) for replacing the "<" in Strings	 */
final static public char REPLACE_CHAR_ASC = '\\';

//Helper Constants

/**Prefix f�r XML Strings	 */
final static public String XML_STR_PREFIX = "xml version=\"1.0\" encoding=\"ISO-8859-1\" ";

/**Message for Grammar Errors	 */
final static public String MSG_ERROR_GRAMMAR = "Error in the Grammar: expected: '";

//Tokens

/**XML significant Characters, they act as separators
 * Their Sequence corresponds to the Counting of the XML Tags. 	 */
final static public String XML_STR_SEPS = "<>='\"" + Scanner.WHITESPACE;

/**Indicates the end of the XML streamIO	 */
final static public int XML_TAG_EOF = IIStreamIn_Int.EOF;

/**Indicates the start Tag of a new Element, the Name of the Element is returned	 */
final static public int XML_TAG_START = Scanner.SCN_TAG_START;

/**Indicates the Stop Tag of an Element, the Name of the Element is returned	 */
final static public int XML_TAG_STOP = Scanner.SCN_TAG_STOP;

/**Indicates the Separator for an Attribute Element,
 * Name and Value are returned in an Pair Object	 */
final static public int XML_TAG_ATTRIBUTE = Scanner.SCN_TAG_ATTRIBUTE;

/**Indicates an Attribute Element using Apostrophe,
 * only used to check the Scanner	 */
final static public int XML_TAG_APOSTROPH = 3;

/**Indicates an Attribute Element using Quotes,
 * only used to check the Scanner	 */
final static public int XML_TAG_QUOTE = 4;

/**Indicates an unknown XML Tag, the Name is returned	 */
final static public int XML_TAG_WHITESPACE = 5;

/**Indicates XML Text Data, the Value is returned	 */
final static public int XML_TAG_TEXT = 6;

//Extended Language Element Tokens

/**Indicates a Processing Instruction <? ?>, the Value is returned	 */
final static public int XML_TAG_PROCESS = 6;

// TODO: LOGIC: XML_TAG_PROCESS above is defined as 6, the same value as XML_TAG_TEXT.
// Any code that distinguishes a Processing Instruction from Text Data by comparing
// currXMLToken against these two constants cannot actually tell them apart.
/**Indicates a Declaration <! >, the Value is returned	 */
final static public int XML_TAG_DECLARE = 7;

/**Indicates a Comment <!-- -->, the Value is returned
 * The Comment can be seen as a normal Declare Statement	 */
//final static public int XML_TAG_COMMENT = 8;

////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////

/**Switches between strict XML checking and more tolerant Parsing and fast Escaping	 */
public boolean strictXML = false;

/**Scanner being used for Parsing the streamIO	 */
private Scanner scn;

/**Contains the current XML Token, public,
 * so it can be queried by different Parsers.
 * Could also be stored by the next Parser, but that would not coordinate
 * competing Parsers or Components.  */
public int currXMLToken;

/**Name of the current Tag, needed for empty tags to remember the Name
 * also indicates the State of the Parser: inside or outside a Tag, 	 */
private String TagName;

/**Initializing Constructor	 */
public XMLScanner(InputStream IS) throws IOException {
	scn = new Scanner(IS, XML_STR_SEPS);
    nextXmlToken(); }

/**Contains the Result of the last nextXmlToken Operation:
 * either an Pair as with Attributes
 * or a String as with Start and Stop Tags. 	 */
public Object Result;

/**Flag indicating that the next Token has to be read
 * This saves some currToken = scn.Token() Commands,
 * but makes the Code slightly more complicated.  */
boolean read = false;

/**reads the next XML Token and holds it in the Result Object
 * (prepares it from elementary Tokens)	 */
/*public int nextXmlToken2() throws IOException {
while (true) { //Recursion instead of Iteration with WhiteSpace is ineffective!
	if (read) scn.nextToken(); read = true; //LL(1) Grammar
	switch (scn.currToken) {
		case Scanner.SCN_TAG_EOF: return currXMLToken = XML_TAG_EOF;
/*		case Scanner.SCN_TAG_SPACE: //Whitespace within
			if (TagName == null) throw new AbstractMethodError(); //outside a Tag this shouldn't happen, otherwise there is an Error in this Parser
			scn.clearString(); return nextXmlToken2();
*/ /*	case XML_TAG_START:
			if (TagName != null) throw new AbstractMethodError(); //already within a Tag!
			scn.clearString();
			char next = (char) scn.Character();
			boolean noEnd;
			if ( noEnd = (next != XML_CHR_TERM)) {
				if (next == XML_CHR_DECLARE) { while (scn.nextToken() != XML_TAG_END);	 return currXMLToken = XML_TAG_DECLARE; } //find the End Declare Tag
				if (next == XML_CHR_PROCESS) { while (scn.nextToken() != XML_TAG_END);
												  if (scn.prevChar!= XML_CHR_PROCESS)
													throw new AbstractMethodError(); return currXMLToken = XML_TAG_PROCESS    ; } //find the End Process Tag
			} else scn.clearString();  //Check for an End Tag
			scn.nextToken(); read = false; //read the Tag Name
			Result = TagName = scn.getResult();
			if (noEnd)	return currXMLToken = XML_TAG_START;
						return currXMLToken = XML_TAG_END;
		case XML_TAG_END:
			if (TagName == null) { //not within a Tag,
				if (strictXML) throw new AbstractMethodError(); //either raise an Error,
				return nextXmlToken2(); } //or ignore the Character and read on tolerantly
			Result = TagName; TagName = null; //start reading Text
			if (scn.prevChar == XML_CHR_TERM) return currXMLToken = XML_TAG_END; //Empty Tag, the last Contents is the Name
			scn.thisToken(XML_CHR_START); read = false; //LL(1) Grammar, already read!
			String Text = scn.getResult();
			if (Text.length() > 0) { Result = Text; return currXMLToken = XML_TAG_TEXT; }
			return nextXmlToken2(); //the Text cannot contain any Separator!!!
	case XML_TAG_ATTRIBUTE: //Mixed Approach: rather than caching a complex State, I process the Grammar!
			if (TagName == null) { //Attribute outside of a Tag
				if (strictXML) throw new AbstractMethodError(); //either raise an Error,
				return nextXmlToken2(); } //or ignore the Item
			String Name = scn.getResult(); //Name of the Attribute
			while  ((scn.nextToken()) >= XML_TAG_WHITESPACE) { //Standard XML can skip all Spaces
				if ((scn.currToken == XML_TAG_QUOTE    ) || //Standard XML
					(scn.currToken == XML_TAG_APOSTROPH)) break; //a Quote or Apostroph follows
				if (XML_STR_SEPS.indexOf(scn.prevChar) < 0) {  //found something meaningful before Starter,
					if (strictXML) throw new AbstractMethodError(); //no XML Standard!
					break; } }
			if ((scn.currToken == XML_TAG_QUOTE    ) || //Standard XML
				(scn.currToken == XML_TAG_APOSTROPH)) {
				 scn.thisToken(XML_STR_SEPS.charAt(scn.currToken)); //search for the same Character at the End
			} else { //nonstandard XML or HTML, no Quotes
				if (strictXML) throw new AbstractMethodError();
				while ((scn.currToken <  XML_TAG_WHITESPACE) && //search for next White Space
					   (scn.currToken != XML_TAG_END))//or the first Closing Tag.
					scn.nextToken();
				read = false; }
			Result = new Pair(Name, scn.getResult());
			return currXMLToken = XML_TAG_ATTRIBUTE;
//		case XML_TAG_APOSTROPH:
//		case XML_TAG_QUOTE:
//		default: //WhiteSpace...
	}
}}
*/

/**reads the next XML Token and holds it in the Result Object
 * (prepares it from elementary Tokens)
 * Alternative Implementation that saves the 'read' Variable
 * by always reading ahead the next Token (more consistent).	 */
public int nextXmlToken() throws IOException {
while (true) { //Recursion instead of Iteration with WhiteSpace is ineffective!
	switch (scn.currToken) {
		case IIStreamIn_Int.EOF: scn.nextToken(); return currXMLToken = XML_TAG_EOF;
/*		case Scanner.SCN_TAG_SPACE: //Whitespace within
			if (TagName == null) throw new AbstractMethodError(); //outside a Tag this shouldn't happen, otherwise there is an Error in this Parser
			scn.clearString(); return nextXmlToken();
*/		case XML_TAG_START:
			if (TagName != null)
				throw new AbstractMethodError(); //already within a Tag!
			scn.clearString(); //prepare reading the Tag Name
			char next = (char) scn.Character(); //
			boolean noEnd; //see if it is a Stop Tag or Declaration or Process Flag
			if ( noEnd = (next != StreamOutXML.XML_CHR_TERM)) { //ignore all Stuff within
				if (next == StreamOutXML.XML_CHR_DECLARE) { while (scn.nextToken() != XML_TAG_STOP); scn.nextToken(); return currXMLToken = XML_TAG_DECLARE; } //find the End Declare Tag
				if (next == StreamOutXML.XML_CHR_PROCESS) { while (scn.nextToken() != XML_TAG_STOP);
												  			if (scn.prevChar    != StreamOutXML.XML_CHR_PROCESS)
																 throw new AbstractMethodError(); scn.nextToken(); return currXMLToken = XML_TAG_PROCESS; } //find the End Process Tag
			} else scn.clearString();  //Check for an End Tag
			scn.nextToken(); //read the Tag Name
			Result = TagName = scn.getResult();
			if (noEnd)	return currXMLToken = XML_TAG_START;
						return currXMLToken = XML_TAG_STOP ;
		case XML_TAG_STOP:
			if (TagName == null) { //not within a Tag,
				if (strictXML)
					throw new AbstractMethodError(); //either raise an Error,
				return nextXmlToken(); } //or ignore the Character and read on tolerantly
			Result = TagName; TagName = null; //start reading Text
			if (scn.prevChar == StreamOutXML.XML_CHR_TERM) { scn.nextToken(); return currXMLToken = XML_TAG_STOP; } //Empty Tag, the last Contents is the Name
			scn.thisToken(StreamOutXML.XML_CHR_OPEN); //LL(1) Grammar, already read!
			String Text = scn.getResult();
			if (Text.length() > 0) { Result = Text; return currXMLToken = XML_TAG_TEXT; }
			return nextXmlToken(); //the Text cannot contain any Separator!!!
		case XML_TAG_ATTRIBUTE: //Mixed Approach: rather than caching a complex State, I process the Grammar!
			if (TagName == null) { //Attribute outside of a Tag
				if (strictXML)
					throw new AbstractMethodError(); //either raise an Error,
				return nextXmlToken(); } //or ignore the Item
			String Name = scn.getResult(); //Name of the Attribute
			while  ((scn.nextToken()) >= XML_TAG_WHITESPACE) { //Standard XML can skip all Spaces
				if ((scn.currToken == XML_TAG_QUOTE    ) || //Standard XML
					(scn.currToken == XML_TAG_APOSTROPH)) break; //a Quote or Apostroph follows
				if (XML_STR_SEPS.indexOf(scn.prevChar) < 0) {  //found something meaningful before Starter,
					if (strictXML)
						throw new AbstractMethodError(); //no XML Standard!
					break; } }
			if ((scn.currToken == XML_TAG_QUOTE    ) || //Standard XML
				(scn.currToken == XML_TAG_APOSTROPH)) {
				 scn.thisToken(XML_STR_SEPS.charAt(scn.currToken)); //search for the same Character at the End
				 scn.nextToken();
			} else { //nonstandard XML or HTML, no Quotes
				if (strictXML)
					throw new AbstractMethodError();
				while ((scn.currToken <  XML_TAG_WHITESPACE) && //search for next White Space
					   (scn.currToken != XML_TAG_STOP))//or the first Closing Tag.
					scn.nextToken(); }
			Result = new KeyValuePair(Name, scn.getResult());
			return currXMLToken = XML_TAG_ATTRIBUTE;
//		case XML_TAG_APOSTROPH:
//		case XML_TAG_QUOTE:
		default: //WhiteSpace...
	}
	scn.nextToken();
}}

/**Quickly skips the current XML Element and still watches for consistent Grammar
 * Expects the current Element to be a start Tag.
 */
public void skipXMLElement() throws IOException {
	int Depth = 0;
	do {
		if (scn.currToken == XML_TAG_START) ++Depth;
		if (scn.currToken == XML_TAG_STOP ) --Depth;
			scn.nextToken();
	} while (Depth > 0); }

/**Test String for Parsing XML	 */
final static public String tstXML =
	"<8. A1='1' />" + //completely empty Element
	"<1.>  0   </1.>" +
	"<2. A1='1' A2='2' >1</2.>" +
	"<3.> Text" +
		"<3.1.>2#0</3.1.>" +
		" more Text " +
		"<3.2.>2#1</3.2.>" +
		"<3.3.>2#2</3.3.>" +
		"<3.4.>2#3</3.4.>" +
		" even more text! " +
	"</3.>" +
	"<4. A1=\"1</><'<>\" A2='\"2\"' />" + //empty Element
	"<5. Number=6>" +
		"<5.1. Pi=3.1415 e=2,7181 u=>4#0</5.1.>" +
		"<5.2.>4#1</5.2.>" +
		"<5.3.>" +
			"<5.3.1.>4#2#0</5.3.1.>" +
			"<5.3.2.>4#2#1</5.3.2.>" +
			"<5.3.3.>4#2#2</5.3.3.>" +
		"</5.3.>" +
		"<5.4.>4#3</5.4.>" +
	"</5.>" +
	"<6.>" +
		"<6.1.>5#0</6.1.>" +
		"<6.2.>5#1</6.2.>" +
		"<6.3.>5#2</6.3.>" +
	"</6.>" +
	"<7.>6</7.>";

/**Test String for Parsing XML	 */
final static public String tstXML2 =
"<Icon>" + //needs a Space or so in front to trick the Scanner
"	<!--Icon Organisationsdiagramm - XML Beispiel Dokument-->" +
"	<Division ProfitCenter=\"yes\">" +
"	<?fdgh version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
"		<Name>SOFTWARE</Name>" +
"		<Established>1992</Established>" +
"		<URL>http://www.icon-is.com/e/dev/sw/sw_main.asp</URL>" +
"		<Manager>AFALK01</Manager>" +
"		<Desc>Entwicklung von kundenspezifischen Softwarel�sungen im technischen und wissenschaftlichen Bereich.</Desc>" +
"		<Person Manager='yes' Programmer='yes' Designer=\"no\">" +
"			<ID>AFALK01</ID>" +
"			<LastName>Falk</LastName>" +
"			<FirstName>Alexander</FirstName>" +
"			<Title>Dipl.-Ing.</Title>" +
"			<PhoneExt>42</PhoneExt>" +
"			<EMail>falk@icon.at</EMail>" +
"		</Person>" +
"		<Person Manager=\"no\" Programmer='yes' Designer='yes'>" +
"			<ID>JLEGA01</ID>" +
"			<LastName>Legat</LastName>" +
"			<FirstName>Joachim</FirstName>" +
"			<Title>Ing.</Title>" +
"			<PhoneExt>54</PhoneExt>" +
"			<EMail>legat@icon.at</EMail>" +
"		</Person>" +
"	</Division>" +
"	<Division ProfitCenter='yes'>" +
"		<Name>CD-ROM</Name>" +
"		<Established>1993</Established>" +
"		<URL>http://www.icon-is.com/d/dev/cd/cd_main.asp</URL>" +
"		<Manager>VGAVR01</Manager>" +
"		<Desc>Entwicklung von CD-ROM Datenbanken.</Desc>" +
"		<Person Manager='yes' Programmer='yes' Designer='no'>" +
"			<ID>VGAVR01</ID>" +
"			<LastName>Gavrielov</LastName>" +
"			<FirstName>Vladislav</FirstName>" +
"			<PhoneExt>32</PhoneExt>" +
"			<EMail>gavrielov@icon.at</EMail>" +
"		</Person>" +
"		<Person Manager='no' Programmer='yes' Designer='no'>" +
"			<ID>MPALL01</ID>" +
"			<LastName>Michael</LastName>" +
"			<FirstName>Pallinger</FirstName>" +
"			<PhoneExt>51</PhoneExt>" +
"			<EMail>pallinger@icon.at</EMail>" +
"		</Person>" +
"	</Division>" +
"	<Division ProfitCenter='yes'>" +
"		<Name>HARDWARE</Name>" +
"		<Established>1994</Established>" +
"		<URL>http://www.icon-is.com/d/dev/hw/hw_main.asp</URL>" +
"		<Manager>TKEFE01</Manager>" +
"		<Desc>Entwicklung von kundenspezifischen mikroelektronischen Ger�ten.</Desc>" +
"		<Person Manager='yes' Programmer='yes' Designer='no'>" +
"			<ID>TKEFE01</ID>" +
"			<LastName>Kefer</LastName>" +
"			<FirstName>Thomas</FirstName>" +
"			<Title>Dipl.-Ing.</Title>" +
"			<PhoneExt>41</PhoneExt>" +
"			<EMail>kefer@icon.at</EMail>" +
"		</Person>" +
"	</Division>" +
"	<Division ProfitCenter='no'>" +
"		<Name>ADMIN</Name>" +
"		<Desc>Buchhaltung und Sekretariat.</Desc>" +
"		<Person Manager='no' Programmer='no' Designer='no'>" +
"			<ID>VAGGA01</ID>" +
"			<LastName>Aggarwal</LastName>" +
"			<FirstName>Veronika</FirstName>" +
"			<PhoneExt>21</PhoneExt>" +
"			<EMail>aggarwal@icon.at</EMail>" +
"		</Person>" +
"	</Division>" +
"</Icon>";

/**Tests this Scanner by printing all Tokens found while parsing {@link #tstXML}.	 */
public static void testIt() throws IOException {
	StringBufferInputStream IS = new StringBufferInputStream(tstXML);
	XMLScanner Scannr = new XMLScanner(IS);
	Scannr.strictXML = false;//true;
	do {
		switch (Scannr.currXMLToken) {
			case XML_TAG_START:		System.out.println("< " + Scannr.Result.toString() + ">"); break;
			case XML_TAG_STOP:		System.out.println("</" + Scannr.Result.toString() + ">"); break;
			case XML_TAG_ATTRIBUTE: System.out.println(		  Scannr.Result.toString()		); break;
			case XML_TAG_TEXT:		System.out.println(		  Scannr.Result.toString()		); break;
			case XML_TAG_WHITESPACE: break;
			default: }
	} while (Scannr.nextXmlToken() != IIStreamIn_Int.EOF);
}

}
