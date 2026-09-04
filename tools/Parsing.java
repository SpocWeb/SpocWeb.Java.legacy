package tools;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamTokenizer;
import java.io.Writer;

/**
 * Static Helpers for reading Separator-delimited Structures and Numbers off a Tokenizer.
 *
 * <p>This Class is slightly obsolete, because all this is implemented
 * in the streamIO Classes Scanner, StreamParser and StreamIterator(old)
 * in a more elegant and consistent Manner, but it is still being used,
 * because it can easily parse Separator Structures, Numbers.
 *
 * <p>The Separator Strings are {@code public static} Fields rather than Parameters, so the
 * Format is configured once, globally, for every Caller in the JVM.
 *
 * <h2>Collaborators</h2>
 *
 * <table>
 * <caption>Types this Class works with</caption>
 * <tr><th>Type</th><th>Relationship</th></tr>
 * <tr><td>{@link java.io.StreamTokenizer}</td>
 *     <td>The Input every read Helper advances and inspects.</td></tr>
 * <tr><td>{@link java.io.Writer}</td>
 *     <td>The Output the Formatting Helpers append to.</td></tr>
 * </table>
 *
 *
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author		 Matthias Heuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-04T16:35:47Z
 * digest: beb87e0522d3c95f516e7faa317ce447b20dc561af6abe11efc3952fc6588479
 * stale: false
 * tags: [code/tokenizer, code/text_parsing, code/legacy_helper]
 * concepts: [Text Parsing]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class Parsing {

	/** Creates an Instance, although every Member of this Class is {@code static}.	*/
	public Parsing() { }


	//////////////////////
	//	Parsing Streams	//
	//////////////////////

	/**Reads until the next Token of this Kind is encountered	 */
	public static void nextToken(StreamTokenizer ST, int Token)
		throws IOException { nextToken(ST, Token, 1); }

	/**Skips the next Tokens of this Kind 	 */
	public static void nextToken(StreamTokenizer ST, int SkipToken, int numItems)
		throws IOException {
		while ((--numItems >= 0) && (ST.ttype != StreamTokenizer.TT_EOF))
			while ((ST.nextToken() != SkipToken) &&
				   (ST.ttype != StreamTokenizer.TT_EOF) &&
				   (ST.ttype != StreamTokenizer.TT_EOL)); }

	/**Skips the next Tokens of SkipToken Kind.
	 * Then searches for the first Token of FindToken Type 	 */
	public static void nextToken(StreamTokenizer ST, int SkipToken, int numItems, int FindToken)
		throws IOException {
		nextToken(ST, SkipToken, numItems);
		while ((ST.nextToken() != FindToken) &&
			   (ST.ttype != StreamTokenizer.TT_EOF) &&
			   (ST.ttype != StreamTokenizer.TT_EOL)); }

	/**Skips until the next Number, ignoring Lines	 */
	public static void nextNumber(StreamTokenizer ST)
		throws IOException {
		ST.eolIsSignificant(false);
		nextNumber(ST, 0, 0, false);
		ST.eolIsSignificant(true);
		ST.pushBack(); }

	/** Searches for the first Number in this streamIO 	 */
	public static double nextNumber(StreamTokenizer ST, boolean EOL)
	throws IOException {
		return nextNumber(ST, 0, 0, EOL);}

	/** Skips the next Tokens of SkipToken Kind
	  * Then searches for the first Number in this streamIO 	 */
	public static double nextNumber(StreamTokenizer ST, int SkipToken, int numItems, boolean EOL)
	throws IOException{
//		ST.eolIsSignificant(EOL);
		while ((--numItems >= 0) && (ST.ttype != StreamTokenizer.TT_EOF))
			while ((ST.nextToken() != SkipToken   ) && (ST.ttype != StreamTokenizer.TT_EOF) && (ST.ttype != StreamTokenizer.TT_EOL));
			while ((ST.nextToken() != StreamTokenizer.TT_NUMBER) && (ST.ttype != StreamTokenizer.TT_EOF) && (ST.ttype != StreamTokenizer.TT_EOL));
		if (ST.ttype == StreamTokenizer.TT_NUMBER) {
			double Return = ST.nval; //buffer
			if (EOL) { skipEOL(ST); }	//read until the EOL
			return Return; }
		throw new NumberFormatException("No Number found! Expected: Token=" + StreamTokenizer.TT_NUMBER + "  Found: Token=" + ST.ttype); }

	/** Reads and skips until the End of the Line or the End of the File */
	final static public void skipEOL(StreamTokenizer ST) throws IOException {
		while ((ST.nextToken() != StreamTokenizer.TT_EOF) && (ST.ttype != StreamTokenizer.TT_EOL)); }

	/**Parses the simple (non-recursive) List in a String into an Array of double.
	 * Skips all other Characters.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
	final static public double [] parseList2double (StreamTokenizer ST, int Length, boolean EOL)
	throws IOException {
		double[] dList = new double[Length];
		int i = -1;
		while (++i < Length)
			dList[i] = nextNumber(ST, false);
		if (EOL) { skipEOL(ST); }	//read until the EOL
		return dList; }

	/**Parses the simple (non-recursive) List in a String into an Array of double.
	 * Skips all other Characters.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
	final static public float[] parseList2float(StreamTokenizer ST, int Length, boolean EOL)
	throws IOException {
		float[] dList = new float[Length];
		int i = -1;
		while (++i < Length)
			dList[i] = (float) nextNumber(ST, false);
		if (EOL) { skipEOL(ST); }	//read until the EOL
		return dList; }

	/**Parses the simple (non-recursive) List in a String into an Array of int.
	 * Skips all other Characters. Length is the maximum Length of the List.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
	final static public int [] parseList2int (StreamTokenizer ST, int Length, boolean EOL)
		throws IOException {
		int[] iList = new int[Length];
		int i = -1;
		try {
			while (++i < Length)
				iList[i] = (int) nextNumber(ST, false);
		} catch (AbstractMethodError x) {
			ST.pushBack();
			int[] tmp = new int[i];
			System.arraycopy (iList, 0, tmp, 0, i);
			iList = tmp; }
		if (EOL) { skipEOL(ST); }	//read until the EOL
		return iList; }

	/**String Constants for Parsing and Formatting	 */
	//many System Dependent Strings are in
	//java.io.StreamTokenizer.TT_EOL;

	/**Determines, whether the Length of an Array is given out (!= "")
	 * and how it is separated from the Rest of the List	 */
	public static String SeparatorLength = ":";

	/**Separator for the Class Description.
	 * A WhiteSpace is enough, because the Class Name is a single Word.	 */
	public static String ClassSep = " ";	//":"

	/**Separator String	 */
	public static String Separator = ",";

	/**Starter String	 */
	public static String Starter = "{";

	/**Stopper String	 */
	public static String Stopper = "}";

	/**Attribute String, separates Attributes in Attribute Grammar	 */
	public static String Attribute = ":";

	/**String containing the Characters for Signs	 */
	final static public String Signs = "+-";

	/**String containing the Characters for Numbers	 */
	final static public String strNumbers = "0123456789";

	/**String containing the Characters for integer Numbers	 */
	final static public String strIntFigures = strNumbers + Signs;

	/**String containing the Characters for float Point Numbers	 */
	final static public String strDblFigures = strNumbers + ".e";

	//////////////////////
	// Parsing Strings	//
	//////////////////////

	/**tolerant Searching for the first numerical expression	 */
	final static public int parseInt(String arg) {
		arg = arg.trim();
		int i = 0;	//first Character may be a Sign!
		int len = arg.length();
		if					(strIntFigures.indexOf(arg.charAt(i)) <  0) return 0;	//not found
		while((++i < len) &&(strNumbers   .indexOf(arg.charAt(i)) >= 0));	//Check for the Length first.
		return Integer.parseInt(arg.substring(0, i)); }

	/**tolerant Searching for the first numerical expression	 */
	final static public double parseDouble(String arg) {
		arg = arg.trim();
		int i = -1;	//first Character may be a Sign!
		int len = arg.length();
		if					(strIntFigures.indexOf(arg.charAt(i)) <  0) return 0;	//not found
		while((++i < len) &&(strDblFigures.indexOf(arg.charAt(i)) >= 0));	//Check for the Length first.
		return Double.valueOf(arg.substring(0, i+1)).doubleValue(); }

	//////////////////////////
	//	Processing Lists	//
	//////////////////////////

	/**Parses the simple (non-recursive) List in a streamIO into an Array of Strings.
	 * Using the default Separators given by this Class.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
/*	final static public String [] parseList(java.io.InputStream arg)
	{return parseList(arg, Separator, Starter, Stopper);}

	/**Parses the simple (non-recursive) List in a String into an Array of Strings.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
/*	final static public String [] parseList(java.io.StreamTokenizer Parsed,
											String Separator,
											String Starter,
											String Stopper)
	{	//Trim from the Starter and Stopper String
		arg = arg.trim();
		if ((Starter != null) && (arg.startsWith(Starter))) arg = arg.substring(                Starter.length());
		if ((Stopper != null) && (arg.endsWith  (Stopper))) arg = arg.substring(0, arg.length()-Stopper.length());
		String[] List = new String[Parsed.countTokens()];
		int i = -1; while (++i < List.length) List [i] = (String) Parsed.nextElement();
		return List; }

	/**Parses the String into an Array of Objects
	 * determined by the Type Information in the String.
	 * Using the default Separators given by this Class.
	 * Works also recursively for nested Objects.	 */
