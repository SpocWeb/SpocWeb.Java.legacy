/**
 * File  Name: IType.java
 * Created on: 01.11.2002
 */
package reflect;

/**
 * Title: IType <p>
 * Description:
 * Interface denoting a Type, i.e. a Set of Instances described by common Properties
 * and Methods rather than by enumerating its Members. Implemented by {@link Type},
 * which wraps a {@code java.lang.Class} (restricted to Interfaces) to expose the same
 * reflective Operations through this reflect Package's own Type abstraction.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:42Z
 * digest: c2f4cbb5ba21efef35cc8c109b5b9d74a43ed6b6952599ec7672bb9899a092c9
 * stale: false
 * tags: [code/reflection_interface, code/type_system]
 * concepts: [Domain Model, Object Classification]
 * facets: {layer: domain, status: stable, complexity: low}
 * -->
 */
public interface IType extends IMathThing {

	/** The Type Object representing this Interface */
	final static public Type TYPE = new Type(IType.class);

	/** additional Methods copied from Class */

}
