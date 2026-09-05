package function.vector;

/**
  * Base {@link IFloatVectorField} implementation supplying the batch (array-of-vectors)
  * overloads of {@code map} in terms of the single-vector abstract methods.
  *
  * Title: AFloatVectorField.java<p>
  * Description:
  * TODO: Describes the Purpose / Responsibilities of this Class, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	06-20-2001, 08:22 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:47:02Z
  * digest: bb0261e08baccfdda6b166ccf9af3e1c97449e0fb8756112d5bc7f4f7f9b1345
  * stale: false
  * tags: [code/vector_math, code/function_composition]
  * concepts: [Vector Field Function]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public abstract class AFloatVectorField
implements IFloatVectorField {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Maps the Vector to a Scalar Value
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[][] to return it's Values.
	 */
	final static public double[][] Map(IFloatVectorField VF, double[][] v, double[][] out) {
		if (out == null) {
			out  = new double[v.length][];
		}
		int i = v.length;
		while (--i >= 0) {
			out[i] = VF.map(v[i], out[i]); }
		return out; }

	/**
	 * Maps the Vector to a Scalar Value
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[][] to return it's Values.
	 */
	final static public float[][] Map(IFloatVectorField VF, float[][] v, float[][] out) {
		if (out == null) {
			out  = new float[v.length][]; }
		int i = v.length;
		while (--i >= 0) {
			out[i] = VF.map(v[i], out[i]); }
		return out; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IFloatVectorField: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Maps the Vector to a Vector Value
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[] to return it's Values.
	 */
	public abstract double[] map(double[] v, double[] out);

	/**
	 * Maps the Vector to a Vector Value
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[] to return it's Values.
	 */
	public abstract float[] map(float[] v, float[] out);

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IFloatVectorField: Implementation
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[][] to return it's Values.
	 */
	public double[][] map(double[][] v, double[][] out) {
		return Map(this, v, out); }

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Positions to evaluate
	 * @param out The Values to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[][] to return it's Values.
	 */
	public float[][] map(float[][] v, float[][] out) {
		return Map(this, v, out); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AFloatVectorField.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

