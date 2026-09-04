package streamIO.object.enumer.container;



/**
 * Interface for a Container with Elements organized in a linked List.
 * The Iterators return the Objects in exactly the same Order.
 * The previous and next Element is always defined
 * and independent of the Object's Contents or State.
 * An Exception to this are sorted Lists, where the Position of the Object might change
 * due to adding a new Object.
 * @stereotype container 
 */
public interface ListContainer 
extends Container {

    /**
      * The Name is synchronized with the Interface FixIndexed
      * @param Object to be searched in this Container
      * @return The Index of the Object in this Container, -1 if not found
      * @see function.ICountAble which allows to retrieve the Index directly from the Object 
	  */
    int getIndex(final Object Item);

    /**
      * Since a Container has a limited Number of Elements, this is well defined. 
	  * @param Object to be searched in this Container
	  * @return The last Index of an equal() Object in this Container, -1 if not found
	  */
	int getLastIndex(final Object Item);

}
