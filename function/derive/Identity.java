package function.derive;

import math.vector.VectorDouble;
import math.vector.VectorFloat;
import streamIO.copy.ACopyAble;
import streamIO.object.IStreamIn;
import function.byref.ByRefDouble;
import function.byref.ByRefFloat;
import function.vector.AFloatVectorField;
import function.vector.IFloatVectorField;

/**This Class encapsulates the Identity Function.
 * It always returns the Argument.
 * It is dangerous to use it, because the Original is returned!!!
 * So you should not do any great calculations with it. */
final public class Identity
extends AFloatDeriveAble
implements IFloatVectorField {

	///////////////////////////////////////////////////////////////////////////
	//  static Variables
	///////////////////////////////////////////////////////////////////////////

	/**Local Reference to the single Instance	 */
	final static public Identity IDENTITY = new Identity();
	
	////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**private Constructor for Singleton Implementation	 */
	protected Identity() { //Singleton, so no STATIC Initializer necessary!
		setInverse   (this);
		setDerivative(CCountAble.One);
	//	setIntegral  (); //x^2/2 //set in the corresponding Class
	}
	
	////////////////////////////////////////////////////////////////////////////
	//  Interface IFunction: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**This Function represents the Identity Function.
	 * It always returns a Copy of the Argument.  */
	public Object Map (Object arg)	{
		try { return ACopyAble.COPY(arg); } //(try to) create a Copy
		catch (InstantiationException e) {
			throw new InstantiationError(e.toString()); } }

	/**This Function represents the Identity Function.
	 * It always returns the Argument.  */
	public Object MapAt(Object arg)	{ return arg; }

	/**This Function represents the Identity Function.
	 * It always returns the Argument.  */
	public double Map(double arg)	{ return arg; }

	/**This Function represents the Identity Function.
	 * It always returns the Argument.  */
	public double[] map(double[] arg, double[] out)	{
		if ((out == null) || (out == arg)) {
			return arg; }
		VectorDouble.COPY(arg, out);
		return out; }

	/**This Function represents the Identity Function.
	 * It always returns the Argument.  */
	public float[] map(float[] arg, float[] out) {
		if  (out == null) {
			 out = new float[arg.length]; } //make a Copy...
		if ((out == null) ||
			(out == arg)) {
			return arg; }
		VectorFloat.COPY(arg, out);
		return out; }
	
    /** @see function.IFloatFunction#getOrder()     */
    public byte getOrder() { return IStreamIn.ORDER_ASC_STRICT; }
    
	/**Returns the Derivative of the Function at Point x	 */
	public double getDerivative(double x) { return 1; }

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public double getFuncDerive (double x, ByRefDouble derivative) {
		derivative.Value = 1; return x; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IFloatVectorField: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Position to evaluate
	 * @param out The Value to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new double[][] to return it's Values.
	 */
	public double[][] map(double[][] v, double[][] out) {
		return AFloatVectorField.Map(this, v, out); }

	/**
	 * Maps the Vectors to Vector Values
	 * @param v The Positions to evaluate
	 * @param out The Values to be returned
	 * The Contract is that if out is null,
	 * the VectorField creates a new float[][] to return it's Values.
	 */
	public float[][] map(float[][] v, float[][] out) {
		return AFloatVectorField.Map(this, v, out); }

	///////////////////////////////////////////////////////////////////////////
	//  Optimizations
	///////////////////////////////////////////////////////////////////////////

	/** Returns the Function Value (mapping) of the Argument arg
	  * This Function represents the Identity Function.
	  * It always returns the Argument.  */
	public float  Map(float  arg)	{ return arg; }

	/**Returns the Derivative of the Function at Point x	 */
	public float getDerivative(float x) { return 1.0f; }

	/** Calculates Function and Derivative at the same time,
	 * returns the Function Value directly and the Derivative ByRef	  */
	public float getFuncDerive (float x, ByRefFloat derivative) {
		derivative.Value = 1.0f; return x; }

}

