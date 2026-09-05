/**
 * File  Name: SSW.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Derives the two remaining unknown Angles of a Triangle from two known Sides and the
 * Angle enclosed between them, flagging a possible second (ambiguous) Solution.
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
 * mtime: 2026-09-05T11:22:38Z
 * digest: 3e9fd6b74f6a293f6c079af02ba8b464eea743b6a37f3ba7b0741d05d8c147c5
 * stale: false
 * tags: [code/rule_based_validation]
 * concepts: [Side-Side-Angle Rule]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
 */
public class SSW extends ATriangleKnowledge {

	/**
	 * Constructor for SSW.
	 * @param tri_
	 */
	public SSW(Triangle tri_) {
		super(tri_);
	}

	/**
	 * Returns whether exactly one Angle and two Sides are known.
	 * @see structure.blackBoard.IKnowledge#check()
	 * @return true iif it makes sense to apply the WSW Algorithm
	 */
	public boolean check() {
		return (tri.numAngles() == 1) && (tri.numSides() == 2);
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
			if (!tri.isSideSet(i)) {
				continue;
			}
			double sin0 = Math.sin(tri.getAngle(i));
			Triangle.COMPLEMENT(ndx, i);
			if (tri.isSideSet(ndx[1]) && !tri.isAngleSet(ndx[1])) {
				double aSin = Math.asin(sin0 * tri.getSide(ndx[1]) / tri.getSide(i));
				if (tri.getSide(i) < tri.getSide(ndx[1])) {
					Triangle alternative = tri.copy();
					alternative.setAngle(ndx[1], Math.PI - aSin);
					tri.setAlternative(alternative); 
				} //there is a second Solution!
				tri.setAngle(ndx[1], aSin);
			}
			if (tri.isSideSet(ndx[2]) && !tri.isAngleSet(ndx[2])) {
				double aSin = Math.asin(sin0 * tri.getSide(ndx[2]) / tri.getSide(i));
				if (tri.getSide(i) < tri.getSide(ndx[2])) {
					Triangle alternative = tri.copy();
					alternative.setAngle(ndx[2], Math.PI - aSin);
					tri.setAlternative(alternative); 
				} //there is a second Solution!
				tri.setAngle(ndx[2], aSin);
			}
		}
	}

}
