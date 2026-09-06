package streamIO.object.integer;

import graphs.KeyValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import streamIO.IDeserializer;
import streamIO.object.AStreamIn;
import streamIO.object.parser.StreamOutXML;
import streamIO.object.parser.XMLFormatter;
import stringOp.parser.Scanner;

/**
 * Title:        XMLInputStream<p>
 * Description:
 * Methods to read Objects from an XML streamIO
 * and static Methods to set Values in Arrays and Objects. <p>
 *
 * @see XMLScanner is used to parse the Input streamIO.
 * @see streamIO.object.parser.XMLStreamIn is nearly identical 
 *
 * Copyright:    Copyright (c) <p>
 * Company:      Matthias Heuer<p>
 * @author		 Matthias Heuer
 * @version 1.0
 * @deprecated since it uses XMLScanner; use streamIO.Object.Parser.XMLStreamIn instead!
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:56:34Z
 * digest: f6b565c8a6c77a43f4312cc46e9c55313c7848efc99d6f62b55a8b56ac416259
 * stale: false
 * tags: [code/parsing, code/xml]
 * concepts: [XML/HTML Parsing]
 * facets: {layer: utility, status: broken, complexity: medium}
 * -->
 */
public class XMLInputStream
extends AStreamIn
implements IDeserializer {
	
	///////////////////////////////////////////////////////////////////////////////
	//	static Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Reads the textual Representation of an Object from a streamIO and returns it.
	 * The Tag Name is ignored, no Containing Class is assumed.
	 */
	public static Object fromString(final String arg)
	throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
		StringBufferInputStream IS = new StringBufferInputStream(arg);
		try{XMLInputStream XIS = new XMLInputStream(IS);
			return XIS.fromXML(); } //should never happen!
		catch (IOException e) { throw new IllegalAccessException(e.toString()); }
	}

	/**Reads the textual Representation of an Object from a streamIO and returns it.
	 * The Tag Name is ignored, no Containing Class is assumed.
	 */
	public static Object fromStringAt(final Object arg, final String str) {
		StringBufferInputStream IS = new StringBufferInputStream(str);
		XMLInputStream XIS = null;
		try{XIS = new XMLInputStream(IS); } catch(IOException x) { } //should never happen!
		return XIS.loadItem(arg); }

	/**Error Message String Constant	  */
	final static public String STR_ERR_NO_PRIMITIVE_TYPE = "Not of primitive Type:";

	/**Sets the Array Element with the given Index to the Value in the String
	 * not possible to do it more elegant, because the set() Method
	 * only considers the primitive Wrappers, not String
	 *
	 * Design Decisions:
	 * faster to use setXXX, because no new Objects have to be created and destroyed!
	 */
	final static public void setPrimitiveArray(final Object arg, final int Index, final String Value) {
		final Class myClass = arg.getClass().getComponentType();
		if (! myClass.isPrimitive()) throw new AbstractMethodError(STR_ERR_NO_PRIMITIVE_TYPE + myClass.toString()); //
		if (myClass == Character.TYPE) { Array.setChar  (arg, Index, Value.charAt(0)); return; }
		if (myClass == Integer  .TYPE) { Array.setInt   (arg, Index, Integer.parseInt	(Value)); return; }
		if (myClass == Long     .TYPE) { Array.setLong  (arg, Index, Long	.parseLong	(Value)); return; }
		if (myClass == Byte     .TYPE) { Array.setByte  (arg, Index, Byte	.parseByte	(Value)); return; }
		if (myClass == Short    .TYPE) { Array.setShort (arg, Index, Short	.parseShort	(Value)); return; }
		if (myClass == Float    .TYPE) { Array.setFloat (arg, Index, Float	.parseFloat	(Value)); return; }
		if (myClass == Double   .TYPE) { Array.setDouble(arg, Index, Double	.parseDouble(Value)); return; }
		if (myClass == Boolean  .TYPE) { Array.set      (arg, Index, new Boolean(Value)); return; }
	}

	/**Sets the specified Field of the Object arg to the Value in the String
	 *
	 * Design Decisions:
	 * faster to use setXXX, because no new Objects have to be created and destroyed!
	 */
	final static public void setPrimitiveField(Object arg, Field fld, String Value)
		   throws IllegalAccessException {
		Class myClass = fld.getType();
		if (! myClass.isPrimitive()) throw new AbstractMethodError(STR_ERR_NO_PRIMITIVE_TYPE + myClass.toString());
		if (myClass == Character.TYPE) { fld.setChar  (arg, Value.charAt(0)); return; }
		if (myClass == Integer  .TYPE) { fld.setInt   (arg, Integer.parseInt   (Value)); return; }
		if (myClass == Long     .TYPE) { fld.setLong  (arg, Long   .parseLong  (Value)); return; }
		if (myClass == Byte     .TYPE) { fld.setByte  (arg, Byte   .parseByte  (Value)); return; }
		if (myClass == Short    .TYPE) { fld.setShort (arg, Short  .parseShort (Value)); return; }
		if (myClass == Float    .TYPE) { fld.setFloat (arg, Float  .parseFloat (Value)); return; }
		if (myClass == Double   .TYPE) { fld.setDouble(arg, Double .parseDouble(Value)); return; }
		if (myClass == Boolean  .TYPE) { fld.set      (arg, new Boolean(Value)); return; }
	}

	///////////////////////////////////////////////////////////////////////////////
	//  static Variables
	///////////////////////////////////////////////////////////////////////////////

	/**Error Message String Constant	  */
	final static public String STR_ERR_CLASS_NOT_ALLOWED = "Class not permitted by the XML Deserialization allow-list: ";

	/**Exact Class Names that {@link #fromXML()} and friends are allowed to load and
	 * instantiate from untrusted XML, in addition to {@link #ALLOWED_PACKAGE_PREFIXES}.
	 * Everything not listed here (or covered by a listed Package Prefix) is denied.
	 * Callers that need more Types have to add them explicitly.	 */
	final static public java.util.Set ALLOWED_CLASS_NAMES =
		java.util.Collections.synchronizedSet(new java.util.HashSet(java.util.Arrays.asList(new String[]{
			"java.lang.String" , "java.lang.Boolean", "java.lang.Character",
			"java.lang.Byte"   , "java.lang.Short"  , "java.lang.Integer"  ,
			"java.lang.Long"   , "java.lang.Float"  , "java.lang.Double"   ,
			"java.lang.Number" })));

	/**Package Name Prefixes whose Classes may be loaded and instantiated from untrusted XML.
	 * Defaults to this Corpus' own Model Packages only; add further Prefixes deliberately.	 */
	final static public java.util.Set ALLOWED_PACKAGE_PREFIXES =
		java.util.Collections.synchronizedSet(new java.util.HashSet(java.util.Arrays.asList(new String[]{
			"streamIO." , "graphs." })));

	/**Tests the given Class Name against {@link #ALLOWED_CLASS_NAMES}
	 * and {@link #ALLOWED_PACKAGE_PREFIXES}.	 */
	final static private boolean isAllowedClassName(final String name) {
		if (ALLOWED_CLASS_NAMES.contains(name)) return true;
		synchronized (ALLOWED_PACKAGE_PREFIXES) {
			for (java.util.Iterator it = ALLOWED_PACKAGE_PREFIXES.iterator(); it.hasNext(); ) {
				if (name.startsWith((String) it.next())) return true; } }
		return false; }

	/**Resolves a Class Name read from (untrusted) XML, but only if the allow-list permits it.
	 * Array Types are unwrapped to their Component Type; primitive Component Types are always allowed.
	 * @throws ClassNotFoundException if the Name is not permitted or cannot be resolved.	 */
	final static public Class checkedForName(final String name) throws ClassNotFoundException {
		if (name == null) throw new ClassNotFoundException(STR_ERR_CLASS_NOT_ALLOWED + name);
		int dim = 0;
		while (dim < name.length() && name.charAt(dim) == '[') dim++; //unwrap Array Types
		String elem = name.substring(dim);
		if (dim > 0) {
			if (elem.length() == 1) return Class.forName(name); //primitive Component Type
			if (elem.length() > 2 && elem.charAt(0) == 'L' && elem.endsWith(";"))
				elem = elem.substring(1, elem.length() - 1); }
		if (!isAllowedClassName(elem))
			throw new ClassNotFoundException(STR_ERR_CLASS_NOT_ALLOWED + name);
		return Class.forName(name); }

	///////////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////////

	/**Input streamIO	 */
	XMLScanner scan;

	/**List of all Objects that have been ore are being synchronized
	 * By creating this Object all Serialization is tracked and cycles avoided.
	 * Additionally the Creation of Duplicates is prevented and thus
	 * exactly the same structure is restored.
	 * Not clearing this List allows saving serialization
	 * of already serialized Objects (shared between Trees)
	 */
	private ArrayList Cache;
	
	/** Reference to the current Object	 */
	protected Object currItem = EOI;
	
	///////////////////////////////////////////////////////////////////////////////
	//  Constructors
	///////////////////////////////////////////////////////////////////////////////

	/**Initializing Constructor	 */
	public XMLInputStream(InputStream IS) throws IOException {
		this.scan = new XMLScanner(IS); }

	/**Initializing Constructor	 */
	public XMLInputStream(XMLScanner scan) {
		this.scan = scan; }

	/** Returns a new Instance of this Parser Class using the given InPut streamIO	 */
	public IDeserializer newInstance(InputStream In) throws IOException { return new XMLInputStream(In); }

	///////////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Clears the Object Cache and breaks Reference Tracking	 */
	public void clearCache() { Cache = null; }

	/**Checks whether an Pair with the given Name follows
	 * and returns the Pair Value as a String.
	 */
	final public String checkPair (String Name) throws IOException {
		if (scan.currXMLToken != XMLScanner.XML_TAG_ATTRIBUTE) scan.nextXmlToken();
		KeyValuePair assoc = (KeyValuePair) scan.Result;
		if (!Name.equals(assoc.key))
			throw new AbstractMethodError(Scanner.STR_ERR_EXPECTED + Name + Scanner.STR_ERR_OCCURRED + assoc.key);
		scan.nextXmlToken();
		return (String) assoc.val; }

	/**Returns the minimum Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 */
	public long availAble() {
		return (scan.currXMLToken == XMLScanner.XML_TAG_EOF) ? 0 : 1; }
	
	/**Returns the current Object	 */
	public Object currItem() { return currItem; }
	
	/**Returns the next Object: ++, postIncremental.	 */
	public Object nextItem() {
		try { return currItem = fromXML(); }
		catch(IOException e) 			{ throw new AbstractMethodError(e.toString()); }
		catch(IllegalAccessException e) { throw new AbstractMethodError(e.toString()); }
		catch(NoSuchFieldException   e) { throw new AbstractMethodError(e.toString()); }
		catch(ClassNotFoundException e) { throw new AbstractMethodError(e.toString()); }
		catch(InstantiationException e) { throw new AbstractMethodError(e.toString()); }
	}
	
	/**Not supported by this streamIO: always returns -1 (no marking capability).
	 * @see streamIO.IMarkAble#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }

	/**Not tracked by this streamIO: always returns 0.
	 * @see streamIO.IAvailAble#getPosition()	 */
	public long getPosition() { return 0; } //scan.; }
	
	/**Reads the textual Representation of an Object from a streamIO and returns it.
	 * The Tag / Object Name is ignored, no Containing Class is assumed.
	 */
	final public Object fromXML()
		throws InstantiationException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException {
//		String Name = scan.Result.toString(); //read Name, already preread and not used
		Class fClass = checkedForName(checkPair(XMLFormatter.STR_TYPE)); //only allow-listed Types, see #checkedForName
		return fromXMLField(fClass, null, null); }

	/**Reads the textual Representation of an Object currField from a streamIO and returns it.
	 * Container Object and Field Object for this Container can be null
	 * or passed in for possibly reusing the Field.
	 */
	private final Object fromXMLField(Class fClass, Field currField, Object Container)
		throws InstantiationException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException {
		int ID = -1;
		Object inner = null;
		boolean read = false;
		if (scan.currXMLToken == XMLScanner.XML_TAG_ATTRIBUTE) { //read an optional ID
			//check whether it is an ID Attribute...
			ID = Integer.parseInt(checkPair(XMLFormatter.STR_ID));
			if (Cache == null) {
				Cache = new ArrayList(); //Create it on Demand
				Cache.add(0, null); } //for restoring 0 denoting 'null'
			if (ID < Cache.size()) inner = Cache.get(ID); //ID must be >= 0 !
		} //try to reuse the existing Object otherwise create new Instance
		if (inner == null) { //no Object identified by ID
			if (currField != null) inner = currField.get(Container); //fromXML(fClass ,currField, Container); //try to reuse existing Member
			if (fClass.isArray()) { //cannot reuse variable Size Objects == Arrays
				int Length  = Integer.parseInt((String) scan.Result)+1;
				int Len2 = 0; if (inner != null) Len2 = Array.getLength(inner);
				if (Length != Len2) { //try to reuse as many Elements as possible, also in Arrays
					Object arr = Array.newInstance(fClass.getComponentType(), Length);
					int MinLength = Math.min(Length, Len2);
					if (MinLength > 0) System.arraycopy(inner, 0, arr, 0, MinLength);
					inner = arr; }
				inner = ArrayFromXMLAt(inner, fClass); //read 1st Element and determine Length from Name
			} else { //unwrapping non-primitive Type from String
				if (inner == null) inner = fClass.newInstance();  //create a new Object, requires an empty Constructor!
				read = true;
		} //set it with ID, in case some Object IDs are missing (cut out etc.)
		if (Cache != null && ID >= 0) { //the Cache has to be filled...
			Cache.ensureCapacity(ID + 1);
			while (Cache.size() <= ID) Cache.add(null); //pad with null Placeholders up to ID
			Cache.set(ID, inner); }
		if (read) fromXMLAt(inner, fClass); } //...before the Recursion is entered, because the Elements may be used.
		if (currField != null) currField.set(Container, inner);
		return inner; }

	/**Error Message String Constant	  */
	final static public String STR_ERR_NO_ARRAY_TYPE = "Not of Array Type: ";

	/**Error Message String Constant	  */
	final static public String STR_LENGTH = "Length ";

	/**Reads the textual Representation of this Array with the given Class (not Element Type!). 	 */
	final public Object ArrayFromXMLAt(Object arg, Class myClass)
	throws IOException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
		if (!myClass.isArray()) throw new AbstractMethodError(STR_ERR_NO_ARRAY_TYPE + myClass.toString());
		myClass = myClass.getComponentType();
		int Length = Integer.parseInt((String) scan.Result)+1; //don't reuse existing Array Elements...
		while (--Length >= 0) { //reverse the Order, directly returns the Number of Elements; give out the Number or a generic Name
			if (scan.currXMLToken   != XMLScanner.XML_TAG_START) throw new AbstractMethodError(Scanner.STR_ERR_EXPECTED + StreamOutXML.XML_CHR_OPEN + Scanner.STR_ERR_OCCURRED + scan.currXMLToken);
			if (Length != Integer.parseInt((String) scan.Result))throw new AbstractMethodError(STR_LENGTH + Scanner.STR_ERR_EXPECTED + Length + Scanner.STR_ERR_OCCURRED + Integer.parseInt((String) scan.Result));
			if (scan.nextXmlToken() != XMLScanner.XML_TAG_TEXT ) throw new AbstractMethodError(Scanner.STR_TOKEN + Scanner.STR_ERR_EXPECTED + XMLScanner.XML_TAG_TEXT + Scanner.STR_ERR_OCCURRED + scan.currXMLToken); //
			if (myClass.isPrimitive()) setPrimitiveArray(arg, Length, (String) scan.Result); else Array.set(arg, Length, fromXML());
			if (scan.nextXmlToken() != XMLScanner.XML_TAG_STOP ) throw new AbstractMethodError(Scanner.STR_TOKEN + Scanner.STR_ERR_EXPECTED + XMLScanner.XML_TAG_STOP + Scanner.STR_ERR_OCCURRED + scan.currXMLToken); //
			if (Length != Integer.parseInt((String) scan.Result))throw new AbstractMethodError(STR_LENGTH + Scanner.STR_ERR_EXPECTED + Length + Scanner.STR_ERR_OCCURRED + Integer.parseInt((String) scan.Result));
				scan.nextXmlToken();
		} return arg; }

	/**Error Message String Constant	  */
	final static public String STR_ERR_NO_SUPER_TYPE = "Not of Super Type, i.e. not assignable: ";

	/**Reads the textual Representation of all fields of the given Object from the InputStream.
	 */
	final public Object fromXMLAtOld(Object arg)
	throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		Class myClass = arg.getClass();
