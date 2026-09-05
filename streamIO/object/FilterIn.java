package streamIO.object;

import streamIO.IIStreamIn;

/**
 * FilterStreamIn.java
 * NOP-Stream, hands the Elements through unchanged. 
 * Delegates to any IStreamIn Implementor and thus allows the Use of StreamIn
 * without these Implementors having to implement the whole StreamIn Interface.
 * 
 * Created on 3. M�rz 2001, 22:32
 * Known SubClasses: AEnumerator
 *
 * @author  Matthias Heuer
 * @version
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:31Z
 * digest: f00125257fb7f4a6bda541396cdffd5dfcd6b23f526ff215ae0bf9a06b7c18d7
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FilterIn
extends AFilterIn {
	
	/** Creates new FilterStreamIn delegating to the given Stream */
	public FilterIn (final IIStreamIn enum_) { super(enum_); }
	
	/** Creates new FilterStreamIn for late Initialization */
	//private FilterIn () { }
	
	/** this is the abstract Template Method 	*/
	protected Object nextItemInternal() { return in.nextItem(); }
	
}
