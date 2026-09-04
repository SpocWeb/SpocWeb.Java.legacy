/*
 * File Name: AMatrix.java
 * Created on: 02.11.2003
 *
 */
package math.matrix;

import math.vector.AVector;

/**
 * Title: AMatrix<p>
 * Description:
 * Purpose:
 * Base Class for Band- and regular Matrices. 
 * holding the usual Flags. 
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

	/** @return the Permutation due to Pivoting.	 */
	public int[] getRows() {
		if (rows == null) return null;
		int[] tmp = new int[rows.length];
		System.arraycopy(rows, 0, tmp, 0, rows.length);
		return tmp;	}

	/** @return the Sign of the Permutation due to Pivoting.	 */
	final public boolean getSign() { return sign; }

	/** @return true when this Matrix Contains the LU Decomposition.	 */
	public boolean isDecomposedLU() { return rows != null; }

}
