package aspect;

import java.lang.reflect.Field;

import synch.InvalidException;

/**
  * Title: AHierarchyAspect<p>
  * Description:
  * Abstract Base Implementation of a HierarchyAspect
  * * it can have 'Parent' Aspects and inherits the Prefix from them
  * * validate and propagate Changes   upward to the Parents  (Multi Field Plausis).
  * * validate and propagate Changes downward to the Children (One   Field Plausis).
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-22-2002, 09:11 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:42Z
  * digest: 834fd581e4836929d6c2f3023550943914b86cdbc4fafb0609b7075521d3ad1a
  * stale: false
  * tags: [code/domain_model, code/hierarchy]
  * concepts: [Aspect Framework, Reflection-Based Dirty Tracking]
  * facets: {layer: domain, status: stable, complexity: high}
  * -->
  */
public abstract class AHierarchyAspect
extends AAspect
implements IHierarchyAspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

//	protected ISubscriber subscriber;
//	protected IValidator validator;

///////////////////////////////////////////////////////////////////////////////////
/// Meta Properties...
///////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Parent Aspect
	  * This creates a bidirectional navigable Relation,
	  * but in GC Languages this poses no Problem.
	  */
	protected IHierarchyAspect Parent;

	/** Returns this Aspect's Parent, if any.
	  * @return the Parent Aspect this Aspect is nested under, or null if this Aspect is a root  */
	public IHierarchyAspect getParent() { return Parent; }

	/** Sets The Aspect Name. TODO: shouldn't this be an Invariant?  */
