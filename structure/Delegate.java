package structure; //

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
  * Title: Delegate<p>
  * Description:
  * Mimicks the .NET Delegate Class.
  * Contains a Reference to an Object Instance and to a Method of this Object.
  * Uses Reflection to call the Method (faster in SDK 1.4).
  * Defining a specific Delegate Type for each Event Type
  * makes handing over Delegates typesafe.
  * Delegates are constant Objects, i.e.
  * * their Target and Method are defined on Construction
  * * they cannot be changed during Existence.
  * This Technique saves defining inner Classes for Callbacks
  * and allows to reuse and hand over Observers.
  *
  * All Delegates rely on a single "Event" Method Signature,
  * that carries the Event Source Object and the EventArgs Parameter.
  *
  * Thus a Delegate represents a "Binding" between a Class and another Class's Method.
  *
  * The Parameters are generic allowing wide Use of Events:
  * * The Event Source which can be of Type Object
  * * a generic Parameter of Type Object which can contain anything.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	04-02-2002, 12:35 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Delegate {

	/** Reused on Querying for the Method by String */
	protected static final Class[] DelegateArgs = {Object.class, Object.class};

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** the Event Target (Subject) 	 */
	protected Object target;

	/** the Event Method 	 */
	protected Method method;

	/** the next Delegate in the Linked List Implementation
	  * This is appropriate when Observers are only added, but rarely removed.
	  * Instead rather the Subject / Topic / Model is destroyed.  	 */
	protected Delegate next;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns the Event Target (Subject) 	 */
	public Object getTarget() {
		return target; }

	/** Returns the Event Method 	 */
	public Method getMethod() {
		return method; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor	 */
	public Delegate(Object _target, Method _method) {
		this.target = _target;
		this.method = _method; }

	/** Constructor	 */
	public Delegate(Object _target, String _method)
	throws NoSuchMethodException {
		this.method = _target.getClass().getMethod(_method, DelegateArgs);
		this.target = _target; }

	/** Constructor	 */
	protected Delegate(Object _target, String _method, Delegate _next)
	throws NoSuchMethodException {
		this.target = _target;
		this.method = _target.getClass().getMethod(_method, DelegateArgs);
		this.next   = _next; }

	/** Constructor	 */
	protected Delegate(Object _target, Method _method, Delegate _next) {
		this.target = _target;
		this.method = _method;
		this.next   = _next; }

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

	/** Adding an Observer is an easy O(1) Operation,
	  * just add a new Delegate to the Beginning of the Linked List
	  * Removing the Delegate is an O(N) Operation
	  */
	public void addObserver(Object _target, Method _method) {
		next = new Delegate(_target, _method, next);
	}

	/** Adding an Observer is an easy O(1) Operation,
	  * just add a new Delegate to the Beginning of the Linked List
	  * Removing the Delegate is an O(N) Operation
	  */
	public void removeObserver(Object _target, Method _method) {
		Delegate curr, prev = this;
		while (null != (curr = prev.next)) {
			if ((curr.target == _target) &&
				(curr.method == _method)) {
				prev.next = curr.next;
				return; }
			prev = curr;
		}
	}

	/**
	  * Actually raises the Event by calling the Method
	  * and handing over these Parameters.
	  *
	  * The Implementation is iterative instead of recursive
	  * to increase Performance.
	  */
	public void raiseEvent(Object Source, Object Arguments) {

		//Recursive Implementation
//		method.invoke(target, params);
//		next.raiseEvent(Source, Arguments);

		//iterative Implementation
		Delegate curr = this;
		Object[] params = new Object[2];
		params[0] = Source;
		params[1] = Arguments;
		try {
			do {
				method.invoke(curr.target, params);
			} while (null != (curr = curr.next));
		} catch (IllegalAccessException x) {
		} catch (InvocationTargetException x) {
		}
	}

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Delegate.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
