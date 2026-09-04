/**
 * File  Name: Adder.java
 * Created on: 10.04.2003
 */
package function.real;

import streamIO.object.IStreamIn;

/**
  * Title: Adder<p>
  * Description: AKA 'Offsetter'
  * Offsets the Elements of the streamIO, but hands them on unchanged,
  * so also other Operations can take place on them.
  * 
  * Especially useful to offset the streamIO for Sum 
  * and SumSquares for greater numerical Stability. 
  * 
  * TODO: this Function does the same as the corresponding Const Type! 
  *
  * Known SubClasses:
  * 
  * See also: 
  * @see function.real.Adder
  * @see function.real.Multiplier
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Adder 
extends StatefulFloatFunction {

	/** Initializing Constructor
	  * defaulting the Start Value to 0.0
	  * @param Generator the actual IStreamInNumber 	*/
    //public Adder() { super(); }
    
	/** Initializing Constructor
	  * @param startValue_ Start Value returned by this Filter */
	public Adder(double startValue_) { super(startValue_); }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/** @return the next single Precision Number	 */
	public float Map(final float value_) { //return (float) (Sum += Generator.nextDouble()); }
	    //	++count;
		return (float)(value_+_Value); }

	/** @return the next double Precision Number	 */
	public double Map(final double value_) { //return Sum += Generator.nextDouble(); }
	    //	++count;
		return value_+_Value; }

}
