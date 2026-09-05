/*
 * File Name: AMatrix.java
 * Created on: 02.11.2003
 *
 */
package math.matrix;

import math.vector.AVector;

/**
 * Abstract base class for band and regular matrices, holding the LU-decomposition
 * permutation and sign flags shared by every matrix subclass.
 *
 * <p>Purpose: base class for Band- and regular Matrices, holding the usual flags.
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see streamIO.copy.group.ring.metric.body.vector.Matrix
 * @see math.MatrixDouble
 * @see math.MatrixBand
 * @see AMatrix
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:42:22Z
 * digest: 8033d7f295acb85370e8d937994c33b1ea9f796fc3b88dcd51c4a402f12c3319
 * stale: false
 * tags: [code/matrix_base_class, code/matrix_algebra]
 * concepts: [Matrix Base Class]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public abstract class AMatrix
extends AVector {

	/////////////////////////////////////////////////////////////////////////////////////
	//	Standard Fields for Matrices... 
	/////////////////////////////////////////////////////////////////////////////////////

	/** Contains the Permutation due to Pivoting.	 */
	protected int[] rows;

	/** Contains the Sign of the Permutation due to Pivoting.	 */
	protected boolean sign;

	/** Returns a defensive copy of the row-permutation array recorded by LU decomposition.
	 * @return the Permutation due to Pivoting.	 */
	public int[] getRows() {
		if (rows == null) return null;
		int[] tmp = new int[rows.length];
		System.arraycopy(rows, 0, tmp, 0, rows.length);
		return tmp;	}

	/** Returns whether the recorded row permutation is even or odd.
	 * @return the Sign of the Permutation due to Pivoting.	 */
	final public boolean getSign() { return sign; }

	/** Returns whether this matrix currently holds an LU decomposition's permutation state.
	 * @return true when this Matrix Contains the LU Decomposition.	 */
	public boolean isDecomposedLU() { return rows != null; }

}
