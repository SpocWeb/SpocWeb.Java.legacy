package function.string;

import java.util.Map;

import math.vector.VectorString;

/**
  * Title: StringFunction<p>
  * Description:
  * Purpose:
  * Maps (the Characters of) an Input String to a String from a HashTable
  * This is used e.g. to replace special Characters 
  * in Encodings like XML or German using ANSI. 
  * 
  * This Packages is dedicated to Mapping Functions 
  * which should make assembling Mapping Filters more modular.
  * 
  * Known Uses: 
  * for encoding Unicode Strings (e.g. XML) into other Encodings e.g. ASCII 
  * 
  * @see streamIO.Byte.Encoding.FilterString2Char
  * @see function.string.Char2String maps Characters 
  * or the Character at a certain Position of the Input String to a String (very fast)  
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-21-2002, 06:56 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class StringFunction 
extends AStringFunction {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
    
	/** (Hash-)Map used internally for mapping	 */
	protected Map map;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constructor: creates a Mapping of Characters to Strings.	 */
	public StringFunction(final char[] chars_, final String[] strings_) {
		map = VectorString.MAP(strings_, chars_); }
	
	/** Constructor: Creates the Mapping vals_[i][0] => vals_[i][1] by building up a HashMap
	  * @return a HashMap with get(vals_[i][0]) == vals_[i][1]
	  */
	public StringFunction(final String[][] vals_, final int keyCol, final int valCol) {
		map = VectorString.MAP(vals_, keyCol, valCol); }
	
	/** Constructor: Creates the Mapping: keys_ => vals_ by building up a HashMap
	  * @return a HashMap with get(keys_[i]) == vals_[i]
	  */
	public StringFunction(final String[] keys_, final String[] vals_) {
		map = VectorString.MAP(keys_, vals_); }
	
	/** Constructor: Creates the Mapping: keys_ => vals_ by building up a HashMap
	  * @return a HashMap with get(keys_[i]) == vals_[i]
	  */
	public StringFunction(final String[] keys_, final char[] vals_, final String container, final int pos) {
		map = VectorString.MAP(keys_, vals_, container, pos); }
	
	/** Constructor: Creates the Mapping: keys_ => vals_ by building up a HashMap
	  * @return a HashMap with get(keys_[i]) == vals_[i]
	  */
	public StringFunction(final String[] keys_, final char[] vals_) {
		map = VectorString.MAP(keys_, vals_); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IStringFunction: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the Mapping of arg by this Function, null otherwise 	 */
	public String Map(final String arg) { return (String) map.get(arg); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
	}

}
