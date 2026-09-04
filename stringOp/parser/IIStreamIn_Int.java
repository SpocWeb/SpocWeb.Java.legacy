/*
 * Created on 25.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package stringOp.parser;

import streamIO.IPushBackAble;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Defines the Interface for a plain input Stream 
 * with discrete Objects/Values, expressed by an int, 
 * but without any Characteristics;
 * Neither the algebraic, nor the topological Properties of int are used. 
 *
 * Design Decisions / Implementation Details:
 * 
 * Known SubClasses: <none>
 * Parsers typically use and also implement this Interface, 
 * to extend it with specifc Methods to parse their Grammar. 
 * In particular they implement a Dispatcher, that decides, 
 * which Structure follows, depending on the next Character or Token. 
 * 
 * Known Uses: <none>
 * 
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public interface IIStreamIn_Int 
extends IPushBackAble {
	
	/**
	 * Integer Encoding of the End Of File (EOF) Code.
	 * This Value is returned by read() when the End of the Input streamIO is reached.
	 * Since the Default of -1 returned by the InputStream.read() Method 
	 * and the IndexOf() Methods for Arrays is quite frequent, 
	 * I rather use MIN_Value, which restricts the Value Set to a symmetric Range around 0. 
	 * Additionally -MIN_VALUE = MIN_VALUE just like -0 = 0, 
	 * so MIN_VALUE should be used to indicate an unusual Value like NaN. 
	 * 
	 * Additionally it should not only be tested for EOF, but also whether available() returns a negative Value: 
	 * if ((EOF == (val = ths.read())) && (ths.available() < 0)) 
	 * if ((EOF == (val = ths.read())) && !ths.isValid()) 
	 * 
	 * while ((EOF != (val = ths.read())) || (ths.available() >= 0)) {{ 
	 * while ((EOF != (val = ths.read())) ||  ths.isValid()) { 
	 */
	final static public int EOF = Byte.MIN_VALUE; // -1; //Integer.MIN_VALUE; //-1;
	
	/** @return the next Integer Number (converts IOException into a RuntimeException) 	 */
	public int nextInt();
	
}
