package streamIO.object.enumer;

import streamIO.IIterAble;

/**
 * ChangeAble.java
 * This Interface is implemented by Containers 
 * supporting the replaceItem() Method
 * and / or a ModStreamIn Iterator 
 * to indicate a Change or Addition in the contained Elements. 
 *
 * Created on 28. Januar 2001, 13:26
 *
 * @author  Matthias Heuer
 * @version
 */
public interface IChangeAble
extends IIterAble {

	/** Returns a new Input streamIO of the Objects in this Container
	  * in exactly the same State as this one.
	  * which allows for changing the Data concurrently. */
	ChangeIterator ChangeIterator();
	
	/** Returns the current minor Version of the Container to support fast-fail Enumerators
	 * Should be incremented on each change of the Container's Content 
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Enumerator.
	 * Using int should be relatively safe,
	 * because Containers will at most contain about |int| Elements.
	 * Calling this Method additionally to nextItem() is quite expensive,
	 * so the Enumerator should try to access the Field directly.
	 */
	public int getMinor();
	
	/**Increments and returns the current Version of the Container
	 * to indicate Modification to fast-fail Iterators.
	 * The Version should be incremented on each change of the Container
	 * and checked for the same Value on each Call of nextItem() or currItem()
	 * to warn the User (Client) of the Iterator.
	 * Using int should be large enough,
	 * because Containers will at most contain about |int| Elements.
	 */
	public int incMinor();

}
