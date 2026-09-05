/**
 * File  Name: WWW.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

/**
 * Derives the last unknown Angle of a Triangle from the other two known Angles, since all
 * three must sum to Pi.
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
 * mtime: 2026-09-05T11:22:48Z
 * digest: e62c6892336c5188c25f78108e83738f694fd36301d245ae7aaacb894fa8291e
 * stale: false
 * tags: [code/rule_based_validation]
 * concepts: [Angle-Angle-Angle Rule]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
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
	 * Returns whether exactly two of the Triangle's three Angles are known.
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