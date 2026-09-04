/*
 * File Name: RandomUniformVector.java
 * Created on: 09.03.2004
 *
 */
package streamIO.real.random;

import math.integration.StratifiedMCIntegrator;
import math.vector.VectorFloat;
import streamIO.object.AStreamIn;
import streamIO.real.IStreamIn_Float;

/**
 * Title: RandomUniformVector<p>
 * Description:
 * Implements a Stream of random Vectors with uniform Distribution in each Dimension. 
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
public class RandomUniformVector 
extends AStreamIn {
	
	/**Creates a Random Point in an n-dim HyperRectangle defined by Regn[2*n]; 
	 * @see StratifiedMCIntegrator uses this Method for Recursive Stratified Sampling 
	 */	
	final static public void RANDOM_VECTOR(final IStreamIn_Float ran, final float[] pt, final float[] region0, final float[] region1, int start, final int stop) {
		for (int j=stop;--j>=start;) {
			pt[j]=region0[j]+(region1[j]-region0[j])*ran.nextFloat(); }
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Member Variables	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Corner of the Range for the random Numbers	 */
	protected final float[] offset; 
	
	/** Width of the Range for the random Numbers	 */
	protected final float[] width; 
	
	/** current Value of the random Numbers	 */
	protected final float[] value; 
	
	/** current Value of the random Numbers	 */
	protected final IStreamIn_Float ran; 
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Constructors	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** initializing Constructor
	 * 
	 * @param ran_ random Number Stream with normed uniform Distribution from  [0,1]
	 * @param width_ the widths of the Hyper-Rectangle, can be null, defaults to 1
	 * @param offset_ the offsets of the Hyper-Rectangle, can be null, defaults to 0
	 */
	public RandomUniformVector(final IStreamIn_Float ran_, final float[] width_, final float[] offset_) {
		this.ran = ran_;
		this.width = width_;
		this.offset = offset_; 
		this.value = new float[width != null ? width.length : offset.length];  
	}
	
	/** initializing Constructor	 */
	public RandomUniformVector(final IStreamIn_Float ran_, final float[] width_) {
		this.ran = ran_;
		this.width = width_;
		this.offset = null; 
		this.value = new float[width_.length];  
	}
	
	/** initializing Constructor	 */
	public RandomUniformVector(final IStreamIn_Float ran_, final int length) {
		this.ran = ran_;
		this.width = null;
		this.offset = null; 
		this.value = new float[length];  
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Methods
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.IFactory#nextItem()	 */
	public Object nextItem() {
		for (int i = value.length; --i >= 0;) 
			value[i] = ran.nextFloat(); 
		if (width != null) 
			VectorFloat.MUL_AT(value, width); 
		if (offset != null) 
			VectorFloat.ADD_AT(value, offset); 
		return value; }
	
	/** @see streamIO.object.IStreamIn#currItem()	 */
	public Object currItem() { return value; }
	
	/** @see streamIO.IAvailAble#availAble()	 */
	public long availAble() { return ran.availAble()/value.length; }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return ran.getMaxMarkSize()/value.length; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return ran.getPosition(); }
	
}
