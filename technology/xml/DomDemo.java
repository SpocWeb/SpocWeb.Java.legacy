/**
 * File  Name: DomDemo.java
 * Created on: 11.02.2003
 */
package technology.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Demonstrates building and reading a DOM: parsing it from a file, constructing one in code,
 * and transforming it with XSLT.
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
 * mtime: 2026-09-05T11:12:52Z
 * digest: 4173c457ab550b0b15d50206d5c2a76444e000d60231212fb663e9de6b7f40f9
 * stale: false
 * tags: [code/xml_parsing]
 * concepts: [DOM Parsing Demo]
 * facets: {layer: test, status: legacy, complexity: low}
 * -->
 */
public class DomDemo {

	private static final String STR_MAX_ROWS = "MaxRows";
	private static final String STR_SEARCH_MODE = "SearchMode";
	private static final String STR_DELETED = "ShowDeleted";
	private static final String STR_LEVEL = "Level";
	/**
	 * Parses, builds and transforms sample DOM documents to demonstrate this class's helpers.
	 *
	 * @param args unused
	 * <!-- docstate
	 * tags: [code/xml_parsing]
	 * concepts: [Demo Entry Point]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */
	public static void main(String[] args) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder = factory.newDocumentBuilder();
		//parse a DOM from a File
		Document doc = builder.parse("./example.xml");
		testIt2(doc);
		//build a DOM by Code
		doc = XslTrafo.NEW_DOCUMENT(); //creates an empty DOM
		Element rootElm = doc.createElement("tagName"); 
		doc.appendChild(rootElm);
		rootElm.setAttribute("name", "value");
		rootElm.appendChild(doc.createTextNode("StringData"));
		//parse a DOM from a File
		doc = builder.parse("E:\\Personal\\Databases\\MusicCollection\\Artists.xml");
		testIt(doc);
		System.out.println(XslTrafo.TO_STRING(doc)); 
	}
	
	/** tolerantly returns "" instead of null or a NullPointerException
	 * for the Value of an Attribute.  
	 * <!-- docstate
	 * tags: [code/xml_parsing]
	 * concepts: [DOM Attribute Lookup]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */	
	final static public String GET_ATTRIB_VALUE
	(final NamedNodeMap atts, final String key) throws Exception {
		if (atts == null) {
			return ""; }
		final Node attr = atts.getNamedItem(key); //Runtime Type is actually Attr
		if (attr == null) {
			return ""; }
		final String ret = attr.getNodeValue(); 
		if (ret == null) {
			return ""; }
		return ret; 
	}
	
	/** 
	 * Takes a DOM Object and applies the XSLT to it. 
	 * DOMSource works fine, without Side Effects. 
	 * The only Disadvantage is that it cannot be used streaming. 
	 * 
	 * DOM is only an Interface. 
	 * To implement Operations, the DOM Interface must be extended 
	 * or all Operations are delegated. 
	 * <!-- docstate
	 * tags: [code/xml_parsing]
	 * concepts: [DOM Traversal Demo]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */	
	final public static void testIt2(Document doc) throws Exception {
		final NamedNodeMap atts = doc.getDocumentElement().getAttributes();
		final String level = atts.getNamedItem(STR_LEVEL).getNodeValue();
		final String showDeleted = atts.getNamedItem(STR_DELETED).getNodeValue();
		final String searchMode = atts.getNamedItem(STR_SEARCH_MODE).getNodeValue();
		final String maxRows = atts.getNamedItem(STR_MAX_ROWS).getNodeValue();
		System.out.println("@["+STR_LEVEL+"]="+level); 
		System.out.println("@["+STR_DELETED+"]="+showDeleted); 
		System.out.println("@["+STR_SEARCH_MODE+"]="+searchMode); 
		System.out.println("@["+STR_MAX_ROWS+"]="+maxRows); 
		NodeList nl = doc.getElementsByTagName("*");
		for (int i=0; i<nl.getLength(); i++) {
			//short nodeType;
			final Node n = nl.item(i);
			final String strName = n.getNodeName();
			final String strValue = getElemText(n); //n.getNodeValue();
			System.out.println("["+strName+"]="+strValue); 
		}
	}

	static final String getElemText(final Node node) {
	  NodeList nl = node.getChildNodes();
	  String strName = "";
	  Node n;
	  short nodeType;

	  for (int i=0; i<nl.getLength(); i++) {
		n = nl.item(i);

		nodeType = n.getNodeType();
		switch (nodeType) {
		  case Node.TEXT_NODE:
			strName = n.getNodeValue();
			break;
		}
	  }
	  return strName;
	}

	/** 
	 * Takes a DOM Object and applies the XSLT to it. 
	 * DOMSource works fine, without Side Effects. 
	 * The only Disadvantage is that it cannot be used streaming. 
	 * 
	 * DOM is only an Interface. 
	 * To implement Operations, the DOM Interface must be extended 
	 * or all Operations are delegated. 
	 * <!-- docstate
	 * tags: [code/xml_parsing]
	 * concepts: [DOM Traversal Demo]
	 * facets: {layer: test, status: legacy, complexity: low}
	 * -->
	 */	
	public static void testIt(Node dom) throws Exception {
//		Document doc; // = 
//		Element res = NEW_ELEMENT(dom, "result"); 
//		DOMResult result = new DOMResult(res); //res = (Element) result.getNode(); //casting necessary!
		StreamResult stream = new StreamResult("E:\\Personal\\Code\\xsl\\Music\\example\\output.xml"); 
		TransformerFactory factory = TransformerFactory.newInstance(); 
		Transformer transformer = factory.newTransformer(new StreamSource("E:\\Personal\\Code\\XSL\\Attribute2Element.xsl"));
		transformer.transform( new DOMSource(dom), stream); // result);
		//stream.
//		System.out.println(res.toString()); 
//		DOMBuilder builder = new DOMBuilder(doc);
//		builder.
	}
	
}
