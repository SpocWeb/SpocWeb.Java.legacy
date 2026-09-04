package structure; //

/**
  * Title: RefCounter<p>
  * Description:
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
