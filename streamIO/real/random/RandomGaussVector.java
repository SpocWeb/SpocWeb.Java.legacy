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
 * Generates a stream of vectors whose components are independently Gaussian-distributed.
 *
 * <p>Implements a Stream of random Vectors with Gaussian Distribution.
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:29:21Z
 * digest: ef48f5b8d514e211f5e7b1a7fc2b2db1469e7c873d70676b5dff58a436e5dd65
 * stale: false
 * tags: [code/random_number_generator, code/vector_math]
 * concepts: [Gaussian Random Vector Generator]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class RandomGaussVector 
extends RandomUniformVector {
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Constructors	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a Gaussian vector generator scaled by {@code width_} and offset by {@code offset_}.
	 * @param ran_ the underlying uniform stream driving the Gaussian generator
	 * @param width_ per-component scale factors
	 * @param offset_ per-component offsets added after scaling
	 */
	public RandomGaussVector(final IStreamIn_Float ran_, final float[] width_, final float[] offset_) {
		super(new RandomGauss(ran_), width_, offset_);
	}

	/** Creates a Gaussian vector generator scaled by {@code width_}, with no offset.
	 * @param ran_ the underlying uniform stream driving the Gaussian generator
	 * @param width_ per-component scale factors
	 */
	public RandomGaussVector(final IStreamIn_Float ran_, final float[] width_) {
		super(new RandomGauss(ran_), width_);
	}

	/** Creates an unscaled, unoffset Gaussian vector generator of the given dimension.
	 * @param ran_ the underlying uniform stream driving the Gaussian generator
	 * @param length the number of vector components
	 */
	public RandomGaussVector(final IStreamIn_Float ran_, final int length) {
		super(new RandomGauss(ran_), length);
	}

	/** Creates a Gaussian vector generator scaled by {@code width_} and offset by {@code offset_},
	 * using a default random source.
	 * @param width_ per-component scale factors
	 * @param offset_ per-component offsets added after scaling
	 */
	public RandomGaussVector(final float[] width_, final float[] offset_) {
		super(new RandomGauss(), width_, offset_);
	}

	/** Creates a Gaussian vector generator scaled by {@code width_}, with no offset, using a
	 * default random source.
	 * @param width_ per-component scale factors
	 */
	public RandomGaussVector(final float[] width_) {
		super(new RandomGauss(), width_);
	}

	/** Creates an unscaled, unoffset Gaussian vector generator of the given dimension, using a
	 * default random source.
	 * @param length the number of vector components
	 */
	public RandomGaussVector(final int length) {
		super(new RandomGauss(), length);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	// Methods	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Fills each component of the reused vector with a scaled, offset Gaussian value.
	 * @see streamIO.IFactory#nextItem()	 */
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
	
	/** Compares the throughput of {@link RandomGauss} and {@link RandomGauss2}.
	 * @param args unused command-line arguments
	 */
	final static public void main(final String[] args) {
		testGaussSpeed();
	}

}
