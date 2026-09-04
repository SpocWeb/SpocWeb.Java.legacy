package streamIO.object.enumer.container;

/** Interface defining a Callback Function of an Observer for a Container
  * This can be used for active Notification of Changes,
  * instead of passive ones via Minor and Major. 
  */
public interface ChangeObserver {

	/** Callback Function of an Observer for a Container
	  * @param Source, the Event Source that changed.
	  * @param Change, an Object describing the Change
	  * @return true when the Event should be further propagated... */
	public boolean changed(Object Source, Object Change); 

}
