package aspect;

//import Synch.InvalidException;

/**
  * Title: SimpleAspect<p>
  * Description:
  * Purpose:
  * Base Class for all Container Aspects without Validation
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-02-2001, 08:02 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class SimpleAspect
extends AHierarchyAspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	protected SimpleAspect(String Name, IHierarchyAspect Parent) { super(Name, Parent); }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AHierarchyAspect: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent AHierarchyAspect: Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @return the Default Value of this Aspect as an Object */
	public Object getVal() { return this; }

	/** Accessor Method for writing the Value(s) at this Level!
	  * need not set the Dirty Flag, because done on CopyAt() already!
	  * Could delegate to a typesafe Routine!
	  * called from CopyAt()!
	  * performs ValidateParent before and updateParent afterwards
	  * @param sets Value of this Aspect as an Object
	  */
	protected void setPrimVal(Object val) { }

	/** Local Validation Routine to validate multifield Checks
	  * Called both from validate() Child and validateParent() Validation!
	  */
	protected void validatePrimVal(Object Value) {} //Source, Object Value, Object oldVal)
//		throws InvalidException { ; }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

}

