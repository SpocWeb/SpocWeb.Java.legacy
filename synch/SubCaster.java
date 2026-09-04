package synch;

/**
 * This is a very low Overhead Class for Publish/Subscribe Mechanisms.
 * It can hold a single Subscriber.
 * The Methods add/removeSubscriber() are actually already overdimensioned
 * and only used in preparation of the MultiCaster.
 * Normally a setSubscriber() Method would be sufficient to both set
 * and unset the Subscriber (using null)
 *
 * Left open to subclassing is the "Value" and it's Type.
 * I could make it a generic "Object", which would probably make it easiest,
 * because I could use the same for Propagation
 * and leave it to the concrete subclass whether to use the Reference or a clone.
 *
 * A huge Drawback is that such a LinkedSubscriber can only be used
 * for a single Publisher!!!
 *
 */
public abstract class SubCaster
extends UniCaster
implements ISubscriber {

	/**This Method is responsible for copying the given Value
	 * into the local Value of this Property.
	 * This is used e.g. on receiving an Update from a Publisher.
	 * All the Rest of the Publication Mechanism is handled automatically!
	 */
	protected abstract void copyAt(Object Value);// {}

	/**Callback used to update all Subscribers
	 * The Return Value is a Boolean to stop Notification, if true
	 * Actually the Source could be a Publisher or any other Object,
	 * so it cannot be declared as Publisher. 	 */
	public void update(Object Source, Object Value, Object oldVal) {
		copyAt(Value); }

}
