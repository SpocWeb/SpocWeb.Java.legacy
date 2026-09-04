package streamIO.copy.group.ring.metric.body.units;

import streamIO.copy.group.ring.metric.body.ABodyDouble;

/**
  * Title: QuantityDouble<p>
  * Description:
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

/** @return the Quantity in the Base Unit	*/
public double getBaseValue() { return mUnit.Map(value); }

/** @return the Base Quantity with the Base Unit	*/
public Quantity getBaseQuantity() {
	return new QuantityDouble(mUnit.Map(value), (Unit) mUnit.getRoot()); }

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

/** @return the Unit of this Type
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
public Unit getUnit() { return mUnit; }

/** @return the Base Unit
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
public Unit getBaseUnit() { return (Unit) mUnit.getRoot(); }

/** @return the Value in this Unit */
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
public QuantityDouble(ABodyDouble arg, Unit unit) { super(arg); }

/**Constructor that takes 'double' as Input.	 */
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
