package streamIO.object.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.security.InvalidParameterException;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import streamIO.IIStreamOut;
import streamIO.integer.AStreamOutByte;
import streamIO.integer.AStreamOutStruct;
import streamIO.integer.IStreamOutByte;
import streamIO.integer.IStreamOutPrimitive;
import streamIO.integer.IStreamOutStruct;
import streamIO.integer.IStreamWriteAble;
import streamIO.integer.adapter.OutputStreamToStreamOutByte;
import streamIO.integer.adapter.WriterToStreamOutByte;
import streamIO.integer.encoding.FilterChar2String;
import stringOp.parser.Scanner;
import synch.ValidationRule;
import tools.IOError;
import function.string.Char2String;
import function.string.StringFunction;

/**
  * Helper Class for writing XML Data to a Character Output streamIO (e.g. a 'Writer').
  * Encoding to a Byte streamIO must happen afterwards (e.g. using a Filter). 
  * Implements the SAX ContentHandler Interface to directly generate XML. 
  * 
  * It enforces correct Grammar
  * and escapes the special Characters '<', '&', ' and " (for Attributes) 
  * by encoding it into Entities and thus shields the Output streamIO! 
  * Alternatively CDATA Sections can be used, but these are expensive and also unreadable
  * and cannot be used in Attribute Values either. 
  * The Delimiter for Attributes is chosen automatically 
  * depending on the Contents of the String, minimizing the Amount of Encoding. 
  * 
  * Does not validate Document Type Definitions (DTD) or XML Schemas!
  * TODO: implement Validation and check for invalid Characters in Element and Attribute Names.
  *
  * Design Decisions:
  * @see XMLFormatter analyzes an Object structure and uses this class
  * to ease the Writing.
  *
  * Didn't extend the class
  * @see java.io.PrintStream because that would allow mixing non XML Contents
  * 
  * Anstatt die Daten in (unlesbares und uneditierbares) UTF-8 zu encoden, 
  * kann man alle Zeichen > 127 als XML Entit�ten schreiben, aber nur in XML! 
  * alternativ k�nnen Umlaute auch k�rzer als ss, ue, ae und oe geschrieben werden. 
  * 
  * folgende Elemente M�SSEN beim Schreiben von XML encoded werden: 
  * &lt; wegen Element Starts und 
  * & wegen der Entit�ten, sowie
  * ' oder " wegen der Attribute (nur dort)
  *   
  * Encoding of other Unicode Characters can either be performed in one Step
  * by choosing a sufficiently complete Entity Set in this Writer.
  * 
  * or in a second Step using a Character Filter plugged AFTER this Writer like: 
  * @see streamIO.Byte.Encoding.FilterChar2String for Characters between 128 and 256 (LATIN-1)
  * @see streamIO.Byte.Encoding.FilterChar2Entity for all Characters larger than a given Number (e.g. 128)
  * 
  * but it would be faster if Escaping and Encoding would be performed in one Step. 
  * 
  * @see com.megginson.sax.StreamOutXML which provides a similar Functionality, 
  * but triggered by the SAX Events. 
  * <!-- docstate
  * tags: [code/xml_parsing, code/xml_streaming]
  * concepts: [XML Read/Write Stream Bridging]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class StreamOutXML 
extends AStreamOutStruct //StreamOutPrimitive  
implements ContentHandler, IStreamOutPrimitive, IStreamOutStruct //, ErrorHandler //not able to handle Errors, except closing the Stream.
{
	 
	////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////
	
	/** Start Character of an XML Tag
	  * Only this Character always has to be escaped.	 */
	final static public char XML_CHR_OPEN = '<';
	
	/** Stop Character of an XML Tag	 */
	final static public char XML_CHR_CLOSE = '>';
	
	/** Termination Character of an XML Tag	 */
	final static public char XML_CHR_TERM = '/';
	
	/** Separator Character of an XML Attribute	 */
	final static public char XML_CHR_ATTR_SEP = '=';
	
	/** Escape Character of an XML Tag	 */
	final static public char XML_CHR_SPACE = ' ';
	
	//Escaping
	
	/** XML standard Markup characters and their required Escape
	  * Strings in XML Ampersand Escape Sequence (Entities) &...;
	  */
	final static public char  [] XML_ARR_ESCAPE_CHARS_ALWAYS = {StringFunction.XML_ENTITY_START, XML_CHR_OPEN}; //, XML_CHR_TERM};
	
	/** XML standard Markup characters and their required Escape
	  * Characters in XML Ampersand Escape Sequence (Entities) &...;
	  */
	final static public String[] XML_ARR_ESCAPE_STRINGS_ALWAYS = {"&amp", "&lt"};
	
	/** XML standard Markup characters and their Escape
	  * Strings in XML Ampersand Escape Sequence (Entities) &...;
	  */
	final static public char  [] XML_ARR_ESCAPE_CHARS = {StringFunction.XML_ENTITY_START, XML_CHR_OPEN, XML_CHR_CLOSE, StringFunction.XML_CHR_APOSTROPH, StringFunction.CHR_QUOTE}; //, XML_CHR_TERM};
	
	/** XML standard Markup characters and their Escape
	  * Characters in XML Ampersand Escape Sequence (Entities) &...;
	  */
	final static public String[] XML_ARR_ESCAPE_STRINGS = {"&amp;", "&lt;", "&gt;", "&apos;", "&quot;"};
	
	//Processing Instructions
	
	/** Processing Instruction Character of an XML Tag	 */
	final static public char XML_CHR_PROCESS = '?';
	
	/** Start Character of an XML Declaration (DTD etc.)	 */
	final static public char XML_CHR_DECLARE = '!';
	
	/** Start String of an XML Comment	 */
	final static public String XML_STR_COMMENT = "--";
	
	/** Start String of an XML CDATA Section	 */
	final static public String XML_STR_CDATA_START = "[CDATA["; 
	
	/** End String of an XML CDATA Section	 */
	final static public String XML_STR_CDATA_END   = "]]"; 
	
	//Defaults and significant Attributes for Client Use
	
	/** Tag for an XML String	 */
	final static public String XML_TAG_XML = "xml";
	
	/** Version of an XML String	 */
	final static public String XML_ATTR_VERSION = "version";
	
	/** Encoding of an XML String	 */
	final static public String XML_ATTR_ENCODING = "encoding";
	
	/** Default Version of an XML String	 */
	public static String XML_ATTR_Version = "1.0";
	
	/** Default Encoding of an XML String	 */
	final static public String XML_ATTR_Encoding = "UTF-8";
	
	/** ID of an XML Element	 */
	final static public String XML_ATTR_ID = "ID";
	
	////////////////////////////////////////////////////////////////////////////
	//  static Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Error Message for Attributes outside of an Element 	 */
	public static String ERR_ALLOWED_OUTSIDE = "Attributes are not allowed outside of an Element!";
	
	/** Error Message for Attributes outside of an Element 	 */
	public static String ERR_CLOSE_PROCESSING = "Close Processing Instructions immediately!";
	
	/** Error Message for CData inside of an Element 	 */
