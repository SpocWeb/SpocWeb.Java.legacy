/**
 * File  Name: Thing.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: IThing<p>
 * Description:
 * Root Interface of the reflect Package's classification hierarchy, denoting any
 * "Thing" that can report its own {@link Type}. Every other classifying Interface
 * ({@link IIndividual}, {@link IIntangible}, etc.) extends this one.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:46Z
 * digest: 0a4784c424b1fdf40b0e093afdc23b60cf6044ec78d43120bd9a6e370cdda543
 * stale: false
 * tags: [code/reflection_interface, code/domain_model]
 * concepts: [Domain Model, Object Classification]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public interface IThing {

	/** The Type Object representing this Interface */
	final static public Type TYPE = new Type(IThing.class);

	/** Reports this Thing's own Type.
	  * @return the {@link Type} Object representing this Thing's Type */
	public Type getType();

}
