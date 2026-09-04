package synch;

/**
  * Title: IValidationRule<p>
  * Description:
  * Defines the Interface for a Validation Rule
  * that validates the given Object and throws a ValidationException
  * when the Value was not valid.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  * @see ValidationRule
  * @see ValidationRuleList
  * @see PathValidationRule
  *
  * related Interfaces: 
  * @see synch.IValidator which defines the Interface for a Validation Method 
  * that takes the Origin and the old Value too!
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	11-27-2002, 11:41 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IValidationRule {

	/** Validates the given Object */
	public void validate(Object arg) throws InvalidException;

}

