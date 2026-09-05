package streamIO.copy.group.ring.metric.body.units;

import java.io.IOException;
/**A {@link Unit} that converts to its Base Unit by a pure multiplicative ratio (no offset),
  * e.g. kilometers to meters.
  *
  * Defines the Interface for a Unit with linear Ratio to the Base Unit.
  * A Unit defines the Metric and Norm for a Parameter Space
  * by defining the Equivalent of 0 and 1
  * thus allowing for Ring Operations.
  *
  * A Dimension is the Equivalence Class of all convertable Units.
  * Whether two Units are convertable is determined by the Equivalence
  * of their Base Units.
  *
  * A primitive Dimension/Unit is a Dimension/Unit
  * that cannot be expressed indirectly using other Units.
  * Examples are:
  * 	Length in Meter
  * 	Time   in Second
  * 	Mass   in KiloGram
  * 	Charge in Coulomb
  *
  * Base Units are modeled by Prime Numbers.
  * Derived Dimensions are expressed by non Prime Numbers and Fractions.
  * This allows for incorporating Dimension/Unit into the Calculation.
  *
  * Units of the same Base Unit / Dimension can be converted
  * (usually by an affine Transformation) using the following Methods:
  * getBaseQuantity
  * getBaseValue
  * getBaseUnit
  *
  * The Combination of Unit and Dimension allows for typesafe Conversions and Aggregations.
  * Scalar Types typically have a continuous Range, so the Type float is used here.
  * The Conversion between monetary Values can vary over Time,
  * so the Conversion may be time dependant!
  *
  * Unit-Systems:
  * SI  (System International)
  * MKS (Meter/KiloGram/Second)
  * CGS (CentiMeter/Gram/Second)
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-08-13, 02;34;24<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:35:13Z
  * digest: 81a89862da43f67427af9d9ba1edbeab4f9f60b558d60345abd47c00bbd5c06a
  * stale: false
  * tags: [code/si_units, code/unit_conversion]
  * concepts: [Physical Units and Conversion]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class UnitLinear
extends Unit {

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

/** The Ratio to the Base Unit */
protected double mRatio;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

/** Empty Constructor	 */
public UnitLinear(double Ratio) {
	mRatio = Ratio; }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface ILinked: Implementation
////////////////////////////////////////////////////////////////////////////////

/**Returns the Function Value (mapping) of the Argument arg */
public double Map(double arg) {
	if (mBaseUnit == this) return arg;
	return mBaseUnit.Map(arg*mRatio); }

/**Returns the Function Value (mapping) of the Argument arg */
public float Map(float arg) {
	if (mBaseUnit == this) return arg;
	return mBaseUnit.Map((float) (arg*mRatio)); }

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) throws IOException {
	System.out.println("Testing " + UnitLinear.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws IOException {
	testIt(args); }

}