/*	final static public copyAble [] parseListFromText(String arg, String Separator)
		throws ClassNotFoundException,
			   InstantiationException,
			   IllegalAccessException {
		java.util.StringTokenizer Parsed = new java.util.StringTokenizer (arg.trim(), Separator);
		copyAble[] List = new copyAble[Parsed.countTokens()];
		int i = -1; while (++i < List.length) List[i] = fromText((String) Parsed.nextElement());
		return List; }

	/**Parses the simple (non-recursive) List in a String into an Array of double.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
/*	final static public double [] parseList2double
										   (String arg,
											String Separator,
											String Starter,
											String Stopper,
											int Length) {
		String[] List = parseList(arg, Separator, Starter, Stopper);
		int i = List.length; if (Length > 0) i = Length;
		double[] dList = new double[i];
		while (--i >= 0) dList [i] = Double.valueOf(List[i]).getDouble();
		return dList; }

	/**Parses the simple (non-recursive) List in a String into an Array of double.
	 * For recursive Parsing use the Parser in the Hashtable. 	 */
/*	final static public int [] parseList2int
										   (String arg,
											String Separator,
											String Starter,
											String Stopper,
											int Length) {
		String[] List = parseList(arg, Separator, Starter, Stopper);
		int i = List.length; if (Length > 0) i = Length;
		int[] iList = new int[i];
		while (--i >= 0) iList [i] = Integer.parseInt(List[i]);
		return iList; }

	//////////////////////////////////////////////////////////////////////////////////
	//	Trivial Implementation of using the Serialization for my own formatting:	//
	//////////////////////////////////////////////////////////////////////////////////

/*	private void writeObject(java.io.ObjectOutputStream out) {
		out.writeChars(toText()); }

	private void readObject(java.io.ObjectInputStream in) {
		fromTextAt(in.readLine()); }
*/
	/**Returns the textual Representation of this Object. 	 */
	final public String toString()	{ return toString(this); }

	/**Writes the textual Representation of this Object. 	 */
	final public void toString(OutputStream OS)
	throws IOException {
		OS.write(toString().getBytes()); }

	/**Writes the textual Representation of this Object. 	 */
	final public void toString(Writer OS)
	throws IOException {
		OS.write(toString());}

	/**Returns the textual Representation of the given Object. 	 */
	final static public String toText(Object arg) {
		return arg.getClass().getName() + ClassSep + arg.toString();}

	/**Returns the given Object from it's textual Representation
	 * Determines the Class from the first  Part
	 * Determines the Value from the second Part
	 * Usually both are separated by a WhiteSpace,
	 * since the Class Name is a single String	 */
