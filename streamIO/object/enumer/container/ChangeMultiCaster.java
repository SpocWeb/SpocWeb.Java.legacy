package streamIO.object.enumer.container;

import java.security.InvalidParameterException;

import streamIO.IIStreamIn;

/** This Class propagates Change Events to many Listeners
  * The Propagation can be stopped by any Listener by returning 'false'
  * or by throwing an Exception.
  * This is necessary to allow e.g. concurrent Modification of Elements in a 'Model'.
  *
  * Typically this Multicaster is just plugged after a ChangeEventSource
  * (that's why it is also an Observer)
  * and distributes the Events sequentially.
  * An alternative Implementation can use a linked List of Observers,
  * but that would mean mixing List Maintenance with Multicasting!
  * Using a HashTable enables rapid Removal of Observers
  * without having to loop over (possibly) all Observers.
  *
  * This is a similar, but different Behavior to the
  * @see streamIO.MultiplexerOut,
  * which calls only a single Element of the List in Round Robin Fashion.
  *
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class ChangeMultiCaster
implements ChangeObserver, ChangeEventSource {

	/** Set of Listeners for the Changes, could be any Container,
	  * the only Criterion is the Speed at which new Elements are removed or added
	  * and the Frequency of these Changes.
	  * Need a Hash, because specific Listeners have to be removed
	  * For add only Multicasters an ArrayList would be faster!  */
	protected HashContainer Listeners = new HashContainer();

	/** adds or removes the given Listener to this MultiCaster	*/
	public ChangeEventSource addChangeListener(ChangeObserver Listener, boolean add) {
		if (Listener == null) throw new InvalidParameterException(); //fail fast!
//		try {
			if (add) Listeners. unionItem(Listener); //use a weak Reference
			else     Listeners.removeItem(Listener); //to avoid bidirectional Reference
//		} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
		return this; } //and achieve a better Garbage Collection.

	/** Callback Function of an Observer for a Container
	  * @param Source, the Event Source that changed.
	  * @param Change, an Object describing the Change
	  * @return true when the Event should be further propagated... */
	public boolean changed(Object Source, Object Change) {
		ChangeObserver Listener;
		IIStreamIn Enum = Listeners.Iterator();
		while (IIStreamIn.EOI != (Listener = (ChangeObserver) Enum.nextItem())) { //no nulls!
			if (!Listener.changed(Source, Change)) {
				return false; }
		} return true; }

}
