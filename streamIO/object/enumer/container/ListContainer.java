package streamIO.object.enumer.container;



/**
 * Interface for a Container with Elements organized in a linked List.
 * The Iterators return the Objects in exactly the same Order.
 * The previous and next Element is always defined
 * and independent of the Object's Contents or State.
 * An Exception to this are sorted Lists, where the Position of the Object might change
 * due to adding a new Object.
 * @stereotype container 
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 243239c73de968650d4c0f84bf275912aa3299bf4ab2c1752046db0bdf464846
 * stale: false
 * -->
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
