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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: 4be99b8d89ed6e05c11f124739cbe1c63bc7fcbf50b5eb6486cd85ffb0a3b1c8
  * stale: false
  * tags: [code/validation_rule]
  * concepts: [Validation Rule Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface IValidationRule {

	/** Validates the given Object */
	public void validate(Object arg) throws InvalidException;

}

