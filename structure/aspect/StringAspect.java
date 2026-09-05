package structure.aspect;

import java.util.Date;
import java.util.zip.DataFormatException;

import synch.InvalidException;

/**
  * Extends {@link Aspect} to store and validate a String Value against a Min/Max Length
  * Range, notifying Validators and Subscribers on every change.
  *
  * Extends and implements the Aspect Class for String Values
  * Purpose / Responsibilities of this Class
  *
  * Implementation Details:
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-01-2002, 05:26 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:20:34Z
  * digest: 3f5c148e278c77e3f740e3ef1a981bce96c614cb9e69c72ea93ee4f203266a37
  * stale: false
  * tags: [code/property_binding, code/data_validation]
  * concepts: [String-Valued Aspect]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public class StringAspect
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

/** The Value of this Aspect */
protected String Value;

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MinLength' with Accessor Methods
/// This is independent of 'Required'.
/// A non required Field can still have a MinLength > 0
/// indicating to enter either Nothing or at minimum the given Number of Characters.
////////////////////////////////////////////////////////////////////////////

/** holds the Minimum Length for the Input    */
protected int MinLength;

/** Returns the shortest Input Length this Aspect accepts.
  * @return the Minimum Length for the Input   */
public int getMinLength() {
	return MinLength; }

/** Sets the Minimum Length for the Input   */
public void setMinLength(final int MinLength_) {
	this.MinLength = MinLength_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MaxLength' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Maximum Length for the Input   */
protected int MaxLength;

/** Returns the longest Input Length this Aspect accepts.
  * @return the Maximum Length for the Input  */
public int getMaxLength() {
	return MaxLength; }

/** Sets the Maximum Length for the Input  */
public void setMaxLength(final int MaxLength_) {
	this.MaxLength = MaxLength_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected StringAspect(String name) { super(name); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** Returns this Aspect's Value, delegating to {@link #getString()} since String is already
  * this Aspect's natural Type.
  * @return The Aspect Value as a (boxed) Object  */
public Object getValue() { return getString(); }

/** Returns this Aspect's Value, or {@code null} when not validly set.
 * @return The Aspect Value as a String Representation
 * This is always possible for any Type
 */
public String getString() {
	if (Status != 0) 
		return null; 
	return Value; }

////////////////////////////////////////////////////////////////////////////////
/// #region : unsafe Accessor Methods (getXXX/isXXX/setXXX)
/// The Question is whether to throw Exceptions when the Type was not suitable
/// or to return special Values indicating the Missing of the Value
////////////////////////////////////////////////////////////////////////////////

/** sets the Aspect Value as a (boxed) Object  */
public void setValue(Object value_) throws DataFormatException {
	setString(value_.toString()); }

/** sets the Aspect Value as a String   */
public void setString(String value_) throws DataFormatException {
	int len = value_.length();
	if (len > MaxLength) throw new DataFormatException("Value " + value_ + " exceeds the Maximum Length " + MaxLength); 
	if (len < MinLength) throw new DataFormatException("Value " + value_ + " exceeds the Minimum Length " + MinLength); 
	try {
		notifyValidators (Value, value_);
		notifySubscribers(Value, value_);
	} catch (final InvalidException x) {
		throw new DataFormatException(x.toString()); 
	}
	Value = value_; 
}

/** Parses this Aspect's Value as a long.
  * @return The Aspect Value as a long Representation
  * @throws DataFormatException when the Status is invalid or the Value does not parse  */
public long getLong() throws DataFormatException {
	if (Status != 0)    
		throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); 
	try {
		return Long.parseLong(Value); 
	} catch (final Exception x) {  
		throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString());
	}
}

/** Parses this Aspect's Value as a double.
  * @return The Aspect Value as a double Representation
  * @throws DataFormatException when the Status is invalid or the Value does not parse  */
public double getDouble() throws DataFormatException {
	if (Status != 0) 
		throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); 
	try {
		return Double.parseDouble(Value); 
	} catch (final Exception x) {
		throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString());
	}
}

/** Parses this Aspect's Value as a Date.
  * @return The Aspect Value as a Date Representation
  * @throws DataFormatException when the Status is invalid or the Value does not parse  */
public Date getDate() throws DataFormatException {
	if (Status != 0) 
		throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); 
	try {
		return new Date(Value); 
	} catch (final Exception x) { 
		throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString());
	}
}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(final String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + StringAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) { //throws java.io.IOException {
		testIt(args); }

}

