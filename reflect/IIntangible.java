/**
 * File  Name: IIntangible.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: IIntangible<p>
 * Description:
 * Marker Interface classifying a {@link IThing} as intangible - an abstract or
 * conceptual entity such as a {@link IMathThing}/{@link IType}, as opposed to a
 * concrete {@link IIndividual}.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:34Z
 * digest: c2f4cbb5ba21efef35cc8c109b5b9d74a43ed6b6952599ec7672bb9899a092c9
 * stale: false
 * tags: [code/reflection_interface, code/domain_model]
 * concepts: [Domain Model, Object Classification]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public interface IIntangible extends IThing {

	/** The Type Object representing this Interface */	
	final static public Type TYPE = new Type(IIntangible.class); 

}
