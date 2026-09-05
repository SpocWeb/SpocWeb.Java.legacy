package streamIO.copy.group.ring.metric.body.units;

import streamIO.copy.group.ring.metric.body.ABodyDouble;

/**Represents a continuous physical Quantity as a primitive {@code double} value paired with
  * a {@link Unit}.
  *
  * Allows to represent a continuous Quantity by
  * -holding the Value represented as a Double
  * -referencing the Unit
  * -referencing the Base Unit
  * -defining the Conversion to the Base Unit
  *  and from the Base Unit (implicitly)
  *
  * It redefines:
  * -Addition / Subtraction
  * -Multiplication / Division with Scalars and Quantities
  * -Comparison Operations.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-08-13, 02;31;25<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:34:52Z
  * digest: 6810c370e938b2effc400f86eb9ea3816797f698d116cf39a8df8214f1bd289f
  * stale: false
  * tags: [code/si_units, code/unit_conversion]
  * concepts: [Physical Units and Conversion]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class QuantityDouble
extends ABodyDouble
implements Quantity {

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/** Reference to the Unit of this Quantity */
protected Unit mUnit;

////////////////////////////////////////////////////////////////////////////////
//  abstract Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/**Returns this Quantity's value converted into its Base Unit.
 * @return the Quantity in the Base Unit	*/
public double getBaseValue() { return mUnit.Map(value); }

/**Returns this Quantity converted into its Base Unit.
 * @return the Base Quantity with the Base Unit	*/
public Quantity getBaseQuantity() {
	return new QuantityDouble(mUnit.Map(value), (Unit) mUnit.getRoot()); }

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/**Returns the Unit of this Type.
  * @return the Unit of this Type
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
public Unit getUnit() { return mUnit; }

/**Returns the Base Unit that this Quantity's Unit converts to.
  * @return the Base Unit
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
public Unit getBaseUnit() { return (Unit) mUnit.getRoot(); }

/**Returns the raw numeric value in this Quantity's own Unit (not the Base Unit).
 * @return the Value in this Unit */
public double getValue() { return value; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/**Constructor that takes any Object as Input.
 * The Argument is converted to 'double' as the common Type.	 */
//public QuantityDouble(Object arg) { super(arg); }

/**Constructor that takes s String as Input.
 * The Argument is converted to 'double' as the common Type.	 */
//public QuantityDouble(StreamTokenizer arg) throws IOException { super(arg); }

/**Constructor that takes an Object of the same Class as Input(Copy Constructor).
 * Uses the Copy Constructors of the Constituents.	 */
// TODO: LOGIC: the 'unit' parameter is never assigned to mUnit, so every QuantityDouble built
// via this constructor has mUnit == null; any subsequent call to getUnit(), getBaseUnit(),
// getBaseValue() or getBaseQuantity() throws NullPointerException.
public QuantityDouble(ABodyDouble arg, Unit unit) { super(arg); }

/**Constructor that takes 'double' as Input.	 */
// TODO: LOGIC: the 'unit' parameter is never assigned to mUnit, so every QuantityDouble built
// via this constructor has mUnit == null; any subsequent call to getUnit(), getBaseUnit(),
// getBaseValue() or getBaseQuantity() throws NullPointerException.
public QuantityDouble(double arg, Unit unit) { super(arg); }

/**Empty Constructor (for newInstance Method).
 * Does not create Dummy Objects for it's Constituents.
 * So those Objects are not well-defined, but contain Null Pointers.	 */
//public QuantityDouble() { super(); }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws java.io.IOException {
	System.out.println("Testing " + QuantityDouble.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	testIt(args); }

}
