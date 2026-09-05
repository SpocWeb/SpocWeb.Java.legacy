/**
 * File  Name: IMathThing.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: IMathThing<p>
 * Description:
 * Marker Interface classifying a {@link IIntangible} as a mathematical/computational
 * Thing - the common super-Interface of {@link IType}, i.e. entities describable
 * purely by their formal Properties and Methods rather than by physical extension.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:38Z
 * digest: c2f4cbb5ba21efef35cc8c109b5b9d74a43ed6b6952599ec7672bb9899a092c9
 * stale: false
 * tags: [code/reflection_interface, code/domain_model]
 * concepts: [Domain Model, Object Classification]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public interface IMathThing extends IIntangible {

	/** The Type Object representing this Interface */	
	final static public Type TYPE = new Type(IMathThing.class); 

}
