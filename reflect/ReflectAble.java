package reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import knowledge.IDirtyFlag;
import streamIO.exception.BaseException;
import synch.IPublisher;
import synch.InvalidError;
import synch.InvalidException;
import synch.UniCastConstrained;
import tester.ITester;

/**
  * Title: ReflectAble<p>
  * Description:
  * Purpose:
  * Implements the basic Functionality for IReflectAble.
  * Additionally provides Convenience Methods for Reflection.
  * Opens up Reflection with Instance Methods of this Instance.
  *
  * Relations can be traversed by Name
  * All Relations and Fields can be listed.
  * Should be extended to also support Arrays and Containers
  * but not as native Classes but in their Role as Multiplicities.
  *
  * Accesses Fields and Methods uniformly by Name and Parameters only!
  * Also allows to use nested Names for reading and writing nested Properties
  * thus navigating along an Object Network!
  * Can be used for Smalltalk-like Programming by sending Messages.
  * Runtime Exceptions due to missing Methods can be caught and suppressed.
  *
  * Additionally Inference Rules can be formulated
  * that allow to recursively traverse, explore and extend the Object Network,
  * thus revealing new Results.
  * The Network stays passive and does not show Behavior,
  * but the Type of Object is still important for the Applicability of the Rule.
  *
  * Change Events can only be sent consistently when using the set Method exclusively.
  * This can be enforced by handing out only the ReflectAble Interface.
  * For internal handling though you would use Properties directly
  * and thus could not raise Events!
  * Instead you can set the dirty Flag manually, creating Bulk Transactions.
  * Also propagating the Event upward does not work, because
  * a) the Element does not know its Field Name
  * b) it cannot acquire the Field Name on propagating up.
  *
  * The Subordinate Objects and Properties are not aware of their Member Names.
  * This means that they...
  * * could be shared by different Objects (Objects only)
  * * cannot react to global Events unless these are prefiltered by the Parent Object!
  *   This leads to the Fact that only superordinate Objects can
  *   * validate Data, which is correct.
  *   * react to Post Methods within their Data which enforces Encapsulation!
  *   * Object will stay agnostic about any other Objects that are not their Members.
  *   * two Objects can not react to each other! It is either A>B or B>A.
  *     This can be relieved by introducing Pointers to either the Parent,
  *     or to other Objects, but then Casting becomes necessary, but Events don't propagate!
  * * Direct Field Access is possible and does not raise any Events!
  *   When using the set Method, you can only raise anonymous Events (no Name)!
  * * when propagating Events upward, these Names can be provided,
  *   but only by sending 'this' and let the Parent compare it to any of its Fields (expensive!).
  *
  * Design Decisions / Implementation Details:
  * This Implementation is very slow, because
  * Reflection is slow in its own and
  * because even the Parameter Types and Methods
  * are determined and wrapped dynamically!
  * The most probable Number of Parameters are implemented directly,
  * since you cannot append Parameters dynamically in Java!
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-24-2002, 09:29 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:32Z
  * digest: d321c6bd5dd918d718134ae02eb7aa430164240d1684b8419ef0659560743340
  * stale: false
  * tags: [code/reflection, code/reflection_based_property_access, code/reflection_object_instantiation]
  * concepts: [Reflection]
  * facets: {layer: domain, status: legacy, complexity: high}
  * -->
  */
