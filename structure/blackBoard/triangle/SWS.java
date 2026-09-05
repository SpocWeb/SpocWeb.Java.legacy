/**
 * File  Name: SWS.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Derives an unknown Side of a Triangle from the two adjacent Sides and their enclosed
 * Angle, via the Law of Cosines.
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
 * mtime: 2026-09-05T11:22:41Z
 * digest: 6cb4458886bdfc91ede42f91fb5cba51b037c9b809d00d43fc4a452d021837ad
 * stale: false
 * tags: [code/rule_based_validation]
 * concepts: [Side-Angle-Side Rule]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
class SWS extends ATriangleKnowledge {

	/**
	 * Constructor for SWS.
	 * @param tri_ the Triangle considered
	 */
	public SWS(Triangle tri_) {
		super(tri_);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : SWS Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns whether at least two Sides and one Angle are known.
	 * @see structure.blackBoard.IKnowledge#check()
	 * @return true iif it makes sense to apply the SWS Algorithm
	 */
	public boolean check() {
		return (tri.numSides() > 1) && (tri.numAngles() > 0);
	}

	/**
	 * instead of returning the Success,
	 * it throws an Exception to indicate futile Use of this Rule
	 * @see structure.blackBoard.IKnowledge#update()
	 */
	public void update() {
		for (int i = 3; --i >= 0;) {
			if (!tri.isAngleSet(i)) {
				continue;
			}
			Triangle.COMPLEMENT(ndx, i);
			if (tri.isSideSet(i)) {
				continue;
			}
			tri.setSide(
				i,
				Math.sqrt(
					  Triangle.SQR(tri.getSide(ndx[1]))
					+ Triangle.SQR(tri.getSide(ndx[2]))
					- 2 * tri.getSide(ndx[1]) * tri.getSide(ndx[2]) * Math.cos(tri.getAngle(i))));
		}
		//			if (sides[] == null) {
		//				sides[ndx[2]]  = new Double(sides[i].getDouble()*Math.sin(angles[ndx[2]].getDouble())/Math.sin(angles[i].getDouble())); }
	}
}
