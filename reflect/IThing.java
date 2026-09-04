/**
 * File  Name: Thing.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: IThing<p>
 * Description:
 * Base Interface of all Interfaces; denoting any Thing
 *
 * Design Decisions / Implementation Details:
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
public interface IThing {

	/** The Type Object representing this Interface */
	final static public Type TYPE = new Type(IThing.class);

	/** @return the Type Object representing this Interface */
	public Type getType();

}