//	public static String ERR_ALLOWED_INSIDE = "CData is not allowed inside of an Element!";
	
	/** Default Stack Size for an XML Writer
	  * Two, because XML will mostly be used to model relational Data
	  */
	public static int INITIAL_STACK_SIZE = 3;
	
	/** Default for the Flag to use double Quotes for Attributes  */
	public static boolean DBL_QUOTES_FOR_ATTRIBUTES_DEFAULT = false;
	
	////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Flag to use double Quotes for Attributes in this Writer Instance */
	public boolean dblQuotesForAttributes = DBL_QUOTES_FOR_ATTRIBUTES_DEFAULT;
	
	/** Flag whether an Element was closed after opening, so the next closing Tag can be indented	 */ 
	protected String startedTag; // = false; 
	
	/** Flag to indicate whether in a Tag or in an Element	*/
	protected boolean inTag; // = false; //unnecessary
	
	/** Flag to indicate whether in an Attribute	*/
	protected boolean inAttribute;
	
	/** Flag to switch between single and double quoted Attribute Contents	*/
	public boolean dblQuote; 
	
	/** Flag to indicate whether in a Processing Instruction or not	*/
	protected boolean isProcessing; // = false; //unnecessary
	
	/** Stack of the currently open Tags
	  * Empty when currently not in a Tag */
	protected String[] tagName = new String[INITIAL_STACK_SIZE];
	
	/** Reference to the Writer to print to with Encoding switched on 
	 * for Attribute Values and Text Nodes 
	 * Actually two Encoders or chained Filters could be used 
	 * to switch between encoding single or double Quotes
	 * and only encoding lt and amp.  */
	protected IStreamOutByte encoder;
	
	/** 
	 * Reference to the Mapper to map the Characters to Entities. 
	 * for modifying the mapping  */
	protected Char2String mapper;
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param out_
	 * @param _separator */
	//public XMLWriter(final PrintStream out_, final Object _separator) { super(out_, _separator); }
	
	/** Initializing Constructor 	
	 * @param streamOut_ the streamIO to write the XML to
	 * @param encodeLatin1 decides whether only the XML Start and Entity Characters 
	 * or all Latin-1 Characters larger than 127 are encoded.  */
	public StreamOutXML(final Writer streamOut_, final boolean encodeLatin1) { 
		this(new WriterToStreamOutByte(streamOut_), encodeLatin1); }
	
	/** Initializing Constructor 
	 * @param streamOut_ the streamIO to write the XML to
	 * @param encodeLatin1 decides whether only the XML Start and Entity Characters 
	 * or all Latin-1 Characters larger than 127 are encoded.  */
	public StreamOutXML(final IStreamOutByte streamOut_, final boolean encodeLatin1) { 
		this(streamOut_, new Char2String(encodeLatin1 
		? StringFunction.GET_ARR_XML_ENTITY_STRINGS_INVERSE()
		: StringFunction.GET_ARR_LATIN1_ENTITY_STRINGS_INVERSE()
		)); }
	
	/** Initializing Constructor 
	 * @param streamOut_ the streamIO to write the XML to
	 * @param encodeLatin1 decides whether only the XML Start and Entity Characters 
	 * or all Latin-1 Characters larger than 127 are encoded.  */
	public StreamOutXML(final OutputStream streamOut_, final boolean encodeLatin1) { 
		this(new OutputStreamToStreamOutByte(streamOut_), encodeLatin1); } 
	
	/** Initializing Constructor 	
	 * @param streamOut_ the streamIO to write the XML to
	 * only the XML Start and Entity Characters are encoded.  */
	public StreamOutXML(final Writer streamOut_) { this(streamOut_, false); }

	/** Initializing Constructor 	
	 * @param streamOut_ the streamIO to write the XML to
	 * only the XML Start and Entity Characters are encoded.  */
	public StreamOutXML(final IStreamOutByte streamOut_) { this( streamOut_, false); }

	/** Initializing Constructor, 
	 * since the Mapper is being modified, it is made private. 	*/
	protected StreamOutXML(final Writer streamOut_, final Char2String mapper_ ) 
	{ this(new WriterToStreamOutByte(streamOut_), mapper_); }

	/** Initializing Constructor, 
	 * since the Mapper is being modified, it is made private. 	*/
	protected StreamOutXML(final IStreamOutByte _streamOut, final Char2String _mapper) {
		super(_streamOut); 
		//chrCol = ' '; //use white Space to separate Items
		this.mapper = _mapper; 
		this.encoder = new FilterChar2String
		( _streamOut, _mapper
		, StringFunction.XML_ENTITY_START
		, StringFunction.XML_ENTITY_STOP); 
		mapper.setEncoding(StringFunction.XML_CHR_APOSTROPH, null); 
		mapper.setEncoding(StringFunction.CHR_QUOTE    , null); 
	}

	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** Writes a Start Tag with the given Name
	  * @return the streamIO to append more Items
	  * Example:
	  * <Name
	  */
	public StreamOutXML startTag(final String Name) throws IOException {
		return startTag(Name, false); }

	/** Writes the given String as an XML Processing Instruction to the streamIO
	  * @return the streamIO to append more Items
	  * only allowed outside of a Tag and inside an Element.
	  * Example:
	  * <?xml version="1.0" encoding="UTF-8" ?>
	  * The Attributes are only pseudo Attributes according to the W3C, 
	  * the data is normally handed over as Test and not parsed. 
	  */
	public StreamOutXML processing (final String target) throws IOException {
		return startTag(target, true); 
	}
	
	/** opens up a Tag	 */
	protected void openTag(final char prefix, final String name) throws IOException {
		closeTag(); 
		/*if (inTag) {
			endAttribute();
			streamByte.write(XML_CHR_CLOSE);
		} else */
		inTag = true; 
		if (!ValidationRule.EQUALS(startedTag, name))
			indent(1);
		streamByte.write(XML_CHR_OPEN); 
		if (prefix > 0)
			streamByte.write(prefix); 
		AStreamOutByte.WRITE(streamByte, name); //no Encoding here!!!
	}
	
	/** Closes the current Tag 
	  * @return the streamIO to append more Items
	  */
	public StreamOutXML closeTag() throws IOException {
		if (!inTag) 
			return this; 
		endAttribute();
		streamByte.write(XML_CHR_CLOSE);
		inTag = false; 
		return this; }
	
	/** Writes a Start Tag with the given Name
	  * @return the streamIO to append more Items
	  * @param Processing determines whether a Processing Instruction is written
	  * instead of a regular Element.
	  * Example:
	  * <Name
	  */
	protected StreamOutXML startTag(final String name, final boolean processing) throws IOException {
		if (name.length() <= 0) {
			throw new InvalidParameterException(""); }
		openTag((isProcessing = processing) ? XML_CHR_PROCESS : 0, tagName[SP] = name); 
		if (!processing) { //ArrayList Behavior
			startedTag = name; 
			int length;
			if (++SP >= (length = tagName.length)) {
				String[] tmp = new String[length << 1];
				System.arraycopy(tagName, 0, tmp, 0, length);
				tagName = tmp; }
		}
		return this; }

	/** Closes all open Tags and the streamIO at latest,
	  * when this Class is being garbage collected */
	public void close() throws IOException {
		endAll();
		super.close();
	}

	/** Writes Closing Tags with the cached Names until all open Tags are closed.
	  * @return the streamIO to append more Items
	  */
	public StreamOutXML endAll() throws IOException {
		while(SP > 0) 
			endTag(); 
		return this; 
	}

	/** Writes an End Tag or closes the current Element or Processing Instruction Tag 
	  * with the current Name. 
	  * Necessary, because it is not possible to tell whether 
	  * to close the previous Element or 
	  * to leave it open. 
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  * Examples:
	  * />
	  * </Name>
	  * ?>
	  */
	public StreamOutXML endTag() throws IOException { return endTag(null); }
	
	/** Writes a Closing or closes the current Element or Processing Instruction Tag 
	  * with the current Name. 
	  * Necessary, because it is not possible to tell whether 
	  * to close the previous Element or 
	  * to leave it open. 
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  * Examples:
	  * />
	  * </Name>
	  * ?>
	  */
	public StreamOutXML endTag(final String tag) throws IOException {
		endAttribute();
		final String stackTag = tagName[--SP]; 
		if (tag != null) {
			if(!tag.equals(stackTag)) {
				throw new RuntimeException("Expected on Closing: '"+tag+"' actual '"+stackTag+"'"); }
		}
		if (inTag){ //simply close the open Tag
			if (isProcessing) 
				throw new AbstractMethodError(ERR_CLOSE_PROCESSING); 
			streamByte.write(XML_CHR_TERM);
		} else { //Start a closing Tag
			openTag(isProcessing ? XML_CHR_PROCESS : XML_CHR_TERM, stackTag); 
		}
		//closeTag(); 
		streamByte.write(XML_CHR_CLOSE);
		inTag = isProcessing = false;
		startedTag = null; 
		return this; }
	
	/** Writes the given Character Data as is to the streamIO
	  * only allowed outside of a Tag and inside an Element.
	  * @return the streamIO to append more Items
	  *
	  * @todo: replace all Special Characters by Entities,
	  * because otherwise they will either be parsed (like '<', '&', ' and ").
	  * Especially Entites larger than 127 are written as Character Entities
	  * otherwise they have to be UTF-8 encoded when larger than 0x80:
	  * either decimal &#ddd;
	  * or hexadecimal &#xXXXX;
	  * using XXXX as their Unicode in Hex Numbers starting with the MSB,
	  * e.g. as described in "IE5 XML" p.57 and "XML Applications" p.57
	  *
	  * Alternatively a <![CDATA[...]]> Section can be used...
	  */
	public StreamOutXML text(final String text) throws IOException {
		if (inTag) //Tag is automatically closed !
			endTag(); 
		AStreamOutByte.WRITE(encoder, text); //has to be encoded!
		return this; }

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Writes the given String as an XML Declaration to the streamIO
	  * @return the streamIO to append more Items
	  * only allowed outside of a Tag and inside an Element.
	  * Example:
	  * Declaration("DOCTYPE myDoc SYSTEM 'xxx.dtd'");
	  * leads to the following Output:
	  * <!DOCTYPE myDoc SYSTEM "xxx.dtd">
	  */
	public StreamOutXML declaration(final String declaration) throws IOException {
		openTag(XML_CHR_DECLARE, declaration);
		streamByte.write(XML_CHR_CLOSE);
		//AStreamOutByte.WRITE(streamByte, STR_CRLF); //pretty printing with CR/LF
		streamByte.write(chrRow); //pretty printing with CR/LF
		return this; }

	/** Writes the given Comment to the streamIO
	  * @return the streamIO to append more Items
	  * Example:
	  * <!-- Comment -->
	  * In the Comment Text there must not be any Sequence '--'
	  * but such two Character Sequences are considered improbable here! 
	  */
	public StreamOutXML comment(final String comment) throws IOException {
		return declaration(XML_STR_COMMENT+comment+XML_STR_COMMENT); }

	/** Writes the given Character Data as a CDATA Section to the streamIO
	  * @return the streamIO to append more Items
	  * In the CDATA Text there must not be any Sequence ']]>'
	  * but such three Character Sequences are considered improbable here! 
	  */
	public StreamOutXML cData(final String CData) throws IOException {
		return declaration(XML_STR_CDATA_START+CData+XML_STR_CDATA_END); }

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// writing Attributes...
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  */
	//public XMLWriter attribute(final String name, final String value) throws IOException {
	//	return attribute(name, value, value.indexOf(StringFunction.XML_CHR_QUOTE) < 0); }
	
	/** Writes a primitive Attribute with the given Name
	 * @param name the Name of the Attribute to write
	 * @return the streamIO to append more Items
	 * only allowed inside a Start Tag */
	public StreamOutXML attribute(final String name, final String value) throws IOException {
		return attribute(name, value, value.indexOf(Scanner.CHR_QUOTE) > 0); }
	
	/** Writes a primitive Attribute with the given Name
	 * @param name the Name of the Attribute to write
	 * @return the streamIO to append more Items
	 * only allowed inside a Start Tag */
	public StreamOutXML attribute(final String name, final String value, final boolean _dblQuote) throws IOException {
		endAttribute();
		startAttribute(name, _dblQuote);
		AStreamOutByte.WRITE(encoder, value);
		endAttribute();
		return this; }
	
	/** Starts an Attribute using this Instance's current default Quote Style.
	 * @param name the Name of the Attribute to write
	 * @param dblQuote
	 * @return the Separator Character used
	 * @throws AbstractMethodError
	 * @throws IOException */
	public StreamOutXML startAttribute(final String name) throws AbstractMethodError, IOException {
		return startAttribute(name, dblQuote); }

	/** Writes the Attribute Name, Separator and opening Quote Character, and switches the
	  * Encoder to escape that Quote Character within the Value that follows.
	 * @param name the Name of the Attribute to write
	 * @param dblQuote
	 * @return the Separator Character used
	 * @throws AbstractMethodError
	 * @throws IOException */
	public StreamOutXML startAttribute(final String _name, final boolean _dblQuote) throws IOException {
		if (!inTag) 
			throw new RuntimeException(ERR_ALLOWED_OUTSIDE); 
		endAttribute();
		inAttribute = true; 
		dblQuote = _dblQuote; //remember for endAttribute
		final char   chrDelim = _dblQuote ? StringFunction.CHR_QUOTE : StringFunction.XML_CHR_APOSTROPH;
		final String strDelim = _dblQuote ? StringFunction.XML_STR_QUOTE : StringFunction.XML_STR_APOSTROPH;
		streamByte.write(XML_CHR_SPACE);
		AStreamOutByte.WRITE(streamByte, _name); //Tag Names must not contain Characters to be encoded with Entities! 
		streamByte.write(XML_CHR_ATTR_SEP);
		streamByte.write(chrDelim); 
		mapper.setEncoding(chrDelim , strDelim);
		return this; }

	/** Writes the closing Quote Character for the current Attribute, if one is open, and restores
	  * the Encoder's Quote-escaping state.
	 * @throws IOException */
	public StreamOutXML endAttribute() throws IOException {
		listChr = CHR_IGNORE; 
		if (!inAttribute) 
			return this; //throw new AbstractMethodError(ERR_ALLOWED_OUTSIDE); 
		inAttribute = false; 
		final char   chrDelim = dblQuote ? StringFunction.CHR_QUOTE : StringFunction.XML_CHR_APOSTROPH;
		streamByte.write(chrDelim); mapper.setEncoding(chrDelim, null);
		return this; }
	
	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * also cares for all Integer Arguments
	  * only allowed inside a Start Tag.
	  */
	public StreamOutXML attribute(final String Name, final long Value) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuotesForAttributes); } // Double.toString(Value)); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * also cares for all float Point Arguments
	  * only allowed inside a Start Tag
	  */
	public StreamOutXML attribute(final String Name, final double Value) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuotesForAttributes); } // Double.toString(Value)); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  */
	public StreamOutXML attribute(final String Name, final boolean Value) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuotesForAttributes); } //Value ? STR_TRUE : STR_FALSE); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  */
	public StreamOutXML attribute(final String Name, final char Value) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuotesForAttributes); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * also cares for all Integer Arguments
	  * only allowed inside a Start Tag.
	  */
	public StreamOutXML attribute(final String Name, final long Value
	, final boolean dblQuote) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuote); } // Double.toString(Value)); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * also cares for all float Point Arguments
	  * only allowed inside a Start Tag
	  */
	public StreamOutXML attribute(final String Name, final double Value
	, final boolean dblQuote) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuote); } // Double.toString(Value)); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  */
	public StreamOutXML attribute(final String Name, final boolean Value
	, final boolean dblQuote) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuote); } //Value ? STR_TRUE : STR_FALSE); }

	/** Writes a primitive Attribute with the given Name
	  * @return the streamIO to append more Items
	  * only allowed inside a Start Tag
	  */
	public StreamOutXML attribute(final String Name, final char Value
	, final boolean dblQuote) throws IOException {
		return attribute(Name, String.valueOf(Value), dblQuote); }
	
	////////////////////////////////////////////////////////////////////////////
	//	Interface: ContentHandler
	////////////////////////////////////////////////////////////////////////////
	
	/** No-op: the Locator is not needed to write XML, only to consume it.
	  * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)	 */
	public void setDocumentLocator(final Locator locator) {
	}

	/** No-op: nothing needs writing before the first Element.
	  * @see org.xml.sax.ContentHandler#startDocument()	 */
	public void startDocument() { //throws SAXException {
	}

	/** Closes every still-open Tag, completing the Document.
	  * @see org.xml.sax.ContentHandler#endDocument()	 */
	public void endDocument() { //throws SAXException {
		closeAll();
	}
	
	/** defines an XML Prefix, can appear in any Element Declaration  
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)	  */
	public void startPrefixMapping(final String prefix, final String uri) { //throws SAXException {
		System.out.println("TODO:"); 
	}
	
	/** ends the XML Prefix, can appear in any Element Declaration  
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)	 */
	public void endPrefixMapping(final String prefix) { //throws SAXException {
		System.out.println("TODO:"); 
	}
	
	/** Translates a SAX startElement Event into a Start Tag, wrapping any IOException as a SAXException.
	  * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)	 */
	public void startElement(final String namespaceURI, final String localName
	, final String qName, final Attributes atts) throws SAXException {
		try {
			this.startTag(qName);
		} catch (final IOException x) {
			throw new SAXException(x);
		}
	}
	
	/** Translates a SAX endElement Event into an End Tag, wrapping any IOException as a SAXException.
	  * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)	 */
	public void endElement(final String namespaceURI, final String localName
	, final String qName) throws SAXException {
		try {
			this.endTag(qName);
		} catch (final IOException x) {
			throw new SAXException(x);
		}
	}
	
	/** Writes the given Character Range as a CDATA Section.
	  * @see org.xml.sax.ContentHandler#characters(char[], int, int)	 */
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		try { //TODO: rather don't write a CDATA Section and encode the Output in UTF and XML! 
			this.cData(new String(ch, start, length));
		} catch (final IOException x) {
			throw new SAXException(x); 
		}
	}
	
	/** Treated identically to {@link #characters(char[], int, int)}.
	  * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)	 */
	public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
		characters(ch, start, length);
	}

	/** Writes the Target and Data joined by '=' as an XML Processing Instruction.
	  * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)	 */
	public void processingInstruction(final String target, final String data) throws SAXException {
		try { //TODO: rather don't write a CDATA Section and encode the Output in UTF and XML! 
			this.processing(target+'='+data); 
		} catch (final IOException x) {
			throw new SAXException(x); 
		}
	}
	
	/** Called when an Entity could not be decoded by the SAX Parser!
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)	 */
	public void skippedEntity(final String name) throws SAXException {
		try { //TODO: rather don't write a CDATA Section and encode the Output in UTF and XML! 
			this.cData('&'+name+';'); 
		} catch (final IOException x) {
			throw new SAXException(x); 
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	//	static Testing and main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws IOException {
		StreamOutXML X = new StreamOutXML
		( new OutputStreamToStreamOutByte// OutputStreamWriter
		( System.out)); //, Stream.character.FileWriter.ISO_8859_1)); //Charset.));
		X.endAll(); //nothing, also no Error
	//	X.Attribute("Hi","there"); //Error: Attribute outside an Element!
	//	X.CData("blablabla"); //is allowed, but makes no sense...
	//	X.CloseTag(); //Error: Index out of bounds
		X.processing(XML_TAG_XML);
		X.attribute(XML_ATTR_VERSION , XML_ATTR_Version , true);
		X.attribute(XML_ATTR_ENCODING, XML_ATTR_Encoding, false);
		X.comment("Comment");
		X.declaration("DOCTYPE myDoc SYSTEM \"xxx.dtd\"");
	//	X.CloseTag(); //not necessary, automatically closed
		X.startTag("People");
		X.startTag("Person");
		X.attribute("FirstName","Matthias");
		X.attribute("LastName","Heuer");
		X.attribute("BirthDay","11.04.1968");
		X.text("Some Remarks of arbitrary Length...");
		X.endTag();
		X.cData("Some Remarks of arbitrary Length...");
		X.startTag("Person"); //Starting a Tag with the same Name could automatically close the previous Element?!?
		X.attribute("FirstName","Nicole");
		X.attribute("LastName","Warmbold");
		X.attribute("BirthDay","25.10.1969");
		X.endTag();
		X.startTag("Person"); //Starting a Tag with the same Name could automatically close the previous Element?!?
		X.attribute("FirstName","N<ic&ole");
		X.attribute("LastName","War'mb'old");
		X.attribute("BirthDay","25.1\"0.1969");
		X.endAll(); //not necessary, automatically closed on finalize()
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws IOException {
		testIt(args); }

	////////////////////////////////////////////////////////////////////////////
	//  IStreamOutStruct
	////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Serialization must either use only Elements 
	 * or simple Attributes and Objects must be written in 2 Passes: 
	 * 1st Pass: only primitive Values as Attributes 
	 * 2nd Pass: Lists and Objects as Elements   */
	
	/** Requires the Open and Close Names to be identical, then delegates to {@link #open_Struct(String)}.
	  * @see streamIO.integer.IStreamOutStruct#open_Struct(String, String)	 */
	public IStreamOutStruct open_Struct(final String open, final String close) {
		if(!ValidationRule.EQUALS(open, close))
			throw new RuntimeException("Opening and Closing Tags should be identical!");
		return open_Struct(open); }

	/** Opens an Element with the given Tag Name.
	  * @see streamIO.integer.IStreamOutStruct#open_Struct(java.lang.String)	 */
	public IStreamOutStruct open_Struct(final String openClose) {
		try { startTag(openClose);  
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes the Name/Value Pair as a quoted Attribute.
	  * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String)	 */
	public IStreamOutStruct writeNameValuePair(final String name, final String value) {
		return writeNameValuePair(name, value, true); }

	/** Returns the Name of the innermost currently open Tag.
	  * @see streamIO.integer.IStreamOutStruct#peek_Struct()	 */
	public String peek_Struct() { return tagName[SP-1]; }

	/** Closes the Element with the given expected Tag Name.
	  * @see streamIO.integer.IStreamOutStruct#closeStruct(String)	 */
	public IStreamOutStruct closeStruct(final String struct) {
		try { endTag(struct);
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Closes the innermost currently open Element.
	  * @see streamIO.integer.IStreamOutStruct#closeStruct()	 */
	public IStreamOutStruct closeStruct() {
		try { endTag(); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		arrIndex = -1; //listType = null; 
		return this; }
	
	/** Closes every still-open Element.
	  * @see streamIO.integer.IStreamOutStruct#closeAll()	 */
	public void closeAll() {
		try { endAll(); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** Writes an Attribute Name without its Value, e.g. as a Preamble to writing a Value separately.
	  * @see streamIO.integer.IStreamOutStruct#writeName(java.lang.String)	 */
	public IStreamOutStruct writeName(final String name) {
		try { startAttribute(name); //open_Struct(name, name); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes the Name/Value Pair as an Attribute, using the given Quote Style.
	  * @see streamIO.integer.IStreamOutStruct#writeNameValuePair(java.lang.String, java.lang.String, boolean)	 */
	public IStreamOutStruct writeNameValuePair(final String name, final String value, final boolean useQuotes) {
		try { attribute(name, value, useQuotes); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  IStreamOut Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Closes the current Tag, wrapping any IOException as an unchecked IOError.
	  * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut closeTagSafe() {
		try { return closeTag();
		} catch(final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/** Closes any open Tag, then delegates writing to the given Object's own writeTo(...).
	  * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final IStreamWriteAble arg) {
		if (inTag)
			closeTagSafe();
		arg.writeTo(this, itemName); return this; }

	/** Closes any open Tag, then delegates to the inherited generic addItem(Object).
	  * @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object arg) {
		if (inTag)
			closeTagSafe(); 
		super.addItem(arg); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Quotes 
	///////////////////////////////////////////////////////////////////////////
	
	final static String STR_TEXT = "text"; 
	
	/** Opens a "text" Element to hold quoted free-form Text.
	  * @see streamIO.integer.IStreamOutStruct#open_Quote()	 */
	public IStreamOutStruct open_Quote() {
		return open_Struct(STR_TEXT, STR_TEXT); }

	/** Closes the "text" Element opened by {@link #open_Quote()}.
	  * @see streamIO.integer.IStreamOutStruct#closeQuote()	 */
	public IStreamOutStruct closeQuote() {
		closeStruct(STR_TEXT); 
		return this; }
	
	///////////////////////////////////////////////////////////////////////////
	/// Handling Lists
	///////////////////////////////////////////////////////////////////////////
	
	//protected String listType; 
	
	/** the current maximum Index used when concatenating Arrays	 */
	protected int arrIndex = 0; 
	
	/** XML Lists without Container Elements are ambiguous to parse!   
	 * @see streamIO.integer.IStreamOutStruct#open_Struct()	 */
	public IStreamOutStruct open_Struct() {
		arrIndex = -1; 
		open_Struct(STR_LIST); //listType =  
		return this; } 
	
	/** Checks whether to start a List Element of the given Type	 */
	private void checkListStart(final String type) throws IOException {
		if (arrIndex < 0) {
			if (!inTag)
				startTag(type); //open_Struct( 
		} //when Markup is needed anyway, it can as well reflect the Positions
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// concrete List Elements 
	///////////////////////////////////////////////////////////////////////////
	
	/** Writes each int Value as a numbered Attribute of the current List Element.
	  * @see streamIO.integer.IStreamOutStruct#addInts(int[])	 */
	public IStreamOutStruct addInts(final int[] values, final int stop, final int start) {
		try { 
			checkListStart(STR_INT);
			for(int i = start-1; ++i < stop; ) //and Attributes are 'smaller' than Elements
				attribute("_"+(++arrIndex), values[i]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes each short Value as a numbered Attribute of the current List Element.
	  * @see streamIO.integer.IStreamOutStructArrays#addShorts(short[], int, int) */
	public IStreamOutStruct addShorts(final short[] values, final int stop, final int start) {
		try { 
			checkListStart(STR_SHORT);
			for(int i = start-1; ++i < stop; ) //and Attributes are 'smaller' than Elements
				attribute("_"+(++arrIndex), values[i]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes each long Value as a numbered Attribute of the current List Element.
	  * @see streamIO.integer.IStreamOutStructArrays#addLongs(long[], int, int) */
	public IStreamOutStruct addLongs(final long[] values, final int stop, final int start) {
		try { 
			checkListStart(STR_LONG);
			for(int i = start-1; ++i < stop; ) //and Attributes are 'smaller' than Elements
				attribute("_"+(++arrIndex), values[i]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes each float Value as a numbered Attribute of the current List Element.
	  * @see streamIO.integer.IStreamOutStructArrays#addFloats(float[], int, int) */
	public IStreamOutStruct addFloats(final float[] values, final int stop, final int start) {
		try { 
			checkListStart(STR_FLOAT);
			for(int i = start-1; ++i < stop; ) //and Attributes are 'smaller' than Elements
				attribute("_"+(++arrIndex), values[i]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes each double Value as a numbered Attribute of the current List Element.
	  * @see streamIO.integer.IStreamOutStructArrays#addDoubles(double[], int, int) */
	public IStreamOutStruct addDoubles(final double[] values, final int stop, final int start) {
		try { 
			checkListStart(STR_DOUBLE);
			for(int i = start-1; ++i < stop; ) //and Attributes are 'smaller' than Elements
				attribute("_"+(++arrIndex), values[i]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	/** Writes each String Value as a numbered Attribute of the current List Element.
	  * @see streamIO.integer.IStreamOutStructArrays#addStrings(java.lang.String[], int, int) */
	public IStreamOutStruct addStrings(final String[] values, int stop, int start) {
		try { 
			checkListStart(STR_STRING);
			for(int i = start-1; ++i < stop; ) //and Attributes are 'smaller' than Elements
				attribute("_"+(++arrIndex), values[i]); 
		} catch(final IOException x) {
			throw new IOError(x); 
		}
		return this; }
	
	// TODO: LOGIC: the `else` on the line below binds only to the immediately following
	// `startTag(name);` statement (no braces) - `closeTag(); AStreamOutByte.WRITE(...);
	// endTag(name);` execute unconditionally on every iteration, including when `value`
	// IS an IStreamWriteAble that already wrote its own Element via writeTo(...) above.
	// Every non-IStreamWriteAble Element also ends up empty-bodied, since closeTag() is
	// called before any Content is written and endTag(name) closes it again immediately.
	/** Writes each Object as its own Element: an {@link IStreamWriteAble} writes itself via
	  * writeTo(...), anything else is written as Text within an Element named by its List Index.
	  * @see streamIO.integer.IStreamOutStructArrays#addItems(java.lang.Object[], int, int) */
	public IStreamOutStruct addItems(final Object[] values, int stop, int start) {
		try {
			checkListStart(STR_OBJECT);
			for(int i = start-1; ++i < stop; ) { //Objects require Elements to contain structured Contents
				final Object value = values[i];
				final String name = "_"+(++arrIndex);
				if (value instanceof IStreamWriteAble)
					((IStreamWriteAble) value).writeTo(this, name);
				else
					startTag(name); closeTag();
					AStreamOutByte.WRITE(encoder, String.valueOf(value));
					endTag(name);
			}
		} catch(final IOException x) {
			throw new IOError(x);
		}
		return this; }
	
}
