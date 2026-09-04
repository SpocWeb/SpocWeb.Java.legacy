/*
 * File Name: AStreamIn_Bound.java Created on: 16.02.2004
 *  
 */
package streamIO.integer;

import streamIO.integer.random.IStreamIn_Bound_Int;

/**
 * Title: AStreamIn_Bound
 * <p>
 * Description: Purpose:
 * 
 * Purpose / Responsibilities of this Class
 * 
 * Design Decisions / Implementation Details: If similar Classes exist (e.g.
 * Polymorphism), characterize the specific Differences to compare these.
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
 * @author mheuer
 * @version 1.0
 *  
 */
public abstract class AStreamIn_Bound 
extends AStreamIn_Int
implements IStreamIn_Bound_Int {
	
	/** @see streamIO.integer.random.IStreamIn_Bound_Int#getMinValue() */
	abstract public long getMinValue();
	
	/** @see streamIO.IAvailAble#availAble()	 */
	abstract public long availAble();
	
	/** @see streamIO.IMarkAble#getMaxMarkSize()	 */
	abstract public long getMaxMarkSize(); 
	
	/** @see streamIO.IOrdered#getOrder()	 */
	abstract public byte getOrder();
	
	/** @see streamIO.IAvailAble#getPosition()	 */
	abstract public long getPosition(); 
	
	/** @see streamIO.real.AAStreamIn_Float#nextInt()	 */
	//abstract public int nextInt(); 
	
	/** @see streamIO.real.AAStreamIn_Float#nextLong()	 */
	abstract protected long nextLongInternal(); 
	
	/////////////////////////////////////////////////////////////////////////////////////
	//generic Routines for filling Arrays:
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	final public double getMinDouble() { return getMinValue(); }
	
	/** @return the maximum Value	 */
	final public double getMaxDouble() { return getMaxValue(); }

	/**
	 * fills the Array Range from this Stream
	 * 
	 * @param arr
	 *            the Array to fill
	 * @param start
	 *            the first Index to fill (inclusive)
	 * @param stop
	 *            the first Index not to fill (exclusive)
	 */
	public void fillArray(final long[] arr, final long maxVal, final int start,
			final int stop) {
		for (int i = stop; --i >= start;)
			arr[i] = nextLong(maxVal);
	}

	/**
	 * fills the whole Array from this Stream
	 * 
	 * @param arr
	 *            the Array to fill
	 */
	public void fillArray(final long[] arr, final long maxVal) {
		fillArray(arr, maxVal, 0, arr.length);
	}

	/**
	 * fills the Array Range from this Stream
	 * 
	 * @param arr
	 *            the Array to fill
	 * @param start
	 *            the first Index to fill (inclusive)
	 * @param stop
	 *            the first Index not to fill (exclusive)
	 * @param maxVal
	 *            the maximum Value to fill with (exclusive)
	 */
	public void fillArray(final int[] arr, final int maxVal, final int start,
			final int stop) {
		for (int i = stop; --i >= start;) {
			arr[i] = nextInt(maxVal);
		}
	}

	/**
	 * fills the whole Array from this Stream
	 * 
	 * @param arr
	 *            the Array to fill
	 * @param maxVal
	 *            the maximum Value to fill with (exclusive)
	 */
	public void fillArray(final int[] arr, final int maxVal) {
		fillArray(arr, maxVal, 0, arr.length);
	}

}