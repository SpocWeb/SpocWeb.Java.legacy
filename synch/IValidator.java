package synch;

/**
  * Title: IValidator<p>
  * Description:
  * Defines the Interface for a Subscriber that can veto the Change
  * by throwing an Exception.
  * The Advantage of an Exception is
  * that it cannot be ignored, neither at Design Time nor at Runtime.
  *
  * The Event Source is transferred to release the Necessity
  * of maintaining two way navigational References,
  * as long as the Source is only needed in the case of an Event!
  * (temporary Binding!)
  * Additionally the Source could be used to do pluggable Multifield Validations!
  *
  * The Interface differs from @see Subscriber
  * so the same Object can be both an IValidator and a Subscriber.
  *
  * Separating Validation from Publication is very important,
  * because it gives validating Subscribers the Chance to distinguish between both
  * and it saves the hassle to possibly undo partly performed Publication!
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Known Uses:
  *
  * related Interfaces: 
  * @see synch.IValidationRule which defines the Interface for a Single Field Validation Method
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-02-2002, 06:12 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IValidator {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** This is be the Interface for a Validator that can veto the Change	 */
	public void validate(Object Source, Object Value, Object oldVal) throws InvalidException;

}

