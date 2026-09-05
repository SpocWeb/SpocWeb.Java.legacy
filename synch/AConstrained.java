package synch;

/**
  * Title: AConstrained<p>
  * Description:
  * Abstract base class that combines the single-subscriber publish/subscribe
  * mechanism of {@link UniCastConstrained} with the {@link IValidator} and
  * {@link ISubscriber} roles, so a subclass can both veto and react to a
  * Value change through the same Object.
  *
  * Known SubClasses:
  * @see synch.aspect.Aspect
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-01-2002, 10:51 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:20Z
  * digest: 48c17a4495dc84c18992e882f1097c178f516a480ae5943d170f8cae692dc4b2
  * stale: false
  * tags: [code/observer_pattern, code/validation]
  * concepts: [Constrained Publisher Base]
  * facets: {layer: domain, status: legacy, complexity: medium}
  * -->
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
