package streamIO.object.enumer.container;

/** Interface defining a Callback Function of an Observer for a Container
  * This can be used for active Notification of Changes,
  * instead of passive ones via Minor and Major. 
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: 9482127b2292c0e714a49d2deb449e27cbd3acf12adae4c9de1edd44452b6c25
  * stale: false
  * -->
  */
public interface ChangeObserver {

	/** Callback Function of an Observer for a Container
	  * @param Source, the Event Source that changed.
	  * @param Change, an Object describing the Change
	  * @return true when the Event should be further propagated... */
	public boolean changed(Object Source, Object Change); 

}
