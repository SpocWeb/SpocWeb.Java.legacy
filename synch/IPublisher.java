package synch;

/**Interface for a Publisher
 * There are two Models in this Interaction:
 * The Publisher notifies the Subscribers automatically
 * or it carries a Dirty Flag that can be queried by the Subscribers,
 * but not cleared by any single Subscriber.
 * This Flag should be set while the Observers are being notified
 * to indicate the incomplete Status of the Notification.
 * This is not necessary in a single threaded Environment.
 * The Methods add/removeSubscriber() are actually already overdimensioned
 * and only used in preparation of the MultiCaster.
 * Normally a get/setSubscriber() Method or public Subscriber Property
 * would be sufficient to query, set and unset the Subscriber (using null).
 *
 * The Publisher is implemented in the UniCaster and MultiCaster classes
 * where MultiCasting just deals with handling the Set of Subscribers
 * and UniCaster is a Class that throws Events but has no Properties yet.
 *
 * Separating Validation from Publication is very important,
 * because it gives validating Subscribers the Chance to distinguish between both
 * and it saves the hassle to possibly undo partly performed Publication!
 */
public interface IPublisher {

	/**Adds a Subscriber to this Publisher.
	 * For UniCaster there can be only one, so an Exception is thrown,
	 * because it cannot be ignored!
	 * Instead of adding Subscribers to a Container
	 * a linked List can be created!
	 * But that would couple the Subscribers!
	 * Additionally finding and removing Subscribers is slow then.
	 */
	public void addSubscriber(ISubscriber arg) throws TooManySubscribersException;

	/**Removes the Subscriber from this Publisher
	 * @return false if this Subscriber was not subscribed at all.
	 */
	public ISubscriber removeSubscriber(ISubscriber arg);

	/** @return false if this Subscriber was not subscribed at all.	 */
	public boolean isSubscriber(ISubscriber arg);

	/** @return the Number of Subscribers of this Publisher	 */
	public int countSubscribers();

	/**Notifies the Subscribers of thie Value
	 * @return true, if no Subscriber has cancelled Notification.	 */
//	public void notifySubscribers(Object Value, Object oldVal);

	/**Notifies the Subscribers of thie Value
	 * @return true, if no Subscriber has cancelled Notification.	 */
//	public boolean notifySubscribers(Object Value);

}
