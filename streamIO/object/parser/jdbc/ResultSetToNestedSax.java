package streamIO.object.parser.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import streamIO.exception.BaseException;
import streamIO.object.AStreamIn;
import technology.xml.ResultSetToAttributes;
import technology.xml.XslTrafo;

import com.sun.org.apache.xml.internal.utils.DOMBuilder;

/**
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
  * <!-- docstate
  * tags: [code/jdbc_adapter, code/sax_event_generation]
  * concepts: [Minimal JDBC Driver over Separated-Format Flat Files]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class ResultSetToNestedSax
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods, dispatching between RS-Fix and RS-Sep
	////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	// processing RS-SEP
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Convenience Overload taking a File Path instead of a File.
	  * @return a DOM loaded with a File in Separated Format */
	final static public Document RESULTSET_SEP_TO_DOM
	( final String filePath, final String root, final String separators
	, final boolean fieldNames
	) throws SAXException, IOException {
		return RESULTSET_SEP_TO_DOM(new File(filePath), root, separators, fieldNames); }

	/** Opens the separated File as a ResultSetSep and drains it into a DOM Document.
	  * @return a DOM loaded with a File in Separated Format */
	final static public Document RESULTSET_SEP_TO_DOM
	( final File file, final String root, final String separators
	, final boolean fieldNames) throws SAXException, IOException {
		return RESULTSET_TO_DOM(new ResultSetSep(file, separators, fieldNames), root); }

	/** Convenience Overload deriving the Separators and Field Names from the File itself.
	  * @return a DOM loaded with a File in Separated Format */
	final static public Document RESULTSET_SEP_TO_DOM
	( final File file) throws SAXException, IOException {
		return RESULTSET_TO_DOM(new ResultSetSep(file, null, null, file.getName())); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	// processing RS no matter where it comes from 
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Convenience Overload defaulting the Root Element Name.
	  * @return a DOM loaded with the given ResultSet */
	public static Document RESULTSET_TO_DOM ( final ResultSet rs) throws SAXException, IOException {
		return RESULTSET_TO_DOM (rs, null); }

	/** Drains the given ResultSet through this Class' SAX-Event simulation into a new DOM Document.
	  * @return a DOM loaded with the given ResultSet */
	public static Document RESULTSET_TO_DOM ( final ResultSet rs, final String root
	) throws SAXException, IOException {
		final Document doc = XslTrafo.NEW_DOCUMENT();
		final DOMBuilder builder = new DOMBuilder(doc); 
		final ResultSetToNestedSax stream = new ResultSetToNestedSax(rs); //
		stream.streamToSax(builder); 
		return doc; 
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Root Node Parameters */
	public String rootNamespaceURI = "";
	/** Local Name of the Root Element. */
	public String rootLocalName = ""; //localRoot";
	/** Qualified Name of the Root Element. */
	public String rootQName = "node";

	/** Row Node Parameters */
	public String namespaceURI = "";
	/** Local Name used for each Row Element. */
	public String localName = ""; //localRow";
	/** Qualified Name used for each Row Element. */
	public String qName = rootQName;
	
	/** contains the Stack of Nesting Elements	*/
	private final Stack stack = new Stack(); 
	
	/** Text added to each Row */
	public char[] rowText = {'\n', '\t'}; 
	
	/** Reference to the ResultSet Object being transmissed.
	 * always alternates between Start and End Tag */
	protected String token;
	
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
	public ResultSetToNestedSax(final ResultSet resultSet_) {
		this(resultSet_, null); }

	/** Initializing Constructor	 */
	public ResultSetToNestedSax(final ResultSet resultSet_, final String rootName_) {
		this.resultSet = resultSet_;
		attributes = new ResultSetToAttributes(resultSet); //being reused for every Row!
		if (rootName_ != null) {
			this.rootQName=rootName_; }
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the reused Attributes View over the current Row.
	  * @see streamIO.Object.IStreamIn#currItem()	 */
	public Object currItem() { return attributes; }

	/** Reports whether a current Row Token exists; does not use the (commented-out) real Cursor Checks.
	  * @see streamIO.IAvailAble#availAble()	 */
	public long availAble() {
		if (token == null) { 
			return -1; }
/*		try {
			if (resultSet.isLast       ()) { return  0; }
			if (resultSet.isAfterLast  ()) { return -1; }
			if (resultSet.isBeforeFirst()) { return  1; }
		} catch(SQLException x) {
			throw new BaseException(x);
		}
*/		return  1; 
	}
	
	/** Not tracked from the ResultSet: always reports the Maximum possible Value.
	  * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; } // resultSet.(); }

	/** Delegates to the ResultSet's own current Row Number.
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() {
		try { return resultSet.getRow(); 
		} catch (final SQLException x) {
			throw new BaseException(x); 
		}
	}
	
	/**
	 * Simulates SAX Events using Tokens in a passive InStream
	 * @see streamIO.IFactory#nextItem()
	 */
	public Object nextItem() {
		try {
			if (!resultSet.next()) { //the attributes Object works directly on the resultSet; 
				return token = null; } //no valid Row; Cursor cannot be rolled back!
			token = resultSet.getString(0);
		} catch(final SQLException x) {
			throw new BaseException(x);
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
		handler.characters(rowText , 0, rowText.length); //make the Output more readable 
		handler.  endElement(rootNamespaceURI, rootLocalName, rootQName); 
		handler.endDocument(); 
	}
	
	/** the current Level, not pushed onto the Stack yet...	 */
	String currToken = ""; 
	
	/** 
	 * streams all Rows out as an XML Fragment 
	 * @param handler any SAX ContentHandler 
	 * @throws SAXException on any Exception the handler cannot handle
	 */
	public void streamToSaxNodeList(final ContentHandler handler) throws SAXException {
		String token;
		while(null != (token = (String) nextItem())) {
			closeTags(handler, token);
			stack.push(currToken); currToken = token;
			handler.characters(rowText , 0, rowText.length); //make the Output more readable 
			handler.startElement(namespaceURI, localName, qName, attributes); 
		}
		closeTags(handler, "");
	}
	
	/** closes all Tags in the Stack up to the given Token. 	*/ 
	private void closeTags(final ContentHandler handler, String token) throws SAXException {
		while (token.compareTo(currToken) <= 0) { 
			if(stack.isEmpty()) {
				break; }
			handler.endElement(namespaceURI, localName, qName);
			//handler.characters(rowText , 0, rowText.length); //make the Output more readable 
			currToken = (String) stack.pop();
		} //level deeper
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	private static final String TEST_RS_SEP_PATH = "../XML/DocMapHierarchy/DocMapHierarchySample.sep"; 
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws SAXException, IOException {
		System.out.println("This Class writes a ResultSet in SEP Format as an XML String to System.out");
		System.out.println("Syntax: java " + ResultSetToNestedSax.class.getName() + " [fileName ]*");
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
				XslTrafo.STREAM(RESULTSET_SEP_TO_DOM(file), System.out);
			}
		}
	}

}
