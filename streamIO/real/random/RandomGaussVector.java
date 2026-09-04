/*
 * File Name: RandomGaussVector.java
 * Created on: 09.03.2004
 *
 */
package streamIO.real.random;

import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomLong;
import streamIO.real.IStreamIn_Float;

/**
 * Title: RandomGaussVector<p>
 * Description:
 * Implements a Stream of random Vectors with Gaussian Distribution. 
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
public class RandomGaussVector 
extends RandomUniformVector {
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Constructors	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param ran_
	 * @param width_
	 * @param offset_
	 */
	public RandomGaussVector(final IStreamIn_Float ran_, final float[] width_, final float[] offset_) {
		super(new RandomGauss(ran_), width_, offset_);
	}

	/**
	 * @param ran_
	 * @param width_
	 */
	public RandomGaussVector(final IStreamIn_Float ran_, final float[] width_) {
		super(new RandomGauss(ran_), width_);
	}

	/**
	 * @param ran_
	 * @param length
	 */
	public RandomGaussVector(final IStreamIn_Float ran_, final int length) {
		super(new RandomGauss(ran_), length);
	}

	/**
	 * @param ran_
	 * @param width_
	 * @param offset_
	 */
	public RandomGaussVector(final float[] width_, final float[] offset_) {
		super(new RandomGauss(), width_, offset_);
	}

	/**
	 * @param ran_
	 * @param width_
	 */
	public RandomGaussVector(final float[] width_) {
		super(new RandomGauss(), width_);
	}

	/**
	 * @param ran_
	 * @param length
	 */
	public RandomGaussVector(final int length) {
		super(new RandomGauss(), length);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.IFactory#nextItem()	 */
	public Object nextItem() {
		for (int i = value.length; --i >= 0;) {
			value[i] = ran.nextFloat(); 
			if (width != null) {
				value[i] *= width[i]; }
			if (offset != null) {
				value[i] += offset[i]; }
		}
		return value; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// static Testing and main() Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	private static final void testGaussSpeed() {
		System.out.println("Testing the Speed of Gaussian Random Number Generators "); 
		final int numItems = 10000000; 
		final IStreamIn_Bound_Int uniform = new RandomLong();
		final RandomGauss  gauss  = new RandomGauss (uniform); 
		final RandomGauss2 gauss2 = new RandomGauss2(uniform);
		long timer = System.currentTimeMillis(); 
		for (int i = numItems; --i >= 0;) {
			gauss.nextDouble();
		}
		System.out.println("Gauss  Time for "+numItems+" Values (ms):"+(System.currentTimeMillis()-timer));
		timer = System.currentTimeMillis(); 
		for (int i = numItems; --i >= 0;) {
			gauss2.nextDouble();
		}
		System.out.println("Gauss2 Time for "+numItems+" Values (ms):"+(System.currentTimeMillis()-timer));
	}
	
	final static public void main(final String[] args) {
		testGaussSpeed(); 
	}

}
