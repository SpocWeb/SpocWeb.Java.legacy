package streamIO.object.parser;

import graphs.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import streamIO.AFormatterOut;
import streamIO.IFormatOut;
import streamIO.IIStreamOut;
import streamIO.exception.BaseException;
import streamIO.integer.adapter.OutputStreamToStreamOutByte;

/**
 * Title:        XMLFormatter<p>
 * Description:
 * This Class provides Methods to write Objects to an XML streamIO<p>
 * Uses the Reflection API to explore the Object
 * and write primitive Fields as Attributes
 * and Objects as nested Elements
 * Main Method: addItem()
 *
 * Previously named 'XMLOutputStream'
 *
 * @see streamIO.Object.Parser.StreamOutXML is used by this Class
 *
 * Copyright:    Copyright (c) <p>
 * Company:      Matthias Heuer<p>
 * @author		 Matthias Heuer
 * @version 1.0
 */
public class XMLFormatter
extends AFormatterOut { //for it's Storage Capabilities.

	////////////////////////////////////////////////////////////////////////////
	//	static Methods
	////////////////////////////////////////////////////////////////////////////

	/**Converts the Object into an XML Element with the given Name,
	 * catches the IO Exceptions (not possible with ByteArrayStreams).
	 */
	public static String TO_XML_ELEMENT(Object arg, String Name, boolean cached)
	throws IllegalAccessException {
		ByteArrayOutputStream OS = new ByteArrayOutputStream();  //StringBuffer SB = new StringBuffer();
		XMLFormatter XOS = new XMLFormatter(OS, cached);
		try { XOS.toXML(arg, Name); } //should not happen!
		catch (IOException e) { throw new IllegalAccessException (e.toString()); }
		return OS.toString(); }

	/**Converts the Object into an XML Element with the given Name,
	 * catches the IllegalAccessException and replaces it with an Error.
	 */
	public static String TO_XML_ELEMENT_SAFE(Object arg, String Name, boolean cached) {
		try { return TO_XML_ELEMENT(arg, Name, cached); } //should not happen!
		catch (IllegalAccessException  e) { throw new IllegalAccessError (e.toString()); } }

	////////////////////////////////////////////////////////////////////////////
	//	static Members
	////////////////////////////////////////////////////////////////////////////

	/**String Constant for the Tag Name of the Super Class Fields	 */
	public static String STR_SUPERCLASS = "SuperClass";

	/**String Constant for the Attribute Name for the Field Type	 */
	public static String STR_TYPE = "Type";

	/**String Constant for the Attribute Name for the Object ID	 */
	public static String STR_ID = "ID";

	////////////////////////////////////////////////////////////////////////////
	//	Variables
	////////////////////////////////////////////////////////////////////////////

	/**List of all Objects that have been ore are being synchronized
	 * By creating this Object all Serialization is tracked and cycles avoided.
	 * Additionally the Creation of Duplicates is prevented and thus
	 * exactly the same structure is restored.
	 * Not clearing this List allows saving serialization
	 * of already serialized Objects (shared between Trees)
	 * Design Decisions:
	 * A HashSet would have been sufficient though.
	 */
	protected HashMap Cache;

	/**ID of the currently serialized Object on avoiding Duplications.
	 * Could also be the Count of the Cache HashTable, if no Elements were removed.     */
//	public static long CacheID;

	/** Use an XML Writer Helper Class instead of creating all Tokens manually	*/
	protected StreamOutXML XW;

	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////

	/**Initializing Constructor	 */
	public XMLFormatter(OutputStream OS, boolean cached) {
		this(new PrintStream(OS), cached); }

	/**Initializing Constructor	 */
	public XMLFormatter(PrintStream PS, boolean cached) {
		super(PS); 
//		try {
			XW = new StreamOutXML
			( new OutputStreamToStreamOutByte(PS)); //This Class has to be rewritten anyway! 
//			( new OutputStreamWriter(PS, Stream.character.FileWriter.ISO_8859_1)); //This Class has to be rewritten anyway! 
//		} catch (UnsupportedEncodingException x) { }
		setCached(cached); }

	/** Returns a new Instance of this Formatter Class using the given OutPut streamIO	 */
	public IFormatOut newInstance(OutputStream OS) { return new XMLFormatter(OS, Cache != null); }

	/** Returns a new Instance of this Formatter Class using the given Print streamIO	 */
	public IFormatOut newInstance(PrintStream PS) { return new XMLFormatter(PS, Cache != null); }

	////////////////////////////////////////////////////////////////////////////
	//	Methods
	////////////////////////////////////////////////////////////////////////////

	/**Returns the Flag whether the Object are cached	 */
	public boolean getCached() { return (Cache != null);	}

	/**Sets the Flag whether the Object are cached	 */
	public void setCached(boolean cached) {
		if (!cached) Cache = null;
		else { //prefill the Cache with the Null Pointer to allow for null ID
			Cache = new HashMap();
			Cache.put(null, new Integer(0)); }
	}

	/**Clears the Object Cache and breaks Reference Tracking	 */
	public void clearCache() { if (Cache != null) Cache.clear(); }

	/**Writes the textual Representation of this Object to the OutputStream.
	 * A synchronized static Method would allow only one thread to enter it,
	 * since there is only one Class Instance.
	 * So essentially, this Method can only run once in a VM.
	 * Using several Instances of this class could result in a DeadLock,
	 * because the same two dep. Objects could be serialized by both Threads,
	 * so both threads wait for the Monitor of the other Object
	 * while their own Monitors are not released.
	 *
	 * The Problem is that the Lock cannot be acquired on all Objects
	 * at the same time, so they could be modified during serialization.
	 */
	public synchronized void toXMLSynch(Object arg, Class argClass)
		throws IOException, IllegalAccessException { toXML(arg, argClass); }

	/**Writes the textual Representation of this Array with Elements
	 * of the given Class Type to the Output streamIO.
	 * Especially Arrays are harder to read in this Representation
	 * compared to the Result of ArrayToString().
	 */
	final public void ArrayToXML(Object arg, Class myClass)
		throws IOException, IllegalAccessException {
		if (!myClass.isArray()) throw new AbstractMethodError();
		int Length = java.lang.reflect.Array.getLength(arg);
		Class CmpType = myClass.getComponentType();
//		OS.println(Length); //should rather be an Attribute than an Element.
		while (--Length >= 0) //reverse the Order, directly returns the Number of Elements;
			toXML(Array.get(arg, Length), Integer.toString(Length), CmpType); //give out the Number or a generic Name
	} //

	/**Writes the textual Representation of this Object with the given Tag Name
	 * to the Output streamIO. Cannot handle primitive Types (use next Routine)
	 * that have been converted to their Container Types.
	 */
	public IIStreamOut addItem(Object arg) {
		try { //using the HashCode for the Object. Could also have used toString(), but that would probably create duplicate Information
			toXML(arg, Integer.toString(arg.hashCode()), arg.getClass());
		} catch (IOException x) { throw new BaseException(XMLFormatter.class.toString() + ".addAt()", x);
		} catch (IllegalAccessException x) { throw new IllegalAccessError(x.toString()); }
		return this; }

	/**Writes the textual Representation of this Object with the given Tag Name
	 * to the Output streamIO. Cannot handle primitive Types (use next Routine)
	 * that have been converted to their Container Types.
	 */
	final public void toXML(Object arg, String Name)
		throws IOException, IllegalAccessException { toXML(arg, Name, arg.getClass()); }

	/**Writes the textual Representation of this Object
	 * with the given Tag Name and Type Specifier to the Output streamIO.
	 * The Type is handed over to be able to handle primitive Types.
	 *
	 * For handling cycles, every Object must get a Flag
	 * that is switched before serializing and after it.
	 * The (slower) alternative is
	 * building up a HashMap of already serialized Objects.
	 * Additionally the Operation must be synchronized
	 * to avoid changes to the Objects during Serialization.
	 * Otherwise you only receive a (potentially inconsistent) snapshot.
	 *
	 * To avoid Duplication of Objects, they should be given an ID
	 * that is used to identify them on Restoration.
	 * This even solves the Problem of sequential Serializations.
	 */
	final public void toXML(Object arg, String Name, Class Type)
		throws IOException, IllegalAccessException {
		XW.startTag(Name); //
		XW.attribute(STR_TYPE, Type.getName(), false); //
		if (Type.isPrimitive()) { PS.print(StreamOutXML.XML_CHR_CLOSE); PS.print(arg); //not necessary to switch by Scalar Type on writing
		} else {
			Integer ID = null; //Compiler doesn't see through the Logic...
			boolean found = false; //Default for not having a Cache
//			if (arg == null) {
//				found = true; XMLScanner.printXMLAttribute(STR_ID, "0", writer, false);
//				} else //write ID == 0
			if (Cache != null) { //even if no Cache is switched on...
				if (found = Cache.containsKey(arg)) //Cycles are avoided here!
					 ID = (Integer) Cache.get(arg); //
				else ID = new Integer(Cache.size());// ++CacheID);
				XW.attribute(StreamOutXML.XML_ATTR_ID, ID.toString(), false); }
			PS.print(StreamOutXML.XML_CHR_CLOSE);
			if (!found) { if (Cache != null) Cache.put(arg, ID); toXML(arg, Type); } //Cache.remove(arg); } //results in independet Object being created and prevents giving IDs based on Cache.size().
		}
		XW.endTag();
	}

	/**Iterates through all Fields of this Object and writes
	 * the textual Representation without enclosing Tags to the OutputStream.
	 * So you lose the Class Type Information and the Name Information
	 */
	final public void toXML(Object arg, Class argClass)
		throws IOException, IllegalAccessException {
		if (argClass.isArray()) { ArrayToXML(arg, argClass); return; }
		java.lang.reflect.Field[] allFields = argClass.getDeclaredFields();
		int i = allFields.length;
		while (--i >= 0) {
			Field currField = allFields[i];
			int mod = currField.getModifiers();
			if (Modifier.isFinal	(mod) ||
				Modifier.isTransient(mod) ||
				Modifier.isStatic	(mod) ||
				Modifier.isVolatile	(mod)) continue;
			currField.setAccessible	(true); //get access to the Data
			Object target;
			if ((target = currField.get(arg)) != arg) {  //quickly avoid common recursion with 'self', even without Cache.
				Class fClass = currField.getType();
				if (! fClass.isPrimitive()) fClass = target.getClass(); //use the actual Class.
				toXML(target, currField.getName(), fClass);
			}
		} //while Fields left
		argClass = argClass.getSuperclass(); //the superclass of Object is 'null'
		if ((argClass != null) && (argClass != Object.class)) {
			XW.startTag(STR_SUPERCLASS);
			XW.attribute(STR_TYPE, argClass.getName(), false);
			toXML(arg, argClass); //Problem with avoiding Cycles: Flag mustn't be checked.
			XW.close(); }
	}

	////////////////////////////////////////////////////////////////////////////
	//	Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////

	/**Method to test all Implementations in this class.
	 * Must call testIt of the super Class.	 */
	public static void testIt(String[] args) {
		try{ //throws IOException, IllegalAccessException  {
		System.out.println("Testing " + XMLFormatter.class.toString());
		XMLFormatter XOS = new XMLFormatter(System.out, true);
		XOS.toXML( new int [6], "XXX"); System.out.println();
		XOS.toXML( new Long(6), "XXX"); System.out.println();
		XOS.toXML( new Pair() , "XXX"); System.out.println();
		} catch (IllegalAccessException e) {
		} catch (			IOException e) {
		}
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws IOException {
		testIt(args); }

}
