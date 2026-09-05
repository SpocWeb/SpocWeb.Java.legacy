package aspect;

//import Synch.InvalidException;

/**
  * Title: SimpleAspect<p>
  * Description:
  * Base Class for composite "container" Aspects that hold no Value of
  * their own (getVal() returns this, setPrimVal()/validatePrimVal() are
  * no-ops) and simply group public Aspect-typed fields, e.g. AddressAspect
  * and PersonAspect.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-02-2001, 08:02 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:25:22Z
  * digest: 6b9e90a4b530a44147529d675b95d5d63473312938cb7e2b238aaf7eeba3b9a5
  * stale: false
  * tags: [code/composite_pattern, code/domain_model]
  * concepts: [Composite Aspect, Attribute Modelling]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
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

