/**
 * File  Name: SaxClientXmlWriter.java
 * Created on: 22.12.2002
 */
package technology.xml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Title: SaxClientXmlWriter<p>
 * Purpose:
 * similar to org.apache.xalan.serialize.SerializerToXML 
 * this Class simply formats SAX-style events into an XML streamIO.
 * 
 * Description:
 * org.xml.sax defines only Interfaces, 
 * except for InputSource, a Value Object which can represent an InputStream, Reader or URL. 
 * org.xml.sax.Parser Interface is deprecated (SAX1) and replaced by 
 * org.xml.sax.XMLReader (SAX2)
 * 
 * SAX1: EntityResolver, DTDHandler, DocumentHandler, ErrorHandler
 * SAX2: EntityResolver, DTDHandler,  ContentHandler, ErrorHandler
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
 */
public class SaxClientXmlWriter 
extends DefaultHandler 
implements org.xml.sax.ContentHandler
{

	PrintWriter out; 

	/**
	 * Constructor for SaxClientXmlWriter.
	 */
	protected SaxClientXmlWriter(String destination) throws IOException {
		out = new PrintWriter(new FileWriter(destination)); 
	}

	/**
	 * Constructor for SaxClientXmlWriter.
	 */
	protected SaxClientXmlWriter() {
		out = new PrintWriter(System.out); 
	}

	////////////////////////////////////////////////////////////////////////////////////
	/// Interface ContentHandler, also usable to write XML directly!
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		out.close(); 
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		write(ch, start, length); 
	}

	protected void write(char[] ch, int start, int length) throws SAXException {
		out.write(ch, start, length); 
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		write(ch, start, length); 
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		throws SAXException {
		out.write("<"); 
		out.write(qName); ResultSetToAttributes.WRITE_ATTRIBUTES(atts, out); 
		out.write(">"); 
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		out.write("</"); 
		out.write(qName); 
		out.write(">"); 
	}

	////////////////////////////////////////////////////////////////////////////////////
	/// unused Event Handlers 
	////////////////////////////////////////////////////////////////////////////////////

	/**
		 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	public void processingInstruction(String target, String data) throws SAXException {
		out.write("<?"); out.write(target); 
		out.write("  "); out.write(data); 
		out.write("?>"); 
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	public void skippedEntity(String name) throws SAXException {}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	public void setDocumentLocator(Locator locator) {}

	////////////////////////////////////////////////////////////////////////////////////
	/// static Methods
	////////////////////////////////////////////////////////////////////////////////////

	/** 
	 * 
	 * e.g. java technology.xml.SaxClientXmlWriter E:\Personal\Code\XML\ZKDB\MixedVersion\SimpleTypesEmpty.xsd
	 */
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		SAXParserFactory factory = SAXParserFactory.newInstance(); 
		SAXParser parser = factory.newSAXParser(); 
		if (args.length < 2) {
			parser.parse(args[0], new SaxClientXmlWriter());
		} else {
			parser.parse(args[0], new SaxClientXmlWriter(args[1]));
		}
	}
}
