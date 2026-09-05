/**
 * File  Name: IIndividual.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: IIndividual<p>
 * Description:
 * Marker Interface classifying a {@link IThing} as a concrete, individual instance
 * (as opposed to an {@link IIntangible} such as a Type or abstract concept),
 * following an upper-ontology style distinction (individual vs. abstract Thing).
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:31Z
 * digest: c2f4cbb5ba21efef35cc8c109b5b9d74a43ed6b6952599ec7672bb9899a092c9
 * stale: false
 * tags: [code/reflection_interface, code/domain_model]
 * concepts: [Domain Model, Object Classification]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public interface IIndividual extends IThing {

	/** The Type Object representing this Interface */	
	final static public Type TYPE = new Type(IIndividual.class); 

}
