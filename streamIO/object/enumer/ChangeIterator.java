package streamIO.object.enumer;

import streamIO.exception.ReadOnlyException;
import streamIO.object.IStreamIn;
import streamIO.object.ModificationException;

/**
  * Title: ModStreamIn.java<p>
  * Description:
  * Defines the Interface for a streamIO that can be modified in it's Contents,
  * but not structurally. Therefore there are two Versions: Minor and Major
  * Minor counts the Modification in Contents  whereas
  * Major counts the Modification in structure
  * Iterators can, based on this, decide, whether to abort or to restart.
  * Modified Data will not be picked up by Iterators which have passed this Index.
  * If this is a Container with several Iterators, these may choose to inspect
  * the Version of this streamIO.
  * Optionally a Container can subscribe to all it's Element's StateChange Events
  * and update Minor self-reliantly.
  * This is used for Containers that don't change their structure,
  * like e.g. Arrays and Manifolds.
  *
  * Known SubInterfaces: Pipe
  *
  * Known Implementors: AModStreamIn
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-05, 09;28;57<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @stereotype enumeration
  * <!-- docstate
  * tags: [code/enumerator, code/iterator_adapter]
  * concepts: [Custom Streaming Enumerator and Iterator Bridge Layer for Object Collections]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface ChangeIterator
extends IStreamIn, IChangeAble {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** Minor counts the Modification in Contents, whereas
  * Major counts the Modification in structure	 */
//public int getMinor();

/** Minor counts the Modification in Contents, whereas
  * Major counts the Modification in structure	 */
//public int incMinor();

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

/** Replaces the current Object in the Container with the given Item.
  * One Problem is other Enumerators that concurrently work through this Container.
  * Another Problem is that removing the Item may not be possible at all.
  * In this Case the Exception is thrown.
  * That is why this Method should throw an Exception if replacing is not allowed.
  * It should also update the Minor Version (or let the Container update it)
  * to announce the Change to other Iterators.
  * @param  The Item to replace the current Object (returned by the latest nextItem())
  * @return the current Object replaced by the Item
  * @throws ReadOnlyException when the Container is read only
  * @throws ModificationException when the Container is sorted.
  */
Object replaceCurr(Object Item); // throws ModificationException;

}
