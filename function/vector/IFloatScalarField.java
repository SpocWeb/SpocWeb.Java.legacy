package function.vector;

/**
  * Title: IFloatScalarField<p>
  * Description:
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
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-16-2002, 08:32 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IFloatScalarField {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Maps the Vector to a Scalar Value 	 */
	double Map(double[] v);

	/** Maps the Vector to a Scalar Value 	 */
	float Map(float[] v);

}

