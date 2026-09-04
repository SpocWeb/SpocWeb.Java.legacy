package synch;

/**
  * Title: IValidation<p>
  * Description:
  * Defines the Interface for Validators
  * to be parameterized by ValidationRule Objects.
  *
  * The Method Name can be varied, but the Signature has to stay the same.
  * By varying the Name several Validation Methods can be put into the same Class.
  * Therefore this Interface is not used. 
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-15-2002, 12:51 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IValidation {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Validating Method:
	 * @param arg Parameter for the Validation
	 * @param value the Value to be validated...
	 */
	void validate(Object arg, String value) throws InvalidException;

}

