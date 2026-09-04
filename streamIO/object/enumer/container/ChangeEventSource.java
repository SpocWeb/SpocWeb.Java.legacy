package streamIO.object.enumer.container;

/** Interface to allow adding and removing typesafe Listeners
  * to / from a ChangeEvent Source.
  * 'Adding only' Operation enables a more efficient Storage Strategy
  * because you can use an ArrayList or linked List instead of a Hash.	*/
public interface ChangeEventSource {

	/** adds or removes the given Listener to this ChangeEventSource	*/
	public ChangeEventSource addChangeListener(ChangeObserver Listener, boolean add);

}
