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
 * Formats SAX-style ContentHandler events back into an XML stream, similar to
 * {@code org.apache.xalan.serialize.SerializerToXML}.
 *
 * <p>Description:
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:13:53Z
 * digest: e3c0775ac98e121d4d2e572c3f67c59dec4844c25811de66fa2ed8c0c17351db
 * stale: false
 * tags: [code/sax_parsing]
 * concepts: [SAX-Based XML Writer]
 * facets: {layer: infrastructure, status: legacy, complexity: medium}
 * -->
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
	 * Does nothing; no output is written when the document starts.
	 *
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {}

	/**
	 * Closes the underlying output.
	 *
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		out.close();
	}

	/**
	 * Writes the given character range verbatim to the output.
	 *
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		write(ch, start, length);
	}

	protected void write(char[] ch, int start, int length) throws SAXException {
		out.write(ch, start, length);
	}

	/**
	 * Writes ignorable whitespace verbatim to the output, same as {@link #characters}.
	 *
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		write(ch, start, length);
	}

	/**
	 * Writes an opening tag with its attributes to the output.
	 *
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
		throws SAXException {
		out.write("<");
		out.write(qName); ResultSetToAttributes.WRITE_ATTRIBUTES(atts, out);
		out.write(">");
	}

	/**
	 * Writes a closing tag to the output.
	 *
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
	 * Does nothing; prefix mappings are not written out.
	 *
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}

	/**
	 * Does nothing; prefix mappings are not written out.
	 *
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {}

	/**
	 * Writes a processing instruction to the output.
	 *
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	public void processingInstruction(String target, String data) throws SAXException {
		out.write("<?"); out.write(target);
		out.write("  "); out.write(data);
		out.write("?>");
	}

	/**
	 * Does nothing; skipped entities are not written out.
	 *
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	public void skippedEntity(String name) throws SAXException {}

	/**
	 * Does nothing; the locator is not used by this writer.
	 *
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
