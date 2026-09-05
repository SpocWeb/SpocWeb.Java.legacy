package streamIO.factory;

import streamIO.IFactory;
import streamIO.exception.BaseException;

//import Graph.ICopy;
//import java.lang.reflect.Constructor;

/**
  * Creates a new, uninitialized instance of the same Class as a given prototype Object,
  * using reflection rather than copying.
  * This is similar to the Prototype Approach,
  * only that the Prototype also copies Data, not only Type Information.
  *
  * Design Decisions / Implementation Details:
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
  * mtime: 2026-09-05T09:06:15Z
  * digest: 0e3e8c5748ccde039fcbefef77c261c8fa507b364a7cdf104c9dd28c2b099b80
  * stale: false
  * tags: [code/factory_pattern, code/reflection]
  * concepts: [Object Instantiation, Prototype Pattern]
  * facets: {layer: infrastructure, status: stable, complexity: low}
  * -->
  */
public class FactoryByClass
implements IFactory {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the Object to clone: 	 */
//	protected ICopy obj;

	/** Reference to the Class of the Object to create: 	 */
	protected Class cls;
//	protected Constructor cnst;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return TODO:   */
//	public ICopy getValue() { return obj; }

	/** sets the Object to clone: 	 */
//	public void setValue(ICopy obj_) { this.obj = obj_; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
//	public FactoryByClass(ICopy obj) { setValue(obj); }

	/** Initializing Constructor	 */
//	public FactoryByClass(Class cls) { setValue(obj); }

	/** Initializing Constructor	 */
	public FactoryByClass(Object obj) {
		cls  = obj.getClass();
//		cnst = cls.getConstructor(null);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IFactory: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Creates a new instance of the Class captured from the constructor's Object, via
	  * its public no-argument constructor.
	  * @return a new instance of the captured Class
	  * @throws BaseException wrapping any {@link InstantiationException} or
	  *         {@link IllegalAccessException} thrown by the reflective construction
	  */
	public Object nextItem() {
		try {
			return cls .newInstance();
//		return cnst.newInstance(null);
//		return obj.Copy();
		} catch ( Exception x) {
			throw new BaseException(x);
//		} catch ( IllegalAccessException x) {
//		} catch ( InstantiationException x) {
		}
//		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FactoryByClass.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

