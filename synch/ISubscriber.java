package synch;

/**This is the Interface for a Subscriber.
 * The Event Source is transferred to release the Necessity
 * of maintaining two way navigational References,
 * as long as the Source is only needed in the case of an Event.
 * (temporary Binding!)
 *
 * Named Subscription like with the Model requires a different Interface,
 * since the Name should be propagated separately, not implicitly.
 *
 * Separating Validation from Publication is very important,
 * because it gives validating Subscribers the Chance to distinguish between both
 * and it saves the hassle to possibly undo partly performed Publication!
 *
 * Design Decisions:
 * The Return Value was a Boolean to stop Notification / veto the Change, if true,
 * but that was changed due to the facts that:
 * * an Exception (although it is slower) cannot be ignored neither at Design Time, nor at Runtime
 * * if normal Subscriber and the VetoAble Subscriber would use the same Interface,
 *   another Parameter would be necessary to indicate when the Veto is valid
 *   and you could not easily use the same Object for validate and update,
 *   which is the usual Case!
 *
 * @see java.beans.ChangeListener and
 * @see java.beans.VetoableChangeListener
 */
public interface ISubscriber {

	/**
	 * Callback used to update all Subscribers
	 * Actually the Source could be a Publisher or any other Object,
	 * so it cannot be declared as Publisher.
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	void    update(Object Source, Object Value, Object oldVal);
//	void    update(Object Source, Object Value, Object oldVal, String PropName); //for primitive Values that don't have Identity the PropName has to be transmitted too
//	boolean update(Object Source, Object Value); //previous Version!

}
