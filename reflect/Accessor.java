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
 * Purpose:
 *
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
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class Accessor {

	/** Logger for Testing, modify Threshold for switching Logging */
	static Log L = new Log(Accessor.class, 1);
	
	final static public String STR_GET = "get"; 
	
	final static public String STR_SET = "set"; 
	
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
	
	final static public void SET_FIELD(final Object ths, final String name, final Object value) {
		SET_FIELD(ths, null, name, value); 
	}
	
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
	
	final static public Object GET_METHOD(final Object ths, final String name) {
		return GET_METHOD(ths, ths.getClass(), name); 
	}
	
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

	public void setOrAddFieldOrMethod(final Object ths, final String name, final Object value) {
		setOrAddFieldOrMethod(ths, null, name, value); 
	}

	public boolean setOrAddFieldOrMethod(final Object ths, final Class cls, final String name, final Object value) {
		return setOrAddFieldOrMethod(ths, cls, name, value, null); }

	public boolean setOrAddFieldOrMethod(final Object ths, final Class cls, final String name, final Object value, final Class argCls) {
		try{ _SET_FIELD(ths, cls,          name, value        ); return true; } catch(final Exception ignored) { L.n(ignored); }
		final String cName = Character.toUpperCase(name.charAt(0))+name.substring(1); 
		try{    _setMethod(ths, cls, STR_SET+cName, value, argCls); return true; } catch(final Exception ignored) { L.n(ignored); }
		try{    _setMethod(ths, cls, STR_ADD+cName, value, argCls); return true; } catch(final Exception ignored) { L.n(ignored); }
		//try{ findSetMethod(ths, cls, STR_SET+cName, value); return true; } catch(final Exception ignored) { L.n(ignored); }
		return false; 
	}
	
	public Object getFieldOrMethod(final Object ths, final String name) {
		return getFieldOrMethod(ths, null, name, false); }
	
	/** Type of the last GET Method for handing it over to the CALLER	 */
	final public Class[] retCls = new Class[1];  
	
	public Class getRetClass() {
		return retCls[0]; }
	
	public Object getFieldOrMethod(final Object ths, final Class cls, final String name, final boolean returnType) {
		retCls[0] = null;
		try{ return _GET_FIELD (ths, cls,          name, returnType, retCls); } catch(final Exception ignored) { L.n(ignored); }
		final String cName = Character.toUpperCase(name.charAt(0))+name.substring(1); 
		try{ return _GET_METHOD(ths, cls, STR_GET+cName, returnType, retCls); } catch(final Exception ignored) { L.n(ignored); }
		return null;
	}
	
}
