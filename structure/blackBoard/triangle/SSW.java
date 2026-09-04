/**
 * File  Name: SSW.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Title: enclosing_type<p>
 * Description:
 * Purpose:
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public class SSW extends ATriangleKnowledge {

	/**
	 * Constructor for SSW.
	 * @param tri_
	 */
	public SSW(Triangle tri_) {
		super(tri_);
	}

	/**
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
