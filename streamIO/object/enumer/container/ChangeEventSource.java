package streamIO.object.enumer.container;

/** Interface to allow adding and removing typesafe Listeners
  * to / from a ChangeEvent Source.
  * 'Adding only' Operation enables a more efficient Storage Strategy
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  * because you can use an ArrayList or linked List instead of a Hash.	*/
public interface ChangeEventSource {

	/** adds or removes the given Listener to this ChangeEventSource	*/
	public ChangeEventSource addChangeListener(ChangeObserver Listener, boolean add);

}
