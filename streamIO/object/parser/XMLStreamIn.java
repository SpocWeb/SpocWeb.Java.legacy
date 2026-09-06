package streamIO.object.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import streamIO.IDeserializer;
import streamIO.object.AStreamIn;
import function.byref.ByRefInt;
import graphs.KeyValuePair;

/**
 * Methods to read complete Object Trees from an XML streamIO
 * and static Methods to set the Values in Arrays and Objects. <p>
 * 
 * @see StreamOutXML is the Pendant to this Class
 * @see XMLScannerStreamIn is used to parse the Input streamIO.
 * 
 * @see streamIO.Object.Byte.XMLInputStream is deprecated and replaced by this Implementation
 * 
 * Copyright:    Copyright (c) <p>
 * Company:      Matthias Heuer<p>
 * @author		 Matthias Heuer
 * @version 1.0
 * <!-- docstate
 * tags: [code/xml_parsing, code/xml_streaming]
 * concepts: [XML Read/Write Stream Bridging]
 * facets: {layer: utility, status: legacy, complexity: high}
 * -->
 */
public class XMLStreamIn
extends AStreamIn
implements IDeserializer {
	
	///////////////////////////////////////////////////////////////////////////////
	//	static Methods
	///////////////////////////////////////////////////////////////////////////////
	
	/**Reads the textual Representation of an Object from a streamIO and returns it.
	 * The Tag Name is ignored, no Containing Class is assumed.
	 */
	public static Object fromString(String arg)
	throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException, InstantiationException {
		StringBufferInputStream IS = new StringBufferInputStream(arg);
		try{XMLStreamIn XIS = new XMLStreamIn(IS);
			return XIS.fromXML(); } //should never happen!
		catch (final IOException e) { 
			throw new IllegalAccessException(e.toString()); }
	}
	
	/**Reads the textual Representation of an Object from a streamIO and returns it.
	 * The Tag Name is ignored, no Containing Class is assumed.
	 */
	public static Object fromStringAt(Object arg, String str) {
		StringBufferInputStream IS = new StringBufferInputStream(str);
		XMLStreamIn XIS = null;
		try{XIS = new XMLStreamIn(IS); } catch(IOException x) { } //should never happen!
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
	final static public void setPrimitiveArray(Object arg, int Index, String Value) {
		Class myClass = arg.getClass().getComponentType();
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
	 * TODO: check if the Field.set(Object arg) Method can be used with Strings
	 * or only primitive Wrapper Types.
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
	
	///////////////////////////////////////////////////////////////////////////////
	//  Variables
	///////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Input streamIO	 */
	protected XMLScannerStreamIn scan;
	
	/** Alias of the Scanner's live Token {@link XMLScannerStreamIn#currXMLToken},
	  * assigned by the Constructors so it always reflects the last XML Token read.	 */
	protected ByRefInt currXMLToken;
	
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
	public XMLStreamIn(InputStream IS) throws IOException {
		this(new XMLScannerStreamIn(IS)); }

	/**Initializing Constructor	 */
	public XMLStreamIn(XMLScannerStreamIn scan) {
		this.scan = scan;
		this.currXMLToken = scan.currXMLToken; } //alias the Scanner's live Token
	
	/** Returns a new Instance of this Parser Class using the given InPut streamIO	 */
	public IDeserializer newInstance(InputStream In) throws IOException { return new XMLStreamIn(In); }
	
	///////////////////////////////////////////////////////////////////////////////
	//  Methods
	///////////////////////////////////////////////////////////////////////////////
	
	/** Delegates to the wrapped XML Scanner's own Mark support.
	  * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return scan.getMaxMarkSize(); }

	/** Delegates to the wrapped XML Scanner's own Position tracking.
	  * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return scan.getPosition(); }
	
	/**Clears the Object Cache and breaks Reference Tracking	 */
	public void clearCache() { Cache = null; }

	/**Checks whether an Pair with the given Name follows
	 * and returns the Pair Value as a String.
	 */
	final public String checkPair (String Name) throws IOException {
		if (((ByRefInt) scan.currXMLToken).Value != XMLScannerStreamIn.XML_TAG_ATTRIBUTE)
			scan.nextItem();
		KeyValuePair assoc = (KeyValuePair) scan.currItem();
		if (!Name.equals(assoc.key))
			throw new AbstractMethodError(InputStream2StreamIn.STR_ERR_EXPECTED + Name + InputStream2StreamIn.STR_ERR_OCCURRED + assoc.key);
		return (String) assoc.val; }

	/**Returns the minimum Number of Items left (in the Buffer).
	 * The actual Number may be higher, so available() should be called again
	 * at the End of this Number.
	 */
	public long availAble() {
		return (currXMLToken.Value == XMLScannerStreamIn.XML_TAG_EOF) ? 0 : 1; }

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

	/**Reads the textual Representation of an Object from a streamIO and returns it.
	 * The Tag / Object Name is ignored, no Containing Class is assumed.
	 */
	final public Object fromXML()
		throws InstantiationException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IOException {
//		String Name = scan.Result.toString(); //read Name, already preread and not used
		Class fClass = Class.forName(checkPair(XMLFormatter.STR_TYPE)); //
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
		if (currXMLToken.Value == XMLScannerStreamIn.XML_TAG_ATTRIBUTE) { //read an optional ID
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
				int Length  = Integer.parseInt((String) scan.currItem())+1;
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
		if (Cache != null) { Cache.ensureCapacity(ID); Cache.add( ID, inner); } //the Cache has to be filled...
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
		int Length = Integer.parseInt((String) scan.currItem())+1; //don't reuse existing Array Elements...
		while (--Length >= 0) { //reverse the Order, directly returns the Number of Elements; give out the Number or a generic Name
			if (currXMLToken.Value  != XMLScannerStreamIn.XML_TAG_START) throw new AbstractMethodError(InputStream2StreamIn.STR_ERR_EXPECTED + StreamOutXML.XML_CHR_OPEN+ InputStream2StreamIn.STR_ERR_OCCURRED + scan.currXMLToken);
			if (Length != Integer.parseInt((String) scan.currItem()))throw new AbstractMethodError(STR_LENGTH + InputStream2StreamIn.STR_ERR_EXPECTED + Length + InputStream2StreamIn.STR_ERR_OCCURRED + Integer.parseInt((String) scan.currItem()));
			if (((ByRefInt) scan.nextItem()).Value != XMLScannerStreamIn.XML_TAG_CDATA ) throw new AbstractMethodError(InputStream2StreamIn.STR_TOKEN + InputStream2StreamIn.STR_ERR_EXPECTED + XMLScannerStreamIn.XML_TAG_CDATA + InputStream2StreamIn.STR_ERR_OCCURRED + scan.currXMLToken); //
			if (myClass.isPrimitive()) setPrimitiveArray(arg, Length, (String) scan.currItem()); else Array.set(arg, Length, fromXML());
			if (((ByRefInt) scan.nextItem()).Value != XMLScannerStreamIn.XML_TAG_END ) throw new AbstractMethodError(InputStream2StreamIn.STR_TOKEN + InputStream2StreamIn.STR_ERR_EXPECTED + XMLScannerStreamIn.XML_TAG_END + InputStream2StreamIn.STR_ERR_OCCURRED + scan.currXMLToken); //
			if (Length != Integer.parseInt((String) scan.currItem()))throw new AbstractMethodError(STR_LENGTH + InputStream2StreamIn.STR_ERR_EXPECTED + Length + InputStream2StreamIn.STR_ERR_OCCURRED + Integer.parseInt((String) scan.currItem()));
				scan.nextItem();
		} return arg; }

	/**Error Message String Constant	  */
	final static public String STR_ERR_NO_SUPER_TYPE = "Not of Super Type, i.e. not assignable: ";

	/**Reads the textual Representation of all fields of the given Object from the InputStream.
	 */
	final public Object fromXMLAtOld(Object arg)
	throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		Class myClass = arg.getClass();
//		String Name = scan.Result.toString(); //read Name (ignored) and Class(checked)
		Class argType = Class.forName(checkPair(XMLFormatter.STR_TYPE));
		if (! myClass.isAssignableFrom(argType)) throw new AbstractMethodError(STR_ERR_NO_SUPER_TYPE + InputStream2StreamIn.STR_ERR_EXPECTED + myClass.toString() + InputStream2StreamIn.STR_ERR_OCCURRED + argType.toString()); //check for subclass
		while (argType != myClass) { //search for the right Level, skip all Super Class Fields
			while (((ByRefInt) scan.nextItem()).Value != XMLScannerStreamIn.XML_TAG_START);
			if (XMLFormatter.STR_SUPERCLASS.equals(scan.currItem())) break; } //scan.thisXMLToken would help here
		scan.nextItem(); //
		return fromXMLAt (arg, myClass); }

	/** Reads the textual Representation of all fields of the given Object from the InputStream.	 */
	final public Object loadItem(Object arg) {
//	throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException {
		if (currXMLToken.Value != XMLScannerStreamIn.XML_TAG_START) throw new AbstractMethodError(InputStream2StreamIn.STR_ERR_EXPECTED + StreamOutXML.XML_CHR_OPEN + InputStream2StreamIn.STR_ERR_OCCURRED + scan.currXMLToken);
//		scan.currXMLToken  = XMLScannerStreamIn.XML_TAG_START;
//		scan.Result = XMLFormatter.STR_SUPERCLASS; //TODO: ??? Warum ???
		try { return fromXMLAt(arg, null); } //map all Errors to 'null' or IOExceptions, because they should not happen!
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
			switch (currXMLToken.Value) {
				case XMLScannerStreamIn.XML_TAG_END : break;
				case XMLScannerStreamIn.XML_TAG_START:
					String Name = scan.currItem().toString(); //read Name and Class
//					//because it also covers primitive Types
					if (XMLFormatter.STR_SUPERCLASS.equals(Name)) { //load Super Class Fields
						Class argType = Class.forName(checkPair(XMLFormatter.STR_TYPE)); //is always a non-primitive Type!
						if (myClass == null) { //check whether we are at the correct Level...
							myClass = arg.getClass(); //check the Compatibility only once...
							if (! myClass.isAssignableFrom(argType)) throw new AbstractMethodError(STR_ERR_NO_SUPER_TYPE + InputStream2StreamIn.STR_ERR_EXPECTED + myClass.toString() + InputStream2StreamIn.STR_ERR_OCCURRED + argType.toString()); //check for subclass
						} else myClass = myClass.getSuperclass();
						if (myClass == argType)
							 fromXMLAt(arg, myClass); //read the Type
						else fromXMLAt(arg, null);
					} else if (myClass == null) { scan.skipXMLElement();  //read the XML as Text //check the closing Tag...
					} else { //load a normal Field
						scan.nextItem(); //overread the Type instead of using it,
						Field currField = myClass.getDeclaredField(Name); //use the Class Name instead
						currField.setAccessible(true);	//get Access to the Data, throws ClassNotFoundException
						Class fClass = currField.getType();
						if (fClass.isPrimitive()) { //load a primitive Type
							while (currXMLToken.Value != XMLScannerStreamIn.XML_TAG_CDATA) scan.nextItem();// throw new AbstractMethodError();
							setPrimitiveField(arg, currField, (String) scan.currItem());
						} else { fromXMLField(Class.forName(checkPair(XMLFormatter.STR_TYPE)), currField, arg); }
					} while (currXMLToken.Value != XMLScannerStreamIn.XML_TAG_END) scan.nextItem(); //skip the rest of the Data
//					if	(scan.nextXmlToken()!= XMLScannerStreamIn.XML_TAG_END) throw new AbstractMethodError(); //read and compare the closing Tag!
					if (!Name.equals(scan.currItem())) {
						if (Name.equals(XMLFormatter.STR_SUPERCLASS)) break;
						throw new AbstractMethodError(XMLScannerStreamIn.STR_ERR_GRAMMAR_ERROR + InputStream2StreamIn.STR_ERR_EXPECTED + Name + InputStream2StreamIn.STR_ERR_OCCURRED + scan.currItem()); } //read and compare the closing Tag!
				default: scan.nextItem(); //ignore all other Tags.
			}
		} while (currXMLToken.Value != XMLScannerStreamIn.XML_TAG_END);
		return arg; }

	///////////////////////////////////////////////////////////////////////////////
	//  static Testing and Main Methods
	///////////////////////////////////////////////////////////////////////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt() { try { //throws InstantiationException, ClassNotFoundException, IOException, IllegalAccessException, NoSuchFieldException  {
		System.out.println("Testing XMLStreamIn:");
		String Result = "<XXX Type='[I' ID='0'><5>9</5><4>8</4><3>7</3><2>6</2><1>5</1><0>4</0></XXX>";
		XMLStreamIn XIS = new XMLStreamIn(new StringBufferInputStream(Result));
		Object obj = XIS.fromXML();
		System.out.println(obj); //Error, because Long has no empty Constructor
		Result = "<XXX Type='Stream.Pair' ID='0'></XXX>";
		XIS = new XMLStreamIn(new StringBufferInputStream(Result));
		obj = XIS.fromXML();
		System.out.println(obj);
//		Result = "<XXX Type='java.lang.Long' ID='1'><value Type='long'>6</value><SuperClass Type='java.lang.Number'></SuperClass></XXX>";
//		XIS = new XMLStreamIn(new StringBufferInputStream(Result));
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
