package streamIO.object.parser.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import math.vector.VectorString;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import streamIO.exception.BaseException;
import streamIO.integer.jdbc.ConnectionFix;
import streamIO.integer.jdbc.ResultSetFix;
import streamIO.object.AStreamIn;
import streamIO.object.parser.XMLScannerStreamIn;
import technology.xml.ResultSetToAttributes;
import technology.xml.XslTrafo;

import com.sun.org.apache.xml.internal.utils.DOMBuilder;

import function.byref.ByRefInt;

/**
  * Title: ResultSetToSax<p>
  *
  * Purpose:
  * Feeds the SAX Interface from a JDBC ResultSet. 
  * Unfortunately this is not understood by common XML Parsers as a SAXInputStream. 
  *
  * Design Decisions / Implementation Details:
  * @see streamIO.object.parser.StreamOutXML which could be used alternatively
  * to write XML, instead of feeding a DOM SAX Handler to build a Document, 
  * but this cannot handle Namespaces well.  
  *
  * Instead of replicating all Parsing and Initialization Logic in ResultSetSep,
  * it is being reused by accessing the Data Row-wise
  * and handing all Fields over
  * either as SubElements with Text Contents
  * or as Attributes with their Field Names.
  * The Attributes Interface is very similar
  * to the ResultSet and ResultSetMetaData Interfaces.
  *
  * The XMLReader Interface is not as fundamental
  * as the Fact that this Class calls the Callback Methods
  * of a org.xml.sax.ContentHandler.
  * The most important Callbacks are:
  *
  * startDocument / endDocument
  * startElement  / endElement with all Attributes
  * characters
  *
  * less important Callbacks are:
  * ignorableWhitespace
  * warning / error / fatalError
  * setDocumentLocator for determining where in the Document an Event occurred.
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes: 
  * @see ResultSetToAttributes
  * @see
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-08-2003, 02:07 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class ResultSetToSax
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	final static public String SCHEME_FILE = "file:///"; 
		
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods, dispatching between RS-Fix and RS-Sep
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return a DOM loaded with a File in Fixed or Separated Format */
	final static public Document RESULTSET_TO_DOM
	(  String filePath, final String root, final String separators
	, final boolean fieldNames, final String attributePrefix
	) throws SAXException, SQLException, IOException {
		if (filePath.startsWith(SCHEME_FILE))
			filePath = filePath.substring(SCHEME_FILE.length()); 
		return RESULTSET_TO_DOM(new File(filePath), root, separators, fieldNames, attributePrefix); }

	/** @return a DOM loaded with a File in Fixed or Separated Format */
	final static public Document RESULTSET_TO_DOM
	( final File file, final String root, final String separators
	, final boolean fieldNames, final String attributePrefix
	) throws SAXException, SQLException, IOException {
		if (VectorString.ENDS_WITH(file, ConnectionFix.SUFFIX_FIX)) { return RESULTSET_TO_DOM(new ResultSetFix(file), root, attributePrefix); } //, separators, fieldNames, fieldDefaults); }
		if (VectorString.ENDS_WITH(file, ConnectionSep.SUFFIX_SEP)) { return RESULTSET_TO_DOM(new ResultSetSep(file, separators, fieldNames), root, attributePrefix); }
		if (VectorString.ENDS_WITH(file, ConnectionSep.SUFFIX_TAB)) { return RESULTSET_TO_DOM(new ResultSetSep(file, null!=separators?separators:ResultSetFix.TAB_SEPARATORS, fieldNames), root, attributePrefix); }
		return null; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// processing RS-FIX
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @return a DOM loaded with a File in Fixed Format */
	final static public Document RESULTSET_FIX_TO_DOM
	( final String filePath, final String root, final String separators
	, final boolean fieldNames, final boolean fieldDefaults, final String attributePrefix
	) throws SAXException, SQLException, IOException {
		return RESULTSET_FIX_TO_DOM(new File(filePath), root, attributePrefix); }//, separators, fieldNames, fieldDefaults); }

	/** @return a DOM loaded with a File in Fixed Format */
	final static public Document RESULTSET_FIX_TO_DOM
	( final File file, final String root//, final String separators, final boolean fieldNames, final boolean fieldDefaults
			, final String attributePrefix
	) throws SAXException, SQLException, IOException {
		return RESULTSET_TO_DOM(new ResultSetFix(file), root, attributePrefix); } //, separators, fieldNames, fieldDefaults); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// processing RS-SEP
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @return a DOM loaded with a File in Separated Format */
	final static public Document RESULTSET_SEP_TO_DOM
	( final String filePath, final String root, final String separators
	, final boolean fieldNames, final String attributePrefix) throws SAXException, IOException {
		return RESULTSET_SEP_TO_DOM(new File(filePath), root, separators, fieldNames, attributePrefix); }
	
	/** @return a DOM loaded with a File in Separated Format */
	final static public Document RESULTSET_SEP_TO_DOM
	( final File file, final String root, final String separators
	, final boolean fieldNames, final String attributePrefix
	) throws SAXException, IOException {
		return RESULTSET_TO_DOM(new ResultSetSep(file, separators, fieldNames), root, attributePrefix); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// processing RS no matter where it comes from 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @return a DOM loaded with the given ResultSet */
	public static Document RESULTSET_TO_DOM ( final ResultSet rs, final String root
			, final String attributePrefix
	) throws SAXException, IOException {
		final Document doc = XslTrafo.NEW_DOCUMENT();
		final DOMBuilder builder = new DOMBuilder(doc); 
		final ResultSetToSax stream = new ResultSetToSax(rs, attributePrefix); //
		stream.rootQName = root;
		stream.streamToSax(builder); 
		return doc; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Root Node Parameters */
	public String rootNamespaceURI = "";
	public String rootLocalName = ""; //localRoot";
	public String rootQName = "root";
	
	/** Row Node Parameters */
	public String namespaceURI = "";
	public String localName = ""; //localRow";
	public String qName = "Row";

	/** Text added to each Row */
	public char[] rowText = {'\n', '\t'}; 

	/** Reference to the ResultSet Object being transmissed.
	 * always alternates between Start and End Tag */
	protected ByRefInt token = new ByRefInt(XMLScannerStreamIn.XML_TAG_END);

	/** Reference to the ResultSet Object being transmissed. 	  */
	protected ResultSet resultSet;

	/**
	 * the same attributes Object is being reused for every Row!
	 * Thus it is not suited for concurrent Use! 
	 * returned by currItem()
	 */
	protected ResultSetToAttributes attributes;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public ResultSetToSax(final ResultSet resultSet_, String attributePrefix) {
		this.resultSet = resultSet_;
		attributes = new ResultSetToAttributes(resultSet); //being reused for every Row!
		if (attributePrefix != null)
			attributes.uri = attributePrefix; 
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { 
		try { return resultSet.getRow(); 
		} catch (final SQLException x) {
			throw new BaseException(x); 
		}
	}
	
	/** @see streamIO.Object.IStreamIn#currItem()	 */
	public Object currItem() { return attributes; }

	/** @see streamIO.IAvailAble#availAble()	 */
	public long availAble() {
		if (token == null) 
			return -1; 
/*		try {
			if (resultSet.isLast       ()) { return  0; }
			if (resultSet.isAfterLast  ()) { return -1; }
			if (resultSet.isBeforeFirst()) { return  1; }
		} catch(SQLException x) {
			throw new BaseException(x);
		}
*/		return  1; 
	}

	/**
	 * Simulates SAX Events using Tokens in a passive InStream
	 * @see streamIO.IFactory#nextItem()
	 */
	public Object nextItem() {
		if (token.Value == XMLScannerStreamIn.XML_TAG_START) { //close the Element right again
			token.Value  = XMLScannerStreamIn.XML_TAG_END; } else 
		if (token.Value == XMLScannerStreamIn.XML_TAG_END) { //close the Element right again
			token.Value  = XMLScannerStreamIn.XML_TAG_CDATA; 
		} else {
			token.Value  = XMLScannerStreamIn.XML_TAG_START; 
			try {
				if (!resultSet.next()) { //the attributes Object works directly on the resultSet; 
					return token = null; } //no valid Row; Cursor cannot be rolled back! 
			} catch(SQLException x) {
				throw new BaseException(x);
			}
		}
		return token; //attributes;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : bulk Methods to stream the whole ResultSet
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * writes a complete XML Document to the ContentHandler
	 * @param handler any SAX ContentHandler 
	 * @throws SAXException on any Exception the handler cannot handle
	 */
	public void streamToSax(final ContentHandler handler) throws SAXException {
		handler.startDocument(); 
		handler.startElement(rootNamespaceURI, rootLocalName, rootQName, new AttributesImpl()); // attributes);
		streamToSaxNodeList(handler);
		handler.  endElement(rootNamespaceURI, rootLocalName, rootQName); 
		handler.endDocument(); 
	}
	
	/** 
	 * streams all Rows out as an XML Fragment 
	 * @param handler any SAX ContentHandler 
	 * @throws SAXException on any Exception the handler cannot handle
	 */
	public void streamToSaxNodeList(final ContentHandler handler) throws SAXException {
		ByRefInt token;
		while(null != (token = (ByRefInt) nextItem())) {
			switch(token.Value) {
				case XMLScannerStreamIn.XML_TAG_END  : handler.  endElement(namespaceURI, localName, qName)            ; break; 
				case XMLScannerStreamIn.XML_TAG_START: handler.startElement(namespaceURI, localName, qName, attributes); break; 
				case XMLScannerStreamIn.XML_TAG_CDATA: handler.characters(rowText , 0, rowText.length); break; 
				default: throw new RuntimeException("unexpected Token Value"); 
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	private static final String TEST_RS_SEP_PATH = "../../Databases/MusicCollection/Artists.sep"; 
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws SAXException, IOException {
		System.out.println("This Class writes a ResultSet in SEP Format as an XML String to System.out");
		System.out.println("Syntax: java " + ResultSetToSax.class.getName() + " [fileName ]*");
		main(new String[] {TEST_RS_SEP_PATH}); 
		System.out.println();
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws SAXException, IOException {
		if (args.length == 0) {
			testIt();
		} else {
			for(int i = args.length; --i >=0; ) {
				final File file = new File(args[i]); 
				XslTrafo.STREAM(RESULTSET_SEP_TO_DOM(file, file.getName(), null, true, ""), System.out);
			}
		}
	}

}
