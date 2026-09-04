/**
 * File  Name: XslTrafo.java
 * Created on: 22.12.2002
 */
package technology.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import streamIO.Log;
import streamIO.exception.BaseException;
import streamIO.integer.encoding.FilterLookup;
import streamIO.object.parser.SaxReader;
import streamIO.object.parser.jdbc.ResultSetSep;
import streamIO.object.parser.jdbc.ResultSetToSax;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import com.sun.org.apache.xml.internal.serializer.Serializer;
import com.sun.org.apache.xml.internal.serializer.SerializerFactory;
import com.sun.org.apache.xml.internal.utils.TreeWalker;

/**
 * Title: XslTrafo<p>
 * Description:
 * Purpose:
 * Implements an XSL Trafo Architecture:
 * different XML Sources (XML Documents, separated or fixed Length Tables) 
 * are loaded into DOMs
 * and can be tranformed and merged into higher Level DOMs
 * which are retained in RAM and can be transformed again etc.
 * Thus XML needn't be serialized
 * and complex DOMs don't need to be parsed again but stored in DOM Variables.
 * 
 * Additionally an XML Language is defined (with DOM Variables) 
 * to describe the concatenated Process. 
 * (should be XML, the Parser comes for free then) and an Interpreter for it
 * to allow for Scripting.
 *
 * The Interpreter should heavily rely on these Filter
 * and Join Classes to make it easier.
 *
 * Later Scripting could be made visual using Drag and Drop Tools.
 *
 *
 * Design Decisions / Implementation Details:
 * Just like XSLTs show, it doesn't pay off
 * to generate different Outputs from the same XML DOM.
 * This can be easily realized using DOM Variables.
 * 
 * transformer.transform(new SAXSource(), new SAXResult(args[2]));
 * transformer.transform(new DOMSource(args[0]), new DOMResult(args[2]));
 * can use a StreamSource and a StreamResult to chain Trafos: expensive Serialization and Parsing
 * can use a DOMSource and a DOMResult to chain Trafos: expensive Memory Consumption (possibly fastest as long as XSLTs require the full DOM)
 * can use a SAXSource and a SAXResult to chain Trafos: no Memory, no Serialization (fastest for streaming Processing)
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
public class XslTrafo {
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	private static final Log L = new Log(XslTrafo.class); 
	
	/** globally used Trafo Factory Instance */
	final static public TransformerFactory TRAFO_FACTORY =
		TransformerFactory.newInstance();
	
	final static public XMLReader NEW_READER() {
		try {
			return  XMLReaderFactory.createXMLReader();
		} catch (final SAXException x) {
			L.n(x); 
			return null;
		}
	}
	
	/** globally used Document Factory Instance */
	final static public DocumentBuilder DOM_BUILDER;
	
	static {
		// 
		DocumentBuilder tmp = null;
		DocumentBuilderFactory domFactory =
			DocumentBuilderFactory.newInstance();
		try {
			tmp = domFactory.newDocumentBuilder();
		} catch (final ParserConfigurationException x) {
			L.n(x); 
		}
		DOM_BUILDER = tmp;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants for the XML Grammar
	////////////////////////////////////////////////////////////////////////////////
	
	/// Element Names
	final static public String STR_ELM_LOAD  = "load";
	final static public String STR_ELM_STORE = "store";
	final static public String STR_ELM_JOIN  = "join";
	final static public String STR_ELM_TRAFO = "trafo";
	final static public String STR_ELM_DEBUG = "debug";
	
	/// Attribute Names
	final static public String STR_ATTR_NAME   = "name";
	final static public String STR_ATTR_ROOT   = "root"; //optional root Name for the loaded Element
	final static public String STR_ATTR_SOURCE = "source"; //
	final static public String STR_ATTR_DEST   = "dest"; //
	final static public String STR_ATTR_INPUT  = "input"; //
	final static public String STR_ATTR_XSLT   = "xslt"; //Path to an XSLT 
	final static public String STR_ATTR_XPATH  = "xpath"; 
	final static public String STR_ATTR_NAMES  = "fieldNames"; //Flag whether Field Names are given in a DB RS
	final static public String STR_ATTR_DEFAULTS="fieldDefaults"; //Flag whether Defaults are given in a DB RS
	final static public String STR_ATTR_SEPS   = "separators"; 
	final static public String STR_ATTR_REF    = "urn"; //reference to the Root of a loaded Variable
	final static public String STR_ATTR_SUFFIX = "suffix"; //Suffix for Files when loading a full Directory
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

	/** @return a new absolute URL from the given Locator and a (possibly relative) URL */
	final static public String GET_ABSOLUTE_URL(String systemID, String url) {
		if (url.indexOf(':') >= 0) //either a Drive or a Protocol 
			return url;
		String unescaped = systemID.replaceAll("%5C", "/"); 
		if (url.startsWith(".")) {
			int pos = unescaped.lastIndexOf('/'); 
			return unescaped.substring(0, pos+1) + url; 
		}
		if (!url.startsWith(".")) //absolute path
			return url; 
		
		try {
			return new URL(new URL(unescaped), url).toString();
		} catch (final MalformedURLException x) {
			throw new BaseException(x);
		}
	}
	
	/** fills the given Properties Object with some Default Values for XSL Trafo */
	final static public void DEFAULT_FORMAT_PROPERTIES(Properties format) {
		//		format.put(OutputKeys.CDATA_SECTION_ELEMENTS, "4");
		format.put(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
		format.put(OutputKeys.OMIT_XML_DECLARATION, "false");
		//		format.put(OutputKeys.DOCTYPE_SYSTEM, "*.DTD");
		//		format.put(OutputKeys.DOCTYPE_PUBLIC, "");
		format.put(OutputKeys.STANDALONE, "yes");
		format.put(OutputKeys.MEDIA_TYPE, "text/xml");
		format.put(OutputKeys.METHOD, "xml");
	}
	
	/** streams the given DOM, converted into a File.
	 * @throws SAXException although this shouldn't happen!
	 */
	final public static void TO_FILE(final Node node, final String fileName
	) throws SAXException, IOException {
		final FileOutputStream writer = new FileOutputStream(fileName);
		STREAM(node, writer);
	}
	
	/** @return the Node with the given XPath from the given DOM.
	 * @throws SAXException although this shouldn't happen!
	 */
	final public static Node XPATH
	( final Node node
	, final String root
	, final String xPath
	) throws SAXException, IOException {
		Log.TODO("implement XPath");
		//TODO: implement XPath	
		return node;
		/*.get
				, atts.getValue(STR_ATTR_ROOT  )
				, atts.getValue(STR_ATTR_XPATH )
				);
		*/
	}
	
	/** @return the given DOM, converted into an XML String.
	 * @throws SAXException although this shouldn't happen!
	 */
	final static public String TO_STRING(final Node node
	) throws SAXException, IOException {
		final StringWriter writer = new StringWriter();
		STREAM(node, writer);
		return writer.toString();
	}
	
	/** 
	 * writes the Node to the given streamIO. 
	 * @throws SAXException although this shouldn't happen!
	 */
	final public static void STREAM(final Node node, final OutputStream stream
	) throws SAXException, UnsupportedEncodingException, IOException {
		STREAM(node, new PrintWriter(stream));
	}
	
	/** 
	 * writes the Node to the given Writer. 
	 * @throws SAXException although this shouldn't happen!
	 */
	final public static void STREAM(final Node node, final Writer writer
	) throws IOException, SAXException {
		//SerializerToText(); creates only the Text Contents!!!
		final Properties format;
		//format = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML); //Java 1.4
		format = new Properties(); DEFAULT_FORMAT_PROPERTIES(format); //Java 1.5
		//Java 1.5
		final Serializer serial = SerializerFactory.getSerializer(format);
		serial.setWriter(writer);
		//Java 1.4
		//final SerializerToXML serial = new SerializerToXML();
		//serial.init(writer, format); //null); //format);
		final TreeWalker walker = new TreeWalker(serial.asContentHandler());
		walker.traverse(node);
		//last Row does not receive a CR/LF! Use "System.out.println();" to flush Console!
	}
	
	/** @return a new Document with the given Name created from the global DOM_Builder */
	final public static Document NEW_DOCUMENT() {
		return DOM_BUILDER.newDocument();
	} //creates an empty DOM
	
	/** @return a new Element with the given Name, created from the existing Node */
	final public static Element NEW_ELEMENT(
		final Node node,
		final String name) {
		Document owner =
			(node instanceof Document)
				? (Document) node
				: node.getOwnerDocument();
		return owner.createElement(name);
	}
	
	/** @return a new, parsed Trafo Object from the given XSLT DOM */
	final public static Transformer NEW_TRAFO(final Node trafo)
		throws TransformerConfigurationException {
		return TRAFO_FACTORY.newTransformer(new DOMSource(trafo));
	} //somehow doesn't work!!!
	
	/** loads and parses an XSLT Object from the given URI
	 * @param uri Location of the XSLT Document
	 */
	final static public Transformer NEW_TRAFO(final String uri)
		throws TransformerException {
		return TRAFO_FACTORY.newTransformer(new StreamSource(uri));
	}
	
	/**skips all Attribute and Text Nodes. 
	 * @return the first Child of Type 'Element' from the given Node 
	 */
	final static public Element FIRST_ELEMENT(final Node node) {
		Node ret = node.getFirstChild();
		while ((ret != null) && !(ret instanceof Element)) {
			ret = ret.getNextSibling();
		}
		return (Element) ret;
	}
	
	/** 
	 * @return the (parent) Document from the given Node 
	 * no matter if it is a regular Node or a Document itself
	 */
	final static public Document GET_DOCUMENT(final Node node) {
		return (node instanceof Document)
			? (Document) node
			: node.getOwnerDocument();
	}
	
	/** 
	 * @return the given Node itself or the Root Node it 'node' is a Document 
	 */
	final static public Element GET_ELEMENT(final Node node) {
		return (node instanceof Document)
			? ((Document) node).getDocumentElement()
			: (Element) node;
	}
	
	/** creates a new DOM Element containing both Nodes as Children 
	 * @param name Name of the new Root Element
	 * @param node1 first  XML Document
	 * @param node1 second XML Document
	 */
	final static public Document JOIN(
		final Node node1,
		final String name,
		final Node node2,
		final boolean inPlace) {
		Document owner = inPlace ? GET_DOCUMENT(node1) : NEW_DOCUMENT();
		Element ret = owner.createElement(name); //NEW_ELEMENT(node1, name);
		Element elm1 = GET_ELEMENT(node1);
		Element elm2 = GET_ELEMENT(node2);
		if (inPlace) {
			owner.removeChild(elm1);
		} else {
			elm1 = (Element) owner.importNode(elm1, true);
		}
		owner.appendChild(ret);
		ret.appendChild(elm1);
		//.cloneNode(true)); //cloning not sufficient! Owner has to change!
		ret.appendChild(owner.importNode(elm2, true));
		//doesn't work otherwise!
		return owner;
	}
	
	/** creates a new DOM containing both Nodes as Children 
	 * @param name Name of the new Root Element
	 * @param nodes list of XML Document Variables
	 */
	final static public Document JOIN(
		final String name,
		final Iterator nodes) {
		final Document owner = NEW_DOCUMENT();
		final Element ret = owner.createElement(name);
		owner.appendChild(ret);
		for (Node node; nodes.hasNext();) {
			node = (Node) nodes.next();
			final Element elm = GET_ELEMENT(node);
			ret.appendChild(owner.importNode(elm, true));
			//use 'importNode', doesn't work otherwise!
		}
		return owner;
	}
	
	/** creates a new DOM containing both Nodes as Children 
	 * @param name Name of the new Root Element
	 * @param nodes list of XML Document Variables
	 */
	final static public Document JOIN(final String name, final Node[] nodes) {
		final Document owner = NEW_DOCUMENT();
		final Element ret = owner.createElement(name);
		owner.appendChild(ret);
		for (int i = nodes.length; --i >= 0;) {
			final Element elm = GET_ELEMENT(nodes[i]);
			ret.appendChild(owner.importNode(elm, true));
			//doesn't work otherwise!
		}
		return owner;
	}
	
	/** Helper Routine to add any Node to any other Node 
	 * no matter where the Nodes come from. 
	 * The Child Node is deep copied, if necessary. 
	 * @return the Parent Node to allow chaining adding Child Nodes, 
	 * 	unlike the appendChild Method which returns the Child Node. 
	 */
	final static public Node APPEND_CHILD(final Node parent, Node child) {
		final Document owner = parent.getOwnerDocument();
		if (owner != child.getOwnerDocument()) {
			child = owner.importNode(child, true);
		}
		parent.appendChild(child);
		return parent;
	}
	
	/**loads the DOM from the given URI and places it in the Variable HashMap
	 * does only load valid Documents, not e.g. multiple Elements
	 * @param absoluteUri Location of the XML Document
	 * @param root optional Root Name for loading ResultSets
	 * @param separators optional String of Separator Characters (CR/LF first), indicates a separated File. 
	 * @param fieldNames optional Flag whether first Row contains the Field Names
	 * @param fieldDefaults optional Flag whether second Row contains the Field Defaults
	 * @return the XML Document created from the given URI
	 * @throws IOException
	 * @throws SAXException
	 */
	final static public Document LOAD(
		final String absoluteUri,
		final String root,
		final String separators,
		final boolean fieldNames,
		final boolean fieldDefaults)
		throws IOException, SAXException {
		URI uri = null; 
		try {
			uri = new URI(absoluteUri); 
		} catch (final URISyntaxException x) {
			try {
				uri = new URI(ResultSetToSax.SCHEME_FILE+absoluteUri.replace('\\', '/')); 
			} catch (final URISyntaxException e) {
				throw new SAXException(e); 
			}
		}
		return LOAD_RS_OR_XML //URIs cannot handle Spaces in the File Names!!!
		(uri, root, separators, fieldNames, fieldDefaults);
	}
	
	/** loads an XML or ASCII (Separated or Fixed Size) Database File into a DOM 
	 * @param name Name of the Variable to store it to,
	 * 				alternatively the Name of the Root Element could have been chosen
	 * 
	 * @param absoluteUri Location of the XML Document 
	 * @param rootName optional Root Name for loading ResultSets
	 * @param separators optional String of Separator Characters (CR/LF first), indicates a separated File. 
	 * @param fieldNames optional Flag whether first Row contains the Field Names
	 * @param fieldDefaults optional Flag whether second Row contains the Field Defaults
	 * @return a DOM containing the Data from the Table or XML Document at the given URL 
	 * @throws IOException 
	 * @throws SAXException wrapping any SQLException 
	 */
	final static public Document LOAD_RS_OR_XML(
		final URI absoluteUri, 
		final String rootName, 
		final String separators, 
		final boolean fieldNames, 
		final boolean fieldDefaults)
		throws IOException, SAXException {
		return LOAD_RS_OR_XML(absoluteUri.toString(), rootName, separators, fieldNames, fieldDefaults); 
	}
	
	/** loads an XML or ASCII (Separated or Fixed Size) Database File into a DOM 
	 * @param name Name of the Variable to store it to,
	 * 				alternatively the Name of the Root Element could have been chosen
	 * 
	 * @param absoluteUri Location of the XML Document 
	 * @param rootName optional Root Name for loading ResultSets
	 * @param separators optional String of Separator Characters (CR/LF first), indicates a separated File. 
	 * @param fieldNames optional Flag whether first Row contains the Field Names
	 * @param fieldDefaults optional Flag whether second Row contains the Field Defaults
	 * @return a DOM containing the Data from the Table or XML Document at the given URL 
	 * @throws IOException 
	 * @throws SAXException wrapping any SQLException 
	 */
	final static public Document LOAD_RS_OR_XML(
		final String absolutePath,
		final String rootName,
		final String separators,
		final boolean fieldNames,
		final boolean fieldDefaults)
		throws IOException, SAXException {
		final Document ret; 
		if (rootName != null) {
			try {
			ret = ResultSetToSax.RESULTSET_TO_DOM(
					absolutePath, rootName,
					separators, fieldNames, rootName); //make the Attributes distinct!
			} catch (final SQLException x) {
				throw new  SAXException(x);
			}
		} else {
			ret = null; 
		}
		if (ret != null) 
			return ret;
		//no ASCII File
		if (absolutePath.startsWith(ResultSetToSax.SCHEME_FILE)) 
			return DOM_BUILDER.parse(new File(absolutePath.substring(ResultSetToSax.SCHEME_FILE.length()))); 
		return DOM_BUILDER.parse(absolutePath); //
	}
	
	/** Performs a Transformation of the input DOM 
	 * 
	 * @param input DOM to be transformed 
	 * @param trafo Root Node of the transformer XSLT DOM to use
	 * @return the Transformation Result as a new DOM Object
	 * @throws TransformerException wrapping other Exceptions
	 */
	final static public Document TRAFO(final Node input, final Node trafo)
		throws TransformerException {
		return TRAFO(input, NEW_TRAFO(trafo));
	}
	
	/** Performs a Transformation of the input DOM 
	 * 
	 * @param input DOM to be transformed 
	 * @param trafoUri URI of the transformer XSLT Document to use
	 * @return the Transformation Result as a new DOM Object
	 * @throws TransformerException wrapping other Exceptions
	 */
	final static public Document TRAFO(final Node input, final String trafoUri)
		throws TransformerException {
		return TRAFO(input, NEW_TRAFO(trafoUri));
	}
	
	/** Performs a Transformation of the input DOM 
	 * 
	 * @param input DOM to be transformed 
	 * @param transformer the (parsed) Transformer Object to use
	 * @return the Transformation Result as a new DOM Object
	 * @throws TransformerException wrapping other Exceptions
	 */
	final static public Document TRAFO(
		final Node input,
		final Transformer transformer)
		throws TransformerException {
		Document result = NEW_DOCUMENT();
		transformer.transform(new DOMSource(input), new DOMResult(result));
		//somehow this Trafo didn't work properly when Namespaces were involved! 
		return result;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** The SystemID of the ??? */
	protected String systemId;
	
	/** Map of the Variables containing DOMs and Trafos for Reuse */
	protected HashMap variables = new HashMap();
	
	/**
	 * stores the given Value Node with the given Name.  
	 * or tries to load the Node from the given URI
	 * @param name the Name of the Node
	 * @param value the Value associated with the given Name 
	 */
	private void putVariable(final String name, final Node value) {
		variables.put(name, value);
	}
	
	/**
	 * returns the Value of the named Variable 
	 * or tries to load the Node from the given URI
	 * @param name the Name of the Node
	 * @return the DOM Node with the given Name 
	 */
	private Node getVariable(final String name) { //throws IOException, SAXException {
		final Node ret = (Node) variables.get(name);
		if (ret != null) 
			return ret;
		try { //works only for XML Documents, otherwise it will have an arbitrary Root!
			return DOM_BUILDER.parse(getAbsoluteUrl(name)); //
			//ret = LOAD(name, "tmp", "\n\t", false, false); 
		} catch (final Exception x) {
			x.printStackTrace(System.err);
		}
		return null;
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/** Constructor for XslTrafo.	 */
	public XslTrafo() {
	}
	
	/** Constructor for XslTrafo.	 */
	public XslTrafo(final String systemId_) {
		systemId = systemId_;
	}
	
	/** returns a new absolute URL from the given Locator and a (possibly relative) URL */
	private String getAbsoluteUrl(final String url) {
		return GET_ABSOLUTE_URL(
			saxDispatcher.getDocumentLocator().getSystemId(),
			url);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, made public so they are accessible to Scripting and Reflection
	////////////////////////////////////////////////////////////////////////////
	
	/** temporary State on joining multiple Sub-Elements */
	private Node parent;
	
	/** This is the Root Element of the XTL File 	 */
	public void chain(final Attributes atts) { }
	
	/** joins the Variables in all Sub-Elements of this one 
	 * beneath a new Root Element given by the Attribute "root"
	 * and stores it into the VariableStore using the root Name as Key. 
	 */
	public void union(final Attributes atts) { union(atts.getValue(STR_ATTR_ROOT)); }
	
	/** joins the Variables in all Sub-Elements of this one 
	 * beneath a new Root Element given by the Attribute "root"
	 * and stores it into the VariableStore using the root Name as Key. 
	 */
	public void unionEnd() { 
		parent = parent.getParentNode(); 
		if (parent instanceof Document)
			parent = null; 
	}
	
	/** unions the Variables in all Sub-Elements 
	 * beneath a new Root Element given by the Attribute "root"
	 * and stores it into the VariableStore using the root Name as Key. 
	 */
	private void union(final String rootName ) {
		final Document owner;
		if (parent != null) {
			owner = parent.getOwnerDocument(); 
		} else {
			owner = NEW_DOCUMENT(); parent = owner; //.getDocumentElement(); 
			putVariable(rootName, parent);
		}
		final Element elm = owner.createElement(rootName); 
		parent.appendChild(elm); parent = elm; 
	}
	
	/** 'var' Child Element of 'join' 
	 * @param atts the attributes of this Sax Node
	 */
	public void var(final Attributes atts) {
		final String urn = atts.getValue(STR_ATTR_REF);
		final Node node = (Node) getVariable(urn);
		var(node); 
	}
	
	/** 'var' Child Element of 'join' 
	 * @param node the Node to append to the current Root. 
	 */
	protected void var(final Node node) { 
		final Element elm = GET_ELEMENT(node);
		if (parent != null)
			parent.appendChild( //use 'importNode', doesn't work otherwise!
			parent.getOwnerDocument().importNode(elm, true)); 
	}
	
	/** stores the Node Variable determined by the 'source' Attribute 
	 * in the File defined by the 'dest' Attribute.  
	 * @param atts the attributes of this Sax Node
	 */
	public void store(final Attributes atts) throws SAXException, IOException {
		TO_FILE(
			(Node) getVariable(atts.getValue(STR_ATTR_SOURCE)),
			getAbsoluteUrl(atts.getValue(STR_ATTR_DEST)));
	}
	
	/** extracts the given Node from the Document 
	 * and places the Root Node into the Variable HashMap with the given qName
	 * @param atts the attributes of this Sax Node
	 */
	public void xPath(final Attributes atts) throws SAXException, IOException {
		XPATH( //TODO: not implemented yet...
			(Node) getVariable(
					atts.getValue(STR_ATTR_SOURCE)),
					atts.getValue(STR_ATTR_ROOT),
					atts.getValue(STR_ATTR_XPATH));
	}
	
	/** loads the Document and places the Root Node in the Variable HashMap with it's own qName
	 * which is typically it's Table Name
	 * Allowed Attributes are: 
	 * root the Root Element Name 
	 * source a File or Directory Path or a URL 
	 * suffix the File Suffix in the Case of a Directory
	 * 
	 * seps Separator Characters for separated Data
	 * names Flag for separated Data
	 * defaults Flag for separated Data
	 * @param atts the Attributes determining which Document to load. 
	 */
	public void load(final Attributes atts) throws SAXException, IOException {
		final String source = atts.getValue(STR_ATTR_SOURCE); 
		final String root   = atts.getValue(STR_ATTR_ROOT); 
		final boolean colNames    = Boolean.valueOf(atts.getValue(STR_ATTR_NAMES)).booleanValue(); 
		final boolean colDefaults = Boolean.valueOf(atts.getValue(STR_ATTR_DEFAULTS)).booleanValue();
		String seps = atts.getValue(STR_ATTR_SEPS);
		if (seps != null) //transform certain Escape Sequences
			seps  = FilterLookup.ESCAPE2ASCII(seps).toString();
		final File file = new File(source);
		if (file.exists()) {
			if (file.isDirectory()) {
				final String suffix = atts.getValue(STR_ATTR_SUFFIX);
				final File[] files = file.listFiles();
				union(root != null ? root: file.getName()); 
				for(int i = files.length; --i >= 0; ) {
					if (! files[i].getName().endsWith(suffix))
						continue; 
					try {
						load(files[i].getAbsolutePath(), root, seps, colNames, colDefaults);
					} catch(final Exception x) {
						L.n(files[i]).l(" caused the following Exception: ").l(x); 
					}
				}
				unionEnd();
			} else {
				load(source, root, seps, colNames, colDefaults); 
			}
		} else {
			load(source, root, seps, colNames, colDefaults); 
		}
	}
	
	/** loads the Document and places the Root Node in the Variable HashMap with it's own qName
	 * which is typically it's Table Name
	 * @param root optional Root Name for loading ResultSets
	 * @param seps optional String of Separator Characters (CR/LF first), indicates a separated File. 
	 * @param colNames optional Flag whether first Row contains the Field Names
	 * @param colDefaults optional Flag whether second Row contains the Field Defaults
	 */
	protected void load(final String source, String root, final String seps, final boolean colNames, final boolean colDefaults) throws SAXException, IOException {
		String url = getAbsoluteUrl(source); 
		final Element elm =
			GET_ELEMENT(LOAD(url, root, seps, colNames, colDefaults)); 
		if (parent != null)
			var(elm); 
		if (root == null)
			root = elm.getNodeName(); 
		putVariable(root, elm);
	}
	
	/** Outputs the Node selected by the root Attribute from the Variable HashMap 
	 * to System.out.   
	 * @param node the Document or Element to output.
	 */
	public void debug(final Attributes atts) throws SAXException, IOException {
		final Element elm = 
			GET_ELEMENT((Node) getVariable(atts.getValue(STR_ATTR_SOURCE)));
		STREAM(elm, System.out);
		System.out.println(); 
		System.out.println(); 
	}
	
	/** performs the Transformation determined by the Attributes: 
	 * name   = a Variable Name specifying the Trafo 
	 * source = a Url specifying the Trafo 
	 * input  = a Variable Name specifying the Input
	 * @param node the Document or Element to store.
	 */
	public void trafo(final Attributes atts)
		throws TransformerException, SAXException {
		final String name   = atts.getValue(STR_ATTR_NAME);
		final String source = atts.getValue(STR_ATTR_SOURCE);
		final Transformer transformer;
		if (source == null) {
			if (name == null) 
				throw new SAXException("Trafo Element missing both 'name' and 'source'!");
			transformer = (Transformer) variables.get(name);
		} else { //sources != null
			final String absUrl = getAbsoluteUrl(source); 
			transformer = NEW_TRAFO(absUrl);
			if (name != null) 
				variables.put(name, transformer);
		}
		final String strInput = atts.getValue(STR_ATTR_INPUT); 
		final Node input = (Node) getVariable(strInput);
		Element elm = TRAFO(input, transformer).getDocumentElement();
		if (elm != null) {
			String root   = atts.getValue(STR_ATTR_ROOT);
			if (root == null)
				root = elm.getNodeName(); 
			putVariable(root, elm);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods 
	////////////////////////////////////////////////////////////////////////////////
	
	/** Make the Dispatcher available to the Methods of this Class */
	private SaxDispatcher saxDispatcher = new SaxDispatcher(this, false); //true);
	
	/** 
	 * Processes the XML Document at the given URI,  
	 * which describes which Documents to load, merge, transform and output. 
	 */
	public synchronized void process(final String uri)
		throws IOException, SAXException, ParserConfigurationException {
		saxDispatcher.parse(uri);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing Methods 
	////////////////////////////////////////////////////////////////////////////
	
	/** Root Directory for the Test Files */
	private static final String ROOT_DIR = "../../";
	
	/**
	 * Demonstrates streaming a parsed XML File
	 */
	final static public void testIt()
		throws IOException, SAXException, TransformerException {
		testStream();
		testJoin();
		//testFails(); //didn't work!
	}
	
	/**
	 * Demonstrates streaming a parsed XML File
	 */
	private static final void testJoin()
		throws IOException, SAXException, TransformerException {

		//Document cdsArtists = LOAD("E:/Personal/Databases/MusicCollection/CDsArtists.xml");
		Document artists =
			LOAD(
				ROOT_DIR + "Databases/MusicCollection/Artists.xml",
				"",
				null,
				true,
				true);
		Document cds =
			LOAD(
				ROOT_DIR + "Databases/MusicCollection/CDs.xml",
				"",
				null,
				true,
				true);
		Transformer trafo = NEW_TRAFO(ROOT_DIR + "Code/XSL/CDjointArtists.xsl");
		//		Document dbDoc = JOIN   ("DataBase", cds, artists); //dataBase.getOwnerDocument();
		Document dataBase = JOIN(cds, "DataBase", artists, false);
		STREAM(dataBase, System.out);
		System.out.println();
		//Node output = TRAFO(cdsArtists, trafo);
		Node output1 = TRAFO(dataBase.getDocumentElement(), trafo);
		//need a real Document here!
		STREAM(output1, System.out);
		System.out.println();
	}
	
	/**
	 * Demonstrates streaming a parsed XML File
	 */
	private static final void testStream()
		throws IOException, SAXException, TransformerException {
		Document doc =
			LOAD(
				ROOT_DIR + "Databases/MusicCollection/Artists.xml",
				"",
				null,
				true,
				true);
		System.out.println(TO_STRING(doc));
	}
	
	/**
	 * Doesn't work: the Xalan Trafo is too implicit to easily integrate a new SAX Source!
	 * Possibly easier to add a DOM Source? Needn't be parsed anymore, but doesn't work streaming!
	 */
	protected static void testFails()
		throws IOException, SAXException, TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer =
			factory.newTransformer(
				new StreamSource(ROOT_DIR + "Code/XSL/Attribute2Element.xsl"));
		transformer.transform(
			new SAXSource(
				new SaxReader(
					new ResultSetToSax(
						new ResultSetSep(ROOT_DIR
								+ "Databases/MusicCollection/Artists.sep"), "")),
				null),
			new StreamResult(ROOT_DIR + "Code/xsl/Music/example/output.xml"));
	}
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! ^
	 * e.g. java technology.xml.XslTrafo
	 * "E:\Personal\Code\XSL\Music\example\Seal Second 06 Kiss_from_a_Rose.xml"
	 * E:\Personal\Code\XSL\Music\example\SongStyle.xsl
	 * E:\Personal\Code\xsl\Music\example\output.html
	 */
	final static public void trafo(final String[] args) throws IOException
	, SAXException
	, ParserConfigurationException
	, TransformerConfigurationException
	, TransformerException {
		Log.L(args);
		if (args.length == 1) { //process a single Document describing the Inputs, Trafos and Outputs 
			new XslTrafo().process(args[0]);
			return;
		}
		if (args.length == 3) { //read Input, Trafo and Output URI and transform the Input
			TRAFO_FACTORY.newTransformer(new StreamSource(args[1])).transform(
				new StreamSource(args[0]),
				new StreamResult(args[2]));
			return;
		}
		System.out.println(
			"Syntax: \n "
				+ "java technology.xml.XslTrafo inputURL, trafoURL, outputURL \n"
				+ "java technology.xml.XslTrafo trafoChain.xtl \n");
		testIt();
	}
	
	/**
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! 
	 * e.g. java technology.xml.XslTrafo
	 * "E:\Personal\Code\XSL\Music\example\Seal Second 06 Kiss_from_a_Rose.xml"
	 * E:\Personal\Code\XSL\Music\example\SongStyle.xsl
	 * E:\Personal\Code\xsl\Music\example\output.html
	 */
	public static void main(final String[] args) throws Exception { //
		trafo(args);
	}
	
}
