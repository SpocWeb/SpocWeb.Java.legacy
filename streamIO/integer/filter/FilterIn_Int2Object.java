package streamIO.integer.filter; //

import math.vector.VectorChar;
import streamIO.IIStreamIn;
import streamIO.IReSetAble;
import streamIO.exception.BaseException;
import streamIO.integer.IStreamIn_Int;
import streamIO.integer.StreamIn_Arithmetic;
import streamIO.object.AStreamIn;
import streamIO.object.ArrayStreamIn;
import streamIO.object.IStreamIn;
import function.byref.ByRefLong;

/**
  * Title: FilterIn_Char2Object<p>
  * Description:
  * Bridges the IStreamIn_Int Interface to the StreamIn Interface.
  * Converts Streams of Integer Numbers into Streams of Objects. 
  * 
  * Reuses the same ByRefFloat to save Instantiation
  * when large Quantities of Values are needed.
  * 
  * Use @see streamIO.CopyStreamIn to create new Instances. 
  * @see streamIO.Float.FilterIn_Float2Object for a formally identical Class. 
  * 
  * Known SubClasses:
  * @deprecated since IStreamIn_Int extends IStreamIn
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	05-19-2001, 01:02 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T21:45:12Z
  * digest: a49e473fe406481549999bc8f5f52b6574ca38163306f449d0314504a75f5193
  * stale: false
  * tags: [code/stream_filter]
  * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterIn_Int2Object
extends AStreamIn {
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	//  static Constants and Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** Constant denoting the Start Value of the Alphabet  */
	final static public char START_VALUE = 'A';
	
	/** Constant denoting the Final Value of the Alphabet  */
	final static public char STOP_VALUE = 'Z';
	
	/** Constant containing all Vowels of the Alphabet  */
	final static public char[] VOWELS = {'A','E','I','O','U'};
	
	/** Constant containing all Vowels of the Alphabet  */
	final static public Character[] Vowels = VectorChar.const2Const(VOWELS);
	
	/** Constant streamIO containing all Vowels of the Alphabet  */
	final static public IStreamIn VowelStream = new ArrayStreamIn(Vowels);//, ORDER_ASC); 
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Constant streamIO containing all Vowels of the Alphabet  */
	final static public IStreamIn getCharacterStream() {
		return new FilterIn_Int2Object(
		new StreamIn_Arithmetic(START_VALUE, STOP_VALUE+1)); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Input streamIO being Transformed	 */
	protected IStreamIn_Int in;
	
	/** Local Reference to the Object returned by nextItem() */
	protected ByRefLong value = new ByRefLong();
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FilterIn_Int2Object(final IStreamIn_Int in_) {
		this.in = in_; }
	
	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * If this Container does not support multiple concurrent Iterators, returns 'null'
	  * @return  a new Input streamIO of the Objects in this Container.
	  * @see     Math.Iterator
	  */
	public IIStreamIn Iterator() {
		final FilterIn_Int2Object ret; //first create the (outer) Filter...
		try { ret = (FilterIn_Int2Object) clone(); }
		catch (final CloneNotSupportedException x) { 
			throw new BaseException("Should never happen!", x); 
		}
		ret.in = in.IntIterator(); //...then the inner Stream
		ret.value = new ByRefLong();
		return ret; }
		
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamIn: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	  * Returns the boxed long value produced by the most recent {@link #nextItem()} call.
	  * @return the current Elements in this streamIO.
	  */
	public Object currItem() { return value; }

	/**
	  * Reports whether the wrapped integer stream has reached EOF.
	  * @return the Number of Elements left in this streamIO.
	  */
	public long availAble() {
		if (IStreamIn_Int.EOF == value.Value)
			return -1;
		return 1; }

	/**
	  * Reads the next long value from the wrapped stream and boxes it into {@link #value}.
	  * @return the next Element in this streamIO.
	  */
	public Object nextItem() {
		if (IStreamIn_Int.EOF == (value.Value = in.nextLong()))
			return null;
		return value; }

	/** Resets the wrapped stream.
	 * @see IStreamIn#reSet() */
	public IReSetAble reSet() { return in.reSet(); }

	/**
	  * Returns the order of the wrapped stream's elements.
	  * @return the Order of the Elements in this streamIO.
	  * -1 for descending
	  *  0 for unordered
	  * +1 for ascending
	  */
	public byte getOrder() { return in.getOrder(); }

	/** Returns the maximum mark size of the wrapped stream.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return in.getMaxMarkSize(); }

	/** Returns the current position of the wrapped stream.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return in.getPosition(); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() throws Exception {
		System.out.println("Testing " + FilterIn_Int2Object.class.getName()); }
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(); }
	
}
