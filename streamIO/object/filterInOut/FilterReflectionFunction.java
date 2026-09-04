/**
 * File  Name: FilterReflectionFunction.java
 * Created on: 27.12.2002
 */
package streamIO.object.filterInOut;

//import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import reflect.ReflectAble;
import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IStreamOut;
import streamIO.object.AFilter;
import streamIO.object.IStreamIn;

/**
 * Title: FilterReflectionFunction<p>
 * Description:
 * Purpose:
 *
 * Applies a Reflection Function to the Objects in this streamIO.
 * This saves defining a Class with a Template Method 
 * for every Function to apply to Objects in a streamIO.
 * Additionally there are Factory Methods for the most common Functions.
 *
 * Design Decisions / Implementation Details:
 *
 * similar Classes:
 * @see streamIO.Object.FilterInOut.FilterFileToName
 * @see streamIO.Object.FilterInOut.FilterString
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
public class FilterReflectionFunction
extends AFilter {

	/** Name of the toString() Method for Reflection */
	final static public String FUNCTION_TO_STRING = "toString";

	/** @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamOut FILTER_TO_STRING(IIStreamOut out) {
		return new FilterReflectionFunction(out, FUNCTION_TO_STRING, null); }

	/** @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamIn FILTER_TO_STRING(IIStreamIn in) {
		return new FilterReflectionFunction(in, FUNCTION_TO_STRING, null); }

	/** Name of the toString() Method for Reflection */
	final static public String FUNCTION_GET_NAME = "getName";

	/** @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamOut FILTER_GET_NAME(IIStreamOut out) {
		return new FilterReflectionFunction(out, FUNCTION_GET_NAME, null); }

	/** @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamIn FILTER_GET_NAME(IIStreamIn in) {
		return new FilterReflectionFunction(in, FUNCTION_GET_NAME, null); }

	///////////////////////////////////////////////////////////////////////////////////

	/** Switches returning an Exception instead of returning null */
	public boolean returnException;

	/** Switches returning null on instead of returning an Exception */
	public boolean returnOriginal = true;

	/** Name of the Function */
	protected String functionName;

	/** Parameters to the Function */
	protected Object[] parameters;

	/** Function to apply on the Objects in the streamIO
	 * initialized on the first Evaluation
	 */
	protected Method function;

	/** @return the Return Value of the Function applied to the Argument
	 * or the Exception if the Function throws one!
	 */
	public Object applyMethod(Object obj)
//	throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		try {
//			if (obj == null) {
//				return null; }
			if (function == null) {
				function = ReflectAble.GET_METHOD(obj, functionName, parameters);
			}
			return function.invoke(obj, parameters);
		} catch (Exception x) {
			if (returnException) {
				return x; }
			if (returnOriginal) {
				return obj; }
				return null;
		}
	}

	/**
	 * Constructor for FilterReflectionFunction.
	 * @param out_
	 */
	public FilterReflectionFunction(IIStreamOut out_, String functionName_, Object[] parameters_) {
		super(out_);
		functionName = functionName_;
		parameters = parameters_;
	}

	/**
	 * Constructor for FilterReflectionFunction.
	 * @param Enum
	 */
	public FilterReflectionFunction(IIStreamIn Enum, String functionName_, Object[] parameters_) {
		super(Enum);
		functionName = functionName_;
		parameters = parameters_;
	}

	/**@return the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		if (((currItem = in.nextItem()) == EOI) && !in.isValid()) {
			return currItem; }
		return applyMethod(currItem); }

	/**
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(Object arg) {
		out.addItem(applyMethod(arg));
		return this; }

	public static void main(String[] args) {}

}
