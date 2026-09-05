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
 * Filter that applies a named method via reflection to every item passing through, resolving
 * the {@link Method} lazily against each item's runtime class.
 * <p>
 * Title: FilterReflectionFunction<p>
 * Description:
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:48:37Z
 * digest: b47caa8818441d828901257f6f544483af9211ae4e3afacf8c540aef2aa83b1b
 * stale: false
 * tags: [code/stream_filter, code/decorator_pattern]
 * concepts: [Stream Filter (Input)]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public class FilterReflectionFunction
extends AFilter {

	/** Name of the toString() Method for Reflection */
	final static public String FUNCTION_TO_STRING = "toString";

	/** Creates a filter converting objects written to {@code out} into their {@code toString()}.
	 * @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamOut FILTER_TO_STRING(IIStreamOut out) {
		return new FilterReflectionFunction(out, FUNCTION_TO_STRING, null); }

	/** Creates a filter converting objects read from {@code in} into their {@code toString()}.
	 * @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamIn FILTER_TO_STRING(IIStreamIn in) {
		return new FilterReflectionFunction(in, FUNCTION_TO_STRING, null); }

	/** Name of the toString() Method for Reflection */
	final static public String FUNCTION_GET_NAME = "getName";

	/** Creates a filter converting objects written to {@code out} into their class/member name.
	 * @return a Filter to the given StreamOut converting Objects into Strings */
	final static public IStreamOut FILTER_GET_NAME(IIStreamOut out) {
		return new FilterReflectionFunction(out, FUNCTION_GET_NAME, null); }

	/** Creates a filter converting objects read from {@code in} into their class/member name.
	 * @return a Filter to the given StreamOut converting Objects into Strings */
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

	/**
	 * Invokes the configured reflective method on {@code obj}, resolving it lazily on first use.
	 *
	 * @return the Return Value of the Function applied to the Argument
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
	 * Applies the configured reflective method to {@code arg} before adding the result to the
	 * wrapped output.
	 *
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(Object arg) {
		out.addItem(applyMethod(arg));
		return this; }

	/**
	 * Unused entry point.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {}

}
