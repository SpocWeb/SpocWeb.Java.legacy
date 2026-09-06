package structure.aspect;

import java.util.Date;
import java.util.zip.DataFormatException;

import synch.InvalidException;

/**
  * Extends {@link NumberAspect} to store and validate a double Value against the inherited
  * Min/Max/Modulus Range, notifying Validators and Subscribers on every change.
  *
  * Extends and implements the NumberAspect Class for double Values
  * Purpose / Responsibilities of this Class
  *
  * Implementation Details:
  * Instead of a separate IntegerAspect Class this Class can be used
  * with a Module of 1.0!
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-01-2002, 05:32 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:19:49Z
  * digest: 823c76cd0520c7fef7672bcc8d9c011d3bf5fd0560ce99849de5f6c9a44a0604
  * stale: false
  * tags: [code/property_binding]
  * concepts: [Double-Valued Aspect]
  * facets: {layer: domain, status: broken, complexity: low}
  * -->
  */
public class DoubleAspect
extends NumberAspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Value of this Aspect as a Scalar */
	protected double value;

	/** The Value of this Aspect as an Object */
	protected Double Value;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected DoubleAspect(String name) { super(name); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** sets The Aspect Value as a double (long is automatically cast!)  */
public void setValue(double Value_)throws    DataFormatException {
	setValue(new Double(Value_)); }

/** sets The Aspect Value as a double (long is automatically cast!)  */
public void setValue(Double Value_) throws    DataFormatException{
	double value_ = Value_.doubleValue();
	if  (value_ > MaxValue)       { throw new DataFormatException("Value " + Value_ + " exceeds the Maximum Value " + MaxValue); }
	if  (value_ < MinValue)       { throw new DataFormatException("Value " + Value_ + " exceeds the Minimum Value " + MinValue); }
	if ((Modulus != 0) &&
		(value_ % Modulus) != 0)  { throw new DataFormatException("Value " + Value_ + " is not a Multiple of " + Modulus); }
	try {
		notifyValidators (Value, Value_);
		notifySubscribers(Value, Value_);
	} catch (InvalidException x) {
		throw new DataFormatException(x.toString());
	}
	this.Value = Value_;
	this.value = value_; //keep the primitive Field in Sync, getLong()/getDouble() read it
}

/** Returns this Aspect's Value as a boxed Double, or {@code null} when not validly set.
  * @return The Aspect Value as a (boxed) Object  */
public Object getValue() {
	if (Status != 0) {
		return null; }
	return Value; }

/** Returns this Aspect's Value rendered as a String, or {@code null} when not validly set.
 * @return The Aspect Value as a String Representation
 * This is always possible for any Type
 */
public String getString() {
	if (Status != 0) { return null; }
	return Value.toString(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : unsafe Accessor Methods (getXXX/isXXX/setXXX)
/// The Question is whether to throw Exceptions when the Type was not suitable
/// or to return special Values indicating the Missing of the Value
////////////////////////////////////////////////////////////////////////////////

/** sets the Aspect Value as a (boxed) Object  */
public void setValue(Object value) throws DataFormatException {
	if (value instanceof Number) {
		setValue(((Number) value).doubleValue());
	} else {
		throw new DataFormatException("Value '" + value + "' is not numeric!"); }
}

/** sets the Aspect Value as a String   */
public void setString(String value) throws DataFormatException {
	try {
		setValue(Double.parseDouble(value)); }
	catch (Exception x) {
		throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

/** Returns this Aspect's Value truncated to a long, rejecting a non-integral Value.
  * @return The Aspect Value as a long Representation
  * @throws DataFormatException when the Status is invalid or the Value is not integral  */
public long getLong() throws DataFormatException {
	if (Status != 0)          { throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); }
	long ret  = (long) value;
	if  (ret !=        value) { throw new DataFormatException("Value '" + Value + "' is not integer!"); }
	return ret; }

/** Returns this Aspect's Value as a primitive double.
  * @return The Aspect Value as a double Representation
  * @throws DataFormatException when the Status is invalid  */
public double getDouble() throws DataFormatException {
	if (Status != 0)          { throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); }
	return value; }

/** Returns this Aspect's Value as a Date, derived from {@link #getLong()}.
  * @return The Aspect Value as a Date Representation
  * @throws DataFormatException when the Status is invalid or the Value is not integral  */
public Date getDate() throws DataFormatException {
	if (Status != 0)          { throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); }
	return new Date(getLong()); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + DoubleAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

