package streamIO.object.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import streamIO.IIStreamIn;
import streamIO.integer.IStreamIn_Byte;
import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;
import function.byref.ByRefInt;
import graphs.KeyValuePair;

//The Problem with Parsing is, that you may find out that you've read too far (LL(1))
//and the Result must be stored for the next Call, because you encountered a new Item.

/** This Class defines an Event Based Interface to SGML-like Streams
  * similar to the SAX Interface.
  * Callbacks are called on
  * -entering and exiting a  Tag
  * -reading an Attribute (in contrast to the SAX Interface)
  * 
  * @see XMLStreamIn uses this Class to parse an Input streamIO.
  *
  * The Protocol could be independent of the Grammar, like in yacc
  *
  * Although defaulting the Separator Characters to <\>, you can override them.
  *
  * <!-- docstate
  * tags: [code/xml_parsing, code/xml_streaming]
  * concepts: [XML Read/Write Stream Bridging]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class XMLScannerStreamIn
extends AFilterIn {

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
	
	/** XML significant Characters, they act as separators
	  * Their Sequence corresponds to the Counting of the XML Tags. 	 */
	final static public String XML_STR_SEPS = "<>='\"" + IStreamIn_Byte.WHITESPACE;// + "&" to be able to replace Entities!
	
	/** Indicates the end of the XML streamIO
	  * The Value corresponds to the Position in the String
	  * @see XML_STR_SEPS  */
	final static public int XML_TAG_EOF = InputStream2StreamIn.SCN_TAG_EOF;
	
	/** Indicates the start Tag of an XML Tag.
	  * The Name of the Element is returned
	  * The Value corresponds to the Position in the String
	  * @see XML_STR_SEPS  */
	final static public int XML_TAG_START = 0;
	
	/** Indicates the Stop Tag of an Element.
	  * The Name of the Element is returned
	  * The Value corresponds to the Position in the String
	  * @see XML_STR_SEPS  */
	final static public int XML_TAG_END = 1;
	
	/** Indicates the Separator for an Attribute Element.
	  * Name and Value are returned in an Pair Object
	  * The Value corresponds to the Position in the String
	  * @see XML_STR_SEPS  */
	final static public int XML_TAG_ATTRIBUTE = 2;
	
	/**Indicates an Attribute Element using Apostrophe,
	 * only used to check the Scanner	 */
	final static public int XML_TAG_APOSTROPH = 3;
	
	/**Indicates an Attribute Element using Quotes,
	 * only used to check the Scanner	 */
	final static public int XML_TAG_QUOTE = 4;
	
	/** Indicates an unknown XML Tag, the Value is returned	 */
	//final static public int XML_TAG_UNKNOWN = -2;
	
	/** Indicates a WhiteSpace XML Tag, the Value is returned	 */
	final static public int XML_TAG_WHITESPACE = 5;
	
	/**Indicates XML Text Data, the Value is returned	 */
	final static public int XML_TAG_CDATA = 6;
	
	//Extended Language Element Tokens
	
	/**Indicates a Processing Instruction <? ?>, the Value is returned	 */
	final static public int XML_TAG_PROCESS = 7;
	
	/**Indicates a Declaration <! >, the Value is returned	 */
	final static public int XML_TAG_DECLARE = 8;
	
	/**Indicates a Comment <!-- -->, the Value is returned
	 * The Comment can be treated as a normal Declare Statement	 */
	final static public int XML_TAG_COMMENT = 9;
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/**Switches between strict XML checking and more tolerant Parsing and fast Escaping	 */
	public boolean strictXML = false;
	
	/**Contains the current XML Token.
	 * public, so it can be queried by different Parsers.
	 * Could also be stored by the next Parser,
	 * but that would not coordinate competing Parsers or Components.  */
	protected ByRefInt currXMLToken = new ByRefInt();
	
	/**Contains the current XML Token.
	 * public, so it can be queried by different Parsers.
	 * Could also be stored by the next Parser,
	 * but that would not coordinate competing Parsers or Components.  */
	protected ByRefInt nextXMLToken = new ByRefInt(InputStream2StreamIn.SCN_TAG_PLAIN);
	
	/** Current String	*/
	protected StringBuffer Buffer;
	
	/**Name of the current Tag, needed for empty tags to remember the Name
	 * also indicates the State of the Parser: inside or outside a Tag, 	 */
	protected String TagName;
	
	/**Contains the Result of the last nextXmlToken Operation:
	 * either an Pair as with Attributes
	 * or a String as with Start and Stop Tags. 	 */
	//protected Object nextItem;
	
	/** Flag indicating that the current Tag is a Processing Instruction */
	protected boolean isProcessing;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/**Initializing Constructor	 */
	public XMLScannerStreamIn(final IIStreamIn _IS) {
		super(_IS); }
	
	/**Initializing Constructor	 */
	public XMLScannerStreamIn(final InputStream _IS) throws IOException {
		super( new InputStream2StreamIn(_IS, XML_STR_SEPS)); }
	
	/**Initializing Constructor	 */
	public XMLScannerStreamIn(final IStreamIn_Byte _IS) throws IOException {
		super( new InputStream2StreamIn(_IS, XML_STR_SEPS)); }
	
	////////////////////////////////////////////////////////////////////////////
	
	/** reads the next XML Token and holds it in the Result Object
	  * (prepares it from elementary Tokens)
	  * Alternative Implementation that saves the 'read' Variable
	  * by always reading ahead the next Token (more consistent).
	  * 
	  * Design Decisions:
	  * Works iteratively instead of recursively.
	  * Thus the while() Loop and 'continue' instead of 'return nextItem()'
	  * as well as 'break' instead of 'return currItem' */
	protected Object nextItemInternal() {
		int BLen;
		if (Buffer != null)
			Buffer.setLength(0); //((StreamIn) Enum).currItem(); //just clear the Buffer
		while (true) { //Recursion instead of Iteration with WhiteSpace is ineffective!
			if (nextXMLToken.Value == InputStream2StreamIn.SCN_TAG_PLAIN) {
				if ((Buffer != null) && (Buffer.length() == 1))
					  Buffer.setLength(0); //((StreamIn) Enum).currItem(); //just clear the Buffer
				currXMLToken.Value  = ((ByRefInt) in.nextItem()).Value; }else{
				currXMLToken.Value  = nextXMLToken.Value; //this cares for the LL(1) Cases!
				nextXMLToken.Value  = InputStream2StreamIn.SCN_TAG_PLAIN; }
			switch (currXMLToken.Value) {//
				case InputStream2StreamIn.SCN_TAG_EOF: return currXMLToken; //break;
				case XML_TAG_START:
					if (TagName != null)
						if (strictXML) //either raise an Error...
							throw new AbstractMethodError("Start Character encountered within a Tag!");
					if ((Buffer != null) && ((BLen = Buffer.length()) > 1)) { //CData before the Start Tag
						currItem = Buffer.substring(0, BLen-1); //clear the Buffer
						nextXMLToken.Value = currXMLToken.Value;
						currXMLToken.Value = XML_TAG_CDATA; return currXMLToken; } //break; //} //CData Section
					nextXMLToken.Value = ((ByRefInt) in.nextItem()).Value; //read the Tag Name
					Buffer = (StringBuffer) ((IStreamIn) in).currItem(); //unfortunately clears the Buffer
					currItem = Buffer.substring(1); //.toString();
					switch (Buffer.charAt(0)) {	//the Return Value also contains the Character
						case StreamOutXML.XML_CHR_TERM: //closing XML Tag
							currXMLToken.Value = XML_TAG_END; return currXMLToken; //break;
						case StreamOutXML.XML_CHR_PROCESS: //Processing Instructions can have Attributes!
							currXMLToken.Value = XML_TAG_PROCESS; return currXMLToken; //find the End Process Tag
						case StreamOutXML.XML_CHR_DECLARE:	//ignore all Stuff within
							currXMLToken.Value = XML_TAG_END; //search for the next Stop Character
							((IStreamIn) in).findNext(currXMLToken); // != ((ByRefInt) Enum.nextItem()).Value);
							currItem = ((String) currItem) + ((IStreamIn) in).currItem().toString(); //append all the Rest
							currXMLToken.Value = XML_TAG_DECLARE; return currXMLToken; //break; //find the End Declare Tag
						default:  //opening XML Tag...
							if (Buffer.charAt(0) != StreamOutXML.XML_CHR_OPEN) {
								currItem = TagName = Buffer.toString();
							} else {
								TagName = (String) currItem; }//
							currXMLToken.Value = XML_TAG_START; return currXMLToken; //break; //TODO: can be optimized to return only at the End of the Method...
					}
				case XML_TAG_END:
					if (TagName == null) { //not within a Tag,
						if (strictXML) //either raise an Error...
							throw new AbstractMethodError("Stop Character encountered outside of a Tag!");
						continue; } //...or ignore the Character and read on tolerantly
					currItem = TagName; TagName = null; //start reading Text
					if ((BLen = Buffer.length()) == 1) {
						Buffer.setLength(0); //remove the Stop Character
					} else if (BLen > 1) { //
						switch (Buffer.charAt(Buffer.length()-2)) { //next to last Character...
							case StreamOutXML.XML_CHR_PROCESS:
								if (!isProcessing) //not in Processing Instruction
									if (strictXML) //either raise an Error...
										throw new AbstractMethodError("Processing Instruction not properly closed"); //
								currXMLToken.Value = XML_TAG_PROCESS; return currXMLToken; //break;  //Empty Tag, the last Contents is the Name
							case StreamOutXML.XML_CHR_TERM:
								if (isProcessing) //not in Processing Instruction
									if (strictXML) //either raise an Error...
										throw new AbstractMethodError("Processing Instruction not properly closed"); //
								currXMLToken.Value = XML_TAG_END; return currXMLToken; //break;  //Empty Tag, the last Contents is the Name
							default: //CData Section...
						}
					}
					if (isProcessing) //not in Processing Instruction
						if (strictXML) //either raise an Error...
							throw new AbstractMethodError("Processing Instruction not properly closed"); //
					while (XML_TAG_START != (nextXMLToken.Value = ((ByRefInt) in.nextItem()).Value)); //TODO: use findNext() here search for the next Start Token!
					//this is even so tolerant to overread any special Characters in the Element, except for the Start Tag.
					Buffer = (StringBuffer) ((IStreamIn) in).currItem(); //by getting it only at the End, everything is appended!
					if (Buffer.length() > 0) { //return CData Section
						currItem = Buffer.toString(); //clear the Buffer
						currXMLToken.Value = XML_TAG_CDATA; return currXMLToken; }//break; } //Empty Tag, the last Contents is the Name
					continue; //the Text cannot contain any Separator!!!
				case XML_TAG_ATTRIBUTE: //Mixed Approach: rather than caching a complex State, I process the Grammar at the Attribute Level!
					if (TagName == null) { //Attribute outside of a Tag
						if (strictXML)
							throw new AbstractMethodError(); //either raise an Error,
						continue; } //or ignore the Item
					String Name = ((StringBuffer) ((IStreamIn) in).currItem()).toString(); //Name of the Attribute
	
					while  ( XML_TAG_WHITESPACE<= (currXMLToken.Value = ((ByRefInt) in.nextItem()).Value)) { //Standard XML can skip all Spaces //as long as no WhiteSpace is encountered...
						if ((XML_TAG_QUOTE     ==  currXMLToken.Value) || //Standard XML
							(XML_TAG_APOSTROPH ==  currXMLToken.Value)) break;  //a Quote or Apostroph follows
						if  (XML_STR_SEPS.indexOf (Buffer.charAt(Buffer.length()-2)) < 0) {  //found something meaningful before Starter,
							if (strictXML)
								throw new AbstractMethodError(); //no XML Standard!
							break; } }
					if ((XML_TAG_QUOTE     == currXMLToken.Value) || //Standard XML
						(XML_TAG_APOSTROPH == currXMLToken.Value)) { //search for the same Character at the End
						Buffer.setLength(0); //remove Quote Character...
						while (((ByRefInt) in.nextItem()).Value != currXMLToken.Value); //TODO: use nextItem() here!
					} else { //nonstandard XML or HTML, no Quotes
						if (strictXML)
							throw new AbstractMethodError();
						while ((currXMLToken.Value <  XML_TAG_WHITESPACE) && //search for next White Space
							   (currXMLToken.Value != XML_TAG_END)) { //or the first Closing Tag.
								currXMLToken.Value  = ((ByRefInt) in.nextItem()).Value; }
					}
					currItem = new KeyValuePair(Name, ((IStreamIn) in).currItem().toString());
					currXMLToken.Value = XML_TAG_ATTRIBUTE;
					return currXMLToken; //break;
		//		case XML_TAG_APOSTROPH:
		//		case XML_TAG_QUOTE:
				default: //Log.L.L("WhiteSpace...");
			} //switch
		}//while(true);
	} //return currXMLToken; } //
	
	/**Quickly skips the current XML Element and still watches for consistent Grammar
	 * Expects the current Element to be a start Tag.
	 *
	 * The Method findNext() is faster, but it doesn't track the Grammar!
	 */
	public void skipXMLElement() throws IOException {
		int Depth = 0;
		do { //doesn't care at all for the actual Element Contents
			if (currXMLToken.Value == XML_TAG_START) ++Depth;
			if (currXMLToken.Value == XML_TAG_END  ) --Depth;
				currXMLToken.Value  = ((ByRefInt) in.nextItem()).Value;
		} while (Depth > 0); }
	
	/**Test String for Parsing XML	 */
	final static public String tstXML =
			"<5.1. Pi=3.1415 e=2,7181 u=>4#0</5.1.>" +
		"<8. A1='1' />" + //completely empty Element
		"<1.>  0   </1.>" +
		"<2. A1='1' A2='2' >1</2.>" +
		"<3.> Text" +
			"<3.1.>2#0</3.1.>" +
			" more Text " +
			"<3.2.>2#1</3.2.>" +
			"just more Text " +
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
	
	/** Tests all Methods of this Class	*/
	public static void testIt() throws IOException {
		StringBufferInputStream IS = new StringBufferInputStream(tstXML);
		XMLScannerStreamIn Scannr = new XMLScannerStreamIn(IS);
		Scannr.strictXML = false;//true;
		int Token;
		while (InputStream2StreamIn.SCN_TAG_EOF != (Token = ((ByRefInt) Scannr.nextItem()).Value)) {
			switch (Token) { //formats the XML Stream as it is parsed...
				case XML_TAG_START:	    System.out.println("< "     + Scannr.currItem() + ">"); break;
				case XML_TAG_END:	    System.out.println("</"     + Scannr.currItem() + ">"); break;
				case XML_TAG_ATTRIBUTE: System.out.println("Attr:"  + Scannr.currItem()      ); break;
				case XML_TAG_CDATA:	    System.out.println("CData'" + Scannr.currItem() + "'"); break;
				case XML_TAG_WHITESPACE:System.out.println("WhiteSpace:"); 		  break;
	//			case XML_TAG_UNKNOWN:   System.out.println("Unknown:"); 		  break;
				default: System.out.println("Default:"); 		  break;
			}
	//		Scannr.Buffer.setLength(0);
		}
	}
	
}
