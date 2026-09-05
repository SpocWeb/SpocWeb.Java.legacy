/*
 * File Name: SaxHandler.java
 * Created on: 24.04.2004
 *
 */
package technology.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Collects common functionality for most SAX parsers: lazy parser creation, parsing from a
 * URI/File/InputStream/InputSource, and locator tracking with a location-annotated error.
 *
 * Design Decisions / Implementation Details:
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
 * mtime: 2026-09-05T11:14:21Z
 * digest: 67e4bd82b426fdd97a7e73925044b17cb6a65d7779538063a861c94b6b306e1f
 * stale: false
 * tags: [code/sax_parsing]
 * concepts: [SAX Content Handler Base]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class SaxHandler 
extends DefaultHandler {

	/** globally used Trafo Factory Instance */
	final static public SAXParserFactory SAX_FACTORY = SAXParserFactory.newInstance();
	
	/**
	 * Creates a handler with no parser yet assigned; one is created lazily on first {@link #parse}.
	 */
	public SaxHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Flag whether not collect White Space */
	public boolean collectWhiteSpace;

	/** Reference to a local Parser Object */
	protected SAXParser parser;

	/** last Document Row before a Parsing Error occurred	 */
	protected int lastGoodRow;

	/** last Document Column before a Parsing Error occurred	 */
	protected int lastGoodCol;

	/**
	 * parses the Document at the given URL
	 * @param uri the URI from where to load the Document
	 */
	public void parse(final String uri) throws ParserConfigurationException, SAXException, IOException {
		parse(new InputSource(uri));
	}

	/**
	 * parses the given File 
	 * @param file the File to parse
	 */
	public void parse(final File file) throws ParserConfigurationException, SAXException, IOException {
		parse(new FileInputStream(file));
	}

	/**
	 * parses the given InputStream 
	 * @param in InputStream to parse
	 */
	public void parse(final InputStream in) throws ParserConfigurationException, SAXException, IOException {
		parse(new InputSource(in));
	}

	/**
	 * parses the given 
	 * @param uri
	 */
	public void parse(final InputSource source) throws ParserConfigurationException, SAXException, IOException {
		try {
			if (parser == null) {
				//SAXParserFactory factory = SAXParserFactoryImpl.newInstance();  //
				//parser = factory.newSAXParser(); //  
				parser = SAX_FACTORY.newSAXParser();
			}
			parser.parse(source, this);
		} catch(final SAXException x) {
			System.out.print("outer:");
			x.printStackTrace(); 
			Exception inner = x.getException();
			if (null == inner) { //native Exception
				StringBuffer messageWithLocation = new StringBuffer(x.getMessage());
				messageWithLocation.append(" in Line   " + lastGoodRow); //locator.getLineNumber()); 
				messageWithLocation.append(" at Column " + lastGoodCol); //locator.getColumnNumber()); 
				throw new SAXException(messageWithLocation.toString()); //rethrow it
			}
			System.out.print("inner:");
			inner.printStackTrace(); 
			throw new SAXException(x); //inner); 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Cacheing of the Locator Object 	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Handed over by the SAX Source to allow for 
	 * determining the URI, Public ID, Row and Column of an XML Parsing Error
	 * Contains the current Location of the Parser. 
	 */
	protected Locator locator;

	/**
	 * Returns the Locator handed over by the SAX source, or {@code null} before parsing starts.
	 *
	 * @return the Locator for the given Document
	 */
	final public Locator getDocumentLocator() {
		return locator;
	}

	/**
	 * Called as the very first Callback to set the Locator 
	 * to allow for Error Handling. 
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	final public void setDocumentLocator(Locator locator_) {
		super.setDocumentLocator(this.locator = locator_);
	}

}
