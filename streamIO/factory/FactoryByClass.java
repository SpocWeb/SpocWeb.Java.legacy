package streamIO.factory;

import streamIO.IFactory;
import streamIO.exception.BaseException;

//import Graph.ICopy;
//import java.lang.reflect.Constructor;

/**
  * Title: FactoryByClass<p>
  * Description:
  * Purpose:
  * Returns new Object of the same Class.
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
	
	/** @return the next (Parent) Object of this one.
	  * No Exception is thrown at the End, instead EOI is returned.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * This is less explicit, but much faster because Exception Handling can be extremely slow.
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
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

