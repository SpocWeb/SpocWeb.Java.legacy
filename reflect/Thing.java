/**
 * File  Name: Thing.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: Thing<p>
 * Description:
 * Minimal concrete Implementation of {@link IThing}, returning the shared
 * {@link IThing#TYPE} constant for every Instance. Serves as the root default
 * Implementation of the classification hierarchy rooted at {@link IThing}.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:23:41Z
 * digest: 0f64399909c8e1b83f25bb752f289fe676dbbf3709cf6fca35552cdebd1cbf8a
 * stale: false
 * tags: [code/reflection_interface, code/domain_model]
 * concepts: [Domain Model, Object Classification]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public class Thing implements IThing {

	/**
	 * Constructor for Thing.
	 */
	public Thing() {
		super();
	}

	/**
	 * Reports this Thing's Type, which is always the shared {@link IThing#TYPE} constant.
	 * @return the shared {@link IThing#TYPE} Type Object.
	 * @see reflect.IThing#getType()
	 */
	public Type getType() { return IThing.TYPE; }

}
