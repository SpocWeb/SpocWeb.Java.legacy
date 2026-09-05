package synch.aspect;

import synch.InvalidException;

/**
  * Title: StringAspect<p>
  * Description:
  * Leaf {@link Aspect} holding a single String Value, with an enforced MinLength/
  * MaxLength range checked in {@link #myValidate}. Setting the Value propagates
  * validation and update notifications up to the Parent Aspect when it actually changes.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-19-2002, 10:55 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:37Z
  * digest: a7c3eb09fd43cfeac800b6c9d2875c7ccfb9bd804aac2ab2c62be3a24abf5ae5
  * stale: false
  * tags: [code/attached_property]
  * concepts: [String Value Aspect]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public class StringAspect
extends Aspect
{

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** The Value of this Aspect	*/
	private String Value; // = "";

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICPair, IPair: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @param sets Value of this Aspect as an Object */
	public void setValue(Object val) throws InvalidException {
		String oldVal = Value;
		if (val instanceof Aspect) {
			setValue(((Aspect) val).getVal());
			return; }
		if (val == null) {
			Value = "";
		} else { //regular and Boxed Objects
			Value  = val.toString(); }
		if ((Value   ==   oldVal) ||
			(Value.equals(oldVal))) {
			return; }
		if (Parent  != null) {
			Parent.validateParent(this, Value, oldVal);
			Parent.  updateParent(this, Value, oldVal); } //propagate the Change up the Tree!
	}

	/** Accessor Method
	  * @return the Value of this Aspect as an Object */
	public Object getVal() { return Value; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MinLength' with Accessor Methods
/// This is independent of 'Required'.
/// A non required Field can still have a MinLength > 0
/// indicating to enter either Nothing or at minimum the given Number of Characters.
////////////////////////////////////////////////////////////////////////////

	/** holds the Minimum Length for the Input    */
	protected int MinLength;

	/** Returns the minimum accepted Length for the Input.
	  * @return the Minimum Length for the Input   */
	public int getMinLength() {
		return MinLength; }

	/** Sets the Minimum Length for the Input   */
	public void setMinLength(int MinLength_) {
		this.MinLength = MinLength_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'MaxLength' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** holds the Maximum Length for the Input   */
	protected int MaxLength = Integer.MAX_VALUE;

	/** Returns the maximum accepted Length for the Input.
	  * @return the Maximum Length for the Input  */
	public int getMaxLength() {
		return MaxLength; }

	/** Sets the Maximum Length for the Input  */
	public void setMaxLength(int MaxLength_) {
		this.MaxLength = MaxLength_; }

/** @return The Aspect Value as a long Representation  */
/*public long getLong() throws DataFormatException {
	if (Status != 0)    { throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); }
	try { return Long.parseLong(Value); }
	catch (Exception x) { throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

/** sets the Aspect Value as a long Representation  */
//public void setLong(long value) { this.Value = Long.toString(value); }

/** @return The Aspect Value as a double Representation  */
/*public double getDouble() throws DataFormatException {
	if (Status != 0)    { throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); }
	try { return Double.parseDouble(Value); }
	catch (Exception x) { throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

/** sets the Aspect Value as a double Representation  */
//public void setDouble(double value) { this.Value = Double.toString(value); }

/** @return The Aspect Value as a Date Representation  */
/*public Date getDate() throws DataFormatException {
	if (Status != 0)    { throw new DataFormatException("Aspect '" + Name + "' has Status:" + Status); }
	try { return new Date(Value); }
	catch (Exception x) { throw new DataFormatException("The Value '" + Value + "' could not be parsed: " + x.toString()); }
}

/** sets the Aspect Value as a Date Representation  */
//public void setDate(Date value) { this.Value = value.toString(); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public StringAspect(String Name, Aspect Parent) {
		super(Name, Parent); }

	/** Initializing Constructor	 */
	public StringAspect(String Name, Aspect Parent, String Value) {
		super(Name, Parent);
		this.Value = Value; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/**This Method is responsible for copying the given Value
	 * into the local Value of this Property.
	 * This is used e.g. on receiving an Update from a Publisher.
	 * All the Rest of the Publication Mechanism is handled automatically!
	 */
	protected void copyFieldsAt(Object Value) {
		if (Value instanceof StringAspect) {
			StringAspect Value_ = (StringAspect) Value;
			this.MaxLength = Value_.MaxLength;
			this.MinLength = Value_.MinLength;
		}
		setVal(Value);
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Aspect: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Recursively clears all Aspects and Subaspects */
	public void clear() { Value = ""; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent Aspect: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Object: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Returns this Aspect's current String Value.
	  * @return the current Value  */
	public String toString() { return Value; }

	/** Sets this Aspect's Value directly to the given String, bypassing validation and
	  * update propagation (unlike {@link #setValue(Object)}).  */
	public void fromString(String Value) { this.Value = Value; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IValidator: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Local Validation Routine to validate multifield Checks, not used in this Class! */
//	public void validate(Object Source, Object Value, Object oldVal)
//		throws InvalidException { }

	/**
	 * This is be the Interface for a Subscriber that can veto the Change
	 * To enforce the Creation of an individual Method for Validation
	 * of a specific named Aspect Reflection could be used:
	 * search for validate<Name>(Object Source, Aspect Value, Aspect oldVal)
	 * catch and examine any thrown Exception and convert it to InvalidException.
	 * But Reflection is not safe, because the Method could simply not be found.
	 * Attributes like in C# are not possible in Java.
	 */
	protected void myValidate(Object Source, Object Value, Object oldVal)
		throws InvalidException {
/*		if (!(Source instanceof StringAspect)) {
			throw new InvalidException(Source, Value, "Illegal Type of Source"); }
		StringAspect Value_ = (StringAspect) Value;
*/		if (Value == null) {
			return; }
		String str = Value.toString();
		int len = str.length();
		if (len == 0) {
			return; }
		if (len > MaxLength) { throw new InvalidException(Source, Value, "Maximum Length is " + MaxLength); }
		if (len < MinLength) { throw new InvalidException(Source, Value, "Minimum Length is " + MinLength); }
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + StringAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

