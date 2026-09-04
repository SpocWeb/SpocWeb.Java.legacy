/*
 * Created on 19.03.2006
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import streamIO.IIStreamOut;

/**
 * Title:
 * <p>
 * Description: 
 * Purpose: 
 * Defines the Interface for a Stream 
 * that allows to write structured Information.
 * 
 * Design Decisions / Implementation Details: Instead of extending the
 * IStreamOutPrimitive Interface, it would be better to completely wrap and hide
 * it or give Access to it only through a different Interface so as not to
 * clutter both!
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 * 
 * Copyright: Copyright (c) Matthias Heuer
 * <p>
 * Company: personal
 * <p>
 * Created on 10-26-2002, 12:47 PM
 * <p>
 * 
 * @author heuerm
 * @version 1.0
 */
public interface IStreamOutStruct 
extends IStreamOutPrimitive, IStreamOutStructArrays, IIStreamOut {
	
	/** Reserved Keyword for the Type Information Attribute 	 */
	final public static String STR_VALUE = "_value_"; 
	
	final public static String STR_CLASS = "_class_"; 
	
	final public static String STR_REF_ID = "_refId_"; 
	
	final public static String STR_OBJ_ID = "_objId_"; 
	
	/** Name of the 'Dummy' Name-Value Pair for the Member Name 	 */
	//public static String STR_NAME = "_name_"; 
	
	/**
	 * return the underlying Stream for primitive Values
	 * 
	 * @return the underlying Stream for primitive Values not necessary, since
	 *         it (still) extends IStreamOutPrimitive
	 */
	
	//public IStreamOutPrimitive getStreamOutPrimitive();
	
	///////////////////////////////////////////////////////////////////////////
	/// Generic Structures
	///////////////////////////////////////////////////////////////////////////
	/** returns the Name of the currently open Structure */
	public String peek_Struct();
	
	/**
	 * Opens up a Structure with the same given Opening and Closing Name.
	 * Since both are the same (e.g. Quote Chars), the Structure cannot be
	 * nested and is thus primitive (typically Strings) Expects to be followed
	 * by a Name-Value Pair denoting the Type of the Object to be created.
	 * 
	 * @param name the Name for the Structure
	 * @param obj the Object to write to this Stream. 
	 * @return this Stream or null, if the Object was already added to the Stream. 
	 */
	public IStreamOutStruct open_Struct(final String name, final Object obj);
	
	/** opens the given Struct and closes it right away	 */
	public IStreamOutStruct writeStruct(final String name, final Object obj); 

	/**
	 * Opens up a Structure with the same given Opening and Closing Name.
	 * Since both are the same (e.g. Quote Chars), the Structure cannot be
	 * nested and is thus primitive (typically Strings) Expects to be followed
	 * by a Name-Value Pair denoting the Type of the Object to be created.
	 * 
	 * @param name
	 *            the Name for the Structure
	 * @return this Stream or (in Case of direct copying) prepares the named
	 *         Member Object to be filled with a Reference
	 */
	public IStreamOutStruct open_Struct(final String name);
	
	/**
	 * closes the current Structure with checking
	 * 
	 * @param name
	 *            the Name of the Struct (to be able to check it).
	 * @return TODO
	 */
	public IStreamOutStruct closeStruct(final String name);
	
	/**
	 * closes the current Structure without checking
	 * 
	 * @return TODO
	 */
	public IStreamOutStruct closeStruct();
	
	/** closes all open Structures */
	public void closeAll();
	
	///////////////////////////////////////////////////////////////////////////
	/// Lists of Values
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Opens up an unnamed Struct. 
	 * Typically used for a simple List of Values. Examples are primitive Values e.g. in Arrays
	 * or Name-Value Pairs separated e.g. by White Space like XML Attributes.
	 * 
	 * @return this Stream to allow for Concatenation
	 */
	public IStreamOutStruct open_Struct(); //open_List(); 
	
	///////////////////////////////////////////////////////////////////////////
	/// Name-Value Pairs
	///////////////////////////////////////////////////////////////////////////

	/**
	 * writes the Name of a Name-Value Pair to the underlying Stream The Value
	 * can be written directly to the Stream without having to buffer it into a
	 * String.
	 * 
	 * @param name
	 *            the Name of the Pair
	 * @return this Stream to allow for Concatenation
	 */
	public IStreamOutStruct writeName(final String name);

	/**
	 * writes the given Name-Value Pair to the underlying Stream
	 * 
	 * @param name
	 *            the Name of the Pair
	 * @param value
	 *            the Value of the Pair as a single String
	 * @param useQuotes
	 *            optional Flag whether to use Double Quotes to enclose the
	 *            Value In XML this results in using " instead of ' In
	 *            Structures this results in enclosing the Value in Double
	 *            Quotes with it's own Escaping Mechanism using duplicate Double
	 *            Quotes. Optional, since the Writer can decide on it's own
	 *            whether it is a) necessary to use Quoting and b) which Quote
	 *            Character to use best.
	 * @return this Stream to allow for Concatenation
	 */
	public IStreamOutStruct writeNameValuePair(final String name,
			final String value);

	/**
	 * writes the given Name-Value Pair to the underlying Stream
	 * 
	 * @param name
	 *            the Name of the Pair
	 * @param value
	 *            the Value of the Pair as a single String
	 * @param useQuotes
	 *            Flag whether to use Double Quotes to enclose the Value In XML
	 *            this results in using " instead of ' In Structures this
	 *            results in enclosing the Value in Double Quotes with it's own
	 *            Escaping Mechanism using duplicate Double Quotes.
	 * @return this Stream to allow for Concatenation
	 */
	public IStreamOutStruct writeNameValuePair(final String name,
			final String value, final boolean useQuotes);
	
	///////////////////////////////////////////////////////////////////////////
	/// Quoting
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts the Quote-Mode.
	 * 
	 * @return this Stream to allow for Concatenation
	 */
	public IStreamOutStruct open_Quote();
	
	/**
	 * Ends the Quote-Mode.
	 * 
	 * @return this Stream to allow for Concatenation
	 */
	public IStreamOutStruct closeQuote();
	
}