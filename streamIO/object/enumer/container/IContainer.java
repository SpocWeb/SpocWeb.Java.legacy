package streamIO.object.enumer.container;

/**
  * Title: IContainer.java<p>
  * Description:
  * Defines the minimum Interface for Containers
  * Registries are Relations and thus special Containers for Associations.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-13-2002, 09:10 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: c0a1f94c3b6946c03039901c915133442725ec37b34cd9818a7d66094a6b2b5e
  * stale: false
  * -->
  */
public interface IContainer
extends streamIO.IIStreamOut
{

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
//  The Triad add/remove/contains would corresponds roughly to
//  the Triad set/get/is when get() would remove the Value, which it doesn't!!!
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** @return the Object when it is contained in this Container
	  * This is the same Operation as StreamIn.findFirst()
	  * @see Sub() and SubEq() for the according Container Methods,
	  * The Name containsItem() is only to be used for single Elements
	  */
	//Object containsItem(Object Item); //

	/** @return true when this Object is contained in this Container
	  * This is the same Operation as (findFirst() != EOI) || (available() >= 0)
	  * and is defined in streamIO.Object.StreamIn
	  * only here it does not refer to a streamIO, but a Container.
	  * It is left out of this Interface, because rarely used
	  * and would mix in a Trace of StreamIn with all it's other Methods!
	  */
	//boolean contains(Object Item); //already defined in StreamIn or Pipe

	/** Removes this Item from the Container
	  * Corresponds to subAt(), but retained, because it also returns Information
	  * whether the Container was changed
	  * @return the Item, if found, otherwise 'null' resp 'EOI'	 */
	Object removeItem(Object Item) throws streamIO.object.ModificationException;

}
