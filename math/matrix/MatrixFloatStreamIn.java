/*
 * File Name: MatrixFloatStreamIn.java
 * Created on: 01.06.2003
 *
 */
package math.matrix;

import streamIO.object.AStreamIn;

/**
 * Title: MatrixFloatStreamIn<p>
 * Description:
 * Purpose: 
 * Iterator for the MatrixFloat Class (in reverse Order) 
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
 * @author mheuer
 * @version	1.0
 *
 */
public class MatrixFloatStreamIn 
extends AStreamIn {
	
	protected int currPos;
	
	final MatrixFloat matrix;
	
	public MatrixFloatStreamIn(final MatrixFloat matrix_) {
		this.matrix = matrix_;
		currPos = matrix.getInt();
	}
	
	public float[] currVector() { return matrix.items[currPos]; }
	
	public float[] nextVector() { return matrix.items[--currPos]; }
	
	/** @see Stream.IFactory#nextItem()	 */
	public Object nextItem() { return nextVector(); }

	/** @see Stream.Object.StreamIn#currItem()	 */
	public Object currItem() { return currVector(); }
	
	/** @see Stream.IAvailAble#availAble()	 */
	public long availAble() { return currPos; }
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return availAble(); }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return matrix.getInt()-currPos; }

}
