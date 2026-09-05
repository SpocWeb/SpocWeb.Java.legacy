/**
 * File  Name: XmlHandler.java
 * Created on: 23.02.2003
 */
package technology.xml;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Provides a documented, chainable default implementation of the SAX handler interfaces by
 * making each public callback {@code final} and delegating to an overridable, boolean-
 * returning {@code _}-suffixed method that decides whether to propagate the event further
 * down the filter chain.
 *
 * Design Decisions / Implementation Details:
 * The Base Class provides sufficient, but not very useful 
 * empty Implementations of these Methods. 
 * Instead of deriving from org.xml.sax.helpers.DefaultHandler
 * which only allows to process a streamIO, 
 * an XMLFilter is implemented to allow for chaining 
 * several smaller and thus more modular Handlers. 
 * 
 * Instead of allowing to override the Callbacks, 
 * which could destroy the Chaining Behavior 
 * and swallow Events, the Events Callbacks themselves are made final 
 * and are always called, 
 * when the Return Value of the corresponding polymorph Method is true. 
 * Due to the Return Value, the Decision whether to call is enforced, 
 * but the Parameters cannot be changed. 
 * This could be changed using ByRefString Objects as Parameters. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:14:37Z
 * digest: fdad091f5029218e2ce72185ce8af80cf4e9061a77730ab49055318e429925d4
 * stale: false
 * tags: [code/xml_parsing]
 * concepts: [XML Handler Base]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class XmlHandler extends XMLFilterImpl 
implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler {

	/**
	 * Constructor for XmlHandler.
	 * This Instance registers itself as Callback for the following Interfaces: 
	 * EntityResolver, DTDHandler, ContentHandler, ErrorHandler 
	 * @param parent the Parent SAX Event Source
	 */
	public XmlHandler(XMLReader parent) {
		super(parent);
	}

