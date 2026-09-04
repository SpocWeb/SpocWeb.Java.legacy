package synch.aspect;

import graphs.IPair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Vector;

import synch.AConstrained;
import synch.InvalidException;

/**
 * The Instances of this Class have a name, so it can serialize and deserialize itself
 * into e.g. a Properties File or a HashMap or a SessionContext.
 *
 * An Aspect is a Value Object and consists of Value Objects.
 * Thus is performs a deep Copy in the clone() Method!
 *
 * An Aspect could be realized as an untyped Object or (only slightly better) a String,
 * but typed Subclasses are better to handle!
 *
 * Composed Objects can also be Aspects,
 * but they don't have a single Value except for themselves.
 * Instead they have a Collection of Values.
 * They have all Meta Properties of regular Attributes.
 *
 * Using explicit Members is possible when dealing with fixed Cardinality Relations.
 * Advantages of explicit Members are:
 * -IDE Support
 * -Type Safety and other Compile Time Checks.
 *
 * Actually for parsing the Names and propagating update() and validate()
 * it is not necessary to implement the Publisher and Subscriber Interfaces!
 * It is sufficient to implement the Subscriber and Validator Interfaces!
 */
public abstract class Aspect
	extends AConstrained
	implements IPair, Cloneable {

////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////

	/** Constant Separator Character  */
	final static public String SEP = "_";

////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////

	/** @return all public, protected and private Fields
	  * declared in this Class and up to the given Parent Class.
	  * This is a quite useful Method to (de-)serialize an Object.
	  */
	public static Vector getAllFields(Object Instance, Class upToThisClass) {
		Vector fields = new Vector();
		if (upToThisClass == null) {
			upToThisClass  = Object.class; }
		if (upToThisClass.isInterface()) {
			throw new IllegalArgumentException(upToThisClass + " must be a Class, not an Interface!"); }
		Class cls = Instance.getClass();
		if (!upToThisClass.isAssignableFrom(cls)) {
			throw new IllegalArgumentException(upToThisClass + " must be a Parent Class of " + cls); }
		do {
			Field[] currFields = cls.getDeclaredFields();
			int i = currFields.length;
			while (--i >= 0) {
				fields.add(currFields[i]); }
		} while (upToThisClass != (cls = cls.getSuperclass()));
		return fields; }
/*		Field[] ret = new Field[fields.size()];
		fields.copyInto(ret);
		return ret;	}
*/

///////////////////////////////////////////////////////////////////////////////////
/// Meta Properties...
///////////////////////////////////////////////////////////////////////////////////

	/** Reference to the Parent Aspect
	  * This creates a bidirectional navigable Relation,
	  * but in GC Languages this poses no Problem.
	  */
	protected Aspect Parent;

	/** @return The Aspect Parent  */
	public Aspect getParent() { return Parent; }

	/** Sets The Aspect Name. TODO: shouldn't this be an Invariant?  */
//	public void setName(Aspect Parent_) { this.Parent = Parent_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	protected Aspect(String Name, Aspect Parent) {
		this.Parent = Parent;
		this.Name = Name;
		//also all Children are Subscribers and Validators...
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Name' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** holds The Aspect Name   */
	protected String Name;

	/** @return The Aspect Name  */
	public String getName() { return Name; }

	/** Sets The Aspect Name. TODO: shouldn't this be an Invariant?  */
//	public void setName(String Name_) { this.Name = Name_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Enabled' with Accessor Methods
/// possibly use a Formula referring to other Aspects, instead of a boolean Flag
////////////////////////////////////////////////////////////////////////////

	/** holds the Flag whether the Control editable   */
	protected boolean Enabled;

	/** @return the Flag whether the Control editable  */
	public boolean getEnabled() {
		return Enabled; }

	/** Sets the Flag whether the Control editable  */
	public void setEnabled(boolean Enabled_) {
		this.Enabled = Enabled_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Required' with Accessor Methods
/// possibly use a Formula referring to other Aspects, instead of a boolean Flag
////////////////////////////////////////////////////////////////////////////

	/** holds the Control required to enter Data  */
	protected boolean Required;

	/** @return the Control required to enter Data  */
	public boolean getRequired() {
		return Required; }

	/** Sets the Control required to enter Data  */
	public void setRequired(boolean Required_) {
		this.Required = Required_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Visible' with Accessor Methods
/// Making a Control (in-)visible is probably unintuitive to the User!
/// possibly use a Formula referring to other Aspects, instead of a boolean Flag
////////////////////////////////////////////////////////////////////////////

	/** holds the Control visible to the User   */
	protected boolean Visible;

	/** @return the Control visible to the User  */
	public boolean getVisible() {
		return Visible; }

	/** Sets the Control visible to the User  */
	public void setVisible(boolean Visible_) {
		this.Visible = Visible_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Error' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** holds the Error Status for this Aspect   */
	protected int Status;

	/** @return the Error Status for this Aspect
	 *   0 when the Value is filled / valid / initialized,
	 *  -1 when the Value is not filled / empty / initialized,
	 *  otherwise the (positive) Error Number
	 */
	public int getStatus() {
		return Status; }

	/** Sets the Status for this Aspect  */
	public void setStatus(int Status_) {
		this.Status = Status_; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICPair, IPair: Implementation
////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @return the key of the Pair */
	public Object getKey() { return Name; }

	/** Accessor Method
	  * @param sets the key of the Pair */
	public void setKey(Object Key) { this.Name = Key.toString(); }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICPair, IPair: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @return the Value of this Aspect as an Object */
	public abstract Object getVal();

	/** Accessor Method
	  * @param sets Value of this Aspect as an Object */
	public abstract void setValue(Object val) throws InvalidException;

	/** Accessor Method
	  * @param sets Value of this Aspect as an Object
	  * @throws InvalidArgumentException instead of InvalidException! */
	final public void setVal(Object val) {
		try {
			setValue(val);
		} catch (InvalidException x) {
			throw new IllegalArgumentException(x.toString());
		}
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/** This Method has to copy Value and other Properties from the given Value
	  * It should be able to copy from primitive Wrappers
	  * as well as from equivalent primitive Aspects!
	  * Derived Classes should copy their own Fields
	  * and call super.copyFieldsAt(Value);
	  */
	protected abstract void copyFieldsAt(Object Value);

	/**
	 * This Method is responsible for copying the given Value
	 * into the local Value of this Property.
	 * This is used e.g. on receiving an Update from a Publisher.
	 * All the Rest of the Publication Mechanism is handled automatically!
	 *
	 * Performs a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared public final
	 * and are initialized in the Constructor.
	 *
	 * cannot be declared final, because the primitive Aspects have to define their own copyAt() Method!?
	 */
	protected Aspect copyAt(Object Value) {
		copyFieldsAt(Value); //copy the Value
		if (!getClass().isInstance(Value)) { //not an Instance of this Type!
			return this; }	//only copy the Value!
		Aspect Value_ = (Aspect) Value;
		//copy specific primitive non-public Fields, they are not returned by getFields();
		//this is faster, has more Control, but also creates work for Subclasses
//		this.Name     = Value_.Name    ;
//		this.Parent   = Value_.Parent  ;
		this.Enabled  = Value_.Enabled ;
		this.Required = Value_.Required;
		this.Status   = Value_.Status  ;
		this.Visible  = Value_.Visible ;
		Field[] fields = getClass().getFields(); //gets all public Fields, also inherited ones!
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				Field field = fields[i];
				if (Aspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((Aspect) field.get(this)).copyAt(field.get(Value));
				} else { //all other Fields are shallow copied!
/*					if (0 != (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL))) {
						continue; }
					field.set(this, field.get(Value));
*/				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
		return this; }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface Aspect: Implementation
////////////////////////////////////////////////////////////////////////////

	/** Recursively clears all Aspects and Subaspects */
	public void clear() {
		Field[] fields = getClass().getFields();
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the clear Operations!
				Field field = fields[i];
				if (Aspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((Aspect) field.get(this)).clear();
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}

	/** @return a deep Copy of this Object */
	final public Aspect newInstance(String Name) {
		Class[] cls = { String.class, Aspect.class };
		Object[] args = { Name, Parent };
		Constructor cnstr;
		try {
			cnstr = getClass().getConstructor(cls);
			return (Aspect) cnstr.newInstance(args);
		} catch (Exception x) {
			x.printStackTrace();
			throw new NoSuchMethodError(x.toString()); }
	}


	/** @return a deep Copy of this Object */
	final public Aspect Clone(String Name) {
		return newInstance(Name).copyAt(this); }
/*		Aspect ret;
		try {
			ret = (Aspect) super.clone(); //shallow Copy, leaves all References!
		} catch (CloneNotSupportedException x) {
			return null; } //should never happen!
		ret.copyAt();
		//cannot exchange the References with inner Clones, because const!
		//don't want to work with get/set Pairs for each Field either!
		return ret; }
*/

	/** @return a deep Copy of this Object
	  * made final to allow for INLINEing  */
	final public Object clone() { return Clone(this.Name); }

	/**
	 * Callback used to update all Subscribers
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	protected final void updateParent(Object Source, Object Value, Object oldVal) {
		if (subscriber != null) { subscriber.update      (Source, Value, oldVal); }
		if (Parent     != null) {     Parent.updateParent(Source, Value, oldVal); }
	}

	/**
	 * Callback used to update all Subscribers
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	final public void update(Object Source, Object Value, Object oldVal) {
		//Validation should not be necessary,
		//because validate should have been called before!
//		myUpdate(Source, Value, oldVal);
		Aspect Source_ = (Aspect) Source; //convention is to transfer the Aspect in Source
		if (Source_.Name.equals(Name)) { //and the Values in Value and oldVal!
			copyAt(Value); }
		if (subscriber != null) {
			subscriber.update(Source, Value, oldVal); }
		Field[] fields = getClass().getFields();
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				Field field = fields[i];
				if (Aspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((Aspect) field.get(this)).update(Source, Value, oldVal);
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}

	/** Local Validation Routine to validate multifield Checks */
	protected final void validateParent(Object Source, Object Value, Object oldVal)
		throws InvalidException {
		myValidate(Source, Value, oldVal);
		if (validator != null) { validator.validate      (Source, Value, oldVal); }
		if (Parent    != null) {    Parent.validateParent(Source, Value, oldVal); }
	}

	/** Local Validation Routine to validate multifield Checks
	  * Called both from validate() Child and validateParent() Validation!
	  */
	protected abstract void myValidate(Object Source, Object Value, Object oldVal)
		throws InvalidException;

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
		myValidate(Source, Value, oldVal);
		if (validator != null) {
			validator.validate      (Source, Value, oldVal); }
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
				if (Aspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((Aspect) field.get(this)).validate(Source, Value, oldVal);
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}

	/**Writes the Value out to the streamIO	 */
//	public OutputStream toStream(OutputStream str) { return stream; }

	/**Reads the Value from the streamIO	 */
//	public InputStream fromStreamAt(InputStream stream) { return stream; }

}
