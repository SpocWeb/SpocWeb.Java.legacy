package streamIO.copy.boole;

import function.ICountAble;

/**
  * Title: Ternary<p>
  * Description:
  * This Class is a Realization of Boole
  * with a single tri State Value (Bit).
  * Values like these occur e.g. when reading Data from a DataBase
  * with outer Joins or not mandatory boolean Columns.
  *
  * Unfortunately this Class implements CopyAble,
  * so its Instances can in principle be modified in Place,
  * which is a dangerous Feature and especially useless in finite Sets
  * like this one, because you can modify inner Components,
  * have to create expensive Copies to prevent that
  * and cannot use the faster Identity Check == to test the Values!
  * Additionally you have to check for the Range on Value Assignment.
  *
  * (R, min, max) is a Lattice, but not a Boolean one:
  * NOT can be defined as NOT a = MaxEl-a
  *
  * Commutativity and Assiociativity of min and max are evident
  *
  * Idempotency:
  * a min a = a max a = a
  *
  * Adjunctivity:
  * a min (a max b) = a
  * a max (a min b) = a
  *
  * Distributivity:
  * 1) a min (b max c) = (a min b) max (a min c)
  * 2) a max (b min c) = (a max b) min (a max c)
  *
  * Distributivity is fulfilled, also for continuous Values,
  * as can be shown considering all Cases:
  * Consider the following 6 Cases:
  * a <= b <= c => 1) a = a 2) b = b
  * a <= c <= b => 1) a = a 2) c = c
  * b <= a <= c => 1) a = a 2) a = a
  * b <= c <= a => 1) c = c 2) a = a
  * c <= b <= a => 1) b = b 2) a = a
  * c <= a <= b => 1) a = a 2) a = a
  *
  * The Komplementariness Axioms of the Boolean Lattice are not fulfilled
  * for 0 or continuous Values between -1 and +1,
  * since there is no full Certainty, there is also no full Uncertainty:
  * a AND NOT a == False <=> a min -a == -|a| >= False
  * a OR  NOT a == True  <=> a max -a ==  |a| <= True
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-24-2002, 02:53 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class Ternary
extends ABoole
implements ICountAble {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Constant denoting FALSE	 */
	final static public byte FALSE = -1;

	/** Constant denoting NULL or Unknown	 */
	final static public byte NULL  =  0;

	/** Constant denoting TRUE	 */
	final static public byte TRUE  = +1;

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** The actual Value of this ternary Object. 	 */
	protected byte Value;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** returns the Value converted to Byte	 */
	public byte getValue() { return Value; }

	/** sets the Value converted from Byte 	 */
	public void setValue(byte _value) {
		if ((_value < FALSE) ||
			(_value > TRUE )) throw new RuntimeException("Ternary: Value out of Range!");
		Value = _value; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected Ternary() { }

	/** Empty Constructor	 */
	protected Ternary(boolean _value) { Value = (_value ? TRUE : FALSE); }

	/** Empty Constructor	 */
	protected Ternary(byte _value) { setValue(_value); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface ILattice: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** AND Operation in Place: &=
	  * This corresponds to the MinAt Operation.
	  * @return a & b
	  * a AND b = true <=> (a = true) AND (b = true) 	 */
	public Lattice ANDat	(Object arg) {
		if (Value > ((Ternary) arg).Value) {
			Value = ((Ternary) arg).Value; }
		return this; }

	/** OR Operation in Place: |=
	  * This corresponds to the MaxAt Operation.
	  * @return a | b
	  * a OR b = true <=> (a = true) OR (b = true) 	 */
	public Lattice ORat	(Object arg) {
		if (Value < ((Ternary) arg).Value) {
			Value = ((Ternary) arg).Value; }
		return this; }

////////////////////////////////////////////////////////////////////////////////
//  Interface IBoole: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	public Boole FalseAt() {
		Value = FALSE;
		return this; }

	/** Boolean NOT Operation in Place: ~=, != for single Bit
	  * @return !a
	  * NOT a = true <=> (a = false)
	  * This Operation cannot be implemented by infinite Sets,
	  * Therefore you need other means to define some Operations.	 */
	public Boole NOTat	() {
		Value = (byte) -Value;
		return this; }

////////////////////////////////////////////////////////////////////////////////
//  Optimizations
////////////////////////////////////////////////////////////////////////////////

	/** Boolean Constant for the Representation of 'false': =0
	  * @return false
	  * Sets this Object to False, i.e. not 'true';
	  * with Vectors it sets all Elements to their respective Value of False*/
	public Boole TrueAt() {
		Value = TRUE;
		return this; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICountAble: Implementation
////////////////////////////////////////////////////////////////////////////

	/** Returns the Object Value represented by an 8 Bit Integer	 */
	public byte	 getByte() { return Value; }

	/**Returns the Object Value represented by a 16 Bit Integer	 */
	public short getShort() { return Value; }

	/**Returns the Object Value represented by a 32 Bit Integer	 */
	public int    getInt() { return Value; }

	/**Returns the Object Value represented by a 64 Bit Integer	 */
	public long  getLong() { return Value; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface IMeasurAble: Implementation
////////////////////////////////////////////////////////////////////////////

	/**Returns the Object Value represented by a scalar Variable of Type double.
	 * It consists of an IEEE Number with 64 Bit (8 Byte):
	 * 52 Bit Mantissa, 11 Bit Exponent, 1 Bit Sign	 */
	public double getDouble() { return Value; }

	/**Returns the Object Value represented by a scalar Variable of Type float.
	 * It consists of an IEEE Number with 32 Bit (4 Byte):
	 * 23 Bit Mantissa, 8 Bit Exponent, 1 Bit Sign	 */
	public float  getFloat() { return Value; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface OrderAble: Implementation
////////////////////////////////////////////////////////////////////////////

	/** less: '<' Returns True, when 'Self' < arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean isLessThan (Object arg) {
		return Value < ((Ternary) arg).Value; }

	/** Sloppy (on Equality) but fast 'between' Implementation
	  * @param arg1 : first  Border to compare to <CODE>this</CODE>
	  * @param arg2 : second Border to compare to <CODE>this</CODE>
	  * @return True, when 'Self' is between arg1 and arg2
	  */
	public boolean isBetween (Object arg1, Object arg2) {
		return
			(((Ternary) arg2).Value <= Value) ==
			(Value <= ((Ternary) arg2).Value); }

	/** greater: '>' Returns True, when 'Self' > arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean isMoreThan (Object arg) {
		return Value > ((Ternary) arg).Value; }

	/** greater or equal: '>=' Returns True, when 'Self' >= arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean notLessThan (Object arg) {
		return Value >= ((Ternary) arg).Value; }

	/** less or equal: '<=' Returns True, when 'Self' <= arg
	 * @param arg  : Object to compare to <CODE>this</CODE>
	 * @return
	 */
	public boolean notMoreThan (Object arg) {
		return Value <= ((Ternary) arg).Value; }

	/** Returns the Position of this Object relative to arg:
	  * This Operation is faster than compareTo.
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return -1 for this smaller than arg, otherwise +1
	  */
	public int Position(Object arg) {
		if (Value >= ((Ternary) arg).Value) { return 1; }
		return -1; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ComparAble: Implementation
////////////////////////////////////////////////////////////////////////////

	/**
	  * @return Sign(this-arg), the exact Position of this Object relative to arg
	  *
	  * @see java.lang.Comparable#compareTo
	  * @see java.util.Comparator#compare
	  * @param arg  : Object to compare to <CODE>this</CODE>
	  * @return
	  * -1 for this smaller than arg,
	  *  0 for this equal   to   arg, otherwise +1
	  */
	public int compareTo(Object arg) {
		Ternary arg_ = (Ternary) arg;
		if (Value > arg_.Value) { return  1; }
		if (Value < arg_.Value) { return -1; }
		return 0; } //least probably Case last for Performance!

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Ternary.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