	/**
	 * Constructor for XmlHandler.
	 */
	public XmlHandler() {
		super();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : ContentHandler Events in Order of their Occurence
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Handed over by the SAX Source to allow for 
	 * determining the URI, Public ID, Row and Column of an XML Parsing Error
	 * Contains the current Location of the Parser. 
	 */
	protected Locator locator; 

	/**
	 * Called as the very first Callback to set the Locator 
	 * to allow for Error Handling. 
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	final public void setDocumentLocator(Locator locator_) { 
		super . setDocumentLocator(this.locator = locator_); }

	/**
	 * Called second, directly after the setDocumentLocator() Method. 
	 * Corresponds to the Document Root, 
	 * which must not be confused with the XML Root Element, 
	 * which follows after Declarations and Comments
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	final public void startDocument() throws SAXException { startDocument_(); super.startDocument(); }

	/**
	 * Called last, after all other Callback Method. 
	 * Corresponds to the Document Root, 
	 * which must not be confused with the XML Root Element, 
	 * which follows after Declarations and Comments
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	final public void endDocument() throws SAXException { endDocument_(); super.endDocument(); }

	/**
	 * Processing Instructions look like Elements with Attributes, 
	 * but the Attributes are considered as Strings and are not being parsed! 
	 * Also the XML Declaration at the Beginning does not trigger this Callback, 
	 * because the XML Data should be independent of this Information!!!
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	final public void processingInstruction(String target, String data) throws SAXException {
		if (processingInstruction_(target, data)) {
			super.processingInstruction(target, data); }
	}

	/**
	 * Reports an Entity that could not be resolved. 
	 * Non-validating Parsers are allowed to skip Entities, 
	 * but must then call this Method
	 * @param name The Entity Name without leading Ampersand or following Semicolon. 
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	final public void skippedEntity(String name) throws SAXException {
		if (skippedEntity_(name)) {
			super.skippedEntity(name); }
	}

	/**
	 * Invokes {@link #startElement_} and, when it returns {@code true}, propagates the Element
	 * start to the underlying filter chain.
	 *
	 * @param namespaceURI The Namespace, already decoded by the SAX Parser
	 * @param localName Name without Prefix or Namespace
	 * @param qName Name as it appears in the Dokument: 'prefix:name'
	 * @param atts parsed Quasi-Map of Attributes (Names are unique!)
	 * containing Names, Indices, Values, Namespace Prefixes etc.
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	final public void startElement(String namespaceURI, String localName, String qName, Attributes atts
	) throws SAXException {
		if (startElement_(namespaceURI, localName, qName, atts)) { 
			super.startElement(namespaceURI, localName, qName, atts); }
	}

	/** StringBuffer to collect Text Data within Elements
	 * Should rather be a Stack of String Buffers 
	 * to collect all Text both preceding, mixed within and trailing inner Elements. 
	 */
	protected StringBuffer buffer = new StringBuffer(); 

	/**
	 * Invokes {@link #endElement_} and, when it returns {@code true}, propagates the Element
	 * end to the underlying filter chain, then clears the text {@link #buffer}.
	 *
	 * @param namespaceURI the Namespace, already decoded by the SAX Parser
	 * @param localName Name without Prefix or Namespace
	 * @param qName Name as it appears in the Dokument: 'prefix:name'
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	final public void endElement(String namespaceURI, String localName, String qName
	) throws SAXException {
		if (endElement_(namespaceURI, localName, qName)) { 
			super.endElement(namespaceURI, localName, qName); }
		buffer.setLength(0); //Text is not transferred between Nodes. 
	}

	/**
	 * Reports back Text and also Whitespace in Elements. 
	 * Whitespace is only reported when a DTD or an XML Schema defines 
	 * that Text ('CDATA')is allowed in the Element, 
	 * otherwise ignorableWhitespace() is being called.
	 * Parsers typically use a char Array to speed up Parsing. 
	 * This is even more efficient than a StringBuffer, 
	 * but less encapsulated. 
	 * Additionally the Parsers may decide to distribute Text Data 
	 * into several distinct characters() Calls. 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	final public void characters(char[] ch, int start, int length) throws SAXException {
		if (characters_(ch, start, length)) {
			super.characters(ch, start, length); }
		buffer.append(ch, start, length); 
	}

	/**
	 * Reports back Whitespace in Elements when a DTD or an XML Schema defines 
	 * that no Text ('CDATA')is allowed in the Element, 
	 * otherwise characters() is being called.
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	final public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		if (ignorableWhitespace_(ch, start, length)) {
			super.ignorableWhitespace(ch, start, length); }
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : Prefix Mapping Default Implementation
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** 
	 * Using a simple Stack, rather than a HashMap, 
	 * because a Map does not work with locally redefined Mappings. 
	 * No lazy Initialization */
	protected ArrayList prefixMappings = new ArrayList(); 
	
	/** 
	 * Using a simple Stack, rather than a HashMap, 
	 * because a Map does not work with locally redefined Mappings. 
	 * No lazy Initialization */
	protected int prefixMappingStackPointer = -1; 
	
	/** 
	 * Using a simple Stack, rather than a HashMap, 
	 * because a Map does not work with locally redefined Mappings. 
	 * This is slower for more than about ten Prefix Mappings, 
	 * but these rarely happen! 
	 */
	protected String getUriFromPrefix(String prefix) {
		for (int i = prefixMappings.size(); --i >= 0;) {		
			String[] mapping = (String[]) prefixMappings.get(i); 
			if (mapping[0].equals(prefix)) {
				return mapping[1]; }
		}
		return null; }
	
	/** The Map is not apted, because it holds only a single Value for each key 
	 * Actually the Container Implementation of a HashTable 
	 * can hold several Instances and thus acts both as a Stack and a HashMap! 
	 * @todo: use a HashTable here! 
	 */
//	protected HashMap map; 
	
	/**
	 * This Event is triggered by a Namespace Declaration 
	 * and is actually called BEFORE the 'startElement' of the Element where it is declared, 
	 * because the Element Name might have to be decoded by the Prefix 
	 * returned in one of its Attributes. 
	 * This is also the Reason why Attributes are handed over 
	 * together with the Element Start and not as separate Events. 
	 * These two Callbackes are called only when the Feature 
	 * http://xml.org/sax/features/namespace-prefixes is switched on
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 * @param prefix the Prefix prepended to the so called 'local Name' 
	 * @param uri the actual URI which the Prefix evaluates to 
	 */
	final public void startPrefixMapping(String prefix, String uri) throws SAXException {
		if (startPrefixMapping_(prefix, uri)) { 
			super.startPrefixMapping(prefix, uri); }
		prefixMappings.add(new String[]{prefix, uri});
	}

	/**
	 * This Event is actually called AFTER 'endElement', 
	 * because the Element Name might have to be decoded by the Prefix 
	 * returned in one of its Attributes. 
	 * These two Callbackes are called only when the Feature 
	 * http://xml.org/sax/features/namespace-prefixes is switched on. 
	 * Otherwise the following Feature should be switched on, 
	 * http://xml.org/sax/features/namespace-prefixes 
	 * so the SAX Parser handles Namespace Resoulutions by itself. 
	 * 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	final public void endPrefixMapping(String prefix) throws SAXException {
		if (endPrefixMapping_(prefix)) {
			super.endPrefixMapping(prefix); }
		String[] mapping = (String[]) prefixMappings.remove(prefixMappings.size()-1);
		if (!mapping[0].equals(prefix)) {
			//@todo: check what happens with several Mappings declared for the same Element! Possibly SAX Parser dependant!
			throw new SAXException("Prefix Mapping '"+prefix+"' ended, but does not match the expected Mapping of '"+mapping[0]+"' to '"+mapping[1]+"'!"); }
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : EntityResolver
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * called before an Entity is resolved by the SAX Parser, 
	 * e.g. by using the DTD and / or looking it up in the Internet. 
	 * 
	 * This can be used to resolve Standard Character Entities 
	 * or not explicitly declared Entities locally
	 * without trying to access the Internet and thus slowing down Parsing 
	 * or making it even impossible. 
	 * 
	 * Here a Chain of Responsibility is implemented:
	 * The Entity Reference is handed down the Filter Pipeline 
	 * until one of the Filters can resolve it. 
	 * Typically this Methods loops over a List of possible Entity Names 
	 * or consists of a long if() else if() Chain. 
	 * Make sure that the Names are sorted by descending Frequency, 
	 * so the most frequent Names are tested first! 
	 * 
	 * @see org.xml.sax.EntityResolver#resolveEntity(String, String)
	 * @return null if the SAX Parser should resolve the Entity, 
	 * otherwise an InputSource to read the Entity from. 
	 */
	final public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		InputSource ret; 
		if(null ==(ret = resolveEntity_(publicId, systemId))) {
			return super.resolveEntity(publicId, systemId); }
		return ret;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : DTDHandler
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Notifies the DTDHandler of a Notation Declaration. 
	 * Notations can be used in Attribute and Element Declarations 
	 * to determine the Processing Executable 
	 * &lt;!NOTATION jpeg SYSTEM "images/jpeg">
	 * @param name will evaluate to 'jpeg'
	 * @param publicId
	 * @param systemId, 
	 * @see org.xml.sax.DTDHandler#notationDecl(String, String, String)
	 */
	final public void notationDecl(String name, String publicId, String systemId) throws SAXException {
		if (notationDecl_(name, publicId, systemId)) {
			super.notationDecl(name, publicId, systemId); }
	}

	/**
	 * Notifies the DTDHandler of an (unprocessed) Entity Declaration. 
	 * 
	 * &lt;!ENTITY stars-logo PUBLIC "-//Catalog/public-id" "http://www.nhl.com/images/jpeg/pic1.jpg" NDATA jpeg>
	 * @param name will evaluate to 'stars-logo'
	 * @param publicId Public IDs are supposed to be resolved by local "CATALOG" Files, 
	 * to allow for Customization and to avoid Network Access for frequent Entities. 
	 * @param systemId System IDs are URIs from where to retrieve the Entity Contents. 
	 * @param notationName The Name of a Notation (see notationDecl), 
	 * usually used to determine the Application to process the given Entity. 
	 * @see org.xml.sax.DTDHandler#unparsedEntityDecl(String, String, String, String)
	 */
	final public void unparsedEntityDecl
	( String name, String publicId, String systemId, String notationName
	) throws SAXException {
		if (unparsedEntityDecl_(name, publicId, systemId, notationName)) {
			super.unparsedEntityDecl(name, publicId, systemId, notationName); }
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : ErrorHandler
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Warnings are called when 
	 * @param exception Contains Information about Public ID, Line and Column
	 * of the Parsing Process as well as Stack Trace of this and nested inner Exceptions. 
	 * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
	 */
	public void warning(SAXParseException exception) throws SAXException {
		if (warning_(exception)) { //this Method can re-throw the Exception
			super.warning(exception); } 
		
	}

	/**
	 * These non-critical Errors 
	 * One Error that can possibly be ignored is a higher XML Version in the XML Header
	 * than supported by the SAX Parser, which then warns the Application about this Fact, 
	 * because it might possibly overlook Features of later Versions. 
	 * @param exception Contains Information about Public ID, Line and Column
	 * of the Parsing Process as well as Stack Trace of this and nested inner Exceptions. 
	 * @see org.xml.sax.ErrorHandler#error(SAXParseException)
	 */
	public void error(SAXParseException exception) throws SAXException {
		if (error_(exception)) { //this Method can re-throw the Exception
			super.error(exception); }
	}

	/**
	 * Critical Errors typically inform about not well-formed XML 
	 * @param exception Contains Information about Public ID, Line and Column
	 * of the Parsing Process as well as Stack Trace of this and nested inner Exceptions. 
	 * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
	 */
	public void fatalError(SAXParseException exception) throws SAXException {
		if (fatalError_(exception)) { //this Method can re-throw the Exception
			super.fatalError(exception); }
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : ErrorHandler Methods to be overridden by Subclasses
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * this Method can re-throw the Exception
	 * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
	 */
	protected boolean warning_(SAXParseException exception
	) throws SAXException { return true; }

	/**
	 * this Method can re-throw the Exception
	 * @see org.xml.sax.ErrorHandler#error(SAXParseException)
	 */
	protected boolean error_(SAXParseException exception
	) throws SAXException { return true; }

	/**
	 * this Method can re-throw the Exception
	 * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
	 */
	protected boolean fatalError_(SAXParseException exception
	) throws SAXException { return true; }

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : ContentHandler Methods to be overridden by Subclasses
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	protected void startDocument_() throws SAXException {}

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	protected boolean processingInstruction_(String target, String data) throws SAXException {
		return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	public boolean skippedEntity_(String name) throws SAXException { return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	protected boolean startPrefixMapping_(String prefix, String uri) throws SAXException { return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	protected boolean startElement_(String namespaceURI, String localName, String qName, Attributes atts)
		throws SAXException { return true; }

	/**
	 * Callback Detour to be overwritten
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 * @return true when the Event should be passed on. 
	 */
	protected boolean characters_(char[] ch, int start, int length) throws SAXException { 
		return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	protected boolean ignorableWhitespace_(char[] ch, int start, int length) throws SAXException {
		return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	protected boolean endElement_(String namespaceURI, String localName, String qName) throws SAXException {
		return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	protected boolean endPrefixMapping_(String prefix) throws SAXException { return true; }

	/**
	 * Since this Event MUST be propagated there is no Return Value
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	protected void endDocument_() throws SAXException { }

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : EntityResolver Methods to be overridden by Subclasses
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * internal Callback to override 
	 */
	protected InputSource resolveEntity_(String publicId, String systemId) throws SAXException, IOException {
		return null;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : DTDHandler Methods to be overridden by Subclasses
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.DTDHandler#notationDecl(String, String, String)
	 */
	protected boolean notationDecl_(String name, String publicId, String systemId) throws SAXException { return true; }

	/**
	 * internal Callback to override 
	 * @see org.xml.sax.DTDHandler#unparsedEntityDecl(String, String, String, String)
	 */
	protected boolean unparsedEntityDecl_(String name, String publicId, String systemId, String notationName)
		throws SAXException { return true; }

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// #region : static Test and Main Methods
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Main Method to be called from the Command Line */
	public static void main(String[] args) {}
	
}
