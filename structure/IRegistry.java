package structure; //

import function.FunctionByHash;

/**
  * Declares a keyed Lookup Relation, typically implemented as a Singleton, mapping an
  * arbitrary key Object to a registered value Object via {@link #getAt}/{@link #setAt}.
  *
  * A Registry is a Relation and typically used as a Singleton!
  * The Methods getAt() and setAt() parallel the get() and set() Methods
  * only in that they take an Object Parameter (typically a String).
  * A concrete Implementation can be found in @see function.FunctionByHash.
  * The Registry is a universal Class that can be used anywhere
  * to separate Creation and Use of Objects as well as
  * to separate concrete Types from abstract Types.
  *
  * It can be used to keep track centrally of other Singletons that register with it.
  * It can also be used to maintain a List of Prototype Instances.
  * It's use is demonstrated in the Naming and Lookup Services (JNDI)
  * provided by Enterprise Applications.
  * 
  * Known SubClasses:
  *
  * @see FunctionByHash which is used as a Registry for Objects
  *
  * similar Classes: 
  * @see streamIO.IFactory  which doesn't take a Parameter and defines a Stream. 
  * @see function.IFunction which is a read-only Registry. 
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-13-2002, 07:19 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:16:32Z
  * digest: f6bd3ac81da0926a31a6874a5a764478f4b6690850c403913f69de4b946dba5d
  * stale: false
  * tags: [code/registry_pattern]
  * concepts: [Registry Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface IRegistry
//extends    Stream.AStreamOut
//implements Stream.Object.Enumerator.Container.IContainer //Stream.IStreamOut
{

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Singleton Instance.
	  * No lazy instantiation necessary,
	  * since this Class is final anyway. 	 */
//	final static public Reg = new Registry();

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

	/** Singleton Factory Method	 */
//	final static public getRegistry() { return Reg; }

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** HashMap to contain the Mappings by String	 */
//	protected ;

////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////

	/**
	  * This Method is typically called Map()
	  * @return the Object at the given key  */
	Object getAt(Object Key);

	/**
	  * Sets the Object at the given key
	  * This Method is named identically to the one in IDynamicFunction.  */
	void setAt(Object Key, Object Value);

}