public class ReflectAble
extends UniCastConstrained //UniCaster
implements  IReflectAble, IPublisher, IDirtyFlag {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants
	////////////////////////////////////////////////////////////////////////////////
	
	/** When true (the default), a failed {@link #get(String)}/{@code set(String, Object)} throws instead of returning/ignoring silently */
	public static boolean throwExceptions = true;

	/** Constant to make handing over no Parameters more explicit */
	private static final Class [] noTypes  = null; //new Class [0];

	/** Constant to make handing over no Parameters more explicit */
	private static final Object[] noParams = null; //new Object[0];

	/** Constant to speed up handing over a single Parameter.
	  * TODO: Not Thread safe!
	  * Calls should be synchronized or the Arrays be allocated dynamically
	  */
	private static final Class [] singleType  = new Class [1];

	/** Constant to speed up handing over a single Parameter.
	  * TODO: Not Thread safe!
	  * Calls should be synchronized or the Arrays be allocated dynamically
	  */
	private static final Object[] singleParam = new Object[1];

	/** Constant to speed up handing over a single Parameter.
	  * TODO: Not Thread safe!
	  * Calls should be synchronized or the Arrays be allocated dynamically
	  */
	//private static final Class [] doubleType  = new Class [2];

	/** Constant to speed up handing over a single Parameter.
	  * TODO: Not Thread safe!
	  * Calls should be synchronized or the Arrays be allocated dynamically
	  */
	private static final Object[] doubleParam = new Object[2];

	/** Constant to speed up handing over a single Parameter.
	  * TODO: Not Thread safe!
	  * Calls should be synchronized or the Arrays be allocated dynamically
	  */
	//private static final Class [] tripleType  = new Class [3];

	/** Constant to speed up handing over a single Parameter.
	  * TODO: Not Thread safe!
	  * Calls should be synchronized or the Arrays be allocated dynamically
	  */
	private static final Object[] tripleParam = new Object[3];

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reflection Helper Routine
	  * @return the Types of the given Parameters
	  * The Fact that these Classes
	  * may be Subclasses of the required Parameters
	  * is tolerated by the Java Reflection System Methods:
	  * getMethod(), getConstructor()
	  */
	final public static Class[] GET_TYPES(Object[] params) {
		if ((params == null) ||
			(params.length == 0)) {
			return noTypes; }
		int i = params.length;
		Class[] types = new Class[i];
		while (--i >= 0) {
			types[i] = params[i].getClass(); }
		return types; }

	/////////////////////////////////////////////////////////////////////////////////////
	/// #region acquiring a Method, quite simple
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Reflection Helper Routine
	  * @return the Method with the given Name and Parameter Types
	  */
	final public static Method GET_METHOD(Class cls, String name, Class[] parameterTypes) 
	throws NoSuchMethodException {
		return cls.getMethod(name, parameterTypes); }

	/** Reflection Helper Routine
	  * @return the Method with the given Name and Parameter Types
	  */
	final public static Method GET_METHOD(Class cls, String name, Object[] parameters) 
	throws NoSuchMethodException {
		return cls.getMethod(name, GET_TYPES(parameters)); }
	
	/** Reflection Helper Routine
	 * works only if the Parameter Types are exactly the declared ones!
	 * @return the Method with the given Name and Parameter Types
	 */
	final public static Method GET_METHOD(Object obj, String name, Object[] parameters) 
	throws NoSuchMethodException {
		return obj.getClass().getMethod(name, GET_TYPES(parameters)); }

	/** Reflection Helper Routine
	  * @return the Method with the given Name and Parameter Types
	  */
	final public static Method GET_METHOD(Object obj, String name, Class[] parameterTypes) 
	throws NoSuchMethodException {
		return obj.getClass().getMethod(name, parameterTypes); }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// #region calling a Method by Name
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Invokes the Method named {@code name} on {@code ths}, matching Parameter Types from the given arguments.
	  * @return the given Function (or indexed Data Value) Result (analogous to SmallTalk)
	  * This Method is shorter, because no Fields and no Ambiguities in the Method Name are allowed.
	  */
	final static public Object CALL(Object ths, String name, Object[] params) {
		return CALL(ths, name, params, null); }

	/** Invokes the Method named {@code name}, resolved against {@code cls} (or {@code ths.getClass()} when null).
	  * @return the given Function (or indexed Data Value) Result (analogous to SmallTalk)
	  * This Method is shorter, because no Fields and no Ambiguities in the Method Name are allowed.
	  */
	protected static final Object CALL(Object ths, String name, Object[] params, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		try {
			Method mtd = cls.getMethod(name, GET_TYPES(params));
			if (mtd == null) {
				return null; }
			return mtd.invoke(ths, params);
	//	} catch (     NoSuchMethodException x) {
	//	} catch (    IllegalAccessException x) { //not possible, since only public Methods are found!
		} catch ( InvocationTargetException x) { //contains a wrapped Exception
			throw new BaseException("called from ReflectAble.get!", x.getTargetException()); //
		} catch (                 Exception x) {
			throw new BaseException("called from ReflectAble.get!", x); //NoSuchMethodError(x.toString());
		}
	//	return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// #region GET & SET iterate over Interface & determined by Tester
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Iterates over all Values and Properties
	  * Sets the Data Value selected by the ITester
	  * This can be used to implement logical Queries.
	  * @TODO: what if no Value was selected during Iteration, should it throw an Exception?
	  * @see #set(ITester, Object) uses this Method
	  */
	final static public void SET(final Object ths, final ITester select, final Object value, Class cls) { // throws InvalidException;
		if (cls == null) {
			cls =  ths.getClass(); }
		Field[] fields = cls.getFields(); //finds only the public Fields,
		for (int i = fields.length; --i >= 0;) {
			try {
				Field field = fields[i];
//				if (Modifier.isStatic(field.getModifiers())) { //only static Methods!
//					continue; }
				Object ret = field.get(ths);
				if (select.test(ret)) {
					field.set(ths, value);
					return;  }
			} catch (IllegalAccessException x) {
				throw new BaseException("called from ReflectAble.get!", x); //
			}
		}
		//not well defined to have set() work on Methods
/*		Method[] mtds = cls.getMethods(); //finds only the public Fields,
		i = mtds.length;
		Object[] param = {value};
		while (--i >= 0) {
			Method mtd = mtds[i];
			Class[] params = mtd.getParameterTypes();
			if (void.class != cls.getReturnType()) {
				continue; }
			if (params.length != 1) {
				continue; }
			mtd.invoke(ths, param);
			if (select.Test(ret)) {
				fld[i].set(ths, value);
				return ret;  }
		}
*/	}

	/** Iterates over all Values and Properties
	  * This can be used to implement logical Queries.
	  * @return the Data Value selected by the ITester
	  * or null when no Value was selected.
	  */
	final static public Object GET(final Object ths, final ITester select, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		Field[] fields = cls.getFields(); //finds only the public Fields,
		for (int i = fields.length; --i >= 0;) {
			try {
				Field field = fields[i];
//				if (Modifier.isStatic(field.getModifiers())) { //only static Methods!
//					continue; }
				Object ret = field.get(ths);
				if (select.test(ret)) {
					return ret;  }
			} catch (IllegalAccessException x) {
				throw new BaseException("called from ReflectAble.get!", x); //
			}
		}
		//not well defined to have get() work on Methods
		Method[] methods = cls.getMethods(); //finds only the public Methods,
		for (int i = methods.length; --i >= 0; ) {
			Method method = methods[i];
			Class[] params = method.getParameterTypes();
			if (params.length > 0) {
				continue; }
//			if (Modifier.isStatic(method.getModifiers())) { //only static Methods!
//				continue; }
			Object ret = null;
			try {
				ret = method.invoke(ths, null);
			} catch ( InvocationTargetException x) { //contains a wrapped Exception
				throw new BaseException("called from ReflectAble.get!", x.getTargetException()); //
			} catch (IllegalAccessException x) {
				throw new BaseException("called from ReflectAble.get!", x); //
			}
			if (select.test(ret)) {
				return ret;  }
		}
		return null; }
	
	/////////////////////////////////////////////////////////////////////////////////////
	/// #region setting / getting either Field or Method
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Returns the given Data Value or Input streamIO
	  * Generic "Reader" Routine returning a Value,
	  * any Number of Readers are allowed, except when a Writer is active.
	  */
	final static public Object GET(Object ths, String PropName) {
		return GET(ths, PropName, null); }

	/** Returns the given Data Value or Input streamIO
	  * Generic "Reader" Routine returning a Value,
	  * any Number of Readers are allowed, except when a Writer is active.
	  */
	protected static final Object GET(Object ths, String PropName, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		String str;
		Object ret = ths;
		int Pos = -1;
		int len = PropName.length();
		do { //be tolerant to SEP appearing in the Field Name
			try {
				Pos = PropName.indexOf(SEP, Pos+1);
				if (Pos < 0) { //Separator not found
					Pos = len;
					str = PropName;
				} else {
					str = PropName.substring(0, Pos); }
				try {
					Field fld = cls.getField(str);
					if (fld != null) {
						ret = fld.get(ths); break; }
				} catch (      NoSuchFieldException x) { }
				try {
					Method mtd = cls.getMethod(str, noTypes);
					if (mtd != null) {
						ret = mtd.invoke(ths, noParams); break; }
				} catch (     NoSuchMethodException x) { }
			} catch ( InvocationTargetException x) { //contains a wrapped Exception
				throw new BaseException("called from ReflectAble.get!", x.getTargetException()); //
			} catch (    IllegalAccessException x) { //not possible, since only public Methods are found!
				throw new BaseException("called from ReflectAble.get!", x); //NoSuchMethodError(x.toString());
			} // finally {}
		} while (Pos < PropName.length());
		if (ret != ths) {
			if (Pos == len) {
				return ret; }
			return ((IReflectAble) ret).get(PropName.substring(Pos + 1)); }
		if (!throwExceptions) {
			return null; }
		throw new NoSuchMethodError("called from ReflectAble.get!"); }
	
	/** Sets the given Data Value or Output streamIO
	  * Generic "Writer" Routine returning no Value,
	  * but only a single Writer should be allowed at any Time
	  */
	final static public void SET(final Object ths, final String PropName, final Object value) {
		SET(ths, PropName, value, null); }

	/** Sets the given named Data Value or Output streamIO
	  * Generic "Writer" Routine returning no Value,
	  * but only a single Writer should be allowed at any Time, because not Thread-safe! 
	  * sets either the named Field or the Method
	  */
	final static public void SET(final Object ths, final String propName, final Object value, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		int pos = -1;
		final int len = propName.length();
		singleType [0] = value.getClass();
		singleParam[0] = value;
		try { //catch Exceptions when
			do { //be tolerant to SEP appearing in the Field Name
				final String str;
				pos = propName.indexOf(SEP, pos+1);
				if (pos < 0) { //Separator not found
					pos = len;
					str = propName;
				} else {
					str = propName.substring(0, pos); 
				}
				if (SET_FIELD(ths, propName, value, cls, pos, len, str)) {
					return; } 
				if (SET_METHOD(ths, propName, value, cls, pos, len, str)) {
					return; } 
			} while (pos < propName.length());
		} catch ( InvocationTargetException x) { //contains a wrapped Exception
			throw new BaseException("called from ReflectAble.get!", x.getTargetException()); //
		} catch (    IllegalAccessException x) { //not possible, since only public Methods are found!
			throw new BaseException("called from ReflectAble.get!", x); //NoSuchMethodError(x.toString());
		} // finally {}
		if (throwExceptions) {
			throw new NoSuchMethodError("called from ReflectAble.set!"); }
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// #region: individual Methods to set or get Fields or use IReflectAble!
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Tries {@code str} as a single-argument setter Method (if {@code pos == len}) or as a no-argument
	 * getter Method whose Result is then recursed into via {@link IReflectAble#set(String, Object)}.
	 * @param ths Object to set the Field of
	 * @param propName Name of the Property to set
	 * @param value Value to set the Property to
	 * @param cls Class of ths, can be null
	 * @param pos Position in the Path
	 * @param len Length in the Path
	 * @param str Path String
	 * @return true when a matching Method was found and invoked
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	final static public boolean SET_METHOD(
		final Object ths,
		final String propName,
		final Object value,
		Class cls,
		int pos,
		final int len,
		final String str)
		throws
			SecurityException,
			IllegalAccessException,
			IllegalArgumentException,
			InvocationTargetException {
		try { //check if this is a Method
			if (pos == len) { //no nested Names:
				final Method mtd = cls.getMethod(str, singleType); //finds only the public Methods,
				if (mtd != null) { //but from all Superclasses and Interfaces!
					mtd.invoke(ths, singleParam); 
					return true; 
				}
			} else { //nested Names...
				final Method mtd = cls.getMethod(str, noTypes); //do a get
				if (mtd != null) {
					final IReflectAble ref = (IReflectAble) mtd.invoke(ths, noParams);
					ref.set(propName.substring(pos+1), value);
					return true; 
				} 
			}
		} catch (final NoSuchMethodException x) { 
			x.printStackTrace(); 
		}
		return false; 
	}
	
	/** @see #set(Object, String, Object, Class) uses this Method exclusively 	 */
	static final boolean SET_FIELD(
	final Object ths,
	final String propName,
	final Object value,
	final int pos,
	final int len,
	final String str)
	throws SecurityException, IllegalArgumentException, IllegalAccessException {
		return SET_FIELD(ths, propName, value, ths.getClass(), pos, len, str); 
	}
	
	/** @see #set(Object, String, Object, Class) uses this Method exclusively 	 */
	private static final boolean SET_FIELD(
	final Object ths,
	final String propName,
	final Object value,
	final Class cls,
	final int pos,
	final int len,
	final String str)
	throws SecurityException, IllegalArgumentException, IllegalAccessException {
		try { //check if this is a Field
			final Field fld = cls.getField(str); //finds only the public Fields,
			if (fld != null) { //but from all Superclasses and Interfaces!
				if (pos == len) { //nested Names:
					fld.set(ths, value); 
					return true; 
				}
				final IReflectAble ref = (IReflectAble) fld.get(ths);
				ref.set(propName.substring(pos+1), value); //Recursion
				return true; 
			}
		} catch (final NoSuchFieldException x) {
			//x.printStackTrace(); 
		}
		return false; 
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	// #region: perform Deep Copies of IReflectAble
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates a new Instance of {@code ths}'s Class and deep-copies all public Fields into it.
	 * @return a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared final public to enforce Value Types!
	 * and are initialized in the Constructor.
	 */
	final static public Object COPY(Object ths) {
		return COPY(ths, null); }

	/**
	 * @return a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared final public to enforce Value Types!
	 * and are initialized in the Constructor.
	 */
	protected static final Object COPY(Object ths, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		Object ret = NEW_INSTANCE(ths, null, cls);
		COPY_AT(ret, ths, cls);
		return ret; }

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
	final static public Object COPY_AT(Object ths, Object Value) {
		return COPY_AT(ths, Value, null); }

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
	protected static final Object COPY_AT(final Object ths, final Object Value, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		Field[] fields = cls.getFields(); //gets all public Fields, also inherited ones!
		int i = fields.length;
		try {
			while (--i >= 0) {	//for each Field of Aspect Type, perform the get, clone and set Operations!
				Field field = fields[i];
				Object val = field.get(Value);
				if (val instanceof IReflectAble) {
					((IReflectAble) field.get(ths)).CopyAt(val);
				} else { //all other Fields are shallow copied!
					if (0 != (field.getModifiers() & (Modifier.STATIC | Modifier.FINAL))) {
						continue; }
					field.set(ths, val);
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString()); }
		return ths; }

	/** Creates a new Instance of {@code ths}'s Class via its empty Constructor, or a matching Constructor when {@code params} is given.
	  * @return a new Instace of this Type 	 */
	final static public Object NEW_INSTANCE(final Object ths, final Object[] params) {
		return NEW_INSTANCE(ths, params, null); }

	/** Creates a new Instance of {@code cls} (or {@code ths.getClass()} when null) via its empty Constructor, or a matching Constructor when {@code params} is given.
	  * @return a new Instace of this Type 	 */
	protected static final Object NEW_INSTANCE(final Object ths, final Object[] params, Class cls) {
		if (cls == null) {
			cls =  ths.getClass(); }
		try {
			if ((params == null) ||
				(params.length == 0)) {
				return (IReflectAble) cls.newInstance(); } //faster!
			Constructor cnst = cls.getConstructor(GET_TYPES(params));
			return (IReflectAble) cnst.newInstance(params);
//		} catch (    InstantiationException x) { //not possible, only when this Object is abstract or an Interface
//		} catch (    IllegalAccessException x) { //not possible, since only public Methods are found!
//		} catch (     NoSuchMethodException x) {
		} catch ( InvocationTargetException x) { //contains a wrapped Exception
			throw new BaseException("called from ReflectAble.newInstance!", x.getTargetException()); //
		} catch (                 Exception x) {
			throw new BaseException("called from ReflectAble.newInstance!", x); //NoSuchMethodError(x.toString());
		}
//		return null;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Flag switching resetting the dirty Flag to false after calling update() */
	public static boolean autoReset = true;

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Stores a Reference to this Class to speed up the Calls */
	private Class cls = getClass();

	/** Dirty Flag recursively (re-)set by the set() Operation
	  * to enable and speed up Bulk Updates
	  * A set dirty Flag also prevents further update()s
	  */
	public boolean dirty;

	/** Reports whether this Object (or any nested {@link ReflectAble} Property) has been modified since the Dirty Flag was last reset.
	  * @return the Dirty Flag recursively set by the set() Operation */
	public boolean isDirty() { return dirty; }

	/** Reports the Dirty Flag of the given nested Property instead of this Object itself.
	  * @return the Dirty Flag recursively set by the set() Operation on the given Property */
	public boolean isDirty(String PropName) {
		if ((PropName == null) ||
			(PropName.length() == 0)) {
			return dirty; }
		IDirtyFlag dirt = (IDirtyFlag) get(PropName);
		return dirt.isDirty(); }

	/** recursively (re-)sets the Dirty Flag 	 */
	public void setDirty(boolean dirty_) {
		this.dirty = dirty_;
		Field[] fields = cls.getFields();
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {
				field = fields[i];
				if   (ReflectAble.class.isAssignableFrom(field.getType())) {
					((ReflectAble) field.get(this)).setDirty(dirty_); }
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
		/** Empty Constructor	 */
	//	protected ReflectAble() { }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IPublisher: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** recursively updates all Subscribers with the current Values */
	public void update() {
		Field[] fields = cls.getFields();
		Field   field;
		int i = fields.length;
		try {
			while (--i >= 0) {
				field = fields[i];
				Object obj = field.get(this);
				if (ReflectAble.class.isAssignableFrom(field.getType())) {
					ReflectAble fld = (ReflectAble) obj;
					if (fld.isDirty()) { //update only changed Sub-Properties!
						fld.update(); }
				} else {
					update(obj, null, field.getName());
				}
			}
		} catch (IllegalAccessException x) {
			throw new IllegalAccessError(x.toString());
		}
	}

	/** updates all Subscribers with the new Value */
	protected void update(Object value, Object oldVal, String PropName) {
		if ((!dirty) &&
			(subscriber != null)) { //update after!
			 subscriber. update(this, value, oldVal);  //oldVal);
			if (!autoReset) { dirty = true; } //after Updating reset the Dirty Flag!
		} //or leave this to the Subscriber!
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IReflectAble: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Sets the given Data Value or Output streamIO
	  * @throws InvalidError when the new value was invalidated by a Validator!
	  */
	final public void set(String PropName, Object value)
		throws InvalidError { //Exception {
		Object oldVal = null;
		if ((!dirty) &&
			(subscriber != null) &&
			( validator != null)) {
			oldVal = get(PropName); }
		try {
			if ((!dirty) &&
				(validator != null)) { //validate before
				 validator.validate(this, value, PropName); } //oldVal); }
			SET(this, PropName, value, cls); dirty = true;
			update(value, oldVal, PropName); //);
		} catch (InvalidException x) {
			throw new InvalidError("ReflectAble.set failed!", x);
		}
	}

	/** Returns the given Data Value or Input streamIO 	 */
	final public Object get(String PropName) { return GET(this, PropName, cls); }

	/** Iterates over all Values and Properties
	  * Sets the Data Value selected by the ITester
	  * This can be used to implement logical Queries.
	  * @TODO: what if no Value was selected, should it throw an Exception?
	  */
	public void set(ITester select, Object value) throws InvalidError { //Exception
		SET(this, select, value, this.getClass()); }

	/** Iterates over all Values and Properties
	  * This can be used to implement logical Queries.
	  * @return the Data Value selected by the ITester
	  * or null when no Value was selected.
	  */
	public Object get(ITester select) {
		return GET(this, select, this.getClass()); }

	///////////////////////////////////////////////////////////////////////////////////
    /// Convenience Methods to call Methods with 1,2 or 3 Parameters by Name.
    ///////////////////////////////////////////////////////////////////////////////////

	/** Returns the given Function (or indexed Data Value) Result (analogous to SmallTalk) 	 */
	final public Object call(String name, Object param1) {
		singleParam[0] = param1;
		return CALL(this, name, singleParam, cls); }

	/** Returns the given Function (or indexed Data Value) Result (analogous to SmallTalk) 	 */
	final public Object call(String name, Object param1, Object param2) {
		doubleParam[0] = param1;
		doubleParam[1] = param2;
		return CALL(this, name, doubleParam, cls); }

	/** Returns the given Function (or indexed Data Value) Result (analogous to SmallTalk) 	 */
	final public Object call(String name, Object param1, Object param2, Object param3) {
		tripleParam[0] = param1;
		tripleParam[1] = param2;
		tripleParam[2] = param3;
		return CALL(this, name, tripleParam, cls); }

	/** Returns the given Function (or indexed Data Value) Result (analogous to SmallTalk) 	 */
	final public Object call(String name, Object[] params) {
		return CALL(this, name, params, cls); }

	/** Creates a new, empty Instance of this Object's concrete Class via reflection.
	  * @return a new Instace of this Type
	  * @param Params the Parameters to the Constructor.
	  * If null or an empty Array, the empty Constructor is called.
	  */
	final public IReflectAble  newInstance(Object[] params) {
		return  (IReflectAble) NEW_INSTANCE(this, params, cls); }

	/**
	 * Creates a new empty Instance of this Object's Class and deep-copies this Object's Fields into it.
	 * @return a deep Copy!
	 * Substitute for the Clone() Method which cannot be performed recursively,
	 * because the Members are declared final public to enforce Value Types!
	 * and are initialized in the Constructor.
	 */
	final public IReflectAble Copy() {
		return newInstance(null).CopyAt(this); }

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
	final public IReflectAble CopyAt(Object Value) {
		COPY_AT(this, Value, cls);
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + ReflectAble.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

