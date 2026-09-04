/**
 * File  Name: WWW.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Title: WWW<p>
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
class WWW extends ATriangleKnowledge {

	/**
	 * Constructor for WWW.
	 * @param tri_ the Triangle considered
	 */
	public WWW(Triangle tri_) {
		super(tri_);
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : WWW Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see structure.blackBoard.IKnowledge#check()
	 * @return true iif it makes sense to apply the WWW Algorithm 
	 */
	public boolean check() {
		return tri.numAngles() == 2;
	}

	/** 
	 * instead of returning the Success,
	 * it throws an Exception to indicate futile Use of this Rule
	 * @see structure.blackBoard.IKnowledge#update()
	 */
	public void update() {
		int unknown = -1;
		double sum = 0;
		for (int i = 3; --i >= 0;) {
			if (!tri.isAngleSet(i)) {
				if (unknown != -1) {
					throw new RuntimeException("Illegal use! Too many unknown Angles");
				}
				unknown = i;
				continue;
			}
			sum += tri.getAngle(i);
		}
		if (unknown == -1) {
			throw new RuntimeException("Illegal use! Too few unknown Angles");
		}
		tri.setAngle(unknown, Math.PI - sum);
	}

}