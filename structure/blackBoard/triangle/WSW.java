/**
 * File  Name: WSW.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Title: WSW<p>
 * Description:
 * Calculates the third Angle from two other known Angles
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
class WSW extends ATriangleKnowledge {

	/**
	 * Constructor for WSW.
	 * @param tri_ the Triangle considered
	 */
	public WSW(Triangle tri_) {
		super(tri_);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : WSW Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see structure.blackBoard.IKnowledge#check()
	 * @return true iif it makes sense to apply the WSW Algorithm 
	 */
	public boolean check() {
		return (tri.numAngles() > 1) && (tri.numSides() > 0) && (tri.numSides() < 3);
	}

	/** 
	 * instead of returning the Success,
	 * it throws an Exception to indicate futile Use of this Rule
	 * @see structure.blackBoard.IKnowledge#update()
	 */
	public void update() {
		//		if (testWWW()) { //calculate the last Angle
		//			calcWWW();
		//		}
		for (int i = 3; --i >= 0;) {
			if (!tri.isSideSet(i)) { 
				continue;
			}
			Triangle.COMPLEMENT(ndx, i);
			if(!tri.isSideSet(ndx[1])) {
				tri.setSide(ndx[1], tri.getSide(i) * Math.sin(tri.getAngle(ndx[1])) / Math.sin(tri.getAngle(i)));
			}
			if(!tri.isSideSet(ndx[2])) {
				tri.setSide(ndx[2], tri.getSide(i) * Math.sin(tri.getAngle(ndx[2])) / Math.sin(tri.getAngle(i)));
			}
		}
	}

}