package aspect;

import java.util.Date;
import java.util.zip.DataFormatException;

import synch.InvalidException;

/**
  * Title: StringAspect<p>
  * Description:
  * Purpose:
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
  */
public class StringAspect
extends AHierarchyAspect { //Aspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

/** The Value of this Aspect */
protected String Value = ""; //String is the only Class where 'null' has a regular Representation!

//double can use NAN!

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
protected int MinLength = 0;

/** @return the Minimum Length for the Input   */
public int getMinLength() {
	return MinLength; }

/** Sets the Minimum Length for the Input   */
public void setMinLength(int MinLength_) {
	if (MinLength_ < 0) {
		throw new IllegalArgumentException("Minimum Length must be >= 0 but is :" + MinLength_); }
	this.MinLength = MinLength_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MaxLength' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Maximum Length for the Input   */
protected int MaxLength = Integer.MAX_VALUE;

/** @return the Maximum Length for the Input  */
public int getMaxLength() {
	return MaxLength; }

/** Sets the Maximum Length for the Input  */
public void setMaxLength(int MaxLength_) {
	if (MaxLength_ < 0) {
		throw new IllegalArgumentException("Maximum Length must be >= 0 but is :" + MaxLength_); }
	this.MaxLength = MaxLength_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'RegExp' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds Regular Expression this String has to obey to   */
protected String RegExp;

/** @return Regular Expression this String has to obey to  */
public String getRegExp() {
	return RegExp; }

/** Sets Regular Expression this String has to obey to  */
public void setRegExp(String RegExp_) {
	this.RegExp = RegExp_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor for Standalone String Aspects	 */
	public StringAspect(String name) { super(name, null); }

	/** Initializing Constructor for String Aspects as Part of a complex Aspect	 */
	public StringAspect(String name, IHierarchyAspect Parent) { super(name, Parent); }

	/** @return a new Instance of this Object */
//	public IAspect newInstance(String Name) {
//		return new StringAspect(Name, null); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** @return The Aspect Value as a (boxed) Object  */
public Object getVal() {
//	if (Status != 0) { return null; }
	return Value; }

/**
 * @return The Aspect Value as a String Representation
 * This is always possible for any Type
 * This replaces the getString() Method for typed Results.
 */
public String toString() {
//	if (Status != 0) { return null; }
	return Value; }

////////////////////////////////////////////////////////////////////////////////
/// #region : unsafe Accessor Methods (getXXX/isXXX/setXXX)
/// The Question is whether to throw Exceptions when the Type was not suitable
/// or to return special Values indicating the Missing of the Value
////////////////////////////////////////////////////////////////////////////////

/** Local Validation Routine to validate multifield Checks
  * Called both from validate() Child and validateParent() Validation!
  */
/*protected void validatePrimVal(Object Source, Object Value, Object oldVal) throws InvalidException {
	validatePrimVal(Value.toString()); }

/** Local Validation Routine to validate multifield Checks
  * Called both from validate() Child and validateParent() Validation!
  */
protected void validatePrimVal(Object value) throws InvalidException {
	if (value == null) {
		return; }
	String value_ = value.toString(); //faster than Type Checking!
	int len = value_.length();
	if (len == 0) { return; }
	if (len > MaxLength) { throw new InvalidException(this, value_, "Value " + value_ + " exceeds the Maximum Length " + MaxLength); }
	if (len < MinLength) { throw new InvalidException(this, value_, "Value " + value_ + " exceeds the Minimum Length " + MinLength); }
}

/** sets the Aspect Value as a (boxed) Object  */
protected void setPrimVal(Object value) {
	String str = null;
	if (value != null) {
		str = value.toString(); }
	if (str == null) {
		str = ""; } //String is the only primitive Type where null has a Representation!
	Value = str; }
//	setString(str); }

/** sets the Aspect Value as a String   */
//protected void setString(String value_) throws InvalidException {
//	Value = value_; }

/** @return The Aspect Value as a long Representation  */
public long getLong() throws DataFormatException {
//	if (Status != 0)    { throw new InvalidException(this, value_, "Aspect '" + Name + "' has Status:" + Status); }
	try {
		return Long.parseLong(Value); }
	catch (Exception x) { throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

/** @return The Aspect Value as a double Representation  */
public double getDouble() throws DataFormatException {
//	if (Status != 0)    { throw new InvalidException(this, value_, "Aspect '" + Name + "' has Status:" + Status); }
	try {
		return Double.parseDouble(Value); }
	catch (Exception x) { throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

/** @return The Aspect Value as a Date Representation  */
public Date getDate() throws DataFormatException {
//	if (Status != 0)    { throw new InvalidException("Aspect '" + Name + "' has Status:" + Status); }
	try {
		return new Date(Value); }
	catch (Exception x) { throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + StringAspect.class.getName());
		StringAspect sa = new StringAspect("TestName");
		sa.Value = "Hello";
		System.out.println("old Instance: '" +  sa + "'");
		IAspect asp = sa.newInstance("test");
		System.out.println("new Instance: '" + asp + "'");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