/*	final static public copyAble fromText(InputStream IS)
		throws ClassNotFoundException,
			   InstantiationException,
			   IllegalAccessException,
			   IOException
	{return fromText(new StreamTokenizer(IS));}

	/**Returns the given Object from it's textual Representation
	 * Determines the Class from the first  Part
	 * Determines the Value from the second Part
	 * Usually both are separated by a WhiteSpace,
	 * since the Class Name is a single String	 */
/*	final static public copyAble fromText(String Text)
	throws ClassNotFoundException,
		   InstantiationException,
		   IllegalAccessException,
		   IOException {
	return fromText(new StringReader(Text));}

	/**Returns the given Object from it's textual Representation
	 * Determines the Class from the first  Part
	 * Determines the Value from the second Part
	 * Usually both are separated by a WhiteSpace,
	 * since the Class Name is a single String	 */
/*	final static public copyAble fromText(Reader IS)
	throws ClassNotFoundException,
		   InstantiationException,
		   IllegalAccessException,
		   IOException {
		return fromText(new StreamTokenizer(IS));}

	/**Returns the given Object from it's textual Representation
	 * Determines the Class from the first  Part
	 * Determines the Value from the second Part
	 * Usually both are separated by a WhiteSpace,
	 * since the Class Name is a single String	 */
