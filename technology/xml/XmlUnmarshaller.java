/*
 * File Name: XmlUnmarshaller.java
 * Created on: 23.04.2004
 *
 */
package technology.xml;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import reflect.Accessor;
import streamIO.Log;
import technology.xml.test.Nachricht;
import function.IFunction;

/**
 * Unmarshals XML into an object graph by reflection, matching Element and Attribute names
 * directly to member variable names of the respective objects.
 *
 * <p>The Instances are derived either from the Member Variable Types
 * (when they are concrete and final)
 * or from a generic Factory which maps the Element Names to Instances.
 * (possibly you need to consider the whole Path, not just the Element Name)
 *
 * The corresponding Java Value Classes can be generated either 
 * using Castor (www.apache.org) or by applying an XSLT to the Schema File. 
 * For aquiring one Java File per Class unfortunately 
 * you have to split up your Schema into one Schema File per XML/Java Class! 
 * 
 * Design Decisions / Implementation Details:
 * Since the Control is exerted by the SAX Parser, 
 * this Class has to use a Stack to keep track of the current Object 
 * and it's Parent Objects. 
 * The Root Object has to be given in the Constructor 
 * or retrieved from the Object Factory. 
 * 
 * An open Question is how to handle an Element with String Contents.
 * Normally you would map an Element without Attributes or Sub-Elements to a String. 
 * But an Element with Attributes and/or Sub-Elements 
 * should have a Default Property of Type String (similar to the VB Tag Element). 
 * Castor maps this to the 'Content' Variable
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
 * mtime: 2026-09-05T11:15:36Z
 * digest: 1f8617c789746218e8c139f985a8e4b6d1975507d0fd03454bb14c95279e8b25
 * stale: false
 * tags: [code/xml_deserialization, code/reflection_dispatch]
 * concepts: [Reflection-Based XML Unmarshaller]
 * facets: {layer: infrastructure, status: broken, complexity: high}
 * -->
 */
public class XmlUnmarshaller extends SaxHandler {
	
	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(XmlUnmarshaller.class, 0);
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Default Name for the Variable to contain the XML Element Text	 */
	static final String ELEMENT_CONTENT_NAME_DEFAULT = "Content";
	
	/** Initialized to the Root, pushed onto the Stack	 */
	Object currObject; //could also use stack.peek()
	
	/** Initialized to the Root, pushed onto the Stack	 */
	StringBuffer currBuffer = new StringBuffer(); //could also use stack.peek()
	
	/** Initialized to the Root, pushed onto the Stack	 */
	Class currClass;
	
	/** Initialized to the Root, pushed onto the Stack	 */
	final String rootName;
	
	//Design Decision to use two Stacks instead of one with an Association
	//actually could use three Stacks with currClass 
	//or alternatively a Record Object on the Stack containing Object, Class and Buffer
	
	/** Initialized to the Root, pushed onto the Stack	 */
	final Stack objectStack = new Stack();
	
	/** StringBuffer to collect Text Data within Elements
	 * Should rather be a Stack of String Buffers 
	 * to collect all Text both preceding, mixed within and trailing inner Elements. 
	 */
	final Stack bufferStack = new Stack();
	
	/** the Factory to create Instances from Element Names 	 */
	final IFunction factory;
	
	/** Name for the Variable to contain the XML Element Text
	 * if not given (null) or empty(""), the Text Content is ignored.
	 */
	public String elementContentName = ELEMENT_CONTENT_NAME_DEFAULT;
	
	/**
	 * initializing Constructor
	 * one of the two Parameters can be null
	 * @param root_
	 */
	public XmlUnmarshaller(final Object root_) {
		this(root_, null);
	}

	/**
	 * initializing Constructor
	 * one of the two Parameters can be null
	 * @param factory_
	 */
	public XmlUnmarshaller(final IFunction factory_) {
		this(null, factory_);
	}

