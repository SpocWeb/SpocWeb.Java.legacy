package streamIO.factory;

import graphs.ICopy;
import streamIO.IFactory;

/**
  * Creates a new Object by copying a prototype Object's data, not only its Class.
  * This is similar to {@link FactoryByClass}'s Approach,
  * only that the Prototype also copies Data, not only Type Information.
  *
  * Design Decisions / Implementation Details:
  * Since Cloneable() is only a tagging Interface
  * and the clone() Method is protected, an explicit ICopy Interface has to be used!
  *
  * Known SubClasses: <none>
  * 
  * Known Uses: <none>
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-01-2002, 09:24 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:06:26Z
  * digest: 7c5a248552a32f2ecf0d2b79051f4e31047176293281134fc09db2bd28cc332f
  * stale: false
  * tags: [code/factory_pattern, code/cloneable_pattern]
  * concepts: [Object Instantiation, Prototype Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class FactoryByPrototype
implements IFactory {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Class of the Object to create: 	 */
	protected ICopy obj;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return TODO:   */
//	public ICopy getVal() { return obj; }

	/** sets the Object to clone: 	 */
	protected void setVal(ICopy obj_) { this.obj = obj_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FactoryByPrototype(ICopy obj) { setVal(obj); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IFactory: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a new Object by copying the prototype supplied to the constructor.
	  * @return a copy of the prototype Object, via {@link ICopy#Copy()}
	  */
	public Object nextItem() { return obj.Copy(); }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FactoryByPrototype.class.getName());
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }
	
}

