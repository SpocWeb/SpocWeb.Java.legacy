package structure; //

/**
  * Declares the push-Model {@link #update} Callback a {@link Publisher} invokes on every
  * registered Observer, carrying both the Publisher and its new State.
  *
  * Defines the Interface for ...TODO: Describes the Purpose / Responsibilities
  * of this Interface, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  * All interface Operations are implicitly public and abstract.
  * All interface Attributes are implicitly public, final and static.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-22-2002, 08:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:17:50Z
  * digest: 4934c00d44e911a4b722756ba9061fde3c8fdd440adeac0a195763cf9c99a42d
  * stale: false
  * tags: [code/observer_pattern]
  * concepts: [Observer Pattern Subscriber]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface Subscriber
{

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Update Method called by the Publisher.
	  * Multicast synchronous Callback, so the Originator is sent too.
	  * @param sender By sending the Publisher with the Command,
	  * the Subscriber can discern between different Publishers and additionaly
	  * the Lifetime of the Publisher is not bound to the Subscriber
	  * since the Reference is valid only during the Method Call!
	  * Publishers can be destroyed at any Time and don't need to live longer
	  * or as long as their Subscribers.
	  * @param newState By sending the new State the push Model is implemente.
	  * The pull Model requires more Methods on the Publisher side.  	 */
	void update(Publisher sender, Object newState);

}
