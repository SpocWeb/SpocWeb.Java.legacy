package streamIO.object.enumer.container;

/** Interface to allow adding and removing typesafe Listeners
  * to / from a ChangeEvent Source.
  * 'Adding only' Operation enables a more efficient Storage Strategy
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: 4351e52a6e2dc6a3e458409b28f5e2c412205d32ac784d045c1707db54a154c8
  * stale: false
  * -->
  * because you can use an ArrayList or linked List instead of a Hash.	*/
public interface ChangeEventSource {

	/** adds or removes the given Listener to this ChangeEventSource	*/
	public ChangeEventSource addChangeListener(ChangeObserver Listener, boolean add);

}
