package aspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import knowledge.DirtyFlag;
import streamIO.IInstantiAble;
import synch.InvalidError;
import synch.InvalidException;

/**
  * Title: AAspect<p>
  * Description:
  * Purpose:
  * Base Class for a BO Aspect
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
  * Created on	07-22-2002, 07:23 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public abstract class AAspect
extends DirtyFlag
implements IAspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** Separator Character.
	  * Normally any Character could be used,
	  * '/' and'\' are popular,
	  * but the Name should also be usable as a regular Identifier!
	  */
	final static public char SEP = '_';

	/** Switch to automatically mark an Aspect dirty when being changed */
	public static boolean autoMarkDirty = false;

////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////

	/** String Processing Helper Routine:
	  * @return PropName optionally stripped from left by LocalName
	  */
	final static public String getLocalPropName(String PropName, String LocalName) {
		if (PropName == null) {
			return ""; }
		if (PropName.startsWith(LocalName)) { //strip the Prefix!
			PropName = PropName.substring(LocalName.length() + 1); } //SEP.length()); }
		return PropName; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Variable 'Name' with Accessor Methods
////////////////////////////////////////////////////////////////////////////

	/** holds The Aspect Name == key   */
	protected String Name;

	/** @return The Aspect Name identical to getKey() but typed as String  */
	public String getName() { return Name; }

	/** Sets The Aspect Name. TODO: shouldn't this be an Invariant?  */
//	public void setName(String Name_) { this.Name = Name_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor */
	public AAspect(String Name) {
		this.Name = Name; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICPair, IPair: Implementation
////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @return the key of the Pair */
	public Object getKey() { return Name; }

	/** Accessor Method
	  * @param sets the key of the Pair */
	public void setKey(Object Key) { this.Name = Key.toString(); }

	/** Accessor Method
	  * delegates to copyAt and wraps all Exceptions to be conformant to IPair Interface!
	  * Recursively sets the Value from the given Value to this and all Subordinates.
	  * @param val the Value of this Aspect as an Object
	  * @throws InvalidError instead of InvalidException! */
	final public void setVal(Object val)
		throws InvalidError {
		try {
			CopyAt(val);
		} catch (InvalidException x) {
			throw new InvalidError("in AAspect.setVal", x);
		}
	}

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICPair, IPair: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @return the Default Value of this Aspect as an Object */
	public abstract Object getVal(); //{ return this; }

	/** Accessor Method for writing the Value(s) at this Level!
	  * need not set the Dirty Flag, because done on CopyAt() already!
	  * Could delegate to a typesafe Routine!
	  * called from CopyAt()!
	  * @param sets Value of this Aspect as an Object
	  */
	protected abstract void setValue(Object val) throws InvalidException;

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IAspect: abstract Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tries to fill this Object with the given Value */
//	protected abstract IAspect copyFieldsAt(Object Value);

	/** @return the Default Field for this Object  */
//	protected abstract Object getDefaultField();

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface IAspect: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** (re-)sets the Dirty Flag 	 */
	public void setDirty(boolean dirty_) {
		if (this.dirty == dirty_) { //Optimization
			return; }
		this.dirty = dirty_;
		//recursively go down...
		Field[] fields = getClass().getFields();
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the setDirty Operations!
				field = fields[i];
				if (IAspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					((IAspect) field.get(this)).setDirty(dirty_); }
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
	}

	/** (re-)sets the Dirty Flag 	 */
	public String toString() {
		StringBuffer SB = new StringBuffer();
		SB.append("\n");
		SB.append(getClass().getName());
		SB.append(":\n");
		//recursively go down...
		Field[] fields = getClass().getFields();
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the toString Operations!
				field = fields[i];
				if (IAspect.class.isAssignableFrom(field.getType())) { //Aspect Type => recourse
					SB.append("[");
					SB.append(field.getName());
					SB.append("] = '");
					SB.append(field.get(this));
					SB.append("'\n"); }
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
		return SB.toString(); }


	/** Accessor Method
	  * @param sets Value of this Aspect as an Object
	  * @throws InvalidArgumentException instead of InvalidException! */
/*	final public CopyAble copyAt(Object val) {
		setVal(val);
		return this; }
*/

	/**
	 * This Method is responsible for recursively copying the given Value
	 * into the local Values of this Property.
	 * This is used e.g. on receiving an Update from a Publisher.
	 * All the Rest of the Publication Mechanism is handled automatically!
	 *
	 * Performs a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared public final
	 * and are initialized in the Constructor.
	 *
	 * called from set() via setVal() and externally
	 */
	final public IAspect CopyAt(Object Value) throws InvalidException {
		setValue(Value); //copy the Value(s) at this Level!
		if (autoMarkDirty) { setDirty(true); }
		Class cls = getClass();
		if ((Value != null) &&
			(!cls.isAssignableFrom(Value.getClass()))) { //not an Instance of this Type!
			return this; }	//only copy the Value!
		//copy specific primitive non-public Fields, they are not returned by getFields();
		//this is faster, has more Control, but also creates work for Subclasses
/*		IAspect Value_ = (IAspect) Value;
		this.Name     = Value_.Name    ;
//		this.Parent   = Value_.Parent  ;
		this.Enabled  = Value_.Enabled ;
		this.Required = Value_.Required;
		this.Status   = Value_.Status  ;
		this.Visible  = Value_.Visible ;
*/		Field[] fields = cls.getFields(); //gets all public Fields, also inherited ones!
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				field = fields[i];
				if   (IAspect.class.isAssignableFrom  (field.getType())) { //Aspect Type => recourse
					((IAspect) field.get(this)).CopyAt(Value == null ? null : field.get(Value));
				} else { //all other Fields are shallow copied!
/*					if (0 != (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL))) {
						continue; }
					field.set(this, field.get(Value));
*/				}
			}
		} catch      (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
		return this; }

	/** @return the Aspect denoted by the given Name
	  * Works both with local Names and fully qualified Names!
	  * SubStr is a Monoid and the hierarchical structure matches the Object structure.
	  */
	public IAspect getField(String PropName) { //throws NoSuchFieldException {
		return getLocalField(getLocalPropName(PropName, Name), null); }

	/** @return the Aspect denoted by the given Name
	  * Works both with local Names and fully qualified Names!
	  * SubStr is a Monoid and the hierarchical structure matches the Object structure.
	  */
	protected IAspect getLocalField(String PropName, String[] LocalName) { //throws NoSuchFieldException {
//		PropName = getLocalPropName(PropName); //already normalized!
		if (PropName.length() == 0) {
			return this; }
		//could also strip only Part of the full Name, but that would be too confusing
		//e.g. "Kunde1_Beschaeftigung_Adresse_Strasse" could be accessed at Level Adresse
		// either via this full Name
		// or via "Strasse"
		// or via "Beschaeftigung_Adresse_Strasse"
		// the latter has to check whether "Kunde1_Beschaeftigung_Adresse"
		// has a Substring in common with the given PropName
		// regionMatches() cannot be used for this!
		Class cls = getClass(); //polymorph!
//		Object[] arg = { val };
//		Class [] cls = { Aspect.class };
//		Method method = cls[0].getMethod("set" + PropName, cls); //no Parameters
//			method.invoke(this, arg); //for explicit get/set Methods
		String str;
		int Pos = -1;
		do { //be tolerant to SEP appearing in the Field Name
			Pos = PropName.indexOf(SEP, Pos+1);
			if (Pos < 0) { //Separator not found
				Pos = PropName.length(); }
			str = PropName.substring(0, Pos);
			try {
				Field field = cls.getField(str);
				if (LocalName != null) {
					LocalName[0] = str; }
				return (IAspect) field.get(this); //only Aspects are allowed!
			} catch (NoSuchFieldException   x) { //have to catch it!
			} catch (IllegalAccessException x) {
				throw new IllegalAccessError(x.toString()); }
		} while (Pos < PropName.length());
		return null; }

	/** Accessor Method
	  * @param sets the Value of the named Property
	  * @throws NullPointerException instead of NoSuchFieldException if the Field is not found!
	  */
	final public void set(String PropName, Object val) //Aspect val)
		throws InvalidException { //, NoSuchFieldException, InvocationTargetException { //NoSuchMethodException, IllegalAccessException,
		if ((Name    ==    null    ) ||
			(Name    ==    PropName) ||
			(Name.length() == 0    ) ||
			 Name.endsWith(PropName)) {
//			 Name.equals  (PropName)) {
			setVal(val); return; }
		PropName = getLocalPropName(PropName, Name);
		String[] localName = new String[1];
		IAspect asp = getLocalField(PropName, localName); //only Aspects are allowed!
		int len = localName[0].length() + 1; //SEP.length();
		if (PropName.length() > len) {
			PropName = PropName.substring(len);
		} else {
			PropName = ""; //defect of the substring() Function!
		}
		asp.set(PropName, val); //asp.setValue(val);
	}

	/** Accessor Method
	  * @return the Value of the named Property
	  * @throws NullPointerException instead of NoSuchFieldException if the Field is not found!
	  */
	final public Object //IAspect //
		get(String PropName)
//		throws NoSuchFieldException, InvocationTargetException //NoSuchMethodException, IllegalAccessException
	{
		if ((Name    ==    null    ) ||
			(Name    ==    PropName) ||
			(Name.length() == 0    ) ||
			 Name.endsWith(PropName)) {
//			 Name.equals  (PropName)) {
			return getVal(); }
		PropName = getLocalPropName(PropName, Name);
		String[] localName = new String[1];
		IAspect asp = getLocalField(PropName, localName); //only Aspects are allowed!
		int len = localName[0].length() + 1; //SEP.length();
		if (PropName.length() > len) {
			PropName = PropName.substring(len);
		} else {
			PropName = ""; //defect of the substring() Function!
		}
		return asp.get(PropName); } //asp.setValue(val); }

	/** Creates an uninitalized new Instance of it's class.
	  * When overriding, use newInstance on all Components.	 */
	public IInstantiAble NewInstance() {
		return newInstance(Name); }

	/** @return a new Instance of this Object */
	final public Object newInstance() {
		return newInstance(Name); }

	/** @return a new Instance of this Object */
	public IAspect newInstance(String Name) {
		Class[] cls = { String.class }; //, IHierarchyAspect.class };
		Object[] args = { Name }; //, Parent };
		Constructor cnstr;
		try {
			cnstr = getClass().getConstructor(cls);
			return (IAspect) cnstr.newInstance(args);
		} catch (Exception x) {
			x.printStackTrace();
			throw new NoSuchMethodError(x.toString()); }
	}

	/** @return a deep Copy of this Object */
	final public IAspect Clone(String Name) {
		IAspect ret = (IAspect) newInstance(Name);
		ret.setVal(this);
		return ret; }

	/** @return a deep Copy of this Object
	  * made final to allow for INLINEing  */
	final public Object clone() { return Clone(this.Name); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + AAspect.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (final String[] args) throws Exception {
		testIt(args); }

}

