package streamIO.copy.group.ring.metric.body.units;

import streamIO.copy.group.ring.metric.IMetricIRing;
import function.IMeasurAble;

/** Quantities are Values of a Dimension
  * quantified by a Unit.
  * This makes it possible to apply Group Operations on them:
  * Multiplication with a Scalar
  * Multiplication with a Quantity resulting in a different Unit.
  * Addition / Subtraction with a Quantity of this Dimension
  * (with a possible Distinction between absolute and relative Values:
  *  absolute +/- relative = absolute
  *  relative +/- relative = relative
  *  absolute   - absolute = relative
  *  absolute +   absolute is not defined!
  *  )
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:34:30Z
  * digest: 4d7d35ba225a41aa281e8e39d161eb705a02ebc638110c8be3229dd6f52243b1
  * stale: false
  * tags: [code/si_units, code/unit_conversion]
  * concepts: [Physical Units and Conversion]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public interface Quantity
extends IMetricIRing, IMeasurAble { //Unit {

/** @return the Value in this Unit */
//double getValue(); // instead use getDouble()

/**Returns the Unit of this Type.
  * @return the Unit of this Type
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
Unit getUnit(); // { return Dimension; }

/** @return the Unit of this Type
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
//Unit getBaseUnit(); //instead use getUnit().getBaseUnit()

/** @return the Base Quantity with the Base Unit	*/
//Quantity getBaseValue(); //instead use getUnit().Map(getValue)

/**Returns this Quantity converted into its Base Unit.
  * @return the Base Quantity with the Base Unit and the converted Value	*/
Quantity getBaseQuantity();

}
