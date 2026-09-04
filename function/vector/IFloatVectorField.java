package function.vector;

/**
  * Title: IFloatVectorField<p>
  * Description:
  * Defines the Interface for ...
  * TODO: Describes the Purpose / Responsibilities
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
public interface IFloatVectorField {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Maps the Vector to a Vector Value
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[] to return it's Values.
	 */
	double[] map(double[] v, double[] out);

	/**
	 * Maps the Vector to a Vector Value
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[] to return it's Values.
	 */
	float[] map(float[] v, float[] out);

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[][] to return it's Values.
	 */
	double[][] map(double[][] v, double[][] out);

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[][] to return it's Values.
	 */
	float[][] map(float[][] v, float[][] out);

}

