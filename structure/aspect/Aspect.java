package structure.aspect;

import java.util.Date;
import java.util.zip.DataFormatException;

import synch.UniCastConstrained;

/**
  * Title: Aspect<p>
  * Description:
  * Purpose:
  * Abstract Base Class for all Aspect Types
  * No need to add getProperty(int Num),
  * because you can use Reflection for this!
  *
  * The Base Class UniCastConstrained adds Support for
  * a single VetoListener and
  * a single ChangeListener.
  *
  * Implementation Details:
  *
  * Known SubClasses:
  *
  * Known Uses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-01-2002, 03:53 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class Aspect
extends UniCastConstrained {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Name' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds The Aspect Name   */
protected String Name;

/** @return The Aspect Name  */
public String getName() {
	return Name; }

/** Sets The Aspect Name. TODO: shouldn't this be an Invariant?  */
public void setName(String Name_) {
	this.Name = Name_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Enabled' with Accessor Methods
/// possibly use a Formula referring to other Aspects, instead of a boolean Flag
////////////////////////////////////////////////////////////////////////////

/** holds the Control editable   */
protected boolean Enabled;

/** @return the Control editable  */
public boolean getEnabled() {
	return Enabled; }

/** Sets the Control editable  */
public void setEnabled(boolean Enabled_) {
	this.Enabled = Enabled_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Required' with Accessor Methods
/// possibly use a Formula referring to other Aspects, instead of a boolean Flag
////////////////////////////////////////////////////////////////////////////

/** holds the Control required to enter   */
protected boolean Required;

/** @return the Control required to enter  */
public boolean getRequired() {
	return Required; }

/** Sets the Control required to enter  */
public void setRequired(boolean Required_) {
	this.Required = Required_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Visible' with Accessor Methods
/// Making a Control (in-)visible is probably unintuitive to the User!
/// possibly use a Formula referring to other Aspects, instead of a boolean Flag
////////////////////////////////////////////////////////////////////////////

/** holds the Control visible to the User   */
protected boolean Visible;

/** @return the Control visible to the User  */
public boolean getVisible() {
	return Visible; }

/** Sets the Control visible to the User  */
public void setVisible(boolean Visible_) {
	this.Visible = Visible_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Error' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

/** holds the Error Status for this Aspect   */
protected int Status;

/** @return the Error Status for this Aspect
 *   0 when the Value is filled / valid / initialized,
 *  -1 when the Value is not filled / empty / initialized,
 *  otherwise the (positive) Error Number
 */
public int getStatus() {
	return Status; }

/** Sets the Status for this Aspect  */
public void setStatus(int Status_) {
	this.Status = Status_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** @return The Aspect Value as a (boxed) Object  */
public abstract Object getValue();

/**
 * @return The Aspect Value as a String Representation
 * This is always possible for any Type
 */
public abstract String getString();

////////////////////////////////////////////////////////////////////////////////
/// #region : unsafe Accessor Methods (getXXX/isXXX/setXXX)
/// The Question is whether to throw Exceptions when the Type was not suitable
/// or to return special Values indicating the Missing of the Value
////////////////////////////////////////////////////////////////////////////////

/** sets the Aspect Value as a (boxed) Object  */
public abstract void setValue(Object value) throws DataFormatException;

/** sets the Aspect Value as a String   */
public abstract void setString(String value) throws DataFormatException;

/** @return The Aspect Value as a long Representation  */
public abstract long getLong() throws DataFormatException;

/** @return The Aspect Value as a double Representation  */
public abstract double getDouble() throws DataFormatException;

/** @return The Aspect Value as a Date Representation  */
public abstract Date getDate() throws DataFormatException;

/** @return The Aspect Value as a Currency Representation  */
//public abstract Currency getCurrency() throws DataFormatException;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected Aspect(String name_) {
		this.Name = name_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface TODO: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Object: Implementation
////////////////////////////////////////////////////////////////////////////////

    /** @return a hashcode for this Aspect.     */
	public int hashCode() { return Name.hashCode(); }

    /** @return a String Representation for this Aspect.     */
	public String toString() { return Name; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Aspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

