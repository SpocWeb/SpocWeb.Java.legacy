package streamIO.object.parser;

import java.io.IOException;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import streamIO.object.IStreamIn;
import function.byref.ByRefInt;

/**
  * Title: SaxReader.java<p>
  *
  * Purpose:
  * implements the org.xml.sax.XMLReader Interface 
  * for an XML Parser implementing the @see streamIO.IStreamIn Interface. 
  * Unfortunately this is not sufficient for the Xalan Trafo to work with! 
  *
  * Design Decisions / Implementation Details:
  * separated from the actual Parser to just generate the correct Callback Events
  *
  * Extends Class  because ...
  * Implements Interface org.xml.sax.XMLReader because ...
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-09-2003, 11:50 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class SaxReader
implements XMLReader {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Namespace used in this Document */
	public String namespaceURI = "";

	/** Reference to the XML Scanner Object	 */
	protected IStreamIn xmlScanner; 

	/** @see org.xml.sax.XMLReader#getContentHandler()	 */
	protected ContentHandler contentHandler;

	/** @see org.xml.sax.XMLReader#getDTDHandler()	 */
	protected DTDHandler dtdHandler;

	/** @see org.xml.sax.XMLReader#getEntityResolver()	 */
	protected EntityResolver entityResolver;

	/** @see org.xml.sax.XMLReader#getErrorHandler()	 */
	protected ErrorHandler errorHandler;

	/** Container for the given Properties and Features */
	protected HashMap properties = new HashMap();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see org.xml.sax.XMLReader#getContentHandler()	 */
	public ContentHandler getContentHandler() { return contentHandler; }

	/** @see org.xml.sax.XMLReader#setContentHandler(ContentHandler)	 */
	public void setContentHandler(ContentHandler handler) { contentHandler = handler; }

	/** @see org.xml.sax.XMLReader#getDTDHandler()	 */
	public DTDHandler getDTDHandler() { return dtdHandler; }

	/** @see org.xml.sax.XMLReader#setDTDHandler(DTDHandler)	 */
	public void setDTDHandler(DTDHandler handler) { dtdHandler = handler; }

	/** @see org.xml.sax.XMLReader#getEntityResolver()	 */
	public EntityResolver getEntityResolver() { return entityResolver; }

	/** @see org.xml.sax.XMLReader#setEntityResolver(EntityResolver)	 */
	public void setEntityResolver(EntityResolver resolver) { entityResolver = resolver; }

	/** @see org.xml.sax.XMLReader#getErrorHandler()	 */
	public ErrorHandler getErrorHandler() { return errorHandler; }

	/** @see org.xml.sax.XMLReader#setErrorHandler(ErrorHandler)	 */
	public void setErrorHandler(ErrorHandler handler) { errorHandler = handler; }

	/** @see org.xml.sax.XMLReader#getFeature(String)	 */
	public boolean getFeature(String name) { //throws SAXNotRecognizedException, SAXNotSupportedException {
		Boolean value = (Boolean) properties.get(name);
		if (value == null) {
			return false; }
		return value.booleanValue();
	}

	/** @see org.xml.sax.XMLReader#setFeature(String, boolean)	 */
	public void setFeature(String name, boolean value) { //throws SAXNotRecognizedException, SAXNotSupportedException {
		properties.put(name, new Boolean(value)); }

	/** @see org.xml.sax.XMLReader#getProperty(String)	 */
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		return properties.get(name); }

	/** @see org.xml.sax.XMLReader#setProperty(String, Object)	 */
	public void setProperty(String name, Object value) { //throws SAXNotRecognizedException, SAXNotSupportedException {
		properties.put(name, value); }


	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public SaxReader(IStreamIn xmlScanner_) { 
		this.xmlScanner = xmlScanner_; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface org.xml.sax.XMLReader: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * an InputSource abstracts from Streams, Readers and URIs
	 * @see org.xml.sax.XMLReader#parse(InputSource)
	 */
	public void parse(InputSource input) throws IOException, SAXException {}

	/** @see org.xml.sax.XMLReader#parse(String)	 */
	public void parse(String systemId) throws IOException, SAXException {
		contentHandler.startDocument(); 
		contentHandler.startElement("", "Table", "Table", null); 
		contentHandler.  endElement("", "Table", "Table"); while(next());
		contentHandler.  endDocument();
	}

	/**
	 * Iterator Method for SAX Parsing 
	 * @return true when there was more XML Contents, false otherwise
	 * @throws SAXException on any Exception e.g. I/O
	 */
	public boolean next() throws SAXException {
		ByRefInt token = (ByRefInt) xmlScanner.nextItem(); 
		if (token == null) { 
			return false; }
		String localName = "row"; //null;
		Object obj = xmlScanner.currItem(); 
		Attributes atts = null;
		if (obj instanceof Attributes) {
			atts = (Attributes) obj; }
		if (obj instanceof String) {
			localName = (String) obj; }
		switch(token.Value) {
			case XMLScannerStreamIn.XML_TAG_START     : { contentHandler.startElement(namespaceURI, localName, namespaceURI+localName, atts) ; break; }
			case XMLScannerStreamIn.XML_TAG_END       : { contentHandler.  endElement(namespaceURI, localName, namespaceURI+localName); break; }
			case XMLScannerStreamIn.XML_TAG_ATTRIBUTE : { ; break; }
			case XMLScannerStreamIn.XML_TAG_APOSTROPH : { ; break; }
			case XMLScannerStreamIn.XML_TAG_CDATA     : { ; break; }
			case XMLScannerStreamIn.XML_TAG_DECLARE   : { ; break; }
			case XMLScannerStreamIn.XML_TAG_EOF       : { ; break; }
			case XMLScannerStreamIn.XML_TAG_QUOTE     : { ; break; }
			case XMLScannerStreamIn.XML_TAG_WHITESPACE: { ; break; }
			case XMLScannerStreamIn.XML_TAG_PROCESS   : { ; break; }
			case XMLScannerStreamIn.XML_TAG_COMMENT   : { ; break; }
			default: { throw new IndexOutOfBoundsException("Unexpected Case in switch():'"+token.Value+"'"); }
		} //switch()
		return true; 
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + SaxReader.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); 
	}

}

