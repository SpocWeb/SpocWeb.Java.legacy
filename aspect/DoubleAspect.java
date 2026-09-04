package aspect;

import java.util.Date;

import streamIO.Assert;
import synch.InvalidException;
import function.ACountAble;
import function.byref.ByRefDouble;

/**
  * Title: DoubleAspect<p>
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
public class DoubleAspect
extends AHierarchyAspect { //Aspect {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Value of this Aspect */
	protected double value = Double.NaN; //Use NAN as a regular Representation for 'null' !
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'MinValue' with Accessor Methods
	/// This is independent of 'Required'.
	/// A non required Field can still have a MinValue > 0
	/// indicating to enter either Nothing or at minimum the given Number of Characters.
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the Minimum Value for the Input    */
	protected double MinValue = Double.MIN_VALUE;
	
	/** @return the Minimum Value for the Input   */
	public double getMinValue() {
		return MinValue; }
	
	/** Sets the Minimum Value for the Input   */
	public void setMinValue(double MinValue_) {
		this.MinValue = MinValue_; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'MaxValue' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the Maximum Value for the Input   */
	protected double MaxValue = Double.MAX_VALUE;
	
	/** @return the Maximum Value for the Input  */
	public double getMaxValue() {
		return MaxValue; }
	
	/** Sets the Maximum Value for the Input  */
	public void setMaxValue(double MaxValue_) {
		this.MaxValue = MaxValue_; }
	
	////////////////////////////////////////////////////////////////////////////
	/// #region : Variable 'Module' with Accessor Methods
	////////////////////////////////////////////////////////////////////////////
	
	/** holds the Maximum Value for the Input   */
	protected double Module = Double.MAX_VALUE;
	
	/** @return the Maximum Value for the Input  */
	public double getModule() {
		return Module; }
	
	/** Sets the Maximum Value for the Input  */
	public void setModule(double Module_) {
		if (Module_ < 0) {
			throw new IllegalArgumentException("Module must be >= 0 but is :" + Module_); }
		this.Module = Module_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
		/** Initializing Constructor for Standalone Double Aspects	 */
		public DoubleAspect(String name) { super(name, null); }
	
		/** Initializing Constructor for Double Aspects as Part of a complex Aspect	 */
		public DoubleAspect(String name, IHierarchyAspect Parent) { super(name, Parent); }
	
		/** @return a new Instance of this Object */
	//	public IAspect newInstance(String Name) {
	//		return new DoubleAspect(Name, null); }
	
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
		return new Double(value); }
	
	///////////////////////////////////////////////////////////////////////////////////
	/// Convenience Access Methods.
	///////////////////////////////////////////////////////////////////////////////////
	
	/** @return The Aspect Value as a long Representation  */
	public long getLong() { return ACountAble.getLong(value); }
	
	/** @return The Aspect Value as a double Representation  */
	public double getDouble() { return value; }
	
	/** @return The Aspect Value as a Date Representation  */
	public Date getDate() { return new Date(ACountAble.getLong(value)); }
	
	/**
	 * @return The Aspect Value as a String Representation
	 * This is always possible for any Type
	 */
	public String toString() {
		if (value != value) { //cannot test for NaN or Infinity using '==' !
			return ""; }
		return Double.toString(value); }
	
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
	protected void validatePrimVal(Object _value) throws InvalidException {
		if (_value == null) {
			return; }
		double value = ByRefDouble.GET_DOUBLE(_value);
		if (value != value) { //cannot test for NaN or Infinity using '==' !
			throw new InvalidException(this, _value, "Value " + _value + " is not a Number!"); }
	//		return; }
		if  (value > MaxValue) { throw new InvalidException(this, _value, "Value " + _value + " exceeds the Maximum Value " + MaxValue); }
		if  (value < MinValue) { throw new InvalidException(this, _value, "Value " + _value + " exceeds the Minimum Value " + MinValue); }
		if ((0     < Module  ) && (Math.abs
			(value % Module  ) >
			(0.001 * Module))) { throw new InvalidException(this, _value, "Value " + _value + " is not an integer Multiple of  " + Module); }
	}
	
	/** sets the Aspect Value as a (boxed) Object  */
	protected void setPrimVal(Object _value) {
		if (_value == null) {
			value = Double.NaN; return; }
		value = ByRefDouble.GET_DOUBLE(_value); }
	
	/** sets the Aspect Value as a String   */
	//protected void setString(String value_) throws InvalidException {
	//	Value = value_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() { //throws java.io.IOException {
		System.out.println("Testing " + DoubleAspect.class.getName());
		DoubleAspect sa = new DoubleAspect("TestName");
		sa.setModule(1);
		sa.value = 3.14159;
		try {
			sa.setValue("3");
			sa.setValue("3.14159");
			Assert.FAIL("Expected: Exception due to invalid Module!");
		} catch (InvalidException x) {
			System.out.println("Error: " +  x);
		}
		System.out.println("old Instance: '" +  sa + "'");
		IAspect asp = sa.newInstance("test");
		System.out.println("new Instance: '" + asp + "'");
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

