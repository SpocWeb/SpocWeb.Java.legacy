/**
 * File  Name: SSS.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Calculates the remaining unknown Angle(s) of a Triangle once all three Sides are known.
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
 * mtime: 2026-09-05T11:22:34Z
 * digest: 6d8dbb47da669ddb23af70a7f3d53ba4c1b3b001fbc971351ff149e16aef9b8f
 * stale: false
 * tags: [code/rule_based_validation]
 * concepts: [Side-Side-Side Rule]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
class SSS extends ATriangleKnowledge {

	/**
	 * Constructor for SSS.
	 * @param tri_ the Triangle considered
	 */
	public SSS(Triangle tri_) {
		super(tri_);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : WSW Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns whether all three Sides are known and at least one Angle is still missing.
	 * @see structure.blackBoard.IKnowledge#check()
	 * @return true iif it makes sense to apply the WSW Algorithm
	 */
	public boolean check() {
		return (tri.numSides() == 3) && (tri.numAngles() < 3);
	}

	/** 
	 * instead of returning the Success,
	 * it throws an Exception to indicate futile Use of this Rule
	 * @see structure.blackBoard.IKnowledge#update()
	 */
	public void update() {
		for (int i = 3; --i >= 0;) {
			if (tri.isAngleSet(i)) {
				continue;
			}
			Triangle.COMPLEMENT(ndx, i);
			tri.setAngle(i, Math.acos(
				((Triangle.SQR(tri.getSide(ndx[1]))
				+ Triangle.SQR(tri.getSide(ndx[2]))
				- Triangle.SQR(tri.getSide(i)))
				/ (2 * tri.getSide(ndx[1]) * tri.getSide(ndx[2])))));
		}
	}

}
