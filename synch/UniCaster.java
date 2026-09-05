package synch;

/**This is a very low Overhead Class for Publish/Subscribe Mechanisms
 * it can hold a single Subscriber.
 * The Methods add/removeSubscriber() are actually already overdimensioned
 * and only used in preparation of the MultiCaster.
 * Normally a setSubscriber() Method would be sufficient to both set
 * and unset the Subscriber (using null)
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: f911df095d6cb9c5f15a0ffc33d13ac369768f067b13c63f69d82dcb5d751a04
 * stale: false
 * tags: [code/publish_subscribe, code/observer_pattern]
 * concepts: [Single-Subscriber Publisher]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class UniCaster
	implements IPublisher {

////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////

	/** Returns false if this Subscriber was not subscribed at all.	 */
	final static public boolean IS_SUBSCRIBER(ISubscriber subscriber, ISubscriber arg)	{
		if (subscriber instanceof MultiCaster) {
			return ((MultiCaster) subscriber).isSubscriber(arg); }
		return (subscriber == arg); }

	/** Returns the Number of Subscribers of this Publiser	 */
	final static public int NUM_SUBSCRIBERS(ISubscriber subscriber) {
		if (subscriber instanceof MultiCaster) {
			return ((MultiCaster) subscriber).countSubscribers(); }
		return (subscriber == null) ? 0 : 1; }

	/**Checks if the given Subscriber is already set
	 * and if yes, it is replaced by a MultiCaster.
	 * arg to this Publisher.
	 * For UniCaster there can be only one, so an Exception is thrown.
	 * Difficult Decision to use an Exception or a boolean Return Value.
	 * For Consistency I use an Exception
	 * This Class transparently creates a MultiCaster if necessary.
	 */
	final static public ISubscriber ADD_SUBSCRIBER(ISubscriber subscriber, ISubscriber arg)
		throws TooManySubscribersException	{
		if  (subscriber == null) { //throw new TooManySubscribersException();
			 return arg; }
		MultiCaster ret;
		if  (subscriber instanceof MultiCaster) {
			ret = (MultiCaster) subscriber;
		} else {
			ret = new MultiCaster();
			ret.addSubscriber(subscriber); //add the previous Subscriber!
		}
		ret.addSubscriber(arg); //add the new Subscriber
		return ret; }

	/**Removes the Subscriber from this Publisher
	 * @return subscriber if it is a MultiCaster, null otherwise
	 */
	final static public ISubscriber REMOVE_SUBSCRIBER(ISubscriber subscriber, ISubscriber arg)	{
		if (subscriber == arg) { //quick Tests to the Beginning!
			subscriber =  null;
			return arg; }
		if (subscriber instanceof MultiCaster) { //never remove the Multicaster!
			return ((MultiCaster) subscriber).removeSubscriber(arg); }
		return null; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////

	/**The single UniCast Subscriber,
	 * automatically extended to a MultiCaster	 */
	protected ISubscriber subscriber;

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IPublisher: Implementation
////////////////////////////////////////////////////////////////////////////

	/**Adds a Subscriber to this Publisher.
	 * For UniCaster there can be only one, so an Exception is thrown.
	 * Difficult Decision to use an Exception or a boolean Return Value.
	 * For Consistency I use an Exception
	 * This Class transparently creates a MultiCaster if necessary.	 */
	public void addSubscriber(ISubscriber arg)
		throws TooManySubscribersException	{
			subscriber = ADD_SUBSCRIBER(subscriber, arg); }

	/**Removes the Subscriber from this Publisher
	 * @return false if this Subscriber was not subscribed at all.	 */
	public ISubscriber removeSubscriber(ISubscriber arg)	{
		if (subscriber == arg) {
			subscriber =  null;
			return arg; }
		if (subscriber instanceof MultiCaster) { //never remove the Multicaster!
			return ((MultiCaster) subscriber).removeSubscriber(arg); }
		return null; }

	/** Returns false if this Subscriber was not subscribed at all.	 */
	public boolean isSubscriber(ISubscriber arg)	{
		return IS_SUBSCRIBER(subscriber, arg); }

	/** Returns the Number of Subscribers of this Publiser	 */
	public int countSubscribers() {
		return NUM_SUBSCRIBERS(subscriber); }

	/** Notifies the Subscribers of thie Value and returns their Answer	 */
	protected void notifySubscribers(Object Value, Object oldVal) {
		subscriber.update(this, Value, oldVal); }

	/** Notifies the Subscribers of thie Value and returns their Answer	 */
//	public boolean notifySubscribers(Object Value) {
//		return subscriber.update(this, Value); }

}