/*	final static public copyAble fromText(StreamTokenizer ST)
		throws ClassNotFoundException,
			   InstantiationException,
			   IllegalAccessException,
			   IOException {
//		int pos = arg.indexOf(ClassSep);	//find first ":"
		ST.nextToken(); //== ST.TT_WORD)	//check for the correct Type
		copyAble obj = (copyAble) Class.forName(ST.sval).newInstance();
		return (obj.fromStreamAt(ST)); }

	/**Returns an Object from a String.
	 * Reading an Object from a String is useful only for simple Objects
	 * with fixed Length, because the String Provider cannot know the String Length,
	 * except if you use Length Indicators or Tags to bracket Objects. 	 */
//	public copyAble fromString(String arg){}

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
/*	public copyAble fromStream(StreamTokenizer ST) throws IOException
	{ return newInstance().fromStreamAt(ST); }

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
/*	public copyAble fromStream(InputStream IS) throws IOException
	{ return fromStream(new StreamTokenizer(IS)); }

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
/*	public copyAble fromStreamAt(InputStream IS) throws IOException
	{ return fromStreamAt(new StreamTokenizer(IS)); }

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
/*	public copyAble fromStream(Reader IS) throws IOException
	{ return fromStream(new StreamTokenizer(IS)); }

	/**Creates an uninitalized new Instance of it's class
	 * and fills it with the Contents read from the String.	 */
//	public copyAble fromStreamAt(Reader IS) throws IOException
//	{ return fromStreamAt(new StreamTokenizer(IS)); }


	/**Converts ANY Object (as opposed to the standard toString() Method)!
	 * Arrays of primitive Type or Object Type
	 * for simple Objects simply toString() is used.
	 * Similar to the copyAt() Routines in Polynom.
	 * Should be used in the toString() Routines of Array Types
	 * and Polygons.	 */
	final static public String toString (Object arg) { //First test for known structured Types
		Class C = arg.getClass();
		if (! C.isArray()) return arg.toString();	//simple Object
		Class Typ = C.getComponentType();	//determine the Type of Array Elements.
		StringBuffer Buffer = new StringBuffer(Starter);
		int Length = java.lang.reflect.Array.getLength(arg);
		if ((SeparatorLength != null) && (SeparatorLength.length() > 0))
			Buffer.append(Length).append (SeparatorLength);
		if (Typ.isPrimitive()) { 	//Array of primitive Types
			for (int i = -1; ++i < Length;) {
				 if  (Typ == Character.TYPE) Buffer.append(java.lang.reflect.Array.getChar	 (arg, i)).append(Separator);
			else if ((Typ == Integer  .TYPE) ||
					 (Typ == Long	  .TYPE) ||
					 (Typ == Byte	  .TYPE) ||
					 (Typ == Short	  .TYPE))Buffer.append(java.lang.reflect.Array.getLong	 (arg, i)).append(Separator);
			else if ((Typ == Float	  .TYPE) ||
					 (Typ == Double	  .TYPE))Buffer.append(java.lang.reflect.Array.getDouble (arg, i)).append(Separator);
			else if  (Typ == Boolean  .TYPE) Buffer.append(java.lang.reflect.Array.getBoolean(arg, i)).append(Separator);
			}
		} else { 	//Array of Objects, start Recursion
			Object[] tmp = (Object[]) arg;
			for (int i = -1; ++i < Length;)
				Buffer.append(toString(tmp[i])).append(Separator);
		}	//This is what you cannot do with an OutPut Stream!
		Buffer.setLength(Buffer.length()-Separator.length());	//take away the last Separator
		return Buffer.append(Stopper).toString();
	}

}
