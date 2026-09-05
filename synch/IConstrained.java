package synch; //

/**
  * Title: Constrained<p>
  * AKA: Subject in the Observer Pattern
  * Description:
  * Defines the Interface for Constrained Objects of Information / State
  * and to maintain a (List of) Observer(s).
  * This Interface is very similar to the Publisher Interface,
  * because it is used for the same Purpose (adding and removing Validators)
  * but needs a separate Method for this!
  *
  * Separating Validation from Publication is very important,
  * because it gives validating Subscribers the Chance to distinguish between both
  * and it saves the hassle to possibly undo partly performed Publication!
  *
  * The two Tasks of the Constrained can be separated:
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
  * mtime: 2026-09-05T10:42:49Z
  * digest: 8bf2a329abfe8bb2a0b41233a9b2968ecf256fe7de59970790b06b3932a8b36d
  * stale: false
  * tags: [code/observer_pattern, code/validation]
  * concepts: [Constrained Publisher Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface IConstrained {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds the given Validator to the List of Validators	  */
	void addValidator(IValidator observer) throws TooManySubscribersException;

	/** removes the given Validator from the List of Validators	  */
	IValidator removeValidator(IValidator observer);

	/** Checks whether the given Validator is currently registered.
	  * @return true if the given Validator is currently registered.	 */
	public boolean isValidator(IValidator arg);

	/** Reports how many Validators are currently registered.
	  * @return the current Number of registered Validators	 */
	public int countValidators();

	/** Notification can be triggered externally
	  * to prevent premature Updates before the Subject is in a consistent State.
	  */
//	void notifyObservers();

	/** @return the State of the Subject
	  */
//	Memento getState();

}