	/**
	 * initializing Constructor 
	 * one of the two Parameters can be null 
	 * @param root_ the Root Object of the XML Stream to parse and fill into 
	 * @param factory_ the Factory to create Instances from Element Names 
	 */
	public XmlUnmarshaller(final Object root_, final IFunction factory_) {
		this(root_, factory_, null); 
	}

	/**
	 * initializing Constructor 
	 * one of the two Parameters can be null 
	 * @param root_ the Root Object of the XML Stream to parse and fill into 
	 * @param factory_ the Factory to create Instances from Element Names 
	 */
	public XmlUnmarshaller(final Object root_, final IFunction factory_, final String name_) {
		this.rootName = name_;
		this.currObject = root_;
		this.currClass = currObject.getClass(); 
		this.factory = factory_;
		if ((root_ == null) && (factory_ == null)) {
			throw new InvalidParameterException(
				"Either the Root Object ("
					+ root_
					+ ") or the Factory ("
					+ factory_
					+ ") MUST be given!");
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * parses the given Stream
	 * @param uri
	 */
	public void parse(final InputSource source)
		throws ParserConfigurationException, SAXException, IOException {
		try {
			if (parser == null) {
				//SAXParserFactory factory = SAXParserFactoryImpl.newInstance();  //
				//parser = factory.newSAXParser(); //  
				parser = SAX_FACTORY.newSAXParser();
			}
			parser.parse(source, this);
		} catch (SAXException x) {
			System.out.print("outer:");
			x.printStackTrace();
			Exception inner = x.getException();
			if (null == inner) { //native Exception
				StringBuffer messageWithLocation = new StringBuffer(x.getMessage());
				messageWithLocation.append(" in Line " + lastGoodRow); //locator.getLineNumber()); 
				messageWithLocation.append(" at Column " + lastGoodCol);
				//locator.getColumnNumber());
				throw new SAXException(messageWithLocation.toString()); //rethrow it
			}
			System.out.print("inner:");
			inner.printStackTrace();
			throw new SAXException(x); //inner); 
		}
	}

	/**
	 * parses the given 
	 * @param uri
	 */
	public void parse(final String uri) throws ParserConfigurationException, SAXException, IOException {
		parse(new InputSource(uri));
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Event Interface ContentHandler: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * Default for initial Length
	 */
	int arrayLength = 7; 
	
	Accessor accessor = new Accessor(); 
	
	/**
	 * Only dispatches the different Instructions of the TrafoChain Language. 
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts)
	throws SAXException {
		L.enter().l("namespaceURI=").l(namespaceURI).l(" localName=").l(localName).l("	qName=").l(qName).l("	atts=").l(atts);
		//check Type of the Attribute with given Name from same Package as current Class...
		Object obj = null;
		if (currObject != null) try {
			//if ((stack.size() == 0) && currClass.getName().equals(qName)) {
			if ((objectStack.size() == 0) && (qName.equals(rootName))) {
				return; }
			/*/reuse this Object's Field
			obj = accessor.getFieldOrMethod(currObject, currClass, qName);
			final Class fldCls = accessor.retCls[0]; 
			if(fldCls != null) {
				if (fldCls.isArray()) { //TODO: Arrays verlangen Sonderbehandlung
				} else if(obj == null){ //create a new, empty Object
					obj = accessor.retCls[0].newInstance(); 
				} 
			}
			*/
			if((obj == null) && (factory != null)) {
				obj = factory.Map(qName); } //then create a new Sub-Object from the Factory 
			if (obj != null) {
				accessor.setOrAddFieldOrMethod(currObject, currClass, qName, obj); 
				currClass = obj.getClass();
				fillAttributes(obj, atts); //handing over obj to save another Test for null
			}
		//} catch (final ClassNotFoundException ignored) {
		} catch (final IllegalAccessException ignored) {
			L.n(ignored); 
		} catch (final InstantiationException ignored) {
			L.n(ignored); 
		} catch (final NoSuchFieldException ignored) {
			L.n(ignored); 
		}
		objectStack.push(currObject); currObject = obj;
		bufferStack.push(currBuffer); currBuffer = new StringBuffer(); 
		//buffer.setLength(0); //Text is not transferred into Nodes. 
	}

	/**Only in the End the Buffer is filled enough so it can be parsed properly! 
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)	 */
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		L.enter().l("uri=").l(uri).l("	localName=").l(localName).l("	qName=").l(qName);
		//append to Default Text Property of Object
		
		final boolean bufferSet;  
		if (currObject != null) {
			bufferSet = setBuffer(qName); 
		} else {
			bufferSet = false; 
		}
		
		//pop current Object from Stack
		if (objectStack.size() <= 0) {
			currObject = null;
			return; }
		currObject = objectStack.pop(); 
		if (null != currObject) { 
			currClass = currObject.getClass();
			if (!bufferSet) {
				if (!setBuffer(qName)) {
					if (!accessor.setOrAddFieldOrMethod(currObject, currClass, qName, currBuffer.toString(), String.class)) {
						try{ accessor.findSetMethod(currObject, currClass, Accessor.STR_SET+qName, currBuffer.toString()); 
						} catch(final Exception ignored) { 
							setBuffer(elementContentName); 
						}
					}
				} 
			}
		}
		currBuffer = (StringBuffer) bufferStack.pop();
	}

