package structure; //

/**
  * Declares the Observer-registration Contract of a Subject in the Observer Pattern, leaving
  * State-carrying and Notification to the implementing Class.
  *
  * AKA: Subject in the Observer Pattern
  *
  * Defines the Interface for Publishers of Information / State
  * and to maintain a (List of) Observer(s).
  *
  * The two Tasks of the Publisher can be separated:
  * -carrying the State
  * -maintaining and notifying the Observers can be delegated
  *  to a local MultiCaster or a global single ChangeManager .
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
  * mtime: 2026-09-05T11:17:13Z
  * digest: a0e315587e892f7e155237467fcc0f4bf97cd19cb40cf38719129243a6314bc3
  * stale: false
  * tags: [code/observer_pattern]
  * concepts: [Observer Pattern Publisher]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface Publisher {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds the given Subscriber to the List of Observers
	  * @param Aspect optionally the Aspect this Subscriber is interested in,
	  * can be specified. 	 */
	void addObserver(Subscriber observer);

	/** removes the given Subscriber from the List of Observers
	  * @param Aspect optionally the Aspect this Subscriber was interested in,
	  * can be specified. 	 */
	void removeObserver(Subscriber observer);

	/** Notification can be triggered externally
	  * to prevent premature Updates before the Subject is in a consistent State.
	  */
//	void notifyObservers();

	/** @return the State of the Subject
	  */
//	Memento getState();

}
