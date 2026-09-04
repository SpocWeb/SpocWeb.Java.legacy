package structure.aspect;

import java.util.zip.DataFormatException;

/**
  * Title: NumberAspect<p>
  * Description:
  * Purpose:
  * Extends the Aspect Class with Methods to specify a numeric Range
  * Normally a double Aspect would be sufficient, because it is large enough (53 Bits)
  * to simulate the int (32 Bits) and nearly the long (64 Bits) Range with full Accuracy.
  *
  *
  * Implementation Details:
  * This is the Base Class for all numeric Aspects like
  * IntegerAspect
  *  DoubleAspect
  * BooleanAspect
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-01-2002, 05:15 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class NumberAspect
extends Aspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** sets The Aspect Value as a double (long is automatically cast!)  */
public abstract void setValue(double Value) throws DataFormatException;

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MinValue' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Minimum Value of this Aspect   */
protected double MinValue;

/** @return the Minimum Value of this Aspect  */
public double getMinValue() {
	return MinValue; }

/** Sets the Minimum Value of this Aspect  */
public void setMinValue(double MinValue_) {
	this.MinValue = MinValue_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MaxValue' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Maximum Value of this Aspect   */
protected double MaxValue;

/** @return the Maximum Value of this Aspect  */
public double getMaxValue() {
	return MaxValue; }

/** Sets the Maximum Value of this Aspect  */
public void setMaxValue(double MaxValue_) {
	this.MaxValue = MaxValue_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Modulus' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Module of this Aspect. The Value must be divisible by the Modulus without Rest   */
protected double Modulus;

/** @return the Module of this Aspect. The Value must be divisible by the Modulus without Rest  */
public double getModulus() {
	return Modulus; }

/** Sets the Module of this Aspect. The Value must be divisible by the Modulus without Rest  */
public void setModulus(double Modulus_) {
	this.Modulus = Modulus_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected NumberAspect(String name) { super(name); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + NumberAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

