package synch;

/**
  * Title: AConstrained<p>
  * Description:
  * Purpose:
  * Abstract Base Class for validating and subscribing Publishers
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Extends UniCastConstrained  because ...
  * Implements Interface IValidator because ...
  * Consists of Members ...
  * Uses Class for ...
  *
  * Known SubClasses:
  * @see synch.aspect.Aspect
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-01-2002, 10:51 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class AConstrained
	extends UniCastConstrained
	implements IValidator, ISubscriber {

	/**
	 * Callback used to update all Subscribers
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	public abstract void    update(Object Source, Object Value, Object oldVal);

	/** This is be the Interface for a Subscriber that can veto the Change	 */
	public abstract void validate(Object Source, Object Value, Object oldVal) throws InvalidException;

}
