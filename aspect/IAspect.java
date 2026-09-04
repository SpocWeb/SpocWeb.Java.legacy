package aspect;

import graphs.IPair;
import knowledge.IDirtyFlag;
import reflect.IReflectAble;
import streamIO.IInstantiAble;
import synch.InvalidException;

/**
  * Title: IAspect<p>
  * Description:
  * Defines the Interface for an Aspect Object or Hierarchy
  * that allows to read and write Properties
  * both explicitly like "Customer.Address.Street = "Elm Street"
  * and by their Name like "Customer.set("Address_Street") = "Elm Street"
  * The Characteristics of an Aspect are:
  * * it knows its Name and maintains the dirty Flag.
  * * it can have Subaspects and transparently read and write them
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-22-2002, 11:18 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see IReflectAble does not define a Name for its Properties
  * thus it cannot act as autonomous as IAspect.
  * Additionally IReflectAble can not automatically validate
  * and propagate Changes!
  *
  * The primitive Values String, Double, Date are wrapped with the according Types
  * and are slightly harder to use due to Accessor Methods,
  * but they are still typesafe AND other Convenience can be used
  * like Read Only Values etc.
  */
public interface IAspect
	extends IPair, Cloneable, IInstantiAble, IDirtyFlag { //ICopyAble {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** @return The Aspect Name, which is identical to getKey() but typed to String   */
	public String getName();

	/** @return the Aspect denoted by the given Name
	  * Works both with local Names and fully qualified Names!
	  * SubStr is a Monoid and the hierarchical structure matches the Object structure.
	  */
	public IAspect getField(String PropName); //throws NoSuchFieldException {

	/** Accessor Method
	  * @return the Value of the named Property
	  * @throws NullPointerException instead of NoSuchFieldException if the Field is not found!
	  */
	public Object //IAspect
		get(String PropName);

	/** Accessor Method
	  * @param sets the Value of the named Property
	  * @throws NullPointerException instead of NoSuchFieldException if the Field is not found!
	  */
	public void set(String PropName, Object val) //IAspect val)
		throws InvalidException;  //, NoSuchFieldException, InvocationTargetException { //NoSuchMethodException, IllegalAccessException,

	/** Accessor Method for writing this Value validated.
	  * Could delegate to a typesafe Routine!
	  * @param sets Value of this Aspect as an Object */
//	public void setValue(Object val) throws InvalidException;

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Recursively clears this Aspect and all Subaspects
	  * This is achieved by handing over 'null' to set()
	  */
//	public void clear();

	/** Recursively clears this Aspect and all Subaspects */
//	public void clear(String Property);

	/** @return a deep Copy of this Object */
	public IAspect newInstance(String Name);

	/** Tries to fill this Object with the given Value.
	  * Typed overloads are defined in the primitive Aspect Implementations
	  */
	public IAspect CopyAt(Object Value) throws InvalidException;
	//replaced by setVal(Object Value)

}

