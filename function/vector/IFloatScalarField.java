package function.vector;

/**
  * Defines a scalar field: a function mapping a position vector to a single {@code double} or
  * {@code float} value.
  *
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:46:47Z
  * digest: 933de58d30b33b9e34921f10ac3a359fc3aaa70dfe6f05002bd272bb814b39a9
  * stale: false
  * tags: [code/vector_math, code/function_composition]
  * concepts: [Vector Field Function]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
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

