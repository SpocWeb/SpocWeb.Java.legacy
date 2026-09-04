/**
 * File  Name: Thing.java
 * Created on: 01.11.2002
 */
package reflect;

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
public class Thing implements IThing {

	/**
	 * Constructor for Thing.
	 */
	public Thing() {
		super();
	}

	/**
	 * @see reflect.IThing#getType()
	 */
	public Type getType() { return IThing.TYPE; }

}
