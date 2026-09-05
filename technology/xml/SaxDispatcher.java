/*
 * File Name: SaxDispatcher.java
 * Created on: 05.07.2003
 *
 */
package technology.xml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A SAX handler that dispatches each Element event by reflection to a method of an arbitrary
 * target object, passing the Attributes object as its single parameter.
 *
 * <p>
 * The Method Name corresponds to the Element Name for Start Elements 
 * and qName+"End" for End Elements. 
 * In-between any Text Content is accumulated in a public Stringbuffer, 
 * which can be cleared or manipulated anytime. 
 * 
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * @see graphic.svg.SvgHandler     uses this Class to parse SVG XML Syntax
 * @see technology.xml.XslTrafo    uses this Class to parse custom Syntax to describe XSLT Pipes
 * @see technology.xml.DamlHandler uses this Class to parse DAML Assertions into relational Structures
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:14:09Z
 * digest: 5acbf086ba95b1ebfc0ff6fa9349f47a9510dbccee3662810426cbaa3ab882fe
 * stale: false
 * tags: [code/sax_parsing, code/reflection_dispatch]
 * concepts: [Reflection-Based SAX Dispatcher]
 * facets: {layer: infrastructure, status: legacy, complexity: medium}
 * -->
 */
public class SaxDispatcher 
extends SaxHandler {
	
	/** calls a Method by it's Name 
	 * 
	 * @param methodName Method Name 
	 * @param strict flag whether to raise a SAXException if the Method cannot be found
	 * @param paramTypes the Method Parameter Types
	 * @param params the Method Parameters
	 * @throws SAXException if 
	 * 	the Method cannot be found
	 * 	the Method is not accessible 
	 * 	the Method throws an Exception  
	 */
	final static public void callByReflection(
			String methodName,
			final boolean strict,
			final Object target,  
			final Class[] paramTypes,
			final Object[] params)
	throws SAXException {
		try { //trim the ???
			methodName = methodName.substring(methodName.lastIndexOf(':')+1);
			final Method method = target.getClass().getMethod(methodName, paramTypes);
			method.invoke(target, params);
		} catch (final IllegalAccessException x) {
			x.printStackTrace(System.err);
			throw new SAXException(x);
		} catch (final InvocationTargetException x) {
			x.printStackTrace(System.err);
			throw new SAXException(x);
		} catch (final NoSuchMethodException x) {
			if (strict) {
				x.printStackTrace(System.err);
				//throw new SAXException(x); //
				throw new SAXException("Invalid Element Name '"+methodName+"' detected!");
			}
		}
	}

	/**
	 * Parses the document at the given URI, dispatching each Element event by reflection to
	 * eventHandler through a new {@link SaxDispatcher}.
	 *
	 * @param uri the URI from where to load the document
	 * @param eventHandler the object whose methods are invoked for each Element
	 * @param strict whether to raise a SAXException for an Element with no matching Method
	 */
	final static public void PARSE(
		String uri,
		Object eventHandler,
		boolean strict)
		throws IOException, SAXException, ParserConfigurationException {
		SAX_FACTORY.newSAXParser().parse(
			new InputSource(uri),
			new SaxDispatcher(eventHandler));
	}
	
	/** Constant to get the Method by Reflection */
	private static final Class[] ATTRIBUTE_ARG = { Attributes.class };
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// Member Variables
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Flag whether not declared Elements Starts are allowed */
	public boolean strictStart;
	
	/** Flag whether not declared Elements Stops are allowed */
	public boolean strictEnd;
	
	/** Reference to the Object handling the Sax Element Events */
	private Object eventHandler;

	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Initializing Constructor 
	 * checks whether the given Handler is not null!
	 */
	public SaxDispatcher(Object eventHandler_) {
		this(eventHandler_, false, false, false);
	}
	
	/**
	 * Initializing Constructor 
	 */
	public SaxDispatcher(Object eventHandler_, boolean strictStart_) {
		this(eventHandler_, strictStart_, false, false);
	}
	
	/**
	 * Initializing Constructor 
	 */
	public SaxDispatcher(Object eventHandler_, boolean strictStart_, boolean strictEnd_) {
		this(eventHandler_, strictStart_, strictEnd_, false);
	}
	
	/** 
	 * checks whether the given Handler is not null!
	 * @param eventHandler_ must not be null
	 * @param strictStart_ 
	 * @param strictEnd_
	 * @param collectWhiteSpace_
	 */
	public SaxDispatcher(Object eventHandler_, boolean strictStart_, boolean strictEnd_, boolean collectWhiteSpace_) {
		super();
		if (null == (this.eventHandler = eventHandler_)) {
			throw new NullPointerException("Event Handler must not be null!");
		}
		this.strictEnd = strictEnd_;
		this.strictStart = strictStart_;
		this.collectWhiteSpace = collectWhiteSpace_;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Event Interface ContentHandler: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Flag to prevent the CarryOver of Text Collection across Elements.  	 */
	public boolean carryOverText = true; 
	
	/**
	 * Only dispatches the different Instructions of the TrafoChain Language. 
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(
			final String namespaceURI,
			final String localName,
			final String qName,
			final Attributes atts)
	throws SAXException {
		lastGoodRow = locator.getLineNumber();
		lastGoodCol = locator.getColumnNumber(); 
		callByReflection(qName, strictStart, eventHandler, ATTRIBUTE_ARG, new Object[] { atts });
		if (!carryOverText) buffer.setLength(0); //Text is not transferred into Nodes. 
	}

	/**
	 * Dispatches the Element's end by reflection to a method named qName + "End".
	 *
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(final String uri, final String localName, final String qName)
		throws SAXException {
		callByReflection(qName+"End", strictEnd, eventHandler, null, null); // 
		if (!carryOverText) buffer.setLength(0); //Text is not transferred out of Nodes. 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * StringBuffer to collect Text Data within Elements
	 * Should rather be a Stack of String Buffers 
	 * to collect all Text both preceding, mixed within and trailing inner Elements. 
	 * Usually evaluated at the Element End Event. 
	 */
	final public StringBuffer buffer = new StringBuffer(); 
	
	/**
	 * Appends the given character range to {@link #buffer}.
	 *
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(final char[] ch, final int start, final int length)
	throws SAXException {
		buffer.append(ch, start, length);
	}

	/**
	 * Appends the given whitespace to {@link #buffer} only when {@code collectWhiteSpace} is set.
	 *
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(final char[] ch, final int start, final int length)
	throws SAXException {
		if (collectWhiteSpace) {
			buffer.append(ch, start, length); 
		}
	}
	
	/** @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)	 */
/*	public void processingInstruction(String target, String data)
		throws SAXException {
		callByReflection(target, strictEnd, new Class[] { String }, new Object[] { data } );
	}
*/
}
