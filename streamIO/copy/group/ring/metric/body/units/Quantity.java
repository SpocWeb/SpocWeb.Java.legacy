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
  */
public interface Quantity
extends IMetricIRing, IMeasurAble { //Unit {

/** @return the Value in this Unit */
//double getValue(); // instead use getDouble()

/** @return the Unit of this Type
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
Unit getUnit(); // { return Dimension; }

/** @return the Unit of this Type
  * This allows to find out whether two Types can be converted.
  * This defines an Equivalence Relation to a Base Element */
//Unit getBaseUnit(); //instead use getUnit().getBaseUnit()

/** @return the Base Quantity with the Base Unit	*/
//Quantity getBaseValue(); //instead use getUnit().Map(getValue)

/** @return the Base Quantity with the Base Unit and the converted Value	*/
Quantity getBaseQuantity();

}