//	public void setParent(IAspect Parent_) { this.Parent = Parent_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	protected AHierarchyAspect(String Name, IHierarchyAspect Parent) {
		super(Name);
		this.Parent = Parent;
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent : abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Accessor Method for writing the Value(s) at this Level!
	  * need not set the Dirty Flag, because done on CopyAt() already!
	  * Could delegate to a typesafe Routine!
	  * called from CopyAt()!
	  * performs ValidateParent before and updateParent afterwards
	  * @param sets Value of this Aspect as an Object
	  */
	protected abstract void setPrimVal(Object val); // throws InvalidException;

	/** Accessor Method for writing the Value(s) at this Level!
	  * need not set the Dirty Flag, because done on CopyAt() already!
	  * Could delegate to a typesafe Routine!
	  * called from CopyAt()!
	  * performs ValidateParent before and updateParent afterwards
	  * @param sets Value of this Aspect as an Object
	  */
	protected void setValue(Object val) throws InvalidException {
		Object oldVal = null;
		if (!autoMarkDirty) {
			if (
//				( validator != null) ||
//				(subscriber != null) ||
				(    Parent != null)) {
				oldVal = getVal(); }
				validatePrimVal(val); //this, val, null); //oldVal); //first test locally! knows its old Value!
//			if (( validator != null))  { //validate before
//				  validator.validate   (this, val, oldVal); } //oldVal); }
			if (( Parent    != null))  { //validate before
				  Parent.validateParent(this, val, oldVal); } //oldVal); }
		}
		setPrimVal(val);
		if (!autoMarkDirty) { //notify all Clients immediately
//			if ((subscriber != null)) { //update before
//				 subscriber.update    (this, val, oldVal); } //oldVal); }
			if (( Parent    != null)) { //update before
				  Parent.updateParent (this, val, oldVal); } //oldVal); }
		} //this will result in Bulk Updates!
	}

	/** (re-)sets the Dirty Flag 	 */
	public void setDirty(boolean dirty_) {
		super.setDirty(dirty_);
		if (!dirty_) {
			return;  }
		AHierarchyAspect par = this; //TODO: move this to a protected Routine!
		while (par != null) {
			par.dirty = true;
			par = (AHierarchyAspect) par.Parent; }
	}

	/** recursively Bulk validate all Validators on the current new Values	 */
	final public void validate() throws InvalidException {
		if (!dirty)  {
			return; }
		validatePrimVal(this); //, this, null); //first test locally!
//		if (subscriber != null) {
//			subscriber.validate(this, this, null); }
//		if (Parent != null) {
//			Parent.validate(); }
		Field[] fields = getClass().getFields();
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				field = fields[i];
				if   (IHierarchyAspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((IHierarchyAspect) field.get(this)).validate();
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}


	/**
	 * recursively Bulk update all Subscribers on the current new Values
	 */
	final public void update() {
		if (!dirty)  {
			return; }
//		if (subscriber != null) {
//			subscriber.update(this, this, null); }
//		if (Parent != null) {
//			Parent.update(); }
		Field[] fields = getClass().getFields();
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				field = fields[i];
				if   (IHierarchyAspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((IHierarchyAspect) field.get(this)).update();
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
		dirty = false; } //Recursion already performed!

////////////////////////////////////////////////////////////////////////////////
/// #region : Parent : Implementation / Overrides
////////////////////////////////////////////////////////////////////////////////

	/**
	 * This is be the Interface for a Subscriber that can veto the Change
	 *
	 * Design Decisions:
	 * To enforce the Creation of an individual Method for Validation
	 * of a specific named Aspect Reflection could be used:
	 * search for validate<Name>(Object Source, Aspect Value, Aspect oldVal)
	 * catch and examine any thrown Exception and convert it to InvalidException.
	 * But Reflection is not safe, because the Method could simply not be found.
	 * Attributes like in C# are not possible in Java.
	 *
	 * It is useful to separate Child Aspects from regular Subscribers
	 * thus Child Aspects are not added as Validators or Subscribers
	 * but are validating explicitly!
	 */
	final public void validate(Object Source, Object Value, Object oldVal)
		throws InvalidException {
		validatePrimVal(Value); //Source, Value, oldVal); //first test locally!
//		if (validator != null) { //this should be moved into validatePrimVal()!
//			validator.validate      (Source, Value, oldVal); }
		//Values should only validated by the Objects with according Name!
		//on the other Hand, these Checks have then to be programmed
		//into the the Objects or their Parents / Owners
		//If you want to add Checks, you  have to subclass the Person Object,
		//add your Checks and instantiate it.
		Field[] fields = getClass().getFields();
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				Field field = fields[i];
				if   (IHierarchyAspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((IHierarchyAspect) field.get(this)).validate(Source, Value, oldVal);
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}

	/**
	 * Callback used to recursively update all Subscribers on a new Value
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	final public void update(Object Source, Object Value, Object oldVal) {
		//Validation should not be necessary,
		//because validate should have been called before!
//		myUpdate(Source, Value, oldVal);
		IAspect Source_ = (IAspect) Source; //convention is to transfer the Aspect in Source
		if (Source_.getName().equals(Name)) { //and the Values in Value and oldVal!
			setVal(Value); }
//		if (subscriber != null) {
//			subscriber.update(Source, Value, oldVal); }
		Field[] fields = getClass().getFields();
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				Field field = fields[i];
				// TODO: LOGIC: type check uses IAspect.class but the result is cast to IHierarchyAspect;
				// a public field typed as a plain (non-hierarchy) IAspect would pass the check and then
				// throw ClassCastException here. Other methods in this class (validate(), the no-arg
				// update()) consistently check IHierarchyAspect.class instead - this one does not.
				if (IAspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((IHierarchyAspect) field.get(this)).update(Source, Value, oldVal);
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IAspect: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Local Validation Routine to validate multifield Checks
	  * Called both from validate() Child and validateParent() Validation!
	  */
	protected abstract void validatePrimVal(Object Value) //Source, Object Value, Object oldVal)
		throws InvalidException;

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IAspect: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Local Validation Routine to validate multifield Checks */
	public void validateParent(Object Source, Object Value, Object oldVal)
		throws InvalidException {
		validatePrimVal(Value); //Source, Value, oldVal);
//		if (validator != null) { validator.validate      (Source, Value, oldVal); }
		if (Parent    != null) {    Parent.validateParent(Source, Value, oldVal); }
	}

	/**
	 * Callback used to update all Subscribers
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	public void updateParent(Object Source, Object Value, Object oldVal) {
//		if (subscriber != null) { subscriber.update      (Source, Value, oldVal); }
		if (Parent     != null) {     Parent.updateParent(Source, Value, oldVal); }
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AHierarchyAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

