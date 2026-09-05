package structure; //

/**
  * Declares the increment/decrement Contract for tracking whether a valid Reference to an
  * Object remains, used by {@link BiRef}.
  *
  * Defines the Interface for Reference Counting.
  * Reference Counting simplifies keeping track
  * whether there is a valid Reference to this Object left.
  * Since the actual References don't matter, this scalar calculated Value is sufficient.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-21-2002, 08:47 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see BiRef which uses this Interface when setting References.
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:15Z
  * digest: fee2a848e30baf752a0c151789dcb11cce71290aa49ad2f26a25d1faafdc633e
  * stale: false
  * tags: [code/reference_counting]
  * concepts: [Reference Counter]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface RefCounter {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** increases the Reference Counter 	 */
	void incRef();

	/** decreases the Reference Counter 	 */
	void decRef();

}
