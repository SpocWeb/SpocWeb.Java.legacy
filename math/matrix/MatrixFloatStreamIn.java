/*
 * File Name: MatrixFloatStreamIn.java
 * Created on: 01.06.2003
 *
 */
package math.matrix;

import streamIO.object.AStreamIn;

/**
 * Iterates a {@link MatrixFloat}'s row vectors back to front, from the last row to the first.
 *
 * <p>Purpose: iterator for the MatrixFloat Class (in reverse Order).
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:42:58Z
 * digest: 1cb29bef72324d3bdc13e6bac5f0c8fe40369d33c6f54ec3aa56f91b7351e726
 * stale: false
 * tags: [code/matrix_operations]
 * concepts: [Float Matrix Row Stream Iterator]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 */
public class MatrixFloatStreamIn
extends AStreamIn {

	protected int currPos;

	final MatrixFloat matrix;

	/** Creates a reverse-order row iterator positioned just past the last row of {@code matrix_}. */
	public MatrixFloatStreamIn(final MatrixFloat matrix_) {
		this.matrix = matrix_;
		currPos = matrix.getInt();
	}

	/** Returns the row vector at the current iteration position. */
	public float[] currVector() {
		if (currPos >= matrix.getInt())
			throw new IllegalStateException("no row read yet: call nextVector() first");
		return matrix.items[currPos]; }

	/** Decrements the iteration position and returns the row vector now at it. */
	public float[] nextVector() { return matrix.items[--currPos]; }

	/** Returns the next row vector as an untyped item.
	 * @see Stream.IFactory#nextItem()	 */
	public Object nextItem() { return nextVector(); }

	/** Returns the current row vector as an untyped item.
	 * @see Stream.Object.StreamIn#currItem()	 */
	public Object currItem() { return currVector(); }

	/** Returns the number of rows still available before the start of the matrix.
	 * @see Stream.IAvailAble#availAble()	 */
	public long availAble() { return currPos; }

	/** Returns the maximum mark size, equal to the rows still available.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return availAble(); }

	/** Returns how many rows have been consumed since iteration started.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return matrix.getInt()-currPos; }

}
