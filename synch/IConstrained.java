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
  */
public interface IConstrained {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds the given Validator to the List of Validators	  */
	void addValidator(IValidator observer) throws TooManySubscribersException;

	/** removes the given Validator from the List of Validators	  */
	IValidator removeValidator(IValidator observer);

	/** @return false if this Validator was not subscribed at all.	 */
	public boolean isValidator(IValidator arg);

	/** @return the Number of Validators of this Publisher	 */
	public int countValidators();

	/** Notification can be triggered externally
	  * to prevent premature Updates before the Subject is in a consistent State.
	  */
//	void notifyObservers();

	/** @return the State of the Subject
	  */
//	Memento getState();

}
