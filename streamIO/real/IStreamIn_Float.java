package streamIO.real;

import streamIO.IAvailAble;
import streamIO.IIStreamIn;
import streamIO.IIterAble;
import streamIO.IMarkAble;
import streamIO.IOrdered;

/** Interface for a streamIO of float Numbers
  * e.g. for a Random Number Generator.
  * The Generator is implemented with primitive Types,
  * because of Performance Reasons.
  * The Assumption is that the Output Range is normed to [0..1)
  * The Generator is kept with simple Types for now,
  * mostly because of Performance Reasons.
  * The same Plethora of Classes as for IStreamIn and IStreamOut
  * can be implemented for this Interface, and even more,
  * because this Type is passive and more Operations can be predefined.
  * It is always possible to build a Wrapper around the Random Generator.
  * But unlike with Matrix Operations this is not wanted.  
  * 
  * Subclasses: 
  * @see streamIO.real.StreamIn_Float
  */
public interface IStreamIn_Float 
extends IIStreamIn, IAvailAble, IIterAble, IMarkAble, IOrdered  {
	
	/** Indicates that the streamIO is at an end, 
	 * unfortunately 0 is too frequent, and you cannot check NaN using == 
	 * TODO: Instead check that NaN != NaN
	 * or relie on the Fact that this Number is rare enough, 
	 * although it does not appear unsusual in a Stream!
	 */
	final static public float EOS = Float.intBitsToFloat(Integer.MIN_VALUE+1234567);// Float.NaN; //Float.MIN_VALUE; //
	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @return the next double Precision Number	 */
	public double nextDouble();
	
	/** @return the next single Precision Number	 */
	public float nextFloat();
	
	/** @return the current Value that was returned from the last nextItem() Method.	 */
	public double currDouble();  
	
	/** @return the current Value that was returned from the last nextItem() Method.	 */
	public float currFloat();  
	
	/** @return the next Value without moving to it.	 */
	public float peekFloat(); //throws    NoSuchMethodException; 
	
	/** @return the next Value without moving to it.	 */
	public double peekDouble(); //throws    NoSuchMethodException; 
	
	/** This is a type-safe Substitute for clone()
	 * returns a new Iterator for the same Base Set
	 * @return a new Iterator for the same Base Set
	 */
	public IStreamIn_Float FloatIterator(); 
	
}
