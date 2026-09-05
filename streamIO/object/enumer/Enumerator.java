package streamIO.object.enumer;

import streamIO.exception.ReadOnlyException;
import streamIO.object.IPipe;
import streamIO.object.ModificationException;

/** Interface for an Enumerator through a Collection or on a streamIO.
  * This defines Read and Write Operations and (different from Pipe) also Positions
  * ReverseEnumerator defines these Operations also for the current and previous Item.
  *
  * The writing Partner of this reading Enumerator Interface is 'SemiGroup'
  * This introduces add() very early, but the writing Interface IS very small.
  *
  * Design Decisions:
  * The Enumerator Object is singled out from the Container Classes,
  * because it is very probable, that you have several Enumerators
  * running over the same Object at the same time!
  * Therefore the Interface is renamed from 'iterAble' to 'Enumerator'.
  * A Container is 'iterAble', because it can create Enumerators on itself.
  *
  * The Name unfortunately conflicts with the new Java 1.2 Collection FrameWork,
  * but I chose not to rename it to "StreamIn" or "Enum".
  *
  * The Enumerator has been designed to look more like a streamIO.
  * This allows for Streams and Enumerators to be treated the same.
  * Especially the following changes have been made:
  * 'boolean hasMoreItems()' has been redefined to 'int available()'
  * 'reStart()' has been redefined to 'reset()'
  * 'close()', 'skip()', 'mark()' and 'markSupported()' are new.
  * nextItem() now returns EOI and SOI (null) and doesn't take the ByRefLong Parameter
  * any more, which is usually not necessary and only increases Coupling.
  * skip(), mark() and reset() are optionally supported.
  *
  * Could also throw Exceptions in Operations like nextItem(), prevItem() etc.
  * but that is much more expensive (> 10*) than checking for EOI or SOI!
  * This is only done when the Operation is generally not supported
  * and not just because it is in the wrong State!
  *
  * Since some Containers can contain 'null's,
  * the End is marked by both returning the Object EOI (End Of Enumerator, 'null')
  * AND by available() <= 0.
  * @stereotype enumeration
  * <!-- docstate
  * tags: [code/enumerator, code/iterator_adapter]
  * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface Enumerator
extends ChangeIterator, IPipe, IAlterAble {

	// modifying Operations, dependent on the Sequence

    /**Replaces the next Object from the streamIO with this Item.
     * It should also update the Minor Version (or let the Container update it)
     * to announce the Change to other Iterators.
	 * This Operation can be used to e.g. influence Parsers concurrently.
	 * @param Object to replace the next Item in the Container
	 * @return the next Item in the Container
	 * @throws ModificationException when the Container is sorted
	 * @throws ReadOnlyException when the Container is read only
	 */
	public Object replaceNext(Object Item);// throws ModificationException;

	/** Adds the given Item after the current Object to the Container.
	  * Returns the Container to allow for concatenated adding.
	  * One Problem is other Enumerators that concurrently work through this Container.
	  * Another Problem is that removing the Item may not be possible at all.
	  * In this Case the Exception is thrown.
	  * That is why this Method should throw an Exception if removing is not allowed.
	  * Could also return a boolean whether the Method is supported or not
	  * @param Object to be added at the next Position
	  * @return the Enumerator to allow for concatenated Adding
	  * @throws ModificationException when the Container is sorted
	  * @throws ReadOnlyException when the Container is read only
	  */
	public Enumerator addNext(Object Item) throws ModificationException;

    /** removes the current Item (returned by the latest nextItem())
      * @return the current Item
	  * @throws ReadOnlyException when the Container is read only
	  * @throws ModificationException when the Container doesn't allow restructuring. 
	  */
	public Object removeCurr() throws ModificationException;

	/** Removes the next Object from the Set and Iteration,
	  * returns the removed Item,
	  * this makes it necessary to define it separately,
	  * because it returns more Information: whether the Item was found or not!
	  * @throws ModificationException when the Container is read only
	  */
	public Object removeNext() throws ModificationException;

}
