/*
 * File Name: Accessor.java
 * Created on: 18.05.2004
 *
 */
package reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import streamIO.Log;

/**
 * Title: Accessor<p>
 * Description:
 * Reflection helper that reads or writes a named Field or JavaBean-style getter/setter
 * on an arbitrary Object, swallowing the usual reflection Exceptions so callers can
 * treat "no such Member" as a simple boolean/null result instead of a checked Exception.
 * Unlike {@link ReflectAble}, this Class works on any Object without requiring it to
 * implement {@link IReflectAble}; it also caches the last resolved Class/Field/Method
 * for repeated Calls with the same Name.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:22:13Z
 * digest: e75575b684f7c2d0b3b4fedebd4518a89641971d3c9fad5febd490cf56885fc5
 * stale: false
 * tags: [code/reflection_helper, code/reflection_based_property_access]
 * concepts: [Reflection]
 * facets: {layer: utility, status: stable, complexity: medium}
 * -->
 */
public class Accessor {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Accessor.class, 1);
	
	/** Prefix used to build a getter Method Name from a Property Name */
	final static public String STR_GET = "get";

	/** Prefix used to build a setter Method Name from a Property Name */
	final static public String STR_SET = "set";

	/** Prefix used to build an "add" Method Name from a Property Name, for collection-typed Properties */
	final static public String STR_ADD = "add";
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Arguments for performing the Call, not for concurrent Access!
	/////////////////////////////////////////////////////////////////////////////////////
		
	protected final Object[] args = new Object[1]; 
	
	protected final Class[] argTypes = new Class[1]; 
	
	/////////////////////////////////////////////////////////////////////////////////////
	//cached Arguments for repetitive Calls
	/////////////////////////////////////////////////////////////////////////////////////
	
	/** Class last used	 */	
	protected Class cls; 
	
	/** Argument Class last used	 */	
	protected Class argCls; 

	/** Name of the Field or Method last used	 */	
	protected String name; 
	
	/** Field last used	 */	
	protected Field fld; 
	
	/** Method last used	 */	
	protected Method mtd; 
	
	/** Sets the public Field named {@code name} on {@code ths}, silently doing nothing if it does not exist or is not accessible */
	final static public void SET_FIELD(final Object ths, final String name, final Object value) {
		SET_FIELD(ths, null, name, value);
	}

	/** Sets the public Field named {@code name} on {@code ths}, resolved against {@code cls} (or {@code ths.getClass()} when null); silently does nothing on failure */
	final static public void SET_FIELD(final Object ths, final Class cls, final String name, final Object value) {
		try {
			_SET_FIELD(ths, cls, name, value);
		} catch (IllegalAccessException x) {
		} catch (NoSuchFieldException x) {
		}
	}

	private static void _SET_FIELD( final Object ths, Class cls, final String name, final Object value)
	throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (cls == null) {
			cls = ths.getClass(); }
		final Field fld = cls.getField(name); 
		fld.set(ths, value);
	}
	
	protected static final Object GET_FIELD(final Object ths, final String name) {
		return GET_FIELD(ths, null, name); 
	}
	
	protected static final Object GET_FIELD(final Object ths, final Class cls, final String name) {
		try {
			return _GET_FIELD(ths, cls, name, false, null);
		} catch (IllegalAccessException x) {
		} catch (NoSuchFieldException x) {
		}
		return null; 
	}

	private static Object _GET_FIELD(final Object ths, final Class cls, final String name, final boolean returnType, final Class[] retType)
		throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field fld = cls.getField(name);
		if (returnType) {
			return fld.getType(); }
		return fld.get(ths);
	}
	
	protected void setMethod(final Object ths, final String name, final Object value) {
		setMethod(ths, null, name, value, null); 
	}
	
	protected void setMethod(final Object ths, final Class cls, final String name, final Object value, final Class argCls) {
		try {
			_setMethod(ths, cls, name, value, argCls);
		} catch (IllegalAccessException x) {
		} catch (NoSuchMethodException x) {
		} catch (InvocationTargetException x) {
		}
	}
	/*
	private void _setMethod(
	final Object ths, Class cls, final String name, final Object value)
	throws
		NoSuchMethodException,
		SecurityException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException {
			_setMethod(ths, cls, name, value, null); 
	}
	*/
	
	private void _setMethod(
	final Object ths, Class cls, final String name, final Object value, final Class argCls)
	throws
		NoSuchMethodException,
		SecurityException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException {
		if (cls == null) {
			cls = ths.getClass();
		}
		if (argCls == null) {
			argTypes[0] = value.getClass();
		} else {
			argTypes[0] = argCls ; 
		}
		args[0] = value;
		final Method mtd = cls.getMethod(name, argTypes);
		mtd.invoke(ths, args);
	}
	
	/** Finds a setter Method matching {@code name} case-insensitively with exactly one Parameter, constructs that Parameter's Type from {@code value} via a matching single-argument Constructor, and invokes it; throws if no such Method is found */
	public void findSetMethod(
	final Object ths, Class cls, final String name, final Object value)
	throws
		IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (cls == null) {
			cls = ths.getClass(); }
		final Method[] methods = cls.getMethods();
		for (int i = methods.length; --i >= 0; ) {
			final Method method = methods[i]; 
			if (!method.getName().equalsIgnoreCase(name)) {
				continue; }
			final Class[] params = method.getParameterTypes(); 
			if (params.length != 1) {
				continue; }
			argTypes[0] = value.getClass();
			args[0] = value; 
			args[0] = params[0].getConstructor(argTypes).newInstance(args); 
			method.invoke(ths, args);
			return; 
		}
		throw new NoSuchMethodException(name+" not found!"); 
	}
	
	/** Invokes the no-argument Method named {@code name} on {@code ths} and returns its Result, or null on failure */
	final static public Object GET_METHOD(final Object ths, final String name) {
		return GET_METHOD(ths, ths.getClass(), name);
	}

	/** Invokes the no-argument Method named {@code name}, resolved against {@code cls}, and returns its Result, or null on failure */
	final static public Object GET_METHOD(final Object ths, final Class cls, final String name) {
		try { //no Parameters can be expressed using null!
			return _GET_METHOD(ths, cls, name, false, null);
		} catch (   IllegalAccessException x) {
		} catch (    NoSuchMethodException x) {
		} catch (InvocationTargetException x) {
		}
		return null; 
	}

	private static final Object _GET_METHOD(final Object ths, Class cls, final String name, final boolean returnType, final Class[] retType)
	throws
	NoSuchMethodException,
	SecurityException,
	IllegalAccessException,
	IllegalArgumentException,
	InvocationTargetException {
		if (cls == null) {
			cls  = ths.getClass(); }
		final Method mtd = cls.getMethod(name, null);
		if (retType != null)  {
			retType[0] = mtd.getReturnType(); //required, because the Return Value does not distinguish between int and Integer
		}
		if (returnType) {
			if (retType != null) { 
				return retType[0]; }
			return mtd.getReturnType(); 
		}
		return mtd.invoke(ths, null);
	}

	/** Tries, in order, to set the Field {@code name}, then call {@code setName(value)}, then call {@code addName(value)} */
	public void setOrAddFieldOrMethod(final Object ths, final String name, final Object value) {
		setOrAddFieldOrMethod(ths, null, name, value);
	}

	/** Tries, in order, to set the Field {@code name} on {@code ths} (resolved against {@code cls}), then call its setter, then its adder.
	  * @return true if the Field was set or a setter/adder Method was successfully invoked */
	public boolean setOrAddFieldOrMethod(final Object ths, final Class cls, final String name, final Object value) {
		return setOrAddFieldOrMethod(ths, cls, name, value, null); }

	/** Tries, in order, to set the Field {@code name}, then call its setter, then its adder, using {@code argCls} as the declared Parameter Type when given.
	  * @return true if the Field was set or a setter/adder Method was successfully invoked */
	public boolean setOrAddFieldOrMethod(final Object ths, final Class cls, final String name, final Object value, final Class argCls) {
		try{ _SET_FIELD(ths, cls,          name, value        ); return true; } catch(final Exception ignored) { L.n(ignored); }
		final String cName = Character.toUpperCase(name.charAt(0))+name.substring(1); 
		try{    _setMethod(ths, cls, STR_SET+cName, value, argCls); return true; } catch(final Exception ignored) { L.n(ignored); }
		try{    _setMethod(ths, cls, STR_ADD+cName, value, argCls); return true; } catch(final Exception ignored) { L.n(ignored); }
		//try{ findSetMethod(ths, cls, STR_SET+cName, value); return true; } catch(final Exception ignored) { L.n(ignored); }
		return false; 
	}
	
	/** Reads the Field {@code name} on {@code ths}, or (failing that) calls its getter Method.
	  * @return the Field {@code name}'s Value, or (failing that) the Result of calling {@code getName()}; null if neither exists */
	public Object getFieldOrMethod(final Object ths, final String name) {
		return getFieldOrMethod(ths, null, name, false); }

	/** Type of the last GET Method for handing it over to the CALLER	 */
	final public Class[] retCls = new Class[1];

	/** Reports the declared Type resolved by the most recent {@link #getFieldOrMethod} Call.
	  * @return the declared Type resolved by the last {@link #getFieldOrMethod} Call */
	public Class getRetClass() {
		return retCls[0]; }

	/** Reads the Field {@code name} (resolved against {@code cls}), or (failing that) calls its getter Method; when {@code returnType} is true, returns the declared Type instead of the Value.
	  * @return the Field {@code name}'s Value or Type (if {@code returnType}), or (failing that) the Result/Type of calling {@code getName()}; null if neither exists */
	public Object getFieldOrMethod(final Object ths, final Class cls, final String name, final boolean returnType) {
		retCls[0] = null;
		try{ return _GET_FIELD (ths, cls,          name, returnType, retCls); } catch(final Exception ignored) { L.n(ignored); }
		final String cName = Character.toUpperCase(name.charAt(0))+name.substring(1); 
		try{ return _GET_METHOD(ths, cls, STR_GET+cName, returnType, retCls); } catch(final Exception ignored) { L.n(ignored); }
		return null;
	}
	
}
