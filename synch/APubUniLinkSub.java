package synch;

import graphs.ILinkAble;
import graphs.ILinked;

/**
  * Title: APubUniLinkSub<p>
  * Description:
  * Allows to chain Subscribers!
  * Can subscribe to a single Publisher only.
  * Can automatically enchain multiple Subscribers. 
  * Purpose:
  * Linkable Subscriber Type
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * To alleviate the Change from a UniCast to a MultiCast,
  * another Method is added to the Subscriber Interface
  * that allows to chain Subscribers of this Type.
  * Chaining is done in a Queue like Manner
  * (i.e. new Items are added to the Start).
  *
  * A huge Drawback is that such a LinkedSubscriber can only be used
  * for a single Publisher!!!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-19-2002, 03:24 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class APubUniLinkSub
implements IPublisher, ISubscriber, ILinkAble {

/** Reference to the next Subscriber */
protected APubUniLinkSub subscriber;

/** Accessor Method:
  * @param sets the Parent of this ILinkAble */
public void setPrnt(ILinked parent) {
	subscriber = (APubUniLinkSub) parent; }

/** Accessor Method:
  * @return the final Parent == Root
  * getRoot().getKey() is equivalent to StreamIn.lastItem()
  * and is used to handle disjoint Sets
  * It can be implemented using iterated getParent() Methods,
  * but the Reason to make this Method virtual is that there are different Implementations
  * depending on the Strategy.  */
public ILinked getRoot() {
	if (subscriber == null) {
		return this; }
	return subscriber.getRoot(); }

/** Accessor Method:
  * @return the Parent of this ILinked */
public ILinked getPrnt() { return subscriber; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Publisher: Implementation
////////////////////////////////////////////////////////////////////////////


	/**Adds a Subscriber to this Publisher.
	 * For LinkedSubscriber these are chained
	 */
	public void addSubscriber(ISubscriber arg) {
		((APubUniLinkSub)arg).setPrnt((ILinked) subscriber);
		subscriber = (APubUniLinkSub) arg; }

	/**Removes the Subscriber from this Publisher
	 * @return false if this Subscriber was not subscribed at all.	 */
	public ISubscriber removeSubscriber(ISubscriber arg) {
		if (subscriber == null) {
			return null; }
		if ((subscriber   ==   arg) ||
			(subscriber.equals(arg))){ //never remove the Multicaster!
			subscriber =  (APubUniLinkSub) ((ILinked) subscriber).getPrnt();
			return arg; }
		return ((APubUniLinkSub) subscriber).removeSubscriber(arg); }

	/** Returns false if this Subscriber was not subscribed at all.	 */
	public boolean isSubscriber(ISubscriber arg)	{
		if (subscriber == null) {
			return false; }
		if ((subscriber   ==   arg) ||
			(subscriber.equals(arg))){ //never remove the Multicaster!
			return true; }
		return ((APubUniLinkSub) subscriber).isSubscriber(arg); }

	/** Returns the Number of Subscribers of this Publiser	 */
	public int countSubscribers() {
		if (subscriber == null) {
			return 0; }
		return 1 + ((APubUniLinkSub) subscriber).countSubscribers(); }

/**
 * Callback used to update all Subscribers
 * Actually the Source could be a Publisher or any other Object,
 * so it cannot be declared as Publisher.
 * @param Source the Object whose Value is changed
 * @param Value  the new Value
 * @param oldVal the old Value, optional can be null
 */
public void update(Object Source, Object Value, Object oldVal) {
	myUpdate(Source, Value, oldVal);
	subscriber.update(Source, Value, oldVal); }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface LinkedSubscriber: abstract Methods
////////////////////////////////////////////////////////////////////////////

/**
 * Callback used to update all Subscribers
 * Actually the Source could be a Publisher or any other Object,
 * so it cannot be declared as Publisher.
 * @param Source the Object whose Value is changed
 * @param Value  the new Value
 * @param oldVal the old Value, optional can be null
 */
protected abstract void myUpdate(Object Source, Object Value, Object oldVal);

}

