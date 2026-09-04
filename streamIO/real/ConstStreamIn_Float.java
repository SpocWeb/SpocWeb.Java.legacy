/*
 * File Name: ConstStreamIn_Float.java
 * Created on: 04.02.2004
 *
 */
package streamIO.real;

import streamIO.IReSetAble;
import streamIO.object.IStreamIn;

/**
 * Title: ConstStreamIn_Float<p>
 * Description:
 * Implements a constant Stream of float Number. 
 * Since there is only a single (unfortunately non-atomic) Parameter to this Class, 
 * this Parameter is made publicly available. 
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class ConstStreamIn_Float 
extends AStreamIn_Float {

	/** Empty Constructor defaulting the Value to 0 	 */
	public ConstStreamIn_Float() { }

	/** Initializing Constructor, setting the Value 	 */
	public ConstStreamIn_Float(final double value_) { currItem.Value = value_; }

	/** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	public double getMinDouble() { return currItem.Value; }

	/** @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	public double nextDoubleInternal() { return currItem.Value; }

	/** @see streamIO.real.IStreamIn_Float#getOrder()	 */
	public byte getOrder() { return IStreamIn.ORDER_CONST; }

	/** @see streamIO.real.AStreamIn_Float#getPosition()	 */
	public long getPosition() { return 0; }

	/** @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { return this; }

	/** @see streamIO.real.AStreamIn_Float#availAble()	 */
	public long availAble() { return Long.MAX_VALUE; }

	/** @see streamIO.real.AStreamIn_Float#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; } 

}