	private boolean setBuffer(final String qName) throws NumberFormatException {
		Class argType = String.class;
		final String strValue;    
		Object newValue = strValue = currBuffer.toString();
		argType = (Class) accessor.getFieldOrMethod(currObject, currClass, qName, true);
		if (argType == String.class) { //   newValue = newValue.toString(); 
		} else if (argType == int  .class){ newValue = Integer.decode(strValue);
		} else if (argType == long .class){ newValue = Long.decode(strValue);
		} else if (argType == float.class){ newValue = Float.valueOf(strValue);
		// TODO: LOGIC: duplicate check of "argType == long.class" (already handled two branches
		// above); almost certainly meant "argType == double.class". As written, a double-typed
		// field is never converted here and falls through to the no-op else branch, leaving
		// newValue as the raw String instead of a Double.
		} else if (argType == long .class){ newValue = Double.valueOf(strValue);
		} else if (argType == Date .class){ newValue = new Date(Date.parse(newValue.toString()));
		} else {
		}
		return accessor.setOrAddFieldOrMethod(currObject, currClass, qName, newValue, argType);
	}

	/** fills the Members of an Object with the (primitive Type) Attributes	 */
	private void fillAttributes(final Object currObject, final Attributes atts) 
	throws NegativeArraySizeException, InstantiationException, IllegalAccessException, NoSuchFieldException {
		for (int i = atts.getLength(); --i >= 0;) {
			final String value = atts.getValue(i);
			final String qName = atts.getQName(i);
			accessor.setOrAddFieldOrMethod(currObject, currClass, qName, value); 
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////

	/**To enable Text Concatenation across Child Elements, 
	 * concatenate directly with the Contents of the current Element 
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)	 
	 */
	public void characters(char[] ch, int start, int length
	) throws SAXException {
		currBuffer.append(ch, start, length);
	}

	/**
	 * Appends the given whitespace to the current Element's text buffer, only when
	 * {@code collectWhiteSpace} is set.
	 *
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		if (collectWhiteSpace) {
			//append to Default Text Property of Object 
			currBuffer.append(ch, start, length);
		}
	}

	/** @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)	 */
	/*	
	public void processingInstruction(String target, String data)
			throws SAXException {
			callByReflection(target, strictEnd, new Class[] { String }, new Object[] { data } );
		}
	*/

	////////////////////////////////////////////////////////////////////////////
	/// #region : main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates streaming a parsed XML File
	 */
	final static public void testIt()
		throws IOException, SAXException, ParserConfigurationException {
		unmarshal(
			new String[] { "E:\\MHeuer\\Code\\Java\\technology\\xml\\test\\zkdbInternBeispiel.xml" });
	}

	/**
	 * Doesn't work: the Xalan Trafo is too implicit to easily integrate a new SAX Source!
	 * Possibly easier to add a DOM Source? Needn't be parsed anymore, but doesn't work streaming!
	 */
	final static public void unmarshal(final String[] args)
		throws IOException, SAXException, ParserConfigurationException {
		if (args.length == 1) { //read a single Document describing the Trafos
			Object ret = new Nachricht();
			XmlUnmarshaller unmarshaller = new XmlUnmarshaller(ret, new PackageFactory(Nachricht.class.getPackage().getName()), "Nachricht");
			unmarshaller.parse(args[0]);
			return;
		}
		System.out.println(
			"Syntax: \n "
				+ "java technology.xml.XslTrafo inputURL, trafoURL, outputURL \n"
				+ "java technology.xml.XslTrafo trafoChain.xml \n");
		testIt();
	}

	/**
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! ^
	 * e.g. java technology.xml.XslTrafo
	 * "E:\Personal\Code\XSL\Music\example\Seal Second 06 Kiss_from_a_Rose.xml"
	 * E:\Personal\Code\XSL\Music\example\SongStyle.xsl
	 * E:\Personal\Code\xsl\Music\example\output.html
	 */
	/**
	 * Logs the given arguments and delegates to {@link #unmarshal(String[])}.
	 *
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! ^
	 * e.g. java technology.xml.XslTrafo
	 * "E:\Personal\Code\XSL\Music\example\Seal Second 06 Kiss_from_a_Rose.xml"
	 * E:\Personal\Code\XSL\Music\example\SongStyle.xsl
	 * E:\Personal\Code\xsl\Music\example\output.html
	 */
	public static void main(String[] args) throws Exception { //
		Log.L(args);
		unmarshal(args);
	}

}

/**
 * Creates a new instance of the class named {@code packageName + "." + arg} by reflection,
 * used by {@link XmlUnmarshaller} to instantiate sub-objects from Element names.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:15:36Z
 * digest: aeb32e1b8a8c689738c9c9750d68bc586403e4bfa0d47517cb6ff1e2dd47f33d
 * stale: false
 * tags: [code/reflection_dispatch]
 * concepts: [Reflective Class Factory]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
class PackageFactory
implements IFunction {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(PackageFactory.class, 1);

	/** The Java package prepended to an Element name to resolve its Class. */
	final public String packageName;

	/**
	 * Creates a factory that resolves Element names within the given Java package.
	 * @param packageName_
	 */
	public PackageFactory(final String packageName_) {
		this.packageName = packageName_; }

	/**
	 * Instantiates {@code packageName + "." + arg}, returning {@code null} when the class
	 * cannot be found or instantiated.
	 *
	 * @see function.IFunction#Map(java.lang.Object)
	 */
	public Object Map(final Object arg) {
		try {
			return Class.forName(packageName+'.'+arg.toString()).newInstance();
		} catch (final ClassNotFoundException ignored) { L.n(ignored); 
		} catch (final IllegalAccessException ignored) { L.n(ignored); 
		} catch (final InstantiationException ignored) { L.n(ignored); 
		}
		return null; 
	}

	/**
	 * Always returns {@code true}; this factory can attempt to process any Object.
	 *
	 * @see function.IFunction#canProcess(java.lang.Object)
	 */
	public boolean canProcess(Object arg) {
		return true;
	}

	/**
	 * Returns this factory unchanged; there is nothing to simplify.
	 *
	 * @see function.IFunction#simplify()
	 */
	public IFunction simplify() { return this; }

	/**
	 * Always returns {@code null}; indexed mapping is not supported.
	 *
	 * @see function.IProcessor#MapAt(java.lang.Object)
	 */
	public Object MapAt(Object arg) { return null; }
}

