/*
 * Created on 19.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.integer;

import java.io.IOException;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Generates a random Vector with a correlated Probability Distribution
 * described by the given Correlation Matrix. 
 *
 * Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public interface IStreamIn_Struct 
extends IStreamIn_Primitive {
	
	/** Default Separator Characters for writing to Streams	 */
	public static final String DEFAULT_SEPS = 
		"\\\"{}\n,=\t"; //"\\\"\0\0\n =\t"; // 
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables: Separator Semantics by decreasing Importance
	////////////////////////////////////////////////////////////////////////////
	
	/** Indicator of a non Separator Token.
	  * The Value corresponds to the Position in the Separator String */
	final static public byte TAG_PLAIN = 0;
	
	/** Indicator of the End of the Input streamIO.
	  * The Value 127 does NEITHER correspond to the Position in the Separator String
	  * NOR is it the same as StreamTokenizer.TT_EOF. 	 */
	final static public byte TAG_EOF    = Byte.MAX_VALUE;
	
	/** Indicates an Escape Sequence. 
	 * Escaping overrides any Separator and is thus more important 
	 * than any other Separator. 
	 */
	final static public byte TAG_ESCAPE = TAG_EOF-1;
	
	/** Indicates a final Quoting Character for primitive Strings, 
	 * which cannot be nested and thus overrides all other Markup Structure. 
	 * Alternative Quoting Characters like ' and " in XML make it necessary 
	 * to additionally test the actual Character. 
	 */
	final static public byte TAG_QUOTE  = TAG_EOF-2;
	
	/** Indicates the Opening Bracket Character for Lists 	 */
	final static public byte TAG_OPEN   = TAG_EOF-3;
	
	/** Indicates the Closing Bracket Character for Lists 	 */
	final static public byte TAG_CLOSE  = TAG_EOF-4;
	
	/** Indicates the Row Separator Character for Tables 
	 * unfortunately this is often only a Formatting Character, 
	 * but then it needs to be explicitly ignored! 	 */
	final static public byte TAG_ROW    = TAG_EOF-5;
	
	/** Indicates the Column Separator Character for Lists 	 */
	final static public byte TAG_COL    = TAG_EOF-6;
	
	/** Indicates the Name-Value-Pair Separator Character 	 */
	final static public byte TAG_PAIR   = TAG_EOF-7;
	
	/** Indicates the Bracket Opening Character for Lists 	 */
	final static public byte TAG_FORMAT = TAG_EOF-8;
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** return the Value of the next Token in this Stream
	 * @return the Value of the next Token in this Stream
	 * To retrieve the corresponding Value, use currItem() 
	 */
	public int nextToken() throws IOException; 
	
	/** return the Value of the current Token in this Stream
	 * @return the Value of the current Token in this Stream
	 * To retrieve the corresponding Value, use currItem() 
	 */
	public int currToken(); 
	
	///////////////////////////////////////////////////////////////////////////
	/// Separators and Escaping allows to split up Lists of arbitrary Strings 
	///////////////////////////////////////////////////////////////////////////
	
	/** return the String Representation of the next Value read from the Stream
	 * @return the String Representation of the next Value read from the Stream
	 */
	public String nextString(); 
	
	///////////////////////////////////////////////////////////////////////////
	/// Separators and Escaping allows to split up Lists of Numbers 
	///////////////////////////////////////////////////////////////////////////
	
	/** return an Array of byte Values read from the Stream
	 * @return an Array of byte Values read from the Stream
	 */
	//public byte[] nextBytes(); 
	
	/** return an Array of char Values read from the Stream
	 * @return an Array of char Values read from the Stream
	 */
	//public char[] nextChars(); 
	
	/** return an Array of int Values read from the Stream
	 * @return an Array of int Values read from the Stream
	 */
	public int[] nextInts(); 
	
	/** return an Array of short Values read from the Stream
	 * @return an Array of short Values read from the Stream
	 */
	public short[] nextShorts(); 
	
	/** return an Array of long Values read from the Stream
	 * @return an Array of long Values read from the Stream
	 */
	public long[] nextLongs(); 
	
	/** return an Array of float Values read from the Stream
	 * @return an Array of float Values read from the Stream
	 */
	public float[] nextFloats(); 
	
	/** return an Array of double Values read from the Stream
	 * @return an Array of double Values read from the Stream
	 */
	public double[] nextDoubles(); 
	
	/** return an Array of Strings read from the Stream
	 * @return an Array of Strings read from the Stream
	 */
	public String[] nextStrings(); 
	
	/** return an Array of Objects read from the Stream
	 * @return an Array of Objects read from the Stream
	 */
	public Object[] nextItems(); 
	
}
