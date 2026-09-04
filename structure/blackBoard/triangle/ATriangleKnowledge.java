/**
 * File  Name: ATriangleKnowledge.java
 * Created on: 26.10.2002
 */
package structure.blackBoard.triangle;

import structure.blackBoard.IKnowledge;

/**
 * Title: ATriangleKnowledge<p>
 * Description:
 * Abstract Base Class for all Knowledge Sources about Triangles 
 *
 * Design Decisions / Implementation Details:
 * Since all the State and Information about the Triangle
 * is stored in itself, these Methods could have been made stateless, 
 * and static but that would defy the usual Situation with a BlackBoard
 * where each Knowlegde Source has some internal Information cached
 * that are related to the concrete Object being analyzed.
 *
 * Known SubClasses: 
 * @see WWW
 * @see WSW
 * @see SSS
 * @see SSW
 *
 * Known Uses: 
 * @see Triangle
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
abstract class ATriangleKnowledge implements IKnowledge {

	/** Helper Array for generating Indices */
	protected int[] ndx = new int[3];

	/** Reference to the Triangle considered */
	protected Triangle tri;

	/**
	 * Method ATriangleKnowledge.
	 * @param tri_ the Triangle considered
	 */
	protected ATriangleKnowledge(Triangle tri_) {
		this.tri = tri_;
	}

}
