package structure; //

import java.security.InvalidParameterException;

import function.AOrderAble;

/**
  * Demonstrates encoding an Enumeration of Elements as a bounded {@code short} Value, meant
  * to be subclassed per Enum Type rather than instantiated on its own.
  *
  * Demonstrates the Implementation of a Class encoding an Enumeration of Elements.
  * This is not a good Example, because you are not forced to create your own Type,
  * but that can be easily enforced by making this Class abstract
  * or the Constructor protected.
  * Some Effort has been put into creating Enums easily by just giving them a Range.
  *
  * To create your Enum, subclass this Class and initialize it with an Array of int Values
  *
  * Enumerations are a common Means to structure and describe limited Size Sets.
  * They are also used as States in a State Machine
  * and to define Parameters for limited State Methods.
  * A third Application is the Flyweight / Singleton Pattern,
  * where a limited Size Set can considerably conserve Memory!
  *
  * The actual Value and Order Relation of an Enum is usually not important,
  * only the Fact that it can be used in a switch () Statement.
  * For Enums denoting discrete Sections of a Dimension like Months, Hours etc.
  * the Order Relation is important, but only within a Period!
  *
  * Design Decisions:
  * Using short on purpose to force Users to cast or use predefined Constants!
  * Making the Value writeAble by adding a setValue() Method results in:
  * * allowing to hand back a Result ByRef, although also an Array could do that!
  * * non constant-ness which opens up Complexities in Algorithms and Concurrency!
  * * requires a Runtime Check on Values in setValue()
  * * replaces fast Identity Check with slower equals Method.
  * * Exactly for Enums with its fixed Set of Members (unlike Strings)
  *   Constant Members are ideal! They implement the Flyweight Pattern!
  * * The Flyweight Pattern also saves Memory
  *   as well as expensive Creation and Destruction of Objects.
  *
  * Known SubClasses:
  * @see also Tools.Enum which defines Enums as Flyweights,
  * which saves Calculation and Memory.
  *
  * Similar Classes:
  * @see function.Derive.Enum for a full fledged Implementation.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-06-14, 01;52;47<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:15:26Z
  * digest: 33f3bbc0d268b4189cd1374577f022ca402a154b5d08c757191d4f131d35343b
  * stale: false
  * tags: [code/enum_like_type]
  * concepts: [Type-Safe Enum Emulation]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Enum
extends AOrderAble {

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

/** sets the Value and returns the previous Value
  * This makes the Method more useful and is only little Overhead
  * because the Call Overhead weighs much heavier!
  * But making the Enum read / write opens up Pandora's box for Runtime Errors!
  * @param self 'this' is passed down
  */
protected static final short setValue(Enum self, short MinValue, short newValue, short MaxValue) {
	if ((newValue <  MinValue) ||
		(newValue >= MaxValue)) {
		throw new InvalidParameterException(newValue + " should be between " + MinValue + " and " + MaxValue); }
	short ret = self.Value; self.Value  = newValue;
	return ret; }

////////////////////////////////////////////////////////////////////////////////
//  static Constants
////////////////////////////////////////////////////////////////////////////////

/** Constant denoting the FALSE Value in ternary Logic	 */
final static public short FALSE = -1;

/** Constant denoting the undefined UNDEF Value in ternary Logic
  * This is a fundamental State denoting Contradiction
  * or just Meaninglessness of the Criterion or Question	 */
final static public short UNDEF = 0;

/** Constant denoting the TRUE Value in ternary Logic	 */
final static public short TRUE = 1;

/**
 * Constant listing all Value in ternary Logic.
 * Problem: a public List would allow Modification!
 */
final static public short[] TERNARY = {FALSE, UNDEF, TRUE};

/** Constant denoting the FALSE Value in ternary Logic	 */
final static public Enum False = new Enum(FALSE, FALSE, TRUE);

/** Constant denoting the undefined UNDEF Value in ternary Logic
  * This is a fundamental State denoting Contradiction
  * or just Meaninglessness of the Criterion or Question	 */
final static public Enum UnDef = new Enum(FALSE, UNDEF, TRUE);

/** Constant denoting the TRUE Value in ternary Logic	 */
final static public Enum True = new Enum(FALSE, TRUE, TRUE);

/** Constant listing all Value in ternary Logic	 */
final static public Enum[] Ternary = {False, UnDef, True};

////////////////////////////////////////////////////////////////////////////////
//  static Variables
////////////////////////////////////////////////////////////////////////////////

//Define static Arrays for each Enum Type.
//Even better: define a new Class for each Enum Type,
//because that reduces the Risk of handing over a self defined Enum!
//This is facilitated by making all Constructors protected!
//Thus you HAVE TO subclass Enum to define new Enums.
//This is the only way to utilize Java Runtime Type Safety for both
// Compile Time and Runtime.
//On Subclassing you can define MinValue and MaxValue as static
// and thus save some Bytes when creating many Objects.

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

/** Minimum Value of this Object	 */
protected short MinValue = 0;

/** Maximum Value of this Object
  * Making is equal to MinValue enforces the Redefinition of this Variable	 */
protected short MaxValue = 0;

/** Actual Value of this Object	 */
protected short Value;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** Returns the lower Bound this Enum's Value is validated against.
  * @return the Minimum Value of this Object	 */
public short getMinValue() { return MinValue; }

/** Returns the upper Bound (exclusive) this Enum's Value is validated against.
  * @return the Maximum Value of this Object	 */
public short getMaxValue() { return MaxValue; }

/** Returns the current Value for this Enum */
public short getValue() { return Value; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/** Initializing Constructor	 */
public Enum(short MinValue, short newValue, short MaxValue) {
	this.MinValue = MinValue;
	this.MaxValue = MaxValue;
	setValue(this, MinValue, newValue, MaxValue); }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + Enum.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}

/** Allows to change the Value within the given Bounds.
  * Useful for ByRef Handover, although you can also use Arrays for that!
  * Opens up dangerous Possibilities like:
  * * modifying inner Values during Algorithms (single- or multithreaded)
  * * requiring Tests by Value instead of by Address / Identity
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:15:26Z
  * digest: 5382b65cad5ba4788242aafdb29305cb76516a799117e5f7ada2c05f609e1b93
  * stale: false
  * tags: [code/enum_like_type]
  * concepts: [Variable Enum Emulation]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
class VarEnum
extends  Enum {

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** Sets the Value for this Enum. This is THE dangerous Method opening up Pandora's Box!  */
public short setValue(short newValue) {
	return setValue(this, MinValue, newValue, MaxValue); }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/** Initializing Constructor	 */
public VarEnum(short MinValue, short newValue, short MaxValue) {
	super(MinValue, newValue, MaxValue); }

}

/** A three-valued {@link Enum} fixed to the {@link #FALSE}..{@link #TRUE} Range, used for
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:15:26Z
  * digest: 2949df58732117d60d6680af6c58c138b39fb82d1e652b0cf73056b432fd54cd
  * stale: false
  * tags: [code/ternary_logic]
  * concepts: [Ternary Logic Value]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  * ternary Logic Values. */
class Ternary
extends Enum {

/** Initializing Constructor	 */
public Ternary(short newValue) { super(FALSE, newValue, TRUE); }


}