//		String Name = scan.Result.toString(); //read Name (ignored) and Class(checked)
		Class argType = checkedForName(checkPair(XMLFormatter.STR_TYPE));
		if (! myClass.isAssignableFrom(argType)) throw new AbstractMethodError(STR_ERR_NO_SUPER_TYPE + Scanner.STR_ERR_EXPECTED + myClass.toString() + Scanner.STR_ERR_OCCURRED + argType.toString()); //check for subclass
		while (argType != myClass) { //search for the right Level, skip all Super Class Fields
			while (scan.nextXmlToken() != XMLScanner.XML_TAG_START);
			if (XMLFormatter.STR_SUPERCLASS.equals(scan.Result)) break; } //scan.thisXMLToken would help here
		scan.nextXmlToken(); //
		return fromXMLAt (arg, myClass); }

	/**Reads the textual Representation of all fields of the given Object from the InputStream.	 */
	final public Object loadItem(Object arg) {
//	throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		if (scan.currXMLToken != XMLScanner.XML_TAG_START) throw new AbstractMethodError(Scanner.STR_ERR_EXPECTED + StreamOutXML.XML_CHR_OPEN + Scanner.STR_ERR_OCCURRED + scan.currXMLToken);
//		scan.currXMLToken  = XMLScanner.XML_TAG_START;
		scan.Result = XMLFormatter.STR_SUPERCLASS;
		try { return fromXMLAt (arg, null); } //map all Errors to 'null' or IOExceptions, because they should not happen!
		catch(Exception e) { return null; } //At expects this or a SuperClass, so it is known!
//		catch(ClassNotFoundException e) { throw new IOException(e.toString()); } //At expects this or a SuperClass, so it is known!
//		catch(IllegalAccessException e) { throw new IOException(e.toString()); } //should be able to access all it's fields
//		catch(InstantiationException e) { throw new IOException(e.toString()); } //no abstract Classes or Interfaces involved
//		catch(  NoSuchFieldException e) { throw new IOException(e.toString()); } //only known Fields should occur!
//		catch(           IOException e) { throw new IOException(e.toString()); } //
		}

	/**Reads the textual Representation of all fields of the given Object from a streamIO.
	 * e.g. <Name Type="Long" ID="1"><Value Type="long">6</Value></Name>
	 * reuses already instantiated fixed Size Fields, creates Arrays new.
	 */
	private final Object fromXMLAt(Object arg, Class myClass)
	throws ClassNotFoundException, NoSuchFieldException, InstantiationException, IllegalAccessException, IOException {
		do { //
			switch (scan.currXMLToken) {
				case XMLScanner.XML_TAG_STOP : break;
				case XMLScanner.XML_TAG_START:
					String Name = scan.Result.toString(); //read Name and Class
//					//because it also covers primitive Types
					if (XMLFormatter.STR_SUPERCLASS.equals(Name)) { //load Super Class Fields
						Class argType = checkedForName(checkPair(XMLFormatter.STR_TYPE)); //is always a non-primitive Type!
						if (myClass == null) { //check whether we are at the correct Level...
							myClass = arg.getClass(); //check the Compatibility only once...
							if (! myClass.isAssignableFrom(argType)) throw new AbstractMethodError(STR_ERR_NO_SUPER_TYPE + Scanner.STR_ERR_EXPECTED + myClass.toString() + Scanner.STR_ERR_OCCURRED + argType.toString()); //check for subclass
						} else myClass = myClass.getSuperclass();
						if (myClass == argType)
							 fromXMLAt(arg, myClass); //read the Type
						else fromXMLAt(arg, null);
					} else if (myClass == null) { scan.skipXMLElement();  //read the XML as Text //check the closing Tag...
					} else { //load a normal Field
						scan.nextXmlToken(); //overread the Type instead of using it,
						Field currField = myClass.getDeclaredField(Name); //use the Class Name instead
						currField.setAccessible(true);	//get Access to the Data, throws ClassNotFoundException
						Class fClass = currField.getType();
						if (fClass.isPrimitive()) { //load a primitive Type
							while (scan.currXMLToken != XMLScanner.XML_TAG_TEXT) scan.nextXmlToken();// throw new AbstractMethodError();
							setPrimitiveField(arg, currField, (String) scan.Result);
						} else { fromXMLField(checkedForName(checkPair(XMLFormatter.STR_TYPE)), currField, arg); }
					} while (scan.currXMLToken  != XMLScanner.XML_TAG_STOP) scan.nextXmlToken(); //skip the rest of the Data
//					if	(scan.nextXmlToken()!= XMLScanner.XML_TAG_END) throw new AbstractMethodError(); //read and compare the closing Tag!
					if (!Name.equals(scan.Result)) {
						if (Name.equals(XMLFormatter.STR_SUPERCLASS)) break;
						throw new AbstractMethodError(XMLScanner.STR_ERR_GRAMMAR_ERROR + Scanner.STR_ERR_EXPECTED + Name + Scanner.STR_ERR_OCCURRED + scan.Result); } //read and compare the closing Tag!
				default: scan.nextXmlToken(); //ignore all other Tags.
			}
		} while (scan.currXMLToken != XMLScanner.XML_TAG_STOP);
		return arg; }

	///////////////////////////////////////////////////////////////////////////////
	//  static Testing and Main Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() { try { //throws InstantiationException, ClassNotFoundException, IOException, IllegalAccessException, NoSuchFieldException  {
		System.out.println("Testing XMLInputStream:");
		String Result = "<XXX Type='[I' ID='0'><5>9</5><4>8</4><3>7</3><2>6</2><1>5</1><0>4</0></XXX>";
		XMLInputStream XIS = new XMLInputStream(new StringBufferInputStream(Result));
		Object obj = XIS.fromXML();
		System.out.println(obj); //Error, because Long has no empty Constructor
		Result = "<XXX Type='Stream.Pair' ID='0'></XXX>";
		XIS = new XMLInputStream(new StringBufferInputStream(Result));
		obj = XIS.fromXML();
		System.out.println(obj);
//		Result = "<XXX Type='java.lang.Long' ID='1'><value Type='long'>6</value><SuperClass Type='java.lang.Number'></SuperClass></XXX>";
//		XIS = new XMLInputStream(new StringBufferInputStream(Result));
//		obj = XIS.fromXML();
//		System.out.println(obj);
		} catch (InstantiationException e) { e.printStackTrace();
		} catch (ClassNotFoundException e) { e.printStackTrace();
		} catch (IllegalAccessException e) { e.printStackTrace();
		} catch (  NoSuchFieldException e) { e.printStackTrace();
		} catch (			IOException e) { e.printStackTrace();
		}
	}
	
}
